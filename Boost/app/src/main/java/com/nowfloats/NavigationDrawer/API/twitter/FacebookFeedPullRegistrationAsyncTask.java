package com.nowfloats.NavigationDrawer.API.twitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FacebookFeedPullRegistrationAsyncTask extends AsyncTask<Void, String, String>{

	private SharedPreferences pref = null;
	SharedPreferences.Editor prefsEditor;
	private Activity appContext = null;
	ProgressDialog pd = null;
	Boolean success = false;
	String response = null;
	String responseMessage = null;
	JSONObject obj;
	TextView fromPage;
	CheckBox checkBox;
	UserSessionManager sessionManager;
	String count;
	int responseCode;
	ImageView ivFbPageAutoPull;


	public FacebookFeedPullRegistrationAsyncTask(Activity context, JSONObject obj, TextView FromPage, ImageView ivFbPageAutoPull, CheckBox Checkbox, UserSessionManager sessionManager) {
		this.appContext = context;
		this.obj = obj;
		this.fromPage = FromPage;
		this.checkBox = Checkbox;
		this.sessionManager = sessionManager;
		this.ivFbPageAutoPull = ivFbPageAutoPull;
		//this.count = count;
	}

	@Override
	protected void onPreExecute() {

		pd = ProgressDialog.show(appContext, null, "Please wait");
		pd.setCancelable(true);
		pd.show();
		pref = appContext.getSharedPreferences(Constants.PREF_NAME,Activity.MODE_PRIVATE);
		prefsEditor = pref.edit();
	}

	@Override
	protected void onPostExecute(String result) {

		if(pd!=null){
			pd.dismiss();
		}
		if(responseCode==200 || responseCode==202){
			//Util.toast("success", appContext);
			try {
				sessionManager.storeFPDetails(Key_Preferences.FB_PULL_ENABLED, "true");
				sessionManager.storeFPDetails(Key_Preferences.FB_PULL_PAGE_NAME, obj.getString("FacebookPageName"));
				sessionManager.storeFPDetails(Key_Preferences.FB_PULL_COUNT, String.valueOf(obj.getInt("Count")));
				fromPage.setVisibility(View.VISIBLE);
				fromPage.setText(obj.getString("FacebookPageName"));
				ivFbPageAutoPull.setImageDrawable(appContext.getResources().getDrawable(R.drawable.facebook_page));
			}catch (JSONException e){
				e.printStackTrace();
				Util.toast("Uh oh. Something went wrong. Please try again", appContext);
				checkBox.setChecked(false);
			}
		}
		else{
			BoostLog.d("Object:",  obj.toString());
			Util.toast("Uh oh. Something went wrong. Please try again", appContext);
			checkBox.setChecked(false);
		}
	}

	@Override
	protected String doInBackground(Void... params) {
		String response = "";

		try {
			String content = obj.toString();
			response = getDataFromServer(content, Constants.HTTP_POST, Constants.NOW_FLOATS_API_URL+"/Discover/v1/FloatingPoint/AutoPublishMessages");
			if (!Util.isNullOrEmpty(response)) {



			}// end of if condition

			// end of
		} catch (Exception e) {

		}
		return response;
	}

	public String getDataFromServer(String content, String requestMethod,
									String serverUrl) {
		String response = "";
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
			connection.setRequestProperty("Content-Type",
					Constants.BG_SERVICE_CONTENT_TYPE_JSON);
			outputStream = new DataOutputStream(connection.getOutputStream());

			byte[] BytesToBeSent = content.getBytes();

			if (BytesToBeSent != null) {
				outputStream.write(BytesToBeSent, 0, BytesToBeSent.length);
			}
			responseCode = connection.getResponseCode();

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
