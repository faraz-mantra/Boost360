package com.nowfloats.riachatsdk.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    private RelativeLayout rlButtons;
    private RecyclerView recyclerView;

    public RvButtonsAdapter(Context mContext, List<Button> mButtonList,
                            ChatManager.ChatType chatType, RelativeLayout rlButtons, RecyclerView recyclerView) {
        this.mContext = mContext;
        this.mButtonList = mButtonList;
        this.rlButtons = rlButtons;
        this.chatType = chatType;
        this.recyclerView = recyclerView;
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

    public void setOnCItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    public void notifyDataSetChangedRequest() {

        switch (chatType) {
            case CREATE_WEBSITE:
                this.notifyDataSetChanged();
                break;
            case FEEDBACK:

                RecyclerView.LayoutManager myLayoutManager = recyclerView.getLayoutManager();
                recyclerView.setAdapter(null);
                recyclerView.setLayoutManager(null);
                recyclerView.setAdapter(this);

                int maxHeight = Utils.dpToPx(mContext, (45 * 4 + 50));

                if (mButtonList != null && mButtonList.size() > 0) {

                    int dummyMaxHeight = Utils.dpToPx(mContext, (45 * mButtonList.size() + 25));

                    if (dummyMaxHeight < maxHeight) {
                        maxHeight = dummyMaxHeight;
                    }
                }

                LinearLayout.LayoutParams relLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, maxHeight);
                rlButtons.setLayoutParams(relLayoutParams);

                recyclerView.setLayoutManager(myLayoutManager);
                this.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
                break;
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
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
}
