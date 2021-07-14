package com.nowfloats.customerassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nowfloats.customerassistant.adapters.ThirdPartySharedItemAdapter;
import com.nowfloats.customerassistant.callbacks.ThirdPartyCallbacks;
import com.nowfloats.customerassistant.models.MessageDO;
import com.nowfloats.customerassistant.models.SharedSuggestionsDO;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Admin on 10-10-2017.
 */

public class ThirdPartySuggestionDetailActivity extends AppCompatActivity implements ThirdPartyCallbacks, View.OnClickListener {

    public static final int ADD_PRODUCTS = 0, ADD_UPDATES = 2, SHOW_MESSAGE = -1;
    private final String ACTION_TYPE_NUMBER = "contactNumber";

    private final String ACTION_TYPE_EMAIL = "email";
    SuggestionsDO mSuggestionDO;
    LinearLayout messageLayout;
    FrameLayout fragmentLayout;
    List<SharedSuggestionsDO> sharedSuggestionsDOList = new ArrayList<>();
    ThirdPartySharedItemAdapter mSuggestionAdapter;
    private String appVersion = "";
    private SharedPreferences pref;
    private int noOfTimesResponded = 0;
    //private Bus mBus;
    //private CustomerAssistantApi customerApis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        findViewById(R.id.img_back).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        Intent intent = getIntent();
        mSuggestionDO = (SuggestionsDO) intent.getSerializableExtra("message");
        //mBus = BusProvider.getInstance().getBus();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            title.setText(mSuggestionDO.getActualMessage());
        }

        try {
            appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //customerApis = new CustomerAssistantApi(mBus);
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        noOfTimesResponded = pref.getInt(Key_Preferences.NO_OF_TIMES_RESPONDED, 0);
        /*try {
         *//*shareSuggestionDo = (SuggestionsDO) mSuggestionDO.clone();
            shareSuggestionDo.setUpdates(new ArrayList<SugUpdates>());
            shareSuggestionDo.setProducts(new ArrayList<SugProducts>());*//*
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }*/
        init();
        addFragments(SHOW_MESSAGE);
        MixPanelController.track(MixPanelController.THIRD_PARTY_DATA_DETAIL, null);
    }

    private void init() {
        TextView addressText = (TextView) findViewById(R.id.tv_address);
        TextView timeText = (TextView) findViewById(R.id.tv_time);
        EditText messageEdit = (EditText) findViewById(R.id.et_message);
        ImageView sourceImg = findViewById(R.id.img_source);
        messageEdit.setSelection(messageEdit.getText().length());
        addressText.setText(mSuggestionDO.getValue());
        timeText.setText(Methods.getFormattedDate(mSuggestionDO.getDate(), "dd MMM, hh:mm a"));
        messageLayout = (LinearLayout) findViewById(R.id.layout_message);
        fragmentLayout = (FrameLayout) findViewById(R.id.layout_fragment);
        RecyclerView suggestionsRecyclerView = (RecyclerView) findViewById(R.id.rv_suggestions);
        suggestionsRecyclerView.setHasFixedSize(true);
        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(ThirdPartySuggestionDetailActivity.this));
        mSuggestionAdapter = new ThirdPartySharedItemAdapter(ThirdPartySuggestionDetailActivity.this, sharedSuggestionsDOList);
        suggestionsRecyclerView.setAdapter(mSuggestionAdapter);
        findViewById(R.id.btn_send).setOnClickListener(this);
        TextView callBtn = (TextView) findViewById(R.id.btn_call);
        callBtn.setOnClickListener(this);
        timeText.setText(Methods.getFormattedDate(mSuggestionDO.getDate(), "dd MMM, hh:mm a"));
        callBtn.setText(mSuggestionDO.getType().equalsIgnoreCase(ACTION_TYPE_NUMBER) ? "CALL" : "MAIL");
        if (!TextUtils.isEmpty(mSuggestionDO.getLogoUrl()) && mSuggestionDO.getLogoUrl().contains("http")) {
            Glide.with(this)
                    .load(mSuggestionDO.getLogoUrl())
                    .into(sourceImg);
        } else {
            sourceImg.setVisibility(View.GONE);

        }
        TextView productButton = findViewById(R.id.btn_add_products);
        TextView updateButton = findViewById(R.id.btn_add_updates);
        if (mSuggestionDO.getProducts().size() == 0) {
            productButton.setVisibility(View.GONE);
        } else {
            productButton.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this, R.drawable.ic_plus_btn), null, null, null);
            productButton.setOnClickListener(this);
        }
        if (mSuggestionDO.getUpdates().size() == 0) {
            updateButton.setVisibility(View.GONE);
        } else {
            updateButton.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this, R.drawable.ic_plus_btn), null, null, null);
            updateButton.setOnClickListener(this);
        }
    }

    private void addFragments(int screen) {
        FragmentManager manager = getSupportFragmentManager();
        switch (screen) {
            case ADD_UPDATES:
            case ADD_PRODUCTS:
                fragmentLayout.setVisibility(View.VISIBLE);
                Methods.hideKeyboard(this);
                Bundle b = new Bundle();
                b.putInt("type", screen);
                b.putSerializable("message", mSuggestionDO);
                if (screen == ADD_PRODUCTS) {
                    if (mSuggestionDO.getProducts().size() == 0) {
                        Toast.makeText(ThirdPartySuggestionDetailActivity.this, getString(R.string.no_product_found_for_this_category), Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else if (mSuggestionDO.getUpdates().size() == 0) {
                    Toast.makeText(ThirdPartySuggestionDetailActivity.this, getString(R.string.no_update_found_for_this_query), Toast.LENGTH_SHORT).show();
                    break;
                }

                manager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_out_up1, R.anim.slide_to_down)
                        .replace(R.id.layout_fragment, ShowThirdPartyProductsFragment.getInstance(b))
                        .addToBackStack(null)
                        .commit();
                break;
            case SHOW_MESSAGE:
                if (fragmentLayout.getVisibility() == View.VISIBLE) {
                    manager.popBackStack();
                    fragmentLayout.setVisibility(View.GONE);
                }
                break;

            default:
                break;
        }
    }

    private void shareSuggestionToCustomer() {
        FirebaseLogger.getInstance().logSAMEvent(mSuggestionDO.getMessageId(), FirebaseLogger.SAMSTATUS.ACTION_SHARE, mSuggestionDO.getFpId(), appVersion);


        mSuggestionDO.setStatus(1);
        updateActionsToServer(mSuggestionDO);
        JSONObject json = new JSONObject();

        try {
            json.put("sharedData", sharedSuggestionsDOList.size());
            json.put("messageId", mSuggestionDO.getMessageId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MixPanelController.track(MixPanelController.THIRD_PARTY_ACTION_SHARE, json);
        if (mSuggestionDO.getType().equalsIgnoreCase(ACTION_TYPE_NUMBER)) {
            prepareMessageForShare(CallToActionFragment.SHARE_VIA.SMS);
        } else if (mSuggestionDO.getType().equalsIgnoreCase(ACTION_TYPE_EMAIL)) {
            prepareMessageForShare(CallToActionFragment.SHARE_VIA.GMAIL);
        }
    }

    public void updateActionsToServer(SuggestionsDO mSuggestionsDO) {

        ArrayList<MessageDO> arrMessageDO = new ArrayList<>();

        MessageDO messageDO = new MessageDO();
        messageDO.setMessageId(mSuggestionsDO.getMessageId());
        messageDO.setFpId(mSuggestionsDO.getFpId());
        messageDO.setStatus(mSuggestionsDO.getStatus());
        arrMessageDO.add(messageDO);

        //customerApis.updateMessage(arrMessageDO);
    }

    private void prepareMessageForShare(CallToActionFragment.SHARE_VIA share_via) {

        String selectedProducts = "";
        String imageUrl = "";

        for (SharedSuggestionsDO shareItem : sharedSuggestionsDOList) {
            try {
                selectedProducts = selectedProducts + shareItem.getName() + "\n" + "\n" + getString(R.string.view_details_) + shareItem.getUrl() + "\n" + "\n";
                imageUrl = shareItem.getImage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(selectedProducts)) {
            Methods.showSnackBarNegative(this, getString(R.string.select_update_or_product));
        } else {
            switch (share_via) {
                case GMAIL:
                    final Intent shareIntent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:" +
                            mSuggestionDO.getValue()));
                    final String finalSelectedProducts = selectedProducts;
                    if (TextUtils.isEmpty(imageUrl)) {
                        shareIntent.putExtra(Intent.EXTRA_TEXT, finalSelectedProducts);
                        shareIntent.setType("text/plain");

                        pref.edit().putInt(Key_Preferences.NO_OF_TIMES_RESPONDED, ++noOfTimesResponded).apply();
                        if (shareIntent.resolveActivity(ThirdPartySuggestionDetailActivity.this.getPackageManager()) != null) {
                            startActivity(shareIntent);
                        } else {
                            Methods.showSnackBarNegative(ThirdPartySuggestionDetailActivity.this,
                                    getString(R.string.no_app_available_for_action));
                        }
                        return;
                    }
                    Picasso.get()
                            .load(imageUrl)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                    View view = new View(ThirdPartySuggestionDetailActivity.this);
                                    view.draw(new Canvas(mutableBitmap));
                                    try {
                                        String path = MediaStore.Images.Media.insertImage(ThirdPartySuggestionDetailActivity.this.getContentResolver(), mutableBitmap, "Nur", null);
                                        BoostLog.d("Path is:", path);
                                        Uri uri = Uri.parse(path);
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, finalSelectedProducts);
                                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                        shareIntent.setType("image/*");

                                        pref.edit().putInt(Key_Preferences.NO_OF_TIMES_RESPONDED, ++noOfTimesResponded).apply();
                                        if (shareIntent.resolveActivity(ThirdPartySuggestionDetailActivity.this.getPackageManager()) != null) {
                                            startActivity(shareIntent);
                                        } else {
                                            Methods.showSnackBarNegative(ThirdPartySuggestionDetailActivity.this,
                                                    getString(R.string.no_app_available_for_action));
                                        }

                                    } catch (Exception e) {
                                        ActivityCompat.requestPermissions(ThirdPartySuggestionDetailActivity.this
                                                , new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                        android.Manifest.permission.CAMERA}, 2);
                                    }
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                    Methods.showSnackBarNegative(ThirdPartySuggestionDetailActivity.this, getString(R.string.failed_to_download_image));

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                    break;
                case SMS:
                    try {

                        Uri uri = Uri.parse("smsto:" + mSuggestionDO.getValue());
                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                        smsIntent.putExtra("address", mSuggestionDO.getValue());
                        smsIntent.putExtra("sms_body", selectedProducts);
                        startActivity(smsIntent);
                        pref.edit().putInt(Key_Preferences.NO_OF_TIMES_RESPONDED, ++noOfTimesResponded).apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (fragmentLayout.getVisibility() == View.VISIBLE) {
            getSupportFragmentManager().popBackStack();
            fragmentLayout.setVisibility(View.GONE);
            messageLayout.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();

        }

    }

    @Override
    public void addSuggestions(int type, ArrayList<Integer> positions) {

        if (type != SHOW_MESSAGE && positions.size() == 0) {
            String item = type == ADD_PRODUCTS ? "product" : "update";
            Toast.makeText(ThirdPartySuggestionDetailActivity.this, getString(R.string.please_select_at_least_one) + item, Toast.LENGTH_SHORT).show();
            return;
        }
        switch (type) {

            case ADD_PRODUCTS:
                for (int i : positions) {
                    SharedSuggestionsDO suggestionsDO =
                            new SharedSuggestionsDO(mSuggestionDO.getProducts().get(i).getProductName(),
                                    mSuggestionDO.getProducts().get(i).getProductUrl());
                    if (!sharedSuggestionsDOList.contains(suggestionsDO))
                        sharedSuggestionsDOList.add(suggestionsDO);
                }
                mSuggestionAdapter.notifyDataSetChanged();
                break;
            case ADD_UPDATES:

                for (int i : positions) {
                    SharedSuggestionsDO suggestionsDO =
                            new SharedSuggestionsDO(mSuggestionDO.getUpdates().get(i).getName(),
                                    mSuggestionDO.getUpdates().get(i).getUpdateUrl());
                    if (!sharedSuggestionsDOList.contains(suggestionsDO))
                        sharedSuggestionsDOList.add(suggestionsDO);
                }
                mSuggestionAdapter.notifyDataSetChanged();
                break;
        }
        addFragments(SHOW_MESSAGE);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_send:
                shareSuggestionToCustomer();
                break;
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.btn_add_products:
                addFragments(ADD_PRODUCTS);
                break;
            case R.id.btn_add_updates:
                addFragments(ADD_UPDATES);
                break;
            case R.id.btn_call:
                JSONObject json = new JSONObject();

                try {
                    json.put("messageId", mSuggestionDO.getMessageId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (mSuggestionDO.getType().equalsIgnoreCase(ACTION_TYPE_NUMBER)) {
                    if (!TextUtils.isEmpty(mSuggestionDO.getValue())) {
                        FirebaseLogger.getInstance().logSAMEvent(mSuggestionDO.getMessageId(), FirebaseLogger.SAMSTATUS.ACTION_CALL, mSuggestionDO.getFpId(), appVersion);
                        MixPanelController.track(MixPanelController.THIRD_PARTY_ACTION_CALL, json);

                        pref.edit().putInt(Key_Preferences.NO_OF_TIMES_RESPONDED, ++noOfTimesResponded).apply();

                        mSuggestionDO.setStatus(1);
                        //updateActionsToServer(mSuggestionDO);
                        Methods.makeCall(ThirdPartySuggestionDetailActivity.this, mSuggestionDO.getValue());
                    } else {
                        Methods.showSnackBarNegative(ThirdPartySuggestionDetailActivity.this, getString(R.string.number_is_not_valid));
                    }
                } else {
                    if (!TextUtils.isEmpty(mSuggestionDO.getValue())) {
                        FirebaseLogger.getInstance().logSAMEvent(mSuggestionDO.getMessageId(), FirebaseLogger.SAMSTATUS.ACTION_CALL, mSuggestionDO.getFpId(), appVersion);
                        MixPanelController.track(MixPanelController.THIRD_PARTY_ACTION_CALL, json);
                        pref.edit().putInt(Key_Preferences.NO_OF_TIMES_RESPONDED, ++noOfTimesResponded).apply();

                        mSuggestionDO.setStatus(1);
                        //updateActionsToServer(mSuggestionDO);
                        Methods.sendEmail(ThirdPartySuggestionDetailActivity.this, new String[]{mSuggestionDO.getValue()}, "");
                    } else {
                        Methods.showSnackBarNegative(ThirdPartySuggestionDetailActivity.this, getString(R.string.email_is_not_valid));
                    }

                }
                break;
        }
    }
}

