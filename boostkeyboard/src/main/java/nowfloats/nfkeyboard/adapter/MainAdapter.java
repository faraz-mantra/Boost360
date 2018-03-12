package nowfloats.nfkeyboard.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

import nowfloats.nfkeyboard.interface_contracts.ItemClickListener;
import nowfloats.nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Admin on 23-02-2018.
 */

public class MainAdapter extends RecyclerView.Adapter {

    private BaseAdapterManager baseAdapterManager;
    private ArrayList<AllSuggestionModel> mSuggestionModels;
    public MainAdapter(Context context, ItemClickListener listener){
        baseAdapterManager = new BaseAdapterManager(context, listener);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return baseAdapterManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        baseAdapterManager.onBindViewHolder(holder,mSuggestionModels.get(position),getItemViewType(position));
    }

    @Override
    public int getItemViewType(int position) {
        return mSuggestionModels.get(position).getTypeEnum().getValue();
    }

    public void setSuggestionModels(ArrayList<AllSuggestionModel> models){
        if (mSuggestionModels == null){
            mSuggestionModels = new ArrayList<>();
        }
        mSuggestionModels.clear();
        mSuggestionModels.addAll(models);
    }
    @Override
    public int getItemCount() {
        return mSuggestionModels.size();
    }

}
