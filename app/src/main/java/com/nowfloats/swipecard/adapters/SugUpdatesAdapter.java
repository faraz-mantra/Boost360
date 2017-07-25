package com.nowfloats.swipecard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.swipecard.models.SugUpdates;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by guru
 */

public class SugUpdatesAdapter extends RecyclerView.Adapter<SugUpdatesAdapter.ViewHolder> {

    private ArrayList<SugUpdates> arrUpdates;

    private Context mContext;

    public SugUpdatesAdapter(Context mContext, ArrayList<SugUpdates> arrUpdates) {
        this.mContext = mContext;
        this.arrUpdates = arrUpdates;
    }

    private int MAX_LINE_COUNT = 3;

    @Override
    public SugUpdatesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.updates_list_view_design, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SugUpdatesAdapter.ViewHolder viewHolder, int position) {

        final SugUpdates mSugUpdate = (SugUpdates) arrUpdates.get(position);

        if (mSugUpdate.isSelected()) {
            viewHolder.flOverlay.setVisibility(View.VISIBLE);
        } else {
            viewHolder.flOverlay.setVisibility(View.GONE);
        }

        viewHolder.tvUpdate.setText(mSugUpdate.getName());
//        updateMaxLines(mSugUpdate, viewHolder.tvViewMore, viewHolder.tvUpdate);

        if (viewHolder.tvUpdate.getLineCount() > MAX_LINE_COUNT) {
            viewHolder.tvViewMore.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvViewMore.setVisibility(View.GONE);
        }


        viewHolder.tvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSugUpdate.setViewMore(!mSugUpdate.isViewMore());
                updateMaxLines(mSugUpdate, (TextView) view, viewHolder.tvUpdate);
            }
        });

        viewHolder.itemView.setTag(R.string.key_details, mSugUpdate);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SugUpdates sugUpdates = (SugUpdates) view.getTag(R.string.key_details);
                sugUpdates.setSelected(!sugUpdates.isSelected());
                FrameLayout flMain = (FrameLayout) view.findViewById(R.id.flMain);
                FrameLayout flOverlay = (FrameLayout) view.findViewById(R.id.flOverlay);
                View vwOverlay = view.findViewById(R.id.vwOverlay);
                if (sugUpdates.isSelected()) {
                    flOverlay.setVisibility(View.VISIBLE);
                    setOverlay(vwOverlay, 200, flMain.getWidth(), flMain.getHeight());
                } else {
                    flOverlay.setVisibility(View.GONE);
                }
            }
        });

        setOverlay(viewHolder.vwOverlay, 200, viewHolder.flMain.getWidth(), viewHolder.itemView.getHeight());
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

        ImageView ProductImageView;
        TextView tvUpdate, tvViewMore;
        FrameLayout flMain;
        FrameLayout flOverlay;
        View vwOverlay;
        View itemView;


        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            ProductImageView = (ImageView) itemView.findViewById(R.id.proudct_image_view);
            tvUpdate = (TextView) itemView.findViewById(R.id.tvUpdate);
            tvViewMore = (TextView) itemView.findViewById(R.id.tvViewMore);
            flMain = (FrameLayout) itemView.findViewById(R.id.flMain);
            flOverlay = (FrameLayout) itemView.findViewById(R.id.flOverlay);
            vwOverlay = (View) itemView.findViewById(R.id.vwOverlay);
        }
    }
}