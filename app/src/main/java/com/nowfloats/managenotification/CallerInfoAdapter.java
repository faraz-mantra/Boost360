package com.nowfloats.managenotification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.Business_Enquiries.Model.Business_Enquiry_Model;
import com.nowfloats.Business_Enquiries.Model.Entity_model;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.signup.UI.UI.WebSiteAddressActivity;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.nowfloats.util.Constants.PREF_NOTI_CALL_LOGS;
import static com.nowfloats.util.Constants.PREF_NOTI_ENQUIRIES;

/**
 * Created by vinay on 22-05-2018.
 */

public class CallerInfoAdapter extends RecyclerView.Adapter<CallerInfoAdapter.MyHolder> {

    private Context mContext;
    private ArrayList<VmnCallModel> mCallList;
    private ArrayList<Business_Enquiry_Model> mEnquiryList;
    private NOTI_TYPE noti_type;
    private int leftMargin, callTopMargin, enQTopMargin;
    private SharedPreferences pref;
    final Gson gson = new Gson();

    public <T> CallerInfoAdapter(Context context, NOTI_TYPE noti_type, ArrayList<T> list, SharedPreferences pref) {
        mContext = context;
        this.noti_type = noti_type;
        this.pref = pref;
        leftMargin = Methods.dpToPx(6, mContext);
        callTopMargin = Methods.dpToPx(10, mContext);
        enQTopMargin = Methods.dpToPx(22, mContext);

        switch (noti_type) {
            case CALLS:
                mCallList = (ArrayList<VmnCallModel>) list;
                break;
            case ENQUIRIES:
                mEnquiryList = (ArrayList<Business_Enquiry_Model>) list;
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
                final VmnCallModel vmnCallModel = mCallList.get(position);
                if (vmnCallModel.getCallStatus().equalsIgnoreCase("MISSED")) {
                    holder.ivIcon.setImageResource(R.drawable.ic_call_missed);
                } else {
                    holder.ivIcon.setImageResource(R.drawable.ic_call_received);
                }
                holder.ivIcon.setPadding(leftMargin, callTopMargin, leftMargin, leftMargin);
                holder.tvSource.setText(vmnCallModel.getCallerNumber());
                holder.tvDate.setText(getDate(Methods.getFormattedDate(vmnCallModel.getCallDateTime())));
                holder.tvInfo.setVisibility(View.GONE);
                holder.itemView.setTag(vmnCallModel);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ArrayList<VmnCallModel> vmnList = new ArrayList<>(mCallList);
                        vmnList.remove(vmnCallModel);

                        String json = gson.toJson(vmnList);
                        pref.edit().putString(PREF_NOTI_CALL_LOGS, json).commit();

                        ((CallerInfoDialog) mContext).checkValues();
//                        final PackageManager manager = mContext.getPackageManager();
                        Intent intent = new Intent(mContext, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("from", "notification");
                        intent.putExtra("url", mContext.getString(R.string.deep_link_call_tracker));
                        mContext.startActivity(intent);
                    }
                });
                break;
            case ENQUIRIES:
                final Business_Enquiry_Model entity_model = mEnquiryList.get(position);
                holder.ivIcon.setImageResource(R.drawable.ic_mail);
                holder.ivIcon.setPadding(leftMargin, enQTopMargin, leftMargin, leftMargin);
                holder.tvSource.setText("From: " + entity_model.getContact());
                holder.tvInfo.setText(entity_model.getMessage());
                holder.tvDate.setText(getDate(Methods.getFormattedDate(entity_model.getCreatedOn())));
                holder.tvInfo.setVisibility(View.VISIBLE);
                holder.itemView.setTag(entity_model);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ArrayList<Business_Enquiry_Model> eqList = new ArrayList<>(mEnquiryList);
                        eqList.remove(entity_model);

                        String json = gson.toJson(eqList);
                        pref.edit().putString(PREF_NOTI_ENQUIRIES, json).commit();

                        ((CallerInfoDialog) mContext).checkValues();

//                        final PackageManager manager = mContext.getPackageManager();
//                        Intent intent = manager.getLaunchIntentForPackage(mContext.getPackageName());
//                        if (intent == null) return;
//                        intent.putExtra("from", "notification");
//                        intent.putExtra("url", "ttb");
//                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//                        try {
//                            pendingIntent.send(mContext, 0, intent);
//                        } catch (PendingIntent.CanceledException e) {
//                            e.printStackTrace();
//                        }

                        Intent intent = new Intent(mContext, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("from", "notification");
                        intent.putExtra("url", "ttb");
                        mContext.startActivity(intent);
                    }
                });
                break;
        }
    }

    private String getDate(String date) {
      /*  int comaIndex = DATE.indexOf(",");
        String subYear = DATE.substring(comaIndex,DATE.lastIndexOf(" at"));*/
        return date.replaceAll(",.*?at", /*"'"+subYear.substring(subYear.length()-2)*/ "");
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
