package com.nowfloats.Analytics_Screen;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.Analytics_Screen.API.NfxFacebbokAnalytics;
import com.nowfloats.Analytics_Screen.Fragments.FacebookLoginFragment;
import com.nowfloats.Analytics_Screen.Fragments.ProcessFacebookDataFragment;
import com.nowfloats.Analytics_Screen.Fragments.PostFacebookUpdateFragment;
import com.nowfloats.Analytics_Screen.Fragments.SocialMediaConnectPromptFragment;
import com.nowfloats.Analytics_Screen.model.GetFacebookAnalyticsData;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by Abhi on 12/1/2016.
 */

public class SocialAnalytics extends AppCompatActivity implements FacebookLoginFragment.OpenNextScreen, View.OnClickListener {

    private int facebookStatus = 0;
    private final static int FETCH_DATA = 20,POST_UPDATE = 10,LOGIN_FACEBOOK = 30;
    WebView web;
    ProgressDialog progress;
    LinearLayout layout;
    Toolbar toolbar;
    public static final String FACEBOOK="facebook", QUIKR = "quikr";

    String[] socialArray;
    int[] images = new int[]{R.drawable.facebook_round,R.drawable.quikr};
    UserSessionManager session;
    ImageView spinner;
    ImageView toolbarImage;
    TextView title;
    PopupWindow popup = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_analytics);
        session = new UserSessionManager(getApplicationContext(), this);
        socialArray=getResources().getStringArray(R.array.social_array);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.title);

        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            title.setText("Social Analytics");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbarImage = (ImageView) findViewById(R.id.social_img);
        web = (WebView) findViewById(R.id.webview);
        layout = (LinearLayout) findViewById(R.id.linearlayout);
        spinner = (ImageView) findViewById(R.id.toolbar_spinner);
       // spinner.setOnItemSelectedListener(this);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,socialArray);
        //SpinnerAdapter adapter = new SpinnerAdapter(this,images);
        //spinner.setAdapter(adapter);

        //TODO: Canbrand: Update these lines to check if any social media channel is connected
        //                 For the time being only FB-flag is being checked, it needs to be changed
        Intent intent = getIntent();
        facebookStatus = intent.getIntExtra("GetStatus",0);

        progress=new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.please_wait));
        progress.setCanceledOnTouchOutside(false);

        if(facebookStatus == 1 ){
            checkForMessage(FACEBOOK);
        }else{
            addFragment(LOGIN_FACEBOOK,FACEBOOK);
        }
        spinner.setVisibility(View.GONE);
