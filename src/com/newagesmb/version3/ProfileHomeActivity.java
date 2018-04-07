package com.newagesmb.version3;

import org.json.JSONException;
import org.json.JSONObject;

import com.newagesmb.version3.R.drawable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileHomeActivity extends Activity {

	
	public static String  profile_id;
	public static String profile_details;
	public ImageLoaderReco img_loader;
	public static String login_id;
	public static String PREF_NAME = "men_pref";
	public static final int PICK_CONTACT=1;
	public ProgressDialog progress;
	public boolean change_status=false;
	public boolean from_gaydar;
	public String video_url,user_name;
	int gaydar_tab;
	TextView meetme_msg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_home);
		from_gaydar=false;
		if(getIntent().hasExtra("gaydar_tab")){
			from_gaydar=true;
			gaydar_tab=getIntent().getIntExtra("gaydar_tab", 0);
		}
		profile_id = getIntent().getStringExtra("profile_id");
		profile_details=getIntent().getStringExtra("profile_details");
		
		String defValue = new String();
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		login_id = sp.getString("id", defValue);
		if(!profile_id.equals(login_id)){
			ImageButton ib=(ImageButton) findViewById(R.id.edit_profile);
			ib.setVisibility(View.INVISIBLE);
		}
		meetme_msg=(TextView) findViewById(R.id.meetme_msg);
		meetme_msg.setSelected(true);
		meetme_msg.setMovementMethod(new ScrollingMovementMethod());
		setBasicDetails(profile_details);
		
		findViewById(R.id.edit_profile).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//ProfileEditActivity.class
				Intent prof_intent = new Intent(ProfileHomeActivity.this,EditBasicsActivity.class);
				prof_intent.putExtra("profile_id", profile_id);
				prof_intent.putExtra("profile_details", profile_details);
				ProfileHomeActivity.this.startActivity(prof_intent);
				
			}
		});
		findViewById(R.id.report_user).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				showPopup(v);
			}
		});
		
		findViewById(R.id.play_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent intent=new Intent(ProfileHomeActivity.this, VideoPlayActivity.class);
				intent.putExtra("video_url", video_url);
				intent.putExtra("user_name", user_name);
				intent.putExtra("login_id", login_id);
				intent.putExtra("profile_id", profile_id);
				ProfileHomeActivity.this.startActivity(intent);
			}
		});
	}
	@Override
	public void onBackPressed() {
		if(change_status){
			if(from_gaydar){
				Intent intent=new Intent(ProfileHomeActivity.this,GaydarActivity.class);
				intent.putExtra("tab_select", String.valueOf(gaydar_tab) );
				ProfileHomeActivity.this.startActivity(intent);
				finish();
			}else{
				Intent intent=new Intent(ProfileHomeActivity.this,HomeActivity.class);
				ProfileHomeActivity.this.startActivity(intent);
				finish();
			}
			
		}else{
			finish();
		}
	  
	} 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile_home, menu);
		return true;
	}

	public class Holder {
		TextView tv, perc, age_location,name_header;
		ImageView prof_img, del_img, fav_img,report_img,play_btn;
	}

	protected void setBasicDetails(String details) {
		try {
			final JSONObject jobj = new JSONObject(details);
			final Holder holder = new Holder();
			
			if (jobj.getString("userimage") != null) {
				
				holder.prof_img = (ImageView) findViewById(R.id.prof_img);
				img_loader = new ImageLoaderReco(ProfileHomeActivity.this);
				img_loader.DisplayImage(jobj.getString("userimage"),holder.prof_img);
			}
			holder.play_btn=(ImageView) findViewById(R.id.play_btn);
			if(jobj.has("video_url") && !jobj.getString("video_url").equals("") && jobj.getString("video_url")!=null){
				video_url=jobj.getString("video_url");
				holder.play_btn.setVisibility(View.VISIBLE);
			}else{
				holder.play_btn.setVisibility(View.INVISIBLE);
			}
			if(jobj.has("status_text") && !jobj.getString("status_text").equals("") && !jobj.getString("status_text").isEmpty() && jobj.getString("status_text")!=null && !jobj.getString("status_text").equals("null")){
				meetme_msg.setText(jobj.getString("status_text")+" at "+ jobj.getString("place")+" on "+jobj.getString("date_time"));
				meetme_msg.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						
						if(!login_id.equals(profile_id)){
							try{
								
								JSONObject prof_job=new JSONObject(profile_details);
								GetMeetDetails gd=new GetMeetDetails();
								gd.execute(prof_job.getString("user_id"),prof_job.getString("meetid"));
							}catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
						
					}
				});
			}else{
				meetme_msg.setVisibility(View.GONE);
			}
			if (!jobj.isNull("username")) {
				holder.tv = (TextView) findViewById(R.id.first_name);
				holder.tv.setText(jobj.getString("username"));
				holder.name_header = (TextView) findViewById(R.id.prof_user_name);
				holder.name_header.setText(jobj.getString("username"));
				user_name=jobj.getString("username");
			}

			if (!jobj.isNull("perc")) {
				holder.perc = (TextView) findViewById(R.id.per);
				int per = (int) Float.parseFloat(jobj.getString("perc"));
				holder.perc.setText(String.valueOf(per) + " %");
			}
			String age_location = null;
			holder.age_location = (TextView) findViewById(R.id.age_location);
			if (!jobj.isNull("age")) {

				age_location = jobj.getString("age");
			}
			if (!jobj.isNull("city")) {
				age_location += ", " + jobj.getString("city");
			}
			if (age_location != null) {
				holder.age_location.setText(age_location);
			}

			// Percentage, favorites and report buttons need not required if this is his own profile
			if( Integer.parseInt(login_id)!= Integer.parseInt(profile_id)){
				
			String fav_status = null;
			if (!jobj.isNull("fav_status")) {
				fav_status = jobj.getString("fav_status");
				holder.fav_img = (ImageView) findViewById(R.id.fav_status);

				if (fav_status.equals("N")) {
					holder.fav_img
							.setImageResource(drawable.btnaddfav_gray);
				} else {
					holder.fav_img.setImageResource(drawable.btnaddfav);
				}
				
				ImageView fav_btn = holder.fav_img;
				fav_btn.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String rec_usr_id = null;
						String rec_status = null;
						try {
							change_status=true;
							JSONObject tmp_usr = new JSONObject(profile_details);
							
							rec_usr_id = tmp_usr.getString("profile_id");
							rec_status = tmp_usr.getString("fav_status");
							tmp_usr.remove("fav_status");
							if (rec_status.equals("Y")) {
								tmp_usr.put("fav_status", "N");
								holder.fav_img.setImageResource(R.drawable.btnaddfav_gray);

							} else {
								tmp_usr.put("fav_status", "Y");
								holder.fav_img.setImageResource(R.drawable.btnaddfav);
							}
							profile_details=tmp_usr.toString();
							

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						AddFaviritesReco fav_reco = new AddFaviritesReco();
						String json_url = getString(R.string.json_url);
						fav_reco.execute(json_url, rec_usr_id, rec_status);						
						
					}
				});
				
				holder.report_img = (ImageView) findViewById(R.id.report_user);
				holder.report_img.setImageResource(R.drawable.btnaction);
				
				}

			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	
	public class AddFaviritesReco extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String result = addFavorite(params[0], params[1], params[2]);
			return result;
		}
		public String addFavorite(String url, String uid, String fav_status) {

			
			String result = "";
			try {

				String defValue = new String();
				SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
				String login_id = sp.getString("id", defValue);
				
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("user_id", uid);
				jsonObject.accumulate("login_id", login_id);
				
				HttpRequester hr=new HttpRequester();
				
				result=hr.POST("add_fav", jsonObject, url);
				
				

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}

			
			return result;
			
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progress = ProgressDialog.show(ProfileHomeActivity.this, "","Processing Request..", true);
		}

		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();

			String message = "Favorite status chaged";
			String mutual_fav="N";
			try {
				JSONObject jonj = new JSONObject(result);
				message = jonj.getString("message");
				if(jonj.has("mutual")){
					if(jonj.getString("mutual").equals("Y")){
						mutual_fav=jonj.getString("mutual_msg");
					}
				}
			} catch (JSONException e) {
				
				e.printStackTrace();
			}

			Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT)
					.show();
			if(!mutual_fav.equals("N")){
				Toast.makeText(getBaseContext(), mutual_fav, Toast.LENGTH_SHORT)
				.show();
			}

		}

	}
	public void showPopup(final View v){
		PopupMenu pm=new PopupMenu(ProfileHomeActivity.this, v);
		MenuInflater inflater=pm.getMenuInflater();
		inflater.inflate(R.menu.report_options,pm.getMenu());
		pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				switch(item.getItemId()){
				case R.id.report:
					
					ReportUser ru=new ReportUser();
					ru.execute();
					return true;
				case R.id.tell_friend:
					
					Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult(intent, PICK_CONTACT);
					return true;
				case R.id.block_user:
					BlockUser bu=new BlockUser();
					bu.execute();
					return true;
				case R.id.cencel:
					//initHotSports(2);
					return true;
				
					
				}
				return false;
			}
		});
		pm.show();
	}
	
	
	
	class ReportUser extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String result="";
			String url=getString(R.string.json_url);
			HttpRequester hr=new HttpRequester();
			JSONObject job=new JSONObject();
			try {
				job.accumulate("user_id", profile_id);
				job.accumulate("login_id", login_id);
				result=hr.POST("report_user", job, url);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progress = ProgressDialog.show(ProfileHomeActivity.this, "","Processing Request..", true);
		}

		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			return;
		}
		
	}
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
	  super.onActivityResult(reqCode, resultCode, data);
	  String[] PROJECTION=new String[]{BaseColumns._ID,Contacts.DISPLAY_NAME,Phone.NUMBER}; 
	  try{
		  switch (reqCode) {
		    case (PICK_CONTACT) :
		      if (resultCode == Activity.RESULT_OK) {
		       /* Uri contactData = data.getData();
		        Cursor c =  getContentResolver().query(contactData, null, null, null, null);
		        String id = "";
		        
		        if (c.moveToFirst()) {
		          String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		          id=c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));		         
		          Toast.makeText(getApplicationContext(), "name:"+name, Toast.LENGTH_SHORT).show();
		        }*/
		    	  getCOntactInfo(data);
		       
		      }
		      break;
		  }
	  }catch (IllegalArgumentException e) {
	        e.printStackTrace();
	        Log.e("IllegalArgumentException :: ", e.toString());
	    } catch (Exception e) {
	        e.printStackTrace();
	        Log.e("Error :: ", e.toString());
	    }
	 
	}
	public void getCOntactInfo(Intent data){
		 ContentResolver cr = getContentResolver();

		 String Name,phoneNo="";


	        Cursor cursor = managedQuery(data.getData(), null, null, null, null);
	        while (cursor.moveToNext()) {
	            String contactId = cursor.getString(cursor
	                    .getColumnIndex(BaseColumns._ID));
	            Name = cursor
	                    .getString(cursor
	                            .getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

	            String hasPhone = cursor
	                    .getString(cursor
	                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

	            Cursor emailCur = cr.query( 
	                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
	                    null,
	                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
	                    new String[]{contactId}, null); 
	            String email ="",emailType="";
	            while (emailCur.moveToNext()) { 
	        	    // This would allow you get several email addresses
	                    // if the email addresses were stored in an array
	        	    email = emailCur.getString(
	                              emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
	         	    emailType = emailCur.getString(
	                              emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)); 
	         	} 

	           	
	            
	            emailCur.close();

	            String contact_pic=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));




	     if (hasPhone.equalsIgnoreCase("1"))
	                hasPhone = "true";
	            else
	                hasPhone = "false";

	            if (Boolean.parseBoolean(hasPhone)) {
	                Cursor phones = getContentResolver().query(
	                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
	                        null,
	                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
	                                + " = " + contactId, null, null);
	                while (phones.moveToNext()) {
	                    phoneNo = phones
	                            .getString(phones
	                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	                }
	                phones.close();
	            }

	           /* pname.setText(Name);
	            
	            phno.setText(phoneNo);*/

	        //Toast.makeText(this, Name + "   " + phoneNo+" "+contact_pic+" "+email+" "+emailType, Toast.LENGTH_SHORT).show();
	        Intent intent=new Intent(ProfileHomeActivity.this, TellYourFriendActivity.class);
	        intent.putExtra("contact_pic", contact_pic);
	        intent.putExtra("email_id", email);
	        intent.putExtra("phone_number", phoneNo);
	        intent.putExtra("contact_name", Name);
	        ProfileHomeActivity.this.startActivity(intent);	
	        }

	}
	class BlockUser extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String result="";
			String url=getString(R.string.json_url);
			HttpRequester hr=new HttpRequester();
			JSONObject job=new JSONObject();
			try {
				job.accumulate("user_id", profile_id);
				job.accumulate("login_id", login_id);
				result=hr.POST("block_user", job, url);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progress = ProgressDialog.show(ProfileHomeActivity.this, "","Processing Request..", true);
		}

		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			return;
		}
		
	}
	
	class GetMeetDetails extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String result="";
			HttpRequester hr=new HttpRequester();
			String url=getString(R.string.json_url);
			JSONObject job=new JSONObject();
			try {
				job.accumulate("user_id", params[0]);
				job.accumulate("meet_id", params[1]);
				result=hr.POST("get_meet", job, url);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progress = ProgressDialog.show(ProfileHomeActivity.this, "","Loading..", true);
		}

		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			
			Intent intent=new Intent(ProfileHomeActivity.this, MeetMeDetailsActivity.class);
			intent.putExtra("meetInfo", result);
			intent.putExtra("from_tab", "open");
			ProfileHomeActivity.this.startActivity(intent);
			
		}	
		
	}

}
