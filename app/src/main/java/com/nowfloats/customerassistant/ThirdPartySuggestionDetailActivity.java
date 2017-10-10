package com.nowfloats.customerassistant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.thinksity.R;

/**
 * Created by Admin on 10-10-2017.
 */

public class ThirdPartySuggestionDetailActivity extends AppCompatActivity {

    FrameLayout frameLayout;

    public static final int ADD_PRODUCT = 0, SHOW_MESSAGE =- 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        frameLayout = (FrameLayout) findViewById(R.id.layout_fragment);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void addFragments(int screen){
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag;
        switch (screen){
            case ADD_PRODUCT:
                frag = manager.findFragmentByTag("product");
                if(frag == null){
                    frag = ShowSuggestionsFragment.getInstance(new Bundle());
                }
                manager.beginTransaction().replace(R.id.layout_fragment,frag,"product").commit();
                break;
            case SHOW_MESSAGE:
            default:
                frag = manager.findFragmentByTag("message");
                if(frag == null){
                    frag = ShowSuggestionsFragment.getInstance(new Bundle());
                }
                manager.beginTransaction().replace(R.id.layout_fragment,frag,"message").commit();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

