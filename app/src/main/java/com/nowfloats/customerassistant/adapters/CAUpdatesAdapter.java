package com.nowfloats.customerassistant.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nowfloats.customerassistant.SuggestionSelectionListner;
import com.nowfloats.customerassistant.models.SugUpdates;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by guru
 */

public class CAUpdatesAdapter extends RecyclerView.Adapter<CAUpdatesAdapter.ViewHolder> {

    private ArrayList<SugUpdates> arrUpdates;

    private Context mContext;

    private SuggestionSelectionListner mSuggestionSelectionListner;
    private int MAX_LINE_COUNT = 3;

    public CAUpdatesAdapter(Context mContext,
                            ArrayList<SugUpdates> arrUpdates,
                            SuggestionSelectionListner mSuggestionSelectionListner) {
        this.mContext = mContext;
        this.arrUpdates = arrUpdates;
        this.mSuggestionSelectionListner = mSuggestionSelectionListner;
    }

    @Override
    public CAUpdatesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.updates_list_view_design, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CAUpdatesAdapter.ViewHolder viewHolder, int position) {

        final SugUpdates mSugUpdate = (SugUpdates) arrUpdates.get(position);

        viewHolder.tvUpdate.setText(mSugUpdate.getName());
        updateMaxLines(mSugUpdate, viewHolder.tvViewMore, viewHolder.tvUpdate);

        viewHolder.tvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSugUpdate.setViewMore(!mSugUpdate.isViewMore());
                updateMaxLines(mSugUpdate, (TextView) view, viewHolder.tvUpdate);
            }
        });

        viewHolder.itemView.setTag(R.string.key_details, mSugUpdate);
        viewHolder.cbUpdate.setTag(R.string.key_details, mSugUpdate);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateView(view);
            }
        });

        viewHolder.cbUpdate.setChecked(mSugUpdate.isSelected());
        viewHolder.cbUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateView(view);
            }
        });

    }

    private void updateView(View view) {
        final SugUpdates sugUpdates = (SugUpdates) view.getTag(R.string.key_details);
        sugUpdates.setSelected(!sugUpdates.isSelected());
        CheckBox cbUpdate = (CheckBox) view.findViewById(R.id.cbUpdate);
        cbUpdate.setChecked(sugUpdates.isSelected());
        mSuggestionSelectionListner.onSelection(sugUpdates.isSelected);
    }

    private void updateMaxLines(SugUpdates mSugUpdate, TextView tvViewMore, TextView tvUpdate) {

        if (mSugUpdate.isViewMore()) {
            tvViewMore.setText(Html.fromHtml(mContext.getString(R.string.view_more)));
            tvUpdate.setMaxLines(MAX_LINE_COUNT);

        } else {
            tvViewMore.setText(Html.fromHtml(mContext.getString(R.string.view_less)));
            tvUpdate.setMaxLines(100);

        }
    }

    public void setOverlay(View v, int opac, int width, int height) {
        int opacity = opac; // from 0 to 255
        v.setBackgroundColor(opacity * 0x1000000); // black with a variable alpha
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(width, height);
        params.gravity = Gravity.NO_GRAVITY;
        v.setLayoutParams(params);
        v.invalidate();
    }

    @Override
    public int getItemCount() {

        if (arrUpdates != null && arrUpdates.size() > 0) {
            return arrUpdates.size();
        }

        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUpdate, tvViewMore;
        View itemView;
        CheckBox cbUpdate;

        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            tvUpdate = (TextView) itemView.findViewById(R.id.tvUpdate);
            tvViewMore = (TextView) itemView.findViewById(R.id.tvViewMore);
            cbUpdate = (CheckBox) itemView.findViewById(R.id.cbUpdate);
        }
    }
}