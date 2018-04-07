package com.newagesmb.version3;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MeetMeOpenDateActivity extends Activity implements OnItemClickListener{
	public static String PREF_NAME = "men_pref";
	String defValue = new String();
	ProgressDialog progress;
	String user_id;
	
	int page=0;
	public ImageLoader img_loader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meet_me_open_date);
		String latitude, longitude;
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		user_id=sp.getString("id", defValue);
		
		GPSTracker gpsTracker = new GPSTracker(this);
		if (gpsTracker.canGetLocation()) {
			latitude = String.valueOf(gpsTracker.latitude);
			longitude = String.valueOf(gpsTracker.longitude);
		}else{
			latitude="00.0";
			longitude="00.0";
		}
		String url=getString(R.string.json_url);
		
		OpenDates od=new OpenDates();
		od.execute(user_id,latitude,longitude);
		
		
		
	}

	class OpenDates extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String result="";
			try {
				JSONObject jsonObject=new JSONObject();
				jsonObject.accumulate("user_id", params[0]);
				jsonObject.accumulate("lat", params[1]);
				jsonObject.accumulate("long", params[2]);
				String url=getString(R.string.json_url);
				
				HttpRequester hr=new HttpRequester();
				result=hr.POST("get_status", jsonObject, url);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(MeetMeOpenDateActivity.this, "","Please wait...", true);
		}	
		
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			//Log.v("Result",result);
			setOpenList(result);
			
		}	
		
		
	}
	class Holder{
		ImageView open_user_image;
		TextView user_name,time_location,meet_date,meet_day_month,message;
		ImageView want_meet;
		
		
	}
	public void setOpenList(String result){
		try {
			JSONArray jarray=new JSONArray(result);
			
			if(jarray.length()>0){
				ArrayList<String> list=new ArrayList<String>();
				for(int i=0;i<jarray.length();i++){
					list.add(jarray.getJSONObject(i).toString());
				}
				//Toast.makeText(getApplicationContext(), list.toString(), Toast.LENGTH_SHORT).show();
				
				ListView lv=(ListView) findViewById(R.id.list_view);
				CustomAdapter adapter=new CustomAdapter(MeetMeOpenDateActivity.this, R.layout.meetme_opendate_item, list);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(MeetMeOpenDateActivity.this);
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
				
				hl.open_user_image=(ImageView) convertView.findViewById(R.id.open_user_image);
				if(!jobj.getString("eimage").equals("")){
					img_loader.DisplayImage(jobj.getString("eimage"), hl.open_user_image);
				}
				
				hl.user_name=(TextView) convertView.findViewById(R.id.user_name);
				hl.user_name.setText(jobj.getString("username"));
				
				hl.time_location=(TextView) convertView.findViewById(R.id.time_location);
				String location=jobj.getString("time")+" @ "+jobj.getString("place");
				if(location.length()>28){
					location.subSequence(0, 25);
					location=location+"...";
				}
				hl.time_location.setText(location);
				
				hl.meet_date=(TextView) convertView.findViewById(R.id.meet_date);
				hl.meet_date.setText(jobj.getString("date"));
				
				hl.meet_day_month=(TextView) convertView.findViewById(R.id.meet_day_month);
				hl.meet_day_month.setText(jobj.getString("day")+" "+jobj.getString("month"));
				
				hl.message=(TextView) convertView.findViewById(R.id.message);
				hl.message.setText(jobj.getString("status_text"));
				
				hl.want_meet=(ImageView) convertView.findViewById(R.id.want_meet);
				String want_meet_status=jobj.getString("want_meet_status");
				if(want_meet_status.equals("Y")){
					
					hl.want_meet.setImageResource(R.drawable.btncancelmeet);
				}else{
					
					hl.want_meet.setImageResource(R.drawable.btnwanttomeet);
					
				}
				final String meet_id=jobj.getString("meet_id");
				//final String user_id=jobj.getString("user_id");
				hl.want_meet.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						try {
							JSONObject job=new JSONObject(mlist.get(position).toString());
							
							if(job.getString("want_meet_status").equals("N")){
								hl.want_meet.setImageResource(R.drawable.btncancelmeet);
								job.remove("want_meet_status");
								job.put("want_meet_status", "Y");
								
							}else{
								
								
								hl.want_meet.setImageResource(R.drawable.btnwanttomeet);
								job.remove("want_meet_status");
								job.put("want_meet_status", "N");
								
								
							}
							mlist.set(position,job.toString());
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
						
						WantToMeet wtm=new WantToMeet();
						wtm.execute(user_id,meet_id);
						
					}
				});
				
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
			return convertView;
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		String item=(String) arg0.getItemAtPosition(position);
		
		Intent intent=new Intent(MeetMeOpenDateActivity.this, MeetMeDetailsActivity.class);
		intent.putExtra("meetInfo", item);
		intent.putExtra("from_tab", "open");
		MeetMeOpenDateActivity.this.startActivity(intent);
		
	}
	
	class WantToMeet extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String result="";
			HttpRequester hr=new HttpRequester();
			String url=getString(R.string.json_url);
			JSONObject jsonObject=new JSONObject();
			try {
				jsonObject.accumulate("user_id", params[0]);
				jsonObject.accumulate("meet_id", params[1]);
				result=hr.POST("meet_request", jsonObject, url);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(MeetMeOpenDateActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			//Log.v("Want To Meet Result",result);
			try {
				JSONObject jobj=new JSONObject(result);
				
				if(jobj.getBoolean("status")){
					Toast.makeText(getApplicationContext(), "Meet request send!", Toast.LENGTH_SHORT).show();
					
				}else{
					Toast.makeText(getApplicationContext(), "Meet request cancelled!", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
		}	
		
		
	}
	
	

}
