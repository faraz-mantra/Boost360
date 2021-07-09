package com.nowfloats.Store;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Adapters.ItemsRecyclerViewAdapter;
import com.nowfloats.Store.Model.AllPackage;
import com.nowfloats.Store.Model.OPCModels.UpdateDraftInvoiceModel;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Store.Model.PaymentTokenResult;
import com.nowfloats.Store.Model.PricingPlansModel;
import com.nowfloats.Store.Model.PurchaseDetail;
import com.nowfloats.Store.Model.ReceiveDraftInvoiceModel;
import com.nowfloats.Store.Model.ReceivedDraftInvoice;
import com.nowfloats.Store.Model.SendDraftInvoiceModel;
import com.nowfloats.Store.Model.SupportedPaymentMethods;
import com.nowfloats.Store.Model.TaxDetail;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DataBase;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;
import com.thinksity.Specific;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ProductCheckoutActivity extends AppCompatActivity {

    private UserSessionManager mSessionManager;
//    private OrderDataModel mOrderData;

    private String mNewPackage, mFinalAmount, mInvoiceId;

    Toolbar toolbar;
    MaterialDialog materialProgress;
    TextView headerText, tvUserName, tvUserEmail, tvPhoneNumber, tvNetTotal, tvTaxes,
             tvAmountToBePaid, tvTanNo, tvTdsAmount,btnPayNow, btnOpcApply;
    RecyclerView rvItems;
    EditText etOpc;
    LinearLayout llOpcInputLayout;

    TableRow trTanNo, trTdsAmount;

    ArrayList<ReceiveDraftInvoiceModel.KeyValuePair> mOpcDetails;

    private List<PackageDetails> mPurchasePlans;
    private final int DIRECT_REQUEST_CODE = 2013;
    private final int OPC_REQUEST_CODE = 2;
    private final int PADDLE_REQUEST_CODE = 3;
    private String[] mPackageIds;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_checkout);

        toolbar = (Toolbar) findViewById(R.id.product_checkout_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPackageIds = getIntent().getExtras().getStringArray("package_ids");
        mSessionManager = new UserSessionManager(this, this);

        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText(getResources().getString(R.string.product_checkout));

        materialProgress = new MaterialDialog.Builder(this)
                .widgetColorRes(R.color.accentColor)
                .content(getString(R.string.please_wait_))
                .progress(true, 0)
                .cancelable(false)
                .show();

        etOpc = (EditText) findViewById(R.id.etOpcCode);

        tvUserName = (TextView) findViewById(R.id.tv_username);
        tvUserEmail = (TextView) findViewById(R.id.tv_user_email);
        tvPhoneNumber = (TextView) findViewById(R.id.tv_user_phone_no);
        tvNetTotal = (TextView) findViewById(R.id.tv_net_total);
        tvTaxes = (TextView) findViewById(R.id.tv_taxes);
        tvAmountToBePaid = (TextView) findViewById(R.id.tv_amount_to_be_paid);
        tvTanNo = (TextView) findViewById(R.id.tv_user_tan_no);
        tvTdsAmount = (TextView) findViewById(R.id.tv_tds_amount);

        rvItems = (RecyclerView) findViewById(R.id.rv_store_items);
        trTanNo = (TableRow) findViewById(R.id.tr_tan_no);
        trTdsAmount = (TableRow) findViewById(R.id.tr_tds_amount);

        btnPayNow = findViewById(R.id.btn_pay_now);
        btnOpcApply = findViewById(R.id.btnOpcApply);
        llOpcInputLayout = findViewById(R.id.llOpcInputLayout);
        if (Specific.PACKAGE_NAME.equalsIgnoreCase("com.biz2.nowfloats")) {
            llOpcInputLayout.setVisibility(View.VISIBLE);
        } else {
            llOpcInputLayout.setVisibility(View.GONE);
        }
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ProductCheckoutActivity.this, PaymentOptionsActivity.class));

                if(!Util.isNullOrEmpty(mNewPackage) && !Util.isNullOrEmpty(mFinalAmount)) {
//                    Intent i = new Intent(ProductCheckoutActivity.this, InstaMojoMainActivity.class);
//                    mOrderData = new OrderDataModel(mSessionManager.getFpTag(), mSessionManager.getFpTag(),
//                            mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL),
//                            mFinalAmount , mNewPackage.substring(0, mNewPackage.length() - 4),
//                            mSessionManager.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM),
//                            "NowFloats Package", mPurchasePlans.get(0).getCurrencyCode());
//                    i.putExtra(com.romeo.mylibrary.Constants.PARCEL_IDENTIFIER, mOrderData);
//                    i.putExtra("packageList",new Gson().toJson(mPurchasePlans));
//                    //write logic for with and without opc cases
//                    if(!etOpc.isEnabled()) {
//                        if(mOpcDetails==null) {
//                            initiatePaymentProcess(i, mInvoiceId);
//                        }else {
//                            showConfirmationDialog(i, mInvoiceId);
//                        }
//                    }else {
//                        initiatePaymentProcess(i, mInvoiceId);
//                    }
                }else {
                    Methods.showSnackBarNegative(ProductCheckoutActivity.this, getString(R.string.error_in_processing_amount));
                }
            }
        });

        btnOpcApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateDraftInvoice(etOpc.getText().toString().trim());
            }
        });

        getPricingPlanDetails();

        /*PurchaseDetail purchaseDetail = new PurchaseDetail();
        String clientId;
        if (!Util.isNullOrEmpty(mSessionManager.getSourceClientId())) {
            clientId = mSessionManager.getSourceClientId();
        } else {
            clientId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
        }
        double totalTax = 0;
        if(StoreDataActivity.product.Taxes != null) {
            for (TaxDetail taxData : StoreDataActivity.product.Taxes*//*taxes*//*) {
                totalTax += taxData.getValue();
            }
        }
        purchaseDetail.setBasePrice(Double.parseDouble(StoreDataActivity.product.Price));
        purchaseDetail.setClientId(clientId);
        purchaseDetail.setDurationInMnths(StoreDataActivity.product.ValidityInMths);
        purchaseDetail.setFPId(mSessionManager.getFPID());
        purchaseDetail.setMRP(Double.parseDouble(StoreDataActivity.product.Price) + (Double.parseDouble(StoreDataActivity.product.Price)*totalTax)/100);
        purchaseDetail.setMRPCurrencyCode(StoreDataActivity.product.CurrencyCode);
        purchaseDetail.setPackageId(StoreDataActivity.product._id);
        purchaseDetail.setPackageName(StoreDataActivity.product.Name);
        purchaseDetail.setTaxDetails(StoreDataActivity.product.Taxes*//*taxes*//*);
        List<PurchaseDetail> purchaseDetailList = new ArrayList<PurchaseDetail>();
        purchaseDetailList.add(purchaseDetail);

        ReceiveDraftInvoiceModel receiveDraftInvoiceModel = new ReceiveDraftInvoiceModel();
        receiveDraftInvoiceModel.setPurchaseDetails(purchaseDetailList);*/

        //initializeVal(receiveDraftInvoiceModel, false);
        //createDraftInvoice();
    }

    private void getPricingPlanDetails() {

        String accId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
        String appId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID);
        String country = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY);
        Map<String, String> params = new HashMap<>();
        if (accId.length()>0){
            params.put("identifier", accId);
        }else{
            params.put("identifier", appId);
        }
        params.put("clientId", Constants.clientId);
        params.put("fpId", mSessionManager.getFPID());
        params.put("country",country.toLowerCase());
        params.put("fpCategory", mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).toUpperCase());

        Constants.restAdapter.create(StoreInterface.class).getStoreList(params, new Callback<PricingPlansModel>() {
            @Override
            public void success(PricingPlansModel storeMainModel, Response response) {
                if(storeMainModel != null){
                    preProcessAndDispatchPlans(storeMainModel);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Test", error.getMessage());
            }
        });

    }

    private void preProcessAndDispatchPlans(final PricingPlansModel storeMainModel){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PackageDetails> packageDetailsList = new ArrayList<>();
                if(mPackageIds == null)
                    return;
                for(AllPackage packageType : storeMainModel.allPackages){
                    for(PackageDetails packDetail : packageType.getValue()){
                        for (String item : mPackageIds) {
                            if (packDetail.getId().equalsIgnoreCase(item)) {
                                packageDetailsList.add(packDetail);
                                break; // No need to look further.
                            }
                        }
                    }
                }
                mPurchasePlans = packageDetailsList;
                List<PurchaseDetail> purchaseDetailList = new ArrayList<PurchaseDetail>();
                for(PackageDetails packageDetail : mPurchasePlans) {
                    PurchaseDetail purchaseDetail = new PurchaseDetail();
                    String clientId;
                    if (!Util.isNullOrEmpty(mSessionManager.getSourceClientId())) {
                        clientId = mSessionManager.getSourceClientId();
                    } else {
                        clientId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
                    }
                    double totalTax = 0;
                    if(packageDetail.getTaxes() != null) {
                        for (TaxDetail taxData : packageDetail.getTaxes()) {
                            totalTax += taxData.getValue();
                        }
                    }
                    purchaseDetail.setBasePrice(packageDetail.getPrice());
                    purchaseDetail.setClientId(clientId);
                    purchaseDetail.setDurationInMnths(packageDetail.getValidityInMths());
                    purchaseDetail.setFPId(mSessionManager.getFPID());
                    purchaseDetail.setMRP(packageDetail.getPrice() + (packageDetail.getPrice()*totalTax)/100);
                    purchaseDetail.setMRPCurrencyCode(packageDetail.getCurrencyCode());
                    purchaseDetail.setPackageId(packageDetail.getId());
                    purchaseDetail.setPackageName(packageDetail.getName());
                    purchaseDetail.setTaxDetails(packageDetail.getTaxes());
                    purchaseDetailList.add(purchaseDetail);
                }
                final ReceiveDraftInvoiceModel receiveDraftInvoiceModel = new ReceiveDraftInvoiceModel();
                receiveDraftInvoiceModel.setPurchaseDetails(purchaseDetailList);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        initializeVal(receiveDraftInvoiceModel, false);
                        createDraftInvoice();
                    }
                });
            }
        }).start();

    }

    private void showConfirmationDialog(final Intent i, final String mInvoiceId) {
        ReceiveDraftInvoiceModel.KeyValuePair keyVal = mOpcDetails.get(0);
        if(keyVal.getValue()!=null){
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.please_note_that_your_package_will_be_activated) + keyVal.getValue() +getString(R.string.are_you_sure_want_to_proceed))
                    .setPositiveButton(getString( R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initiatePaymentProcess(i, mInvoiceId);
                        }
                    })
                    .setNegativeButton(getString( R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();

        }else {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.dialog_to_be_activated_null_text))
                    .setPositiveButton(getString( R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initiatePaymentProcess(i, mInvoiceId);
                        }
                    })
                    .setNegativeButton(getString( R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }

    private void updateDraftInvoice(String OPCCode) {
        if(Util.isNullOrEmpty(OPCCode)){
            Methods.showSnackBarNegative(this, getString(R.string.online_voucher_cant_be_empty));
            return;
        }
        try {
            DataBase dataBase = new DataBase(ProductCheckoutActivity.this);
            Cursor cursor = dataBase.getLoginStatus();
            String fpUserProfileId;
            if (cursor.moveToFirst()){
                fpUserProfileId = cursor.getString(cursor.getColumnIndex(DataBase.colloginId));
            }else {
                showDialog("Alert!", getString(R.string.this_is_an_added_security_feature_to_protect_your_package_details_kindly_log_out_and_login_again_to_pay_for_this));
                return;
            }
            UpdateDraftInvoiceModel updateDraftInvoiceModel;
            if(mInvoiceId!=null && fpUserProfileId!=null) {
                updateDraftInvoiceModel = new UpdateDraftInvoiceModel(fpUserProfileId, OPCCode, mInvoiceId);
            }else {
                Methods.showSnackBarNegative(this, getString(R.string.unable_to_create_draft_invoice));
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("clientId", Constants.clientId);
            if(materialProgress!=null && !materialProgress.isShowing()){
                materialProgress.show();
            }
            if(updateDraftInvoiceModel==null){
                Methods.showSnackBarNegative(this, getString(R.string.unable_to_apply_coupon));
                return;
            }
            StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
            storeInterface.updateDraftInvoice(params, updateDraftInvoiceModel, new Callback<ReceivedDraftInvoice>() {
                @Override
                public void success(ReceivedDraftInvoice receiveDraftInvoice, Response response) {
                    if(receiveDraftInvoice!=null){
                        if(materialProgress!=null && materialProgress.isShowing()){
                            materialProgress.dismiss();
                        }
                        if(receiveDraftInvoice.getError().getErrorList()==null || receiveDraftInvoice.getStatusCode()==200) {
                            if(receiveDraftInvoice.getResult().getPurchaseDetails().get(0).getPackageId().equals(mPurchasePlans.get(0).getId())) {
                                etOpc.setEnabled(false);
                                btnOpcApply.setEnabled(false);
                                mInvoiceId = receiveDraftInvoice.getResult().getInvoiceId();
                                mOpcDetails = receiveDraftInvoice.getResult().getOpcDetails();
                                initializeVal(receiveDraftInvoice.getResult(), true);
                                Methods.showSnackBarPositive(ProductCheckoutActivity.this, getString(R.string.online_voucher_applied_successfully));
                            }else {
                                Methods.showSnackBarNegative(ProductCheckoutActivity.this, getString(R.string.the_entered_online_voucher_is_not_valid_for_this_product));
                            }
                        }else {
                            Methods.showSnackBarNegative(ProductCheckoutActivity.this, receiveDraftInvoice.getError().getErrorList().get(0).Key);
                            //layout.setError(;
                        }
                    }else {
                        if(materialProgress!=null && materialProgress.isShowing()){
                            materialProgress.dismiss();
                        }
                        Methods.showSnackBarNegative(ProductCheckoutActivity.this, getResources().getString(R.string.error_invoice));
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    if(materialProgress!=null && materialProgress.isShowing()){
                        materialProgress.dismiss();
                    }
                    Methods.showSnackBarNegative(ProductCheckoutActivity.this, getResources().getString(R.string.error_invoice));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            if(materialProgress!=null && materialProgress.isShowing()){
                materialProgress.dismiss();
            }
            Toast.makeText(this, R.string.error_while_generating_invoice, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==DIRECT_REQUEST_CODE || requestCode==OPC_REQUEST_CODE || requestCode == PADDLE_REQUEST_CODE && resultCode==RESULT_OK) {
            if (data == null) {
                return;
            }
            if(mOpcDetails!=null){
                data.putExtra("showToBeActivatedOn", true);
                if(mOpcDetails.get(0).getValue()!=null){
                    data.putExtra("toBeActivatedOn", mOpcDetails.get(0).getValue());
                }
            }
            setResult(RESULT_OK, data);
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initiatePaymentProcess(final Intent i, final String invoiceId) {
        if(mInvoiceId==null){
            Toast.makeText(this, getString(R.string.invalid_invoice), Toast.LENGTH_SHORT).show();
            return;
        }
        StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("clientId", Constants.clientId);
        params.put("invoiceId", invoiceId);

        SupportedPaymentMethods method = null;
        if(mPurchasePlans!=null &&
                mPurchasePlans.get(0).getSupportedPaymentMethods()!=null
                && mPurchasePlans.get(0).getSupportedPaymentMethods().size()>0){
            for (SupportedPaymentMethods paymentMethod : mPurchasePlans.get(0).getSupportedPaymentMethods()){
                if(paymentMethod.Type==1){
                    method = paymentMethod;
                }
            }
            if(materialProgress!=null && !materialProgress.isShowing()){
                materialProgress.show();
            }
            if(method != null)
            {
                method.RedirectUri = "https://hello.nowfloats.com";
            }

            storeInterface.initiatePaymentProcess(params, method, new Callback<PaymentTokenResult>() {
                @Override
                public void success(PaymentTokenResult paymentTokenResult, Response response) {
                    if (materialProgress!=null){
                        materialProgress.dismiss();
                    }
                    if(paymentTokenResult!=null && paymentTokenResult.getResult()!=null) {
                        switch (paymentTokenResult.getResult().getPaymentMethodType())
                        {
                            case "INSTAMOJO":
//                                i.putExtra(com.romeo.mylibrary.Constants.PAYMENT_REQUEST_IDENTIFIER, paymentTokenResult.getResult().getPaymentRequestId());
//                                i.putExtra(com.romeo.mylibrary.Constants.ACCESS_TOKEN_IDENTIFIER, paymentTokenResult.getResult().getAccessToken());
//                                i.putExtra(com.romeo.mylibrary.Constants.WEB_HOOK_IDENTIFIER, "https://api.withfloats.com/Payment/v1/floatingpoint/instaMojoWebHook?clientId="+Constants.clientId);//change this later

                                startActivityForResult(i, OPC_REQUEST_CODE);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;
                            case "PADDLE":
                                Intent paddleIntent = new Intent(ProductCheckoutActivity.this, PaddleCheckoutActivity.class);
                                paddleIntent.putExtra("paymentUrl", paymentTokenResult.getResult().getTargetPaymentCollectionUri());
                                startActivityForResult(paddleIntent, PADDLE_REQUEST_CODE);
                                break;
                            default:
                                Methods.showSnackBarNegative(ProductCheckoutActivity.this, getString(R.string.error_while_processing_payment));
                        }

                    }else {
                        Methods.showSnackBarNegative(ProductCheckoutActivity.this, getString(R.string.error_while_processing_payment));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if(materialProgress!=null && materialProgress.isShowing()){
                        materialProgress.dismiss();
                    }
                    Methods.showSnackBarNegative(ProductCheckoutActivity.this, getString(R.string.error_while_processing_payment));
                }
            });
        }

    }

    private void createDraftInvoice() {
        try {
            SendDraftInvoiceModel sendDraftInvoiceModel = new SendDraftInvoiceModel();
            List<PurchaseDetail> purchaseDetailList = new ArrayList<PurchaseDetail>();
            for(PackageDetails packageDetail : mPurchasePlans) {
                PurchaseDetail purchaseDetail = new PurchaseDetail();
                String clientId;

                if (!Util.isNullOrEmpty(mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID))) {
                    clientId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
                } else if (!Util.isNullOrEmpty(mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID))) {
                    clientId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID);
                } else if (!Util.isNullOrEmpty(mSessionManager.getSourceClientId())) {
                    clientId = mSessionManager.getSourceClientId();
                } else {
                    Methods.showSnackBarNegative(this, getString(R.string.can_t_proceed_for_payment));
                    return;
                }

                double totalTax = 0;
                if (packageDetail.getTaxes() != null) {
                    for (TaxDetail taxData : packageDetail.getTaxes()) {
                        totalTax += taxData.getValue();
                    }
                }
                purchaseDetail.setBasePrice(packageDetail.getPrice());
                purchaseDetail.setClientId(clientId);
                purchaseDetail.setDurationInMnths(packageDetail.getValidityInMths());
                purchaseDetail.setFPId(mSessionManager.getFPID());
                purchaseDetail.setMRP(packageDetail.getPrice() + (packageDetail.getPrice() * totalTax) / 100);
                purchaseDetail.setMRPCurrencyCode(packageDetail.getCurrencyCode());
                purchaseDetail.setPackageId(packageDetail.getId());
                purchaseDetail.setPackageName(packageDetail.getName());
                purchaseDetail.setTaxDetails(packageDetail.getTaxes());
                purchaseDetailList.add(purchaseDetail);
            }

            sendDraftInvoiceModel.setPurchaseDetails(purchaseDetailList);
            DataBase dataBase = new DataBase(ProductCheckoutActivity.this);
            Cursor cursor = dataBase.getLoginStatus();
            if (cursor.moveToFirst() && !cursor.getString(cursor.getColumnIndex(DataBase.colloginId)).equals("0")){
                sendDraftInvoiceModel.setFpUserProfileId(cursor.getString(cursor.getColumnIndex(DataBase.colloginId)));
                sendDraftInvoiceModel.setOpc(null);
            }else {
                showDialog("Alert!", getString(R.string.this_is_an_added_security_feature_to_protect_your_package_details_kindly_log_out_and_login_again_to_pay_for_this_package));
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("clientId", Constants.clientId);
            if(materialProgress!=null && !materialProgress.isShowing()){
                materialProgress.show();
            }
            StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
            storeInterface.createDraftInvoice(params, sendDraftInvoiceModel, new Callback<ReceivedDraftInvoice>() {
                @Override
                public void success(ReceivedDraftInvoice receiveDraftInvoice, Response response) {
                    if(receiveDraftInvoice!=null && receiveDraftInvoice.getStatusCode()==200){
                        if(materialProgress!=null && materialProgress.isShowing()){
                            materialProgress.dismiss();
                        }
                        initializeVal(receiveDraftInvoice.getResult(), false);
                        mInvoiceId = receiveDraftInvoice.getResult().getInvoiceId();
                    }else {
                        if(materialProgress!=null && materialProgress.isShowing()){
                            materialProgress.dismiss();
                        }
                        Methods.showSnackBarNegative(ProductCheckoutActivity.this, getResources().getString(R.string.error_invoice));
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    if(materialProgress!=null && materialProgress.isShowing()){
                        materialProgress.dismiss();
                    }
                    Methods.showSnackBarNegative(ProductCheckoutActivity.this, getResources().getString(R.string.error_invoice));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            if(materialProgress!=null && materialProgress.isShowing()){
                materialProgress.dismiss();
            }
            Toast.makeText(ProductCheckoutActivity.this, "Error while generating Invoice", Toast.LENGTH_SHORT).show();
        }
    }

    // showing list of products with discount, if opc added
    private void initializeVal(final ReceiveDraftInvoiceModel invoiceData, boolean showDiscount) {
        if(invoiceData==null || mPurchasePlans == null){
            return;
        }
        tvUserName.setText(mSessionManager.getFpTag().toLowerCase());
        tvUserEmail.setText(mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
        tvPhoneNumber.setText(mSessionManager.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM));
        if(showDiscount) {
            trTanNo.setVisibility(View.VISIBLE);
            tvTanNo.setText(invoiceData.getTanNumber() + "");
        }
        double netAmount = 0;
        if (invoiceData.getPurchaseDetails() != null && invoiceData.getPurchaseDetails().size() != 0) {
            for (PurchaseDetail data : invoiceData.getPurchaseDetails()) {
                if (data.getDiscount() == null) {
                    netAmount += data.getBasePrice();
                } else {
                    netAmount += (data.getBasePrice() - (data.getBasePrice() * data.getDiscount().value / 100.0));
                }
            }
            netAmount = Math.round((netAmount * 100) / 100.0);
            tvNetTotal.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " +
                    NumberFormat.getIntegerInstance(Locale.US).format(netAmount) + " /-");

            float taxVal = 0;
            StringBuilder taxNames = new StringBuilder();

            for (TaxDetail taxData : invoiceData.getPurchaseDetails().get(0).getTaxDetails()) {
                taxVal += taxData.getValue();
                taxNames.append(taxData.getKey() + "-" + taxData.getValue() + "%,\n ");
            }

            double taxAmount = 0;
            if (invoiceData.getPurchaseDetails().get(0).getTaxDetails().get(0).getAmountType() == 0) {
                taxAmount = (netAmount * taxVal) / 100.0;
            } else {
                taxAmount = (int) taxVal;
            }
            taxAmount = Math.round((taxAmount * 100) / 100.0);
            tvTaxes.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " +
                    NumberFormat.getIntegerInstance(Locale.US).format(taxAmount) + " /-\n" + "( " + taxNames.substring(0, taxNames.length() - 3) + " )");
            if (showDiscount) {
                trTdsAmount.setVisibility(View.VISIBLE);
                tvTdsAmount.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " + invoiceData.getTdsAmount());
            }

            tvAmountToBePaid.setText(mPurchasePlans.get(0).getCurrencyCode() + " " +
                    NumberFormat.getIntegerInstance(Locale.US).format(Math.round((netAmount + taxAmount - invoiceData.getTdsAmount()) * 100) / 100) + " /-");
            String packages = "";
            for (int i = 0; i < invoiceData.getPurchaseDetails().size(); i++) {
                packages += invoiceData.getPurchaseDetails().get(i).getPackageName() + " and ";
            }
            mNewPackage = packages;
            mFinalAmount = String.valueOf(Math.round((netAmount + taxAmount - invoiceData.getTdsAmount()) * 100) / 100);


            rvItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            ItemsRecyclerViewAdapter adapter = new ItemsRecyclerViewAdapter(invoiceData.getPurchaseDetails(), mPurchasePlans.get(0).getCurrencyCode(), showDiscount);
            rvItems.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private void showDialog(String title, String msg){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(msg).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }

    //TODO: tax calculation re calculate
}
