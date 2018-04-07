package com.newagesmb.version3;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.Toast;

public class GoogleMapActivity extends Activity {

	 private GoogleMap googleMap;
	 String lat,lng,location;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_map);
		try{
			Bundle extra =getIntent().getExtras();
			if(extra!=null){
				lat=getIntent().getStringExtra("lat");
				lng=getIntent().getStringExtra("lng");
				location=getIntent().getStringExtra("location");
				initilizeMap(lat,lng,location);
				googleMap.setMyLocationEnabled(true);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	 /**
     * function to load map. If map is not created it will create it for you
     * */
    @SuppressLint("NewApi")
	private void initilizeMap(String lat,String lng,String location) {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
         // latitude and longitude
            double latitude =Double.parseDouble(lat);
            double longitude =Double.parseDouble(lng);
             
            // create marker
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(location);
             
            // adding marker
            googleMap.addMarker(marker);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitude, longitude)).zoom(12).build();
     
            	googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap(lat,lng,location);
    }
}
