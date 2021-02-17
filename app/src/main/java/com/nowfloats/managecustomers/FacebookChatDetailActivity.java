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
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nowfloats.managecustomers.adapters.FacebookChatDetailAdapter.USER;

/**
 * Created by Admin on 17-08-2017.
 */

public class FacebookChatDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView chatUserRecycerView;
    private List<FacebookChatDataModel.Datum> chatModelList = new ArrayList<>();
    private List<FacebookChatDataModel.Datum> totalDataList = new ArrayList<>();
    private FacebookChatDetailAdapter adapter;
    private ImageView sendButton, scrollButton;
    private EditText etReply;
    private ProgressDialog progressDialog;
    private UserSessionManager sessionManager;
    private String userId;
    private SharedPreferences pref;
    public static final String INTENT_FILTER = "nfx.facebook.messages";
    static{
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MixPanelController.track(EventKeysWL.FACEBOOK_PAGE_CHATS_DETAILS, null);
        setContentView(R.layout.activity_facebook_chat_details);
        init();
    }

    private void init(){
        getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(),Methods.decodeSampledBitmap(getResources(),R.drawable.facebook_chat_bg1,720,720)));//ContextCompat.getDrawable(this, R.drawable.facebook_chat_bg2));
        Toolbar toolbar  = (Toolbar) findViewById(R.id.facebook_toolbar);
        setSupportActionBar(toolbar);
        ImageView imgUser = (ImageView) findViewById(R.id.img_chat_user);
        imgUser.setVisibility(View.VISIBLE);
        TextView title = (TextView) findViewById(R.id.tv_chat_user);
        TextView description = (TextView) findViewById(R.id.tv_chat_user_description);
        Typeface face= Typeface.createFromAsset(getAssets(), "Roboto-MediumItalic.ttf");
        Typeface robotoMedium= Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        title.setTypeface(robotoMedium);
        description.setTypeface(face);
        description.setText("facebook user");
        scrollButton = (ImageView) findViewById(R.id.iv_scroll_down);
        findViewById(R.id.img_back).setOnClickListener(this);
        chatUserRecycerView = (RecyclerView) findViewById(R.id.rv_facebook_chat);
        sendButton = (ImageView) findViewById(R.id.iv_send_msg);
        etReply = (EditText) findViewById(R.id.et_facebook_chat);
        String user_data = getIntent().getStringExtra("user_data");
        FacebookChatDataModel.UserData userData = new Gson().fromJson(user_data,FacebookChatDataModel.UserData.class);
        if(userData == null){
            title.setText("Unknown");
            return;
        }
        pref = getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        pref.edit().putString("facebookChatUser",userId).apply();
        pref.edit().putBoolean("IsNewFacebookMessage",false).apply();
        sendButton.setOnClickListener(this);
        scrollButton.setOnClickListener(this);
        //Glide.with(this).load(userData.getProfilePic()).into(imgUser);
        Picasso.get()
                .load(userData.getProfilePic())
                .resize(200, 0)
                .placeholder(R.drawable.ic_user)
                .into(imgUser);

        userId = userData.getId();
        String userName = "Unknown";
        userName = TextUtils.isEmpty(userData.getFirstName())?
                userName : userData.getFirstName();
        if(!userName.equals("Unknown")){
            userName = TextUtils.isEmpty(userData.getLastName())?
                    userName : userName +" "+userData.getLastName();
        }
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
                if(s.toString().trim().length()>0){
                    sendButton.getBackground().setColorFilter(Color.parseColor("#157EFB"), PorterDuff.Mode.DARKEN);
                } else {
                    sendButton.getBackground().setColorFilter(Color.parseColor("#40157EFB"), PorterDuff.Mode.DARKEN);
                }
            }
        });

        chatUserRecycerView.setHasFixedSize(true);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        chatUserRecycerView.setLayoutManager(manager);
        FacebookChatItemDecorator decorator = new FacebookChatItemDecorator(Methods.dpToPx(40,this),
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
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(manager.findLastCompletelyVisibleItemPosition()>0 &&manager.findLastCompletelyVisibleItemPosition() < manager.getItemCount()-5){
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
    }
    private void getChatData(){
        if(TextUtils.isEmpty(userId)){
            Methods.showSnackBarNegative(this,"Unable to find chat user");
            return;
        }
        if(totalDataList.size()>0){
            totalDataList.clear();
            chatModelList.clear();
            adapter.notifyDataSetChanged();
        }
        showProgress();
        FacebookChatApis.FacebookApis apis = FacebookChatApis.getFacebookChatApis();
        apis.getAllMessages(userId,"facebook", sessionManager.getFPID(), new Callback<FacebookChatDataModel>() {
            @Override
            public void success(FacebookChatDataModel facebookChatUsersModel, Response response) {
                if(facebookChatUsersModel == null || !"success".equals(facebookChatUsersModel.getMessage())||response.getStatus() != 200){
                    Methods.showSnackBarNegative(FacebookChatDetailActivity.this,getString(R.string.something_went_wrong_try_again));
                    hideProgress();
                    return;
                }
                totalDataList = facebookChatUsersModel.getData();
                decideCornersForChatItems();
                int size = totalDataList.size();
                for (int i = 0; i<size && i<15;i++){
                    chatModelList.add(0,totalDataList.get(i));
                }
                adapter.notifyDataSetChanged();
                chatUserRecycerView.scrollToPosition(chatModelList.size()-1);
                hideProgress();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                Methods.showSnackBarNegative(FacebookChatDetailActivity.this,getString(R.string.something_went_wrong_try_again));
            }
        });
    }

    private void decideCornersForChatItems() {
        Collections.sort(chatModelList, new Comparator<FacebookChatDataModel.Datum>() {
            @Override
            public int compare(FacebookChatDataModel.Datum object1, FacebookChatDataModel.Datum object2) {
                return object2.getTimestamp().compareTo(object1.getTimestamp());
            }
        });
        int size = totalDataList.size();
        for (int i =0 ;i<size;i++){
            if(!totalDataList.get(i).getSender().equals(USER)) {
                totalDataList.get(i).setShowCornerBackground(size-1 == i || totalDataList.get(i+1).getSender().equals(USER));
            }else{
                totalDataList.get(i).setShowCornerBackground(size-1 == i || !totalDataList.get(i+1).getSender().equals(USER));
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pref!= null &&pref.getBoolean("IsNewFacebookMessage",false)){
            pref.edit().putBoolean("IsNewFacebookMessage",false).apply();
            getChatData();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter(INTENT_FILTER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(pref != null) {
            pref.edit().putString("facebookChatUser", "").apply();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(pref != null) {
                pref.edit().putBoolean("IsNewFacebookMessage", false).apply();

                String message = intent.getStringExtra("message");

                FacebookChatDataModel.Message messageData = new Gson().fromJson(message, FacebookChatDataModel.Message.class);
                if (messageData != null) {
                    addMessageToList(messageData, USER);
                }
                Log.v("ggg", "clicked");
            }
        }

    };

    private void setCallback(){
        setResult(221);
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
                onBackPressed();
                break;
            case R.id.iv_scroll_down:
                if(chatModelList.size()>0)
                chatUserRecycerView.scrollToPosition(chatModelList.size()-1);
                break;
            case R.id.iv_send_msg:
                if(sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
                    Methods.showFeatureNotAvailDialog(this);
                }else {
                    String message = etReply.getText().toString().trim();
                    if (message.length() > 0) {
                        sendData(message);
                    }
                    etReply.setText("");
                }
                break;
            default:
                break;
        }
    }

    private int addMessageToList(FacebookChatDataModel.Message message, String status){

        FacebookChatDataModel.Datum chatData = new FacebookChatDataModel.Datum();
        chatData.setMessage(message);
        chatData.setSender(status);
        int currPos = chatModelList.size();
        if(!status.equals(USER)) {
            chatData.setShowCornerBackground(currPos == 0 || chatModelList.get(currPos-1).getSender().equals(USER));
        }else{
            chatData.setShowCornerBackground(currPos == 0  || !chatModelList.get(currPos-1).getSender().equals(USER));
        }
        chatData.setTimestamp(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis());
        totalDataList.add(0,chatData);
        chatModelList.add(chatData);
        adapter.notifyItemInserted(currPos);
        chatUserRecycerView.scrollToPosition(currPos);
        return currPos;
    }
    private void sendData(String message) {

        if(message.length() == 0){
           return;
        }
        FacebookChatDataModel.Message messageData = new FacebookChatDataModel.Message();
        messageData.setType("text");
        FacebookChatDataModel.Data data = new FacebookChatDataModel.Data();
        data.setText(message);
        data.setUrl("");
        messageData.setData(data);
        final int currPos = addMessageToList(messageData,FacebookChatDetailAdapter.WAITING);

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
                        response.getStatus() != 200){
                    chatModelList.get(currPos).setSender(FacebookChatDetailAdapter.ERROR);
                    adapter.notifyItemChanged(currPos);
                }else if("success".equalsIgnoreCase(jsonObject.get("message").getAsString())){

                    chatModelList.get(currPos).setSender(FacebookChatDetailAdapter.MERCHANT);
                    adapter.notifyItemChanged(currPos);

                }else if ("session_expired".equalsIgnoreCase(jsonObject.get("message").getAsString())){
                    chatModelList.get(currPos).setSender(FacebookChatDetailAdapter.ERROR);
                    adapter.notifyItemChanged(currPos);
                    Snackbar bar = Snackbar.make(FacebookChatDetailActivity.this.findViewById(android.R.id.content),
                            R.string.its_been_more_than_twenty_hours_since_you_last,
                            Snackbar.LENGTH_INDEFINITE);
                    bar.getView().setBackgroundColor(Color.parseColor("#E02200"));
                    bar.show();
                }

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
