package com.nowfloats.Analytics_Screen.Fragments;

import android.app.Activity;
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

import static com.nowfloats.helper.DigitalChannelUtil.startDigitalChannel;
import static com.nowfloats.util.Constants.BASE_IMAGE_URL;

/**
 * Created by Abhi on 11/25/2016.
 */

public class SocialMediaConnectPromptFragment extends Fragment {

    UserSessionManager session;

    public static Fragment getInstance() {
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
                startDigitalChannel(getActivity(), session);
            }
        });
    }

}
