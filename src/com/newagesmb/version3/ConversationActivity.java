package com.newagesmb.version3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ConversationActivity extends Activity {
	ProgressDialog progress;
	//Sender is you
	String your_id,his_id,userImage,loginImage,username,age,perce,city;
	int last_msg_id=0;
	CustomAdapter adapter;
	private Uri mImageCaptureUri=null;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	private int REQUEST_CAMERA=2;
	private static int SELECT_FILE=3;
	private int ACTIVITY_ID_PICK_PHOTO = 42;
	boolean from_list;
	ImageView mImageView;
	Bitmap bitMap;
	boolean img_status;
	PopupWindow pw;
	private View pView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String receiver_id;
		final String sender_id;
		setContentView(R.layout.activity_conversation);
		mImageView=new ImageView(ConversationActivity.this);
		EditText tv=(EditText) findViewById(R.id.message_txt);
		hideKeyboard(tv);
		Bundle extra=getIntent().getExtras();
		if(extra!=null){
			his_id=receiver_id=getIntent().getStringExtra("receiver_id");
			your_id=sender_id=getIntent().getStringExtra("sender_id");
			loginImage=getIntent().getStringExtra("login_image");
			userImage=getIntent().getStringExtra("user_image");
			from_list=getIntent().getBooleanExtra("from_list", false);
			username=getIntent().getStringExtra("username");
			if(from_list){
				
				age=getIntent().getStringExtra("age");
				perce=getIntent().getStringExtra("perce");
				city=getIntent().getStringExtra("city");
				setUserDetails(username,age, city,  perce, userImage);
			}else{
				LinearLayout ll=(LinearLayout) findViewById(R.id.conversation_user);
				ll.setVisibility(View.GONE);
			}
			//getConversation(sender_id,receiver_id);
		     Timer timer=new Timer();
		     TimerTask getChat=new TimerTask() {
				 @Override
				 public void run() {
					 
					 runOnUiThread(new Runnable() {
						 @Override
						public void run() {
									//Toast.makeText(getBaseContext(), "Refresh:"+last_msg_id, Toast.LENGTH_SHORT).show();
									//last_msg_id++;
							 getConversation(your_id,his_id);
						}
					});
							
				}
			};
			timer.schedule(getChat,01,1000*5);
			
		}
		findViewById(R.id.conversation_user).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				visitProfile(his_id);
			}
		});
		findViewById(R.id.btnsend).setOnClickListener(new View.OnClickListener() {
			
			@SuppressWarnings("null")
			@Override
			public void onClick(View v) {
				/*Toast.makeText(getBaseContext(), "Post", Toast.LENGTH_SHORT).show();*/
				EditText tv=(EditText) findViewById(R.id.message_txt);
				String message=tv.getText().toString();
				tv.setText("");
				if(message!=null && !message.trim().equals("")){
					
					hideKeyboard(tv);
					sendMessage(message,"text");
				}
			}
		});
		findViewById(R.id.attachments).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				showAttachments(v);
				
			}
		});
		
		findViewById(R.id.custom_msg).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSuggest(v);
				
			}
		});
			
			
		
	}
/*	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != RESULT_OK) return;
	   
	    switch (requestCode) {
		    case PICK_FROM_CAMERA:
		    	doCrop();
		    	
		    	break;
		    	
		    case PICK_FROM_FILE: 
		    	mImageCaptureUri = data.getData();
		    	
		    	doCrop();
	    
		    	break;	    	
	    
		    case CROP_FROM_CAMERA:	    	
		        Bundle extras = data.getExtras();
	
		        if (extras != null) {	        	
		            Bitmap photo = extras.getParcelable("data");
		
		            mImageView.setImageBitmap(photo);
		            sendMessage("","image");
		            
		        }
	
		        File f = new File(mImageCaptureUri.getPath());            
		        
		        if (f.exists()) f.delete();
	
		        break;

	    }
	}*/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	//check the id of the result
	    if(data!=null){
	    	  if (requestCode == ACTIVITY_ID_PICK_PHOTO){
	  	        selectPhotoControl(data);
	  	        
	  	    }else if (requestCode == REQUEST_CAMERA) {
	  	    	try {
	  	    		Bundle extras = data.getExtras();
		  			bitMap = (Bitmap) extras.get("data");
		  			if (bitMap != null){
		  	        	mImageView.setImageBitmap(bitMap);
		  	        	img_status=true;
		  	        	sendMessage("","image");
		  	        }else{
		  	        	Toast.makeText(getApplicationContext(), bitMap.toString(), Toast.LENGTH_SHORT).show();
		  	        }
				} catch (Exception e) {
					e.printStackTrace();
				}
	  			
	  			
	  		} else if (requestCode == SELECT_FILE) {
	  			selectPhotoControl(data);
	  		}
	    }
	  
	}
