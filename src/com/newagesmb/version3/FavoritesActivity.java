package com.newagesmb.version3;

import java.util.ArrayList;

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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FavoritesActivity extends Activity implements OnItemClickListener {
	
	ProgressDialog progress;
	String defValue = new String();
	public static String PREF_NAME = "men_pref";
	ImageLoader img_loader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites);
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		String login_id=sp.getString("id", defValue);
		img_loader=new ImageLoader(getApplicationContext());
		Favorites fav=new Favorites();
		fav.execute(login_id);
		
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
		
		
	}

	class Favorites extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			HttpRequester hr=new HttpRequester();
			String url=getString(R.string.json_url);
			String result="";
			try {
				JSONObject jobj=new JSONObject();
				jobj.accumulate("login_id", params[0]);
				result=hr.POST("get_fav", jobj, url);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			progress = ProgressDialog.show(FavoritesActivity.this, "","Please wait...", true);
		}
		
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			
			setFavorites(result);
		}
	}
	
	public void setFavorites(String result){
		//Log.v("Result",result);
		try {
			JSONArray jarray=new JSONArray(result);
			
			if(jarray.length()>0){
				ArrayList<String> list=new ArrayList<String>();
				for(int i=0;i<jarray.length();i++){
					list.add(jarray.getJSONObject(i).toString());
				}
				//Toast.makeText(getApplicationContext(), list.toString(), Toast.LENGTH_SHORT).show();
				
				ListView lv=(ListView) findViewById(R.id.list_view);
				CustomAdapter adapter=new CustomAdapter(FavoritesActivity.this, R.layout.favorite_list_item, list);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(FavoritesActivity.this);
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
		
		@SuppressLint("ResourceAsColor")
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
				// Inflate user details
				hl.user_name=(TextView) convertView.findViewById(R.id.user_name);
				hl.user_name.setText(jobj.getString("username"));
				
				hl.aget_location=(TextView) convertView.findViewById(R.id.age_location);
				hl.aget_location.setText(jobj.getString("age")+", "+jobj.getString("city"));
				
				hl.perc=(TextView) convertView.findViewById(R.id.perc);
				hl.perc.setText(String.valueOf(Math.round(Double.parseDouble(jobj.getString("perc"))))+"%");
				
				hl.login_user_fav=(ImageView) convertView.findViewById(R.id.login_fav_stat);
				
				hl.fav_list_item=(LinearLayout) convertView.findViewById(R.id.fav_list_item);
				hl.list_user_fav=(ImageView) convertView.findViewById(R.id.user_fav_stat);
				if(jobj.getString("mutual").equals("N")){
					hl.list_user_fav.setVisibility(View.INVISIBLE);
					hl.fav_list_item.setBackgroundColor(Color.WHITE);
				}else{
					hl.list_user_fav.setVisibility(View.VISIBLE);
					hl.fav_list_item.setBackgroundColor(getResources().getColor(R.color.mutual_fav_back));
				}
				hl.online_status=(ImageView) convertView.findViewById(R.id.online_status);
				if(jobj.getString("chat_status").equals("N")){
					hl.online_status.setImageResource(R.drawable.offline);
				}else{
					hl.online_status.setImageResource(R.drawable.online);
				}
				
				hl.profile_pic=(ImageView) convertView.findViewById(R.id.profile_img);
				
				img_loader.DisplayImage(jobj.getString("thumb_image"), hl.profile_pic);
				
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
	}
	
	class Holder{
		TextView user_name, aget_location, perc;
		ImageView profile_pic, online_status, login_user_fav, list_user_fav;
		LinearLayout fav_list_item;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		String item=(String) arg0.getItemAtPosition(position);
		JSONObject job;
		String profile_id="";
		try {
			job = new JSONObject(item);
			profile_id=job.getString("id");
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		Intent prof_intent = new Intent(FavoritesActivity.this,ProfileTabActivity.class);
		prof_intent.putExtra("profile_id", profile_id);
		FavoritesActivity.this.startActivity(prof_intent);
		
	}
}