//        if (!Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {
//            spinner.setVisibility(View.GONE);
//        }else {
//            String[] quikrArray = getResources().getStringArray(R.array.quikr_widget);
//            //Log.v("ggg",quikrArray[3]+session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).toLowerCase());
//            if ("91".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE))) {
//                for (String category : quikrArray) {
//                    if (category.contains(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).toLowerCase())) {
//                        spinner.setVisibility(View.VISIBLE);
//                        break;
//                    }
//                }
//            }
//            spinner.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    initiatePopupWindow(spinner);
//                }
//            });
//        }

    }
    private void initiatePopupWindow(View image) {

        try {
            popup = new PopupWindow(this);
            View layout = LayoutInflater.from(this).inflate(R.layout.pop_up_window, null);
            popup.setContentView(layout);

            popup.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popup.setOutsideTouchable(true);
            popup.setFocusable(true);
            popup.showAsDropDown(image,-10,0);
            layout.findViewById(R.id.facebook).setOnClickListener(this);
            layout.findViewById(R.id.quikr).setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void showDialog(){
        if(!isFinishing())
            progress.show();
    }
    private void hideDialog(){
        if(progress.isShowing())
            progress.hide();
    }

    private void checkForMessage(final String mType){
        //Log.v("ggg","checkformessage");
        try {
            showDialog();
            NfxFacebbokAnalytics.nfxFacebookApis facebookApis = NfxFacebbokAnalytics.getAdapter();
            facebookApis.nfxFetchFacebookData(session.getFPID(), mType, new Callback<GetFacebookAnalyticsData>() {
                @Override
                public void success(GetFacebookAnalyticsData facebookAnalyticsData, Response response) {
                    if (isFinishing()){
                        return;
                    }
                    hideDialog();
                    if (facebookAnalyticsData == null) {
                        return;
                    }
                    String status = facebookAnalyticsData.getStatus();
                    String message = facebookAnalyticsData.getMessage();
                    if (message != null && message.equalsIgnoreCase("success")) {
                        startWebView(mType);
                        setImpressionValue(facebookAnalyticsData.getData());
                    } else if (status != null && message != null) {
                        addFragment(Integer.parseInt(status), mType);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (isFinishing()){
                        return;
                    }
                    hideDialog();
                    Toast.makeText(SocialAnalytics.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    //Log.v("ggg", error + "");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setImpressionValue(List<GetFacebookAnalyticsData.Datum> list) {
        for (GetFacebookAnalyticsData.Datum data :list) {
            if("facebook".equalsIgnoreCase(data.getIdentifier())){
                session.storeFacebookImpressions(String.valueOf(data.getValues().getPostImpressions()));
                break;
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void startWebView(String mType) {
        //Log.v("ggg","webview");
        layout.setVisibility(View.GONE);
        web.setVisibility(View.VISIBLE);
        showDialog();
        web.setWebChromeClient(new SocialAnalytics.MyWebViewClient());
        web.getSettings().setJavaScriptEnabled(true);
        Map<String,String> mp=new HashMap<>();
        mp.put("key","78234i249123102398");
        mp.put("pwd","JYUYTJH*(*&BKJ787686876bbbhl)");
        web.loadUrl(makeUrl(mType,session.getFPID()),mp);
    }


    @Override
    public void onNextScreen() {
        checkForMessage(FACEBOOK);
    }

    @Override
    public void onClick(View v) {
        int pos = 0;
        if(popup !=null){
            popup.dismiss();
        }
        switch(v.getId()) {
            case R.id.facebook:
                pos = 0;
                if (facebookStatus == 1) {
                    checkForMessage(FACEBOOK);
                }else {
                    addFragment(LOGIN_FACEBOOK, FACEBOOK);
                }
                break;
            case R.id.quikr:
                pos = 1;
                MixPanelController.track(Key_Preferences.SHOW_QUIKR_ANALYTICS,null);
                checkForMessage(QUIKR);
                break;
            default:
                break;
        }
        toolbarImage.setImageResource(images[pos]);
    }

    private class MyWebViewClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(newProgress==100){
                hideDialog();
            }
            super.onProgressChanged(view, newProgress);
        }
    }
    private void addFragment(int i,String mType){
        Bundle b = new Bundle();
        b.putString("mType",mType);
        if(layout.getVisibility() != View.VISIBLE) {
            layout.setVisibility(View.VISIBLE);
            web.setVisibility(View.GONE);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment frag=null;
        switch(i){
            case FETCH_DATA:

                frag = ProcessFacebookDataFragment.getInstance(b);
                transaction.replace(R.id.linearlayout,frag,"FetchFacebookData").commit();
                break;
            case POST_UPDATE:

                frag = PostFacebookUpdateFragment.getInstance(b);
                transaction.replace(R.id.linearlayout,frag,"PostFacebookUpdate").commit();
                break;
            case LOGIN_FACEBOOK:
                frag = manager.findFragmentByTag("SocialMediaConnectPromptFragment");
                if(frag == null)
                    frag = SocialMediaConnectPromptFragment.getInstance();

                transaction.replace(R.id.linearlayout,frag,"SocialMediaConnectPromptFragment").commit();
                break;
        }
    }



    private String makeUrl(String mType, String fpId){
        return Constants.NFX_WITH_NOWFLOATS+"/dataexchange/v1/fetch/analytics?" +
                "identifier="+mType+"&nowfloats_id="+fpId;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==android.R.id.home ){
            BoostLog.d("Back", "Back Pressed");
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
