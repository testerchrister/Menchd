package com.newagesmb.version3;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GaydarNearByActivity extends Activity implements OnItemClickListener {
	public static String PREF_NAME = "men_pref";
	String login_id;
	int page_no=0;
	public ProgressDialog progress;
	public ImageLoader img_loader;
	String stringLatitude,stringLongitude, filter;
	int search_type;
	String defValue = new String();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gaydar_near_by);
		
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		login_id = sp.getString("id", defValue);
		GPSTracker gpsTracker = new GPSTracker(this);
		if (gpsTracker.canGetLocation()) {
			stringLatitude = String.valueOf(gpsTracker.latitude);
			stringLongitude = String.valueOf(gpsTracker.longitude);
			
			/*Log.v("Lat & Long", stringLatitude + ":" + stringLongitude);*/
		}else{
			stringLatitude="00.0";
			stringLongitude="00.0";
		}
		
		
		String s_type=getIntent().getStringExtra("type");
		search_type=Integer.parseInt(s_type);

		loadNearbyList(login_id,search_type);
	}

	public void loadNearbyList(String id,int type){
		NearbyList nb=new NearbyList();		
		nb.execute(id,String.valueOf(type));
	}
	public class Holder{
		TextView result_count,user_name,age_location,match_perc,ago;
		ImageView prof_img,chat_status,fav_status;
		ImageButton filter_btn;
		LinearLayout list_item;
	}
	public class NearbyList extends AsyncTask< String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
		String function_name="";	
		String id=params[0];
		int type=Integer.parseInt(params[1]);
		
		JSONObject jobj=new JSONObject();
		String url=getString(R.string.json_url);
		String result="";
		try {
			
			switch (type) {
				case 1:
					function_name="nearby_search2";
					jobj.accumulate("lat", stringLatitude);
					jobj.accumulate("long", stringLongitude);
					jobj.accumulate("user_id", id);
					jobj.accumulate("page", page_no);
					break;
				case 2:
					function_name="visitors_search";
					jobj.accumulate("user_id", id);
					jobj.accumulate("page", page_no);
					break;
				case 3:
					function_name="filter_search";	
					SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
					jobj.accumulate("user_id", id);
					jobj.accumulate("page", page_no);
					jobj.accumulate("min_age", sp.getString("min_age", ""));	
					jobj.accumulate("max_age", sp.getString("max_age", ""));	
					jobj.accumulate("min_height", sp.getString("min_height", ""));	
					jobj.accumulate("max_height", sp.getString("max_height", ""));	
					jobj.accumulate("min_weight", sp.getString("min_weight", ""));	
					jobj.accumulate("max_weight", sp.getString("max_weight", ""));						
					jobj.accumulate("body_type", sp.getString("body_type", ""));	
					jobj.accumulate("ethnicity", sp.getString("ethnicity", ""));	
					jobj.accumulate("horoscope", sp.getString("horoscope", ""));	
					jobj.accumulate("smoke", sp.getString("smoke", ""));	
					jobj.accumulate("drink", sp.getString("drink", ""));	
					jobj.accumulate("weed", sp.getString("weed", ""));	
					jobj.accumulate("drug", sp.getString("drug", ""));	
					jobj.accumulate("diet", sp.getString("diet", ""));	
					jobj.accumulate("pets", sp.getString("pets", ""));	
					jobj.accumulate("college", sp.getString("college", ""));	
					jobj.accumulate("term", sp.getString("term", ""));	
					jobj.accumulate("income", sp.getString("income", ""));	
					jobj.accumulate("chat_status", sp.getString("chat_status", ""));	
					jobj.accumulate("keyword", sp.getString("set_keywords", ""));	
					jobj.accumulate("location", sp.getString("set_location", ""));	
					jobj.accumulate("latitude", sp.getString("latitude", ""));
					jobj.accumulate("longitude", sp.getString("longitude", ""));
					
					break;				
				default:
					function_name="nearby_search2";
					jobj.accumulate("user_id", id);
					jobj.accumulate("page", page_no);
					jobj.accumulate("lat", stringLatitude);
					jobj.accumulate("long", stringLongitude);
					break;
			}			
			
			HttpRequester hr=new HttpRequester();
			result=hr.POST(function_name, jobj, url);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
			
					
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(GaydarNearByActivity.this, "","Please wait...", true);
		}	
		
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			//Log.v("Gaydar Result",result);
			//Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
			searchListing(result);
		}	
	}

	public void searchListing(String result){
		try {
			JSONArray jobj=new JSONArray(result);
			Holder hd=new Holder();
			if(jobj.length()>0){
				
				hd.result_count=(TextView) findViewById(R.id.result_count);
				hd.result_count.setText(jobj.length()+" Results found");
				
				ArrayList<String> list=new ArrayList<String>();
				for(int i=0;i<jobj.length();i++){
					list.add(jobj.getJSONObject(i).toString());
				}
				ListView lv=(ListView) findViewById(R.id.list_view);
				CustomAdapter adapter=new CustomAdapter(GaydarNearByActivity.this, R.layout.gaydar_list_item, list);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(GaydarNearByActivity.this);
				
			}else{
				hd.result_count=(TextView) findViewById(R.id.result_count);
				hd.result_count.setText(jobj.length()+" Results found");
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
			img_loader = new ImageLoader(this.mContext);
		}
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final Holder hd=new Holder();
			if (convertView == null) {
				
				LayoutInflater inflater = ((Activity) mContext)
						.getLayoutInflater();
				convertView = inflater.inflate(layoutResourceId, parent, false);

			}
			try {
				
				final JSONObject jobj = new JSONObject(mlist.get(position).toString());
				
				hd.prof_img=(ImageView) convertView.findViewById(R.id.prof_pic);
				
				img_loader.DisplayImage(jobj.getString("thumb_image"), hd.prof_img);
				hd.user_name=(TextView) convertView.findViewById(R.id.user_name);
				hd.user_name.setText(jobj.getString("username"));
				hd.age_location=(TextView) convertView.findViewById(R.id.age_location);
				hd.age_location.setText(jobj.getString("age")+", "+jobj.getString("city"));
				hd.match_perc=(TextView) convertView.findViewById(R.id.perc);
				if(!jobj.getString("perc").isEmpty() && !jobj.getString("perc").equals("null")){
					Float per=Float.parseFloat(jobj.getString("perc"));
					hd.match_perc.setText(String.valueOf(Math.round(per))+"%");
				}
				hd.chat_status=(ImageView) convertView.findViewById(R.id.online);
				if(jobj.getString("chat_status").equals("Y")){
					
					hd.chat_status.setImageResource(R.drawable.online);
				}else{
					hd.chat_status.setImageResource(R.drawable.offline);
				}
				hd.fav_status=(ImageView) convertView.findViewById(R.id.fav_btn);
				if(jobj.getString("fav_status").equals("Y")){
					
					hd.fav_status.setImageResource(R.drawable.addfav60);
				}else{
					hd.fav_status.setImageResource(R.drawable.addfav_gray60);
				}
				hd.ago=(TextView) convertView.findViewById(R.id.ago);
				if(search_type==2){
					
					hd.ago.setText(jobj.getString("ago"));
				}else{
					hd.ago.setVisibility(View.INVISIBLE);
				}
				
				hd.fav_status.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String rec_usr_id = null;
						String rec_status = null;
						try {
							JSONObject tmp_usr = new JSONObject(mlist.get(
									position).toString());
							/*Log.v("Before Updates:", tmp_usr.toString());*/
							rec_usr_id = tmp_usr.getString("id");
							rec_status = tmp_usr.getString("fav_status");
							tmp_usr.remove("fav_status");
							if (rec_status.equals("Y")) {
								tmp_usr.put("fav_status", "N");
								hd.fav_status
										.setImageResource(R.drawable.addfav_gray60);

							} else {
								tmp_usr.put("fav_status", "Y");
								hd.fav_status
										.setImageResource(R.drawable.addfav60);
							}
							/*Log.v("After Updates:", tmp_usr.toString());*/
							/* rec_users.remove(position); */
							mlist.set(position, tmp_usr.toString());

						} catch (JSONException e) {
							
							e.printStackTrace();
						}

						AddFaviriteRecommandations fav_reco = new AddFaviriteRecommandations();
						String json_url = getString(R.string.json_url);
						fav_reco.execute(json_url, rec_usr_id);
						
					}
				});
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			return convertView;
		}
	}
	class AddFaviriteRecommandations extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			

			//String result = addFavorite(params[0], params[1], params[2]);
			InputStream inputStream = null;
			String result = "";
			try {
				HttpRequester hr=new HttpRequester();
				JSONObject job=new JSONObject();
				job.accumulate("user_id", params[1]);
				job.accumulate("login_id", login_id);
				result=hr.POST("add_fav", job, params[0]);
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progress = ProgressDialog.show(GaydarNearByActivity.this, "",
					"Processing Request..", true);
		}

		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();

			String message = "Favorite status chaged";
			try {
				JSONObject jonj = new JSONObject(result);
				message = jonj.getString("message");
			} catch (JSONException e) {
				
				e.printStackTrace();
			}

			Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT)
					.show();

		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		String item=(String) arg0.getItemAtPosition(position);
		try {
			JSONObject job=new JSONObject(item);
			Intent prof_intent = new Intent(GaydarNearByActivity.this,ProfileTabActivity.class);
			prof_intent.putExtra("profile_id", job.getString("id"));
			prof_intent.putExtra("search_type", search_type);
			GaydarNearByActivity.this.startActivity(prof_intent);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		//Log.v("Itemssssssssssssssss",item);
		
	}

}
