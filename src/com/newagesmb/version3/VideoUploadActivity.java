package com.newagesmb.version3;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoUploadActivity extends Activity {
	static final int REQUEST_VIDEO_CAPTURE = 1;
	VideoView mVideoView;
	Uri videoUri;
	String user_id,profile_details,profile_id,video_url;
	boolean upload_status;
	ProgressDialog progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_upload);
		mVideoView=(VideoView) findViewById(R.id.surface_view);
		user_id=getIntent().getStringExtra("user_id");
		profile_details=getIntent().getStringExtra("profile_details");
		profile_id=getIntent().getStringExtra("profile_id");
		upload_status=false;
		recordVideo();
		
		findViewById(R.id.upload_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//Toast.makeText(getBaseContext(), videoUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
				videoUri.getLastPathSegment();
				UploadClip uc=new UploadClip();
				uc.execute();
				
			}
		});
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//finish();
				onBackPressed();
			}
		});
	}
	
	@Override
	public void onBackPressed(){
	     if(upload_status){
	    	 try {
				JSONObject job=new JSONObject(profile_details);
				job.remove("video_url");
				job.accumulate("video_url", video_url);
				profile_details=job.toString();
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
	    	 
	    	 Intent intent=new Intent(VideoUploadActivity.this, EditBasicsActivity.class);
	    	 intent.putExtra("profile_id", profile_id);
	    	 intent.putExtra("profile_details", profile_details);
	    	 VideoUploadActivity.this.startActivity(intent);
	    	 finish();
	    	 
	     }else{
	    	 finish();
	     }
	}
	public void recordVideo(){
		 Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		 takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
		    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
		        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
		    }
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
	        videoUri = data.getData();
	        mVideoView.setVideoURI(videoUri);
	        mVideoView.setMediaController(new MediaController(this));
	        mVideoView.requestFocus();
	        mVideoView.start();
	    }
	}
	
	
	class UploadClip extends AsyncTask<String, Void, String>{
		
		@Override
		protected String doInBackground(String... arg0) {
			String result="";
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				
				FileInputStream fis = new FileInputStream(new File(getRealPathFromURI(videoUri)));
				
				byte[] buf = new byte[1024];
				int n;
				while (-1 != (n = fis.read(buf)))
				    baos.write(buf, 0, n);
				
				byte[] videoBytes = baos.toByteArray();
				String fileName = String.format("File_%d.mp4",new Date().getTime());
				ByteArrayBody bab = new ByteArrayBody(videoBytes, fileName);
				
				String url=getString(R.string.video_upload_url);
				
				HttpRequester hr=new HttpRequester();
				result= hr.uploadVideo(url, user_id, bab);
				
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(VideoUploadActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			/*Log.v("Video Upload Response", result);*/
			String msg;
			try {
				JSONObject job=new JSONObject(result);
				msg=job.getString("message");
				video_url=job.getString("video_url");
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
				upload_status=true;
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
	}
	private String getRealPathFromURI(Uri contentURI) {
	    String result;
	    String[] proj = { MediaColumns.DATA };
	    Cursor cursor = getContentResolver().query(contentURI, proj, null, null, null);
	    if (cursor == null) { // Source is Dropbox or other similar local file path
	        result = contentURI.getPath();
	    } else { 
	        cursor.moveToFirst(); 
	        int idx = cursor.getColumnIndex(MediaColumns.DATA); 
	        result = cursor.getString(idx);
	        cursor.close();
	    }
	    return result;
	}
}
