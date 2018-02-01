package com.nowfloats.BusinessProfile.UI.UI;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.AccountDetails.AccountInfoActivity;
import com.nowfloats.BusinessProfile.UI.UI.FAQ.FAQMainAcivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.Mobile_Site_Activity;
import com.nowfloats.NavigationDrawer.SidePanelFragment;
import com.nowfloats.SiteAppearance.SiteAppearanceActivity;
import com.nowfloats.riachatsdk.ChatManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class Settings_Fragment extends Fragment {
    FrameLayout signOutLayout, changePaswordLayout, feedbackLayout, chatfeedback_Layout, likeusFacebookLayout, aboutUsLayout,
            rateUsLayout, faqLayout, accountLayout, flSiteAppearance, flFollowTwitter, flTermsOfUse, flPrivacyPolicy;
    private EditText old_pwd, new_pwd, confirm_pwd;
    Boolean confirmCheckerActive = false;
    private ImageView confirmChecker;
    String oldPass, newPass, confirmPass;
    UserSessionManager session;
    private String versionName;
    public Activity activity;
    //facebook page constant
    public static String FACEBOOK_PAGE_ID = "nowfloats";
    private Context mContext;
    //facebook page constant

    @Override
    public void onResume() {
        super.onResume();
        if (Constants.gotoStore) {
            Constants.gotoStore = false;
            ((SidePanelFragment.OnItemClickListener) activity).onClick("Store");
        }
        if (HomeActivity.headerText != null)
            HomeActivity.headerText.setText(getString(R.string.setting));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        session = new UserSessionManager(activity.getApplicationContext(), activity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountLayout = (FrameLayout) view.findViewById(R.id.account_info_Layout);
        signOutLayout = (FrameLayout) view.findViewById(R.id.logout_Layout);
        changePaswordLayout = (FrameLayout) view.findViewById(R.id.change_password_Layout);
        faqLayout = (FrameLayout) view.findViewById(R.id.faq_Layout);
        flSiteAppearance = (FrameLayout) view.findViewById(R.id.fl_site_appearance);
        flFollowTwitter = (FrameLayout) view.findViewById(R.id.follow_us_twitter_Layout);
        flTermsOfUse = (FrameLayout) view.findViewById(R.id.terms_of_use_Layout);
        flPrivacyPolicy = (FrameLayout) view.findViewById(R.id.privacy_policy_Layout);
        String Sdate = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON);
        if (Sdate.contains("/Date")) {
            Sdate = Sdate.replace("/Date(", "").replace(")/", "");
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
                        // settingsInterface = (Settings_Interface)  activity;
                        TextView versionCode = (TextView) view.findViewById(R.id.version_name_text);
                        versionCode.setText(versionName);
                    }
                });
            }
        }).start();

        accountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("AccountInfo", null);
                Intent intent = new Intent(activity, AccountInfoActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        flSiteAppearance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SiteAppearanceActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        signOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("Logout", null);
                logoutAlertDialog_Material();
            }
        });

        changePaswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("ChangePassword", null);
                changePassword();
            }
        });

        feedbackLayout = (FrameLayout) view.findViewById(R.id.feedback_Layout);
        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("NeedHelp", null);
