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
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MessageTabActivity extends Activity implements OnItemClickListener {
	public static String profile_id;
	public static String profile_details;
	public static String login_id;
	public static String PREF_NAME = "men_pref";
	public ProgressDialog progress;	
	public ImageLoader imageLoader;
	public static String userImage;
	public static String loginImage;
	public static String profile_user_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_tab);//activity_message_tab
		profile_id = getIntent().getStringExtra("profile_id");
		profile_details=getIntent().getStringExtra("profile_details");

		//Log.v("Profile Details",profile_details);
		messageList(profile_details);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message_tab, menu);
		return true;
	}
	public class Holder {
		TextView name_header;
		ImageView prof_img, del_img, fav_img;
	}
	public void messageList(String details){
		 
		try {
			JSONObject jobj = new JSONObject(details);
			final Holder holder = new Holder();
			
			if (!jobj.isNull("username")) {

				holder.name_header = (TextView) findViewById(R.id.prof_user_name);
				holder.name_header.setText(jobj.getString("username"));
				userImage=jobj.getString("userimage");
				loginImage=jobj.getString("loginimage");
				profile_user_name=jobj.getString("username");
			}
		}catch(JSONException je){
			je.printStackTrace();
		}
		String defValue = new String();
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		login_id = sp.getString("id", defValue);
		UserMessages um=new UserMessages();
		um.execute(profile_id,login_id);
	}
	public class UserMessages extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			String profile_id=arg0[0];
			String user_id=arg0[1];
			String result=POST(profile_id, user_id);
			return result;
		}
		public String POST(String pro_id,String usr_id){
			
			JSONObject job=new JSONObject();
			String result=null;
			String fn_name=null;
			try {
				
				HttpRequester hr=new HttpRequester();
				if(login_id.equals(profile_id)){
					job.accumulate("user_id", usr_id);
					fn_name="get_messages";
				}else{
					job.accumulate("profile_id", pro_id);
					job.accumulate("login_id", usr_id);
					job.accumulate("open_status", "Y");
					job.accumulate("page", 0);
					fn_name="get_message2";
				}
				
				/* Log.v("Function Name",fn_name);*/
				String url=getString(R.string.json_url);
				result=hr.POST(fn_name, job, url);
			   // Log.v("Message List",result);
		}catch (JSONException e) {
			
			e.printStackTrace();
		}
		
			return result;
	}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(MessageTabActivity.this, "","Please wait...", true);
		}

		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			/*Log.v("On Post Execute",result);	*/
			
				messageListing(result);
			
		}
		public void conversation(String result){
			
			
		}
		public void messageListing(String result){
			try {
				JSONArray jobj=new JSONArray(result);
				ListView lv= (ListView) findViewById(R.id.list_view);
				ArrayList< String> list=new ArrayList<String>();
				for(int i=0;i<jobj.length();i++){
					list.add(jobj.getJSONObject(i).toString());
				}
				final CustomAdapter adapter;
				if(jobj.length()==0){
					TextView no_item=(TextView) findViewById(R.id.no_item);
					if(login_id.equals(profile_id)){
						no_item.setText("No conversations in your inbox");
					}else{
						no_item.setText("You haven't made any conversation with "+profile_user_name);
					}
					
				}else{
					TextView no_item=(TextView) findViewById(R.id.no_item);
					no_item.setVisibility(View.GONE);
					if(login_id.equals(profile_id)){
						adapter= new CustomAdapter(MessageTabActivity.this,R.layout.message_list, list);
						lv.setAdapter(adapter);
						lv.setOnItemClickListener(MessageTabActivity.this);
					}else{
						adapter = new CustomAdapter(MessageTabActivity.this,R.layout.conversation_other, list);
						lv.setAdapter(adapter);
					}
					
					
				}
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
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
			imageLoader = new ImageLoader(this.mContext);
		}
		@Override
		public int getCount() {
			
			return mlist.size();
		}

		@Override
		public String getItem(int position) {
			
			return mlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return 0;
		}

		public class Holder {
			TextView user_name, time_ago,status_msg,last_msg_time;
			TextView chat_message,receiver_time,sender_time,ago_time,view_chat;
			ImageView user_img,chat_img,time_img;
			ImageView receiver_img,sender_img,receiver_chat_img,sender_chat_img,receiver_time_img,sender_time_img;
			LinearLayout msg_layout;
			
			
		}
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent){
			
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			if (convertView == null) {
				
				convertView = inflater.inflate(layoutResourceId, parent, false);
				

			}
			final Holder holder = new Holder();
			holder.msg_layout=(LinearLayout) findViewById(R.id.single_message_item);
			try {
				JSONObject jobj = new JSONObject(mlist.get(position).toString());
				if(login_id.equals(profile_id)){
					final String sender_id=jobj.getString("sender_id");
					
					/*	holder.msg_layout.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							Toast.makeText(getBaseContext(), "Sender ID:"+sender_id, Toast.LENGTH_SHORT).show();
							
						}
					});*/
					
					//Log.v("Single Msg",jobj.toString());
					//Log.v("read status",jobj.getString("read_status").toString());
					String read_status=jobj.getString("read_status").toString();
					/*if(read_status.equals("N")){
						holder.msg_layout.setBackgroundResource(R.drawable.message_list_border_unread);
					}else{
						holder.msg_layout.setBackgroundResource(R.drawable.message_list_border);
					}*/
					holder.user_img=(ImageView) convertView.findViewById(R.id.user_img);
					imageLoader.DisplayImage(jobj.getString("user_image"),holder.user_img);
					
					holder.user_name=(TextView) convertView.findViewById(R.id.user_name);
					holder.user_name.setText(jobj.getString("username"));
					
					holder.view_chat=(TextView) convertView.findViewById(R.id.view_chat);
					holder.view_chat.setVisibility(View.VISIBLE);
					if(read_status.equals("N")){
						if(Integer.parseInt(jobj.getString("msg_status"))>0){
							if(Integer.parseInt(jobj.getString("unread_count"))>0){
								holder.view_chat.setText(jobj.getString("unread_count"));
								holder.view_chat.setBackgroundResource(R.drawable.count);
							}
						}else{
							if(login_id.equals(sender_id)){
								
								holder.view_chat.setBackgroundResource(R.drawable.sent);
								holder.view_chat.setText("");
							}
							else{
	
								holder.view_chat.setVisibility(View.GONE);
							
							}
						}
					}else{
						if(login_id.equals(sender_id)){
							
							holder.view_chat.setBackgroundResource(R.drawable.sent);
							holder.view_chat.setText("");
						}
						else{

							holder.view_chat.setVisibility(View.GONE);
						}
					}
					
						
						

					
					holder.time_ago=(TextView) convertView.findViewById(R.id.time_ago);
					holder.time_ago.setText(jobj.getString("ago"));
					
					String message=jobj.getString("message");
					message=message.substring(0,Math.min(message.length(),15));
					if(message.length()>15){
						message+="...";
					}
					holder.status_msg=(TextView) convertView.findViewById(R.id.status_msg);
					holder.status_msg.setText(message);
					
					holder.last_msg_time=(TextView) convertView.findViewById(R.id.last_msg_time);
					holder.last_msg_time.setText(jobj.getString("date"));
					
					
					
				}else{
					String sender_id=jobj.getString("sender_id");
					String message,ago_time;
					message=jobj.getString("message");
					ago_time=jobj.getString("ago");
					
					
					if(sender_id.equals(profile_id)){
						convertView = inflater.inflate(R.layout.his_chat, parent, false);
						holder.user_img=(ImageView) convertView.findViewById(R.id.user_img);
						holder.time_img=(ImageView) convertView.findViewById(R.id.time_img);
						holder.chat_img=(ImageView) convertView.findViewById(R.id.chat_img);
						holder.chat_message=(TextView) convertView.findViewById(R.id.chat_message);
						holder.ago_time=(TextView) convertView.findViewById(R.id.ago_time);
						
						holder.chat_message.setText(message);
						holder.ago_time.setText(ago_time);
						imageLoader.DisplayImage(userImage,holder.user_img);
						
					}else{
						convertView = inflater.inflate(R.layout.my_chat, parent, false);
						holder.chat_img=(ImageView) convertView.findViewById(R.id.chat_img);
						holder.time_img=(ImageView) convertView.findViewById(R.id.time_img);
						holder.user_img=(ImageView) convertView.findViewById(R.id.user_img);
						holder.chat_message=(TextView) convertView.findViewById(R.id.chat_message);
						holder.ago_time=(TextView) convertView.findViewById(R.id.ago_time);
						
						holder.chat_message.setText(message);
						holder.ago_time.setText(ago_time);
						imageLoader.DisplayImage(loginImage,holder.user_img);
					}
					
					

				}
				
				
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			return convertView;
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				String item=(String) arg0.getItemAtPosition(position);
				//Log.v("Itemsssssssss",item);
				//Toast.makeText(getBaseContext(), item.toString(), Toast.LENGTH_SHORT).show();
				
				// Here we have to redirect to the new Activity for chat
				try {
					JSONObject messageItem=new JSONObject(item);
					String receiver_id=messageItem.getString("receiver_id");
					String sender_id=messageItem.getString("sender_id");
					String loginimage=messageItem.getString("loginimage");
					String user_image=messageItem.getString("user_image");
					String username=messageItem.getString("username");
					String age=messageItem.getString("age");
					String perce=messageItem.getString("perce");
					String city=messageItem.getString("city");
					String his_id="";
					if(receiver_id.equals(login_id)){
						his_id=sender_id;
					}else{
						his_id=receiver_id;
					}
					Intent convesationIntent=new Intent(MessageTabActivity.this,ConversationActivity.class);
					convesationIntent.putExtra("receiver_id", his_id);
					convesationIntent.putExtra("sender_id", login_id);
					convesationIntent.putExtra("login_image", loginimage);
					convesationIntent.putExtra("user_image", user_image);
					convesationIntent.putExtra("username", username);
					convesationIntent.putExtra("age", age);
					convesationIntent.putExtra("perce", perce);
					convesationIntent.putExtra("city", city);
					convesationIntent.putExtra("from_list", true);
					MessageTabActivity.this.startActivity(convesationIntent);
					
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
				
				//convesationIntent.
	}
}			
