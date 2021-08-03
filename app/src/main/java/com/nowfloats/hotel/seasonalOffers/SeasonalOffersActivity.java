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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boost.upgrades.UpgradeActivity;
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

public class SeasonalOffersActivity extends AppCompatActivity implements SeasonalOffersListener {

    UserSessionManager session;
    SeasonalOffersAdapter adapter;
    RecyclerView recyclerView;
    LinearLayout secondaryLayout;
    TextView buyItemButton;
    List<Data> dataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasonal_offers);

        initView();
    }

    private void initView() {

        session = new UserSessionManager(this, this);
        adapter = new SeasonalOffersAdapter(new ArrayList(), this);
        recyclerView = findViewById(R.id.recycler_view);
        secondaryLayout = findViewById(R.id.secondary_layout);
        buyItemButton = findViewById(R.id.buy_item);

        //setHeader
        setHeader();

        //this feature is free to use
//        if (Constants.StoreWidgets.contains("OFFERS")) {
        recyclerView.setVisibility(View.VISIBLE);
        secondaryLayout.setVisibility(View.GONE);
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
        loadData();
//        }
    }

    void showSecondaryLayout() {
        buyItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateBuyFromMarketplace();
            }
        });
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
                        recyclerView.setVisibility(View.VISIBLE);
                        secondaryLayout.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        secondaryLayout.setVisibility(View.VISIBLE);
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
        LinearLayout rightButton, backButton;
        ImageView rightIcon;
        TextView title;

        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        rightButton = findViewById(R.id.right_icon_layout);
        rightIcon = findViewById(R.id.right_icon);
        title.setText("Seasonal Offers");
        //this feature is free to use
//        if (Constants.StoreWidgets.contains("OFFERS")) {
        rightIcon.setImageResource(R.drawable.ic_add_white);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SeasonalOffersDetailsActivity.class);
                intent.putExtra("ScreenState", "new");
                startActivity(intent);
            }
        });
//        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
        if (session.getFPEmail() != null) {
            intent.putExtra("email", session.getFPEmail());
        } else {
            intent.putExtra("email", "ria@nowfloats.com");
        }
        if (session.getUserPrimaryMobile() != null) {
            intent.putExtra("mobileNo", session.getUserPrimaryMobile());
        } else {
            intent.putExtra("mobileNo", "9160004303");
        }
        intent.putExtra("profileUrl", session.getFPLogo());
        intent.putExtra("buyItemKey", "OFFERS");
        startActivity(intent);
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            finish();
        }, 1000);
    }
}