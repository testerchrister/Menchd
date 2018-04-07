package com.newagesmb.version3;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class GaydarLocationSettingsActivity extends Activity {

	String location=null;
	private RadioButton radioSexButton;
	public static String PREF_NAME = "men_pref";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gaydar_location_settings);
		RadioGroup location_grp=(RadioGroup) findViewById(R.id.radio_location);
		int selectedId = location_grp.getCheckedRadioButtonId();
		radioSexButton = (RadioButton) findViewById(selectedId);
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		location=sp.getString("set_location", "");
		if(!location.isEmpty() && !location.equals("")){
			if(location.equals("any where")){
				RadioButton rb1=(RadioButton) findViewById(R.id.location_anywhere);
				rb1.setChecked(true);
			}
			if(location.equals("my location")){
				RadioButton rb2=(RadioButton) findViewById(R.id.location_near_me);
				rb2.setChecked(true);
			}
			if(location.equals("current")){
				RadioButton rb3=(RadioButton) findViewById(R.id.location_user_current);
				rb3.setChecked(true);
			}

		}
		findViewById(R.id.location_anywhere).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				location="any where";
				
			}
		});
		findViewById(R.id.location_near_me).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				location="my location";
				
			}
		});
		findViewById(R.id.location_user_current).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				location="current";
				
			}
		});
		findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor= getSharedPreferences(PREF_NAME, 0).edit();
				editor.putString("set_location", location);
				editor.commit();
				callBack();
			}
		});
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor= getSharedPreferences(PREF_NAME, 0).edit();
				editor.putString("set_location", "");
				editor.commit();
				callBack();
			}
		});
		
		
	}
	
	public void callBack(){
		Intent intent=new Intent(GaydarLocationSettingsActivity.this, FilterActivity.class);
		GaydarLocationSettingsActivity.this.startActivity(intent);
		finish();
	}
}
