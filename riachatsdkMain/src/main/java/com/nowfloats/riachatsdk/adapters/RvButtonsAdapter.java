package com.nowfloats.riachatsdk.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.riachatsdk.ChatManager;
import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.models.Button;
import com.nowfloats.riachatsdk.utils.Utils;

import java.util.List;


/**
 * Created by NowFloats on 16-03-2017.
 */

public class RvButtonsAdapter extends RecyclerView.Adapter<RvButtonsAdapter.ButtonViewHolder> {

    private List<Button> mButtonList;
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;
    private ChatManager.ChatType chatType;

    public RvButtonsAdapter(Context mContext, List<Button> mButtonList, ChatManager.ChatType chatType) {
        this.mContext = mContext;
        this.mButtonList = mButtonList;
        this.chatType = chatType;
    }

    @Override
    public ButtonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (chatType) {
            case CREATE_WEBSITE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_item_layout, parent, false);
                break;
            case FEEDBACK:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_item_layout_feedback, parent, false);
                break;
        }
        return new ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ButtonViewHolder holder, final int position) {
        Button btn = mButtonList.get(position);
        holder.tvButtonText.setText(btn.getButtonText());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnItemClickListener) {
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mButtonList.size();
    }

    public class ButtonViewHolder extends RecyclerView.ViewHolder {

        TextView tvButtonText;
        View view;

        public ButtonViewHolder(View itemView) {
            super(itemView);
            tvButtonText = (TextView) itemView.findViewById(R.id.tv_btn_text);
            view = itemView;
        }
    }

    public void setOnCItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void notifyDataSetChangedRequest(ChatManager.ChatType chatType,
                                            RecyclerView recyclerview,
                                            RvButtonsAdapter myAdapter,
                                            RecyclerView.LayoutManager myLayoutManager) {

        switch (chatType) {
            case CREATE_WEBSITE:
                myAdapter.notifyDataSetChanged();
                break;
            case FEEDBACK:
                recyclerview.setAdapter(null);
                recyclerview.setLayoutManager(null);
                recyclerview.setAdapter(myAdapter);

                int maxHeight = Utils.dpToPx(mContext, (45 * 4 + 50));

                if (mButtonList != null && mButtonList.size() > 0) {

                    int dummyMaxHeight = Utils.dpToPx(mContext, (45 * mButtonList.size() + 25));

                    if (dummyMaxHeight < maxHeight) {
                        maxHeight = dummyMaxHeight;
                    }
                }

                LinearLayout.LayoutParams relLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, maxHeight);
                recyclerview.setLayoutParams(relLayoutParams);

                recyclerview.setLayoutManager(myLayoutManager);
                myAdapter.notifyDataSetChanged();
                recyclerview.scrollToPosition(0);
                break;
        }

    }
}
