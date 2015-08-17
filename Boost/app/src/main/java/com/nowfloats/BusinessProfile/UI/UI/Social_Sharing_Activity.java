package com.nowfloats.BusinessProfile.UI.UI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.FacebookFeedPullAutoPublishAsyncTask;
import com.nowfloats.NavigationDrawer.API.twitter.FacebookFeedPullRegistrationAsyncTask;
import com.nowfloats.NavigationDrawer.API.twitter.PrepareRequestTokenActivity;
import com.nowfloats.NavigationDrawer.Create_Message_Activity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
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

import java.util.ArrayList;

import oauth.signpost.OAuth;

public class Social_Sharing_Activity extends ActionBarActivity {
    private Toolbar toolbar;
    int size = 0;
    boolean[] checkedPages;
    UserSessionManager session;

    TextView connectTextView, autoPostTextView, bottomFeatureTextView;
    final Facebook facebook = new Facebook(Constants.FACEBOOK_API_KEY);
    private SharedPreferences pref = null;
    SharedPreferences.Editor prefsEditor;
    private ImageView facebookHome;
    private ImageView facebookPage;
    private ImageView twitter;
    private TextView facebookHomeStatus,facebookPageStatus,twitterStatus;
    private CheckBox facebookHomeCheckBox,facebookPageCheckBox,twitterCheckBox;
    private CheckBox facebookautopost;
    private TextView headerText;
    ArrayList<String> items;
    private int numberOfUpdates = 0;
    private boolean numberOfUpdatesSelected = false;
    private Activity activity;
    private MaterialDialog materialProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_sharing);
        session = new UserSessionManager(getApplicationContext(),Social_Sharing_Activity.this);
       // Facebook_Auto_Publish_API.autoPublish(Social_Sharing_Activity.this,session.getFPID());
        Methods.isOnline(Social_Sharing_Activity.this);
        pref = this.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
        activity = Social_Sharing_Activity.this;

        toolbar = (Toolbar) findViewById(R.id.app_bar);

        Typeface myCustomFont = Typeface.createFromAsset(this.getAssets(),"Roboto-Light.ttf");
        Typeface myCustomFont_Medium = Typeface.createFromAsset(this.getAssets(),"Roboto-Medium.ttf");

        setSupportActionBar(toolbar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText("Social Sharing");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        facebookHome = (ImageView) findViewById(R.id.social_sharing_facebook_profile_image);
        facebookPage = (ImageView) findViewById(R.id.social_sharing_facebook_page_image);
        twitter = (ImageView) findViewById(R.id.social_sharing_twitter_image);

        facebookHomeStatus = (TextView) findViewById(R.id.social_sharing_facebook_profile_flag_text);
        facebookPageStatus = (TextView) findViewById(R.id.social_sharing_facebook_page_flag_text);
        twitterStatus = (TextView) findViewById(R.id.social_sharing_twitter_flag_text);
        connectTextView = (TextView) findViewById(R.id.connectTextView);
        autoPostTextView = (TextView) findViewById(R.id.autoPostTextView);
        bottomFeatureTextView = (TextView) findViewById(R.id.bottomFeatureText);

        facebookHomeStatus.setTypeface(myCustomFont);
        facebookPageStatus.setTypeface(myCustomFont);
        twitterStatus.setTypeface(myCustomFont);

        facebookHomeCheckBox = (CheckBox) findViewById(R.id.social_sharing_facebook_profile_checkbox);
        facebookPageCheckBox = (CheckBox) findViewById(R.id.social_sharing_facebook_page_checkbox);
        twitterCheckBox = (CheckBox) findViewById(R.id.social_sharing_twitter_checkbox);
        facebookautopost = (CheckBox) findViewById(R.id.social_sharing_facebook_page_auto_post);

        connectTextView.setTypeface(myCustomFont);
        autoPostTextView.setTypeface(myCustomFont);
        bottomFeatureTextView.setTypeface(myCustomFont);


        facebookPageCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(facebookPageCheckBox.isChecked()) {
                   fbPageData();
                } else {
                    DataBase dataBase = new DataBase(activity);
                    dataBase.updateFacebookPage("","","");
                    session.storeFacebookPage("");
                    session.storeFacebookPageID("");
                    session.storeFacebookAccessToken("");
                    facebookPage.setImageDrawable(getResources().getDrawable(R.drawable.facebookpage_icon_inactive));
                    facebookPageStatus.setText("Disconnected");
                }
            }
        });

        facebookHomeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(facebookHomeCheckBox.isChecked()){
                        fbData();
                } else {
                    DataBase dataBase = new DataBase(activity);
                    dataBase.updateFacebookNameandToken("","");

                    session.storeFacebookName("");
                    session.storeFacebookAccessToken("");
                    facebookHome.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon_inactive));
                    facebookHomeStatus.setText("Disconnected");
                }
            }
        });

        facebookautopost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(facebookautopost.isChecked()){

                    if(session.getShowUpdates() && !Util.isNullOrEmpty(Constants.fbPageFullUrl))
                        selectNumberUpdatesDialog();

                    boolean FbRegistered = pref.getBoolean("FacebookFeedRegd", false);
                    if(FbRegistered == false){
                        if(!Util.isNullOrEmpty(Constants.fbPageFullUrl)){
                           pullFacebookFeedDialog();
                        }
                        else{
                            Util.toast("Please select a Facebook page", getApplicationContext());
                            facebookautopost.setChecked(false);
                        }
                    }
                    else{
                        final JSONObject obj = new JSONObject();
                        try {
                            obj.put("fpId", session.getFPID());
                            obj.put("autoPublish", true);
                            obj.put("clientId", Constants.clientId);
                            obj.put("FacebookPageName", Constants.fbFromWhichPage);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        FacebookFeedPullAutoPublishAsyncTask fap = new FacebookFeedPullAutoPublishAsyncTask(Social_Sharing_Activity.this, obj, true, facebookPageStatus);
                        fap.execute();
                    }

                } else {
                    Toast.makeText(Social_Sharing_Activity.this,"Auto Post Updates are turned OFF",Toast.LENGTH_SHORT).show();

                }

            }
        });

        twitterCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(twitterCheckBox.isChecked()){
                    if(!Util.isNullOrEmpty(Constants.TWITTER_TOK) && !Util.isNullOrEmpty(Constants.TWITTER_SEC) ){
                        twitter.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_active));
                        twitterStatus.setText("Connected");
                        twitterCheckBox.setHighlightColor(getResources().getColor(R.color.primaryColor));

                        if (!Constants.twitterShareEnabled) {
                            MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_TWITTER, null);
                            Constants.twitterShareEnabled = true;
                            Create_Message_Activity.twittersharingenabled = true;
                            twitter.setImageDrawable(getResources().getDrawable(
                                    R.drawable.twitter_icon_active));
                            if (Util.isNullOrEmpty(Constants.TWITTER_TOK)
                                    || Util.isNullOrEmpty(Constants.TWITTER_SEC))
                                Constants.twitterShareEnabled = false;
                            Intent it = new Intent(Social_Sharing_Activity.this, PrepareRequestTokenActivity.class);
                            startActivity(it);
                        } else {
                            twitter.setImageDrawable(getResources().getDrawable(
                                    R.drawable.twitter_icon_inactive));
                            Constants.twitterShareEnabled = false;
                            Create_Message_Activity.twittersharingenabled = false;

                            Constants.TWITTER_TOK = "";
                            Constants.TWITTER_SEC = "";
                            prefsEditor.putString(OAuth.OAUTH_TOKEN, "");
                            prefsEditor.putString(OAuth.OAUTH_TOKEN_SECRET, "");
                            prefsEditor.putString("TWITUName", "");
                        }
                        prefsEditor.putBoolean("twitterShareEnabled",Constants.twitterShareEnabled);
                        prefsEditor.commit();
                    }
                    else{
                        twitter.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_active));
                        //twitter_txt.setText("Twitter");
                        Constants.twitterShareEnabled = true;
                        twitterStatus.setText("Connected");
                        twitterCheckBox.setHighlightColor(getResources().getColor(R.color.primaryColor));


                        if(Util.isNullOrEmpty(Constants.TWITTER_TOK) || Util.isNullOrEmpty(Constants.TWITTER_SEC) )
                        {
                            twitterData();
                        }
                    }
                    prefsEditor.putBoolean("twitterShareEnabled", Constants.twitterShareEnabled);
                    prefsEditor.commit();
                }else{
                 //   if(Util.isNullOrEmpty(Constants.TWITTER_TOK) && Util.isNullOrEmpty(Constants.TWITTER_SEC) ){
                        twitter.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_inactive));
                        twitterStatus.setText("Disconnected");
                        Constants.twitterShareEnabled = false;
                        Constants.TWITTER_TOK 				="";
                        Constants.TWITTER_SEC 				= "";
                        twitterCheckBox.setHighlightColor(getResources().getColor(R.color.primaryColor));

                        prefsEditor.putString(OAuth.OAUTH_TOKEN, "");
                        prefsEditor.putString(OAuth.OAUTH_TOKEN_SECRET, "");
                        prefsEditor.putString("TWITUName", "");
                        prefsEditor.commit();
                  //  }
                }
            }
        });

        InitShareResources();
    }

    private void selectNumberUpdatesDialog() {
       final String[] array = {"Post 5 Updates","Post 10 Updates"};
       new MaterialDialog.Builder(Social_Sharing_Activity.this)
                .title("Post to Facebook Page")
                .items(array)
                .negativeText("Cancel")
                .negativeColorRes(R.color.light_gray)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                })
               .widgetColorRes(R.color.primaryColor)
               .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                   @Override
                   public boolean onSelection(MaterialDialog dialog, View view, int position, CharSequence text) {
                       numberOfUpdatesSelected = true ;
                       session.storeShowUpdates(false);
                       if(position == 0)
                       {
                           numberOfUpdates = 5 ;
                       }

                       if(position == 1)
                       {
                           numberOfUpdates = 10 ;
                       }
                       dialog.dismiss();
                       return true;
                   }
               }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
        if (materialProgress!=null){
            materialProgress.dismiss();
        }
    }

    public void fbPageData() {
        final String[] PERMISSIONS = new String[] { "photo_upload",
                "user_photos", "publish_stream", "read_stream",
                "offline_access", "manage_pages", "publish_actions" };
        materialProgress = new MaterialDialog.Builder(this)
                .widgetColorRes(R.color.accentColor)
                .content("Please Wait...")
                .progress(true, 0).show();

        facebook.authorize(this, PERMISSIONS, new Facebook.DialogListener() {
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
                                        items.add(i,(String)((JSONObject) Constants.FbPageList
                                                .get(i)).get("name"));
                                    }

                                    for (int i = 0; i < size; i++) {
                                        checkedPages[i] = false;
                                    }
                                    facebookPage.setImageDrawable(getResources().getDrawable(R.drawable.facebook_page));
                                }
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        finally {
                            Social_Sharing_Activity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (items!=null && items.size()>0){
                                        final String[] array = items.toArray(new String[items.size()]);
                                        new MaterialDialog.Builder(Social_Sharing_Activity.this)
                                                .title("Select a Page")
                                                .items(array)
                                                .widgetColorRes(R.color.primaryColor)
                                                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                                    @Override
                                                    public boolean onSelection(MaterialDialog dialog, View view, int position, CharSequence text) {
                                                        String strName = array[position];
                                                        try {
                                                            String FACEBOOK_PAGE_ID = (String) ((JSONObject) Constants.FbPageList.get(position)).get("id");
                                                            String page_access_token = ((String) ((JSONObject) Constants.FbPageList.get(position)).get("access_token"));
                                                            session.storePageAccessToken(page_access_token);
                                                            session.storeFacebookPageID(FACEBOOK_PAGE_ID);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        pageSeleted(position,strName,session.getFacebookPageID(),session.getPageAccessToken());
                                                        dialog.dismiss();
                                                        return true;
                                                    }
                                                }).show();
                                    }else {
                                        Methods.materialDialog(activity,"Uh oh~","Looks like there is no Facebook page\nlinked to this account.");
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }

            @Override
            public void onCancel() {
                onFBPageError();
            }

            @Override
            public void onFacebookError(FacebookError e) {
                onFBPageError();
            }

            @Override
            public void onError(DialogError e) {
                onFBPageError();
            }



        });
    }

    public void pageSeleted(int id,final String pageName, String pageID, String pageAccessToken) {
        String s = "";
        JSONObject obj;
        session.storeFacebookPage(pageName);
        JSONArray data = new JSONArray();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                facebookPageStatus.setText("" + pageName);
                facebookPageCheckBox.setChecked(true);
            }
        });


        DataBase dataBase =new DataBase(activity);
        dataBase.updateFacebookPage(pageName, pageID, pageAccessToken);

        obj = new JSONObject();
        try {
            obj.put("id", pageID);
            obj.put("access_token",pageAccessToken);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        return super.onOptionsItemSelected(item);
    }

    public void fbData() {
        final String[] PERMISSIONS = new String[] { "photo_upload",
                "user_photos", "publish_stream", "read_stream",
                "offline_access", "publish_actions" };
        materialProgress = new MaterialDialog.Builder(this)
                .widgetColorRes(R.color.accentColor)
                .content("Please Wait...")
                .progress(true, 0).show();
        facebook.authorize(this, PERMISSIONS,  new Facebook.DialogListener() {

            public void onComplete(Bundle values) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject me;
                        try {
                            me = new JSONObject(facebook.request("me"));
                            Constants.FACEBOOK_USER_ACCESS_ID = facebook.getAccessToken();
                            Constants.FACEBOOK_USER_ID = (String) me.getString("id");

                            String FACEBOOK_ACCESS_TOKEN = facebook.getAccessToken();
                            String FACEBOOK_USER_NAME = (String) me.getString("name");

                            session.storeFacebookName(FACEBOOK_USER_NAME);
                            session.storeFacebookAccessToken(FACEBOOK_ACCESS_TOKEN);
                            DataBase dataBase = new DataBase(activity);
                            dataBase.updateFacebookNameandToken(FACEBOOK_USER_NAME,FACEBOOK_ACCESS_TOKEN);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    facebookHome.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon));
                                    facebookHomeCheckBox.setChecked(true);
                                    facebookHomeStatus.setText(session.getFacebookName());
                                }
                            });
