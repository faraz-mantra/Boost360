package com.nowfloats.Analytics_Screen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.framework.views.fabButton.FloatingActionButton;
import com.google.gson.Gson;
import com.nowfloats.Analytics_Screen.API.SubscriberApis;
import com.nowfloats.Analytics_Screen.Search_Query_Adapter.SubscribersAdapter;
import com.nowfloats.Analytics_Screen.model.AddSubscriberModel;
import com.nowfloats.Analytics_Screen.model.SubscriberModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.framework.webengageconstant.EventLabelKt.ADDED;
import static com.framework.webengageconstant.EventLabelKt.ERROR_SUBSCRIBER;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_ADD_SUBSCRIBER;
import static com.framework.webengageconstant.EventLabelKt.NEWSLETTER_SUBSCRIPTIONS;
import static com.framework.webengageconstant.EventNameKt.ADD_SUBSCRIBER;
import static com.framework.webengageconstant.EventNameKt.ADD_SUBSCRIBER_FAILED;
import static com.framework.webengageconstant.EventNameKt.CLICKED_ON_NEWSLETTER_SUBSCRIPTIONS;
import static com.framework.webengageconstant.EventValueKt.NO_EVENT_VALUE;
import static com.framework.webengageconstant.EventValueKt.TO_BE_ADDED;

public class SubscribersActivity extends AppCompatActivity implements View.OnClickListener, SubscribersAdapter.SubscriberInterfaceMethods {

    private static final int SUBSCRIBER_REQUEST_CODE = 221;
    ArrayList<SubscriberModel> mSubscriberList = new ArrayList<>();
    SubscribersAdapter mSubscriberAdapter;
    TextView titleTextView;
    AutoCompleteTextView searchEditText;
    ImageView searchImage;
    FloatingActionButton deleteImage;
    LinearLayout emptyLayout;
    private UserSessionManager mSessionManager;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    private LinearLayoutManager mLayoutManager;
    private boolean stop;
    private int itemClicked = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebEngageController.trackEvent(CLICKED_ON_NEWSLETTER_SUBSCRIPTIONS, NEWSLETTER_SUBSCRIPTIONS, NO_EVENT_VALUE);
        MixPanelController.track(EventKeysWL.SIDE_PANEL_SUBSCRIBERS, null);
        setContentView(R.layout.activity_subscribers);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        searchEditText = (AutoCompleteTextView) findViewById(R.id.search_edittext);
        deleteImage = (FloatingActionButton) findViewById(R.id.btn_add);
        searchImage = (ImageView) findViewById(R.id.search_image);

