package com.nowfloats.Analytics_Screen.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
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

public class ProcessFacebookDataFragment extends Fragment {

    String mType;
    public static Fragment getInstance(Bundle b){
        Fragment frag =new ProcessFacebookDataFragment();
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
        Log.v("ggg",mType+" ");
        if(SocialAnalytics.FACEBOOK.equals(mType)){
            image.setImageResource(R.drawable.boost_with_facebook);
            socialType = "Facebook Page";
        }else if(SocialAnalytics.QUIKR.equals(mType)){
            socialType = "Quikr";
            image.setImageResource(R.drawable.boost_with_quikr);
        }
        String text1_message = "Gathering "+socialType+" analytics";
        String text2_message = getString(R.string.please_wait_as_we_are_collecting_statistics_from_your)+socialType+". It usually takes upto 24 hours to fetch data.";
        textView2.setText(text2_message);
        textView1.setText(text1_message);
    }
}
