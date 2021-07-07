package com.nowfloats.Business_Enquiries;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.Business_Enquiries.Model.Business_Enquiry_Model;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.Mobile_Site_Activity;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.GetStoreFrontImageAsyncTask;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.framework.webengageconstant.EventLabelKt.CLICKED_REPLY_AS_CALL;
import static com.framework.webengageconstant.EventLabelKt.CLICKED_REPLY_AS_EMAIL;
import static com.framework.webengageconstant.EventNameKt.BUSINESS_ENQUIRIES_CALL;
import static com.framework.webengageconstant.EventNameKt.BUSINESS_ENQUIRIES_EMAIL;
import static com.framework.webengageconstant.EventValueKt.NULL;


/*created using Android Studio (Beta) 0.8.14
www.101apps.co.za*/

public class Business_CardAdapter extends RecyclerView.Adapter<Business_CardAdapter.MyViewHolder> {

    final HashMap<String, SoftReference<Bitmap>> _cache = null;
    Business_Enquiry_Model data;
    PorterDuffColorFilter whiteLabelFilter;
    String headerValue;
    private Context appContext;

    public Business_CardAdapter(Context context) {
        appContext = context;
        whiteLabelFilter = new PorterDuffColorFilter(appContext.getResources().getColor(R.color.primaryColor), PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business_enquires_cards_layout, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view, appContext);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        TextView fromTextView = holder.fromTextView;
        TextView dateTextView = holder.dateTextView;
        TextView queryTextView = holder.queryTextView;
        TextView contactText = holder.contactText;
        TextView entityText = holder.entityTexView;
        LinearLayout sentByLinear = holder.sentByLinearLayout;
        holder.entityLayout.setVisibility(View.GONE);
        holder.entityLayout.setTag(listPosition + "");
//        Typeface myCustomFont = Typeface.createFromAsset(appContext.getAssets(),"Roboto-Medium.ttf");
//        Typeface myCustomFontLight = Typeface.createFromAsset(appContext.getAssets(),"Roboto-Light.ttf");

//        fromTextView.setTypeface(myCustomFont);
//        dateTextView.setTypeface(myCustomFontLight);
//        queryTextView.setTypeface(myCustomFontLight);
//        contactText.setTypeface(myCustomFont);
//        entityText.setTypeface(myCustomFontLight);

        BoostLog.d("$$$$$$", "Biz Data : " + listPosition + " Data : " + Constants.StorebizQueries.size());
        data = Constants.StorebizQueries.get(listPosition);

        try {
            String email = data.contact;
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher m = p.matcher(email);
            boolean matchFound = m.matches();
            if (matchFound) {
//                Drawable img = appContext.getResources().getDrawable( R.drawable.ic_mail_white_48dp );
//                img.setBounds( 0, 0, 60, 60 );
//                contactText.setCompoundDrawables( img, null, null, null );
//                holder.contactIcon.setImageResource(R.drawable.ic_mail_white_48dp);
                contactText.setText(appContext.getResources().getString(R.string.email));
                fromTextView.setText(data.contact);
//                holder.setIsRecyclable(false);
            } else {
//                holder.contactIcon.setImageResource(R.drawable.ic_call_white_48dp);
                contactText.setText(appContext.getResources().getString(R.string.call));
                fromTextView.setText(data.contact);
            }
            dateTextView.setText(data.createdOn);

            Log.d("DATE_FORMAT_CHECK", data.createdOn);

            queryTextView.setText("\"" + data.message + "\"");
            if (data.entityType.equalsIgnoreCase("CHATBOTENQUIRY")) {
                sentByLinear.setVisibility(View.VISIBLE);
            } else {
                sentByLinear.setVisibility(View.GONE);
            }
            try {
                if (data.entityMessage != null && !data.entityMessage.equals("null") && data.entityMessage.trim().length() > 0) {
                    holder.entityLayout.setVisibility(View.VISIBLE);
//                    SpannableString content = new SpannableString("In response to your update: '"+ data.entityMessage +"'");
//                    content.setSpan(new UnderlineSpan(), 20, 26, 0);
                    SpannableString content = new SpannableString("\"" + data.entityMessage + "\"");
                    content.setSpan(new UnderlineSpan(), 0, data.entityMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    entityText.setText(content);

                    holder.entityLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String value = "";
                            int pos = Integer.parseInt(v.getTag().toString());
                            Business_Enquiry_Model Cur_data = Constants.StorebizQueries.get(pos);
                            value = Cur_data.entityMessage;
//                            Intent i = new Intent(appContext,BusinessEnqUpdateDetails.class);
//                            i.putExtra("key",value);
                            if (Cur_data.entityUrl != null && !Cur_data.entityUrl.equals("null") && Cur_data.entityUrl.length() > 0) {
                                Intent i = new Intent(appContext, Mobile_Site_Activity.class);
                                i.putExtra("WEBSITE_NAME", Cur_data.entityUrl);
                                i.putExtra("tag", Cur_data.entityUrl);
                                appContext.startActivity(i);
                                /*Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(Cur_data.entityUrl));
                                appContext.startActivity(i);*/
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            holder.setIsRecyclable(false);

            holder.contactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getId();
                    headerValue = (String) holder.fromTextView.getText();
                    Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
                    Matcher m = p.matcher(headerValue);
                    boolean matchFound = m.matches();

                    if (matchFound) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", headerValue, null));
                        appContext.startActivity(Intent.createChooser(emailIntent, appContext.getResources().getString(R.string.send_email)));
                        WebEngageController.trackEvent(BUSINESS_ENQUIRIES_EMAIL, CLICKED_REPLY_AS_EMAIL, NULL);
                    } else {
                        Intent call = new Intent(Intent.ACTION_DIAL);
                        call.setData(Uri.parse("tel:" + headerValue));
                        appContext.startActivity(call);
                        WebEngageController.trackEvent(BUSINESS_ENQUIRIES_CALL, CLICKED_REPLY_AS_CALL, NULL);
                    }
                }
            });
            BoostLog.d("Adapter Data", "Adapter Data : " + data.contact + " , " + data.createdOn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapImage(String id) {
        SoftReference<Bitmap> reference = _cache.get(id);
        Bitmap bitmap = null;
        if (reference != null) {
            // The bitmap is cached with SoftReference
            bitmap = reference.get();
        }
        BoostLog.d("Bitmap", "Bitmap : " + bitmap + " , " + id);
        return bitmap;
    }

    private void saveImagebitmap(Bitmap bmp, String key) {

        BoostLog.d("imagebitmap", "key : " + key + " bmp : " + bmp);

        _cache.put(key, new SoftReference<>(bmp));

    }

    private void InitiateDownload(Context appContext, String imageUri) {
        GetStoreFrontImageAsyncTask sfimg = new GetStoreFrontImageAsyncTask((HomeActivity) appContext, imageUri);
        sfimg.execute();
    }

    @Override
    public int getItemCount() {
        return Constants.StorebizQueries.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fromTextView;
        TextView dateTextView;
        TextView queryTextView;
        TextView contactText;
        //        ImageView contactIcon;
        TextView entityTexView, samTextView;
        LinearLayout entityLayout, sentByLinearLayout;
        FrameLayout contactButton;

        public MyViewHolder(View itemView, final Context appContext) {
            super(itemView);
            this.fromTextView = (TextView) itemView.findViewById(R.id.fromTextView);
            this.dateTextView = (TextView) itemView.findViewById(R.id.enquiry_dateTextView);
            this.queryTextView = (TextView) itemView.findViewById(R.id.queryTexView);
            this.contactText = (TextView) itemView.findViewById(R.id.contactText);
            this.entityTexView = (TextView) itemView.findViewById(R.id.entityTexView);
//            this.contactIcon = (ImageView)itemView.findViewById(R.id.contact_icon);
            this.contactButton = (FrameLayout) itemView.findViewById(R.id.contactButton);
            this.entityLayout = (LinearLayout) itemView.findViewById(R.id.entity_layout);
            this.samTextView = itemView.findViewById(R.id.samTextView);
            sentByLinearLayout = itemView.findViewById(R.id.sent_byLinearLayout);
            samTextView.setText(Html.fromHtml("<u>Sam</u>"));
            samTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Dialog dialog = new Dialog(appContext);
                    dialog.setContentView(R.layout.dialog_sam);
                    dialog.setTitle("");

                    dialog.show();
                }
            });
        }
    }
}
