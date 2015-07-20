package com.nowfloats.NavigationDrawer.API;

import android.app.Activity;
import android.os.AsyncTask;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;

public class GetSearchQueryCountAsyncTask extends AsyncTask<Void, String, String>{
	Activity mContext;
	String response = "";
	int count;
	UserSessionManager session;
    public GetSearchQueryCountAsyncTask(Activity context,UserSessionManager session) {
       super();
       mContext = context;
       this.session=session;
    }
	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    
    @Override
    protected void onPostExecute(String string) {
    	try{
    		if(response.equals("Ok")){
    			session.setSearchCount(count + "");
    		}
    		else{
                Methods.showSnackBarNegative(mContext,"Something went wrong! in Search queries count...");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
	@Override
	protected String doInBackground(Void... params) {
       // https://api.withfloats.com/Dashboard/v1/
       // {fpTag}/
       // summary?clientId={clientId}&
       // fpId={fpId}&
       // scope={scope}

        URI website = null;

        response = "";
		try{
			HttpClient client = new DefaultHttpClient();
            if(session.getISEnterprise().equals("true"))
            {
                String queryURL = Constants.NOW_FLOATS_API_URL+"/Dashboard/v1/"+
                        session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PARENTID)+"/summary?clientId="+Constants.clientId+
                        "&fpId="+session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PARENTID)+"&scope=1";
                website = new URI(queryURL);
            } else {
                website = new URI(Constants.SearchQueryCount +
                        Constants.clientId + "&fpTag=" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            }
			HttpGet httpRequest = new HttpGet();
			httpRequest.setURI(website);
			HttpResponse responseOfSite = client.execute(httpRequest);
			HttpEntity entity = responseOfSite.getEntity();
			if(entity!=null){
				String responseString = EntityUtils.toString(entity);
				count = Integer.parseInt(responseString);
				int code = responseOfSite.getStatusLine().getStatusCode();
				if(code == 200)
				{
				    response = "Ok";
				}else{
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
