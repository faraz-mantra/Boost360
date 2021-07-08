package com.nowfloats.NavigationDrawer.businessApps;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public static ImageDialogFragment getInstance(ArrayList<String> list) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("list", list);
        ImageDialogFragment frag = new ImageDialogFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            screenShots = getArguments().getStringArrayList("list");
            //Log.v("ggg",screenShots.size()+"");
        }
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog_NoActionBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            Log.v("ggg", "null");
        } else if (container instanceof LinearLayout) {
            Log.v("ggg", "linear");
        } else if (container instanceof RelativeLayout) {
            Log.v("ggg", "relative");
        }
        return inflater.inflate(R.layout.dialog_images, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ViewPager pager = (ViewPager) view.findViewById(R.id.viewpager);
        ImageView cancel = (ImageView) view.findViewById(R.id.cancel);
        ScreenShotsAdapter adapter = new ScreenShotsAdapter(getChildFragmentManager(), screenShots);
        CirclePageIndicator mIndicator = (CirclePageIndicator) view.findViewById(R.id.ps_indicator);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusinessAppPreview frag = (BusinessAppPreview) getParentFragment();
                if (frag != null) {
                    frag.hideImageDialog();
                }
            }
        });
        pager.setAdapter(adapter);
        mIndicator.setPageColor(R.color.background_grey_onclick);
        mIndicator.setStrokeWidth(0);
        mIndicator.setStrokeColor(R.color.white);
        mIndicator.setFillColor(R.color.business_button_black);
        mIndicator.setRadius(8);
        mIndicator.setViewPager(pager);
    }
}
