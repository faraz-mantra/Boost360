package com.nowfloats.AccrossVerticals.domain.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nowfloats.AccrossVerticals.domain.interfaces.ActiveDomainListener;
import com.thinksity.R;

import java.util.List;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.MyViewHolder> {

    Context mContext;
    List<String> detailsList;
    ActiveDomainListener listener;

    public EmailAdapter(List<String> list, ActiveDomainListener listener) {
        this.detailsList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.item_single_email, parent, false);
        mContext = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailAdapter.MyViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEmailItemClicked(String.valueOf(position) + " position Clicked");
            }
        });
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
