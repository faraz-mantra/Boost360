package com.nowfloats.CustomWidget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.ProgressBar;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.views.ProgressBarIndeterminate;

/**
 * Created by NowFloatsDev on 27/05/2015.
 */
public class MaterialProgressBar {

    private static ProgressDialog progressDialog;

    public static void startProgressBar(Activity activity,String status,boolean isCancelable)
    {
        progressDialog = ProgressDialog.show(activity, "", status);
        progressDialog.setCancelable(isCancelable);
    }

    public static void dismissProgressBar()
    {
        progressDialog.dismiss();
    }
}
