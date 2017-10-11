package com.nowfloats.customerassistant.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 11-10-2017.
 */

public class ThirdPartySuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    List<String> rvList = new ArrayList<>();
    public ThirdPartySuggestionAdapter(Context context){
        mContext = context;
        //rvList = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_suggestion_third_party_item,parent,false);
        return new MySuggestedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return rvList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    /*class SuggestionsCallback extends DiffUtil.Callback{

            List<> newList;
            SuggestionsCallback(List<> newList){
                this.newList = newList;
            }
            @Override
            public int getOldListSize() {
                return 0;
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return false;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return false;
            }
        }*/
    private class MySuggestedViewHolder extends RecyclerView.ViewHolder{

        int viewType;
        ImageView cancelImage;
        TextView nameText;

        public MySuggestedViewHolder(View itemView) {
            super(itemView);
            this.viewType = getItemViewType();
            cancelImage = (ImageView) itemView.findViewById(R.id.img_cancel);
            nameText = (TextView) itemView.findViewById(R.id.tv_name);
            cancelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }


    }
}
