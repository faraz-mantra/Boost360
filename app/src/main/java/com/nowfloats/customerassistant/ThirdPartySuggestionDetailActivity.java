package com.nowfloats.customerassistant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.customerassistant.adapters.ThirdPartySharedItemAdapter;
import com.nowfloats.customerassistant.callbacks.ThirdPartyCallbacks;
import com.nowfloats.customerassistant.models.SharedSuggestionsDO;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Methods;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thinksity.R;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        findViewById(R.id.img_back).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        Intent intent = getIntent();
        mSuggestionDO = (SuggestionsDO) intent.getSerializableExtra("message");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            title.setText(mSuggestionDO.getAction());
        }


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

    private void init() {
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
        TextView callBtn = (TextView) findViewById(R.id.btn_call);
        callBtn.setOnClickListener(this);
        callBtn.setText(mSuggestionDO.getType().equalsIgnoreCase(ACTION_TYPE_NUMBER)?"CALL":"MAIL");
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
                //messageLayout.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

    private void shareSuggestionToCustomer() {
        if (mSuggestionDO.getType().equalsIgnoreCase(ACTION_TYPE_NUMBER)) {
            prepareMessageForShare(CallToActionFragment.SHARE_VIA.SMS);
        } else if (mSuggestionDO.getType().equalsIgnoreCase(ACTION_TYPE_EMAIL)) {
            prepareMessageForShare(CallToActionFragment.SHARE_VIA.GMAIL);
        }
        //shareSuggestionDo share
    }

    private void prepareMessageForShare(CallToActionFragment.SHARE_VIA share_via) {

            String selectedProducts = "";
            String imageUrl = "";

            for (SharedSuggestionsDO shareItem: sharedSuggestionsDOList) {
                try {
                    selectedProducts = selectedProducts + shareItem.getName() + "\n" + "\n" + "View Details : " + shareItem.getUrl()+ "\n" + "\n";
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
                        if(TextUtils.isEmpty(imageUrl)){
                            return;
                        }
                        Picasso.with(ThirdPartySuggestionDetailActivity.this)
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
                                    public void onBitmapFailed(Drawable errorDrawable) {
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

                    if (mSuggestionDO.getType().equalsIgnoreCase(ACTION_TYPE_NUMBER)) {
                        if (!TextUtils.isEmpty(mSuggestionDO.getValue())) {
                            Methods.makeCall(ThirdPartySuggestionDetailActivity.this, mSuggestionDO.getValue());
                        }else {
                            Methods.showSnackBarNegative(ThirdPartySuggestionDetailActivity.this, "Number is not valid");
                        }
                    }
                    else
                    {
                        if(!TextUtils.isEmpty(mSuggestionDO.getValue())) {
                            Methods.sendEmail(ThirdPartySuggestionDetailActivity.this, mSuggestionDO.getValue());
                        }
                        else{
                            Methods.showSnackBarNegative(ThirdPartySuggestionDetailActivity.this,"Email is not valid");
                        }

                    }
               break;
        }
    }
}

