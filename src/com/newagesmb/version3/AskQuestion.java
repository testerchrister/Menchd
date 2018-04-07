package com.newagesmb.version3;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class AskQuestion extends Activity{

	TabHost tabs;
	String login_id;
	String question_id=null;
	TextView your_qstn,expected_qstn;
	String your_answer=null,expected_answer=null;
	String profile_id,profile_details;
	int total_questions,answerd_questions;
	public ProgressDialog progress;	
	public static String PREF_NAME = "men_pref";
	public static String from_activity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ask_question);
		
		String defValue = new String();
		SharedPreferences sp = AskQuestion.this.getSharedPreferences(
				PREF_NAME, 0);
		login_id= sp.getString("id", defValue);
		total_questions= Integer.parseInt(sp.getString("total_questions", defValue));
		answerd_questions=Integer.parseInt(sp.getString("answerd_questions", defValue));
		
		tabs=(TabHost) findViewById(R.id.TabHost01);
		tabs.setup();
		
		TabHost.TabSpec your_ans=tabs.newTabSpec("Your Answer");
		your_ans.setContent(R.id.your_ans_tab);
		your_ans.setIndicator("Your Answer");		
		tabs.addTab(your_ans);
		
		TabHost.TabSpec exp_ans=tabs.newTabSpec("Expected Answer");
		exp_ans.setContent(R.id.exp_ans_tab);
		exp_ans.setIndicator("Expected Answer");		
		tabs.addTab(exp_ans);
		tabs.setCurrentTab(0);
		
		
		Bundle extra= new Bundle();
		extra=getIntent().getExtras();
		JSONObject jobj;
		if(extra!=null){
			from_activity=getIntent().getStringExtra("from_activity");
			if(from_activity.equals("login")){
				try {
					jobj=new JSONObject(getIntent().getStringExtra("questions").toString());
					//Log.v("questions",jobj.toString());
					int total_questions=jobj.getInt("total_questions");
					int answerd_questions=jobj.getInt("answerd_questions");
					if(total_questions > answerd_questions){
						questionLoad();
					}else{
						
						nomoreQuestion();
					}
					
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
			}else{
				if(total_questions > answerd_questions){
					profile_id=getIntent().getStringExtra("profile_id");
					profile_details=getIntent().getStringExtra("profile_details");
							
					questionLoad();
				}else{
					
					nomoreQuestion();
				}
			}
			
		}else{
			// From Edit Questions when the user haven't answered at leat one questions.
			questionLoad();
		}
		

		findViewById(R.id.nxt_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tabs.setCurrentTab(1);
				
			}
		});
		findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean cancel=false;
				String message="Choose";
				
				try{
					if(your_answer==null){
						
						message+=" Your answer";
						cancel=true;
					}
					if(expected_answer==null){
						if(cancel){
							message+=" &";
						}
						message+=" Expected answer";
						cancel=true;
					}
					if(cancel){
						Toast.makeText(getBaseContext(),message, Toast.LENGTH_SHORT).show();
					}else{
						
						SubmitQuestion sq=new SubmitQuestion();
						sq.execute(question_id,your_answer,expected_answer,login_id);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		});
		findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				LoadQuestion lq=new LoadQuestion();
				lq.execute(login_id,question_id);
			}
		});
		findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(from_activity.equals("login")){
					Intent logintent = new Intent(AskQuestion.this,
							HomeActivity.class);
					AskQuestion.this.startActivity(logintent);
					AskQuestion.this.finish();
				}else{
					Intent logintent = new Intent(AskQuestion.this,
							ProfileTabActivity.class);
					logintent.putExtra("tab", "questions");
					logintent.putExtra("profile_id", profile_id);
					AskQuestion.this.startActivity(logintent);
					AskQuestion.this.finish();
				}
				
				
				
			}
		});
	}
	@Override
	public void onBackPressed(){
		
		AskQuestion.this.finish();
	}
	public void questionLoad(){
		LoadQuestion lq=new LoadQuestion();
		lq.execute(login_id);
	}
	@SuppressWarnings("deprecation")
	public void nomoreQuestion(){
		AlertDialog adl = new AlertDialog.Builder(AskQuestion.this)
		.create();
		adl.setCanceledOnTouchOutside(false);
		adl.setTitle("Question");
		adl.setMessage("No more questions left to be answered.");
		adl.setOnKeyListener(new DialogInterface.OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode== KeyEvent.KEYCODE_BACK){
					AskQuestion.this.finish();
				}
				return false;
			}
		});
		adl.setButton("Ok", new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			AskQuestion.this.finish();
		
			}
		});
