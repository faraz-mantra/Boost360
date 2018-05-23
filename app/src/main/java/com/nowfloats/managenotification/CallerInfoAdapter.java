package com.nowfloats.managenotification;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by vinay on 22-05-2018.
 */

public class CallerInfoAdapter extends RecyclerView.Adapter<CallerInfoAdapter.MyHolder> {

    private Context mContext;
    private ArrayList<VmnCallModel> mList;

    public CallerInfoAdapter(Context context, ArrayList<VmnCallModel> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public CallerInfoAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = null;
        convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_caller_info, parent, false);
        return new CallerInfoAdapter.MyHolder(convertView, viewType);

    }

    @Override
    public void onBindViewHolder(@NonNull final CallerInfoAdapter.MyHolder holder, int position) {
        final VmnCallModel childModel = mList.get(position);

        if (childModel.getCallStatus().equalsIgnoreCase("MISSED")) {
            holder.date.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(mContext, R.drawable.ic_call_missed), null, null, null);
            holder.play.setText("Missed\nCall");
            holder.play.setTextColor(ContextCompat.getColor(mContext, R.color.gray_transparent));
            holder.play.setPaintFlags(holder.play.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        } else {
            holder.date.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(mContext, R.drawable.ic_call_received), null, null, null);
            holder.play.setText(mContext.getString(R.string.play_with_underline));
            holder.play.setPaintFlags(holder.play.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((TextView) v).getText().toString().equalsIgnoreCase("play")) {
                        holder.play.setTextColor(ContextCompat.getColor(mContext, R.color.gray_transparent));
                    }
                }
            });

        }
        holder.number.setText(mList.get(position).getCallerNumber());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView number, date, play;
        int viewType;

        public MyHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            itemView.findViewById(R.id.llayout_number).setOnClickListener(this);
            itemView.findViewById(R.id.call_icon).setOnClickListener(this);
            number = (TextView) itemView.findViewById(R.id.tv_number);
            date = (TextView) itemView.findViewById(R.id.tv_date);
            play = (TextView) itemView.findViewById(R.id.tv_play);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.llayout_number:
                case R.id.call_icon:
//                    Methods.makeCall(mContext, mList.get(getAdapterPosition()).getCallerNumber());
                    break;
            }
        }
    }

}
