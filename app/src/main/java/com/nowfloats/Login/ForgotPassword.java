package com.nowfloats.Login;

/**
 * Created by Prashant on 1/30/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_NULL;
import static com.framework.webengageconstant.EventNameKt.PS_FORGOT_PASSWORD_PAGE_LOAD;
import static com.framework.webengageconstant.EventValueKt.FORGOT_PASSWORD_FAILED;
import static com.framework.webengageconstant.EventValueKt.NULL;

public class ForgotPassword extends Activity {

    EditText userName;
    Button send, back;
    View backLi;
    boolean isUpdatedOnServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forgot_password_screen);
        Methods.isOnline(ForgotPassword.this);
        userName = (EditText) findViewById(R.id.forgot_password_text_box);
        send = (Button) findViewById(R.id.forget_password_send_button);
        back = (Button) findViewById(R.id.forgot_password_top_bar_store_back_button);
        backLi = (View) findViewById(R.id.forgot_password_div_back_click);

        send.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                String enteredText = userName.getText().toString().trim();
                if (enteredText.length() > 1) {
                    sendPasswordToEmail(enteredText);
                } else {
                    //Util.toast("Please enter the username", getApplicationContext());
                }
            }
        });


        back.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                //MixPanelController.track(EventKeys.forgot_password_back, null);
                finish();
            }
        });

        backLi.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // MixPanelController.track(EventKeys.forgot_password_back, null);
                finish();
            }
        });

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

        String url = Constants.NOW_FLOATS_API_URL + "/Discover/v1/floatingpoint/forgotPassword";

        com.android.volley.Response.Listener<String> listener = new com.android.volley.Response.Listener<String>() {

            public void onResponse(String response) {
            }
        };

        com.android.volley.Response.ErrorListener error = new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isUpdatedOnServer) {
                    SuccessDialog();
                    WebEngageController.trackEvent(PS_FORGOT_PASSWORD_PAGE_LOAD, EVENT_LABEL_NULL, NULL);
                } else {
                    Toast.makeText(ForgotPassword.this, getString(R.string.enter_correct_user_name), Toast.LENGTH_SHORT);
                    WebEngageController.trackEvent(PS_FORGOT_PASSWORD_PAGE_LOAD, EVENT_LABEL_NULL, FORGOT_PASSWORD_FAILED);

                }
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<String> req = new JsonRequest<String>(Method.POST, url,
                obj.toString(), listener, error) {

            @Override
            protected com.android.volley.Response<String> parseNetworkResponse(
                    NetworkResponse response) {
                if (response.statusCode == 200) {
                    isUpdatedOnServer = true;

                } else {
                    isUpdatedOnServer = false;
                }
                return null;
            }
        };
        queue.add(req);
    }


    protected void SuccessDialog() {
        // TODO Auto-generated method stub

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.check_your_email));
        AlertDialog alert11 = builder1.create();
        alert11.show();

//        try{
//            final Dialog dialog = new Dialog(ForgotPassword.this);
//
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//            dialog.setContentView(R.layout.basic_dialog_layout);
//
//            TextView text = (TextView) dialog
//                    .findViewById(R.id.textview_heading);
//
//            text.setText("sent_check your email!");
//
//            ImageView image = (ImageView) dialog
//                    .findViewById(R.id.alert_imageView);
//
//            image.setImageResource(R.drawable.forgot_password);
//            image.setVisibility(View.GONE);
//
//            TextView description = (TextView) dialog
//                    .findViewById(R.id.alert_message_text);
//
//            description
//                    .setText("We have sent you an email with\n password details.");
//
//            dialog.show();
//
//            Button cancelButton = (Button) dialog
//                    .findViewById(R.id.cancelButton);
//            cancelButton.setVisibility(View.GONE);
//
//            Button okbutton = (Button) dialog
//                    .findViewById(R.id.okButton);
//            okbutton.setText("OK");
//
//            okbutton.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    dialog.cancel();
//                }
//            });
//        }catch(Exception e){
//            //e.printStackTrace();
//        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
