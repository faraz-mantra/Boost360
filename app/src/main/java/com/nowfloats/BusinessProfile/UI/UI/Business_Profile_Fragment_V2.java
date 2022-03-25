package com.nowfloats.BusinessProfile.UI.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.appservice.ui.business_logo.BusinessLogoActivityV2;
import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.CustomWidget.roboto_lt_24_212121;
import com.nowfloats.CustomWidget.roboto_md_60_212121;
import com.nowfloats.GMB.GMBHandler;
import com.nowfloats.Image_Gallery.ImageGalleryActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.DomainApiService;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.ImageMenuActivity;
import com.nowfloats.NavigationDrawer.SidePanelFragment;
import com.nowfloats.NavigationDrawer.model.DomainDetails;
import com.nowfloats.NavigationDrawer.model.EmailBookingModel;
import com.nowfloats.SiteAppearance.SiteAppearanceActivity;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.AccrossVerticals.domain.DomainDetailsActivity;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Model;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.thinksity.BuildConfig;
import com.thinksity.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import static com.nowfloats.util.Key_Preferences.GET_FP_DETAILS_CATEGORY;

/**
 * A simple {@link Fragment} subclass.
 */
public class Business_Profile_Fragment_V2 extends Fragment implements DomainApiService.DomainCallback {
    private final static int LIGHT_HOUSE_EXPIRED = -1, DEMO = 0, DEMO_EXPIRED = -2;
    private static final String DOMAIN_SUCCESS_STATUS = "17";
    private static final String PAYMENT_STATE_SUCCESS = "1";
    private static final String ROOT_ALIAS_URI = "nowfloats";
    private static final String FP_WEB_WIDGET_DOMAIN = "DOMAINPURCHASE";
    public static ImageView businessProfileImageView;
    public static TextView websiteTextView;
    public static TextView businessInfoTextView;
    public static TextView category;
    TextView businessAddressLayout, contactInformationLayout, businessHoursLayout, businessLogoLayout, socialSharingLayout,
            tvCustomPages, tvPhotoGallery, tvSiteAppearance, tvDomainDetails;
    Typeface robotoLight;
    UserSessionManager session;
    SharedPreferences.Editor prefsEditor;
    private SharedPreferences pref = null;
    private Activity activity;
    private DomainApiService domainApiService;
    private GMBHandler GmbHandler;
    private ProgressDialog progressDialog;
    private Get_FP_Details_Model get_fp_details_model;
    private boolean isAlreadyCalled = false;
    private boolean isDomainDetailsAvali = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_business_profile_v3, container, false);
        pref = activity.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
        session = new UserSessionManager(activity.getApplicationContext(), activity);
        domainApiService = new DomainApiService(this);
        GmbHandler = new GMBHandler(getContext(), session);

        try {
            GmbHandler.sendDetailsToGMB(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mainView;
    }

    @Override
    public void onViewCreated(final View mainView, Bundle savedInstanceState) {
        super.onViewCreated(mainView, savedInstanceState);
        if (!isAdded()) return;
        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources()
                .getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        final LinearLayout progressLayout = mainView.findViewById(R.id.progress_layout);
        final LinearLayout profileLayout = mainView.findViewById(R.id.business_profile_layout);
        profileLayout.setVisibility(View.INVISIBLE);
        progressLayout.setVisibility(View.VISIBLE);
//        if (shareButton != null) {
//            shareButton.setImageResource(R.drawable.share_with_apps);
//            shareButton.setColorFilter(whiteLabelFilter_pop_ip);
//        }

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
                            websiteTextView = mainView.findViewById(R.id.websiteTitleTextView_ProfileV2);
                            websiteTextView.setTypeface(robotoMedium);
                            websiteTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
//                            if (shareButton != null) {
//                                shareButton.setVisibility(View.VISIBLE);
//                                shareButton.setOnClickListener(new View.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(View v) {
//                                        String url = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
//                                        if (!Util.isNullOrEmpty(url)) {
//                                            String eol = System.getProperty("line.separator");
//                                            url = getResources().getString(R.string.visit_to_new_website)
//                                                    + eol + url.toLowerCase();
//                                        } else {
//                                            String eol = System.getProperty("line.separator");
//                                            url = getResources().getString(R.string.visit_to_new_website)
//                                                    + eol + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
//                                                    + activity.getResources().getString(R.string.tag_for_partners);
//                                        }
//
//                                        shareWebsite(url);
//                                    }
//                                });
//                            }
                            businessProfileImageView = mainView.findViewById(R.id.businessProfileIcon_ProfileV2);
                            //if (Constants.IMAGEURIUPLOADED == false) {
                            String iconUrl = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI);
                            BoostLog.d("TAG", iconUrl);
                            if (iconUrl.length() > 0 && iconUrl.contains("BizImages") && !iconUrl.contains("http")) {
                                String baseNameProfileImage = Constants.BASE_IMAGE_URL + "" + iconUrl;
                                Picasso.get()
                                        .load(baseNameProfileImage).placeholder(R.drawable.business_edit_profile_icon).into(businessProfileImageView);
                            } else {
                                if (iconUrl != null && iconUrl.length() > 0) {
                                    Picasso.get()
                                            .load(iconUrl).placeholder(R.drawable.business_edit_profile_icon).into(businessProfileImageView);
                                } else {
                                    Picasso.get().load(R.drawable.business_edit_profile_icon).into(businessProfileImageView);
                                }
                            }
                            //}

                            //session.getIsSignUpFromFacebook().contains("true")
                            if (session.getIsSignUpFromFacebook().contains("true") && !Util.isNullOrEmpty(session.getFacebookPageURL())) {
                                Picasso.get()
                                        .load(session.getFacebookPageURL()).placeholder(R.drawable.business_edit_profile_icon)
                                        // optional
                                        .rotate(90)                     // optional
                                        .into(businessProfileImageView);
                            }

                            category = mainView.findViewById(R.id.categoryTextView_ProfileV2);
                            category.setTypeface(robotoLight);
                            category.setText(session.getFPDetails(GET_FP_DETAILS_CATEGORY));

                            businessInfoTextView = mainView.findViewById(R.id.businessInfoTextView_ProfileV2);
                            businessInfoTextView.setTypeface(robotoLight);
                            businessInfoTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION));

                            TextView editTextView = mainView.findViewById(R.id.tv_edit_profile);
