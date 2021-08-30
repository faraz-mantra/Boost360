package com.nowfloats.hotel.seasonalOffers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boost.upgrades.UpgradeActivity;
import com.framework.views.fabButton.FloatingActionButton;
import com.framework.views.zero.old.AppFragmentZeroCase;
import com.framework.views.zero.old.AppOnZeroCaseClicked;
import com.framework.views.zero.old.AppRequestZeroCaseBuilder;
import com.framework.views.zero.old.AppZeroCases;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.hotel.API.HotelAPIInterfaces;
import com.nowfloats.hotel.API.model.DeleteOffer.DeleteOfferRequest;
import com.nowfloats.hotel.API.model.GetOffers.Data;
import com.nowfloats.hotel.API.model.GetOffers.GetOffersResponse;
import com.nowfloats.hotel.Interfaces.SeasonalOffersListener;
import com.nowfloats.hotel.seasonalOffers.adapter.SeasonalOffersAdapter;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;
import com.thinksity.databinding.ActivitySeasonalOffersBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import static com.nowfloats.util.Key_Preferences.GET_FP_DETAILS_CATEGORY;

public class SeasonalOffersActivity extends AppCompatActivity implements SeasonalOffersListener, AppOnZeroCaseClicked {

    UserSessionManager session;
    SeasonalOffersAdapter adapter;
    RecyclerView recyclerView;
    List<Data> dataList;
    ActivitySeasonalOffersBinding binding;
    String WIDGET_KEY="OFFERS";
    private AppFragmentZeroCase appFragmentZeroCase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new UserSessionManager(this, this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_seasonal_offers);
        appFragmentZeroCase =new AppRequestZeroCaseBuilder(AppZeroCases.SEASONAL_OFFERS,this,this,isPremium()).getRequest().build();
        getSupportFragmentManager().beginTransaction().add(binding.childContainer.getId(),appFragmentZeroCase).commit();


