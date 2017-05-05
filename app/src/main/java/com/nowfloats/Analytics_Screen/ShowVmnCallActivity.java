package com.nowfloats.Analytics_Screen;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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

public class ShowVmnCallActivity extends AppCompatActivity implements VmnCallAdapter.RequestPermission, View.OnClickListener {


    ExpandableListView expList;
    Toolbar toolbar;
    VmnCallAdapter adapter, searchAdapter;
    final static int REQUEST_PERMISSION = 202;
    ImageView searchImage;
    TextView title;
    AutoCompleteTextView autoTextView;
    ArrayList<ArrayList<VmnCallModel>> sortedList;
    ArrayList<ArrayList<VmnCallModel>> searchList = new ArrayList<>();
    boolean isSearchAdapter = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vmn_calls);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        expList = (ExpandableListView) findViewById(R.id.exp_list);
        title = (TextView) findViewById(R.id.titleTextView);
        searchImage = (ImageView) findViewById(R.id.search_image);
        autoTextView = (AutoCompleteTextView) findViewById(R.id.search_edittext);
        autoTextView.setInputType(InputType.TYPE_CLASS_PHONE);
        searchAdapter = new VmnCallAdapter(this, searchList);
        Intent intent = getIntent();
        if (intent != null) {
            Type type = new TypeToken<ArrayList<VmnCallModel>>() {
            }.getType();
            ArrayList<VmnCallModel> list = new Gson().fromJson(intent.getStringExtra("calls"), type);
            numberWiseSeparation(list);
        } else {
            return;
        }
        searchImage.setOnClickListener(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            title.setText("Call Logs");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        autoTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.v("ggg", isSearchAdapter + " ");
                String key = autoTextView.getText().toString().trim();
                if (key.length() == 0) {
                    if (adapter != null && isSearchAdapter) {
                        expList.setAdapter(adapter);
                        isSearchAdapter = false;
                    }
                    Log.v("ggg", sortedList.size() + " ");
                } else {
                    autoCompleteSearch(key);
                    Log.v("ggg", searchList.size() + " ");
                }
            }
        });

    }


    private void autoCompleteSearch(String key) {
        if (searchList.size() > 0) {
            searchList.clear();
        }
        for (ArrayList<VmnCallModel> list : sortedList) {
            if (list.get(0).getCallerNumber().contains(key)) {
                searchList.add(list);
            }
        }
        searchAdapter.notifyDataSetChanged();
        if (!isSearchAdapter) {
            expList.setAdapter(searchAdapter);
            isSearchAdapter = true;
        }
    }

    private void numberWiseSeparation(ArrayList<VmnCallModel> list) {
        HashMap<Object, ArrayList<VmnCallModel>> hashMap = new HashMap<>();
        for (VmnCallModel model : list) {

            if (!hashMap.containsKey(model.getCallerNumber())) {
                ArrayList<VmnCallModel> subList = new ArrayList<>();
                subList.add(model);

                hashMap.put(model.getCallerNumber(), subList);
            } else {
                hashMap.get(model.getCallerNumber()).add(0, model);
            }
        }
        sortedList = new ArrayList<>(hashMap.values());
        Collections.sort(sortedList, new Comparator<ArrayList<VmnCallModel>>() {
            @Override
            public int compare(ArrayList<VmnCallModel> o1, ArrayList<VmnCallModel> o2) {
                String first = o1.get(0).getCallDateTime();
                String second = o2.get(0).getCallDateTime();
                return second.compareToIgnoreCase(first);
            }
        });
        adapter = new VmnCallAdapter(this, sortedList);
        expList.setAdapter(adapter);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            if (autoTextView.getVisibility() == View.VISIBLE) {
                autoTextView.setText("");
                autoTextView.clearFocus();
                autoTextView.setVisibility(View.GONE);
                title.setVisibility(View.VISIBLE);
                searchImage.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoTextView.getWindowToken(), 0);
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (adapter != null && adapter.connectToVmn != null) {
            adapter.connectToVmn.releaseResources();
        }
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public void gotoSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            new MaterialDialog.Builder(this)
                    .title("Recording Download")
                    .content("we need write to external storage permission to download this file.")
                    .negativeColorRes(R.color.gray_transparent)
                    .negativeText(getString(R.string.cancel))
                    .positiveColorRes(R.color.primary_color)
                    .positiveText(getString(R.string.open_setting))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            dialog.dismiss();
                            gotoSetting();
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_image:
                title.setVisibility(View.GONE);
                searchImage.setVisibility(View.GONE);
                autoTextView.setVisibility(View.VISIBLE);
                autoTextView.requestFocus();
                break;
        }
    }
}
