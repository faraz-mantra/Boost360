package com.nowfloats.NavigationDrawer;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.viewHolder.MyViewHolder;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NowFloatsDev on 09/03/2015.
 */
public class CardAdapter_V3 extends RecyclerView.Adapter<MyViewHolder> {

    private static final int STORAGE_CODE = 120;
    private final int VIEW_TYPE_WELCOME = 0;
    private final int VIEW_TYPE_IMAGE_TEXT = 1;
    String imageUri = "";

    private LayoutInflater mInflater;
    public Activity appContext;
//    public HomeActivity appContext;
    FloatsMessageModel data;
    String msg = "", date = "";
    private boolean imagePresent;
    UserSessionManager session;
    Target targetMap = null;

    static ProgressDialog pd;

    public interface Permission {
        void getPermission();
    }

    public CardAdapter_V3(Activity appContext, UserSessionManager session) {
        Log.d("CardAdapter_V3", "Constructor");
        this.appContext =  appContext;
//        this.appContext = (HomeActivity) appContext;
        this.session = session;
        mInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView;
        MyViewHolder.WelcomeViewHolder welcomeViewHolder = null;
        MyViewHolder.Image_Text_ViewHolder image_text_viewHolder = null;

        if (viewType == VIEW_TYPE_WELCOME) {

            convertView = mInflater.inflate(R.layout.card_welcome, parent, false);
            welcomeViewHolder = new MyViewHolder.WelcomeViewHolder(convertView);

            if (Home_Main_Fragment.emptyMsgLayout != null)
                Home_Main_Fragment.emptyMsgLayout.setVisibility(View.GONE);

            return welcomeViewHolder;
        } else {
            convertView = mInflater.inflate(R.layout.cards_layout, parent, false);
            image_text_viewHolder = new MyViewHolder.Image_Text_ViewHolder(convertView);

            convertView.setOnClickListener(Home_Main_Fragment.myOnClickListener);
            return image_text_viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Log.d("CardAdapter_V3", "onBindViewHolder");
        if (holder instanceof MyViewHolder.WelcomeViewHolder) {
            TextView welcomeTextView = holder.welcomeTextView;
            TextView congratsTextView = holder.congratsTitleTextView;
            Typeface robotoMedium = Typeface.createFromAsset(appContext.getAssets(), "Roboto-Medium.ttf");
            Typeface robotoLight = Typeface.createFromAsset(appContext.getAssets(), "Roboto-Light.ttf");

            ImageView welcomeScreenShowWebsite = holder.welcomeScreenCreateWebsiteImage;
            PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(appContext.getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_OUT);
            welcomeScreenShowWebsite.setColorFilter(whiteLabelFilter);

            welcomeTextView.setTypeface(robotoMedium);
            congratsTextView.setTypeface(robotoLight);

            ImageView cancelImageView = holder.cancelCardImageView;
            final CardView initialCard = holder.initialCardView;
            if (Home_Main_Fragment.emptyMsgLayout != null)
                Home_Main_Fragment.emptyMsgLayout.setVisibility(View.GONE);

            // Button showWebSiteButton = holder.showWebSiteButton;
            cancelImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MixPanelController.track(EventKeysWL.SHOW_MOBILE_WEB_SITE_CANCEL, null);
                    Constants.isWelcomScreenToBeShown = false;
                    initialCard.setVisibility(View.GONE);

                    if (Home_Main_Fragment.getMessageList(appContext) != null && Home_Main_Fragment.getMessageList(appContext).size() == 0) {
                        if (Home_Main_Fragment.emptyMsgLayout != null)
                            Home_Main_Fragment.emptyMsgLayout.setVisibility(View.VISIBLE);
                    }
                }
            });

            Button showWebSiteButton = holder.showWebSiteButton;
            showWebSiteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MixPanelController.track(EventKeysWL.SHOW_MOBILE_WEB_SITE, null);
                    String url = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
                    if (!Util.isNullOrEmpty(url)) {
                        url = url.toLowerCase();
                    } else {
                        url = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                                + appContext.getResources().getString(R.string.tag_for_partners);
                    }

                    Constants.isWelcomScreenToBeShown = false;

