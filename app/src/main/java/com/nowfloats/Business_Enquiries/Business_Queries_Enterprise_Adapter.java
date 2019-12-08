package com.nowfloats.Business_Enquiries;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.Business_Enquiries.Model.Entity_model;
import com.nowfloats.NavigationDrawer.CardData;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Business_Queries_Enterprise_Adapter extends RecyclerView.Adapter<Business_Queries_Enterprise_Adapter.MyViewHolder> {

    private ArrayList<CardData> peopleDataSet;
    Entity_model data;
    private Context appContext ;
    final HashMap<String, SoftReference<Bitmap>> _cache = null;
    PorterDuffColorFilter whiteLabelFilter;
    String headerValue;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fromTextView;
        TextView dateTextView;
        TextView queryTextView;
        TextView contactText;
        LinearLayout entityLayout;
        FrameLayout contactButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.fromTextView = (TextView) itemView.findViewById(R.id.fromTextView);
            this.dateTextView = (TextView) itemView.findViewById(R.id.enquiry_dateTextView);
            this.queryTextView = (TextView) itemView.findViewById(R.id.queryTexView);
            this.contactText = (TextView) itemView.findViewById(R.id.contactText);
            this.contactButton = (FrameLayout) itemView.findViewById(R.id.contactButton);
            this.entityLayout = (LinearLayout)itemView.findViewById(R.id.entity_layout);
        }
    }

    public Business_Queries_Enterprise_Adapter(Context context) {
        appContext = context ;
        whiteLabelFilter = new PorterDuffColorFilter(appContext.getResources().getColor(R.color.primaryColor), PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business_enquires_cards_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView fromTextView = holder.fromTextView;
        TextView dateTextView = holder.dateTextView;
        TextView queryTextView = holder.queryTextView;
        TextView contactText = holder.contactText;
        holder.entityLayout.setVisibility(View.GONE);
//        Typeface myCustomFont = Typeface.createFromAsset(appContext.getAssets(),"Roboto-Medium.ttf");
//        Typeface myCustomFontLight = Typeface.createFromAsset(appContext.getAssets(),"Roboto-Light.ttf");

//        fromTextView.setTypeface(myCustomFont);
//        dateTextView.setTypeface(myCustomFontLight);
//        queryTextView.setTypeface(myCustomFontLight);
//        contactText.setTypeface(myCustomFont);

        BoostLog.d("$$$$$$","Biz Data : "+listPosition+" Data : "+ Constants.StorebizEnterpriseQueries.size());
        data = Constants.StorebizEnterpriseQueries.get(listPosition);

        try {
            String email =data.Phone;
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher m = p.matcher(email);
            boolean matchFound = m.matches();
            if (matchFound) {
//                Drawable img = appContext.getResources().getDrawable( R.drawable.ic_action_email );
//                img.setBounds( 0, 0, 60, 60 );
//                contactText.setCompoundDrawables( img, null, null, null );
                contactText.setText(appContext.getResources().getString(R.string.email));
                fromTextView.setText(data.Phone);
                holder.setIsRecyclable(false);
            }else {
                fromTextView.setText("+"+Constants.StoreCountryCode+data.Phone);
            }
            dateTextView.setText(data.CreatedDate);

            Log.d("DATE_FORMAT_CHECK", data.CreatedDate);

            queryTextView.setText("\""+data.Message+"\"");
            holder.setIsRecyclable(false);

            holder.contactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getId();
                    headerValue = (String) holder.fromTextView.getText();
                    Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
                    Matcher m = p.matcher(headerValue);
                    boolean matchFound = m.matches();
                    if(matchFound){

                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", headerValue, null));
                        appContext.startActivity(Intent.createChooser(emailIntent, appContext.getResources().getString(R.string.send_email)));

                    }else{
                        Intent call = new Intent(Intent.ACTION_DIAL);
                        call.setData(Uri.parse("tel:"+headerValue));
                        appContext.startActivity(call);
                    }
                }
            });
            BoostLog.d("Adapter Data","Adapter Data : "+data.Phone+" , "+data.CreatedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {

        return Constants.StorebizEnterpriseQueries.size();
    }
}
