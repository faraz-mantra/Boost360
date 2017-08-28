package com.nowfloats.customerassistant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nowfloats.customerassistant.adapters.ActionItemsAdapter;
import com.thinksity.R;


/**
 * Created by admin on 5/31/2017.
 */

public class ActionItemsFragment extends Fragment {


    private RecyclerView rvList;

    private ActionItemsAdapter actionItemsAdapter;

    public ProgressBar pbView;

    public ActionItemsFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.csp_fragment_action_items, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvList = (RecyclerView) view.findViewById(R.id.rvActionItems);
        pbView = (ProgressBar) view.findViewById(R.id.pbView);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        actionItemsAdapter = new ActionItemsAdapter(getActivity());
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(actionItemsAdapter);

        pbView.setVisibility(View.VISIBLE);

        ((SuggestionsActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle(Html.fromHtml("Sam <i>says..</i>"));
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));
    }

}
