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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.BusinessProfile.UI.API.UploadPictureAsyncTask;
import com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.DeleteBackgroundImageAsyncTask;
import com.nowfloats.on_boarding.OnBoardingApiCalls;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.twitter.TwitterConnection;
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
    private static int logoWeight = 5;
    private boolean mUserLearnedDrawer;
    private View containerView;
    public ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean mFromSavedInstanceState;
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private Activity mainActivity;
    TextView dashBoardTextView, analyticsTextView, tvManageCustomers, tvSocialSharing, tvManageInventory, tvManageContent,
            accountSettingsText, upgradeText, addOnText, keyboardTextView, helpAndSupportText, shareText, aboutText;
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
    Uri imageUri;
    private static final int GALLERY_PHOTO = 2;
    private static final int CAMERA_PHOTO = 1;

    LinearLayout homeLayout, analyticsLayout, upgradeLayout, addOnLayout, accountSettingsLayout, keyboardLayout, aboutLayout, helpAndSupportLayout, shareLayout,
            manageContentLayout, manageCustomersLayout, socialLayout, manageInventoryLayout;
    private RelativeLayout siteMeter;
    private int siteMeterTotalWeight;
    private ProgressBar progressbar;
    private TextView meterValue;
    private boolean fiveUpdatesDone = false;
    private int onUpdate = 4;

    private final int media_req_id = 5;
    private final int gallery_req_id = 6;

    private static HashMap<String, Integer> backgroundImages = new HashMap<String, Integer>();
    private ImageView shareImageView, keyboardImageView, upgradeImageView, addOnImageView, analyticsImageView, dasbBoardImageView, helpAndSupportImageView,
            accountSettingsImageView, manageCustomerImageView, manageContentImageView, aboutImageView,
            socialImageView, manageInventoryImageView;
    private PorterDuffColorFilter defaultLabelFilter, whiteLabelFilter;
    private SharedPreferences pref, mSharedPreferences;


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
        mSharedPreferences = getActivity().getSharedPreferences(TwitterConnection.PREF_NAME, Context.MODE_PRIVATE);

        progressbar = (ProgressBar) view.findViewById(R.id.ProgressBar);
        meterValue = (TextView) view.findViewById(R.id.fragment_side_panel_progress_meter_value);
        containerImage = (ImageView) view.findViewById(R.id.backgroundImage);
        siteMeterCalculation();
        try {
            String versionName = mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0).versionName;
            TextView versionCode = (TextView) view.findViewById(R.id.version_name_text);
            versionCode.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Typeface robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");

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
            if (baseNameProfileImage.length() > 0) {
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
        String normalURI = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase() + getActivity().getResources().getString(R.string.tag_for_partners);
        if (rootAlisasURI != null && !rootAlisasURI.equals("null") && rootAlisasURI.trim().length() > 0) {
            fpNameTextView.setText(Methods.fromHtml("<u>" + rootAlisasURI + "</u>"));
        } else {
            fpNameTextView.setText(Methods.fromHtml("<u>" + normalURI + "</u>"));
        }
        fpNameTextView.setTypeface(robotoMedium);
        fpNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = fpNameTextView.getText().toString().trim();

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

        homeLayout = card.findViewById(R.id.firstRow_Layout);
        analyticsLayout = card.findViewById(R.id.analytics_row_Layout);
        manageCustomersLayout = card.findViewById(R.id.tenth_Layout);
        manageContentLayout = card.findViewById(R.id.layout_manage_content);
        manageInventoryLayout = card.findViewById(R.id.twelveth_Layout);
        socialLayout = card.findViewById(R.id.eleventh_Layout);
        keyboardLayout = (LinearLayout) card.findViewById(R.id.keyboard_layout);
        accountSettingsLayout = card.findViewById(R.id.fifthRow_Layout);
        upgradeLayout = card.findViewById(R.id.secondRow_Layout);
        addOnLayout = card.findViewById(R.id.thirdRow_Layout);
        helpAndSupportLayout = card.findViewById(R.id.seventhRow_Layout);
        aboutLayout = card.findViewById(R.id.layout_about);
        shareLayout = (LinearLayout) card.findViewById(R.id.eigthRow_Layout);


        if (session.getISEnterprise().equals("true")) {
            upgradeLayout.setVisibility(View.GONE);
            addOnLayout.setVisibility(View.GONE);
            siteMeter.setVisibility(View.GONE);
            helpAndSupportLayout.setVisibility(View.GONE);
            shareLayout.setVisibility(View.GONE);
        }

        dashBoardTextView = (TextView) homeLayout.findViewById(R.id.firstrow_TextView);
        analyticsTextView = (TextView) analyticsLayout.findViewById(R.id.analytics_row_TextView);
        tvManageCustomers = (TextView) manageCustomersLayout.findViewById(R.id.tvManageCustomers);
        tvManageContent = (TextView) manageContentLayout.findViewById(R.id.tvManageContent);
        tvManageInventory = (TextView) manageInventoryLayout.findViewById(R.id.tvManageInventory);
        tvSocialSharing = (TextView) socialLayout.findViewById(R.id.tvSocialSharing);
        accountSettingsText = (TextView) accountSettingsLayout.findViewById(R.id.fifthRow_TextView);
        upgradeText = (TextView) upgradeLayout.findViewById(R.id.secondRow_TextView);
        addOnText = (TextView) addOnLayout.findViewById(R.id.thirdRow_TextView);
        helpAndSupportText = (TextView) helpAndSupportLayout.findViewById(R.id.seventhRow_TextView);
        aboutText = (TextView) aboutLayout.findViewById(R.id.tv_about);
        keyboardTextView = (TextView) keyboardLayout.findViewById(R.id.keyboard_TextView);
        shareText = (TextView) shareLayout.findViewById(R.id.eighthRow_TextView);

        dasbBoardImageView = homeLayout.findViewById(R.id.firstrow_ImageView);
        analyticsImageView = analyticsLayout.findViewById(R.id.analytics_row_ImageView);
        manageCustomerImageView = manageCustomersLayout.findViewById(R.id.tenthRow_ImageView);
        manageContentImageView = manageContentLayout.findViewById(R.id.img_manage_content);
        manageInventoryImageView = manageInventoryLayout.findViewById(R.id.twelveth_ImageView);
        socialImageView = socialLayout.findViewById(R.id.eleventh_ImageView);
        accountSettingsImageView = accountSettingsLayout.findViewById(R.id.fifthRow_ImageView);
        upgradeImageView = upgradeLayout.findViewById(R.id.secondRow_ImageView);
        addOnImageView = addOnLayout.findViewById(R.id.thirdRow_ImageView);
        helpAndSupportImageView = helpAndSupportLayout.findViewById(R.id.seventhRow_ImageView);
        aboutImageView = aboutLayout.findViewById(R.id.img_about);
        shareImageView = shareLayout.findViewById(R.id.eigthRow_ImageView);
//        if (!Methods.isAccessibilitySettingsOn(getActivity())) {
////            if(session.isBoostBubbleEnabled()){
////                session.setBubbleTime(-1);
////                ((OnItemClickListener) mainActivity).onClick(getString(R.string.home));
////            }
//            session.setBubbleStatus(false);
//        }
//
//        bubbleSwitch.setChecked(session.isBoostBubbleEnabled());
//
//        bubbleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isChecked) {
//                    session.setBubbleStatus(isChecked);
//                } else {
//
//                    if ((android.os.Build.VERSION.SDK_INT >= 23 && getActivity() != null && !Settings.canDrawOverlays(getActivity()))
//                            || (!Methods.isAccessibilitySettingsOn(getActivity()))) {
//
////                        if (!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("1")) {
////                            showAlertMaterialDialog();
////                            bubbleSwitch.setChecked(false);
////                        } else {
//                        session.setBubbleTime(-1);
//                        ((OnItemClickListener) mainActivity).onClick(getString(R.string.home));
////                        }
//                    } else {
//                        session.setBubbleStatus(isChecked);
//                    }
//
//                }
//            }
//        });
        dasbBoardImageView = (ImageView) homeLayout.findViewById(R.id.firstrow_ImageView);
        keyboardImageView = (ImageView) keyboardLayout.findViewById(R.id.keyboard_ImageView);
        manageCustomerImageView = (ImageView) manageCustomersLayout.findViewById(R.id.tenthRow_ImageView);
        socialImageView = (ImageView) socialLayout.findViewById(R.id.eleventh_ImageView);
        manageInventoryImageView = (ImageView) manageInventoryLayout.findViewById(R.id.twelveth_ImageView);
        shareImageView = (ImageView) shareLayout.findViewById(R.id.eigthRow_ImageView);
        //ivSiteAppearance = (ImageView) llSiteAppearance.findViewById(R.id.iv_site_appearance);

        dashBoardTextView.setTypeface(robotoMedium);
        analyticsTextView.setTypeface(robotoMedium);
        tvManageCustomers.setTypeface(robotoMedium);
        tvManageContent.setTypeface(robotoMedium);
        tvManageInventory.setTypeface(robotoMedium);
        tvSocialSharing.setTypeface(robotoMedium);
        accountSettingsText.setTypeface(robotoMedium);
        upgradeText.setTypeface(robotoMedium);
        addOnText.setTypeface(robotoMedium);
        helpAndSupportText.setTypeface(robotoMedium);
        aboutText.setTypeface(robotoMedium);
        shareText.setTypeface(robotoMedium);

        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.home));
                onclickColorChange(dasbBoardImageView, dashBoardTextView, homeLayout);
            }
        });

        upgradeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.upgrades));
                onclickColorChange(upgradeImageView, upgradeText, upgradeLayout);
            }
        });
        addOnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.add_ons));
                onclickColorChange(addOnImageView, addOnText, addOnLayout);
            }
        });
        keyboardTextView.setTypeface(robotoMedium);
        keyboardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.keyboard));
                onclickColorChange(keyboardImageView, keyboardTextView, keyboardLayout);
            }
        });

        manageCustomersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickColorChange(manageCustomerImageView, tvManageCustomers, manageCustomersLayout);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.manage_customers));
            }
        });

        manageContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickColorChange(manageContentImageView, tvManageContent, manageContentLayout);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.manage_content));
            }
        });
        analyticsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickColorChange(analyticsImageView, analyticsTextView, analyticsLayout);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.deeplink_analytics));
            }
        });


        socialLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickColorChange(socialImageView, tvSocialSharing, socialLayout);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.title_activity_social__sharing_));
            }
        });

        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickColorChange(aboutImageView, aboutText, aboutLayout);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.about));
            }
        });


        manageInventoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickColorChange(manageInventoryImageView, tvManageInventory, manageInventoryLayout);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.manage_inventory));
            }
        });


        accountSettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickColorChange(accountSettingsImageView, accountSettingsText, accountSettingsLayout);
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.account_settings));
            }
        });

        helpAndSupportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.help_and_support));
                onclickColorChange(helpAndSupportImageView, helpAndSupportText, helpAndSupportLayout);
            }
        });

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnItemClickListener) mainActivity).onClick(getString(R.string.share));
                //onclickColorChange(shareImageView, shareText, shareLayout);
            }
        });


        view.findViewById(R.id.tv_write_to_ria_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMailDetailDilaog();
            }
        });

        onclickColorChange(dasbBoardImageView, dashBoardTextView, homeLayout);
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

    public static int getCategoryBackgroundImage(String category) {

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
                if (getActivity() != null)
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

                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == media_req_id) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();

            }
        } else if (requestCode == gallery_req_id) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();

            }

        }
    }


    public void cameraIntent() {
        try {
            // use standard intent to capture an image
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        media_req_id);
            } else {
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
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        gallery_req_id);
            } else {
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
            if (iconUrl.length() > 0 && !iconUrl.contains("http") && (iconUrl != Constants.NOW_FLOATS_API_URL + "/FP/Actual/default.png")) {
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
            upgradeLayout.setVisibility(View.GONE);
            addOnLayout.setVisibility(View.GONE);
            siteMeter.setVisibility(View.GONE);
        }
    }

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
                !Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LATITUDE)) &&
                !Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LONGITUDE)) &&
                !getResources().getString(R.string.address_percentage).equals("0")) {
            siteMeterTotalWeight += businessAddressWeight;
        }

        if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL)) && !getResources().getString(R.string.email_percentage).equals("0")) {
            siteMeterTotalWeight += emailWeight;
        }
        if (HomeActivity.StorebizFloats.size() < 5) {
            siteMeterTotalWeight += (HomeActivity.StorebizFloats.size() * onUpdate);

        } else {
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

        if (!session.getOnBoardingStatus() && session.getSiteHealth() != siteMeterTotalWeight) {
            session.setSiteHealth(siteMeterTotalWeight);
            OnBoardingApiCalls.updateData(session.getFpTag(), String.format("site_health:%s", siteMeterTotalWeight));
        }
    }

    private void onclickColorChange(ImageView img, TextView tv, LinearLayout llPaletes) {
        dashBoardTextView.setTextColor(getResources().getColor(R.color.cell_text_color));
        analyticsTextView.setTextColor(getResources().getColor(R.color.cell_text_color));
        keyboardTextView.setTextColor(getResources().getColor(R.color.cell_text_color));
        tvManageCustomers.setTextColor(getResources().getColor(R.color.cell_text_color));
        tvManageContent.setTextColor(getResources().getColor(R.color.cell_text_color));
        tvManageInventory.setTextColor(getResources().getColor(R.color.cell_text_color));
        tvSocialSharing.setTextColor(getResources().getColor(R.color.cell_text_color));
        accountSettingsText.setTextColor(getResources().getColor(R.color.cell_text_color));
        upgradeText.setTextColor(getResources().getColor(R.color.cell_text_color));
        addOnText.setTextColor(getResources().getColor(R.color.cell_text_color));
        helpAndSupportText.setTextColor(getResources().getColor(R.color.cell_text_color));
        aboutText.setTextColor(getResources().getColor(R.color.cell_text_color));
        shareText.setTextColor(getResources().getColor(R.color.cell_text_color));


        dasbBoardImageView.setColorFilter(defaultLabelFilter);
        analyticsImageView.setColorFilter(defaultLabelFilter);
        manageCustomerImageView.setColorFilter(defaultLabelFilter);
        keyboardImageView.setColorFilter(defaultLabelFilter);
        manageContentImageView.setColorFilter(defaultLabelFilter);
        manageInventoryImageView.setColorFilter(defaultLabelFilter);
        socialImageView.setColorFilter(defaultLabelFilter);
        accountSettingsImageView.setColorFilter(defaultLabelFilter);
        upgradeImageView.setColorFilter(defaultLabelFilter);
        addOnImageView.setColorFilter(defaultLabelFilter);
        helpAndSupportImageView.setColorFilter(defaultLabelFilter);
        aboutImageView.setColorFilter(defaultLabelFilter);
        shareImageView.setColorFilter(defaultLabelFilter);


        homeLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
        analyticsLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
        manageCustomersLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
        keyboardLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
        manageContentLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
        manageInventoryLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
        socialLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
        accountSettingsLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
        upgradeLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
        addOnLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
        helpAndSupportLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
        aboutLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));
        shareLayout.setBackgroundColor(getResources().getColor(R.color.cell_background_color));


        if (tv != null) {
            tv.setTextColor(getResources().getColor(R.color.black));
        }
        if (img != null) {
            img.setColorFilter(whiteLabelFilter);
        }
        if (llPaletes != null) {
            llPaletes.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
        }
    }

}