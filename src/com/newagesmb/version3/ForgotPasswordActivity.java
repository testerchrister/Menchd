package com.newagesmb.version3;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgotPasswordActivity extends Activity {

	TextView description,tab_heading;
	EditText email;
	Button submit_btn;
	public ProgressDialog progress;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		description = (TextView) findViewById(R.id.forgot_instruction);
		email=(EditText) findViewById(R.id.email);
		submit_btn=(Button) findViewById(R.id.submit_btn);
		tab_heading=(TextView) findViewById(R.id.tab_heading);
		Typeface font= Typeface.createFromAsset(getAssets(), "fonts/HELVET10.TTF");
		Typeface fontH= Typeface.createFromAsset(getAssets(), "fonts/HELVCOND.TTF");
		description.setTypeface(font);
		email.setTypeface(font);
		submit_btn.setTypeface(font);
		tab_heading.setTypeface(fontH);
		
		findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String email_id;
				boolean cancel=false;
				View focusView = null;
				email_id=email.getText().toString();
				if(email_id.isEmpty()){
					cancel=true;
					email.setError("Please enter your email ID");
					focusView=email;
				}else if(!email_id.contains("@")){
					cancel=true;
					email.setError("Please enter a valid email ID");
					focusView=email;
				}
				if(cancel){
					focusView.requestFocus();
				}else{
					ResetPassword rp=new ResetPassword();
					rp.execute(email_id);
				}
				
			}
		});
		
	}
	
	class ResetPassword extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String email_id=params[0];
			String result=POST(email_id);
			return result;
		}
		String POST(String email_id){
			JSONObject job=new JSONObject();
			String result=null,fn_name;
			try {
				
				HttpRequester hr=new HttpRequester();				
				job.accumulate("email_id", email_id);
				fn_name="forgot_password";
				
				String url=getString(R.string.json_url);
				result=hr.POST(fn_name, job, url);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			progress = ProgressDialog.show(ForgotPasswordActivity.this, "","Please wait...", true);
		}		
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			try {
				JSONObject jobj=new JSONObject(result);
				Toast.makeText(getBaseContext(), jobj.getString("message"),Toast.LENGTH_LONG).show();
				if(jobj.getBoolean("status")){
					email.setText("");
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			 
		}		
		
	}

}
