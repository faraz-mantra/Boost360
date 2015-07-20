package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.NavigationDrawer.API.Home_View_Card_Delete;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Card_Full_View_MainActivity extends AppCompatActivity implements Home_View_Card_Delete.CardRefresh {

    PageAdapter pageAdapter;
    ViewPager viewPager ;
    private Toolbar toolbar;
    ImageView deleteButton;
    private String cardId;
    int position;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card__full__view__main);
        Methods.isOnline(Card_Full_View_MainActivity.this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        Bundle extras = getIntent().getExtras();
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        position = extras.getInt("POSITION");

        deleteButton = (ImageView) toolbar.findViewById(R.id.home_view_delete_card);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                new MaterialDialog.Builder(Card_Full_View_MainActivity.this)
                        .customView(R.layout.exit_dialog, true)
                        .positiveText("Delete")
                        .negativeText("Cancel")
                        .positiveColorRes(R.color.primaryColor)
                        .negativeColorRes(R.color.light_gray)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                            MixPanelController.track(EventKeysWL.MESSAGE_FULL_VIEW_DELETE, null);

                                try {
                                    cardId = HomeActivity.StorebizFloats.get(position)._id;
                                    JSONObject content = new JSONObject();
                                    content.put("dealId", cardId);
                                    content.put("clientId", Constants.clientId1);


                                    Home_View_Card_Delete obj =  new Home_View_Card_Delete(Card_Full_View_MainActivity.this,
                                            Constants.DeleteCard,content,position,v,0);
                                    //obj.CardRefresh_Listener(this);
                                    obj.execute();

                                    pd = ProgressDialog.show(Card_Full_View_MainActivity.this, null,
                                            "Deleting Message . . . ");
                                    pd.setCancelable(false);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        pageAdapter = new PageAdapter(getSupportFragmentManager(),Card_Full_View_MainActivity.this);
        //  Log.d("View Pager", "View Pager  :"+viewPager+" , "+pageAdapter);
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(position);
    }

    @Override
    protected void onResume() {
        MixPanelController.track(EventKeysWL.MESSAGE_FULL_VIEW,null);
        super.onResume();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card__full__view__main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void cardrefresh(boolean flag) {
        Log.d("full Activity","True");
        pd.dismiss();
        CardAdapter_v2 adapter =  new CardAdapter_v2(null,Card_Full_View_MainActivity.this);
        adapter.notifyDataSetChanged();
        adapter.notifyItemRemoved(position);
        Card_Full_View_MainActivity.this.finish();

    }
}
