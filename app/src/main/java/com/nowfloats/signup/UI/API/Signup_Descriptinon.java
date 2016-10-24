package com.nowfloats.signup.UI.API;

import android.os.AsyncTask;
import android.util.Log;

import com.nowfloats.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by NowFloatsDev on 12/06/2015.
 */
public class Signup_Descriptinon extends AsyncTask<Void, String, String> {

    static Boolean success = false;
//    {
//        "clientId":"AC16E0892F2F45388F439BDE9F6F3FB5C31F0FAA628D40CD9814A79D8841397E",
//            "fpTag":"DEVTEST",
//            "updates":[
//
//        {"key":"DESCRIPTION","value":"Testing Page"}
//
//        ]
//    }
    String fpTag, description,facebookID;
    ArrayList<String> widgetsList ;

    public Signup_Descriptinon(String fpTag,String description,String facebookID,ArrayList<String> fpWidgetList)
    {
        this.fpTag = fpTag ;
        this.description = description ;
        this.facebookID = facebookID ;
        widgetsList = fpWidgetList;
    }

    @Override
    protected String doInBackground(Void... params) {
        String response = "";
        JSONObject offerObj = new JSONObject();
        JSONArray ja = new JSONArray();
        JSONObject descriptionObject = new JSONObject();
        JSONObject facebookPageObject = new JSONObject();
        JSONObject fblikeBox = new JSONObject() ;
        String webWidgetsList = "" ;
        try {
            descriptionObject.put("key", "DESCRIPTION");
            descriptionObject.put("value", description);

            facebookPageObject.put("key", "FB");
            facebookPageObject.put("value", facebookID);

            for (int i = 0 ; i < widgetsList.size();i++)
            {
                webWidgetsList += widgetsList.get(i)+"#";
            }

            webWidgetsList = webWidgetsList+ "FBLIKEBOX";
            Log.d("WebWidgetsList","List : "+webWidgetsList);

            fblikeBox.put("key","WEBWIDGETS");
            fblikeBox.put("value",webWidgetsList);

            ja.put(descriptionObject);
            ja.put(facebookPageObject);
            ja.put(fblikeBox);
            offerObj.put("clientId", Constants.clientId);
            offerObj.put("fpTag", fpTag);
           offerObj.put("updates", ja);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String content= offerObj.toString();
        response  = getDataFromServer(content,Constants.HTTP_POST,
                Constants.FpsUpdate,Constants.BG_SERVICE_CONTENT_TYPE_JSON);

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
