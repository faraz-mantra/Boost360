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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.nowfloats.NavigationDrawer.model.RiaNodeDataModel;
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
import com.nowfloats.util.RiaEventLogger;
import com.thinksity.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class Create_Message_Activity extends AppCompatActivity {
    private Toolbar toolbar;
    ImageView cameraButton ;
    ContentValues values;
    Bitmap CameraBitmap;
    Uri imageUri;
    public static String path = null;
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

    public static boolean facbookEnabled = false ;
    public static boolean twittersharingenabled = false ;
    public static boolean isFacebookPageShareLoggedIn = false;
    public static final int ACTION_REQUEST_IMAGE_EDIT = 110;


    String mOutputFilePath;
    Uri picUri;
    private Activity activity;
    DataBase dataBase;
    private final int gallery_req_id = 0;
    private final int media_req_id = 1;
    private SharedPreferences mSharedPreferences = null;

    private int mFbPageShare = 0, mFbProfileShare = 0, mTwitterShare = 0;
    private RiaNodeDataModel mRiaNodedata;
    private boolean mIsImagePicking = false;
    private CardView image_card;
    private ImageView deleteButton,editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__message_v2);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        activity = Create_Message_Activity.this;
        Methods.isOnline(activity);
        pref = getSharedPreferences(Constants.PREF_NAME,Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
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
        tosubscribers = false;
        msg = (EditText) findViewById(R.id.createMessageEditText);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mRiaNodedata = getIntent().getParcelableExtra(Constants.RIA_NODE_DATA);

        post = (TextView) toolbar.findViewById(R.id.saveTextView);
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getString(R.string.post_update));
        post.setText(getString(R.string.post_in_capital));

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Methods.isOnline(Create_Message_Activity.this)){

                    return;
                }
                if(!Util.isNullOrEmpty(msg.getText().toString())) {
                    Methods.hideKeyboard(msg, Create_Message_Activity.this);

                    if (!imageIconButtonSelected) {

                        MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_IMAGE_NOT_SELECTED, null);
                        Constants.imageNotSet = true;
                        HashMap<String, String> eventKey = new HashMap<String, String>();
                        eventKey.put(EventKeysWL.CREATE_MESSAGE_ACTIVITY_IMAGE_NOT_SELECTED, "Posted Update with only text");

                    } else {

                        MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY_IMAGE_SELECTED, null);
                        Constants.imageNotSet = false;

                        HashMap<String, String> eventKey = new HashMap<String, String>();
                        eventKey.put(EventKeysWL.CREATE_MESSAGE_ACTIVITY_IMAGE_SELECTED, "Posted Update with text and image");

                    }

                    if (Home_Main_Fragment.bus==null){Home_Main_Fragment.bus = BusProvider.getInstance().getBus();}
                    Constants.createMsg = true;
                    String socialShare = "";
                    if(mFbProfileShare==1){
                        socialShare +="FACEBOOK.";
                    }
                    if(mFbPageShare==1){
                        socialShare +="FACEBOOK_PAGE.";
                    }
                    if (mTwitterShare == 1) {
                        socialShare+="TWITTER.";
                    }
                    Home_Main_Fragment.bus.post(new UploadPostEvent(path, msg.getText().toString(), socialShare));
                    if (path!=null && path.trim().length()>0){
                        //Log.v("ggg",path+" path for upadte");
                        new AlertArchive(Constants.alertInterface,"FEATURE IMAGE",session.getFPID());
                    }else{
                        new AlertArchive(Constants.alertInterface,"UPDATE",session.getFPID());
                    }
                    if(mRiaNodedata!=null){
                        RiaEventLogger.getInstance().logPostEvent(session.getFpTag(),
                                mRiaNodedata.getNodeId(), mRiaNodedata.getButtonId(),
                                mRiaNodedata.getButtonLabel(), RiaEventLogger.EventStatus.COMPLETED.getValue());
                        mRiaNodedata = null;
                    }
                    finish();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } else {
                    YoYo.with(Techniques.Shake).playOn(msg);
                    Methods.showSnackBarNegative(Create_Message_Activity.this,getString(R.string.enter_message));
                }
            }
        });


        facebookShare = (ImageView) findViewById(R.id.create_message_activity_facebokhome_button);
        facebookPageShare = (ImageView) findViewById(R.id.create_message_activity_facebokpage_button);
        twitterloginButton = (ImageView) findViewById(R.id.create_message_activity_twitter_button);

        if(!Util.isNullOrEmpty(session.getFacebookName()) && pref.getInt("fbStatus", 3)==1) {
            facbookEnabled = true;
            mFbProfileShare = 1;
            facebookShare.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon));
        }

        if(!Util.isNullOrEmpty(session.getFacebookPage()) && pref.getInt("fbPageStatus", 3) ==1) {
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


                if(!Util.isNullOrEmpty(session.getFacebookPage()) && pref.getInt("fbPageStatus", 3) ==1) {
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

            }
        });




        facebookShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Util.isNullOrEmpty(session.getFacebookName()) && pref.getInt("fbStatus", 3)==1){
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

            }
        });

        cameraButton = (ImageView) findViewById(R.id.create_mee_activity_facebokhome_button);
        imageIconButton = (ImageView) findViewById(R.id.imageIcon);
        deleteButton = (ImageView) findViewById(R.id.delete_image);
        editButton = (ImageView) findViewById(R.id.edit_image);
        image_card = (CardView)findViewById(R.id.image_card);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageIconButtonSelected =false;
                path = null;
                prefsEditor.putString("image_post",path).apply();
                //Log.v("ggg"," delete"+ path);
                image_card.setVisibility(View.GONE);
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });
        imageIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImage();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImage();
            }
        });
        restoreData();
        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.v("ggg","ontextchanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.v("ggg",s.toString()+" after");
                prefsEditor.putString("msg_post",s.toString()).apply();
            }
        });
    }

    private void editImage() {
        Methods.hideKeyboard(msg,Create_Message_Activity.this);
        if (path != null && path.length() >0) {

            Intent in =new Intent(Create_Message_Activity.this,EditImageActivity.class);
            in.putExtra("image",path);
            startActivityForResult(in, ACTION_REQUEST_IMAGE_EDIT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mRiaNodedata!=null && !mIsImagePicking){
            RiaEventLogger.getInstance().logPostEvent(session.getFpTag(),
                    mRiaNodedata.getNodeId(), mRiaNodedata.getButtonId(),
                    mRiaNodedata.getButtonLabel(), RiaEventLogger.EventStatus.DROPPED.getValue());
            mRiaNodedata = null;
        }
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

    private void restoreData() {
        msg.setText(pref.getString("msg_post",""));
        path = pref.getString("image_post",null);
        //Log.v("ggg","restore "+ msg.getText().toString()+" "+path);
        if (path != null && path.length() >0) {
            CameraBitmap = Util.getBitmap(path, activity);
            setPicture(CameraBitmap);
        }
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
                mIsImagePicking = true;
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Constants.GALLERY_PHOTO);

            }
        } catch (ActivityNotFoundException anfe) {

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

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        media_req_id);
            }
            else {
                mIsImagePicking = true;
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

    }

    public void setPicture(Bitmap bmp) {
        if (!Util.isNullOrEmpty(path)) {
            if (bmp != null) {
                prefsEditor.putString("image_post",path).apply();
                image_card.setVisibility(View.VISIBLE);
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

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(imgUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if (data.getType().equals("image/jpeg") || data.getType().equals("image/png")) {

            filePath = imgUri.getPath();
        }
        return filePath;
    }

    public void setPicture(Uri uri) {
        path = getPath(uri);


        path = Util.saveBitmap(path, this, 720,tagName + System.currentTimeMillis());

        if (!Util.isNullOrEmpty(path)) {
            bmp = Util.getBitmap(path, this);
            CameraBitmap = bmp;
            if (bmp != null) {
                image_card.setVisibility(View.VISIBLE);
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

                try {
                    if (picUri == null) {
                        CameraBitmap = (Bitmap) data.getExtras().get("data");
                        path = Util.saveBitmap(CameraBitmap, activity, tagName + System.currentTimeMillis());
                        picUri = Uri.parse(path);
                        setPicture(CameraBitmap);

                    } else {
                        path = getRealPathFromURI(picUri);
                        CameraBitmap = Util.getBitmap(path, activity);
                        setPicture(CameraBitmap);

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

                        }else{
                            path = getRealPathFromURI(picUri);
                            CameraBitmap = Util.getBitmap(path, activity);
                            setPicture(CameraBitmap);

                        }
                    }else{
                        Methods.showSnackBar(activity,getString(R.string.try_again));
                    }
                }else{
                    path = getRealPathFromURI(picUri);
                    CameraBitmap = Util.getBitmap(path, activity);
                    setPicture(CameraBitmap);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }catch(OutOfMemoryError E){
                E.printStackTrace();
                CameraBitmap.recycle();
                System.gc();
                Methods.showSnackBar(activity,getString(R.string.try_again));
            }
        }else if(ACTION_REQUEST_IMAGE_EDIT==requestCode && resultCode == RESULT_OK){
            //Log.v("ggg", data.getStringExtra("edit_image")+" ");
            path = data.getStringExtra("edit_image");
            CameraBitmap = Util.getBitmap(path, activity);
            setPicture(CameraBitmap);
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



        MixPanelController.track(EventKeysWL.CREATE_MESSAGE_ACTIVITY,null);
        if(Constants.twitterShareEnabled) {
            twitterloginButton.setImageDrawable(getResources().getDrawable(R.drawable.twitter_icon_active));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageIconButtonSelected = false;

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_create__message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        int id = item.getItemId();

        if(id==android.R.id.home){

            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }


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

        //Log.d("photo Effect", "Pictures folder: " + baseDir.getAbsolutePath());
        File aviaryFolder = new File(baseDir, "image_edit");

        if (aviaryFolder.exists()) {
            return aviaryFolder;
        }
        if (aviaryFolder.mkdirs()) {
            return aviaryFolder;
        }
        return Environment.getExternalStorageDirectory();
    }




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

}