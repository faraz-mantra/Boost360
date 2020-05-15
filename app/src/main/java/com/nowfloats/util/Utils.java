package com.nowfloats.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.thinksity.R;


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

	public static int dipToPx(Context c,float dipValue) {
		DisplayMetrics metrics = c.getResources().getDisplayMetrics();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}

	public static void logOnBoardingCompleteConversionGoals(Context context, String fpId){
		try{
			WebEngageController.trackEvent("Business Profile Creation Success", "Business Profile Creation Success", fpId);

			String loginMethod = "unknown";
			try{
				FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
				if (user != null) {
					for (UserInfo profile : user.getProviderData()) {
						// Id of the provider (ex: google.com)
						loginMethod = profile.getProviderId();
					}
				}
			} catch (Exception e1){
				loginMethod = "unknown";
			}

			FirebaseAnalytics mAnalytics = FirebaseAnalytics.getInstance(context);
			if(mAnalytics != null){
				Bundle params = new Bundle();
				params.putString(FirebaseAnalytics.Param.METHOD, loginMethod);
				mAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, params);
			}
		} catch (Exception e){

		}
	}

	public static int dpToPx(Context context, int dp){
		return (int)(dp * context.getResources().getDisplayMetrics().density);
	}

	public static String getDefaultOrderROIType(String category_code){
		switch (category_code){
			case "SVC":
			case "DOC":
			case "HOS":
			case "SPA":
			case "SAL":
			case "EDU":
				return "Appointments";
			case "HOT":
				return "Room Bookings";
			case "RTL":
			case "MFG":
				return "Orders";
			case "CAF":
				return "Food Orders";
			default:
				return "Orders";
		}
	}
}
