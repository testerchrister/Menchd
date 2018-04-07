package com.newagesmb.version3;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoPlayActivity extends Activity {
	String video_url;
	VideoView vv;
	TextView video_title;
	ImageButton report_video;
	ProgressBar mProgressBar;
	RelativeLayout mVideoLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_play);
		if(getIntent().hasExtra("video_url")){
			mProgressBar = new ProgressBar(this);
			mProgressBar.setIndeterminate(true);
	        mProgressBar.setVisibility(View.VISIBLE);
	        mProgressBar.setEnabled(true);
	        
	        
	        
			video_url=getIntent().getStringExtra("video_url");		
			vv=(VideoView) findViewById(R.id.surface_view);
			RelativeLayout.LayoutParams lProgressBarLayoutParms = new RelativeLayout.LayoutParams(
	                ViewGroup.LayoutParams.WRAP_CONTENT,
	                ViewGroup.LayoutParams.WRAP_CONTENT);
	        lProgressBarLayoutParms.addRule(RelativeLayout.CENTER_IN_PARENT);
	        mProgressBar.setLayoutParams(lProgressBarLayoutParms);
	        mVideoLayout=(RelativeLayout) findViewById(R.id.videoLayout);
	        mVideoLayout.addView(mProgressBar);
	        
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        vv.setVideoURI(Uri.parse(video_url));
	        vv.setMediaController(new MediaController(this));
	        
	        vv.requestFocus();
	        vv.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer arg0) {
					mProgressBar.setVisibility(View.GONE);
					 vv.start();
				}
			});
	       

		}
		if(getIntent().hasExtra("user_name") && !getIntent().getStringExtra("user_name").isEmpty()){
			video_title=(TextView) findViewById(R.id.video_title);
			video_title.setText(getIntent().getStringExtra("user_name"));
		}
		if(getIntent().hasExtra("login_id") && getIntent().hasExtra("profile_id")){
			report_video=(ImageButton) findViewById(R.id.report_video);
			String login_id,profile_id;
			login_id=getIntent().getStringExtra("login_id");
			profile_id=getIntent().getStringExtra("profile_id");
			if(login_id.equals(profile_id)){
				report_video.setVisibility(View.INVISIBLE);
			}
		}
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
		
		
	}
	 

}
