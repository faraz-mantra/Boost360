package com.nowfloats.AccrossVerticals.Testimonials;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boost.upgrades.UpgradeActivity;
import com.nowfloats.AccrossVerticals.API.APIInterfaces;
import com.nowfloats.AccrossVerticals.API.model.testimonials.Data;
import com.nowfloats.AccrossVerticals.API.model.testimonials.TestimonialModel;
import com.nowfloats.Analytics_Screen.API.CallTrackerApis;
import com.nowfloats.Analytics_Screen.VmnCallCardsActivity;
import com.nowfloats.Login.UserSessionManager;
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

public class TestimonialsActivity extends AppCompatActivity implements TestimonialsListener {

    private LinearLayout mainLayout, secondaryLayout;
    private TextView buyItemButton, headerText;
    private UserSessionManager session;
    private ImageView addNewButton;

    private Toolbar toolbar;
    private TestimonialsAdapter testimonialsAdapter;
    private RecyclerView recyclerView;
    ProgressDialog vmnProgressBar;
    List<Data> dataList = new ArrayList<>();
    int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimonials);

        initialization();
        loadData();
    }

    void initialization() {
        vmnProgressBar = new ProgressDialog(this);
        vmnProgressBar.setIndeterminate(true);
        vmnProgressBar.setMessage(getString(R.string.please_wait));
        vmnProgressBar.setCancelable(false);

        session = new UserSessionManager(getApplicationContext(), this);

        toolbar = (Toolbar) findViewById(R.id.app_bar_site_appearance);

        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        addNewButton = (ImageView) findViewById(R.id.image_gallery_add_image_button);
        setSupportActionBar(toolbar);
        headerText.setText(getResources().getString(R.string.testimonials));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        recyclerView = (RecyclerView) findViewById(R.id.testimonials_recycler);
        testimonialsAdapter = new TestimonialsAdapter(new ArrayList(), this);
        initialiseRecycler();


        //show or hide if feature is available to user
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        secondaryLayout = (LinearLayout) findViewById(R.id.secondary_layout);
        buyItemButton = (TextView) findViewById(R.id.buy_item);
        if (!Constants.StoreWidgets.contains("TESTIMONIALS")) {
            mainLayout.setVisibility(View.VISIBLE);
            secondaryLayout.setVisibility(View.GONE);
            addNewButton.setVisibility(View.VISIBLE);
        } else {
            mainLayout.setVisibility(View.GONE);
            secondaryLayout.setVisibility(View.VISIBLE);
        }
        buyItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateBuyFromMarketplace();
            }
        });

        addNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestimonialsFeedbackActivity.class);
                startActivity(intent);
            }
        });

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecyclerMenuOption(-1, false);
            }
        });
    }

    private void initialiseRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(testimonialsAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void loadData() {
        try {
            showProgress();
            JSONObject query = new JSONObject();
            query.put("WebsiteId", session.getFpTag());
            APIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(APIInterfaces.class);

            APICalls.getTestimonialsList(query, currentIndex, new Callback<TestimonialModel>() {
                @Override
                public void success(TestimonialModel testimonialModel, Response response) {
                    hideProgress();
                    if (testimonialModel == null || response.getStatus() != 200) {
                        Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dataList.addAll(testimonialModel.getData());
                    updateRecyclerView();
                }

                @Override
                public void failure(RetrofitError error) {
                    hideProgress();
                    Methods.showSnackBarNegative(TestimonialsActivity.this, getString(R.string.something_went_wrong));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateRecyclerView() {
        testimonialsAdapter.updateList(dataList);
        testimonialsAdapter.notifyDataSetChanged();
    }

    private void initiateBuyFromMarketplace() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        String status = "Loading. Please wait...";
        progressDialog.setMessage(status);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Intent intent = new Intent(this, UpgradeActivity.class);
        intent.putExtra("expCode", session.getFP_AppExperienceCode());
        intent.putExtra("fpName", session.getFPName());
        intent.putExtra("fpid", session.getFPID());
        intent.putExtra("loginid", session.getUserProfileId());
        if (session.getFPEmail() != null) {
            intent.putExtra("email", session.getFPEmail());
        } else {
            intent.putExtra("email", "ria@nowfloats.com");
        }
        if (session.getFPPrimaryContactNumber() != null) {
            intent.putExtra("mobileNo", session.getFPPrimaryContactNumber());
        } else {
            intent.putExtra("mobileNo", "9160004303");
        }
        intent.putExtra("profileUrl", session.getFPLogo());
        intent.putExtra("buyItemKey", "TESTIMONIALS");
        startActivity(intent);
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
        }, 1000);
    }

    @Override
    public void itemMenuOptionStatus(int pos, boolean status) {
        updateRecyclerMenuOption(pos, status);
    }

    void updateRecyclerMenuOption(int pos, boolean status) {
        testimonialsAdapter.menuOption(pos, status);
        testimonialsAdapter.notifyDataSetChanged();
    }

    private void showProgress() {
        if (!vmnProgressBar.isShowing() && !isFinishing()) {
            vmnProgressBar.show();
        }
    }

    private void hideProgress() {
        if (vmnProgressBar.isShowing() && !isFinishing()) {
            vmnProgressBar.dismiss();
        }
    }
}