package com.newagesmb.version3;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements com.newagesmb.version3.Config {
	public static String PREF_NAME = "men_pref";
	private static String username = "";
	private static String password = "";
	private LinearLayout  horizontalOuterLayout;
	private HorizontalScrollView horizontalScrollview;
	private int scrollPos =	0;
	private TimerTask scrollerSchedule;
	private int scrollMax;
	ImageView scroll_img;
	private Timer scrollTimer		=	null;
	EditText userView;
	EditText pssView;
	Button submit_btn;
	TextView forget_pswd,register;
	UserLogin loginObj = null;
	GoogleCloudMessaging gcm;
	String regId;
	Context context;
	public ProgressDialog progress;
	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";
	 
	static final String TAG = "Register Activity";
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		
		horizontalScrollview  = (HorizontalScrollView) findViewById(R.id.horiztonal_scrollview_id);
		horizontalOuterLayout =	(LinearLayout)findViewById(R.id.horiztonal_outer_layout_id);
		horizontalScrollview.setHorizontalScrollBarEnabled(false);
		scroll_img=(ImageView) findViewById(R.id.scroll_img);
		 ViewTreeObserver vto 		=	horizontalOuterLayout.getViewTreeObserver();
	        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            @Override
	            public void onGlobalLayout() {
	            	horizontalOuterLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	            	getScrollMaxAmount();
	            	startAutoScrolling();            	          	
	            }
	        });
		
		
		context=getApplicationContext();
		Typeface font= Typeface.createFromAsset(getAssets(), "fonts/HELVET10.TTF");
		userView=(EditText) findViewById(R.id.email);
		pssView=(EditText) findViewById(R.id.password);
		submit_btn=(Button) findViewById(R.id.sign_in_button);
		forget_pswd=(TextView) findViewById(R.id.forget_pswd);
		register=(TextView) findViewById(R.id.register);
		forget_pswd.setTypeface(font);
		userView.setTypeface(font);
		pssView.setTypeface(font);
		submit_btn.setTypeface(font);
		register.setTypeface(font);
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netinfo = cm.getActiveNetworkInfo();

		if (netinfo == null || !netinfo.isConnectedOrConnecting()) {
			AlertDialog adl = new AlertDialog.Builder(LoginActivity.this)
					.create();
			adl.setTitle("Connectivity");
			adl.setMessage("No Internet Connection. Please check your data connectivity");
			adl.setButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();

				}
			});
			adl.show();

		}
		String defValue = new String();
		/*
		 * SharedPreferences sp =
		 * LoginActivity.this.getSharedPreferences(PREF_NAME,0);
		 */
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		String user = sp.getString("username", defValue);
		String paswd = sp.getString("password", defValue);

		if ((user != null) && (user.trim().length() > 0) && (paswd != null)
				&& (paswd.trim().length() > 0)) {
			userView.setText(user);
			pssView.setText(paswd);
			String json_url = getString(R.string.json_url);
			String android_regid=registerGCM();
			if(!android_regid.isEmpty() && !android_regid.equals("")){
				loginObj = new UserLogin();
				loginObj.execute(json_url, user, paswd,android_regid);
			}
			
		}
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
												
						login();	
					}
				});
		findViewById(R.id.forget_pswd).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent forgetIntent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
				LoginActivity.this.startActivity(forgetIntent);
			}
		});
		findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			Intent registreIntent=new Intent(LoginActivity.this,RegistrationActivity.class);
			LoginActivity.this.startActivity(registreIntent);
				
			}
		});
