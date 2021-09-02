package com.nowfloats.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.appservice.model.accountDetails.AccountDetailsResponse;
import com.appservice.model.kycData.PaymentKycDataResponse;
import com.boost.presignup.PreSignUpActivity;
import com.boost.presignup.datamodel.userprofile.ConnectUserProfileResponse;
import com.boost.presignup.datamodel.userprofile.UserProfileResponse;
import com.boost.presignup.datamodel.userprofile.UserResult;
import com.boost.presignup.datamodel.userprofile.VerificationRequestResult;
import com.boost.presignup.utils.CustomFirebaseAuthHelpers;
import com.boost.presignup.utils.CustomFirebaseAuthListeners;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.Model.Login_Data_Model;
import com.nowfloats.NavigationDrawer.API.GetVisitorsAndSubscribersCountAsyncTask;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.helper.ui.KeyboardUtil;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Event;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.Utils;
import com.nowfloats.util.WebEngageController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import jp.wasabeef.richeditor.RichEditor;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABLE_LOGIN;
import static com.framework.webengageconstant.EventNameKt.PS_LOGIN_USERNAME_PAGE_LOAD;

public class Login_MainActivity extends AppCompatActivity implements API_Login.API_Login_Interface, View.OnClickListener {
    /*private String[] permission = new String[]{Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_PHONE_STATE};*/
    private final static int READ_MESSAGES_ID = 221;
    public static ProgressDialog progressDialog;
    Bus bus;
    EditText userName, password;
    CardView loginButton;
    View line1, line2;
    UserSessionManager session;
    String userNameText, passwordText;
    String currentProvider = "";
    boolean isUpdatedOnServer = true;
    LinearLayout parent_layout;
    //private Toolbar toolbar;
    private TextView forgotPassword;
    //private TextView headerText;
    private Intent dashboardIntent;
    private RichEditor mEditor;
    private TextView mPreview, tvHeadingText;
    private CardView cvFacebookLogin, cvGoogleLogin, cvOtpVerification;
    private LoginButton loginFacebookButton;
    private CustomFirebaseAuthHelpers customFirebaseAuthHelpers;
    private CallbackManager callbackManager;

