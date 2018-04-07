package com.newagesmb.version3;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;

public class GaydarKeywordSettingsActivity extends Activity {
	public static String PREF_NAME = "men_pref";
	String keywords;
	EditText keword_txt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gaydar_keyword_settings);
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		keywords=sp.getString("set_keywords", "");
		if(!keywords.isEmpty() && keywords.equals("")){
			keword_txt=(EditText) findViewById(R.id.keyword_txt);
			keword_txt.setText(keywords);
		}
		findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				keword_txt=(EditText) findViewById(R.id.keyword_txt);
				keywords=keword_txt.getText().toString();
				SharedPreferences.Editor editor= getSharedPreferences(PREF_NAME, 0).edit();
				editor.putString("set_keywords", keywords);
				editor.commit();
				callBack();
			}
		});
		
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor= getSharedPreferences(PREF_NAME, 0).edit();
				editor.putString("set_keywords", "");
				editor.commit();
				callBack();
			}
		});
	}

	public void callBack(){
		Intent intent=new Intent(GaydarKeywordSettingsActivity.this, FilterActivity.class);
		GaydarKeywordSettingsActivity.this.startActivity(intent);
		finish();
	}

}
