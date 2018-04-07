package com.newagesmb.version3;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TellYourFriendActivity extends Activity {
	TextView contact_name_tv,email_id_tv,phone_number_tv;
	ImageView contact_pic_iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tell_your_friend);
		
		String contact_pic=getIntent().getStringExtra("contact_pic");
		final String phone_number=getIntent().getStringExtra("phone_number");
		final String email_id=getIntent().getStringExtra("email_id");
		String contact_name=getIntent().getStringExtra("contact_name");
		
		contact_name_tv=(TextView) findViewById(R.id.contact_name);
		contact_name_tv.setText(contact_name);
		
		email_id_tv=(TextView) findViewById(R.id.email_id);
		email_id_tv.setText(email_id);
		
		phone_number_tv=(TextView) findViewById(R.id.phone_number);
		phone_number_tv.setText(phone_number);
		
		if(contact_pic!=null && !contact_pic.equals("")){
			contact_pic_iv=(ImageView) findViewById(R.id.contact_pic);
			Uri img_uri=Uri.parse(contact_pic);
			contact_pic_iv.setImageURI(img_uri);
		}
		
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
		email_id_tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email_id});
				
				i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.tell_your_friend_email_subject));
				i.putExtra(Intent.EXTRA_TEXT   ,getString(R.string.tell_your_friend_email_body));
				try {
				    startActivity(Intent.createChooser(i, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(TellYourFriendActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		phone_number_tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);         
				sendIntent.setData(Uri.parse("sms:"));
				sendIntent.putExtra("sms_body",getString(R.string.tell_your_friend_sms)); 
				sendIntent.setData(Uri.parse("sms:" + phone_number)); 
				startActivity(sendIntent);
			}
		});
	}


}
