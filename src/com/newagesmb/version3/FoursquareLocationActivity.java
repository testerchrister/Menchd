package com.newagesmb.version3;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FoursquareLocationActivity extends Activity implements OnItemClickListener {
	String stringLatitude,stringLongitude,status_text,date_time;
	ArrayList<String> Originalist;
	String original_list;
	ProgressDialog progress;
	EditText search_key;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_foursquare_location);
		
		GPSTracker gpsTracker = new GPSTracker(this);
		if (gpsTracker.canGetLocation()) {
			stringLatitude = String.valueOf(gpsTracker.latitude);
			stringLongitude = String.valueOf(gpsTracker.longitude);
		}else{
			stringLatitude="00.0";
			stringLongitude="00.0";
		}
		Bundle extras = getIntent().getExtras();
		if(extras!=null){
			status_text=getIntent().getStringExtra("status_text");
			date_time=getIntent().getStringExtra("date_time");
		}
		GetForsquareLocation gfl=new GetForsquareLocation();
		gfl.execute(stringLatitude,stringLongitude);
		
		search_key=(EditText) findViewById(R.id.search_key);
		/*ArrayAdapter<String> auto_adapter=new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,COUNTRIES);
		search_key.setAdapter(auto_adapter);*/
		search_key.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				
				//Toast.makeText(getApplicationContext(), search_key.getText().toString(), Toast.LENGTH_SHORT).show();
				String tmp_key=search_key.getText().toString();
				keySuggest(tmp_key);
			}
		});
		
		ImageButton clear_btn=(ImageButton) findViewById(R.id.clear_btn);
		clear_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				finish();
			}
		});
	}
	@SuppressLint("DefaultLocale")
	public void keySuggest(String key){
		
		
		try {
			
			JSONArray jaray=new JSONArray(original_list);
			JSONArray jarray_new=new JSONArray();
			
			String loc_name="";
			for(int i=0;i<jaray.length();i++){
				JSONObject job=new JSONObject(jaray.get(i).toString());
				
				loc_name=job.getString("name").toLowerCase();
				if(loc_name.contains(key)){
					
					jarray_new.put(job);
				}
			}
			setLocations(jarray_new.toString());
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
	}
	class GetForsquareLocation extends AsyncTask<String, Void, String>{

		@SuppressLint("SimpleDateFormat")
		@Override
		protected String doInBackground(String... params) {
			String result="";
			String url=getString(R.string.forsquare_url);
			String client_id=getString(R.string.forsquare_client_id);
			String client_secret=getString(R.string.forsquare_client_secret);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			String cur_date=sdf.format(date);
			url+="radius=2000"+"&ll="+params[0]+","+params[1]+"&limit=20"+"&client_id="+client_id+"&v="+cur_date+"&client_secret="+client_secret;
			//Log.v("URL",url);
			try{
				HttpRequester hr=new HttpRequester();
				result=hr.GET(url);
			}catch (Exception e) {
				e.printStackTrace();
				
			}
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(FoursquareLocationActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			
			try {
				JSONObject jobj=new JSONObject(result);
				String tmp=jobj.getString("response");

				JSONObject tmp_obj=new JSONObject(tmp);
				original_list=tmp_obj.getString("venues");
				setLocations(tmp_obj.getString("venues"));
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
		}			
		
	}
	
	class Holder{
		TextView location_title,location_details;
	}
	
	public void setLocations(String result){
		
		try {
			JSONArray jarray=new JSONArray(result);
			
			if(jarray.length()>0){
				ArrayList<String> list=new ArrayList<String>();
				for(int i=0;i<jarray.length();i++){
					list.add(jarray.getJSONObject(i).toString());
				}
				Originalist=list;
				//Toast.makeText(getApplicationContext(), list.toString(), Toast.LENGTH_SHORT).show();
				//Log.v("Venue List",list.toString());
				ListView lv=(ListView) findViewById(R.id.list_view);
				CustomAdapter adapter=new CustomAdapter(FoursquareLocationActivity.this, R.layout.foursquare_liet_item, list);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(FoursquareLocationActivity.this);
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	
	class CustomAdapter extends ArrayAdapter<String> {
		private final LayoutInflater inflater = null;
		ArrayList<String> mlist;
		Context mContext;
		int layoutResourceId;
		public CustomAdapter(Context context, int layoutResourceId,
				ArrayList<String> list) {
			
			super(context, layoutResourceId, list);
			
			mlist=list;
			mContext=context;
			this.layoutResourceId = layoutResourceId;
			
		}
		@Override
		public String getItem(int position) {
			
			return mlist.get(position);
		}
		@Override
		public long getItemId(int position) {
			
			return 0;
		}
		
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			
			if (convertView == null) {
				
				LayoutInflater inflater = ((Activity) mContext)
						.getLayoutInflater();
				convertView = inflater.inflate(layoutResourceId, parent, false);
					
			}
			final Holder hl=new Holder();
			try {
				JSONObject jobj = new JSONObject(mlist.get(position).toString());
				//Log.v("Position"+position,jobj.toString());
				hl.location_title=(TextView)  convertView.findViewById(R.id.location_title);
				hl.location_title.setText(jobj.getString("name"));
				hl.location_details=(TextView) convertView.findViewById(R.id.location_details);
				hl.location_details.setText("");
				String location_data=jobj.getString("location");
				JSONObject job=new JSONObject(location_data);
				String address=job.getString("address");
				String crossStreet=job.getString("crossStreet");
				hl.location_details.setText(address+" "+crossStreet);
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}	
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		String item=(String) arg0.getItemAtPosition(position);
		
		try {
				JSONObject jobj=new JSONObject(item);
				String place_id=jobj.getString("id");
				String place=jobj.getString("name");
				String location_data=jobj.getString("location");
				JSONObject job=new JSONObject(location_data);
				String address="";
				if(job.has("address")){
				 address=job.getString("address");
				}
				String crossStreet="";
				if(job.has("crossStreet")){
					crossStreet=job.getString("crossStreet");
				}
				
				place+=" "+address+" "+crossStreet;
				JSONObject jtemp=new JSONObject(jobj.getString("location"));
				String lat=jtemp.getString("lat");
				String lng=jtemp.getString("lng");				
				Intent intent=new Intent(FoursquareLocationActivity.this,MeetMeActivity.class);
				intent.putExtra("place", place);
				intent.putExtra("place_id", place_id);
				intent.putExtra("lat", lat);
				intent.putExtra("lng", lng);
				intent.putExtra("location_status", 1); 
				intent.putExtra("status_text", status_text);
				intent.putExtra("date_time", date_time);
				intent.putExtra("tab_select", 1);
				//Toast.makeText(getBaseContext(), intent.toString(), Toast.LENGTH_SHORT).show();
				FoursquareLocationActivity.this.startActivity(intent);
				finish();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		
		
		//Log.v("Item",item);
	}

}
