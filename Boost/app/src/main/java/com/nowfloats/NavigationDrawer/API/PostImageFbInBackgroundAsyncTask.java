package com.nowfloats.NavigationDrawer.API;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.facebook.android.Facebook;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public final class PostImageFbInBackgroundAsyncTask extends AsyncTask<Void,String, String> {

	
	
	ProgressDialog pd 				= null;
	String Username 				= null, Password = null;
	private SharedPreferences pref 	= null;
	String responseMessage			= "";
	Boolean success 				= false;
	String clientIdConcatedWithQoutes = "\"" + Constants.clientId +"\"";
	int size = 0;
	SharedPreferences.Editor prefsEditor;
	String path = null;
	PostModel post = null;
	String dealId = null;
	PostModel postUser =null,postPage = null;

	String shareText = "";
	public PostImageFbInBackgroundAsyncTask(PostModel postUser,PostModel postPage,String id ) {
		this.post = post;
		this.postUser = postUser;
		this.postPage = postPage;
		dealId = id;
	}
	public PostImageFbInBackgroundAsyncTask( ) {									
	}
	@Override
	protected void onPreExecute() {
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		
			
	}

	@Override
	protected String doInBackground(Void... params) {
		String response = "";
		
		try {
			if(postUser!=null || postPage !=null)
			{
						
						String url = Constants.NOW_FLOATS_API_URL+"/Discover/v1/bizFloatForWeb/"+dealId+"?clientId="+ Constants.clientId;
						try{
							Thread.sleep(5000);
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
						}
						JSONObject dealObj = Util.readJsonFromUrl(url);
						JSONObject targetFloat = dealObj.getJSONObject("targetFloat");
						String imgUrl = targetFloat.getString("ActualImageUri");
						if(!Util.isNullOrEmpty(imgUrl))
						{
							String storeUri = targetFloat.getString("DealUri");
							storeUri = storeUri.toLowerCase();
							String mesgUrl = null;
							if(!storeUri.equals("http://www.nowfloats.com/"))
							{
								mesgUrl = storeUri+"/bizFloat/"+dealId;
								mesgUrl = Util.shortUrl(mesgUrl);
							}
							
							imgUrl = "https://api.withfloats.com"+imgUrl;
							Facebook facebook=null;
							facebook=new Facebook(Constants.FACEBOOK_API_KEY);
							facebook.setAccessToken(Constants.FACEBOOK_USER_ACCESS_ID);
							if(postUser!=null && postPage == null)
								Post.statusUpdate(postUser,facebook,imgUrl,mesgUrl);
							
						//	if(postPage!=null && postUser == null)
								Post.statusUpdate(postPage,facebook,imgUrl,mesgUrl);
							
						//	if(postPage!=null  postUser !=null)
						//		{
                                    Post.forFBandPagestatusUpdate(postPage,facebook,imgUrl,mesgUrl);
								try {
									Thread.sleep(5000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Facebook fb=null;
								fb=new Facebook(Constants.FACEBOOK_API_KEY);
								fb.setAccessToken(Constants.FACEBOOK_USER_ACCESS_ID);
								Post.forFBandPagestatusUpdate(postUser,fb,imgUrl,mesgUrl);
						//		}
								
							
							
						
						}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if(post != null){
//			Facebook facebook=null;
//			facebook=new Facebook(Constants.FACEBOOK_API_KEY);
//			facebook.setAccessToken(Constants.FACEBOOK_USER_ACCESS_ID);
//			Post.statusUpdate(post,facebook);
//		}
		return response ;
	}
	
	public static String getDataFromServer(String content,
			String requestMethod, String serverUrl, String contentType) {
		String response = "", responseMessage = "";
		Boolean success = false;
		DataOutputStream outputStream = null;
		try {

			URL new_url = new URL(serverUrl);
			HttpURLConnection connection = (HttpURLConnection) new_url
					.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(true);
			// Enable PUT method
			connection.setRequestMethod(requestMethod);
			connection.setRequestProperty("Connection", "Keep-Alive");

			connection.setRequestProperty("Content-Type", contentType);
			 connection.setRequestProperty("Content-Length", "" + Integer.toString(content.getBytes().length));
		      connection.setRequestProperty("Content-Language", "en-US");  
			outputStream = new DataOutputStream(connection.getOutputStream());

			byte[] BytesToBeSent = content.getBytes();
			if (BytesToBeSent != null) {
				outputStream.write(BytesToBeSent, 0, BytesToBeSent.length);
			}
			int responseCode = connection.getResponseCode();

			responseMessage = connection.getResponseMessage();

			if (responseCode == 200 || responseCode == 202) {
				success = true;

			}

			InputStreamReader inputStreamReader = null;
			BufferedReader bufferedReader = null;
			try {
				inputStreamReader = new InputStreamReader(
						connection.getInputStream());
				bufferedReader = new BufferedReader(inputStreamReader);

				StringBuilder responseContent = new StringBuilder();

				String temp = null;

				boolean isFirst = true;

				while ((temp = bufferedReader.readLine()) != null) {
					if (!isFirst)
						responseContent.append(Constants.NEW_LINE);
					responseContent.append(temp);
					isFirst = false;
				}

				response = responseContent.toString();

			} catch (Exception e) {
			} finally {
				try {
					inputStreamReader.close();
				} catch (Exception e) {
				}
				try {
					bufferedReader.close();
				} catch (Exception e) {
				}

			}

		} catch (Exception ex) {
			success = false;
		} finally {
			try {
				outputStream.flush();
				outputStream.close();
			} catch (Exception e) {
			}
		}

		return response;
	}
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }

	  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }
	
}
