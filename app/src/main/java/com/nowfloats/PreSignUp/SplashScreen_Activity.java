package com.nowfloats.PreSignUp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.boost.presignup.utils.PresignupManager;
import com.boost.upgrades.UpgradeActivity;
import com.framework.analytics.FirebaseAnalyticsUtils;
import com.nowfloats.Login.Fetch_Home_Data;
import com.nowfloats.Login.Login_MainActivity;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.GetVisitorsAndSubscribersCountAsyncTask;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Event;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.sync.DbController;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DataBase;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.BuildConfig;
import com.thinksity.R;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nowfloats.util.Key_Preferences.GET_FP_DETAILS_CATEGORY;
import static java.lang.String.format;

public class SplashScreen_Activity extends Activity implements Fetch_Home_Data.Fetch_Home_Data_Interface, PresignupManager.SignUpLoginHandler {
    public static ProgressDialog pd;
    UserSessionManager session;
    Bus bus;
    LottieAnimationView animationView;
    private String loginCheck = null, deepLink;
    private String deepLinkViewType = "", deepLinkFpId = "", deepLinkDay = "", deepLinkFpTag = "";
    private Thread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_);
        Methods.isOnline(SplashScreen_Activity.this);
        Bundle bundle = getIntent().getExtras();
        if (getIntent() != null && getIntent().getStringExtra("from") != null) {
            MixPanelController.track(EventKeysWL.NOTIFICATION_CLICKED, null);
            deepLink = getIntent().getStringExtra("url");
        }
        if (bundle != null) {
            deepLinkViewType = bundle.getString("deepLinkViewType");
            deepLinkFpId = bundle.getString("deepLinkFpId");
            deepLinkFpTag = bundle.getString("deepLinkFpTag");
            deepLinkDay = bundle.getString("deepLinkDay");
        }
        bus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(getApplicationContext(), SplashScreen_Activity.this);
        initLottieAnimation();

    }

    private void initLottieAnimation() {
        if (animationView == null)
            animationView = findViewById(R.id.pre_dashboard_animation);
        animationView.setAnimation(R.raw.pre_dashboard_lottie);
        animationView.setRepeatCount(LottieDrawable.INFINITE);
        animationView.playAnimation();
    }

    @Override
    public void loginClicked(Activity activity) {
        MixPanelController.track(EventKeysWL.LOGIN_BUTTON, null);
        Intent intent = new Intent(activity, Login_MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtras(getIntent());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void Start() {
        if (mThread == null) {
            mThread = new Thread(new DataRunnable());
        }
        mThread.start();
    }

    private void displayHomeScreen() {
       /* pd = ProgressDialog.show(SplashScreen_Activity.this, "", getString(R.string.getting_details));
        pd.setCancelable(false);*/
        fetchData();
    }

    private void fetchData() {
        try {
            Util.addBackgroundImages();
            getFPDetails_retrofit(SplashScreen_Activity.this, session.getFPID(), Constants.clientId, bus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFPDetails_retrofit(Activity activity, String fpId, String clientId, Bus bus) {
        new Get_FP_Details_Service(activity, fpId, clientId, bus);
    }

    private void displayPreSignUpScreens() {
        if (pd != null && pd.isShowing()) pd.dismiss();
        HomeActivity.StorebizFloats = new ArrayList<>();
        // user is not logged in redirect him to Login Activity

        if (BuildConfig.APPLICATION_ID.equals("com.kitsune.biz")) {
            Intent i = new Intent(SplashScreen_Activity.this, KitsunePreSignUpActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            Intent i = new Intent(SplashScreen_Activity.this, com.boost.presignup.PreSignUpActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtras(getIntent());
            // Staring Login Activity
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        finish();
//
    }

    @Override
    protected void onResume() {
        super.onResume();
        Start();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
        mThread = null;
    }

    @Override
    protected void onDestroy() {
        if (animationView != null) animationView.cancelAnimation();
        mThread = null;
        super.onDestroy();
    }

    @Subscribe
    public void post_getFPDetails(Get_FP_Details_Event response) {
//        API_Business_enquiries businessEnquiries = new API_Business_enquiries(null,session);
//        businessEnquiries.getMessages();
//        VISITOR and SUBSCRIBER COUNT API
        if (Methods.isOnline(this)) {
            GetVisitorsAndSubscribersCountAsyncTask visit_subcribersCountAsyncTask = new GetVisitorsAndSubscribersCountAsyncTask(SplashScreen_Activity.this, session);
            visit_subcribersCountAsyncTask.execute();
        }
        if (deepLinkViewType != null && deepLinkViewType.equalsIgnoreCase("CART_FRAGMENT")
                && deepLinkFpId != null && deepLinkFpId.trim().equals(session.getFPID().trim())) {
            initiateAddonMarketplace();
        } else {
            if (deepLinkViewType != null && deepLinkViewType.equalsIgnoreCase("CART_FRAGMENT")) {
                showAlertDialog();
            } else goHomePage();
        }
        //Staring Login Activity
        /*Fetch_Home_Data fetch_home_data  = new Fetch_Home_Data(this,0);
        fetch_home_data.setFetchDataListener(SplashScreen_Activity.this);
        fetch_home_data.getMessages(session.getFPID(), "0");*/
    }

    private void goHomePage() {
//        Intent i = new Intent(SplashScreen_Activity.this, HomeActivity.class);
        try {
            Intent i = new Intent(SplashScreen_Activity.this, Class.forName("com.dashboard.controller.DashboardActivity"));
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (deepLink != null) {
                if (!deepLink.contains("logout")) {
                    i.putExtras(getIntent());
                    startActivity(i);
                    if (pd != null && pd.isShowing()) pd.dismiss();
                    finish();
                } else {
                    session.logoutUser();
                    DataBase db = new DataBase(this);
                    DbController.getDbController(getApplicationContext()).deleteDataBase();
                    db.deleteLoginStatus();
                }
            } else {
                startActivity(i);
                if (pd != null && pd.isShowing()) pd.dismiss();
                finish();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog() {
        String str = format(getResources().getString(R.string.error_right_fptag), deepLinkFpTag);
        new AlertDialog.Builder(this).setMessage(str)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, (dialog, i) -> {
                    dialog.dismiss();
                    goHomePage();
                }).show();
    }

    private void initiateAddonMarketplace() {
        Intent intent = new Intent(SplashScreen_Activity.this, UpgradeActivity.class);
        intent.putExtra("expCode", session.getFP_AppExperienceCode());
        intent.putExtra("fpName", session.getFPName());
        intent.putExtra("fpid", session.getFPID().trim());
        intent.putExtra("isDeepLink", true);
        intent.putExtra("deepLinkViewType", deepLinkViewType);
        intent.putExtra("deepLinkDay", deepLinkDay);
        intent.putExtra("fpTag", session.getFpTag());
        intent.putExtra("accountType", session.getFPDetails(GET_FP_DETAILS_CATEGORY));
        intent.putStringArrayListExtra("userPurchsedWidgets", Constants.StoreWidgets);
        if (session.getFPEmail() != null) {
            intent.putExtra("email", session.getFPEmail());
        } else {
            intent.putExtra("email", "ria@nowfloats.com");
        }
        if (session.getFPPrimaryContactNumber() != null) {
            intent.putExtra("mobileNo", session.getFPPrimaryContactNumber());
        } else {
            intent.putExtra("mobileNo", "9160004303");
        }
        intent.putExtra("profileUrl", session.getFPLogo());
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    @Subscribe
    public void getResponse(Response response) {
        try {
            Intent i = new Intent(SplashScreen_Activity.this, Class.forName("com.dashboard.controller.DashboardActivity"));
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (pd != null && pd.isShowing())
                pd.dismiss();
            startActivity(i);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void getError(RetrofitError retrofitError) {
        try {
            Intent i = new Intent(SplashScreen_Activity.this, Class.forName("com.dashboard.controller.DashboardActivity"));
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (pd != null && pd.isShowing())
                pd.dismiss();
            startActivity(i);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen_, menu);
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

    @Override
    public void dataFetched(int skip, boolean isNewMessage) {
        try {
            Intent i = new Intent(SplashScreen_Activity.this, Class.forName("com.dashboard.controller.DashboardActivity"));
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            if (deepLink != null) {
                if (!deepLink.contains("logout")) {
                    //i.putExtra("url", deepLink);
                    i.putExtras(getIntent());
                    startActivity(i);
                    if (pd != null)
                        pd.dismiss();
                    finish();
                } else {
                    session.logoutUser();
                    DataBase db = new DataBase(this);
                    DbController.getDbController(getApplicationContext()).deleteDataBase();
                    db.deleteLoginStatus();
                }
            } else {
                startActivity(i);
                if (pd != null)
                    pd.dismiss();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendFetched(FloatsMessageModel jsonObject) {

    }

    private class DataRunnable implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(3100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        displayHomeScreen();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
