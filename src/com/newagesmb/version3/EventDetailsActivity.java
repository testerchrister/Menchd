package com.newagesmb.version3;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetailsActivity extends Activity {
	TextView day,week,month_year,event_title,event_location,event_time,event_description;
	ImageView event_img;
	ImageLoader img_loader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_details);
		Bundle extra=getIntent().getExtras();
		if(extra!=null){
			String events=getIntent().getStringExtra("events");
			Log.v("Events",events);
			
			setEventDetails(events);
		}
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
	}

	public void setEventDetails(String details){
		try {
			JSONObject jobj = new JSONObject(details);
			event_img=(ImageView) findViewById(R.id.event_img);
			
			
			if(jobj.has("eimage") && !jobj.getString("eimage").equals("")){
				img_loader=new ImageLoader(getApplicationContext());
				img_loader.DisplayImage(jobj.getString("eimage"), event_img);
				
			}
			event_title=(TextView) findViewById(R.id.event_title);
			event_title.setText(jobj.getString("event_title"));
			
			event_location=(TextView) findViewById(R.id.event_location);
			event_location.setText(jobj.getString("event_place"));
			
			event_time=(TextView) findViewById(R.id.event_time);
			event_time.setText(jobj.getString("event_time"));
			
				String[] temp_event_date=new String[4];
			temp_event_date=jobj.getString("edate").split(",");
			day=(TextView) findViewById(R.id.day);
			day.setText(temp_event_date[0]);
			
			week=(TextView) findViewById(R.id.week);
			week.setText(temp_event_date[1]);
			
			month_year=(TextView) findViewById(R.id.month_year);
			month_year.setText(temp_event_date[2]+" "+temp_event_date[3]);
			
			event_description=(TextView) findViewById(R.id.event_description);
			event_description.setText(jobj.getString("event_desc"));
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
}
