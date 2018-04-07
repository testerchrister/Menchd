package com.newagesmb.version3;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	public static String PREF_NAME = "men_pref";
	ProgressDialog progress;
	ImageView fav_settings,message_settings;
	String fav_set_value,msg_set_value,fav_set_cur_value,msg_set_cur_value,user_id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		SharedPreferences sp=getSharedPreferences(PREF_NAME, 0);
		user_id=sp.getString("id", "0");
		findViewById(R.id.privacy_policy).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(SettingsActivity.this, PrivacyPolicyActivity.class);
				SettingsActivity.this.startActivity(intent);
				
			}
		});
		findViewById(R.id.terms_and_conditions).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(SettingsActivity.this, TermsAndConditionsActivity.class);
				SettingsActivity.this.startActivity(intent);
				
			}
		});
		findViewById(R.id.recommandation).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent(SettingsActivity.this, RecommendationSettngsActivity.class);
				SettingsActivity.this.startActivity(intent);
			}
		});
		findViewById(R.id.change_password).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent(SettingsActivity.this, ChangePasswordActivity.class);
				SettingsActivity.this.startActivity(intent);
			}
		});
		findViewById(R.id.blocked_user_list).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent intent=new Intent(SettingsActivity.this, BlockedUsersActivity.class);
				SettingsActivity.this.startActivity(intent);
			}
		});
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(fav_set_value!=fav_set_cur_value || msg_set_cur_value!=msg_set_value){
					SetPushNotifications spn=new SetPushNotifications();
					spn.execute(user_id,fav_set_cur_value,msg_set_cur_value);
				}else{
					finish();
				}
				
			}
		});
		
		findViewById(R.id.del_account).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this).create();
				dialog.setTitle("Confirmation");
			    dialog.setMessage("Do you want to delete this account?");
			    dialog.setCancelable(false);
			    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
			        @Override
					public void onClick(DialogInterface dialog, int buttonId) {
			           //Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
			        	DeleteAccount da=new DeleteAccount();
			        	da.execute();
			        }
			    });
			    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
		            @Override
					public void onClick(DialogInterface dialog, int buttonId) {
		                dialog.cancel();
		            }
		        });
			    dialog.show();
			   
			}
		});
		
		fav_settings=(ImageView) findViewById(R.id.fav_settings);
		fav_settings.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(fav_set_cur_value.equals("Y")){
					fav_set_cur_value="N";
				}else{
					fav_set_cur_value="Y";
				}
				setNotificationView();
			}
		});
		message_settings=(ImageView) findViewById(R.id.message_settings);
		message_settings.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(msg_set_cur_value.equals("Y")){
					msg_set_cur_value="N";
				}else{
					msg_set_cur_value="Y";
				}
				setNotificationView();
			}
		});
		GetPushNotifications gpn=new GetPushNotifications();

		gpn.execute(user_id);
	}

	class GetPushNotifications extends AsyncTask<String,Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String result="";
			String url=getString(R.string.json_url);
			HttpRequester hr=new HttpRequester();
			JSONObject jsonObject=new JSONObject();
			try {
				jsonObject.accumulate("user_id", params[0]);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			result=hr.POST("get_push_status", jsonObject, url);
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			progress = ProgressDialog.show(SettingsActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			//Log.v("Notificationssssssssssssssssssssss",result);
			setNotifications(result);
		}
		
	}
	public void setNotifications(String result){
		try {
			JSONObject job=new JSONObject(result);
			fav_set_value=fav_set_cur_value=job.getString("favorite");
			msg_set_value=msg_set_cur_value=job.getString("message");
			setNotificationView();
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
	}
	public void setNotificationView(){
		if(fav_set_cur_value.equals("Y")){
			fav_settings.setImageResource(R.drawable.seton);
		}else{
			fav_settings.setImageResource(R.drawable.setoff);
		}
		
		if(msg_set_cur_value.equals("Y")){
			message_settings.setImageResource(R.drawable.seton);
		}else{
			message_settings.setImageResource(R.drawable.setoff);
		}
		
	}
	
	class SetPushNotifications extends AsyncTask<String,Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String result="";
			String url=getString(R.string.json_url);
			HttpRequester hr=new HttpRequester();
			JSONObject jsonObject=new JSONObject();
			try {
				jsonObject.accumulate("user_id", params[0]);
				jsonObject.accumulate("fav_push", params[1]);
				jsonObject.accumulate("msg_push", params[2]);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			result=hr.POST("set_push_status", jsonObject, url);
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			progress = ProgressDialog.show(SettingsActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			//Log.v("Notificationssssssssssssssssssssss",result);
			//setNotifications(result);
			finish();
		}
		
	}
	@Override
	public void onBackPressed(){
		if(fav_set_value!=fav_set_cur_value || msg_set_cur_value!=msg_set_value){
			SetPushNotifications spn=new SetPushNotifications();
			spn.execute(user_id,fav_set_cur_value,msg_set_cur_value);
		}else{
			finish();
		}
	}
	
	class DeleteAccount extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String result="";
			String url=getString(R.string.json_url);
			HttpRequester hr=new HttpRequester();
			JSONObject jsonObject=new JSONObject();
			try {
				jsonObject.accumulate("user_id",user_id);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			result=hr.POST("delete_account", jsonObject, url);
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			progress = ProgressDialog.show(SettingsActivity.this, "","Saving settings...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			Toast.makeText(getApplicationContext(), "Your account deleted successfully!", Toast.LENGTH_SHORT).show();
			Intent intent=new Intent(SettingsActivity.this, LoginActivity.class);
			SettingsActivity.this.startActivity(intent);
			finish();
		}
		
	}
}
