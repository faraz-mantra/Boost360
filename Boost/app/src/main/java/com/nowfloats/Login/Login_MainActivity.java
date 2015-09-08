package com.nowfloats.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.gc.materialdesign.views.Button;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.NavigationDrawer.API.GetVisitorsAndSubscribersCountAsyncTask;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.PreSignUp.PreSignUp_MainActivity;
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

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.richeditor.RichEditor;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Login_MainActivity extends AppCompatActivity implements
        API_Login.API_Login_Interface,View.OnClickListener{
    Bus bus;
    EditText userName, password ;
    Button loginButton ;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        Methods.isOnline(Login_MainActivity.this);

        bus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(getApplicationContext(),Login_MainActivity.this);
        dashboardIntent = new Intent(Login_MainActivity.this, HomeActivity.class);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText("Welcome Back");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (EditText) findViewById(R.id.userNameEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        forgotPassword = (TextView) findViewById(R.id.forgotPwdTextView);
        forgotPassword.setOnClickListener(this);

        ImageView userNameIcon = (ImageView) findViewById(R.id.userNameIcon);
        ImageView passwordIcon = (ImageView) findViewById(R.id.passwordIcon);

        userName.requestFocus();
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(userName.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
        userNameIcon.setColorFilter(whiteLabelFilter);
        passwordIcon.setColorFilter(whiteLabelFilter);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameText = userName.getText().toString();
                passwordText = password.getText().toString();

                if (userNameText.length() > 0 && passwordText.length() > 0) {
                    progressDialog = ProgressDialog.show(Login_MainActivity.this, "", "Loading...");
                    progressDialog.setCancelable(true);
                    API_Login apiLogin = new API_Login(Login_MainActivity.this,session,bus);
                    apiLogin.authenticate(userName.getText().toString(), password.getText().toString(), Specific.clientId2);
                } else {
                    YoYo.with(Techniques.Shake).playOn(userName);
                    YoYo.with(Techniques.Shake).playOn(password);
                    Methods.showSnackBarNegative(Login_MainActivity.this,"Enter valid username/password");
                }
            }
        });


       /* //////////////////////////////////
        mEditor = (RichEditor) findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setPlaceholder("Insert text here...");

        mPreview = (TextView) findViewById(R.id.preview);
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                mPreview.setText(text);
            }
        });

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });


        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.WHITE : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        ///////////////////////////////////
*/
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
                    .title("Forgot Password")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("Enter Username", null, new MaterialDialog.InputCallback() {
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
                                     Methods.showSnackBarNegative(Login_MainActivity.this, "Enter username !");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .positiveText("OK")
                    .negativeText("Cancel")
                    .positiveColorRes(R.color.primaryColor)
                    .negativeColorRes(R.color.primaryColor)
                    .show();
        }
    }

    @Override
    public void authenticationStatus(String value) {
        if(value.equals("Success"))
        {
            if(progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null ;
            }

            Date date = new Date(System.currentTimeMillis());
            String dateString = date.toString();

            MixPanelController.setProperties("LastLoginDate", dateString);
            MixPanelController.setProperties("LoggedIn", "True");

            getFPDetails(Login_MainActivity.this, session.getFPID(), Constants.clientId, bus);
            HomeActivity.registerChat(session.getFPID(),Constants.gcmRegistrationID);
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
        if(progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null ;
            }

//        API_Business_enquiries businessEnquiries = new API_Business_enquiries(null,session);
//        businessEnquiries.getMessages();


        //VISITOR and SUBSCRIBER COUNT API
        GetVisitorsAndSubscribersCountAsyncTask visit_subcribersCountAsyncTask = new GetVisitorsAndSubscribersCountAsyncTask(Login_MainActivity.this,session);
        visit_subcribersCountAsyncTask.execute();

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
                            Methods.showSnackBarNegative(Login_MainActivity.this,"Your username is incorrect, please try again");
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
                                        .title("Check your email !")
                                        .content("We have sent you an email with password details")
                                        .positiveText("OK")
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
                            Methods.showSnackBarNegative(Login_MainActivity.this,"Your username is incorrect, please try again");
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
        Intent signup = new Intent(Login_MainActivity.this, PreSignUp_MainActivity.class);
        startActivity(signup);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        // Otherwise defer to system default behavior.
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==android.R.id.home){
            //NavUtils.navigateUpFromSameTask(this);
            Intent signup = new Intent(Login_MainActivity.this, PreSignUp_MainActivity.class);
            startActivity(signup);
//            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }



        return super.onOptionsItemSelected(item);
    }


}
