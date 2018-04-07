package com.newagesmb.version3;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FilterActivity extends Activity {

	public static String PREF_NAME = "men_pref";
	TextView set_age,set_height,set_weight,location_settings_txt,keywords_settings_txt;
	String min_age,max_age,min_height,max_height,min_weight,max_weight,location,keyword,body_type,ethnicity,horoscope,college,
	income,smoke,drink,weed,drug,term,diet,pets,chat_status,religion, page="0";
	String user_id, latitude="", longitude="";
	Spinner body_spinner,ethnicity_spinner,horoscope_spinner,university_spinner,income_spinner,smoke_spinner,drink_spiSpinner,weed_spinner,drug_spinner,term_spinner,diet_spinner,pets_spinner;
	CheckBox online;
	ProgressDialog progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		
		body_spinner=(Spinner) findViewById(R.id.body_type);
		ethnicity_spinner=(Spinner) findViewById(R.id.ethnicity);
		horoscope_spinner=(Spinner) findViewById(R.id.horoscope);
		university_spinner=(Spinner) findViewById(R.id.university);
		income_spinner=(Spinner) findViewById(R.id.income);
		smoke_spinner=(Spinner) findViewById(R.id.smoke);
		drink_spiSpinner=(Spinner) findViewById(R.id.drink);
		weed_spinner=(Spinner) findViewById(R.id.weed);
		drug_spinner=(Spinner) findViewById(R.id.drug);
		term_spinner=(Spinner) findViewById(R.id.term);
		diet_spinner=(Spinner) findViewById(R.id.diet);
		pets_spinner=(Spinner) findViewById(R.id.pets);
		
		online=(CheckBox) findViewById(R.id.online);
		
		user_id=sp.getString("login_id", "");
		min_age=sp.getString("min_age", "");
		max_age=sp.getString("max_age", "");
		if(!min_age.isEmpty()&& !max_age.isEmpty() && !min_age.equals("") && !max_age.equals("")){
			set_age=(TextView) findViewById(R.id.set_age);
			set_age.setText(min_age+" To "+max_age);
		}
		findViewById(R.id.age_settings).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setTempChanges();
				Intent intent=new Intent(getBaseContext(),GaydarAgeSettingsActivity.class);
				FilterActivity.this.startActivity(intent);
				
			}
		});
		min_height=sp.getString("min_height", "");
		max_height=sp.getString("max_height", "");
		
		if(!min_height.isEmpty()&& !max_height.isEmpty() && !min_height.equals("") && !max_height.equals("")){
			set_height=(TextView) findViewById(R.id.set_height);
			set_height.setText(min_height+"cm To "+max_height+"cm");
		}
		findViewById(R.id.height_settings).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setTempChanges();
				Intent intent=new Intent(getBaseContext(),GaydarHeightSettingsActivity.class);
				FilterActivity.this.startActivity(intent);
				
			}
		});
		
		min_weight=sp.getString("min_weight", "");
		max_weight=sp.getString("max_weight", "");
		if(!min_weight.isEmpty()&& !max_weight.isEmpty() && !min_weight.equals("") && !max_weight.equals("")){
			set_weight=(TextView) findViewById(R.id.set_weight);
			set_weight.setText(min_weight+" lbs To "+max_weight+" lbs");
		}
		
		findViewById(R.id.weight_settings).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setTempChanges();
				Intent intent=new Intent(getBaseContext(),GaydarWeightSettingsActivity.class);
				FilterActivity.this.startActivity(intent);
				finish();
			}
		});
		
		location=sp.getString("set_location", "");
		if(!location.isEmpty() && !location.equals("")){
			location_settings_txt=(TextView) findViewById(R.id.location_settings_txt);
			location_settings_txt.setText(location);
			if(location.equals("current") && latitude.equals("") && longitude.equals("")){
				GPSTracker gpsTracker = new GPSTracker(this);
				if (gpsTracker.canGetLocation()) {
					latitude = String.valueOf(gpsTracker.latitude);
					longitude = String.valueOf(gpsTracker.longitude);
					SharedPreferences.Editor editor= getSharedPreferences(PREF_NAME, 0).edit();
					editor.putString("latitude", latitude);
					editor.putString("longitude", longitude);
					editor.commit();
				}else{
					Toast.makeText(getApplicationContext(), "Unable to find your currrent location", Toast.LENGTH_SHORT).show();
					location="my location";
				}
			}
			
		}
		findViewById(R.id.location_settings).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setTempChanges();
				Intent intent=new Intent(getBaseContext(),GaydarLocationSettingsActivity.class);
				FilterActivity.this.startActivity(intent);
				finish();
				
			}
		});
		
		keyword=sp.getString("set_keywords", "");
		if(!keyword.isEmpty() && !keyword.equals("")){
			keywords_settings_txt=(TextView) findViewById(R.id.keywords_settings_txt);
			keywords_settings_txt.setText(keyword);
		}
		findViewById(R.id.keywords_settings).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setTempChanges();
				Intent intent=new Intent(getBaseContext(),GaydarKeywordSettingsActivity.class);
				FilterActivity.this.startActivity(intent);
				finish();
				
			}
		});
		 findViewById(R.id.clear_settings).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				SharedPreferences.Editor editor= getSharedPreferences(PREF_NAME, 0).edit();
				editor.putString("body_type", "");
				editor.putString("ethnicity", "");
				editor.putString("horoscope", "");
				editor.putString("smoke", "");
				editor.putString("drink", "");
				editor.putString("weed", "");
				editor.putString("drug", "");
				editor.putString("diet", "");
				editor.putString("pets", "");
				editor.putString("college", "");
				editor.putString("term", "");
				editor.putString("income", "");
				editor.putString("chat_status", "");
				
				editor.putString("min_age", "");
				editor.putString("max_age", "");
				editor.putString("min_height", "");
				editor.putString("max_height", "");
				editor.putString("min_weight", "");
				editor.putString("max_weight", "");
				editor.putString("set_keywords", "");
				editor.putString("set_location", "");
				editor.putString("latitude", "");
				editor.putString("longitude", "");
				editor.commit();
				
				Intent intent=new Intent(FilterActivity.this, GaydarActivity.class);
				intent.putExtra("tab_select", "search");
				FilterActivity.this.startActivity(intent);
				finish();
			}
		});
		
		
		
		findViewById(R.id.save_settings).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				body_type=String.valueOf(body_spinner.getSelectedItemPosition()) ;
				if(body_type.equals("0")){
					body_type="";
				}
				if(smoke.equals("0")){
					smoke="";
				}
				if(drink.equals("0")){
					drink="";
				}
				if(weed.equals("0")){
					weed="";
				}
				if(drug.equals("0")){
					drug="";
				}
				if(diet.equals("0")){
					diet="";
				}
				if(pets.equals("0")){
					pets="";
				}
				if(college.equals("0")){
					college="";
				}
				if(term.equals("0")){
					term="";
				}
				if(income.equals("0")){
					income="";
				}
				if(income.equals("0")){
					income="";
				}
				ethnicity=String.valueOf(ethnicity_spinner.getSelectedItemPosition());
				
				horoscope=String.valueOf(horoscope_spinner.getSelectedItemPosition());
				smoke=String.valueOf(smoke_spinner.getSelectedItemPosition());
				drink=String.valueOf(drink_spiSpinner.getSelectedItemPosition());
				weed=String.valueOf(weed_spinner.getSelectedItemPosition());
				drug=String.valueOf(drug_spinner.getSelectedItemPosition());
				diet=String.valueOf(diet_spinner.getSelectedItemPosition());
				pets=String.valueOf(pets_spinner.getSelectedItemPosition());
				college=String.valueOf(university_spinner.getSelectedItemPosition());
				term=String.valueOf(term_spinner.getSelectedItemPosition());
				income=String.valueOf(income_spinner.getSelectedItemPosition());
				religion="";
				
				if(online.isChecked()){
					chat_status="Y";
				}else{
					chat_status="N";
				}
				//Toast.makeText(getApplicationContext(), "chat_status"+chat_status, Toast.LENGTH_SHORT).show();
				/* {"function":"filter_search", "parameters": {"user_id": "1","min_age": "25","max_age": "35","min_height": "145",
				 * "max_height": "235","min_weight": "60 ","max_weight": "300 ","bodytype": "1","ethnicity": "1","horoscope": "8",
				 * "smoke": "2","drink": "3","weed": "2","drugs": "2","diet": "6","pets": "2","religion": "","location": "my location",
				 * "keyword": "kochi","latitude": "","longitude": "","college": "2","term": "2","income": "3","chat_status": "Y",
				 * "page": "0"},"token":""}*/
				/*Toast.makeText(getApplicationContext(), "body_type"+body_type, Toast.LENGTH_SHORT).show();*/
				
				
				SharedPreferences.Editor editor= getSharedPreferences(PREF_NAME, 0).edit();
				editor.putString("body_type", body_type);
				editor.putString("ethnicity", ethnicity);
				editor.putString("horoscope", horoscope);
				editor.putString("smoke", smoke);
				editor.putString("drink", drink);
				editor.putString("weed", weed);
				editor.putString("drug", drug);
				editor.putString("diet", diet);
				editor.putString("pets", pets);
				editor.putString("college", college);
				editor.putString("term", term);
				editor.putString("income", income);
				editor.putString("chat_status", chat_status);
				editor.commit();
				
				Intent intent=new Intent(FilterActivity.this, GaydarActivity.class);
				intent.putExtra("tab_select", "search");
				FilterActivity.this.startActivity(intent);
				finish();
				
				
			}
		});
		body_type=sp.getString("body_type", "");
		if(!body_type.isEmpty() && !body_type.equals("")){
			body_spinner.setSelection(Integer.parseInt(body_type));
		}
		ethnicity=sp.getString("ethnicity", "");
		if(!ethnicity.isEmpty() && !ethnicity.equals("")){
			ethnicity_spinner.setSelection(Integer.parseInt(ethnicity));
		}
		horoscope=sp.getString("horoscope", "");
		if(!horoscope.isEmpty() && !horoscope.equals("")){
			horoscope_spinner.setSelection(Integer.parseInt(horoscope));
		}
		smoke=sp.getString("smoke", "");
		if(!smoke.isEmpty() && !smoke.equals("")){
			smoke_spinner.setSelection(Integer.parseInt(smoke));
		}
		drink=sp.getString("drink", "");
		if(!drink.isEmpty() && !drink.equals("")){
			drink_spiSpinner.setSelection(Integer.parseInt(drink));
		}
		weed=sp.getString("weed", "");
		if(!weed.isEmpty() && !weed.equals("")){
			weed_spinner.setSelection(Integer.parseInt(weed));
		}
		drug=sp.getString("drug", "");
		if(!drug.isEmpty() && !drug.equals("")){
			drug_spinner.setSelection(Integer.parseInt(drug));
		}
		diet=sp.getString("diet", "");
		if(!diet.isEmpty() && !diet.equals("")){
			diet_spinner.setSelection(Integer.parseInt(diet));
		}
		pets=sp.getString("pets", "");
		if(!pets.isEmpty() && !pets.equals("")){
			pets_spinner.setSelection(Integer.parseInt(pets));
		}
		college=sp.getString("college", "");
		if(!college.isEmpty() && !college.equals("")){
			university_spinner.setSelection(Integer.parseInt(college));
		}
		term=sp.getString("term", "");
		if(!term.isEmpty() && !term.equals("")){
			term_spinner.setSelection(Integer.parseInt(term));
		}
		income=sp.getString("income", "");
		if(!income.isEmpty() && !income.equals("")){
			income_spinner.setSelection(Integer.parseInt(income));
		}
		chat_status=sp.getString("chat_status", "");
		if(!chat_status.isEmpty() && !chat_status.equals("")){
			if(chat_status.equals("Y"))
				online.setChecked(true);
			else
				online.setChecked(false);	
		}
	}
	
	public void setTempChanges(){
		body_type=String.valueOf(body_spinner.getSelectedItemPosition()) ;
		if(body_type.equals("0")){
			body_type="";
		}
		if(smoke.equals("0")){
			smoke="";
		}
		if(drink.equals("0")){
			drink="";
		}
		if(weed.equals("0")){
			weed="";
		}
		if(drug.equals("0")){
			drug="";
		}
		if(diet.equals("0")){
			diet="";
		}
		if(pets.equals("0")){
			pets="";
		}
		if(college.equals("0")){
			college="";
		}
		if(term.equals("0")){
			term="";
		}
		if(income.equals("0")){
			income="";
		}
		if(income.equals("0")){
			income="";
		}
		ethnicity=String.valueOf(ethnicity_spinner.getSelectedItemPosition());
		
		horoscope=String.valueOf(horoscope_spinner.getSelectedItemPosition());
		smoke=String.valueOf(smoke_spinner.getSelectedItemPosition());
		drink=String.valueOf(drink_spiSpinner.getSelectedItemPosition());
		weed=String.valueOf(weed_spinner.getSelectedItemPosition());
		drug=String.valueOf(drug_spinner.getSelectedItemPosition());
		diet=String.valueOf(diet_spinner.getSelectedItemPosition());
		pets=String.valueOf(pets_spinner.getSelectedItemPosition());
		college=String.valueOf(university_spinner.getSelectedItemPosition());
		term=String.valueOf(term_spinner.getSelectedItemPosition());
		income=String.valueOf(income_spinner.getSelectedItemPosition());
		religion="";
		
		if(online.isChecked()){
			chat_status="Y";
		}else{
			chat_status="N";
		}
		SharedPreferences.Editor editor= getSharedPreferences(PREF_NAME, 0).edit();
		editor.putString("body_type", body_type);
		editor.putString("ethnicity", ethnicity);
		editor.putString("horoscope", horoscope);
		editor.putString("smoke", smoke);
		editor.putString("drink", drink);
		editor.putString("weed", weed);
		editor.putString("drug", drug);
		editor.putString("diet", diet);
		editor.putString("pets", pets);
		editor.putString("college", college);
		editor.putString("term", term);
		editor.putString("income", income);
		editor.putString("chat_status", chat_status);
		editor.commit();
	}
	
	class SubmitFilter extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String url=getString(R.string.json_url);
			String result="";
			JSONObject jobj;
			try {
				jobj = new JSONObject(params[0]);
				HttpRequester hr=new HttpRequester();
				result=hr.POST("filter_search", jobj, url);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progress = ProgressDialog.show(FilterActivity.this, "","Please wait...", true);
		}
		
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			
			
		}	

		
	}
	@Override
	public void onBackPressed(){
		
		Intent intent=new Intent(FilterActivity.this, GaydarActivity.class);
		intent.putExtra("tab_select", "search");
		FilterActivity.this.startActivity(intent);
		finish();
	
	
	}

}
