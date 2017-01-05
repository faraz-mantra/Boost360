package com.nowfloats.NavigationDrawer.businessApps;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;


/**
 * Created by Admin on 12/27/2016.
 */
public class BusinessAppDevelopment extends Fragment implements View.OnClickListener {
    private static final int SHOW_PREVIEW = 0,SITE_HEALTH=2;

    private String type;
    private Context context;
    private UserSessionManager session;

    public static Fragment getInstance(String type){
        Fragment frag=new BusinessAppDevelopment();
        Bundle b=new Bundle();
        b.putString("type",type);
        frag.setArguments(b);
        return  frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            type=getArguments().getString("type","android");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_business_app_development_screen,container,false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(!isAdded()) return;

        session = new UserSessionManager(context, getActivity());

        TextView androidTextView= (TextView) view.findViewById(R.id.android_app);
        TextView appNameTextView= (TextView) view.findViewById(R.id.app_name);
        TextView congratesTextView= (TextView) view.findViewById(R.id.textview_congrates);
        TextView firstCharText= (TextView) view.findViewById(R.id.textcharacter);
        Button previewButton= (Button) view.findViewById(R.id.preview_button);
        Button siteHealthButton= (Button) view.findViewById(R.id.site_health);
        ImageView logoImage = (ImageView) view.findViewById(R.id.app_logo);
        ImageView back = (ImageView) view.findViewById(R.id.background_image_view);

        previewButton.setOnClickListener(this);
        siteHealthButton.setOnClickListener(this);

        Animation animation = AnimationUtils.loadAnimation(context,R.anim.progressbar_anim);
        back.startAnimation(animation);

        String logo = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl);

        String name=session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME);
        appNameTextView.setText(name);

        if(logo==null || logo.isEmpty()){
            firstCharText.setText(String.valueOf(name.charAt(0)));
            firstCharText.setVisibility(View.VISIBLE);
        }
        else if(!logo.contains("http")){
            logo = "https://"+logo;
        }
        Glide.with(context)
                .load(logo)
                .placeholder(getResources().getDrawable(R.drawable.studio_architecture))
                .into(logoImage);

        if(type.equalsIgnoreCase("android")){
            androidTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.android_green), null, null, null );
            androidTextView.setText(getResources().getString(R.string.android_app));
            previewButton.setText(getResources().getString(R.string.android_app_preview));
            congratesTextView.setText(getResources().getString(R.string.congratulation_android_business_app_development));
        }else{
            siteHealthButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.site_health),null,null,null);
            androidTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ios_icon_blac), null, null, null );
            androidTextView.setText(getResources().getString(R.string.ios_app));
            previewButton.setText(getResources().getString(R.string.ios_app_preview));
            congratesTextView.setText(getResources().getString(R.string.congratulation_ios_business_app_development));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.preview_button:
                BusinessAppPreview frag= (BusinessAppPreview) getParentFragment();
                frag.addAndroidFragment(BusinessAppPreview.SHOW_COMPLETE);
                break;
            case R.id.site_health:
                ((BusinessAppsActivity)context).addFragments(SITE_HEALTH);
                break;
            default:
                break;
        }
    }
}