        //autoCompleteAdapter = new SpinnerAdapter(this,searchList);
        //searchEditText.setAdapter(autoCompleteAdapter);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String key = searchEditText.getText().toString().trim();
                if (key.length() == 0) {

                    mRecyclerView.setAdapter(mSubscriberAdapter);
                } else {
                    searchSubcribers(key);
                }
            }
        });
        deleteImage.setOnClickListener(this);
        searchImage.setOnClickListener(this);

        titleTextView.setText(getResources().getString(R.string.subscriptions));
        emptyLayout = (LinearLayout) findViewById(R.id.emplty_layout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mProgressBar = (ProgressBar) findViewById(R.id.pb_subscriber);
        mRecyclerView = (RecyclerView) findViewById(R.id.lv_subscribers);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSubscriberAdapter = new SubscribersAdapter(this, mSubscriberList);
        mRecyclerView.setAdapter(mSubscriberAdapter);

        mSessionManager = new UserSessionManager(getApplicationContext(), SubscribersActivity.this);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int count = mLayoutManager.getItemCount();
                int visiblePosition = mLayoutManager.findLastVisibleItemPosition();
                if (visiblePosition >= count - 2 && !stop) {//call api when second last item visible
                    getSubscribersList();
                }
            }
        });

        getSubscribersList();
    }

    private void searchSubcribers(final String key) {
        //mProgressBar.setVisibility(View.VISIBLE);

        SubscriberApis mSubscriberApis = Constants.restAdapter.create(SubscriberApis.class);
        mSubscriberApis.search(key, key, Constants.clientId, mSessionManager.getFpTag(), new Callback<ArrayList<SubscriberModel>>() {
            @Override
            public void success(ArrayList<SubscriberModel> subscriberModels, Response response) {

                if (subscriberModels == null || response.getStatus() != 200) {
                    return;
                }

                //autoCompleteAdapter = new SpinnerAdapter(SubscribersActivity.this,subscriberModels);
                //searchEditText.setAdapter(autoCompleteAdapter);

                for (SubscriberModel model : mSubscriberList) {
                    if (!subscriberModels.contains(model) && model.getUserMobile().toLowerCase().contains(key.toLowerCase())) {
                        subscriberModels.add(model);
                    }
                }
                SubscribersAdapter adapter = new SubscribersAdapter(SubscribersActivity.this, subscriberModels);
                mRecyclerView.setAdapter(adapter);
                mProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("ggg", error.getMessage());
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getSubscribersList() {
        stop = true;
        final int count = mSubscriberList.size();
        String offset = String.valueOf(String.valueOf(count + 1));

        mProgressBar.setVisibility(View.VISIBLE);
        SubscriberApis mSubscriberApis = Constants.restAdapter.create(SubscriberApis.class);
        mSubscriberApis.getsubscribers(mSessionManager.getFpTag(), Constants.clientId, offset, new Callback<List<SubscriberModel>>() {
            @Override
            public void success(List<SubscriberModel> subscriberModels, Response response) {
                mProgressBar.setVisibility(View.GONE);
                if (subscriberModels == null) {
                    return;
                }
                int newItems = subscriberModels.size();

                for (int i = 0; i < newItems; i++) {
                    //Log.v("ggg",subscriberModels.get(i).getUserMobile());
                    mSubscriberList.add(subscriberModels.get(i));
                    mSubscriberAdapter.notifyItemChanged(count + i);
                }
                //autoCompleteAdapter.notifyDataSetChanged();
                //autoCompleteAdapter = new ArrayAdapter<SubscriberModel>(SubscribersActivity.this,android.R.layout.simple_list_item_activated_1,mSubscriberList);
                // searchEditText.setAdapter(autoCompleteAdapter);
                //Log.v("ggg","size "+autoCompleteAdapter.getCount()+" auto "+autoCompleteAdapter.toString()+" subscribe "+mSubscriberList.toString());
                if (newItems >= 10) {
                    stop = false;
                }
                if (mSubscriberList.size() == 0) {
                    emptyLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);
                Log.v("ggg", error.getMessage());
                Methods.showSnackBarNegative(SubscribersActivity.this, getString(R.string.something_went_wrong));
            }
        });
    }

    private void addSubscriber(final String email, final MaterialDialog dialog) {
        AddSubscriberModel model = new AddSubscriberModel();
        model.setClientId(Constants.clientId);
        model.setCountryCode(mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY));
        model.setFpTag(mSessionManager.getFpTag());
        model.setUserContact(email);
        mProgressBar.setVisibility(View.VISIBLE);
        SubscriberApis mSubscriberApis = Constants.restAdapter.create(SubscriberApis.class);
        mSubscriberApis.addSubscriber(model, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                mProgressBar.setVisibility(View.GONE);
                if (response.getStatus() == 200 || response.getStatus() == 201 || response.getStatus() == 202) {
                    WebEngageController.trackEvent(ADD_SUBSCRIBER, ADDED, TO_BE_ADDED);
                    mSubscriberList.clear();
                    mSubscriberAdapter.notifyDataSetChanged();
                    getSubscribersList();
                    Toast.makeText(SubscribersActivity.this, email + getString(R.string.successfully_added), Toast.LENGTH_SHORT).show();
                    if (!isFinishing()) {
                        dialog.dismiss();
                    }
                } else {
                    Methods.showSnackBarNegative(SubscribersActivity.this, getString(R.string.something_went_wrong_try_again));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("ggg", error.getMessage());
                mProgressBar.setVisibility(View.GONE);
                Methods.showSnackBarNegative(SubscribersActivity.this, getString(R.string.something_went_wrong_try_again));
                WebEngageController.trackEvent(ADD_SUBSCRIBER_FAILED, ERROR_SUBSCRIBER, mSessionManager.getFpTag());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_add:
                subscriberDialog();
                break;
            case R.id.search_image:
                titleTextView.setVisibility(View.GONE);
                searchImage.setVisibility(View.GONE);
                searchEditText.setVisibility(View.VISIBLE);
                searchEditText.requestFocus();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSubscriberAdapter.notifyDataSetChanged();
    }

    private boolean checkIsEmailOrNumber(String email) {
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher mat = pattern.matcher(email);
        return mat.matches();
    }

    private void subscriberDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_subscriber, null);
        final EditText email = (EditText) view.findViewById(R.id.edittext);
        new MaterialDialog.Builder(this)
                .customView(view, false)
                .positiveText("Add")
                .negativeText("Cancel")
                .negativeColorRes(R.color.gray_transparent)
                .positiveColorRes(R.color.colorAccentLight)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        if (!checkIsEmailOrNumber(email.getText().toString().trim())) {
                            Methods.showSnackBarNegative(SubscribersActivity.this, "Add only email Id");
                        } else {
                            addSubscriber(email.getText().toString().trim(), dialog);
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                }).build().show();
    }
    //method call when view changed from adapter

    @Override
    public void onitemSeleted(int pos) {
        itemClicked = pos;
        Intent i = new Intent(this, SubscriberDetailsActivity.class);
        i.putExtra("data", new Gson().toJson(mSubscriberList.get(pos)));
        i.putExtra("fpTag", mSessionManager.getFpTag());
        startActivityForResult(i, SUBSCRIBER_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SUBSCRIBER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (itemClicked != -1 && data != null) {
                mSubscriberList.get(itemClicked).setSubscriptionStatus(data.getStringExtra("STATUS"));
                mSubscriberAdapter.notifyItemChanged(itemClicked);
                itemClicked = -1;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (searchEditText.getVisibility() == View.VISIBLE) {
                    searchEditText.clearFocus();
                    searchEditText.setText("");
                    searchEditText.setVisibility(View.GONE);
                    titleTextView.setVisibility(View.VISIBLE);
                    searchImage.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                } else {
                    onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}