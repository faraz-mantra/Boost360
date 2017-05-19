package com.nowfloats.Login;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.NavigationDrawer.API.GetVisitorsAndSubscribersCountAsyncTask;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Event;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;
import com.thinksity.Specific;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.richeditor.RichEditor;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Login_MainActivity extends AppCompatActivity implements
        API_Login.API_Login_Interface,View.OnClickListener{
    Bus bus;
    EditText userName, password ;
    CardView loginButton ;

    UserSessionManager session;
    String userNameText,passwordText ;
    ProgressDialog progressDialog ;

    private Toolbar toolbar;
    private TextView forgotPassword;
    boolean isUpdatedOnServer = true;
    private TextView headerText;
    private Intent dashboardIntent;
    private RichEditor mEditor;
    private TextView mPreview;
    private String[] permission = new String[]{Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_PHONE_STATE};
    private final static int READ_MESSAGES_ID=221;
    LinearLayout parent_layout;
    TextView termAndPolicyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        Methods.isOnline(Login_MainActivity.this);

        bus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(getApplicationContext(),Login_MainActivity.this);
        dashboardIntent = new Intent(Login_MainActivity.this, HomeActivity.class);
        parent_layout = (LinearLayout) findViewById(R.id.parent_layout);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        termAndPolicyTextView = (TextView) findViewById(R.id.term_and_policy);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText(getString(R.string.welcome_back));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (EditText) findViewById(R.id.userNameEditText);
        password = (EditText) findViewById(R.id.passwordEditText);

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction()== MotionEvent.ACTION_DOWN) {
                    int tot_width = password.getWidth();
                    float cur_x = arg1.getX();
                    float res = (cur_x / Float.parseFloat(tot_width + "") * (Float.parseFloat("100")));
                    if (res >= 85) {
                        String d = password.getTag().toString();
                        if (d.equals("pwd")) {
                            password.setTag("show");
                            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            password.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.pwd_hide, 0);
                        }else {
                            password.setTag("pwd");
                            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            password.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.pwd_show, 0);
                        }
                    }
                }
                return false;
            }
        });
        forgotPassword = (TextView) findViewById(R.id.forgotPwdTextView);
        forgotPassword.setOnClickListener(this);
        termAndPolicyTextView.setText(Methods.fromHtml("By clicking Login, you agree to our <a href=\""+getString(R.string.settings_tou_url)+"\"><u>Terms and Conditions</u></a>  and that you have read our <a href=\""+getString(R.string.settings_privacy_url)+"\"><u>Privacy Policy</u></a>."));
        termAndPolicyTextView.setMovementMethod(LinkMovementMethod.getInstance());

        ImageView userNameIcon = (ImageView) findViewById(R.id.userNameIcon);
        ImageView passwordIcon = (ImageView) findViewById(R.id.passwordIcon);

        userName.requestFocus();
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(userName.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
        userNameIcon.setColorFilter(whiteLabelFilter);
        passwordIcon.setColorFilter(whiteLabelFilter);

        loginButton = (CardView) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameText = userName.getText().toString().trim();
                passwordText = password.getText().toString().trim();

                if (userNameText.length() > 0 && passwordText.length() > 0) {
                    userName.clearFocus();
                    progressDialog = ProgressDialog.show(Login_MainActivity.this, "", getString(R.string.loading));
                    progressDialog.setCancelable(true);
                    API_Login apiLogin = new API_Login(Login_MainActivity.this,session,bus);
                    apiLogin.authenticate(userName.getText().toString(), password.getText().toString(), Specific.clientId2);
                } else {
                    YoYo.with(Techniques.Shake).playOn(userName);
                    YoYo.with(Techniques.Shake).playOn(password);
                    Methods.showSnackBarNegative(Login_MainActivity.this,getString(R.string.enter_valid_username_or_password));
                }
            }
        });
        //getPermission();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
        if(v.getId()==R.id.forgotPwdTextView   ){
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
    public void authenticationStatus(String value) {
        if(value.equals("Success"))
        {

            Date date = new Date(System.currentTimeMillis());
            String dateString = date.toString();

            MixPanelController.setProperties("LastLoginDate", dateString);
            MixPanelController.setProperties("LoggedIn", "True");

            getFPDetails(Login_MainActivity.this, session.getFPID(), Constants.clientId, bus);
            HomeActivity.registerChat(session.getFPID());
        } else {
            if(progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null ;
            }
        }
    }


    private void getFPDetails(Activity activity, String fpId, String clientId, Bus bus) {
        new Get_FP_Details_Service(activity,fpId,clientId,bus);
    }

    @Subscribe
    public void post_getFPDetails(Get_FP_Details_Event response)
    {
        // Close of Progress Bar

//        API_Business_enquiries businessEnquiries = new API_Business_enquiries(null,session);
//        businessEnquiries.getMessages();


        //VISITOR and SUBSCRIBER COUNT API
        GetVisitorsAndSubscribersCountAsyncTask visit_subcribersCountAsyncTask = new GetVisitorsAndSubscribersCountAsyncTask(Login_MainActivity.this,session);
        visit_subcribersCountAsyncTask.execute();

        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null ;
        }

        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dashboardIntent);
        finish();
    }

    @Override
    public void authenticationFailure(String value) {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null ;
        }
    }


    @Subscribe
    public void getMessages(ArrayList<FloatsMessageModel> floats){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("message",floats);
        dashboardIntent.putExtras(bundle);
        authenticationStatus("Success");
    }

    protected void sendPasswordToEmail(String enteredText) {
        // TODO Auto-generated method stub
        JSONObject obj = new JSONObject();
        try {
            obj.put("clientId", Constants.clientId);
            obj.put("fpKey", enteredText);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = Constants.NOW_FLOATS_API_URL+"/Discover/v1/floatingpoint/forgotPassword";

        com.android.volley.Response.Listener<String> listener = new com.android.volley.Response.Listener<String>() {
            public void onResponse(String response) {
            }
        };

        com.android.volley.Response.ErrorListener error = new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (!isUpdatedOnServer) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Methods.showSnackBarNegative(Login_MainActivity.this,getString(R.string.enter_correct_user_name));
                        }
                    });
                }
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<String> req = new JsonRequest<String>(Request.Method.POST, url,
                obj.toString(), listener, error) {
            @Override
            protected com.android.volley.Response<String> parseNetworkResponse(
                    NetworkResponse response) {
                if (response.statusCode == 200) {
                    isUpdatedOnServer = true;
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                Methods.showSnackBarPositive(Login_MainActivity.this,"\n" +
//                                        "We’ve sent you an email with your login details");
                                new MaterialDialog.Builder(Login_MainActivity.this)
                                        .title(getString(R.string.check_your_email))
                                        .content(getString(R.string.we_sent_email_with_password))
                                        .positiveText(getString(R.string.ok))
                                        .show();
                            }
                        });
                    }catch (Exception e){
                        e.getCause();
                    }
                } else {
                    isUpdatedOnServer = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Methods.showSnackBarNegative(Login_MainActivity.this,getString(R.string.enter_correct_user_name));
                        }
                    });
                }
                return null;
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

        if(id==android.R.id.home){
            //NavUtils.navigateUpFromSameTask(this);

//
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }
    private void getPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)== PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED){

            // start the service to send data to firebase
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            // if user deny the permissions
           /* if(shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)||
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){

                Snackbar.make(parent_layout, com.nfx.leadmessages.R.string.required_permission_to_show, Snackbar.LENGTH_INDEFINITE)
                        .setAction(com.nfx.leadmessages.R.string.enable, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                startActivity(intent);
                            }
                        })  // action text on the right side of snackbar
                        .setActionTextColor(ContextCompat.getColor(this,android.R.color.holo_green_light))
                        .show();
            }
            else{*/
                requestPermissions(permission,READ_MESSAGES_ID);
           // }

        }

    }

    // this method called when user react on permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch(requestCode){
           case READ_MESSAGES_ID:
               //getPermission();
               break;
           default:
               break;
       }
    }
}