package com.newagesmb.version3;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditDetailsActivity extends Activity {

	public static String profile_id;
	public static String profile_details;
	public static boolean window_min = true;
	public static String PREF_NAME = "men_pref";
	public ProgressDialog progress;
	public ImageLoader img_loader;
	
	TextView eye_color, hair_color,religion,  home_town,career_field, languages,weight;
	ImageView prof_img, del_img, fav_img;
	Spinner body_type,ethnicity,university,horoscope, smoke, drink, weed, drug, diet, sex,position, pets,term, 
	income,height_ft,height_in, kids_question, want_kids;
	String city;
	boolean change_status=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_details);
		
		profile_id = getIntent().getStringExtra("profile_id");
		profile_details=getIntent().getStringExtra("profile_details");
		//Log.v("profile_details",profile_details);
		
		religion = (TextView) findViewById(R.id.religion);
		home_town = (TextView) findViewById(R.id.home_town);
		eye_color=(TextView) findViewById(R.id.eye_color);
		hair_color=(TextView) findViewById(R.id.hair_color);
		body_type=(Spinner) findViewById(R.id.body_type);
		ethnicity=(Spinner) findViewById(R.id.ethnicity);
		horoscope = (Spinner) findViewById(R.id.horoscope);
		religion=(TextView) findViewById(R.id.religion);
		university=(Spinner) findViewById(R.id.university);
		career_field=(TextView) findViewById(R.id.career_field);
		smoke=(Spinner) findViewById(R.id.smoke);
		drink=(Spinner) findViewById(R.id.drink);
		weed=(Spinner) findViewById(R.id.weed);
		drug=(Spinner) findViewById(R.id.drug);
		sex=(Spinner) findViewById(R.id.sex);
		position=(Spinner) findViewById(R.id.position);
		pets=(Spinner) findViewById(R.id.pets);
		languages=(TextView) findViewById(R.id.languages);
		term=(Spinner) findViewById(R.id.term); 
		home_town=(TextView) findViewById(R.id.home_town);		
		kids_question=(Spinner) findViewById(R.id.kids_question);
		want_kids=(Spinner) findViewById(R.id.want_kids);
		diet=(Spinner) findViewById(R.id.diet); 
		height_ft=(Spinner) findViewById(R.id.height_ft);
		height_in=(Spinner) findViewById(R.id.height_in);
		weight=(TextView) findViewById(R.id.weight); 
		income=(Spinner) findViewById(R.id.income);	
		
		setBasicDetails(profile_details);
		
		findViewById(R.id.edit_profile).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				JSONObject jobj=new JSONObject();
				try {
					jobj.accumulate("user_id", profile_id);
					int height_ft_cm=height_ft.getSelectedItemPosition();
					int height_in_cm=height_in.getSelectedItemPosition();

					float height_val=(float) (height_ft_cm*30.48);
					height_val+=height_in_cm*2.54;
					jobj.accumulate("height", height_val);
					
					jobj.accumulate("weight", weight.getText());
					jobj.accumulate("eye_color", eye_color.getText());
					jobj.accumulate("hair_color", hair_color.getText());
					
					if(body_type.getSelectedItemPosition()!=0){
						jobj.accumulate("body_type", body_type.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("body_type", "");
					}
					if(ethnicity.getSelectedItemPosition()!=0){
						jobj.accumulate("ethnicity", ethnicity.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("ethnicity", "");
					}
					if(horoscope.getSelectedItemPosition()!=0){
						jobj.accumulate("horoscope", horoscope.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("horoscope", "");
					}					

					jobj.accumulate("religion", religion.getText());
					if(university.getSelectedItemPosition()!=0){
						jobj.accumulate("college", university.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("college", "");
					}					

					jobj.accumulate("carrier_type", career_field.getText());
					if(smoke.getSelectedItemPosition()!=0){
						jobj.accumulate("smoke", smoke.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("smoke", "");
					}

					if(drink.getSelectedItemPosition()!=0){
						jobj.accumulate("drink", drink.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("drink", "");
					}					
					if(weed.getSelectedItemPosition()!=0){
						jobj.accumulate("weed", weed.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("weed", "");
					}						
					if(drug.getSelectedItemPosition()!=0){
						jobj.accumulate("drug", drug.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("drug", "");
					}	
					if(position.getSelectedItemPosition()!=0){
						jobj.accumulate("position", position.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("position", "");
					}
					if(pets.getSelectedItemPosition()!=0){
						jobj.accumulate("pet", pets.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("pet", "");
					}

					jobj.accumulate("language", languages.getText());
					if(term.getSelectedItemPosition()!=0){
						jobj.accumulate("term", term.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("term", "");
					}				

					jobj.accumulate("home_town", home_town.getText());
					jobj.accumulate("city", city);

					if(kids_question.getSelectedItemPosition()!=0){
						jobj.accumulate("kids", kids_question.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("kids", "");
					}					
					if(want_kids.getSelectedItemPosition()!=0){
						jobj.accumulate("want_kid", want_kids.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("want_kid", "");
					}	
					if(diet.getSelectedItemPosition()!=0){
						jobj.accumulate("diet", diet.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("diet", "");
					}						
					if(sex.getSelectedItemPosition()!=0){
						jobj.accumulate("sex", sex.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("sex", "");
					}
					if(income.getSelectedItemPosition()!=0){
						jobj.accumulate("income", income.getSelectedItemPosition()-1);
					}else{
						jobj.accumulate("income", "");
					}
					UpdateDetails ud=new UpdateDetails();
					ud.execute(jobj.toString());

				} catch (JSONException e) {
					
					e.printStackTrace();
				}
				
			}
		});
	}
	
	@Override
	public void onBackPressed(){
		if(change_status){
			Intent intent=new Intent(EditDetailsActivity.this, ProfileTabActivity.class);
			intent.putExtra("tab", "details");
			intent.putExtra("profile_id", profile_id);
			EditDetailsActivity.this.startActivity(intent);
			finish();
		}else{
			finish();
		}
	}
	protected void setBasicDetails(String details) {
		try {
			JSONObject jobj = new JSONObject(details);
			//Log.v("jobj",jobj.toString());
			if(!jobj.isNull("eye_color")){
				eye_color.setText(jobj.getString("eye_color"));
			}
			if (!jobj.isNull("hair_color")) {
				hair_color.setText(jobj.getString("hair_color"));
			}

			if (!jobj.isNull("body_type")) {
				
				body_type.setSelection(jobj.getInt("body_type")+1);
			}
			if (!jobj.isNull("ethnicity")) {
				
				ethnicity.setSelection(jobj.getInt("ethnicity")+1);
			}
			if (!jobj.isNull("horoscope")) {
				
				horoscope.setSelection(jobj.getInt("horoscope")+1);
			}
			
			if (!jobj.isNull("religion")) {
				
				religion.setText(jobj.getString("religion"));
			}
			
			if (!jobj.isNull("college")) {
				
				university.setSelection(jobj.getInt("college")+1);
				
			}
			if (!jobj.isNull("carrier_type")) {
				
				career_field.setText(jobj.getString("carrier_type"));
			}
			if (!jobj.isNull("smoke")) {
				
				smoke.setSelection(jobj.getInt("smoke")+1);
			}
			if (!jobj.isNull("drink")) {
				
				drink.setSelection(jobj.getInt("drink")+1);
			}
			if (!jobj.isNull("weed")) {
				
				weed.setSelection(jobj.getInt("weed")+1);
			}
			if (!jobj.isNull("drug")) {
				
				drug.setSelection(jobj.getInt("drug")+1);
			}
			if (!jobj.isNull("sex")) {
				
				sex.setSelection(jobj.getInt("sex")+1);
			}
			if (!jobj.isNull("position")) {
				
				position.setSelection(jobj.getInt("position")+1);
			}
			if (!jobj.isNull("pet")) {
				
				pets.setSelection(jobj.getInt("pet")+1);
			}
			if (!jobj.isNull("language")) {
				
				languages.setText(jobj.getString("language"));
			}
			if (!jobj.isNull("term")) {
				
				term.setSelection(jobj.getInt("term")+1);
			}

			if (!jobj.isNull("home_town")) {
				
				home_town.setText(jobj.getString("home_town"));
			}
			if (!jobj.isNull("kids") && !jobj.getString("kids").equals("")) {
				
				kids_question.setSelection(jobj.getInt("kids")+1);
			}
			if (!jobj.isNull("want_kid") && !jobj.getString("want_kid").equals("")) {
				
				want_kids.setSelection(jobj.getInt("want_kid")+1);
			}
			if (!jobj.isNull("diet")) {
				
				diet.setSelection(jobj.getInt("diet")+1);
			}
			if (!jobj.isNull("height")) {

				int height = jobj.getInt("height");
				int feet = (int) (height / 30.48);
				int inch = (int) ((int) (height % 30.48) / 2.54);
				//Log.v("Height",height+"::"+feet+"::"+inch);
				height_ft.setSelection(feet);
				height_in.setSelection(inch+1);
			}
			if (!jobj.isNull("weight")) {
				
				weight.setText(jobj.getString("weight") + "  Pounds");
			}
			if (!jobj.isNull("income") && !jobj.getString("income").equals("")) {

				
				income.setSelection(jobj.getInt("income")+1);
			}
			city=jobj.getString("city");
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	class UpdateDetails extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String details=params[0];
			String url=getString(R.string.json_url);
			JSONObject jsonObject;
			String result="";
			HttpRequester hr=new HttpRequester();
			try {
				jsonObject = new JSONObject(details);
				result=hr.POST("set_profile", jsonObject, url);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(EditDetailsActivity.this, "","Please wait...", true);
		}
		
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			
			try {
				JSONObject jobj=new JSONObject(result);
				if(jobj.getBoolean("status")){
					Toast.makeText(getBaseContext(), jobj.getString("message"), Toast.LENGTH_SHORT).show();
					change_status=true;
				}else{
					Toast.makeText(getBaseContext(), jobj.getString("message"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
		}		
	}

}
