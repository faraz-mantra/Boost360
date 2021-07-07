package com.nowfloats.Analytics_Screen.Fragments;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.thinksity.R;

public class LegendViewHolder extends RecyclerView.ViewHolder {

    public TextView tvColor, tvLabel;

    public LegendViewHolder(View itemView) {
        super(itemView);
        tvColor = (TextView) itemView.findViewById(R.id.tvColor);
        tvLabel = (TextView) itemView.findViewById(R.id.tvLabel);
    }
}
