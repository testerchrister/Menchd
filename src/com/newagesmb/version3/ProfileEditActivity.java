package com.newagesmb.version3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class ProfileEditActivity extends TabActivity {

	public static String profile_id;
	public static String PREF_NAME = "men_pref";
	public ProgressDialog progress;
	public ImageLoader img_loader;
	public static String profile_details;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_edit_tab);
		
		profile_id = getIntent().getStringExtra("profile_id");
		
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
		Resources ressources = getResources();
	     TabHost tabHost = getTabHost();

	     // Home tab
	        Intent intentHome = new Intent().setClass(this, EditBasicsActivity.class);
	        intentHome.putExtra("profile_id", profile_id);
	        intentHome.putExtra("profile_details", profile_details);	        
	        TabSpec tabSpecHome = tabHost.newTabSpec("Basic")
	        		.setIndicator("Basic")
	                .setContent(intentHome);
	     
			// Basic tab
	        Intent intentBasics = new Intent().setClass(this, EditDetailsActivity.class);
	        intentBasics.putExtra("profile_id", profile_id);
	        intentBasics.putExtra("profile_details", profile_details);
	        TabSpec tabSpecBasics = tabHost.newTabSpec("Details")
	                .setIndicator("Details")
	                .setContent(intentBasics);	 
	     // Photo tab
	        Intent intentPhotos = new Intent().setClass(this, PhotoTabActivity.class);
	        intentPhotos.putExtra("profile_id", profile_id);
	        intentPhotos.putExtra("profile_details", profile_details);
	        TabSpec tabSpecPhotos = tabHost.newTabSpec("Photos")
	                .setIndicator("Photos")
	                .setContent(intentPhotos);	
		     // Message tab
	        Intent intentMessages = new Intent().setClass(this, MessageTabActivity.class);
	        intentMessages.putExtra("profile_id", profile_id);
	        intentMessages.putExtra("profile_details", profile_details);
	        TabSpec tabSpecMessages = tabHost.newTabSpec("Messages")
	                .setIndicator("Messages")
	                .setContent(intentMessages);
		     // Question tab
	        Intent intentQuestions = new Intent().setClass(this, EditQuestionsActivity.class);
	        intentQuestions.putExtra("profile_id", profile_id);
	        intentQuestions.putExtra("profile_details", profile_details);
	        TabSpec tabSpecQuestions = tabHost.newTabSpec("Questions")
	                .setIndicator("Questions")
	                .setContent(intentQuestions);


	        
	        tabHost.addTab(tabSpecHome);
	        tabHost.addTab(tabSpecBasics);
	        tabHost.addTab(tabSpecPhotos);
	        tabHost.addTab(tabSpecMessages);
	        tabHost.addTab(tabSpecQuestions);
	        
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
			// TODO Auto-generated method stub
			String result = POST(params[0]);
			return result;
		}

		protected String POST(String prof_id) {
			InputStream inputStream = null;
			String result = "";
			String defValue = new String();
			SharedPreferences sp = ProfileEditActivity.this.getSharedPreferences(
					PREF_NAME, 0);
			String login_id = sp.getString("id", defValue);
			/*Log.v("Login:", login_id);*/
			try {

				String url = getString(R.string.json_url);
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);

				String json = "";

				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("user_id", prof_id);
				jsonObject.accumulate("login_id", login_id);

				JSONObject jsonObjParent = new JSONObject();
				jsonObjParent.accumulate("function", "get_profile");
				jsonObjParent.accumulate("parameters", jsonObject);

				json = jsonObjParent.toString();
				StringEntity se = new StringEntity(json);

				httpPost.setEntity(se);

				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");

				HttpResponse httpResponse = httpclient.execute(httpPost);
				inputStream = httpResponse.getEntity().getContent();

				if (inputStream != null)
					result = convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}

			return result;
		}

		private String convertInputStreamToString(InputStream inputStream)
				throws IOException {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = "";
			String result = "";
			while ((line = bufferedReader.readLine()) != null)
				result += line;

			inputStream.close();
			return result;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progress = ProgressDialog.show(ProfileEditActivity.this, "",	"Please wait...", true);
		}

		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			/*
			 *  Toast.makeText(getBaseContext(),
			 * "User Details:"+result, Toast.LENGTH_LONG).show();
			 */
			/*Log.v("User Details:",result);*/
			profile_details=result;
			setProfileTabs();
			//setBasicDetails(result);
		}

		

	}

}
