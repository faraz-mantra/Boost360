package com.nfx.leadmessages;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * Created by Admin on 1/14/2017.
 */

public class ShowMessagesActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int READ_MESSAGES_ID = 221 ;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    private static String fpId,mobileId;
    MessageListModel messageListModel;
    MessageAdapter adapter;
    private ArrayList<MessageListModel.SmsMessage> messageList;
    private String[] permission = new String[]{Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        linearLayout = (LinearLayout) findViewById(R.id.parent_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageListModel = MessageListModel.getInstance();
        messageList= new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);
        SharedPreferences pref =getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        fpId =pref.getString(Constants.FP_ID,null);


        //mobileId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        getPermission();
    }

    private void getPermission(){
        // check read sms permission
       /* int count=0;boolean enable=false;
        for(String s:permission){
            if(ContextCompat.checkSelfPermission(this,s) == PackageManager.PERMISSION_GRANTED){

                count++;
                Log.v("ggg","get permission "+s);
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                // if user deny the permissions
                if(shouldShowRequestPermissionRationale(s)){
                    enable=true;
                    Log.v("ggg","enable "+s);
                }

            }
        }
       if(enable) {
            Snackbar.make(linearLayout, R.string.required_permission_to_show, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.enable, this)  // action text on the right side of snackbar
                    .setActionTextColor(ContextCompat.getColor(this,android.R.color.holo_green_light))
                    .show();
       }*/
       if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED){

            // start the service to send data to firebase
            
           TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
           mobileId = tm.getDeviceId();
           if(mobileId == null || fpId == null){
               return;
           }
           Intent intent = new Intent(this, ReadMessages.class);
           startService(intent);
           addListener();
       }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
       {
            // if user deny the permissions
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)||
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){

                Snackbar.make(linearLayout, R.string.required_permission_to_show, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.enable, this)  // action text on the right side of snackbar
                        .setActionTextColor(ContextCompat.getColor(this,android.R.color.holo_green_light))
                        .show();
            }
           else{
                Log.v("ggg","request");
                requestPermissions(permission,READ_MESSAGES_ID);
            }

       }

    }

    // this method called when user react on permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==READ_MESSAGES_ID && grantResults.length>0){
            int count=0;
            for (int i=0;i<permissions.length;i++){
                Log.v("ggg","on result"+permissions[i]);
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    break;
                }else{
                    count++;
                }
            }
            if(count == permissions.length) {
                // if he grant the permissions
                Intent intent = new Intent(this, ReadMessages.class);
                startService(intent);
            }else{
               getPermission();
            }
        }
    }


    @Override
    public void onClick(View v) {
        // after click on action button of snackbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission,READ_MESSAGES_ID);
        }
    }
    // add the listener with firebase database
    private void addListener(){
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyBowYAh4TLbruwJoqL3QnQaKhBmB83EgEs")
                .setApplicationId("1:506969475000:android:afe3b748e8b5c95f")
                .setDatabaseUrl("https://nfxteam-153211.firebaseio.com")
                .build();
        FirebaseApp secondApp = null;
        try {
            secondApp = FirebaseApp.getInstance("second app");
        }catch(Exception e) {
            secondApp = FirebaseApp.initializeApp(getApplicationContext(), options, "second app");
        }
        FirebaseDatabase secondDatabase = FirebaseDatabase.getInstance(secondApp);
        DatabaseReference mDatabase = secondDatabase.getReference();
        mDatabase.child(fpId+Constants.MESSAGES).child(mobileId).addValueEventListener(new ValueEventListener() {

        @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();
                ArrayList<MessageListModel.SmsMessage> modelList = new ArrayList<MessageListModel.SmsMessage>();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Log.v("ggg","hey");
                    messageList.add(dataSnapshot1.getValue(MessageListModel.SmsMessage.class));
                }

                Log.v("ggg","size "+messageList.size());
                if (messageList.size()==0){
                    Snackbar.make(linearLayout,R.string.contact_empty,Snackbar.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(linearLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });
    }
}