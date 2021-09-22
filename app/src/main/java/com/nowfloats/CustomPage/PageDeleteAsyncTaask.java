package com.nowfloats.CustomPage;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.CustomWidget.HttpDeleteWithBody;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.Utils;
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

public class PageDeleteAsyncTaask extends AsyncTask<String, String, String> {
    String url = "", tag = "";
    Activity activity;
    private boolean flag = false;
    private MaterialDialog materialProgress;
    private CustomPageInterface pageInterface;
    private Bus bus;

    public PageDeleteAsyncTaask(String url, Activity activity, String tag, CustomPageInterface pageInterface, Bus bus) {
        this.url = url;
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
                .content(activity.getString(R.string.deleting))
                .progress(true, 0)
                .show();
        materialProgress.setCancelable(false);
    }

    @Override
    protected void onPostExecute(final String result) {
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            for (int i = 0; i < CustomPageFragment.posList.size(); i++) {
                int n = Integer.parseInt(CustomPageFragment.posList.get(i).toString());
                final JSONObject map = new JSONObject();
                map.put("PageId", CustomPageFragment.dataModel.get(n).PageId);
                map.put("Tag", "" + tag);
                map.put("clientId", "" + Constants.clientId);
                if (CustomPageFragment.posList.size() - 1 == i) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            deleteMethod(map.toString(), true);
                        }
                    }).start();
                } else {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void deleteMethod(String values, final boolean lastChk) {
        flag = false;
        HttpClient httpclient = new DefaultHttpClient();
        HttpDeleteWithBody del = new HttpDeleteWithBody(url);
        del.addHeader("Authorization", Utils.getAuthToken());
        StringEntity se;
        try {
            se = new StringEntity(values, HTTP.UTF_8);
            se.setContentType("application/json");
            del.setEntity(se);
            HttpResponse response = httpclient.execute(del);

            StatusLine status = response.getStatusLine();
            Log.i("Delete Page---", "status----" + status);
            if (status.getStatusCode() == 200) {
                MixPanelController.track("DeleteCustomPages", null);
                Log.i("Delete page...", "Success");
                flag = true;
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
                    }
                });
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (lastChk) {
                        if (materialProgress != null)
                            materialProgress.dismiss();
                        if (flag) {
//                            if (CustomPageFragment.dataModel!=null && CustomPageFragment.dataModel.size()>0){
//                        for (int i = 0; i < CustomPageFragment.posList.size(); i++) {
//                            int n = Integer.parseInt(CustomPageFragment.posList.get(i).toString());
//                            CustomPageFragment.dataModel.remove(n);
//                        }

                            new CustomPageService().GetPages(tag, Constants.clientId, pageInterface, bus);
                            CustomPageFragment.posList = new ArrayList<>();

//                        CustomPageFragment.custompageAdapter.notifyDataSetChanged();
//                            }
//                    if (CustomPageFragment.recyclerView!=null){
//                        CustomPageFragment.recyclerView.invalidate();
//                    }
                            Methods.showSnackBarPositive(activity, activity.getString(R.string.page_removed));
                        } else {
                            Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
                            if (CustomPageFragment.custompageAdapter != null)
                                CustomPageFragment.custompageAdapter.notifyDataSetChanged();
                            if (CustomPageFragment.recyclerView != null)
                                CustomPageFragment.recyclerView.invalidate();
                            CustomPageFragment.posList = new ArrayList<>();
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