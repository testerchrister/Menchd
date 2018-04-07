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
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BlockedUsersActivity extends Activity {

	public static String PREF_NAME = "men_pref";
	ProgressDialog progress;
	ImageLoader img_loader;
	String login_id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blocked_users);
		
		
		
		
		SharedPreferences sp= getSharedPreferences(PREF_NAME, 0);
		login_id=sp.getString("id", "0");
		
		GetUnblocked gu=new GetUnblocked();
		gu.execute(login_id);
		
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	class GetUnblocked extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String result="";
			String url=getString(R.string.json_url);
			HttpRequester hr=new HttpRequester();
			JSONObject json=new JSONObject();
			try {
				json.accumulate("login_id", params[0]);
				result=hr.POST("blocked_users", json, url);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			progress = ProgressDialog.show(BlockedUsersActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			//Log.v("Resultsssssssssssss",result);
			setBlockUsers(result);
			
		}
		
	}
	
	public void setBlockUsers(String result){
		
		try {
			JSONArray jarray=new JSONArray(result);
			
			if(jarray.length()>0){
				ArrayList<String> list=new ArrayList<String>();
				for(int i=0;i<jarray.length();i++){
					list.add(jarray.getJSONObject(i).toString());
				}
				//Toast.makeText(getApplicationContext(), list.toString(), Toast.LENGTH_SHORT).show();
				
				ListView lv=(ListView) findViewById(R.id.list_view);
				CustomAdapter adapter=new CustomAdapter(BlockedUsersActivity.this, R.layout.blocked_user_details, list);
				lv.setAdapter(adapter);
				//lv.setOnItemClickListener(BlockedUsersActivity.this);
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	class Holder{
		ImageView user_image;
		TextView user_name, age_city, perc;
		Button unlock_btn;
		
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
				Log.v("Position"+position,jobj.toString());
				hl.user_name=(TextView) convertView.findViewById(R.id.user_name);
				hl.age_city=(TextView) convertView.findViewById(R.id.age_city);
				hl.perc=(TextView) convertView.findViewById(R.id.perc);
				
				if(jobj.has("username")){
					hl.user_name.setText(jobj.getString("username"));
				}
				hl.age_city.setText(jobj.getString("age")+", "+jobj.getString("city"));
				hl.perc.setText(String.valueOf(Math.ceil(Double.parseDouble(jobj.getString("perce"))))+"%");
				
				if(jobj.has("user_image") && !jobj.getString("user_image").equals("")){
					img_loader=new ImageLoader(mContext);
					hl.user_image=(ImageView) convertView.findViewById(R.id.user_image);
					img_loader.DisplayImage(jobj.getString("user_image"), hl.user_image);
				}
				
				hl.unlock_btn=(Button) convertView.findViewById(R.id.unblock_btn);
				final String user_id=jobj.getString("userid");
				hl.unlock_btn.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Unblock ub=new Unblock();
						ub.execute(login_id,user_id);
						mlist.remove(position);
						notifyDataSetChanged();
						
					}
				});
			
			}catch (Exception e) {
				
			}
			return convertView;
		}
	}
	
	class Unblock extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			
			String result="";
			String url=getString(R.string.json_url);
			HttpRequester hr=new HttpRequester();
			JSONObject json=new JSONObject();
			try {
				json.accumulate("login_id", params[0]);
				json.accumulate("user_id", params[1]);
				result=hr.POST("unblock_user", json, url);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			progress = ProgressDialog.show(BlockedUsersActivity.this, "","Please wait...", true);
		}
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			//Log.v("Resultzzzzzzzzzzzz",result);
			//setBlockUsers(result);
			
		}
		
	}

}
