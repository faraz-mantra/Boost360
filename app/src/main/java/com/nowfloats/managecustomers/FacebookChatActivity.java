package com.nowfloats.managecustomers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.NfxGetTokensResponse;
import com.nowfloats.BusinessProfile.UI.UI.Social_Sharing_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.managecustomers.adapters.FacebookChatAdapter;
import com.nowfloats.managecustomers.apis.FacebookChatApis;
import com.nowfloats.managecustomers.models.FacebookChatUsersModel;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 17-08-2017.
 */

public class FacebookChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int NO_MESSAGES = 0,CONNECT_TO_PAGE = 1;
    RecyclerView chatUserRecycerView;
    List<FacebookChatUsersModel.Datum> chatModelList = new ArrayList<>();
    FacebookChatAdapter adapter;
    UserSessionManager sessionManager;
    ProgressDialog progressDialog;
    LinearLayout layout,chatlayout;
    TextView facebookPageName;
    Bus bus;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_chat);
        init();
    }
    private void init(){
        bus = BusProvider.getInstance().getBus();
        Toolbar toolbar  = (Toolbar) findViewById(R.id.facebook_toolbar);
        findViewById(R.id.img_chat_user).setVisibility(View.GONE);
        findViewById(R.id.img_back).setOnClickListener(this);
        facebookPageName = (TextView) findViewById(R.id.tv_facebook_page);
        layout  = (LinearLayout) findViewById(R.id.fragment_layout);
        chatlayout  = (LinearLayout) findViewById(R.id.chat_user_layout);
        TextView title = (TextView) findViewById(R.id.tv_chat_user);
        title.setText("Facebook Chat");
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);

        sessionManager = new UserSessionManager(this,this);

        chatUserRecycerView = (RecyclerView) findViewById(R.id.rv_facebook_chat);
        chatUserRecycerView.setHasFixedSize(true);
        chatUserRecycerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FacebookChatAdapter(this,chatModelList);
        chatUserRecycerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        chatUserRecycerView.setAdapter(adapter);
        checkNfxConnection();
        //getChatData();
    }

    private void checkNfxConnection() {
        showProgress();
        Get_FP_Details_Service.newNfxTokenDetails(this,sessionManager.getFPID(),bus);
    }
    @Subscribe
    public void nfxCallback(NfxGetTokensResponse response){
        if(response.getNFXAccessTokens() != null ) {
            if(TextUtils.isEmpty(sessionManager.getFacebookPage())){
                showEmptyMessages(CONNECT_TO_PAGE);
                hideProgress();
            }else{

                getChatData();
            }

        }else{
            hideProgress();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    private void showEmptyMessages(int i){
        if(layout.getVisibility() != View.VISIBLE) {
            layout.setVisibility(View.VISIBLE);
            chatlayout.setVisibility(View.GONE);
        }
        TextView mainMessage = (TextView) findViewById(R.id.tv_main_message);
        TextView noteMessage = (TextView) findViewById(R.id.tv_note_message);
        Button connect = (Button) findViewById(R.id.btn_connect);
        switch(i){
            case NO_MESSAGES:
                connect.setVisibility(View.GONE);
                noteMessage.setVisibility(View.VISIBLE);
                noteMessage.setText(getString(R.string.facebook_chat_note));
                mainMessage.setText(getString(R.string.customer_have_not_messaged));
                break;
            case CONNECT_TO_PAGE:
                noteMessage.setVisibility(View.GONE);
                connect.setVisibility(View.VISIBLE);
                mainMessage.setText(getString(R.string.to_manage_facebook_page_message));
                connect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(FacebookChatActivity.this, Social_Sharing_Activity.class);
                        startActivityForResult(i,333);
                    }
                });
                break;
        }
    }
    private void getChatData(){

        if(chatModelList.size()>0) {
            chatModelList.clear();
            adapter.notifyDataSetChanged();
        }

        FacebookChatApis.FacebookApis apis = FacebookChatApis.getFacebookChatApis();
        apis.getAllUsers("facebook", sessionManager.getFPID(), new Callback<FacebookChatUsersModel>() {
            @Override
            public void success(FacebookChatUsersModel facebookChatUsersModel, Response response) {
                hideProgress();
                if(facebookChatUsersModel == null || !"success".equals(facebookChatUsersModel.getMessage())||response.getStatus() != 200){
                    Methods.showSnackBarNegative(FacebookChatActivity.this,getString(R.string.something_went_wrong_try_again));
                    showEmptyMessages(NO_MESSAGES);
                    return;
                }

                List<FacebookChatUsersModel.Datum> data = facebookChatUsersModel.getData();
                int size = data.size();
                if(size>0) {
                    if(chatlayout.getVisibility() != View.VISIBLE) {
                        layout.setVisibility(View.GONE);
                        chatlayout.setVisibility(View.VISIBLE);
                    }
                    facebookPageName.setText(sessionManager.getFacebookPage());
                    for (int i = 0; i<size;i++){
                        chatModelList.add(data.get(i));
                    }
                    //showEmptyMessages(1);
                    //logic for showing screens
                    adapter.notifyDataSetChanged();
                }else{
                    showEmptyMessages(NO_MESSAGES);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                Methods.showSnackBarNegative(FacebookChatActivity.this,getString(R.string.something_went_wrong_try_again));
                showEmptyMessages(NO_MESSAGES);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 221:
                getChatData();
                break;
            case 333:

                checkNfxConnection();
                //refresh
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    private void showProgress(){
        if(!isFinishing() && !progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideProgress(){
        if(!isFinishing() && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
