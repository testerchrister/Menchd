package com.newagesmb.version3;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

public class EditPhotosActivity extends Activity {


	public static String PREF_NAME = "men_pref";
	public ProgressDialog progress;	
	public ImageLoader img_loader;
	
	private Uri mImageCaptureUri=null;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	UserAlbum ua;
	String user_id,photo_id,profile_id; 
	LinearLayout ll_face,ll_body,ll_action,ll_group,ll_misc,ll_tst;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_photos);
		Bundle extras = this.getIntent().getExtras();
		user_id=this.getIntent().getStringExtra("user_id");
		profile_id=this.getIntent().getStringExtra("profile_id");
		ll_face=(LinearLayout) findViewById(R.id.face_linear);
		ll_body=(LinearLayout) findViewById(R.id.body_linear);
		ll_action=(LinearLayout) findViewById(R.id.action_linear);
		ll_group=(LinearLayout) findViewById(R.id.group_linear);
		ll_misc=(LinearLayout) findViewById(R.id.misc_linear);
		
		setPhotoAlbum(user_id);
		
		findViewById(R.id.add_face).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						
						Intent photoIntent=new Intent(EditPhotosActivity.this,ManagePhotoActivity.class);
						photoIntent.putExtra("user_id", user_id);
						photoIntent.putExtra("category_id", "1");
						photoIntent.putExtra("profile_id", profile_id);
						
						EditPhotosActivity.this.startActivity(photoIntent);
						finish();
					}
				});
		findViewById(R.id.add_body).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent photoIntent=new Intent(EditPhotosActivity.this,ManagePhotoActivity.class);
				photoIntent.putExtra("user_id", user_id);
				photoIntent.putExtra("category_id", "2");
				photoIntent.putExtra("profile_id", profile_id);
				EditPhotosActivity.this.startActivity(photoIntent);
				finish();
				
			}
		});
		findViewById(R.id.add_action).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent photoIntent=new Intent(EditPhotosActivity.this,ManagePhotoActivity.class);
				photoIntent.putExtra("user_id", user_id);
				photoIntent.putExtra("category_id", "3");
				photoIntent.putExtra("profile_id", profile_id);
				EditPhotosActivity.this.startActivity(photoIntent);
				finish();
				
			}
		});	
		findViewById(R.id.add_group_shot).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent photoIntent=new Intent(EditPhotosActivity.this,ManagePhotoActivity.class);
				photoIntent.putExtra("user_id", user_id);
				photoIntent.putExtra("category_id", "4");
				photoIntent.putExtra("profile_id", profile_id);
				EditPhotosActivity.this.startActivity(photoIntent);
				finish();
				
			}
		});	
		findViewById(R.id.add_misc).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent photoIntent=new Intent(EditPhotosActivity.this,ManagePhotoActivity.class);
				photoIntent.putExtra("user_id", user_id);
				photoIntent.putExtra("category_id", "5");
				photoIntent.putExtra("profile_id", profile_id);
				EditPhotosActivity.this.startActivity(photoIntent);
				finish();
				
			}
		});			
		
	}
	

	public class Holder {
		TextView name_header;
		ImageView prof_img, del_img, fav_img;
	}
	public void setPhotoAlbum(String user_id){
				
		String json_url = getString(R.string.json_url);
		ua = new UserAlbum();
		ua.execute(json_url, user_id); 
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
				

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
			return result;
		}
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(EditPhotosActivity.this, "","Please wait...", true);
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

				img_loader = new ImageLoader(EditPhotosActivity.this);
				
				TextView tv;
				ImageView imageView ;
				for(int i=0;i<photo_length;i++){
					
					temp_array=jarray.getJSONObject(i).toString();
					jobj=new JSONObject(temp_array);
					tmp_json_array=jobj.getJSONArray("images");
					final JSONArray json_ar=tmp_json_array;
					for(int j=0;j<tmp_json_array.length();j++){
						/*Log.v("Tmp",tmp_json_array.getJSONObject(j).getString("thumb_image").toString());*/
						
						switch(i){
							case 0: 								 
									
									ImageView imageView1=new ImageView(EditPhotosActivity.this);									
									imageView1.setPadding(3,3, 3,3);
									LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
									
									imageView1.setLayoutParams(layoutParams);
									ll_face.addView(imageView1);
									
									img_loader.DisplayImage(tmp_json_array.getJSONObject(j).getString("thumb_image"),imageView1);
									final int group=i;
									final int img_index=j;
									imageView1.setOnClickListener(new View.OnClickListener() {
										
										@Override
										public void onClick(View v) {
											
											
											showPopup(group,img_index,json_ar,v);
											
			
										}
									});
									break;
							case 1:
								imageView=new ImageView(EditPhotosActivity.this);
								imageView.setPadding(3,3, 3,3);
								ll_body.addView(imageView);
								img_loader.DisplayImage(tmp_json_array.getJSONObject(j).getString("thumb_image"),imageView);
								final int group1=i;
								final int img_index1=j;
								imageView.setOnClickListener(new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										
										showPopup(group1,img_index1,json_ar,v);											
									}
								});								
									break;
							case 2:
								imageView=new ImageView(EditPhotosActivity.this);
								imageView.setPadding(3,3, 3,3);
								ll_action.addView(imageView);
								img_loader.DisplayImage(tmp_json_array.getJSONObject(j).getString("thumb_image"),imageView);
								final int group2=i;
								final int img_index2=j;
								imageView.setOnClickListener(new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										
										showPopup(group2,img_index2,json_ar,v);											
									}
								});		
									break;
							case 3:
								
								imageView=new ImageView(EditPhotosActivity.this);
								imageView.setPadding(3,3, 3,3);
								ll_group.addView(imageView);
								img_loader.DisplayImage(tmp_json_array.getJSONObject(j).getString("thumb_image"),imageView);
								final int group3=i;
								final int img_index3=j;
								imageView.setOnClickListener(new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										
										showPopup(group3,img_index3,json_ar,v);											
									}
								});	
									break;
							case 4:
								imageView=new ImageView(EditPhotosActivity.this);
								imageView.setPadding(3,3, 3,3);
								ll_misc.addView(imageView);
								img_loader.DisplayImage(tmp_json_array.getJSONObject(j).getString("thumb_image"),imageView);
								final int group4=i;
								final int img_index4=j;
								imageView.setOnClickListener(new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										
										showPopup(group4,img_index4,json_ar,v);											
									}
								});	
									break;
						}
						
					}
					if(tmp_json_array.length()==0){
						tv=new TextView(EditPhotosActivity.this);
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
			Intent gallaryIntent= new Intent(EditPhotosActivity.this,GallaryActivity.class);
			gallaryIntent.putExtra("group", group);
			gallaryIntent.putExtra("img_index", img_index);
			String image_array=json_ar.toString();
			gallaryIntent.putExtra("Image_array", image_array);
			EditPhotosActivity.this.startActivity(gallaryIntent);
		}
		@SuppressLint("NewApi")
		public void showPopup(int group,final int img_index,final JSONArray json_ar,final View v){
			/*Toast.makeText(getBaseContext(), "Test:"+img_index, Toast.LENGTH_SHORT).show();*/
			
			PopupMenu pm=new PopupMenu(EditPhotosActivity.this, v);
			MenuInflater inflater=pm.getMenuInflater();
			inflater.inflate(R.menu.image_edit_popup,pm.getMenu());
			pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					
					switch(item.getItemId()){
					case R.id.delete:
						
						try {
							JSONArray j_array=new JSONArray(json_ar.toString());
							JSONObject jobj=new JSONObject(j_array.get(img_index).toString());
							/*Toast.makeText(getBaseContext(),"User_id"+user_id+" image_id:"+jobj.getString("image_id"),Toast.LENGTH_SHORT).show(); */
							v.setVisibility(View.GONE);
							DeletePhoto dp=new DeletePhoto();
							dp.execute(user_id,jobj.getString("image_id"));
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
						
						return true;
					case R.id.edit:
						JSONArray j_array;
						try {
							j_array = new JSONArray(json_ar.toString());
							JSONObject jobj=new JSONObject(j_array.get(img_index).toString());
							Intent editCaption=new Intent(EditPhotosActivity.this,ManagePhotoActivity.class);
							editCaption.putExtra("user_id", user_id);
							editCaption.putExtra("profile_id", profile_id);
							editCaption.putExtra("photo_id",jobj.getString("image_id"));
							editCaption.putExtra("photo_src",jobj.getString("thumb_image"));
							editCaption.putExtra("caption",jobj.getString("caption"));
							EditPhotosActivity.this.startActivity(editCaption);
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
						
						
						/*Toast.makeText(getBaseContext(),"You Clicked : Edit Caption",Toast.LENGTH_SHORT).show(); */
						return true;
					case R.id.cancel:
						
						return false;
					}
					return false;
				}
			});
			pm.show();
		}

	}
	
	class DeletePhoto extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String login_id=params[0];
			String pic_id=params[1];
			String url=getString(R.string.json_url);
			String result = "";
			try {

				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("pic_id", pic_id);
				jsonObject.accumulate("login_id", login_id);
				HttpRequester hr=new HttpRequester();
				result=hr.POST("delete_pic", jsonObject, url);
				

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
			return result;
			
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(EditPhotosActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			
			// Remove image from list
		}
		
	}
	@Override
	public void onBackPressed(){
		
			Intent intent=new Intent(EditPhotosActivity.this, ProfileTabActivity.class);
			intent.putExtra("tab", "photo");
			intent.putExtra("profile_id", profile_id);
			EditPhotosActivity.this.startActivity(intent);
			finish();
		/*Toast.makeText(getApplicationContext(), "Back press Profile Id"+profile_id, Toast.LENGTH_SHORT).show();*/
		
	}
	
}
