package com.nowfloats.managecustomers.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nowfloats.managecustomers.FacebookChatDetailActivity;
import com.nowfloats.managecustomers.models.FacebookChatUsersModel;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.List;

/**
 * Created by Admin on 17-08-2017.
 */

public class FacebookChatAdapter extends RecyclerView.Adapter<FacebookChatAdapter.MyUserViewHolder>{

    List<FacebookChatUsersModel.Datum> chatList;
    Context mContext;
    public FacebookChatAdapter(Context context, List<FacebookChatUsersModel.Datum> list){
        chatList = list;
        mContext = context;
    }
    @Override
    public MyUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_facebook_chat_item, parent, false);
        return new MyUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyUserViewHolder holder, int position) {
        if(holder == null) return;
        FacebookChatUsersModel.Datum data = chatList.get(position);
        holder.userName.setText(data.getUserData().getFirstName()+" "+data.getUserData().getLastName());
        holder.message.setText(data.getLatestMessage().getData());
        holder.date.setText(Methods.getFormattedDate(data.getTimestamp()));
        Glide.with(mContext).load(data.getUserData().getProfilePic()).placeholder(R.drawable.ic_user).into(holder.userPic);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MyUserViewHolder extends RecyclerView.ViewHolder{

        TextView userName,message,date;
        ImageView userPic;
        public MyUserViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.tv_chat_user);
            message = (TextView) itemView.findViewById(R.id.tv_chat_message);
            date = (TextView) itemView.findViewById(R.id.tv_chat_date);
            userPic = (ImageView) itemView.findViewById(R.id.img_chat_user);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext,FacebookChatDetailActivity.class);
                    i.putExtra("user_id",chatList.get(getAdapterPosition()).getUserId());
                    i.putExtra("user_name",chatList.get(getAdapterPosition()).getUserData().getFirstName()+" "+
                            chatList.get(getAdapterPosition()).getUserData().getLastName());
                    ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    mContext.startActivity(i);
                }
            });
        }
    }

}
