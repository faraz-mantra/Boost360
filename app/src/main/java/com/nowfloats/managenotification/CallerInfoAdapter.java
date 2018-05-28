package com.nowfloats.managenotification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.Business_Enquiries.Model.Entity_model;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by vinay on 22-05-2018.
 */

public class CallerInfoAdapter extends RecyclerView.Adapter<CallerInfoAdapter.MyHolder> {

    private Context mContext;
    private ArrayList<VmnCallModel> mCallList;
    private ArrayList<Entity_model> mEnquiryList;
    private NOTI_TYPE noti_type;

    public <T> CallerInfoAdapter(Context context, NOTI_TYPE noti_type, ArrayList<T> list) {
        mContext = context;
        this.noti_type = noti_type;

        switch (noti_type) {
            case CALLS:
                mCallList = (ArrayList<VmnCallModel>) list;
                break;
            case ENQUIRIES:
                mEnquiryList = (ArrayList<Entity_model>) list;
                break;
        }
    }

    public enum NOTI_TYPE {
        CALLS,
        ENQUIRIES
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

        switch (noti_type) {
            case CALLS:
                VmnCallModel vmnCallModel = mCallList.get(position);
                holder.tvSource.setText(vmnCallModel.getCallerNumber());
                holder.tvDate.setText(vmnCallModel.getCallDateTime());
                holder.tvInfo.setVisibility(View.GONE);
                holder.itemView.setTag(vmnCallModel);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VmnCallModel vmnModel = (VmnCallModel) holder.itemView.getTag();
                        Methods.makeCall(mContext, vmnModel.getCallerNumber());

                    }
                });
                break;
            case ENQUIRIES:
                Entity_model entity_model = mEnquiryList.get(position);
                holder.tvSource.setText(entity_model.getPhone());
                holder.tvInfo.setText(entity_model.getMessage());
                holder.tvDate.setText(entity_model.getCreatedDate());
                holder.tvInfo.setVisibility(View.VISIBLE);
                holder.itemView.setTag(entity_model);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PackageManager manager = mContext.getPackageManager();
                        Intent intent = manager.getLaunchIntentForPackage(mContext.getPackageName());
                        if (intent == null) return;
                        intent.putExtra("from", "notification");
                        intent.putExtra("url", "ttb");
                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        try {
                            pendingIntent.send(mContext, 0, intent);
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        switch (noti_type) {
            case CALLS:
                return mCallList.size();
            case ENQUIRIES:
                return mEnquiryList.size();
        }
        return 0;
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        ImageView ivIcon;
        TextView tvSource, tvInfo, tvDate;

        public MyHolder(View itemView, int viewType) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvSource = (TextView) itemView.findViewById(R.id.tvSource);
            tvInfo = (TextView) itemView.findViewById(R.id.tvInfo);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
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
