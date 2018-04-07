package com.newagesmb.version3;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends Activity {

	String cur_pswd,login_id;
	public static String PREF_NAME = "men_pref";
	EditText curr_pwd, new_pwd, conf_pwd;
	ProgressDialog progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		
		
		curr_pwd=(EditText) findViewById(R.id.current_password);
		new_pwd=(EditText) findViewById(R.id.new_password);
		conf_pwd=(EditText) findViewById(R.id.confirm_password);
		SharedPreferences sp=getSharedPreferences(PREF_NAME, 0);
		cur_pswd=sp.getString("password", "0");
		login_id=sp.getString("id", "0");
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
		findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String typed_cur_pwd, typed_new_pwd, typed_conf_pwd;
				typed_cur_pwd=curr_pwd.getText().toString();
				
				if(!typed_cur_pwd.equals(cur_pswd)){
					curr_pwd.setError("WRONG password!");
					return;
				}
				typed_new_pwd=new_pwd.getText().toString();
				typed_conf_pwd=conf_pwd.getText().toString();
				if(typed_new_pwd.equals(typed_conf_pwd)){
					if(typed_new_pwd.length()>5){
						ChangePassword cp=new ChangePassword();
						cp.execute();
					}else{
						new_pwd.setError("Password strength must be atleast 6");
					}
				}else{
					conf_pwd.setError("Passwords mismatch");
				}
			}
		});
		
	}

	class ChangePassword extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String result="";
			String url=getString(R.string.json_url);
			HttpRequester hr=new HttpRequester();
			JSONObject jsonObject=new JSONObject();
			try {
				jsonObject.accumulate("login_id", login_id);
				jsonObject.accumulate("new_password", new_pwd.getText());
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			result=hr.POST("change_password", jsonObject, url);
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			progress = ProgressDialog.show(ChangePasswordActivity.this, "","Save account settings...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			
			
			Toast.makeText(getApplicationContext(),"Password updated successfully", Toast.LENGTH_SHORT).show();
			curr_pwd.setText("");
			new_pwd.setText("");
			conf_pwd.setText("");
			finish();
			
		}
		
	}
	
}
