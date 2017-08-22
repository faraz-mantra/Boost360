package com.nowfloats.sam.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.sam.SAMCustomerListActivity;
import com.nowfloats.sam.models.SuggestionsDO;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by guru
 */

public class SAMCustomersAdapter extends RecyclerView.Adapter<SAMCustomersAdapter.ViewHolder> {


    private Context mContext;

    private ArrayList<SuggestionsDO> arrSuggestionsDOs;

    private Typeface hebrewFont,hebrewFontBold;

    public SAMCustomersAdapter(Context mContext, ArrayList<SuggestionsDO> arrSuggestionsDOs) {
        this.mContext = mContext;
        this.arrSuggestionsDOs = arrSuggestionsDOs;

        hebrewFont = Typeface.createFromAsset(mContext.getAssets(),"OpenSansHebrew-Regular.ttf");
        hebrewFontBold = Typeface.createFromAsset(mContext.getAssets(),"OpenSansHebrew-Bold.ttf");

    }

    @Override
    public SAMCustomersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sam_customer_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SAMCustomersAdapter.ViewHolder viewHolder, int position) {

        SuggestionsDO mSuggestionsDO = arrSuggestionsDOs.get(position);


        if (position == 0) {
            viewHolder.itemView.setPadding(30, 100, 30, 0);
        } else {
            viewHolder.itemView.setPadding(30, 30, 30, 0);
        }

        if (position % 2 == 0) {
            viewHolder.tvSource.setText("JU");
        } else {
            viewHolder.tvSource.setText("IN");
        }

        viewHolder.tvSource.setTypeface(hebrewFontBold);
        viewHolder.tvValue.setTypeface(hebrewFont);
        viewHolder.tvMessage.setTypeface(hebrewFont);

        viewHolder.tvValue.setText("Pawan ("+mSuggestionsDO.getValue()+")");
        viewHolder.tvMessage.setText(mSuggestionsDO.getActualMessage());

        viewHolder.itemView.setTag(mSuggestionsDO);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SAMCustomerListActivity) mContext).onCustomerSelection((SuggestionsDO) viewHolder.itemView.getTag());
            }
        });
    }

    public void refreshDetails(ArrayList<SuggestionsDO> arrSuggestionsDOs){
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

        TextView tvSource, tvValue, tvMessage;
        View itemView;


        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            tvSource = (TextView) itemView.findViewById(R.id.tvSource);
            tvValue = (TextView) itemView.findViewById(R.id.tvValue);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
        }
    }
}