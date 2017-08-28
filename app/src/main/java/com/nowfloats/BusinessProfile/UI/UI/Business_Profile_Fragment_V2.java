package com.nowfloats.BusinessProfile.UI.UI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.Image_Gallery.ImageGalleryActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.Home_Fragment_Tab;
import com.nowfloats.NavigationDrawer.SidePanelFragment;
import com.nowfloats.SiteAppearance.SiteAppearanceActivity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class Business_Profile_Fragment_V2 extends Fragment {
    TextView businessAddressLayout, contactInformationLayout, businessHoursLayout, businessLogoLayout, socialSharingLayout,
            tvCustomPages, tvPhotoGallery, tvSiteAppearance;
    public static ImageView businessProfileImageView;
    public static TextView websiteTextView;
    public static TextView businessInfoTextView;
    public static TextView category;
    Typeface robotoLight;
    private SharedPreferences pref = null;
    UserSessionManager session;
    SharedPreferences.Editor prefsEditor;
    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_business_profile_v3, container, false);
        pref = activity.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
        session = new UserSessionManager(activity.getApplicationContext(), activity);
        return mainView;
    }

    @Override
    public void onViewCreated(final View mainView, Bundle savedInstanceState) {
        super.onViewCreated(mainView, savedInstanceState);
        if (!isAdded()) return;
        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources()
                .getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        final LinearLayout progressLayout = (LinearLayout) mainView.findViewById(R.id.progress_layout);
        final LinearLayout profileLayout = (LinearLayout) mainView.findViewById(R.id.business_profile_layout);
        profileLayout.setVisibility(View.INVISIBLE);
        progressLayout.setVisibility(View.VISIBLE);
        if (HomeActivity.shareButton != null) {
            HomeActivity.shareButton.setImageResource(R.drawable.share_with_apps);
            HomeActivity.shareButton.setColorFilter(whiteLabelFilter_pop_ip);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Typeface robotoMedium = Typeface.createFromAsset(activity.getAssets(), "Roboto-Medium.ttf");
                            robotoLight = Typeface.createFromAsset(activity.getAssets(), "Roboto-Light.ttf");
                            HomeActivity.shareButton.setVisibility(View.VISIBLE);
                            HomeActivity.shareButton.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    String url = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
                                    if (!Util.isNullOrEmpty(url)) {
                                        String eol = System.getProperty("line.separator");
                                        url = getResources().getString(R.string.visit_to_new_website)
                                                + eol + url.toLowerCase();
                                    } else {
                                        String eol = System.getProperty("line.separator");
                                        url = getResources().getString(R.string.visit_to_new_website)
                                                + eol + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                                                + activity.getResources().getString(R.string.tag_for_partners);
                                    }

                                    shareWebsite(url);
                                }
                            });

                            websiteTextView = (TextView) mainView.findViewById(R.id.websiteTitleTextView_ProfileV2);
                            websiteTextView.setTypeface(robotoMedium);
                            websiteTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));

                            businessProfileImageView = (ImageView) mainView.findViewById(R.id.businessProfileIcon_ProfileV2);
                            //if (Constants.IMAGEURIUPLOADED == false) {
                            String iconUrl = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI);
                            BoostLog.d("TAG", iconUrl);
                            if (iconUrl.length() > 0 && iconUrl.contains("BizImages") && !iconUrl.contains("http")) {
                                String baseNameProfileImage = Constants.BASE_IMAGE_URL + "" + iconUrl;
                                Picasso.with(activity)
                                        .load(baseNameProfileImage).placeholder(R.drawable.business_edit_profile_icon).into(businessProfileImageView);
                            } else {
                                if (iconUrl != null && iconUrl.length() > 0) {
                                    Picasso.with(activity)
                                            .load(iconUrl).placeholder(R.drawable.business_edit_profile_icon).into(businessProfileImageView);
                                } else {
                                    Picasso.with(activity).load(R.drawable.business_edit_profile_icon).into(businessProfileImageView);
                                }
                            }
                            //}

                            //session.getIsSignUpFromFacebook().contains("true")
                            if (session.getIsSignUpFromFacebook().contains("true") && !Util.isNullOrEmpty(session.getFacebookPageURL())) {
                                Picasso.with(activity)
                                        .load(session.getFacebookPageURL()).placeholder(R.drawable.business_edit_profile_icon)
                                        // optional
                                        .rotate(90)                     // optional
                                        .into(businessProfileImageView);
                            }

                            category = (TextView) mainView.findViewById(R.id.categoryTextView_ProfileV2);
                            category.setTypeface(robotoLight);
                            category.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY));

                            businessInfoTextView = (TextView) mainView.findViewById(R.id.businessInfoTextView_ProfileV2);
                            businessInfoTextView.setTypeface(robotoLight);
                            businessInfoTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION));

                            LinearLayout editProfileImageView = (LinearLayout) mainView.findViewById(R.id.editProfile);
                            editProfileImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent businessAddress = new Intent(activity, Edit_Profile_Activity.class);
                                    startActivity(businessAddress);
                                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            });


                            View businessProfileList = mainView.findViewById(R.id.businessProfile_List_ProfileV2);

                            businessAddressLayout = (TextView) businessProfileList.findViewById(R.id.businessAddress_Layout_ProfileV2);
                            //TextView businessAddressTextView = (TextView) businessAddressLayout.findViewById(R.id.firstrow_TextView_ProfileV2);
                            businessAddressLayout.setTypeface(robotoMedium);

                            contactInformationLayout = (TextView) businessProfileList.findViewById(R.id.contactInformation_Layout_ProfileV2);
                            //TextView contactInfoTextView = (TextView) contactInformationLayout.findViewById(R.id.secondrow_TextView_ProfileV2);
                            contactInformationLayout.setTypeface(robotoMedium);

                            businessHoursLayout = (TextView) businessProfileList.findViewById(R.id.businessHours_Layout_ProfileV2);
                            // TextView businessHoursText = (TextView) businessHoursLayout.findViewById(R.id.thirdrow_TextView_ProfileV2);
                            businessHoursLayout.setTypeface(robotoMedium);


                            ImageView businessHoursImageView = (ImageView) businessProfileList.findViewById(R.id.thirdrow_ImageView_ProfileV2);
                            LinearLayout businessHoursLinearLayout = (LinearLayout) businessProfileList.findViewById(R.id.businessHoursLinearLayout);

                            if (session.getIsThinksity().equals("true")) {
                                businessHoursLayout.setVisibility(View.GONE);
                                businessHoursLinearLayout.setVisibility(View.GONE);
                                businessHoursImageView.setVisibility(View.GONE);
                            }
                            ImageView lockWidgetImageView_BusinessEnq = (ImageView) businessProfileList.findViewById(R.id.lock_widget_business_hours);

                            if (!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS).equals("TIMINGS")) {
                                lockWidgetImageView_BusinessEnq.setVisibility(View.VISIBLE);
                            }
                            businessLogoLayout = (TextView) businessProfileList.findViewById(R.id.businessLogo_Layout_ProfileV2);
                            //TextView businessLogoText = (TextView) businessLogoLayout.findViewById(R.id.fourth_TextView_ProfileV2);
                            businessLogoLayout.setTypeface(robotoMedium);

                            socialSharingLayout = (TextView) businessProfileList.findViewById(R.id.socialSharing_Layout_ProfileV2);
                            //TextView socialSharingTextView = (TextView) socialSharingLayout.findViewById(R.id.fifth_TextView_ProfileV2);
                            socialSharingLayout.setTypeface(robotoMedium);

                            tvCustomPages = (TextView) businessProfileList.findViewById(R.id.tvCustomPages);
                            tvCustomPages.setTypeface(robotoMedium);

                            tvPhotoGallery = (TextView) businessProfileList.findViewById(R.id.tvPhotoGallery);
                            tvPhotoGallery.setTypeface(robotoMedium);

                            tvSiteAppearance = (TextView) businessProfileList.findViewById(R.id.tvSiteAppearance);
                            tvSiteAppearance.setTypeface(robotoMedium);


                            businessAddressLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    MixPanelController.track(EventKeysWL.BUSINESS_ADDRESS, null);
                                    Intent businessAddress = new Intent(activity, Business_Address_Activity.class);
                                    startActivity(businessAddress);
                                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                }
                            });


                            tvPhotoGallery.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    MixPanelController.track(EventKeysWL.SIDE_PANEL_IMAGE_GALLERY, null);
                                    Intent businessAddress = new Intent(activity, ImageGalleryActivity.class);
                                    startActivity(businessAddress);
                                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                }
                            });


                            tvCustomPages.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

