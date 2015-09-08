package com.nowfloats.CustomPage;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.CustomWidget.HttpDeleteWithBody;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.thinksity.R;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by guru on 26-08-2015.
 */

public class PageDeleteAsyncTaask extends AsyncTask<String,String,String>{
    String url="",tag = "";
    Activity activity;
    private boolean flag = false;
    private MaterialDialog materialProgress;
    private CustomPageInterface pageInterface;
    private Bus bus;

    public PageDeleteAsyncTaask(String url, Activity activity, String tag, CustomPageInterface pageInterface, Bus bus){
        this.url=url;
        this.activity = activity;
        this.tag = tag;
        this.bus = bus;
        this.pageInterface = pageInterface;
        flag = false;
    }

    @Override
    protected void onPreExecute() {
        materialProgress = new MaterialDialog.Builder(activity)
                .widgetColorRes(R.color.accentColor)
                .content("Deleting....")
                .progress(true, 0)
                .show();
        materialProgress.setCancelable(false);
    }

    @Override
    protected void onPostExecute(final String result){}

    @Override
    protected String doInBackground(String... params) {
        try {
            for (int i = 0; i < CustomPageActivity.posList.size(); i++) {
                int n = Integer.parseInt(CustomPageActivity.posList.get(i).toString());
                final JSONObject map = new JSONObject();
                map.put("PageId", CustomPageActivity.dataModel.get(n).PageId);
                map.put("Tag", "" + tag);
                map.put("clientId", "" + Constants.clientId);
                if (CustomPageActivity.posList.size()-1 == i){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            deleteMethod(map.toString(), true);
                        }
                    }).start();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            deleteMethod(map.toString(), false);
                        }
                    }).start();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {e.printStackTrace();}
        return  null;
    }

    private void deleteMethod(String values, final boolean lastChk) {
        flag = false;
        HttpClient httpclient = new DefaultHttpClient();
        HttpDeleteWithBody del = new HttpDeleteWithBody(url);
        StringEntity se;
        try {
            se = new StringEntity(values, HTTP.UTF_8);
            se.setContentType("application/json");
            del.setEntity(se);
            HttpResponse response = httpclient.execute(del);

            StatusLine status = response.getStatusLine();
            Log.i("Delete Page---", "status----" + status);
            if (status.getStatusCode() == 200) {
                Log.i("Delete page...","Success");
                flag = true;
            }else{
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Methods.showSnackBarNegative(activity,"Something went wrong ,Try again...");
                    }
                });
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (lastChk){
                        if (materialProgress!=null)
                            materialProgress.dismiss();
                        if (flag){
//                            if (CustomPageActivity.dataModel!=null && CustomPageActivity.dataModel.size()>0){
//                        for (int i = 0; i < CustomPageActivity.posList.size(); i++) {
//                            int n = Integer.parseInt(CustomPageActivity.posList.get(i).toString());
//                            CustomPageActivity.dataModel.remove(n);
//                        }

                            new CustomPageService().GetPages(activity,tag,Constants.clientId,pageInterface,bus);
                            CustomPageActivity.posList = new ArrayList<>();

//                        CustomPageActivity.custompageAdapter.notifyDataSetChanged();
//                            }
//                    if (CustomPageActivity.recyclerView!=null){
//                        CustomPageActivity.recyclerView.invalidate();
//                    }
                            Methods.showSnackBarPositive(activity, "Pages removed...");
                        }else{
                            Methods.showSnackBarNegative(activity,"Something went wrong ,Try again...");
                            if (CustomPageActivity.custompageAdapter!=null)
                                CustomPageActivity.custompageAdapter.notifyDataSetChanged();
                            if (CustomPageActivity.recyclerView!=null)
                                CustomPageActivity.recyclerView.invalidate();
                            CustomPageActivity.posList = new ArrayList<>();
                        }
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}