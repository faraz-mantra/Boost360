package com.nowfloats.Analytics_Screen;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.nowfloats.Analytics_Screen.API.SearchQueryApi;
import com.nowfloats.Analytics_Screen.model.SearchAnalyticsSummaryForFP;
import com.nowfloats.Analytics_Screen.model.SearchReportModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchReportFragment extends Fragment {
    private Context mContext;
    private UserSessionManager mSession;
    List<SearchAnalyticsSummaryForFP> overViewSearchReport = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_search_report, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded()) {
            return;
        }
        getSearch();
        ArrayList<ArrayList<String>> childList = new ArrayList<>(3);
        ArrayList<SearchReportModel> parentList = new ArrayList<>();
        //parentList.add(new SearchReportModel(SearchReportModel.OVERVIEW_TYPE,))

        ExpandableListView expandableListView = view.findViewById(R.id.info_exlv);
        //expandableListView.setAdapter(new TextExpandableAdapter(mContext, childList, parentList));
        expandableListView.expandGroup(0);
    }

    private void getSearch() {
        HashMap<String, String> map = new HashMap<>();

        mSession = new UserSessionManager(mContext, requireActivity());
        //map.put("fpTag", mSession.getFpTag());
        map.put("fpTag", "TESTFP");

        SearchQueryApi searchQueryApi = Constants.restAdapter.create(SearchQueryApi.class);
        searchQueryApi.GetSearchAnalyticsSummaryForFP("TESTFP", new Callback<SearchAnalyticsSummaryForFP>() {
            @Override
            public void success(SearchAnalyticsSummaryForFP searchAnalyticsSummaryForFP, Response response) {
                overViewSearchReport.add(searchAnalyticsSummaryForFP);
            }

            @Override
            public void failure(RetrofitError error) {
                Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong_try_again));
            }
        });
    }
}