        initView();
    }

    private Boolean isPremium(){
        return session.getStoreWidgets().contains(WIDGET_KEY);
    }

    private void nonEmptyView() {
        binding.mainlayout.setVisibility(View.VISIBLE);
        binding.childContainer.setVisibility(View.GONE);
    }


    private void emptyView() {
        binding.mainlayout.setVisibility(View.GONE);
        binding.childContainer.setVisibility(View.VISIBLE);

    }
    private void initView() {

        adapter = new SeasonalOffersAdapter(new ArrayList(), this);
        recyclerView = findViewById(R.id.recycler_view);


        //setHeader
        setHeader();

        //this feature is free to use
//        if (Constants.StoreWidgets.contains("OFFERS")) {
        recyclerView.setVisibility(View.VISIBLE);
        initialiseRecycler();
//        }else{
//            recyclerView.setVisibility(View.GONE);
//            secondaryLayout.setVisibility(View.VISIBLE);
//            showSecondaryLayout();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //this feature is free to use
//        if (Constants.StoreWidgets.contains("OFFERS")) {
//        }

        if (isPremium()){
            nonEmptyView();
            loadData();
        }else {
            emptyView();
        }
    }



    void loadData() {
        try {
            JSONObject query = new JSONObject();
            query.put("WebsiteId", session.getFpTag());
            HotelAPIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(HotelAPIInterfaces.class);

            APICalls.getOffersList(query, 0, 1000, new Callback<GetOffersResponse>() {
                @Override
                public void success(GetOffersResponse getOffersResponse, Response response) {
                    if (getOffersResponse == null || response.getStatus() != 200) {
                        Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    dataList = getOffersResponse.getData();
                    if (dataList.size() > 0) {
                        updateRecyclerView();
                        nonEmptyView();
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView();
                        recyclerView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Methods.showSnackBarNegative(SeasonalOffersActivity.this, getString(R.string.something_went_wrong));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initialiseRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void updateRecyclerView() {
        adapter.menuOption(-1, false);
        adapter.updateList(dataList);
        adapter.notifyDataSetChanged();
    }


    public void setHeader() {
        LinearLayout  backButton;
        ImageView rightIcon;
        TextView title;
        FloatingActionButton btnAdd;

        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        btnAdd = findViewById(R.id.btn_add);
        rightIcon = findViewById(R.id.right_icon);
        rightIcon.setVisibility(View.INVISIBLE);
        title.setText(R.string.seasional_offer_n);
        //this feature is free to use
//        if (Constants.StoreWidgets.contains("OFFERS")) {
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SeasonalOffersDetailsActivity.class);
            intent.putExtra("ScreenState", "new");
            startActivity(intent);
        });
//        }

        backButton.setOnClickListener(v -> onBackPressed());
    }


    @Override
    public void itemMenuOptionStatus(int pos, boolean status) {
        adapter.menuOption(pos, status);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void editOptionClicked(Data data) {
        Intent intent = new Intent(getApplicationContext(), SeasonalOffersDetailsActivity.class);
        intent.putExtra("ScreenState", "edit");
        intent.putExtra("data", new Gson().toJson(data));
        startActivity(intent);
    }

    @Override
    public void deleteOptionClicked(Data data) {
        try {
            DeleteOfferRequest requestBody = new DeleteOfferRequest();
            requestBody.setQuery("{_id:'" + data.getId() + "'}");
            requestBody.setUpdateValue("{$set : {IsArchived: true }}");
            requestBody.setMulti(true);

            HotelAPIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .setConverter(new GsonConverter(new GsonBuilder().setLenient().create()))
                    .build()
                    .create(HotelAPIInterfaces.class);

            APICalls.deleteOffer(requestBody, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    if (response != null && response.getStatus() == 200) {
                        Log.d("deletePlacesAround ->", response.getBody().toString());
                        Methods.showSnackBarPositive(SeasonalOffersActivity.this, getString(R.string.successfully_deleted_));
                        loadData();
                    } else {
                        Methods.showSnackBarNegative(SeasonalOffersActivity.this, getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getResponse().getStatus() == 200) {
                        Methods.showSnackBarPositive(SeasonalOffersActivity.this, getString(R.string.successfully_deleted_));
                        loadData();
                    } else {
                        Methods.showSnackBarNegative(SeasonalOffersActivity.this, getString(R.string.something_went_wrong));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initiateBuyFromMarketplace() {
        ProgressDialog progressDialog = new ProgressDialog(SeasonalOffersActivity.this);
        String status = "Loading. Please wait...";
        progressDialog.setMessage(status);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Intent intent = new Intent(SeasonalOffersActivity.this, UpgradeActivity.class);
        intent.putExtra("expCode", session.getFP_AppExperienceCode());
        intent.putExtra("fpName", session.getFPName());
        intent.putExtra("fpid", session.getFPID());
        intent.putExtra("fpTag", session.getFpTag());
        intent.putExtra("accountType", session.getFPDetails(GET_FP_DETAILS_CATEGORY));
        intent.putStringArrayListExtra("userPurchsedWidgets", Constants.StoreWidgets);
        if (session.getUserProfileEmail() != null) {
            intent.putExtra("email", session.getUserProfileEmail());
        } else {
            intent.putExtra("email", "ria@nowfloats.com");
        }
        if (session.getUserPrimaryMobile() != null) {
            intent.putExtra("mobileNo", session.getUserPrimaryMobile());
        } else {
            intent.putExtra("mobileNo", "9160004303");
        }
        intent.putExtra("profileUrl", session.getFPLogo());
        intent.putExtra("buyItemKey", WIDGET_KEY);
        startActivity(intent);
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            finish();
        }, 1000);
    }

    @Override
    public void primaryButtonClicked() {
        if (isPremium()){
            Intent intent = new Intent(getApplicationContext(), SeasonalOffersDetailsActivity.class);
            intent.putExtra("ScreenState", "new");
            startActivity(intent);
        }else {
            initiateBuyFromMarketplace();
        }
    }

    @Override
    public void secondaryButtonClicked() {

    }

    @Override
    public void ternaryButtonClicked() {

    }

    @Override
    public void appOnBackPressed() {

    }
}