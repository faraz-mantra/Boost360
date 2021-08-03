package com.nowfloats.Analytics_Screen.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NFXApi.NfxRequestClient;
import com.nowfloats.managecustomers.FacebookChatActivity;
import com.nowfloats.socialConnect.FacebookHandler;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DataBase;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Abhi on 11/25/2016.
 */

public class FacebookLoginFragment extends Fragment implements NfxRequestClient.NfxCallBackListener,FacebookHandler.FacebookCallbacks {
    private static final int PAGE_NO_FOUND = 404;
    private SharedPreferences pref = null;
    SharedPreferences.Editor prefsEditor;

    UserSessionManager session;
    private static final int FB_PAGE_CREATION = 101;
    private final int FBTYPE = 0;
    private final int FBPAGETYPE = 1;
    private final int FROM_FB_PAGE = 0;
    private String fpPageName;
    private final static String FB_PAGE_DEFAULT_LOGO = "https://s3.ap-south-1.amazonaws.com/nfx-content-cdn/logo.png";
    private final static String FB_PAGE_COVER_PHOTO = "https://cdn.nowfloats.com/fpbkgd-kitsune/abstract/24.jpg";
    private ProgressDialog progressDialog;
    private int mNewPosition = -1,status;
    private Context mContext;
    FacebookHandler facebookHandler;
    final List<String> readPermissions = Arrays.asList(Constants.FACEBOOK_READ_PERMISSIONS);
    final List<String> publishPermissions = Arrays.asList(Constants.FACEBOOK_PUBLISH_PERMISSIONS);

