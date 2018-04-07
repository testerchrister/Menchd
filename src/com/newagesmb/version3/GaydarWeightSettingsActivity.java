package com.newagesmb.version3;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class GaydarWeightSettingsActivity extends Activity {

	public static String PREF_NAME = "men_pref";
	TextView min_weight, max_weight;
	int min=0,max=0;
	static final int DATE_DIALOG_ID = 0;
	static final int MIN=1, MAX=2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gaydar_weight_settings);
		min_weight=(TextView) findViewById(R.id.minimum_weight_txt);
		max_weight=(TextView) findViewById(R.id.maximum_weight_txt);
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		String min_val=sp.getString("min_weight", "");
		String max_val=sp.getString("max_weight", "");
		if(!min_val.equals("")){
			min_weight.setText(sp.getString("min_weight", "")+" lbs");
		}
		if(!max_val.equals("")){
			max_weight.setText(sp.getString("max_weight", "")+" lbs");
		}
		
		if(!sp.getString("max_weight", "").equals("")){
			max=Integer.parseInt(sp.getString("max_weight", "0"));
		}
		if(!sp.getString("min_weight", "").equals("")){
			min=Integer.parseInt(sp.getString("min_weight", "0"));
		}
		
		findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				if(min!=0 && max!=0 && min>max){
					Toast.makeText(GaydarWeightSettingsActivity.this, "Minimum weight must be less than Maximum weight", Toast.LENGTH_SHORT).show();
					return;
					
				}
				weightSettings(min,max);
				callBack();

			}
		});
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				min_weight.setText("");
				max_weight.setText("");
				weightSettings(0,0);
				callBack();
			}
		});
		findViewById(R.id.minimum_weight).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showWeights(MIN);
				
			}
		});
		findViewById(R.id.maximum_weight).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showWeights(MAX);
				
			}
		});
	}
	
	public void callBack(){
		Intent intent=new Intent(GaydarWeightSettingsActivity.this, FilterActivity.class);
		GaydarWeightSettingsActivity.this.startActivity(intent);
		finish();
	}
	public void weightSettings(int min, int max){
		SharedPreferences.Editor editor= getSharedPreferences(PREF_NAME, 0).edit();
		if(min!=0){
			editor.putString("min_weight", String.valueOf(min));
		}else{
			editor.putString("min_weight", "");
		}
		if(max!=0){
			editor.putString("max_weight", String.valueOf(max));
		}else{
			editor.putString("max_weight", "");
		}
		
		editor.commit();
		return;
	}
	public void showWeights(final int weight_type){
		 final Dialog d = new Dialog(GaydarWeightSettingsActivity.this);
		 d.setTitle("Select Weight");
		 d.setContentView(R.layout.weight_picker);
		 
		 final NumberPicker np=(NumberPicker) d.findViewById(R.id.weightPicker);
		 np.setMaxValue(30);
		 np.setMinValue(6);
		 np.setDisplayedValues(new String[]{ "60 lbs", "70 lbs", "80 lbs", "90 lbs", "100 lbs", "110 lbs", "120 lbs", "130 lbs", "140 lbs", "150 lbs", "160 lbs", "170 lbs", "180 lbs", "190 lbs", "200 lbs", "210 lbs", "220 lbs", "230 lbs", "240 lbs", "250 lbs", "260 lbs", "270 lbs", "280 lbs", "290 lbs", "300 lbs"});
		 Button b1 = (Button) d.findViewById(R.id.set_weight);
		 b1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				switch(weight_type){
				case 1:
					min=np.getValue();
					min=(min*10);
					min_weight=(TextView) findViewById(R.id.minimum_weight_txt);
					min_weight.setText(String.valueOf(min)+" lbs");
					
						break;
				case 2:
					max=np.getValue();
					max=(max*10);
					max_weight=(TextView) findViewById(R.id.maximum_weight_txt);
					max_weight.setText(String.valueOf(max)+" lbs");
					break;
				}
				
				d.dismiss();
			}
		});
		  d.show();
		 
	}

}
