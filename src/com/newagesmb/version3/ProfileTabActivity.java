package com.newagesmb.version3;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class ProfileTabActivity extends TabActivity {

	public static String profile_id,login_id;
	public static String PREF_NAME = "men_pref";
	public ProgressDialog progress;
	public ImageLoader img_loader;
	public static String profile_details;
	public String tab_selected;
	public boolean from_gaydar;
	public int gaydar_tab=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_tab);
		from_gaydar=false;
		profile_id = getIntent().getStringExtra("profile_id");
		tab_selected=getIntent().getStringExtra("tab");
		if(getIntent().hasExtra("search_type")){
			from_gaydar=true;
			gaydar_tab=getIntent().getIntExtra("search_type", 0);
		}
		String defValue = new String();
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		login_id = sp.getString("id", defValue);
		
        UserDetails ud = new UserDetails();
		ud.execute(profile_id); 
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile_tab, menu);
		return true;
	}
	public void setProfileTabs(){

	     TabHost tabHost = getTabHost();

	     // Home tab
	        Intent intentHome = new Intent().setClass(this, ProfileHomeActivity.class);
	        intentHome.putExtra("profile_id", profile_id);
	        intentHome.putExtra("profile_details", profile_details);	 
	        if(from_gaydar){
	        	intentHome.putExtra("gaydar_tab", String.valueOf(gaydar_tab) );
	        }
	        TabSpec tabSpecHome = tabHost.newTabSpec("Home")
	        		.setIndicator("Basics")
	                .setContent(intentHome);
	     
			// Basic tab
	        Intent intentBasics = new Intent().setClass(this, BasicProfActivity.class);
	        intentBasics.putExtra("profile_id", profile_id);
	        intentBasics.putExtra("profile_details", profile_details);
	        if(from_gaydar){
	        	intentBasics.putExtra("gaydar_tab", String.valueOf(gaydar_tab) );
	        }
	        TabSpec tabSpecBasics = tabHost.newTabSpec("Basics")
	                .setIndicator("Details")
	                .setContent(intentBasics);	 
	     // Photo tab
	        Intent intentPhotos = new Intent().setClass(this, PhotoTabActivity.class);
	        intentPhotos.putExtra("profile_id", profile_id);
	        intentPhotos.putExtra("profile_details", profile_details);
	        if(from_gaydar){
	        	intentPhotos.putExtra("gaydar_tab", String.valueOf(gaydar_tab) );
	        }
	        TabSpec tabSpecPhotos = tabHost.newTabSpec("Photos")
	                .setIndicator("Photos")
	                .setContent(intentPhotos);	
		     // Message tab 
	        TabSpec tabSpecMessages ;
	        if(login_id.equals(profile_id)){
		        Intent intentMessages = new Intent().setClass(this, MessageTabActivity.class);
		        intentMessages.putExtra("profile_id", profile_id);
		        intentMessages.putExtra("profile_details", profile_details);
		        if(from_gaydar){
		        	intentMessages.putExtra("gaydar_tab", String.valueOf(gaydar_tab) );
		        }
		        tabSpecMessages = tabHost.newTabSpec("Messages")
		                .setIndicator("Messages")
		                .setContent(intentMessages);
	        }else{
	        	JSONObject jonj;
	        	String loginimage="";
	        	String user_image="";
	        	String username="";
				try {
					jonj = new JSONObject(profile_details);
					loginimage=jonj.getString("loginimage");
		        	user_image=jonj.getString("userimage");
		        	username=jonj.getString("username");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        			
	        	Intent intentMessages = new Intent().setClass(this, ConversationActivity.class);
		        intentMessages.putExtra("profile_id", profile_id);
		        intentMessages.putExtra("profile_details", profile_details);
		        intentMessages.putExtra("receiver_id", profile_id);
		        intentMessages.putExtra("sender_id", login_id);
		        intentMessages.putExtra("login_image", loginimage);
		        intentMessages.putExtra("user_image", user_image);
		        intentMessages.putExtra("username", username);
		        if(from_gaydar){
		        	intentMessages.putExtra("gaydar_tab", String.valueOf(gaydar_tab) );
		        }
		        tabSpecMessages = tabHost.newTabSpec("Messages")
		                .setIndicator("Messages")
		                .setContent(intentMessages);
	        }


		     // Question tab
	        Intent intentQuestions = new Intent().setClass(this, QuestionsTabActivity.class);
	        intentQuestions.putExtra("profile_id", profile_id);
	        intentQuestions.putExtra("profile_details", profile_details);
	        if(from_gaydar){
	        	intentQuestions.putExtra("gaydar_tab", String.valueOf(gaydar_tab) );
	        }
	        TabSpec tabSpecQuestions = tabHost.newTabSpec("Questions")
	                .setIndicator("Questions")
	                .setContent(intentQuestions);


	        
	        tabHost.addTab(tabSpecHome);
	        tabHost.addTab(tabSpecBasics);
	        tabHost.addTab(tabSpecPhotos);
	        tabHost.addTab(tabSpecMessages);
	        tabHost.addTab(tabSpecQuestions);
	        
	        if(tab_selected!=null && tab_selected.equals("details")){
	        	tabHost.setCurrentTab(1);
	        }
	        if(tab_selected!=null && tab_selected.equals("questions")){
	        	tabHost.setCurrentTab(4);
	        }
	        if(tab_selected!=null && tab_selected.equals("photo")){
	        	tabHost.setCurrentTab(2);
	        }
	        
	        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55,
	        		getResources().getDisplayMetrics());
	        		
	        		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {

	        		tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = (int) px;

	        		 tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#107301"));
	        		// // unselected
	        		tabHost.getTabWidget().getChildAt(i)
	        		.setBackgroundColor(Color.TRANSPARENT);
	        		// tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.button_ingredients);
	        		// tabHost.getTabWidget().getChildAt(i).setPadding(40,10,40,10);

	        		TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i)
	        		.findViewById(android.R.id.title);
	        		// tv.setTextColor(color.white);
	        		// tv.setBackgroundColor(Color.TRANSPARENT);
	        		tv.setTextSize(11);


	        		}
	        
	}

	class UserDetails extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			String result = POST(params[0]);
			return result;
		}

		protected String POST(String prof_id) {
			
			String result = "";
			String defValue = new String();
			SharedPreferences sp = ProfileTabActivity.this.getSharedPreferences(PREF_NAME, 0);
			String login_id = sp.getString("id", defValue);
			/*Log.v("Login:", login_id);*/
			try {

				String url = getString(R.string.json_url);

				
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("user_id", prof_id);
				jsonObject.accumulate("login_id", login_id);

				HttpRequester hr=new HttpRequester();
				result=hr.POST("get_profile", jsonObject, url);
				
				
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
			/*Log.v("Get Profile Result",result);	*/
			return result;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progress = ProgressDialog.show(ProfileTabActivity.this, "",	"Please wait...", true);
		}

		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			/*
			 *  Toast.makeText(getBaseContext(),
			 * "User Details:"+result, Toast.LENGTH_LONG).show();
			 */
			Log.v("User Details:",result);
			String stringLatitude,stringLongitude;
			GPSTracker gpsTracker = new GPSTracker(ProfileTabActivity.this);
			if (gpsTracker.canGetLocation()) {
				stringLatitude = String.valueOf(gpsTracker.latitude);
				stringLongitude = String.valueOf(gpsTracker.longitude);
			}else{
				stringLatitude="00.0";
				stringLongitude="00.0";
			}
			
			JSONObject jobj;
			try {
				jobj = new JSONObject(result);
				jobj.put("lat", stringLatitude);
				jobj.put("long", stringLongitude);
				profile_details=jobj.toString();
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			setProfileTabs();
			//setBasicDetails(result);
		}

		

	}

}
