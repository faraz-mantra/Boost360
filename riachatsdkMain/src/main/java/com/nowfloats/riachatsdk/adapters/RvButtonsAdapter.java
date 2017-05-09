package com.nowfloats.riachatsdk.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.models.Button;

import java.util.List;


/**
 * Created by NowFloats on 16-03-2017.
 */

public class RvButtonsAdapter extends RecyclerView.Adapter<RvButtonsAdapter.ButtonViewHolder>{

    private List<Button> mButtonList;
    private OnItemClickListener mOnItemClickListener;


    public RvButtonsAdapter(List<Button> mButtonList) {
        this.mButtonList = mButtonList;
    }

    @Override
    public ButtonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_item_layout, parent, false);
        return new ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ButtonViewHolder holder, final int position) {
        Button btn = mButtonList.get(position);
        holder.tvButtonText.setText(btn.getButtonText());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnItemClickListener){
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });
        if(position==0){
            holder.view.setBackgroundResource(R.drawable.first_button_bg);
        }
    }

    @Override
    public int getItemCount() {
        return mButtonList.size();
    }

    public class ButtonViewHolder extends RecyclerView.ViewHolder{

        TextView tvButtonText;
        View view;

        public ButtonViewHolder(View itemView) {
            super(itemView);
            tvButtonText = (TextView) itemView.findViewById(R.id.tv_btn_text);
            view = itemView;
        }
    }

    public void setOnCItemClickListener(OnItemClickListener itemClickListener){
        this.mOnItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
