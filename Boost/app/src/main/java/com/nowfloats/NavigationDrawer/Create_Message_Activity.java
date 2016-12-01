package com.nowfloats.NavigationDrawer;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nowfloats.BusinessProfile.UI.UI.Social_Sharing_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.PostModel;
import com.nowfloats.NavigationDrawer.model.UploadPostEvent;
import com.nowfloats.NotificationCenter.AlertArchive;
import com.nowfloats.Twitter.TwitterConstants;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DataBase;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;


public class Create_Message_Activity extends AppCompatActivity {
    private Toolbar toolbar;
    ImageView cameraButton ;
    ContentValues values;
    Bitmap CameraBitmap;
    Uri imageUri;
    public static String path = "";
    public static String[] keywords = null;
    ImageView facebookShare ;
    ArrayList<String> items;
    boolean[] checkedPages;
    UserSessionManager session;
    String tagName = "";
    public static PostModel postUser = null;
    public static PostModel postPage = null;
    int size = 0;
    EditText msg;
    Bitmap bmp;
    public static boolean tosubscribers = false;
    private ImageView imageIconButton;
    private SharedPreferences pref = null;
    SharedPreferences.Editor prefsEditor;
    private ImageView facebookPageShare;
    private ImageView twitterloginButton,create_message_subscribe_button;
    public static boolean imageIconButtonSelected = false;
    private TextView post;
    private boolean isfirstTimeFacebook = false;
    private boolean isFirstTimeFacebookPage = false;
    public static boolean isFirstTimeTwitter = false;
    private boolean isFirstTimeSendToSubscriber = false;
//    public static String page_access_token;
    public static boolean facbookEnabled = false ;
    public static boolean twittersharingenabled = false ;
    public static boolean isFacebookPageShareLoggedIn = false;
    public static final int ACTION_REQUEST_IMAGE_EDIT = 110;
//    File mGalleryFolder;
//    private static final String FOLDER_NAME = "aviary-sample";
    String mOutputFilePath;
    Uri picUri;
    private Activity activity;
    DataBase dataBase;
    private final int gallery_req_id = 0;
    private final int media_req_id = 1;
    private SharedPreferences mSharedPreferences = null;

    private int mFbPageShare = 0, mFbProfileShare = 0, mTwitterShare = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__message_v2);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        activity = Create_Message_Activity.this;
        Methods.isOnline(activity);
        session = new UserSessionManager(getApplicationContext(),Create_Message_Activity.this);
        dataBase = new DataBase(activity);
        LinearLayout socialSharingIconLayout = (LinearLayout) findViewById(R.id.socialSharingIconLayout);

        TextView shareText = (TextView) findViewById(R.id.shareTextView);
        tagName = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG);
        if(session.getISEnterprise().equals("true"))
        {
            socialSharingIconLayout.setVisibility(View.GONE);
            shareText.setVisibility(View.GONE);
        }

