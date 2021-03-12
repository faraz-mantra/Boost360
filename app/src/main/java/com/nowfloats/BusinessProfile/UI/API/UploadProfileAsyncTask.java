package com.nowfloats.BusinessProfile.UI.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadProfileAsyncTask extends AsyncTask<Void, String, String> {

	JSONObject obj;
	ProgressDialog pd = null;
    Activity appcontext = null;
    String[] arr = new String[20];
    String FpAddress=null;
    double lat,lng;
    LatLng latlong;
    UserSessionManager session ;
    
	public UploadProfileAsyncTask(Activity context, JSONObject obj, String[] profilesattr) {
		this.appcontext = context;
		this.obj = obj;
		this.arr= profilesattr;
        session = new UserSessionManager(context.getApplicationContext(),context);

	}

	public UploadProfileAsyncTask(Activity context, JSONObject obj, String[] profilesattr, String latlongAddress) {
		// TODO Auto-generated constructor stub

		this.appcontext = context;
		this.obj = obj;
		this.arr= profilesattr;
		this.FpAddress=latlongAddress;
	}

	@Override
	protected void onPreExecute() {
		if (appcontext != null) {
			pd = ProgressDialog.show(appcontext, null,"Saving your data...");
//			pd.setCancelable(false);		
//			pd = new ProgressDialog(appcontext,R.style.MyTheme);
//			pd.setCancelable(false);
//			pd.setMessage("Saving Your Data...");
//			pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//			pd.show();
		}
	}

	@Override
	protected void onPostExecute(String result) {
      	String ch="";

		if(pd !=null && pd.isShowing()) {
			pd.dismiss();
		}
		if (result == null){
			Methods.showSnackBarPositive(appcontext,appcontext.getString(R.string.something_went_wrong_try_again));
			return;
		}
		result = result.replace("[","");
		result = result.replace("]","");
		result = result.replace("\"","");
		String[] arr = result.split(",");
        try {
            for (int i = 0; i < arr.length; i++) {
            	if (TextUtils.isEmpty(arr[i]))
            	    continue;
            	switch (arr[i]){
					case "CONTACTNAME":


						break;
					case "NAME":


						break;
					case "DESCRIPTION":

//                    Constants.StoreDescription = Edit_Profile_Activity.msgtxt4buzzdescriptn;

						break;
					case "PRODUCTCATEGORYVERB":

						break;
					case "TIMINGS":
						break;
					case "CONTACTS":

						break;
					case "EMAIL":

						break;
					case "URL":

						break;
					case "FB":

						break;
					default:
						break;
				}
            }
            Methods.showSnackBarPositive(appcontext,appcontext.getString(R.string.changes_are_successfully_updated));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    appcontext.finish();
                    appcontext.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            },1000);

        }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }




	@Override
	protected String doInBackground(Void... arg0) {
		String response = "",content = ""; 	
		if(obj != null)
			content= obj.toString();

		response  = getDataFromServer(content,Constants.HTTP_POST,
                Constants.FpsUpdate,Constants.BG_SERVICE_CONTENT_TYPE_JSON);

		Log.d("CONTACT_INFORMATION", "URL: " + Constants.FpsUpdate);
		Log.d("CONTACT_INFORMATION", "DATA: " + content);

		// TODO Auto-generated method stub
		return response;
	}
	
	public static String getDataFromServer(String content,
			String requestMethod, String serverUrl, String contentType) {
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

			connection.setRequestProperty("Content-Type", contentType);

			outputStream = new DataOutputStream(connection.getOutputStream());

			byte[] BytesToBeSent = content.getBytes();
			if (BytesToBeSent != null) {
				outputStream.write(BytesToBeSent, 0, BytesToBeSent.length);
			}
			int responseCode = connection.getResponseCode();

			if (responseCode == 200 || responseCode == 202) {

			}

			InputStreamReader inputStreamReader = null;
			BufferedReader bufferedReader = null;
			try {
				inputStreamReader = new InputStreamReader(connection.getInputStream());
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
                e.printStackTrace();
			} finally {
				try {
					inputStreamReader.close();
				} catch (Exception e) {
                    e.printStackTrace();
				}
				try {
					bufferedReader.close();
				} catch (Exception e) {
                    e.printStackTrace();
				}

			}

		} catch (Exception ex) {
            ex.printStackTrace();

		} finally {
			try {
				outputStream.flush();
				outputStream.close();
			} catch (Exception e) {
                e.printStackTrace();
			}
		}

		return response;
	}


}
