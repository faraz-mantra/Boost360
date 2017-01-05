package com.nowfloats.NavigationDrawer.businessApps;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.Login.UserSessionManager;
import com.thinksity.R;

/**
 * Created by Abhi on 12/12/2016.
 */

public class BusinessAppPreview extends Fragment {
    private static final String ANDROID = "android",IOS="ios";
    Context context;
    public final static int SHOW_STUDIO=0,SHOW_DEVELOPMENT=1,SHOW_COMPLETE=2;
    private UserSessionManager session;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_business_app_preview,container,false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    public void addAndroidFragment(int id){
        Fragment frag;
        FragmentTransaction transaction=getChildFragmentManager().beginTransaction();
        switch (id){
            case SHOW_STUDIO:
                frag = BusinessAppStudio.getInstance(ANDROID);
                transaction.add(R.id.card_view_android,frag,"studio");
                break;
            case SHOW_DEVELOPMENT:
                frag = BusinessAppDevelopment.getInstance(ANDROID);
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.replace(R.id.card_view_android,frag,"development");
                break;
            case SHOW_COMPLETE:
                frag = BusinessAppCompleteFragment.getInstance(ANDROID);
                transaction.replace(R.id.card_view_android,frag,"complete");
                break;
            default:
                break;
        }
        transaction.commit();
    }
    public void addIosFragment(int id){
        Fragment frag;
        FragmentTransaction transaction=getChildFragmentManager().beginTransaction();
        switch (id){
            case SHOW_STUDIO:
                frag = BusinessAppStudio.getInstance(IOS);
                transaction.add(R.id.card_view_ios,frag,"studio");
                break;
            case SHOW_DEVELOPMENT:
                frag = BusinessAppDevelopment.getInstance(IOS);
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.replace(R.id.card_view_ios,frag,"development");
                break;
            case SHOW_COMPLETE:
                frag = BusinessAppCompleteFragment.getInstance(IOS);
                transaction.replace(R.id.card_view_ios,frag,"complete");
                break;
            default:
                break;
        }
        transaction.commit();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addAndroidFragment(SHOW_STUDIO);
        addIosFragment(SHOW_STUDIO);
    }
}
