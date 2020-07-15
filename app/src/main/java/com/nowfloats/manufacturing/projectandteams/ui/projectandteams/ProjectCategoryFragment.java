package com.nowfloats.manufacturing.projectandteams.ui.projectandteams;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.manufacturing.projectandteams.ProjectAndTermsActivity;
import com.nowfloats.manufacturing.projectandteams.adapter.ProjectAdapter;
import com.nowfloats.manufacturing.projectandteams.adapter.TeamAdapter;
import com.thinksity.R;

import java.util.ArrayList;

public class ProjectCategoryFragment extends Fragment {

    private ProjectCategoryViewModel mViewModel;
    private RecyclerView recyclerView;
    private ProjectAdapter adapter;

    public static ProjectCategoryFragment newInstance() {
        return new ProjectCategoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(ProjectCategoryViewModel.class);
        return inflater.inflate(R.layout.project_category_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler);
        adapter = new ProjectAdapter(new ArrayList(),(ProjectAndTermsActivity)requireActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setAdapter(adapter);
    }
}