package com.nowfloats.CustomPage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.nowfloats.CustomPage.Model.CustomPageEvent;
import com.nowfloats.CustomPage.Model.CustomPageModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

/**
 * Created by guru on 25/08/2015.
 */
public class CustomPageActivity extends AppCompatActivity implements CustomPageDeleteInterface {
    public static RecyclerView recyclerView;
    public static CustomPageAdapter custompageAdapter;
    public CustomPageInterface pageInterface;
    Bus bus;
    public static ArrayList<CustomPageModel> dataModel = new ArrayList<>();
    public static ArrayList<String> posList = new ArrayList<>();
    private LinearLayout emptylayout,progress_layout;
    UserSessionManager session;
    Activity activity;
    private boolean customPageDeleteCheck = false;
    private TextView titleTextView;
    private ImageView delete;
    private Toolbar toolbar;
    private Drawable defaultColor;
//    private View deleteView = null;

    @Override
    public void onResume() {
        MixPanelController.track("CustomPages", null);
        super.onResume();
        bus.register(this);
        if (custompageAdapter!=null)
            custompageAdapter.notifyDataSetChanged();
        if (recyclerView!=null)
            recyclerView.invalidate();
        if (dataModel.size()==0){
            emptylayout.setVisibility(View.VISIBLE);
        }else {
            emptylayout.setVisibility(View.GONE);
        }
//        if (deleteView!=null){
//            deleteView.setBackgroundColor(android.R.attr.selectableItemBackground);
//            deleteView = null;
//        }

        posList = new ArrayList<String>();
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setBackgroundDrawable(defaultColor);
        titleTextView.setText("Custom Pages");
        delete.setVisibility(View.GONE);
        customPageDeleteCheck = false;

    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_custom_page);

        activity = CustomPageActivity.this;
        pageInterface = Constants.restAdapter.create(CustomPageInterface.class);
        session = new UserSessionManager(activity.getApplicationContext(),activity);
        bus = BusProvider.getInstance().getBus();

        toolbar = (Toolbar) findViewById(R.id.tool_bar_product_detail);
       // defaultColor = activity.getResources().getColor(R.color.primaryColor);
//        toolbar.setBackgroundResource(defaultColor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        defaultColor = new ColorDrawable(getResources().getColor(R.color.primaryColor));
        getSupportActionBar().setBackgroundDrawable(defaultColor);
        getSupportActionBar().setTitle("Custom Pages");
        //Title
        titleTextView = (TextView) toolbar.findViewById(R.id.titleProduct);
        titleTextView.setText("Custom Pages");
        delete = (ImageView)toolbar.findViewById(R.id.home_view_delete_card);
        delete.setBackgroundResource(0);
        delete.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.custompage_recycler_view);
        recyclerView.setHasFixedSize(true);
        String[] arrayList = null;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.design_child_store_feature, R.id.text, arrayList);

        emptylayout = (LinearLayout) findViewById(R.id.emptycustompage);
        progress_layout = (LinearLayout) findViewById(R.id.progress_custom_page);
        progress_layout.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setItemAnimator(new FadeInUpAnimator());

        LoadPageList(activity, bus);

        final FloatingActionButton addProduct =(FloatingActionButton)findViewById(R.id.fab_custom_page);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("AddCustomPage", null);
                Intent intent = new Intent(activity,CreateCustomPageActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void LoadPageList(Activity activity, Bus bus) {
        new CustomPageService().GetPages(activity,session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG),
                Constants.clientId,pageInterface,bus);
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);

//        String[] columns = new String[] { "_id", "text", "description" };
//        MatrixCursor matrixCursor= new MatrixCursor(columns);
//        startManagingCursor(matrixCursor);

//        matrixCursor.addRow(new Object[] { 1, "Item A", "...." });
//        matrixCursor.addRow(new Object[] { 2, "Item B", "...." });
//        matrixCursor.addRow(new Object[] { 3, "Item C", "...." });
//        matrixCursor.addRow(new Object[] { 4, "Item D", "...." });
//        DragNDropListView list = (DragNDropListView)view.findViewById(R.id.custompage_recycler_view);

