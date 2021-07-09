package com.nowfloats.managecustomers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.Fragments.FacebookLoginFragment;
import com.nowfloats.Analytics_Screen.model.NfxGetTokensResponse;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.managecustomers.adapters.FacebookChatAdapter;
import com.nowfloats.managecustomers.apis.FacebookChatApis;
import com.nowfloats.managecustomers.models.FacebookChatUsersModel;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
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

public class FacebookChatActivity extends AppCompatActivity implements View.OnClickListener, FacebookLoginFragment.OpenNextScreen {

    private static final int NO_MESSAGES = 0,CONNECT_TO_PAGE = 1;
    private RecyclerView chatUserRecyclerView;
    private List<FacebookChatUsersModel.Datum> chatModelList = new ArrayList<>();
    private FacebookChatAdapter adapter;
    private UserSessionManager sessionManager;
    private ProgressDialog progressDialog;
    private LinearLayout chatLayout;
    private FrameLayout frameLayout;
    private SwipeRefreshLayout listSwipeLayout;
    private Bus bus;
    private TextView title, description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MixPanelController.track(EventKeysWL.SIDE_PANEL_FACEBOOK_PAGE_CHATS, null);
        setContentView(R.layout.activity_facebook_chat);
        init();
    }
    private void init(){
        bus = BusProvider.getInstance().getBus();
        Toolbar toolbar  = (Toolbar) findViewById(R.id.facebook_toolbar);
        findViewById(R.id.img_chat_user).setVisibility(View.GONE);
        findViewById(R.id.img_back).setOnClickListener(this);
        listSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.sfl_list);
        listSwipeLayout.setColorSchemeResources(R.color.primary_color);
        listSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getChatData();
            }
        });
        sessionManager = new UserSessionManager(this,this);
        frameLayout  = (FrameLayout) findViewById(R.id.fragment_layout);
        chatLayout = (LinearLayout) findViewById(R.id.chat_user_layout);
        title = (TextView) findViewById(R.id.tv_chat_user);
        Typeface robotoMedium= Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        title.setTypeface(robotoMedium);
        findViewById(R.id.facebook_icon).setVisibility(View.VISIBLE);
        description = (TextView) findViewById(R.id.tv_chat_user_description);
        Typeface face= Typeface.createFromAsset(getAssets(), "Roboto-LightItalic.ttf");
        description.setTypeface(face);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);

        chatUserRecyclerView = (RecyclerView) findViewById(R.id.rv_facebook_chat);
        chatUserRecyclerView.setHasFixedSize(true);
        chatUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FacebookChatAdapter(this,chatModelList);
        chatUserRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        chatUserRecyclerView.setAdapter(adapter);
        checkNfxConnection();
    }

    private void checkNfxConnection() {
        showProgress();
        Get_FP_Details_Service.newNfxTokenDetails(this, sessionManager.getFPID(), bus);
    }
    @Subscribe
    public void nfxCallback(NfxGetTokensResponse response){
        SharedPreferences pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        if(pref.getInt("facebookChatStatus",0) == 1) {
            getChatData();
        }else{
            showEmptyMessages(CONNECT_TO_PAGE);
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
        title.setText("My Facebook Chats");
        switch(i){
            case NO_MESSAGES:
                chatLayout.setVisibility(View.VISIBLE);
                FacebookChatUsersModel.Datum data = new FacebookChatUsersModel.Datum();
                data.setSender(FacebookChatAdapter.NO_MESSAGES);
                chatModelList.add(data);
                adapter.notifyItemInserted(0);
                break;
            case CONNECT_TO_PAGE:
                description.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                chatLayout.setVisibility(View.GONE);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment frag=null;
                frag = manager.findFragmentByTag("FacebookLoginFragment");
                if(frag == null)
                    frag = FacebookLoginFragment.getInstance(0);

                transaction.replace(R.id.fragment_layout,frag,"FacebookLoginFragment").commit();
                break;
        }
    }
    private void getChatData(){
        if(TextUtils.isEmpty(sessionManager.getFacebookPage())){
            showEmptyMessages(CONNECT_TO_PAGE);
            hideProgress();
            return;
        }else{
            description.setVisibility(View.VISIBLE);
            description.setText(Methods.fromHtml("On page: <b>"+sessionManager.getFacebookPage()+"</b>"));
        }

        if(chatModelList.size()>0) {
            chatModelList.clear();
            adapter.notifyDataSetChanged();
        }
        showProgress();
        FacebookChatApis.FacebookApis apis = FacebookChatApis.getFacebookChatApis();
        apis.getAllUsers("facebook", sessionManager.getFPID(), new Callback<FacebookChatUsersModel>() {
            @Override
            public void success(FacebookChatUsersModel facebookChatUsersModel, Response response) {

                frameLayout.setVisibility(View.GONE);
                chatLayout.setVisibility(View.VISIBLE);

                if(listSwipeLayout.isRefreshing()) {
                    listSwipeLayout.setRefreshing(false);
                }
                if(facebookChatUsersModel == null || (response.getStatus() < 200 && response.getStatus()>=300)){
                    Methods.showSnackBarNegative(FacebookChatActivity.this,getString(R.string.something_went_wrong_try_again));
                    showEmptyMessages(NO_MESSAGES);
                    return;
                }else
                {

                    List<FacebookChatUsersModel.Datum> data = facebookChatUsersModel.getData();
                    if (data != null) {
                        int size = data.size();
                        if (size > 0) {

                            for (int i = 0; i < size; i++) {
                                chatModelList.add(data.get(i));
                            }
                            Collections.sort(chatModelList, new Comparator<FacebookChatUsersModel.Datum>() {
                                @Override
                                public int compare(final FacebookChatUsersModel.Datum object1, final FacebookChatUsersModel.Datum object2) {
                                    return object2.getTimestamp().compareTo(object1.getTimestamp());
                                }
                            });
                            title.setText("Facebook Chats ("+chatModelList.size()+")");
                            adapter.notifyDataSetChanged();
                        } else {
                            showEmptyMessages(NO_MESSAGES);
                        }
                    } else {
                        showEmptyMessages(NO_MESSAGES);
                    }
                }
                hideProgress();
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
            case R.id.img_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNextScreen() {
        checkNfxConnection();
    }
}
