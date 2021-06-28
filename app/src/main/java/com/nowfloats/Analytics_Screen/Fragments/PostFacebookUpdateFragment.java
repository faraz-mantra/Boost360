package com.nowfloats.Analytics_Screen.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.SocialAnalytics;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.Create_Message_Activity;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;


/**
 * Created by Abhi on 11/28/2016.
 */

public class PostFacebookUpdateFragment extends Fragment {
    TextView postUpdate;
    Context context;
    String mType;

    public static Fragment getInstance(Bundle b){
        Fragment frag = new PostFacebookUpdateFragment();
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
        View root =inflater.inflate(R.layout.layout_empty_img_text_button_screen,container,false);
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postUpdate= view.findViewById(R.id.btn_action);
        ImageView image = view.findViewById(R.id.image1);
        image.setImageResource(R.drawable.no_updates);
        TextView message = (TextView) view.findViewById(R.id.message_text2);
        String socialTypeText1 = null,socialTypeText2 = null;
        if(SocialAnalytics.FACEBOOK.equals(mType)){
            socialTypeText2 = "Facebook Page";
            socialTypeText1 = "Facebook Page";
        }else if(SocialAnalytics.QUIKR.equals(mType)){
            socialTypeText2 = "Quikr";
            socialTypeText1 = "Quikr account";
        }
        String text = getString(R.string.looks_like_you_havent_posted_any_update_on_your)+socialTypeText1+" through "+ getString(R.string.app_name)+getString(R.string.yet_make_sure_you_select_the)+socialTypeText2+" option</b> while creating an update";
        message.setText(Methods.fromHtml(text));

        if(!isAdded()) return;

        final UserSessionManager session = new UserSessionManager(context,getActivity());
        postUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
                    Methods.showFeatureNotAvailDialog(getContext());
                }else{
                    Intent i = new Intent(context, Create_Message_Activity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
    }
}