//                            try {
//
//                                // code runs in a thread
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        facebookHome.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon));
//                                        facebookHomeCheckBox.setChecked(true);
//                                        facebookHomeStatus.setText(Constants.FACEBOOK_USER_NAME);
//                                    }
//                                });
//                            } catch (final Exception ex) {
//                                Log.i("---", "Exception in thread");
//                            }


                      //      facebookHomeStatus.setText(Constants.FACEBOOK_USER_NAME);
                            Constants.fbShareEnabled = true;
                            prefsEditor.putBoolean("fbShareEnabled", true);
                            prefsEditor.putString("fbId", Constants.FACEBOOK_USER_ID);
                            prefsEditor.putString("fbAccessId",Constants.FACEBOOK_USER_ACCESS_ID);
                            prefsEditor.putString("fbUserName",FACEBOOK_USER_NAME);
                            prefsEditor.commit();

                        } catch (Exception e1) {
                            e1.printStackTrace();

                        }

//                        try {
//
//                                // code runs in a thread
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        facebookHome.setImageDrawable(getResources().getDrawable(
//                                                R.drawable.facebook_icon));
//                                        facebookHomeCheckBox.setChecked(true);
//                                        facebookHomeStatus.setText(Constants.FACEBOOK_USER_NAME);
//                                    }
//                                });
//                            } catch (final Exception ex) {
//                                Log.i("---", "Exception in thread");
//                            }
//

                        }

                }).start();


            }

            @Override
            public void onCancel() {
                onFBError();
            }

            @Override
            public void onFacebookError(FacebookError e) {
                onFBError();
            }

            @Override
            public void onError(DialogError e) {
                onFBError();
            }

        });