/*		pssView.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				
				return false;
			}
		});*/
		
		
	}

    public void getScrollMaxAmount(){
    	int actualWidth = (horizontalOuterLayout.getMeasuredWidth()-512);
    	scrollMax   = actualWidth;
    }
    
    public void startAutoScrolling(){
		if (scrollTimer == null) {
			scrollTimer					=	new Timer();
			final Runnable Timer_Tick 	= 	new Runnable() {
			    public void run() {
			    	moveScrollView();
			    }
			};
			
			if(scrollerSchedule != null){
				scrollerSchedule.cancel();
				scrollerSchedule = null;
			}
			scrollerSchedule = new TimerTask(){
				@Override
				public void run(){
					runOnUiThread(Timer_Tick);
				}
			};
			
			scrollTimer.schedule(scrollerSchedule, 30, 30);
		}
	}
    public void moveScrollView(){
    	scrollPos							= 	(int) (horizontalScrollview.getScrollX() + 1.0);
		if(scrollPos >= scrollMax){
			scrollPos						=	0;
		}
		horizontalScrollview.scrollTo(scrollPos, 0);
				
	}
	public void login(){
		userView = (EditText) findViewById(R.id.email);
		pssView = (EditText) findViewById(R.id.password);
		View focusView = null;
		username = userView.getText().toString();
		password = pssView.getText().toString();
		boolean cancel = false;

		if (username.isEmpty()) {
			userView.setError("Please enter your email");
			focusView = userView;
			cancel = true;
		} else if (password.length() < 4 || password.isEmpty()) {
			pssView.setError("Please enter your password");
			focusView = pssView;
			cancel = true;
		}else{
			Common cm=new Common();
			
			if(!cm.emailValidation(username)){
				userView.setError("Invalid email address");
				focusView = userView;
				cancel = true;
			}
		}
		if (cancel) {
			focusView.requestFocus();
		} else {
			 
			String json_url = getString(R.string.json_url);
			/*Toast.makeText(getBaseContext(),
					 "json_url:"+json_url,
					 Toast.LENGTH_LONG).show();*/
			String android_regid=registerGCM();
			if(!android_regid.isEmpty() && !android_regid.equals("")){
				loginObj = new UserLogin();
				loginObj.execute(json_url, username, password,android_regid);
				
			}
			

		}
	}
	public String registerGCM() {
		 
	    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
	    regId = getRegistrationId(context);
	 
	    if (TextUtils.isEmpty(regId)) {
	 
	      registerInBackground();
	 
	      /*Log.d("RegisterActivity",
	          "registerGCM - successfully registered with GCM server - regId: "
	              + regId);*/
	    } else {
	      //Toast.makeText(getApplicationContext(),	          "RegId already available. RegId: " + regId,	          Toast.LENGTH_LONG).show();
	    }
	    return regId;
	  }
	 private String getRegistrationId(Context context) {
		    final SharedPreferences prefs = getSharedPreferences(
		    MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		    String registrationId = prefs.getString(REG_ID, "");
		    if (registrationId.isEmpty()) {
		      Log.i(TAG, "Registration not found.");
		      return "";
		    }
		    int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		    int currentVersion = getAppVersion(context);
		    if (registeredVersion != currentVersion) {
		      Log.i(TAG, "App version changed.");
		      return "";
		    }
		    return registrationId;
		  }
	 private static int getAppVersion(Context context) {
		    try {
		      PackageInfo packageInfo = context.getPackageManager()
		          .getPackageInfo(context.getPackageName(), 0);
		      return packageInfo.versionCode;
		    } catch (NameNotFoundException e) {
		      Log.d("RegisterActivity",
		          "I never expected this! Going down, going down!" + e);
		      throw new RuntimeException(e);
		    }
		  } 
	private void registerInBackground() {
	    new AsyncTask<Void, Void, String>() {
	      @Override
	      protected String doInBackground(Void... params) {
	        String msg = "";
	        
	        try {
	          if (gcm == null) {
	        	  
	            gcm = GoogleCloudMessaging.getInstance(context);
	          }
	          
	         
	          regId = gcm.register(com.newagesmb.version3.Config.GOOGLE_PROJECT_ID);
	         
	          Log.d("RegisterActivity", "registerInBackground - regId: "
	              + regId);
	         
	          msg = "Device registered, registration ID=" + regId;	
	          
	          storeRegistrationId(context, regId);
	          
	          
	        } catch (Exception ex) {
	          msg = "Error :" + ex.getMessage();
	          Log.d("RegisterActivity", "Error: " + msg);
	        }
	        Log.d("RegisterActivity", "AsyncTask completed: " + msg);
	        return msg;
	      }
	 
	      @Override
	      protected void onPostExecute(String msg) {
	       /* Toast.makeText(getApplicationContext(),
	            "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
	            .show();*/
	      }
	    }.execute(null, null, null);
	  }
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getSharedPreferences(
	        MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(REG_ID, regId);
	    editor.putInt(APP_VERSION, appVersion);
	    editor.commit();
	    
	  }
	public class UserLogin extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String response_string = null;
			response_string = POST(params[0], params[1], params[2],params[3]);
			
			return response_string;

			// return params[0];

		}

		public String POST(String url, String username, String password,String android_regid) {
			
			String result = "";
			try {

				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("username", username);
				jsonObject.accumulate("password", password);
				jsonObject.accumulate("android_regid", android_regid);
				HttpRequester hr=new HttpRequester();
				
				result=hr.POST("login", jsonObject, url);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
			return result;
		}


		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progress = ProgressDialog.show(LoginActivity.this, "",
					"Please wait...", true);
		}

		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();

			boolean status;
			try {

				//Log.v("Login Response", result);
				/*
				 * Toast.makeText(getBaseContext(),"Testing:"+result,
				 * Toast.LENGTH_LONG).show();
				 */
				JSONObject jsn = new JSONObject(result);

				JSONObject json_main = jsn.getJSONObject("main");
				JSONObject json_qstions = jsn.getJSONObject("questions");

				/*
				 * SharedPreferences sharedPref =
				 * LoginActivity.this.getSharedPreferences(PREF_NAME,0);
				 */
				SharedPreferences.Editor editor = getSharedPreferences(
						PREF_NAME, 0).edit();
				editor.putString("username", json_main.get("email").toString());
				editor.putString("password", json_main.get("password")
						.toString());
				editor.putString("login_id", json_main.getString("id"));
				editor.putString("image", json_main.get("image").toString());
				editor.putString("chat_status", json_main.get("chat_status")
						.toString());
				editor.putString("profile_count", json_main
						.get("profile_count").toString());
				editor.putString("id", json_main.get("id").toString());
				editor.putString("total_questions",
						json_qstions.get("total_questions").toString());
				editor.putString("answerd_questions",
						json_qstions.get("answerd_questions").toString());
				editor.commit();

				status = json_main.getBoolean("status");

				if (status) {
					/*
					 * Toast.makeText(getBaseContext(),json_main.get("message").
					 * toString(), Toast.LENGTH_LONG).show();
					 */
					
					Toast.makeText(getBaseContext(),
							json_main.get("message").toString(),
							Toast.LENGTH_LONG).show();
					String terms_status=json_main.getString("terms_status");
					
					if(terms_status.equals("Y")){
						int total_questions=Integer.parseInt(json_qstions.get("total_questions").toString());
						int answerd_questions=Integer.parseInt(json_qstions.get("answerd_questions").toString());
						//Log.v("Total_questions & Answerd_questions",answerd_questions+"::"+total_questions);
						if(total_questions > answerd_questions){
							
							Intent logintent = new Intent(LoginActivity.this,
									AskQuestion.class);
									logintent.putExtra("questions", json_qstions.toString());
									logintent.putExtra("from_activity", "login");
									LoginActivity.this.startActivity(logintent);
									LoginActivity.this.finish();
							
							
						}else{
							
							Intent logintent = new Intent(LoginActivity.this,
									HomeActivity.class);
							logintent.putExtra("first_launch", "1");
							LoginActivity.this.startActivity(logintent);
							LoginActivity.this.finish();
						}
						
						
					}else{
						
						Intent logintent = new Intent(LoginActivity.this,
								TermsActivity.class);
						logintent.putExtra("user_id", json_main.getString("id"));
						LoginActivity.this.startActivity(logintent);
						LoginActivity.this.finish();
					}
					

				} else {
					Toast.makeText(getBaseContext(),
							"You account is inactive. Please contact Menchd.",
							Toast.LENGTH_LONG).show();
				}

			} catch (JSONException e) {

				e.printStackTrace();
				JSONObject jsn;
				try {
					jsn = new JSONObject(result);
					status = jsn.getBoolean("status");
					if (!status) {
						Toast.makeText(
								getBaseContext(),
								jsn.getString("message").toString()
										+ " Please try again.",
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e1) {
					
					e1.printStackTrace();
				}

			}

		}

	}
	
	@Override
	public void onBackPressed(){
		
		return;
	}

}
