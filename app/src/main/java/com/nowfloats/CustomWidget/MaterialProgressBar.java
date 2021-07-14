package com.nowfloats.CustomWidget;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by NowFloatsDev on 27/05/2015.
 */
public class MaterialProgressBar {

    private static ProgressDialog progressDialog;

    public static void startProgressBar(Activity activity, String status, boolean isCancelable) {
        progressDialog = ProgressDialog.show(activity, "", status);
        progressDialog.setCancelable(isCancelable);
    }

    public static void dismissProgressBar() {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}