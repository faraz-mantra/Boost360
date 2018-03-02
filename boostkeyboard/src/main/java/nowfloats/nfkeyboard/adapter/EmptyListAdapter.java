package nowfloats.nfkeyboard.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nowfloats.nfkeyboard.R;
import nowfloats.nfkeyboard.interface_contracts.ItemClickListener;
import nowfloats.nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Admin on 01-03-2018.
 */

public class EmptyListAdapter extends BaseAdapter<AllSuggestionModel> {

    EmptyListAdapter(Context context, ItemClickListener listener) {
        super(context,listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_text,parent,false);
        return new EmptyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, AllSuggestionModel suggestion) {
        if (holder instanceof EmptyHolder){
            EmptyHolder myHolder = (EmptyHolder) holder;
            myHolder.setModelData(suggestion);
        }
    }

    class EmptyHolder extends RecyclerView.ViewHolder{
        TextView suggestionTv;
        AllSuggestionModel dataModel;
        public EmptyHolder(View itemView) {
            super(itemView);
            linLayoutParams.setMargins(leftSpace+leftSpace/2, topSpace, leftSpace, topSpace);
            itemView.setLayoutParams(linLayoutParams);
            itemView.setPadding(padding, paddingTop, padding, padding);
            suggestionTv = itemView.findViewById(R.id.textView);
        }

        void setModelData(AllSuggestionModel model){
            dataModel = model;
            suggestionTv.setText(model.getText());
        }
    }
}