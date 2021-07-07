package com.nowfloats.signup.UI.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.nowfloats.Login.Login_MainActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.PreSignUp.ReverseGeoCoderAsyncTask;
import com.nowfloats.signup.UI.Model.LocationProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.concurrent.ExecutionException;

import static com.nowfloats.util.Constants.lastKnownAddress;

public class PreSignUpActivity extends FragmentActivity {

    private LinearLayout llLogin, llSignUp;

    private int permision_request_id = 0;

    private String[] permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private LocationProvider loc_provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Constants.PACKAGE_NAME.equalsIgnoreCase("com.biz2.nowfloats")
                || Constants.PACKAGE_NAME.equals("com.redtim")) {
            setContentView(R.layout.activity_pre_sign_up__main_v3);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.WHITE);
            }
        } else {
            setContentView(R.layout.activity_pre_sign_up_white_labels);
        }

        initializeViews();

        llLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MixPanelController.track(EventKeysWL.LOGIN_BUTTON, null);
                Intent intent = new Intent(PreSignUpActivity.this, Login_MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(getIntent());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        llSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MixPanelController.track(EventKeysWL.CREATE_WEBSITE_BUTTON, null);
                Intent signUpIntent = null;
                if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {
                    signUpIntent = new Intent(PreSignUpActivity.this, RiaChatInitActivity.class);
                    signUpIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(signUpIntent);
                    finish();
                } else {
                    signUpIntent = new Intent(PreSignUpActivity.this, PreSignUpActivityRia.class);
                    signUpIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    Bundle bundle = new Bundle();
                    bundle.putBundle("mBundle", new Bundle());
                    signUpIntent.putExtras(bundle);
                    startActivity(signUpIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

            }
        });
    }

    private void initializeViews() {

        llLogin = (LinearLayout) findViewById(R.id.llLogin);
        llSignUp = (LinearLayout) findViewById(R.id.llSignUp);

    }


    private void getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PreSignUpActivity.this, permission, permision_request_id);

        } else {
            loc_provider = new LocationProvider(PreSignUpActivity.this);
            if (!loc_provider.canGetLocation()) {
                loc_provider.showSettingsAlert();
            }
            Location location = null;
            if (loc_provider.canGetLocation()) {
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
                lastKnownAddress = lastKnownAddress;
                //Log.d("Last known Address","lastAddr : "+lastKnownAddress);
                if (lastKnownAddress != null)
                    GetCountryZipCode(lastKnownAddress.getCountryCode());
            }

        }

    }

    public void GetCountryZipCode(String countryid) {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        UserSessionManager sessionManager = new UserSessionManager(this, PreSignUpActivity.this);

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

}
