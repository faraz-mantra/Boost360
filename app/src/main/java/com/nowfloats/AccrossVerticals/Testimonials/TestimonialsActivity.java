package com.nowfloats.AccrossVerticals.Testimonials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boost.upgrades.UpgradeActivity;
import com.dashboard.utils.CodeUtilsKt;
import com.framework.utils.ContentSharing;
import com.framework.views.fabButton.FloatingActionButton;
import com.framework.views.zero.FragmentZeroCase;
import com.framework.views.zero.OnZeroCaseClicked;
import com.framework.views.zero.RequestZeroCaseBuilder;
import com.framework.views.zero.ZeroCases;
import com.framework.views.zero.old.AppFragmentZeroCase;
import com.framework.views.zero.old.AppOnZeroCaseClicked;
import com.framework.views.zero.old.AppRequestZeroCaseBuilder;
import com.framework.views.zero.old.AppZeroCases;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.AccrossVerticals.API.APIInterfaces;
import com.nowfloats.AccrossVerticals.API.model.DeleteTestimonials.DeleteTestimonialsData;
import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.GetTestimonialData;
import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.TestimonialData;
import com.nowfloats.AccrossVerticals.API.model.GetToken.GetTokenData;
import com.nowfloats.AccrossVerticals.API.model.GetToken.WebActionsItem;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;
import com.thinksity.databinding.ActivityTestimonialsBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class TestimonialsActivity extends AppCompatActivity implements TestimonialsListener, AppOnZeroCaseClicked {

    public static List<String> allTestimonialType = Arrays.asList("testimonials", "testimonial", "guestreviews");
//    TextView addTestimonialsButton;
    ProgressDialog vmnProgressBar;
    List<TestimonialData> dataList = new ArrayList<>();
    LinearLayout backButton;
    FloatingActionButton rightButton;
    ImageView rightIcon;
    TextView title;
    private String headerToken = "59c89bbb5d64370a04c9aea1";
    private String testimonialType = "testimonials";
    private UserSessionManager session;
    private TestimonialsAdapter testimonialsAdapter;
    private RecyclerView recyclerView;
    private boolean isLoad = false;
    private AppFragmentZeroCase appFragmentZeroCase;
    private String WIDGET_KEY="TESTIMONIALS";
    private static final String TAG = "TestimonialsActivity";
    private ActivityTestimonialsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        session = new UserSessionManager(this,this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_testimonials);
        appFragmentZeroCase = new AppRequestZeroCaseBuilder(AppZeroCases.TESTIMONIAL,
                TestimonialsActivity.this  , getApplicationContext(),isPremium()).getRequest().build();
        getSupportFragmentManager().beginTransaction().add(binding.childContainer.getId(),appFragmentZeroCase).commit();
        setHeader();
        initialization();
        checkIsAdd();
        Log.v("experincecode", " themeID: " + session.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID) + " FpTag: " + session.getFpTag() + " exp: " + session.getFPDetails(Key_Preferences.GET_FP_EXPERIENCE_CODE));
    }



    private void nonEmptyView() {
        binding.mainlayout.setVisibility(View.VISIBLE);
        binding.childContainer.setVisibility(View.GONE);
    }


    private void emptyView() {
        binding.mainlayout.setVisibility(View.GONE);
        binding.childContainer.setVisibility(View.VISIBLE);

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
        if (isPremium()) {
            nonEmptyView();
            getHeaderAuthToken();
        }else {
            emptyView();
        }
    }

    void initialization() {

        vmnProgressBar = new ProgressDialog(this);
        vmnProgressBar.setIndeterminate(true);
        vmnProgressBar.setMessage(getString(R.string.please_wait));
        vmnProgressBar.setCancelable(false);
//        addTestimonialsButton = findViewById(R.id.add_testimonials);
        recyclerView = findViewById(R.id.testimonials_recycler);
        testimonialsAdapter = new TestimonialsAdapter(new ArrayList(), this, session, this);
        initialiseRecycler();

        //show or hide if feature is available to user
//        secondaryLayout = (LinearLayout) findViewById(R.id.secondary_layout);

        binding.mainlayout.setOnClickListener(v -> updateRecyclerMenuOption(-1, false));

//        addTestimonialsButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), TestimonialsFeedbackActivity.class);
//            intent.putExtra("ScreenState", "new");
//            startActivity(intent);
//        });
    }

    private Boolean isPremium(){
        return /*session.getStoreWidgets().contains(WIDGET_KEY);*/ true;
    }
    public void setHeader() {
        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        rightButton = findViewById(R.id.btn_add);
        title.setText(getResources().getString(R.string.testimonials));
        rightButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), TestimonialsFeedbackActivity.class);
            intent.putExtra("ScreenState", "new");
            startActivity(intent);
        });

        backButton.setOnClickListener(v ->finishAfterTransition());
    }

    private void initialiseRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(testimonialsAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    void loadData() {
        try {
            if (!isLoad) showProgress();
            JSONObject query = new JSONObject();
            query.put("WebsiteId", session.getFpTag());
            APIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(APIInterfaces.class);
            Log.v("headerToken", " " + headerToken);
            APICalls.getTestimonialsList(headerToken, testimonialType, query, 0, 1000, new Callback<GetTestimonialData>() {
                @Override
                public void success(GetTestimonialData testimonialModel, Response response) {
                    hideProgress();
                    if (testimonialModel == null || response.getStatus() != 200) {
                        Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (testimonialModel.getData().size() > 0) {
                        isLoad = true;
                        dataList = testimonialModel.getData();
                        updateRecyclerView();
                        rightButton.setVisibility(View.VISIBLE);
                        nonEmptyView();
                        Log.i(TAG, "success api testimonial: ");
                    } else {
                        emptyView();
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
    public void editOptionClicked(TestimonialData data) {
        Intent intent = new Intent(getApplicationContext(), TestimonialsFeedbackActivity.class);
        intent.putExtra("ScreenState", "edit");
        intent.putExtra("data", new Gson().toJson(data));
        startActivity(intent);
    }

    @Override
    public void deleteOptionClicked(TestimonialData data) {
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

            APICalls.deleteTestimonials(headerToken, testimonialType, requestBody, new Callback<String>() {
                @Override
                public void success(String data, Response response) {
                    if (response != null && response.getStatus() == 200) {
                        Log.d("deleteTestimonials ->", response.getBody().toString());
                        getHeaderAuthToken();
                        Toast.makeText(getApplicationContext(), "Successfully Deleted.", Toast.LENGTH_LONG).show();
                    } else {
                        Methods.showSnackBarNegative(TestimonialsActivity.this, getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error != null && error.getResponse() != null && error.getResponse().getStatus() == 200) {
                        getHeaderAuthToken();
                        Toast.makeText(getApplicationContext(), "Successfully Deleted.", Toast.LENGTH_LONG).show();
                    } else {
                        Methods.showSnackBarNegative(TestimonialsActivity.this, getString(R.string.something_went_wrong));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shareOptionClicked(TestimonialData data) {
        String productType = CodeUtilsKt.getProductType(session.getFP_AppExperienceCode());
        String subDomain = "";
        if (productType.equals("PRODUCTS")) {
            subDomain = "all-products";
        } else {
            subDomain = "all-services";
        }
        ContentSharing.Companion.shareTestimonial(this, data.getDescription(), data.getUsername(), session.getRootAliasURI() + "/testimonials", session.getRootAliasURI() + "/" + subDomain, session.getFPPrimaryContactNumber(), false);
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

    private void getHeaderAuthToken() {
        try {
            APIInterfaces APICalls = new RestAdapter.Builder().setEndpoint("https://developer.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg"))
                    .build().create(APIInterfaces.class);
            Log.v("newvlue", " " + session.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID) + " " + session.getFpTag());
            APICalls.getHeaderAuthorizationtoken(session.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID), session.getFpTag(), new Callback<GetTokenData>() {
                @Override
                public void success(GetTokenData s, Response response) {
                    Log.v("experincecode1", " " + s.getToken());
                    int status = response.getStatus();
                    if ((status == 200 || status == 201 || status == 202) && s != null) {
                        Log.v("experincecode", " " + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY) + " headerToken: " + headerToken);

                        if (s.getWebActions() != null && !s.getWebActions().isEmpty()) {
                            loopBreak:
                            for (WebActionsItem action : s.getWebActions()) {
                                for (String type : allTestimonialType) {
                                    if (action.getName().equalsIgnoreCase(type)) {
                                        testimonialType = action.getName();
                                        break loopBreak;
                                    }
                                }
                            }
                        }

                        headerToken = s.getToken();
                        loadData();
                    } else {
                        Toast.makeText(getApplicationContext(), response.getStatus(), Toast.LENGTH_SHORT).show();
                        headerToken = "";
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.v("experincecode2", " " + error.getBody() + " " + error.getMessage());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.v("experincecode3", " " + e.getMessage() + " " + e.getStackTrace());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void primaryButtonClicked() {
        if (isPremium()) {
            Intent intent = new Intent(getApplicationContext(), TestimonialsFeedbackActivity.class);
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
        finishAfterTransition();
    }

    private void initiateBuyFromMarketplace() {

        ProgressDialog progressDialog = new android.app.ProgressDialog((this));
        String status = "Loading. Please wait...";
        progressDialog.setMessage(status);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Intent intent =new Intent(this, UpgradeActivity.class);
        intent.putExtra("expCode", session.getFP_AppExperienceCode());
        intent.putExtra("fpName", session.getFPName());
        intent.putExtra("fpid", session.getFPID());
        intent.putExtra("loginid", session.getUserProfileId());
        intent.putStringArrayListExtra("userPurchsedWidgets", com.nowfloats.util.Constants.StoreWidgets);
        intent.putExtra("fpTag", session.getFpTag());
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                finish();
            }
        }, 1000);

    }
}