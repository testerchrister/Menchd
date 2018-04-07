package com.newagesmb.version3;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class GallaryActivity extends Activity {
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private ViewFlipper mViewFlipper;	
	private AnimationListener mAnimationListener;
	private Context mContext;
	private String ImageArray,login_id,profile_id;
	private int selectIndex;
	private int img_group;
	public ImageLoaderReco img_loader;
	public static Button report_btn;
	@SuppressWarnings("deprecation")
	private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallary);
		ImageArray=getIntent().getStringExtra("Image_array");
		login_id=getIntent().getStringExtra("login_id");
		profile_id=getIntent().getStringExtra("profile_id");
		Log.v("ImageArray",ImageArray);
		selectIndex=getIntent().getIntExtra("img_index",0);
		
		img_group=getIntent().getIntExtra("group",0);
		
		mContext = this;
		mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);
		
		setGallery(ImageArray,selectIndex,mViewFlipper,img_group);
		mViewFlipper.setDisplayedChild(selectIndex);
		mViewFlipper.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				detector.onTouchEvent(event);
				return true;
			}
		});
		report_btn=(Button) findViewById(R.id.report_btn);
		if(login_id.equals(profile_id)){
			report_btn.setVisibility(View.INVISIBLE);
		}else{
			report_btn.setVisibility(View.VISIBLE);
		}
		report_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				 Toast.makeText(getBaseContext(),
				 "Successfully Reported",
				 Toast.LENGTH_LONG).show();
				//report_image(mViewFlipper.getDisplayedChild());
			}
		});
		
	}
  
	 private void report_image(int index){
		 /* Admin side function is not yet written for this option*/
	 }
	
	class SwipeGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
					// controlling animation
					mViewFlipper.getInAnimation().setAnimationListener(mAnimationListener);
					mViewFlipper.showNext();
					return true;
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_in));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,R.anim.right_out));
					// controlling animation
					mViewFlipper.getInAnimation().setAnimationListener(mAnimationListener);
					mViewFlipper.showPrevious();
					return true;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			return false;
		}
	}
	public void setGallery(String img_array,int index,ViewFlipper vf,int img_group){

		String Imgage_group_title="";
		Log.v("Image Array",img_array);
		try {
			JSONArray jarray=new JSONArray(img_array);
			ImageView imageView ;

			img_loader = new ImageLoaderReco(GallaryActivity.this);
			
			for(int i=0;i<jarray.length();i++){
				imageView=new ImageView(GallaryActivity.this);
				TextView tv1=new TextView(GallaryActivity.this);
				
				LinearLayout ll=new LinearLayout(GallaryActivity.this);
			    ll.setOrientation(LinearLayout.VERTICAL);
			    ll.addView(imageView);
			    
			    tv1.setText(jarray.getJSONObject(i).getString("caption"));
			    tv1.setTextColor(Color.WHITE);
			    tv1.setTextAppearance(getApplicationContext(), R.style.ImageTitle);
			    ll.addView(tv1);
				vf.addView(ll);
				
				 /*RelativeLayout rl=new RelativeLayout(GallaryActivity.this);
				 RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams) imageView.getLayoutParams();
				 params.addRule(RelativeLayout.CENTER_IN_PARENT);
				 imageView.setLayoutParams(params);
				 rl.addView(imageView);
				 tv1.setText(jarray.getJSONObject(i).getString("caption"));
				 tv1.setTextColor(Color.WHITE);
				 tv1.setTextAppearance(getApplicationContext(), R.style.ImageTitle);
				 rl.addView(tv1);
				 vf.addView(rl);*/
				 
				/*imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));*/
				img_loader.DisplayImage(jarray.getJSONObject(i).getString("other_image"),imageView);
				
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
}
