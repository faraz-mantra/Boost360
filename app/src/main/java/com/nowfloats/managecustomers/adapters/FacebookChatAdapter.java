package com.nowfloats.managecustomers.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nowfloats.managecustomers.FacebookChatActivity;
import com.nowfloats.managecustomers.FacebookChatDetailActivity;
import com.nowfloats.managecustomers.models.FacebookChatUsersModel;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Admin on 17-08-2017.
 */

public class FacebookChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final String NO_MESSAGES = "no_messages";
    List<FacebookChatUsersModel.Datum> chatList;
    Context mContext;
    final String TEXT = "text", IMAGE = "image";
    final int EMPTY_LAYOUT = -1, MESSAGE_LAYOUT = -2;

    public FacebookChatAdapter(Context context, List<FacebookChatUsersModel.Datum> list){
        chatList = list;
        mContext = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch(viewType) {
            case MESSAGE_LAYOUT:
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_facebook_chat_item, parent, false);
            return new MyUserViewHolder(view);
            case EMPTY_LAYOUT:
                view = LayoutInflater.from(mContext).inflate(R.layout.facebook_no_messages, parent, false);
                return new MyEmptyViewHolder(view);
            default:
                return null;

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder == null) return;
        if(viewHolder instanceof MyUserViewHolder) {
            MyUserViewHolder userViewHolder = (MyUserViewHolder) viewHolder;
            FacebookChatUsersModel.Datum data = chatList.get(position);
            String userName = "Unknown";
            userName = TextUtils.isEmpty(data.getUserData().getFirstName())?
                    userName : data.getUserData().getFirstName();
            if(!userName.equals("Unknown")){
                userName = TextUtils.isEmpty(data.getUserData().getLastName())?
                        userName : userName +" "+data.getUserData().getLastName();
            }

            userViewHolder.userName.setText(userName);

            switch (data.getLatestMessage().getType()) {
                case TEXT:
                    userViewHolder.message.setText(data.getLatestMessage().getData().getText());
                    break;
                case IMAGE:
                    userViewHolder.message.setText("Sent a photo");
                    break;
                default:
                    break;
            }

            userViewHolder.date.setText(getFormattedTime(data.getTimestamp()));
            //Glide.with(mContext).load(data.getUserData().getProfilePic()).placeholder(R.drawable.ic_user).into(holder.userPic);
            Picasso.get()
                    .load(data.getUserData().getProfilePic())
                    .resize(200, 0)
                    .placeholder(R.drawable.ic_user)
                    .into(userViewHolder.userPic);
        }else if(viewHolder instanceof MyEmptyViewHolder){

        }
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getSender().equals(NO_MESSAGES)?EMPTY_LAYOUT:MESSAGE_LAYOUT;
    }

    private String getFormattedTime(Long milliSecond){

        return new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date(milliSecond));
    }
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class MyEmptyViewHolder extends RecyclerView.ViewHolder {

        public TextView mainMessage, message;

        public MyEmptyViewHolder(View itemView) {
            super(itemView);
            mainMessage = (TextView) itemView.findViewById(R.id.tv_main_message);
            message = (TextView) itemView.findViewById(R.id.tv_note_message);

        }
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
                    i.putExtra("user_data",new Gson().toJson(chatList.get(getAdapterPosition()).getUserData()));
                   /* i.putExtra("user_id",chatList.get(getAdapterPosition()).getUserId());
                    i.putExtra("profile_pic",chatList.get(getAdapterPosition()).getUserData().getProfilePic());
                    i.putExtra("user_name",chatList.get(getAdapterPosition()).getUserData().getFirstName()+" "+
                            chatList.get(getAdapterPosition()).getUserData().getLastName());*/
                    ((FacebookChatActivity) mContext).startActivityForResult(i,221);
                    ((FacebookChatActivity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }
    }

}
