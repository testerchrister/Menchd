package com.newagesmb.version3;

import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MeetMeDetailsActivity extends Activity {
	public static String PREF_NAME = "men_pref";
	String meetInfo, from_tab;
	TextView user_name,age_city,day_date,time,status_text;
	ImageView open_user_image;
	ImageButton delete_meetme;
	 private GoogleMap googleMap;
	 public ImageLoader img_loader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meet_me_details);
		try{
			Bundle extra =getIntent().getExtras();			
			if(extra!=null){
				meetInfo=getIntent().getStringExtra("meetInfo");
				from_tab=getIntent().getStringExtra("from_tab");
				Log.v("Meet Details",meetInfo);
				delete_meetme=(ImageButton) findViewById(R.id.delete_meetme);
				if(from_tab.equals("open")){
					// No need of remove button;
					delete_meetme.setVisibility(View.INVISIBLE);
				}else{
					delete_meetme.setVisibility(View.VISIBLE);
				}
				JSONObject jobj=new JSONObject(meetInfo);
				user_name=(TextView) findViewById(R.id.user_name);
				if(!jobj.getString("username").equals("") && jobj.getString("username")!=null){
					user_name.setText(jobj.getString("username"));
				}
				age_city=(TextView) findViewById(R.id.age_city);
				String age=jobj.getString("age");
				String age_city_data="";
				if(!age.equals("") && age!=null){
					age_city_data=age;
				}
				String city=jobj.getString("city");
				if(!city.equals("") && city!=null){
					age_city_data+=", "+city;
				}
				age_city.setText(age_city_data);
				
				day_date=(TextView) findViewById(R.id.day_date);
				day_date.setText(jobj.getString("day")+" "+jobj.getString("month")+" "+jobj.getString("date"));
				
				time=(TextView) findViewById(R.id.time);
				time.setText(jobj.getString("time"));
				
				status_text=(TextView) findViewById(R.id.status_text);
				status_text.setText(jobj.getString("place"));
				
				open_user_image=(ImageView) findViewById(R.id.open_user_image);
				
				img_loader = new ImageLoader(getApplicationContext());
				img_loader.DisplayImage(jobj.getString("eimage"), open_user_image);
				
				
				String lat, lng, location;
				lat=jobj.getString("lat");
				lng=jobj.getString("long");
				//location=jobj.getString("city");
				location=jobj.getString("username")+" "+jobj.getString("day")+" "+jobj.getString("month")+" "+jobj.getString("date");
				
				initilizeMap(lat,lng,location);
				googleMap.setMyLocationEnabled(true);
				
				
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
		findViewById(R.id.message_meet).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try{
					JSONObject jobj=new JSONObject(meetInfo);
					Intent convesationIntent=new Intent(MeetMeDetailsActivity.this,ConversationActivity.class);
					
					convesationIntent.putExtra("receiver_id", jobj.getString("user_id"));
					SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
					String defValue = new String();
					String login_id=sp.getString("id", defValue);
					String loginimage=sp.getString("image", defValue);
					convesationIntent.putExtra("sender_id", login_id);
					convesationIntent.putExtra("login_image", loginimage);
					convesationIntent.putExtra("user_image", jobj.getString("eimage"));
					convesationIntent.putExtra("username", jobj.getString("username"));
					convesationIntent.putExtra("age", jobj.getString("age"));
					convesationIntent.putExtra("perce", jobj.getString("perc"));
					convesationIntent.putExtra("city", jobj.getString("city"));
					convesationIntent.putExtra("from_list", true);
					MeetMeDetailsActivity.this.startActivity(convesationIntent);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				
				
			}
		});
		
	}

	@SuppressLint("NewApi")
	private void initilizeMap(String lat,String lng,String location) {
		
		if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
         // latitude and longitude
            double latitude =Double.parseDouble(lat);
            double longitude =Double.parseDouble(lng);
             
            // create marker
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(location);
             
            // adding marker
            googleMap.addMarker(marker);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitude, longitude)).zoom(12).build();
     
            	googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
	}

}
