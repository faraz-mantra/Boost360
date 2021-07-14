package com.nowfloats.Store;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//import com.romeo.mylibrary.Constants;
import com.thinksity.R;

public class PaddleCheckoutActivity extends AppCompatActivity {


    private final int PADDLE_REQUEST_CODE = 3;
    private final String REDIRECT_URL = "https://hello.nowfloats.com";
    WebView wvPaddlePg;
    ProgressDialog dialog;
    private String mPaymentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paddle_checkout);

        dialog = ProgressDialog.show(this, "", getString(R.string.please_wait), true);
        dialog.setCanceledOnTouchOutside(false);

        wvPaddlePg = findViewById(R.id.wv_paddle_pg);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("paymentUrl")) {
            mPaymentUrl = bundle.getString("paymentUrl");
        }

        wvPaddlePg.getSettings().setJavaScriptEnabled(true);
        wvPaddlePg.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        wvPaddlePg.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (dialog != null && !isFinishing() && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                setResult(null);
                //show error
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //show error
                setResult(null);
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (dialog != null && !isFinishing() && !dialog.isShowing()) {
                    dialog.show();
                }
                //check state code is same
                if (request.getUrl().toString().startsWith(REDIRECT_URL)) {
                    handleUri(request.getUrl());
                    return super.shouldOverrideUrlLoading(view, request);
                } else {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (dialog != null && !isFinishing() && !dialog.isShowing()) {
                    dialog.show();
                }
                if (url != null && url.startsWith(REDIRECT_URL)) {
                    handleUri(Uri.parse(url));
                    return super.shouldOverrideUrlLoading(view, url);
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

        });
        wvPaddlePg.loadUrl(mPaymentUrl);

    }

    private void setResult(String paymentReqId) {
        if (TextUtils.isEmpty(paymentReqId)) {
            setResult(RESULT_OK, null);
            finish();
        }
        Intent resultData = new Intent();
//        resultData.putExtra(Constants.RESULT_SUCCESS_KEY, true);
//        resultData.putExtra(Constants.RESULT_STATUS, "Success");
//        resultData.putExtra(Constants.ERROR_MESSAGE, "Payment Successful");
//        resultData.putExtra(Constants.PAYMENT_ID, "0");
//        resultData.putExtra(Constants.TRANSACTION_ID, paymentReqId);
//        resultData.putExtra(Constants.FINAL_AMOUNT, "0");
        setResult(RESULT_OK, resultData);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void handleUri(Uri loadUri) {
        String reqId = loadUri.getQueryParameter("payment_request_id");
        setResult(reqId);
    }
}
