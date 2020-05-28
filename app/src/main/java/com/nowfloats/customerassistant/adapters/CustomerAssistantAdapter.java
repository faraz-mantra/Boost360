package com.nowfloats.customerassistant.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.customerassistant.CustomerAssistantActivity;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by guru
 */

public class CustomerAssistantAdapter extends RecyclerView.Adapter<CustomerAssistantAdapter.ViewHolder> {


    private Context mContext;

    private ArrayList<SuggestionsDO> arrSuggestionsDOs;

    public CustomerAssistantAdapter(Context mContext, ArrayList<SuggestionsDO> arrSuggestionsDOs) {
        this.mContext = mContext;
        this.arrSuggestionsDOs = arrSuggestionsDOs;
    }

    @Override
    public CustomerAssistantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ca_customer_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CustomerAssistantAdapter.ViewHolder viewHolder, int position) {

        SuggestionsDO mSuggestionsDO = arrSuggestionsDOs.get(position);

        viewHolder.tvSource.setText(mSuggestionsDO.getShortText());

        viewHolder.tvValue.setText(mSuggestionsDO.getValue());
        viewHolder.tvMessage.setText(mSuggestionsDO.getActualMessage());
        viewHolder.tvExpiryDate.setText("(expires on " + Methods.getFormattedDate(mSuggestionsDO.getExpiryDate()) + ")");

        viewHolder.itemView.setTag(mSuggestionsDO);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CustomerAssistantActivity) mContext).onCustomerSelection((SuggestionsDO) viewHolder.itemView.getTag());
            }
        });
    }

    public void refreshDetails(ArrayList<SuggestionsDO> arrSuggestionsDOs) {
        this.arrSuggestionsDOs = arrSuggestionsDOs;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (arrSuggestionsDOs != null && arrSuggestionsDOs.size() > 0)
            return arrSuggestionsDOs.size();
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvSource, tvValue, tvMessage, tvExpiryDate;
        View itemView;


        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            tvSource = (TextView) itemView.findViewById(R.id.tvSource);
            tvValue = (TextView) itemView.findViewById(R.id.tvValue);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            tvExpiryDate = (TextView) itemView.findViewById(R.id.tvExpiryDate);
        }
    }
}