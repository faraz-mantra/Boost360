package nfkeyboard.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.separ.neural.inputmethod.indic.R;
import nfkeyboard.interface_contracts.ItemClickListener;
import nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Admin on 04-03-2018.
 */

class LoaderAdapter extends BaseAdapter<AllSuggestionModel> {

    LoaderAdapter(Context context, ItemClickListener listener) {
        super(context, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_loader, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, AllSuggestionModel suggestion) {
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public MyHolder(View itemView) {
            super(itemView);
            setViewLayoutSize(itemView);
        }
    }
}
