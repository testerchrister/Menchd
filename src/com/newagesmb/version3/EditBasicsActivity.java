package com.newagesmb.version3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class EditBasicsActivity extends Activity {

	TextView tab_heading,name_lbl, password_lbl,dob_lbl, city_lbl,edit_picture,dob,edit_video;
	EditText name,city,about;
	ImageButton save_btn;
	String profile_details,profile_id,user_image,video_url;	
	String user_name,dob_string,about_user,city_name,latitude,longitude,user_id;
	File tmp_file;
	public boolean video_exist;
	private int REQUEST_CAMERA=2;
	private static int SELECT_FILE=3;
	private int ACTIVITY_ID_PICK_PHOTO = 42;
	Bitmap bitMap;
	static boolean change_status=false;
	static final int DATE_DIALOG_ID = 0;
	private int mYear, mMonth, mDay;
	
	private Uri mImageCaptureUri=null;
	private ImageView mImageView;
	boolean img_status,img_change_status;
	public ProgressDialog progress;	
	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	
	public EditBasicsActivity(){
		 final Calendar c = Calendar.getInstance();
         mYear = c.get(Calendar.YEAR)-18;
         mMonth = c.get(Calendar.MONTH);
         mDay = c.get(Calendar.DAY_OF_MONTH);

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_basic);
		img_status=false;
		img_change_status=false;
		video_exist=false;
		Typeface font= Typeface.createFromAsset(getAssets(), "fonts/HELVET10.TTF");
		Typeface fontH= Typeface.createFromAsset(getAssets(), "fonts/HELVCOND.TTF");
		mImageView	= (ImageView) findViewById(R.id.profile_image);
		edit_picture=(TextView) findViewById(R.id.edit_picture);
		name_lbl=(TextView) findViewById(R.id.name_lbl);
		dob_lbl=(TextView) findViewById(R.id.dob_lbl);
		city_lbl=(TextView) findViewById(R.id.city_lbl);
		tab_heading=(TextView) findViewById(R.id.tab_heading);
		
		name=(EditText) findViewById(R.id.name);
		dob=(TextView) findViewById(R.id.dob);
		city=(EditText) findViewById(R.id.city);
		about=(EditText) findViewById(R.id.about_txt);
		edit_video=(TextView) findViewById(R.id.edit_video);
		save_btn=(ImageButton) findViewById(R.id.save_btn);
		
		
		tab_heading.setTypeface(fontH);
		name_lbl.setTypeface(font);
		name.setTypeface(font);
		dob_lbl.setTypeface(font);
		dob.setTypeface(font);
		city_lbl.setTypeface(font);
		city.setTypeface(font);
		
		city.setKeyListener(null);
		Bundle extras = getIntent().getExtras();

		if(extras != null) {
			profile_details=getIntent().getStringExtra("profile_details");
			profile_id=getIntent().getStringExtra("profile_id");
			
			if(!profile_details.isEmpty()){
				
				
				try {
					JSONObject user_obj=new JSONObject(profile_details);
					user_id=user_obj.getString("user_id");
					user_name=user_obj.getString("username");
					user_image=user_obj.getString("userimage");
					latitude=user_obj.getString("lat");
					longitude=user_obj.getString("long");
					
					if(getIntent().hasExtra("BitmapImage")){
						//bitMap=(Bitmap) getIntent().getParcelableExtra("BitmapImage");
						byte[] byteArray;
						byteArray =getIntent().getByteArrayExtra("BitmapImage");  
						if(byteArray.length>0){
							
							ByteArrayInputStream bytes = new ByteArrayInputStream(byteArray); 
							@SuppressWarnings("deprecation")
							BitmapDrawable bmd = new BitmapDrawable(bytes); 
							bitMap=bmd.getBitmap(); 
							if(bitMap!=null){
								img_status=true;
								img_change_status=true;
								mImageView.setImageBitmap(bitMap);
								
							}
						}
						//bitMap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);        
						
					}else{
						if(!user_image.isEmpty()||user_image!=null){
							img_status=true;
							ImageLoader il=new ImageLoader(getApplicationContext());
							il.DisplayImage(user_image, mImageView);
							mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
									   "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
						}	
					}
					if(!user_name.isEmpty()){
						name.setText(user_name);
					}else{
						Log.v("User Name",user_name);
					}
					
					dob_string=user_obj.getString("dob");
					if(!dob_string.isEmpty()){
						dob.setText(dob_string);
					}
					city_name=user_obj.getString("city");
					if(!city_name.isEmpty()){
						city.setText(city_name);
					}
					about_user=user_obj.getString("about");
					if(!about_user.isEmpty() && about_user!=null){
						about.setText(about_user);
					}
					
					if(!user_obj.getString("video_url").equals("") && user_obj.getString("video_url")!=null){
						edit_video.setText(" Edit Video");
						video_url=user_obj.getString("video_url");
						video_exist=true;
					}
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
			}
						
		}
		
		
		findViewById(R.id.edit_video).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				if(video_exist){
					showPopup(arg0);
				}else{
					Intent intent=new Intent(EditBasicsActivity.this, VideoUploadActivity.class);
					intent.putExtra("user_id", user_id);
					EditBasicsActivity.this.startActivity(intent);
				}
				
			}
		});
		
		findViewById(R.id.city).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent citySearch=new Intent(EditBasicsActivity.this,CitySearchActivity.class);
				citySearch.putExtra("profile_details", profile_details);
				citySearch.putExtra("profile_id", profile_id);
				citySearch.putExtra("from_activity", "2");	
				
				if(img_change_status){
					
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					byte[] byteArray = stream.toByteArray();
					citySearch.putExtra("BitmapImage", byteArray);
					
				}
				EditBasicsActivity.this.startActivity(citySearch);
				finish();
				/*Toast.makeText(getBaseContext(), name.getText(),Toast.LENGTH_SHORT).show();*/
				
			}
		});
		
		findViewById(R.id.dob).setOnClickListener(new View.OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)getSystemService(
          		      Context.INPUT_METHOD_SERVICE);
          		imm.hideSoftInputFromWindow(dob.getWindowToken(), 0);
				showDialog(DATE_DIALOG_ID);
				
			}
		});
		
		findViewById(R.id.dob).setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				InputMethodManager imm = (InputMethodManager)getSystemService(
	          		      Context.INPUT_METHOD_SERVICE);
	          		imm.hideSoftInputFromWindow(dob.getWindowToken(), 0);
				
			}
		});


		findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean cancel=false;
				
				//if(mImageCaptureUri==null){
				if(!img_status){
					Toast.makeText(getBaseContext(),"Plese upload profile image.",Toast.LENGTH_SHORT).show();
					cancel=true;
				}
				if(name.getText().toString().isEmpty()){
					
					name.setError("Plese enter your name.");
					cancel=true;
				}
				if(dob.getText().toString().isEmpty()){
					
					dob.setError("Please enter your DOB.");
					cancel=true;
				}
				if(city.getText().toString().isEmpty()){
					
					city.setError("Please enter your city.");
					cancel=true;
				}
				
				if(!cancel){
					//register(name.getText().toString(), password.getText().toString(), dob.getText().toString(), city.getText().toString(),email.getText().toString());
					updateBasic(name.getText().toString(), dob.getText().toString(), city.getText().toString(),about.getText().toString(),user_id);
					
				}
			}
		});
		
		
		mImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//dialog.show();
				selectImage();
				
			}
		});
	
		edit_picture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//dialog.show();
				selectImage();
				
			}
		});
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
	@Override
	public void onBackPressed(){
	     if(change_status){
	    	 Intent intent=new Intent(EditBasicsActivity.this, ProfileTabActivity.class);
	    	 intent.putExtra("profile_id", profile_id);
	    	 EditBasicsActivity.this.startActivity(intent);
	    	 finish();
	    	 
	     }else{
	    	 finish();
	     }
	}
	 @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//check the id of the result
		 if(data!=null){
			  if (requestCode == ACTIVITY_ID_PICK_PHOTO){
			        selectPhotoControl(data);
			        
			    }else if (requestCode == REQUEST_CAMERA) {
			    	try{
			    		Bundle extras = data.getExtras();
						bitMap = (Bitmap) extras.get("data");
						if (bitMap != null){
							img_status=true;
							img_change_status=true;
				        	mImageView.setImageBitmap(bitMap);
				        }else{
				        	Toast.makeText(getApplicationContext(), bitMap.toString(), Toast.LENGTH_SHORT).show();
				        }
			    	}catch (Exception e) {
						e.printStackTrace();
					}
					
					
				} else if (requestCode == SELECT_FILE) {
					selectPhotoControl(data);
				} 
		 }
		  
		}
	 private void selectPhotoControl(Intent data) {
			
		 	mImageCaptureUri = data.getData();
			String tempPath = getPath(mImageCaptureUri, EditBasicsActivity.this);
			
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
	        	img_change_status=true;
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
	    
	
	DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
        // the callback received when the user "sets" the Date in the DatePickerDialog
				@Override
                public void onDateSet(DatePicker view, int yearSelected,
                                      int monthOfYear, int dayOfMonth) {
                   int year = yearSelected;
                   int month = (monthOfYear+1);
                   int day = dayOfMonth;
                   // Set the Selected Date in Select date Button
                   if(year>mYear){
                	   Toast.makeText(getBaseContext(), "Your Age must be 18+",Toast.LENGTH_SHORT).show();
                   }else{
                	   dob.setText(day+"-"+month+"-"+year);
                   }
                   
                   
                }

				
				
            };	
    @Override 
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
 // create a new DatePickerDialog with values you want to show 
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);

       
        }
        return null;
    } 
    
   public void updateBasic(String name, String dob, String city,String about,String user_id){
	   UdateUser rg=new UdateUser();
	   rg.execute(name,dob,city,latitude,longitude,about,user_id);
	   //Log.v("Post Vallues",name+"::"+dob+"::"+city+"::"+latitude+"::"+longitude+"::"+about+"::"+user_id);
   }
   class UdateUser extends AsyncTask<String, Void, String>{

	@Override
	protected String doInBackground(String... params) {
		String result=null;
		try{
			JSONObject jobj=new JSONObject();
			
			jobj.accumulate("name", params[0]);
			jobj.accumulate("dob", params[1]);
			jobj.accumulate("city", params[2]);
			jobj.accumulate("lat", params[3]);
			jobj.accumulate("lng", params[4]);
			jobj.accumulate("about", params[5]);
			jobj.accumulate("user_id",params[6]);
			String url=getString(R.string.update_profile_url);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
			Bitmap bitmap = drawable.getBitmap();
			bitmap.compress(CompressFormat.JPEG, 50, bos);
			byte[] data = bos.toByteArray();
			String fileName = String.format("File_%d.png",new Date().getTime());
			ByteArrayBody bab = new ByteArrayBody(data, fileName);
			
			try {
				HttpRequester hr=new HttpRequester();
				result=hr.updateUserBasic(url,jobj,bab);
				
				
			} catch (ClientProtocolException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			
		}catch(JSONException e){
			e.printStackTrace();
		}
		return result;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		progress = ProgressDialog.show(EditBasicsActivity.this, "","Please wait...", true);
	}
	@Override
	protected void onPostExecute(String result) {
		progress.dismiss();

		/*Log.v("On Post Execute",result);	*/	
		
		 
		 try {
			JSONObject job=new JSONObject(result);
			 boolean status=job.getBoolean("status");
			 if(status){
				 Toast.makeText(getBaseContext(),job.getString("message") ,
						 Toast.LENGTH_LONG).show();
				 change_status=true;
				 //Reset Image
			 }else{
				 Toast.makeText(getBaseContext(),job.getString("message") ,
						 Toast.LENGTH_LONG).show();
			 }
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		 
		
	}
	   
   }
   
   public void showPopup(final View v){
	   PopupMenu pm=new PopupMenu(EditBasicsActivity.this, v);
	   MenuInflater inflater=pm.getMenuInflater();
	   inflater.inflate(R.menu.video_menu,pm.getMenu());
	   pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
		
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch(item.getItemId()){
			case R.id.delete:
				DeleteVideo dv=new DeleteVideo();
				dv.execute();
				return true;
			case R.id.update:
				
				Intent intent=new Intent(EditBasicsActivity.this, VideoUploadActivity.class);
				intent.putExtra("user_id", user_id);
				intent.putExtra("profile_id", profile_id);
				intent.putExtra("profile_details", profile_details);
				EditBasicsActivity.this.startActivity(intent);
				return true;
			case R.id.watch:
				Intent intent1=new Intent(EditBasicsActivity.this, VideoPlayActivity.class);
				intent1.putExtra("video_url", video_url);
				intent1.putExtra("user_name", user_name);
				EditBasicsActivity.this.startActivity(intent1);
				return true;
			case R.id.cancel:
				
				return false;

				
			}
			return false;
			
		}
	});
	   pm.show();
   }
   
   class DeleteVideo extends AsyncTask<String, Void, String>{

	@Override
	protected String doInBackground(String... params) {
		String url=getString(R.string.video_delete_url);
		url+="/"+user_id;
		HttpRequester hr=new HttpRequester();
		String result=hr.GET(url);
		return result;
	}
	@Override
	protected void onPostExecute(String result) {
		
		video_exist=false;
		edit_video.setText(" Add Video");
		
	}
	   
   }
    
}