    public static void registerChat(String userId) {
        BoostLog.d("HomeActivity", "This is getting Called");
        try {
            final HashMap<String, String> params = new HashMap<String, String>();
            params.put("Channel", FirebaseInstanceId.getInstance().getToken());
            params.put("UserId", userId);
            params.put("DeviceType", "ANDROID");
            params.put("clientId", Constants.clientId);

            Log.i("Ria_Register GCM id--", "API call Started");

            Login_Interface emailValidation = Constants.restAdapter.create(Login_Interface.class);
            emailValidation.post_RegisterRia(params, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.i("GCM local ", "reg success" + params.toString());
                    Log.d("Response", "Response : " + s);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("GCM local ", "reg FAILed" + params.toString());
                }
            });
        } catch (Exception e) {
            Log.i("Ria_Register ", "API Exception:" + e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (customFirebaseAuthHelpers != null) {
            if (callbackManager != null) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            } else {
                if (resultCode != RESULT_OK) {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Methods.showSnackBar(this, getString(R.string.login_failed_please_try_again));
                    return;
                }
                customFirebaseAuthHelpers.disableAutoUserProfileCreationMode();
                customFirebaseAuthHelpers.googleLoginActivityResult(requestCode, data);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
        Methods.isOnline(Login_MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.forgotPwdTextView) {
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.forgot_password))
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(getString(R.string.enter_user_name), null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            try {
                                MixPanelController.track(EventKeysWL.LOGIN_SCREEN_FORGOT_PWD, null);
                                String enteredText = input.toString().trim();
                                if (enteredText.length() > 1) {
                                    sendPasswordToEmail(enteredText);
                                    dialog.dismiss();
                                } else {
                                    YoYo.with(Techniques.Shake).playOn(dialog.getInputEditText());
                                    Methods.showSnackBarNegative(Login_MainActivity.this, getString(R.string.enter_correct_user_name));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .positiveText(getString(R.string.ok))
                    .negativeText(getString(R.string.cancel))
                    .positiveColorRes(R.color.primaryColor)
                    .negativeColorRes(R.color.primaryColor)
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_login_main_v2);
        Methods.isOnline(Login_MainActivity.this);

        new KeyboardUtil(this, findViewById(R.id.fl_parent_layout));

//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        if(mAuth != null && mAuth.getCurrentUser() != null){
//            mAuth.signOut();
//        }

        bus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(getApplicationContext(), Login_MainActivity.this);
        Class<?> dashBoardActivity;
        try {
            dashBoardActivity = Class.forName("com.dashboard.controller.DashboardActivity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
//        dashboardIntent = new Intent(Login_MainActivity.this, HomeActivity.class);
        dashboardIntent = new Intent(Login_MainActivity.this, dashBoardActivity);
        dashboardIntent.putExtras(getIntent());
        parent_layout = findViewById(R.id.parent_layout);
        cvFacebookLogin = findViewById(R.id.cv_facebook_login);
        loginFacebookButton = findViewById(R.id.facebook_login_button);
        cvGoogleLogin = findViewById(R.id.cv_google_login);
        cvOtpVerification = findViewById(R.id.cv_otp);

        line1 = findViewById(R.id.fl_line_1);
        line2 = findViewById(R.id.fl_line_2);


        //toolbar = (Toolbar) findViewById(R.id.app_bar);
        // headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        //headerText.setText(getString(R.string.welcome_back));
        // setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
        //      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WebEngageController.trackEvent(PS_LOGIN_USERNAME_PAGE_LOAD, EVENT_LABLE_LOGIN, session.getFpTag());
        userName = findViewById(R.id.userNameEditText);
        password = findViewById(R.id.passwordEditText);

        new Handler().postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(userName, InputMethodManager.SHOW_IMPLICIT);
        }, 500);

        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ViewGroup.LayoutParams lp = line1.getLayoutParams();
                if (hasFocus) {
                    lp.height = 5;
                } else {
                    lp.height = 2;
                }
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                line1.setLayoutParams(lp);
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ViewGroup.LayoutParams lp = line2.getLayoutParams();
                if (hasFocus) {
                    lp.height = 5;
                } else {
                    lp.height = 2;
                }
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                line2.setLayoutParams(lp);
            }
        });

        password.setOnTouchListener((arg0, arg1) -> {
            if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                int tot_width = password.getWidth();
                float cur_x = arg1.getX();
                float res = (cur_x / Float.parseFloat(tot_width + "") * (Float.parseFloat("100")));
                if (res >= 85) {
                    String d = password.getTag().toString();
                    if (d.equals("pwd")) {
                        password.setTag("show");
                        password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pwd_hide, 0);
                    } else {
                        password.setTag("pwd");
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pwd_show, 0);
                    }
                }
            }
            return false;
        });

        findViewById(R.id.im_back_button).setOnClickListener(v -> {
            Login_MainActivity.this.onBackPressed();
        });

        forgotPassword = findViewById(R.id.forgotPwdTextView);
        forgotPassword.setPaintFlags(forgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        forgotPassword.setOnClickListener(this);

        ImageView userNameIcon = findViewById(R.id.userNameIcon);
        ImageView passwordIcon = findViewById(R.id.passwordIcon);

        userName.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(userName.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
        userNameIcon.setColorFilter(whiteLabelFilter);
        passwordIcon.setColorFilter(whiteLabelFilter);


        CustomFirebaseAuthListeners customFirebaseAuthListeners = new CustomFirebaseAuthListeners() {
            @Override
            public void onSuccess(@Nullable ConnectUserProfileResponse response) {

            }

            @Override
            public void onSuccess(@Nullable VerificationRequestResult response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response != null && response.getLoginId() != null)
                    processLoginSuccessRequest(response);
                else
                    Methods.showSnackBarNegative(Login_MainActivity.this, getString(R.string.ensure_that_the_entered_username_password));
            }


            @Override
            public void onSuccess(@Nullable UserProfileResponse response, String uniqueId) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response != null) {
                    loginSuccess(response);
                } else {
                    Methods.showSnackBarNegative(Login_MainActivity.this, getString(R.string.error_occured_while_processing_your_login));
                }
            }


            @Override
            public void onFailure() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(Login_MainActivity.this, getString(R.string.error_occured_while_processing_your_login), Toast.LENGTH_LONG).show();

            }
        };

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            userNameText = userName.getText().toString().trim();
            passwordText = password.getText().toString().trim();
            if (userNameText.length() > 0 && passwordText.length() > 0) {
                Methods.hideKeyboard(Login_MainActivity.this);
                userName.clearFocus();
                progressDialog = ProgressDialog.show(Login_MainActivity.this, "", getString(R.string.processing_request));
                progressDialog.setCancelable(true);
                currentProvider = "";
                customFirebaseAuthHelpers = new CustomFirebaseAuthHelpers(Login_MainActivity.this, customFirebaseAuthListeners, "");
                customFirebaseAuthHelpers.verifyUserProfileAPI(userNameText, passwordText, "");
            } else {
                YoYo.with(Techniques.Shake).playOn(userName);
                YoYo.with(Techniques.Shake).playOn(password);
                Toast.makeText(Login_MainActivity.this, getString(R.string.enter_valid_login_details), Toast.LENGTH_SHORT).show();
            }
        });

        cvFacebookLogin.setOnClickListener(v -> {
            currentProvider = "Facebook";

            loginFacebookButton.performClick();
            callbackManager = CallbackManager.Factory.create();
            customFirebaseAuthHelpers = new CustomFirebaseAuthHelpers(Login_MainActivity.this, customFirebaseAuthListeners, "");
            customFirebaseAuthHelpers.disableAutoUserProfileCreationMode();
            customFirebaseAuthHelpers.startFacebookLogin(loginFacebookButton, callbackManager);
        });

        cvGoogleLogin.setOnClickListener(v -> {
            progressDialog = ProgressDialog.show(Login_MainActivity.this, "", getString(R.string.loading));
            progressDialog.setCancelable(true);
            currentProvider = "Google";
            customFirebaseAuthHelpers = new CustomFirebaseAuthHelpers(Login_MainActivity.this, customFirebaseAuthListeners, "");
            customFirebaseAuthHelpers.disableAutoUserProfileCreationMode();
            customFirebaseAuthHelpers.startGoogleLogin();
        });

        cvOtpVerification.setOnClickListener(v -> {
            gotoMobileOtpVerificationFragment();
        });

        findViewById(R.id.ll_2).setOnClickListener(v -> {
            password.requestFocus();
        });
    }


    private void getFPDetails(Activity activity, String fpId, String clientId, Bus bus) {
        new Get_FP_Details_Service(activity, fpId, clientId, bus);
    }

    private void startDashboard() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(dashboardIntent);
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    public void authenticationFailure(String value) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        Toast.makeText(Login_MainActivity.this, getString(R.string.sorry_no_boost_account_was_found_for_the_given), Toast.LENGTH_SHORT).show();
    }

    protected void sendPasswordToEmail(String enteredText) {
        final ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.processing_request), true);
        JSONObject obj = new JSONObject();
        try {
            obj.put("clientId", Constants.clientId);
            obj.put("fpKey", enteredText);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = Constants.NOW_FLOATS_API_URL + "/Discover/v1/floatingpoint/forgotPassword";

        com.android.volley.Response.Listener<String> listener = response -> {
        };

        com.android.volley.Response.ErrorListener error = error1 -> {
            error1.printStackTrace();
            if (!isFinishing() && dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (!isUpdatedOnServer) {
                runOnUiThread(() -> Methods.showSnackBarNegative(Login_MainActivity.this, getString(R.string.enter_correct_user_name)));
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<String> req = new JsonRequest<String>(Request.Method.POST, url,
                obj.toString(), listener, error) {
            @Override
            protected com.android.volley.Response<String> parseNetworkResponse(
                    NetworkResponse response) {
                if (!isFinishing() && dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (response.statusCode == 200) {
                    isUpdatedOnServer = true;
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                Methods.showSnackBarPositive(Login_MainActivity.this,"\n" +
//                                        "We’ve sent you an email with your login details");
                                if (!isFinishing()) {
                                    new MaterialDialog.Builder(Login_MainActivity.this)
                                            .title(getString(R.string.check_your_email))
                                            .content(getString(R.string.we_sent_email_with_password))
                                            .positiveText(getString(R.string.ok))
                                            .show();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.getCause();
                    }
                } else {
                    isUpdatedOnServer = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Methods.showSnackBarNegative(Login_MainActivity.this, getString(R.string.enter_correct_user_name));
                        }
                    });
                }
                return null;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("Authorization", Utils.getAuthToken());
                return headers;

            }
        };
        queue.add(req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login__main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        /*if(BuildConfig.APPLICATION_ID.equals("com.kitsune.biz")){
            Intent signup = new Intent(Login_MainActivity.this, KitsunePreSignUpActivity.class);
            startActivity(signup);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }else {
            Intent signup = new Intent(Login_MainActivity.this, PreSignUp_MainActivity.class);
            startActivity(signup);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }*/
        // Otherwise defer to system default behavior.
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            //NavUtils.navigateUpFromSameTask(this);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
//    private void getPermission(){
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)== PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED){
//
//            // start the service to send data to firebase
//        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//        {
//            // if user deny the permissions
//           /* if(shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)||
//                    shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){
//
//                Snackbar.make(parent_layout, com.nfx.leadmessages.R.string.required_permission_to_show, Snackbar.LENGTH_INDEFINITE)
//                        .setAction(com.nfx.leadmessages.R.string.enable, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent();
//                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                                intent.setData(Uri.parse("package:" + getPackageName()));
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                                startActivity(intent);
//                            }
//                        })  // action text on the right side of snackbar
//                        .setActionTextColor(ContextCompat.getColor(this,android.R.color.holo_green_light))
//                        .show();
//            }
//            else{*/
//            //requestPermissions(permission,READ_MESSAGES_ID);
//            // }
//
//        }
//
//    }

    // this method called when user react on permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_MESSAGES_ID:
                //getPermission();
                break;
            default:
                break;
        }
    }

    private void processLoginSuccessRequest(VerificationRequestResult response) {
        if (response.getLoginId() != null && !response.getLoginId().isEmpty()) {
            try {
                session.setUserProfileId(response.getLoginId());
                session.setUserProfileEmail(response.getProfileProperties().getUserEmail());
                session.setUserProfileName(response.getProfileProperties().getUserName());
                session.setUserProfileMobile(response.getProfileProperties().getUserMobile());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (response.getValidFPIds() == null || response.getValidFPIds().length == 0) {
                showBusinessProfileCreationStartScreen(response.getLoginId());
            } else {
                session.setUserLogin(true);
                progressDialog = ProgressDialog.show(Login_MainActivity.this, "", "Loading");
                session.storeISEnterprise(response.isEnterprise() + "");
                session.storeIsThinksity((response.getSourceClientId() != null && response.getSourceClientId().equals(Constants.clientIdThinksity)) + "");
                session.storeFPID(response.getValidFPIds()[0]);
                authenticationStatus("Success");
            }
        } else {
            Toast.makeText(this, R.string.signup_your_account, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Login_MainActivity.this, PreSignUpActivity.class);
            intent.putExtra("isSignUpBottomSheet", true);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    private void startPhoneNumberAuth(String phoneNumber, boolean gotoNextPage) {
        progressDialog = ProgressDialog.show(this, "", "Loading");

        customFirebaseAuthHelpers = new CustomFirebaseAuthHelpers(this, new CustomFirebaseAuthListeners() {


            @Override
            public void onSuccess(@Nullable ConnectUserProfileResponse response) {

            }


            @Override
            public void onSuccess(@Nullable VerificationRequestResult response) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (response != null) processLoginSuccessRequest(response);
                else
                    Methods.showSnackBarNegative(Login_MainActivity.this, getString(R.string.unable_to_validate_your_phone));
            }


            @Override
            public void onSuccess(@Nullable UserProfileResponse response, String uniqueId) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (response != null) {
                    loginSuccess(response);
                } else {
                    Methods.showSnackBarNegative(Login_MainActivity.this, getString(R.string.error_occured_while_processing_your_mobile));
                }
            }

            @Override
            public void onFailure() {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                Methods.showSnackBarNegative(Login_MainActivity.this, getString(R.string.unable_to_validate_your_phone));
            }
        }, "");

        customFirebaseAuthHelpers.disableAutoUserProfileCreationMode();
        customFirebaseAuthHelpers.startPhoneAuth(phoneNumber, () -> {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (gotoNextPage)
                gotoOtpVerificationFragment(phoneNumber);
        });
    }

    private void gotoMobileOtpVerificationFragment() {

        MobileOtpFragment.OnMobileProvidedListener onMobileProvidedListener = mobileNumber -> startPhoneNumberAuth(mobileNumber, true);

        MobileOtpFragment mobileOtpFragment = new MobileOtpFragment(onMobileProvidedListener);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.fl_parent_layout, mobileOtpFragment).commit();
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

        MobileOtpVerificationFragment otpVerificationFragment = new MobileOtpVerificationFragment(onOTPProvidedListener);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fl_parent_layout, otpVerificationFragment).commit();
    }

    private void submitUserOtp(String otp) {
        progressDialog = ProgressDialog.show(this, "", "Loading");
        customFirebaseAuthHelpers.phoneAuthVerification(otp);
    }

    private void processUserProfile(UserProfileResponse userProfileResponse) {
        if (userProfileResponse == null) {
            Methods.showSnackBarNegative(this, "Login failed");
        } else if (userProfileResponse.getResult().getFpIds() == null || userProfileResponse.getResult().getFpIds().length == 0) {
            session.setUserLogin(true);
            try {
                session.setUserProfileEmail(userProfileResponse.getResult().getProfileProperties().getUserEmail());
                session.setUserProfileName(userProfileResponse.getResult().getProfileProperties().getUserName());
                session.setUserProfileMobile(userProfileResponse.getResult().getProfileProperties().getUserMobile());
            } catch (Exception e) {
            }
            showBusinessProfileCreationStartScreen(userProfileResponse.getResult().getLoginId());
        } else {
            progressDialog = ProgressDialog.show(this, "", "Loading");

            UserResult result = userProfileResponse.getResult();
            session.storeISEnterprise(result.getIsEnterprise() + "");
            session.storeIsThinksity((result.getSourceClientId() != null &&
                    result.getSourceClientId().equals(Constants.clientIdThinksity)) + "");
            session.storeFPID(userProfileResponse.getResult().getFpIds()[0]);
            authenticationStatus("Success");
        }
    }

  private void showBusinessProfileCreationStartScreen(String userProfileId) {
    WebEngageController.initiateUserLogin(userProfileId);
    WebEngageController.setUserContactInfoProperties(session);

        Intent signupConfirmationPage = new Intent(Login_MainActivity.this, com.boost.presignup.SignUpConfirmation.class);
        signupConfirmationPage.putExtra("profileUrl", "");
        signupConfirmationPage.putExtra("person_name", "");
        signupConfirmationPage.putExtra("profile_id", userProfileId);
        startActivity(signupConfirmationPage);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void loginSuccess(UserProfileResponse userProfileResponse) {
        if (userProfileResponse != null && userProfileResponse.getResult() != null) {
            if (userProfileResponse.getResult().getFpIds() == null || userProfileResponse.getResult().getFpIds().length == 0)
                return;

            Login_Data_Model response_Data = new Login_Data_Model();
            UserResult result = userProfileResponse.getResult();

            response_Data.accessType = result.getProfileAccessType() + "";
            response_Data.sourceClientId = result.getSourceClientId();
            response_Data.isEnterprise = result.getIsEnterprise() + "";
            response_Data.isRestricted = false + "";
            response_Data.ValidFPIds = new ArrayList<>();
            session.storeFPID(result.getFpIds()[0]);
            response_Data.ValidFPIds.addAll(Arrays.asList(result.getFpIds()));
            processUserProfile(userProfileResponse);
        }
    }

    @Override
    public void authenticationStatus(String value) {
        if (value.equals("Success")) {
            session.setUserLogin(true);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("message", new ArrayList<FloatsMessageModel>());
            dashboardIntent.putExtras(bundle);
            Date date = new Date(System.currentTimeMillis());
            String dateString = date.toString();
//            MixPanelController.setProperties("LastLoginDate", dateString);
//            MixPanelController.setProperties("LoggedIn", "True");
            getFPDetails_retrofit(Login_MainActivity.this, session.getFPID(), Constants.clientId, bus);
        } else {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            if (value.equals("Partial")) {
                session.setUserLogin(true);
                showBusinessProfileCreationStartScreen(session.getUserProfileId());
            }
        }
    }

    private void getFPDetails_retrofit(Activity activity, String fpId, String clientId, Bus bus) {
        new Get_FP_Details_Service(activity, fpId, clientId, bus);
    }

    @Subscribe
    public void post_getFPDetails(Get_FP_Details_Event response) {
        // Close of Progress Bar
//        API_Business_enquiries businessEnquiries = new API_Business_enquiries(null,session);
//        businessEnquiries.getMessages();
        //VISITOR and SUBSCRIBER COUNT API
        fetchData();
        new Handler().postDelayed(() -> Login_MainActivity.this.runOnUiThread(() -> {
            GetVisitorsAndSubscribersCountAsyncTask visit_subcribersCountAsyncTask = new GetVisitorsAndSubscribersCountAsyncTask(Login_MainActivity.this, session);
            visit_subcribersCountAsyncTask.execute();
            startDashboard();
        }), 2000);
    }

    @Subscribe
    public void getResponse(Response response) {
        startDashboard();
    }

    @Subscribe
    public void getError(RetrofitError retrofitError) {
        startDashboard();
    }

    @Subscribe
    public void getErrorMessage(String error) {
        startDashboard();
    }

    private void fetchData() {
        try {
            Util.addBackgroundImages();
            getNfxTokenData();
            registerChat(session.getFPID());
            checkSelfBrandedKyc();
            checkUserAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkSelfBrandedKyc() {
        StoreInterface boostKit = Constants.restAdapterBoostKit.create(StoreInterface.class);
        boostKit.getSelfBrandedKyc(getQuery(), new Callback<PaymentKycDataResponse>() {
            @Override
            public void success(PaymentKycDataResponse data, Response response) {
                if (data.getData() != null && !data.getData().isEmpty())
                    session.setSelfBrandedKycAdd(true);
                else session.setSelfBrandedKycAdd(false);
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("Error KYC api", "message : " + error.getLocalizedMessage());
            }
        });
    }

    private String getQuery() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fpTag", session.getFpTag());
            return jsonObject.toString();
        } catch (JSONException e) {
            return "";
        }
    }

    private void checkUserAccount() {
        StoreInterface getAccountDetail = Constants.restAdapterWithFloat.create(StoreInterface.class);
        getAccountDetail.userAccountDetail(session.getFPID(), Constants.clientId, new Callback<AccountDetailsResponse>() {
            @Override
            public void success(AccountDetailsResponse data, Response response) {
                if (!(data.getResult() != null && data.getResult().getBankAccountDetails() != null))
                    session.setAccountSave(false);
                else session.setAccountSave(true);
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("Error account api", "message : " + error.getLocalizedMessage());
            }
        });
    }

    private void getNfxTokenData() {
        Get_FP_Details_Service.newNfxTokenDetails(this, session.getFPID(), bus);
        Get_FP_Details_Service.autoPull(this, session.getFPID());
    }
}
