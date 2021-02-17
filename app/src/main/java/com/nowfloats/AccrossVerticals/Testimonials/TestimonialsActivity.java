package com.nowfloats.AccrossVerticals.Testimonials;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.boost.upgrades.UpgradeActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.AccrossVerticals.API.APIInterfaces;
import com.nowfloats.AccrossVerticals.API.model.DeleteTestimonials.DeleteTestimonialsData;
import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.Data;
import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.GetTestimonialData;
import com.nowfloats.AccrossVerticals.API.model.GetToken.GetTokenData;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.ProductGallery.Model.Product;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
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

public class TestimonialsActivity extends AppCompatActivity implements TestimonialsListener {

    private LinearLayout mainLayout, secondaryLayout;
    private UserSessionManager session;
    TextView addTestimonialsButton;

    private TestimonialsAdapter testimonialsAdapter;
    private RecyclerView recyclerView;
    ProgressDialog vmnProgressBar;
    List<Data> dataList = new ArrayList<>();

    LinearLayout rightButton, backButton;
    ImageView rightIcon;
    TextView title;
    String headerToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimonials);
        //setheaders
        setHeader();
        initialization();
        checkIsAdd();
        Log.v("experincecode"," themeID: "+  session.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID) +" FpTag: " + session.getFpTag() + " exp: "+ session.getFPDetails(Key_Preferences.GET_FP_EXPERIENCE_CODE) );
    }

    private void checkIsAdd() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            boolean isAdd = bundle.getBoolean("IS_ADD");
            if (isAdd) {
                Intent intent = new Intent(getApplicationContext(), TestimonialsFeedbackActivity.class);
                intent.putExtra("ScreenState", "new");
                startActivityForResult(intent, 202);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadData();
        getHeaderAuthToken();
/*        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).equals("HOTEL & MOTELS")){
//            loadHotelsData();
            getHeaderAuthToken();
        }else{
            loadData();
        }*/
//        getHeaderAuthToken();
    }

    void initialization() {
        vmnProgressBar = new ProgressDialog(this);
        vmnProgressBar.setIndeterminate(true);
        vmnProgressBar.setMessage(getString(R.string.please_wait));
        vmnProgressBar.setCancelable(false);

        addTestimonialsButton = findViewById(R.id.add_testimonials);

        session = new UserSessionManager(getApplicationContext(), this);

        recyclerView = (RecyclerView) findViewById(R.id.testimonials_recycler);
        testimonialsAdapter = new TestimonialsAdapter(new ArrayList(), this, session);
        initialiseRecycler();


        //show or hide if feature is available to user
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        secondaryLayout = (LinearLayout) findViewById(R.id.secondary_layout);

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecyclerMenuOption(-1, false);
            }
        });

        addTestimonialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestimonialsFeedbackActivity.class);
                intent.putExtra("ScreenState", "new");
                startActivity(intent);
            }
        });
    }

    public void setHeader() {

        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        rightButton = findViewById(R.id.right_icon_layout);
        rightIcon = findViewById(R.id.right_icon);
        title.setText("Testimonials");
        rightIcon.setImageResource(R.drawable.ic_add_white);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestimonialsFeedbackActivity.class);
                intent.putExtra("ScreenState", "new");
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initialiseRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(testimonialsAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
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
            Log.v("headerToken"," "+ headerToken);
            APICalls.getTestimonialsList(/*headerToken,*/query, 0, 1000, new Callback<GetTestimonialData>() {
                @Override
                public void success(GetTestimonialData testimonialModel, Response response) {
                    hideProgress();
                    if (testimonialModel == null || response.getStatus() != 200) {
                        Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (testimonialModel.getData().size() > 0) {
                        dataList = testimonialModel.getData();
                        updateRecyclerView();
                        mainLayout.setVisibility(View.VISIBLE);
                        secondaryLayout.setVisibility(View.GONE);
                        rightButton.setVisibility(View.VISIBLE);
                    } else {
                        mainLayout.setVisibility(View.GONE);
                        secondaryLayout.setVisibility(View.VISIBLE);
                        rightButton.setVisibility(View.INVISIBLE);
                    }
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
    void loadHotelsData() {
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
            Log.v("headerToken"," "+ headerToken);
            APICalls.getHotelsTestimonialsList(headerToken, query, 0, 1000, new Callback<GetTestimonialData>() {
                @Override
                public void success(GetTestimonialData testimonialModel, Response response) {
                    hideProgress();
                    if (testimonialModel == null || response.getStatus() != 200) {
                        Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (testimonialModel.getData().size() > 0) {
                        dataList = testimonialModel.getData();
                        updateRecyclerView();
                        mainLayout.setVisibility(View.VISIBLE);
                        secondaryLayout.setVisibility(View.GONE);
                        rightButton.setVisibility(View.VISIBLE);
                    } else {
                        mainLayout.setVisibility(View.GONE);
                        secondaryLayout.setVisibility(View.VISIBLE);
                        rightButton.setVisibility(View.INVISIBLE);
                    }
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


    @Override
    public void itemMenuOptionStatus(int pos, boolean status) {
        updateRecyclerMenuOption(pos, status);
    }

    @Override
    public void editOptionClicked(Data data) {
        Intent intent = new Intent(getApplicationContext(), TestimonialsFeedbackActivity.class);
        intent.putExtra("ScreenState", "edit");
        intent.putExtra("data", new Gson().toJson(data));
        startActivity(intent);
    }

    @Override
    public void deleteOptionClicked(Data data) {
        try {
            DeleteTestimonialsData requestBody = new DeleteTestimonialsData();
            requestBody.setQuery("{_id:'" + data.getId() + "'}");
            requestBody.setUpdateValue("{$set : {IsArchived: true }}");
            requestBody.setMulti(true);

            APIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .setConverter(new GsonConverter(new GsonBuilder().setLenient().create()))
                    .build()
                    .create(APIInterfaces.class);

            APICalls.deleteTestimonials(headerToken, requestBody, new Callback<String>() {
                @Override
                public void success(String data, Response response) {
                    if (response != null && response.getStatus() == 200) {
                        Log.d("deleteTestimonials ->", response.getBody().toString());
//                        loadData();
                        getHeaderAuthToken();
                        Toast.makeText(getApplicationContext(), getString(R.string.successfully_deleted_), Toast.LENGTH_LONG).show();
//                        Methods.showSnackBarPositive(TestimonialsActivity.this,  getString(R.string.successfully_deleted_));
                    } else {
                        Methods.showSnackBarNegative(TestimonialsActivity.this, getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getResponse().getStatus() == 200) {
//                        loadData();
                        getHeaderAuthToken();
                        Toast.makeText(getApplicationContext(), getString(R.string.successfully_deleted_), Toast.LENGTH_LONG).show();
//                        Methods.showSnackBarPositive(TestimonialsActivity.this,  getString(R.string.successfully_deleted_));
                    } else {
                        Methods.showSnackBarNegative(TestimonialsActivity.this, getString(R.string.something_went_wrong));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 202) {
            if (!(data != null && data.getBooleanExtra("IS_REFRESH", false))) onBackPressed();
        }
    }
    String getHeaderAuthToken() {

        try {

            APIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://developer.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(APIInterfaces.class);
            Log.v("newvlue"," "+ session.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID)+ " "+ session.getFpTag());
            APICalls.getHeaderAuthorizationtoken(session.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID), session.getFpTag(), new Callback<GetTokenData>() {
                @Override
                public void success(GetTokenData s, Response response) {
                    Log.v("experincecode1"," "+ s.getToken() );
                    if (response.getStatus() != 200) {
                        Toast.makeText(getApplicationContext(), response.getStatus(), Toast.LENGTH_SHORT).show();
                        headerToken = "";
                        return ;
                    }else{
                        headerToken = s.getToken();
                        Log.v("experincecode"," "+ session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY) + " headerToken: "+ headerToken);
//                        loadData();
                        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).equals("HOTEL & MOTELS")){
                            loadHotelsData();
                        }else if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).equals("MANUFACTURERS")){
                            loadData();
                        }else if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).equals("SALON")){
                            loadHotelsData();
                        }else{
                            loadData();
                        }
//                        Toast.makeText(getApplicationContext(), response.getBody().toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.v("experincecode2"," "+ error.getBody() + " "+ error.getMessage() );
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    Methods.showSnackBarNegative(TestimonialsActivity.this, getString(R.string.something_went_wrong));
                }
            });


        } catch (Exception e) {
            Log.v("experincecode3"," "+ e.getMessage() +" "+ e.getStackTrace() );
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return headerToken;
    }
}