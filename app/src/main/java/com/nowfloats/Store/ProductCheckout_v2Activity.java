package com.nowfloats.Store;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Adapters.ItemsRecyclerViewAdapter;
import com.nowfloats.Store.Model.AllPackage;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Store.Model.PricingPlansModel;
import com.nowfloats.Store.Model.PurchaseDetail;
import com.nowfloats.Store.Model.ReceiveDraftInvoiceModel;
import com.nowfloats.Store.Model.TaxDetail;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.romeo.mylibrary.Models.OrderDataModel;
import com.thinksity.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ProductCheckout_v2Activity extends AppCompatActivity {

    private static final int DIRECT_REQUEST_CODE = 2013;
    private UserSessionManager mSessionManager;
    private OrderDataModel mOrderData;

    private String mNewPackage, mFinalAmount;

    Toolbar toolbar;
    MaterialDialog materialProgress;
    TextView headerText, tvUserName, tvUserEmail, tvPhoneNumber, tvNetTotal, tvTaxes,
            tvAmountToBePaid, tvTanNo, tvTdsAmount;
    RecyclerView rvItems;
    TextView btnPayNow, btnOpcApply;
    EditText etOpc;

    TableRow trTanNo, trTdsAmount;

    private List<PackageDetails> mPurchasePlans;
    private String[] mPackageIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_checkout);

        toolbar = (Toolbar) findViewById(R.id.product_checkout_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPackageIds = getIntent().getExtras().getStringArray("package_ids");
        mSessionManager = new UserSessionManager(this, this);

        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText(getResources().getString(R.string.product_checkout));

        materialProgress = new MaterialDialog.Builder(this)
                .widgetColorRes(R.color.accentColor)
                .content("Please Wait...")
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
        findViewById(R.id.llOpcInputLayout).setVisibility(View.GONE);
        btnPayNow = findViewById(R.id.btn_pay_now);
        btnOpcApply = findViewById(R.id.btnOpcApply);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ProductCheckoutActivity.this, PaymentOptionsActivity.class));

                if (!Util.isNullOrEmpty(mNewPackage) && !Util.isNullOrEmpty(mFinalAmount)) {
                    Intent i = new Intent(ProductCheckout_v2Activity.this, PaymentOptionsActivity.class);
                    mOrderData = new OrderDataModel(mSessionManager.getFpTag(), mSessionManager.getFpTag(),
                            mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL),
                            mFinalAmount, mNewPackage.substring(0, mNewPackage.length() - 4),
                            mSessionManager.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM),
                            "NowFloats Package", mPurchasePlans.get(0).getCurrencyCode());
                    i.putExtra(com.romeo.mylibrary.Constants.PARCEL_IDENTIFIER, mOrderData);
                    i.putExtra("packageList", new Gson().toJson(mPurchasePlans));
                    startActivityForResult(i, DIRECT_REQUEST_CODE);
                    //write logic for with and without opc cases
                } else {
                    Methods.showSnackBarNegative(ProductCheckout_v2Activity.this, "Error in processing Amount");
                }
            }
        });

        getPricingPlanDetails();
    }

    private void getPricingPlanDetails() {

        String accId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
        String appId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID);
        String country = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY);
        Map<String, String> params = new HashMap<>();
        if (accId.length() > 0) {
            params.put("identifier", accId);
        } else {
            params.put("identifier", appId);
        }
        params.put("clientId", Constants.clientId);
        params.put("fpId", mSessionManager.getFPID());
        params.put("country", country.toLowerCase());
        params.put("fpCategory", mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).toUpperCase());

        Constants.restAdapter.create(StoreInterface.class).getStoreList(params, new Callback<PricingPlansModel>() {
            @Override
            public void success(PricingPlansModel storeMainModel, Response response) {
                if (storeMainModel != null) {
                    preProcessAndDispatchPlans(storeMainModel);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Test", error.getMessage());
            }
        });

    }

    private void preProcessAndDispatchPlans(final PricingPlansModel storeMainModel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PackageDetails> packageDetailsList = new ArrayList<>();
                if (mPackageIds == null)
                    return;
                for (AllPackage packageType : storeMainModel.allPackages) {
                    for (PackageDetails packDetail : packageType.getValue()) {
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
                for (PackageDetails packageDetail : mPurchasePlans) {
                    PurchaseDetail purchaseDetail = new PurchaseDetail();
                    String clientId;
                    if (!Util.isNullOrEmpty(mSessionManager.getSourceClientId())) {
                        clientId = mSessionManager.getSourceClientId();
                    } else {
                        clientId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
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
                final ReceiveDraftInvoiceModel receiveDraftInvoiceModel = new ReceiveDraftInvoiceModel();
                receiveDraftInvoiceModel.setPurchaseDetails(purchaseDetailList);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        initializeVal(receiveDraftInvoiceModel, false);
                    }
                });
            }
        }).start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DIRECT_REQUEST_CODE) {
            setResult(RESULT_OK, data);
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // showing list of products with discount, if opc added
    private void initializeVal(final ReceiveDraftInvoiceModel invoiceData, boolean showDiscount) {
        if (invoiceData == null || mPurchasePlans == null) {
            return;
        }
        tvUserName.setText(mSessionManager.getFpTag().toLowerCase());
        tvUserEmail.setText(mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
        tvPhoneNumber.setText(mSessionManager.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM));
        if (showDiscount) {
            trTanNo.setVisibility(View.VISIBLE);
            tvTanNo.setText(invoiceData.getTanNumber() + "");
        }
        double netAmount = 0;
        for (PurchaseDetail data : invoiceData.getPurchaseDetails()) {
            if (data.getDiscount() == null) {
                netAmount += data.getBasePrice();
            } else {
                netAmount += (data.getBasePrice() - (data.getBasePrice() * data.getDiscount().value / 100.0));
            }
        }
        netAmount = Math.round((netAmount * 100) / 100.0);
        if (invoiceData != null && invoiceData.getPurchaseDetails() != null && !invoiceData.getPurchaseDetails().isEmpty()) {
            tvNetTotal.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " +
                    NumberFormat.getIntegerInstance(Locale.US).format(netAmount) + " /-");
        }
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
        if (materialProgress.isShowing())
            materialProgress.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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
