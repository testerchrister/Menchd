package com.newagesmb.version3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ManagePhotoActivity extends Activity {

	public ProgressDialog progress;	
	public ImageLoader img_loader;
	
	private Uri mImageCaptureUri=null;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	private int ACTIVITY_ID_PICK_PHOTO = 42;
	private static int SELECT_FILE=3;
	private int REQUEST_CAMERA=2;
	Bitmap bitMap;
	boolean img_status;
	ImageView mImageView;
	EditText caption;
	public String user_id,photo_id,category_id,img_url,caption_txt,profile_id; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_photo);
		img_status=false;
		photo_id=null;
		user_id=this.getIntent().getStringExtra("user_id");
		profile_id=this.getIntent().getStringExtra("profile_id");
		category_id=this.getIntent().getStringExtra("category_id");
		mImageView=(ImageView) findViewById(R.id.gallery_image);
		caption=(EditText) findViewById(R.id.caption);		
		
		
		if(getIntent().hasExtra("photo_id")){
			photo_id = this.getIntent().getStringExtra("photo_id");
			img_url=this.getIntent().getStringExtra("photo_src");
			caption_txt=this.getIntent().getStringExtra("caption");
			caption.setText(caption_txt);
		}

			if(photo_id==null){
				//dialog.show();
				selectImage();
			}else{
				ImageLoader img=new ImageLoader(getApplication());
				img.DisplayImage(img_url, mImageView);
			}
			
			findViewById(R.id.gallery_image).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(photo_id==null)
						//dialog.show();
						selectImage();
				}
			}); 
			
			
			findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// Validation
					if(photo_id==null){
						if(img_status){
							String caption_txt=caption.getText().toString();
							UpdatePhoto up=new UpdatePhoto();
							up.execute(caption_txt,user_id,category_id);
						}else{
							Toast.makeText(getBaseContext(), "Please choose a photo.", Toast.LENGTH_LONG ).show();
						}
					}else{
						// Update Caption
						String caption_txt=caption.getText().toString();
						UpdatePhotoCaption upc= new UpdatePhotoCaption();
						upc.execute(user_id,photo_id,caption_txt);
					}
					
					
				}
			});
			findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					Intent photo_intent=new Intent(ManagePhotoActivity.this,EditPhotosActivity.class);
					photo_intent.putExtra("user_id", user_id);
					photo_intent.putExtra("profile_id",profile_id);
					ManagePhotoActivity.this.startActivity(photo_intent);
					ManagePhotoActivity.this.finish(); 
				}
			});
	}

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, REQUEST_CAMERA);
					
				} else if (items[item].equals("Choose from Library")) {
					
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					
					intent.setType("image/*");
					
					startActivityForResult(
							Intent.createChooser(intent, "Select File"),
							SELECT_FILE);
					
					
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
				
			}
		});
		builder.show();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	//check the id of the result
	    if(data!=null){
	    	  if (requestCode == ACTIVITY_ID_PICK_PHOTO){
	  	        selectPhotoControl(data);
	  	        
	  	    }else if (requestCode == REQUEST_CAMERA) {
	  	    	try {
	  	    		Bundle extras = data.getExtras();
		  			bitMap = (Bitmap) extras.get("data");
		  			if (bitMap != null){
		  	        	mImageView.setImageBitmap(bitMap);
		  	        	img_status=true;
		  	        }else{
		  	        	Toast.makeText(getApplicationContext(), bitMap.toString(), Toast.LENGTH_SHORT).show();
		  	        }
				} catch (Exception e) {
					e.printStackTrace();
				}
	  			
	  			
	  		} else if (requestCode == SELECT_FILE) {
	  			selectPhotoControl(data);
	  		}
	    }
	  
	}
	
private void selectPhotoControl(Intent data) {
		
		Uri selectedImageUri = data.getData();
		String tempPath = getPath(selectedImageUri, ManagePhotoActivity.this);
		
		BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
		btmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(tempPath, btmapOptions);
		
		// The new size we want to scale to
		final int REQUIRED_SIZE = 200;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = btmapOptions.outWidth, height_tmp = btmapOptions.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options btmapOptions2 = new BitmapFactory.Options();
		btmapOptions2.inSampleSize = scale;
		bitMap = BitmapFactory.decodeFile(tempPath, btmapOptions2);
		if (bitMap != null){
        	mImageView.setImageBitmap(bitMap);
        	img_status=true;
        }else{
        	Toast.makeText(getApplicationContext(), bitMap.toString(), Toast.LENGTH_SHORT).show();
        }
		//Log.v("SELECT_FILE", bitMap.toString());
		
	}
public String getPath(Uri uri, Activity activity) {
	String[] projection = { MediaColumns.DATA };
	Cursor cursor = activity
			.managedQuery(uri, projection, null, null, null);
	int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	cursor.moveToFirst();
	return cursor.getString(column_index);
}

	class UpdatePhoto extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String result="";
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
			Bitmap bitmap = drawable.getBitmap();
			bitmap.compress(CompressFormat.JPEG, 50, bos);
			byte[] data = bos.toByteArray();
			String fileName = String.format("File_%d.png",new Date().getTime());
			ByteArrayBody bab = new ByteArrayBody(data, fileName);
			try {
				JSONObject jobj=new JSONObject();
				String url=getString(R.string.update_other_image_url);
				jobj.accumulate("caption", params[0]);
				jobj.accumulate("user_id", params[1]);
				jobj.accumulate("category_id", params[2]);
				
				HttpRequester hr=new HttpRequester();
				result=hr.updatePhotoGallery(url,jobj,bab);
				
				
			}catch(JSONException e){
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			progress = ProgressDialog.show(ManagePhotoActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();

			 try {
				JSONObject jobj=new JSONObject(result);
				boolean reg_status=jobj.getBoolean("status");
				if(reg_status){
					Toast.makeText(getBaseContext(),jobj.getString("message") ,Toast.LENGTH_SHORT).show();
					String user_id=jobj.getString("user_id");
					Intent photo_intent=new Intent(ManagePhotoActivity.this,EditPhotosActivity.class);
					photo_intent.putExtra("user_id", user_id);
					photo_intent.putExtra("profile_id",profile_id);
					ManagePhotoActivity.this.startActivity(photo_intent);
					ManagePhotoActivity.this.finish(); 
				}else{
					Toast.makeText(getBaseContext(),jobj.getString("message") ,
							 Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			 
			
		}		
	}

	class UpdatePhotoCaption extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String login_id=params[0];
			String pic_id=params[1];
			String caption_string=params[2];
			String url=getString(R.string.json_url);
			String result = "";
			try {

				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("pic_id", pic_id);
				jsonObject.accumulate("login_id", login_id);
				jsonObject.accumulate("caption", caption_string);
				HttpRequester hr=new HttpRequester();
				result=hr.POST("edit_pic", jsonObject, url);
				

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
			return result;
			
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(ManagePhotoActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			try {
				JSONObject jobj=new JSONObject(result);
				if(jobj.getBoolean("status")){
					Toast.makeText(getBaseContext(), "Caption updated successfully!", Toast.LENGTH_SHORT).show();
					Intent photo_intent=new Intent(ManagePhotoActivity.this,EditPhotosActivity.class);
					photo_intent.putExtra("user_id", user_id);
					photo_intent.putExtra("profile_id",profile_id);
					ManagePhotoActivity.this.startActivity(photo_intent);
					ManagePhotoActivity.this.finish(); 
				}
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
	}

}
