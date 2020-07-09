package com.nowfloats.manufacturing.projectandteams.ui.projectandteamsdetails;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksity.R;

public class ProjectAndTeamsDetailsFragment extends Fragment {

    private ProjectAndTeamsDetailsViewModel mViewModel;

    public static ProjectAndTeamsDetailsFragment newInstance() {
        return new ProjectAndTeamsDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(ProjectAndTeamsDetailsViewModel.class);
        return inflater.inflate(R.layout.project_and_teams_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}