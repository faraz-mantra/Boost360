package com.nowfloats.BusinessProfile.UI.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.framework.webengageconstant.EventLabelKt.CHANGE_PASSWORD_ERROR;
import static com.framework.webengageconstant.EventLabelKt.SUCCESSFULLY_CHANGED_PASSWORD;
import static com.framework.webengageconstant.EventNameKt.CHANGE_PASSWORD;
import static com.framework.webengageconstant.EventValueKt.NULL;

public class changePasswordAsyncTask extends AsyncTask<Void, Void, Void> {

    Context context;
    JSONObject obj;
    ProgressDialog pd = null;
    String responseMessage = "";
    boolean success = false;
    Activity app = null;

    public changePasswordAsyncTask(Context context, JSONObject obj, Activity app) {
        this.context = context;
        this.obj = obj;
        this.app = app;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (pd != null)
            pd.dismiss();
        if (success) {
            Methods.showSnackBarPositive(app, context.getResources().getString(R.string.password_updated));
            WebEngageController.trackEvent(CHANGE_PASSWORD, SUCCESSFULLY_CHANGED_PASSWORD, NULL);
//			app.finish();
        } else {
            Methods.showSnackBarNegative(app, context.getResources().getString(R.string.entered_password_incorrect));
            WebEngageController.trackEvent(CHANGE_PASSWORD, CHANGE_PASSWORD_ERROR, NULL);
        }
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.please_wait), context.getResources().getString(R.string.updating_password));
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        String response = "";
        String content = obj.toString();
        DataOutputStream outputStream = null;

        URL new_url;
        try {
            new_url = new URL(Constants.ChangePassword);
            HttpURLConnection connection = (HttpURLConnection) new_url
                    .openConnection();
            connection.setRequestMethod(Constants.HTTP_POST);
            connection.setRequestProperty("Content-Type",
                    Constants.BG_SERVICE_CONTENT_TYPE_JSON);
            connection.setRequestProperty("Connection", "Keep-Alive");
            outputStream = new DataOutputStream(connection.getOutputStream());
            byte[] BytesToBeSent = content.getBytes();
            if (BytesToBeSent != null) {
                outputStream.write(BytesToBeSent, 0, BytesToBeSent.length);
            }
            int responseCode = connection.getResponseCode();
            if (responseCode == 200 || responseCode == 202) {
                success = true;
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
