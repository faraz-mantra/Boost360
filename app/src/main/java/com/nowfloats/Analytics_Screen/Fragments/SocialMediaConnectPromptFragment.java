package com.nowfloats.Analytics_Screen.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NFXApi.NfxRequestClient;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.managecustomers.FacebookChatActivity;
import com.nowfloats.socialConnect.FacebookHandler;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DataBase;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

        Button button = (Button) view.findViewById(R.id.connect_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Bundle bundle = new Bundle();
                    Intent channelIntent = new Intent(getContext(), Class.forName("com.onboarding.nowfloats.ui.updateChannel.ContainerUpdateChannelActivity"));
                    String rootAlisasURI = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
                    String normalURI = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase() + getString(R.string.tag_for_partners);
                    session.setHeader(Constants.WA_KEY);
                    bundle.putString(UserSessionManager.KEY_FP_ID, session.getFPID());
                    bundle.putString(Key_Preferences.GET_FP_DETAILS_TAG, session.getFpTag());
                    bundle.putString(Key_Preferences.GET_FP_EXPERIENCE_CODE, session.getFP_AppExperienceCode());
                    bundle.putBoolean("IsUpdate", true);
                    if (rootAlisasURI != null && !rootAlisasURI.isEmpty()) bundle.putString("website_url", rootAlisasURI);
                    else bundle.putString("website_url", normalURI);
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
