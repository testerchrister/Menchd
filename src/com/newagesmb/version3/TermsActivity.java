package com.newagesmb.version3;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

public class TermsActivity extends Activity {

	public String user_id;
	WebView wv;
	public ProgressDialog progress;	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_iagree);
		Bundle extras= getIntent().getExtras();
		if(extras!=null){
			user_id=getIntent().getStringExtra("user_id");
		}
		wv=(WebView) findViewById(R.id.web_view);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.loadUrl("http://www.menchd.com/client/terms");
	
		findViewById(R.id.iagree_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AcceptTerms at=new AcceptTerms();
				at.execute(user_id);
			}
		});	
	}
	class AcceptTerms extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			JSONObject jobj=new JSONObject();
			String fun_name="accept_terms";
			String url=getString(R.string.json_url);
			String result=null;
			HttpRequester hr=new HttpRequester();
			try{
				jobj.accumulate("userid", params[0]);
				jobj.accumulate("Accept_terms", "Y");
				result=hr.POST(fun_name, jobj, url);
				
			}catch(JSONException e){
				e.printStackTrace();
			}
			
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(TermsActivity.this, "","Please wait...", true);
		}
		
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			Log.v("I agree Result",result);
			try{
				JSONObject jobj=new JSONObject(result);
				boolean status=jobj.getBoolean("status");
				if(status){
					Intent intent=new Intent(TermsActivity.this, LoginActivity.class);
					TermsActivity.this.startActivity(intent);
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
		}	
		
		
	}


}
