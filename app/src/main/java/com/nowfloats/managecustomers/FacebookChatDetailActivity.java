package com.nowfloats.managecustomers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.nowfloats.managecustomers.adapters.FacebookChatDetailAdapter;
import com.nowfloats.managecustomers.models.FacebookChatDataModel;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 17-08-2017.
 */

public class FacebookChatDetailActivity extends AppCompatActivity {
    RecyclerView chatUserRecycerView;
    List<FacebookChatDataModel> chatModelList = new ArrayList<>();
    FacebookChatDetailAdapter adapter;
    ImageView sendButton;
    EditText etReply;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_chat_details);
        init();
    }
    private void init(){
        Toolbar toolbar  = (Toolbar) findViewById(R.id.facebook_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("User Chat");
        }
        getChatData(getIntent().getStringExtra("user_id"));
        chatUserRecycerView = (RecyclerView) findViewById(R.id.rv_facebook_chat);
        sendButton = (ImageView) findViewById(R.id.img_send_facebook_chat);
        etReply = (EditText) findViewById(R.id.et_facebook_chat);
        chatUserRecycerView.setHasFixedSize(true);
        chatUserRecycerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FacebookChatDetailAdapter(this);
        chatUserRecycerView.setAdapter(adapter);
    }

    private void getChatData(String userId){
        if(TextUtils.isEmpty(userId)){
            Methods.showSnackBarNegative(this,"Unable to find chat user");
            return;
        }
        Log.v("ggg",userId);
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
}
