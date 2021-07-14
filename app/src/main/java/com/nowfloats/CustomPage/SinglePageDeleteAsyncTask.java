package com.nowfloats.CustomPage;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.CustomWidget.HttpDeleteWithBody;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
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

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_DELETE_CUSTOMPAGE;
import static com.framework.webengageconstant.EventLabelKt.FAILED_TO_DELETE_CUSTOMPAGE;
import static com.framework.webengageconstant.EventNameKt.DELETE_CUSTOMPAGE;

/**
 * Created by guru on 26-08-2015.
 */

public class SinglePageDeleteAsyncTask extends AsyncTask<String, String, String> {
    String url = "", tag = "";
    Activity activity;
    private boolean flag = false;
    private MaterialDialog materialProgress;
    private String pageId;
    private int position;

    public SinglePageDeleteAsyncTask(String url, Activity activity, String tag, String pageId,
                                     int position) {
        this.url = url;
        this.activity = activity;
        this.tag = tag;
        this.pageId = pageId;
        this.position = position;
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
            JSONObject map = new JSONObject();
            map.put("PageId", pageId);
            map.put("Tag", "" + tag);
            map.put("clientId", "" + Constants.clientId);
            deleteMethod(map.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void deleteMethod(String values) {
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
                MixPanelController.track("DeleteCustomPage", null);
                Log.i("Delete page...", "Success");
                WebEngageController.trackEvent(DELETE_CUSTOMPAGE, EVENT_LABEL_DELETE_CUSTOMPAGE, pageId);
                flag = true;
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
                        WebEngageController.trackEvent(DELETE_CUSTOMPAGE, FAILED_TO_DELETE_CUSTOMPAGE, pageId);
                    }
                });
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (materialProgress != null)
                        materialProgress.dismiss();
                    if (flag) {
                        activity.finish();
                        CustomPageFragment.dataModel.remove(position);
                        Methods.showSnackBarPositive(activity, activity.getString(R.string.page_removed));
                        if (CustomPageFragment.custompageAdapter != null)
                            CustomPageFragment.custompageAdapter.notifyDataSetChanged();
                        if (CustomPageFragment.recyclerView != null)
                            CustomPageFragment.recyclerView.invalidate();
                    } else {
                        Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
                        if (CustomPageFragment.custompageAdapter != null)
                            CustomPageFragment.custompageAdapter.notifyDataSetChanged();
                        if (CustomPageFragment.recyclerView != null)
                            CustomPageFragment.recyclerView.invalidate();
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