package com.newagesmb.version3;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class RoutingActivity extends Activity {
	public static String PREF_NAME = "men_pref";
	public ProgressDialog progress;
	public String profile_id;
	public static String profile_details;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_routing);
		profile_id = getIntent().getStringExtra("profile_id");
		 UserDetails ud = new UserDetails();
			ud.execute(profile_id); 
		
	}
	public void routing(String result){
		profile_details=result;
        Intent intentMessages = new Intent().setClass(this, MessageTabActivity.class);
        intentMessages.putExtra("profile_id", profile_id);
        intentMessages.putExtra("profile_details", profile_details);       
        this.startActivity(intentMessages);
        this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.routing, menu);
		return true;
	}
	class UserDetails extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String result = POST(params[0]);
			return result;
		}

		protected String POST(String prof_id) {
			
			String result = "";
			String defValue = new String();
			SharedPreferences sp = RoutingActivity.this.getSharedPreferences(
					PREF_NAME, 0);
			String login_id = sp.getString("id", defValue);
			String fn_name="";
			try {
				HttpRequester hr=new HttpRequester();
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("user_id", prof_id);
				jsonObject.accumulate("login_id", login_id);
				fn_name="get_profile";
				String url=getString(R.string.json_url);
				result=hr.POST(fn_name, jsonObject, url);
			   /* Log.v("Profile Details",result);*/

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}

			return result;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progress = ProgressDialog.show(RoutingActivity.this, "",	"Please wait...", true);
		}

		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			/*
			 *  Toast.makeText(getBaseContext(),
			 * "User Details:"+result, Toast.LENGTH_LONG).show();
			 */
			/*Log.v("User Details:",result);*/
			
			routing(result);
			//setBasicDetails(result);
		}

		

	}	

}
