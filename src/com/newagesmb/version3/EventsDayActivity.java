package com.newagesmb.version3;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventsDayActivity extends Activity implements OnItemClickListener {
	ImageLoader img_loader;
	String last_date;
	boolean back_cal=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events_day);
		Bundle extra=getIntent().getExtras();
		if(extra!=null){
			String events=getIntent().getStringExtra("events");
			if(getIntent().hasExtra("show_calender_icon")){
				if(getIntent().getStringExtra("show_calender_icon").equals("Y")){
					back_cal=true;
				}
			}
			if(!back_cal){
				findViewById(R.id.btn_calendar).setVisibility(View.INVISIBLE);
			}else{
				findViewById(R.id.clear_btn).setVisibility(View.INVISIBLE);
			}
			last_date="";
			setEventList(events);
		}
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		findViewById(R.id.btn_calendar).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
	}
	public void setEventList(String events){
		try {
			JSONArray jarray=new JSONArray(events);
			
			if(jarray.length()>0){
				ArrayList<String> list=new ArrayList<String>();
				for(int i=0;i<jarray.length();i++){
					list.add(jarray.getJSONObject(i).toString());
				}
				//Toast.makeText(getApplicationContext(), list.toString(), Toast.LENGTH_SHORT).show();
				
				ListView lv=(ListView) findViewById(R.id.list_view);
				CustomAdapter adapter=new CustomAdapter(EventsDayActivity.this, R.layout.activity_event_list_item, list);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(EventsDayActivity.this);
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
	}
	class Holder{
		TextView event_title, event_location, event_time,day,week,month_year;
		ImageView event_img;
		RelativeLayout day_title;
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
			try{
				JSONObject jobj = new JSONObject(mlist.get(position).toString());
				hl.event_img=(ImageView) convertView.findViewById(R.id.event_img);
				if(jobj.has("eimage") && !jobj.getString("eimage").equals("")){
					img_loader.DisplayImage(jobj.getString("eimage"), hl.event_img);
					
				}else{
					hl.event_img.setImageResource(Color.WHITE);
				}
				hl.event_title=(TextView) convertView.findViewById(R.id.event_title);
				hl.event_title.setText(jobj.getString("event_title"));
				
				hl.event_location=(TextView) convertView.findViewById(R.id.event_place);
				hl.event_location.setText(jobj.getString("event_place"));
				
				hl.event_time=(TextView) convertView.findViewById(R.id.event_time);
				hl.event_time.setText(jobj.getString("event_time"));
				
				hl.day_title=(RelativeLayout) convertView.findViewById(R.id.day_title);
				
				if(!last_date.equals(jobj.getString("edate"))||position==0){
					last_date=jobj.getString("edate");
					hl.day_title.setVisibility(View.VISIBLE);
					String[] temp_event_date=new String[4];
					temp_event_date=jobj.getString("edate").split(",");
					hl.day=(TextView) convertView.findViewById(R.id.day);
					hl.day.setText(temp_event_date[0]);
					
					hl.week=(TextView) convertView.findViewById(R.id.week);
					hl.week.setText(temp_event_date[1]);
					
					hl.month_year=(TextView) convertView.findViewById(R.id.month_year);
					hl.month_year.setText(temp_event_date[2]+" "+temp_event_date[3]);
				}else{
					hl.day_title.setVisibility(View.GONE);
				}
				
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		String item=(String) arg0.getItemAtPosition(position);
		Intent intent=new Intent(EventsDayActivity.this, EventDetailsActivity.class);
		intent.putExtra("events", item);
		EventsDayActivity.this.startActivity(intent);
		
	}

}