//        facebookHome.setImageDrawable(getResources().getDrawable(
//                R.drawable.facebook_icon));
//        facebookHomeCheckBox.setChecked(true);
//        facebookHomeStatus.setText(Constants.FACEBOOK_USER_NAME);
    }

    void onFBError() {
        Constants.fbShareEnabled = false;
        prefsEditor.putBoolean("fbShareEnabled", false);
        prefsEditor.commit();
    }

    void onFBPageError() {
        Constants.fbPageShareEnabled = false;
        prefsEditor.putBoolean("fbPageShareEnabled", false);
        prefsEditor.commit();
    }


    public void twitterData() {
        Intent it = new Intent(this, PrepareRequestTokenActivity.class);
        startActivity(it);
    }


    public void InitShareResources(){
        Constants.FACEBOOK_USER_ID 			= pref.getString("fbId", "");
        Constants.FACEBOOK_USER_ACCESS_ID 	= pref.getString("fbAccessId", "");
        Constants.fbShareEnabled 			= pref.getBoolean("fbShareEnabled", false);
//        Constants.FACEBOOK_PAGE_ID 			= pref.getString("fbPageId", "");
        Constants.FACEBOOK_PAGE_ACCESS_ID 	= pref.getString("fbPageAccessId", "");
        Constants.fbPageShareEnabled 		= pref.getBoolean("fbPageShareEnabled", false);
        Constants.twitterShareEnabled 		= pref.getBoolean("twitterShareEnabled", false);
        Constants.TWITTER_TOK 				= pref.getString(OAuth.OAUTH_TOKEN, "");
        Constants.TWITTER_SEC 				= pref.getString(OAuth.OAUTH_TOKEN_SECRET, "");
        Constants.FbFeedPullAutoPublish     = pref.getBoolean("FBFeedPullAutoPublish", false);
        Constants.fbPageFullUrl             = pref.getString("fbPageFullUrl", "");
        Constants.fbFromWhichPage           = pref.getString("fbFromWhichPage", "");


        if(!Util.isNullOrEmpty(Constants.TWITTER_TOK) || !Util.isNullOrEmpty(Constants.TWITTER_SEC) ){
            twitter.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_active));
            //twitter_txt.setText("Twitter");
            //twitterName.setText("connected");
            String twitterName = pref.getString("TWITUName", "");
            twitterStatus.setText(twitterName);
            twitterCheckBox.setChecked(true);
            prefsEditor.putBoolean("twitterShareEnabled", true);
            prefsEditor.commit();
        }


        if (!Util.isNullOrEmpty(Constants.FACEBOOK_USER_ACCESS_ID)) {
            facebookHome.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon));
            facebookHomeStatus.setText("connected");
            String fbUName = pref.getString("fbUserName", "");
            prefsEditor.putBoolean("fbShareEnabled", true);
         //   facebookHomeCheckBox.setChecked(true);
            prefsEditor.commit();
        }

    }


    public void createFacebookAutoPost(){
        final JSONObject obj = new JSONObject();
        try {
            obj.put("ClientId", Constants.clientId);
            obj.put("Count", 5);
            obj.put("Tag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            obj.put("FacebookPageName", Constants.fbFromWhichPage);

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void pullFacebookFeedDialog() {

        final JSONObject obj = new JSONObject();
        try {
            obj.put("ClientId", Constants.clientId);
            obj.put("Count", 5);
            obj.put("Tag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            obj.put("FacebookPageName", Constants.fbPageFullUrl);
            obj.put("AutoPublish", true);
        }catch(Exception e){
            e.printStackTrace();
        }
        FacebookFeedPullRegistrationAsyncTask fpa = new FacebookFeedPullRegistrationAsyncTask(Social_Sharing_Activity.this,obj,facebookPageStatus,facebookautopost);
        fpa.execute();
    }


    @Override
    protected void onResume(){
        super.onResume();
        Methods.isOnline(Social_Sharing_Activity.this);
        facebookHome.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon_inactive));
        facebookHomeCheckBox.setChecked(false);
        facebookPageStatus.setText("Disconnected");

        facebookPage.setImageDrawable(getResources().getDrawable(R.drawable.facebookpage_icon_inactive));
        facebookPageCheckBox.setChecked(false);
        facebookHomeStatus.setText("Disconnected");

        if(!Util.isNullOrEmpty(session.getFacebookName()))
        {
            facebookHome.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon));
            facebookHomeCheckBox.setChecked(true);
            facebookHomeStatus.setText(session.getFacebookName());

        }
        if(!Util.isNullOrEmpty(session.getFacebookPage()))
        {
            facebookPage.setImageDrawable(getResources().getDrawable(R.drawable.facebook_page));
            facebookPageCheckBox.setChecked(true);
            String text = session.getFacebookPage();
            facebookPageStatus.setText(session.getFacebookPage());
        }

        if(Util.isNullOrEmpty(Constants.TWITTER_TOK) || Util.isNullOrEmpty(Constants.TWITTER_SEC) ){
            twitter.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_inactive));
            //twitter_txt.setText("Twitter");
            //twitterName.setText("connected");
            String fbUName = pref.getString("TWITUName", "");
            twitterStatus.setText("Disconnected");
            twitterCheckBox.setChecked(false);
            prefsEditor.putBoolean("twitterShareEnabled", false);
            prefsEditor.commit();
        }else{
            String twitterName = pref.getString("TWITUName", "");
            twitterStatus.setText("@"+twitterName);
        }
//        if (Util.isNullOrEmpty(Constants.FACEBOOK_USER_ACCESS_ID)) {
//            facebookHome.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon_inactive));
//            facebookHomeStatus.setText("Disconnected");
//            String fbUName = pref.getString("fbUserName", "");
//            prefsEditor.putBoolean("fbShareEnabled", false);
//            facebookHomeCheckBox.setChecked(false);
//            prefsEditor.commit();
//        }
    }


}
