package com.nowfloats.sam.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.sam.SAMCustomerListActivity;
import com.nowfloats.sam.models.SugUpdates;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by guru
 */

public class SAMCustomersAdapter extends RecyclerView.Adapter<SAMCustomersAdapter.ViewHolder> {


    private Context mContext;

    public SAMCustomersAdapter(Context mContext, ArrayList<SugUpdates> arrUpdates) {
        this.mContext = mContext;
    }

    @Override
    public SAMCustomersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sam_customer_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SAMCustomersAdapter.ViewHolder viewHolder, int position) {

        if (position == 0) {
            viewHolder.itemView.setPadding(30, 100, 30, 0);
        } else {
            viewHolder.itemView.setPadding(30, 30, 30, 0);
        }

        if (position % 2 == 0) {
            viewHolder.tvSource.setText("J");
        } else {
            viewHolder.tvSource.setText("I");
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SAMCustomerListActivity) mContext).onCustomerSelection();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 100;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvSource;
        View itemView;


        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            tvSource = (TextView) itemView.findViewById(R.id.tvSource);
        }
    }
}