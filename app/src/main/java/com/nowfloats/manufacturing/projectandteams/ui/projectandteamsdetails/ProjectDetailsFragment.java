package com.nowfloats.manufacturing.projectandteams.ui.projectandteamsdetails;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.manufacturing.projectandteams.ProjectAndTermsActivity;
import com.nowfloats.manufacturing.projectandteams.adapter.ProjectDetailsImageAdapter;
import com.nowfloats.manufacturing.projectandteams.adapter.ProjectImageAdapter;
import com.thinksity.R;

import java.util.ArrayList;

public class ProjectDetailsFragment extends Fragment {

    private ProjectDetailsViewModel mViewModel;
    RecyclerView recyclerView;

    public static ProjectDetailsFragment newInstance() {
        return new ProjectDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(ProjectDetailsViewModel.class);
        return inflater.inflate(R.layout.project_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setheader
        setHeader(view);

        recyclerView = view.findViewById(R.id.image_recycler);


        ProjectDetailsImageAdapter adapter = new ProjectDetailsImageAdapter(new ArrayList());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

    }

    public void setHeader(View view){
        LinearLayout rightButton,backButton;
        ImageView rightIcon;
        TextView title;

        title = view.findViewById(R.id.title);
        backButton = view.findViewById(R.id.back_button);
        rightButton = view.findViewById(R.id.right_icon_layout);
        rightIcon = view.findViewById(R.id.right_icon);
        title.setText("Project Details");
        rightIcon.setImageResource(R.drawable.ic_delete_white_outerline);
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