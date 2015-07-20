package com.nowfloats.NavigationDrawer.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.Analytics_Fragment;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.text.NumberFormat;
import java.util.Locale;

public class GetVisitorsAndSubscribersCountAsyncTask extends AsyncTask<Void, String, String>{
	Activity mContext;
	ProgressDialog pd;
	String response = "";
	//public IOnObserverListener onObserverListener;
//	int count;
	UserSessionManager sessionManager;
    private String numberOfViews,numberOfSubscribers;

    public GetVisitorsAndSubscribersCountAsyncTask(Activity context, UserSessionManager session) {
       super();
       mContext = context;
       sessionManager= session;
    }
	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       
    }
    
    @Override
    protected void onPostExecute(String string) {
        try{
            if(response.equals("Ok")){
                try {
                    if (!numberOfViews.contains(","))
                        numberOfViews = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(numberOfViews));
                    if (!numberOfSubscribers.contains(",")){
                        numberOfSubscribers = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(numberOfSubscribers));
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                sessionManager.setVisitorsCount(numberOfViews);
                sessionManager.setSubcribersCount(numberOfSubscribers);

                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(Analytics_Fragment.subscriberCount != null && Analytics_Fragment.subscriber_progress!=null)
                        {
                            Analytics_Fragment.subscriberCount.setVisibility(View.VISIBLE);
                            Analytics_Fragment.subscriber_progress.setVisibility(View.GONE);
                            Analytics_Fragment.subscriberCount.setText(numberOfSubscribers);
                            Log.i("Subscribers",""+numberOfSubscribers);
                        }
                        if(Analytics_Fragment.visitCount != null && Analytics_Fragment.visits_progressBar!=null)
                        {
                            Analytics_Fragment.visitCount.setVisibility(View.VISIBLE);
                            Analytics_Fragment.visits_progressBar.setVisibility(View.GONE);
                            Analytics_Fragment.visitCount.setText(numberOfViews);
                            Log.i("Visitors",""+numberOfSubscribers);
                        }
                    }
                });
            } else{ Methods.showSnackBarNegative(mContext, "Something went wrong! in Visitor and Subscriber count...");}
        }catch(Exception e){e.printStackTrace();}
    }
    
	@Override
	protected String doInBackground(Void... params) {
        URI website = null;
        response = "";
		try{
			HttpClient client = new DefaultHttpClient();
            if(sessionManager.getISEnterprise().equals("true"))
            {
                String queryURL = Constants.NOW_FLOATS_API_URL+"/Dashboard/v1/"+
                        sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PARENTID)+"/summary?clientId="+Constants.clientId+
                        "&fpId="+sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PARENTID)+"&scope=1";
                website = new URI(queryURL);
            } else {
               website = new URI(Constants.NOW_FLOATS_API_URL+"/Dashboard/v1/"+sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)+"/summary?clientId="
                        + Constants.clientId+"&fpId="+sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)+"&scope=0");
            }
//            normal
//            https://api.withfloats.com/Dashboard/v1/TECHNEWS/summary?clientId=DB96EA35A6E44C0F8FB4A6BAA94DB017C0DFBE6F9944B14AA6C3C48641B3D70&fpId=TECHNEWS&scope=0

//            enterprise
//            https://api.withfloats.com/Dashboard/v1/30/summary?clientId=DB96EA35A6E44C0F8FB4A6BAA94DB017C0DFBE6F9944B14AA6C3C48641B3D70&fpId=30&scope=1

			HttpGet httpRequest = new HttpGet();
			httpRequest.setURI(website);
			HttpResponse responseOfSite = client.execute(httpRequest);
			HttpEntity entity = responseOfSite.getEntity();
            int code = responseOfSite.getStatusLine().getStatusCode();

			if(entity!=null){
                Log.i("Count--",""+entity);
				String responseString = EntityUtils.toString(entity);
                Log.i("responseString--",""+responseString);
                JSONObject responseJson = new JSONObject(responseString);
                JSONArray entityArray = responseJson.getJSONArray("Entity");

                for(int i = 0 ; i < entityArray.length() ;i++) {
                    JSONObject data = (JSONObject) entityArray.get(i);
                    numberOfViews = data.getString("NoOfViews");
                    String numberOfMessages = data.getString("NoOfMessages");
                    numberOfSubscribers = data.getString("NoOfSubscribers");

                }
                    if(code == 200)
                    {
                        response = "Ok";
                    }
                    else{
                        response = "Failed";
                    }
				}
		}
		catch(Exception ex)
		{
            ex.printStackTrace();
            response = "Failed";
		}
		return response;
    }
}
