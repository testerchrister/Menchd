package com.newagesmb.version3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.entity.mime.*;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpRequester {
	public String POST(String function_name, JSONObject jsonObject, String url) {
		InputStream inputStream = null;
		String result = "";
		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			

			// 3. build jsonObject
				/* Received Object as parameter*/
				
			JSONObject jsonObjParent = new JSONObject();
			jsonObjParent.accumulate("function", function_name);
			jsonObjParent.accumulate("parameters", jsonObject);

			// 4. convert JSONObject to JSON to String
			String json = "";
			json = jsonObjParent.toString();
			/*Log.v("Post Json",json);	*/
			// ** Alternative way to convert Person object to JSON string
			// usin Jackson Lib
			// ObjectMapper mapper = new ObjectMapper();
			// json = mapper.writeValueAsString(person);

			// 5. set json to StringEntity
			StringEntity se = new StringEntity(json);

			// 6. set httpPost Entity
			httpPost.setEntity(se);

			// 7. Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// 9. receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// 10. convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		// 11. return result

		return result;
	}
	private String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		
		return result;

	}
	public String GET(String url){
		BufferedReader in = null;
		String line="";
		String result="";
		  try{
	           HttpClient httpclient = new DefaultHttpClient();

	           HttpGet request = new HttpGet();
	           URI website = new URI(url);
	           request.setURI(website);
	           HttpResponse response = httpclient.execute(request);
	           in = new BufferedReader(new InputStreamReader(
	                   response.getEntity().getContent()));

	          while((line = in.readLine())!=null){
	        	  result += line;
	          }
	       }catch(Exception e){
	           Log.e("log_tag", "Error in http connection "+e.toString());
	       }
		return result;
	}
	
	 @SuppressWarnings("deprecation")
	public String sendMultiPartPost(String url, String imagePath,JSONObject jsonObject,ByteArrayBody prof_file) throws IOException, ClientProtocolException  {
		 String username=null,password=null,email=null,city=null,dob=null,lat=null,lng=null;
		 String result="Failed to POST";
		 try {
			 username=jsonObject.getString("name");
			 password=jsonObject.getString("password");
			 email=jsonObject.getString("email");
			 city=jsonObject.getString("city");
			 dob=jsonObject.getString("dob");
			 lat=jsonObject.getString("lat");
			 lng=jsonObject.getString("lng");
			 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		 
		// result=jsonObject.toString();
		 /*Log.v("Post Values",username+":::"+password+":::"+email+":::"+city+":::"+dob+":::");*/
		 try{
			 InputStream inputStream = null;
			 HttpClient httpclient = new DefaultHttpClient();
	         httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	         
	         HttpPost httppost = new HttpPost(url);
	         /*httppost.setHeader("Content-type", "multipart/form-data");*/
	             
	         MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	         
	        
	        /* ContentBody cbFile = new FileBody(prof_file, "image/jpeg");*/
	         
	         mpEntity.addPart("image",prof_file);
	         mpEntity.addPart("username",new StringBody(username));
	         mpEntity.addPart("password",new StringBody(password));
	         mpEntity.addPart("email",new StringBody(email));
	         mpEntity.addPart("city",new StringBody(city)); 
	         mpEntity.addPart("lat",new StringBody(lat));
	         mpEntity.addPart("long",new StringBody(lng));
	         mpEntity.addPart("dob",new StringBody(dob));
	         
	         httppost.setEntity(mpEntity);
	         HttpResponse response = httpclient.execute(httppost);
	         HttpEntity resEntity = response.getEntity();

	         inputStream=resEntity.getContent();
	        
	         if (inputStream != null)
					result = convertInputStreamToString(inputStream);
				else
					result = "Did not work!";
	        
		 }catch(Exception e){
			 Log.d("InputStream", e.getLocalizedMessage());
		 }
		 
         return result;
	 }	
	 
	 @SuppressWarnings("deprecation")
		public String updateUserBasic(String url,JSONObject jsonObject,ByteArrayBody prof_file) throws IOException, ClientProtocolException  {
			 String username=null,city=null,dob=null,lat=null,lng=null,about=null,user_id=null;
			 String result="Failed to POST";
			 try {
				 username=jsonObject.getString("name");
				 city=jsonObject.getString("city");
				 dob=jsonObject.getString("dob");
				 lat=jsonObject.getString("lat");
				 lng=jsonObject.getString("lng");
				 about=jsonObject.getString("about");
				 user_id=jsonObject.getString("user_id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			 
			/*result="Post Values"+username+":::"+lat+":::"+lng+":::"+city+":::"+dob+":::";*/
			 
			 try{
				 InputStream inputStream = null;
				 HttpClient httpclient = new DefaultHttpClient();
		         httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		         
		         HttpPost httppost = new HttpPost(url);
		             
		         MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		         
		         mpEntity.addPart("image",prof_file);
		         mpEntity.addPart("username",new StringBody(username));
		         mpEntity.addPart("about",new StringBody(about));
		         mpEntity.addPart("city",new StringBody(city)); 
		         mpEntity.addPart("lat",new StringBody(lat));
		         mpEntity.addPart("long",new StringBody(lng));
		         mpEntity.addPart("dob",new StringBody(dob));
		         mpEntity.addPart("user_id",new StringBody(user_id));
		         
		         httppost.setEntity(mpEntity);
		         HttpResponse response = httpclient.execute(httppost);
		         HttpEntity resEntity = response.getEntity();

		         inputStream=resEntity.getContent();
		        
		         if (inputStream != null)
						result = convertInputStreamToString(inputStream);
					else
						result = "Did not work!";
		        
			 }catch(Exception e){
				 Log.d("InputStream", e.getLocalizedMessage());
			 }
			 
	         return result;
		 }	
	 
	 @SuppressWarnings("deprecation")
		public String updatePhotoGallery(String url,JSONObject jsonObject,ByteArrayBody other_image) throws IOException, ClientProtocolException  {
			 String user_id=null,caption=null,category_id=null;
			 String result="Failed to POST";
			
			 try {
				 user_id=jsonObject.getString("user_id");
				 caption=jsonObject.getString("caption");
				 category_id=jsonObject.getString("category_id");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			 
			/*result="Post Values"+username+":::"+lat+":::"+lng+":::"+city+":::"+dob+":::";*/
			 
			 try{
				 InputStream inputStream = null;
				 HttpClient httpclient = new DefaultHttpClient();
		         httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		         
		         HttpPost httppost = new HttpPost(url);
		             
		         MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		         
		         mpEntity.addPart("other_image",other_image);
		         mpEntity.addPart("category_id",new StringBody(category_id));
		         mpEntity.addPart("caption",new StringBody(caption));
		         mpEntity.addPart("user_id",new StringBody(user_id));
		         
		         httppost.setEntity(mpEntity);
		         HttpResponse response = httpclient.execute(httppost);
		         HttpEntity resEntity = response.getEntity();

		         inputStream=resEntity.getContent();
		        
		         if (inputStream != null)
						result = convertInputStreamToString(inputStream);
					else
						result = "Did not work!";
		        
			 }catch(Exception e){
				 Log.d("InputStream", e.getLocalizedMessage());
			 }
			 
	         return result;
		 }	
	 @SuppressWarnings("deprecation")
		public String sendMessage(String url,JSONObject jsonObject) throws IOException, ClientProtocolException  {
			 String sender_id=null,receiver_id=null,message=null,lat=null,lng=null,location=null;
			 String result="Failed to POST";
			 Log.v("HttpPost Location JSON",jsonObject.toString());
			 try {
				 sender_id=jsonObject.getString("sender_id");
				 receiver_id=jsonObject.getString("receiver_id");
				 message=jsonObject.getString("message");
				 lat=jsonObject.getString("lat");
				 lng=jsonObject.getString("lng");
				 location=jsonObject.getString("location");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			 
			/*result="Post Values"+username+":::"+lat+":::"+lng+":::"+city+":::"+dob+":::";*/
			 
			 try{
				 InputStream inputStream = null;
				 HttpClient httpclient = new DefaultHttpClient();
		         httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		         
		         HttpPost httppost = new HttpPost(url);
		             
		         MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		         mpEntity.addPart("sender_id",new StringBody(sender_id));
		         mpEntity.addPart("receiver_id",new StringBody(receiver_id));
		         mpEntity.addPart("message",new StringBody(message));
		         mpEntity.addPart("lat",new StringBody(lat));
		         mpEntity.addPart("lng",new StringBody(lng));
		         mpEntity.addPart("location",new StringBody(location));
		         
		         httppost.setEntity(mpEntity);
		         HttpResponse response = httpclient.execute(httppost);
		         HttpEntity resEntity = response.getEntity();

		         inputStream=resEntity.getContent();
		        
		         if (inputStream != null)
						result = convertInputStreamToString(inputStream);
					else
						result = "Did not work!";
		        
			 }catch(Exception e){
				 Log.d("InputStream", e.getLocalizedMessage());
			 }
			 
	         return result;
		 }	
	 
	 @SuppressWarnings("deprecation")
		public String sendImageMessage(String url,JSONObject jsonObject,ByteArrayBody other_image) throws IOException, ClientProtocolException  {
			 String sender_id=null,receiver_id=null,message=null,lat=null,lng=null,location=null;
			 String result="Failed to POST";
			 //Log.v("HttpPost JSON",jsonObject.toString());
			 try {
				 sender_id=jsonObject.getString("sender_id");
				 receiver_id=jsonObject.getString("receiver_id");
				 message=jsonObject.getString("message");
				 lat=jsonObject.getString("lat");
				 lng=jsonObject.getString("lng");
				 location=jsonObject.getString("location");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			 
			/*result="Post Values"+username+":::"+lat+":::"+lng+":::"+city+":::"+dob+":::";*/
			 
			 try{
				 InputStream inputStream = null;
				 HttpClient httpclient = new DefaultHttpClient();
		         httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		         
		         HttpPost httppost = new HttpPost(url);
		             
		         MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		         mpEntity.addPart("chat_image",other_image);	
		         mpEntity.addPart("sender_id",new StringBody(sender_id));
		         mpEntity.addPart("receiver_id",new StringBody(receiver_id));
		         mpEntity.addPart("message",new StringBody(message));
		         mpEntity.addPart("lat",new StringBody(lat));
		         mpEntity.addPart("lng",new StringBody(lng));
		         mpEntity.addPart("location",new StringBody(location));
		         
		         httppost.setEntity(mpEntity);
		         HttpResponse response = httpclient.execute(httppost);
		         HttpEntity resEntity = response.getEntity();

		         inputStream=resEntity.getContent();
		        
		         if (inputStream != null)
						result = convertInputStreamToString(inputStream);
					else
						result = "Did not work!";
		        
			 }catch(Exception e){
				 Log.d("InputStream", e.getLocalizedMessage());
			 }
			 
	         return result;
		 }
	 
	 @SuppressWarnings("deprecation")
	public String uploadVideo(String url,String user_id,ByteArrayBody video_clip){
		 String result="";
		 try{
			 InputStream inputStream = null;
			 HttpClient httpclient = new DefaultHttpClient();
	         httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	         
	         HttpPost httppost = new HttpPost(url);
	             
	         MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	         mpEntity.addPart("profile_video",video_clip);
	         mpEntity.addPart("user_id",new StringBody(user_id));
	         
	         httppost.setEntity(mpEntity);
	         HttpResponse response = httpclient.execute(httppost);
	         HttpEntity resEntity = response.getEntity();

	         inputStream=resEntity.getContent();
	        
	         if (inputStream != null)
					result = convertInputStreamToString(inputStream);
	         else
					result = "Did not work!";
	         
		 }catch (Exception e) {
			 Log.d("InputStream exception", e.getLocalizedMessage());
		}
		 
		 return result;
	 }
}
