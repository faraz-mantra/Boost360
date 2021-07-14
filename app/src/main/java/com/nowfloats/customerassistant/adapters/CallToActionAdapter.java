package com.nowfloats.customerassistant.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.customerassistant.CallToActionFragment;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 5/31/2017.
 */

public class CallToActionAdapter extends RecyclerView.Adapter<CallToActionAdapter.ActionViewHolder> {


    private ArrayList<SuggestionsDO> arrSuggestions;
    private CallToActionFragment callToActionFragment;

    public CallToActionAdapter(CallToActionFragment callToActionFragment) {
        this.callToActionFragment = callToActionFragment;
    }

    @Override
    public ActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.csp_fragment_cta_list_item_v1, parent, false);
        ActionViewHolder actionViewHolder = new ActionViewHolder(view);
        return actionViewHolder;
    }

    public void refreshList(List<SuggestionsDO> arrSuggestions) {
        this.arrSuggestions = (ArrayList<SuggestionsDO>) arrSuggestions;
    }

    @Override
    public void onBindViewHolder(final ActionViewHolder holder, int position) {

        final SuggestionsDO suggestionsDO = arrSuggestions.get(position);

        holder.tvSource.setText(suggestionsDO.getSource());
        holder.tvActualMessage.setText("'" + suggestionsDO.getActualMessage() + "'");
        holder.tvDate.setText(Methods.getFormattedDate(suggestionsDO.getDate()));
        holder.tvValue.setText(suggestionsDO.getValue());

        holder.llMessage.setVisibility(View.VISIBLE);

        holder.tvCTA.setTag(suggestionsDO);

        holder.tvCTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callToActionFragment.performAction((SuggestionsDO) holder.tvCTA.getTag());
            }
        });

    }


    @Override
    public int getItemCount() {

        if (arrSuggestions == null || arrSuggestions.size() == 0)
            return 0;
        return arrSuggestions.size();
    }

    public class ActionViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSource, tvActualMessage,
                tvDate, tvValue, tvCTA;
        private LinearLayout llMessage, llValue;

        public ActionViewHolder(View itemView) {
            super(itemView);
            tvSource = (TextView) itemView.findViewById(R.id.tvSource);
            tvActualMessage = (TextView) itemView.findViewById(R.id.tvActualMessage);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvValue = (TextView) itemView.findViewById(R.id.tvValue);
            llValue = (LinearLayout) itemView.findViewById(R.id.llValue);
            tvCTA = (TextView) itemView.findViewById(R.id.tvCTA);
            llMessage = (LinearLayout) itemView.findViewById(R.id.llMessage);
        }
    }
}
