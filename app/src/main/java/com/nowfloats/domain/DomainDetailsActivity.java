package com.nowfloats.domain;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.CustomWidget.roboto_lt_24_212121;
import com.nowfloats.CustomWidget.roboto_md_60_212121;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.DomainApiService;
import com.nowfloats.NavigationDrawer.model.DomainDetails;
import com.nowfloats.Store.Model.StoreEvent;
import com.nowfloats.Store.Model.StoreModel;
import com.nowfloats.Store.PricingPlansActivity;
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


public class DomainDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private TextView headerText;

    private UserSessionManager session;

    private DomainApiService domainApiService;

    private Bus mBus;

    private ProgressDialog progressDialog;


    private static final int DOMAIN_EXPIRY_GRACE_PERIOD = 30;

    private static final int PLAN_EXPIRY_GRACE_PERIOD = 90;
    private static final String FP_WEB_WIDGET_DOMAIN = "DOMAINPURCHASE";

    private int domainExpiryDays = 0, planExpiryDays = 0;

    private long currentTime, totalNoOfDays = 0;

    private HashMap<String, Integer> hmPrices = new HashMap<>();

    private ArrayList<String> arrDomainExtensions;

    private String domainType = "", domainExpiryDate = "", domainCreatedDate = "";

    private Get_FP_Details_Model get_fp_details_model;
    int processingStatus = -1;
    SharedPreferences pref;
    private int domainYears = 0, bookedYears = 0;
    private boolean isDomainBookFailed = false;
    TextView domainNameTv, domainCreatedTv, domainExpiredTv, statusTv,expireMsgTv;
    CardView domainDetailsCard, emailDetailsCard;
    LinearLayout chooseDomainLayout, expiredLayout;
    Button proceedBtn;
    RadioButton chooseBtn, linkBtn;
    private MaterialDialog domainBookDialog;

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

        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);

        domainCreatedTv = (TextView) findViewById(R.id.tv_domain_created_date);
        domainExpiredTv = (TextView) findViewById(R.id.tv_domain_expire_date);
        domainNameTv = (TextView) findViewById(R.id.tv_domain_name);
        statusTv = (TextView) findViewById(R.id.tv_status);
        domainDetailsCard = (CardView) findViewById(R.id.cv_domain_details);
        emailDetailsCard = (CardView) findViewById(R.id.cv_email_details);
        chooseDomainLayout = (LinearLayout) findViewById(R.id.ll_choose_domain);
        expiredLayout = (LinearLayout) findViewById(R.id.ll_plan_expired);
        expireMsgTv = (TextView) expiredLayout.findViewById(R.id.tv_expire_msg);
        proceedBtn = (Button) findViewById(R.id.btn_proceed);
        chooseBtn = (RadioButton) findViewById(R.id.rb_book_a_domain);
        linkBtn = (RadioButton) findViewById(R.id.rb_link_a_domain);
        setSupportActionBar(toolbar);
        headerText.setText(getResources().getString(R.string.side_panel_row_domain_details));

        if (getSupportActionBar() != null ){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        session = new UserSessionManager(this, this);
        domainApiService = new DomainApiService(mBus);
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

    }

    private void initializePrices() {
        hmPrices.put(".COM", 680);
        hmPrices.put(".NET", 865);
        hmPrices.put(".CO.IN", 375);
        hmPrices.put(".IN", 490);
        hmPrices.put(".ORG", 500);
    }

    private void bindListeners() {

        proceedBtn.setOnClickListener(this);

    }
    private void linkDomain() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(DomainDetailsActivity.this)
                //.title(getString(R.string.have_an_existing_domain))
                .customView(R.layout.dialog_link_domain, false)
                .positiveColorRes(R.color.primaryColor);

            final MaterialDialog materialDialog = builder.show();
            View maView = materialDialog.getCustomView();
            final RadioButton rbPointExisting = (RadioButton) maView.findViewById(R.id.rbPointExisting);
            final RadioButton rbPointNFWeb = (RadioButton) maView.findViewById(R.id.rbPointNFWeb);
            final EditText edtComments = (EditText) maView.findViewById(R.id.edtComments);
            Button btnBack = (Button) maView.findViewById(R.id.btnBack);
            Button btnSubmitRequest = (Button) maView.findViewById(R.id.btnSubmitRequest);
            edtComments.setText(String.format(getString(R.string.link_comments), session.getFpTag()));
            edtComments.setSelection(edtComments.getText().toString().length());
            btnSubmitRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String subject = "";
                    if (rbPointNFWeb.isChecked()) {
                        subject = rbPointNFWeb.getText().toString();
                    } else if (rbPointExisting.isChecked()) {
                        subject = rbPointExisting.getText().toString();
                    }

                    if (TextUtils.isEmpty(subject)) {
                        Methods.showSnackBarNegative(DomainDetailsActivity.this,
                                getString(R.string.please_select_subject));
                    } else if (TextUtils.isEmpty(edtComments.getText().toString())) {
                        Methods.showSnackBarNegative(DomainDetailsActivity.this,
                                getString(R.string.please_enter_message));
                    } else {

                        MixPanelController.track(MixPanelController.LINK_DOMAIN, null);
                        materialDialog.dismiss();
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("Subject", subject);
                        hashMap.put("Mesg", edtComments.getText().toString());
                        domainApiService.linkDomain(hashMap, getLinkDomainParam());
                    }
                }
            });
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDialog.dismiss();
                }
            });

    }
    private HashMap<String, String> getLinkDomainParam() {
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("authClientId", Constants.clientId);
        offersParam.put("fpTag", session.getFpTag());
        return offersParam;
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
        if (domainAPI == DomainApiService.DomainAPI.RENEW_DOMAIN){

            showCustomDialog(getString(R.string.renew_domain),
                    getString(R.string.are_you_sure_do_you_want_to_book_domain),
                    getString(R.string.yes), getString(R.string.no), DialogFrom.DOMAIN_AVAILABLE);

        }else if(domainAPI == DomainApiService.DomainAPI.RENEW_NOT_AVAILABLE){
            showCustomDialog(getString(R.string.domain_booking_failed),getString(R.string.drop_us_on_email_or_call) ,
                    getString(R.string.ok), null, DialogFrom.DEFAULT);
            /*expireMsgTv.setText(String.format(getString(R.string.renew_domain_not_available_message),domainNameTv.getText().toString(),session.getFpTag()+getString(R.string.nowfloats_com)));
            expiredLayout.findViewById(R.id.btn_renew_domain).setVisibility(View.GONE);
            expiredLayout.findViewById(R.id.btn_book_domain).setVisibility(View.VISIBLE);
            expiredLayout.findViewById(R.id.btn_book_domain).setOnClickListener(this);*/

            /*if (domainBookDialog != null)
                 Methods.showSnackBarNegative(domainBookDialog.getView(), getString(R.string.domain_not_available));
            else{
                Methods.showSnackBarNegative(this, getString(R.string.domain_not_available));
            }*/

        } else if (domainAPI == DomainApiService.DomainAPI.CHECK_DOMAIN) {
            Methods.showSnackBarPositive(DomainDetailsActivity.this, "Domain is available");

            showCustomDialog(getString(R.string.book_a_new_domain),
                    getString(R.string.are_you_sure_do_you_want_to_book_domain),
                    getString(R.string.yes), getString(R.string.no), DialogFrom.DOMAIN_AVAILABLE);


        } else if (domainAPI == DomainApiService.DomainAPI.LINK_DOMAIN) {
            showCustomDialog(getString(R.string.domain_booking_process),"You have successfully requested to link a domain, Our team will contact you with in 48 hours." ,
                    getString(R.string.ok), null, DialogFrom.DEFAULT);
        } else {
            if (domainBookDialog != null)
                Methods.showSnackBarNegative(domainBookDialog.getView(), getString(R.string.domain_not_available));
            else {
                Methods.showSnackBarNegative(DomainDetailsActivity.this, getString(R.string.link_domain_not_available));
            }
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
            bookDomain(null,null, DomainApiService.DomainAPI.CHECK_DOMAIN);
            /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DomainDetailsActivity.this,
                    android.R.layout.simple_spinner_item, arrDomainExtensions);
            spDomainTypes.setAdapter(arrayAdapter);*/

            /*if(btnBookDomain.getVisibility() == View.VISIBLE || btnRenewDomain.getVisibility() == View.VISIBLE){
                setDomainYearsAdapter(domainYears);
                btnLinkDomain.setVisibility(View.VISIBLE);
            }else{
                setBookedDomainYears(bookedYears);
                spDomainYears.setSelection(0);
                spDomainYears.setEnabled(false);
                spDomainYears.setClickable(false);
            }
            if (!TextUtils.isEmpty(domainType)) {
                if (arrDomainExtensions.contains(domainType)) {
                    spDomainTypes.setSelection(arrDomainExtensions.indexOf(domainType));

                }
                spDomainTypes.setEnabled(false);
                spDomainTypes.setClickable(false);
            } else {
                spDomainTypes.setSelection(0);
            }*/

        } else {
            Methods.showSnackBarNegative(DomainDetailsActivity.this, getString(R.string.domain_details_getting_error));
        }

    }

   /* private void setBookedDomainYears(int years){
        Integer[] array = new Integer[1];
        array[0] =years;
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(DomainDetailsActivity.this,android.R.layout.simple_spinner_item,array);
        spDomainYears.setAdapter(adapter);
    }*/
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

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
                dateFormat.setCalendar(dbCalender);
                domainCreatedDate = dateFormat.format(dbCalender.getTime());
                dbCalender.add(Calendar.YEAR, domainDetails.getValidityInYears());

                domainExpiryDays = (int) ((dbCalender.getTimeInMillis() - currentTime) / totalNoOfDays);
                domainExpiryDate = dateFormat.format(dbCalender.getTime());
            }
            /*tvDomainStatus.setVisibility(View.VISIBLE);
            edtDomainName.setText(domainDetails.getDomainName());*/

            domainType = domainDetails.getDomainType();
            domainNameTv.setText(domainDetails.getDomainName()+domainType);
            if( !TextUtils.isEmpty(domainDetails.getErrorMessage()) && domainDetails.getIsProcessingFailed()){
                //error domain failed
                isDomainBookFailed = true;
            }

        }
        new API_Service(DomainDetailsActivity.this, session.getSourceClientId(), session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID), session.getFPID(), mBus);
    }

   /* private void setDomainYearsAdapter(int length){
        if(length == 0){
            return;
        }
        Integer[] array = new Integer[length];
        for (int i=1;i<=length;i++){
            array[i-1] = i;
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(DomainDetailsActivity.this,android.R.layout.simple_spinner_item,array);
        spDomainYears.setAdapter(adapter);
    }*/

    @Subscribe
    public void getStoreList(StoreEvent response) {
        hideLoader();
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
            //domainApiService.getDomainSupportedTypes(getDomainDetailsParam());
        } else {
            Methods.showSnackBarNegative(DomainDetailsActivity.this, getString(R.string.something_went_wrong));
        }
    }


    //------------------|--------- PlanExpiry<=0 ---------------PlanExpiry<90 --------|----- PlanExpiry>=90--------|
    //                  |                                |    p<60     plan renew     |                            |
    //-DomainExpiry<0-- |          Plan Expired          |    p>= 60  Renew Domain    |      Expired Renew Domain  |
    //                  |                                |                            |                            |
    //-DomainExpiry<30--|          Plan Expired          |       P<=D  renew plan     |      TBE-Renew Domain      |
    //                  |                                |     P-D>=60  renew domain  |                            |
    //                  |                                |     P-D<60  renew plan                        |                            |
    //-DomainExpiry>=30-|          Plan Expired          |    Do nothing(show domain) |    Do nothing(show domain) |
    //------------------|--------------------------------|----------------------------|----------------------------|

    private void applyDomainLogic() {
         if (planExpiryDays <= 0) {
             domainDetailsCard.setVisibility(View.VISIBLE);

             expiredLayout.setVisibility(View.VISIBLE);
             if(domainExpiryDays <= 0){
                 setDomainDetailsCard(false,"NowFloats Plan and Domain Expired");
                 expireMsgTv.setText(getString(R.string.renew_nowfloats_and_domain_plan));
             }
             else{
                 setDomainDetailsCard(true,"NowFloats Plan Expired");
                 expireMsgTv.setText(getString(R.string.renew_nowfloats_plan));
             }
             expiredLayout.findViewById(R.id.btn_plan_expired).setVisibility(View.VISIBLE);
             expiredLayout.findViewById(R.id.btn_plan_expired).setOnClickListener(this);
            //card background light_gray
            // nowfloats plan expired

        } else if(isDomainBookFailed){
             // will not come here
        } else if(TextUtils.isEmpty(domainType)){
             // first domain purchase widget exist
            if(planExpiryDays <=90){
                showExpiryDialog(LIGHT_HOUSE_EXPIRE, planExpiryDays);
            }
            if(get_fp_details_model.getFPWebWidgets() != null && get_fp_details_model.getFPWebWidgets().contains(FP_WEB_WIDGET_DOMAIN)){
                chooseDomainLayout.setVisibility(View.VISIBLE);
            }else{
                // do not have option to buy
            }

        }else if (domainExpiryDays <= 0) {
             // renew button
             //first check domain purchase widget exist
             if(planExpiryDays <=90){
                 showExpiryDialog(LIGHT_HOUSE_EXPIRE, planExpiryDays);
             }
             if(get_fp_details_model.getFPWebWidgets() != null && get_fp_details_model.getFPWebWidgets().contains(FP_WEB_WIDGET_DOMAIN)) {
                 setDomainDetailsCard(false,"Domain Expired");
                 expiredLayout.setVisibility(View.VISIBLE);
                 expiredLayout.findViewById(R.id.ll_domain_expired).setVisibility(View.VISIBLE);
                 expireMsgTv.setText(String.format(getString(R.string.renew_domain_message),domainNameTv.getText().toString(),session.getFpTag()+getString(R.string.nowfloats_com)));
                 expiredLayout.findViewById(R.id.btn_renew_domain).setOnClickListener(this);
                 expiredLayout.findViewById(R.id.btn_link_domain).setOnClickListener(this);
             }else{
                 // do not have option to renew
             }

             // first renew after that book new domain

        }else{

             setDomainDetailsCard(true,null);
             // margin 10
             //elevation 3
         }
    }

    private static final int LIGHT_HOUSE_EXPIRE = 0;
    private static final int WILD_FIRE_EXPIRE = 1;
    private static final int DEMO_EXPIRE = 3;
    private static final int DEMO_DAYS_LEFT = 4;
    private static final int LIGHT_HOUSE_DAYS_LEFT = 5;

    private void setDomainDetailsCard(boolean active,String statusMessage){
        domainDetailsCard.setVisibility(View.VISIBLE);
        emailDetailsCard.setVisibility(View.VISIBLE);
        if(active){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int margins = Methods.dpToPx(10, this);
            params.setMargins(margins, margins, margins, margins);
            domainDetailsCard.setLayoutParams(params);
            emailDetailsCard.setLayoutParams(params);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                domainDetailsCard.setElevation(3);
                emailDetailsCard.setElevation(3);
            }
        }else{
            domainDetailsCard.setBackgroundColor(ContextCompat.getColor(this,R.color.light_gray));
            emailDetailsCard.setBackgroundColor(ContextCompat.getColor(this,R.color.light_gray));
        }

        if (!TextUtils.isEmpty(statusMessage)){
            statusTv.setVisibility(View.VISIBLE);
            statusTv.setText(statusMessage);
        }
    }
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
                        Intent intent = new Intent(DomainDetailsActivity.this, PricingPlansActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_proceed:
                if (chooseBtn.isChecked()){
                    showLoader(getString(R.string.please_wait));
                    domainApiService.getDomainSupportedTypes(getDomainDetailsParam());
                }else{
                    linkDomain();
                }
                break;
            case R.id.btn_book_domain:
                showLoader(getString(R.string.please_wait));
                domainApiService.getDomainSupportedTypes(getDomainDetailsParam());
                break;
            case R.id.btn_renew_domain:
                bookDomain(domainNameTv.getText().toString(),domainType, DomainApiService.DomainAPI.RENEW_DOMAIN);

                break;
            case R.id.btn_link_domain:
                linkDomain();
                break;
            case R.id.btn_plan_expired:
                Intent intent = new Intent(DomainDetailsActivity.this, PricingPlansActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            default:
                break;
        }
    }

    private void bookDomain(String domainName, String domainType, final DomainApiService.DomainAPI domainApi) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                //.title(getString(R.string.book_a_new_domain))
                .customView(R.layout.dialog_book_a_domain, false)
                .positiveColorRes(R.color.primaryColor);


        if (!isFinishing()) {
            domainBookDialog = builder.show();
            View maView = domainBookDialog.getCustomView();
            final EditText edtDomainName = (EditText) maView.findViewById(R.id.edtDomainName);
            final Spinner spDomainTypes = (Spinner) maView.findViewById(R.id.spDomainTypes);
            TextView tvCompanyName = (TextView) maView.findViewById(R.id.tvCompanyName);
            TextView tvTag = (TextView) maView.findViewById(R.id.tvTag);
            TextView tvAddress = (TextView) maView.findViewById(R.id.tvAddress);
            TextView tvCity = (TextView) maView.findViewById(R.id.tvCity);
            final EditText edtZip = (EditText) maView.findViewById(R.id.edtZip);
            TextView tvCountryCode = (TextView) maView.findViewById(R.id.tvCountryCode);
            TextView tvISDCode = (TextView) maView.findViewById(R.id.tvISDCode);
            TextView tvCountry = (TextView) maView.findViewById(R.id.tvCountry);
            TextView tvEmail = (TextView) maView.findViewById(R.id.tvEmail);
            TextView tvPrimaryNumber = (TextView) maView.findViewById(R.id.tvPrimaryNumber);
            final TextView tvPrice = (TextView) maView.findViewById(R.id.tvPrice);
            final TextView tvPriceDef = (TextView) maView.findViewById(R.id.tvPriceDef);
            Button btnActivateDomain = (Button) maView.findViewById(R.id.btnActivateDomain);
            Button btnBack = (Button) maView.findViewById(R.id.btnBack);
//            btnActivateDomain.setEnabled(false);
//            btnActivateDomain.setClickable(false);

            if(!TextUtils.isEmpty(domainName)) {
                arrDomainExtensions = new ArrayList<>();
                arrDomainExtensions.add(domainType);
                edtDomainName.setText(domainName);
                spDomainTypes.setEnabled(false);
                edtDomainName.setEnabled(false);
                if (hmPrices.containsKey(domainType)) {
                    tvPrice.setText(hmPrices.get(domainType) + "*");
                } else {
                    tvPrice.setText("");
                }
            }
            tvPriceDef.setText(String.format(getString(R.string.price_of_domain), arrDomainExtensions.get(0)));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DomainDetailsActivity.this,
                    android.R.layout.simple_spinner_item, arrDomainExtensions);
            spDomainTypes.setAdapter(arrayAdapter);

            spDomainTypes.setSelection(0);

            tvTag.setText(get_fp_details_model.getAliasTag());
            tvCompanyName.setText(get_fp_details_model.getTag());
            tvAddress.setText(get_fp_details_model.getAddress());
            tvCity.setText(get_fp_details_model.getCity());
            if (!TextUtils.isEmpty(get_fp_details_model.getPinCode())) {
                edtZip.setText(get_fp_details_model.getPinCode());
                edtZip.setBackgroundDrawable(null);
                edtZip.setClickable(false);
                edtZip.setEnabled(false);
            }
            tvCountryCode.setText(get_fp_details_model.getLanguageCode());
            tvISDCode.setText(get_fp_details_model.getCountryPhoneCode());
            tvCountry.setText(get_fp_details_model.getCountry());
            tvEmail.setText(get_fp_details_model.getEmail());
            tvPrimaryNumber.setText(get_fp_details_model.getPrimaryNumber());

            btnActivateDomain
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Methods.hideKeyboard(DomainDetailsActivity.this);
                            String domainName = edtDomainName.getText().toString();
                            if (TextUtils.isEmpty(domainName)) {
                                Methods.showSnackBarNegative(DomainDetailsActivity.this,
                                        getString(R.string.enter_domain_name));
                            } else if (TextUtils.isEmpty(edtZip.getText().toString())) {
                                Methods.showSnackBarNegative(DomainDetailsActivity.this,
                                        getString(R.string.enter_zip_code));
                            } else {
                                showLoader(getString(R.string.please_wait));
                                get_fp_details_model.setDomainName(domainName);
                                get_fp_details_model.setDomainValidityInYears("1");
                                get_fp_details_model.setDomainType(spDomainTypes.getSelectedItem().toString());
                                get_fp_details_model.setPinCode(edtZip.getText().toString());
                                domainApiService.checkDomainAvailability(domainName, getDomainAvailabilityParam((String) spDomainTypes.getSelectedItem()),domainApi);
                            }
                        }
                    });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    domainBookDialog.dismiss();
                }
            });

            spDomainTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    tvPriceDef.setText(String.format(getString(R.string.price_of_domain), arrDomainExtensions.get(position)));
                    if (hmPrices.containsKey(arrDomainExtensions.get(position))) {
                        tvPrice.setText(hmPrices.get(arrDomainExtensions.get(position)) + "*");
                    } else {
                        tvPrice.setText("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
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
        hashMap.put("domainName", get_fp_details_model.getDomainName());
        hashMap.put("domainType", get_fp_details_model.getDomainType());
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
        hashMap.put("validityInYears",get_fp_details_model.getDomainValidityInYears());
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
