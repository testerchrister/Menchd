package com.newagesmb.version3;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class GaydarHeightSettingsActivity extends Activity {
	public static String PREF_NAME = "men_pref";
	TextView min_height, max_height;
	int min=0,max=0;
	static final int DATE_DIALOG_ID = 0;
	static final int MIN=1, MAX=2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gaydar_height_settings);
		min_height=(TextView) findViewById(R.id.minimum_height_txt);
		max_height=(TextView) findViewById(R.id.maximum_height_txt);
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		String min_val=sp.getString("min_height", "");
		String max_val=sp.getString("max_height", "");
		if(!min_val.equals("")){
			min_height.setText(sp.getString("min_height", "")+"cm");
		}
		if(!max_val.equals("")){
			max_height.setText(sp.getString("max_height", "")+"cm");
		}
		
		if(!sp.getString("max_height", "").equals("")){
			max=Integer.parseInt(sp.getString("max_height", "0"));
		}
		if(!sp.getString("min_height", "").equals("")){
			min=Integer.parseInt(sp.getString("min_height", "0"));
		}
		findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				if(min!=0 && max!=0 && min>max){
					Toast.makeText(GaydarHeightSettingsActivity.this, "Minimum height must be less than Maximum height", Toast.LENGTH_SHORT).show();
					return;
					
				}
				heightSettings(min,max);
				callBack();

			}
		});
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				min_height.setText("");
				max_height.setText("");
				heightSettings(0,0);
				callBack();
			}
		});
		findViewById(R.id.minimum_height).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showHeights(MIN);
				
			}
		});
		findViewById(R.id.maximum_height).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showHeights(MAX);
				
			}
		});
	}

	public void callBack(){
		Intent intent=new Intent(GaydarHeightSettingsActivity.this, FilterActivity.class);
		GaydarHeightSettingsActivity.this.startActivity(intent);
		finish();
	}
	public void heightSettings(int min, int max){
		SharedPreferences.Editor editor= getSharedPreferences(PREF_NAME, 0).edit();
		if(min!=0){
			editor.putString("min_height", String.valueOf(min));
		}else{
			editor.putString("min_height", "");
		}
		if(max!=0){
			editor.putString("max_height", String.valueOf(max));
		}else{
			editor.putString("max_height", "");
		}
		
		editor.commit();
		return;
	}
	public void showHeights(final int height_type){
		 final Dialog d = new Dialog(GaydarHeightSettingsActivity.this);
		 d.setTitle("Select Height");
		 d.setContentView(R.layout.height_picker);
		 
		 final NumberPicker np=(NumberPicker) d.findViewById(R.id.heightPicker);
		 np.setMaxValue(23);
		 np.setMinValue(1);
		 np.setDisplayedValues(new String[]{ "125cm", "130cm", "135cm", "140cm", "145cm", "150cm", "155cm", "160cm", "165cm", "170cm", "175cm", "180cm", "185cm", "190cm", "195cm", "200cm", "205cm", "210cm", "215cm", "220cm", "225cm", "230cm", "235cm"});
		 Button b1 = (Button) d.findViewById(R.id.set_height);
		 b1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				switch(height_type){
				case 1:
					min=np.getValue();
					min=(min*5)+120;
					min_height=(TextView) findViewById(R.id.minimum_height_txt);
					min_height.setText(String.valueOf(min)+"cm");
					
						break;
				case 2:
					max=np.getValue();
					max=(max*5)+120;
					max_height=(TextView) findViewById(R.id.maximum_height_txt);
					max_height.setText(String.valueOf(max)+"cm");
					break;
				}
				
				d.dismiss();
			}
		});
		  d.show();
		 
	}
    @Override 
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
        	Context context;
        	context = this.getApplicationContext();
            return null;//new DatePickerDialog(this,mDateSetListener, mYear, mMonth, mDay);

       
        }
        return null;
    }

}
