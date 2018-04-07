package com.newagesmb.version3;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class GaydarAgeSettingsActivity extends Activity {

	public static String PREF_NAME = "men_pref";
	EditText min_age, max_age;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gaydar_age_settings);
		min_age=(EditText) findViewById(R.id.minimum_age_txt);
		max_age=(EditText) findViewById(R.id.maximum_age_txt);
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		min_age.setText(sp.getString("min_age", ""));
		max_age.setText(sp.getString("max_age", ""));
		findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String minage,maxage;
				minage=min_age.getText().toString();
				maxage=max_age.getText().toString();
				if(!minage.isEmpty() && !minage.equals("") && !maxage.isEmpty() && !maxage.equals("") ){
					if(Integer.parseInt(minage)>Integer.parseInt(maxage)){
						Toast.makeText(GaydarAgeSettingsActivity.this, "Minimum age must be less than Maximum age", Toast.LENGTH_SHORT).show();
						min_age.setText("");
						max_age.setText("");
						return;
						
					}
					
				}
				ageSettings(minage,maxage);
				callBack();

			}
		});
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				min_age.setText("");
				max_age.setText("");
				ageSettings("","");
				callBack();
			}
		});
		
	}
	
	public void callBack(){
		Intent intent=new Intent(GaydarAgeSettingsActivity.this, FilterActivity.class);
		GaydarAgeSettingsActivity.this.startActivity(intent);
		finish();
	}
	public void ageSettings(String min, String max){
		SharedPreferences.Editor editor= getSharedPreferences(PREF_NAME, 0).edit();
		editor.putString("min_age", min);
		editor.putString("max_age", max);
		editor.commit();
		return;
	}

	
	
}
