package com.nowfloats.customerassistant;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.customerassistant.adapters.ThirdPartySuggestionAdapter;
import com.nowfloats.customerassistant.callbacks.ThirdPartyCallbacks;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.thinksity.R;

import java.util.ArrayList;

import static com.nowfloats.customerassistant.ThirdPartySuggestionDetailActivity.ADD_PRODUCTS;
import static com.nowfloats.customerassistant.ThirdPartySuggestionDetailActivity.ADD_UPDATES;
import static com.nowfloats.customerassistant.ThirdPartySuggestionDetailActivity.SHOW_MESSAGE;

/**
 * Created by Admin on 10-10-2017.
 */

public class ShowThirdPartyProductsFragment extends Fragment implements View.OnClickListener,ThirdPartySuggestionAdapter.ThirdPartyFragment {

    private Context mContext;
    private SuggestionsDO mSuggestionDO;
    int type = -1;
    TextView addProductBtn;
    ThirdPartySuggestionAdapter adapter;
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
            type = b.getInt("type");
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

        RecyclerView suggestionsRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);
        suggestionsRecyclerView.setHasFixedSize(true);
        addProductBtn = (TextView) view.findViewById(R.id.btn_send);
        TextView messageTv = view.findViewById(R.id.tv_message);
        switch (type){
            case ADD_UPDATES:
                suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                adapter = new ThirdPartySuggestionAdapter(mContext, ThirdPartySuggestionAdapter.ListType.UPDATES);
                adapter.setUpdateList(mSuggestionDO.getUpdates(),this);
                addProductBtn.setText(R.string.add_updates);
                messageTv.setText(R.string.select_update_to_share);
                break;
            case ADD_PRODUCTS:
                suggestionsRecyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
                adapter = new ThirdPartySuggestionAdapter(mContext,ThirdPartySuggestionAdapter.ListType.PRODUCTS);
                adapter.setProductList(mSuggestionDO.getProducts(),this);
                addProductBtn.setText(R.string.select_products_to_share);
                break;
        }

        suggestionsRecyclerView.setAdapter(adapter);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        addProductBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_send:
                adapter.sendSuggestions(type);
                break;
            case R.id.btn_cancel:
                ((ThirdPartyCallbacks) mContext).addSuggestions(SHOW_MESSAGE,new ArrayList<Integer>());
                break;
            default:
                break;
        }
    }


    @Override
    public void itemSelected(int type,int num) {
        if(type == ADD_PRODUCTS) {
            addProductBtn.setText("Add Products " + String.valueOf(num > 0 ? "(" + num + ")" : ""));
        }else{
            addProductBtn.setText("Add Updates " + String.valueOf(num > 0 ? "(" + num + ")" : ""));
        }
    }
}