//                                    MixPanelController.track(EventKeysWL.PRODUCT_GALLERY, null);
                                    Intent businessAddress = new Intent(activity, CustomPageActivity.class);
                                    startActivity(businessAddress);
                                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                }
                            });


                            tvSiteAppearance.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    MixPanelController.track(EventKeysWL.SITE_APPEARANCE, null);
                                    Intent businessAddress = new Intent(activity, SiteAppearanceActivity.class);
                                    startActivity(businessAddress);
                                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                }
                            });

                            contactInformationLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MixPanelController.track(EventKeysWL.CONTACT_INFO, null);
                                    Intent contactInfo = new Intent(activity, Contact_Info_Activity.class);
                                    startActivity(contactInfo);
                                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            });

                            businessHoursLinearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MixPanelController.track(MixPanelController.Bhours, null);
                                    if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS).equals("TIMINGS")) {
                                        Intent businessHoursIntent = new Intent(activity, Business_Hours_Activity.class);
                                        startActivity(businessHoursIntent);
                                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    } else {
                                        new MaterialDialog.Builder(activity)
                                                .title(getResources().getString(R.string.features_not_available))
                                                .content(getResources().getString(R.string.check_store_for_upgrade_info))
                                                .positiveText(getResources().getString(R.string.goto_store))
                                                .negativeText(getResources().getString(R.string.cancel))
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
//                                                    Constants.showStoreScreen = true ;
                                                        Home_Fragment_Tab.viewPager = null;
                                                        dialog.dismiss();
//                                                    activity.getSupportFragmentManager().popBackStack();
                                                        ((SidePanelFragment.OnItemClickListener) activity).onClick(getResources().getString(R.string.store));
                                                    }
                                                }).show();
                                    }
                                }
                            });

                            businessLogoLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MixPanelController.track(EventKeysWL.LOGO, null);
                                    Intent businessLogoIntent = new Intent(activity, Business_Logo_Activity.class);
                                    startActivity(businessLogoIntent);
                                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            });

                            socialSharingLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    /*if (Constants.PACKAGE_NAME.equals("com.digitalseoz")) {
                                        Toast.makeText(activity, "This feature is coming soon", Toast.LENGTH_LONG).show();
                                    }else {*/
                                    MixPanelController.track(EventKeysWL.SOCIAL_SHARING, null);
                                    Intent socialSharingIntent = new Intent(activity, Social_Sharing_Activity.class);
                                    startActivity(socialSharingIntent);
                                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    //}
                                }
                                // }
                            });
                            progressLayout.setVisibility(View.GONE);
                            profileLayout.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

        BoostLog.d("Test", "OnCeate View is called");
    }


    private int getCategoryBackgroundImage(String category) {
//        backgroundImages.put("GENERALSERVICES", R.drawable.general_services_background_img);
/*
        if(category.contains("GENERALSERVICES"))
        {
            return R.drawable.general_services_background_img;
        }
//        backgroundImages.put("ARCHITECTURE",R.drawable.architecture_background_img);
        if(category.contains("ARCHITECTURE"))
        {
            return R.drawable.architecture_background_img;
        }
//        backgroundImages.put("AUTOMOTIVE",R.drawable.automotive_background_img);
        if(category.contains("AUTOMOTIVE"))
        {
            return R.drawable.automotive_background_img;
        }
//        backgroundImages.put("BLOGS",R.drawable.blogs_background_img);
        if(category.contains("BLOGS"))
        {
            return R.drawable.blogs_background_img;
        }
//        backgroundImages.put("EDUCATION",R.drawable.education_background_img);
        if(category.contains("EDUCATION"))
        {
            return R.drawable.education_background_img;
        }
//        backgroundImages.put("ELECTRONICS", R.drawable.electronics_background_img);
        if(category.contains("ELECTRONICS"))
        {
            return R.drawable.electronics_background_img;
        }
//        backgroundImages.put("ENTERTAINMENT",R.drawable.entertainment_background_img);
        if(category.contains("ENTERTAINMENT"))
        {
            return R.drawable.entertainment_background_img;
        }
//        backgroundImages.put("EVENTS",R.drawable.events_background_img);
        if(category.contains("EVENTS"))
        {
            return R.drawable.events_background_img;
        }
//        backgroundImages.put("F&B-BAKERY",R.drawable.fb_bakery_background_img);
        if(category.contains("F&B-BAKERY"))
        {
            return R.drawable.fb_bakery_background_img;
        }
//        backgroundImages.put("F&B-BARS",R.drawable.fb_bars_background_img);
        if(category.contains("F&B-BARS"))
        {
            return R.drawable.fb_bars_background_img;
        }

//        backgroundImages.put("F&B-CAFE",R.drawable.fb_cafe_background_img);
        if(category.contains("F&B-CAFE"))
        {
            return R.drawable.fb_cafe_background_img;
        }
//        backgroundImages.put("F&B-RESTAURANTS", R.drawable.fb_restaurants_background_img);
        if(category.contains("F&B-RESTAURANTS"))
        {
            return R.drawable.fb_restaurants_background_img;
        }
//        backgroundImages.put("FASHION-APPAREL",R.drawable.fashion_apparel_background_img);
        if(category.contains("FASHION-APPAREL"))
        {
            return R.drawable.fashion_apparel_background_img;
        }
//        backgroundImages.put("FASHION-FOOTWEAR",R.drawable.fashion_footwear_background_img);
        if(category.contains("FASHION-FOOTWEAR"))
        {
            return R.drawable.fashion_footwear_background_img;
        }
//        backgroundImages.put("FLOWERSSHOP",R.drawable.flower_shop_background_img);
        if(category.contains("FLOWERSSHOP"))
        {
            return R.drawable.flower_shop_background_img;
        }
//        backgroundImages.put("FURNITURE",R.drawable.furniture_background_img);
        if(category.contains("FURNITURE"))
        {
            return R.drawable.furniture_background_img;
        }
//        backgroundImages.put("GIFTS&NOVELTIES", R.drawable.gifts_novelties_background_img);
        if(category.contains("GIFTS&NOVELTIES"))
        {
            return R.drawable.gifts_novelties_background_img;
        }
//        backgroundImages.put("HEALTH&FITNESS",R.drawable.health_fitness_background_img);
        if(category.contains("HEALTH&FITNESS"))
        {
            return R.drawable.health_fitness_background_img;
        }
//        backgroundImages.put("HOMEAPPLICANCES",R.drawable.home_appliances_background_img);
        if(category.contains("HOMEAPPLICANCES"))
        {
            return R.drawable.home_appliances_background_img;
        }
//        backgroundImages.put("HOMECARE",R.drawable.home_care_background_img);
        if(category.contains("HOMECARE"))
        {
            return R.drawable.home_care_background_img;
        }
//        backgroundImages.put("HOMEMAINTENANCE",R.drawable.home_maintenance_background_img);
        if(category.contains("HOMEMAINTENANCE"))
        {
            return R.drawable.home_maintenance_background_img;
        }
//        backgroundImages.put("HOTEL&MOTELS",R.drawable.hotel_motels_background_img);
        if(category.contains("HOTEL&MOTELS"))
        {
            return R.drawable.hotel_motels_background_img;
        }
//        backgroundImages.put("INTERIORDESIGN",R.drawable.interior_design_background_img);
        if(category.contains("INTERIORDESIGN"))
        {
            return R.drawable.interior_design_background_img;
        }
//        backgroundImages.put("MEDICAL-DENTAL",R.drawable.medical_dental_background_img);
        if(category.contains("MEDICAL-DENTAL"))
        {
            return R.drawable.medical_dental_background_img;
        }
//        backgroundImages.put("MEDICAL-GENERAL",R.drawable.medical_general_background_img);
        if(category.contains("MEDICAL-GENERAL"))
        {
            return R.drawable.medical_general_background_img;
        }
//        backgroundImages.put("NATAURAL&AYURVEDA",R.drawable.natural_ayurveda_background_img);
        if(category.contains("NATAURAL&AYURVEDA"))
        {
            return R.drawable.natural_ayurveda_background_img;
        }
//        backgroundImages.put("KINDERGARTEN",R.drawable.kinder_garten_background_img);
        if(category.contains("KINDERGARTEN"))
        {
            return R.drawable.kinder_garten_background_img;
        }
//        backgroundImages.put("PETS",R.drawable.pets_background_img);
        if(category.contains("PETS"))
        {
            return R.drawable.pets_background_img;
        }
//        backgroundImages.put("PHOTOGRAPHY", R.drawable.photography_background_img);
        if(category.contains("PHOTOGRAPHY"))
        {
            return R.drawable.photography_background_img;
        }
//        backgroundImages.put("REALESTATE&CONSTRUCTION",R.drawable.real_estate_construction_background_img);
        if(category.contains("REALESTATE&CONSTRUCTION"))
        {
            return R.drawable.real_estate_construction_background_img;
        }
//        backgroundImages.put("SPA",R.drawable.spa_background_img);
        if(category.contains("SPA"))
        {
            return R.drawable.spa_background_img;
        }
//        backgroundImages.put("SPORTS",R.drawable.sports_background_img);
        if(category.contains("SPORTS"))
        {
            return R.drawable.sports_background_img;
        }
//        backgroundImages.put("TOURISM",R.drawable.tourism_background_img);
        if(category.contains("TOURISM"))
        {
            return R.drawable.tourism_background_img;
        }
//        backgroundImages.put("WATCHES&JEWELRY",R.drawable.watches_jewelry_background_img);
        if(category.contains("WATCHES&JEWELRY"))
        {
            return R.drawable.watches_jewelry_background_img;
        }
//        backgroundImages.put("OTHERRETAIL", R.drawable.other_retail_background_img);
        if(category.contains("OTHERRETAIL"))
        {
            return R.drawable.other_retail_background_img;
        }
*/

        return R.drawable.general_services_background_img;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (HomeActivity.headerText != null)
            HomeActivity.headerText.setText(getResources().getString(R.string.business_profile_title));
        if (websiteTextView != null)
            websiteTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        if (businessInfoTextView != null)
            businessInfoTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION));

        if (session.getIsSignUpFromFacebook().contains("true")) {
            if (businessInfoTextView != null)
                businessInfoTextView.setText(session.getFacebookProfileDescription());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        HomeActivity.headerText.setText(Constants.StoreName);
    }


    public void shareWebsite(String text) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "Share with:"));
        prefsEditor.putBoolean("shareWebsite", true);
        prefsEditor.commit();
        session.setWebsiteshare(true);
    }
}
