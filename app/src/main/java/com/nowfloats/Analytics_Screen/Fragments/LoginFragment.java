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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class LoginFragment extends Fragment implements NfxRequestClient.NfxCallBackListener,FacebookHandler.FacebookCallbacks {
    private static final int PAGE_NO_FOUND = 404;
    private SharedPreferences pref = null;
    SharedPreferences.Editor prefsEditor;

    UserSessionManager session;

    private final int FBTYPE = 0;
    private final int FBPAGETYPE = 1;
    private final int FROM_FB_PAGE = 0;


    private ProgressDialog pd;
    private int mNewPosition = -1,status;
    private Context mContext;
    FacebookHandler facebookHandler;
    final List<String> readPermissions=Arrays.asList("email", "public_profile", "user_friends", "read_insights", "business_management", "pages_messaging");
    final List<String> publishPermissions = Arrays.asList("publish_actions", "publish_pages", "manage_pages");

    public static Fragment getInstance(int i){
        LoginFragment frag=new LoginFragment();
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
            message.setText("Your Facebook session has expired. Please login.");
        if(getActivity() instanceof FacebookChatActivity){
            message.setText("Please connect your Facebook page.");
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
                facebookHandler.getFacebookPermissions(LoginFragment.this,readPermissions,publishPermissions);
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd = ProgressDialog.show(getActivity(), "", "Wait While Subscribing...");
                }
            });
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
            if(pages == null || items == null) {
                // no pages found on something wrong
                onFBPageError();
                NfxRequestClient requestClient = new NfxRequestClient(LoginFragment.this)
                        .setmFpId(session.getFPID())
                        .setmType("facebookpage")
                        .setmCallType(PAGE_NO_FOUND)
                        .setmName("");
                requestClient.nfxNoPageFound();
                pd = ProgressDialog.show(mContext, "", getString(R.string.please_wait));
            }else if (items.size() > 0)
            {
                    //final String[] array = items.toArray(new String[items.size()]);
                new MaterialDialog.Builder(mContext)
                        .title(getString(R.string.select_page))
                        .items(items)
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
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                mNewPosition = dialog.getSelectedIndex();
                                if (mNewPosition == -1) {
                                    Toast.makeText(mContext, "Please select any Facebook page", Toast.LENGTH_SHORT).show();
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
                                            showDialog("Alert", "You cannot select the same Facebook Page to share your updates. This will lead to an indefinite loop of updates on your website and Facebook Page.");
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
            pd = ProgressDialog.show(mContext, "", getString(R.string.wait_while_subscribing));

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
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        if (response.equals("error")) {
            Toast.makeText(mContext, "Something went wrong!!! Please try later.", Toast.LENGTH_SHORT).show();
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
                Constants.fbPageShareEnabled = true;
                prefsEditor.putBoolean("fbPageShareEnabled",true);
                prefsEditor.putInt("fbPageStatus",1);
                prefsEditor.apply();
               //call other fragment
                ((OpenNextScreen)mContext).onNextScreen();
                break;
            case PAGE_NO_FOUND:
                Methods.materialDialog(getActivity(), "Alert", getString(R.string.look_like_no_facebook_page));
                break;
            default:
                break;
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

            Methods.showSnackBarNegative(getActivity(),"Required permissions are not given, please connect again");
        }else if(!publishContain){
            Methods.showSnackBarNegative(getActivity(),"Required permissions are not given, please connect again");
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
