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
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MeetMeMyDateActivity extends Activity {
	public static String PREF_NAME = "men_pref";
	ProgressDialog progress;
	String defValue = new String();
	int page=0;
	public ImageLoader img_loader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meet_me_my_date);
		String user_id,latitude, longitude;
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
		
		MyDepens md=new MyDepens();
		md.execute(user_id,latitude,longitude);
	}
	
	class MyDepens extends AsyncTask<String, Void, String>{

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
				
				result=hr.POST("get_my_status", jsonObject, url);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			progress = ProgressDialog.show(MeetMeMyDateActivity.this, "","Please wait...", true);
		}	
		
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			
			setOpenList(result);
			Log.v("Result",result);
		}	
		
		
	}
	
	public void setOpenList(String result){
		try {
			JSONArray jarray=new JSONArray(result);
			if(jarray.length()>0){
				ArrayList<String> list=new ArrayList<String>();
				for(int i=0;i<jarray.length();i++){
					list.add(jarray.getJSONObject(i).toString());
				}
				ListView lv=(ListView) findViewById(R.id.list_view1);
				CustomAdapter adapter=new CustomAdapter(MeetMeMyDateActivity.this, R.layout.meetme_mydate_item, list);
				lv.setAdapter(adapter);
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
	}
	class Holder{
		ImageView open_user_image;
		TextView user_name,time_location,meet_date,meet_day_month,message,people_interest;
		ImageView confirm_btn;
		
		
	}
	
	class CustomAdapter extends ArrayAdapter<String> {
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
			
			if (convertView == null) {
				
				LayoutInflater inflater = ((Activity) mContext)
						.getLayoutInflater();
				convertView = inflater.inflate(layoutResourceId, parent, false);

			}
			Holder hl=new Holder();
			try {
				JSONObject jobj = new JSONObject(mlist.get(position).toString());
				hl.confirm_btn=(ImageView) convertView.findViewById(R.id.confirm_btn);
				hl.open_user_image=(ImageView) convertView.findViewById(R.id.open_user_image);
				hl.people_interest=(TextView) convertView.findViewById(R.id.people_interest);
				hl.time_location=(TextView) convertView.findViewById(R.id.time_location);
				hl.meet_date=(TextView) convertView.findViewById(R.id.meet_date);
				hl.meet_day_month=(TextView) convertView.findViewById(R.id.meet_day_month);
				
				
					
					if(jobj.getString("confirm").equals("Y")){
						hl.confirm_btn.setVisibility(View.INVISIBLE);
						if(!jobj.getString("confirm_image").equals("")){
							img_loader.DisplayImage(jobj.getString("confirm_image"), hl.open_user_image);
						}
						
						hl.people_interest.setText(jobj.getString("confirm_user"));
						hl.people_interest.setTextColor(Color.BLACK);
					}else{
						if(jobj.getInt("status_count")==0){
							hl.confirm_btn.setVisibility(View.INVISIBLE);
						}else{
							hl.confirm_btn.setVisibility(View.VISIBLE);
						}
						hl.open_user_image.setImageResource(R.drawable.confirmthumb);
						hl.people_interest.setTextColor(Color.RED);
						hl.people_interest.setText(jobj.getString("status_count")+" people interested");
						final String meet_id=jobj.getString("meet_id");
						hl.confirm_btn.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								// Call activity for list requested users list
								Intent intent=new Intent(MeetMeMyDateActivity.this, MeetMeSelectUsersActivity.class);
								intent.putExtra("meet_id", meet_id);
								MeetMeMyDateActivity.this.startActivity(intent);
								
							}
						});
					}

					
				
				
				hl.time_location.setText(jobj.getString("time")+" @ "+jobj.getString("place"));
				hl.meet_date.setText(jobj.getString("date"));
				hl.meet_day_month.setText(jobj.getString("day")+" "+jobj.getString("month"));
				

				
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
			return convertView;
		}
	}

}
