package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.Home_View_Card_Delete;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_NULL;
import static com.framework.webengageconstant.EventNameKt.DELETE_AN_UPDATE;

public class Card_Full_View_MainActivity extends AppCompatActivity implements Home_View_Card_Delete.CardRefresh {

    PageAdapter pageAdapter;
    ViewPager viewPager ;
    private boolean isDashboard;
    private Toolbar toolbar;
    ImageView deleteButton;
    private String cardId;
    int position;
    private ProgressDialog pd;
    private UserSessionManager session;

    public static ArrayList<FloatsMessageModel> getMessageList(boolean isDashboard) {
        if (!isDashboard) return HomeActivity.StorebizFloats;
        else return Home_Main_Fragment.StorebizFloats;
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card__full__view__main);
        session = new UserSessionManager(this, this);
        Methods.isOnline(Card_Full_View_MainActivity.this);
        viewPager = findViewById(R.id.viewPager);

        Bundle extras = getIntent().getExtras();
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        position = extras.getInt("POSITION");
        isDashboard = extras.getBoolean("IS_DASHBOARD",false);
        BoostLog.d("POSITION: ", position + "");

        deleteButton = toolbar.findViewById(R.id.home_view_delete_card);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (!Methods.isOnline(Card_Full_View_MainActivity.this)) {
                    return;
                }

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
                                WebEngageController.trackEvent(DELETE_AN_UPDATE,EVENT_LABEL_NULL,null);

                                try {
                                    cardId = getMessageList(isDashboard).get(viewPager.getCurrentItem())._id;
                                    JSONObject content = new JSONObject();
                                    content.put("dealId", cardId);
                                    content.put("clientId", Constants.clientId1);


                                    Home_View_Card_Delete obj =  new Home_View_Card_Delete(Card_Full_View_MainActivity.this,
                                            Constants.DeleteCard,content,viewPager.getCurrentItem(),v,0);
//                                    obj.CardRefresh_Listener(this);
                                    obj.setDashboard(isDashboard);
                                    obj.execute();

                                    pd = ProgressDialog.show(Card_Full_View_MainActivity.this, null,
                                            getString(R.string.deleting_message));
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
        List<FloatsMessageModel> floatsMessageModels = new ArrayList<>(getMessageList(isDashboard));
        pageAdapter = new PageAdapter(getSupportFragmentManager(), floatsMessageModels,Card_Full_View_MainActivity.this);
        //  Log.d("View Pager", "View Pager  :"+viewPager+" , "+pageAdapter);
        viewPager.setAdapter(pageAdapter);
        pageAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(position);
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

    @Override
    public void error(boolean flag) {
        Log.d("full Activity",""+flag);
        pd.dismiss();
    }
}
