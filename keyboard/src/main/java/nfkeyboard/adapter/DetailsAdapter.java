package nfkeyboard.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.separ.neural.inputmethod.indic.R;
import nfkeyboard.interface_contracts.ItemClickListener;
import nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Shimona on 07-06-2018.
 */

public class DetailsAdapter extends BaseAdapter<AllSuggestionModel> {

    DetailsAdapter(Context context, ItemClickListener listener) {
        super(context, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_details_business_card, parent, false);
        return new DetailsHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, AllSuggestionModel suggestion) {
        if (holder instanceof DetailsHolder) {
            DetailsHolder myHolder = (DetailsHolder) holder;
            myHolder.setModelData(suggestion);
        }
    }

    class DetailsHolder extends RecyclerView.ViewHolder {
        TextView nameTv, businessNameTv, websiteTv;
        Button shareButton;
        AllSuggestionModel dataModel;

        public DetailsHolder(View itemView) {
            super(itemView);
            linLayoutParams.setMargins(metrics.widthPixels * 12 / 140, topSpace, 0, topSpace);
            itemView.setLayoutParams(linLayoutParams);
            nameTv = itemView.findViewById(R.id.tv_name);
            businessNameTv = itemView.findViewById(R.id.tv_business_name);
            websiteTv = itemView.findViewById(R.id.tv_website);
            shareButton = itemView.findViewById(R.id.btn_share);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClicked(dataModel);
                }
            });
        }

        void setModelData(AllSuggestionModel model) {
            dataModel = model;
            nameTv.setText(model.getName());
            businessNameTv.setText(model.getBusinessName());
            websiteTv.setText(model.getWebsite());
        }
    }

}
