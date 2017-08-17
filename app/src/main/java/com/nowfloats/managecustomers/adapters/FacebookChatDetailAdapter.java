package com.nowfloats.managecustomers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thinksity.R;

/**
 * Created by Admin on 17-08-2017.
 */

public class FacebookChatDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    final int TEXT = 0, IMAGE = 1;
    final int merchant = 0, customer = 1;
    public FacebookChatDetailAdapter(Context context){
        mContext = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
           case TEXT:
               view = LayoutInflater.from(mContext).inflate(R.layout.adapter_facebook_chat_text,parent,false);
               return new MyTextChatDetailViewHolder(view, viewType);
           case IMAGE:
               view = LayoutInflater.from(mContext).inflate(R.layout.adapter_facebook_chat_image,parent,false);
               return new MyImageChatDetailViewHolder(view, viewType);
           default:
               return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyTextChatDetailViewHolder)
        {
            MyTextChatDetailViewHolder textHolder = (MyTextChatDetailViewHolder) holder;
            setAlignment(merchant,textHolder.parentLayout);

        }else if(holder instanceof MyImageChatDetailViewHolder)
        {
            MyImageChatDetailViewHolder imageHolder = (MyImageChatDetailViewHolder) holder;
            setAlignment(customer,imageHolder.parentLayout);

        }

    }

    private void setAlignment(int user,LinearLayout layout){
        switch (user){
            case merchant:
                layout.setHorizontalGravity(Gravity.END);
                break;
            default:
                layout.setHorizontalGravity(Gravity.START);
                break;
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position%2;
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class MyTextChatDetailViewHolder extends RecyclerView.ViewHolder{

        TextView tvDate,tvMessage;
        LinearLayout parentLayout;
        int viewType;
        public MyTextChatDetailViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            parentLayout = (LinearLayout) itemView;
            tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }

    class MyImageChatDetailViewHolder extends RecyclerView.ViewHolder{

        LinearLayout parentLayout;
        ImageView imgMessage;
        TextView tvDate;
        int viewType;
        public MyImageChatDetailViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            parentLayout = (LinearLayout) itemView;
            imgMessage = (ImageView) itemView.findViewById(R.id.img_message);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
}
