package com.newagesmb.version3;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PrivacyPolicyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_policy);
		WebView wv=(WebView) findViewById(R.id.web_view);
		wv.setWebViewClient(new WebViewClient(){
			 @Override
			public boolean shouldOverrideUrlLoading(WebView view, String url){
			        view.loadUrl(url);
			        return false; 
			   }
		});
		String url=getString(R.string.json_url);
		url+="/privacy";
		wv.loadUrl(url);
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

	}
	
}
