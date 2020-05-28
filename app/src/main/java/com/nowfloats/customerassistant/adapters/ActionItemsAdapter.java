package com.nowfloats.customerassistant.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.customerassistant.SuggestionsActivity;
import com.nowfloats.customerassistant.models.ActionItemsDO;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by admin on 5/31/2017.
 */

public class ActionItemsAdapter extends RecyclerView.Adapter<ActionItemsAdapter.ActionViewHolder> {


    private ArrayList<ActionItemsDO> actionItemsList;

    private Context mContext;

    public ActionItemsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.csp_fragment_action_items_list, parent, false);
        ActionViewHolder actionViewHolder = new ActionViewHolder(view);
        return actionViewHolder;
    }

    public void refreshList(ArrayList<ActionItemsDO> actionItemsList) {
        this.actionItemsList = actionItemsList;
    }

    @Override
    public void onBindViewHolder(ActionViewHolder holder, final int position) {

        holder.tvActionItemName.setText(actionItemsList.get(position).getActionItemName());
        holder.tvActionItemCount.setText(actionItemsList.get(position).getActionItemCount() + "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (actionItemsList.get(position).getActionItemCount() > 0) {
                    ((SuggestionsActivity) mContext).
                            switchView(SuggestionsActivity.SwitchView.CALL_TO_ACTION);

                }
            }
        });
    }

    @Override
    public int getItemCount() {

        if (actionItemsList == null || actionItemsList.size() == 0)
            return 0;
        return actionItemsList.size();
    }

    public class ActionViewHolder extends RecyclerView.ViewHolder {

        private TextView tvActionItemName;
        private TextView tvActionItemCount;

        public ActionViewHolder(View itemView) {
            super(itemView);
            tvActionItemName = (TextView) itemView.findViewById(R.id.tvActionItemName);
            tvActionItemCount = (TextView) itemView.findViewById(R.id.tvActionItemCount);
        }
    }
}
