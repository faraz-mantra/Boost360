package com.nowfloats.manufacturing.projectandteams.ui.imagepopup;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thinksity.R;

public class ProjectImagePopUpFragment extends DialogFragment {

    ImageView closeButton, backButton, nextButton;

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

        closeButton = view.findViewById(R.id.close_button);
        backButton = view.findViewById(R.id.back_button);
        nextButton = view.findViewById(R.id.next_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}