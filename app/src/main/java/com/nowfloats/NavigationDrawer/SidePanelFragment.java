package com.nowfloats.NavigationDrawer;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.BusinessProfile.UI.API.UploadPictureAsyncTask;
import com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.DeleteBackgroundImageAsyncTask;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.Twitter.TwitterConnection;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.HashMap;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class SidePanelFragment extends Fragment {
    private static int businessNameWeight = 5;
    private static int businessDescriptionWeight = 10;
    private static int businessCategoryWeight = 5;
    private static int featuredImageWeight = 10;
    private static int phoneWeight = 5;
    private static int emailWeight = 5;
    private static int businessAddressWeight = 10;
    private static int businessTimingWeight = 5;
    private static int facebookWeight = 10;
    private static int twitterWeight = 10;
    private static int shareWebsiteWeight = 5;
    private static int firstUpdatesWeight = 5;
    private static int logoWeight=5;
    private boolean mUserLearnedDrawer;
    private View containerView;
    public ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean mFromSavedInstanceState;
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private Activity mainActivity;
    TextView dashBoardTextView;
    TextView businessProfileTextView;
    TextView customerQueries;
    TextView imageGalleryTextView,businessAppTextview;
    TextView productGalleryTextView;
    TextView StoreTextView;
    TextView cspTextView;
    //TextView enqCount;
    TextView settingsText, chatText, callText, shareText,tvBoostBubble /*tvSiteAppearance*/;
    public static TextView fpNameTextView;
    UserSessionManager session;
    public static ImageView iconImage;
    public static TextView storeName;
    public static ImageView containerImage;
    public ImageView backgroundImageButton;
    private Toolbar toolbar;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    Bitmap CameraBitmap;
    String path = null;
    String imageUrl = "";
    private TextView titleTextView;
    ContentValues values;
    Uri imageUri;
    private static final int GALLERY_PHOTO = 2;
    private static final int CAMERA_PHOTO = 1;
    private Switch bubbleSwitch;
//    protected ImageLoader imageLoader = ImageLoader.getInstance();

    LinearLayout homeLayout, profileLayout, businessAppsLayout, storeLayout, /*customerQueriesLayout,*/ imageGalleryLayout, cspLayout,
            productGalleryLayout, Store_Layout, settingsLayout, chatLayout, callLayout, shareLayout, llGetInTouch,bubbleLayout /*llSiteAppearance*/;
    private RelativeLayout siteMeter;
    private int siteMeterTotalWeight;
    private ProgressBar progressbar;
    private TextView meterValue;
    private boolean fiveUpdatesDone = false;
    private int onUpdate = 4;
    private String originalSite1;

    private final int media_req_id=5;
    private final int gallery_req_id=6;

    private ImageView lockWidgetImageView, lockWidget_ProductGallery, /*lockWidgetImageView_BusinessEnq,*/ lockWidgetImageView_CSP;
    private Button newButton;
    private static HashMap<String, Integer> backgroundImages = new HashMap<String, Integer>();
    private ImageView shareImageView, businessProfileImageView, dasbBoardImageView, callImageView, chatImageView, cspImageView,
            settingsImageView, StoreImageView, productGalleryImageView,businessappImageView, imageGalleryImageView/*, customerQueriesImageView*/ /*ivSiteAppearance*/;
    private PorterDuffColorFilter defaultLabelFilter, whiteLabelFilter;
    private ImageView businessLockImage;
    SharedPreferences pref, mSharedPreferences;


    public interface OnItemClickListener {
        public void onClick(String nextScreen);
    }

    public SidePanelFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mainActivity = activity;
//        imageLoader.init(ImageLoaderConfiguration.createDefault(mainActivity));
        session = new UserSessionManager(activity.getApplicationContext(), activity);
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_side_panel2, container, false);
        addBackgroundImages();
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Log.d("ILUD", "Hello");
        pref = getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        mSharedPreferences = getActivity().getSharedPreferences(TwitterConnection.PREF_NAME,Context.MODE_PRIVATE);

        final String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);
        final String paymentLevel = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL);

        progressbar = (ProgressBar) view.findViewById(R.id.ProgressBar);
        meterValue = (TextView) view.findViewById(R.id.fragment_side_panel_progress_meter_value);
        containerImage = (ImageView) view.findViewById(R.id.backgroundImage);
        siteMeterCalculation();
        try {
            String versionName = mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0).versionName;
            TextView versionCode= (TextView) view.findViewById(R.id.version_name_text);
            versionCode.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Typeface robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");

        Typeface robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");

        whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
        defaultLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.dark_grey), PorterDuff.Mode.SRC_IN);

        //containerImage.setIm
        String category = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY);
        int Imagedrawable = getCategoryBackgroundImage(category);

        if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE).length() > 0) {
            String baseNameProfileImage = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE);
            if (!baseNameProfileImage.contains("http")) {
                baseNameProfileImage = Constants.BASE_IMAGE_URL + "" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE);
            }
            if (baseNameProfileImage != null && baseNameProfileImage.length() > 0) {
                Picasso.with(getActivity())
                        .load(baseNameProfileImage)
                        .resize(540, 0)
                        .placeholder(R.drawable.general_services_background_img)
                        .into(containerImage);
                //Picasso.with(getActivity()).load(baseNameProfileImage).placeholder(R.drawable.general_services_background_img).resize(0, 400).into(containerImage);
            } else {
                Picasso.with(getActivity()).load(R.drawable.general_services_background_img).into(containerImage);
            }
