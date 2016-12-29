package com.nowfloats.BusinessProfile.UI.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.nowfloats.BusinessProfile.UI.UI.Contact_Info_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.SidePanelFragment;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadProfileAsyncTask extends AsyncTask<Void, String, String> {

	JSONObject obj;
	ProgressDialog pd = null;
	static Activity appcontext = null;
	static Boolean success = false;
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
        pd.dismiss();
        try {
            for (int i = 0; i < 3; i++) {
                if (arr[i] == "CONTACTNAME") {
                    Constants.ContactName = Edit_Profile_Activity.msgtxt4_name;
                    Edit_Profile_Activity.yourname.setText(Constants.ContactName);
                }
                if (arr[i] == "NAME") {
                    Constants.StoreName = Edit_Profile_Activity.msgtxt4buzzname;
                    session.storeFPName(Constants.StoreName);
                    Edit_Profile_Activity.buzzname.setText(Constants.StoreName);
                    SidePanelFragment.storeName.setText(Constants.StoreName);
                }
                if (arr[i] == "DESCRIPTION") {
                    session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION,""+Edit_Profile_Activity.msgtxt4buzzdescriptn);
//                    Constants.StoreDescription = Edit_Profile_Activity.msgtxt4buzzdescriptn;
                    Edit_Profile_Activity.buzzdescription.setText(Constants.StoreDescription);
                }

                if (arr[i] == "TIMINGS") {
                    Constants.mondayStartTime = session.getStartTime();
//                    Constants.StoreDescription = Edit_Profile_Activity.msgtxt4buzzdescriptn;
                    //Edit_Profile_Activity.buzzdescription.setText(Constants.StoreDescription);
                }

                if (arr[i] == "CONTACTS") {
                    session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER,Contact_Info_Activity.primary);
//                    Constants.StoreContact[0] = Contact_Info_Activity.primary;
                    if (!Util.isNullOrEmpty(Contact_Info_Activity.alternate1))
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1,Contact_Info_Activity.alternate1);
//                        Constants.StoreContact[1] = Contact_Info_Activity.alternate1;
                    if (!Util.isNullOrEmpty(Contact_Info_Activity.alternate2)) {
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_2,Contact_Info_Activity.alternate2);
                        if (Util.isNullOrEmpty(Contact_Info_Activity.alternate1)) {
                            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1,Contact_Info_Activity.alternate2);
//                            Constants.StoreContact[1] = Contact_Info_Activity.alternate2;
                        } else {
                            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_2,Contact_Info_Activity.alternate2);
//                            Constants.StoreContact[2] = Contact_Info_Activity.alternate2;
                        }
                    }
                }

                if (arr[i] == "EMAIL") {
                    Constants.StoreEmail = Contact_Info_Activity.msgtxt4_email;
                    Contact_Info_Activity.emailAddress.setText(Contact_Info_Activity.msgtxt4_email);
                }
                if (arr[i] == "URL") {
                    Constants.StoreWebSite = Contact_Info_Activity.msgtxt4website;
                    Contact_Info_Activity.websiteAddress.setText(Contact_Info_Activity.msgtxt4website);
                }
                if (arr[i] == "FB") {
                    Contact_Info_Activity.facebookPage.setText(Contact_Info_Activity.msgtxt4fbpage);
                }
            }
            Methods.showSnackBarPositive(appcontext,"Changes are successfully updated");
			appcontext.finish();
			appcontext.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //Site Meter
            for(int i=0;i<arr.length;i++)
            {
                if(arr[i]=="CONTACTNAME" || arr[i]=="NAME" || arr[i]=="DESCRIPTION")
                {
                    Edit_Profile_Activity.saveTextView.setVisibility(View.GONE);
                    break;
                }

                if(arr[i]=="CONTACTS" || arr[i]=="EMAIL" || arr[i]=="URL"|| arr[i]=="FB")
                {
                    Contact_Info_Activity.saveTextView.setVisibility(View.GONE);
                    break;
                }
            }
        }




	@Override
	protected String doInBackground(Void... arg0) {
		String response = "",content = ""; 	
		if(obj != null)
			content= obj.toString();
		
		//setLatLong(FpAddress);
		String location = lat+","+lng;
		JSONObject myObj2 = new JSONObject();
		try{
			myObj2.put("key", "GEOLOCATION");
			myObj2.put("value", location);	
			
			//obj.put(myObj2)
		}
		catch (Exception e) {
		}
		

		response  = getDataFromServer(content,Constants.HTTP_POST,
                Constants.FpsUpdate,Constants.BG_SERVICE_CONTENT_TYPE_JSON);
		
		  
		
		// TODO Auto-generated method stub
		return response;
	}
	
	public static String getDataFromServer(String content,
			String requestMethod, String serverUrl, String contentType) {
		String response = "", responseMessage = "";
		
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

			responseMessage = connection.getResponseMessage();

			if (responseCode == 200 || responseCode == 202) {
				success = true;
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
