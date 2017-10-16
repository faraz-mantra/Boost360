package com.nowfloats.BusinessProfile.UI.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.SiteAppearance.SiteAppearanceActivity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

/**
 * Created by Admin on 05-10-2017.
 */

public class Business_Profile_Fragment_V3 extends Fragment implements View.OnClickListener {
    @Nullable
    Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_business_profile_v2,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!isAdded()){
            return;
        }
        UserSessionManager session = new UserSessionManager(mContext,getActivity());
        ImageView profileImage = (ImageView) view.findViewById(R.id.img_profile);
        ImageView editImage = (ImageView) view.findViewById(R.id.img_edit);
        TextView description = (TextView) view.findViewById(R.id.tv_business_description);
        TextView businessName = (TextView) view.findViewById(R.id.tv_business_name);
        TextView category = (TextView) view.findViewById(R.id.tv_business_category);
        view.findViewById(R.id.cv_business_details).setOnClickListener(this);
        view.findViewById(R.id.cv_business_images).setOnClickListener(this);
        view.findViewById(R.id.cv_site_appearance).setOnClickListener(this);
        view.findViewById(R.id.cv_custom_pages).setOnClickListener(this);
        view.findViewById(R.id.cv_pricing_plans).setOnClickListener(this);
        view.findViewById(R.id.cv_account_details).setOnClickListener(this);
        editImage.setOnClickListener(this);
        String iconUrl = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI);
        if (iconUrl.length() > 0 && iconUrl.contains("BizImages") && !iconUrl.contains("http")) {

            Picasso.with(mContext)
                    .load(Constants.BASE_IMAGE_URL + "" + iconUrl).placeholder(R.drawable.business_edit_profile_icon).into(profileImage);
        } else if ( iconUrl.length() > 0) {
                Picasso.with(mContext)
                        .load(iconUrl).placeholder(R.drawable.business_edit_profile_icon).into(profileImage);
        }

        if (session.getIsSignUpFromFacebook().contains("true") && !Util.isNullOrEmpty(session.getFacebookPageURL())) {
            Picasso.with(mContext)
                    .load(session.getFacebookPageURL()).placeholder(R.drawable.business_edit_profile_icon)
                    .rotate(90)
                    .into(profileImage);
        }

        Typeface robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        category.setTypeface(robotoLight);
        category.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY));
        businessName.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        description.setTypeface(robotoLight);
        description.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION));

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.cv_business_details:
                intent = new Intent(mContext,BusinessDetailsActivity.class);
                break;
            case R.id.cv_business_images:
                break;
            case R.id.cv_site_appearance:
                intent = new Intent(mContext,SiteAppearanceActivity.class);
                break;
            case R.id.cv_custom_pages:
                intent = new Intent(mContext,CustomPageActivity.class);
                break;
            case R.id.cv_account_details:
                break;
            case R.id.cv_pricing_plans:
                break;
            case R.id.img_edit:
                intent = new Intent(mContext,Edit_Profile_Activity.class);
                break;
        }
        if(intent != null){
            startActivity(intent);
            if(mContext instanceof Activity) {
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }
}
