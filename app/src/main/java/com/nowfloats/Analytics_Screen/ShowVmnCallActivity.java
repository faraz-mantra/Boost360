package com.nowfloats.Analytics_Screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.Analytics_Screen.Search_Query_Adapter.VmnCallAdapter;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.thinksity.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 27-04-2017.
 */

public class ShowVmnCallActivity extends AppCompatActivity {


    RecyclerView rvList;
    ArrayList<VmnCallModel> list;
    Toolbar toolbar;
    String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vmn_calls);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();

        if(intent != null){
            Type type = new TypeToken<List<VmnCallModel>>(){}.getType();
            list = new Gson().fromJson(intent.getStringExtra("Calls"),type);
            title = intent.getStringExtra("CallType");
        }else{
            return;
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            setTitle(title);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        VmnCallAdapter adapter = new VmnCallAdapter(this,list);
        rvList = (RecyclerView) findViewById(R.id.list);
        rvList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvList.setHasFixedSize(true);
        rvList.setAdapter(adapter);
    }

}
