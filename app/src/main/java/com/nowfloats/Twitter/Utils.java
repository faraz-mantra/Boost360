package com.nowfloats.Twitter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Boost Android on 07/05/2016.
 */
public class Utils {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager mConnectManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectManager != null) {
            NetworkInfo[] mNetworkInfo = mConnectManager.getAllNetworkInfo();
            for (int i = 0; i < mNetworkInfo.length; i++) {
                if (mNetworkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    return true;
            }
        }
        return false;
    }

}
