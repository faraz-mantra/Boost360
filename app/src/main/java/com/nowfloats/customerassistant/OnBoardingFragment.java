package com.nowfloats.customerassistant;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.thinksity.R;


/**
 * Created by admin on 5/31/2017.
 */

public class OnBoardingFragment extends android.app.Fragment {

    private LayoutInflater mLayoutInflater;

    private LinearLayout llGetStarted;

    private ImageView ivCA;

    public OnBoardingFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.csp_on_board_1, null);
        this.mLayoutInflater = inflater;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llGetStarted = (LinearLayout) view.findViewById(R.id.llGetStarted);
        ivCA = (ImageView) view.findViewById(R.id.ivCA);
        DisplayMetrics mDisplayMetrics = getActivity().getResources().getDisplayMetrics();

        int width = (int) (mDisplayMetrics.widthPixels * 0.5);
        int height = (int) (width * 0.925);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(0, 50, 0, 0);
        ivCA.setLayoutParams(layoutParams);

        llGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CustomerAssistantActivity) getActivity()).loadCallToActionItems();
            }
        });
    }
}