//        Session fbsession = Session.getActiveSession();
//        if (fbsession == null) {
//            if (savedInstanceState != null) {
//                fbsession = Session.restoreSession(this, null, statusCallback, savedInstanceState);
//            }
//            if (fbsession == null) {
//                fbsession = new Session(this);
//            }
//            Session.setActiveSession(fbsession);
//            if (fbsession.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
//                fbsession.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
//            }
//        }


        msg = (EditText) findViewById(R.id.createMessageEditText);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        post = (TextView) toolbar.findViewById(R.id.saveTextView);
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getString(R.string.post_update));
        post.setText(getString(R.string.post_in_capital));

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Methods.isOnline(Create_Message_Activity.this)){
                    //Methods.showSnackBarNegative(Create_Message_Activity.this, "");
                    return;
                }
                if(!Util.isNullOrEmpty(msg.getText().toString())) {
                    Methods.hideKeyboard(msg, Create_Message_Activity.this);
                    // MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_TEXT,null);
                    if (!imageIconButtonSelected) {
                        // Log.d("Create_Message_Activity","imageIconButton : "+imageIconButton);
                        MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_IMAGE_NOT_SELECTED, null);
                        Constants.imageNotSet = true;
                        HashMap<String, String> eventKey = new HashMap<String, String>();
                        eventKey.put(EventKeysWL.CREATE_MESSAGE_ACTIVITY_IMAGE_NOT_SELECTED, "Posted Update with only text");
                        //ContextSdk.tagEvent(Create_Message_Activity.class.getCanonicalName(),eventKey);
                    } else {
                        // Log.d("Create_Message_Activity","imageIconButton : "+imageIconButton);
                        MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_IMAGE_SELECTED, null);
                        Constants.imageNotSet = false;
                        //imageIconButtonSelected = false;
                        HashMap<String, String> eventKey = new HashMap<String, String>();
                        eventKey.put(EventKeysWL.CREATE_MESSAGE_ACTIVITY_IMAGE_SELECTED, "Posted Update with text and image");
                        //ContextSdk.tagEvent(Create_Message_Activity.class.getCanonicalName(),eventKey);
                    }
                    // MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_POST,null);
                    if (Home_Main_Fragment.bus==null){Home_Main_Fragment.bus = BusProvider.getInstance().getBus();}
                    Constants.createMsg = true;
                    String socialShare = "";
                    if(mFbProfileShare==1){
                        socialShare +="FACEBOOK.";
                        //String.valueOf(mFbProfileShare) + "." + String.valueOf(mFbPageShare) + "." + String.valueOf(mTwitterShare);
                    }
                    if(mFbPageShare==1){
                        socialShare +="FACEBOOK_PAGE.";
                    }
                    if (mTwitterShare == 1) {
                        socialShare+="TWITTER.";
                    }
                    Home_Main_Fragment.bus.post(new UploadPostEvent(path, msg.getText().toString(), socialShare));
                    if (path!=null && path.trim().length()>0){
                        new AlertArchive(Constants.alertInterface,"FEATURE IMAGE",session.getFPID());
                    }else{
                        new AlertArchive(Constants.alertInterface,"UPDATE",session.getFPID());
                    }
                    finish();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } else {
                    YoYo.with(Techniques.Shake).playOn(msg);
                    Methods.showSnackBarNegative(Create_Message_Activity.this,getString(R.string.enter_message));
                }
            }
        });

        pref = getSharedPreferences(Constants.PREF_NAME,Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();

        facebookShare = (ImageView) findViewById(R.id.create_message_activity_facebokhome_button);
        facebookPageShare = (ImageView) findViewById(R.id.create_message_activity_facebokpage_button);
        twitterloginButton = (ImageView) findViewById(R.id.create_message_activity_twitter_button);

        if(!Util.isNullOrEmpty(session.getFacebookName())) {
            facbookEnabled = true;
            mFbProfileShare = 1;
            facebookShare.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon));
        }

        if(!Util.isNullOrEmpty(session.getFacebookPage())) {
            isFacebookPageShareLoggedIn = true;
            mFbPageShare = 1;
            facebookPageShare.setImageDrawable(getResources().getDrawable(R.drawable.facebook_page));
        }
        mSharedPreferences = getSharedPreferences(TwitterConstants.PREF_NAME, MODE_PRIVATE);
        if(mSharedPreferences.getBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN, false)) {
            twitterloginButton.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_active));
            Constants.twitterShareEnabled = true;
            mTwitterShare = 1;
        }else{
            Constants.twitterShareEnabled = false;
            mTwitterShare = 0;
            twitterloginButton.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_inactive));
        }


        twitterloginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (isFirstTimeTwitter == false)
                if(mSharedPreferences.getBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN, false)){
                    if(Constants.twitterShareEnabled){
                        Constants.twitterShareEnabled = false;
                        mTwitterShare = 0;
                        twitterloginButton.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_inactive));
                    }else {
                        Constants.twitterShareEnabled = true;
                        mTwitterShare = 1;
                        twitterloginButton.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_active));
                    }

                }else {
                    Intent i = new Intent(Create_Message_Activity.this, Social_Sharing_Activity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }

                /*if (Constants.twitterShareEnabled) {
                    twitterloginButton.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_inactive));
                    Constants.twitterShareEnabled = false;
                    logoutFromTwitter();
                } else {
                    //twitterloginButton.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_inactive));
                   *//* if (twittersharingenabled) {
                        Constants.twitterShareEnabled = true ;
                        twitterloginButton.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_active));
                    } else {*//*
                    new MaterialDialog.Builder(Create_Message_Activity.this)
                            .title("Post to Twitter")
                            .content("Connect to post website updates to your Twitter Page")
                            .positiveText("Connect")
                            .negativeText("Cancel")
                            .positiveColorRes(R.color.primaryColor)
                            .negativeColorRes(R.color.light_gray)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    Constants.twitterShareEnabled = false;
                                    twittersharingenabled = false ;
                                    dialog.dismiss();
                                }
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    //if (!Constants.twitterShareEnabled) {
                                        MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_TWITTER, null);
                                         //Constants.twitterShareEnabled = true;
                                        //twittersharingenabled = true ;
                                        *//*twitterloginButton.setImageDrawable(getResources().getDrawable(
                                                R.drawable.twitter_icon_active));*//*
                                           //Constants.twitterShareEnabled = false;
                                        Intent in = new Intent(Create_Message_Activity.this, Social_Sharing_Activity.class);
                                        startActivity(in);
                                    //} else {
                                        //twitterloginButton.setImageDrawable(getResources().getDrawable(
                                                //R.drawable.twitter_icon_inactive));
                                        //Constants.twitterShareEnabled = false;
                                    //}
                                    dialog.dismiss();
                                }
                            })
                            .show();
                //}
            }*/
            }
        });


        create_message_subscribe_button = (ImageView) findViewById(R.id.create_message_subscribe_button);
        create_message_subscribe_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constants.isFirstTimeSendToSubscriber == false) {
                    Constants.isFirstTimeSendToSubscriber = true;
                    new MaterialDialog.Builder(Create_Message_Activity.this)
                            .title(getString(R.string.send_to_subscribers))
                            .content(getString(R.string.unable_to_send_website_updates_to_subscribers))
                            .positiveText(getString(R.string.enable))
                            .negativeText(getString(R.string.cancel))
                            .positiveColorRes(R.color.primaryColor)
                            .negativeColorRes(R.color.light_gray)
                            .callback(new MaterialDialog.ButtonCallback() {

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    dialog.dismiss();
                                }

                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    if (tosubscribers == true) {
                                        MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_SEND_TO_SUBSCRIBERS, null);
                                        create_message_subscribe_button.setImageDrawable(getResources().getDrawable(R.drawable.subscribe_icon));
                                        tosubscribers = false;
                                    } else {
                                        create_message_subscribe_button.setImageDrawable(getResources().getDrawable(R.drawable.subscribe_icon_selected));
                                        tosubscribers = true;
                                    }

                                    dialog.dismiss();
                                }
                            })
                            .show();

                }else {
                    if (tosubscribers == true) {
                        MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_SEND_TO_SUBSCRIBERS, null);
                        create_message_subscribe_button.setImageDrawable(getResources().getDrawable(R.drawable.subscribe_icon));
                        tosubscribers = false;
                    } else {
                        create_message_subscribe_button.setImageDrawable(getResources().getDrawable(R.drawable.subscribe_icon_selected));
                        tosubscribers = true;
                    }
                }
            }
        });

        facebookPageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // boolean isFacebookPageShareLoggedIn = false;
                if(!Util.isNullOrEmpty(session.getFacebookPage())){
                    if(mFbPageShare==1){
                        mFbPageShare = 0;
                        facebookPageShare.setImageDrawable(getResources().getDrawable(R.drawable.facebookpage_icon_inactive));
                    }else {
                        mFbPageShare = 1;
                        facebookPageShare.setImageDrawable(getResources().getDrawable(R.drawable.facebook_page));
                    }

                }else {
                    Intent i = new Intent(Create_Message_Activity.this, Social_Sharing_Activity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
                /*if(!isFacebookPageShareLoggedIn)
                {

                    MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_FACEBOOK_PAGE, null);

                    if (Constants.FbPageList != null) {
                        // session.storeFacebookPage("");
                        facebookPageShare.setImageDrawable(getResources().getDrawable(R.drawable.facebookpage_icon_inactive));
                        Constants.FbPageList = null;
                        Constants.fbShareEnabled = false;

                    } else if (Util.isNullOrEmpty(session.getFacebookPage())) {
                        new MaterialDialog.Builder(Create_Message_Activity.this)
                                .title("Post to Facebook Page")
                                .content("Connect to post website updates to your Facebook Page.")
                                .positiveText("Connect")
                                .negativeText("Cancel")
                                .positiveColorRes(R.color.primaryColor)
                                .negativeColorRes(R.color.light_gray)
                                .callback(new MaterialDialog.ButtonCallback() {

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        isFacebookPageShareLoggedIn = true ;
                                        fbPageData();
                                        dialog.dismiss();
                                    }
                                })
                                .show();


                    } else {
                        isFacebookPageShareLoggedIn = true;
                        facebookPageShare.setImageDrawable(getResources().getDrawable(
                                R.drawable.facebook_page));
                    }
                } else {
                    isFacebookPageShareLoggedIn = false;
                    facebookPageShare.setImageDrawable(getResources().getDrawable(R.drawable.facebookpage_icon_inactive));
                }*/
            }
        });

        /*
         *Calling App Ice Code
         */
        //AppICECode();

        facebookShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Util.isNullOrEmpty(session.getFacebookName())){
                    if(mFbProfileShare==1){
                        mFbProfileShare = 0;
                        facebookShare.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon_inactive));
                    }else {
                        mFbProfileShare = 1;
                        facebookShare.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon));
                    }

                }else {
                    Intent i = new Intent(Create_Message_Activity.this, Social_Sharing_Activity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
                /*if (Util.isNullOrEmpty(session.getFacebookName())) {
                    if (facbookEnabled) {
                        facbookEnabled = false ;
                        facebookShare.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon_inactive));
                        Constants.fbShareEnabled = false;
                        Constants.FACEBOOK_USER_ID = "";
                        Constants.FACEBOOK_USER_ACCESS_ID = "";

                    } else if (Util.isNullOrEmpty(session.getFacebookName())) {
                        new MaterialDialog.Builder(Create_Message_Activity.this)
                                .title("Post to Facebook")
                                .content("Connect to post website updates to your Facebook Profile Page.")
                                .positiveText("Connect")
                                .negativeText("Cancel")
                                .positiveColorRes(R.color.primaryColor)
                                .negativeColorRes(R.color.light_gray)
                                .callback(new MaterialDialog.ButtonCallback() {

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);

                                        fbData();
                                        //  }
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        fbData();
                    }
                    MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_FACEBOOK_SELECTED, null);
                } else {
                    if (facbookEnabled) {
                        facbookEnabled = false ;
                        facebookShare.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon_inactive));
                        Constants.fbShareEnabled = false;
                        Constants.FACEBOOK_USER_ID = "";
                        Constants.FACEBOOK_USER_ACCESS_ID = "";
                    } else {
                        facbookEnabled = true ;
                        facebookShare.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon));
                    }
                }*/
            }
        });

        cameraButton = (ImageView) findViewById(R.id.create_mee_activity_facebokhome_button);
        imageIconButton = (ImageView) findViewById(R.id.imageIcon);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        imageIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Methods.hideKeyboard(msg,Create_Message_Activity.this);
                if (picUri != null) {
                    /*Intent in =new Intent("com.android.camera.action.CROP");
                    in.setDataAndType(picUri,"image*//*");
                    in.putExtra("crop",true);
                    in.putExtra("return-data",true);
                    startActivityForResult(in,ACTION_REQUEST_IMAGE_EDIT);*/

//                    if (android.os.Build.VERSION.SDK_INT >15)
//                        startFeather(picUri);

                    Intent in =new Intent(Create_Message_Activity.this,EditImageActivity.class);
                    in.putExtra("image",path);
                    startActivityForResult(in, ACTION_REQUEST_IMAGE_EDIT);
                }
            }
        });
       /* mGalleryFolder = createFolders();
        if (android.os.Build.VERSION.SDK_INT >15){
            // pre-load the cds service
            Intent cdsIntent = AviaryIntent.createCdsInitIntent(getBaseContext());
            startService(cdsIntent);
            // verify the CreativeSDKImage configuration
            try {
                AviaryIntentConfigurationValidator.validateConfiguration(this);
            } catch (Throwable e) {
                new AlertDialog.Builder(this).setTitle("Error")
                        .setMessage(e.getMessage()).show();
            }
        }*/
    }
    public void choosePicture() {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.featuredimage_popup,true)
                .show();
        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);

        View view = dialog.getCustomView();
        TextView header = (TextView) view.findViewById(R.id.textview_heading);
        LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
        LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);
        ImageView   cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
        ImageView galleryImg = (ImageView) view.findViewById(R.id.pop_up_gallery_img);
        cameraImg.setColorFilter(whiteLabelFilter_pop_ip);
        galleryImg.setColorFilter(whiteLabelFilter_pop_ip);

        takeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
                dialog.dismiss();
            }
        });

        takeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
                dialog.dismiss();

            }
        });
    }

    public void choosePictureOptionDilog() {
        Typeface robotoMedium = Typeface.createFromAsset(Create_Message_Activity.this.getAssets(),"Roboto-Medium.ttf");
        Typeface robotoLight = Typeface.createFromAsset(Create_Message_Activity.this.getAssets(),"Roboto-Light.ttf");

        final MaterialDialog dialog = new MaterialDialog.Builder(Create_Message_Activity.this)
                .customView(R.layout.featuredimage_popup,true)
                .show();
        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);

        View view = dialog.getCustomView();
        TextView header = (TextView) view.findViewById(R.id.textview_heading);
        header.setTypeface(robotoMedium);
        header.setText("Upload Photo");
        LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
        LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);
        ImageView   cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
        ImageView galleryImg = (ImageView) view.findViewById(R.id.pop_up_gallery_img);
        TextView cameraTextView = (TextView) view.findViewById(R.id.alert_message_text);
        cameraImg.setColorFilter(whiteLabelFilter_pop_ip);
        galleryImg.setColorFilter(whiteLabelFilter_pop_ip);

        takeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
                dialog.dismiss();
            }
        });

        takeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
                dialog.dismiss();

            }
        });
    }

    public void galleryIntent() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        gallery_req_id);
            }
            else {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Constants.GALLERY_PHOTO);
                /*Intent i = new Intent();
                i.setType("image*//*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(i,"Select an Image" ), Constants.GALLERY_PHOTO);*/
            }
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(activity,errorMessage);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if(requestCode==media_req_id)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();

            }

        }
        else if(requestCode==gallery_req_id)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();

            }

        }
    }
    public void cameraIntent() {
        try {
            // use standard intent to capture an image
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        media_req_id);
            }
            else {
                ContentValues Cvalues = new ContentValues();
                Intent captureIntent;
                Cvalues.put(MediaStore.Images.Media.TITLE, "New Picture");
                Cvalues.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                picUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Cvalues);
                captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                startActivityForResult(captureIntent, Constants.CAMERA_PHOTO);
            }
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(activity,errorMessage);
        } catch (SecurityException e){
            Toast.makeText(this, "Please eanble camera permission in settings to use your camera", Toast.LENGTH_SHORT);
        }
    }

    public void removeImage() {
        path = null;
        imageIconButton.setImageResource(R.drawable.post_picture_blank);
        msg.setHint("Description");
        String eol = System.getProperty("line.separator");
        // updateHint.setText("ADD" + eol + "PHOTO");
    }

    public void setPicture(Bitmap bmp) {
        if (!Util.isNullOrEmpty(path)) {
            if (bmp != null) {
                imageIconButton.setVisibility(View.VISIBLE);
                imageIconButtonSelected = true ;
                imageIconButton.setImageBitmap(bmp);
                msg.setHint(getString(R.string.add_context_to_picture));
            }
        }
    }

    public String getGalleryImagePath(Intent data) {
        Uri imgUri = data.getData();
        String filePath = "";
        if (data.getType() == null) {
            // For getting images from default gallery app.
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(imgUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if (data.getType().equals("image/jpeg") || data.getType().equals("image/png")) {
            // For getting images from dropbox or any other gallery apps.
            filePath = imgUri.getPath();
        }
        return filePath;
    }

    public void setPicture(Uri uri) {
        path = getPath(uri);
        //path = getGalleryImagePath(data);
        // Log.d("Set Pic"," Set Pic Path : "+path);
        path = Util.saveBitmap(path, this, 720,tagName + System.currentTimeMillis());
        // Log.d("Set Pic"," Set Pic Path Util : "+path);
        if (!Util.isNullOrEmpty(path)) {
            bmp = Util.getBitmap(path, this);
            CameraBitmap = bmp;
            if (bmp != null) {
                imageIconButton.setVisibility(View.VISIBLE);
                imageIconButtonSelected = true ;
                imageIconButton.setImageBitmap(bmp);
                msg.setHint(getString(R.string.add_context_to_picture));
            }
        }
    }

    public String getPath(Uri uri) {
        try {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
        }
        return null;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && (Constants.GALLERY_PHOTO == requestCode)) {
            if (data != null) {
                picUri = data.getData();
                /*try {
                    BoostLog.d("Uri Image:", Uri.decode(picUri.toString()));
                    path = getPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI ,"_id=?",new String[]{Uri.decode(picUri.toString()).split(":")[2]});
                    setPicture(BitmapFactory.decodeFile(path));
                } catch (Exception e) {
                    e.printStackTrace();
                    String[] projection = { MediaStore.Images.Media.DATA };
                    Cursor cursor = managedQuery(picUri, projection, null, null, null);
                    if( cursor != null ){
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        path = cursor.getString(column_index);
                    }else {
                        path  =picUri.getPath();
                    }
                    setPicture(BitmapFactory.decodeFile(path));
                    // this is our fallback here

                }*/
                try {
                    if (picUri == null) {
                        CameraBitmap = (Bitmap) data.getExtras().get("data");
                        path = Util.saveBitmap(CameraBitmap, activity, tagName + System.currentTimeMillis());
                        picUri = Uri.parse(path);
                        setPicture(CameraBitmap);
                        //if (replaceImage) replaceProductImage(product_data._id);
                    } else {
                        path = getRealPathFromURI(picUri);
                        CameraBitmap = Util.getBitmap(path, activity);
                        setPicture(CameraBitmap);
                        // if (replaceImage) replaceProductImage(product_data._id);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Methods.showSnackBarNegative(Create_Message_Activity.this, getString(R.string.select_offline_image));
                }

            }
        }else if (resultCode == RESULT_OK && (Constants.CAMERA_PHOTO == requestCode)) {
            try {
                if (picUri==null){
                    if (data != null) {
                        picUri = data.getData();
                        if (picUri == null) {
                            CameraBitmap = (Bitmap) data.getExtras().get("data");
                            path = Util.saveCameraBitmap(CameraBitmap,activity,tagName + System.currentTimeMillis());
                            picUri = Uri.parse(path);
                            setPicture(CameraBitmap);
                           // if (replaceImage) replaceProductImage(product_data._id);
                        }else{
                            path = getRealPathFromURI(picUri);
                            CameraBitmap = Util.getBitmap(path, activity);
                            setPicture(CameraBitmap);
                            //if (replaceImage) replaceProductImage(product_data._id);
                        }
                    }else{
                        Methods.showSnackBar(activity,getString(R.string.try_again));
                    }
                }else{
                    path = getRealPathFromURI(picUri);
                    CameraBitmap = Util.getBitmap(path, activity);
                    setPicture(CameraBitmap);
                   // if (replaceImage) replaceProductImage(product_data._id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }catch(OutOfMemoryError E){
                E.printStackTrace();
                CameraBitmap.recycle();
                System.gc();
                Methods.showSnackBar(activity,getString(R.string.try_again));
            }
        }
    }

    public String getPath( Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public String getRealPathFromURI(Uri contentUri) {
        try{
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(contentUri, projection, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }catch (Exception e) {
        }
        return null;
    }





    @Override
    protected void onResume() {
        super.onResume();
        //uiHelper.onResume();


        MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY,null);
        if(Constants.twitterShareEnabled) {
            twitterloginButton.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_active));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        //uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageIconButtonSelected = false;
        //uiHelper.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create__message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==android.R.id.home){
//            NavUtils.navigateUpFromSameTask(this);
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    /**
     * Check the external storage status
     *
     * @return
     */
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    private File createFolders() {
        File baseDir;

        if (android.os.Build.VERSION.SDK_INT < 8) {
            baseDir = Environment.getExternalStorageDirectory();
        } else {
            baseDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }

        if (baseDir == null) {
            return Environment.getExternalStorageDirectory();
        }

        Log.d("photo Effect", "Pictures folder: " + baseDir.getAbsolutePath());
        File aviaryFolder = new File(baseDir, "image_edit");

        if (aviaryFolder.exists()) {
            return aviaryFolder;
        }
        if (aviaryFolder.mkdirs()) {
            return aviaryFolder;
        }
        return Environment.getExternalStorageDirectory();
    }
    /**
     * Return a new image file. Name is based on the current time. Parent folder
     * will be the one created with createFolders
     *
     * @return
     * @see #createFolders()
     */
    /*private File getNextFileName() {
        if (mGalleryFolder != null) {
            if (mGalleryFolder.exists()) {
                File file = new File(
                        mGalleryFolder, "aviary_"
                        + System.currentTimeMillis() + ".jpg");
                return file;
            }
        }
        return null;
    }*/
    /**
     * Once you've chosen an image you can start the feather activity
     *
     * @param
     */
    /*@SuppressWarnings ("deprecation")
    private void startFeather(Uri uri) {
        Log.d("Photo Effects", "uri: " + uri);
        MixPanelController.track("EditPhoto",null);
        // first check the external storage availability
        if (!isExternalStorageAvailable()) {
            showDialog(1);
            return;
        }

        // create a temporary file where to store the resulting image
        File file = getNextFileName();

        if (null != file) {
            mOutputFilePath = file.getAbsolutePath();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage("Failed to create a new File").show();
            return;
        }

        //Tools
        //SHARPNESS, EFFECTS, REDEYE, CROP, WHITEN, DRAW, STICKERS, TEXT, BLEMISH, MEME, ORIENTATION, ENHANCE, FRAMES, SPLASH, FOCUS, BLUR, VIGNETTE, LIGHTING, COLOR, OVERLAYS
        Intent newIntent = new AviaryIntent.Builder(this).setData(uri)
                .withOutput(Uri.parse("file://" + mOutputFilePath))
                .withOutputFormat(Bitmap.CompressFormat.JPEG)
                .withOutputSize(MegaPixels.Mp5).withNoExitConfirmation(true)
                .saveWithNoChanges(true).withPreviewSize(1024)
                .withToolList(new ToolLoaderFactory.Tools[]{ToolLoaderFactory.Tools.CROP, ToolLoaderFactory.Tools.ORIENTATION
                        , ToolLoaderFactory.Tools.ENHANCE, ToolLoaderFactory.Tools.EFFECTS, ToolLoaderFactory.Tools.FRAMES,
                        ToolLoaderFactory.Tools.BLUR, ToolLoaderFactory.Tools.TEXT})
                .build();

        // ..and start feather
        startActivityForResult(newIntent, ACTION_REQUEST_IMAGE_EDIT);
    }*/
    public  void logoutFromTwitter() {
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.remove(TwitterConstants.PREF_KEY_OAUTH_TOKEN);
        e.remove(TwitterConstants.PREF_KEY_OAUTH_SECRET);
        e.remove(TwitterConstants.PREF_KEY_TWITTER_LOGIN);
        e.remove(TwitterConstants.PREF_USER_NAME);
        e.commit();
        Constants.twitterShareEnabled = false;
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }
    /*private void AppICECode() {
        boolean isSemusiSensing = ContextSdk.isSemusiSensing(getApplicationContext());
        if (isSemusiSensing == false) {
            SdkConfig config = new SdkConfig();
            config.setAnalyticsTrackingAllowedState(true);
            Api.startContext(getApplicationContext(), config);
        }
    }*/
}