package com.nowfloats.swipecard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nowfloats.swipecard.adapters.ActionItemsAdapter;
import com.nowfloats.swipecard.models.ActionItemsDO;
import com.nowfloats.swipecard.models.SuggestionsDO;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;


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

        rvList = (RecyclerView) view.findViewById(R.id.rvList);
        pbView = (ProgressBar) view.findViewById(R.id.pbView);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        actionItemsAdapter = new ActionItemsAdapter(getActivity());
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(actionItemsAdapter);

        pbView.setVisibility(View.VISIBLE);

        ((SuggestionsActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("Action Items");
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));
    }

    public void filterData() {

        pbView.setVisibility(View.GONE);

        ArrayList<ActionItemsDO> actionItemsDOList = new ArrayList<>();

        int callCount = 0, emailCount = 0;

        if (((SuggestionsActivity) getActivity()).smsSuggestions != null) {
            List<SuggestionsDO> smSuggestions = ((SuggestionsActivity) getActivity()).smsSuggestions.getSuggestionList();

            if (smSuggestions != null) {
                for (SuggestionsDO mSuggestionsDO : smSuggestions) {
                    if (mSuggestionsDO.getType().equalsIgnoreCase("contactNumber")) {
                        callCount++;
                    } else if (mSuggestionsDO.getType().equalsIgnoreCase("email")) {
                        emailCount++;
                    }
                }
            }
        }


        ActionItemsDO caActionItemsDO = new ActionItemsDO();
        caActionItemsDO.setActionItemName("Calls To Make");
        caActionItemsDO.setActionItemCount(callCount);

        ActionItemsDO emActionItemsDO = new ActionItemsDO();
        emActionItemsDO.setActionItemName("Emails To Sent");
        emActionItemsDO.setActionItemCount(emailCount);

        actionItemsDOList.add(caActionItemsDO);
        actionItemsDOList.add(emActionItemsDO);

        actionItemsAdapter.refreshList(actionItemsDOList);
    }

}
