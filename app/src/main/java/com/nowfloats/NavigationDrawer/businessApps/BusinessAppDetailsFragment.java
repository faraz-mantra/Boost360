package com.nowfloats.NavigationDrawer.businessApps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nowfloats.BusinessProfile.UI.UI.Business_Logo_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Contact_Info_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Key_Preferences;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.thinksity.R;

/**
 * Created by Abhi on 12/21/2016.
 */

public class BusinessAppDetailsFragment extends Fragment implements View.OnTouchListener {

    MaterialEditText mEmail,mLogDesc,mAppName,mPhoneNumber;
    private Context context;
    private ImageView mImage;
    private UserSessionManager session;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_business_app_details,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(!isAdded()) return;
        session = new UserSessionManager(context,getActivity());

        getActivity().setTitle("Edit App Details");
        mImage= (ImageView) view.findViewById(R.id.app_image);
        mEmail= (MaterialEditText) view.findViewById(R.id.app_email);
        mLogDesc= (MaterialEditText) view.findViewById(R.id.app_long_disc);
        mAppName= (MaterialEditText) view.findViewById(R.id.app_name);
        mPhoneNumber= (MaterialEditText) view.findViewById(R.id.app_number);

        mEmail.setOnTouchListener(this);
        mLogDesc.setOnTouchListener(this);
        mAppName.setOnTouchListener(this);
        mPhoneNumber.setOnTouchListener(this);
        mImage.setOnTouchListener(this);

        mPhoneNumber.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));
        mEmail.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
        mLogDesc.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION));
        mAppName.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        String logo = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl);

        if(logo!=null && !logo.contains("http")){
            logo = "https://"+logo;
        }
        Glide.with(context)
                .load(logo)
                .placeholder(getResources().getDrawable(R.drawable.studio_t))
                .into(mImage);
      /*  mShortDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()<5){
                    mShortDesc.setError("It field should have atleast five character");
                }
                else mShortDesc.setError(null);
            }
        });*/
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_settings).setVisible(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(getString(R.string.business_apps));
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(MotionEvent.ACTION_UP == motionEvent.getAction()) {
            switch (view.getId()) {
                case R.id.app_image:
                    Intent i = new Intent(context, Business_Logo_Activity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                case R.id.app_number:
                case R.id.app_email:
                    Intent i1 = new Intent(context, Contact_Info_Activity.class);
                    startActivity(i1);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                case R.id.app_long_disc:
                case R.id.app_name:
                    Intent i2 = new Intent(context, Edit_Profile_Activity.class);
                    startActivity(i2);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                default:
                    break;
            }
        }
        return false;
    }
}