//        DragNDropCursorAdapter adapter = new DragNDropCursorAdapter(activity,
//                R.layout.custom_page_list_design,
//                matrixCursor,
//                new String[]{"text"},
//                new int[]{R.id.page_name},
//                R.id.full_layout_card);
//
//        list.setDragNDropAdapter(adapter);
//    }

    @Subscribe
    public void getPageList(CustomPageEvent response){
        dataModel = (ArrayList<CustomPageModel>)response.model;
        if(dataModel!=null){
            if (dataModel.size()==0){
                emptylayout.setVisibility(View.VISIBLE);
            }else {
                emptylayout.setVisibility(View.GONE);
            }
            progress_layout.setVisibility(View.GONE);
            custompageAdapter = new CustomPageAdapter(activity, dataModel, session, pageInterface);
//            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(custompageAdapter);
//            ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
//            scaleAdapter.setFirstOnly(false);
//            scaleAdapter.setInterpolator(new OvershootInterpolator());
            recyclerView.setAdapter(custompageAdapter);
            custompageAdapter.notifyDataSetChanged();
            recyclerView.invalidate();
        }else{
            emptylayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home && customPageDeleteCheck){
//            if (deleteView!=null){
//                deleteView.setBackgroundColor(android.R.attr.selectableItemBackground);
//                deleteView = null;
//            }
            CustomPageAdapter.deleteCheck = false;
            posList = new ArrayList<String>();
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setBackgroundDrawable(defaultColor);
            titleTextView.setText("Custom Pages");
            delete.setVisibility(View.GONE);
            customPageDeleteCheck = false;

            if (custompageAdapter!=null)
                custompageAdapter.notifyDataSetChanged();
            if (recyclerView!=null)
                recyclerView.invalidate();

        }else if(id==android.R.id.home){
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(customPageDeleteCheck){
//            if (deleteView!=null){
//                deleteView.setBackgroundColor(android.R.attr.selectableItemBackground);
//                deleteView = null;
//            }
            CustomPageAdapter.deleteCheck = false;
            posList = new ArrayList<String>();
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setBackgroundDrawable(defaultColor);
            titleTextView.setText("Custom Pages");
            delete.setVisibility(View.GONE);
            customPageDeleteCheck = false;

            if (custompageAdapter!=null)
                custompageAdapter.notifyDataSetChanged();
            if (recyclerView!=null)
                recyclerView.invalidate();
        }else {
            super.onBackPressed();
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    @Override
    public void DeletePageTrigger(final int position, boolean chk,View v) {
        if (chk){
//        deleteView = v;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.gray_transparent)));
        String size = "";
        if (posList.size()>0){
            size = posList.size()+"";
        }
        titleTextView.setText(size+" PAGE SELECTED");
        delete.setVisibility(View.VISIBLE);
        customPageDeleteCheck = true;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (deleteView!=null){
//                    deleteView.setBackgroundColor(android.R.attr.selectableItemBackground);
//                    deleteView = null;
//                }
                new MaterialDialog.Builder(activity)
                        .title("Are you sure to Delete?")
                        .content("Page will be deleted")
                        .positiveText("DELETE")
                        .negativeText("CANCEL")
                        .positiveColorRes(R.color.primaryColor)
                        .negativeColorRes(R.color.grey)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                try {

                                    String url = Constants.NOW_FLOATS_API_URL + "/Discover/v1/floatingpoint/custompage/delete";
                                    for (int i = 0; i < posList.size(); i++) {
                                        new PageDeleteAsyncTaask(url, activity,session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG),pageInterface,bus).execute();
                                    }
                                    dialog.dismiss();
                                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                                    getSupportActionBar().setBackgroundDrawable(defaultColor);
                                    titleTextView.setText("Custom Pages");
                                    delete.setVisibility(View.GONE);
                                    customPageDeleteCheck = false;
                                    CustomPageAdapter.deleteCheck = false;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    posList = new ArrayList<String>();
                                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                                    getSupportActionBar().setBackgroundDrawable(defaultColor);
                                    titleTextView.setText("Custom Pages");
                                    delete.setVisibility(View.GONE);
                                    customPageDeleteCheck = false;
                                    CustomPageAdapter.deleteCheck = false;
                                    if (custompageAdapter!=null)
                                        custompageAdapter.notifyDataSetChanged();
                                    if (recyclerView!=null)
                                        recyclerView.invalidate();

                                }
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                                dialog.dismiss();
                                posList = new ArrayList<String>();
                                getSupportActionBar().setDisplayShowTitleEnabled(false);
                                getSupportActionBar().setBackgroundDrawable(defaultColor);
                                titleTextView.setText("Custom Pages");
                                delete.setVisibility(View.GONE);
                                customPageDeleteCheck = false;
                                CustomPageAdapter.deleteCheck = false;
                                if (custompageAdapter!=null)
                                    custompageAdapter.notifyDataSetChanged();
                                if (recyclerView!=null)
                                    recyclerView.invalidate();
                            }
                        })
                        .show();
            }
        });
    }else{
            String size = "";
            if (posList.size()>0){
                size = posList.size()+"";
                titleTextView.setText(size+" PAGE SELECTED");
            }
            if(posList.size()==0){
                posList = new ArrayList<String>();
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setBackgroundDrawable(defaultColor);
                titleTextView.setText("Custom Pages");
                delete.setVisibility(View.GONE);
                customPageDeleteCheck = false;
                CustomPageAdapter.deleteCheck = false;
                if (custompageAdapter!=null)
                    custompageAdapter.notifyDataSetChanged();
                if (recyclerView!=null)
                    recyclerView.invalidate();
            }

        }
    }
}