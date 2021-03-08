package com.nowfloats.NavigationDrawer.businessApps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_PAID;


/**
 * Created by Admin on 12/27/2016.
 */
public class BusinessAppDevelopment extends Fragment implements View.OnClickListener {

    private String type;
    private Context context;
    private UserSessionManager session;
    ImageView back;
    LinearLayout parent;
    SharedPreferences pref;

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
            if(type.equals("android")){
                setHasOptionsMenu(true);
            }
        }
        pref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        MixPanelController.track(MixPanelController.BUSINESS_APP_BUILD_IN_PROCESS,null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_business_app_development_screen,container,false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(!isAdded() || isDetached()) return;

        session = new UserSessionManager(context, requireActivity());
        if(pref.getInt(Key_Preferences.ABOUT_BUSINESS_APP,BIZ_APP_DEMO) == BIZ_APP_DEMO){
            pref.edit().putInt(Key_Preferences.ABOUT_BUSINESS_APP,BIZ_APP_PAID).apply();
        }

        TextView androidTextView= (TextView) view.findViewById(R.id.android_app);
        TextView appNameTextView= (TextView) view.findViewById(R.id.app_name);
        TextView congratesTextView= (TextView) view.findViewById(R.id.textview_congrates);
        TextView firstCharText= (TextView) view.findViewById(R.id.textcharacter);
        Button previewButton= (Button) view.findViewById(R.id.preview_button);
        Button siteHealthButton= (Button) view.findViewById(R.id.site_health);
        ImageView logoImage = (ImageView) view.findViewById(R.id.app_logo);
        back = (ImageView) view.findViewById(R.id.background_image_view);
        parent = (LinearLayout) view.findViewById(R.id.anim_parent);
        previewButton.setOnClickListener(this);
        siteHealthButton.setOnClickListener(this);

        //Animation animation = AnimationUtils.loadAnimation(context,R.anim.progressbar_anim);
        Glide.with(context)
                .asGif()
                .load(R.drawable.progress_bar)
                .into(back);

        String logo = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl);

        String name=session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME);
        appNameTextView.setText(name);

        if(logo==null || logo.isEmpty()){
            firstCharText.setText(String.valueOf(name.charAt(0)));
            firstCharText.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(R.drawable.studio_architecture)
                    .placeholder(ContextCompat.getDrawable(context,R.drawable.studio_architecture))
                    .into(logoImage);
        }
        else if(!logo.contains("http")){
            logo = "https://"+logo;
            Picasso.get()
                    .load(logo)
                    .placeholder(ContextCompat.getDrawable(context,R.drawable.studio_architecture))
                    .into(logoImage);
        }

        if(type.equalsIgnoreCase("android")){
            androidTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.android_green), null, null, null );
            androidTextView.setText(getResources().getString(R.string.android_app));
            previewButton.setText(getResources().getString(R.string.tips_for_business_app_completeness));
            congratesTextView.setText(getResources().getString(R.string.congratulation_android_business_app_development));
        }else{
            siteHealthButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.studio),null,null,null);
            androidTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ios_icon_blac), null, null, null );
            androidTextView.setText(getResources().getString(R.string.ios_app));
            previewButton.setText(getResources().getString(R.string.ios_app_preview));
            congratesTextView.setText(getResources().getString(R.string.congratulation_ios_business_app_development));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.preview_button:
                startActivity(new Intent(context,BusinessAppTipsActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.site_health:
                ((BusinessAppsDetailsActivity)context).addFragments(BusinessAppsDetailsActivity.SHOW_SITE_HEALTH);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final BusinessAppPreview frag= (BusinessAppPreview) getParentFragment();
        if(!isAdded()) return false;
        switch(item.getItemId()){
            case R.id.action_notif:
                Methods.materialDialog(getActivity(),"Send Push Notification","Inform your app users about your latest product offerings via push notifications. This feature is coming soon.");
                return true;
            case R.id.app_preview:
                frag.showScreenShots();
                return true;
            case R.id.about:
                ((BusinessAppsDetailsActivity)context).addFragments(BusinessAppsDetailsActivity.SHOW_ABOUT_APP);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.business_app,menu);
        MenuItem item = menu.findItem(R.id.app_preview);
        MenuItem about = menu.findItem(R.id.about);
        if(item != null) {
            item.setVisible(true);
        }
        if(pref != null){
            about.setVisible(pref.getInt(Key_Preferences.ABOUT_BUSINESS_APP,BIZ_APP_DEMO)>BIZ_APP_PAID);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

}
