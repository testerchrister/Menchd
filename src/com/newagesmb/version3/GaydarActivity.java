package com.newagesmb.version3;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class GaydarActivity extends TabActivity {

	String tab_select;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gaydar);
		tab_select=getIntent().getStringExtra("tab_select");
		setTab();
		findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent(GaydarActivity.this, FilterActivity.class);
				GaydarActivity.this.startActivity(intent);
				
			}
		});
		findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*Intent intent=new Intent(GaydarActivity.this, HomeActivity.class);
				GaydarActivity.this.startActivity(intent);*/
				finish();
				
			}
		});
	}

	@SuppressWarnings("deprecation")
	protected void setTab(){
		final TabHost tabHost = getTabHost();

	     // Nearby
	        Intent intentNear = new Intent().setClass(this, GaydarNearByActivity.class);   
	        intentNear.putExtra("type", "1");
	        TabSpec tabSpecNear = tabHost.newTabSpec("Nearby")
	        		.setIndicator("Nearby")
	                .setContent(intentNear);
		// Recent visitors
	        Intent intentRecentVisit = new Intent().setClass(this, GaydarNearByActivity.class); 
	        intentRecentVisit.putExtra("type", "2");
	        TabSpec tabSpecRecentVisit = tabHost.newTabSpec("Recent visitors")
	        		.setIndicator("Recent visitors")
	                .setContent(intentRecentVisit);	
			// Recent visitors
	        Intent intentSearch = new Intent().setClass(this, GaydarNearByActivity.class);   
	        intentSearch.putExtra("type", "3");
	        TabSpec tabSpecSearch = tabHost.newTabSpec("Search")
	        		.setIndicator("Search")
	                .setContent(intentSearch);	
	        
	        tabHost.addTab(tabSpecNear);
	        tabHost.addTab(tabSpecRecentVisit);
	        tabHost.addTab(tabSpecSearch);
	        
	        if(tab_select!=null && tab_select.equals("search")){
	        	tabHost.setCurrentTab(2);
	        	ImageButton filter=(ImageButton) findViewById(R.id.filter);
				filter.setVisibility(View.VISIBLE);
	        }
	        
	        
	        getTabWidget().getChildAt(2).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					tabHost.setCurrentTab(2);
					ImageButton filter=(ImageButton) findViewById(R.id.filter);
					filter.setVisibility(View.VISIBLE);
				}
			});
	        getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					tabHost.setCurrentTab(1);
					ImageButton filter=(ImageButton) findViewById(R.id.filter);
					filter.setVisibility(View.INVISIBLE);
				}
			});
	        getTabWidget().getChildAt(0).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					tabHost.setCurrentTab(0);
					ImageButton filter=(ImageButton) findViewById(R.id.filter);
					filter.setVisibility(View.INVISIBLE);
				}
			});
	        
	        
	}

}
