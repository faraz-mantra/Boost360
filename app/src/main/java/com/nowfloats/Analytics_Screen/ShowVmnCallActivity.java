package com.nowfloats.Analytics_Screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ExpandableListView;

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

public class ShowVmnCallActivity extends AppCompatActivity {


    ExpandableListView expList;
    Toolbar toolbar;

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
        for(VmnCallModel model: list) {
            if (!hashMap.containsKey(model.getCallerNumber())) {
                ArrayList<VmnCallModel> subList = new ArrayList<>();
                subList.add(model);

                hashMap.put(model.getCallerNumber(), subList);
            } else {
                hashMap.get(model.getCallerNumber()).add(model);
            }
        }
        ArrayList<ArrayList<VmnCallModel>> bsort = new ArrayList<>(hashMap.values());
        Collections.sort(bsort, new Comparator<ArrayList<VmnCallModel>>() {
            @Override
            public int compare(ArrayList<VmnCallModel> o1, ArrayList<VmnCallModel> o2) {
                String first = removeDate(o1.get(0).getCallDateTime());
                String second = removeDate(o2.get(0).getCallDateTime());
                return second.compareToIgnoreCase(first);
            }
        });
        expList.setAdapter(new VmnCallAdapter(this,bsort));

    }

    private String removeDate(String withDate){
        return  withDate.replace("/Date(", "").replace(")/", "");
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
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
