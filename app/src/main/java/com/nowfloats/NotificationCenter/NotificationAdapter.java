package com.nowfloats.NotificationCenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.DeepLinkInterface;
import com.nowfloats.NavigationDrawer.Mobile_Site_Activity;
import com.nowfloats.NotificationCenter.Model.AlertModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
//import semusi.activitysdk.ContextSdk;

/**
 * Created by guru on 27-04-2015.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private SimpleDateFormat format;
    private PorterDuffColorFilter primaryColorFilter,defaultColorFilter;
    private int imageId;
    Activity appContext;
    View displayView;
    ArrayList<AlertModel> alertData;
    private LayoutInflater mInflater;
    private HashMap<String,Integer> alertImageMap;
    DeepLinkInterface linkInterface;
    NotificationInterface alertInterface;
    UserSessionManager session;
    Bus bus;
    private String currentUrl;
    private String ruleId;

    public NotificationAdapter(Activity appContext, ArrayList<AlertModel> alertData, NotificationInterface alertInterface, UserSessionManager session,Bus bus) {
        loadImages();
        this.appContext = appContext;
        this.alertData = alertData;
        this.alertInterface = alertInterface;
        this.session = session;
        this.bus = bus;
        mInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        primaryColorFilter = new PorterDuffColorFilter(appContext.getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
        defaultColorFilter = new PorterDuffColorFilter(appContext.getResources().getColor(R.color.inactive_text), PorterDuff.Mode.SRC_IN);
        format = new SimpleDateFormat("MMM dd, hh:mm aa");
        format.setTimeZone(TimeZone.getDefault());
        imageId = R.drawable.alert_default;
        linkInterface = (DeepLinkInterface)appContext;
    }

    public void addAlerts(ArrayList<AlertModel> data){
        try {
            for (int i = 0; i < data.size(); i++) {
                if (!alertData.contains(data.get(i)))
                    alertData.add(data.get(i));
            }
            notifyDataSetChanged();

        }catch(Exception e) {e.printStackTrace();}
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        public TextView titleText,descText,date;
        public Button alertBtn;
        public LinearLayout full_layout;
        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView)itemView.findViewById(R.id.storeDataIcon);
            titleText = (TextView)itemView.findViewById(R.id.titleText);
            descText = (TextView)itemView.findViewById(R.id.descText);
            date = (TextView)itemView.findViewById(R.id.created_date);
            alertBtn = (Button)itemView.findViewById(R.id.alert_btn);
            full_layout = (LinearLayout)itemView.findViewById(R.id.full_layout);
        }
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        displayView = mInflater.inflate(R.layout.alert_list_design, parent, false);
        ViewHolder viewHolder = new ViewHolder(displayView);
        return viewHolder;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        if (alertData.size() > 0){
            holder.alertBtn.setVisibility(View.GONE);
            holder.imageView.setImageResource(imageId);
            holder.descText.setText(alertData.get(position).Message);
            holder.full_layout.setTag(position + "");
            holder.alertBtn.setTag(position + "");
            for (int i = 0; i < alertData.get(position).NotificationData.size(); i++) {
                String key = alertData.get(position).NotificationData.get(i).Key;
                if (key.equals("notificationType") || key.equals("Type")) {
                    try {
                        if (alertImageMap.get(alertData.get(position).NotificationData.get(i).Value) != null &&
                                alertImageMap.get(alertData.get(position).NotificationData.get(i).Value) != 0) {
                            imageId = alertImageMap.get(alertData.get(position).NotificationData.get(i).Value);
                        } else {
                            imageId = R.drawable.alert_default;
                        }
                        holder.imageView.setImageResource(imageId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        imageId = R.drawable.alert_default;
                    }
                } else if (key.equals("ButtonText")) {
                    String valueStr = alertData.get(position).NotificationData.get(i).Value;
                    if (valueStr != null && valueStr.trim().length() > 0) {
                        holder.alertBtn.setVisibility(View.VISIBLE);
                        holder.alertBtn.setText(valueStr);
                    } else {
                        holder.full_layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClickMethod(v, position);
                            }
                        });
                    }
                } else if (key.equals("headText")) {
                    holder.titleText.setText(alertData.get(position).NotificationData.get(i).Value);
                }
            }

    //        String url = Constants.NOW_FLOATS_API_URL+alertData.get(position).PrimaryImageUri;
    //        Picasso.with(appContext).load(url).into(holder.imageView);

                try {
                    String dateString = alertData.get(position).CreatedOn;
                    dateString = dateString.replace("/Date(", "").replace(")/", "");
                    Long epochTime = Long.parseLong(dateString);
                    Date date = new Date(epochTime);
                    if (date != null)
                        holder.date.setText(format.format(date));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //HashMap<String, String> eventKey = new HashMap<String, String>();
                //eventKey.put(EventKeysWL.SEARCH_QUERIES, "Clicked on Alert tab");
                //ContextSdk.tagEvent(NotificationAdapter.class.getCanonicalName(),eventKey);

                if (alertData.get(position).isRead.equalsIgnoreCase("true")) {
                    holder.titleText.setTextColor(appContext.getResources().getColor(R.color.inactive_text));
                    holder.descText.setTextColor(appContext.getResources().getColor(R.color.inactive_text));
                    holder.alertBtn.setBackground(appContext.getResources().getDrawable(R.drawable.corner_grey_backgrnd));
                    holder.imageView.setColorFilter(defaultColorFilter);
                } else {
                    holder.imageView.setColorFilter(primaryColorFilter);
                    holder.titleText.setTextColor(appContext.getResources().getColor(R.color.primaryColor));
                    holder.descText.setTextColor(appContext.getResources().getColor(R.color.main_text_color));
                    holder.alertBtn.setBackground(appContext.getResources().getDrawable(R.drawable.selector));
                }

                holder.alertBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onClickMethod(v, position);
                }
            });
        }
    }

    private void onClickMethod(View v,int position) {
        boolean blogChk = false;
        try {
            final int pos = Integer.parseInt(v.getTag().toString());
            for (int i = 0; i < alertData.get(pos).NotificationData.size(); i++) {
                String key = alertData.get(pos).NotificationData.get(i).Key;
                if (key.equals("notificationType") || key.equals("Type")) {
                    String alertType = alertData.get(pos).NotificationData.get(i).Value;
                    if (alertType != null && alertType.trim().length() > 0) {
                        MixPanelController.track("AlertType-" + alertType, null);
                        if (alertType.equals("BLOG")) {
                            blogChk = true;
                        }
                    }
                } else if (key.equals("url")) {
                    currentUrl = alertData.get(position).NotificationData.get(i).Value;
                } else if (key.equals("ruleId")) {
                    ruleId = alertData.get(position).NotificationData.get(i).Value;
                }
            }
            if (blogChk) {
                Intent showWebSiteIntent = new Intent(appContext, Mobile_Site_Activity.class);
                showWebSiteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                showWebSiteIntent.putExtra("WEBSITE_NAME", currentUrl);
                appContext.startActivity(showWebSiteIntent);
            } else {
                HashMap<String, String> value = new HashMap<String, String>();
                value.put("fpId", session.getFPID());
                value.put("clientId", Constants.clientId);
                value.put("notificationId", alertData.get(pos)._id);
                value.put("isRead", "true");
                alertInterface.setRead(value, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        Log.i("setRead  Success---", "" + s);
                        alertData.get(pos).isRead = "true";
                        notifyDataSetChanged();
                        //google analytics
                        updateGoogleAnalytics(ruleId);
                        //update alert count
                        NotificationFragment.getAlertCount(session, alertInterface, bus);
                        //deep linking
                        if (currentUrl != null && currentUrl.trim().length() > 0)
                            linkInterface.deepLink(currentUrl);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Methods.showSnackBarNegative(appContext, "Something went wrong, please try again");
                    }
                });
            }
        }catch (Exception e){e.printStackTrace();}
    }

    private void updateGoogleAnalytics(String ruleId) {
        try{
            String appName = appContext.getPackageName();
            //http://www.google-analytics.com/collect?
            // v=1&tid=UA-35051129-20&cid="+appName+"&t=event&ec="+ruleId+"&ea=open&el="+fptag+"&cs=newsletter&cm=notification&cn="+appName+"&cm1=1
            String url = "http://www.google-analytics.com/collect?v=1&tid=UA-35051129-20&cid="
                    +appName+"&t=event&ec="+ruleId+"&ea=open&el="+
                    session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)+"&cs=newsletter&cm=notification&cn="+appName+"&cm1=1";
            new GAalerttAsyncTask(appContext,url).execute();
        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return alertData.size();
    }

    public void loadImages() {
        if (alertImageMap==null || alertImageMap.size()==0){
            alertImageMap = new HashMap<>();
            alertImageMap.put("TOPUPDATES",R.drawable.alert_category_bestupdates);
            alertImageMap.put("BLOG",R.drawable.alert_blog);
            alertImageMap.put("TESTIMONIALS",R.drawable.alert_testimonial);
            alertImageMap.put("MAINTENANCE",R.drawable.alert_error);
            alertImageMap.put("SOCIAL",R.drawable.alert_facbook);
            alertImageMap.put("PRODUCTUDPDATE",R.drawable.alert_product_gallery);
            alertImageMap.put("LOGO",R.drawable.alert_logo);
            alertImageMap.put("SEARCHQUERIES",R.drawable.alert_search);
            alertImageMap.put("SHARE",R.drawable.alert_share);
            alertImageMap.put("ENQUIRIES",R.drawable.alert_enq);
            alertImageMap.put("UPDATE",R.drawable.alert_update);
            alertImageMap.put("ACCOUNTINFO",R.drawable.alert_info);
            alertImageMap.put("STORE",R.drawable.alert_store);
            alertImageMap.put("VISITS",R.drawable.alert_visit);
            alertImageMap.put("PROFILE",R.drawable.alert_business_description);
            alertImageMap.put("TOPSITES",R.drawable.alert_category_bestupdates);
            alertImageMap.put("KEYWORDS",R.drawable.alert_keyword);
            alertImageMap.put("TIMELINE",R.drawable.alert_default);
            alertImageMap.put("FEATUREDIMAGE",R.drawable.alert_logo);
            alertImageMap.put("REFER",R.drawable.alert_refer);
            alertImageMap.put("DOTCOM",R.drawable.alert_dot_com);
            alertImageMap.put("BIZAROUNDYOU",R.drawable.alert_store);
        }
    }
}