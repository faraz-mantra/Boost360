package com.romeo.mylibrary.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.instamojo.android.Instamojo;
import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.callbacks.OrderRequestCallBack;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.Order;
import com.instamojo.android.network.Request;
import com.romeo.mylibrary.BuildConfig;
import com.romeo.mylibrary.Constants;
import com.romeo.mylibrary.Models.OrderDataModel;
import com.romeo.mylibrary.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InstaMojoMainActivity extends AppCompatActivity {

    private static final HashMap<String, String> env_options = new HashMap<>();

    static {
        env_options.put("Test", "https://test.instamojo.com/");
        env_options.put("Production", "https://api.instamojo.com/");
    }

    private TextView tvUserName;
    private TextView tvBusinessName;
    private TextView tvEmail;
    private TextView tvPrice;
    private TextView tvExpires;
    private TextView tvPhoneNumber;
    private ImageView ivPackageType;
    private Button btnProceedToPay;
    private ProgressDialog pd;

    private OrderDataModel mOrderData;
    private String mCurrentEnv = null, mAccessToken = null, mFinalTransactionID = null, mWebhook = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Instamojo.initialize(getApplicationContext());

        setContentView(R.layout.activity_code_mojo_main);

        mOrderData = getIntent().getParcelableExtra(Constants.PARCEL_IDENTIFIER);
        mAccessToken = getIntent().getStringExtra(Constants.ACCESS_TOKEN_IDENTIFIER);
        mWebhook = getIntent().getStringExtra(Constants.WEB_HOOK_IDENTIFIER);
        mFinalTransactionID = getIntent().getStringExtra(Constants.PAYMENT_REQUEST_IDENTIFIER);

        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvBusinessName = (TextView) findViewById(R.id.tv_business_name);
        tvEmail = (TextView) findViewById(R.id.tv_payer_email);
        tvPrice = (TextView) findViewById(R.id.tv_package_price);
        tvExpires = (TextView) findViewById(R.id.tv_expiry);
        tvPhoneNumber = (TextView) findViewById(R.id.tv_payer_ph_no);

        ivPackageType = (ImageView) findViewById(R.id.iv_package);
        if (mOrderData.getExpires().toLowerCase().contains("lite")) {
            ivPackageType.setImageDrawable(getResources().getDrawable(R.drawable.boost_lite_logo));
        } else if (mOrderData.getExpires().toLowerCase().contains("pro")) {
            ivPackageType.setImageDrawable(getResources().getDrawable(R.drawable.boost_pro_logo));
        } else if (mOrderData.getExpires().toLowerCase().contains("wildfire")) {
            ivPackageType.setImageDrawable(getResources().getDrawable(R.drawable.wildfire));
        }

        btnProceedToPay = (Button) findViewById(R.id.btn_proceed_to_pay);

        tvUserName.setText(" " + mOrderData.getUsername());
        tvBusinessName.setText(" " + mOrderData.getBusinessName());
        tvEmail.setText(" " + mOrderData.getEmail());
        tvPrice.setText(" " + mOrderData.getCurrency() + " " + NumberFormat.getIntegerInstance(Locale.US).format(Long.valueOf(mOrderData.getPrice())));
        tvExpires.setText(" " + mOrderData.getExpires());
        tvExpires.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvExpires.setSingleLine(true);
        tvExpires.setMarqueeRepeatLimit(5);
        tvExpires.setSelected(true);
        tvPhoneNumber.setText(" " + mOrderData.getPhNo());

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.please_wait_));
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        btnProceedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchTokenAndTransactionId();
            }
        });
        if (BuildConfig.DEBUG) {
            mCurrentEnv = "Test";
        } else {
            mCurrentEnv = "Production";
        }
        Instamojo.initialize(getApplicationContext());
        Instamojo.setBaseUrl(env_options.get(mCurrentEnv));
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchTokenAndTransactionId() {
        /*if (!pd.isShowing()) {
            pd.show();
        }

        OkHttpClient client = new OkHttpClient();
        HttpUrl url = getHttpURLBuilder()
                .addPathSegment("create")
                .build();
        RequestBody body = new FormBody.Builder()
                .add("env", mCurrentEnv.toLowerCase())
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }

                        showToast("Failed to fetch the Order Tokens");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString;
                String errorMessage = null;
                String transactionID = null;
                responseString = response.body().string();
                response.body().close();
                try {
                    JSONObject responseObject = new JSONObject(responseString);
                    if (responseObject.has("error")) {
                        errorMessage = responseObject.getString("error");
                    } else {
                        mAccessToken = responseObject.getString("access_token");
                        transactionID = responseObject.getString("transaction_id");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMessage = "Failed to fetch Order tokens";
                }

                final String finalErrorMessage = errorMessage;
                final String finalTransactionID = transactionID;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }

                        if (finalErrorMessage != null) {
                            showToast(finalErrorMessage);
                            return;
                        }

                        createOrder(mAccessToken, finalTransactionID);
                    }
                });


            }
        });*/
        createOrder(mAccessToken, mFinalTransactionID);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void createOrder(String accessToken, String finalTransactionID) {
        Order order = new Order(accessToken,
                finalTransactionID,
                mOrderData.getUsername().trim(),
                mOrderData.getEmail().trim(),
                mOrderData.getPhNo().trim(),
                mOrderData.getPrice().trim(),
                mOrderData.getReason().trim());
        order.setWebhook(mWebhook);

        if (!order.isValidName()) {
            showToast("Buyer name is invalid");
            return;
        }

        if (!order.isValidEmail()) {
            showToast("Buyer email is invalid");
            return;
        }

        if (!order.isValidPhone()) {
            showToast("Buyer phone is invalid");
            return;
        }

        if (!order.isValidAmount()) {
            showToast("Amount is invalid or has more than two decimal places");
            return;
        }

        if (!order.isValidDescription()) {
            showToast("Description is invalid");
            return;
        }

        if (!order.isValidTransactionID()) {
            showToast("Transaction is Invalid");
            return;
        }

        if (!order.isValidRedirectURL()) {
            showToast("Redirection URL is invalid");
            return;
        }

        pd.show();
        Request request = new Request(order, new OrderRequestCallBack() {
            @Override
            public void onFinish(final Order order, final Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        if (error != null) {
                            if (error instanceof Errors.ConnectionError) {
                                showToast("No internet connection");
                            } else if (error instanceof Errors.ServerError) {
                                showToast("Server Error. Try again");
                            } else if (error instanceof Errors.AuthenticationError) {
                                showToast("Access token is invalid or expired. Please Update the token!!");
                            } else if (error instanceof Errors.ValidationError) {
                                // Cast object to validation to pinpoint the issue
                                Errors.ValidationError validationError = (Errors.ValidationError) error;

                                if (!validationError.isValidTransactionID()) {
                                    showToast("Transaction ID is not Unique");
                                    return;
                                }

                                if (!validationError.isValidRedirectURL()) {
                                    showToast("Redirect url is invalid");
                                    return;
                                }

                                if (!validationError.isValidPhone()) {
                                    showToast("Buyer's Phone Number is invalid/empty");
                                    return;
                                }

                                if (!validationError.isValidEmail()) {
                                    showToast("Buyer's Email is invalid/empty");
                                    return;
                                }

                                if (!validationError.isValidAmount()) {
                                    showToast("Amount is either less than Rs.9 or has more than two decimal places");
                                    return;
                                }

                                if (!validationError.isValidName()) {
                                    showToast("Buyer's Name is required");
                                    return;
                                }
                            } else {
                                showToast(error.getMessage());
                            }
                            return;
                        }

                        startPreCreatedUI(order);
                        //startCustomUI(order);
                        //TODO: Create custom Activity for Entering Payment Details
                    }
                });
            }
        });

        request.execute();

    }

    private void startPreCreatedUI(Order order) {
        /*order.setWalletOptions(null);
        order.setUpiOptions(null);*/
        Intent intent = new Intent(getBaseContext(), PaymentDetailsActivity.class);
        intent.putExtra(com.instamojo.android.helpers.Constants.ORDER, order);
        startActivityForResult(intent, com.instamojo.android.helpers.Constants.REQUEST_CODE);
    }

    private void startCustomUI(Order order) {
        //Custom UI Implementation
        /*Intent intent = new Intent(getBaseContext(), CustomPaymentActivity.class);
        intent.putExtra(com.instamojo.android.helpers.Constants.ORDER, order);
        startActivityForResult(intent, com.instamojo.android.helpers.Constants.REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
    }

    private HttpUrl.Builder getHttpURLBuilder() {
        return new HttpUrl.Builder()
                .scheme(Constants.PROTOCOL_SCHEME)
                .host(Constants.DOMAIN_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == com.instamojo.android.helpers.Constants.REQUEST_CODE && data != null) {
            String orderID =
                    data.getStringExtra(com.instamojo.android.helpers.Constants.ORDER_ID);
            String transactionID =
                    data.getStringExtra(com.instamojo.android.helpers.Constants.TRANSACTION_ID);
            String paymentID =
                    data.getStringExtra(com.instamojo.android.helpers.Constants.PAYMENT_ID);

            // Check transactionID, orderID, and orderID for null 
            // before using them to check the Payment status.
            if (orderID != null && transactionID != null && paymentID != null) {
                checkPaymentStatus(transactionID);
            } else {
                showToast("Oops!! Payment was cancelled");
                Intent resultData = new Intent();
                resultData.putExtra(Constants.RESULT_SUCCESS_KEY, false);
                resultData.putExtra(Constants.RESULT_STATUS, "Error");
                resultData.putExtra(Constants.ERROR_MESSAGE, "Payment Error");
                if (paymentID != null) {
                    resultData.putExtra(Constants.PAYMENT_ID, paymentID);
                } else {
                    resultData.putExtra(Constants.PAYMENT_ID, "0");
                }
                if (transactionID != null) {
                    resultData.putExtra(Constants.TRANSACTION_ID, transactionID);
                } else {
                    resultData.putExtra(Constants.TRANSACTION_ID, "0");
                }
                resultData.putExtra(Constants.FINAL_AMOUNT, "0");
                setResult(RESULT_OK, resultData);
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        }
    }

    private void checkPaymentStatus(final String transactionID) {

        if (mAccessToken == null || transactionID == null) {
            return;
        }

        if (pd != null && !pd.isShowing()) {
            pd.show();
        }

        showToast("checking transaction status");
        OkHttpClient client = new OkHttpClient();
        HttpUrl url = getHttpURLBuilder()
                .addPathSegment("status")
                .addQueryParameter("transaction_id", transactionID)
                .addQueryParameter("env", mCurrentEnv.toLowerCase())
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                        showToast("Failed to fetch the Transaction status");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                response.body().close();
                String status = null;
                String paymentID = null;
                String amount = null;
                String errorMessage = null;

                try {
                    JSONObject responseObject = new JSONObject(responseString);
                    JSONObject payment = responseObject.getJSONArray("payments").getJSONObject(0);
                    status = payment.getString("status");
                    paymentID = payment.getString("id");
                    amount = responseObject.getString("amount");

                } catch (JSONException e) {
                    errorMessage = "Failed to fetch the Transaction status";
                }

                final String finalStatus = status;
                final String finalErrorMessage = errorMessage;
                final String finalPaymentID = paymentID;
                final String finalAmount = amount;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                        if (finalStatus == null) {
                            showToast(finalErrorMessage);
                            Intent resultData = new Intent();
                            resultData.putExtra(Constants.RESULT_SUCCESS_KEY, false);
                            resultData.putExtra(Constants.RESULT_STATUS, "Failure");
                            resultData.putExtra(Constants.ERROR_MESSAGE, finalErrorMessage);
                            if (finalPaymentID != null) {
                                resultData.putExtra(Constants.PAYMENT_ID, finalPaymentID);
                            } else {
                                resultData.putExtra(Constants.PAYMENT_ID, "0");
                            }
                            if (transactionID != null) {
                                resultData.putExtra(Constants.TRANSACTION_ID, transactionID);
                            } else {
                                resultData.putExtra(Constants.TRANSACTION_ID, "0");
                            }
                            if (finalAmount != null) {
                                resultData.putExtra(Constants.FINAL_AMOUNT, finalAmount);
                            } else {
                                resultData.putExtra(Constants.FINAL_AMOUNT, "0");
                            }
                            setResult(RESULT_OK, resultData);
                            finish();
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            return;
                        }

                        if (!finalStatus.equalsIgnoreCase("successful")) {
                            showToast("Transaction still pending");
                            Intent resultData = new Intent();
                            resultData.putExtra(Constants.RESULT_SUCCESS_KEY, false);
                            resultData.putExtra(Constants.RESULT_STATUS, "Pending");
                            resultData.putExtra(Constants.ERROR_MESSAGE, "Payment Pending");
                            if (finalPaymentID != null) {
                                resultData.putExtra(Constants.PAYMENT_ID, finalPaymentID);
                            } else {
                                resultData.putExtra(Constants.PAYMENT_ID, "0");
                            }
                            if (transactionID != null) {
                                resultData.putExtra(Constants.TRANSACTION_ID, transactionID);
                            } else {
                                resultData.putExtra(Constants.TRANSACTION_ID, "0");
                            }
                            if (finalAmount != null) {
                                resultData.putExtra(Constants.FINAL_AMOUNT, finalAmount);
                            } else {
                                resultData.putExtra(Constants.FINAL_AMOUNT, "0");
                            }
                            setResult(RESULT_OK, resultData);
                            finish();
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            return;
                        }

                        Intent resultData = new Intent();
                        resultData.putExtra(Constants.RESULT_SUCCESS_KEY, true);
                        resultData.putExtra(Constants.RESULT_STATUS, "Success");
                        resultData.putExtra(Constants.ERROR_MESSAGE, "Payment Successful");
                        if (finalPaymentID != null) {
                            resultData.putExtra(Constants.PAYMENT_ID, finalPaymentID);
                        } else {
                            resultData.putExtra(Constants.PAYMENT_ID, "0");
                        }
                        if (transactionID != null) {
                            resultData.putExtra(Constants.TRANSACTION_ID, transactionID);
                        } else {
                            resultData.putExtra(Constants.TRANSACTION_ID, "0");
                        }
                        if (finalAmount != null) {
                            resultData.putExtra(Constants.FINAL_AMOUNT, finalAmount);
                        } else {
                            resultData.putExtra(Constants.FINAL_AMOUNT, "0");
                        }
                        setResult(RESULT_OK, resultData);
                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        //TODO: Write Code for Payment refund
                        //refundTheAmount(finalPaymentID, finalAmount);
                    }
                });
            }
        });
    }

    private void refundTheAmount(String finalPaymentID, String finalAmount) {
        if (mAccessToken == null || finalPaymentID == null || finalAmount == null) {
            return;
        }

        if (pd != null && !pd.isShowing()) {
            pd.show();
        }

        showToast("Initiating a refund for - " + finalAmount);
        OkHttpClient client = new OkHttpClient();
        HttpUrl url = getHttpURLBuilder()
                .addPathSegment("refund")
                .build();

        RequestBody body = new FormBody.Builder()
                .add("env", mCurrentEnv.toLowerCase())
                .add("payment_id", finalPaymentID)
                .add("amount", finalAmount)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                        showToast("Failed to Initiate a refund");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                        String message;

                        if (response.isSuccessful()) {
                            message = "Refund initiated successfully";
                        } else {
                            message = "Failed to Initiate a refund";
                        }

                        showToast(message);
                    }
                });
            }
        });
    }
}
