package com.nowfloats.PreSignUp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nowfloats.Login.Fetch_Home_Data;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.GetVisitorsAndSubscribersCountAsyncTask;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Event;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.BuildConfig;
import com.thinksity.R;

import java.util.ArrayList;

public class SplashScreen_Activity extends Activity implements Fetch_Home_Data.Fetch_Home_Data_Interface{
    UserSessionManager session;
    Bus bus;
    public static ProgressDialog pd ;
    private String loginCheck = null, deepLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_);
        Methods.isOnline(SplashScreen_Activity.this);
        Log.d("Splash Screen", "Splash Screen");

       /* try {
            Constants.restAdapter = Methods.createAdapter(this,Constants.NOW_FLOATS_API_URL);
            Constants.validEmailAdapter = Methods.createAdapter(this,"https://bpi.briteverify.com");
            Constants.alertInterface = Constants.restAdapter.create(NotificationInterface.class);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        if(getIntent()!=null && getIntent().getStringExtra("from")!=null){
            MixPanelController.track(EventKeysWL.NOTIFICATION_CLICKED, null);

        }


        Constants.deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Device ID","Device ID : "+Constants.deviceId);

        bus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(getApplicationContext(),SplashScreen_Activity.this);
    }

    private void Start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {Thread.sleep(1000);}catch(Exception e){e.printStackTrace();}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // This method will be executed once the timer is over Start your app main activity
                            MixPanelController.setMixPanel(SplashScreen_Activity.this, MixPanelController.mainActivity);
                            if (session.checkLogin()) {
                                loginCheck = "true";
                                displayHomeScreen();
                            } else {
                                loginCheck = "false";
                                displayPreSignUpScreens();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void displayHomeScreen() {
       /* pd = ProgressDialog.show(SplashScreen_Activity.this, "", getString(R.string.getting_details));
        pd.setCancelable(false);*/
        fetchData();
    }

    private void fetchData() {
        try{
           new Thread(new Runnable() {
               @Override
               public void run() {
                   Util.addBackgroundImages();
               }
           }).start();
           getFPDetails_retrofit(SplashScreen_Activity.this, session.getFPID(), Constants.clientId, bus);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getFPDetails_retrofit(Activity activity, String fpId, String clientId, Bus bus) {
        new Get_FP_Details_Service(activity,fpId,clientId,bus);
    }

    private void displayPreSignUpScreens() {
        if (pd!=null)
            pd.dismiss();
        HomeActivity.StorebizFloats = new ArrayList<>();
        // user is not logged in redirect him to Login Activity

        if(BuildConfig.APPLICATION_ID.equals("com.kitsune.biz")){
            Intent i = new Intent(SplashScreen_Activity.this, KitsunePreSignUpActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }else {
            Intent i = new Intent(SplashScreen_Activity.this, PreSignUp_MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
    }

    @Subscribe
    public void post_getFPDetails(Get_FP_Details_Event response)
    {

//        API_Business_enquiries businessEnquiries = new API_Business_enquiries(null,session);
//        businessEnquiries.getMessages();

        //VISITOR and SUBSCRIBER COUNT API
        if(Methods.isOnline(this)) {
            GetVisitorsAndSubscribersCountAsyncTask visit_subcribersCountAsyncTask = new GetVisitorsAndSubscribersCountAsyncTask(SplashScreen_Activity.this, session);
            visit_subcribersCountAsyncTask.execute();
        }


        Intent i = new Intent(SplashScreen_Activity.this, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        startActivity(i);
        if(pd != null)
            pd.dismiss();
        finish();

        /*Fetch_Home_Data fetch_home_data  = new Fetch_Home_Data(this,0);
        fetch_home_data.setFetchDataListener(SplashScreen_Activity.this);
        fetch_home_data.getMessages(session.getFPID(), "0");*/
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

        Intent i = new Intent(SplashScreen_Activity.this, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        startActivity(i);
        if(pd != null)
            pd.dismiss();
        finish();
//        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void sendFetched(FloatsMessageModel jsonObject) {

    }
}
