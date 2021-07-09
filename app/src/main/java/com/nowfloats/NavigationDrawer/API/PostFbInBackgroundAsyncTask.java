package com.nowfloats.NavigationDrawer.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.nowfloats.util.Constants;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public final class PostFbInBackgroundAsyncTask extends AsyncTask<Void,String, String> {

	
	private Activity appContext 	= null;
	ProgressDialog pd 				= null;
	String Username 				= null, Password = null;
	private SharedPreferences pref 	= null;
	String responseMessage			= "";
	Boolean success 				= false;
	String clientIdConcatedWithQoutes = "\"" + Constants.clientId +"\"";
	int size = 0;
	SharedPreferences.Editor prefsEditor;
	String mesgUrl = null;
	
	String shareText = "";
	public PostFbInBackgroundAsyncTask(Activity context ,String shareText,String url ) {									
		this.appContext = context;
		this.shareText  = shareText;
		this.mesgUrl = url;
	}
	public PostFbInBackgroundAsyncTask(Activity context ) {									
		this.appContext = context;
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
		 String data = "";
		 mesgUrl = Util.shortUrl(mesgUrl);
		 shareText = shareText+System.getProperty("line.separator")+mesgUrl;
		 if(Constants.fbShareEnabled){
			 try {
				 data = "access_token=" + URLEncoder.encode(Constants.FACEBOOK_USER_ACCESS_ID, "UTF-8") +	"&message=" + URLEncoder.encode(shareText, "UTF-8");
			 } catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			 }
			 response = getDataFromServer(data, Constants.HTTP_POST, "https://graph.facebook.com/"+ Constants.FACEBOOK_USER_ID+"/feed", "application/x-www-form-urlencoded");
		 }
		if (Constants.fbPageShareEnabled) {
			int size = 0;
			if (Constants.FbPageList != null)
				size = Constants.FbPageList.length();
			if (size > 0) {
				for (int i = 0; i < size ; i++) {
					String key = "", id = "";
					try {
						
						try {
							key = (String) ((JSONObject) Constants.FbPageList
									.get(i)).get("access_token");
							id = (String) ((JSONObject) Constants.FbPageList
									.get(i)).get("id");
						} catch (JSONException e) {

						}
						data = "access_token="
								+ URLEncoder.encode(key, "UTF-8") + "&message="
								+ URLEncoder.encode(shareText, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (!Util.isNullOrEmpty(key) && !Util.isNullOrEmpty(id)) {
						response = getDataFromServer(data, Constants.HTTP_POST,
								"https://graph.facebook.com/" + id + "/feed",
								"application/x-www-form-urlencoded");
					}
				}
			}
	}
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
	
	
}
