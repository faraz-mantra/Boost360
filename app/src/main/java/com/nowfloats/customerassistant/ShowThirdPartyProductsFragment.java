package com.nowfloats.customerassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.customerassistant.adapters.ThirdPartySuggestionAdapter;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.thinksity.R;

/**
 * Created by Admin on 10-10-2017.
 */

public class ShowThirdPartyProductsFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private SuggestionsDO mSuggestionDO;
    public static Fragment getInstance(Bundle b){
        Fragment frag = new ShowThirdPartyProductsFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            Bundle b = getArguments();
            mSuggestionDO = (SuggestionsDO) b.getSerializable("message");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_third_party_products,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!isAdded()) return;

        RecyclerView suggestionsRecyclerView = (RecyclerView) view.findViewById(R.id.rv_suggestions);
        suggestionsRecyclerView.setHasFixedSize(true);
        suggestionsRecyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
        suggestionsRecyclerView.setAdapter(new ThirdPartySuggestionAdapter(mContext));
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_send:
                //((ThirdPartySuggestionDetailActivity.Callback)mContext).sendAddedSuggestions();
                break;
            default:
                break;
        }
    }


}
