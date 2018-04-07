package com.newagesmb.version3;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;

public class RecommendationSettngsActivity extends Activity {
	String user_id;
	public static String PREF_NAME = "men_pref";
	ProgressDialog progress;
	Button local, global;
	TextView RecStatLbl;
	String RecStat="";
	String curStat="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommendation_settngs);
		SharedPreferences sp=getSharedPreferences(PREF_NAME, 0);
		user_id=sp.getString("id", "0");
		GetRec gr=new GetRec();
		gr.execute();
		local=(Button) findViewById(R.id.local_btn);
		global=(Button) findViewById(R.id.global_btn);
		RecStatLbl=(TextView) findViewById(R.id.rec_status);
		findViewById(R.id.local_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				local.setBackgroundResource(R.drawable.left_round_btn);
				global.setBackgroundResource(R.drawable.right_round_btn_gray);
				RecStatLbl.setText("Locally");
				curStat="L";
			}
		});
		findViewById(R.id.global_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				local.setBackgroundResource(R.drawable.left_round_btn_gray);
				global.setBackgroundResource(R.drawable.right_round_btn);
				RecStatLbl.setText("Globally");
				curStat="G";
			}
		});
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checkRest();
		
			}
		});
	}
    public void checkRest(){
    	if(!RecStat.equals(curStat)){
			ResetRecStatus rrs=new ResetRecStatus();
			rrs.execute();
		}else{
			finish();
		}
    }
	class GetRec extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			HttpRequester hr=new HttpRequester();
			JSONObject jsonObject=new JSONObject();
			String url=getString(R.string.json_url);
			try {
				jsonObject.accumulate("user_id", user_id);
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			String result=hr.POST("get_rec_status", jsonObject, url);
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			progress = ProgressDialog.show(RecommendationSettngsActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			
			setRecStatus(result);
		}
		
		public void setRecStatus(String result){
			local=(Button) findViewById(R.id.local_btn);
			global=(Button) findViewById(R.id.global_btn);
			RecStatLbl=(TextView) findViewById(R.id.rec_status);
			try {
				JSONObject json=new JSONObject(result);
				String stat=json.getString("rec_status");
				
				if(stat.equals("L")){
					RecStat="L";
					curStat="L";
					local.setBackgroundResource(R.drawable.left_round_btn);
					global.setBackgroundResource(R.drawable.right_round_btn_gray);
					RecStatLbl.setText("Locally");
				}else{
					RecStat="G";
					curStat="G";
					local.setBackgroundResource(R.drawable.left_round_btn_gray);
					global.setBackgroundResource(R.drawable.right_round_btn);
					RecStatLbl.setText("Globally");
				}
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
		}
	}
	class ResetRecStatus extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			HttpRequester hr=new HttpRequester();
			JSONObject jsonObject=new JSONObject();
			String url=getString(R.string.json_url);
			try {
				jsonObject.accumulate("user_id", user_id);
				jsonObject.accumulate("rec_status", curStat);
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			String result=hr.POST("post_rec_status", jsonObject, url);
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			progress = ProgressDialog.show(RecommendationSettngsActivity.this, "","Saving settings...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			
			finish();
		}
		
	}
	@Override
	public void onBackPressed(){
		
		checkRest();
	}
}
