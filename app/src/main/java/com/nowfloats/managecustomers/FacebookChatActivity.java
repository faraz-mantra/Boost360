package com.nowfloats.managecustomers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Collections;
import java.util.Comparator;
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
    SwipeRefreshLayout listSwipeLayout;
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
        findViewById(R.id.back_layout).setOnClickListener(this);
        listSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.sfl_list);
       // noMessageSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.sfl_no_messages);
        listSwipeLayout.setColorSchemeResources(R.color.primary_color);
       // noMessageSwipeLayout.setColorSchemeResources(R.color.primary_color);
        listSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getChatData();
            }
        });

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

        switch(i){
            case NO_MESSAGES:
                chatlayout.setVisibility(View.VISIBLE);
                FacebookChatUsersModel.Datum data = new FacebookChatUsersModel.Datum();
                data.setSender("no_messages");
                chatModelList.add(data);
                adapter.notifyItemInserted(0);
                //chatUserRecycerView.setVisibility(View.GONE);
               /* findViewById(R.id.no_messages_layout).setVisibility(View.VISIBLE);
                TextView mainMessage = (TextView) findViewById(R.id.tv_main_message);

                TextView noteMessage = (TextView) findViewById(R.id.tv_note_message);
                //connect.setVisibility(View.GONE);
                //noteMessage.setVisibility(View.VISIBLE);
                noteMessage.setText(Methods.fromHtml(getString(R.string.facebook_chat_note)));
                mainMessage.setText(getString(R.string.customer_have_not_messaged));*/
                break;
            case CONNECT_TO_PAGE:
                Button connect = (Button) findViewById(R.id.facebook_login);
                layout.setVisibility(View.VISIBLE);
                chatlayout.setVisibility(View.GONE);
                //noteMessage.setVisibility(View.GONE);
                //connect.setVisibility(View.VISIBLE);
                TextView connectText = (TextView) findViewById(R.id.facebook_analytics_connect_text1);
                connectText.setText(getString(R.string.to_manage_facebook_page_message));
                connect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(FacebookChatActivity.this, Social_Sharing_Activity.class);
                        startActivityForResult(i,333);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
                break;
        }
    }
    private void getChatData(){
        facebookPageName.setText(sessionManager.getFacebookPage());
        if(chatModelList.size()>0) {
            chatModelList.clear();
            adapter.notifyDataSetChanged();
        }
        showProgress();
        FacebookChatApis.FacebookApis apis = FacebookChatApis.getFacebookChatApis();
        apis.getAllUsers("facebook", sessionManager.getFPID(), new Callback<FacebookChatUsersModel>() {
            @Override
            public void success(FacebookChatUsersModel facebookChatUsersModel, Response response) {
                hideProgress();

                layout.setVisibility(View.GONE);
                chatlayout.setVisibility(View.VISIBLE);

                if(listSwipeLayout.isRefreshing()) {
                    listSwipeLayout.setRefreshing(false);
                }
                if(facebookChatUsersModel == null || response.getStatus() != 200){
                    Methods.showSnackBarNegative(FacebookChatActivity.this,getString(R.string.something_went_wrong_try_again));
                    showEmptyMessages(NO_MESSAGES);
                    return;
                }

                List<FacebookChatUsersModel.Datum> data = facebookChatUsersModel.getData();
                if(data != null) {
                    int size = data.size();
                    if (size > 0) {

                        //chatUserRecycerView.setVisibility(View.VISIBLE);
                        for (int i = 0; i < size; i++) {
                            chatModelList.add(data.get(i));
                        }
                        Collections.sort(chatModelList, new Comparator<FacebookChatUsersModel.Datum>() {
                            @Override
                            public int compare(final FacebookChatUsersModel.Datum object1, final FacebookChatUsersModel.Datum object2) {
                                return object2.getTimestamp().compareTo(object1.getTimestamp());
                            }
                        });
                        //showEmptyMessages(1);
                        //logic for showing screens
                        adapter.notifyDataSetChanged();
                    } else {
                        showEmptyMessages(NO_MESSAGES);
                    }
                }else{
                    showEmptyMessages(NO_MESSAGES);
                }


            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                if(listSwipeLayout.isRefreshing()) {
                    listSwipeLayout.setRefreshing(false);
                }
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
        if(!isFinishing() && !progressDialog.isShowing() && !listSwipeLayout.isRefreshing())
            progressDialog.show();
    }
    private void hideProgress(){
        if(!isFinishing() && progressDialog.isShowing() && !listSwipeLayout.isRefreshing()){
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
            case R.id.back_layout:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
