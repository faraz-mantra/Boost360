package com.nowfloats.BusinessProfile.UI.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.thinksity.R;

/**
 * Created by Admin on 10-07-2017.
 */

public class FeedbacksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            setTitle("Feedback");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner_feedback);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.spinner_list_item_array,android.R.layout.simple_list_item_1);
        spinner.setAdapter(adapter);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_ratting);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(new FeedbackAdapter(this));
    }
}
