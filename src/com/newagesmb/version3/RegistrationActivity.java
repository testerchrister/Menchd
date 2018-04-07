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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends Activity {
	private static int REQUEST_PICTURE = 1;
	private static int SELECT_FILE=3;
	private int ACTIVITY_ID_PICK_PHOTO = 42;
	private int REQUEST_CAMERA=2;
	private int maxWidth = 350;
	private int maxHeight = 350;
	TextView tab_heading,name_lbl, password_lbl,dob_lbl, city_lbl, email_lbl,dob;
	EditText name,password,city,email;
	Button register_btn,cancel_btn;
	String city_name,latitude,longitude;
	File tmp_file;
	Bitmap bitMap;
	boolean img_status;
	static final int DATE_DIALOG_ID = 0;
	private int mYear, mMonth, mDay, mHour, mMinute;
	
	private Uri mImageCaptureUri=null;
	private ImageView mImageView;
	
	public ProgressDialog progress;	
	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	
	public RegistrationActivity(){
		 final Calendar c = Calendar.getInstance();
         mYear = c.get(Calendar.YEAR)-18;
         mMonth = c.get(Calendar.MONTH);
         mDay = c.get(Calendar.DAY_OF_MONTH);
         mHour = c.get(Calendar.HOUR_OF_DAY);
         mMinute = c.get(Calendar.MINUTE);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		img_status=false;
		Typeface font= Typeface.createFromAsset(getAssets(), "fonts/HELVET10.TTF");
		Typeface fontH= Typeface.createFromAsset(getAssets(), "fonts/HELVCOND.TTF");
		mImageView		= (ImageView) findViewById(R.id.profile_image);
		name_lbl=(TextView) findViewById(R.id.name_lbl);
		password_lbl=(TextView) findViewById(R.id.password_lbl);
		dob_lbl=(TextView) findViewById(R.id.dob_lbl);
		city_lbl=(TextView) findViewById(R.id.city_lbl);
		email_lbl=(TextView) findViewById(R.id.email_lbl);
		tab_heading=(TextView) findViewById(R.id.tab_heading);
		
		name=(EditText) findViewById(R.id.name);
		password=(EditText) findViewById(R.id.password);
		dob=(TextView) findViewById(R.id.dob);
		city=(EditText) findViewById(R.id.city);
		email=(EditText) findViewById(R.id.email);	
		
		register_btn=(Button) findViewById(R.id.register_btn);
		cancel_btn=(Button) findViewById(R.id.cancel_btn);
		
		tab_heading.setTypeface(fontH);
		name_lbl.setTypeface(font);
		name.setTypeface(font);
		password_lbl.setTypeface(font);
		password.setTypeface(font);
		dob_lbl.setTypeface(font);
		dob.setTypeface(font);
		email_lbl.setTypeface(font);
		email.setTypeface(font);
		city_lbl.setTypeface(font);
		city.setTypeface(font);
		
		register_btn.setTypeface(font);
		cancel_btn.setTypeface(font);
		
		city.setKeyListener(null);
		Bundle extras = getIntent().getExtras();

		if(extras != null) {
			city_name=getIntent().getStringExtra("city");
			latitude=getIntent().getStringExtra("lat");
			longitude=getIntent().getStringExtra("lng");
			 String user_name=getIntent().getStringExtra("name");
			 String pwd=getIntent().getStringExtra("password");
			 String user_dob=getIntent().getStringExtra("dob");
			 String user_email=getIntent().getStringExtra("email");	
			 
			 try{
					if(!city_name.isEmpty()){
						city.setText(city_name);
					}
					if(!user_name.isEmpty()){
						name.setText(user_name);
					}
					if(!pwd.isEmpty()){
						password.setText(pwd);
					}
					if(!user_dob.isEmpty()){
						dob.setText(user_dob);
					}
					if(!user_email.isEmpty()){
						email.setText(user_email);
					}
					if(getIntent().hasExtra("BitmapImage")){
						//bitMap=(Bitmap) getIntent().getParcelableExtra("BitmapImage");
						byte[] byteArray;
						byteArray =getIntent().getByteArrayExtra("BitmapImage");  
						if(byteArray.length>0){
							img_status=true;
							ByteArrayInputStream bytes = new ByteArrayInputStream(byteArray); 
							@SuppressWarnings("deprecation")
							BitmapDrawable bmd = new BitmapDrawable(bytes); 
							bitMap=bmd.getBitmap(); 
							if(bitMap!=null){
								
								mImageView.setImageBitmap(bitMap);
							}
						}
						//bitMap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);        
						
					}
					
			 }catch(NullPointerException e){
				 e.printStackTrace();
			 }
			
		}


		
		findViewById(R.id.city).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent citySearch=new Intent(RegistrationActivity.this,CitySearchActivity.class);
				citySearch.putExtra("name", name.getText().toString());
				citySearch.putExtra("password", password.getText().toString());
				citySearch.putExtra("dob", dob.getText().toString());
				citySearch.putExtra("email", email.getText().toString());
				citySearch.putExtra("from_activity", "1");
				if(bitMap!=null){
					/*ByteArrayOutputStream bs = new ByteArrayOutputStream();
					bitMap.compress(Bitmap.CompressFormat.PNG, 50, bs);
					citySearch.putExtra("BitmapImage", bs.toByteArray());*/
					
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					byte[] byteArray = stream.toByteArray();
					citySearch.putExtra("BitmapImage", byteArray);
				}
				RegistrationActivity.this.startActivity(citySearch);
				
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
		findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent login_intent=new Intent(RegistrationActivity.this,LoginActivity.class);
				RegistrationActivity.this.startActivity(login_intent);
				RegistrationActivity.this.finish();
				
			}
		});

		findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean cancel=false;
				
				/*if(mImageCaptureUri==null){*/
				if(!img_status){
					Toast.makeText(getBaseContext(),"Please upload profile image.",Toast.LENGTH_SHORT).show();
					cancel=true;
				}
				if(name.getText().toString().isEmpty()){
					
					name.setError("Plese enter your name.");
					cancel=true;
				}
				if(password.getText().toString().isEmpty()){
					
					password.setError("Plese enter your name.");
					cancel=true;
				}				
				if(password.getText().toString().length()<6){
					
					password.setError("Password is too short.");
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
				if(email.getText().toString().isEmpty()){
					
					email.setError("Please enter your email.");
					cancel=true;
				}else{
					Common cm=new Common();
					
					if(!cm.emailValidation(email.getText().toString())){
						email.setError("Invalid email address");		
						cancel = true;
					}
				}
				
				
				if(!cancel){
					register(name.getText().toString(), password.getText().toString(), dob.getText().toString(), city.getText().toString(),email.getText().toString());
					
					
				}
			}
		});
        
		
	
		
		
		mImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//dialog.show();
				//SelectPhoto();
				selectImage();
			}
		});
		
	}
	//Call the activity for select photo into the gallery
	private void SelectPhoto(){
	    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
	    photoPickerIntent.setType("image/*");
	    startActivityForResult(photoPickerIntent, ACTIVITY_ID_PICK_PHOTO);
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

	// check the return of the result
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
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
		String tempPath = getPath(mImageCaptureUri, RegistrationActivity.this);
		
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
			img_status=true;
        	mImageView.setImageBitmap(bitMap);
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
    
   public void register(String name, String password, String dob, String city, String email){
	   Registration rg=new Registration();
	   rg.execute(name,password,dob,city,email,latitude,longitude);
   }
   class Registration extends AsyncTask<String, Void, String>{

	@Override
	protected String doInBackground(String... params) {
		String result=null;
		try{
			JSONObject jobj=new JSONObject();
			jobj.accumulate("name", params[0]);
			jobj.accumulate("password", params[1]);
			jobj.accumulate("dob", params[2]);
			jobj.accumulate("city", params[3]);
			jobj.accumulate("email", params[4]);
			jobj.accumulate("lat", params[5]);
			jobj.accumulate("lng", params[6]);
			String url=getString(R.string.register_url);
			String imgPath="";//mImageCaptureUri.toString();
			HttpRequester hr=new HttpRequester();
			
			//tmp_file=new File(mImageCaptureUri.getPath());
			boolean call_stat=true;
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
			Bitmap bitmap = drawable.getBitmap();
			bitmap.compress(CompressFormat.JPEG, 50, bos);
			byte[] data = bos.toByteArray();
			String fileName = String.format("File_%d.png",new Date().getTime());
			ByteArrayBody bab = new ByteArrayBody(data, fileName);
			
			try {
				
				 
				if(call_stat)
					result=hr.sendMultiPartPost(url, imgPath, jobj,bab);
				else
					result="Empty File";	
				 /*Bitmap icon = BitmapFactory.decodeResource(RegistrationActivity.this.getResources(),
                         R.drawable.pr);*/
			/*	 Drawable prof_img=getResources().getDrawable(mImageView.getId());
				 BitmapDrawable bitmapDrawable = ((BitmapDrawable) prof_img);
				 Bitmap pro_img=bitmapDrawable.getBitmap();*/
				
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
		
		progress = ProgressDialog.show(RegistrationActivity.this, "","Please wait...", true);
	}
	@Override
	protected void onPostExecute(String result) {
		progress.dismiss();

		/*Log.v("On Post Execute",result);*/		
		
		/* Toast.makeText(getBaseContext(), "Profile Upload:"+result,
		 Toast.LENGTH_LONG).show();*/
		 try {
			JSONObject jobj=new JSONObject(result);
			boolean reg_status=jobj.getBoolean("status");
			if(reg_status){
				Toast.makeText(getBaseContext(),jobj.getString("message") ,
						 Toast.LENGTH_SHORT).show();
				String user_id=jobj.getString("user_id");
				Intent tearm_intent=new Intent(RegistrationActivity.this,TermsActivity.class);
				tearm_intent.putExtra("user_id", user_id);
				RegistrationActivity.this.startActivity(tearm_intent);
				RegistrationActivity.this.finish(); 
			}else{
				Toast.makeText(getBaseContext(),jobj.getString("message") ,
						 Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		 
		
	}
	   
   }
    
}
