package com.nowfloats.on_boarding;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.boost.presignup.datamodel.userprofile.ConnectUserProfileResponse;
import com.boost.presignup.datamodel.userprofile.ConnectUserProfileResult;
import com.boost.presignup.datamodel.userprofile.UserProfileResponse;
import com.boost.presignup.utils.CustomFirebaseAuthHelpers;
import com.boost.presignup.utils.CustomFirebaseAuthListeners;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.Login.AuthLoginInterface;
import com.nowfloats.Login.LoginManager;
import com.nowfloats.Login.Login_MainActivity;
import com.nowfloats.Login.MobileOtpFragment;
import com.nowfloats.Login.MobileOtpVerificationFragment;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.Mobile_Site_Activity;
import com.nowfloats.NavigationDrawer.businessApps.FragmentsFactoryActivity;
import com.nowfloats.ProductGallery.ProductGalleryActivity;
import com.nowfloats.helper.ui.KeyboardUtil;
import com.nowfloats.on_boarding.models.OnBoardingModel;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import static android.view.Window.FEATURE_NO_TITLE;

/**
 * Created by Admin on 16-03-2018.
 */

public class OnBoardingActivity extends AppCompatActivity implements OnBoardingAdapter.ItemClickListener, AuthLoginInterface {

    private UserSessionManager session;
    private OnBoardingAdapter adapter;
    private boolean isSomethingChanged = false;
    private CustomFirebaseAuthHelpers customFirebaseAuthHelpers;
    private ProgressDialog progressDialog;
    private LoginButton facebookloginButton;
    private String channel = "";
    private CallbackManager callbackManager;
    private MobileOtpVerificationFragment mobileOtpVerificationFragment;
    private boolean isOtpInForegrouond = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(FEATURE_NO_TITLE);
        setContentView(R.layout.activity_onboarding);
        OnBoardingModel mOnBoardingModel = getIntent().getParcelableExtra("data");
        new KeyboardUtil(this, findViewById(R.id.fl_parent_layout));

        if (mOnBoardingModel == null) {
            finish();
            return;
        }
        shrinkScreen();

        LoginManager.getInstance().setListener(this);