//                Mobihelp.showFeedback(activity);
                String headerValue = getResources().getString(R.string.settings_feedback_link);     //"create@prostinnovation.com";

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", headerValue, null));
                activity.startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
            }
        });
        chatfeedback_Layout = (FrameLayout) view.findViewById(R.id.chatfeedback_Layout);
        chatfeedback_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("ChatFeedback", null);

                ChatManager.getInstance(getActivity()).startChat(ChatManager.ChatType.FEEDBACK);

            }
        });

        likeusFacebookLayout = (FrameLayout) view.findViewById(R.id.like_us_Layout);
        likeusFacebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Methods.likeUsFacebook(mContext, "");
            }
        });

        flFollowTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent twitterIntent = new Intent(Intent.ACTION_VIEW);
                //String facebookUrl = getFacebookPageURL(getActivity().getApplicationContext());
                try {
                    getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                    twitterIntent.setData(Uri.parse(Constants.TWITTER_ID_URL));
                } catch (PackageManager.NameNotFoundException e1) {
                    twitterIntent.setData(Uri.parse(Constants.TWITTER_URL));
                    e1.printStackTrace();
                }
                twitterIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(twitterIntent);
            }
        });

        flTermsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getResources().getString(R.string.settings_tou_url);
                ;//"http://prostinnovation.com/";
                Intent showWebSiteIntent = new Intent(activity, Mobile_Site_Activity.class);
                // showWebSiteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                showWebSiteIntent.putExtra("WEBSITE_NAME", url);
                activity.startActivity(showWebSiteIntent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        flPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getResources().getString(R.string.settings_privacy_url);
                ;//"http://prostinnovation.com/";
                Intent showWebSiteIntent = new Intent(activity, Mobile_Site_Activity.class);
                // showWebSiteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                showWebSiteIntent.putExtra("WEBSITE_NAME", url);
                activity.startActivity(showWebSiteIntent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        aboutUsLayout = (FrameLayout) view.findViewById(R.id.about_us_Layout);
        aboutUsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("AboutUs", null);
                String url = getResources().getString(R.string.settings_about_us_link);
                ;//"http://prostinnovation.com/";
                Intent showWebSiteIntent = new Intent(activity, Mobile_Site_Activity.class);
                // showWebSiteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                showWebSiteIntent.putExtra("WEBSITE_NAME", url);
                activity.startActivity(showWebSiteIntent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        rateUsLayout = (FrameLayout) view.findViewById(R.id.rate_us_Layout);
        rateUsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("RateOnPlayStore", null);
                // String url = "https://play.google.com/store/apps/details?id=com.thinksity&hl=en";
                /*String url = getResources().getString(R.string.settings_rate_us_link);
                Intent showWebSiteIntent = new Intent(activity, Mobile_Site_Activity.class);
                // showWebSiteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                showWebSiteIntent.putExtra("WEBSITE_NAME", url);
                activity.startActivity(showWebSiteIntent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                rateUsPlayStore();
            }
        });
        faqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FAQMainAcivity.class);
                i.putExtra("array", getResources().getStringArray(R.array.faqmain));
                activity.startActivity(i);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    public void logoutAlertDialog_Material() {

        new MaterialDialog.Builder(activity)
                .customView(R.layout.exit_dialog, true)
                .positiveText(getString(R.string.setting_logout))
                .negativeText(getString(R.string.cancel))
                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.light_gray)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                       // getActivity().stopService(new Intent(getActivity(), CustomerAssistantActivity.class));
                        session.logoutUser();

                        //Constants.IS_LOGIN = false;

                        //      SharedPreferences.Editor editor;
                        dialog.dismiss();
//                        Intent i = new Intent( activity, Login_MainActivity.class);
//                        // Closing all the Activities
//                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                        // Add new Flag to start new Activity
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//                        // Staring Login Activity
//                         activity.startActivity(i);
//
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void changePassword() {
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.change_password, true)
                .positiveText(getString(R.string.ok))
                .negativeText(getString(R.string.cancel))
                .positiveColorRes(R.color.primaryColor)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        oldPass = old_pwd.getText().toString().trim();
                        newPass = new_pwd.getText().toString().trim();
                        confirmPass = confirm_pwd.getText().toString().trim();
                        Boolean confirm = newPass.equals(confirmPass);

                        if (newPass.length() > 5) {
                            if (confirm) {
                                JSONObject obj = new JSONObject();
                                try {
                                    obj.put("clientId", Constants.clientId);
                                    obj.put("currentPassword", oldPass);
                                    obj.put("newPassword", newPass);
                                    obj.put("username", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (Util.isNetworkStatusAvialable(activity)) {
                                    changePasswordAsyncTask task = new changePasswordAsyncTask(activity, obj, activity);
                                    dialog.dismiss();
                                    task.execute();
                                } else {
                                    Methods.showSnackBarNegative(activity, getString(R.string.check_internet_connection));
                                }

                            } else {
                                Methods.showSnackBarNegative(activity, getString(R.string.both_password_not_matched));
                            }
                        } else {
                            Methods.showSnackBarNegative(activity, getString(R.string.min_6char_required));
                        }
                    }
                }).show();
        confirmDetails(dialog);
    }

    private void confirmDetails(MaterialDialog view) {

        old_pwd = (EditText) view.findViewById(R.id.change_pwd_old_pwd);
        new_pwd = (EditText) view.findViewById(R.id.change_pwd_new_pwd);
        confirm_pwd = (EditText) view.findViewById(R.id.change_pwd_confirm_pwd);
        confirmChecker = (ImageView) view.findViewById(R.id.confirm_pwd_status_img);
        confirm_pwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newPwd = new_pwd.getText().toString().trim();
                String confirmPwd = confirm_pwd.getText().toString().trim();
                confirmCheckerActive = true;
                if (confirmPwd.equals(newPwd)) {
                    confirmChecker.setVisibility(View.VISIBLE);
                    confirmChecker.setImageResource(R.drawable.domain_available);
                } else {
                    confirmChecker.setVisibility(View.VISIBLE);
                    confirmChecker.setImageResource(R.drawable.domain_not_available);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }
        });

        new_pwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (confirmCheckerActive) {
                    String newPwd = new_pwd.getText().toString().trim();
                    String confirmPwd = confirm_pwd.getText().toString().trim();
                    if (confirmPwd.equals(newPwd)) {
                        confirmChecker.setImageResource(R.drawable.domain_available);
                    } else {
                        confirmChecker.setImageResource(R.drawable.domain_not_available);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
        });
    }

    private void rateUsPlayStore() {
        Uri uri = Uri.parse("market://details?id=" + Constants.PACKAGE_NAME);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            String url = getResources().getString(R.string.settings_rate_us_link);
            Intent showWebSiteIntent = new Intent(activity, Mobile_Site_Activity.class);
            showWebSiteIntent.putExtra("WEBSITE_NAME", url);
            activity.startActivity(showWebSiteIntent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

}