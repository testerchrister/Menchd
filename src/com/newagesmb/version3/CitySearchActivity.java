package com.newagesmb.version3;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CitySearchActivity extends Activity {
	EditText searchKey;
	public ProgressDialog progress;
	public static String request_type;
	public static String city_name;
	public static String stringLatitude;
	public static String stringLongitude;
	public static String name;
	public static String password;
	public static String dob;
	public static String email;
	public static String sender_id,receiver_id,age,perc,receiver_city,login_image,user_image,username;
	public static int from_activity;
	public static String profile_details,profile_id; 
	Bitmap bitMap;
	byte[] byteArray;
	boolean img_pas;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_search);
		
		Bundle extras = getIntent().getExtras();
		img_pas=false;
		if(extras != null) {
			 from_activity=Integer.parseInt(getIntent().getStringExtra("from_activity"));
			 switch(from_activity){
			 case 1:
				name=getIntent().getStringExtra("name");
				password=getIntent().getStringExtra("password");
				dob=getIntent().getStringExtra("dob");
				email=getIntent().getStringExtra("email");
				if(getIntent().hasExtra("BitmapImage")){
					img_pas=true;
					byteArray =getIntent().getByteArrayExtra("BitmapImage");   
					
				}
				
				break;
			 case 2:
				profile_details=getIntent().getStringExtra("profile_details");
				profile_id=getIntent().getStringExtra("profile_id");
				if(getIntent().hasExtra("BitmapImage")){
					img_pas=true;
					byteArray =getIntent().getByteArrayExtra("BitmapImage");   
					
				}
				//Log.v("City Search",profile_details);
				break;
			 case 3:
				 sender_id=getIntent().getStringExtra("sender_id");
				 receiver_id=getIntent().getStringExtra("receiver_id");	
				 age=getIntent().getStringExtra("age");	
				 perc=getIntent().getStringExtra("perce");
				 receiver_city=getIntent().getStringExtra("city");
				 login_image=getIntent().getStringExtra("login_image");
				 user_image=getIntent().getStringExtra("user_image");
				 username=getIntent().getStringExtra("username");
				break;	
			default:
				name=getIntent().getStringExtra("name");
				password=getIntent().getStringExtra("password");
				dob=getIntent().getStringExtra("dob");
				email=getIntent().getStringExtra("email");
				break;	
			 }
			 		 
		}
		findViewById(R.id.cur_location_btn).setOnClickListener(new View.OnClickListener() {
		 	
			@Override
			public void onClick(View v) {
				GPSTracker gps=new GPSTracker(CitySearchActivity.this);
				if(gps.canGetLocation){
					stringLatitude=String.valueOf(gps.getLatitude());
					stringLongitude=String.valueOf(gps.getLongitude());
					/*Toast.makeText(getBaseContext(), "Your current location"+stringLatitude+" "+stringLongitude,Toast.LENGTH_SHORT).show();*/
					getCurrentLocation(stringLatitude,stringLongitude);
				}else{
					Toast.makeText(getBaseContext(), "Error: unable to fetch your current location",Toast.LENGTH_SHORT).show();
					
				}
				
			}
			
		});
		findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String key=null;
				boolean cancel=false;
				searchKey=(EditText) findViewById(R.id.location);
				key=searchKey.getText().toString();
				if(key.isEmpty()){
					cancel=true;
				}else if(key.length()<2){
					searchKey.setError("Invalid Search Key");
					cancel=true;
				}
				
				if(!cancel){
					getCitySuggestions(key);
				}
				//Toast.makeText(getBaseContext(), "Search Key"+key,Toast.LENGTH_SHORT).show();
			}
		});

	}
	
	public void getCurrentLocation(String lat,String lng){
		request_type="latlng";
		String url=getString(R.string.google_address_lookup);
		url+="&latlng="+lat+","+lng;
		
		GoogleLocation gl=new GoogleLocation();
		gl.execute(url);
	}
	public void getCitySuggestions(String key){
		request_type="key";
		String url="";
		url=getString(R.string.google_address_lookup);
		url+="&address="+key.trim();
		//Log.v("url",url);
		GoogleLocation gl=new GoogleLocation();
		gl.execute(url);
	}
	class GoogleLocation extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {

			String api_url=params[0];
			HttpRequester hr=new HttpRequester();
			String result=hr.GET(api_url);
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(CitySearchActivity.this, "","Please wait...", true);
		}
		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			JSONObject jobj;	
			//Log.v("On Post Execute",result);		
			if(request_type.equals("latlng")){
				// Use current Location
				try {
					jobj=new JSONObject(result);
					JSONArray jar=jobj.getJSONArray("results");
					if(jar.length()>0){
						String address_components=jar.get(0).toString();
							
						JSONObject add_cmp=new JSONObject(address_components);
						
						
						JSONArray add_cmp_array=add_cmp.getJSONArray("address_components");
	
						int add_cmp_length=add_cmp_array.length();
						String city_details;
						if(add_cmp_length>4){
							city_details=add_cmp_array.getString(add_cmp_length-4);
							JSONObject city_obj=new JSONObject(city_details);
							city_name=city_obj.getString("long_name");
							callBack(city_name,stringLatitude,stringLongitude);
						}
					}else{
						Toast.makeText(getBaseContext(), "Unable to fetch location details.",Toast.LENGTH_SHORT).show();
					}
					
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
			}else{
				//Search with Keyword
				try {
					jobj=new JSONObject(result);
					JSONArray jar=jobj.getJSONArray("results");
					String [] city_list;
					String [] city_lat_lng; 
					if(jar.length()>0){
						city_list=new String[jar.length()];
						city_lat_lng=new String[jar.length()];
						for(int i=0;i<jar.length();i++){
							
							String address_components=jar.get(i).toString();							
							JSONObject add_cmp=new JSONObject(address_components);
							city_list[i]=add_cmp.getString("formatted_address");
							String tmp_data=add_cmp.getString("geometry");
							
							if(!tmp_data.isEmpty()){
								JSONObject tmp_jobj=new JSONObject(tmp_data);
								city_lat_lng[i]=tmp_jobj.getString("location");
							}
						}
						InputMethodManager imm = (InputMethodManager)getSystemService(
	                		      Context.INPUT_METHOD_SERVICE);
	                		imm.hideSoftInputFromWindow(searchKey.getWindowToken(), 0);
						cityList(city_list,city_lat_lng);	
					}else{
						Toast.makeText(getBaseContext(), "Unable to fetch your current location.",Toast.LENGTH_SHORT).show();
					}
					
				} catch (JSONException e) {
					
					e.printStackTrace();
				}				
			}
			
		}
		public void cityList(String [] city_list, String [] city_lat_lng){
			
			final ListView lv = (ListView) findViewById(R.id.city_list);
			final ArrayList<String> list = new ArrayList<String>();
			
			for(int i=0; i<city_list.length;i++){
				try {
					JSONObject jobj=new JSONObject();
					jobj.accumulate("formatted_address", city_list[i]);
					jobj.accumulate("geometry", city_lat_lng[i]);
					list.add(jobj.toString());
					
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
			}
			final CustomAdapter adapter = new CustomAdapter(CitySearchActivity.this,
					R.layout.formated_city_address, list);
			lv.setAdapter(adapter);
			
		}
			
		
	}
	
	class CustomAdapter extends ArrayAdapter<String> {

		private final LayoutInflater inflater = null;
		Context mContext;
		JSONObject city_jobj;
		ArrayList<String> city_list;
		int layoutResourceId;

		String rec_user_id;

		public CustomAdapter(Context context, int layoutResourceId,
				ArrayList<String> list) {

			super(context, layoutResourceId, list);
			city_list = list;
			this.mContext = context;
			this.layoutResourceId = layoutResourceId;
			 //Log.v("Response Constructor:", city_list.get(0).toString()); 
		}

		@Override
		public int getCount() {
			
			return city_list.size();
		}

		@Override
		public String getItem(int position) {
			
			return null;
		}

		@Override
		public long getItemId(int position) {
			
			return 0;
		}

		public class Holder {
			TextView city_address;
			LinearLayout city_content;
		}
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				// inflate the layout

				LayoutInflater inflater = ((Activity) mContext)
						.getLayoutInflater();
				convertView = inflater.inflate(layoutResourceId, parent, false);

			}
			try {
				final JSONObject city_json = new JSONObject(city_list.get(
						position).toString());

				final Holder holder = new Holder();
				holder.city_address=(TextView) convertView.findViewById(R.id.city_address);
				holder.city_address.setText(city_json.getString("formatted_address"));
				holder.city_content=(LinearLayout) convertView.findViewById(R.id.city_content);
				holder.city_content.setTag(position);
				holder.city_content.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						int j=(Integer) holder.city_content.getTag();
						try {
							//Toast.makeText(getBaseContext(), city_json.getString("formatted_address"),Toast.LENGTH_SHORT).show();
							String formated_address[]=city_json.getString("formatted_address").split(",");
							String city_name=formated_address[0];
							JSONObject latlng=new JSONObject(city_json.getString("geometry"));
							callBack(city_name,latlng.getString("lat"),latlng.getString("lng"));
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
					}
				});
				
			}catch(JSONException e){
				
			}
			return convertView;
		}	
	}

   void callBack(String city,String lat,String lon){
	   switch(from_activity){
	   case 1:
		   // Register Activity
		   	Intent city_intent= new Intent(CitySearchActivity.this, RegistrationActivity.class);
			city_intent.putExtra("city", city);
			city_intent.putExtra("lat", lat);
			city_intent.putExtra("lng",lon);
			city_intent.putExtra("name", name);			
			city_intent.putExtra("password", password);
			city_intent.putExtra("dob", dob);
			city_intent.putExtra("email", email);
			if(img_pas){

				city_intent.putExtra("BitmapImage", byteArray);
			}
				
			
			CitySearchActivity.this.startActivity(city_intent);
			CitySearchActivity.this.finish();
		    break;
	   case 2:
		// Redirect to EditBasicActivity
		   	Intent city_intent2= new Intent(CitySearchActivity.this, EditBasicsActivity.class);
		   	//Log.v("Img paaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaas",String.valueOf(img_pas));
		   	if(img_pas){

		   		city_intent2.putExtra("BitmapImage", byteArray);
			}
		try {
			JSONObject jobj=new JSONObject(profile_details);
			jobj.put("city", city);
			jobj.put("lat", lat);
			jobj.put("long", lon);
			city_intent2.putExtra("profile_details", jobj.toString());
			city_intent2.putExtra("profile_id", profile_id);
			CitySearchActivity.this.startActivity(city_intent2);
			CitySearchActivity.this.finish();
		} catch (JSONException e) {
			
			e.printStackTrace();
		}		

		   	break;	
	   case 3:
		// Redirect to Conversation Activity
		   	
		try {
			JSONObject jobj=new JSONObject();
			jobj.put("location", city);
			jobj.put("lat", lat);
			jobj.put("lng", lon);
			jobj.put("message", "");
			jobj.put("sender_id", sender_id);
			jobj.put("receiver_id", receiver_id);
			PostLocation pl=new PostLocation();
			pl.execute(jobj.toString());
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}		

		   	break;			   	
	   }
   }
   class PostLocation extends AsyncTask<String, Void, String>{

	@Override
	protected String doInBackground(String... params) {
		String result="";
		String location_details=params[0];
		try {
			JSONObject job=new JSONObject(location_details);
			HttpRequester hr;
			hr = new HttpRequester();
			String url=getString(R.string.send_message_url);
			hr.sendMessage(url, job);
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return result;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		progress = ProgressDialog.show(CitySearchActivity.this, "","Please wait...", true);
	}
	@Override
	protected void onPostExecute(String result) {
		progress.dismiss();
		Intent city_intent3= new Intent(CitySearchActivity.this, ConversationActivity.class);
		city_intent3.putExtra("receiver_id", receiver_id);
		city_intent3.putExtra("sender_id", sender_id);
		city_intent3.putExtra("login_image", login_image);
		city_intent3.putExtra("user_image", user_image);
		city_intent3.putExtra("username", username);
		city_intent3.putExtra("age", age);
		city_intent3.putExtra("perce", perc);
		city_intent3.putExtra("city", receiver_city);
		
		//Toast.makeText(getBaseContext(), "Location Submitted", Toast.LENGTH_SHORT).show();
		CitySearchActivity.this.startActivity(city_intent3);
		CitySearchActivity.this.finish();
	}
	
   }
}