                    initialCard.setVisibility(View.GONE);
                    Intent showWebSiteIntent = new Intent(appContext, Mobile_Site_Activity.class);
                    showWebSiteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    showWebSiteIntent.putExtra("WEBSITE_NAME", url);
                    appContext.startActivity(showWebSiteIntent);

                    if (Home_Main_Fragment.getMessageList(appContext) != null && Home_Main_Fragment.getMessageList(appContext).size() == 0) {
                        if (Home_Main_Fragment.emptyMsgLayout != null)
                            Home_Main_Fragment.emptyMsgLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            final TextView textView1 = holder.textView;
            TextView dateText = holder.dateText;
            ImageView imageView = holder.imageView;
            ImageView shareImageView = holder.shareImageView;
            ImageView shareFacebook = holder.share_facebook;
            ImageView shareWhatsapp = holder.share_whatsapp;

            final String imageShare = Home_Main_Fragment.getMessageList(appContext).get(position).imageUri;

            shareFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    shareContent("facebook", imageShare, position);


                }
            });

            /*try {


                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                sendIntent.setPackage(getString(R.string.whatsapp_package));
                appContext.startActivity(sendIntent);
            }
            catch (Exception e)
            {

            }*/
            shareWhatsapp.setOnClickListener(v -> {

                shareContent("whatsapp", imageShare, position);


            });

            shareImageView.setOnClickListener(v -> {


                shareContent("default", imageShare, position);


            });

            if (Constants.isWelcomScreenToBeShown) {
                Constants.isWelcomScreenToBeShown = false;
                data = Home_Main_Fragment.getMessageList(appContext).get(position - 1);
            } else {
                data = Home_Main_Fragment.getMessageList(appContext).get(position);
            }

            try {
                if (data != null) {
                    msg = data.message;
                    date = Methods.getFormattedDate(data.createdOn);
                    imageUri = data.tileImageUri;

                    String baseName = "";
                    textView1.setText(msg);
                    dateText.setText(date);

                    if (Util.isNullOrEmpty(imageUri) || imageUri.contains("deal.png")) {
                        imagePresent = false;
                        imageView.setVisibility(View.GONE);
                    } else if (imageUri.contains("BizImages")) {
                        imagePresent = true;
                        imageView.setVisibility(View.VISIBLE);
                        baseName = Constants.BASE_IMAGE_URL + imageUri;
                        Picasso.get().load(baseName)/*.resize(450, 450)*/.placeholder(R.drawable.default_product_image).into(imageView);
//                        imageLoader.displayImage(baseName,imageView,options);
                    } else if (imageUri.contains("/storage/emulated") || imageUri.contains("/mnt/sdcard")) {
                        imagePresent = true;

                        imageView.setVisibility(View.VISIBLE);
                        Bitmap bmp = Util.getBitmap(imageUri, appContext);
                        imageView.setImageBitmap(bmp);
                    } else {
                        imagePresent = true;
                        imageView.setVisibility(View.VISIBLE);
                        baseName = imageUri;
                        Picasso.get().load(baseName)/*.resize(450, 450)*/.placeholder(R.drawable.default_product_image).into(imageView);
//                        imageLoader.displayImage(baseName,imageView,options);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
    }

    private void shareIntentToPackages(String type, String subject, Uri uri) {
        List<Intent> targetShareIntents = new ArrayList<Intent>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType(type);
        List<ResolveInfo> resInfos = appContext.getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfos.isEmpty()) {
            System.out.println("Have packages");
            for (ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;
                Log.i("Package Name", packageName);
                if (!packageName.contains("com.twitter.android") && !packageName.contains("com.facebook.katana")) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType(type);
                    intent.putExtra(Intent.EXTRA_TEXT, subject);
                    if (uri != null)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

                    intent.setPackage(packageName);
                    targetShareIntents.add(intent);
                }
            }
            if (!targetShareIntents.isEmpty()) {
                System.out.println("Have Intent");
                Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), appContext.getString(R.string.no_app_to_share));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                appContext.startActivity(chooserIntent);
            }
        }
    }


    public static void filterByPackageName(Context context, Intent intent, String prefix) {
        List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith(prefix)) {
                intent.setPackage(info.activityInfo.packageName);
                return;
            }
        }
    }


    private List<String> getShareApplication() {
        List<String> mList = new ArrayList<String>();
        mList.add("com.facebook.katana");
        mList.add("com.twitter.android");
        mList.add("com.google.android.gm");
        return mList;

    }

    private void Share(List<String> PackageName, String Text) {
        try {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            List<ResolveInfo> resInfo = appContext.getPackageManager().queryIntentActivities(share, 0);
            if (!resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {
                    Intent targetedShare = new Intent(android.content.Intent.ACTION_SEND);
                    targetedShare.setType("text/plain"); // put here your mime type
                    if (PackageName.contains(info.activityInfo.packageName.toLowerCase())) {
                        targetedShare.putExtra(Intent.EXTRA_TEXT, Text);
                        targetedShare.setPackage(info.activityInfo.packageName.toLowerCase());
                        targetedShareIntents.add(targetedShare);
                    }
                }
                Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), appContext.getString(R.string.share));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                appContext.startActivity(chooserIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (Constants.isWelcomScreenToBeShown) {
            return VIEW_TYPE_WELCOME;
        } else {
            return VIEW_TYPE_IMAGE_TEXT;
        }
    }

    @Override
    public int getItemCount() {
        if (Constants.isWelcomScreenToBeShown) {
            return 1;
        } else {
            return Home_Main_Fragment.getMessageList(appContext).size();
        }
    }


    void shareContent(String type, String imageShare, int position) {
        MixPanelController.track("SharePost", null);
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Methods.showDialog(appContext, appContext.getString(R.string.storage_permission), appContext.getString(R.string.to_share_service_image_we_need_storage_permission),
                    (dialog, which) -> ActivityCompat.requestPermissions(appContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_CODE));
            return;
        }
        pd = ProgressDialog.show(appContext, "", "Sharing . . .");
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        switch (type) {
            case "whatsapp":
                shareIntent.setPackage(appContext.getString(R.string.whatsapp_package));
                break;
            case "facebook":
                shareIntent.setPackage("com.facebook.katana");
                break;
        }
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!Util.isNullOrEmpty(imageShare) && !imageShare.contains("/Tile/deal.png")) {
            if (Methods.isOnline(appContext)) {
                String url;
                if (imageShare.contains("BizImages")) {
                    url = Constants.NOW_FLOATS_API_URL + "" + imageShare;
                } else {
                    url = imageShare;
                }
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        pd.dismiss();
                        targetMap = null;
                        try {
                            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                            View view = new View(appContext);
                            view.draw(new Canvas(mutableBitmap));
                            String path = MediaStore.Images.Media.insertImage(appContext.getContentResolver(), mutableBitmap, "Nur", null);
                            BoostLog.d("Path is:", path);
                            Uri uri = Uri.parse(path);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, Home_Main_Fragment.getMessageList(appContext).get(position).message + " View more at: " +
                                    Home_Main_Fragment.getMessageList(appContext).get(position).url);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            shareIntent.setType("image/*");
                            if (shareIntent.resolveActivity(appContext.getPackageManager()) != null) {
                                appContext.startActivityForResult(Intent.createChooser(shareIntent, appContext.getString(R.string.share_updates)), 1);
                            } else {
                                Methods.showSnackBarNegative(appContext, appContext.getString(R.string.no_app_available_for_action));
                            }
                        } catch (OutOfMemoryError e) {
                            Toast.makeText(appContext, appContext.getString(R.string.image_size_is_large), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(appContext, "Image not able to share", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        pd.dismiss();
                        targetMap = null;
                        Methods.showSnackBarNegative(appContext, appContext.getString(R.string.failed_to_download_image));
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                };
                targetMap = target;
                Picasso.get().load(url).into(target);
            } else {
                pd.dismiss();
                Methods.showSnackBarNegative(appContext, appContext.getString(R.string.can_not_share_image_offline_mode));
            }
        } else {
            pd.dismiss();
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, Home_Main_Fragment.getMessageList(appContext).get(position).message + " View more at: " + Home_Main_Fragment.getMessageList(appContext).get(position).url);
            if (shareIntent.resolveActivity(appContext.getPackageManager()) != null) {
                appContext.startActivityForResult(Intent.createChooser(shareIntent, appContext.getString(R.string.share_updates)), 1);
            } else {
                Methods.showSnackBarNegative(appContext, appContext.getString(R.string.no_app_available_for_action));
            }
        }
    }
}