//                            editTextView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent businessAddress = new Intent(activity, Edit_Profile_Activity.class);
//                                    startActivity(businessAddress);
//                                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                                }
//                            });
                            mainView.findViewById(R.id.cv_edit_profile).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent businessAddress = new Intent(activity, Edit_Profile_Activity.class);
                                    startActivity(businessAddress);
                                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            });

                            View businessProfileList = mainView.findViewById(R.id.businessProfile_List_ProfileV2);

                            businessAddressLayout = businessProfileList.findViewById(R.id.businessAddress_Layout_ProfileV2);
                            //TextView businessAddressTextView = (TextView) businessAddressLayout.findViewById(R.id.firstrow_TextView_ProfileV2);
                            //businessAddressLayout.setTypeface(robotoMedium);

                            contactInformationLayout = businessProfileList.findViewById(R.id.contactInformation_Layout_ProfileV2);
                            //TextView contactInfoTextView = (TextView) contactInformationLayout.findViewById(R.id.secondrow_TextView_ProfileV2);
                            //contactInformationLayout.setTypeface(robotoMedium);

                            businessHoursLayout = businessProfileList.findViewById(R.id.businessHours_Layout_ProfileV2);
                            // TextView businessHoursText = (TextView) businessHoursLayout.findViewById(R.id.thirdrow_TextView_ProfileV2);
                            //businessHoursLayout.setTypeface(robotoMedium);


                            ImageView businessHoursImageView = businessProfileList.findViewById(R.id.thirdrow_ImageView_ProfileV2);
                            LinearLayout businessHoursLinearLayout = businessProfileList.findViewById(R.id.businessHoursLinearLayout);

                            if (session.getIsThinksity().equals("true")) {
                                businessHoursLayout.setVisibility(View.GONE);
                                businessHoursLinearLayout.setVisibility(View.GONE);
                                businessHoursImageView.setVisibility(View.GONE);
                            }
                            ImageView lockWidgetImageView_BusinessEnq = businessProfileList.findViewById(R.id.lock_widget_business_hours);

                            if (!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS).equals("TIMINGS")) {
                                lockWidgetImageView_BusinessEnq.setVisibility(View.VISIBLE);
                            }
                            businessLogoLayout = businessProfileList.findViewById(R.id.businessLogo_Layout_ProfileV2);
                            //TextView businessLogoText = (TextView) businessLogoLayout.findViewById(R.id.fourth_TextView_ProfileV2);
                            //businessLogoLayout.setTypeface(robotoMedium);

                            socialSharingLayout = businessProfileList.findViewById(R.id.socialSharing_Layout_ProfileV2);
                            //TextView socialSharingTextView = (TextView) socialSharingLayout.findViewById(R.id.fifth_TextView_ProfileV2);
                            //socialSharingLayout.setTypeface(robotoMedium);

                            tvCustomPages = businessProfileList.findViewById(R.id.tvCustomPages);
                            tvCustomPages.setTypeface(robotoMedium);

                            tvPhotoGallery = businessProfileList.findViewById(R.id.tvPhotoGallery);
                            tvPhotoGallery.setTypeface(robotoMedium);

                            tvSiteAppearance = businessProfileList.findViewById(R.id.tvSiteAppearance);
                            tvDomainDetails = businessProfileList.findViewById(R.id.tvDomainDetails);
                            tvSiteAppearance.setTypeface(robotoMedium);
                            tvDomainDetails.setTypeface(robotoMedium);
                            if (TextUtils.isDigitsOnly(session.getWebTemplateType()) && Integer.parseInt(session.getWebTemplateType()) > 6) {
                                businessProfileList.findViewById(R.id.layout_site_appearance).setVisibility(View.GONE);
                                businessProfileList.findViewById(R.id.sixth_ImageView_ProfileV2).setVisibility(View.GONE);
                            }

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
                            tvDomainDetails.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    isAlreadyCalled = false;
                                    MixPanelController.track(EventKeysWL.SITE_SCORE_GET_YOUR_OWN_IDENTITY, null);
                                    if (!BuildConfig.APPLICATION_ID.equals("com.biz2.nowfloats")) {
                                        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                                                .title("Get A Domain")
                                                .customView(R.layout.dialog_link_layout, false)
                                                .positiveText(getString(R.string.ok))
                                                .positiveColorRes(R.color.primaryColor)
                                                .callback(new MaterialDialog.ButtonCallback() {
                                                    @Override
                                                    public void onPositive(MaterialDialog dialog) {
                                                        super.onPositive(dialog);
                                                    }

                                                });
                                        if (!activity.isFinishing()) {
                                            builder.show();
                                        }
                                    } else if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equalsIgnoreCase("0")) {
                                        showExpiryDialog(DEMO);
                                    } else if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equalsIgnoreCase("-1") &&
                                            session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL).equalsIgnoreCase("0")) {
                                        showExpiryDialog(DEMO_EXPIRED);
                                    } else if (Methods.isOnline(getActivity())) {
                                        showLoader(getString(R.string.please_wait));
                                        domainApiService.getDomainDetails(activity, session.getFpTag(), getDomainDetailsParam());
                                    } else {
                                        Methods.showSnackBarNegative(getActivity(), getString(R.string.noInternet));
                                    }
                                }
                            });

                            contactInformationLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MixPanelController.track(EventKeysWL.CONTACT_INFO, null);
                                    //Intent contactInfo = new Intent(activity, Contact_Info_Activity.class);
                                    Intent contactInfo = new Intent(activity, ContactInformationActivity.class);
                                    startActivity(contactInfo);
                                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            });

                            businessHoursLinearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MixPanelController.track(MixPanelController.Bhours, null);
                                    if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS).equals("TIMINGS")) {
                                        Intent businessHoursIntent = new Intent(activity, BusinessHoursActivity.class);
                                        startActivity(businessHoursIntent);
                                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    } else if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
                                        Methods.showFeatureNotAvailDialog(getContext());
                                    }
                                }
                            });

                            businessLogoLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MixPanelController.track(EventKeysWL.LOGO, null);
                                    Intent businessLogoIntent = new Intent(activity, BusinessLogoActivityV2.class);
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {

                            final ScrollView scrollView = mainView.findViewById(R.id.scrollView);
                            scrollView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressLayout.setVisibility(View.GONE);
                                    profileLayout.setVisibility(View.VISIBLE);
                                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            }, 500);

                        }
                    }
                });
            }
        }).start();

        BoostLog.d("Test", "OnCeate View is called");
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {

        super.onStop();
    }

    /*
     * Domain Details Param
     */
    private HashMap<String, String> getDomainDetailsParam() {
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("clientId", Constants.clientId);
        return offersParam;
    }

    private void showLoader(final String message) {

        if (getActivity() == null) return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setCanceledOnTouchOutside(false);
                }
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        });
    }

    private void hideLoader() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void getDomainDetails(DomainDetails domainDetails) {
//        domainDetails = null;
        hideLoader();

        if (!isAlreadyCalled) {
            if (domainDetails == null) {
                Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong));
            } else if (domainDetails.isFailed()) {
                showCustomDialog(getString(R.string.domain_booking_failed),
                        Methods.fromHtml(TextUtils.isEmpty(domainDetails.getErrorMessage()) ?
                                getString(R.string.drop_us_contact) : domainDetails.getErrorMessage()).toString(),
                        getString(R.string.ok), null, DialogFrom.DEFAULT);
            } else if (domainDetails.isPending()) {

                showCustomDialog(getString(R.string.domain_booking_process),
                        getString(R.string.domain_booking_process_message),
                        getString(R.string.ok), null, DialogFrom.DEFAULT);
            } else {
                showDomainDetails();
            }
        }
    }

    @Override
    public void emailBookingStatus(ArrayList<EmailBookingModel.EmailBookingStatus> bookingStatuses) {

    }

    @Override
    public void getEmailBookingList(ArrayList<String> ids, String error) {

    }

    @Override
    public void getDomainSupportedTypes(ArrayList<String> arrExtensions) {

    }

    @Override
    public void domainAvailabilityStatus(String domainName, String domainType, DomainApiService.DomainAPI domainAPI) {

    }

    @Override
    public void domainBookStatus(String response) {

    }

    private void showDomainDetails() {
        isAlreadyCalled = true;
        Intent domainIntent = new Intent(activity, DomainDetailsActivity.class);
        startActivity(domainIntent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private HashMap<String, String> getLinkDomainParam() {
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("authClientId", Constants.clientId);
        offersParam.put("fpTag", session.getFpTag());
        return offersParam;
    }

    private void showExpiryDialog(int showDialog) {

        String callUsButtonText, cancelButtonText, dialogTitle, dialogMessage;
        int dialogImage, dialogImageBgColor;

        switch (showDialog) {
            case LIGHT_HOUSE_EXPIRED:
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.renew_light_house_plan);
                dialogMessage = getString(R.string.light_house_plan_expired_some_features_visible);
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                break;
            case DEMO:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.buy_light_house_plan);
                dialogMessage = getString(R.string.buy_light_house);
                break;
            case DEMO_EXPIRED:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.buy_light_house_plan);
                dialogMessage = getString(R.string.demo_plan_expired);
                break;
            default:
                return;
        }

        MaterialDialog mExpireDailog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.pop_up_restrict_post_message, false)
                .backgroundColorRes(R.color.white)
                .positiveText(callUsButtonText)
                .negativeText(cancelButtonText)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(activity, NewPricingPlansActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .show();

        View view = mExpireDailog.getCustomView();

        roboto_md_60_212121 title = view.findViewById(R.id.textView1);
        title.setText(dialogTitle);

        ImageView expireImage = view.findViewById(R.id.img_warning);
        expireImage.setBackgroundColor(dialogImageBgColor);
        expireImage.setImageDrawable(ContextCompat.getDrawable(activity, dialogImage));

        roboto_lt_24_212121 message = view.findViewById(R.id.pop_up_create_message_body);
        message.setText(Methods.fromHtml(dialogMessage));
    }

    private void showCustomDialog(String title, String message, String postiveBtn, String negativeBtn,
                                  final DialogFrom dialogFrom) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title(title)
                .customView(R.layout.dialog_link_layout, false)
                .positiveText(postiveBtn)
                .negativeText(negativeBtn)
                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.primaryColor)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        switch (dialogFrom) {

                            case CONTACTS_AND_EMAIL_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) activity).
                                        onClick(getResources().getString(R.string.contact__info));
                                break;
                            case CATEGORY_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) activity).
                                        onClick(getResources().getString(R.string.basic_info));
                                break;
                            case ADDRESS_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) activity).
                                        onClick(getResources().getString(R.string.business__address));
                            case DEFAULT:

                                break;
                        }
                    }
                    /*
                    ((SidePanelFragment.OnItemClickListener) activity).
                onClick(getResources().getString(R.string.business_profile));
                     */

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);

                        switch (dialogFrom) {

                            case DOMAIN_AVAILABLE:
                                MixPanelController.track(MixPanelController.DOMAIN_SEARCH, null);
                                break;
                            case DEFAULT:

                                break;
                        }
                    }
                });

        final MaterialDialog materialDialog = builder.show();
        View maView = materialDialog.getCustomView();

        TextView tvMessage = maView.findViewById(R.id.toast_message_to_contact);
        tvMessage.setText(message);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof HomeActivity && HomeActivity.headerText != null)
            HomeActivity.headerText.setText(getResources().getString(R.string.manage_website));
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


    private enum DialogFrom {
        DOMAIN_AVAILABLE,
        CONTACTS_AND_EMAIL_REQUIRED,
        CATEGORY_REQUIRED,
        ADDRESS_REQUIRED,
        DEFAULT
    }
}
