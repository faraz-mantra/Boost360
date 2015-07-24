package com.nowfloats.NavigationDrawer.Chat;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by guru on 24/07/2015.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
        Activity appContext;
        View displayView;
        ArrayList<ChatModel> chatData;
        private LayoutInflater mInflater;
    public ChatAdapter(Activity appContext, ArrayList<ChatModel> chatData) {
        this.appContext = appContext;
        this.chatData = chatData;
        mInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

public class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public ImageView imageView;
    public TextView sendText,recText;
    public LinearLayout send_layout;
    public LinearLayout rec_layout;
    public ViewHolder(View v) {
        super(v);
        sendText = (TextView)itemView.findViewById(R.id.send_txt);
        recText = (TextView)itemView.findViewById(R.id.rec_txt);
        send_layout = (LinearLayout)itemView.findViewById(R.id.send_layout);
        rec_layout = (LinearLayout)itemView.findViewById(R.id.rec_layout);
    }
}

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        displayView = mInflater.inflate(R.layout.chat_list_design, parent, false);
        ViewHolder viewHolder = new ViewHolder(displayView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        try{
            if (chatData.get(position)!=null){
                if(chatData.get(position).incoming){
                    holder.rec_layout.setVisibility(View.VISIBLE);
                    holder.recText.setText(chatData.get(position).message);
                    holder.send_layout.setVisibility(View.GONE);
                }else{
                    holder.rec_layout.setVisibility(View.GONE);
                    holder.send_layout.setVisibility(View.VISIBLE);
                    holder.sendText.setText(chatData.get(position).message);
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return chatData.size();
    }
}
