package com.newagesmb.version3;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BasicProfActivity extends Activity {

	public static String profile_id;
	public static String profile_details;
	public static boolean window_min = true;
	public static String PREF_NAME = "men_pref";
	public ProgressDialog progress;
	public ImageLoader img_loader;
	String[] horoscope_array = { "Aries", "Taurus", "Gemini", "Cancer", "Leo",
			"Virgo", "Libra", "Scorpio", "Sagittarius", "Capricorn",
			"Aquarius", "Pisces" };
	String[] university_array = { "Academy of Art University (CA)",
			"Albright College", "American University",
			"Arizona State University", "Art Institute of Fort Lauderdale",
			"Art Institute of Pittsburgh", "Art Institute of Portland",
			"Art Institute of Washington", "Auburn University",
			"Azusa Pacific University", "Ball State University",
			"Baruch College", "Bates College", "Belmont University",
			"Bentley University", "Berklee College of Music",
			"Binghamton University", "Boise State University",
			"Boston College", "Boston University", "Bowie State University",
			"Brandeis University", "Brown University", "Bucknell University",
			"Buffalo State College",
			"California Polytechnic State University - Pomona",
			"California Polytechnic State University - San Luis Obispo",
			"California State University - Fullerton",
			"California State University - Long Beach",
			"California State University - Northridge", "Carleton College",
			"Carnegie Mellon University", "Champlain College",
			"Chapman University", "Christopher Newport University",
			"City College of New York", "Claremont McKenna College",
			"Clark Atlanta University", "Clemson University", "Colby College",
			"Colgate University", "College of Charleston", "College of DuPage",
			"College of the Holy Cross", "Colorado State University",
			"Columbia College Chicago", "Columbia University",
			"Connecticut College", "Cornell University",
			"Culver-Stockton College", "Dalhousie University (Canada)",
			"Dartmouth College", "Denison University", "DePaul University",
			"Dickinson College", "Drake University", "Drexel University",
			"Duke University", "East Carolina University",
			"Eastern Connecticut State University",
			"Eastern Washington University", "Elon University",
			"Emerson College", "Emory University", "Fairfield University",
			"Fashion Institute of Technology (FIT)", "FIDM", "FIT",
			"Flagler College", "Florida A&M University",
			"Florida International University", "Florida State University",
			"Fordham University", "Franklin and Marshall College",
			"Geneseo State University", "George Brown College",
			"George Mason University", "George Washington University",
			"Georgetown University", "Georgia Southern University",
			"Georgia State University", "Gettysburg College",
			"Gonzaga University", "Hampton University", "Hartwick College",
			"Harvard University", "High Point University",
			"Hofstra University", "Howard University",
			"Humber College (Canada)", "Hunter College",
			"Illinois State University", "Illinois Wesleyan University",
			"Indiana State University", "Indiana University",
			"Iowa State University", "Ithaca College",
			"James Madison University", "Johns Hopkins University",
			"Kansas State University", "Kent State University",
			"La Salle University", "Lafayette College", "Lasell College",
			"Lehigh University", "LIM College", "Lipscomb University",
			"Louisiana State University", "Loyola Marymount University",
			"Loyola University Chicago", "Loyola University Maryland",
			"Manhattan College", "Manhattan School of Music", "Marist College",
			"Marquette University", "Maryville College",
			"McGill University (Canada)", "McMaster University (Canada)",
			"Memorial University of Newfoundland", "Messiah College",
			"Miami International University of Art & Design",
			"Miami University (Ohio)", "Michigan State University",
			"Middle Tennessee State University", "Minnesota State University",
			"Misericordia University", "Missouri State University",
			"Montclair State University", "Moore College of Art & Design",
			"Morehouse College", "Mount St. Mary's University",
			"New England College", "New Mexico State University",
			"New York University", "North Carolina A&T State University",
			"North Carolina State University", "Northeastern University",
			"Northern Arizona University", "Northern Illinois University",
			"Northern Michigan University", "Northwestern University",
			"Oakland University", "Ohio State University", "Ohio University",
			"Old Dominion University", "Oregon State University",
			"Pace University", "Parsons New School for Design",
			"Penn State University", "Pepperdine University",
			"Philadelphia University", "Pitzer College", "Pomona College",
			"Prairie View A&M University", "Providence College",
			"Purdue University", "Queens University (Canada)",
			"Radford University", "Rhode Island School of Design", "RISD",
			"Roanoke College", "Rochester Institute of Technology",
			"Rutgers University", "Ryerson University (Canada)",
			"Sacred Heart University", "Saint Anselm College",
			"Saint Joseph's University", "Saint Mary's College - Notre Dame",
			"Saint Vincent College", "Salve Regina University",
			"San Diego Miramar College", "San Diego State University",
			"San Francisco State University", "Santa Clara University", "SCAD",
			"Seattle Pacific University", "Seattle University",
			"Smith College", "South Texas College",
			"Southern Methodist University", "Spelman College",
			"St. Edward's University", "St. John's University",
			"St. Olaf College", "Stonehill College", "Stony Brook University",
			"Suffolk University", "SUNY Oneonta", "Syracuse University",
			"Temple University", "Texas A&M University",
			"Texas Christian University", "Texas State University",
			"Texas Tech University", "The Art Institute of Portland",
			"The College of New Jersey", "The College of Saint Rose",
			"The Illinois Institute of Art-Chicago", "Towson University",
			"Trinity College", "Tufts University", "Tulane University",
			"Union College", "University at Albany", "University of Alabama",
			"University of Alberta (Canada)", "University of Arizona",
			"University of Arkansas", "University of Bridgeport",
			"University of British Columbia (Canada)",
			"University of California - Berkeley",
			"University of California - Irvine",
			"University of California - Los Angeles",
			"University of California - San Diego",
			"University of California - Santa Barbara",
			"University of California - Santa Cruz",
			"University of California- Davis",
			"University of California-Riverside",
			"University of Central Florida", "University of Chicago",
			"University of Cincinnati", "University of Colorado",
			"University of Connecticut", "University of Dayton",
			"University of Delaware", "University of Denver",
			"University of Florida", "University of Georgia",
			"University of Hawaii", "University of Houston",
			"University of Idaho", "University of Illinois",
			"University of Iowa", "University of Kansas",
			"University of Kentucky", "University of Louisiana",
			"University of Mary Washington", "University of Maryland",
			"University of Massachusetts", "University of Miami",
			"University of Michigan", "University of Minnesota",
			"University of Mississippi", "University of Missouri - Columbia",
			"University of Nebraska", "University of Nevada",
			"University of New Hampshire", "University of North Carolina",
			"University of North Florida", "University of Notre Dame",
			"University of Oklahoma", "University of Oregon",
			"University of Pennsylvania", "University of Pittsburgh",
			"University of Portland", "University of Puget Sound",
			"University of Rhode Island", "University of Richmond",
			"University of San Diego", "University of San Francisco",
			"University of South Carolina",
			"University of Southern California", "University of St Thomas",
			"University of Tampa", "University of Tennessee",
			"University of Texas", "University of Texas - Pan American",
			"University of Toronto (Canada)", "University of Utah",
			"University of Vermont", "University of Virginia",
			"University of Washington", "University of Waterloo (Canada)",
			"University of Western Ontario (Canada)", "University of Windsor",
			"University of Wisconsin", "Valdosta State University",
			"Vanderbilt University", "Vassar College", "Villanova University",
			"Virginia Commonwealth University", "Virginia Tech",
			"Wake Forest University", "Washington State University",
			"Washington University-St Louis", "Webster University",
			"Wellesley College", "Wesleyan University",
			"West Virginia University", "Western Illinois University",
			"Western Kentucky University", "Western Washington University",
			"Westminster College", "Wheaton College", "Whittier College",
			"Wilfrid Laurier University (Canada)", "Wofford College",
			"Woodbury University", "Yale University", "Other" };
	String[] term_array = { "LTR", "STR", "Friendship", "Open Relationship" };
	String[] income_array = { "Less than $25000", "$25001-$50000",
			"$50001-$100000", "$100001-$150000", "$150001-$200000",
			"More than $200000" };
	String[] smoke_array = { "When I’m drinking", "A pack a day",
			"I’m giving it up", "No" };
	String[] drink_array = { "No", "Once in a while", "Socially", "Wino" };
	String[] weed_array = { "Never", "A hit a day", "When I was younger",
			"Socially" };
	String[] drug_drray = { "No", "Yes", "On occasion",
			"I’ve never experimented with Drugs" };
	String[] diet_array = { "Vegan", "Vegetarian", "Paleo", "Pescetarian",
			"Some meat", "Meat lover", "Other", "Clear" };
	String[] sex_array = { "Daily", "I'm a Virgin", "When im in the mood" };
	String[] position_array = { "Top", "Bottom", "Versatile" };
	String[] pets_array = { "Pet Friendly", "It depends", "I'm allergic",
			"I'm a Dog guy" };
	String[] havekids_array = { "Yes", "No" };
	String[] wantKids_array = { "Yes", "No", "May Be" };
	String[] body_array = { "Slim", "Slender", "Twink", "Bear", "Chub",
			"Gym Bunny", "Gym Rat", "Daddy muscle pup", "Muscle Bear",
			"Athletic", "Average", "Jacked", "Otter" };
	String[] ethni_array = { "Asian", "Middle Eastern", "Black",
			"Native American", "Indian", "Pacific Islander", "Hispanic/Latino",
			"White", "Other" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basic_prof);
		String defValue = new String();
		profile_id = getIntent().getStringExtra("profile_id");
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		String login_id = sp.getString("id", defValue);
		if(!profile_id.equals(login_id)){
			ImageButton ib=(ImageButton) findViewById(R.id.edit_profile);
			ib.setVisibility(View.INVISIBLE);
		}
		
		profile_details=getIntent().getStringExtra("profile_details");
		setBasicDetails(profile_details);
		
		findViewById(R.id.edit_profile).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//ProfileEditActivity.class
				Intent prof_intent = new Intent(BasicProfActivity.this,EditDetailsActivity.class);
				prof_intent.putExtra("profile_id", profile_id);
				prof_intent.putExtra("profile_details", profile_details);
				BasicProfActivity.this.startActivity(prof_intent);
				
			}
		});
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.basic_prof, menu);
		return true;
	}
	
	public class Holder {
		TextView horoscope, religion, home_town,
				current_city, college_univercity, career_field, languages,
				term, income, height, weight, eye_color, hair_color,
				body_type, ethnicity, smoke, drink, weed, drug, diet, sex,
				position, pets, kids_question, want_kids, about_txt,name_header;
		ImageView prof_img, del_img, fav_img;
	}
	
	protected void setBasicDetails(String details) {
		
		try {
			JSONObject jobj = new JSONObject(details);
			final Holder holder = new Holder();
			
			if (!jobj.isNull("username")) {

				holder.name_header = (TextView) findViewById(R.id.prof_user_name);
				holder.name_header.setText(jobj.getString("username"));
			}
			if (!jobj.isNull("horoscope")) {

				holder.horoscope = (TextView) findViewById(R.id.horoscope);
				holder.horoscope.setText(horoscope_array[jobj
						.getInt("horoscope")]);
			}
			if (!jobj.isNull("religion")) {
				holder.religion = (TextView) findViewById(R.id.religion);
				holder.religion.setText(jobj.getString("religion"));
			}

			if (!jobj.isNull("home_town")) {
				holder.home_town = (TextView) findViewById(R.id.home_town);
				holder.home_town.setText(jobj.getString("home_town"));
			}
			if (!jobj.isNull("city")) {

				holder.current_city = (TextView) findViewById(R.id.current_city);
				holder.current_city.setText(jobj.getString("city"));
			}
			if (!jobj.isNull("college")) {
				holder.college_univercity = (TextView) findViewById(R.id.college_univercity);
				holder.college_univercity.setText(university_array[jobj
						.getInt("college")]);
			}

			if (!jobj.isNull("carrier_type")) {
				holder.career_field = (TextView) findViewById(R.id.career_field);
				holder.career_field.setText(jobj.getString("carrier_type"));
			}
			if (!jobj.isNull("language")) {
				holder.languages = (TextView) findViewById(R.id.languages);
				holder.languages.setText(jobj.getString("language"));
			}

			if (!jobj.isNull("term")) {
				holder.term = (TextView) findViewById(R.id.term);
				holder.term.setText(term_array[jobj.getInt("term")]);
			}

			if (!jobj.isNull("income")
					&& !jobj.getString("income").equals("")) {

				holder.income = (TextView) findViewById(R.id.income);
				holder.income.setText(income_array[jobj.getInt("income")]);
			}

			if (!jobj.isNull("height")) {

				int height = jobj.getInt("height");
				int feet = (int) (height / 30.48);
				int inch = (int) ((int) (height % 30.48) / 2.54);
				holder.height = (TextView) findViewById(R.id.height);
				holder.height.setText(String.valueOf(feet) + "' "
						+ String.valueOf(inch) + "\"");
			}
			if (!jobj.isNull("weight")) {
				holder.weight = (TextView) findViewById(R.id.weight);
				holder.weight
						.setText(jobj.getString("weight") + "  Pounds");
			}

			if (!jobj.isNull("eye_color")) {
				holder.eye_color = (TextView) findViewById(R.id.eye_color);
				holder.eye_color.setText(jobj.getString("eye_color"));
			}

			if (!jobj.isNull("hair_color")) {
				holder.hair_color = (TextView) findViewById(R.id.hair_color);
				holder.hair_color.setText(jobj.getString("hair_color"));
			}
			if (!jobj.isNull("body_type")) {
				holder.body_type = (TextView) findViewById(R.id.body_type);
				holder.body_type.setText(body_array[jobj
						.getInt("body_type")]);
			}
			if (!jobj.isNull("ethnicity")) {
				holder.ethnicity = (TextView) findViewById(R.id.ethnicity);
				holder.ethnicity.setText(ethni_array[jobj
						.getInt("ethnicity")]);
			}
			if (!jobj.isNull("smoke")) {
				holder.smoke = (TextView) findViewById(R.id.smoke);
				holder.smoke.setText(smoke_array[jobj.getInt("smoke")]);
			}
			if (!jobj.isNull("drink")) {
				holder.drink = (TextView) findViewById(R.id.drink);
				holder.drink.setText(drink_array[jobj.getInt("drink")]);
			}
			if (!jobj.isNull("weed")) {
				holder.weed = (TextView) findViewById(R.id.weed);
				holder.weed.setText(weed_array[jobj.getInt("weed")]);
			}
			if (!jobj.isNull("drug")) {
				holder.drug = (TextView) findViewById(R.id.drug);
				holder.drug.setText(drug_drray[jobj.getInt("drug")]);
			}
			if (!jobj.isNull("diet")) {
				holder.diet = (TextView) findViewById(R.id.diet);
				holder.diet.setText(diet_array[jobj.getInt("diet")]);
			}

			if (!jobj.isNull("sex")) {
				holder.sex = (TextView) findViewById(R.id.sex);
				holder.sex.setText(sex_array[jobj.getInt("sex")]);
			}
			if (!jobj.isNull("position")) {
				holder.position = (TextView) findViewById(R.id.position);
				holder.position.setText(position_array[jobj
						.getInt("position")]);
			}
			if (!jobj.isNull("pet")) {
				holder.pets = (TextView) findViewById(R.id.pets);
				holder.pets.setText(pets_array[jobj.getInt("pet")]);
			}
			if (!jobj.isNull("kids")) {
				holder.kids_question = (TextView) findViewById(R.id.kids_question);
				holder.kids_question.setText(havekids_array[jobj
						.getInt("kids")]);
			}
			if (!jobj.isNull("want_kid") && !jobj.getString("want_kid").equals("")) {
				holder.want_kids = (TextView) findViewById(R.id.want_kids);
				holder.want_kids.setText(wantKids_array[jobj
						.getInt("want_kid")]);
			}
			if (!jobj.isNull("about")) {
				holder.about_txt = (TextView) findViewById(R.id.about_txt);
				holder.about_txt.setText(jobj.getString("about"));
			}			



		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
