package com.nowfloats.BusinessProfile.UI.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.nowfloats.BusinessProfile.UI.Model.FacebookFeedPullModel;
import com.nowfloats.CustomWidget.roboto_lt_24_212121;
import com.nowfloats.CustomWidget.roboto_md_60_212121;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NFXApi.NfxRequestClient;
import com.nowfloats.NavigationDrawer.API.twitter.FacebookFeedPullRegistrationAsyncTask;
import com.nowfloats.NavigationDrawer.Adapter.QuikrAdapter;
import com.nowfloats.Twitter.ITwitterCallbacks;
import com.nowfloats.Twitter.TokenRequest;
import com.nowfloats.Twitter.TwitterAuthenticationActivity;
import com.nowfloats.Twitter.TwitterConstants;
import com.nowfloats.Twitter.Utils;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DataBase;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.BuildConfig;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class Social_Sharing_Activity extends AppCompatActivity implements ITwitterCallbacks, NfxRequestClient.NfxCallBackListener {
    private static final int PAGE_NO_FOUND = 404;
    private Toolbar toolbar;
    int size = 0;
    boolean[] checkedPages;
    UserSessionManager session;
    private final int LIGHT_HOUSE_EXPIRE = 0;
    private final int WILD_FIRE_EXPIRE = 1;
    private final int DEMO_EXPIRE = 3;

    TextView connectTextView, topFeatureTextView;
    //final Facebook facebook = new Facebook(Constants.FACEBOOK_API_KEY);
    private SharedPreferences pref = null;
    SharedPreferences.Editor prefsEditor;
    private ImageView facebookHome;
    private ImageView facebookPage;
    private ImageView twitter;
    private ImageView ivFbPageAutoPull;
    private TextView facebookHomeStatus, facebookPageStatus, twitterStatus, fbPullStatus;
    private CheckBox facebookHomeCheckBox, facebookPageCheckBox, twitterCheckBox;
    private CheckBox facebookautopost;
    private TextView headerText;
    ArrayList<String> items;
    private int numberOfUpdates = 0;
    private boolean numberOfUpdatesSelected = false;
    private Activity activity;
    private MaterialDialog materialProgress;
    private ImageView ivHelpTool;

    //Rahul Twitter

    //Variables are required to store twitter key and sec
    private String mConsumerKey = null;
    private String mConsumerSecret = null;
    private String mCallbackUrl = null;
    private String mAuthVerifier = null;
    private String mTwitterVerifier = null;
    private Twitter mTwitter = null;
    private RequestToken mRequestToken = null;
    private SharedPreferences mSharedPreferences = null;
    private boolean called = false;
    private ProgressDialog pd = null;
    private int mNewPosition =-1;


    //Rahul Twitter


    private final int FBTYPE = 0;
    private final int FBPAGETYPE = 1;
    private final int TWITTERTYPE = 2;
    private final int FB_DECTIVATION = 3;
    private final int FB_PAGE_DEACTIVATION = 4;
    private final int TWITTER_DEACTIVATION = 11;

    private final int FROM_AUTOPOST = 1;
    private final int FROM_FB_PAGE = 0;


    MaterialDialog mExpireDailog;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_social_sharing);

        session = new UserSessionManager(getApplicationContext(), Social_Sharing_Activity.this);
        // Facebook_Auto_Publish_API.autoPublish(Social_Sharing_Activity.this,session.getFPID());
        Methods.isOnline(Social_Sharing_Activity.this);
        pref = this.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
        TwitterAuthenticationActivity.setListener(this);
        mSharedPreferences = this.getSharedPreferences(TwitterConstants.PREF_NAME,MODE_PRIVATE);
        activity = Social_Sharing_Activity.this;

        toolbar = (Toolbar) findViewById(R.id.app_bar_social);

        Typeface myCustomFont = Typeface.createFromAsset(this.getAssets(), "Roboto-Light.ttf");
        Typeface myCustomFont_Medium = Typeface.createFromAsset(this.getAssets(), "Roboto-Regular.ttf");

        setSupportActionBar(toolbar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText("Social Sharing");

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        facebookHome = (ImageView) findViewById(R.id.social_sharing_facebook_profile_image);
        facebookPage = (ImageView) findViewById(R.id.social_sharing_facebook_page_image);
        twitter = (ImageView) findViewById(R.id.social_sharing_twitter_image);
        ivFbPageAutoPull = (ImageView) findViewById(R.id.auto_pull_facebook_page_image);

        facebookHomeStatus = (TextView) findViewById(R.id.social_sharing_facebook_profile_flag_text);
        facebookPageStatus = (TextView) findViewById(R.id.social_sharing_facebook_page_flag_text);
        twitterStatus = (TextView) findViewById(R.id.social_sharing_twitter_flag_text);
        fbPullStatus = (TextView) findViewById(R.id.tv_fb_page_name);
        connectTextView = (TextView) findViewById(R.id.connectTextView);
        //autoPostTextView = (TextView) findViewById(R.id.autoPostTextView);
        topFeatureTextView = (TextView) findViewById(R.id.topFeatureText);

        //Quikr added
        CardView card = (CardView) findViewById(R.id.quikr_card);
        String[] quikrArray = getResources().getStringArray(R.array.quikr_widget);
        //Log.v("ggg",quikrArray[3]+session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).toLowerCase());
        LinearLayout layout = (LinearLayout) findViewById(R.id.float_a_picture_share_quikr_parent);
        for(String category: quikrArray){
            if(category.contains(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).toLowerCase())){
                card.setVisibility(View.VISIBLE);

                RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.list);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
                mRecyclerView.setAdapter(new QuikrAdapter(this,getResources().getStringArray(R.array.quikr_tip_points)));
                break;
            }
        }

        facebookHomeStatus.setTypeface(myCustomFont);
        facebookPageStatus.setTypeface(myCustomFont);
        twitterStatus.setTypeface(myCustomFont);
        fbPullStatus.setTypeface(myCustomFont);

        facebookHomeCheckBox = (CheckBox) findViewById(R.id.social_sharing_facebook_profile_checkbox);
        facebookPageCheckBox = (CheckBox) findViewById(R.id.social_sharing_facebook_page_checkbox);
        twitterCheckBox = (CheckBox) findViewById(R.id.social_sharing_twitter_checkbox);
        facebookautopost = (CheckBox) findViewById(R.id.social_sharing_facebook_page_auto_post);

        connectTextView.setTypeface(myCustomFont_Medium);
        //autoPostTextView.setTypeface(myCustomFont);
        topFeatureTextView.setTypeface(myCustomFont_Medium);


        facebookPageCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facebookPageCheckBox.isChecked()) {
                    Toast.makeText(Social_Sharing_Activity.this,"Reconnect with facebook",Toast.LENGTH_SHORT).show();
                    facebookPageCheckBox.setChecked(false);
//                    final Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            fbPageData(FROM_FB_PAGE);
//                        }
//                    }, 200);
//
                } else {
                    NfxRequestClient requestClient = new NfxRequestClient((NfxRequestClient.NfxCallBackListener) Social_Sharing_Activity.this)
                            .setmFpId(session.getFPID())
                            .setmType("facebookpage")
                            .setmUserAccessTokenKey("")
                            .setmUserAccessTokenSecret("")
                            .setmUserAccountId("")
                            .setmCallType(FB_PAGE_DEACTIVATION)
                            .setmName("");
                    requestClient.connectNfx();

                    pd = ProgressDialog.show(Social_Sharing_Activity.this, "", getString(R.string.wait_while_unsubscribing));
                }
            }
        });

        facebookHomeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facebookHomeCheckBox.isChecked()) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            fbData();
                        }
                    }, 200);
                } else {

                    NfxRequestClient requestClient = new NfxRequestClient((NfxRequestClient.NfxCallBackListener) Social_Sharing_Activity.this)
                            .setmFpId(session.getFPID())
                            .setmType("facebookusertimeline")
                            .setmUserAccessTokenKey("")
                            .setmUserAccessTokenSecret("")
                            .setmUserAccountId("")
                            .setmCallType(FB_DECTIVATION)
                            .setmName("");
                    requestClient.connectNfx();

                    NfxRequestClient pageRequestClient = new NfxRequestClient((NfxRequestClient.NfxCallBackListener) Social_Sharing_Activity.this)
                            .setmFpId(session.getFPID())
                            .setmType("facebookpage")
                            .setmUserAccessTokenKey("")
                            .setmUserAccessTokenSecret("")
                            .setmUserAccountId("")
                            .setmCallType(FB_PAGE_DEACTIVATION)
                            .setmName("");
                    pageRequestClient.connectNfx();
                    pd = ProgressDialog.show(Social_Sharing_Activity.this, "", getString(R.string.wait_while_unsubscribing));

                }
            }
        });

        facebookautopost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (facebookautopost.isChecked()) {

                    if (session.getShowUpdates() && !Util.isNullOrEmpty(Constants.fbPageFullUrl))
                        selectNumberUpdatesDialog();
                    if(!called){
                        autoPostSelectListener();
                    }


                } else {
                    session.setShowUpdates(false);
                    final JSONObject obj = new JSONObject();
                    try {
                        obj.put("fpId", session.getFPID());
                        obj.put("autoPublish", false);
                        obj.put("clientId", Constants.clientId);
                        obj.put("FacebookPageName", Constants.fbFromWhichPage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FacebookFeedPullAutoPublishAsyncTask fap = new FacebookFeedPullAutoPublishAsyncTask(Social_Sharing_Activity.this, obj, false, facebookPageStatus);
                    fap.execute();


                }*/
                String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);
                String paymentLevel = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL);
                if (paymentState.equals("-1")) {
                    try {
                        if (Integer.parseInt(paymentLevel) > 10) {
                            //LH expire
                            if(BuildConfig.APPLICATION_ID.equals("com.kitsune.biz")){
                                renewKitsune(LIGHT_HOUSE_EXPIRE);
                            }else {
                                renewPlanDialog(LIGHT_HOUSE_EXPIRE);
                            }
                        } else{
                            //Demo expire

                            if(BuildConfig.APPLICATION_ID.equals("com.kitsune.biz")){
                                renewKitsune(DEMO_EXPIRE);
                            }else {
                                renewPlanDialog(DEMO_EXPIRE);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    facebookautopost.setChecked(false);
                }else {
                    if (facebookautopost.isChecked()) {
                        // connecting to auto pull
                        fbPageData(FROM_AUTOPOST);
                    } else {
                        //disconnected to auto pull
                        updateAutopull(session.getFPDetails(Key_Preferences.FB_PULL_PAGE_NAME), false);
                    }
                }

            }
        });

        twitterCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (twitterCheckBox.isChecked()) {
                    /*twitter.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_active));
                    twitterStatus.setText("Connected");
                    twitterCheckBox.setHighlightColor(getResources().getColor(R.color.primaryColor));
                    Constants.twitterShareEnabled = true;
                    MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_TWITTER, null);*/
                    //Rahul twitter
                    if (!Utils.isNetworkConnected(Social_Sharing_Activity.this)) {
                        showAlertBox();
                    } else {
                        mConsumerKey = getApplicationContext().getResources().getString(R.string.twitter_consumer_key);
                        mConsumerSecret = getApplicationContext().getResources().getString(R.string.twitter_consumer_secret);
                        mAuthVerifier = "oauth_verifier";
                        final ConfigurationBuilder builder = new ConfigurationBuilder();
                        builder.setOAuthConsumerKey(mConsumerKey);
                        builder.setOAuthConsumerSecret(mConsumerSecret);
                        final Configuration configuration = builder.build();
                        final TwitterFactory factory = new TwitterFactory(configuration);
                        mTwitter = factory.getInstance();
                        new TokenRequest(Social_Sharing_Activity.this).execute();
                        initTwitterSDK(Social_Sharing_Activity.this);
                    }
                    //Rahul twitter
                }else {
                    NfxRequestClient requestClient1 = new NfxRequestClient((NfxRequestClient.NfxCallBackListener) Social_Sharing_Activity.this)
                            .setmFpId(session.getFPID())
                            .setmType("twitter")
                            .setmUserAccessTokenKey("")
                            .setmUserAccessTokenSecret("")
                            .setmUserAccountId(String.valueOf(""))
                            .setmCallType(TWITTER_DEACTIVATION)
                            .setmName("");
                    requestClient1.connectNfx();
                    pd = ProgressDialog.show(Social_Sharing_Activity.this, "", getString(R.string.wait_while_unsubscribing));

                }
            }
        });
        findViewById(R.id.iv_help_tool).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Updates will reflect on your website one hour after getting posted on the Facebook Page. Please <u>do not</u> select this option if you are using social share from your website.";
                showDialog("Tip!", message);
            }
        });
        /*findViewById(R.id.iv_help_auto_pull).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Updates will reflect on your website one hour after getting posted on the Facebook Page. Do not select this option if you are using social share from your website.";
                showDialog(message);
            }
        });*/
        InitShareResources();
        setStatus();
    }
    private void renewKitsune(int expiryType) {
        String dialogTitle = null;
        String dialogMessage = null;
        String callUsButtonText = "";
        String cancelButtonText = null;
        int dialogImage = 0;
        int dialogImageBgColor = 0;
        int days;
        prefsEditor.putBoolean("EXPIRE_DIALOG",true);
        prefsEditor.commit();
        boolean dialogShowFlag = true;
        switch (expiryType) {
            case LIGHT_HOUSE_EXPIRE:
                dialogTitle = getString(R.string.kitsune_renew_dialog_title);
                dialogMessage = getString(R.string.kitsune_renew_dialog_body);
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                break;
            case DEMO_EXPIRE:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                dialogTitle = getString(R.string.kitsune_demo_expire_dialog_title);
                dialogMessage = getString(R.string.kitsune_demo_expire_dialog_body);
                break;
            default:
                callUsButtonText = "";
                cancelButtonText = "";
                dialogTitle = "";
                dialogMessage = "";
                dialogImage = -1;
                break;
        }
        mExpireDailog = new MaterialDialog.Builder(this)
                .customView(R.layout.pop_up_restrict_post_message, false)
                .backgroundColorRes(R.color.white)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog mExpireDailog) {
                        super.onNegative(mExpireDailog);
                        mExpireDailog.dismiss();
                        prefsEditor.putBoolean("EXPIRE_DIALOG", true);
                        prefsEditor.putBoolean("IGNORE_CLICKED", true);
                        prefsEditor.commit();
                    }
                }).show();

            mExpireDailog.setCancelable(true);
            View view = mExpireDailog.getCustomView();
            if(view == null) return;
            roboto_md_60_212121 title = (roboto_md_60_212121) view.findViewById(R.id.textView1);
            title.setText(dialogTitle);

            ImageView expireImage = (ImageView) view.findViewById(R.id.img_warning);
            expireImage.setBackgroundColor(dialogImageBgColor);
            expireImage.setImageDrawable(getResources().getDrawable(dialogImage));

            roboto_lt_24_212121 message = (roboto_lt_24_212121) view.findViewById(R.id.pop_up_create_message_body);
            message.setText(dialogMessage);
    }
    private void renewPlanDialog(final int expireAccount) {
        String dialogTitle = null;
        String dialogMessage = null;
        String callUsButtonText = "";
        String cancelButtonText = null;
        int dialogImage = 0;
        int dialogImageBgColor = 0;

        prefsEditor.putBoolean("EXPIRE_DIALOG",true);
        prefsEditor.commit();
        boolean dialogShowFlag = true;
        switch (expireAccount) {
            case LIGHT_HOUSE_EXPIRE:
                callUsButtonText = "Ok";
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.renew_light_house_plan);
                dialogMessage = getString(R.string.light_house_plan_expired_some_features_visible);
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                break;
            case DEMO_EXPIRE:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                callUsButtonText = "Ok";
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.buy_light_house_plan);
                dialogMessage = getString(R.string.demo_plan_expired);
                break;
            default:
                callUsButtonText = "";
                cancelButtonText = "";
                dialogTitle = "";
                dialogMessage = "";
                dialogImage = -1;
                break;
        }
        mExpireDailog = new MaterialDialog.Builder(this)
                .customView(R.layout.pop_up_restrict_post_message, false)
                .backgroundColorRes(R.color.white)
                .negativeText(cancelButtonText)
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onNegative(MaterialDialog mExpireDailog) {
                        super.onNegative(mExpireDailog);
                        mExpireDailog.dismiss();
                        prefsEditor.putBoolean("EXPIRE_DIALOG", true);
                        prefsEditor.putBoolean("IGNORE_CLICKED", true);
                        prefsEditor.commit();
                    }
                }).show();

            mExpireDailog.setCancelable(true);
            View view = mExpireDailog.getCustomView();
            if(view == null ) return;
            roboto_md_60_212121 title = (roboto_md_60_212121) view.findViewById(R.id.textView1);
            title.setText(dialogTitle);

            ImageView expireImage = (ImageView) view.findViewById(R.id.img_warning);
            expireImage.setBackgroundColor(dialogImageBgColor);
            expireImage.setImageDrawable(getResources().getDrawable(dialogImage));

            roboto_lt_24_212121 message = (roboto_lt_24_212121) view.findViewById(R.id.pop_up_create_message_body);
            message.setText(dialogMessage);
    }
    private void updateAutopull(String name,boolean autoPublish) {
        numberOfUpdatesSelected = false;
        FacebookFeedPullModel.Update obj = new FacebookFeedPullModel().new Update();
        try {
            obj.setFpId(session.getFPID());
            obj.setAutoPublish(autoPublish);
            obj.setClientId(Constants.clientId);
            obj.setFacebookPageName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FacebookFeedPullRegistrationAsyncTask fap = new FacebookFeedPullRegistrationAsyncTask(Social_Sharing_Activity.this, fbPullStatus, ivFbPageAutoPull, facebookautopost, session);
        fap.autoUpdate(obj);
    }

    private void autoPostSelectListener(String pageName) {
        /*called = true;
        boolean FbRegistered = pref.getBoolean("FacebookFeedRegd", false);
        if (FbRegistered == false) {
            if (!Util.isNullOrEmpty(Constants.fbPageFullUrl)) {
                pullFacebookFeedDialog();
            } else {
                Util.toast("Please select a Facebook page", getApplicationContext());
                facebookautopost.setChecked(false);
            }
        } else {
            final JSONObject obj = new JSONObject();
            try {
                obj.put("fpId", session.getFPID());
                obj.put("autoPublish", true);
                obj.put("clientId", Constants.clientId);
                obj.put("FacebookPageName", Constants.fbFromWhichPage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            FacebookFeedPullAutoPublishAsyncTask fap = new FacebookFeedPullAutoPublishAsyncTask(Social_Sharing_Activity.this, obj, true, facebookPageStatus);
            fap.execute();
        }*/
        if(numberOfUpdatesSelected){
            FacebookFeedPullModel.Registration obj = new FacebookFeedPullModel().new Registration();
            try {
                obj.setTag(session.getFpTag());
                obj.setAutoPublish(true);
                obj.setClientId(Constants.clientId);
                obj.setFacebookPageName(pageName);
                obj.setCount(numberOfUpdates);
            } catch (Exception e) {
                e.printStackTrace();
            }
            FacebookFeedPullRegistrationAsyncTask fap = new FacebookFeedPullRegistrationAsyncTask(Social_Sharing_Activity.this, fbPullStatus, ivFbPageAutoPull, facebookautopost, session);
            fap.autoRegister(obj);
        }else {
            facebookautopost.setChecked(false);
        }
    }


    private void selectNumberUpdatesDialog(final String name) {
        final String[] array = getResources().getStringArray(R.array.post_updates);
        new MaterialDialog.Builder(Social_Sharing_Activity.this)
                .title(getString(R.string.post_on_website))
                .items(array)
                .negativeText(getString(R.string.cancel))
                .cancelable(false)
                .positiveText(getString(R.string.ok))
                .negativeColorRes(R.color.light_gray)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        facebookautopost.setChecked(false);
                        dialog.dismiss();
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        int position = dialog.getSelectedIndex();
                        if (position == 0) {
                            numberOfUpdates = 5;
                            numberOfUpdatesSelected = true;
                        }else if (position == 1) {
                            numberOfUpdates = 10;
                            numberOfUpdatesSelected = true;
                        }else{
                            // == 0 ? 5 : dialog.getSelectedIndex() ==1 ? 10 : 5;
                            Toast.makeText(Social_Sharing_Activity.this, "Please select any Facebook page", Toast.LENGTH_SHORT).show();
                            numberOfUpdatesSelected = false;
                        }
                        autoPostSelectListener(name);
                        dialog.dismiss();
                    }
                })
                .widgetColorRes(R.color.primaryColor)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int position, CharSequence text) {

                        //session.storeShowUpdates(false);
                        if (position == 0) {
                            numberOfUpdates = 5;
                        }

                        if (position == 1) {
                            numberOfUpdates = 10;
                        }
                        //dialog.dismiss();
                        return true;
                    }
                }).show();
    }

    @Override
    public void returnToken(Intent data) {
        if (materialProgress != null) {
            materialProgress.dismiss();
        }
        if (data != null) {
            mTwitterVerifier = data.getExtras().getString(mAuthVerifier);
            AccessToken accessToken;
            try {
                if (mTwitter == null) {
                    final ConfigurationBuilder builder = new ConfigurationBuilder();
                    builder.setOAuthConsumerKey(mConsumerKey);
                    builder.setOAuthConsumerSecret(mConsumerSecret);
                    final Configuration configuration = builder.build();
                    final TwitterFactory factory = new TwitterFactory(configuration);
                    mTwitter = factory.getInstance();
                }
                accessToken = mTwitter.getOAuthAccessToken(mRequestToken, mTwitterVerifier);
                long userID = accessToken.getUserId();
                final User user = mTwitter.showUser(userID);
                String username = user.getName();
                twitterStatus.setVisibility(View.VISIBLE);
                twitterStatus.setText(username);
                saveTwitterInformation(accessToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(Social_Sharing_Activity.this, getString(R.string.problem_with_twitter_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);//added

        //facebook.authorizeCallback(requestCode, resultCode, data);//removed
        if (materialProgress != null) {
            materialProgress.dismiss();
        }
    }
//added


    public void fbPageData(final int from) {
        if(from==FROM_AUTOPOST)
        pd = ProgressDialog.show(this, "", getString(R.string.wait_while_subscribing));
        List<String> readPermissions=Arrays.asList("email"
                , "public_profile",  "user_friends", "read_insights", "business_management");
        final List<String> publishPermissions = Arrays.asList("publish_actions",
                "publish_pages", "manage_pages");
        final LoginManager loginManager = LoginManager.getInstance();
    /*    com.facebook.AccessToken currentToken = com.facebook.AccessToken.getCurrentAccessToken();

        if (currentToken!=null && !currentToken.isExpired() && currentToken.getPermissions().containsAll(publishPermissions)){
            GraphRequest request = GraphRequest.newGraphPathRequest(
                    currentToken,
                    "/me/accounts",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            // Insert your code here
                            processGraphResponse(response, from);
                        }
                    });

            request.executeAsync();
        }else {*/
            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    boolean contain=loginResult.getAccessToken().getPermissions().containsAll(publishPermissions);
                    if(!contain){
                        loginManager.logInWithPublishPermissions(Social_Sharing_Activity.this, publishPermissions);
                    }else {
                        GraphRequest request = GraphRequest.newGraphPathRequest(
                                loginResult.getAccessToken(),
                                "/me/accounts",
                                new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse response) {
                                        // Insert your code here
                                        processGraphResponse(response, from);
                                    }
                                });

                        request.executeAsync();
                    }

                }

                @Override
                public void onCancel() {

                    if(!isFinishing() && pd!=null)
                    pd.dismiss();
                    onFBPageError(from);
                }

                @Override
                public void onError(FacebookException error) {
                    onFBPageError(from);
                    Log.v("ggg",error.toString()+" error");
                    LoginManager.getInstance().logOut();
                    com.facebook.AccessToken.refreshCurrentAccessTokenAsync();
                   if(pd!=null && !isFinishing())
                    pd.dismiss();
                }
            });
            loginManager.logInWithReadPermissions(this, readPermissions);

        }

        /*facebook.authorize(this, PERMISSIONS, new Facebook.DialogListener() {
            public void onComplete(Bundle values) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject pageMe = new JSONObject(facebook.request("me/accounts"));
                            Constants.FbPageList = pageMe.getJSONArray("data");
                            if (Constants.FbPageList != null) {
                                size = Constants.FbPageList.length();

                                checkedPages = new boolean[size];
                                if (size > 0) {
                                    items = new ArrayList<String>();
                                    for (int i = 0; i < size; i++) {
                                        items.add(i, (String) ((JSONObject) Constants.FbPageList
                                                .get(i)).get("name"));
                                        //BoostLog.d("ILUD Test: ", (String) ((JSONObject) Constants.FbPageList
                                        //.get(i)).get("name"));
                                    }

                                    for (int i = 0; i < size; i++) {
                                        checkedPages[i] = false;
                                    }


                                }
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        } finally {

                            Social_Sharing_Activity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (items != null && items.size() > 0) {
                                        final String[] array = items.toArray(new String[items.size()]);
                                        new MaterialDialog.Builder(Social_Sharing_Activity.this)
                                                .title(getString(R.string.select_page))
                                                .items(array)
                                                .widgetColorRes(R.color.primaryColor)
                                                .cancelable(false)
                                                .positiveText("Ok")
                                                .negativeText("Cancel")
                                                .negativeColorRes(R.color.light_gray)
                                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                                    @Override
                                                    public boolean onSelection(MaterialDialog dialog, View view, int position, CharSequence text) {

                                                        //dialog.dismiss();
                                                        mNewPosition = position;
                                                        return true;
                                                    }
                                                })
                                                .callback(new MaterialDialog.ButtonCallback() {
                                                    @Override
                                                    public void onPositive(MaterialDialog dialog) {
                                                        mNewPosition = dialog.getSelectedIndex();
                                                        if(mNewPosition == -1){
                                                            Toast.makeText(Social_Sharing_Activity.this, "Please select any Facebook page", Toast.LENGTH_SHORT).show();
                                                            if(from==FROM_FB_PAGE){
                                                                facebookPageCheckBox.setChecked(false);
                                                            }else if(from == FROM_AUTOPOST){
                                                                facebookautopost.setChecked(false);
                                                            }
                                                        }else {
                                                            String strName = array[mNewPosition];
                                                            String FACEBOOK_PAGE_ID = null;
                                                            String page_access_token = null;
                                                            try {
                                                                FACEBOOK_PAGE_ID = (String) ((JSONObject) Constants.FbPageList.get(mNewPosition)).get("id");
                                                                page_access_token = ((String) ((JSONObject) Constants.FbPageList.get(mNewPosition)).get("access_token"));
                                                            } catch (JSONException e) {

                                                            }
                                                            if (from == FROM_FB_PAGE && !Util.isNullOrEmpty(FACEBOOK_PAGE_ID) && !Util.isNullOrEmpty(page_access_token)) {
                                                                session.storePageAccessToken(page_access_token);
                                                                session.storeFacebookPageID(FACEBOOK_PAGE_ID);
                                                                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.FB_PULL_PAGE_NAME)) && !strName.equals(session.getFPDetails(Key_Preferences.FB_PULL_PAGE_NAME))) {
                                                                    pageSeleted(mNewPosition, strName, session.getFacebookPageID(), session.getPageAccessToken());
                                                                } else if (Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.FB_PULL_PAGE_NAME))) {
                                                                    pageSeleted(mNewPosition, strName, session.getFacebookPageID(), session.getPageAccessToken());
                                                                } else {
                                                                    facebookPageCheckBox.setChecked(false);
                                                                    showDialog("Alert", "You cannot select the same Facebook Page to share your updates. This will lead to an indefinite loop of updates on your website and Facebook Page.");
                                                                }
                                                                //pageSeleted(position, strName, session.getFacebookPageID(), session.getPageAccessToken());
                                                            } else if (from == FROM_AUTOPOST) {
                                                                if (!Util.isNullOrEmpty(session.getFacebookPage()) && !strName.equals(session.getFacebookPage())) {
                                                                    selectNumberUpdatesDialog(strName);
                                                                } else if (Util.isNullOrEmpty(session.getFacebookPage())) {
                                                                    selectNumberUpdatesDialog(strName);
                                                                } else {
                                                                    //Toast.makeText(getApplicationContext(), "You can't post and pull from the same Facebook page", Toast.LENGTH_SHORT).show();
                                                                    facebookautopost.setChecked(false);
                                                                    showDialog("Alert", "You cannot select the same Facebook Page to auto-update your website. This will lead to an indefinite loop of updates on your website and Facebook Page.");
                                                                }
                                                            }
                                                            dialog.dismiss();
                                                        }
                                                    }

                                                    @Override
                                                    public void onNegative(MaterialDialog dialog) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();
                                    } else {
                                        Methods.materialDialog(activity, "Uh oh~", getString(R.string.look_like_no_facebook_page));
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }

            @Override
            public void onCancel() {
                onFBPageError(from);
            }

            @Override
            public void onFacebookError(FacebookError e) {
                onFBPageError(from);
            }

            @Override
            public void onError(DialogError e) {
                onFBPageError(from);
            }


        });*/


    private void processGraphResponse(final GraphResponse response, final int from) {
        if(from==FROM_AUTOPOST && pd!=null)
            pd.dismiss();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject pageMe = response.getJSONObject();
                    Constants.FbPageList = pageMe.getJSONArray("data");
                    if (Constants.FbPageList != null) {
                        size = Constants.FbPageList.length();

                        checkedPages = new boolean[size];
                        if (size > 0) {
                            items = new ArrayList<String>();
                            for (int i = 0; i < size; i++) {
                                items.add(i, (String) ((JSONObject) Constants.FbPageList
                                        .get(i)).get("name"));
                                //BoostLog.d("ILUD Test: ", (String) ((JSONObject) Constants.FbPageList
                                //.get(i)).get("name"));
                            }

                            for (int i = 0; i < size; i++) {
                                checkedPages[i] = false;
                            }


                        }
                    }
                }catch (Exception e1) {
                    e1.printStackTrace();
                }finally
                {

                    Social_Sharing_Activity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (items != null && items.size() > 0) {
                                final String[] array = items.toArray(new String[items.size()]);
                                new MaterialDialog.Builder(Social_Sharing_Activity.this)
                                        .title(getString(R.string.select_page))
                                        .items(array)
                                        .widgetColorRes(R.color.primaryColor)
                                        .cancelable(false)
                                        .positiveText("Ok")
                                        .negativeText("Cancel")
                                        .negativeColorRes(R.color.light_gray)
                                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                            @Override
                                            public boolean onSelection(MaterialDialog dialog, View view, int position, CharSequence text) {

                                                //dialog.dismiss();
                                                mNewPosition = position;
                                                return true;
                                            }
                                        })
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                mNewPosition = dialog.getSelectedIndex();
                                                if(mNewPosition == -1){
                                                    Toast.makeText(Social_Sharing_Activity.this, "Please select any Facebook page", Toast.LENGTH_SHORT).show();
                                                    if(from==FROM_FB_PAGE){
                                                        facebookPageCheckBox.setChecked(false);
                                                    }else if(from == FROM_AUTOPOST){
                                                        facebookautopost.setChecked(false);
                                                    }
                                                }else {
                                                    String strName = array[mNewPosition];
                                                    String FACEBOOK_PAGE_ID = null;
                                                    String page_access_token = null;
                                                    try {
                                                        FACEBOOK_PAGE_ID = (String) ((JSONObject) Constants.FbPageList.get(mNewPosition)).get("id");
                                                        page_access_token = ((String) ((JSONObject) Constants.FbPageList.get(mNewPosition)).get("access_token"));
                                                    } catch (JSONException e) {

                                                    }
                                                    if (from == FROM_FB_PAGE && !Util.isNullOrEmpty(FACEBOOK_PAGE_ID) && !Util.isNullOrEmpty(page_access_token)) {
                                                        session.storePageAccessToken(page_access_token);
                                                        session.storeFacebookPageID(FACEBOOK_PAGE_ID);
                                                        if (Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.FB_PULL_PAGE_NAME))
                                                                || !pref.getBoolean("FBFeedPullAutoPublish",false)
                                                                || !strName.equals(session.getFPDetails(Key_Preferences.FB_PULL_PAGE_NAME))) {
                                                            pageSeleted(mNewPosition, strName, session.getFacebookPageID(), session.getPageAccessToken());
                                                        }else {
                                                            facebookPageCheckBox.setChecked(false);
                                                            showDialog("Alert", "You cannot select the same Facebook Page to share your updates. This will lead to an indefinite loop of updates on your website and Facebook Page.");
                                                        }
                                                        //pageSeleted(position, strName, session.getFacebookPageID(), session.getPageAccessToken());
                                                    } else if (from == FROM_AUTOPOST) {
                                                        if (Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.FB_PULL_PAGE_NAME))) {
                                                            selectNumberUpdatesDialog(FACEBOOK_PAGE_ID);
                                                        }else if(!strName.equals(session.getFacebookPage())){
                                                            updateAutopull(FACEBOOK_PAGE_ID,true);
                                                        }else {
                                                            //Toast.makeText(getApplicationContext(), "You can't post and pull from the same Facebook page", Toast.LENGTH_SHORT).show();
                                                            facebookautopost.setChecked(false);
                                                            showDialog("Alert", "You cannot select the same Facebook Page to auto-update your website. This will lead to an indefinite loop of updates on your website and Facebook Page.");
                                                        }
                                                    }
                                                    dialog.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onNegative(MaterialDialog dialog) {
                                                onFBPageError(from);
                                                dialog.dismiss();
                                            }
                                        }).show();
                            } else {
                                onFBPageError(from);

                                NfxRequestClient requestClient = new NfxRequestClient((NfxRequestClient.NfxCallBackListener) Social_Sharing_Activity.this)
                                        .setmFpId(session.getFPID())
                                        .setmType("facebookpage")
                                        .setmCallType(PAGE_NO_FOUND)
                                        .setmName("");
                                requestClient.nfxNoPageFound();
                                pd = ProgressDialog.show(Social_Sharing_Activity.this, "", getString(R.string.please_wait));
                            }
                        }
                    });
                }

            }
        }).start();
    }

    public void pageSeleted(int id, final String pageName, String pageID, String pageAccessToken) {
        String s = "";
        JSONObject obj;
        session.storeFacebookPage(pageName);
        JSONArray data = new JSONArray();

        NfxRequestClient requestClient = new NfxRequestClient((NfxRequestClient.NfxCallBackListener) Social_Sharing_Activity.this)
                .setmFpId(session.getFPID())
                .setmType("facebookpage")
                .setmUserAccessTokenKey(pageAccessToken)
                .setmUserAccessTokenSecret("null")
                .setmUserAccountId(pageID)
                .setmCallType(FBPAGETYPE)
                .setmName(pageName);
        requestClient.connectNfx();

        pd = ProgressDialog.show(this, "", getString(R.string.wait_while_subscribing));

        DataBase dataBase = new DataBase(activity);
        dataBase.updateFacebookPage(pageName, pageID, pageAccessToken);

        obj = new JSONObject();
        try {
            obj.put("id", pageID);
            obj.put("access_token", pageAccessToken);
            data.put(obj);

            Constants.fbPageFullUrl = "https://www.facebook.com/pages/" + pageName + "/" + pageID;
            Constants.fbFromWhichPage = pageName;
            prefsEditor.putString("fbPageFullUrl",
                    Constants.fbPageFullUrl);
            prefsEditor.putString("fbFromWhichPage",
                    Constants.fbFromWhichPage);
            prefsEditor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        obj = new JSONObject();
        try {
            obj.put("data", data);
            Constants.FbPageList = data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String fbPageData = obj.toString();
        if (!Util.isNullOrEmpty(fbPageData)) {
            if (fbPageData.equals("{\"data\":[]}")) {
                prefsEditor.putString("fbPageData", "");
                Constants.fbPageShareEnabled = false;
                prefsEditor.putBoolean("fbPageShareEnabled",
                        Constants.fbPageShareEnabled);
                prefsEditor.commit();
                Constants.FbPageList = null;
                //InitShareResources();

            } else {
                Constants.fbPageShareEnabled = true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_social__sharing, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fbData() {
        List<String> readPermissions=Arrays.asList("email", "public_profile", "user_friends", "read_insights", "business_management");
        final List<String> publishPermissions = Arrays.asList("publish_actions", "publish_pages", "manage_pages");
        final LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Set<String> permissions = loginResult.getAccessToken().getPermissions();
                boolean contain=permissions.containsAll(publishPermissions);
               // Log.v("ggg",contain+"permission"+loginResult.getAccessToken().getPermissions());
                if(!contain){
                    loginManager.logInWithPublishPermissions(Social_Sharing_Activity.this, publishPermissions);
                }else {

                    final String FACEBOOK_ACCESS_TOKEN = loginResult.getAccessToken().getToken();
                        //Log.v("ggg",FACEBOOK_ACCESS_TOKEN+"ppnull");
                    if(Profile.getCurrentProfile()==null){
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        GraphRequest meRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        try {
                                            JSONObject resp = response.getJSONObject();
                                            saveFbLoginResults(resp.getString("name"),
                                                    FACEBOOK_ACCESS_TOKEN,
                                                    resp.getString("id"));
                                            fbPageData(FROM_FB_PAGE);
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        meRequest.setParameters(parameters);
                        meRequest.executeAsync();

                    }else {
                        //Log.v("ggg",Profile.getCurrentProfile().toString());
                        fbPageData(FROM_FB_PAGE);
                        saveFbLoginResults(Profile.getCurrentProfile().getName(),
                                FACEBOOK_ACCESS_TOKEN,
                                Profile.getCurrentProfile().getId());
                    }
                }
            }

            @Override
            public void onCancel() {
                onFBError();
            }

            @Override
            public void onError(FacebookException error) {
                onFBError();
                //Log.v("ggg",error.toString()+"fberror");
            }
        });
        loginManager.logInWithReadPermissions(this, readPermissions);
    }

    private void saveFbLoginResults(String userName, String accessToken, String id) {
        //String FACEBOOK_USER_NAME = Profile.getCurrentProfile().getName();
        Constants.FACEBOOK_USER_ACCESS_ID = accessToken;
        Constants.FACEBOOK_USER_ID = id;
        NfxRequestClient requestClient = new NfxRequestClient(Social_Sharing_Activity.this)
                .setmFpId(session.getFPID())
                .setmType("facebookusertimeline")
                .setmUserAccessTokenKey(accessToken)
                .setmUserAccessTokenSecret("null")
                .setmUserAccountId(id)
                .setmCallType(FBTYPE)
                .setmName(userName);
        requestClient.connectNfx();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd = ProgressDialog.show(Social_Sharing_Activity.this, "", "Wait While Subscribing...");
            }
        });

        BoostLog.d("FPID: ", session.getFPID());
        session.storeFacebookName(userName);

        session.storeFacebookAccessToken(accessToken);
        DataBase dataBase = new DataBase(activity);
        dataBase.updateFacebookNameandToken(userName, accessToken);

        prefsEditor.putString("fbId", Constants.FACEBOOK_USER_ID);
        prefsEditor.putString("fbAccessId", Constants.FACEBOOK_USER_ACCESS_ID);
        prefsEditor.putString("fbUserName", userName);
        prefsEditor.commit();
    }

    void onFBError() {
        Constants.fbShareEnabled = false;
        prefsEditor.putBoolean("fbShareEnabled", false);
        prefsEditor.commit();
        //Log.v("ggg","hello fberror");
        LoginManager.getInstance().logOut();
        com.facebook.AccessToken.refreshCurrentAccessTokenAsync();
    }

    void onFBPageError(int from) {
        //Log.v("ggg","fbpage error");
        if(from==FROM_AUTOPOST){
            facebookautopost.setChecked(false);
        }else if(from==FROM_FB_PAGE){
            facebookPageCheckBox.setChecked(false);
            Constants.fbPageShareEnabled = false;
            prefsEditor.putBoolean("fbPageShareEnabled", false).apply();
        }

    }



    public void InitShareResources() {
        Constants.FACEBOOK_USER_ID = pref.getString("fbId", "");
        Constants.FACEBOOK_USER_ACCESS_ID = pref.getString("fbAccessId", "");
        Constants.fbShareEnabled = pref.getBoolean("fbShareEnabled", false);
//        Constants.FACEBOOK_PAGE_ID 			= pref.getString("fbPageId", "");
        Constants.FACEBOOK_PAGE_ACCESS_ID = pref.getString("fbPageAccessId", "");
        Constants.fbPageShareEnabled = pref.getBoolean("fbPageShareEnabled", false);
        Constants.twitterShareEnabled = mSharedPreferences.getBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN, false);
        Constants.TWITTER_TOK = pref.getString(OAuth.OAUTH_TOKEN, "");
        Constants.TWITTER_SEC = pref.getString(OAuth.OAUTH_TOKEN_SECRET, "");
        Constants.FbFeedPullAutoPublish = pref.getBoolean("FBFeedPullAutoPublish", false);
        Constants.fbPageFullUrl = pref.getString("fbPageFullUrl", "");
        Constants.fbFromWhichPage = pref.getString("fbFromWhichPage", "");

        if(session.getFPDetails(Key_Preferences.FB_PULL_ENABLED).equals("true")){
            facebookautopost.setChecked(true);
            ivFbPageAutoPull.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.facebook_page));
            fbPullStatus.setVisibility(View.VISIBLE);

            fbPullStatus.setText(session.getFPDetails(Key_Preferences.FB_PULL_PAGE_NAME));
        }



        if (!Util.isNullOrEmpty(Constants.FACEBOOK_USER_ACCESS_ID)) {
            facebookHome.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.facebook_icon));
            facebookHomeStatus.setText(getString(R.string.connected));
            String fbUName = pref.getString("fbUserName", "");
            prefsEditor.putBoolean("fbShareEnabled", true);
            facebookHomeCheckBox.setChecked(true);
            prefsEditor.commit();
        }

    }



    /*public void pullFacebookFeedDialog() {

        final JSONObject obj = new JSONObject();
        try {
            obj.put("ClientId", Constants.clientId);
            obj.put("Count", 5);
            obj.put("Tag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            obj.put("FacebookPageName", Constants.fbPageFullUrl);
            obj.put("AutoPublish", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FacebookFeedPullRegistrationAsyncTask fpa = new FacebookFeedPullRegistrationAsyncTask(Social_Sharing_Activity.this, obj, facebookPageStatus, facebookautopost);
        fpa.execute();

    }*/


   private void setStatus(){
        //Log.v("ggg","resime" +facebookHomeCheckBox.isChecked());
        Methods.isOnline(Social_Sharing_Activity.this);
        facebookHome.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.facebook_icon_inactive));
        facebookHomeCheckBox.setChecked(false);

        facebookPage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.facebookpage_icon_inactive));
        facebookPageCheckBox.setChecked(false);

        ivFbPageAutoPull.setImageResource(R.drawable.facebookpage_icon_inactive);
        facebookautopost.setChecked(pref.getBoolean("FBFeedPullAutoPublish",false));
        if(pref.getInt("fbStatus", 0)==2){
            Methods.showSnackBarNegative(this,"Your Facebook session has expired. Please login.");
        }
        if (!Util.isNullOrEmpty(session.getFacebookName()) && (pref.getInt("fbStatus", 0)==1 || pref.getInt("fbStatus",0)==3)) {
            //Log.v("ggg"," ok");
            facebookHome.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.facebook_icon));
            facebookHomeCheckBox.setChecked(true);
            facebookHomeStatus.setVisibility(View.VISIBLE);
            facebookHomeStatus.setText(session.getFacebookName());

        }
        if (!Util.isNullOrEmpty(session.getFacebookPage()) && pref.getInt("fbPageStatus", 0)==1) {
            facebookPage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.facebook_page));
            facebookPageCheckBox.setChecked(true);
            facebookPageStatus.setVisibility(View.VISIBLE);
            facebookPageStatus.setText(session.getFacebookPage());
        }
        if(!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.FB_PULL_PAGE_NAME))&& pref.getBoolean("FBFeedPullAutoPublish",false)){
            ivFbPageAutoPull.setImageResource(R.drawable.facebook_page);
            fbPullStatus.setText(session.getFPDetails(Key_Preferences.FB_PULL_PAGE_NAME));
            fbPullStatus.setVisibility(View.VISIBLE);
        }
        if (!isAuthenticated()) {
            //twitter.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_inactive));
            // String fbUName = pref.getString(TwitterConstants.PREF_USER_NAME, "");
            twitter.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.twitter_icon_inactive));
            twitterCheckBox.setChecked(false);
            twitterStatus.setVisibility(View.GONE);
            //twitterStatus.setText("Disconnected");
        } else {
            twitterCheckBox.setChecked(true);
            String twitterName = mSharedPreferences.getString(TwitterConstants.PREF_USER_NAME, "");
            twitterStatus.setVisibility(View.VISIBLE);
            twitterStatus.setText("@" + twitterName);
            twitter.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.twitter_icon_active));
        }
    }



    //Rahul Twitter handling
    private void initTwitterSDK(Context context) { // it is fixed for user
        // check whether this could be changed or not
        /*If key and secret key are null ,then it not possbile to communicate with twitter*/
        if (TextUtils.isEmpty(mConsumerKey) || TextUtils.isEmpty(mConsumerSecret)) {
            return;
        }

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(mCallbackUrl)) {
            String verifier = uri.getQueryParameter(mAuthVerifier);
            try {
                AccessToken accessToken = mTwitter.getOAuthAccessToken(
                        mRequestToken, verifier);
                //send twitter info
                saveTwitterInformation(accessToken);
                Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                BoostLog.d("Failed to login ",
                        e.getMessage());
            }

        }
    }
    //check about aleady authenticated
    protected boolean isAuthenticated() {
        return mSharedPreferences.getBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN, false);
    }
    private void saveTwitterInformation(AccessToken accessToken) {
        {
            long userID = accessToken.getUserId();
            User user;
            try {
                user = mTwitter.showUser(userID);
                String username = user.getName();

                NfxRequestClient requestClient = new NfxRequestClient((NfxRequestClient.NfxCallBackListener) Social_Sharing_Activity.this)
                        .setmFpId(session.getFPID())
                        .setmType("twitter")
                        .setmUserAccessTokenKey(accessToken.getToken())
                        .setmUserAccessTokenSecret(accessToken.getTokenSecret())
                        .setmUserAccountId(String.valueOf(userID))
                        .setmCallType(TWITTERTYPE)
                        .setmName(username);
                requestClient.connectNfx();

                pd = ProgressDialog.show(this, "", getString(R.string.wait_while_subscribing));

                SharedPreferences.Editor e = mSharedPreferences.edit();
                e.putString(TwitterConstants.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                e.putString(TwitterConstants.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                //e.putBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN, true);
                e.putString(TwitterConstants.PREF_USER_NAME, username);
                //Log.v("ggg",username+"twittername");
                e.apply();

            } catch (TwitterException e1) {
                BoostLog.d("Failed to Save", e1.getMessage());
            }
        }
    }
    public  void logoutFromTwitter() {
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.remove(TwitterConstants.PREF_KEY_OAUTH_TOKEN);
        e.remove(TwitterConstants.PREF_KEY_OAUTH_SECRET);
        e.remove(TwitterConstants.PREF_KEY_TWITTER_LOGIN);
        e.remove(TwitterConstants.PREF_USER_NAME);
        //Log.v("ggg","twitternameremoved");
        e.apply();
        Constants.twitterShareEnabled = false;
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }
    @Override
    public void startWebAuthentication(String url, RequestToken requestToken) {
        mRequestToken = requestToken;
        final Intent intent = new Intent(this,
                com.nowfloats.Twitter.TwitterAuthenticationActivity.class);
        intent.putExtra(com.nowfloats.Twitter.TwitterAuthenticationActivity.EXTRA_URL, url);
        startActivityForResult(intent, TwitterConstants.WEBVIEW_REQUEST_CODE);
    }
    private void showAlertBox() {
        AlertDialog malertDialog = null;
        AlertDialog.Builder mdialogBuilder = null;
        if (mdialogBuilder == null) {
            mdialogBuilder = new AlertDialog.Builder(Social_Sharing_Activity.this);
            mdialogBuilder.setTitle(getString(R.string.alert));
            mdialogBuilder.setMessage(getString(R.string.no_network));

            mdialogBuilder.setPositiveButton(getString(R.string.enable),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // launch setting Activity
                            startActivityForResult(new Intent(
                                            android.provider.Settings.ACTION_SETTINGS),
                                    0);
                        }
                    });

            mdialogBuilder.setNegativeButton(android.R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert);

            if (malertDialog == null) {
                malertDialog = mdialogBuilder.create();
                malertDialog.show();
            }

        }

    }

    private void showDialog(String headText, String message){
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Html.fromHtml(message));
        builder.setTitle(headText);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog =builder.show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        Typeface face=Typeface.createFromAsset(getAssets(),"Roboto-Light.ttf");
        textView.setTypeface(face);
        textView.setTextColor(Color.parseColor("#808080"));

    }




    /*
     * This callback is called from NfxClient when there is a successfull or failure post
     * to the api for NFX. Cases are there to determine the type of call.
     */
    @Override
    public void nfxCallBack(String response, int callType, String name) {
        if(pd!=null){
            pd.dismiss();
        }
        if(response.equals("error")){
            Toast.makeText(this, "Something went wrong!!! Please try later.", Toast.LENGTH_SHORT).show();
            return;
        }
        BoostLog.d("ggg: ", response + callType + ":");
        switch (callType){
            case FBTYPE:
                Constants.fbShareEnabled = true;
                facebookHome.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon));
                facebookHomeCheckBox.setChecked(true);
                facebookHomeStatus.setVisibility(View.VISIBLE);
                facebookHomeStatus.setText(name);
                prefsEditor = pref.edit();
                prefsEditor.putBoolean("fbShareEnabled", true);
                prefsEditor.putInt("fbStatus",1);
                prefsEditor.apply();
                break;
            case FBPAGETYPE:
                facebookPage.setImageDrawable(getResources().getDrawable(R.drawable.facebook_page));
                facebookPageStatus.setVisibility(View.VISIBLE);
                facebookPageStatus.setText("" + name);
                facebookPageCheckBox.setChecked(true);
                prefsEditor.putBoolean("fbPageShareEnabled", true);
                prefsEditor.putInt("fbPageStatus",1);
                prefsEditor.apply();
                MixPanelController.track(EventKeysWL.FACEBOOK_ANAYTICS,null);
                break;
            case TWITTERTYPE:
                Constants.twitterShareEnabled = true;
                SharedPreferences.Editor e = mSharedPreferences.edit();
                e.putBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN, true);
                e.apply();
                twitterStatus.setVisibility(View.VISIBLE);
                twitterStatus.setText("@" + name);
                twitter.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_active));
                //twitterStatus.setText("Connected");
                twitterCheckBox.setHighlightColor(getResources().getColor(R.color.primaryColor));
                twitterCheckBox.setChecked(true);
                MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_TWITTER, null);
                break;
            case FB_PAGE_DEACTIVATION:
                DataBase dataBase = new DataBase(activity);
                dataBase.updateFacebookPage("", "", "");
                session.storeFacebookPage("");
                session.storeFacebookPageID("");
                session.storeFacebookAccessToken("");
                facebookPage.setImageDrawable(getResources().getDrawable(R.drawable.facebookpage_icon_inactive));
                facebookPageStatus.setVisibility(View.GONE);
                //facebookPageStatus.setText("Disconnected");
                prefsEditor = pref.edit();
                prefsEditor.putBoolean("fbPageShareEnabled", false);
                prefsEditor.apply();
                facebookPageCheckBox.setChecked(false);
                break;
            case FB_DECTIVATION:
                DataBase fb_dataBase = new DataBase(activity);
                fb_dataBase.updateFacebookNameandToken("", "");
                session.storeFacebookName("");
                //Log.v("ggg",session.getFacebookName()+"deactivate name");
                session.storeFacebookAccessToken("");
                facebookHome.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon_inactive));
                facebookHomeStatus.setVisibility(View.GONE);
                //facebookHomeStatus.setText("Disconnected");
                prefsEditor = pref.edit();
                prefsEditor.putBoolean("fbShareEnabled", false);
                prefsEditor.apply();
                break;
            case TWITTER_DEACTIVATION:
                twitterStatus.setVisibility(View.GONE);
                //twitterStatus.setText("Disconnected");
                twitter.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_inactive));
                logoutFromTwitter();
                SharedPreferences.Editor twitterPrefEditor = mSharedPreferences.edit();
                twitterPrefEditor.putBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN, false);
                twitterPrefEditor.apply();
                Constants.twitterShareEnabled = false;
                twitterCheckBox.setChecked(false);
                break;
            case PAGE_NO_FOUND:
                Methods.materialDialog(activity, "Alert", getString(R.string.look_like_no_facebook_page));
                break;
        }
    }
}
