package com.nowfloats.sam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import com.nowfloats.sam.adapters.SAMCustomersAdapter;
import com.nowfloats.sam.decorators.RecyclerSectionItemDecoration;
import com.thinksity.R;

/**
 * Created by admin on 8/17/2017.
 */

public class SAMCustomerListActivity extends Activity
        implements RecyclerSectionItemDecoration.SectionCallback {


    public ProgressBar pbView;

    private RecyclerView rvSAMCustomerList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.bubble_scale_up, R.anim.bubble_scale_down);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sam_customer_list);

        initializeControls();


    }

    private void initializeControls() {

        pbView = (ProgressBar) findViewById(R.id.pbView);
        rvSAMCustomerList = (RecyclerView) findViewById(R.id.rvSAMCustomerList);

        rvSAMCustomerList.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false));


        RecyclerSectionItemDecoration sectionItemDecoration =
                new RecyclerSectionItemDecoration(120,
                        true,
                        SAMCustomerListActivity.this);
        rvSAMCustomerList.addItemDecoration(sectionItemDecoration);

        rvSAMCustomerList.setAdapter(new SAMCustomersAdapter(SAMCustomerListActivity.this, null));

    }

    public void onCustomerSelection() {

        Intent mIntent = new Intent(SAMCustomerListActivity.this, SAMCustomerDetailActivity.class);
        startActivity(mIntent);
    }

    @Override
    public boolean isSection(int position) {
        if (position % 2 == 0)
            return false;
        return true;
    }

    @Override
    public CharSequence getSectionHeader(int position) {
        return "tertgmkldrgkl";
    }
}
