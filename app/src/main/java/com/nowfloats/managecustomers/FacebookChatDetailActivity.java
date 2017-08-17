package com.nowfloats.managecustomers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.managecustomers.adapters.FacebookChatDetailAdapter;
import com.nowfloats.managecustomers.apis.FacebookChatApis;
import com.nowfloats.managecustomers.models.FacebookChatDataModel;
import com.nowfloats.managecustomers.models.FacebookMessageModel;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 17-08-2017.
 */

public class FacebookChatDetailActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView chatUserRecycerView;
    List<FacebookChatDataModel.Datum> chatModelList = new ArrayList<>();
    FacebookChatDetailAdapter adapter;
    ImageView sendButton;
    EditText etReply;
    private ProgressDialog progressDialog;
    private UserSessionManager sessionManager;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_chat_details);
        init();
    }
    private void init(){
        Toolbar toolbar  = (Toolbar) findViewById(R.id.facebook_toolbar);
        userId = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(userName == null ?"":userName);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);

        sessionManager = new UserSessionManager(this,this);
        getChatData();
        chatUserRecycerView = (RecyclerView) findViewById(R.id.rv_facebook_chat);
        sendButton = (ImageView) findViewById(R.id.img_send_facebook_chat);
        etReply = (EditText) findViewById(R.id.et_facebook_chat);
        etReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){

                }else
                {

                }
            }
        });
        sendButton.setOnClickListener(this);
        chatUserRecycerView.setHasFixedSize(true);
        chatUserRecycerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FacebookChatDetailAdapter(this,chatModelList);
        chatUserRecycerView.setAdapter(adapter);
    }

    private void getChatData(){
        if(TextUtils.isEmpty(userId)){
            Methods.showSnackBarNegative(this,"Unable to find chat user");
            return;
        }
        chatModelList.clear();
        showProgress();
        FacebookChatApis.FacebookApis apis = FacebookChatApis.getFacebookChatApis();
        apis.getAllMessages(userId,"facebook", sessionManager.getFPID(), new Callback<FacebookChatDataModel>() {
            @Override
            public void success(FacebookChatDataModel facebookChatUsersModel, Response response) {
                hideProgress();
                if(facebookChatUsersModel == null || !"success".equals(facebookChatUsersModel.getMessage())||response.getStatus() != 200){
                    Methods.showSnackBarNegative(FacebookChatDetailActivity.this,getString(R.string.something_went_wrong_try_again));
                    return;
                }
                List<FacebookChatDataModel.Datum> data = facebookChatUsersModel.getData();
                int size = data.size();
                for (int i = 0; i<size;i++){
                    chatModelList.add(0,data.get(i));
                    adapter.notifyItemInserted(0);
                }
                chatUserRecycerView.smoothScrollToPosition(chatModelList.size()-1);
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                Methods.showSnackBarNegative(FacebookChatDetailActivity.this,getString(R.string.something_went_wrong_try_again));
            }
        });
        Log.v("ggg",userId);
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
        switch(v.getId()){
            case R.id.img_send_facebook_chat:
                String message = etReply.getText().toString();
                if(message.length() > 0){
                    sendData();
                }
                break;
            default:
                break;
        }
    }

    private void sendData() {
        if(etReply.getText().toString().length() == 0){
           return;
        }
        showProgress();
        FacebookChatApis.FacebookApis apis = FacebookChatApis.getFacebookChatApis();
        FacebookMessageModel model = new FacebookMessageModel();
        model.setUserId(userId);
        model.setType("text");
        model.setIdentifier("facebook");
        model.setNowfloatsId(sessionManager.getFPID());
        FacebookMessageModel.Content content = model.new Content();
        content.setText(etReply.getText().toString());
        model.setContent(content);
        apis.sendMessageToUser(model, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                hideProgress();
                if(jsonObject == null || response.getStatus() != 200){
                    Methods.showSnackBarNegative(FacebookChatDetailActivity.this, getString(R.string.something_went_wrong_try_again));
                    return;
                }
                JsonElement success = jsonObject.get("message");
                if(success != null && success.getAsString().equals("success")){

                    FacebookChatDataModel.Datum data = new FacebookChatDataModel().new Datum();
                    FacebookChatDataModel.Message mess = new FacebookChatDataModel().new Message();
                    mess.setType("text");
                    mess.setData(etReply.getText().toString());
                    data.setMessage(mess);
                    data.setSender("merchant");
                    data.setTimestamp(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis());
                    chatModelList.add(data);
                    adapter.notifyItemInserted(chatModelList.size()-1);
                    etReply.setText("");
                    chatUserRecycerView.smoothScrollToPosition(chatModelList.size()-1);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                Methods.showSnackBarNegative(FacebookChatDetailActivity.this, getString(R.string.something_went_wrong_try_again));
            }
        });
    }
}
