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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class EditQuestionsActivity extends Activity {

	public static String profile_id;
	public static String login_id;
	public static String profile_details;

	public static String PREF_NAME = "men_pref";
	public ProgressDialog progress;	
	public ImageLoader img_loader;
	Button ask_question_btn;
	ListView list_view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_questions);
		String defValue = new String();
		SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
		login_id = sp.getString("id", defValue);
		profile_id = getIntent().getStringExtra("profile_id");		
		profile_details=getIntent().getStringExtra("profile_details");		
			
		ask_question_btn=(Button) findViewById(R.id.ask_question_btn);
		list_view=(ListView) findViewById(R.id.list_view);
		/*Log.v("Details",profile_details);*/
		questionsListing(profile_details);
		
		findViewById(R.id.ask_question_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(EditQuestionsActivity.this, AskQuestion.class);
				EditQuestionsActivity.this.startActivity(intent);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.questions_tab, menu);
		return true;
	}
	public class Holder {
		TextView name_header;
		ImageView prof_img, del_img, fav_img;
	}
	public void questionsListing(String details){
		
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
			
			progress = ProgressDialog.show(EditQuestionsActivity.this, "","Please wait...", true);
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			progress.dismiss();
			
			questionsList(result);
		}
		public void questionsList(ArrayList<String> list){
			ListView lv= (ListView) findViewById(R.id.list_view);
			
			if(list.size()==0){
				ask_question_btn.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
			}else{
				ask_question_btn.setVisibility(View.GONE);	
				
				final CustomAdapter adapter = new CustomAdapter(EditQuestionsActivity.this,R.layout.questions_list_self, list);
				lv.setAdapter(adapter);
			}
			
			
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
					holder.prof_ans.setVisibility(View.INVISIBLE);
					holder.him.setText("Him: ");
					holder.you.setText("You: ");
					holder.his_answer.setText(jobj.getString("prof_anser"));
					holder.your_answer.setText(jobj.getString("login_answer"));
					holder.ll.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent askIntent=new Intent(EditQuestionsActivity.this,AskQuestion.class);
							askIntent.putExtra("from_activity", "edit_question");
							askIntent.putExtra("profile_id", profile_id);
							askIntent.putExtra("profile_details", profile_details);
							EditQuestionsActivity.this.startActivity(askIntent);
						}
					});
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return convertView;
			}
			
		}
		
	}
	

}
