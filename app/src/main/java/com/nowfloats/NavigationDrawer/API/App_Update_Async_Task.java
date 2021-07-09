package com.nowfloats.NavigationDrawer.API;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.thinksity.R;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;

/**
 * Created by Kamal on 19-02-2015.
 */

public class App_Update_Async_Task extends AsyncTask<Void, String, String> {

    String response = "";
    String app_Version_num="";
    String play_store_Version_num="";

    Context mContext;

    public App_Update_Async_Task(Context fragmentActivity,String Version_val) {
        super();
        mContext = fragmentActivity;
        this.app_Version_num=   Version_val;

    }
    @Override
    protected void onPreExecute() {
        System.out.println("Checking for the APP Version");}



    @Override
    protected String doInBackground(Void... params) {
        response = "";
        BufferedReader in = null;
        try{

            HttpClient client = new DefaultHttpClient();
            HttpGet httpRequest = new HttpGet("https://androidquery.appspot.com/api/market?app="+mContext.getPackageName());
            org.apache.http.HttpResponse responseOfSite = client.execute(httpRequest);
            HttpEntity entity =(HttpEntity) ((org.apache.http.HttpResponse) responseOfSite).getEntity();
            if(entity!=null){
                String responseString = EntityUtils.toString(entity);
                //count = Integer.parseInt(responseString);
                Log.i("responseString--",""+responseString);
                JSONObject reader = new JSONObject(responseString);
                //JSONObject sys  = reader.getJSONObject("version");
                //String val=reader.get("version");
                int code = responseOfSite.getStatusLine().getStatusCode();

                if(code == 200)
                {
                    response = "Ok";

                    play_store_Version_num=   reader.getString("version");
                }
                else{
                    response = "Failed";
                    //play_store_Version_num = "2.2.2" ;
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return response;

    }

    protected void onPostExecute(String string) {
        try{
            boolean flag = false;
            String[] play_store_version = play_store_Version_num.split("[.]");
            String[] current_app_version = app_Version_num.split("[.]");
            if(play_store_version.length >0 && current_app_version.length >0){
                if(!play_store_version[0].contentEquals(current_app_version[0])){
                    flag = true;
                }
            }
            if(play_store_version.length >1 && current_app_version.length >1){
                if(!play_store_version[1].contentEquals(current_app_version[1])){
                    flag = true;
                }
            }
            if(play_store_version.length >2 && current_app_version.length >2){
                if(!play_store_version[2].contentEquals(current_app_version[2])){
                    flag = true;
                }
            }

            if(flag){
                appUpdateAlertDilog();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void appUpdateAlertDilog() {
        new MaterialDialog.Builder(mContext)
                .title("App update available")
                .content("You are using an old version of NowFloats Boost. Update the app to get new features and enhancements")
                .positiveText("Update")
                .negativeText("Remind me Later")
                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.primaryColor)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        final String appPackageName = mContext.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    dialog.dismiss();
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));


                } catch (android.content.ActivityNotFoundException anfe) {
                    dialog.dismiss();
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));

                }
                    }

                })
                .show();
    }


}
