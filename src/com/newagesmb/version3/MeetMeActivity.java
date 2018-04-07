package com.newagesmb.version3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;


@SuppressWarnings("deprecation")
public class MeetMeActivity extends TabActivity {

	int tab_select=0;
	PopupWindow popwindow;
	boolean popup_click=true;
	PopupWindow pw;
	private View pView;
	static final int DATE_DIALOG_ID = 0;
	private int mYear, mMonth, mDay, mHour, mMinute;
	TextView date_time,suggest_value;
	EditText status_text;
	String place="", place_id, lat,lng, dateTime="",statusText="",suggestValue ;
	int location_status;
	
	public static String PREF_NAME = "men_pref";
	String gmt_time;
	String defValue = new String();
	ProgressDialog progress;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meet_me);
		Bundle extras = getIntent().getExtras();
		if(extras!=null){
			
			tab_select=getIntent().getIntExtra("tab_select", 0);			
			location_status=getIntent().getIntExtra("location_status", 0);
			
		}
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		pw = new PopupWindow(inflater.inflate(R.layout.meetme_popup, null, false),android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT, true);
		if(location_status!=0){
			place=getIntent().getStringExtra("place");
			place_id=getIntent().getStringExtra("place_id");
			lat=getIntent().getStringExtra("lat");
			lng=getIntent().getStringExtra("lng");
			/*Toast.makeText(getApplicationContext(), "Back to Activity", Toast.LENGTH_SHORT).show();
			popupInit();*/
		}
		
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
		findViewById(R.id.add_meetme).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				popupInit();
				
			}
		});
		
		setTab();
		
	}
/*	@Override
	public void onResume() {
	    super.onResume();
	    try {
	    	if(location_status!=0){
				place=getIntent().getStringExtra("place");
				place_id=getIntent().getStringExtra("place_id");
				lat=getIntent().getStringExtra("lat");
				lng=getIntent().getStringExtra("lng");
				Toast.makeText(getApplicationContext(), "Back to Activity", Toast.LENGTH_SHORT).show();
				popupInit();
			}
	    	
	    } catch (Exception e) {}
	}*/
	public void popupInit() {
		
		pw.showAtLocation(this.findViewById(R.id.main), Gravity.CENTER, 0, 0);
		popup_click=false;
		pView=pw.getContentView();
		Button cancel_btn=(Button) pView.findViewById(R.id.cancel_btn);
		suggest_value=(TextView) pView.findViewById(R.id.suggest_value);
		status_text=(EditText) pView.findViewById(R.id.status_text);
		date_time=(TextView) pView.findViewById(R.id.date_and_time);
		cancel_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				suggestValue="";
				statusText="";
				dateTime="";
				suggest_value.setText("");
				date_time.setText("");
				status_text.setText("");
				pw.dismiss();
				
			}
		});
		
		
		
		date_time.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final View dialogView = View.inflate(MeetMeActivity.this, R.layout.date_time_picker, null);
				final AlertDialog alertDialog = new AlertDialog.Builder(MeetMeActivity.this).create();
				dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
				        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

				         Calendar calendar = new GregorianCalendar(datePicker.getYear(),
				                            datePicker.getMonth(),
				                            datePicker.getDayOfMonth(),
				                            timePicker.getCurrentHour(),
				                            timePicker.getCurrentMinute());

				         long time = calendar.getTimeInMillis();
				         //Log.v("Timeeeeeeeeeeeeee",String.valueOf(time));
				         long cur_time=System.currentTimeMillis();
				         if(cur_time<time){
				        	 	dateTime=(String) DateFormat.format("yyyy-MM-dd hh:mm a", time);
				         		date_time.setText(dateTime);
				         		alertDialog.dismiss();
				         }else{
				        	 	AlertDialog dialog = new AlertDialog.Builder(MeetMeActivity.this).create();
								dialog.setTitle("Confirmation");
							    dialog.setMessage("Please select a future Date & Time.");
							    dialog.setCancelable(false);
							    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
							        @Override
									public void onClick(DialogInterface dialog, int buttonId) {
							        	dialog.cancel();
							        }
							    });
							    
							    dialog.show();
				         }
					}
				});
				alertDialog.setView(dialogView);
				alertDialog.show();
			}
		});
		
		
		if(location_status!=0){
			dateTime=getIntent().getStringExtra("date_time");
			statusText=getIntent().getStringExtra("status_text");
			date_time.setText(dateTime);
			status_text.setText(statusText);
			suggestValue=place;
			suggest_value.setText(suggestValue);
		}
		
		suggest_value.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				statusText=status_text.getText().toString();
				Intent intent=new Intent(MeetMeActivity.this, FoursquareLocationActivity.class);
				intent.putExtra("status_text", statusText);
				intent.putExtra("date_time", dateTime);
				MeetMeActivity.this.startActivity(intent);
				
			}
		});
		Button sub_btn=(Button) pView.findViewById(R.id.sub_btn);
		sub_btn.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View arg0) {
				
				
				
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				Date date;
				try {
					date = simpleDateFormat.parse(dateTime);
					gmt_time=simpleDateFormat.format(date);
				} catch (ParseException e) {
					
					e.printStackTrace();
				};
				

				/*Log.v("Local Date Time:",dateTime);
				Log.v("UTC",gmt_time);*/
				/*Toast.makeText(getApplicationContext(), "GMT: "+gmt_time+", "+"IST: "+dateTime, Toast.LENGTH_SHORT).show();*/
				if(!dateTime.equals("") && !place.equals("")){
					CreateMeeting cm=new CreateMeeting();
					cm.execute();
				}else{
					Toast.makeText(getApplicationContext(), "Plese set all the details.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				
			}
		});
		
	}
	
	
	@SuppressWarnings("deprecation")
	protected void setTab(){
		final TabHost tabHost = getTabHost();
		// Open Dates
        Intent intentOPen = new Intent().setClass(this, MeetMeOpenDateActivity.class);
        if(location_status!=0){
        	intentOPen.putExtra("location_status", 1);
        }
        TabSpec tabSpecOpen = tabHost.newTabSpec("Open Dates")
        		.setIndicator("Open Dates")
                .setContent(intentOPen);
        
        // My Dates
        Intent intentMydates = new Intent().setClass(this, MeetMeMyDateActivity.class);   
        TabSpec tabSpecMydates = tabHost.newTabSpec("My Dates")
        		.setIndicator("My Dates")
                .setContent(intentMydates);
        
        tabHost.addTab(tabSpecOpen);
        tabHost.addTab(tabSpecMydates);
        
        tabHost.setCurrentTab(tab_select);

	}
	class CreateMeeting extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			JSONObject jsonObject=new JSONObject();
			String url=getString(R.string.json_url);
			SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
			String user_id=sp.getString("id", defValue);
			String result="";
			HttpRequester hr=new HttpRequester();
			try {
				jsonObject.accumulate("user_id", user_id);
				jsonObject.accumulate("place_id", place_id);
				jsonObject.accumulate("place", place);
				jsonObject.accumulate("status_text", statusText);
				jsonObject.accumulate("lat", lat);
				jsonObject.accumulate("long", lng);
				jsonObject.accumulate("date_time", dateTime);
				jsonObject.accumulate("gmt_time", gmt_time);
				Log.v("Request:",jsonObject.toString());
				result=hr.POST("meet_status", jsonObject, url);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(MeetMeActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			Log.v("Create new Meet Result",result);
			//finish();
			recreate();
			
		}
		
	}

}
