package com.nowfloats.NavigationDrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.GetStoreFrontImageAsyncTask;
import com.nowfloats.util.Key_Preferences;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;


/*created using Android Studio (Beta) 0.8.14
www.101apps.co.za*/

public class CardAdapter_v2 extends RecyclerView.Adapter<CardAdapter_v2.MyViewHolder> {

    private ArrayList<CardData> peopleDataSet;
    FloatsMessageModel data;
    String msg = "", date = "";
    String imageUri = "";
    private Context appContext ;
    int image_count = 0 ;
    final HashMap<String, SoftReference<Bitmap>> _cache = null;
    CardView initial_card_view;
    ImageView cancelCardView ;
    UserSessionManager session;
    //UserSessionManager session;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewEmail;
        ImageView imageViewIcon;
        CardView initial_card_view;
        ImageView cancelCardView ;
        TextView websiteNameTextView ;
        Button showWebsiteButton ;


        public MyViewHolder(View itemView) {
            super(itemView);
          //  Log.d("CardAdapter_v2","MyViewHolder");
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewEmail = (TextView) itemView.findViewById(R.id.textViewEmail);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
            this.initial_card_view = (CardView) itemView.findViewById(R.id.initial_card_view);
            this.cancelCardView = (ImageView) itemView.findViewById(R.id.cancelCardImageView);
            this.websiteNameTextView = (TextView) itemView.findViewById(R.id.card_websiteTextView);
            this.showWebsiteButton = (Button) itemView.findViewById(R.id.showWebsiteButton);
        }
    }

    public CardAdapter_v2(ArrayList<CardData> people, Activity context) {
        this.peopleDataSet = people;
        appContext = context ;
        //Log.d("CardAdapter_v2","Constructor");


       // imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        //Log.d("CardAdapter_v2","onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);

      //  Home_Main_Fragment fragment = new Home_Main_Fragment();

        view.setOnClickListener(Home_Main_Fragment.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        //Log.d("CardAdapter_v2","onBindViewHolder");
        TextView textViewName = holder.textViewName;
        TextView textViewEmail = holder.textViewEmail;
        ImageView imageView = holder.imageViewIcon;
        final CardView initialCardView = holder.initial_card_view ;
        ImageView cancelCard = holder.cancelCardView ;
        TextView websiteText = holder.websiteNameTextView;
        Button showWebSiteButtonView = holder.showWebsiteButton ;

        if (listPosition == 0 && Constants.fromLogin)
        {
            Constants.fromLogin = false ;
            initialCardView.setVisibility(View.VISIBLE);
            //Toast.makeText(appContext,"Constants.StoreWebSite : "+Constants.StoreTag,Toast.LENGTH_LONG).show();
            Log.d("CardAdapter_v2","Constants.StoreWebSite : "+ session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            websiteText.setText(Constants.StoreName);

        }
        else {
            initialCardView.setVisibility(View.GONE);
        }

        showWebSiteButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String url = "http://"+ session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG+".nowfloats.com" ;

                Intent showWebSiteIntent = new Intent(appContext,Mobile_Site_Activity.class);
                appContext.startActivity(showWebSiteIntent);

             //   String url = "http://stackoverflow.com/" ;
               // Uri uriUrl = Uri.parse(url);
               // Intent browserIntent = new Intent(Intent.ACTION_VIEW, uriUrl);
               // appContext.startActivity(browserIntent);
            }
        });

      //  final ImageView cancelCardImageView = (ImageView) holder.findViewById(R.id.cancelCardImageView);
        cancelCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialCardView.setVisibility(View.GONE);
            }
        });

        Typeface myCustomFont = Typeface.createFromAsset(appContext.getAssets(),"Roboto-Light.ttf");
        textViewName.setTypeface(myCustomFont);
        textViewEmail.setTypeface(myCustomFont);

       // textViewName.setTextSize(24);
       // textViewEmail.setTextSize(18);

       textViewName.setTextColor(appContext.getResources().getColor(R.color.home_view_card_main_text_color));
        textViewEmail.setTextColor(appContext.getResources().getColor(R.color.home_view_card_date_text_color));

       // Log.d("CardAdapter_v2","Constants.StorebizFloats : "+Constants.StorebizFloats);
       // JSONArray newData = Constants.StorebizFloats;//session.getFPMessages();
       // if (newData != null) {


        //Log.d("Store biz ","Store Biz : "+Constants.StorebizFloats.get(1));
        data = HomeActivity.StorebizFloats.get(listPosition);
//            try {
//                data = newData.getJSONObject(listPosition);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            try {
            if (data != null) {
                msg = data.message;
                date = data.createdOn;
                imageUri = data.tileImageUri;

//                if(imageUri != null)
//                    imageView.setVisibility(View.VISIBLE);
                if (!imageUri.contains("deal.png") ) {
                    //  Log.d("Image URI","Image URI : "+imageUri);
                    if(Util.isNullOrEmpty(imageUri))
                    {
                        imageView.setVisibility(View.GONE);
                    } else {
                        imageView.setVisibility(View.VISIBLE);
                    }
                } else {
                    imageView.setVisibility(View.GONE);
                }

                String baseName = Constants.BASE_IMAGE_URL+"" + imageUri;
                textViewName.setText(msg);
                textViewEmail.setText(date);

                if (imageUri.contains("/storage/emulated")) {
                    Bitmap bmp = BitmapFactory.decodeFile(imageUri);
                       imageView.setImageBitmap(bmp);
                } else {
                    Picasso.get().load(baseName).into(imageView);
                    //imageLoader.displayImage(baseName, imageView, options);
                }

                image_count++;
                Log.d("Messages : ", "msg  "+msg+" , Date : "+date+" , picimageURI : "+imageUri);
                Log.d("Messages","**********************************************");
            }
        } catch (Exception e) {


        }

    }

    private Bitmap getBitmapImage(String id) {
        SoftReference<Bitmap> reference = _cache.get(id);
        Bitmap bitmap = null;
        if(reference != null) {
            // The bitmap is cached with SoftReference
            bitmap = reference.get();
        }
        Log.d("Bitmap","Bitmap : "+bitmap+" , "+id);
        return bitmap;
    }

    private void saveImagebitmap(Bitmap bmp, String key) {

        Log.d("imagebitmap","key : "+key+" bmp : "+bmp);

        _cache.put(key, new SoftReference<>(bmp));

    }

    private void InitiateDownload(Context appContext, String imageUri) {
        GetStoreFrontImageAsyncTask sfimg = new GetStoreFrontImageAsyncTask((HomeActivity)appContext, imageUri);
        sfimg.execute();
    }

    @Override
    public int getItemCount() {
       // Log.d("CardAdapter_v2","getItemCount : "+Constants.StorebizFloats.size());
        return HomeActivity.StorebizFloats.size();
    }
}
