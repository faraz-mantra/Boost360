package com.nowfloats.domain;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.CustomWidget.roboto_lt_24_212121;
import com.nowfloats.CustomWidget.roboto_md_60_212121;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.DomainApiService;
import com.nowfloats.NavigationDrawer.SidePanelFragment;
import com.nowfloats.NavigationDrawer.model.DomainDetails;
import com.nowfloats.Store.Model.StoreEvent;
import com.nowfloats.Store.Model.StoreModel;
import com.nowfloats.Store.Service.API_Service;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Model;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.nowfloats.NavigationDrawer.HomeActivity.activity;


public class DomainDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView headerText, tvPriceDef, tvPrice;

    private UserSessionManager session;

    private DomainApiService domainApiService;

    private Bus mBus;

    private ProgressDialog progressDialog;

    private TextView tvDomainStatus;

    private LinearLayout llBookDomain;

    private Button btnBookDomain, btnRenewDomain;

    private EditText edtDomainName;

    private static final int DOMAIN_EXPIRY_GRACE_PERIOD = 30;

    private static final int PLAN_EXPIRY_GRACE_PERIOD = 90;

    private int domainExpiryDays = 0, planExpiryDays = 0;

    private long currentTime, totalNoOfDays = 0;

    private Spinner spDomainTypes, spDomainYears;

    private HashMap<String, Integer> hmPrices = new HashMap<>();

    private ArrayList<String> arrDomainExtensions;

    private String domainType = "", domainExpiryDate = "";

    private Get_FP_Details_Model get_fp_details_model;
    int processingStatus = -1;
    SharedPreferences pref;
    private int domainYears = 0, bookedYears = 0;
    private boolean isDomainBookFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        domainExpiryDays = 0;
        planExpiryDays = 0;
        totalNoOfDays = 1000 * 60 * 60 * 24;

        currentTime = System.currentTimeMillis();
        setContentView(R.layout.activity_domain_details);

        get_fp_details_model = (Get_FP_Details_Model) getIntent().getExtras().get("get_fp_details_model");

        initializeControls();
        bindListeners();
        loadData();
    }

    private void initializeControls() {
        mBus = BusProvider.getInstance().getBus();
        toolbar = (Toolbar) findViewById(R.id.app_bar_site_appearance);
        tvDomainStatus = (TextView) findViewById(R.id.tvDomainStatus);
        btnBookDomain = (Button) findViewById(R.id.btnBookDomain);
        btnRenewDomain = (Button) findViewById(R.id.btnRenewDomain);
        edtDomainName = (EditText) findViewById(R.id.edtDomainName);
        spDomainTypes = (Spinner) findViewById(R.id.spDomainTypes);
        spDomainYears = (Spinner) findViewById(R.id.spDomainYears);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        tvPriceDef = (TextView) findViewById(R.id.tvPriceDef);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        setSupportActionBar(toolbar);
        headerText.setText(getResources().getString(R.string.side_panel_row_domain_details));

        if (getSupportActionBar() != null ){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        session = new UserSessionManager(this, this);
        domainApiService = new DomainApiService(mBus);
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        btnBookDomain.setVisibility(View.GONE);
        btnRenewDomain.setVisibility(View.GONE);
    }

    private void initializePrices() {
        hmPrices.put(".COM", 680);
        hmPrices.put(".NET", 865);
        hmPrices.put(".CO.IN", 375);
        hmPrices.put(".IN", 490);
        hmPrices.put(".ORG", 500);
    }

    private void bindListeners() {

        spDomainYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvPrice.setText(String.valueOf(hmPrices.get(arrDomainExtensions.get(spDomainTypes.getSelectedItemPosition()))*
                        ((Integer)spDomainYears.getSelectedItem())) + "*");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spDomainTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvPriceDef.setText(String.format(getString(R.string.price_of_domain), arrDomainExtensions.get(position)));
                if (hmPrices.containsKey(arrDomainExtensions.get(position))) {
                    tvPrice.setText(String.valueOf(hmPrices.get(arrDomainExtensions.get(position))*((Integer)spDomainYears.getSelectedItem())) + "*");
                } else {
                    tvPrice.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnBookDomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookDomain();
            }
        });

        btnRenewDomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookDomain();
            }
        });

    }

    private void bookDomain() {

        Methods.hideKeyboard(DomainDetailsActivity.this);
        String domainName = edtDomainName.getText().toString();
        if (TextUtils.isEmpty(domainName)) {
            Methods.showSnackBarNegative(DomainDetailsActivity.this,
                    getString(R.string.enter_domain_name));
        } else if (TextUtils.isEmpty(get_fp_details_model.getPinCode())) {
            Methods.showSnackBarNegative(DomainDetailsActivity.this,
                    getString(R.string.enter_zip_code));
        } else {
            showLoader(getString(R.string.please_wait));
            domainApiService.checkDomainAvailability(domainName, getDomainAvailabilityParam((String) spDomainTypes.getSelectedItem()));
        }
    }

    private HashMap<String, String> getDomainAvailabilityParam(String domainType) {
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("clientId", Constants.clientId);
        offersParam.put("domainType", domainType);
        return offersParam;
    }

    @Subscribe
    public void domainAvailabilityStatus(DomainApiService.DomainAPI domainAPI) {
        hideLoader();
        if (domainAPI == DomainApiService.DomainAPI.CHECK_DOMAIN) {
            Methods.showSnackBarPositive(DomainDetailsActivity.this, "Domain is available");
            showCustomDialog(getString(R.string.book_a_new_domain),
                    getString(R.string.are_you_sure_do_you_want_to_book_domain),
                    getString(R.string.yes), getString(R.string.no), DialogFrom.DOMAIN_AVAILABLE);
        } else if (domainAPI == DomainApiService.DomainAPI.LINK_DOMAIN) {
            Methods.showSnackBarPositive(DomainDetailsActivity.this, "domain link available");
        } else {
            Methods.showSnackBarNegative(DomainDetailsActivity.this, getString(R.string.domain_not_available));
        }
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

    private void loadData() {
        showLoader(getString(R.string.please_wait));
        initializePrices();
        domainApiService.getDomainDetails(session.getFpTag(), getDomainDetailsParam());
    }

    @Subscribe
    public void getDomainSupportedTypes(ArrayList<String> arrExtensions) {
        hideLoader();
        if (arrExtensions != null && arrExtensions.size() > 0) {
//            arrDomainExtensions = new ArrayList<String>(Arrays.asList(domainSupportedTypes.domainExtension.split(",")));
           /*
               remove below domains as per Rachit
            */
            this.arrDomainExtensions = arrExtensions;

            if (arrDomainExtensions.contains(".IN")) {
                arrDomainExtensions.remove(".IN");
                //String firstIndexValue = arrDomainExtensions.get(0);
                arrDomainExtensions.add(0, ".IN");
            }


            arrDomainExtensions.remove(".CA");
            arrDomainExtensions.remove(".CO.ZA");

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DomainDetailsActivity.this,
                    android.R.layout.simple_spinner_item, arrDomainExtensions);
            spDomainTypes.setAdapter(arrayAdapter);

            if(btnBookDomain.getVisibility() == View.VISIBLE || btnRenewDomain.getVisibility() == View.VISIBLE){
                setDomainYearsAdapter(domainYears);
            }else{
                setBookedDomainYears(bookedYears);
                spDomainYears.setSelection(0);
            }
            if (!TextUtils.isEmpty(domainType)) {
                if (arrDomainExtensions.contains(domainType)) {
                    spDomainTypes.setSelection(arrDomainExtensions.indexOf(domainType));

                }
                spDomainTypes.setEnabled(false);
                spDomainTypes.setClickable(false);
            } else {
                spDomainTypes.setSelection(0);
            }

        } else {
            Methods.showSnackBarNegative(DomainDetailsActivity.this, getString(R.string.domain_details_getting_error));
        }

    }

    private void setBookedDomainYears(int years){
        Integer[] array = new Integer[1];
        array[0] =years;
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(DomainDetailsActivity.this,android.R.layout.simple_spinner_item,array);
        spDomainYears.setAdapter(adapter);
    }
    @Subscribe
    public void getDomainDetails(DomainDetails domainDetails) {

        if (domainDetails != null && domainDetails.response) {

            if(TextUtils.isDigitsOnly(domainDetails.getProcessingStatus())){
                processingStatus = Integer.parseInt(domainDetails.getProcessingStatus());
            }
            if(!TextUtils.isEmpty(domainDetails.getActivatedOn())){
                long activatedDate = Long.parseLong(domainDetails.getActivatedOn().replace("/Date(", "").replace(")/", ""));
                Calendar dbCalender = Calendar.getInstance();
                dbCalender.setTimeInMillis(activatedDate);
                dbCalender.add(Calendar.YEAR, domainDetails.getValidityInYears());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
                dateFormat.setCalendar(dbCalender);
                dateFormat.format(dbCalender.getTime());

                domainExpiryDays = (int) ((dbCalender.getTimeInMillis() - currentTime) / totalNoOfDays);
                domainExpiryDate = dateFormat.format(dbCalender.getTime());
            }
            tvDomainStatus.setVisibility(View.VISIBLE);
            edtDomainName.setText(domainDetails.getDomainName());
            domainType = domainDetails.getDomainType();
            if( !TextUtils.isEmpty(domainDetails.getErrorMessage()) || domainDetails.getIsProcessingFailed()){
                //error domain failed
                isDomainBookFailed = true;
            }

            bookedYears = domainDetails.getValidityInYears();

        }
        new API_Service(DomainDetailsActivity.this, session.getSourceClientId(), session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID), session.getFPID(), mBus);
    }

    private void setDomainYearsAdapter(int length){
        if(length == 0){
            return;
        }
        Integer[] array = new Integer[length];
        for (int i=1;i<=length;i++){
            array[i-1] = i;
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(DomainDetailsActivity.this,android.R.layout.simple_spinner_item,array);
        spDomainYears.setAdapter(adapter);
    }

    @Subscribe
    public void getStoreList(StoreEvent response) {
        ArrayList<StoreModel> allModels = response.model.AllPackages;
        ArrayList<StoreModel> activeIdArray = response.model.ActivePackages;
        ArrayList<StoreModel> additionalPlans = response.model.AllPackages;
        if (allModels != null && activeIdArray != null) {
            long storeExpiryDays = 0;
            for (StoreModel storeModel : activeIdArray) {
                float validity = storeModel.TotalMonthsValidity;
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                cal.setTimeInMillis(Long.parseLong(storeModel.ToBeActivatedOn.replace("/Date(", "").replace(")/", "")));
                cal.add(Calendar.MONTH, (int) validity);
                cal.add(Calendar.DATE, (int) ((validity - Math.floor((double) validity)) * 30));

                long tempExpiryDays = cal.getTimeInMillis();
                if (tempExpiryDays > storeExpiryDays) {
                    storeExpiryDays = tempExpiryDays;
                    domainYears = 0;
                    if(cal.get(Calendar.YEAR)>= year){
                        domainYears = cal.get(Calendar.YEAR)-year;
                        if(cal.get(Calendar.MONTH)>month){
                            domainYears+=1;
                        }else if(cal.get(Calendar.MONTH) == month){
                            if(cal.get(Calendar.DAY_OF_MONTH)>day){
                                domainYears+=1;
                            }
                        }
                    }
                }
            }

            planExpiryDays = (int) ((storeExpiryDays - currentTime) / totalNoOfDays);
            applyDomainLogic();
            domainApiService.getDomainSupportedTypes(getDomainDetailsParam());
        } else {
            hideLoader();
            Methods.showSnackBarNegative(DomainDetailsActivity.this, getString(R.string.something_went_wrong));
        }
    }


    //------------------|--------- PlanExpiry<=0 ---------------PlanExpiry<90 --------|----- PlanExpiry>=90--------|
    //                  |                                |                            |                            |
    //-DomainExpiry<0-- |          Plan Expired          |    Expired Renew Domain    |      Expired Renew Domain  |
    //                  |                                |                            |                            |
    //-DomainExpiry<30--|          Plan Expired          |       P<=D- Do Nothing     |      TBE-Renew Domain      |
    //                  |                                |      P>D-TBE Renew Domain  |                            |
    //                  |                                |                            |                            |
    //-DomainExpiry>=30-|          Plan Expired          |    Do nothing(show domain) |    Do nothing(show domain) |
    //------------------|--------------------------------|----------------------------|----------------------------|

    private void applyDomainLogic() {

        if(TextUtils.isEmpty(domainType)){
            tvDomainStatus.setText("Status: You have not booked any domain.");
            btnBookDomain.setVisibility(View.VISIBLE);
            btnRenewDomain.setVisibility(View.GONE);
        }else if(isDomainBookFailed){
            tvDomainStatus.setText("Status: Your Domain request is failed. Please try again");
            btnRenewDomain.setVisibility(View.VISIBLE);
            btnBookDomain.setVisibility(View.GONE);
            tvDomainStatus.setTextColor(Color.RED);
        }
        else if (planExpiryDays <= 0) {
            showExpiryDialog(LIGHT_HOUSE_EXPIRE, planExpiryDays);
        } else if (domainExpiryDays <= 0 && planExpiryDays < PLAN_EXPIRY_GRACE_PERIOD) {
            tvDomainStatus.setText("Status: Your domain expired on " + domainExpiryDate);
            tvDomainStatus.setTextColor(Color.RED);
            renewDomain();
        } else if (domainExpiryDays <= 0 && planExpiryDays >= PLAN_EXPIRY_GRACE_PERIOD) {
            tvDomainStatus.setText("Status: Your domain expired on " + domainExpiryDate);
            tvDomainStatus.setTextColor(Color.RED);
            renewDomain();
        } else if (domainExpiryDays < DOMAIN_EXPIRY_GRACE_PERIOD &&
                planExpiryDays <= domainExpiryDays && planExpiryDays < PLAN_EXPIRY_GRACE_PERIOD) {
            tvDomainStatus.setText("Status: Your domain will expire on " + domainExpiryDate);
            edtDomainName.setEnabled(false);
            edtDomainName.setFocusable(false);
            showExpiryDialog(LIGHT_HOUSE_DAYS_LEFT, planExpiryDays);
        } else if (domainExpiryDays < DOMAIN_EXPIRY_GRACE_PERIOD &&
                planExpiryDays > domainExpiryDays && planExpiryDays < PLAN_EXPIRY_GRACE_PERIOD) {
            tvDomainStatus.setText("Status: Your domain will expire on " + domainExpiryDate);
            renewDomain();
        } else if (domainExpiryDays < DOMAIN_EXPIRY_GRACE_PERIOD && planExpiryDays >= PLAN_EXPIRY_GRACE_PERIOD) {
            tvDomainStatus.setText("Status: Your domain will expire on " + domainExpiryDate);
            renewDomain();
        } else {
            tvDomainStatus.setText("Status: Your domain will expire on " + domainExpiryDate);
            edtDomainName.setEnabled(false);
            edtDomainName.setFocusable(false);
        }

        if(processingStatus >=0 && processingStatus<=16)
        {
            tvDomainStatus.setText("Status: Your domain request is pending. ");
            edtDomainName.setEnabled(false);
            edtDomainName.setFocusable(false);
            spDomainYears.setEnabled(false);
            spDomainYears.setClickable(false);
            btnBookDomain.setVisibility(View.GONE);
            btnRenewDomain.setVisibility(View.GONE);
        }
    }

    private static final int LIGHT_HOUSE_EXPIRE = 0;
    private static final int WILD_FIRE_EXPIRE = 1;
    private static final int DEMO_EXPIRE = 3;
    private static final int DEMO_DAYS_LEFT = 4;
    private static final int LIGHT_HOUSE_DAYS_LEFT = 5;

    private void showExpiryDialog(int showDialog, float days) {

        String callUsButtonText, cancelButtonText, dialogTitle, dialogMessage;
        int dialogImage, dialogImageBgColor;

        switch (showDialog) {
            case LIGHT_HOUSE_EXPIRE:
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.renew_light_house_plan);
                dialogMessage = getString(R.string.light_house_plan_expired_some_features_visible);
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                break;
            case WILD_FIRE_EXPIRE:
                dialogTitle = getString(R.string.renew_wildfire_plan);
                dialogMessage = getString(R.string.continue_auto_promoting_on_google);
                callUsButtonText = getString(R.string.renew_in_capital);
                cancelButtonText = getString(R.string.ignore_in_capital);
                dialogImage = R.drawable.wild_fire_expire;
                dialogImageBgColor = Color.parseColor("#ffffff");
                break;
            case DEMO_EXPIRE:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.buy_light_house_plan);
                dialogMessage = getString(R.string.demo_plan_expired);
                break;
            case DEMO_DAYS_LEFT:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.buy_light_house_plan);
                if (days < 1) {
                    dialogMessage = String.format(getString(R.string.demo_plan_will_expire), "< 1");
                } else {
                    dialogMessage = String.format(getString(R.string.demo_plan_will_expire), (int) Math.floor(days) + " ");
                }
                break;
            case LIGHT_HOUSE_DAYS_LEFT:
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.renew_light_house_plan);

                if (days < 1) {
                    dialogMessage = String.format(getString(R.string.light_house_pla_will_expire), "< 1");
                } else {
                    dialogMessage = String.format(getString(R.string.light_house_pla_will_expire), (int) Math.floor(days) + " ");
                }

                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                break;
            default:
                return;
        }

        MaterialDialog mExpireDailog = new MaterialDialog.Builder(this)
                .customView(R.layout.pop_up_restrict_post_message, false)
                .backgroundColorRes(R.color.white)
                .positiveText(callUsButtonText)
                .negativeText(cancelButtonText)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

        View view = mExpireDailog.getCustomView();

        roboto_md_60_212121 title = (roboto_md_60_212121) view.findViewById(R.id.textView1);
        title.setText(dialogTitle);

        ImageView expireImage = (ImageView) view.findViewById(R.id.img_warning);
        expireImage.setBackgroundColor(dialogImageBgColor);
        expireImage.setImageDrawable(ContextCompat.getDrawable(this, dialogImage));

        roboto_lt_24_212121 message = (roboto_lt_24_212121) view.findViewById(R.id.pop_up_create_message_body);
        message.setText(Methods.fromHtml(dialogMessage));
    }

    private void renewDomain() {
        edtDomainName.setEnabled(false);
        edtDomainName.setFocusable(false);
        btnBookDomain.setVisibility(View.GONE);
        btnRenewDomain.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        super.onStop();
    }

    private void showLoader(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(DomainDetailsActivity.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                }
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        });
    }

    private void hideLoader() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    /*
   * Domain Details Param
   */
    private HashMap<String, String> getDomainDetailsParam() {
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("clientId", Constants.clientId);
        return offersParam;
    }


    private enum DialogFrom {
        DOMAIN_AVAILABLE,
        CONTACTS_AND_EMAIL_REQUIRED,
        CATEGORY_REQUIRED,
        ADDRESS_REQUIRED,
        DEFAULT
    }

    private void showCustomDialog(String title, String message, String postiveBtn, String negativeBtn,
                                  final DialogFrom dialogFrom) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(DomainDetailsActivity.this)
                .title(title)
                .customView(R.layout.dialog_link_layout, false)
                .positiveText(postiveBtn)
                .negativeText(negativeBtn)
                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.primaryColor)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        switch (dialogFrom) {

                            case DOMAIN_AVAILABLE:
                                prepareAndPublishDomain();
                                break;
                            case CONTACTS_AND_EMAIL_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) activity).
                                        onClick(getResources().getString(R.string.contact__info));
                                break;
                            case CATEGORY_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) activity).
                                        onClick(getResources().getString(R.string.basic_info));
                                break;
                            case ADDRESS_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) activity).
                                        onClick(getResources().getString(R.string.business__address));
                            case DEFAULT:
                                finish();
                                break;
                        }
                    }
                    /*
                    ((SidePanelFragment.OnItemClickListener) activity).
                onClick(getResources().getString(R.string.business_profile));
                     */

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);

                        switch (dialogFrom) {

                            case DOMAIN_AVAILABLE:
                                MixPanelController.track(MixPanelController.DOMAIN_SEARCH, null);
                                break;
                            case DEFAULT:

                                break;
                        }
                    }
                });

        final MaterialDialog materialDialog = builder.show();
        View maView = materialDialog.getCustomView();

        TextView tvMessage = (TextView) maView.findViewById(R.id.toast_message_to_contact);
        tvMessage.setText(message);
    }

    private void prepareAndPublishDomain() {
        MixPanelController.track(MixPanelController.BOOK_DOMAIN, null);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("clientId", Constants.clientId);
        hashMap.put("domainName", edtDomainName.getText().toString().trim());
        hashMap.put("domainType", (String) spDomainTypes.getSelectedItem());
        hashMap.put("existingFPTag", session.getFpTag());
        hashMap.put("addressLine1", get_fp_details_model.getAddress());
        hashMap.put("city", get_fp_details_model.getCity());
        hashMap.put("companyName", get_fp_details_model.getTag());
        hashMap.put("contactName", TextUtils.isEmpty(get_fp_details_model.getContactName()) ?
                session.getFpTag() : get_fp_details_model.getContactName());
        hashMap.put("country", get_fp_details_model.getCountry());
        hashMap.put("countryCode", get_fp_details_model.getLanguageCode());
        hashMap.put("email", get_fp_details_model.getEmail());
        hashMap.put("lat", get_fp_details_model.getLat());
        hashMap.put("lng", get_fp_details_model.getLng());
        hashMap.put("validityInYears",String.valueOf(spDomainYears.getSelectedItemPosition()+1));
        hashMap.put("phoneISDCode", get_fp_details_model.getCountryPhoneCode());
        if (get_fp_details_model.getCategory() != null && get_fp_details_model.getCategory().size() > 0)
            hashMap.put("primaryCategory", get_fp_details_model.getCategory().get(0).getKey());
        else
            hashMap.put("primaryCategory", "");
        hashMap.put("primaryNumber", get_fp_details_model.getPrimaryNumber());
        hashMap.put("regService", "");
        hashMap.put("state", get_fp_details_model.getPaymentState());
        hashMap.put("zip", get_fp_details_model.getPinCode());
        domainApiService.buyDomain(hashMap);
    }

    @Subscribe
    public void domainBookStatus(String response) {

        if (!TextUtils.isEmpty(response) &&
                response.equalsIgnoreCase(getString(R.string.domain_booking_process_message))) {

            showCustomDialog(getString(R.string.domain_booking_process),
                    getString(R.string.domain_booking_process_message),
                    getString(R.string.ok), null, DialogFrom.DEFAULT);

        } else {

            if (TextUtils.isEmpty(response)) {
                response = getString(R.string.domain_booking_failed);
            }
            showCustomDialog(getString(R.string.book_a_new_domain),
                    response,
                    getString(R.string.ok), null, DialogFrom.DEFAULT);
        }
    }
}
