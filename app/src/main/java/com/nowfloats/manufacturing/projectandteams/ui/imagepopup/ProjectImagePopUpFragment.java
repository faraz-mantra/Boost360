package com.nowfloats.manufacturing.projectandteams.ui.imagepopup;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nowfloats.manufacturing.projectandteams.adapter.ProjectImagePreviewAdapter;
import com.thinksity.R;

import java.util.ArrayList;

public class ProjectImagePopUpFragment extends DialogFragment {

    ImageView closeButton, backButton, nextButton;
    private ArrayList<String> imageList;
    int currentPos = 0;
    ViewPager2 viewPager;
    boolean initialLoad = true;
    ProjectImagePreviewAdapter adapter;

    public static ProjectImagePopUpFragment newInstance() {
        return new ProjectImagePopUpFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setLayout(width,height);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.fullscreen_color);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.project_image_pop_up_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            imageList = getArguments().getStringArrayList("imageList");
            currentPos = getArguments().getInt("pos");
        }

        viewPager = view.findViewById(R.id.preview_pager);
        adapter = new ProjectImagePreviewAdapter(imageList);

        closeButton = view.findViewById(R.id.close_button);
        backButton = view.findViewById(R.id.back_button);
        nextButton = view.findViewById(R.id.next_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPos >= 1) {
                viewPager.setCurrentItem(currentPos - 1);
            }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPos < imageList.size()) {
                viewPager.setCurrentItem(currentPos + 1);
            }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        initializeViewPager();

    }

    private void initializeViewPager(){
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(imageList.size());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(!initialLoad) {
                    currentPosition(position);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                initialLoad = false;
                viewPager.setCurrentItem(currentPos);
            }
        }, 100);
    }

    private void currentPosition(int pos){
        currentPos = pos;
    }

}