package com.nowfloats.Analytics_Screen.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.SocialAnalytics;
import com.thinksity.R;


/**
 * Created by Abhi on 11/28/2016.
 */

public class FetchFacebookDataFragment extends Fragment {

    String mType;
    public static Fragment getInstance(Bundle b){
        Fragment frag =new FetchFacebookDataFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            Bundle b = getArguments();
            mType = b.getString("mType");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nfx_fatch_facebook,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView textView1 = (TextView) view.findViewById(R.id.facebook_analytics_connect_text1);
        TextView textView2 = (TextView) view.findViewById(R.id.facebook_analytics_connect_text2);
        ImageView image = (ImageView) view.findViewById(R.id.boost_app_icon);
        String socialType = null;
        if(SocialAnalytics.FACEBOOK.equals(mType)){
            image.setVisibility(View.VISIBLE);
            socialType = "Facebook Page";
        }else if(SocialAnalytics.QUIKR.equals(mType)){
            socialType = "Quikr";
            image.setVisibility(View.GONE);
        }
        String text1_message = "Gathering "+socialType+" analytics";
        String text2_message = "Please wait, as we are collecting statistics from your "+socialType+". It usually takes upto 24 hours to fetch data.";
        textView2.setText(text2_message);
        textView1.setText(text1_message);
    }
}
