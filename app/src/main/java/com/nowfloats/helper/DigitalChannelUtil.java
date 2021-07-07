package com.nowfloats.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import static com.nowfloats.util.Constants.BASE_IMAGE_URL;

public class DigitalChannelUtil {
    public static void startDigitalChannel(Activity activity, UserSessionManager session) {
        try {
            if (activity == null && session == null) return;
            Bundle bundle = new Bundle();
//            Intent channelIntent = new Intent(activity, Class.forName("com.onboarding.nowfloats.ui.updateChannel.DigitalChannelActivity"));
            Intent channelIntent = new Intent(activity, Class.forName("com.onboarding.nowfloats.ui.updateChannel.ContainerDigitalChannelActivity"));
            String rootAlisasURI = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
            session.setHeader(Constants.WA_KEY);
            bundle.putString(UserSessionManager.KEY_FP_ID, session.getFPID());
            bundle.putString(Key_Preferences.GET_FP_DETAILS_TAG, session.getFpTag());
            bundle.putString(Key_Preferences.GET_FP_EXPERIENCE_CODE, session.getFP_AppExperienceCode());
            bundle.putBoolean(Key_Preferences.IS_UPDATE, true);
            bundle.putString(Key_Preferences.BUSINESS_NAME, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
            bundle.putString(Key_Preferences.CONTACT_NAME, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME));
            String imageUri = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl);
            if (!TextUtils.isEmpty(imageUri) && !imageUri.contains("http")) {
                imageUri = BASE_IMAGE_URL + imageUri;
            }
            bundle.putString(Key_Preferences.BUSINESS_IMAGE, imageUri);
            bundle.putString(Key_Preferences.BUSINESS_TYPE, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY));

            String city = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY);
            String country = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY);
            String location = "";
            if (!TextUtils.isEmpty(city) && !TextUtils.isEmpty(country))
                location = city + ", " + country;
            else location = city + country;
            bundle.putString(Key_Preferences.LOCATION, location);

            String normalURI = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase() + activity.getString(R.string.tag_for_partners);
            if (!TextUtils.isEmpty(rootAlisasURI))
                bundle.putString(Key_Preferences.WEBSITE_URL, rootAlisasURI);
            else bundle.putString(Key_Preferences.WEBSITE_URL, normalURI);
            bundle.putString(Key_Preferences.PRIMARY_NUMBER, session.getUserPrimaryMobile());
            bundle.putString(Key_Preferences.PRIMARY_EMAIL, session.getFPEmail());
            channelIntent.putExtras(bundle);
            channelIntent.putExtra("FRAGMENT_TYPE", "MY_DIGITAL_CHANNEL");
            activity.startActivity(channelIntent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
