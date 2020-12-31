package com.nowfloats.Analytics_Screen.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import static com.nowfloats.util.Constants.BASE_IMAGE_URL;

/**
 * Created by Abhi on 11/25/2016.
 */

public class SocialMediaConnectPromptFragment extends Fragment {

    UserSessionManager session;

    public static Fragment getInstance(){
        return new SocialMediaConnectPromptFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social_media_connect_prompt, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new UserSessionManager(getContext(), getActivity());

        Button button = view.findViewById(R.id.connect_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Bundle bundle = new Bundle();
                    Intent channelIntent = new Intent(getContext(), Class.forName("com.onboarding.nowfloats.ui.updateChannel.ContainerUpdateChannelActivity"));
                    String rootAlisasURI = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
                    session.setHeader(Constants.WA_KEY);
                    bundle.putString(UserSessionManager.KEY_FP_ID, session.getFPID());
                    bundle.putString(Key_Preferences.GET_FP_DETAILS_TAG, session.getFpTag());
                    bundle.putString(Key_Preferences.GET_FP_EXPERIENCE_CODE, session.getFP_AppExperienceCode());
                    bundle.putBoolean(Key_Preferences.IS_UPDATE, true);
                    bundle.putString(Key_Preferences.BUSINESS_NAME, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
                    String imageUri = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI);
                    if (!TextUtils.isEmpty(imageUri) && !imageUri.contains("http")) {
                        imageUri = BASE_IMAGE_URL + imageUri;
                    }
                    bundle.putString(Key_Preferences.BUSINESS_IMAGE, imageUri);
                    bundle.putString(Key_Preferences.BUSINESS_TYPE, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY));

                    String city = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY);
                    String country = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY);
                    String location = "";
                    if (!TextUtils.isEmpty(city) && !TextUtils.isEmpty(country)) location = city + ", " + country;
                    else location = city + country;
                    bundle.putString(Key_Preferences.LOCATION, location);

                    String normalURI = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase() + getString(R.string.tag_for_partners);
                    if (!TextUtils.isEmpty(rootAlisasURI)) bundle.putString(Key_Preferences.WEBSITE_URL, rootAlisasURI);
                    else bundle.putString(Key_Preferences.WEBSITE_URL, normalURI);
                    bundle.putString(Key_Preferences.PRIMARY_NUMBER, session.getUserPrimaryMobile());
                    bundle.putString(Key_Preferences.PRIMARY_EMAIL, session.getFPEmail());
                    channelIntent.putExtras(bundle);
                    channelIntent.putExtra("FRAGMENT_TYPE", "MY_DIGITAL_CHANNEL");
                    startActivity(channelIntent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
