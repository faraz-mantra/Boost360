package com.nowfloats.managecustomers.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.managecustomers.FacebookChatDetailActivity;
import com.nowfloats.managecustomers.models.FacebookChatDataModel;
import com.thinksity.R;

import java.util.List;

/**
 * Created by Admin on 17-08-2017.
 */

public class FacebookChatAdapter extends RecyclerView.Adapter<FacebookChatAdapter.MyUserViewHolder>{

    List<FacebookChatDataModel> chatList;
    Context mContext;
    public FacebookChatAdapter(Context context, List<FacebookChatDataModel> list){
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
        //holder.userName.setText();
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class MyUserViewHolder extends RecyclerView.ViewHolder{

        TextView userName,message,date;
        public MyUserViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.tv_chat_user);
            message = (TextView) itemView.findViewById(R.id.tv_chat_message);
            date = (TextView) itemView.findViewById(R.id.tv_chat_date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext,FacebookChatDetailActivity.class);
                    i.putExtra("user_id","1234");
                    ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    mContext.startActivity(i);
                }
            });
        }
    }

}
