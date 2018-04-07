package com.newagesmb.version3;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

public class HotSpotsActivity extends Activity implements OnItemClickListener {
	ProgressDialog progress;
	String latitude,longitude;
	String categoryId[]={"4d4b7105d754a06374d81259","4d4b7105d754a06374d81259","4d4b7105d754a06376d81259","4bf58dd8d48988d162941735","4d4b7104d754a06370d81259",""};
	ImageLoader img_loader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hot_spots);
		
		GPSTracker gpsTracker = new GPSTracker(this);
		if (gpsTracker.canGetLocation()) {
			 latitude = String.valueOf(gpsTracker.latitude);
			 longitude = String.valueOf(gpsTracker.longitude);
		}else{
			 latitude="00.0";
			 longitude="00.0";
		}
		img_loader=new ImageLoader(getApplicationContext());
		initHotSports(1);
		
		ImageButton clear_btn=(ImageButton) findViewById(R.id.clear_btn);
		clear_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				finish();
			}
		});
		findViewById(R.id.spot_category).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPopup(v);
				
			}
		});
	}

	public void initHotSports(int i){
		GetNearestSopts gns=new GetNearestSopts();
		gns.execute(latitude,longitude,categoryId[i]);
	}
	class GetNearestSopts extends AsyncTask<String, Void, String>{

		@SuppressLint("SimpleDateFormat")
		@Override
		protected String doInBackground(String... params) {
			HttpRequester hr=new HttpRequester();
			String url=getString(R.string.forsquare_url);
			String client_id=getString(R.string.forsquare_client_id);
			String client_secret=getString(R.string.forsquare_client_secret);
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			String cur_date=sdf.format(date);
			url+="radius=2000"+"&ll="+params[0]+","+params[1]+"&limit=50"+"&client_id="+client_id+"&v="+cur_date+"&client_secret="+client_secret+"&categoryId="+params[2];
			String result=hr.GET(url);
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(HotSpotsActivity.this, "","Please wait...", true);
		}
		
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			
			try {
				JSONObject jobj=new JSONObject(result);
				String tmp=jobj.getString("response");

				JSONObject tmp_obj=new JSONObject(tmp);
				setLocations(tmp_obj.getString("venues"));
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
	}
	class Holder{
		TextView location_title,location_details,location_distance;
		ImageView location_image;
	}
	
	public void setLocations(String result){
		try {
			JSONArray jarray=new JSONArray(result);
			
			if(jarray.length()>0){
				ArrayList<String> list=new ArrayList<String>();
				for(int i=0;i<jarray.length();i++){
					list.add(jarray.getJSONObject(i).toString());
				}


				ListView lv=(ListView) findViewById(R.id.list_view);
				CustomAdapter adapter=new CustomAdapter(HotSpotsActivity.this, R.layout.hotspot_list_item, list);
				
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(HotSpotsActivity.this);
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	
	class CustomAdapter extends ArrayAdapter<String> {
		private final LayoutInflater inflater = null;
		ArrayList<String> mlist;
		Context mContext;
		int layoutResourceId;
		public CustomAdapter(Context context, int layoutResourceId,
				ArrayList<String> list) {
			
			super(context, layoutResourceId, list);
			
			mlist=list;
			mContext=context;
			this.layoutResourceId = layoutResourceId;
			
		}
		@Override
		public String getItem(int position) {
			
			return mlist.get(position);
		}
		@Override
		public long getItemId(int position) {
			
			return 0;
		}
		
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			
			if (convertView == null) {
				
				LayoutInflater inflater = ((Activity) mContext)
						.getLayoutInflater();
				convertView = inflater.inflate(layoutResourceId, parent, false);
					
			}
			final Holder hl=new Holder();
			try {
				JSONObject jobj = new JSONObject(mlist.get(position).toString());
				//Log.v("Item",jobj.toString());
				try{
					if(jobj.has("categories")){
						JSONArray job_cat=new JSONArray(jobj.getString("categories"));
						JSONObject job_cat_item=new JSONObject(job_cat.get(0).toString());
						if(job_cat_item.has("icon")){
							JSONObject job_icon=new JSONObject(job_cat_item.getString("icon"));
							String cat_image=job_icon.getString("prefix").replace("_v2", "")+"32"+job_icon.getString("suffix");
							//Log.v("Image URL",cat_image);
							if(!cat_image.equals("") && cat_image!=null){
								hl.location_image=(ImageView) convertView.findViewById(R.id.location_image);
								
								img_loader.DisplayImage(cat_image, hl.location_image);
							}
						}
						
					}
					
				}catch (JSONException e) {
					e.printStackTrace();
				}
				hl.location_title=(TextView)  convertView.findViewById(R.id.loc_title);
				hl.location_title.setText(jobj.getString("name"));
				hl.location_details=(TextView) convertView.findViewById(R.id.loc_details);
				String location_data="";
				if(jobj.has("location"))
					location_data=jobj.getString("location");
				if(!location_data.equals("") && location_data!=null){
					try{
						
						JSONObject job=new JSONObject(location_data);
						String address="";
						if(job.has("address"))
							address=job.getString("address");
						String distance="";
						if(job.has("distance"))
							distance=job.getString("distance");;
						hl.location_details.setText(address);
						hl.location_distance=(TextView) convertView.findViewById(R.id.loc_distance);
						if(!distance.equals("") && distance!=null){
							hl.location_distance.setText(distance+" meters");
						}
					}catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}	
	}
	public void showPopup(final View v){
		PopupMenu pm=new PopupMenu(HotSpotsActivity.this, v);
		MenuInflater inflater=pm.getMenuInflater();
		inflater.inflate(R.menu.hotspot_category,pm.getMenu());
		pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				TextView tv=(TextView) v.findViewById(R.id.spot_category);
				tv.setText(item.getTitle());
				switch(item.getItemId()){
				case R.id.nearby:
					
					initHotSports(5);
					return true;
				case R.id.nightlife:
					
					initHotSports(0);
					return true;
				case R.id.food:
					initHotSports(1);
					return true;
				case R.id.shops:
					initHotSports(2);
					return true;
				case R.id.sights:
					initHotSports(3);
					return true;
				case R.id.arts:
					initHotSports(4);
					return true;	
					
				}
				return false;
			}
		});
		pm.show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		String item=(String) arg0.getItemAtPosition(position);
		JSONObject job;
		try {
			job = new JSONObject(item);
			String location_id=job.getString("id");
			Intent intent=new Intent(HotSpotsActivity.this, FoursquareLocationDetailsActivity.class);
			intent.putExtra("location_id", location_id);
			HotSpotsActivity.this.startActivity(intent);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
	}

}
