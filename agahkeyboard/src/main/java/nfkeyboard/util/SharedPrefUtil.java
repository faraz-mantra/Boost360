package nfkeyboard.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by NowFloats on 26-02-2018.
 */

public class SharedPrefUtil {

    private SharedPreferences sBoostPref;
    private static SharedPrefUtil sPrefUtil;
    private SharedPrefUtil() {

    }

    public static SharedPrefUtil fromBoostPref() {
        if(sPrefUtil == null) {
            sPrefUtil = new SharedPrefUtil();
        }
        return  sPrefUtil;
    }
    public SharedPrefUtil getsBoostPref(Context context){

//            Context boostContext = context.createPackageContext("com.biz2.nowfloats", 0);
        sBoostPref =  context.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE);

        return sPrefUtil;
    }
    public String getFpId() {
        if(sBoostPref != null) {
            return sBoostPref.getString("fpid", null);
        }
        return null;
    }

    public String getFpTag() {
        if(sBoostPref != null) {
            return sBoostPref.getString("GET_FP_DETAILS_TAG", null);
        }
        return null;
    }
    public String getProductVerb() {
        if(sBoostPref != null) {
            return sBoostPref.getString("GET_PRODUCT_CATEGORY_VERB", null);
        }
        return null;
    }
    public boolean isLoggedIn(){
        if(sBoostPref != null) {
            return sBoostPref.getBoolean("IsUserLoggedIn", false);
        }
        return false;
    }
}
