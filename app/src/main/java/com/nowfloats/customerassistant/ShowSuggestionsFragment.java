package com.nowfloats.customerassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.nowfloats.util.Methods;
import com.thinksity.R;

/**
 * Created by Admin on 10-10-2017.
 */

public class ShowSuggestionsFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private SuggestionsDO mSuggestionDO;
    public static Fragment getInstance(Bundle b){
        Fragment frag = new ShowSuggestionsFragment();
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
        return inflater.inflate(R.layout.fragment_third_party_response,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!isAdded()) return;
        TextView addressText = (TextView) view.findViewById(R.id.tv_address);
        TextView timeText = (TextView) view.findViewById(R.id.tv_time);
        timeText.setText(Methods.getFormattedDate(mSuggestionDO.getDate()));
        view.findViewById(R.id.btn_add_updates).setOnClickListener(this);
        view.findViewById(R.id.btn_add_products).setOnClickListener(this);
        view.findViewById(R.id.btn_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_send:
                break;
            case R.id.btn_add_products:
                break;
            case R.id.btn_add_updates:
                //((ThirdPartySuggestionDetailActivity)mContext).
                break;
            case R.id.btn_call:
                //Methods.makeCall(mContext,mSuggestionDO.get);
                break;
        }
    }

}
