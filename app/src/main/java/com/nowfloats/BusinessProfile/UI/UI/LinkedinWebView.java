package com.nowfloats.BusinessProfile.UI.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.thinksity.R;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;


/**
 * Created by Admin on 20-06-2017.
 */

public class LinkedinWebView extends AppCompatActivity {

    private static final String LINKEDIN_END_POINT = "https://www.linkedin.com";
    public static final String AUTH_URL = LINKEDIN_END_POINT+"/oauth/v2/authorization";
    public static final String TOKEN_URL = "/oauth/v2/accessToken";
    public static final String CLIENT_ID = "81pbf5p2a5eezm";
    public static final String SECRETS = "v3iMNbz9quAYbIlC";
    public static final int LINKEDIN_CODE = 101;
    public static final String REDIRECT_URL = "https://fabd21b6.ngrok.io/redirect_url";
    WebView webView;
    ProgressDialog dialog;
    TextView text;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkedin_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        text = (TextView) findViewById(R.id.text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dialog = ProgressDialog.show(this,"",getString(R.string.please_wait),true);
        dialog.setCanceledOnTouchOutside(false);
        webView = (WebView) findViewById(R.id.web_view);
        CookieManager cookieManager = CookieManager.getInstance();
        if(Build.VERSION.SDK_INT >= 21) {
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean aBoolean) {

                }
            });
        }
        else{
            cookieManager.removeAllCookie();
        }

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(dialog !=null && !isFinishing() && dialog.isShowing()){
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
                if(dialog !=null && !isFinishing() && !dialog.isShowing()){
                    dialog.show();
                }
                //check state code is same
                if(request.getUrl().toString().startsWith(REDIRECT_URL)){
                    handleUri(request.getUrl());
                    //load our html page
                    return super.shouldOverrideUrlLoading(view, request);
                }else {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(dialog !=null && !isFinishing() && !dialog.isShowing()){
                    dialog.show();
                }
                if(url != null && url.startsWith(REDIRECT_URL)) {
                    handleUri(Uri.parse(url));
                    return super.shouldOverrideUrlLoading(view, url);
                }else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });
        webView.loadUrl(getAuthorizationUrl());
    }

    private void setResult(String token){
        Intent i =  new Intent(this,Social_Sharing_Activity.class);
        i.putExtra("token",token);
        setResult(LINKEDIN_CODE,i);
    }
    public String getAuthorizationUrl(){
        return AUTH_URL+"?response_type=code"+"&client_id="+CLIENT_ID+"&redirect_uri="+
                REDIRECT_URL+"&state=1234"+"&scope=r_basicprofile";

    }

    private void handleUri(Uri loadUri){
        if(loadUri.getBooleanQueryParameter("code",false)){
            callAccessTokenApi(loadUri.getQueryParameter("code"));
            text.setVisibility(View.VISIBLE);
            text.setText(R.string.calling_access_token_api);
            //show html page
        }else{
            setResult(null);
        }
    }
    public void callAccessTokenApi(String code){
        if(dialog !=null && !isFinishing() && !dialog.isShowing()){
            dialog.show();
        }
       RestAdapter adapter = new RestAdapter.Builder().setEndpoint(LINKEDIN_END_POINT)
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setLog(new AndroidLog("ggg"))
        .build();
        HashMap<String,String> map = new HashMap<>();
        map.put("grant_type","authorization_code");
        map.put("code",code);
        map.put("redirect_uri",REDIRECT_URL);
        map.put("client_id",CLIENT_ID);
        map.put("client_secret",SECRETS);
        adapter.create(AccessTokenApi.class).getAccessToken(map, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                text.setText(R.string.successfully_api_response);
                if(dialog !=null && !isFinishing() && dialog.isShowing()){
                    dialog.dismiss();
                }
                String accessToken = null;
                if(jsonObject != null){
                    accessToken = jsonObject.get("access_token").getAsString();
                }
                setResult(accessToken);
            }

            @Override
            public void failure(RetrofitError error) {
                if(dialog !=null && !isFinishing() && dialog.isShowing()){
                    dialog.dismiss();
                }
                text.setText("failed");
                setResult(null);
            }
        });
    }

    interface AccessTokenApi{

        @FormUrlEncoded
        @POST(TOKEN_URL)
        void getAccessToken(@FieldMap Map map, Callback<JsonObject> response);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
