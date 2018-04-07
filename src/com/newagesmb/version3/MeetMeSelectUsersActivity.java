package com.newagesmb.version3;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MeetMeSelectUsersActivity extends Activity {

	public static String PREF_NAME = "men_pref";
	ProgressDialog progress;
	String defValue = new String();
	public ImageLoader img_loader;
	String meet_id,user_id="";
	String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meet_me_select_users);
		url=getString(R.string.json_url);
		meet_id=getIntent().getStringExtra("meet_id");
		UserList ul=new UserList();
		ul.execute(meet_id);
		
		findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
		findViewById(R.id.done_meetme).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!user_id.equals("")){
					ConfirmUser cu=new ConfirmUser();
					cu.execute();
				}
				
			}
		});
	}

	class UserList extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			HttpRequester hr=new HttpRequester();
			String result=null;
			JSONObject jobj=new JSONObject();
			try {
				jobj.accumulate("meet_id", params[0]);
				result=hr.POST("get_requested_users", jobj, url);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(MeetMeSelectUsersActivity.this, "","Please wait...", true);
		}
		
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			//Log.v("Result",result);
			setUserList(result);
			
		}	
		
		
	}
	public void setUserList(String result){
		try {
			JSONArray jarray=new JSONArray(result);
			if(jarray.length()>0){
				ArrayList<String> list=new ArrayList<String>();
				for(int i=0;i<jarray.length();i++){
					list.add(jarray.getJSONObject(i).toString());
				}
				ListView lv=(ListView) findViewById(R.id.list_view);
				CustomAdapter adapter=new CustomAdapter(MeetMeSelectUsersActivity.this, R.layout.meetme_selected_user, list);
				lv.setAdapter(adapter);
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	class Holder{
		TextView user_name;
		ImageView user_image, user_select;
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
			img_loader = new ImageLoader(this.mContext);
			
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
			try{
				final JSONObject  jobj=new JSONObject(mlist.get(position));
				hl.user_image=(ImageView) convertView.findViewById(R.id.user_image);
				if(!jobj.getString("eimage").equals("") && jobj.getString("eimage")!=null){
					img_loader.DisplayImage(jobj.getString("eimage"), hl.user_image);
				}
				hl.user_name=(TextView) convertView.findViewById(R.id.user_name);
				hl.user_name.setText(jobj.getString("username"));
				hl.user_select=(ImageView) convertView.findViewById(R.id.select_btn);
				final String confirm=jobj.getString("confirm");
				if(confirm.equals("Y")){
					hl.user_select.setImageResource(R.drawable.tickmarkactive);
				}else{
					hl.user_select.setImageResource(R.drawable.tickmarkinactive);
				}
				final int pos=position;
				hl.user_select.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						String confm="";
						try {
							confm=jobj.getString("confirm");
						} catch (JSONException e1) {
							
							e1.printStackTrace();
						}
						if(!confm.equals("Y")){
							hl.user_select.setImageResource(R.drawable.tickmarkactive);
							jobj.remove("confirm");
							try {
								user_id=jobj.getString("user_id");
								jobj.put("confirm", "Y");
							} catch (JSONException e) {
								
								e.printStackTrace();
							}
						}
						mlist.set(position, jobj.toString());
						// Reset other selections
						for(int i=0;i<mlist.size();i++){
							if(i!=position){
								
								try {
									JSONObject job1=new JSONObject(mlist.get(i));
									job1.remove("confirm");
									job1.put("confirm", "N");
									mlist.set(i, job1.toString());
								} catch (JSONException e) {
									
									e.printStackTrace();
								}
							}
						}
						
						notifyDataSetChanged();
						
					}
				});
			}catch (Exception e) {

				e.printStackTrace();
			}
			
			return convertView;
		}	
	}
	
	class ConfirmUser extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String result="";
			try {
				JSONObject jobj=new JSONObject();
				jobj.accumulate("meet_id", meet_id);
				jobj.accumulate("user_id", user_id);
				HttpRequester hr=new HttpRequester();
				//Log.v("json_request",jobj.toString());
				result=hr.POST("confirm_user", jobj, url);
			} catch (Exception e) {
				
			}
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(MeetMeSelectUsersActivity.this, "","Please wait...", true);
		}
		
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			//Log.v("Done Result",result);
			Intent intent=new Intent(MeetMeSelectUsersActivity.this, MeetMeActivity.class);
			intent.putExtra("tab_select", 1);
			MeetMeSelectUsersActivity.this.startActivity(intent);
			finish();
			
		}
		
	}

}
