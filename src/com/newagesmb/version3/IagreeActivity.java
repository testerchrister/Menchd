package com.newagesmb.version3;


import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class IagreeActivity extends Activity {

	public String user_id;
	WebView wv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_iagree);

		
		Bundle extras= getIntent().getExtras();
		if(extras!=null){
			user_id=getIntent().getStringExtra("user_id");
		}
		wv=(WebView) findViewById(R.id.web_view);
		wv.loadUrl("http://www.menchd.com/client/terms");
	}
}
