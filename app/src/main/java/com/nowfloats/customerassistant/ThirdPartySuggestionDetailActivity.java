package com.nowfloats.customerassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.customerassistant.adapters.ThirdPartySharedItemAdapter;
import com.nowfloats.customerassistant.callbacks.ThirdPartyCallbacks;
import com.nowfloats.customerassistant.models.SharedSuggestionsDO;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 10-10-2017.
 */

public class ThirdPartySuggestionDetailActivity extends AppCompatActivity implements ThirdPartyCallbacks, View.OnClickListener {

    public static final int ADD_PRODUCTS = 0,ADD_UPDATES = 2, SHOW_MESSAGE = -1;
    SuggestionsDO mSuggestionDO;
    LinearLayout messageLayout;
    FrameLayout fragmentLayout;
    List<SharedSuggestionsDO> sharedSuggestionsDOList = new ArrayList<>();
    ThirdPartySharedItemAdapter mSuggestionAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        mSuggestionDO = (SuggestionsDO) intent.getSerializableExtra("message");
        /*try {
            *//*shareSuggestionDo = (SuggestionsDO) mSuggestionDO.clone();
            shareSuggestionDo.setUpdates(new ArrayList<SugUpdates>());
            shareSuggestionDo.setProducts(new ArrayList<SugProducts>());*//*
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }*/
        init();
        addFragments(SHOW_MESSAGE);
    }

    private void init(){
        setTitle(mSuggestionDO.getActualMessage());
        TextView addressText = (TextView) findViewById(R.id.tv_address);
        TextView timeText = (TextView) findViewById(R.id.tv_time);
        timeText.setText(Methods.getFormattedDate(mSuggestionDO.getDate()));
        messageLayout = (LinearLayout) findViewById(R.id.layout_message);
        fragmentLayout = (FrameLayout) findViewById(R.id.layout_fragment);
        RecyclerView suggestionsRecyclerView = (RecyclerView) findViewById(R.id.rv_suggestions);
        suggestionsRecyclerView.setHasFixedSize(true);
        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(ThirdPartySuggestionDetailActivity.this));
        mSuggestionAdapter = new ThirdPartySharedItemAdapter(ThirdPartySuggestionDetailActivity.this, sharedSuggestionsDOList);
        suggestionsRecyclerView.setAdapter(mSuggestionAdapter);
        findViewById(R.id.btn_add_updates).setOnClickListener(this);
        findViewById(R.id.btn_add_products).setOnClickListener(this);
        findViewById(R.id.btn_send).setOnClickListener(this);
    }
    private void addFragments(int screen){
        FragmentManager manager = getSupportFragmentManager();
        switch (screen){
            case ADD_UPDATES:
            case ADD_PRODUCTS:
                messageLayout.setVisibility(View.GONE);
                fragmentLayout.setVisibility(View.VISIBLE);
                Bundle b= new Bundle();
                b.putInt("type",screen);
                b.putSerializable("message",mSuggestionDO);
                manager.beginTransaction()
                        .replace(R.id.layout_fragment, ShowThirdPartyProductsFragment.getInstance(b))
                        .addToBackStack(null)
                        .commit();
                break;
            case SHOW_MESSAGE:
                if (fragmentLayout.getVisibility() == View.VISIBLE){
                    manager.popBackStack();
                    fragmentLayout.setVisibility(View.GONE);
                }
                messageLayout.setVisibility(View.VISIBLE);
                break;

            default:
                break;
            }
    }

    private void shareSuggestionToCustomer(){
        //shareSuggestionDo share
    }
    @Override
    public void onBackPressed() {
        if (fragmentLayout.getVisibility() == View.VISIBLE){
            getSupportFragmentManager().popBackStack();
            fragmentLayout.setVisibility(View.GONE);
            messageLayout.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
        }

    }

    @Override
    public void addSuggestions(int type, ArrayList<Integer> positions) {

        switch (type){

            case ADD_PRODUCTS:
                for (int i : positions){
                    SharedSuggestionsDO suggestionsDO =
                            new SharedSuggestionsDO(mSuggestionDO.getProducts().get(i).getProductName(),
                                    mSuggestionDO.getProducts().get(i).getProductUrl());
                    if(!sharedSuggestionsDOList.contains(suggestionsDO))
                        sharedSuggestionsDOList.add(suggestionsDO);
                }
                mSuggestionAdapter.notifyDataSetChanged();
                break;
            case ADD_UPDATES:

                for (int i : positions){
                    SharedSuggestionsDO suggestionsDO =
                            new SharedSuggestionsDO(mSuggestionDO.getUpdates().get(i).getName(),
                                    mSuggestionDO.getUpdates().get(i).getUpdateUrl());
                    if(!sharedSuggestionsDOList.contains(suggestionsDO))
                        sharedSuggestionsDOList.add(suggestionsDO);
                }
                mSuggestionAdapter.notifyDataSetChanged();
                break;
        }
        addFragments(SHOW_MESSAGE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_send:
                shareSuggestionToCustomer();
                break;
            case R.id.btn_add_products:
                addFragments(ADD_PRODUCTS);
                break;
            case R.id.btn_add_updates:
                addFragments(ADD_UPDATES);
                break;
            case R.id.btn_call:
                //Methods.makeCall(mContext,mSuggestionDO.get);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

