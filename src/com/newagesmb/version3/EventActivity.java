package com.newagesmb.version3;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class EventActivity extends Activity {

	String current_date;
	TextView month_year_lbl;
	ImageView next_month,previous_month;
	Button btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_10, btn_11, btn_12, btn_13, btn_14, btn_15, btn_16, btn_17, btn_18, btn_19, btn_20, btn_21, btn_22,
	btn_23, btn_24, btn_25, btn_26, btn_27, btn_28, btn_29, btn_30, btn_31, btn_32, btn_33, btn_34, btn_35, btn_36, btn_37, btn_38, btn_39, btn_40, btn_41, btn_42;
	ProgressDialog progress;
	String events,user_id;
	ArrayList<String> monthly_event_list;
	int actual_current_date=0, actual_current_month=0, actual_current_year=0;
	public static String PREF_NAME = "men_pref";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		user_id=sp.getString("id", "0");
		monthly_event_list=new ArrayList<String>();
		initCalenderElements();
		current_date=getCurrentDate();		
		initCalender();
		
		
		findViewById(R.id.previous_month).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Previous Month", Toast.LENGTH_SHORT).show();
				
			}
		});
		findViewById(R.id.next_month).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(getApplicationContext(), "Next Month", Toast.LENGTH_SHORT).show();
			}
		});
	
		
		next_month.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				changeMonthandYear(current_date,"next");
			}
		});
		previous_month.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				changeMonthandYear(current_date,"prev");
			}
		});
		
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		findViewById(R.id.list_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(EventActivity.this, EventsDayActivity.class);
				intent.putExtra("events", monthly_event_list.toString());
				intent.putExtra("show_calender_icon", "Y");
				EventActivity.this.startActivity(intent);
				//finish();
			}
		});
		
	}
	public void callBack(){
		finish();
	}
	class GetMonthsEvents extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			HttpRequester hr=new HttpRequester();
			String url=getString(R.string.json_url);
			String result="";
			try{
				JSONObject json=new JSONObject();
				json.accumulate("userid", user_id);
				json.accumulate("year", params[1]);
				json.accumulate("month", params[0]);
				
				result=hr.POST("get_events", json, url);
			}catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(EventActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			fillCalender(result);
			
		}
		
	}
	public void initCalenderElements(){
		month_year_lbl=(TextView) findViewById(R.id.month_year_lbl);
		next_month=(ImageView) findViewById(R.id.next_month);
		previous_month=(ImageView) findViewById(R.id.previous_month);
		btn_1=(Button) findViewById(R.id.btn_1);
		btn_2=(Button) findViewById(R.id.btn_2);
		btn_3=(Button) findViewById(R.id.btn_3);
		btn_4=(Button) findViewById(R.id.btn_4);
		btn_5=(Button) findViewById(R.id.btn_5);
		btn_6=(Button) findViewById(R.id.btn_6);
		btn_7=(Button) findViewById(R.id.btn_7);
		btn_8=(Button) findViewById(R.id.btn_8);
		btn_9=(Button) findViewById(R.id.btn_9);
		btn_10=(Button) findViewById(R.id.btn_10);
		btn_11=(Button) findViewById(R.id.btn_11);
		btn_12=(Button) findViewById(R.id.btn_12);
		btn_13=(Button) findViewById(R.id.btn_13);
		btn_14=(Button) findViewById(R.id.btn_14);
		btn_15=(Button) findViewById(R.id.btn_15);
		btn_16=(Button) findViewById(R.id.btn_16);
		btn_17=(Button) findViewById(R.id.btn_17);
		btn_18=(Button) findViewById(R.id.btn_18);
		btn_19=(Button) findViewById(R.id.btn_19);
		btn_20=(Button) findViewById(R.id.btn_20);
		btn_21=(Button) findViewById(R.id.btn_21);
		btn_22=(Button) findViewById(R.id.btn_22);
		btn_23=(Button) findViewById(R.id.btn_23);
		btn_24=(Button) findViewById(R.id.btn_24);
		btn_25=(Button) findViewById(R.id.btn_25);
		btn_26=(Button) findViewById(R.id.btn_26);
		btn_27=(Button) findViewById(R.id.btn_27);
		btn_28=(Button) findViewById(R.id.btn_28);
		btn_29=(Button) findViewById(R.id.btn_29);
		btn_30=(Button) findViewById(R.id.btn_30);
		btn_31=(Button) findViewById(R.id.btn_31);
		btn_32=(Button) findViewById(R.id.btn_32);
		btn_33=(Button) findViewById(R.id.btn_33);
		btn_34=(Button) findViewById(R.id.btn_34);
		btn_35=(Button) findViewById(R.id.btn_35);
		btn_36=(Button) findViewById(R.id.btn_36);
		btn_37=(Button) findViewById(R.id.btn_37);
		btn_38=(Button) findViewById(R.id.btn_38);
		btn_39=(Button) findViewById(R.id.btn_39);
		btn_40=(Button) findViewById(R.id.btn_40);
		btn_41=(Button) findViewById(R.id.btn_41);
		btn_42=(Button) findViewById(R.id.btn_42);
		
	}
	public void initCalender(){
		
		int month_int=getDateSegment(current_date,"m");		
		String month_string=getMonthForInt(month_int);
		int year_int=getDateSegment(current_date,"y");
		month_year_lbl.setText(month_string+" "+String.valueOf(year_int));
		GetMonthsEvents gme=new GetMonthsEvents();
		gme.execute(String.valueOf(month_int+1),String.valueOf(year_int));
		
	}
	String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }
	@SuppressLint("SimpleDateFormat")
	String getCurrentDate(){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		String cur_date=dateFormat.format(date).toString();
		actual_current_date=getDateSegment(cur_date, "d");
		actual_current_month=getDateSegment(cur_date, "m");
		actual_current_year=getDateSegment(cur_date, "y");
		return cur_date;
	}
	int getDateSegment(String date,String segment){
		/*Date format should be yyyy/mm/dd*/
		String[] temp_date=new String[3];
		temp_date=date.split("/");
		int value;
		if(segment.equals("y")){
			value=Integer.parseInt(temp_date[0]);
		}else if(segment.equals("m")){
			value=Integer.parseInt(temp_date[1]);
			value--;
		}else{
			value=Integer.parseInt(temp_date[2]);
		}
		return value;
	}
	
	public void changeMonthandYear(String date,String nav){
		//date="2014/01/29";
		Date predefined;
		try {
			int month_int=getDateSegment(date,"m");
			predefined=new SimpleDateFormat("yyyy/MM/dd").parse(date);
			Calendar cal= Calendar.getInstance();
			cal.setTime(predefined);
			if(nav.equals("next")){
				
				cal.roll(Calendar.MONTH, true);
				if(month_int==11){
					cal.roll(Calendar.YEAR, true);
				}
			}else{
				// previous month
				//cal.add(Calendar.DATE, -1);
				cal.roll(Calendar.MONTH, false);
				if(month_int==0){
					cal.roll(Calendar.YEAR, false);
				}
			}
			String nav_date=cal.getTime().toString();
			nav_date=(String) DateFormat.format("yyyy/MM/dd", cal);
			current_date=nav_date;
			
			initCalender();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
	}
	public void fillCalender(String event){
		
		Date predefined;
		try {
			predefined = new SimpleDateFormat("yyyy/MM/dd").parse(current_date);
			Calendar cal= Calendar.getInstance();
			cal.setTime(predefined);
			int day, month, year;
			day=getDateSegment(current_date,"d");
			month=getDateSegment(current_date,"m");
			year=getDateSegment(current_date,"y");
			int first_day=getFirstDay(day,month, year);
			
			int number_of_days=getNumberOfDaysInMonth(day,month, year);
			int last_cell=0;
			JSONObject job=new JSONObject(event);
			JSONObject job_day_list=new JSONObject(job.getString("event").toString());
			
			for(int i=0;i<number_of_days;i++){
				Button btn=(Button) getDaycell(i,first_day);
				btn.setText(String.valueOf(i+1));
				final String event_list_json=job_day_list.getString(String.valueOf(i+1));
				JSONArray json_array=new JSONArray(event_list_json);
				
				if(json_array.length()>0){
					for(int j=0;j<json_array.length();j++){
						monthly_event_list.add(json_array.getString(j));
					}
					
					if(actual_current_date==i+1 && actual_current_month==month && actual_current_year==year){
						btn.setTextColor(Color.WHITE);
						btn.setBackgroundResource(R.drawable.eventbgsdate);
					}else{
						btn.setTextColor(Color.RED);
						btn.setBackgroundResource(R.drawable.eventbg);
					}
					
					btn.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// Call event listing Activity
							Intent intent=new Intent(EventActivity.this, EventsDayActivity.class);
							intent.putExtra("events", event_list_json);
							EventActivity.this.startActivity(intent);
							finish();
						}
					});
					
				}else{
					if(actual_current_date==i+1 && actual_current_month==month && actual_current_year==year){
						btn.setTextColor(Color.WHITE);
						btn.setBackgroundResource(R.drawable.todaydate);
					}else{
						btn.setTextColor(Color.BLACK);
						btn.setBackgroundResource(android.R.color.white);
					}
					
				}
				last_cell=i+first_day;
			}
			//clear previous month dates cells
			for(int j=0;j<first_day-1;j++){
				Button btn=(Button) getDaycell(1,j);
				btn.setText("");
				btn.setBackgroundResource(android.R.color.white);
			}
			
			//clear next month dates cells
			for(int k=42;k>last_cell;k--){
				Button btn=(Button) getDaycell(k,0);
				btn.setText("");
				btn.setBackgroundResource(android.R.color.white);
			}
		} catch (ParseException e) {
			
			e.printStackTrace();
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
	}
	public View getDaycell(int index,int offset){
		Button btn;
		int actual_index=index+offset;
		switch(actual_index){
		case 1:
			btn=(Button) findViewById(R.id.btn_1);
			return btn;
		case 2:
			btn=(Button) findViewById(R.id.btn_2);
			return btn;
		case 3:
			btn=(Button) findViewById(R.id.btn_3);
			return btn;		
		case 4:
			btn=(Button) findViewById(R.id.btn_4);
			return btn;
		case 5:
			btn=(Button) findViewById(R.id.btn_5);
			return btn;	
		case 6:
			btn=(Button) findViewById(R.id.btn_6);
			return btn;		
		case 7:
			btn=(Button) findViewById(R.id.btn_7);
			return btn;
		case 8:
			btn=(Button) findViewById(R.id.btn_8);
			return btn;
		case 9:
			btn=(Button) findViewById(R.id.btn_9);
			return btn;
		case 10:
			btn=(Button) findViewById(R.id.btn_10);
			return btn;
		case 11:
			btn=(Button) findViewById(R.id.btn_11);
			return btn;
		case 12:
			btn=(Button) findViewById(R.id.btn_12);
			return btn;
		case 13:
			btn=(Button) findViewById(R.id.btn_13);
			return btn;
		case 14:
			btn=(Button) findViewById(R.id.btn_14);
			return btn;
		case 15:
			btn=(Button) findViewById(R.id.btn_15);
			return btn;
		case 16:
			btn=(Button) findViewById(R.id.btn_16);
			return btn;
		case 17:
			btn=(Button) findViewById(R.id.btn_17);
			return btn;
		case 18:
			btn=(Button) findViewById(R.id.btn_18);
			return btn;
		case 19:
			btn=(Button) findViewById(R.id.btn_19);
			return btn;
		case 20:
			btn=(Button) findViewById(R.id.btn_20);
			return btn;	
		case 21:
			btn=(Button) findViewById(R.id.btn_21);
			return btn;
		case 22:
			btn=(Button) findViewById(R.id.btn_22);
			return btn;
		case 23:
			btn=(Button) findViewById(R.id.btn_23);
			return btn;		
		case 24:
			btn=(Button) findViewById(R.id.btn_24);
			return btn;
		case 25:
			btn=(Button) findViewById(R.id.btn_25);
			return btn;	
		case 26:
			btn=(Button) findViewById(R.id.btn_26);
			return btn;		
		case 27:
			btn=(Button) findViewById(R.id.btn_27);
			return btn;
		case 28:
			btn=(Button) findViewById(R.id.btn_28);
			return btn;
		case 29:
			btn=(Button) findViewById(R.id.btn_29);
			return btn;
		case 30:
			btn=(Button) findViewById(R.id.btn_30);
			return btn;
		case 31:
			btn=(Button) findViewById(R.id.btn_31);
			return btn;
		case 32:
			btn=(Button) findViewById(R.id.btn_32);
			return btn;
		case 33:
			btn=(Button) findViewById(R.id.btn_33);
			return btn;
		case 34:
			btn=(Button) findViewById(R.id.btn_34);
			return btn;
		case 35:
			btn=(Button) findViewById(R.id.btn_35);
			return btn;
		case 36:
			btn=(Button) findViewById(R.id.btn_36);
			return btn;
		case 37:
			btn=(Button) findViewById(R.id.btn_37);
			return btn;
		case 38:
			btn=(Button) findViewById(R.id.btn_38);
			return btn;
		case 39:
			btn=(Button) findViewById(R.id.btn_39);
			return btn;
		case 40:
			btn=(Button) findViewById(R.id.btn_40);
			return btn;
		case 41:
			btn=(Button) findViewById(R.id.btn_41);
			return btn;
		case 42:
			btn=(Button) findViewById(R.id.btn_42);
			return btn;	
		}
		
		return null;
	}
	
	public int getFirstDay(int day, int month, int year)
    {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DATE, day);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                //return "SUNDAY";
            	return 1;
            case Calendar.MONDAY:
                //return "MONDAY";
            	return 2;
            case Calendar.TUESDAY:
                //return "TUESDAY";
            	return 3;
            case Calendar.WEDNESDAY:
                //return "WEDNESDAY";
            	return 4;
            case Calendar.THURSDAY:
                //return "THURSDAY";
            	return 5;
            case Calendar.FRIDAY:
                //return "FRIDAY";
            	return 6;
            case Calendar.SATURDAY:
                //return "SATURDAY";
            	return 7;
        }
        return 0;
    }
	public int getNumberOfDaysInMonth(int day, int month, int year){
		
			// Get the number of days in that month
			int daysInMonth;
		
		 	Calendar cal = new GregorianCalendar();
	        cal.set(Calendar.DATE, day);
	        cal.set(Calendar.MONTH, month);
	        cal.set(Calendar.YEAR, year);
	        cal.set(Calendar.DAY_OF_MONTH, 1);
	        daysInMonth= cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
	        
		return daysInMonth;
	}

}
