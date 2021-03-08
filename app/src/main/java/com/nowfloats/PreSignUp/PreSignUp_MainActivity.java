package com.nowfloats.PreSignUp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.nowfloats.Login.Login_MainActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.signup.UI.API.LoadCountryData;
import com.nowfloats.signup.UI.Model.Facebook_Event;
import com.nowfloats.signup.UI.Model.LocationProvider;
import com.nowfloats.signup.UI.Service.Facebook_Pages_Service;
import com.nowfloats.signup.UI.UI.PreSignUpActivity;
import com.nowfloats.signup.UI.UI.RiaChatInitActivity;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PreSignUp_MainActivity extends FragmentActivity implements LoadCountryData.LoadCountryData_Interface {
    LinearLayout loginTextView;
    LinearLayout signUpButton, facebookSignUpButton;
    PreSignupFragmentAdapter mAdapter;
    ViewPager mPager;
    CirclePageIndicator mIndicator;
    Address lastKnownAddress;
    FrameLayout mainScreen;
    /*final Facebook facebook = new Facebook(Constants.FACEBOOK_API_KEY);
    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);*/
    String access_token;
    Bus bus;
    UserSessionManager session;
    private int pagesLengthSize;
    private ArrayList<String> pageItems;
    private JSONArray FbPageList;
    private String mFacebookEmail;
    private String mFacebookContactName;
    private int permision_request_id=0;
    ProgressDialog progressDialog;
    Typeface robotoRegular;
    GraphResponse mMeResponse;
    GraphResponse mAccountsResponse;
    private String[] permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private CallbackManager callbackManager;

    private LocationProvider loc_provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_sign_up__main_v2);
        bus = BusProvider.getInstance().getBus();
        Methods.isOnline(PreSignUp_MainActivity.this);
        MixPanelController.track("PreSignUpActivity", null);
        robotoRegular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");

        TextView createWebsiteText = (TextView) findViewById(R.id.createWebSiteText);
        createWebsiteText.setTypeface(robotoRegular);
        TextView signUpFacebookText = (TextView) findViewById(R.id.facebook_create_txt);
        signUpFacebookText.setTypeface(robotoRegular);

        TextView signUpEmailText = (TextView) findViewById(R.id.create_txt);
        signUpEmailText.setTypeface(robotoRegular);

        TextView onlyLoginText = (TextView) findViewById(R.id.onlyloginText);
        onlyLoginText.setTypeface(robotoRegular);

        callbackManager = CallbackManager.Factory.create();

        mainScreen = (FrameLayout) findViewById(R.id.mainLayout);
        TextView belowText = (TextView) findViewById(R.id.pre_signUp_bottom_text);
        belowText.setTypeface(robotoRegular);

        session = new UserSessionManager(getApplicationContext(), PreSignUp_MainActivity.this);
        signUpButton = (LinearLayout) findViewById(R.id.pre_signup_create);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getLastKnownLocation();
                    }
                }).start();
                MixPanelController.track(EventKeysWL.CREATE_WEBSITE_BUTTON, null);
                Intent signUpIntent = new Intent(PreSignUp_MainActivity.this, PreSignUpActivity.class);
                startActivity(signUpIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                Intent i = new Intent(PreSignUp_MainActivity.this, RiaChatInitActivity.class);
                startActivity(i);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


//                ChatManager.getInstance(PreSignUp_MainActivity.this).startChat();
            }
        });

        facebookSignUpButton = (LinearLayout) findViewById(R.id.facebook_signup_create);
        facebookSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastKnownLocation();
                MixPanelController.track("SignUpWithFacebook", null);
                accessTokenFacebook();
            }
        });

        loginTextView = (LinearLayout) findViewById(R.id.pre_signUp_login);
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track(EventKeysWL.LOGIN_BUTTON, null);
                Intent intent = new Intent(PreSignUp_MainActivity.this, Login_MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        getLastKnownLocation();

        mAdapter = new PreSignupFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.ps_pager);
        mPager.setAdapter(mAdapter);
        mIndicator = (CirclePageIndicator) findViewById(R.id.ps_indicator);
        mIndicator.setViewPager(mPager);
        // mPager.
        mPager.setCurrentItem(0, true);

       /* LoadCountryData countryData = new LoadCountryData(PreSignUp_MainActivity.this);
        countryData.LoadCountryData_Listener(this);
        countryData.execute();*/
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                MixPanelController.track("PreSignUpScreen-" + position, null);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pre_sign_up_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void accessTokenFacebook() {

        progressDialog = ProgressDialog.show(PreSignUp_MainActivity.this, "", getString(R.string.fetching_details));
        progressDialog.setCancelable(false);

        List<String> readPermissions= Arrays.asList("email"
                , "public_profile",  "user_friends", "read_insights", "business_management");
        final List<String> publishPermissions = Arrays.asList("publish_actions",
                "publish_pages", "manage_pages");
        final LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Set permissions = loginResult.getAccessToken().getPermissions();
                //String name = Profile.getCurrentProfile().getName();
                if(!permissions.containsAll(new HashSet(publishPermissions))){
                    loginManager.logInWithPublishPermissions(PreSignUp_MainActivity.this, publishPermissions);
                }else {
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email");
                    GraphRequest meRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    mMeResponse = response;
                                }
                            });
                    meRequest.setParameters(parameters);
                    GraphRequestBatch batch = new GraphRequestBatch(
                            meRequest,
                            GraphRequest.newGraphPathRequest(
                                    loginResult.getAccessToken(),
                                    "/me/accounts",
                                    new GraphRequest.Callback() {
                                        @Override
                                        public void onCompleted(GraphResponse response) {
                                            mAccountsResponse = response;
                                        }
                            })
                    );
                    batch.addCallback(new GraphRequestBatch.Callback() {
                        @Override
                        public void onBatchCompleted(GraphRequestBatch batch) {
                            processFbResponse(mMeResponse, mAccountsResponse);
                        }
                    });
                    batch.executeAsync();
                }

            }

            @Override
            public void onCancel() {
                progressDialog.dismiss();
                Log.d("Test", "Test Main Fb Error Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                progressDialog.dismiss();
                error.printStackTrace();
                LoginManager.getInstance().logOut();
                //Log.d("Test", "Test Main Fb Error");
                com.facebook.AccessToken.refreshCurrentAccessTokenAsync();
            }
        });
        loginManager.logInWithReadPermissions(this, readPermissions);

    }

    private void processFbResponse(final GraphResponse mMeResponse, final GraphResponse mAccountsResponse) {
        final int[] selectPosition = {0};
        if(mMeResponse!=null && mAccountsResponse!=null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject me = mMeResponse.getJSONObject();
                        mFacebookEmail = me.getString("email");
                        mFacebookContactName = me.getString("name");
                        session.storeFacebookName(mFacebookContactName);
                        JSONObject accounts = mAccountsResponse.getJSONObject();
                        FbPageList = accounts.getJSONArray("data");
                        if (FbPageList != null) {
                            pagesLengthSize = FbPageList.length();

                            if (pagesLengthSize > 0) {
                                pageItems = new ArrayList<String>();
                                for (int i = 0; i < pagesLengthSize; i++) {
                                    pageItems.add(i, (String) ((JSONObject) FbPageList
                                            .get(i)).get("name"));
                                }


                            }
                        }


                        //System.out.println("******facebook.getAccessToken()****" + facebook.getAccessToken());

                        access_token = AccessToken.getCurrentAccessToken().getToken();
                        session.storeFacebookAccessToken(access_token);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }finally {
                        PreSignUp_MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (pageItems != null && pageItems.size() > 0) {
                                    final String[] array = pageItems.toArray(new String[pageItems.size()]);
                                    new MaterialDialog.Builder(PreSignUp_MainActivity.this)
                                            .title(R.string.choose_a_facebook_page)
                                            .positiveText(R.string.choose)
                                            .items(array)
                                            .widgetColorRes(R.color.primaryColor)
                                            .dismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialog) {
                                                    progressDialog.dismiss();
                                                }
                                            })
                                            .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                                @Override
                                                public boolean onSelection(MaterialDialog dialog, View view, int position, CharSequence text) {
                                                    selectPosition[0] = position;
                                                    String strName = array[position];
                                                    session.storeFacebookPage(strName);

                                                    try {
                                                        String FACEBOOK_PAGE_ID = (String) ((JSONObject) FbPageList.get(position)).get("id");
                                                        String page_access_token = ((String) ((JSONObject) FbPageList.get(position)).get("access_token"));
                                                        session.storePageAccessToken(page_access_token);
                                                        getFacebookImage_FeaturedImage(FACEBOOK_PAGE_ID);
                                                        session.storeFacebookPageID(FACEBOOK_PAGE_ID);

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    new Facebook_Pages_Service(PreSignUp_MainActivity.this, access_token, session.getFacebookPageID(), bus);
                                                    // getFacebookImage_FeaturedImage(session.getFacebookPageID());
                                                    //  pageSeleted(position,strName,session.getFacebookPageID(),session.getPageAccessToken());
                                                    dialog.dismiss();
                                                    return true;
                                                }
                                            })
                                            .callback(new MaterialDialog.ButtonCallback() {
                                                @Override
                                                public void onPositive(MaterialDialog dialog) {
                                                    super.onPositive(dialog);
                                                    int position = selectPosition[0];
                                                    String strName = array[position];
                                                    session.storeFacebookPage(strName);

                                                    try {
                                                        String FACEBOOK_PAGE_ID = (String) ((JSONObject) FbPageList.get(position)).get("id");
                                                        String page_access_token = ((String) ((JSONObject) FbPageList.get(position)).get("access_token"));
                                                        session.storePageAccessToken(page_access_token);
                                                        getFacebookImage_FeaturedImage(FACEBOOK_PAGE_ID);
                                                        session.storeFacebookPageID(FACEBOOK_PAGE_ID);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    new Facebook_Pages_Service(PreSignUp_MainActivity.this, access_token, session.getFacebookPageID(), bus);
                                                    // getFacebookImage_FeaturedImage(session.getFacebookPageID());
                                                    //  pageSeleted(position,strName,session.getFacebookPageID(),session.getPageAccessToken());
                                                    dialog.dismiss();

                                                }
                                            })
                                            .show();

                                } else {
                                    Methods.materialDialog(PreSignUp_MainActivity.this, "Uh oh~", "Looks like there is no Facebook page\nlinked to this account.");
                                }
                            }
                        });

                    }
                }
            }).start();
        }else {
            Toast.makeText(this, getString(R.string.facebook_response_error),Toast.LENGTH_SHORT).show();
        }
    }

    private void getFacebookImage_FeaturedImage(String page_id) {
        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        params.putString("type", "large");
        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + page_id + "/picture",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Insert your code here
                        try {
                            JSONObject imageData = response.getJSONObject().getJSONObject("data");
                            String imageURL = imageData.getString("url");
                            String isDefaultImage = imageData.get("is_silhouette").toString();
                            // if(isDefaultImage.contains("false"))
                            //  {
                            //   Constants.facebookPageURL = imageURL ;
                            session.storeFacebookPageURL(imageURL);

                            //  }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("type", "large");
        parameters.putString("redirect", "false");
        request.setParameters(parameters);
        request.executeAsync();
        /*new Request(
                facebook.getSession(),
                "/" + page_id + "/picture",
                params,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        Log.d("Response", "Response");

                        try {
                            JSONObject imageData = (JSONObject) response.getGraphObject().getProperty("data");
                            String imageURL = imageData.get("url").toString();
                            String isDefaultImage = imageData.get("is_silhouette").toString();
                            // if(isDefaultImage.contains("false"))
                            //  {
                            //   Constants.facebookPageURL = imageURL ;
                            session.storeFacebookPageURL(imageURL);

                            //  }
                            Log.d("ImageURL", "ImageURL : " + imageURL);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
            *//* handle the result *//*
                    }
                }
        ).executeAsync();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        bus.unregister(this);
    }

    @Subscribe
    public void getMeAccountDetails(Facebook_Event response) {
        session.setSignUpFromFacebook("true");


        progressDialog.dismiss();


        Intent signUpIntent = new Intent(PreSignUp_MainActivity.this, PreSignUpActivity.class);
        Bundle facebookBundle = new Bundle();
        facebookBundle.putString("facebook_businessName", session.getFacebookPage());
        if (response.model.location != null) {
            facebookBundle.putString("facebook_city", response.model.location.city);
        } else {
            facebookBundle.putString("facebook_city", "");
        }
        if (response.model.location != null) {
            facebookBundle.putString("facebook_country", response.model.location.country);
        } else {
            facebookBundle.putString("facebook_country", "");
        }
        // Constants.facebookPageURL = response.model.picture.data.url;
        // Constants.facebookPageDescription = response.model.description ;
        session.storeFacebookProfileDescription(response.model.description);
        facebookBundle.putString("facebook_page_url", response.model.picture.data.url);
        // SidePanelFragment.iconImage

//        Picasso.with(this)
//                .load("response.model.picture.data.url")
//                      // optional
//                .resize(150, 100)                        // optional
//                .rotate(90)                             // optional
//                .into(SidePanelFragment.iconImage);
        facebookBundle.putString("facebook_email", mFacebookEmail);
        signUpIntent.putExtras(facebookBundle);
        startActivity(signUpIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        //facebook.authorizeCallback(requestCode, resultCode, data);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLastKnownLocation();

                }

            }
        }

            // other 'case' lines to check for other
            // permissions this app might request
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
    }

    private void getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PreSignUp_MainActivity.this, permission, permision_request_id);

        }else
        {
            loc_provider = new LocationProvider(PreSignUp_MainActivity.this);
            if (!loc_provider.canGetLocation()) {
                loc_provider.showSettingsAlert();
            }
            Location location =null;
            if(loc_provider.canGetLocation()){
                location = loc_provider.getLocation();
                loc_provider.stopUsingGPS();
            }
            if (location != null) {
                ReverseGeoCoderAsyncTask task = new ReverseGeoCoderAsyncTask(this, location);
                //Log.d("ILUD location", String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
                try {
                    task.execute().get();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                lastKnownAddress = Constants.lastKnownAddress;
                //Log.d("Last known Address","lastAddr : "+lastKnownAddress);
                if (lastKnownAddress != null)
                    GetCountryZipCode(lastKnownAddress.getCountryCode());
            }

        }

    }

    public void GetCountryZipCode(String countryid) {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        UserSessionManager sessionManager = new UserSessionManager(this, PreSignUp_MainActivity.this);

        //getNetworkCountryIso
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(countryid)) {
                sessionManager.storeFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE, g[0] + "");
                break;
            }
        }
    }

    @Override
    public void LoadCountry_onPostExecute_Completed(String result) {

    }
}
