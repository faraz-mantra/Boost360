package com.nowfloats.managecustomers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.managecustomers.adapters.FacebookChatDetailAdapter;
import com.nowfloats.managecustomers.apis.FacebookChatApis;
import com.nowfloats.managecustomers.models.FacebookChatDataModel;
import com.nowfloats.managecustomers.models.FacebookMessageModel;
import com.nowfloats.riachatsdk.utils.Utils;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.squareup.picasso.Picasso;
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
    List<FacebookChatDataModel.Datum> totalDataList = new ArrayList<>();
    FacebookChatDetailAdapter adapter;
    ImageView sendButton, scrollButton;
    EditText etReply;
    private ProgressDialog progressDialog;
    private UserSessionManager sessionManager;
    private String userId;
    public static final String INTENT_FILTER = "nfx.facebook.messages";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_chat_details);
        init();
    }
    private void init(){
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, com.nowfloats.riachatsdk.R.drawable.doodle_bg));
        Toolbar toolbar  = (Toolbar) findViewById(R.id.facebook_toolbar);
        setSupportActionBar(toolbar);
        ImageView imgUser = (ImageView) findViewById(R.id.img_chat_user);
        ImageView imgBack = (ImageView) findViewById(R.id.img_back);
        TextView title = (TextView) findViewById(R.id.tv_chat_user);
        scrollButton = (ImageView) findViewById(R.id.iv_scroll_down);
        chatUserRecycerView = (RecyclerView) findViewById(R.id.rv_facebook_chat);
        sendButton = (ImageView) findViewById(R.id.iv_send_msg);
        etReply = (EditText) findViewById(R.id.et_facebook_chat);
        String user_data = getIntent().getStringExtra("user_data");
        FacebookChatDataModel.UserData userData = new Gson().fromJson(user_data,FacebookChatDataModel.UserData.class);
        if(userData == null){
            return;
        }
        SharedPreferences pref = getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        pref.edit().putString("facebookChatUser",userId).apply();
        imgUser.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        scrollButton.setOnClickListener(this);
        //Glide.with(this).load(userData.getProfilePic()).into(imgUser);
        Picasso.with(this)
                .load(userData.getProfilePic())
                .resize(200, 0)
                .placeholder(R.drawable.ic_user)
                .into(imgUser);

        userId = userData.getId();
        String userName = userData.getFirstName()+" "+userData.getLastName();
        title.setText(userName);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);

        sessionManager = new UserSessionManager(this,this);
        getChatData();
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
                    sendButton.getBackground().setColorFilter(Color.parseColor("#157EFB"), PorterDuff.Mode.DARKEN);
                } else {
                    sendButton.getBackground().setColorFilter(Color.parseColor("#40157EFB"), PorterDuff.Mode.DARKEN);
                }
            }
        });

        chatUserRecycerView.setHasFixedSize(true);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        chatUserRecycerView.setLayoutManager(manager);
        FacebookChatItemDecorator decorator = new FacebookChatItemDecorator(Utils.dpToPx(this,30),
                true, getSectionCallback());
        chatUserRecycerView.addItemDecoration(decorator);
        adapter = new FacebookChatDetailAdapter(this,chatModelList);
        chatUserRecycerView.setAdapter(adapter);
        chatUserRecycerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if ( bottom < oldBottom) {
                    chatUserRecycerView.smoothScrollToPosition(manager.getItemCount());
                }
            }
        });
        chatUserRecycerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                /*if(manager.findLastCompletelyVisibleItemPosition() < manager.getItemCount()-5){
                    scrollButton.setVisibility(View.VISIBLE);
                }else{
                    scrollButton.setVisibility(View.GONE);
                }*/
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(manager.findLastCompletelyVisibleItemPosition() < manager.getItemCount()-5){
                    scrollButton.setVisibility(View.VISIBLE);
                }else{
                    scrollButton.setVisibility(View.GONE);
                }

                if(manager.findFirstCompletelyVisibleItemPosition()<4 && chatModelList.size() != totalDataList.size()){
                    //isAddedToTop = false;
                    addChatItemToListTop();
                }

            }
        });

    }

    private void addChatItemToListTop(){
        int size  = totalDataList.size();
        int chatSize = chatModelList.size();
        for (int i = chatSize; i< size && i<chatSize+10;i++){
            chatModelList.add(0,totalDataList.get(i));
            adapter.notifyItemInserted(0);
        }
        //adapter.notifyDataSetChanged();

       /* chatModelList.add(0, totalDataList.get(chatModelList.size()));
        adapter.notifyItemInserted(0);*/
    }
    private void getChatData(){
        if(TextUtils.isEmpty(userId)){
            Methods.showSnackBarNegative(this,"Unable to find chat user");
            return;
        }

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
                totalDataList = facebookChatUsersModel.getData();
                decideCornersForChatItems();
                int size = totalDataList.size();
                for (int i = 0; i<size && i<15;i++){
                    chatModelList.add(0,totalDataList.get(i));
                    //adapter.notifyItemInserted(0);
                    //chatUserRecycerView.smoothScrollToPosition(chatModelList.size()-1);
                }
                adapter.notifyDataSetChanged();
                chatUserRecycerView.smoothScrollToPosition(chatModelList.size()-1);
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                Methods.showSnackBarNegative(FacebookChatDetailActivity.this,getString(R.string.something_went_wrong_try_again));
            }
        });
    }

    private void decideCornersForChatItems() {
        int size = totalDataList.size();
        for (int i =0 ;i<size;i++){
            if(!totalDataList.get(i).getSender().equals(FacebookChatDetailAdapter.USER)) {
                totalDataList.get(i).setShowCornerBackground(size-1 == i || totalDataList.get(i+1).getSender().equals(FacebookChatDetailAdapter.USER));
            }else{
                totalDataList.get(i).setShowCornerBackground(size-1 == i || !totalDataList.get(i+1).getSender().equals(FacebookChatDetailAdapter.USER));
            }

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter(INTENT_FILTER));

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String user_data = intent.getStringExtra("user_data");
            String message = intent.getStringExtra("mp_message");
            FacebookChatDataModel.UserData userData = new Gson().fromJson(user_data,FacebookChatDataModel.UserData.class);
            if(userData != null && userId.equals(userData.getId())){
                addMessageToList(message,FacebookChatDetailAdapter.USER);
            }
            Log.v("ggg", "clicked");
        }

    };

    private void setCallback(){
        setResult(-221);
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
        setCallback();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.img_back:
            case R.id.img_chat_user:
                onBackPressed();
                break;
            case R.id.iv_scroll_down:
                if(chatModelList.size()>0)
                chatUserRecycerView.smoothScrollToPosition(chatModelList.size()-1);
                break;
            case R.id.iv_send_msg:
                String message = etReply.getText().toString();
                if(message.length() > 0){
                    sendData();
                }
                break;
            default:
                break;
        }
    }

    private int addMessageToList(String message,String status){
        String type="text";
        FacebookChatDataModel.Datum chatData = new FacebookChatDataModel.Datum();
        FacebookChatDataModel.Message mess = new FacebookChatDataModel.Message();
        mess.setType(type);
        FacebookChatDataModel.Data data = new FacebookChatDataModel.Data();
        data.setText(message);
        data.setUrl("");
        mess.setData(data);
        chatData.setMessage(mess);
        chatData.setSender(status);
        int currPos = chatModelList.size();
        if(!status.equals(FacebookChatDetailAdapter.USER)) {
            chatData.setShowCornerBackground(currPos == 0 || chatModelList.get(currPos-1).getSender().equals(FacebookChatDetailAdapter.USER));
        }else{
            chatData.setShowCornerBackground(currPos == 0  || !chatModelList.get(currPos-1).getSender().equals(FacebookChatDetailAdapter.USER));
        }
        chatData.setTimestamp(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis());
        totalDataList.add(0,chatData);
        chatModelList.add(chatData);
        adapter.notifyItemInserted(currPos);
        etReply.setText("");
        chatUserRecycerView.smoothScrollToPosition(currPos);
        return currPos;
    }
    private void sendData() {
        String message = etReply.getText().toString().trim();
        if(message.length() == 0){
           return;
        }
        final int currPos = addMessageToList(message,FacebookChatDetailAdapter.WAITING);

        FacebookChatApis.FacebookApis apis = FacebookChatApis.getFacebookChatApis();
        FacebookMessageModel model = new FacebookMessageModel();
        model.setUserId(userId);
        model.setType("text");
        model.setIdentifier("facebook");
        model.setNowfloatsId(sessionManager.getFPID());
        FacebookMessageModel.Content content = model.new Content();
        content.setText(message);
        model.setContent(content);

        apis.sendMessageToUser(model, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

                if(jsonObject == null || jsonObject.get("message") == null ||
                        !jsonObject.get("message").getAsString().equals("success")||
                        response.getStatus() != 200){
                    chatModelList.get(currPos).setSender(FacebookChatDetailAdapter.ERROR);
                    adapter.notifyItemChanged(currPos);
                    return;
                }

                chatModelList.get(currPos).setSender(FacebookChatDetailAdapter.MERCHANT);
                adapter.notifyItemChanged(currPos);

            }

            @Override
            public void failure(RetrofitError error) {
                chatModelList.get(currPos).setSender(FacebookChatDetailAdapter.ERROR);
                adapter.notifyItemChanged(currPos);
            }
        });
    }

    private String getHeader(Long millisecond){
        String date = Methods.getFormattedDate(String.valueOf(millisecond));
        return date.substring(0,date.indexOf("at"));
    }

    private FacebookChatItemDecorator.SectionCallback getSectionCallback() {
        return new FacebookChatItemDecorator.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                return position == 0
                        || !getHeader(chatModelList.get(position).getTimestamp())
                        .equals(getHeader(chatModelList.get(position - 1).getTimestamp()));
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                return getHeader(chatModelList.get(position).getTimestamp());
            }
        };
    }
}
