package com.nowfloats.Analytics_Screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nowfloats.Analytics_Screen.Search_Query_Adapter.VmnCallAdapter;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Admin on 27-04-2017.
 */

public class VmnCallActivity extends AppCompatActivity {
    RecyclerView rvList;
    ArrayList<VmnCallModel> list = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vmn_calls);
        rvList = (RecyclerView) findViewById(R.id.list);
        VmnCallAdapter adapter = new VmnCallAdapter(this,list);
        rvList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvList.setHasFixedSize(true);
        rvList.setAdapter(adapter);

    }
}
