package com.newagesmb.version3;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newagesmb.staggeredgridviewdemo.loader.ImageLoader;
import com.newagesmb.staggeredgridviewdemo.views.ScaleImageView;
import com.newagesmb.version3.HomeGridActivity.AddFaviriteRecommandations;
import com.newagesmb.version3.HomeGridActivity.RemoveRecommandations;

public class StaggeredAdapter extends ArrayAdapter<String> {

	private ImageLoader mLoader;
	int layoutResourceId;
	ArrayList<String> rec_users;
	String rec_user_id;
	Context mContext;
	public ProgressDialog progress;
	public static String PREF_NAME = "men_pref";
	String json_url;
/*	public StaggeredAdapter(Context context, int textViewResourceId,
			String[] objects) {
		super(context, textViewResourceId, objects);
		mLoader = new ImageLoader(context);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater layoutInflator = LayoutInflater.from(getContext());
			convertView = layoutInflator.inflate(R.layout.row_staggered_demo,
					null);
			holder = new ViewHolder();
			holder.imageView = (ScaleImageView) convertView .findViewById(R.id.imageView1);
			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();

		mLoader.DisplayImage(getItem(position), holder.imageView);

		return convertView;
	}*/
	public StaggeredAdapter(Context context, int textViewResourceId,
			ArrayList<String> list) {
		super(context, textViewResourceId, list);
		mLoader = new ImageLoader(context);
		this.layoutResourceId = textViewResourceId;
		rec_users = list;
		this.mContext = context;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		json_url = mContext.getString(R.string.json_url);
		

		if (convertView == null) {
			
			
			
			
			 /* LayoutInflater layoutInflator = LayoutInflater.from(getContext());
			  convertView = layoutInflator.inflate(R.layout.row_staggered_demo,
					null);
			convertView = layoutInflator.inflate(layoutResourceId,
					null);
			*/
			
			LayoutInflater inflater = ((Activity) mContext)
					.getLayoutInflater();
			
			convertView = inflater.inflate(R.layout.single_user_grid, parent, false);
			final ViewHolder holder;
			holder = new ViewHolder();
			holder.imageView = (ScaleImageView) convertView .findViewById(R.id.rec_user_image);
			
			convertView.setTag(holder);
			
		}
		final ViewHolder holder;
		holder = (ViewHolder) convertView.getTag();
		try {
			final JSONObject rec_user_json = new JSONObject(rec_users.get(
					position).toString());
			
			holder.imageView = (ScaleImageView) convertView
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
			mLoader.DisplayImage(rec_user_json.getString("image"),
					holder.imageView);
			holder.tv_pers.setText(String.valueOf(Math.round( Float
					.parseFloat(rec_user_json.getString("pers")) )) + " %");
			
			
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
						
						e.printStackTrace();
					}

					RemoveRecommandations del_reco = new RemoveRecommandations();
					
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
						
						rec_users.set(position, tmp_usr.toString());

					} catch (JSONException e) {
						
						e.printStackTrace();
					}

					AddFaviriteRecommandations fav_reco = new AddFaviriteRecommandations();
					
					fav_reco.execute(json_url, rec_usr_id, rec_status);

				}
			});
			
			ScaleImageView prof_img = holder.imageView;
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
			
			e.printStackTrace();
		}
		
		
		//mLoader.DisplayImage(getItem(position), holder.imageView);

		return convertView;
	}

	static class ViewHolder {
		ScaleImageView imageView;
		TextView tv, tv_pers;
		ImageView img, del_img, fav_img;
	}
	
	class RemoveRecommandations extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			String rec = removeRecUser(params[0], params[1]);
			return rec;
		}

		protected void onPostExecute(boolean result) {

			Toast.makeText(mContext, "Deleted User Id:" + result,
					Toast.LENGTH_LONG).show();

		}

		public String removeRecUser(String url, String uid) {
			String defValue = new String();
			SharedPreferences sp = mContext.getSharedPreferences(
					PREF_NAME, 0);
			String login_id= sp.getString("id", defValue);
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

			String defValue = new String();
			SharedPreferences sp = mContext.getSharedPreferences(
					PREF_NAME, 0);
			String login_id= sp.getString("id", defValue);
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

			progress = ProgressDialog.show(mContext, "",
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

			Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
					.show();
			if(!mutual_fav.equals("N")){
				Toast.makeText(mContext, mutual_fav, Toast.LENGTH_SHORT)
				.show();
			}
		}

	}
	
	public void visitProfile(String profile_id) {

		Intent prof_intent = new Intent(mContext,ProfileTabActivity.class);
		prof_intent.putExtra("profile_id", profile_id);
		mContext.startActivity(prof_intent);
		
		
	}
	
}
