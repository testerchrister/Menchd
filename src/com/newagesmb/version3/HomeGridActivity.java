package com.newagesmb.version3;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.origamilabs.library.views.StaggeredGridView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeGridActivity extends Activity {
	public static String PREF_NAME = "men_pref";
	public ProgressDialog progress;
	public static int screen_width;
	Activity activity;
	int first_launch=0;
	GridView gv;
	public static boolean menu_visibility = false;
	
	ImageLoader imageLoader_grid;
	String stringLatitude;
	String stringLongitude;
	String login_id;
	StaggeredGridView gridView;
	int unread_mdg_count=0;
	TextView msg_cnt,msg_cnt_menu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_grid);
		msg_cnt=(TextView) findViewById(R.id.msg_cnt);
		msg_cnt_menu=(TextView) findViewById(R.id.msg_cnt_menu);
		
		//gv=(GridView) findViewById(R.id.grid_view);
		gridView = (StaggeredGridView) this.findViewById(R.id.staggeredGridView1);
		int margin = getResources().getDimensionPixelSize(R.dimen.margin);
		gridView.setItemMargin(margin); // set the GridView margin		
		gridView.setPadding(margin, 0, margin, 0); // have the margin on the sides as well 
		
		
		GPSTracker gpsTracker = new GPSTracker(this);
		if (gpsTracker.canGetLocation()) {
			stringLatitude = String.valueOf(gpsTracker.latitude);
			stringLongitude = String.valueOf(gpsTracker.longitude);
			
		}else{
			stringLatitude="00.0";
			stringLongitude="00.0";
		}
		
		String defValue = new String();
		SharedPreferences sp = HomeGridActivity.this.getSharedPreferences(
				PREF_NAME, 0);
		String user =login_id= sp.getString("id", defValue);
		
		findViewById(R.id.dashgaydar).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent(getApplicationContext(),GaydarActivity.class);
				HomeGridActivity.this.startActivity(intent);
				
			}
		});
		findViewById(R.id.dashmeetme).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(getApplicationContext(),MeetMeActivity.class);
				HomeGridActivity.this.startActivity(intent);
				
				
			}
		});
		findViewById(R.id.dashhotspot).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(getApplicationContext(),HotSpotsActivity.class);
				HomeGridActivity.this.startActivity(intent);
				
				
			}
		});
		
		findViewById(R.id.dashfav).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(getApplicationContext(),FavoritesActivity.class);
				HomeGridActivity.this.startActivity(intent);
			}
		});
		findViewById(R.id.dashsetting).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),SettingsActivity.class);
				HomeGridActivity.this.startActivity(intent);
				
			}
		});
		findViewById(R.id.dashevents).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(getApplicationContext(),EventActivity.class);
				HomeGridActivity.this.startActivity(intent);
				
			}
		});
		
		findViewById(R.id.change_view).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
		
		if(getIntent().hasExtra("rec_list")){
			String rec_list=getIntent().getStringExtra("rec_list");
			recommandation_list(rec_list);
		}
		
		
	}
	public void recommandation_list(String results) {

		try {
			JSONObject jobj = new JSONObject(results);

			
			final ArrayList<String> list = new ArrayList<String>();
			JSONArray rec_array = jobj.getJSONArray("reco");
			String msg_count=jobj.getString("msg");
			if(!msg_count.isEmpty() && msg_count!=null){
				unread_mdg_count=Integer.parseInt(msg_count);
				if(unread_mdg_count>0){
					msg_cnt.setText(msg_count);
					msg_cnt_menu.setText(msg_count);
				}else{
					msg_cnt.setVisibility(View.INVISIBLE);
					msg_cnt_menu.setVisibility(View.INVISIBLE);
				}
			}
			RelativeLayout rl=(RelativeLayout) findViewById(R.id.no_rec);
			if(rec_array.length()>0){
				rl.setVisibility(View.GONE);
				for (int i = 0; i < rec_array.length(); i++) {
					list.add(rec_array.getJSONObject(i).toString());
				}
				/*final CustomGridAdapter adapter_grid = new CustomGridAdapter(this,
						R.layout.single_user_grid, list);
				gv.setAdapter(adapter_grid);*/
				
				StaggeredAdapter adapter = new StaggeredAdapter(HomeGridActivity.this, R.layout.single_user_grid, list);
				
				gridView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				
				
				
			}else{
				rl.setVisibility(View.VISIBLE);
				TextView tv=(TextView) findViewById(R.id.no_rec_txt);
				tv.setText("No recommendations found..");
			}
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}

	}
	public void visit_profile(View view){
		String defValue = new String();
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		String login_id = sp.getString("id", defValue);
		visitProfile(login_id);
	}
	
	public void message_inbox(View view){
		String defValue = new String();
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		String login_id = sp.getString("id", defValue);
        Intent intentMessages = new Intent().setClass(this, RoutingActivity.class);
        intentMessages.putExtra("profile_id", login_id);
        intentMessages.putExtra("rout_activity", "message");
        this.startActivity(intentMessages);
	}
	
	public void toggleMenu(View view) {
		/*
		 * Toast.makeText(getBaseContext(), "Menu Toggle",
		 * Toast.LENGTH_SHORT).show();
		 */
		msg_cnt_menu.setVisibility(View.INVISIBLE);
		LinearLayout menu_layout = (LinearLayout) findViewById(R.id.menu_layout);
		LinearLayout list_layout = (LinearLayout) findViewById(R.id.List_layout);
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) list_layout
				.getLayoutParams();
		if (menu_visibility) {
			menu_visibility = false;
			menu_layout.setVisibility(View.GONE);
			lp.setMargins(0, 0, 0, 0);
		} else {
			menu_visibility = true;
			menu_layout.setVisibility(View.VISIBLE);
			lp.setMargins(0, 0, -51, 0);
		}
	}

	public void logout() {
		Toast.makeText(getBaseContext(), "Log Out Successfully!",
				Toast.LENGTH_SHORT).show();
		SharedPreferences sp = HomeGridActivity.this.getSharedPreferences(
				PREF_NAME, 0);
		sp.edit().clear().commit();
		finish();
		Intent newIncome = new Intent(this, LoginActivity.class);
		this.startActivity(newIncome);
		// finish();
	}

	public void visitProfile(String profile_id) {

		Intent prof_intent = new Intent(HomeGridActivity.this,ProfileTabActivity.class);
		prof_intent.putExtra("profile_id", profile_id);
		HomeGridActivity.this.startActivity(prof_intent);
		
		
	}
	
	public boolean removeItemFromList(int postion, ArrayList<String> arr_list) {
		AlertDialog.Builder alert = new AlertDialog.Builder(HomeGridActivity.this);
		alert.setTitle("Delete");
		alert.setMessage("Do you want to remove this guy?");

		alert.show();
		return true;
	}
	class CustomGridAdapter extends ArrayAdapter<String> {

		private final LayoutInflater inflater = null;
		Context mContext;
		JSONObject rec_jobj;
		ArrayList<String> rec_users;
		int layoutResourceId;
		Bitmap bmp = null;
		HomeActivity ha;
		String rec_user_id;
		String mlist_type;
		public CustomGridAdapter(Context context, int layoutResourceId,
				ArrayList<String> list) {

			super(context, layoutResourceId, list);
			rec_users = list;
			this.mContext = context;
			this.layoutResourceId = layoutResourceId;
			imageLoader_grid = new ImageLoader(this.mContext);
			
		}
		
		@Override
		public int getCount() {
			return rec_users.size();
		}

		@Override
		public String getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		public class Holder {
			TextView tv, tv_pers;
			ImageView img, del_img, fav_img;
			
		}
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				// inflate the layout

				LayoutInflater inflater = ((Activity) mContext)
						.getLayoutInflater();
				convertView = inflater.inflate(layoutResourceId, parent, false);

			}

			try {
				final JSONObject rec_user_json = new JSONObject(rec_users.get(
						position).toString());

				final Holder holder = new Holder();
				
				
				final Holder hl = new Holder();

				hl.img = (ImageView) convertView
						.findViewById(R.id.rec_user_image);
				hl.tv_pers = (TextView) convertView
						.findViewById(R.id.rec_percentage);
				hl.del_img = (ImageView) convertView
						.findViewById(R.id.del_rec);
				hl.fav_img = (ImageView) convertView
						.findViewById(R.id.request_rec);
				if (rec_user_json.getString("fav_status").equals("Y")) {
					hl.fav_img.setImageResource(R.drawable.addfav60);
				} else {
					hl.fav_img.setImageResource(R.drawable.addfav_gray60);
				}
				imageLoader_grid.DisplayImage(rec_user_json.getString("image"),
						hl.img);
				hl.tv_pers.setText(String.valueOf(Math.round( Float
						.parseFloat(rec_user_json.getString("pers")) )) + " %");
				
				
				ImageView del_btn = hl.del_img;
				rec_user_id = rec_user_json.getString("id");

				del_btn.setTag(position);
				del_btn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String rec_usr_id = null;

						try {
							JSONObject tmp_usr = new JSONObject(rec_users.get(
									position).toString());

							rec_usr_id = tmp_usr.getString("id");

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						RemoveRecommandations del_reco = new RemoveRecommandations();
						String json_url = getString(R.string.json_url);
						del_reco.execute(json_url, rec_usr_id);
						rec_users.remove(position);
						notifyDataSetChanged();

					}
				});
				
				
				ImageView fav_btn = hl.fav_img;
				fav_btn.setTag(position);
				fav_btn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String rec_usr_id = null;
						String rec_status = null;
						try {
							JSONObject tmp_usr = new JSONObject(rec_users.get(
									position).toString());
							
							rec_usr_id = tmp_usr.getString("id");
							rec_status = tmp_usr.getString("fav_status");
							tmp_usr.remove("fav_status");
							if (rec_status.equals("Y")) {
								tmp_usr.put("fav_status", "N");
								hl.fav_img
										.setImageResource(R.drawable.addfav_gray60);

							} else {
								tmp_usr.put("fav_status", "Y");
								hl.fav_img
										.setImageResource(R.drawable.addfav60);
							}
							
							rec_users.set(position, tmp_usr.toString());

						} catch (JSONException e) {
							
							e.printStackTrace();
						}

						AddFaviriteRecommandations fav_reco = new AddFaviriteRecommandations();
						String json_url = getString(R.string.json_url);
						fav_reco.execute(json_url, rec_usr_id, rec_status);

					}
				});
				
				ImageView prof_img = hl.img;
				prof_img.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String rec_usr_id = null;
						try {
							JSONObject tmp_usr = new JSONObject(rec_users.get(
									position).toString());
							rec_usr_id = tmp_usr.getString("id");
						} catch (JSONException e) {
							e.printStackTrace();
						}

						visitProfile(rec_usr_id);

					}
				});

			}catch (JSONException e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}
	
	class RemoveRecommandations extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			String rec = removeRecUser(params[0], params[1]);
			return rec;
		}

		protected void onPostExecute(boolean result) {

			Toast.makeText(getBaseContext(), "Deleted User Id:" + result,
					Toast.LENGTH_LONG).show();

		}

		public String removeRecUser(String url, String uid) {

			
			String result = "";
			try {
				HttpRequester hr= new HttpRequester();
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("user_id", uid);
				jsonObject.accumulate("login_id", login_id);
				result=hr.POST("remove_rec", jsonObject, url);
				
				} catch (JSONException e) {
				e.printStackTrace();
			}

			return result;
			
		}

	}
	
	class AddFaviriteRecommandations extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			String result = addFavorite(params[0], params[1], params[2]);
			return result;
		}

		public String addFavorite(String url, String uid, String fav_status) {

			
			String result = "";
			try {

				HttpRequester hr=new HttpRequester();
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("user_id", uid);
				jsonObject.accumulate("login_id", login_id);
				result=hr.POST("add_fav", jsonObject, url);
				

			} catch (JSONException e) {
				e.printStackTrace();
			}

			
			return result;
			
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progress = ProgressDialog.show(HomeGridActivity.this, "",
					"Processing Request..", true);
		}

		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();

			String message = "Favorite status chaged";
			String mutual_fav="N";
			try {
				JSONObject jonj = new JSONObject(result);
				message = jonj.getString("message");
				if(jonj.has("mutual")){
					if(jonj.getString("mutual").equals("Y")){
						mutual_fav=jonj.getString("mutual_msg");
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT)
					.show();
			if(!mutual_fav.equals("N")){
				Toast.makeText(getBaseContext(), mutual_fav, Toast.LENGTH_SHORT)
				.show();
			}
		}

	}

}