//            imageLoader.displayImage(baseNameProfileImage, containerImage);//
        } else {
            containerImage.setImageDrawable(getResources().getDrawable(Imagedrawable));
        }

        iconImage = (ImageView) view.findViewById(R.id.iconImage);

        iconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ILUD", "Clicked Hello");
                Intent businessAddress = new Intent(getActivity(), Edit_Profile_Activity.class);
                startActivity(businessAddress);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });



        if (session.getIsSignUpFromFacebook().contains("true") && !Util.isNullOrEmpty(session.getFacebookPageURL())) {
            Picasso.with(getActivity())
                    .load(session.getFacebookPageURL())
                    .rotate(90)                             // optional
                    .into(iconImage);
        }

        storeName = (TextView) view.findViewById(R.id.fpNameTextView);
        storeName.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        storeName.setTypeface(robotoMedium);

        siteMeter = (RelativeLayout) view.findViewById(R.id.fragment_side_panel_site_meter);
        siteMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("SiteScore", null);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.site__meter));
                onclickColorChange(null, null, null);
            }
        });

        fpNameTextView = (TextView) view.findViewById(R.id.fpEmailTextView);
        fpNameTextView.setVisibility(View.VISIBLE);

        String rootAlisasURI = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
        String normalURI = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase() + getActivity().getResources().getString(R.string.tag_for_partners);
        if (rootAlisasURI != null && !rootAlisasURI.equals("null") && rootAlisasURI.trim().length() > 0) {
            fpNameTextView.setText(Methods.fromHtml("<u>" + rootAlisasURI + "</u>"));
        }
        else {
            fpNameTextView.setText(Methods.fromHtml("<u>" + normalURI + "</u>"));
        }
        fpNameTextView.setTypeface(robotoMedium);
        fpNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "";
                if (!Util.isNullOrEmpty(url)) {
                    url = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
                } else {
                    url = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                            + getActivity().getResources().getString(R.string.tag_for_partners);
                }
                Intent showWebSiteIntent = new Intent(getContext(), Mobile_Site_Activity.class);
                // showWebSiteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                showWebSiteIntent.putExtra("WEBSITE_NAME", url);
                startActivity(showWebSiteIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        backgroundImageButton = (ImageView) view.findViewById(R.id.side_panel_fragment_change_backgroud_button);
        backgroundImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PorterDuffColorFilter whiteLabelFilter1 = new PorterDuffColorFilter
                        (getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);

                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .customView(R.layout.featuredimage_popup, true)
                        .show();

                View view = dialog.getCustomView();
                LinearLayout deleteImage = (LinearLayout) view.findViewById(R.id.deletebackgroundImage);
                deleteImage.setVisibility(View.GONE);
                TextView title = (TextView) view.findViewById(R.id.textview_heading);
                title.setText(getString(R.string.upload_background_image));
                LinearLayout takeCamera = (LinearLayout) view.findViewById(R.id.cameraimage);
                LinearLayout takeGallery = (LinearLayout) view.findViewById(R.id.galleryimage);

                ImageView cameraImg = (ImageView) view.findViewById(R.id.pop_up_camera_imag);
                ImageView galleryImg = (ImageView) view.findViewById(R.id.pop_up_gallery_img);
                ImageView deleteImg = (ImageView) view.findViewById(R.id.pop_up_delete_img);

                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE)) && session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE).length() > 0) {
                    deleteImage.setVisibility(View.VISIBLE);
                }

                cameraImg.setColorFilter(whiteLabelFilter1);
                galleryImg.setColorFilter(whiteLabelFilter1);
                deleteImg.setColorFilter(whiteLabelFilter1);

                takeCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MixPanelController.track(EventKeysWL.SIDE_PANEL_CHANGE_BACKGROUND_CAMERA, null);
                        cameraIntent();
                        dialog.dismiss();
                    }
                });

                takeGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MixPanelController.track(EventKeysWL.SIDE_PANEL_CHANGE_BACKGROUND_GALLERY, null);
                        galleryIntent();
                        dialog.dismiss();

                    }
                });

                deleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteBackgroundImage();
                        dialog.dismiss();

                    }
                });

            }
        });

        View card = view.findViewById(R.id.navigationDrawer_main_leftPane);

        homeLayout = (LinearLayout) card.findViewById(R.id.firstRow_Layout);
        profileLayout = (LinearLayout) card.findViewById(R.id.secondRow_Layout);
        cspLayout = (LinearLayout) card.findViewById(R.id.csp_Layout);
        //customerQueriesLayout = (LinearLayout) card.findViewById(R.id.thirdRow_Layout);
        businessAppsLayout = (LinearLayout) card.findViewById(R.id.customer_app_Layout);
        imageGalleryLayout = (LinearLayout) card.findViewById(R.id.fourthRow_Layout);
        productGalleryLayout = (LinearLayout) card.findViewById(R.id.product_gal_Layout);
        Store_Layout = (LinearLayout) card.findViewById(R.id.storeRow_Layout);
        settingsLayout = (LinearLayout) card.findViewById(R.id.fifthRow_Layout);
        chatLayout = (LinearLayout) card.findViewById(R.id.sixthRow_Layout);
        callLayout = (LinearLayout) card.findViewById(R.id.seventhRow_Layout);
        shareLayout = (LinearLayout) card.findViewById(R.id.eigthRow_Layout);
        llGetInTouch = (LinearLayout) card.findViewById(R.id.ll_get_in_touch);
        bubbleLayout = (LinearLayout) card.findViewById(R.id.ninethRow_Layout);
        //llSiteAppearance = (LinearLayout) card.findViewById(R.id.ll_site_appearance);

        if (session.getIsThinksity().equals("true")) {
            chatLayout.setVisibility(View.GONE);
            callLayout.setVisibility(View.GONE);
            productGalleryLayout.setVisibility(View.GONE);
        }

        if (session.getISEnterprise().equals("true")) {
            profileLayout.setVisibility(View.GONE);
            cspLayout.setVisibility(View.GONE);
            imageGalleryLayout.setVisibility(View.GONE);
            businessAppsLayout.setVisibility(View.GONE);
            chatLayout.setVisibility(View.GONE);
            siteMeter.setVisibility(View.GONE);
            callLayout.setVisibility(View.GONE);
            shareLayout.setVisibility(View.GONE);
            productGalleryLayout.setVisibility(View.GONE);
            Store_Layout.setVisibility(View.GONE);
        }

        //customerQueries = (TextView) customerQueriesLayout.findViewById(R.id.thirdRow_TextView);
        imageGalleryTextView = (TextView) imageGalleryLayout.findViewById(R.id.fourthRow_TextView);
        productGalleryTextView = (TextView) productGalleryLayout.findViewById(R.id.Product_Gal_TextView);
        StoreTextView = (TextView) Store_Layout.findViewById(R.id.storeRow_TextView);
        dashBoardTextView = (TextView) homeLayout.findViewById(R.id.firstrow_TextView);
        settingsText = (TextView) settingsLayout.findViewById(R.id.fifthRow_TextView);
        businessProfileTextView = (TextView) profileLayout.findViewById(R.id.secondRow_TextView);
        cspTextView = (TextView) cspLayout.findViewById(R.id.csp_TextView);
        businessAppTextview = (TextView) businessAppsLayout.findViewById(R.id.customer_app_TextView);
        chatText = (TextView) chatLayout.findViewById(R.id.sixthRow_TextView);
        callText = (TextView) callLayout.findViewById(R.id.seventhRow_TextView);
        shareText = (TextView) shareLayout.findViewById(R.id.eighthRow_TextView);
        tvBoostBubble = (TextView) bubbleLayout.findViewById(R.id.ninethRow_TextView);
        bubbleSwitch = (Switch) bubbleLayout.findViewById(R.id.ninethRow_Switch);
        //tvSiteAppearance = (TextView) llSiteAppearance.findViewById(R.id.tv_site_appearance);
        if(!Methods.isAccessibilitySettingsOn(getActivity())) {
           session.setBubbleStatus(false);
        }
        bubbleSwitch.setChecked(session.isBoostBubbleEnabled());

        bubbleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    session.setBubbleStatus(isChecked);
                }else{

                    if ((android.os.Build.VERSION.SDK_INT >= 23 && getActivity() != null && !Settings.canDrawOverlays(getActivity()))
                            || (!Methods.isAccessibilitySettingsOn(getActivity()))) {
                        session.setBubbleTime(-1);
                        ((OnItemClickListener) mainActivity).onClick(getString(R.string.home));
                    }else {
                        session.setBubbleStatus(isChecked);
                    }

                }
            }
        });

        lockWidgetImageView = (ImageView) imageGalleryLayout.findViewById(R.id.lock_widget);
        businessLockImage = (ImageView) businessAppsLayout.findViewById(R.id.business_lock_widget);
        lockWidget_ProductGallery = (ImageView) productGalleryLayout.findViewById(R.id.lock_product_gal);
        //lockWidgetImageView_BusinessEnq = (ImageView) customerQueriesLayout.findViewById(R.id.lock_widget_business_enquiries);
        lockWidgetImageView_CSP = (ImageView) cspLayout.findViewById(R.id.lock_widget_csp);
        //Constants.ImageGalleryWidget = false ;
        newButton = (Button) businessAppsLayout.findViewById(R.id.new_business_button);
        // Constants.BusinessEnquiryWidget = true;

        if (!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_GALLERY).contains("IMAGEGALLERY")) {
            lockWidgetImageView.setVisibility(View.VISIBLE);
        } else {
            lockWidgetImageView.setVisibility(View.GONE);
        }
        newButton.setVisibility(View.VISIBLE);

        if (!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_PRODUCT_GALLERY).contains("PRODUCTCATALOGUE")) {
            lockWidget_ProductGallery.setVisibility(View.VISIBLE);
        } else {
            lockWidget_ProductGallery.setVisibility(View.GONE);
        }

        if (!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TOB).contains("TOB")) {
            //lockWidgetImageView_BusinessEnq.setVisibility(View.VISIBLE);

        } else {
            //lockWidgetImageView_BusinessEnq.setVisibility(View.GONE);
        }

        if (!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_CUSTOMPAGES).contains("CUSTOMPAGES")) {
            lockWidgetImageView_CSP.setVisibility(View.VISIBLE);
        } else {
            lockWidgetImageView_CSP.setVisibility(View.GONE);
        }

        dasbBoardImageView = (ImageView) homeLayout.findViewById(R.id.firstrow_ImageView);
        businessProfileImageView = (ImageView) profileLayout.findViewById(R.id.secondRow_ImageView);
        //customerQueriesImageView = (ImageView) customerQueriesLayout.findViewById(R.id.thirdRow_ImageView);
        businessappImageView = (ImageView) businessAppsLayout.findViewById(R.id.customer_app_ImageView);
        imageGalleryImageView = (ImageView) imageGalleryLayout.findViewById(R.id.fourthRow_ImageView);
        productGalleryImageView = (ImageView) productGalleryLayout.findViewById(R.id.Product_Gal_ImageView);
        StoreImageView = (ImageView) Store_Layout.findViewById(R.id.storeRow_ImageView);
        cspImageView = (ImageView) cspLayout.findViewById(R.id.csp_ImageView);
        settingsImageView = (ImageView) settingsLayout.findViewById(R.id.fifthRow_ImageView);
        chatImageView = (ImageView) chatLayout.findViewById(R.id.sixthRow_ImageView);
        callImageView = (ImageView) callLayout.findViewById(R.id.seventhRow_ImageView);
        shareImageView = (ImageView) shareLayout.findViewById(R.id.eigthRow_ImageView);
        //ivSiteAppearance = (ImageView) llSiteAppearance.findViewById(R.id.iv_site_appearance);

        dashBoardTextView.setTypeface(robotoMedium);
        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dashBoardTextView.setTextColor(Color.);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.home));
                MixPanelController.track(EventKeysWL.SIDE_PANEL_DASHBOARD, null);
                onclickColorChange(dasbBoardImageView, dashBoardTextView, homeLayout);
            }
        });

        businessProfileTextView.setTypeface(robotoMedium);
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.business_profile));
                onclickColorChange(businessProfileImageView, businessProfileTextView, profileLayout);
                MixPanelController.track(EventKeysWL.SIDE_PANEL_BUSINESS_PROFILE, null);
            }
        });

        /*tvSiteAppearance.setTypeface(robotoMedium);
        llSiteAppearance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnItemClickListener) mainActivity).onClick(getResources().getString(R.string.side_panel_site_appearance));
                onclickColorChange(ivSiteAppearance, tvSiteAppearance);
                //MixPanelController.track(EventKeysWL.SIDE_PANEL_BUSINESS_PROFILE, null);
            }
        });*/

        //customerQueries.setTypeface(robotoMedium);
        /*customerQueriesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enqCount.getVisibility()==View.VISIBLE){
                    enqCount.setVisibility(View.INVISIBLE);
                    session.setEnquiryCount(session.getLatestEnqCount());
                }
                onclickColorChange(customerQueriesImageView, customerQueries, customerQueriesLayout);
                if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TOB).contains("TOB")) {
                    ((OnItemClickListener) mainActivity).onClick(getString(R.string.business_enquiries_title));
                    MixPanelController.track(EventKeysWL.SIDE_PANEL_BUSINESS_ENQUIRIES, null);
                } else {
                    showAlertMaterialDialog();
                }
            }
        });*/

        imageGalleryTextView.setTypeface(robotoMedium);
        imageGalleryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickColorChange(imageGalleryImageView, imageGalleryTextView, imageGalleryLayout);
                if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_GALLERY).contains("IMAGEGALLERY")) {
                    ((OnItemClickListener) mainActivity).onClick(getString(R.string.image_gallery));
                    MixPanelController.track(EventKeysWL.SIDE_PANEL_IMAGE_GALLERY, null);
                } else {
                    showAlertMaterialDialog();
                }
            }
        });

        productGalleryTextView.setTypeface(robotoMedium);
        productGalleryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickColorChange(productGalleryImageView, productGalleryTextView, productGalleryLayout);
                if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_PRODUCT_GALLERY).contains("PRODUCTCATALOGUE")) {
                    ((OnItemClickListener) mainActivity).onClick(getString(R.string.product_gallery));
                    MixPanelController.track(EventKeysWL.SIDE_PANEL_PRODUCT_GALLERY, null);
                } else {
                    showAlertMaterialDialog();
                }
            }
        });

        StoreTextView.setTypeface(robotoMedium);
        Store_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickColorChange(StoreImageView, StoreTextView, Store_Layout);
                ((OnItemClickListener) mainActivity).onClick("Store");
                MixPanelController.track(EventKeysWL.SIDE_PANEL_PRODUCT_GALLERY, null);
            }
        });

        cspTextView.setTypeface(robotoMedium);
        cspLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_CUSTOMPAGES).contains("CUSTOMPAGES")) {
                    onclickColorChange(cspImageView, cspTextView, cspLayout);
                    ((OnItemClickListener) mainActivity).onClick("csp");
                    MixPanelController.track(EventKeysWL.SIDE_PANEL_PRODUCT_GALLERY, null);
                } else {
                    showAlertMaterialDialog();
                }
            }
        });

        settingsText.setTypeface(robotoMedium);
        callText.setTypeface(robotoMedium);
        chatText.setTypeface(robotoMedium);
        shareText.setTypeface(robotoMedium);

        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickColorChange(settingsImageView, settingsText, settingsLayout);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.setting));
                MixPanelController.track(EventKeysWL.SIDE_PANEL_SETTINGS, null);
            }
        });

        chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickColorChange(null, chatText, chatLayout);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.chat));
                MixPanelController.track("ChatWithRia", null);
            }
        });

        callLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("ContactUs", null);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.call));
                onclickColorChange(callImageView, callText, callLayout);
            }
        });
        businessAppTextview.setTypeface(robotoMedium);
        businessAppsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MixPanelController.track(MixPanelController.BUSINESS_APP, null);
                onclickColorChange(businessappImageView, businessAppTextview, businessAppsLayout);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.my_business_apps));

            }
        });
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("ShareFromSidepanel", null);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.share));
                onclickColorChange(shareImageView, shareText, shareLayout);
            }
        });

        /*if(checkExpiry()){
            llSiteAppearance.setVisibility(View.GONE);
        }*/
        llGetInTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String headerValue = getResources().getString(R.string.settings_feedback_link);     //"create@prostinnovation.com";

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", headerValue, null));
                getActivity().startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
            }
        });
        //enqCount = (TextView)view.findViewById(R.id.enquiry_count_textview);
        //Log.d("Executing Async: ", Constants.beCountUrl + "?clientId=" + Constants.clientId + "&fpId=" + session.getFPID());
       /* RequestQueue queue = AppController.getInstance().getRequestQueue();
        BoostLog.d("Executing Async: ", Constants.beCountUrl + "?clientId=" + Constants.clientId + "&fpId=" + session.getFPID());
        /*RequestQueue queue = AppController.getInstance().getRequestQueue();
        StringRequest beCountRequest = new StringRequest(Request.Method.GET, Constants.beCountUrl + "?clientId=" + Constants.clientId + "&fpId=" + session.getFPID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!Util.isNullOrEmpty(session.getEnquiryCount())){
                    Constants.enqCount = Integer.parseInt(response)-Integer.parseInt(session.getEnquiryCount());
                    if(*//*Constants.enqCount > 0 && lockWidgetImageView_BusinessEnq.getVisibility()!=View.VISIBLE*//*false){
                        //enqCount.setVisibility(View.VISIBLE);
                        //enqCount.setText(Constants.enqCount + "");
                    }
                    session.setLatestEnqCount(response+"");
                }else {
                    session.setEnquiryCount(response);
                }
                BoostLog.d("Response Business Enquiry: ", response + "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                BoostLog.d("Error Business Enquiry: ", error.getMessage());
            }
        });
        queue.add(beCountRequest);*/

        view.findViewById(R.id.tv_write_to_ria_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMailDetailDilaog();
            }
        });


    }

    private void showMailDetailDilaog() {
        new MaterialDialog.Builder(getActivity())
                .title("Connect to Customer Support")
                .content("When you write an email to Ria, a support ticket will be generated and " +
                        "sent to your registered email id automatically. Our Customer Support " +
                        "will get in touch with you within 24 hours.")
                .positiveText("Ok")
                .positiveColor(Color.parseColor("#808080"))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }


    private boolean checkExpiry() {
        boolean flag = false;
        String strExpiryTime = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EXPIRY_DATE);
        long expiryTime = -1;
        if(strExpiryTime!=null){
            expiryTime = Long.parseLong(strExpiryTime.split("\\(")[1].split("\\)")[0]);
        }
        if(expiryTime!=-1 && ((expiryTime - System.currentTimeMillis())/86400000<180) && !session.getWebTemplateType().equals("6")){
            flag = true;
        }
        return flag;
    }

    private void showAlertMaterialDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.features_not_available))
                .content(Html.fromHtml(getString(R.string.check_store_for_upgrade_info)))
                .positiveText(getString(R.string.goto_store))
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
//                        Constants.showStoreScreen = true ;
//                        getActivity().getSupportFragmentManager().popBackStack();
                        if(!getString(R.string.goto_store).equalsIgnoreCase("ok"))//this condition added for flavor check
                        ((OnItemClickListener) mainActivity).onClick("store");

                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static int getCategoryBackgroundImage(String category) {
//        backgroundImages.put("GENERALSERVICES", R.drawable.general_services_background_img);
        /*if(category.contains("GENERALSERVICES"))
        {
            return R.drawable.general_services_background_img;
        }else
//        backgroundImages.put("ARCHITECTURE",R.drawable.architecture_background_img);
        if(category.contains("ARCHITECTURE"))
        {
            return R.drawable.architecture_background_img;
        }else
//        backgroundImages.put("AUTOMOTIVE",R.drawable.automotive_background_img);
        if(category.contains("AUTOMOTIVE"))
        {
            return R.drawable.automotive_background_img;
        }else
//        backgroundImages.put("BLOGS",R.drawable.blogs_background_img);
        if(category.contains("BLOGS"))
        {
            return R.drawable.blogs_background_img;
        }else
//        backgroundImages.put("EDUCATION",R.drawable.education_background_img);
        if(category.contains("EDUCATION"))
        {
            return R.drawable.education_background_img;
        }else
//        backgroundImages.put("ELECTRONICS", R.drawable.electronics_background_img);
        if(category.contains("ELECTRONICS"))
        {
            return R.drawable.electronics_background_img;
        }else
//        backgroundImages.put("ENTERTAINMENT",R.drawable.entertainment_background_img);
        if(category.contains("ENTERTAINMENT"))
        {
            return R.drawable.entertainment_background_img;
        }else
//        backgroundImages.put("EVENTS",R.drawable.events_background_img);
        if(category.contains("EVENTS"))
        {
            return R.drawable.events_background_img;
        }else
//        backgroundImages.put("F&B-BAKERY",R.drawable.fb_bakery_background_img);
        if(category.contains("F&B-BAKERY"))
        {
            return R.drawable.fb_bakery_background_img;
        }else
//        backgroundImages.put("F&B-BARS",R.drawable.fb_bars_background_img);
        if(category.contains("F&B-BARS"))
        {
            return R.drawable.fb_bars_background_img;
        }else

//        backgroundImages.put("F&B-CAFE",R.drawable.fb_cafe_background_img);
        if(category.contains("F&B-CAFE"))
        {
            return R.drawable.fb_cafe_background_img;
        }else
//        backgroundImages.put("F&B-RESTAURANTS", R.drawable.fb_restaurants_background_img);
        if(category.contains("F&B-RESTAURANTS"))
        {
            return R.drawable.fb_restaurants_background_img;
        }else
//        backgroundImages.put("FASHION-APPAREL",R.drawable.fashion_apparel_background_img);
        if(category.contains("FASHION-APPAREL"))
        {
            return R.drawable.fashion_apparel_background_img;
        }else
//        backgroundImages.put("FASHION-FOOTWEAR",R.drawable.fashion_footwear_background_img);
        if(category.contains("FASHION-FOOTWEAR"))
        {
            return R.drawable.fashion_footwear_background_img;
        }else
//        backgroundImages.put("FLOWERSSHOP",R.drawable.flower_shop_background_img);
        if(category.contains("FLOWERSSHOP"))
        {
            return R.drawable.flower_shop_background_img;
        }else
//        backgroundImages.put("FURNITURE",R.drawable.furniture_background_img);
        if(category.contains("FURNITURE"))
        {
            return R.drawable.furniture_background_img;
        }else
//        backgroundImages.put("GIFTS&NOVELTIES", R.drawable.gifts_novelties_background_img);
        if(category.contains("GIFTS&NOVELTIES"))
        {
            return R.drawable.gifts_novelties_background_img;
        }else
//        backgroundImages.put("HEALTH&FITNESS",R.drawable.health_fitness_background_img);
        if(category.contains("HEALTH&FITNESS"))
        {
            return R.drawable.health_fitness_background_img;
        }else
//        backgroundImages.put("HOMEAPPLICANCES",R.drawable.home_appliances_background_img);
        if(category.contains("HOMEAPPLICANCES"))
        {
            return R.drawable.home_appliances_background_img;
        }else
//        backgroundImages.put("HOMECARE",R.drawable.home_care_background_img);
        if(category.contains("HOMECARE"))
        {
            return R.drawable.home_care_background_img;
        }else
//        backgroundImages.put("HOMEMAINTENANCE",R.drawable.home_maintenance_background_img);
        if(category.contains("HOMEMAINTENANCE"))
        {
            return R.drawable.home_maintenance_background_img;
        }else
//        backgroundImages.put("HOTEL&MOTELS",R.drawable.hotel_motels_background_img);
        if(category.contains("HOTEL&MOTELS"))
        {
            return R.drawable.hotel_motels_background_img;
        }else
//        backgroundImages.put("INTERIORDESIGN",R.drawable.interior_design_background_img);
        if(category.contains("INTERIORDESIGN"))
        {
            return R.drawable.interior_design_background_img;
        }else
//        backgroundImages.put("MEDICAL-DENTAL",R.drawable.medical_dental_background_img);
        if(category.contains("MEDICAL-DENTAL"))
        {
            return R.drawable.medical_dental_background_img;
        }else
//        backgroundImages.put("MEDICAL-GENERAL",R.drawable.medical_general_background_img);
        if(category.contains("MEDICAL-GENERAL"))
        {
            return R.drawable.medical_general_background_img;
        }else
//        backgroundImages.put("NATAURAL&AYURVEDA",R.drawable.natural_ayurveda_background_img);
        if(category.contains("NATAURAL&AYURVEDA"))
        {
            return R.drawable.natural_ayurveda_background_img;
        }else
//        backgroundImages.put("KINDERGARTEN",R.drawable.kinder_garten_background_img);
        if(category.contains("KINDERGARTEN"))
        {
            return R.drawable.kinder_garten_background_img;
        }else
//        backgroundImages.put("PETS",R.drawable.pets_background_img);
        if(category.contains("PETS"))
        {
            return R.drawable.pets_background_img;
        }else
//        backgroundImages.put("PHOTOGRAPHY", R.drawable.photography_background_img);
        if(category.contains("PHOTOGRAPHY"))
        {
            return R.drawable.photography_background_img;
        }else
//        backgroundImages.put("REALESTATE&CONSTRUCTION",R.drawable.real_estate_construction_background_img);
        if(category.contains("REALESTATE&CONSTRUCTION"))
        {
            return R.drawable.real_estate_construction_background_img;
        }else
//        backgroundImages.put("SPA",R.drawable.spa_background_img);
        if(category.contains("SPA"))
        {
            return R.drawable.spa_background_img;
        }else
//        backgroundImages.put("SPORTS",R.drawable.sports_background_img);
        if(category.contains("SPORTS"))
        {
            return R.drawable.sports_background_img;
        }else
//        backgroundImages.put("TOURISM",R.drawable.tourism_background_img);
        if(category.contains("TOURISM"))
        {
            return R.drawable.tourism_background_img;
        }else
//        backgroundImages.put("WATCHES&JEWELRY",R.drawable.watches_jewelry_background_img);
        if(category.contains("WATCHES&JEWELRY"))
        {
            return R.drawable.watches_jewelry_background_img;
        }else
//        backgroundImages.put("OTHERRETAIL", R.drawable.other_retail_background_img);
        if(category.contains("OTHERRETAIL"))
        {
            return R.drawable.other_retail_background_img;
        }*/

        return R.drawable.general_services_background_img;
    }

    private void deleteBackgroundImage() {
        DeleteBackgroundImageAsyncTask deleteBackgroundImageAsyncTask = new DeleteBackgroundImageAsyncTask(getActivity(), session);
        deleteBackgroundImageAsyncTask.execute();
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_GALLERY).contains("IMAGEGALLERY")) {
                    lockWidgetImageView.setVisibility(View.VISIBLE);
                } else {
                    lockWidgetImageView.setVisibility(View.GONE);
                }
                if (!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_PRODUCT_GALLERY).contains("PRODUCTCATALOGUE")) {
                    lockWidget_ProductGallery.setVisibility(View.VISIBLE);
                } else {
                    lockWidget_ProductGallery.setVisibility(View.GONE);
                }
                if (!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TOB).contains("TOB")) {
                    //lockWidgetImageView_BusinessEnq.setVisibility(View.VISIBLE);
                } else {
                    //lockWidgetImageView_BusinessEnq.setVisibility(View.GONE);
                }
                if (!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_CUSTOMPAGES).contains("CUSTOMPAGES")) {
                    lockWidgetImageView_CSP.setVisibility(View.VISIBLE);
                } else {
                    lockWidgetImageView_CSP.setVisibility(View.GONE);
                }
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                siteMeterCalculation();
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void syncState() {
                super.syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
//                if(slideOffset<0.6) {
//                    toolbar.setAlpha(1 - slideOffset);
//                }
            }
        };
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void addBackgroundImages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (backgroundImages.size() == 0) {
                    backgroundImages.put("GENERALSERVICES", R.drawable.general_services_background_img);
                  /*backgroundImages.put("ARCHITECTURE",R.drawable.architecture_background_img);
                    backgroundImages.put("AUTOMOTIVE",R.drawable.automotive_background_img);
                    backgroundImages.put("BLOGS",R.drawable.blogs_background_img);
                    backgroundImages.put("EDUCATION",R.drawable.education_background_img);
                    backgroundImages.put("ELECTRONICS", R.drawable.electronics_background_img);
                    backgroundImages.put("ENTERTAINMENT",R.drawable.entertainment_background_img);
                    backgroundImages.put("EVENTS",R.drawable.events_background_img);
                    backgroundImages.put("F&B-BAKERY",R.drawable.fb_bakery_background_img);
                    backgroundImages.put("F&B-BARS",R.drawable.fb_bars_background_img);
                    backgroundImages.put("F&B-CAFE",R.drawable.fb_cafe_background_img);
                    backgroundImages.put("F&B-RESTAURANTS", R.drawable.fb_restaurants_background_img);
                    backgroundImages.put("FASHION-APPAREL",R.drawable.fashion_apparel_background_img);
                    backgroundImages.put("FASHION-FOOTWEAR",R.drawable.fashion_footwear_background_img);
                    backgroundImages.put("FLOWERSSHOP",R.drawable.flower_shop_background_img);
                    backgroundImages.put("FURNITURE",R.drawable.furniture_background_img);
                    backgroundImages.put("GIFTS&NOVELTIES", R.drawable.gifts_novelties_background_img);
                    backgroundImages.put("HEALTH&FITNESS",R.drawable.health_fitness_background_img);
                    backgroundImages.put("HOMEAPPLICANCES",R.drawable.home_appliances_background_img);
                    backgroundImages.put("HOMECARE",R.drawable.home_care_background_img);
                    backgroundImages.put("HOMEMAINTENANCE",R.drawable.home_maintenance_background_img);
                    backgroundImages.put("HOTEL&MOTELS",R.drawable.hotel_motels_background_img);
                    backgroundImages.put("INTERIORDESIGN",R.drawable.interior_design_background_img);
                    backgroundImages.put("MEDICAL-DENTAL",R.drawable.medical_dental_background_img);
                    backgroundImages.put("MEDICAL-GENERAL",R.drawable.medical_general_background_img);
                    backgroundImages.put("NATAURAL&AYURVEDA",R.drawable.natural_ayurveda_background_img);
                    backgroundImages.put("KINDERGARTEN",R.drawable.kinder_garten_background_img);
                    backgroundImages.put("PETS",R.drawable.pets_background_img);
                    backgroundImages.put("PHOTOGRAPHY", R.drawable.photography_background_img);
                    backgroundImages.put("REALESTATE&CONSTRUCTION",R.drawable.real_estate_construction_background_img);
                    backgroundImages.put("SPA",R.drawable.spa_background_img);
                    backgroundImages.put("SPORTS",R.drawable.sports_background_img);
                    backgroundImages.put("TOURISM",R.drawable.tourism_background_img);
                    backgroundImages.put("WATCHES&JEWELRY",R.drawable.watches_jewelry_background_img);
                    backgroundImages.put("OTHERRETAIL", R.drawable.other_retail_background_img);*/
                }
            }
        }).start();
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
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        media_req_id);
            }
            else {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                imageUri = getActivity().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(captureIntent, PICK_FROM_CAMERA);
            }
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
        }
    }

        public void galleryIntent() {
            try {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!=
                        PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            gallery_req_id);
                }else {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(i, GALLERY_PHOTO);
                }
            } catch (ActivityNotFoundException anfe) {
                // display an error message
                String errorMessage = getString(R.string.device_does_not_support_capturing_image);
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            try {
                if (resultCode == getActivity().RESULT_OK && (CAMERA_PHOTO == requestCode)) {
                    try {
                        path = null;
                        if (imageUri != null) {
                            path = getRealPathFromURI(imageUri);
                            CameraBitmap = Util.getBitmap(path, getActivity());
                            imageUrl = getRealPathFromURI(imageUri);
                            path = Util.saveBitmap(path, getActivity(), "ImageFloat" + System.currentTimeMillis());
                        } else {
                            if (data != null) {
                                imageUri = data.getData();
                                if (imageUri == null) {
                                    CameraBitmap = (Bitmap) data.getExtras().get("data");
                                    if (CameraBitmap != null) {
                                        path = Util.saveCameraBitmap(CameraBitmap, getActivity(), "ImageFloat" + System.currentTimeMillis());
                                        imageUri = Uri.parse(path);
                                    }
                                } else {
                                    path = getRealPathFromURI(imageUri);
                                    CameraBitmap = Util.getBitmap(path, getActivity());
                                    imageUrl = getRealPathFromURI(imageUri);
                                    path = Util.saveBitmap(path, getActivity(), "ImageFloat" + System.currentTimeMillis());
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (OutOfMemoryError E) {
                        E.printStackTrace();
                        CameraBitmap.recycle();
                        System.gc();
                        Methods.showSnackBar(getActivity(), getString(R.string.try_again));
                    }

                    if (!Util.isNullOrEmpty(path)) {
                        uploadPrimaryPicture(path);
                    } else Methods.showSnackBar(getActivity(), getString(R.string.select_image_upload));
                } else if (resultCode == getActivity().RESULT_OK && (GALLERY_PHOTO == requestCode)) {
                    {
                        Uri picUri = data.getData();
                        if (picUri != null) {
                            path = getPath(picUri);
                            path = Util.saveBitmap(path, getActivity(), "ImageFloat" + System.currentTimeMillis());

                            if (!Util.isNullOrEmpty(path)) {
                                uploadPrimaryPicture(path);
                            } else
                                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.select_image_upload), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String getRealPathFromURI(Uri contentUri) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        public String getPath(Uri uri) {
            try {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } catch (Exception e) {
            }
            return null;
        }

        public void uploadPrimaryPicture(String path) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            Constants.isImgUploaded = false;
            UploadPictureAsyncTask upa = new UploadPictureAsyncTask(getActivity(), path, true, session.getFPID());
            upa.execute();
        }


        @Override
        public void onResume() {
            super.onResume();
            if (Constants.IMAGEURIUPLOADED == false) {
                String iconUrl = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI);
                //int size = (int) Math.ceil(Math.sqrt(100 * 100));
                if (iconUrl.length() > 0 && !iconUrl.contains("http") && (iconUrl != "https://api.withfloats.com/FP/Actual/default.png")) {
                    String baseNameProfileImage = Constants.BASE_IMAGE_URL + iconUrl;
                    Picasso.with(getActivity())
                            .load(baseNameProfileImage)
                            .resize(200, 0)
                            .placeholder(R.drawable.business_edit_profile_icon)
                            .into(iconImage);
                    //Picasso.with(getActivity()).load(baseNameProfileImage).placeholder(R.drawable.business_edit_profile_icon).into(iconImage);
                } else {
                    if (iconUrl != null && iconUrl.length() > 0) {
                        Picasso.with(getActivity())
                                .load(iconUrl)
                                .resize(200, 0)
                                .placeholder(R.drawable.business_edit_profile_icon)
                                .into(iconImage);
                    } else {
                        Picasso.with(getActivity()).load(R.drawable.business_edit_profile_icon).into(iconImage);
                    }
                }
            }
            if (session.getISEnterprise().equals("true")) {
                profileLayout.setVisibility(View.GONE);
                imageGalleryLayout.setVisibility(View.GONE);
                chatLayout.setVisibility(View.GONE);
                siteMeter.setVisibility(View.GONE);
            }
            bubbleSwitch.setChecked(session.isBoostBubbleEnabled());

            // mDrawerLayout.openDrawer(Gravity.LEFT);
        }

    /*public void siteMeterCalculation(){

        siteMeterTotalWeight = 0;

        if(HomeActivity.StorebizFloats.size()<5 && fiveUpdatesDone == false)
        {
            siteMeterTotalWeight+=onUpdate;
        }else{
            fiveUpdatesDone = true;
            siteMeterTotalWeight+=25;
        }

        if(!Util.isNullOrEmpty(Constants.StoreName)  ){
            siteMeterTotalWeight+=businessNameWeight;
              }

        if(Util.isNullOrEmpty(Constants.FACEBOOK_USER_ID))
        {
            siteMeterTotalWeight += twitterWeight;
        }

        if(!Util.isNullOrEmpty(Constants.StoreDescription)){
            siteMeterTotalWeight+=businessDescriptionWeight;
               }
        if(!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))){
            siteMeterTotalWeight+=businessCategoryWeight;
              }
        if(Constants.StoreContact != null){
            siteMeterTotalWeight+=phoneWeight;
             }
        if(!Util.isNullOrEmpty(Constants.StoreEmail)){
            siteMeterTotalWeight+=emailWeight;
             }
        if(!Util.isNullOrEmpty(Constants.StoreAddress)){
            siteMeterTotalWeight+=businessAddressWeight;}

        if(!Util.isNullOrEmpty(Constants.storePrimaryImage)){
            siteMeterTotalWeight+=featuredImageWeight;
        }

        if(Constants.websiteShared == true){
            siteMeterTotalWeight += 5;

        }

        progressbar.setProgress(siteMeterTotalWeight);
        meterValue.setText(siteMeterTotalWeight+"%");
    }*/
        public void siteMeterCalculation() {
            Constants.fbShareEnabled = pref.getBoolean("fbShareEnabled", false);
            Constants.fbPageShareEnabled = pref.getBoolean("fbPageShareEnabled", false);
            Constants.twitterShareEnabled = mSharedPreferences.getBoolean(TwitterConnection.PREF_KEY_TWITTER_LOGIN, false);
            siteMeterTotalWeight = 0;
            if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI))) {
                siteMeterTotalWeight += 10;
            }
            if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER)) && !getResources().getString(R.string.phoneNumber_percentage).equals("0")) {
                siteMeterTotalWeight += phoneWeight;
            }

            if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY)) && !getResources().getString(R.string.businessCategory_percentage).equals("0")) {
                siteMeterTotalWeight += businessCategoryWeight;
            }

            if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI)) && !getResources().getString(R.string.featuredImage_percentage).equals("0")) {
                siteMeterTotalWeight += featuredImageWeight;
            }

            if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)) && !getResources().getString(R.string.businessName_percentage).equals("0")) {
                siteMeterTotalWeight += businessNameWeight;
            }

            if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION)) && !getResources().getString(R.string.businessdescription_percentage).equals("0")) {
                siteMeterTotalWeight += businessDescriptionWeight;
            }

            if (Constants.twitterShareEnabled && pref.getBoolean("fbShareEnabled", false) && pref.getBoolean("fbPageShareEnabled", false)) {
                siteMeterTotalWeight += twitterWeight;
            }
            if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)) &&
                    !Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LATITUDE))&&
                    !Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LONGITUDE))&&
                    !getResources().getString(R.string.address_percentage).equals("0")) {
                siteMeterTotalWeight += businessAddressWeight;
            }

            if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL)) && !getResources().getString(R.string.email_percentage).equals("0")) {
                siteMeterTotalWeight += emailWeight;
            }
            if (HomeActivity.StorebizFloats.size() < 5 ) {
                siteMeterTotalWeight += (HomeActivity.StorebizFloats.size()*onUpdate);

            }else {
                siteMeterTotalWeight += 20;
            }
            if (!(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl))) && !getResources().getString(R.string.Logo_percentage).equals("0")) {
                siteMeterTotalWeight += logoWeight;
            }

            if (session.getBusinessHours()) {
                siteMeterTotalWeight += businessTimingWeight;
            }

            progressbar.setProgress(siteMeterTotalWeight);
            meterValue.setText(siteMeterTotalWeight + "%");
        }

    private void onclickColorChange(ImageView img, TextView tv, LinearLayout llPaletes) {
        dashBoardTextView.setTextColor(getResources().getColor(R.color.cell_text_color));
        businessProfileTextView.setTextColor(getResources().getColor(R.color.cell_text_color));
        //customerQueries.setTextColor(getResources().getColor(R.color.cell_text_color));
        imageGalleryTextView.setTextColor(getResources().getColor(R.color.cell_text_color));
        StoreTextView.setTextColor(getResources().getColor(R.color.cell_text_color));
        cspTextView.setTextColor(getResources().getColor(R.color.cell_text_color));
        productGalleryTextView.setTextColor(getResources().getColor(R.color.cell_text_color));
        settingsText.setTextColor(getResources().getColor(R.color.cell_text_color));
        chatText.setTextColor(getResources().getColor(R.color.cell_text_color));
        callText.setTextColor(getResources().getColor(R.color.cell_text_color));
        shareText.setTextColor(getResources().getColor(R.color.cell_text_color));
        tvBoostBubble.setTextColor(getResources().getColor(R.color.cell_text_color));
        businessAppTextview.setTextColor(getResources().getColor(R.color.cell_text_color));
        //tvSiteAppearance.setTextColor(getResources().getColor(R.color.cell_text_color));

            shareImageView.setColorFilter(defaultLabelFilter);
            dasbBoardImageView.setColorFilter(defaultLabelFilter);
            businessappImageView.setColorFilter(defaultLabelFilter);
            businessProfileImageView.setColorFilter(defaultLabelFilter);
            //customerQueriesImageView.setColorFilter(defaultLabelFilter);
            imageGalleryImageView.setColorFilter(defaultLabelFilter);
            productGalleryImageView.setColorFilter(defaultLabelFilter);
            StoreImageView.setColorFilter(defaultLabelFilter);
            cspImageView.setColorFilter(defaultLabelFilter);
            settingsImageView.setColorFilter(defaultLabelFilter);
            callImageView.setColorFilter(defaultLabelFilter);
            //ivSiteAppearance.setColorFilter(defaultLabelFilter);


            homeLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            profileLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            //analyticsLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            //storeLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            //customerQueriesLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            imageGalleryLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            businessAppsLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            cspLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            productGalleryLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            Store_Layout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            settingsLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            chatLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            callLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            shareLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
            llGetInTouch.setBackgroundColor(getResources().getColor(R.color.cell_background_color));


            if (tv != null){
                tv.setTextColor(getResources().getColor(R.color.black));
            }
            if (img != null) {
                img.setColorFilter(whiteLabelFilter);
            }
            if(llPaletes!=null){
                llPaletes.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
            }
        }

    }