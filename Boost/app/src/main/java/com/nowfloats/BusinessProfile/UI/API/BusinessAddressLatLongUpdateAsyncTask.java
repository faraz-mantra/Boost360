package com.nowfloats.BusinessProfile.UI.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.nowfloats.BusinessProfile.UI.UI.Business_Address_Activity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BusinessAddressLatLongUpdateAsyncTask extends AsyncTask<Void,String, String>{

	JSONObject obj;
	ProgressDialog pd = null;
	static Activity appcontext = null;
	static Boolean success = false;
    String[] arr = new String[20];
    String FpAddress=null;
    String latlongaddress=null;
    double lat =0.0,lng =0.0;
    LatLng latlong;
    String fpTag ;
	
	public BusinessAddressLatLongUpdateAsyncTask(String address, String[] profilesattr,Activity context, String latlongAddress,String fpTag) {
		//values are coming from the bussiness address fragment
		this.appcontext = context;
		this.FpAddress = address;
		this.arr=profilesattr;
		this.latlongaddress=latlongAddress;
        this.fpTag = fpTag ;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd= ProgressDialog.show(appcontext, null, "Updating Your Address");
		pd.show();
	}
	
	@Override
	protected String doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		try {
            setLatLong(latlongaddress);
            if (lat == 0.0 && lng == 0.0) {
                setLatLong(latlongaddress.replace(Business_Address_Activity.text1.replaceAll(" ", "+") + ",", ""));
            }
        }catch(Exception e){e.printStackTrace();}
		
		String location = lat+","+lng;
		JSONObject obj = new JSONObject();
		JSONObject dataToBeUpdated = new JSONObject();
		try {

			obj.put("clientId", Constants.clientId);
			obj.put("fpTag", fpTag);
			JSONArray data = new JSONArray();

			JSONObject myObj2 = new JSONObject();
			myObj2.put("key", "GEOLOCATION");
			myObj2.put("value", location);
			data.put(myObj2);
			
			JSONObject myObj3 = new JSONObject();
			myObj3.put("key", "ADDRESS");                                      
			myObj3.put("value", FpAddress);
			data.put(myObj3);
			
			for(int i=0;i<arr.length;i++)
			{
					if (arr[i] == "PINCODE") {
						JSONObject myObj4 = new JSONObject();
						myObj4.put("key", "PINCODE");
						myObj4.put("value",Business_Address_Activity.pincodetext);
						data.put(myObj4);
					}
					if (arr[i] == "CITY") {						
						JSONObject myObj5 = new JSONObject();
						myObj5.put("key", "CITY");

						myObj5.put("value", Business_Address_Activity.citytext);
						data.put(myObj5);
					}			
			}
			
			obj.put("updates", data);	
			obj.put("updates", data);
			getDataFromServer(obj.toString(), Constants.HTTP_POST, Constants.FpsUpdate, Constants.BG_SERVICE_CONTENT_TYPE_JSON);
			
			
		} catch (Exception e) {

		}
		return null;
	}
	
	
	@Override
	protected void onPostExecute(String result) {
      String ch="";
      if(pd!=null)
			pd.dismiss();
		
      if(Util.isNetworkStatusAvialable(appcontext))
		{
    	  if(lat!=0.0 && lng != 0.0){
    	  
    	  
      try{

		for(int i=0;i<arr.length;i++)
		{
				if (arr[i] == "PINCODE") {Business_Address_Activity.areaCode.setText(Business_Address_Activity.pincodetext);
				}
				if (arr[i] == "CITY") {Business_Address_Activity.city.setText(Business_Address_Activity.citytext);
				}
		
		}
		for(int i=0;i<arr.length;i++)
		{
	    if(arr[i]=="ADDRESS" || arr[i]=="PINCODE" || arr[i]=="CITY"){
            Business_Address_Activity.saveTextView.setVisibility(View.GONE);
	    	break;	    	
	    }
	    
		}
		LatLng latlong = new LatLng(lat , lng);
          Business_Address_Activity.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));
		
		//BuzzContactInfoFragment.Savetext.setVisibility(View.GONE);
          Methods.showSnackBarPositive(appcontext,"Business Address Updated!");
		pd.dismiss();

		
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		}
		}
      else{
    	  //addressErrorDialog();
      }
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
	
	
	private void setLatLong(String fpAddress)
	{
		 int requestCode;
		 String  responseMessage;
		 
		 
		 HttpClient httpclient = new DefaultHttpClient();
		 HttpGet httpget = new HttpGet("http://maps.googleapis.com/maps/api/geocode/json?address="+fpAddress+"&sensor=false"); 
			try {
				httpget.addHeader("Content-Type", "application/json");
				HttpResponse response = httpclient.execute(httpget);
				requestCode = response.getStatusLine().getStatusCode();
				responseMessage = EntityUtils.toString(response.getEntity());

				if (requestCode == 200) {
					try {
						JSONObject store = new JSONObject(responseMessage);
						this.lng = ((JSONArray) store.get("results"))
								.getJSONObject(0).getJSONObject("geometry")
								.getJSONObject("location").getDouble("lng");

						this.lat = ((JSONArray) store.get("results"))
								.getJSONObject(0).getJSONObject("geometry")
								.getJSONObject("location").getDouble("lat");


                        Constants.latitude = this.lat;
                        Constants.longitude = this.lng;
						
					 if(lng!=0 && lat!=0){	
					 latlong = new LatLng(lng , lat);
					 this.latlong=latlong;
					 		 
					 }
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						
						e.printStackTrace();
					}
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}

	}

}
