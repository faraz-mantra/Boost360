package com.nowfloats.NavigationDrawer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.NavigationDrawer.API.MessageTag_Async_Task;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class Card_Full_View_Fragment extends Fragment {

    public static final String ImageKey = "imageKey";
    public static final String MainTextKey = "mainText";
    public static final String DateTextKey = "dateText";
    public static final String MessageIdKey = "messageIdTag";

    static View.OnClickListener mylongOnClickListener;

    public Card_Full_View_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_card_full_view, container, false);
        Bundle bundle = getArguments();

        Typeface robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");

        Typeface robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");


        ((Card_Full_View_MainActivity) getActivity()).setActionBarTitle("Home");

        CardView cardView = (CardView) mainView.findViewById(R.id.card_view);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                List<Intent> targetShareIntents=new ArrayList<Intent>();
//                Intent shareIntent=new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.setType("text/plain");
//                List<ResolveInfo> resInfos=getActivity().getPackageManager().queryIntentActivities(shareIntent, 0);
//                if(!resInfos.isEmpty()) {
//                    System.out.println("Have package");
//                    for (ResolveInfo resInfo : resInfos) {
//                        String packageName = resInfo.activityInfo.packageName;
//                        Log.i("Package Name", packageName);
//                        if (packageName.contains("com.twitter.android") || packageName.contains("com.facebook.katana") || packageName.contains("com.instagram.android")) {
//                            Intent intent = new Intent();
//                            intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
//                            intent.setAction(Intent.ACTION_SEND);
//                            intent.setType("text/plain");
//                            intent.putExtra(Intent.EXTRA_TEXT, "Text");
//                            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//                            intent.setPackage(packageName);
//                            targetShareIntents.add(intent);
//                        }
//                    }
//                    if (!targetShareIntents.isEmpty()) {
//                        System.out.println("Have Intent");
//                        Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Choose app to share");
//                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
//                        startActivity(chooserIntent);
//                    } else {
//                        System.out.println("Do not Have Intent");
//                        //showDialaog(this);
//                    }
//
//
//                }

            }
        });



       // mylongOnClickListener = new MyLongClickListener(getActivity());


        if (bundle != null) {
            String imagePath = bundle.getString(ImageKey);
            String mainText = bundle.getString(MainTextKey);
            String dateText = bundle.getString(DateTextKey);
            String messageid = bundle.getString(MessageIdKey);

            //Log.d("Card Frag", "Card Fragment : "+imagePath+" , "+mainText+ " , "+dateText);
            //Log.d("Card Frag","Main View : "+mainView);
            setValues(mainView, imagePath, mainText, dateText,messageid);

        }

        return mainView;
    }


    public class MyLongClickListener implements View.OnClickListener {
        private final Context context;


        private MyLongClickListener(Context context)
        {
            this.context = context;
        }

        @Override
        public void onClick(View v) {

            final String[] INTENT_FILTER = new String[] {
                    "com.twitter.android",
                    "com.facebook.katana"
            };

            List<Intent> targetShareIntents=new ArrayList<Intent>();
            Intent shareIntent=new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            List<ResolveInfo> resInfos=getActivity().getPackageManager().queryIntentActivities(shareIntent, 0);
            if(!resInfos.isEmpty()) {
                System.out.println("Have package");
                for (ResolveInfo resInfo : resInfos) {
                    String packageName = resInfo.activityInfo.packageName;
                    Log.i("Package Name", packageName);
                    if (packageName.contains("com.twitter.android") || packageName.contains("com.facebook.katana") || packageName.contains("com.instagram.android")) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, "Text");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                        intent.setPackage(packageName);
                        targetShareIntents.add(intent);
                    }
                }
                if (!targetShareIntents.isEmpty()) {
                    System.out.println("Have Intent");
                    Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Choose app to share");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                    startActivity(chooserIntent);
                } else {
                    System.out.println("Do not Have Intent");
                    //showDialaog(this);
                }


            }

        }


    }


    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle("Home");

    }

    private void setValues(View mainView, String imageUri, String mainText, String dateText,String msgID) {
        //Log.d("Set Values","values  :"+imagePath+" , "+mainText+" , "+dateText);
        ImageView imageView = (ImageView) mainView.findViewById(R.id.mainImageView);
        TextView mainTextView = (TextView) mainView.findViewById(R.id.headingTextView);
        TextView dateTextView = (TextView) mainView.findViewById(R.id.dateTextView);
        TextView messageTag = (TextView) mainView.findViewById(R.id.messagetag);

        MessageTag_Async_Task tag = new MessageTag_Async_Task(getActivity(),messageTag,msgID);
        tag.execute();

        Typeface myCustomFont = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        Typeface myCustomFont_Medium = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");
        String baseName = "";

        mainTextView.setTypeface(myCustomFont);
        dateTextView.setTypeface(myCustomFont_Medium);

        mainTextView.setText(mainText);
        dateTextView.setText(dateText);

        if(Util.isNullOrEmpty(imageUri))
        {
            imageView.setVisibility(View.GONE);
        }
        else if(imageUri.contains("deal.png") || Util.isNullOrEmpty(imageUri))
        {
            imageView.setVisibility(View.GONE);
        }
        else if(imageUri.contains("BizImages") )
        {
            imageView.setVisibility(View.VISIBLE);
            baseName = "https://api.withfloats.com/" + imageUri;
            Picasso.with(getActivity()).load(baseName).noPlaceholder().into(imageView);
//                        imageLoader.displayImage(baseName,imageView,options);
        }

        else if(imageUri.contains("/storage/emulated") || imageUri.contains("/mnt/sdcard") )
        {
            imageView.setVisibility(View.VISIBLE);
            Bitmap bmp = Util.getBitmap(imageUri.toString(),getActivity());
            imageView.setImageBitmap(bmp);
        }
        else
        {
            imageView.setVisibility(View.VISIBLE);
            baseName = imageUri ;
            Picasso.with(getActivity()).load(baseName).noPlaceholder().into(imageView);
//                        imageLoader.displayImage(baseName,imageView,options);
        }
    }
}
