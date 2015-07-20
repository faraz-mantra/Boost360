package com.nowfloats.Product_Gallery.Service;

import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.CustomWidget.HttpDeleteWithBody;
import com.nowfloats.Product_Gallery.Product_Detail_Activity;
import com.nowfloats.Product_Gallery.Product_Gallery_Fragment;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by guru on 10-06-2015.
 */

public class ProductDelete extends AsyncTask<String,String,String>{
    String url="",values="";
    Product_Detail_Activity activity;
    private boolean flag = false;
    private MaterialDialog materialProgress;
    int position;

    public ProductDelete(String url ,String values,Product_Detail_Activity activity,int position){
        this.url=url;
        this.values=values;
        this.activity = activity;
        this.position = position;
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
    protected void onPostExecute(final String result){
        if (materialProgress!=null)
            materialProgress.dismiss();
        if (flag){
            if (Product_Gallery_Fragment.productItemModelList!=null && Product_Gallery_Fragment.productItemModelList.size()>0)
                Product_Gallery_Fragment.productItemModelList.remove(position);
            Methods.showSnackBarPositive(activity, "Product is removed...");
            activity.finish();
            activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }else{
            Methods.showSnackBarNegative(activity,"Something went wrong ,Try again...");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpDeleteWithBody del = new HttpDeleteWithBody(url);
        StringEntity se;
        try {
            se = new StringEntity(values.toString(), HTTP.UTF_8);
            se.setContentType("application/json");
            del.setEntity(se);
            HttpResponse response = httpclient.execute(del);

            StatusLine status = response.getStatusLine();
            Log.i("Delete Product---", "status----" + status);
            if (status.getStatusCode() == 200) {
                Log.i("Delete msg...","Success");
                flag = true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
