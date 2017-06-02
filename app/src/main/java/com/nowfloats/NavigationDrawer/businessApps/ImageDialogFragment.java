package com.nowfloats.NavigationDrawer.businessApps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nowfloats.NavigationDrawer.Adapter.ScreenShotsAdapter;
import com.thinksity.R;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

/**
 * Created by Admin on 01-02-2017.
 */

public class ImageDialogFragment extends DialogFragment {

    private ArrayList<String> screenShots;

    public static ImageDialogFragment getInstance(ArrayList<String> list){
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("list",list);
        ImageDialogFragment frag = new ImageDialogFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            screenShots = getArguments().getStringArrayList("list");
            //Log.v("ggg",screenShots.size()+"");
        }
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(container == null) {
            Log.v("ggg","null");
        }else if(container instanceof LinearLayout){
            Log.v("ggg","linear");
        }else if(container instanceof RelativeLayout){
            Log.v("ggg","relative");
        }
        View v = inflater.inflate(R.layout.dialog_images,container,false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ViewPager pager = (ViewPager) view.findViewById(R.id.viewpager);
        ScreenShotsAdapter adapter = new ScreenShotsAdapter(getChildFragmentManager(),screenShots);
        CirclePageIndicator mIndicator = (CirclePageIndicator) view.findViewById(R.id.ps_indicator);
        pager.setAdapter(adapter);
        mIndicator.setFillColor(R.color.white);
        mIndicator.setViewPager(pager);
    }
}
