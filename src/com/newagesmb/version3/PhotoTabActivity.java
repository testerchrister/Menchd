package com.newagesmb.version3;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PhotoTabActivity extends Activity {

	public static String profile_id;
	public static String profile_details;
	public static String PREF_NAME = "men_pref";
	public ProgressDialog progress;	
	public ImageLoader img_loader;
	String login_id;
	UserAlbum ua;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_tab);
		profile_id = getIntent().getStringExtra("profile_id");
		profile_details=getIntent().getStringExtra("profile_details");
		setPhotoAlbum(profile_details);
		String defValue = new String();
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		login_id = sp.getString("id", defValue);
		if(!profile_id.equals(login_id)){
			ImageButton ib=(ImageButton) findViewById(R.id.edit_profile);
			ib.setVisibility(View.INVISIBLE);
		}
		findViewById(R.id.edit_profile).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//ProfileEditActivity.class
				String user_id="";
				try {
					JSONObject jobj=new JSONObject(profile_details);
					user_id=jobj.getString("user_id");
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
				
				Intent prof_intent = new Intent(PhotoTabActivity.this,EditPhotosActivity.class);
				prof_intent.putExtra("user_id", user_id);
				prof_intent.putExtra("profile_id", profile_id);
				prof_intent.putExtra("profile_details", profile_details);
				PhotoTabActivity.this.startActivity(prof_intent);
				
			}
		});	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo_tab, menu);
		return true;
	}
	public class Holder {
		TextView name_header;
		ImageView prof_img, del_img, fav_img;
	}
	public void setPhotoAlbum(String details){
		try {
			JSONObject jobj = new JSONObject(details);
			final Holder holder = new Holder();
			
			if (!jobj.isNull("username")) {

				holder.name_header = (TextView) findViewById(R.id.prof_user_name);
				holder.name_header.setText(jobj.getString("username"));
			}
		}catch(JSONException je){
			je.printStackTrace();
		}
		
		String json_url = getString(R.string.json_url);
		ua = new UserAlbum();
		ua.execute(json_url, profile_id); 
	}
	public class UserAlbum extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			String photos=POST(params[0],params[1]);
			
			return photos;
		}
		public String POST(String url, String uid) {
			
			String result = "";
			try {

				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("user_id", uid);
				
				HttpRequester hr=new HttpRequester();
				result=hr.POST("get_photos2", jsonObject, url);

			} catch (JSONException e) {
				Log.d("JSONException", e.getLocalizedMessage());
			}
			return result;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(PhotoTabActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			
			setPhotoAlbum(result);
		}
		public void setPhotoAlbum(String photos){

			try {
				/*JSONObject jobj = new JSONObject(photos);*/
				JSONObject jobj;
				String temp_array;
				JSONArray jarray=new JSONArray(photos);
				JSONArray tmp_json_array;
				int photo_length=jarray.length();
				final ArrayList<String> list = new ArrayList<String>();
				HorizontalScrollView hsv_face=(HorizontalScrollView) findViewById(R.id.face_scroll);
				HorizontalScrollView hsv_body=(HorizontalScrollView) findViewById(R.id.body_scroll);
				HorizontalScrollView hsv_action=(HorizontalScrollView) findViewById(R.id.action_scroll);
				HorizontalScrollView hsv_group=(HorizontalScrollView) findViewById(R.id.group_scroll);
				HorizontalScrollView hsv_misc=(HorizontalScrollView) findViewById(R.id.misc_scroll);
				LinearLayout ll_face=(LinearLayout) findViewById(R.id.face_linear);
				LinearLayout ll_body=(LinearLayout) findViewById(R.id.body_linear);
				LinearLayout ll_action=(LinearLayout) findViewById(R.id.action_linear);
				LinearLayout ll_group=(LinearLayout) findViewById(R.id.group_linear);
				LinearLayout ll_misc=(LinearLayout) findViewById(R.id.misc_linear);
				img_loader = new ImageLoader(PhotoTabActivity.this);
				ImageView imageView ;
				TextView tv;
				final int img_group;
				final int image_;
				
				for(int i=0;i<photo_length;i++){
					
					temp_array=jarray.getJSONObject(i).toString();
					jobj=new JSONObject(temp_array);
					tmp_json_array=jobj.getJSONArray("images");
					final JSONArray json_ar=tmp_json_array;
					for(int j=0;j<tmp_json_array.length();j++){
						/*Log.v("Tmp",tmp_json_array.getJSONObject(j).getString("thumb_image").toString());*/
						
						switch(i){
							case 0: 								 
									
									imageView=new ImageView(PhotoTabActivity.this);
									imageView.setPadding(3,3, 3,3);
									ll_face.addView(imageView);
									/*imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));*/
									img_loader.DisplayImage(tmp_json_array.getJSONObject(j).getString("thumb_image"),imageView);
									final int group=i;
									final int img_index=j;
									imageView.setOnClickListener(new View.OnClickListener() {
										
										@Override
										public void onClick(View v) {
											
											showGallery(group,img_index,json_ar);											
										}
									});
									break;
							case 1:
								imageView=new ImageView(PhotoTabActivity.this);
								imageView.setPadding(3,3, 3,3);
								ll_body.addView(imageView);
								img_loader.DisplayImage(tmp_json_array.getJSONObject(j).getString("thumb_image"),imageView);
								final int group1=i;
								final int img_index1=j;
								imageView.setOnClickListener(new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										
										showGallery(group1,img_index1,json_ar);											
									}
								});								
									break;
							case 2:
								imageView=new ImageView(PhotoTabActivity.this);
								imageView.setPadding(3,3, 3,3);
								ll_action.addView(imageView);
								img_loader.DisplayImage(tmp_json_array.getJSONObject(j).getString("thumb_image"),imageView);
								final int group2=i;
								final int img_index2=j;
								imageView.setOnClickListener(new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										
										showGallery(group2,img_index2,json_ar);											
									}
								});		
									break;
							case 3:
								imageView=new ImageView(PhotoTabActivity.this);
								imageView.setPadding(3,3, 3,3);
								ll_group.addView(imageView);
								img_loader.DisplayImage(tmp_json_array.getJSONObject(j).getString("thumb_image"),imageView);
								final int group3=i;
								final int img_index3=j;
								imageView.setOnClickListener(new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										
										showGallery(group3,img_index3,json_ar);											
									}
								});	
									break;
							case 4:
								imageView=new ImageView(PhotoTabActivity.this);
								imageView.setPadding(3,3, 3,3);
								ll_misc.addView(imageView);
								img_loader.DisplayImage(tmp_json_array.getJSONObject(j).getString("thumb_image"),imageView);
								final int group4=i;
								final int img_index4=j;
								imageView.setOnClickListener(new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										
										showGallery(group4,img_index4,json_ar);											
									}
								});	
									break;
						}
						
					}
					if(tmp_json_array.length()==0){
						tv=new TextView(PhotoTabActivity.this);
						tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
						tv.setText("No Photos Uploaded");
						tv.setGravity(Gravity.CENTER);
						tv.setTextColor(Color.RED);
						switch(i){
						case 0:
							ll_face.addView(tv);
							break;
						case 1:
							ll_body.addView(tv);
							break;
						case 2:
							ll_action.addView(tv);
							break;
						case 3:
							ll_group.addView(tv);
							break;
						case 4:
							ll_misc.addView(tv);
							break;
							
							
						}
						
					}
					
				}
				
			}catch(JSONException je){
				
				je.printStackTrace();
			}
			
			
		}
		
		public void showGallery(int group,int img_index,JSONArray json_ar){
			Intent gallaryIntent= new Intent(PhotoTabActivity.this,GallaryActivity.class);
			gallaryIntent.putExtra("group", group);
			gallaryIntent.putExtra("img_index", img_index);
			gallaryIntent.putExtra("login_id", login_id);
			gallaryIntent.putExtra("profile_id", profile_id);
			String image_array=json_ar.toString();
			gallaryIntent.putExtra("Image_array", image_array);
			PhotoTabActivity.this.startActivity(gallaryIntent);
		}
	}

}
