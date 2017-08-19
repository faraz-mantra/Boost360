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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.BusinessProfile.UI.UI.Social_Sharing_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.managecustomers.adapters.FacebookChatAdapter;
import com.nowfloats.managecustomers.apis.FacebookChatApis;
import com.nowfloats.managecustomers.models.FacebookChatUsersModel;
import com.nowfloats.util.Methods;
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
    LinearLayout layout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_chat);
        init();
    }
    private void init(){
        Toolbar toolbar  = (Toolbar) findViewById(R.id.facebook_toolbar);
        findViewById(R.id.img_chat_user).setVisibility(View.GONE);
        findViewById(R.id.img_back).setOnClickListener(this);

        layout  = (LinearLayout) findViewById(R.id.fragment_layout);
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
        getChatData();
    }

    private void showEmptyMessages(int i){
        if(layout.getVisibility() != View.VISIBLE) {
            layout.setVisibility(View.VISIBLE);
            chatUserRecycerView.setVisibility(View.GONE);
        }
        TextView mainMessage = (TextView) findViewById(R.id.tv_main_message);
        TextView noteMessage = (TextView) findViewById(R.id.tv_note_message);
        Button connect = (Button) findViewById(R.id.btn_connect);
        switch(i){
            case NO_MESSAGES:
                connect.setVisibility(View.GONE);
                noteMessage.setVisibility(View.VISIBLE);
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
        showProgress();
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
                for (int i = 0; i<size;i++){
                    chatModelList.add(data.get(i));
                }
                //showEmptyMessages(1);
                adapter.notifyDataSetChanged();
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
                showEmptyMessages(NO_MESSAGES);
                //refresh
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
