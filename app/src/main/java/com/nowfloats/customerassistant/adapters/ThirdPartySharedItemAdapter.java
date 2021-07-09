package com.nowfloats.customerassistant.adapters;

import android.content.Context;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.customerassistant.models.SharedSuggestionsDO;
import com.thinksity.R;

import java.util.List;

/**
 * Created by Admin on 12-10-2017.
 */

public class ThirdPartySharedItemAdapter extends RecyclerView.Adapter<ThirdPartySharedItemAdapter.MySharedViewHolder> {

    private Context mContext;
    private List<SharedSuggestionsDO> mSuggestionsDO;
    public ThirdPartySharedItemAdapter(Context context, List<SharedSuggestionsDO> suggestionsDO){
        mContext = context;
        mSuggestionsDO = suggestionsDO;
    }
    @Override
    public MySharedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_third_party_shared_item,parent,false);
        return new MySharedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MySharedViewHolder holder, int position) {
        holder.nameText.setText(mSuggestionsDO.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mSuggestionsDO.size();
    }

    public void refreshListData(List<SharedSuggestionsDO> newList){
        if(newList == null){
            return;
        }
        if(mSuggestionsDO.size()>0) {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new SuggestionsCallback(newList));
            result.dispatchUpdatesTo(this);
            mSuggestionsDO.addAll(newList);
        }else{
            mSuggestionsDO.addAll(newList);
            notifyDataSetChanged();
        }

    }


    class SuggestionsCallback extends DiffUtil.Callback{

           List<SharedSuggestionsDO> newList;
           SuggestionsCallback(List<SharedSuggestionsDO> newList){
               this.newList = newList;
           }
           @Override
           public int getOldListSize() {
               return mSuggestionsDO.size();
           }

           @Override
           public int getNewListSize() {
               return newList.size();
           }

           @Override
           public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
               return mSuggestionsDO.get(oldItemPosition).equals(newList.get(newItemPosition));
           }

           @Override
           public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
               return true;
           }
       }
    class MySharedViewHolder extends RecyclerView.ViewHolder {

        int viewType;
        ImageView cancelImage;
        TextView nameText;

        public MySharedViewHolder(View itemView) {
            super(itemView);
            this.viewType = getItemViewType();
            cancelImage = (ImageView) itemView.findViewById(R.id.img_cancel);
            nameText = (TextView) itemView.findViewById(R.id.tv_person_name);
            cancelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(getAdapterPosition());
                }
            });
        }
    }

    private void removeItem(int position){
        mSuggestionsDO.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mSuggestionsDO.size());
    }
}