private void selectPhotoControl(Intent data) {
		
		Uri selectedImageUri = data.getData();
		String tempPath = getPath(selectedImageUri, ConversationActivity.this);
		
		BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
		btmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(tempPath, btmapOptions);
		
		// The new size we want to scale to
		final int REQUIRED_SIZE = 200;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = btmapOptions.outWidth, height_tmp = btmapOptions.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options btmapOptions2 = new BitmapFactory.Options();
		btmapOptions2.inSampleSize = scale;
		bitMap = BitmapFactory.decodeFile(tempPath, btmapOptions2);
		if (bitMap != null){
        	mImageView.setImageBitmap(bitMap);
        	img_status=true;
        	sendMessage("","image");
        }else{
        	Toast.makeText(getApplicationContext(), bitMap.toString(), Toast.LENGTH_SHORT).show();
        }
		//Log.v("SELECT_FILE", bitMap.toString());
		
	}
public String getPath(Uri uri, Activity activity) {
	String[] projection = { MediaColumns.DATA };
	Cursor cursor = activity
			.managedQuery(uri, projection, null, null, null);
	int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	cursor.moveToFirst();
	return cursor.getString(column_index);
}
	private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
    	
    	Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        
        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
        
        int size = list.size();
        
        if (size == 0) {	        
        	Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();
        	
            return;
        } else {
        	intent.setData(mImageCaptureUri);
            
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            
        	if (size == 1) {
        		Intent i 		= new Intent(intent);
	        	ResolveInfo res	= list.get(0);
	        	
	        	i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
	        	
	        	startActivityForResult(i, CROP_FROM_CAMERA);
        	} else {
		        for (ResolveInfo res : list) {
		        	final CropOption co = new CropOption();
		        	
		        	co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
		        	co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
		        	co.appIntent= new Intent(intent);
		        	
		        	co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        	
		            cropOptions.add(co);
		        }
	        
		        CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);
		        
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setTitle("Choose Crop App");
		        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
		            @Override
					public void onClick( DialogInterface dialog, int item ) {
		                startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
		            }
		        });
	        
		        builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
		            @Override
		            public void onCancel( DialogInterface dialog ) {
		               
		                if (mImageCaptureUri != null ) {
		                    getContentResolver().delete(mImageCaptureUri, null, null );
		                    mImageCaptureUri = null;
		                }
		            }
		        } );
		        
		        AlertDialog alert = builder.create();
		        
		        alert.show();
        	}
        }
	}
	
	public void setUserDetails(String username,String age,String city, String perc,String userimage){
		
		ImageLoader imageLoader;
		imageLoader=new ImageLoader(ConversationActivity.this);
		
		ImageView iv=(ImageView) findViewById(R.id.prof_img);
		imageLoader.DisplayImage(userimage, iv);
		TextView user_name,age_city_txt,perc_txt;
		user_name=(TextView) findViewById(R.id.user_name);
		age_city_txt=(TextView) findViewById(R.id.age_city);
		perc_txt=(TextView) findViewById(R.id.perc);
		
		user_name.setText(username);
		age_city_txt.setText(age+", "+city);
		Float per=(float) 0.0;
		Log.v("perc",perc);
		if(!perc.isEmpty() && perc!=null && !perc.equals("null")){
			per=Float.parseFloat(perc);	
		}
		
		
		perc_txt.setText( String.valueOf(Math.round(per))+"%");
		
	}
	public void getConversation(String sender,String receiver){
		ConversationList cl=new ConversationList();
		cl.execute(sender,receiver);
	}
	
	class ConversationList extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String sender_id=params[0];
			String receiver_id=params[1];
			String url=getString(R.string.json_url);
			String result="";
			JSONObject job=new JSONObject();
			try {
				job.accumulate("profile_id", receiver_id);
				job.accumulate("login_id", sender_id);
				job.accumulate("open_status", "Y");
				job.accumulate("page", 0);
				HttpRequester hr=new HttpRequester();
				result=hr.POST("get_message2", job, url);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			//progress = ProgressDialog.show(ConversationActivity.this, "","Please wait...", true);
		}		
		@Override
		protected void onPostExecute(String result) {
			//progress.dismiss();
			//Log.v("On Post Execute",result);	
			
			chatHistory(result);
			
		}
		
		
	}
	public void chatHistory(String result){
		try {
			JSONArray jobj=new JSONArray(result);
			TextView no_item=(TextView) findViewById(R.id.no_item);
			if(jobj.length()>0){
				
				no_item.setVisibility(View.GONE);
				ListView lv= (ListView) findViewById(R.id.list_view);
				ArrayList< String> list=new ArrayList<String>();
				int i=0;
				for(;i<jobj.length();i++){
					list.add(jobj.getJSONObject(i).toString());
				}
				int new_msg_last_id=jobj.getJSONObject(0).getInt("id");
				if(last_msg_id!=new_msg_last_id){
					last_msg_id=new_msg_last_id;
					adapter=new CustomAdapter(ConversationActivity.this, R.layout.his_chat, list);
					lv.setAdapter(adapter);
					lv.setSelection(lv.getCount());
				}
				/*Log.v("First last_msg_id", String.valueOf(last_msg_id) );*/
				
			}else{
				no_item.setText("You haven't made any conversation with "+username);
			}
			
		}catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	class CustomAdapter extends ArrayAdapter<String>{
		ArrayList<String> mlist;
		Context mContext;
		int layoutResourceId;
		ImageLoader imageLoader;
		public CustomAdapter(Context context, int resource,ArrayList<String> list) {
			super(context, resource,list);
			mlist=list;
			mContext=context;
			layoutResourceId=resource;
			imageLoader = new ImageLoader(this.mContext);
		}
		public class Holder {
			TextView user_name, time_ago,status_msg,last_msg_time;
			TextView chat_message,receiver_time,sender_time,ago_time;
			ImageView user_img,view_chat,chat_img,time_img, chat_image,chat_map;
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
					Log.v("Single Chat",jobj.toString());
					String sender_id=jobj.getString("sender_id");
					//String receiver_id=jobj.getString("sender_id");
					String message,ago_time,chat_attach_image,chat_attach_map;
					message=jobj.getString("message");
					ago_time=jobj.getString("ago");
					chat_attach_image="";
					chat_attach_map="";
					if(!jobj.getString("chat_image").isEmpty()){
						chat_attach_image=jobj.getString("chat_image");
					}
					if(!jobj.getString("location").isEmpty()){
						chat_attach_map=jobj.getString("location");
					}
					
					
					
					if(sender_id.equals(his_id)){
						convertView = inflater.inflate(R.layout.his_chat_copy, parent, false);
						holder.user_img=(ImageView) convertView.findViewById(R.id.user_img);
						holder.time_img=(ImageView) convertView.findViewById(R.id.time_img);
						holder.chat_img=(ImageView) convertView.findViewById(R.id.chat_img);
						holder.chat_message=(TextView) convertView.findViewById(R.id.chat_message);
						holder.ago_time=(TextView) convertView.findViewById(R.id.ago_time);
						
						holder.chat_image=(ImageView) convertView.findViewById(R.id.chat_image);
						holder.chat_map=(ImageView) convertView.findViewById(R.id.chat_map);
						
						holder.ago_time.setText(ago_time);
						imageLoader.DisplayImage(userImage,holder.user_img);
						
						if(!chat_attach_image.equals("") && chat_attach_image!=null){
							holder.chat_message.setVisibility(View.GONE);
							imageLoader.DisplayImage(chat_attach_image,holder.chat_image);
							holder.chat_map.setVisibility(View.GONE);
							holder.chat_image.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									
									//loadPhoto((ImageView) arg0);
									popupInit((ImageView) arg0);
								}
							});
						}else if(!chat_attach_map.equals("") && chat_attach_map!=null){
							holder.chat_message.setVisibility(View.GONE);
							holder.chat_image.setVisibility(View.GONE);
							final String lat=jobj.getString("lat");
							final String lng=jobj.getString("long");
							final String location=jobj.getString("location");
							//Log.v("Map Message",jobj.toString());
							holder.chat_map.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									loadMap(lat,lng,location);
									
								}
							});
						}else if(!message.equals("") && message!=null){
							holder.chat_message.setText(message);
							holder.chat_image.setVisibility(View.GONE);
							holder.chat_map.setVisibility(View.GONE);
						}
						
						
						
					}else{
						convertView = inflater.inflate(R.layout.my_chat_copy, parent, false);
						holder.chat_img=(ImageView) convertView.findViewById(R.id.chat_img);
						holder.time_img=(ImageView) convertView.findViewById(R.id.time_img);
						holder.user_img=(ImageView) convertView.findViewById(R.id.user_img);
						holder.chat_message=(TextView) convertView.findViewById(R.id.chat_message);
						holder.ago_time=(TextView) convertView.findViewById(R.id.ago_time); 
						
						holder.chat_image=(ImageView) convertView.findViewById(R.id.chat_image);
						holder.chat_map=(ImageView) convertView.findViewById(R.id.chat_map);
						
						holder.ago_time.setText(ago_time);
						imageLoader.DisplayImage(loginImage,holder.user_img);
						
						if(!chat_attach_image.equals("") && chat_attach_image!=null){
							holder.chat_message.setVisibility(View.GONE);
							imageLoader.DisplayImage(chat_attach_image,holder.chat_image);
							holder.chat_map.setVisibility(View.GONE);
							
							holder.chat_image.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									
									//loadPhoto((ImageView) arg0);
									popupInit((ImageView) arg0);
								}
							});
						}else if(!chat_attach_map.equals("") && chat_attach_map!=null){
							holder.chat_message.setVisibility(View.GONE);
							holder.chat_image.setVisibility(View.GONE);
							final String lat=jobj.getString("lat");
							final String lng=jobj.getString("long");
							final String location=jobj.getString("location");
							holder.chat_map.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									loadMap(lat,lng,location);
									
								}
							});
						}if(!message.equals("") && message!=null){
							holder.chat_message.setText(message);
							holder.chat_image.setVisibility(View.GONE);
							holder.chat_map.setVisibility(View.GONE);
						}
						
						
					}
					
					

				
				
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			return convertView;
		}
		
	}
	public void sendMessage(String message,String type){
		String sender_id=your_id;
		String receiver_id=his_id;
		
		Messenger messenger=new Messenger();
		messenger.execute(message,sender_id,receiver_id,type);
	}
	
	class Messenger extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String message=params[0];
			String sender_id=params[1];
			String receiver_id=params[2];
			String type=params[3];
			
			String result="";
			JSONObject jsonObject=new JSONObject();
			try {
				jsonObject.accumulate("message", message);
				jsonObject.accumulate("sender_id", sender_id);
				jsonObject.accumulate("receiver_id", receiver_id);
				jsonObject.accumulate("lat", "");
				jsonObject.accumulate("lng", "");
				jsonObject.accumulate("location", "");
				/*jsonObject.accumulate("page", receiver_id);*/
				
				String url=getString(R.string.send_message_url);
				HttpRequester hr=new HttpRequester();	
				if(type.equals("image")){
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
					Bitmap bitmap = drawable.getBitmap();
					bitmap.compress(CompressFormat.JPEG, 50, bos);
					byte[] data = bos.toByteArray();
					String fileName = String.format("File_%d.png",new Date().getTime());
					ByteArrayBody bab = new ByteArrayBody(data, fileName);
					result=hr.sendImageMessage(url, jsonObject,bab);
				}else if(type.equals("map")){
					result=hr.sendMessage(url, jsonObject);
				}else{
					
					result=hr.sendMessage(url, jsonObject);
				}
				
				
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			//progress = ProgressDialog.show(ConversationActivity.this, "","Please wait...", true);
		}		
		@Override
		protected void onPostExecute(String result) {
			//progress.dismiss();
			//Log.v("Send Message Result:",result);	
			
			//AddChat(result);
			getConversation(your_id,his_id);
			
		}
		
		
	}
	public void AddChat(String result){
		try {
			JSONArray jobj=new JSONArray(result);
			TextView no_item=(TextView) findViewById(R.id.no_item);
			if(jobj.length()>0){
				
				no_item.setVisibility(View.GONE);
				ListView lv= (ListView) findViewById(R.id.list_view);
				ArrayList< String> tmp_list=new ArrayList<String>();
				ArrayList< String> list=new ArrayList<String>();
				int lat_id=jobj.getJSONObject(0).getInt("id");
				Log.v("Before last_msg_id", String.valueOf(last_msg_id) );
				for(int i=0;i<jobj.length();i++){
					int message_id=jobj.getJSONObject(i).getInt("id");
					if(message_id!=last_msg_id){
						tmp_list.add(jobj.getJSONObject(i).toString());
						
					}
					else{
						last_msg_id=lat_id;
						break;
					}
				}
				Collections.reverse(tmp_list);
				list.addAll(tmp_list);
				adapter=new CustomAdapter(ConversationActivity.this, R.layout.my_chat, list);
				lv.setAdapter(adapter);
				lv.setSelection(lv.getCount());
				
				Log.v("After last_msg_id", String.valueOf(last_msg_id) );
				
			}else{
				no_item.setText("You haven't made any conversation with ");
			}
			
		}catch (JSONException e) {
			e.printStackTrace();
		}	
	}
	public void hideKeyboard(View tv){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
	}
	public void visitProfile(String profile_id) {

		Intent prof_intent = new Intent(ConversationActivity.this,ProfileTabActivity.class);
		prof_intent.putExtra("profile_id", profile_id);
		ConversationActivity.this.startActivity(prof_intent);	
		ConversationActivity.this.finish(); 

	}
	public void showSuggest(View v){
		PopupMenu pm=new PopupMenu(ConversationActivity.this,v);
		MenuInflater inflator=pm.getMenuInflater();
		inflator.inflate(R.menu.popup_suggestion, pm.getMenu());
		pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				EditText tv=(EditText) findViewById(R.id.message_txt);
				tv.setText(item.getTitle().toString());
				return false;
			}
		});
		pm.show();
	}
	public void showAttachments(View v){
		PopupMenu pm=new PopupMenu(ConversationActivity.this,v);
		MenuInflater inflator=pm.getMenuInflater();
		inflator.inflate(R.menu.popup_attachments, pm.getMenu());
		pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				switch(item.getItemId()){
				case R.id.attach_images:
					
					//ImageDialogue();
					selectImage();
					return true;
				case R.id.attach_loaction:
					
					Intent location=new Intent(ConversationActivity.this,CitySearchActivity.class);
					location.putExtra("sender_id", your_id);
					location.putExtra("receiver_id", his_id);
					location.putExtra("from_activity", "3"); 
					location.putExtra("age", age);
					location.putExtra("perce", perce);
					location.putExtra("city", city);
					location.putExtra("login_image", loginImage);
					location.putExtra("user_image", userImage);
					location.putExtra("username", username);
					
					ConversationActivity.this.startActivity(location);
					finish();
					/*Toast.makeText(getBaseContext(),"You Clicked : Edit Caption",Toast.LENGTH_SHORT).show(); */
					return true;
				case R.id.cancel:
					
					return false;
				}
				return false;
			}
		});
		//
		pm.show();
	}
	
	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, REQUEST_CAMERA);
					
				} else if (items[item].equals("Choose from Library")) {
					
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					
					intent.setType("image/*");
					
					startActivityForResult(
							Intent.createChooser(intent, "Select File"),
							SELECT_FILE);
					
					
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
				
			}
		});
		builder.show();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	public void popupInit(ImageView imageView) {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		pw = new PopupWindow(inflater.inflate(R.layout.image_popup, null, false),android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT, true);
		try{
			pw.showAtLocation(ConversationActivity.this.findViewById(R.id.main_outer), Gravity.CENTER, 0, 0);
			pView=pw.getContentView();
			ImageView img=(ImageView) pView.findViewById(R.id.popup_Image);
			BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
			Bitmap bitmap = drawable.getBitmap();
			img.setImageBitmap(bitmap);
			ImageButton close_popup=(ImageButton) pView.findViewById(R.id.close_btn);
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
     @SuppressWarnings("deprecation")
	private void loadPhoto(ImageView imageView) {

    	 
    	 ImageView tempImageView = imageView;
    	 
    	 tempImageView.getLayoutParams().height=android.view.ViewGroup.LayoutParams.FILL_PARENT;
    	 tempImageView.getLayoutParams().width=android.view.ViewGroup.LayoutParams.FILL_PARENT;
    	 
         AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
         LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

         View layout = inflater.inflate(R.layout.full_image_view,
                 (ViewGroup) findViewById(R.id.layout_root));
         ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
         image.setImageDrawable(tempImageView.getDrawable());
         imageDialog.setView(layout);
		 imageDialog.setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener(){
		
		     @Override
			public void onClick(DialogInterface dialog, int which) {
		         dialog.dismiss();
		     }
		
		 });
  

         imageDialog.create();
         imageDialog.show();     
     }
     private void loadMap(String lat,String lng,String location){
    	 Intent intent=new Intent(getApplicationContext(), GoogleMapActivity.class);
    	 intent.putExtra("lat", lat);
    	 intent.putExtra("lng", lng);
    	 intent.putExtra("location", location);
    	 this.startActivity(intent);
     }

}
