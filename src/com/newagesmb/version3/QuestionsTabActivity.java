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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class QuestionsTabActivity extends Activity {

	public static String profile_id;
	public static String login_id;
	public static String profile_details;

	public static String PREF_NAME = "men_pref";
	public ProgressDialog progress;	
	public ImageLoader img_loader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questions_tab);
		
		String defValue = new String();
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		login_id = sp.getString("id", defValue);

		profile_id = getIntent().getStringExtra("profile_id");	
		profile_details=getIntent().getStringExtra("profile_details");		
		
		Log.v("Profile Details: Question Tab",profile_details);
		
		if(!profile_id.equals(login_id)){
			ImageButton ib=(ImageButton) findViewById(R.id.edit_profile);
			ib.setVisibility(View.INVISIBLE);
		}	
		
		/*Log.v("Details",profile_details);*/
		questionsListing(profile_details);
		
	findViewById(R.id.edit_profile).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//ProfileEditActivity.class
				Intent prof_intent = new Intent(QuestionsTabActivity.this,EditQuestionsActivity.class);
				prof_intent.putExtra("profile_id", profile_id);
				prof_intent.putExtra("profile_details", profile_details);
				QuestionsTabActivity.this.startActivity(prof_intent);
				
			}
		});
	}

	public class Holder {
		TextView name_header;
		ImageView prof_img, del_img, fav_img;
	}
	public void questionsListing(String details){
		try {
			JSONObject jobj = new JSONObject(details);
			final Holder holder = new Holder();
			
			if (!jobj.isNull("username")) {

				holder.name_header = (TextView) findViewById(R.id.prof_user_name);
				holder.name_header.setText(jobj.getString("username"));
			}
			
			
		}catch(JSONException je){
			je.printStackTrace();
		}
		UserQuestions uq=new UserQuestions();
		uq.execute(profile_id,login_id);
	}
	public class UserQuestions extends AsyncTask<String, Void, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			String pro_id=params[0];
			String usr_id=params[1];
			String result=POST(pro_id,usr_id);
			ArrayList< String> list=new ArrayList<String>();
			list=removeNullQuestion(result);
			return list;
		}
		public String POST(String pro_id,String usr_id){
			JSONObject job=new JSONObject();
			String result=null;
			String fn_name=null;
			try {
				
				HttpRequester hr=new HttpRequester();
				
				job.accumulate("user_id", pro_id);
				job.accumulate("login_id", usr_id);
				fn_name="get_user_question";
				
				String url=getString(R.string.json_url);
				result=hr.POST(fn_name, job, url);
				/*Log.v("Post Response",result);	*/
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = ProgressDialog.show(QuestionsTabActivity.this, "","Please wait...", true);
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			progress.dismiss();

			/*Log.v("On Post Execute",result);*/		
			/*
			 * Toast.makeText(getBaseContext(), "Recommendations:"+result,
			 * Toast.LENGTH_LONG).show();
			 */
			questionsList(result);
		}
		public void questionsList(ArrayList<String> list){
			ListView lv= (ListView) findViewById(R.id.list_view);
			final CustomAdapter adapter = new CustomAdapter(QuestionsTabActivity.this,R.layout.questions_list_self, list);
			lv.setAdapter(adapter);
			
		}
		public ArrayList<String> removeNullQuestion(String questions){
			JSONArray jobj;
			ArrayList< String> list=new ArrayList<String>();
			try {
				
				jobj = new JSONArray(questions);			
				JSONObject jobj_tmp;
				
				for(int i=0;i<jobj.length();i++){
					jobj_tmp=new JSONObject(jobj.getJSONObject(i).toString());
					if(login_id.equals(profile_id)){
							
						if(!jobj_tmp.isNull("login_answer")){
							
							list.add(jobj.getJSONObject(i).toString());
						}
							
					}else{
						
						if(!jobj_tmp.isNull("prof_anser")){
							
							list.add(jobj.getJSONObject(i).toString());
						}
							
					}
					
				}
			} catch (JSONException e) {
					
					e.printStackTrace();
			}
			 return list;
		}
		class CustomAdapter extends ArrayAdapter<String> {

			ArrayList<String> qlist;
			private final LayoutInflater inflater = null;
			Context mContext;
			int layoutResourceId;
			
			public CustomAdapter(Context context, int layoutResourceId,
					ArrayList<String> list) {
				super(context, layoutResourceId, list);
				qlist=list;
				//Log.v("Qlist",qlist.toString());
				mContext=context;
				this.layoutResourceId = layoutResourceId;
			}
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return qlist.size();
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
				TextView qstn, prof_ans,login_ans,his_answer,your_answer,him,you;
				LinearLayout ll;
			}
			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent){
				
				if (convertView == null) {
					// inflate the layout
						
					LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
					
					convertView = inflater.inflate(layoutResourceId, parent, false);
					

				}
				try {
					
					final Holder holder = new Holder();
					JSONObject jobj = new JSONObject(qlist.get(position).toString());
					holder.qstn = (TextView) convertView.findViewById(R.id.question_txt);					
					holder.prof_ans=(TextView) convertView.findViewById(R.id.answer_txt);
					holder.him=(TextView) convertView.findViewById(R.id.him);
					holder.you=(TextView) convertView.findViewById(R.id.you);
					holder.his_answer=(TextView) convertView.findViewById(R.id.his_answer);
					holder.your_answer=(TextView) convertView.findViewById(R.id.your_answer);
					holder.ll=(LinearLayout) convertView.findViewById(R.id.single_question_item);
					/*Log.v("Log_id and prof_id:",login_id+"-"+profile_id);*/
					holder.qstn.setText(jobj.getString("question"));
					if(login_id.equals(profile_id)){
						
						holder.him.setText(null);
						holder.you.setText(null);
						holder.his_answer.setText(null);
						holder.your_answer.setText(null);
						holder.prof_ans.setText(jobj.getString("login_answer"));
						
					}else{
						
						holder.prof_ans.setVisibility(View.INVISIBLE);
						if(!jobj.isNull("login_answer")){
							
							holder.him.setText("Him: ");
							holder.you.setText("You: ");
							holder.his_answer.setText(jobj.getString("prof_anser"));
							holder.your_answer.setText(jobj.getString("login_answer"));
							
						}else{
							holder.him.setText(null);
							holder.you.setText(null);
							holder.his_answer.setText(null);
							holder.your_answer.setText(null); 	

						}
						
					}
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return convertView;
			}
			
		}
		
	}
	

}