        MixPanelController.track(MixPanelController.ON_BOARDING_SCREEN_SHOW, null);
        session = new UserSessionManager(this, this);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels);


        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setFinishOnTouchOutside(true);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new OnBoardingAdapter(this, mOnBoardingModel);
        facebookloginButton = findViewById(R.id.fb_login_button);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.scrollToPosition(mOnBoardingModel.getToBeCompletePos());
        new PagerSnapHelper().attachToRecyclerView(mRecyclerView);
    }

    public void shareWebsite() {
        String url = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
        if (!Util.isNullOrEmpty(url)) {
            String eol = System.getProperty("line.separator");
            url = getString(R.string.visit_to_new_website)
                    + eol + url.toLowerCase();
        } else {
            String eol = System.getProperty("line.separator");
            url = getString(R.string.visit_to_new_website)
                    + eol + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                    + getResources().getString(R.string.tag_for_partners);
        }
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
        session.setWebsiteshare(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSomethingChanged) {
            adapter.refreshAfterComplete();
            isSomethingChanged = false;
        }
    }

    @Override
    public void onItemClick(int position, OnBoardingModel.ScreenData screenData) {
        Intent intent = null;
        switch (position) {
            case 0:
                MixPanelController.track(MixPanelController.ON_BOARDING_WELCOME_ABOARD, null);
                intent = new Intent(this, Mobile_Site_Activity.class);
                intent.putExtra("WEBSITE_NAME", getString(R.string.onboarding_about_product_url));
                // step complete
                if (!screenData.isComplete()) {
                    screenData.setIsComplete(true);
                    adapter.refreshAfterComplete();
                    OnBoardingApiCalls.updateData(session.getFpTag(), "welcome_aboard:true");
                }
                isSomethingChanged = true;
                break;
            case 1:
                MixPanelController.track(MixPanelController.ON_BOARDING_SITE_HEALTH, null);
                intent = new Intent(this, FragmentsFactoryActivity.class);
                intent.putExtra("fragmentName", "SiteMeterFragment");
                isSomethingChanged = true;
                break;
            case 2:
                MixPanelController.track(MixPanelController.ON_BOARDING_CUSTOM_PAGE, null);
                intent = new Intent(this, CustomPageActivity.class);
                isSomethingChanged = true;
                break;
            case 3:
                MixPanelController.track(MixPanelController.ON_BOARDING_ADD_PRODUCT, null);
                intent = new Intent(this, ProductGalleryActivity.class);
                isSomethingChanged = true;
                break;
            case 4:
                isSomethingChanged = true;
                MixPanelController.track(MixPanelController.ON_BOARDING_BOOST_APP, null);
                if (!screenData.isComplete()) {
                    screenData.setIsComplete(true);
                    adapter.refreshAfterComplete();
                    OnBoardingApiCalls.updateData(session.getFpTag(), "boost_app:true");
                }
                return;
            case 5:
                isSomethingChanged = true;
                MixPanelController.track(MixPanelController.ON_BOARDING_SHARE_WEBSITE, null);
                shareWebsite();
                if (!screenData.isComplete()) {
                    screenData.setIsComplete(true);
                    adapter.refreshAfterComplete();
                    OnBoardingApiCalls.updateData(session.getFpTag(), "share_website:true");
                }
                // step complete
                return;
            default:
                return;
        }
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (customFirebaseAuthHelpers != null) {
            if (callbackManager != null && channel.equals("FACEBOOK")) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            } else {
                customFirebaseAuthHelpers.googleLoginActivityResult(requestCode, data);
            }
        }
    }

    private CustomFirebaseAuthListeners customFirebaseAuthListeners = new CustomFirebaseAuthListeners() {
        @Override
        public void onSuccess(@org.jetbrains.annotations.Nullable ConnectUserProfileResponse response) {

            isOtpInForegrouond = false;

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            Log.i(Login_MainActivity.class.getName(), new Gson().toJson(response));
            if(response != null) {

                if(channel.equals("OTP")) {
                   while(getSupportFragmentManager().getBackStackEntryCount() != 0) {
                       getSupportFragmentManager().popBackStackImmediate();
                   }
                }

                Toast.makeText(OnBoardingActivity.this, "Connected", Toast.LENGTH_LONG).show();
                ConnectUserProfileResult result = response.getResult();
                session.isGoogleAuthDone(result.getChannels().getGOOGLE());
                session.isFacebookAuthDone(result.getChannels().getFACEBOOK());
                session.isOTPAuthDone(result.getChannels().getOTP());
                adapter.refreshAfterComplete();

            }else{
                Methods.showSnackBarNegative(OnBoardingActivity.this, "Login failed");
            }

        }

        @Override
        public void onSuccess(@org.jetbrains.annotations.Nullable UserProfileResponse response, String uniqueId) {
            Log.i(Login_MainActivity.class.getName(), new Gson().toJson(response));
        }

        @Override
        public void onFailure() {
            isOtpInForegrouond = false;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Methods.showSnackBarNegative(OnBoardingActivity.this, "Login failed");
        }
    };

    @Override
    public void onBoardingComplete() {
        MixPanelController.track(MixPanelController.ON_BOARDING_COMPLETE, null);
        OnBoardingApiCalls.updateData(session.getFpTag(), "is_complete:true");
        finish();
    }

    @Override
    public void onGoogleLogin() {
        channel = "GOOGLE";
        customFirebaseAuthHelpers = new CustomFirebaseAuthHelpers(OnBoardingActivity.this, customFirebaseAuthListeners, session.getFPID());
        customFirebaseAuthHelpers.startGoogleLogin();
    }

    @Override
    public void onFacebookLogin() {
        channel = "FACEBOOK";
        facebookloginButton.performClick();
        callbackManager = CallbackManager.Factory.create();
        customFirebaseAuthHelpers = new CustomFirebaseAuthHelpers(OnBoardingActivity.this, customFirebaseAuthListeners, session.getFPID());
        customFirebaseAuthHelpers.startFacebookLogin(facebookloginButton, callbackManager);
    }

    @Override
    public void onOTPLogin() {
        channel = "OTP";
        getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        gotoMobileOtpVerificationFragment();
    }

    private void gotoMobileOtpVerificationFragment() {

        isOtpInForegrouond = true;

        MobileOtpFragment.OnMobileProvidedListener onMobileProvidedListener = mobileNumber -> startPhoneNumberAuth(mobileNumber, true);

        MobileOtpFragment mobileOtpFragment = new MobileOtpFragment(onMobileProvidedListener);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.fl_parent_layout, mobileOtpFragment).commit();
    }

    private void startPhoneNumberAuth(String phoneNumber, boolean gotoNextPage) {
        progressDialog = ProgressDialog.show(this, "", "Loading");

        customFirebaseAuthHelpers = new CustomFirebaseAuthHelpers(this, customFirebaseAuthListeners, session.getFPID());

        customFirebaseAuthHelpers.startPhoneAuth(phoneNumber, () -> {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (gotoNextPage)
                gotoOtpVerificationFragment(phoneNumber);
        });
    }

    private void gotoOtpVerificationFragment(String phoneNumber) {
        MobileOtpVerificationFragment.OnOTPProvidedListener onOTPProvidedListener = new MobileOtpVerificationFragment.OnOTPProvidedListener() {

            @Override
            public void onOTPProvided(String otp) {
                submitUserOtp(otp);
            }

            @Override
            public void onResend(String phoneNumber) {
                startPhoneNumberAuth(phoneNumber, false);
            }

            @Override
            public String getMobileEntered() {
                return phoneNumber;
            }
        };

        mobileOtpVerificationFragment = new MobileOtpVerificationFragment(onOTPProvidedListener);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fl_parent_layout, mobileOtpVerificationFragment).commit();
    }

    private void submitUserOtp(String otp) {
        progressDialog = ProgressDialog.show(this, "", "Loading");
        customFirebaseAuthHelpers.phoneAuthVerification(otp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isOtpInForegrouond) {
            shrinkScreen();
        }

    }

    private void shrinkScreen() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels);
        getWindow().setLayout(screenWidth, ConstraintLayout.LayoutParams.WRAP_CONTENT);
    }
}
