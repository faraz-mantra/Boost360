package com.nowfloats.managenotification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.Business_Enquiries.Model.Business_Enquiry_Model;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;

import static com.nowfloats.util.Constants.PREF_NOTI_CALL_LOGS;
import static com.nowfloats.util.Constants.PREF_NOTI_ENQUIRIES;
import static com.nowfloats.util.Constants.PREF_NOTI_ORDERS;

/**
 * Created by vinay on 22-05-2018.
 */

public class CallerInfoAdapter extends RecyclerView.Adapter<CallerInfoAdapter.MyHolder> {

    private Context mContext;
    private ArrayList<VmnCallModel> mCallList;
    private ArrayList<Business_Enquiry_Model> mEnquiryList;
    private ArrayList<OrderModel> mOrderList;
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
            case ORDERS:
                mOrderList = (ArrayList<OrderModel>) list;
                break;
        }
    }

    public enum NOTI_TYPE {
        CALLS,
        ENQUIRIES,
        ORDERS
    }

    @NonNull
    @Override
    public CallerInfoAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = null;
        if (noti_type == NOTI_TYPE.CALLS || noti_type == NOTI_TYPE.ENQUIRIES) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_caller_info, parent, false);
            return new CallerInfoAdapter.MyHolder(convertView, viewType);
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_orders_info, parent, false);
            return new CallerInfoAdapter.MyHolder(convertView, viewType);
        }
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
                        MixPanelController.track(MixPanelController.BUBBLE_CALL_TRACKER, null);

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
                        MixPanelController.track(MixPanelController.BUBBLE_ENQUIRY, null);

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
            case ORDERS:
                final OrderModel order = mOrderList.get(position);
                if (position == mOrderList.size() - 1) {
                    holder.divider.setVisibility(View.INVISIBLE);
                } else {
                    holder.divider.setVisibility(View.VISIBLE);
                }
                holder.tvName.setText(order.getBuyerName());
                holder.tvPhoneNumber.setText(order.getBuyerContactNumber());
                holder.tvLocation.setText(order.getBuyerCity() + ", " + order.getBuyerState());
                holder.tvQuantity.setText(Integer.toString(order.getOrderQuantity()));
                holder.tvValue.setText(order.getOrderCurrency() + " " + Double.toString(order.getOrderValue()));
                holder.tvStatus.setText(order.getOrderStatus());
                holder.tvDate.setText(getDate(Methods.getFormattedDate(order.getCreatedOn())));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MixPanelController.track(MixPanelController.BUBBLE_ORDER_DETAIL, null);

                        ArrayList<OrderModel> eqList = new ArrayList<>(mOrderList);
                        eqList.remove(order);

                        String json = gson.toJson(eqList);
                        pref.edit().putString(PREF_NOTI_ORDERS, json).commit();
                        ((CallerInfoDialog) mContext).checkValues();

                        Intent intent = new Intent(mContext, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("from", "notification");
                        intent.putExtra("url", "myorderdetail");
                        intent.putExtra("payload", order.getOrderId());
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
            case ORDERS:
                return mOrderList.size();
        }
        return 0;
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivIcon;
        TextView tvSource, tvInfo, tvDate, tvName, tvPhoneNumber, tvLocation, tvQuantity, tvValue, tvStatus;
        View divider;

        public MyHolder(View itemView, int viewType) {
            super(itemView);
            if (noti_type == NOTI_TYPE.ENQUIRIES || noti_type == NOTI_TYPE.CALLS) {
                ivIcon = itemView.findViewById(R.id.ivIcon);
                tvSource = itemView.findViewById(R.id.tvSource);
                tvInfo = itemView.findViewById(R.id.tvInfo);
                tvDate = itemView.findViewById(R.id.tvDate);
            } else {
                tvName = itemView.findViewById(R.id.tv_name);
                tvPhoneNumber = itemView.findViewById(R.id.tv_phone_number);
                tvLocation = itemView.findViewById(R.id.tv_location);
                tvQuantity = itemView.findViewById(R.id.tv_order_quantity);
                tvValue = itemView.findViewById(R.id.tv_order_value);
                divider = itemView.findViewById(R.id.divider);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvStatus = itemView.findViewById(R.id.tv_order_status);
            }
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
