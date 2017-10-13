package com.nowfloats.customerassistant.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.customerassistant.ThirdPartySuggestionDetailActivity;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 09-10-2017.
 */

public class ThirdPartyAdapter extends RecyclerView.Adapter<ThirdPartyAdapter.MySmsViewHolder> {


    private Context mContext;
    private List<SuggestionsDO> rvList;
    public boolean isCounterStopped;
    private RecyclerView mRecyclerView;

    public ThirdPartyAdapter(Context context, List<SuggestionsDO> list){
        mContext = context;
        rvList = list;
    }

    @Override
    public MySmsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_third_party_sms_item,parent,false);
        MySmsViewHolder mySmsViewHolder = new MySmsViewHolder(view);
        return mySmsViewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onBindViewHolder(final MySmsViewHolder holder, final int position) {
        final SuggestionsDO suggestionsDO = rvList.get(position);
        holder.addressTextView.setText(suggestionsDO.getActualMessage());
        holder.timeTextView.setText(Methods.getFormattedDate(suggestionsDO.getDate()));
        holder.titleTextView.setText(suggestionsDO.getActualMessage());

        long millis=suggestionsDO.getExpiryDate()-Calendar.getInstance().getTimeInMillis();

        if(holder.customTimer!=null){
            holder.customTimer.cancel();
        }

        holder.customTimer = (CustomTimer) new CustomTimer(millis,holder,suggestionsDO,position).start();;
    }

    class CustomTimer extends CountDownTimer{
        private MySmsViewHolder holder;
        private SuggestionsDO suggestionsDO;
        private  int mPosition;

        CustomTimer(long millis,MySmsViewHolder holder,
                    SuggestionsDO suggestionsDO,int position){
            super(millis,1000);
            this.holder = holder;
            this.suggestionsDO = suggestionsDO;
            this.mPosition = position;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            isCounterStopped = false;
            if(millisUntilFinished<60*60*24*1000){
                String hms = String.format(Locale.ENGLISH,"%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                holder.responseTextView.setText("Respond in "+hms);
                holder.responseTextView.setTextColor(ContextCompat.getColorStateList(mContext,R.color.red_btn_text_color));
                holder.responseTextView.setBackgroundResource(R.drawable.red_btn_state_bg);
            }else {
                holder.responseTextView.setTextColor(ContextCompat.getColorStateList(mContext,R.color.yellow_btn_text_color));
                holder.responseTextView.setBackgroundResource(R.drawable.yellow_btn_bg);
                holder.responseTextView.setText("Respond in " + TimeUnit.MILLISECONDS.toDays(millisUntilFinished) +" days");
            }
            if(!Methods.isMyActivityAtTop(mContext)) {
                this.cancel();
                isCounterStopped = true;
            }
        }

        @Override
        public void onFinish() {
            holder.responseTextView.setText("Expired");
            finishTimer(mPosition);
        }
    }

    private void finishTimer(final int mPosition){
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                rvList.remove(mPosition);
                notifyItemRemoved(mPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rvList.size();
    }

    public void refreshListData(List<SuggestionsDO> newList){
        if(newList == null){
            return;
        }
        if(rvList.size()>0) {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new SuggestionCallback(newList));
            result.dispatchUpdatesTo(this);
            rvList.clear();
            rvList.addAll(newList);
        }else{
            rvList.addAll(newList);
            notifyDataSetChanged();
        }

    }

    private class SuggestionCallback extends DiffUtil.Callback{

        List<SuggestionsDO> newList;
        private SuggestionCallback(List<SuggestionsDO> list){
            newList = list;
        }
        @Override
        public int getOldListSize() {
            return rvList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if(newList.get(newItemPosition).getMessageId() != null && rvList.get(oldItemPosition).getMessageId() != null){
                return newList.get(newItemPosition).getMessageId().equals(rvList.get(oldItemPosition).getMessageId());
            }
           return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return false;
        }
    }
    class MySmsViewHolder extends RecyclerView.ViewHolder{

        TextView addressTextView,titleTextView,timeTextView,responseTextView;
        CustomTimer customTimer;

        public MySmsViewHolder(View itemView) {
            super(itemView);
            addressTextView = (TextView) itemView.findViewById(R.id.tv_address);
            titleTextView = (TextView) itemView.findViewById(R.id.tv_title);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_time);
            responseTextView = (TextView) itemView.findViewById(R.id.tv_response);
            responseTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, ThirdPartySuggestionDetailActivity.class);
                    i.putExtra("message",rvList.get(getAdapterPosition()));
                    mContext.startActivity(i);
                }
            });
        }
    }

    private void showActualMessage(){

    }
}
