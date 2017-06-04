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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NFXApi.NfxRequestClient;
import com.nowfloats.Twitter.TwitterConstants;
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
import java.util.List;
import java.util.Set;

/**
 * Created by Abhi on 11/25/2016.
 */

public class LoginFragment extends Fragment implements NfxRequestClient.NfxCallBackListener {
    private static final int PAGE_NO_FOUND = 404;
    private SharedPreferences pref = null;
    SharedPreferences.Editor prefsEditor;

    int size = 0;
    boolean[] checkedPages;
    UserSessionManager session;

    private SharedPreferences mSharedPreferences = null;
    private final int FBTYPE = 0;
    private final int FBPAGETYPE = 1;
    private final int FROM_FB_PAGE = 0;


    private CallbackManager callbackManager;
    private ArrayList<String> items;
    private ProgressDialog pd;
    private int mNewPosition = -1,status;
    private Context mContext;

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
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
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

        if(status == 2)
            message.setText("Your Facebook session has expired. Please login.");

        Methods.isOnline(getActivity());
        session = new UserSessionManager(getContext(), getActivity());
        pref = mContext.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
        mSharedPreferences = mContext.getSharedPreferences(TwitterConstants.PREF_NAME, Context.MODE_PRIVATE);

        Button button = (Button) view.findViewById(R.id.facebook_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbData();
            }
        });
    }

    public void fbData() {
        List<String> readPermissions = Arrays.asList("email", "public_profile", "user_friends", "read_insights", "business_management");
        final List<String> publishPermissions = Arrays.asList("publish_actions", "publish_pages", "manage_pages");
        final LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Set<String> permissions = loginResult.getAccessToken().getPermissions();
                boolean contain=permissions.containsAll(publishPermissions);
                // Log.v("ggg",contain+"permission"+loginResult.getAccessToken().getPermissions());
                if(!contain){
                    loginManager.logInWithPublishPermissions(LoginFragment.this, publishPermissions);
                } else {

                    final String FACEBOOK_ACCESS_TOKEN = loginResult.getAccessToken().getToken();

                    if (Profile.getCurrentProfile() == null) {
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        GraphRequest meRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        try {
                                            //Log.v("ggg","callforpagewithnull");
                                            JSONObject resp = response.getJSONObject();
                                            saveFbLoginResults(resp.getString("name"),
                                                    FACEBOOK_ACCESS_TOKEN,
                                                    resp.getString("id"));
                                            fbPageData(FROM_FB_PAGE);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        meRequest.setParameters(parameters);
                        meRequest.executeAsync();

                    } else {
                        //Log.v("ggg","callforpage");
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
                error.printStackTrace();
                LoginManager.getInstance().logOut();
                com.facebook.AccessToken.refreshCurrentAccessTokenAsync();
            }
        });
        loginManager.logInWithReadPermissions(this, readPermissions);
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded())
                    pd = ProgressDialog.show(getActivity(), "", "Wait While Subscribing...");
            }
        });

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

    public void fbPageData(final int from) {

        List<String> readPermissions = Arrays.asList("email", "public_profile", "user_friends", "read_insights", "business_management");
        final List<String> publishPermissions = Arrays.asList("publish_actions", "publish_pages", "manage_pages");
        final LoginManager loginManager = LoginManager.getInstance();

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                boolean contain=loginResult.getAccessToken().getPermissions().containsAll(publishPermissions);
                if (!contain) {
                    loginManager.logInWithPublishPermissions(getActivity(), publishPermissions);
                } else {
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
                onFBPageError();
            }

            @Override
            public void onError(FacebookException error) {
                onFBPageError();
                error.printStackTrace();
                LoginManager.getInstance().logOut();
                com.facebook.AccessToken.refreshCurrentAccessTokenAsync();
            }
        });
        loginManager.logInWithReadPermissions(this, readPermissions);
    }

    void onFBError() {
        //Log.v("ggg","fberror");
        Constants.fbShareEnabled = false;
        prefsEditor.putBoolean("fbShareEnabled", false);
        prefsEditor.commit();
    }

    void onFBPageError() {
        //Log.v("ggg","fbpageerror");
        Constants.fbPageShareEnabled = false;
        prefsEditor.putBoolean("fbPageShareEnabled", false);
        prefsEditor.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void processGraphResponse(final GraphResponse response, final int from) {
        //Log.v("ggg","progressgraph");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
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
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    //Log.v("ggg","graphthing");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (items != null && items.size() > 0) {
                                final String[] array = items.toArray(new String[items.size()]);
                                new MaterialDialog.Builder(mContext)
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
                                                if (mNewPosition == -1) {
                                                    Toast.makeText(mContext, "Please select any Facebook page", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    String strName = array[mNewPosition];
                                                    String FACEBOOK_PAGE_ID = null;
                                                    String page_access_token = null;
                                                    try {
                                                        FACEBOOK_PAGE_ID = (String) ((JSONObject) Constants.FbPageList.get(mNewPosition)).get("id");
                                                        page_access_token = ((String) ((JSONObject) Constants.FbPageList.get(mNewPosition)).get("access_token"));
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

                                            @Override
                                            public void onNegative(MaterialDialog dialog) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            } else {

                                onFBPageError();
                                NfxRequestClient requestClient = new NfxRequestClient(LoginFragment.this)
                                        .setmFpId(session.getFPID())
                                        .setmType("facebookpage")
                                        .setmCallType(PAGE_NO_FOUND)
                                        .setmName("");
                                requestClient.nfxNoPageFound();
                                pd = ProgressDialog.show(mContext, "", getString(R.string.please_wait));

                            }
                        }
                    });
                }

            }
        }).start();
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
        if(!isAdded())
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
            prefsEditor.putString("fbPageFullUrl",
                    Constants.fbPageFullUrl);
            prefsEditor.putString("fbFromWhichPage",
                    Constants.fbFromWhichPage);
            prefsEditor.apply();
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
                prefsEditor.putBoolean("fbPageShareEnabled", Constants.fbPageShareEnabled);
                prefsEditor.apply();
                Constants.FbPageList = null;
                //InitShareResources();

            } else {

                Constants.fbPageShareEnabled = true;
            }
        }
    }
    private void showDialog(String headText, String message) {
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(Html.fromHtml(message));
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
        if (pd != null) {
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
                prefsEditor.putBoolean("fbPageShareEnabled",true);
                prefsEditor.putInt("fbPageStatus",1);
                prefsEditor.apply();
               //call other fragment
                ((OpenOtherFacebookScreen)mContext).showFragment();
                break;
            case PAGE_NO_FOUND:
                Methods.materialDialog(getActivity(), "Alert", getString(R.string.look_like_no_facebook_page));
                break;
            default:
                break;
        }
    }

    public interface OpenOtherFacebookScreen{
        void showFragment();
    }
}
