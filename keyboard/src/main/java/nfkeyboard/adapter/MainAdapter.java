package nfkeyboard.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

import nfkeyboard.interface_contracts.ItemClickListener;
import nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Admin on 23-02-2018.
 */

public class MainAdapter extends RecyclerView.Adapter {

    private BaseAdapterManager baseAdapterManager;
    private RecyclerView mRecyclerView;
    private ArrayList<AllSuggestionModel> mSuggestionModels = new ArrayList<>();
    ;

    public MainAdapter(Context context, ItemClickListener listener) {
        baseAdapterManager = new BaseAdapterManager(context, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return baseAdapterManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        baseAdapterManager.onBindViewHolder(holder, mSuggestionModels.get(position), getItemViewType(position));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        return mSuggestionModels.get(position).getTypeEnum().getValue();
    }

    public void setSuggestionModels(final ArrayList<AllSuggestionModel> models) {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mSuggestionModels.clear();
                if (models != null) {
                    mSuggestionModels.addAll(models);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSuggestionModels.size();
    }

    public void setLoginScreen(final AllSuggestionModel model) {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mSuggestionModels.clear();
                mSuggestionModels.add(model);
                notifyDataSetChanged();
            }
        });

    }

    public void unRegisterEventBus() {
        baseAdapterManager.unRegisterEventBus();
    }


}