    public static Fragment getInstance(int i){
        FacebookLoginFragment frag=new FacebookLoginFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("status",i);
        frag.setArguments(bundle);
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_facebook_connect, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            status = getArguments().getInt("status");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView message= (TextView) view.findViewById(R.id.facebook_analytics_connect_text1);
        facebookHandler = new FacebookHandler(this,mContext);
        if(status == 2)
            message.setText(R.string.your_facebook_session_has_expired);
        if(getActivity() instanceof FacebookChatActivity){
            message.setText(R.string.please_connect_your_facebook_page);
        }
        Methods.isOnline(getActivity());
        session = new UserSessionManager(getContext(), getActivity());
        pref = mContext.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();

        Button button = (Button) view.findViewById(R.id.facebook_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // fbData(FROM_FB_PAGE);
                facebookHandler.getFacebookPermissions(FacebookLoginFragment.this,readPermissions,publishPermissions);
            }
        });
    }


    private void saveFbLoginResults(String userName, String accessToken, String id) {
        //String FACEBOOK_USER_NAME = Profile.getCurrentProfile().getName();
        Constants.FACEBOOK_USER_ACCESS_ID = accessToken;
        Constants.FACEBOOK_USER_ID = id;
        NfxRequestClient requestClient = new NfxRequestClient(this)
                .setmFpId(session.getFPID())
                .setmType("facebookusertimeline")
                .setmUserAccessTokenKey(accessToken)
                .setmUserAccessTokenSecret("null")
                .setmUserAccountId(id)
                .setmAppAccessTokenKey("")
                .setmAppAccessTokenSecret("")
                .setmCallType(FBTYPE)
                .setmName(userName);
        requestClient.connectNfx();
        if(isAdded()) {
            showLoader(getString(R.string.wait_while_subscribing));
        }

        //BoostLog.d("ggg", session.getFPID());

        session.storeFacebookName(userName);
        session.storeFacebookAccessToken(accessToken);
        DataBase dataBase = new DataBase(getActivity());
        dataBase.updateFacebookNameandToken(userName, accessToken);

        prefsEditor.putString("fbId", Constants.FACEBOOK_USER_ID);
        prefsEditor.putString("fbAccessId", Constants.FACEBOOK_USER_ACCESS_ID);
        prefsEditor.putString("fbUserName", userName);
        prefsEditor.commit();
    }

    void onFBPageError() {
        //Log.v("ggg","fbpage error");
        Constants.fbPageShareEnabled = false;
        prefsEditor.putBoolean("fbPageShareEnabled", false).apply();
        LoginManager.getInstance().logOut();
        com.facebook.AccessToken.refreshCurrentAccessTokenAsync();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, resultCode, data);
        facebookHandler.onActivityResult(requestCode, resultCode, data);

    }

    private void processGraphResponse(final JSONArray pages,final ArrayList<String> items) {
        //Log.v("ggg","progressgraph");

        try
        {
            if(pages == null || items == null || items.size() == 0) {
                // no pages found on something wrong
                onFBPageError();
                NfxRequestClient requestClient = new NfxRequestClient(FacebookLoginFragment.this)
                        .setmFpId(session.getFPID())
                        .setmType("facebookpage")
                        .setmCallType(PAGE_NO_FOUND)
                        .setmName("");
                requestClient.nfxNoPageFound();
               showLoader(getString(R.string.please_wait));
            }else if (items.size() > 0)
            {
                    //final String[] array = items.toArray(new String[items.size()]);
                new MaterialDialog.Builder(mContext)
                        .title(getString(R.string.select_page))
                        .items(items)
                        .cancelable(false)
                        .positiveText("Ok")
                        .autoDismiss(false)
                        .negativeText("Cancel")
                        .negativeColorRes(R.color.black_4a4a4a)
                        .positiveColorRes(R.color.colorAccent_jio)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int position, CharSequence text) {

                                //dialog.dismiss();
                                mNewPosition = position;
                                return true;
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                mNewPosition = dialog.getSelectedIndex();
                                if (mNewPosition == -1) {
                                    Toast.makeText(mContext, getString(R.string.please_select_any_facebook_page), Toast.LENGTH_SHORT).show();
                                } else {
                                    String strName = items.get(mNewPosition);
                                    String FACEBOOK_PAGE_ID = null;
                                    String page_access_token = null;
                                    try {
                                        FACEBOOK_PAGE_ID = (String) ((JSONObject) pages.get(mNewPosition)).get("id");
                                        page_access_token = ((String) ((JSONObject) pages.get(mNewPosition)).get("access_token"));
                                    } catch (JSONException e) {

                                    }
                                    if (!Util.isNullOrEmpty(FACEBOOK_PAGE_ID) && !Util.isNullOrEmpty(page_access_token)) {
                                        session.storePageAccessToken(page_access_token);
                                        session.storeFacebookPageID(FACEBOOK_PAGE_ID);
                                        if (Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.FB_PULL_PAGE_NAME))
                                                || !pref.getBoolean("FBFeedPullAutoPublish", false)
                                                || !strName.equals(session.getFPDetails(Key_Preferences.FB_PULL_PAGE_NAME)))
                                            pageSeleted(mNewPosition, strName, session.getFacebookPageID(), session.getPageAccessToken());
                                        else {
                                            showDialog("Alert", getString(R.string.you_cannot_select_the_same_facebook_page_to_share_your_update));
                                        }
                                        //pageSeleted(position, strName, session.getFacebookPageID(), session.getPageAccessToken());
                                    }
                                }
                                dialog.dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    private void showLoader(final String message) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(mContext);
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
    public void pageSeleted(int id, final String pageName, String pageID, String pageAccessToken) {
        //Log.v("ggg",pageName+"page");
        String s = "";
        JSONObject obj;
        session.storeFacebookPage(pageName);
        JSONArray data = new JSONArray();

        NfxRequestClient requestClient = new NfxRequestClient(this)
                .setmFpId(session.getFPID())
                .setmType("facebookpage")
                .setmUserAccessTokenKey(pageAccessToken)
                .setmUserAccessTokenSecret("null")
                .setmUserAccountId(pageID)
                .setmAppAccessTokenKey("")
                .setmAppAccessTokenSecret("")
                .setmCallType(FBPAGETYPE)
                .setmName(pageName);
        requestClient.connectNfx();
        if(isAdded())
            showLoader(getString(R.string.wait_while_subscribing));

        DataBase dataBase = new DataBase(getActivity());
        dataBase.updateFacebookPage(pageName, pageID, pageAccessToken);

        obj = new JSONObject();
        try {
            obj.put("id", pageID);
            obj.put("access_token", pageAccessToken);
            data.put(obj);

            Constants.fbPageFullUrl = "https://www.facebook.com/pages/" + pageName + "/" + pageID;
            Constants.fbFromWhichPage = pageName;
            prefsEditor.putString("fbPageFullUrl", Constants.fbPageFullUrl);
            prefsEditor.putString("fbFromWhichPage", Constants.fbFromWhichPage);
            prefsEditor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        obj = new JSONObject();
        try {
            obj.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String fbPageData = obj.toString();
        if (!Util.isNullOrEmpty(fbPageData)) {
            if (fbPageData.equals("{\"data\":[]}")) {
                prefsEditor.putString("fbPageData", "");
                Constants.fbPageShareEnabled = false;
                prefsEditor.putBoolean("fbPageShareEnabled", Constants.fbPageShareEnabled);
                prefsEditor.apply();
                //InitShareResources();

            } else {

                Constants.fbPageShareEnabled = true;
            }
        }
    }
    private void showDialog(String headText, String message) {
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(Methods.fromHtml(message));
        builder.setTitle(headText);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        textView.setTypeface(face);
        textView.setTextColor(Color.parseColor("#808080"));

    }

    @Override
    public void nfxCallBack(String response, int callType, String name) {
        hideLoader();
        if (response.equals("error")) {
            Toast.makeText(mContext, getString(R.string.something_went_wrong_please), Toast.LENGTH_SHORT).show();
            return;
        }
        BoostLog.d("ggg: ", response + callType + ":");
        switch (callType) {
            case FBTYPE:
                Constants.fbShareEnabled = true;
                prefsEditor = pref.edit();
                prefsEditor.putBoolean("fbShareEnabled", true);
                prefsEditor.putInt("fbStatus",1);
                prefsEditor.apply();
                MixPanelController.track(EventKeysWL.FACEBOOK_ANAYTICS,null);
                break;
            case FBPAGETYPE://not put in pref
                session.storeFacebookPage(name);
                Constants.fbPageShareEnabled = true;
                prefsEditor.putBoolean("fbPageShareEnabled",true);
                prefsEditor.putInt("fbPageStatus",1);
                prefsEditor.apply();
               //call other fragment
                ((OpenNextScreen)mContext).onNextScreen();
                break;
            case PAGE_NO_FOUND:
                MixPanelController.track(MixPanelController.FACEBOOK_PAGE_NOT_FOUND, null);
                if (!Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {
                    Methods.materialDialog(getActivity(), "Alert", getString(R.string.look_like_no_facebook_page));
                } else {
                    final String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);
                    final MaterialDialog builder = new MaterialDialog.Builder(mContext)
                            .customView(R.layout.dialog_no_facebook_page, false).build();
                    ((Button) builder.getCustomView().findViewById(R.id.create_page))
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    builder.dismiss();
                                    if ((!TextUtils.isEmpty(paymentState) && "1".equalsIgnoreCase(paymentState))) {
                                        createFBPage(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
                                    }else{
                                        Methods.materialDialog(getActivity(), "Alert",getString(R.string.this_feature_is_available_for_paid_customers));
                                    }
                                }
                            });
                    if (getActivity() != null && !getActivity().isFinishing())
                        builder.show();
                }
                break;
            case FB_PAGE_CREATION:

                switch (response) {
                    case "success_fbDefaultImage":
                        pageCreatedDialog(true);
                        MixPanelController.track(MixPanelController.FACEBOOK_PAGE_CREATED_WITH_DEFAULT_IMAGE, null);
                        break;
                    case "success_logoImage":
                        MixPanelController.track(MixPanelController.FACEBOOK_PAGE_CREATED_WITH_LOGO, null);
                        pageCreatedDialog(false);
                        break;
                    case "profile_incomplete":
                        MixPanelController.track(MixPanelController.FACEBOOK_PAGE_PROFILE_INCOMPLETE, null);
                        showDialog(getString(R.string.site_health_should_be_eighty_percent), getString(R.string.business_profile_incomplete));
                        break;
                    case "error_creating_page":
                        MixPanelController.track(MixPanelController.FACEBOOK_PAGE_ERROR_IN_CREATE, null);
                        Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong));
                        break;
                    case "invalid_name":
                        MixPanelController.track(MixPanelController.FACEBOOK_PAGE_INVALID_NAME, null);
                        pageSuggestionDialog();
                        break;
                    default:
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            default:
                break;
        }
    }
    private void createFBPage(String fpName) {
        MixPanelController.track(MixPanelController.CREATE_FACEBOOK_PAGE, null);
        fpPageName = fpName;
        String businessDesciption = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION);

        String businessCategory = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY);

        String mobileNumber = session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM);

        String fpURI = "";
        String rootAlisasURI = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
        String normalURI = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                + getResources().getString(R.string.tag_for_partners);
        if (rootAlisasURI != null && !rootAlisasURI.equals("null") && rootAlisasURI.trim().length() > 0) {
            fpURI = rootAlisasURI;
        } else {
            fpURI = normalURI;
        }

        showLoader(getString(R.string.please_wait));

        NfxRequestClient requestClient = new
                NfxRequestClient(this)
                .setmFpId(session.getFPID())
                .setmCallType(FB_PAGE_CREATION);

        requestClient.createFBPage(fpName, businessDesciption, businessCategory,
                mobileNumber, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI),
                fpURI, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY));

    }

    private void pageSuggestionDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fb_page_edit, null);
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .customView(view, false)
                .build();
        final EditText pageName = (EditText) view.findViewById(R.id.et_page_name);
        pageName.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME) + " " + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY));
        pageName.requestFocus();
        Button proceed = (Button) view.findViewById(R.id.btn_proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String page = pageName.getText().toString().trim();
                if (page.length() > 0) {
                    dialog.dismiss();
                    createFBPage(page);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.page_name_cant_be_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (!getActivity().isFinishing()) {
            dialog.show();
        }

    }

    private void pageCreatedDialog(boolean showDefaultImageMessage) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fb_page_created, null);
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .customView(view, false)
                .canceledOnTouchOutside(false)
                .build();
        Button connect = (Button) view.findViewById(R.id.btn_connect);
        TextView pageName = (TextView) view.findViewById(R.id.tv_fb_page_name);
        view.findViewById(R.id.llayout_message).setVisibility(showDefaultImageMessage ? View.VISIBLE : View.GONE);
        pageName.setText(fpPageName);
        ImageView logoImage = (ImageView) view.findViewById(R.id.img_logo);
        ImageView featureImage = (ImageView) view.findViewById(R.id.img_feature);
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Picasso.get()
                .load(FB_PAGE_COVER_PHOTO)
                .resize(0, 200)
                .placeholder(R.drawable.general_services_background_img)
                .into(featureImage);

        String logoURI;
        if (showDefaultImageMessage) {
            logoURI = FB_PAGE_DEFAULT_LOGO;
        } else {
            logoURI = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl);
            if (!logoURI.contains("http")) {
                logoURI = "https://" + logoURI;
            }
        }

        Picasso.get()
                .load(logoURI)
                .resize(0, 75)
                .placeholder(R.drawable.facebook_page2)
                .into(logoImage);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
              facebookHandler.getFacebookPermissions(FacebookLoginFragment.this,readPermissions,publishPermissions);
            }
        });

        if (!getActivity().isFinishing()) {
            dialog.show();
        }
    }

    @Override
    public void onError() {
        onFBPageError();
    }

    @Override
    public void onCancel() {
        onFBPageError();
    }

    @Override
    public void onAllPermissionNotGiven(Collection<String> givenPermissions) {
        if(!isAdded() || getActivity() == null) return;
        boolean readContain = givenPermissions.containsAll(publishPermissions);
        boolean publishContain = givenPermissions.containsAll(publishPermissions);
        if(!readContain){

            Methods.showSnackBarNegative(getActivity(),getString(R.string.required_permission_is_not_given_please_connect_again));
        }else if(!publishContain){
            Methods.showSnackBarNegative(getActivity(),getString(R.string.required_permission_is_not_given_please_connect_again));
        }
    }

    @Override
    public void onLoginSuccess(LoginResult loginResult) {

        if(Profile.getCurrentProfile()==null)
        {
            facebookHandler.getFacebookProfile(loginResult.getAccessToken());
        }else
        {
            //Log.v("ggg",Profile.getCurrentProfile().toString());
            saveFbLoginResults(Profile.getCurrentProfile().getName(),
                    loginResult.getAccessToken().getToken(),
                    Profile.getCurrentProfile().getId());
            facebookHandler.getFacebookPages(loginResult.getAccessToken());
        }
    }

    @Override
    public void onProfilePages(JSONArray pages,ArrayList<String> pagesNameList) {

        processGraphResponse(pages,pagesNameList);
    }

    @Override
    public void onProfileConnected(JSONObject profile, AccessToken accessToken) {

        try
        {
            saveFbLoginResults(profile.getString("name"), accessToken.getToken(), profile.getString("id"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public interface OpenNextScreen {
        void onNextScreen();
    }
}