adl.show();
	}
	
	class LoadQuestion extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			HttpRequester hr=new HttpRequester();
			JSONObject jobj=new JSONObject();
			String url=getString(R.string.json_url);
			String result=null;
			try {
				jobj.accumulate("user_id", params[0]);
				if(params.length>1){
					jobj.accumulate("question_id", params[1]);
				}else{
					jobj.accumulate("question_id", 0);
				}
				
				result=hr.POST("get_question", jobj, url);
				Log.v("Resultsssssssssssssssss",result);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(AskQuestion.this, "","Question loading...", true);
		}
		@SuppressLint("ResourceAsColor")
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			/*Log.v("Question ",result);*/
			setupQuestion(result);
			
		}	
	}
	public void setupQuestion(String result){
		try{
			//Log.v("Question",result);
			JSONObject jobj=new JSONObject(result);
			String question_obj=jobj.getString("question");
			String ans_obj=jobj.getString("answer");
			int answerd_count=jobj.getInt("answerd_count");
			int total_count=jobj.getInt("total_count");
			if(answerd_count==total_count){
				nomoreQuestion();
				return;
			}
			JSONObject qstn=new JSONObject(question_obj);
			String question=qstn.getString("question");
			Typeface font= Typeface.createFromAsset(getAssets(), "fonts/HELVET10.TTF");
			question_id=qstn.getString("id");
			your_qstn=(TextView) findViewById(R.id.your_question);
			your_qstn.setText(question);
			your_qstn.setTypeface(font);
			expected_qstn=(TextView) findViewById(R.id.expected_question);
			expected_qstn.setText(question);
			expected_qstn.setTypeface(font);
			/*Log.v("ans_obj",ans_obj);*/
			JSONArray ans_array=new JSONArray(ans_obj);
			if(ans_array.length()>0){
				RadioButton rb[]=new RadioButton[ans_array.length()];
				RadioButton rb_exp[]=new RadioButton[ans_array.length()];
				String tmp_ans;
				JSONObject tmp_ans_obj;
				RadioGroup rg=(RadioGroup) findViewById(R.id.your_radio_grp);
				RadioGroup rg_exp=(RadioGroup) findViewById(R.id.expected_radio_grp);
				/*Log.v("ans_array",String.valueOf(ans_array.length()) );*/
				
				int j=rg.getChildCount();
                for (int i=0; i< j; i++){
                    rg.removeViewAt(0);
                }
				for(int i=0;i<ans_array.length();i++){
					rb[i]=new RadioButton(getApplicationContext());
					tmp_ans=ans_array.get(i).toString();
					tmp_ans_obj=new JSONObject(tmp_ans);
					rb[i].setText(tmp_ans_obj.getString("answer"));
					rb[i].setTextColor(Color.BLACK);
					rb[i].setTypeface(font);
					rb[i].setTextSize(12);
					rg.addView(rb[i]);
					final int ans_id=tmp_ans_obj.getInt("id");
					rb[i].setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							/*Toast.makeText(getBaseContext(),String.valueOf(ans_id), Toast.LENGTH_SHORT).show();*/
							your_answer=String.valueOf(ans_id);
						}
					});
				}
				j=rg_exp.getChildCount();
                for (int i=0; i< j; i++){
                	rg_exp.removeViewAt(0);
                }
				for(int i=0;i<ans_array.length();i++){
					rb_exp[i]=new RadioButton(getApplicationContext());
					tmp_ans=ans_array.get(i).toString();
					tmp_ans_obj=new JSONObject(tmp_ans);
					rb_exp[i].setText(tmp_ans_obj.getString("answer"));
					rb_exp[i].setTextColor(Color.BLACK);
					rb_exp[i].setTypeface(font);
					rb_exp[i].setTextSize(12);
					rg_exp.addView(rb_exp[i]);
					final int ans_id=tmp_ans_obj.getInt("id");
					rb_exp[i].setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							/*Toast.makeText(getBaseContext(),String.valueOf(ans_id), Toast.LENGTH_SHORT).show();*/
							expected_answer=String.valueOf(ans_id);
						}
					});
				}
				
				tabs=(TabHost) findViewById(R.id.TabHost01);
				tabs.setCurrentTab(0);
			}else{
				AlertDialog adl = new AlertDialog.Builder(AskQuestion.this)
				.create();
				adl.setCanceledOnTouchOutside(false);
				adl.setTitle("Question");
				adl.setMessage("No more questions left to be answered.");

				adl.setButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						AskQuestion.this.finish();

					}
				});


				adl.show();
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	class SubmitQuestion extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			
			String question_id=params[0];
			String my_answer=params[1];
			String expect_answer=params[2];
			String user_id=params[3];
			JSONObject jobj=new JSONObject();
			String result=null;
			try{
				jobj.accumulate("question_id", question_id);
				jobj.accumulate("my_answer", my_answer);
				jobj.accumulate("expect_answer", expect_answer);
				jobj.accumulate("user_id", user_id);
				String url=getString(R.string.json_url);
				HttpRequester hr=new HttpRequester();
				Log.v("jobj",jobj.toString());
				result=hr.POST("answer_question", jobj, url);
			}catch(JSONException e){
				e.printStackTrace();
			}
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(AskQuestion.this, "","Submitting ...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			Log.v("Question Submitted Response:",result);
			your_answer=null;
			expected_answer=null;
			setupQuestion(result);
			
		}
		
	}
	/*@Override
	public void onDismiss(DialogInterface dialog) {
		Toast.makeText(getBaseContext(), "sdsada", Toast.LENGTH_SHORT).show();
		dialog.dismiss();
	}
	@Override
	public void onCancel(DialogInterface dialog) {
		Toast.makeText(getBaseContext(), "Cancel..", Toast.LENGTH_SHORT).show();
		dialog.dismiss();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(total_questions==answerd_questions){
			AskQuestion.this.finish();
		}	
		return true;
		
	}
	*/

}
