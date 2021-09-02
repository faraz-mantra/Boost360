package com.nowfloats.BusinessProfile.UI.API;


//Uncomment all the lines to the changes the location from the map for and using the locatingbusinessaddress() 

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class BusinessAddressUpdateAsyncTask extends AsyncTask<Void, String, String> {

    Address currentAddress;
    String buzzAddress = null;
    double latitude = -1, longitude = -1;
    ProgressDialog pd = null;
    Activity activity = null;
    String responseMessage = "";
    String response = "";
    Boolean success = false;
    String address;
    Boolean mapupdateflag = false;
    SharedPreferences sharedpreferences;
    String fpTag;
    int responseCode;
    private Activity appContext = null;

    public BusinessAddressUpdateAsyncTask(double latitude, double longitude, Activity context, String address) {
        this.appContext = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }


    public BusinessAddressUpdateAsyncTask(double latitude,
                                          double longitude,
                                          Activity context,
                                          String address, Boolean flag, String fpTag) {
        //values are coming from the bussiness address fragment
        this.appContext = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.mapupdateflag = flag;
        this.fpTag = fpTag;

    }


    @Override
    protected String doInBackground(Void... params) {

        //locatingBusinessAddress();

        String location = latitude + "," + longitude;
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


            if (!mapupdateflag) {
                JSONObject myObj3 = new JSONObject();
                myObj3.put("key", "ADDRESS");
                myObj3.put("value", address);
                data.put(myObj3);
            }


//			if (mapupdateflag) {
//
//				JSONObject myObj4 = new JSONObject();
//				myObj4.put("key", "CITY");
//
//				myObj4.put("value", (currentAddress.getLocality() == null ? ""
//						: currentAddress.getLocality()));
//				data.put(myObj4);
//
//				JSONObject myObj5 = new JSONObject();
//				myObj5.put("key", "PINCODE");
//				myObj5.put("value",
//						(currentAddress.getPostalCode() == null ? ""
//								: currentAddress.getPostalCode()));
//				data.put(myObj5);
//
//				JSONObject myObj6 = new JSONObject();
//				myObj6.put("key", "COUNTRY");
//
//				myObj6.put("value",
//						(currentAddress.getCountryName() == null ? ""
//								: currentAddress.getCountryName()));
//				data.put(myObj6);
//			}
//

            obj.put("updates", data);

            dataToBeUpdated.put("GEOLOCATION", location);
            if (!mapupdateflag)
                dataToBeUpdated.put("ADDRESS", address);

            getDataFromServer(obj.toString(), Constants.HTTP_POST, Constants.FpsUpdate);


        } catch (Exception e) {

        }

//			try{
//				getDataFromServer(obj.toString(), Constants.HTTP_POST, Constants.FpsUpdate);
//				} catch (Exception e) {
//
//					e.printStackTrace();
//				}
        return null;
    }


    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);
        if (mapupdateflag) {
            try {
                Thread.sleep(1000);
                pd.dismiss();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            pd.dismiss();
        }

        if (responseCode == 200 || responseCode == 202) {
            if (!mapupdateflag)
                Constants.StoreAddress = address;

            //Constants.latitude = latitude;
            //Constants.longitude = longitude;


            LatLng latlong = new LatLng(latitude, longitude);

            //Business_Address_Activity.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));

            Toast.makeText(appContext, R.string.your_business_has_been_updated_successfully, Toast.LENGTH_LONG);
        } else {
            NewMapViewDialogBusinessAddress.updatingPostionFromMap = false;
            //addressErrorDialog();
        }


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = ProgressDialog.show(appContext, null, appContext.getString(R.string.updating_your_address));
        pd.show();
    }

    private void locatingBusinessAddress() {

        try {
            List<Address> locations = null;
            Address address = null;

            if (latitude != -1 && longitude != -1) {

                Geocoder geocoder = new Geocoder(appContext);
                Boolean geocoderIsPresent = Geocoder.isPresent();

                if (geocoderIsPresent && geocoder != null) {
                    try {
                        locations = geocoder.getFromLocation(latitude,
                                longitude, 1);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    //	Geocoder is not available or supported
                }

                if (locations != null) {
                    currentAddress = locations.get(0);

                    String buzzAddress = (currentAddress.getThoroughfare() == null ? "" : currentAddress.getThoroughfare()) + "," +
                            (currentAddress.getLocality() == null ? "" : currentAddress.getLocality()) + "," +
                            (currentAddress.getAdminArea() == null ? "" : currentAddress.getAdminArea()) + "," +
                            (currentAddress.getCountryName() == null ? "" : currentAddress.getCountryName()) + "," +
                            (currentAddress.getPostalCode() == null ? "" : currentAddress.getPostalCode());
                    this.buzzAddress = buzzAddress;
                }
            } else {
                // Last Known Location not available
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDataFromServer(String content, String requestMethod, String serverUrl) {
        String response = "";
        DataOutputStream outputStream = null;
        try {

            //Thread.sleep(2000);
            NewMapViewDialogBusinessAddress.updatingPostionFromMap = false;
            URL new_url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) new_url
                    .openConnection();

            // Allow Inputs & Outputs
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            // Enable PUT method
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Authorization", Utils.getAuthToken());
            connection.setRequestProperty("Content-Type",
                    Constants.BG_SERVICE_CONTENT_TYPE_JSON);
            outputStream = new DataOutputStream(connection.getOutputStream());

            byte[] BytesToBeSent = content.getBytes();
            if (BytesToBeSent != null) {
                outputStream.write(BytesToBeSent, 0, BytesToBeSent.length);
            }
            responseCode = connection.getResponseCode();

            responseMessage = connection.getResponseMessage();
            BoostLog.d("response Code", responseCode + "");
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
                BoostLog.d("response", response);

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

        //return response;
    }


}
