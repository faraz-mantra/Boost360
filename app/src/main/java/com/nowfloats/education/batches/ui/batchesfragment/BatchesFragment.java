package com.nowfloats.education.batches.ui.batchesfragment;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thinksity.R;


public class BatchesFragment extends Fragment {

    private BatchesViewModel mViewModel;

    public static BatchesFragment newInstance() {
        return new BatchesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(BatchesViewModel.class);
        return inflater.inflate(R.layout.batches_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setheader
        setHeader(view);
    }

    public void setHeader(View view){
        LinearLayout rightButton,backButton;
        ImageView rightIcon;
        TextView title;

        title = view.findViewById(R.id.title);
        backButton = view.findViewById(R.id.back_button);
        rightButton = view.findViewById(R.id.right_icon_layout);
        rightIcon = view.findViewById(R.id.right_icon);
        title.setText("Upcoming Batches");
        rightIcon.setImageResource(R.drawable.ic_add_white);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }
}