package com.nowfloats.Analytics_Screen;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.Analytics_Screen.Search_Query_Adapter.VmnCallAdapter;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.thinksity.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Admin on 27-04-2017.
 */

public class ShowVmnCallActivity extends AppCompatActivity implements VmnCallAdapter.RequestPermission {


    ExpandableListView expList;
    Toolbar toolbar;
    VmnCallAdapter adapter;
    int REQUEST_PERMISSION = 202;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vmn_calls);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        expList = (ExpandableListView) findViewById(R.id.exp_list);

        Intent intent = getIntent();
        if(intent != null){
            Type type = new TypeToken<ArrayList<VmnCallModel>>(){}.getType();
            ArrayList<VmnCallModel> list =  new Gson().fromJson(intent.getStringExtra("calls"),type);
            numberWiseSeparation(list);
        }else{
            return;
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            setTitle("Call Logs");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }

    private void numberWiseSeparation(ArrayList<VmnCallModel> list){
        HashMap<Object, ArrayList<VmnCallModel>> hashMap = new HashMap<>();
        /*MediaPlayer player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);*/
        for(VmnCallModel model: list) {
            /*if(model.getCallRecordingUri()!=null && !model.getCallRecordingUri().equalsIgnoreCase("null")) {
                try {
                    player.setDataSource(model.getCallRecordingUri());
                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                model.setCallDuration(player.getDuration());
                Log.v("ggg", "duration " + player.getDuration());
                player.stop();
                player.reset();

            }*/
            if (!hashMap.containsKey(model.getCallerNumber())) {
                ArrayList<VmnCallModel> subList = new ArrayList<>();
                subList.add(model);

                hashMap.put(model.getCallerNumber(), subList);
            } else {
                hashMap.get(model.getCallerNumber()).add(0,model);
            }
        }
        /*player.release();
        player =null;*/
        ArrayList<ArrayList<VmnCallModel>> bsort = new ArrayList<>(hashMap.values());
        Collections.sort(bsort, new Comparator<ArrayList<VmnCallModel>>() {
            @Override
            public int compare(ArrayList<VmnCallModel> o1, ArrayList<VmnCallModel> o2) {
                String first = o1.get(0).getCallDateTime();
                String second = o2.get(0).getCallDateTime();
                return second.compareToIgnoreCase(first);
            }
        });
        adapter = new VmnCallAdapter(this,bsort);
        expList.setAdapter(adapter);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.v("ggg","onback");
        if(adapter != null){
            adapter.releaseResources();
        }
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public void gotoSetting(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    @Override
    public void reuestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION);
        }else{
            new MaterialDialog.Builder(this)
                    .title("Recording Download")
                    .content("we need write to external storage permission to download this file.")
                    .negativeColorRes(R.color.gray_transparent)
                    .negativeText(getString(R.string.cancel))
                    .positiveColorRes(R.color.primary_color)
                    .positiveText(getString(R.string.open_setting))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            dialog.dismiss();
                            gotoSetting();
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }
}
