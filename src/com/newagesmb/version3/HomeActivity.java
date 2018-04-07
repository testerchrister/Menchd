package com.newagesmb.version3;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.woozzu.android.widget.RefreshableListView;
import com.woozzu.android.widget.RefreshableListView.OnRefreshListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	public static String PREF_NAME = "men_pref";
	public ProgressDialog progress;
	UserRecomentation recommendation;
	public static int screen_width;
	Activity activity;
	int first_launch=0;
	String view_type="L";
	int page=0;
	ListView lv;
	boolean isLoading=false;
	private RefreshableListView mListView;
	 GridView gv;
	 int unread_mdg_count=0;
	 int currentFirstVisibleItem,currentVisibleItemCount ,currentScrollState ;
	public static boolean menu_visibility = false;
	/*
	 * Toast.makeText(getBaseContext(), "Recommendations:"+result,
	 * Toast.LENGTH_LONG).show();
	 */
	public ImageLoaderReco imageLoader;
	ImageLoader imageLoader_grid;
	String stringLatitude;
	String stringLongitude;
	String login_id;
	String rec_list;
	PopupWindow pw;
	private View pView;
	WebView wv;
	TextView msg_cnt,msg_cnt_menu;
	public ArrayList<String> list = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.recommandations);
		screen_width=getScreenWidth();
		
		msg_cnt=(TextView) findViewById(R.id.msg_cnt);
		msg_cnt_menu=(TextView) findViewById(R.id.msg_cnt_menu);
		Bundle extras = getIntent().getExtras();
		if(extras!=null){
			String first_launchs=getIntent().getStringExtra("first_launch");
			
			//Announcement Popup
			if(first_launchs.equals("1")){
				
				
				first_launch=1;
			}
		}
		//lv = (ListView) findViewById(R.id.list_view);
		mListView = (RefreshableListView) findViewById(R.id.list_view);
		
		GPSTracker gpsTracker = new GPSTracker(this);
		if (gpsTracker.canGetLocation()) {
			stringLatitude = String.valueOf(gpsTracker.latitude);
			stringLongitude = String.valueOf(gpsTracker.longitude);

		}else{
			stringLatitude="00.0";
			stringLongitude="00.0";
		}

		String defValue = new String();
		SharedPreferences sp = HomeActivity.this.getSharedPreferences(
				PREF_NAME, 0);
		String user =login_id= sp.getString("id", defValue);
		if (!user.isEmpty() && user != null) {

			// call get_recommendations
			
			String json_url = getString(R.string.json_url);
			recommendation = new UserRecomentation();
			recommendation.execute(json_url, user, stringLatitude,stringLongitude);

		}
		
		findViewById(R.id.dashgaydar).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent(getApplicationContext(),GaydarActivity.class);
				HomeActivity.this.startActivity(intent);
				
			}
		});
		findViewById(R.id.dashmeetme).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(getApplicationContext(),MeetMeActivity.class);
				HomeActivity.this.startActivity(intent);
				
				
			}
		});
		findViewById(R.id.dashhotspot).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(getApplicationContext(),HotSpotsActivity.class);
				HomeActivity.this.startActivity(intent);
				
				
			}
		});
		
		findViewById(R.id.dashfav).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(getApplicationContext(),FavoritesActivity.class);
				HomeActivity.this.startActivity(intent);
			}
		});
		findViewById(R.id.dashsetting).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),SettingsActivity.class);
				HomeActivity.this.startActivity(intent);
				
			}
		});
		findViewById(R.id.dashevents).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(getApplicationContext(),EventActivity.class);
				HomeActivity.this.startActivity(intent);
				
			}
		});
		
		findViewById(R.id.change_view).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
					Intent intent=new Intent(HomeActivity.this, HomeGridActivity.class);
					intent.putExtra("rec_list", rec_list);
					HomeActivity.this.startActivity(intent);
					
			}
		});
		
		
	}
    @SuppressLint("NewApi")
	public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
 
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		logout();
		return true;

	}

	public void logout_process(View view) {

		logout();
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
		SharedPreferences sp = HomeActivity.this.getSharedPreferences(
				PREF_NAME, 0);
		sp.edit().clear().commit();
		finish();
		Intent newIncome = new Intent(this, LoginActivity.class);
		this.startActivity(newIncome);
		// finish();
	}

	public class UserRecomentation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			String user_id = params[1];
			String response_string;

			response_string = POST(params[0], params[1], params[2], params[3]);
			return response_string;

		}

		public String POST(String url, String uid, String lat, String lon) {
			
			String result = "";
			try {

				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("user_id", uid);
				jsonObject.accumulate("page",String.valueOf(page));
				jsonObject.accumulate("lat", lat);
				jsonObject.accumulate("long", lon);
					
				HttpRequester hr=new HttpRequester();
				
				result=hr.POST("get_recommendations3", jsonObject, url);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}

		

			return result;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(HomeActivity.this, "","Please wait...", true);
		}

		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			rec_list=result;
			recommandation_list(result);
		}

	}

	public void recommandation_list(String results) {

		try {
			JSONObject jobj = new JSONObject(results);

			
			
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


				final CustomAdapter adapter = new CustomAdapter(this,
						R.layout.rec_single_user, list);
				//lv.setAdapter(adapter);
				mListView.setAdapter(adapter);
				
				 // Callback to refresh the list
		        mListView.setOnRefreshListener(new OnRefreshListener() {
		            
					@Override
					public void onRefresh(RefreshableListView listView) {
						// TODO Auto-generated method stub
						 new NewDataTask().execute();
					}
		        });
		        mListView.setOnScrollListener(new OnScrollListener() {
					
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState) {
						currentScrollState =scrollState;
						
					}
					
					@Override
					public void onScroll(AbsListView view, int firstVisibleItem,
							int visibleItemCount, int totalItemCount) {
						currentFirstVisibleItem=firstVisibleItem;
						currentVisibleItemCount=visibleItemCount;
						isScrollCompleted();
					}
					private void isScrollCompleted() {
					    if (currentVisibleItemCount > 0 && currentScrollState == SCROLL_STATE_IDLE) {
					        /*** In this way I detect if there's been a scroll which has completed ***/
					        /*** do the work for load more date! ***/
					        if(!isLoading){
					             isLoading = true;
					             //loadMoreData();
					             page++;
					             new NewDataTask().execute();
					        }
					    }
					}
				});
				
			}else{
				if(list.size()==0){
					rl.setVisibility(View.VISIBLE);
					TextView tv=(TextView) findViewById(R.id.no_rec_txt);
					tv.setText("No new recommendations found..");
				}
				
			}
			
			if(first_launch==1){
				first_launch=0;
				popupInit();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void visitProfile(String profile_id) {
		/*
		 * AlertDialog.Builder alert=new AlertDialog.Builder(HomeActivity.this);
		 * alert.setTitle("Profile Visit");
		 * alert.setMessage("Profile Id:"+profile_id); alert.show();
		 */
	/*	Intent prof_intent = new Intent(HomeActivity.this,
				ProfileActivity.class);
		prof_intent.putExtra("profile_id", profile_id);
		HomeActivity.this.startActivity(prof_intent);*/
		Intent prof_intent = new Intent(HomeActivity.this,ProfileTabActivity.class);
		prof_intent.putExtra("profile_id", profile_id);
		HomeActivity.this.startActivity(prof_intent);
		
		/* HomeActivity.this.finish(); */

	}

	public boolean removeItemFromList(int postion, ArrayList<String> arr_list) {
		AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
		alert.setTitle("Delete");
		alert.setMessage("Do you want to remove this guy?");

		alert.show();
		return true;
	}
	class CustomAdapter extends ArrayAdapter<String> {

		private final LayoutInflater inflater = null;
		Context mContext;
		JSONObject rec_jobj;
		ArrayList<String> rec_users;
		int layoutResourceId;
		Bitmap bmp = null;
		HomeActivity ha;
		String rec_user_id;
		String mlist_type;
		public CustomAdapter(Context context, int layoutResourceId,
				ArrayList<String> list) {

			super(context, layoutResourceId, list);
			rec_users = list;
			this.mContext = context;
			this.layoutResourceId = layoutResourceId;
			imageLoader = new ImageLoaderReco(this.mContext);
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return rec_users.size();
		}

		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
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
				/*holder.tv = (TextView) convertView
						.findViewById(R.id.rec_user_name);*/
				holder.img = (ImageView) convertView
						.findViewById(R.id.rec_user_image);
				holder.tv_pers = (TextView) convertView
						.findViewById(R.id.rec_percentage);
				holder.del_img = (ImageView) convertView
						.findViewById(R.id.del_rec);
				holder.fav_img = (ImageView) convertView
						.findViewById(R.id.request_rec);
				if (rec_user_json.getString("fav_status").equals("Y")) {
					holder.fav_img.setImageResource(R.drawable.addfav60);
				} else {
					holder.fav_img.setImageResource(R.drawable.addfav_gray60);
				}

				int ImageWidth_scale, ImageHeight_scale;
				ImageWidth_scale=(screen_width/rec_user_json.getInt("width"));
				ImageHeight_scale=	(screen_width*(int) Float.parseFloat(rec_user_json.getString("height"))/100);
				
				holder.img.getLayoutParams().height = ImageHeight_scale;
				holder.img.getLayoutParams().width = screen_width;

				imageLoader.DisplayImage(rec_user_json.getString("image"),
						holder.img);
				holder.tv_pers.setText(String.valueOf(Math.round( Float
						.parseFloat(rec_user_json.getString("pers")) )) + " %");

				/*
				 * This is a perfectly working module. Comment for a new
				 * experiment with java thread.
				 * 
				 * String json_url=mContext.getString(R.string.json_url);
				 * UserProfilePic upp=null; upp=new
				 * UserProfilePic(convertView,rec_user_json);
				 * upp.execute(json_url);
				 */

				ImageView del_btn = holder.del_img;
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

				ImageView fav_btn = holder.fav_img;
				fav_btn.setTag(position);
				fav_btn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String rec_usr_id = null;
						String rec_status = null;
						try {
							JSONObject tmp_usr = new JSONObject(rec_users.get(
									position).toString());
							/*Log.v("Before Updates:", tmp_usr.toString());*/
							rec_usr_id = tmp_usr.getString("id");
							rec_status = tmp_usr.getString("fav_status");
							tmp_usr.remove("fav_status");
							if (rec_status.equals("Y")) {
								tmp_usr.put("fav_status", "N");
								holder.fav_img
										.setImageResource(R.drawable.addfav_gray60);

							} else {
								tmp_usr.put("fav_status", "Y");
								holder.fav_img
										.setImageResource(R.drawable.addfav60);
							}
							/*Log.v("After Updates:", tmp_usr.toString());*/
							/* rec_users.remove(position); */
							rec_users.set(position, tmp_usr.toString());

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						AddFaviriteRecommandations fav_reco = new AddFaviriteRecommandations();
						String json_url = getString(R.string.json_url);
						fav_reco.execute(json_url, rec_usr_id, rec_status);

					}
				});

				ImageView prof_img = holder.img;
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

			} catch (JSONException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}

			return convertView;
		}
		/*
	     * getting screen width
	     */

		public Bitmap DownloadImageFromPath(String path) {
			InputStream in = null;
			Bitmap bmp = null;
			/* ImageView iv = (ImageView)findViewById(R.id.img1); */
			int responseCode = -1;
			try {

				URL url = new URL(path);// "http://192.xx.xx.xx/mypath/img1.jpg
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.setDoInput(true);
				con.connect();
				responseCode = con.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					// download
					in = con.getInputStream();
					bmp = BitmapFactory.decodeStream(in);
					in.close();
					// iv.setImageBitmap(bmp);
				}

			} catch (Exception ex) {
				Log.e("Exception", ex.toString());
			}
			return bmp;
		}

	}

	class UserProfilePic extends AsyncTask<String, Void, String> {

		View cv;
		JSONObject jobj = null;

		public UserProfilePic(View convertView, JSONObject rec_user_json) {
			cv = convertView;
			jobj = rec_user_json;

		}

		@Override
		protected String doInBackground(String... params) {
			InputStream in = null;
			Bitmap bmp = null;
			/* ImageView iv = (ImageView)findViewById(R.id.img1); */
			int responseCode = -1;
			try {

				URL url = new URL(jobj.getString("image"));// "http://192.xx.xx.xx/mypath/img1.jpg
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.setDoInput(true);
				con.connect();
				responseCode = con.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					// download
					in = con.getInputStream();
					bmp = BitmapFactory.decodeStream(in);
					in.close();
					// iv.setImageBitmap(bmp);
				}

			} catch (Exception ex) {
				Log.e("Exception", ex.toString());
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] arr = baos.toByteArray();
			String result = Base64.encodeToString(arr, Base64.DEFAULT);
			return result;

		}

		public class Holder {
			TextView tv;
			ImageView img;
		}

		@Override
		protected void onPostExecute(String result) {

			Holder holder = new Holder();
			/*holder.tv = (TextView) cv.findViewById(R.id.rec_user_name);*/
			holder.img = (ImageView) cv.findViewById(R.id.rec_user_image);
			Bitmap bmp = null;
			try {
				holder.tv.setText(jobj.getString("username"));
				if (result != null && !result.isEmpty()) {
					byte[] encodeByte = Base64.decode(result, Base64.DEFAULT);
					bmp = BitmapFactory.decodeByteArray(encodeByte, 0,
							encodeByte.length);
					holder.img.setImageBitmap(bmp);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			/*
			 * Bitmap
			 * bmp=this.DownloadImageFromPath(rec_user_json.getString("image"));
			 */
			/**/
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

				
				String defValue = new String();
				SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
				String login_id = sp.getString("id", defValue);
				
				HttpRequester hr=new HttpRequester();
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("user_id", uid);
				jsonObject.accumulate("login_id", login_id);
				result=hr.POST("remove_rec", jsonObject, url);


			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
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

				
				String defValue = new String();
				SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
				String login_id = sp.getString("id", defValue);
			
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("user_id", uid);
				jsonObject.accumulate("login_id", login_id);

				HttpRequester hr =new HttpRequester();
				result=hr.POST("add_fav", jsonObject, url);
				
				

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}

			
			return result;
			
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progress = ProgressDialog.show(HomeActivity.this, "",
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

	@Override
	protected void onDestroy() {
		super.onDestroy();

		try {
			trimCache(this);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}

	public static void trimCache(Context context) {
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}
	

	@SuppressLint("SetJavaScriptEnabled")
	public void popupInit() {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		pw = new PopupWindow(inflater.inflate(R.layout.announcement_popup, null, false),android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);	
		try{
			pw.showAtLocation(this.findViewById(R.id.main_outer), Gravity.CENTER, 0, 0);
			pView=pw.getContentView();
			wv=(WebView) pView.findViewById(R.id.web_view);
			wv.getSettings().setJavaScriptEnabled(true);
			wv.loadUrl("http://www.menchd.com/client/announcement");
			
			ImageView close_popup=(ImageView) pView.findViewById(R.id.close_popup);
			close_popup.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					pw.dismiss();
					
				}
			});
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private class NewDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
        	String result = "";
        	try {
                //Thread.sleep(3000);
            	
    			
    				//page++;
    				JSONObject jsonObject = new JSONObject();
    				jsonObject.accumulate("user_id", login_id);
    				jsonObject.accumulate("page",String.valueOf(page));
    				jsonObject.accumulate("lat", stringLatitude);
    				jsonObject.accumulate("long", stringLongitude);
    					
    				HttpRequester hr=new HttpRequester();
    				String url=getString(R.string.json_url);
    				result=hr.POST("get_recommendations3", jsonObject, url);

    			

                
            }catch (JSONException e) {
				
				e.printStackTrace();
			}
            
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        	//list.add(0, result);
        	recommandation_list(result);
            // This should be called after refreshing finished
            mListView.completeRefreshing();

            super.onPostExecute(result);
        }
        
    }
	
	

}
