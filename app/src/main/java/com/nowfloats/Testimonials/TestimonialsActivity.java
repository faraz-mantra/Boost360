package com.nowfloats.Testimonials;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boost.upgrades.UpgradeActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.domain.DomainDetailsActivity;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.util.ArrayList;

public class TestimonialsActivity extends AppCompatActivity implements TestimonialsListener {

    private LinearLayout mainLayout, secondaryLayout;
    private TextView buyItemButton, headerText;
    private UserSessionManager session;
    private ImageView addNewButton;

    private Toolbar toolbar;
    private TestimonialsAdapter testimonialsAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimonials);

        initialization();
    }

    void initialization(){
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
        mainLayout= (LinearLayout) findViewById(R.id.main_layout);
        secondaryLayout= (LinearLayout) findViewById(R.id.secondary_layout);
        buyItemButton = (TextView) findViewById(R.id.buy_item);
        if(Constants.StoreWidgets.contains("TESTIMONIALS")) {
            mainLayout.setVisibility(View.VISIBLE);
            secondaryLayout.setVisibility(View.GONE);
            addNewButton.setVisibility(View.VISIBLE);
        }else{
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
                updateRecyclerView(-1,false);
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
        },1000);
    }

    @Override
    public void itemMenuOptionStatus(int pos, boolean status) {
        updateRecyclerView(pos,status);
    }

    void updateRecyclerView(int pos, boolean status) {
        testimonialsAdapter.menuOption(pos,status);
        testimonialsAdapter.notifyDataSetChanged();
    }
}