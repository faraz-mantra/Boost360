package nfkeyboard.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.separ.neural.inputmethod.indic.R;
import nfkeyboard.interface_contracts.ItemClickListener;
import nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Admin on 23-02-2018.
 */

public class TextAdapter extends BaseAdapter<AllSuggestionModel> {

    TextAdapter(Context context, ItemClickListener listener) {
        super(context, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_text, parent, false);
        return new TextHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, AllSuggestionModel suggestion) {
        if (holder instanceof TextHolder) {
            TextHolder myHolder = (TextHolder) holder;
            myHolder.setModelData(suggestion);
        }
    }

    class TextHolder extends RecyclerView.ViewHolder {
        TextView suggestionTv;
        AllSuggestionModel dataModel;

        public TextHolder(View itemView) {
            super(itemView);
            setViewLayoutSize(itemView);
            suggestionTv = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClicked(dataModel);
                }
            });
        }

        void setModelData(AllSuggestionModel model) {
            dataModel = model;
            suggestionTv.setText(model.getText());
        }
    }
}
