package com.newagesmb.version3;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FoursquareLocationDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_foursquare_location_details);
		String url=getString(R.string.foursquare_location_url);
		Log.v("URL",url);
		Bundle extras= getIntent().getExtras();
		String location_id="";
		if(extras!=null){
			location_id=getIntent().getStringExtra("location_id");
		}
		WebView wv=(WebView) findViewById(R.id.web_view);
		wv.loadUrl(url+location_id);
		wv.setWebViewClient(new WebViewClient(){
			 @Override
			public boolean shouldOverrideUrlLoading(WebView view, String url){
			        view.loadUrl(url);
			        return false; // then it is not handled by default action
			   }
		});
		
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

}
