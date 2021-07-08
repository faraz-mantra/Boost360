package com.nowfloats.Store;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Adapters.ItemsRecyclerViewAdapter;
import com.nowfloats.Store.Model.AllPackage;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Store.Model.PricingPlansModel;
import com.nowfloats.Store.Model.PurchaseDetail;
import com.nowfloats.Store.Model.ReceiveDraftInvoiceModel;
import com.nowfloats.Store.Model.SalesmanModel;
import com.nowfloats.Store.Model.TaxDetail;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
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


public class ProductCheckout_v2Activity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnTouchListener {

    private static final int DIRECT_REQUEST_CODE = 2013;
    public RadioGroup rgSalesman;
//    private OrderDataModel mOrderData;
    private UserSessionManager mSessionManager;
    private String mNewPackage, mFinalAmount;
    private Toolbar toolbar;
    private MaterialDialog materialProgress;
    private TextView headerText, tvUserName, tvUserEmail, tvPhoneNumber, tvNetTotal, tvTaxes,
            tvAmountToBePaid, tvTanNo, tvTdsAmount, tvDiscountAmount, tvDiscountPercentage;
    private RecyclerView rvItems;
    private TextView btnPayNow, btnOpcApply, btnDeleteOPC;
    private EditText etOpc, edtSalesman;
    private TableRow trTanNo, trTdsAmount;

    private List<PackageDetails> mPurchasePlans;
    private String[] mPackageIds;

    private TableRow trDiscount;

    private DiscountCoupon discountCoupon;

    private CheckBox cvTwo, cvTen;

    private int tdsPercentage;

    private SalesmanModel salesmanModel;
    private ArrayList<SalesmanModel> arrSalesman;
    private ReceiveDraftInvoiceModel receiveDraftInvoiceModel;

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
        tdsPercentage = 0;
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
        edtSalesman = (EditText) findViewById(R.id.edtSalesman);
        rgSalesman = (RadioGroup) findViewById(R.id.rgSalesman);

        tvUserName = (TextView) findViewById(R.id.tv_username);
        tvUserEmail = (TextView) findViewById(R.id.tv_user_email);
        tvPhoneNumber = (TextView) findViewById(R.id.tv_user_phone_no);
        tvNetTotal = (TextView) findViewById(R.id.tv_net_total);
        tvTaxes = (TextView) findViewById(R.id.tv_taxes);
        tvAmountToBePaid = (TextView) findViewById(R.id.tv_amount_to_be_paid);
        tvTanNo = (TextView) findViewById(R.id.tv_user_tan_no);
        tvTdsAmount = (TextView) findViewById(R.id.tv_tds_amount);
        tvDiscountPercentage = (TextView) findViewById(R.id.tv_discount_percent);
        tvDiscountAmount = (TextView) findViewById(R.id.tv_discount_amount);
        trDiscount = (TableRow) findViewById(R.id.trDiscount);
        cvTwo = (CheckBox) findViewById(R.id.cvTwo);
        cvTen = (CheckBox) findViewById(R.id.cvTen);

        rvItems = (RecyclerView) findViewById(R.id.rv_store_items);
        trTanNo = (TableRow) findViewById(R.id.tr_tan_no);
        trTdsAmount = (TableRow) findViewById(R.id.tr_tds_amount);
//        findViewById(R.id.llOpcInputLayout).setVisibility(View.GONE);
        btnPayNow = findViewById(R.id.btn_pay_now);
        btnOpcApply = findViewById(R.id.btnOpcApply);
        btnDeleteOPC = findViewById(R.id.btnDeleteOPC);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ProductCheckoutActivity.this, PaymentOptionsActivity.class));

                if (!Util.isNullOrEmpty(mNewPackage) && !Util.isNullOrEmpty(mFinalAmount)) {
//                    Intent i = new Intent(ProductCheckout_v2Activity.this, PaymentOptionsActivity.class);
//                    mOrderData = new OrderDataModel(mSessionManager.getFpTag(), mSessionManager.getFpTag(),
//                            mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL),
//                            mFinalAmount, mNewPackage.substring(0, mNewPackage.length() - 4),
//                            mSessionManager.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM),
//                            "NowFloats Package", mPurchasePlans.get(0).getCurrencyCode());
//                    i.putExtra(com.romeo.mylibrary.Constants.PARCEL_IDENTIFIER, mOrderData);
//                    i.putExtra("packageList", new Gson().toJson(mPurchasePlans));
//                    i.putExtra("discountCoupon", discountCoupon);
//                    i.putExtra("salesmanModel", salesmanModel);
//                    i.putExtra("tdsPercentage", tdsPercentage);
//                    startActivityForResult(i, DIRECT_REQUEST_CODE);
                    //write logic for with and without opc cases
                } else {
                    Methods.showSnackBarNegative(ProductCheckout_v2Activity.this, "Error in processing Amount");
                }
            }
        });


        btnDeleteOPC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discountCoupon = null;
                initializeVal(receiveDraftInvoiceModel, false);
                btnDeleteOPC.setVisibility(View.GONE);
                btnOpcApply.setVisibility(View.VISIBLE);
                etOpc.setEnabled(true);
                hideDiscount();
            }
        });

        btnOpcApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyPromo();
            }
        });

        getPricingPlanDetails();

        cvTwo.setOnCheckedChangeListener(this);
        cvTen.setOnCheckedChangeListener(this);

        edtSalesman.setVisibility(View.GONE);
        edtSalesman.setFocusable(false);
        edtSalesman.setFocusableInTouchMode(false);
        edtSalesman.setOnTouchListener(this);

        rgSalesman.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() == R.id.radioButton_yes) {
                    edtSalesman.setVisibility(View.VISIBLE);
                } else {
                    edtSalesman.setText("");
                    edtSalesman.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (v.getId() == R.id.edtSalesman) {
                getSalesmanList();
                return true;
            }
        }
        return false;
    }

    private void getSalesmanList() {
        if (arrSalesman == null) {
            final ProgressDialog pd = ProgressDialog.show(this, "", getResources().getString(R.string.wait_while_loading_salesman));
            StoreInterface api = Constants.restAdapterSalesman.create(StoreInterface.class);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("clientId", "ERPWE0892F2F45388F439BDE9F6F3FB5C31F0FAA628D40CD2920A79D8841597B");//
            // Constants.clientId);
            api.getActiveEmployees(jsonObject, new Callback<ArrayList<SalesmanModel>>() {

                @Override
                public void success(ArrayList<SalesmanModel> salesmanModels, Response response) {
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    arrSalesman = salesmanModels;
                    showSalesmanDialog(arrSalesman);
                }

                @Override
                public void failure(RetrofitError error) {
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    Toast.makeText(ProductCheckout_v2Activity.this
                            , getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            showSalesmanDialog(arrSalesman);
        }
    }

    private void showSalesmanDialog(ArrayList<SalesmanModel> arrSalesman) {

        final ArrayAdapter<SalesmanModel> adapter = new ArrayAdapter<>(this,
                R.layout.search_list_item_layout, arrSalesman);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle(R.string.select_salesman);

        View view = LayoutInflater.from(this).inflate(R.layout.search_list_layout, null);
        builderSingle.setView(view);

        EditText edtSearch = (EditText) view.findViewById(R.id.edtSearch);
        ListView lvItems = (ListView) view.findViewById(R.id.lvItems);

        lvItems.setAdapter(adapter);


        final Dialog dialog = builderSingle.show();

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SalesmanModel strVal = adapter.getItem(position);
                salesmanModel = strVal;
                dialog.dismiss();
                edtSalesman.setText(strVal.getEmployeeId());
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString().toLowerCase());
            }
        });

        dialog.setCanceledOnTouchOutside(false);
    }

    private void applyPromo() {

        try {
            showLoader();
            StoreInterface storeInterface = Constants.restAdapterV2.create(StoreInterface.class);
            RedeemDiscountRequestModel redeemDiscountRequestModel
                    = new RedeemDiscountRequestModel(Constants.clientId, etOpc.getText().toString(), false);
            storeInterface.redeemDiscountCode(redeemDiscountRequestModel, new Callback<DiscountCoupon>() {

                @Override
                public void success(DiscountCoupon discountResponse, Response response) {
                    discountCoupon = discountResponse;
                    discountCoupon.setCouponCode(etOpc.getText().toString());
                    btnDeleteOPC.setVisibility(View.VISIBLE);
                    etOpc.setEnabled(false);
                    btnOpcApply.setVisibility(View.GONE);
                    trDiscount.setVisibility(View.VISIBLE);
                    if (receiveDraftInvoiceModel != null) {
                        initializeVal(receiveDraftInvoiceModel, false);
                    }
                    hideLoader();
                }

                @Override
                public void failure(RetrofitError error) {
                    hideDiscount();
                    if (error.getResponse().getStatus() == 400) {
                        Toast.makeText(ProductCheckout_v2Activity.this, getString(R.string.coupon_does_not_exist), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductCheckout_v2Activity.this, getString(R.string.error_processing_the_request), Toast.LENGTH_SHORT).show();
                    }
                    hideLoader();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideDiscount();
            hideLoader();
            Toast.makeText(ProductCheckout_v2Activity.this, getString(R.string.error_processing_the_request), Toast.LENGTH_SHORT).show();
        }

    }

    private void hideDiscount() {
        etOpc.setText("");
        discountCoupon = null;
        trDiscount.setVisibility(View.GONE);
        tvDiscountPercentage.setText("");
        tvDiscountAmount.setText("");
    }

    private void showLoader() {
        materialProgress = new MaterialDialog.Builder(this)
                .widgetColorRes(R.color.accentColor)
                .content(getString(R.string.please_wait_))
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    private void hideLoader() {
        if (materialProgress != null && materialProgress.isShowing()) {
            materialProgress.dismiss();
        }
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
                receiveDraftInvoiceModel = new ReceiveDraftInvoiceModel();
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
        double netAmount = 0, discountAmount = 0;
        for (PurchaseDetail data : invoiceData.getPurchaseDetails()) {
//            if (data.getDiscount() == null) {
//                if (discountCoupon != null) {
//                    netAmount += (data.getBasePrice() - (data.getBasePrice() * discountCoupon.getDiscountPercentage() / 100.0));
//                } else {
            netAmount += data.getBasePrice();
//                }
//            } else {
//                double discountPercentage = data.getDiscount().value;
//                if (discountCoupon != null) {
//                    discountPercentage = discountCoupon.getDiscountPercentage();
//                }
//                netAmount += (data.getBasePrice() - (data.getBasePrice() * discountPercentage / 100.0));
//            }
        }

        netAmount = Math.round((netAmount * 100) / 100.0);
        if (invoiceData != null && invoiceData.getPurchaseDetails() != null && !invoiceData.getPurchaseDetails().isEmpty()) {
            tvNetTotal.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " +
                    NumberFormat.getIntegerInstance(Locale.US).format(netAmount) + " /-");
        }
        double taxVal = 0;
        StringBuilder taxNames = new StringBuilder();

        for (TaxDetail taxData : invoiceData.getPurchaseDetails().get(0).getTaxDetails()) {
            taxVal += taxData.getValue();
            taxNames.append(taxData.getKey() + "-" + taxData.getValue() + "%,\n ");
        }

        if (discountCoupon != null) {
            discountAmount += (netAmount * discountCoupon.getDiscountPercentage() / 100.0);
            tvDiscountPercentage.setText("Promo Disc (" + discountCoupon.getDiscountPercentage() + "%) ");
            tvDiscountAmount.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " + discountAmount + " /-");
        } else {
            hideDiscount();
        }

        double taxAmount = 0, tdsAmount = 0;
        if (invoiceData.getPurchaseDetails().get(0).getTaxDetails().get(0).getAmountType() == 0) {
            taxAmount = ((netAmount - discountAmount) * taxVal) / 100.0;
        } else {
            taxAmount = (int) taxVal;
        }
        taxAmount = Math.round((taxAmount * 100) / 100.0);

//        tdsAmount = Math.round((((netAmount + taxAmount - discountAmount) * tdsPercentage / 100)));
        tdsAmount = Math.round((((netAmount - discountAmount) * tdsPercentage / 100)));

        tvTaxes.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " +
                NumberFormat.getIntegerInstance(Locale.US).format(taxAmount) + " /-\n" + "( " + taxNames.substring(0, taxNames.length() - 3) + " )");
        if (tdsPercentage > 0) {
            trTdsAmount.setVisibility(View.VISIBLE);
            tvTdsAmount.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " + tdsAmount + " /-");
        } else {
            trTdsAmount.setVisibility(View.GONE);
            tvTdsAmount.setText("");
        }

        tvAmountToBePaid.setText(mPurchasePlans.get(0).getCurrencyCode() + " " +
                NumberFormat.getIntegerInstance(Locale.US).format(Math.round((netAmount + taxAmount - discountAmount - tdsAmount) * 100) / 100) + " /-");
        String packages = "";
        for (int i = 0; i < invoiceData.getPurchaseDetails().size(); i++) {
            packages += invoiceData.getPurchaseDetails().get(i).getPackageName() + " and ";
        }
        mNewPackage = packages;
        mFinalAmount = String.valueOf(Math.round((netAmount + taxAmount - discountAmount - tdsAmount) * 100) / 100);


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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


        if (buttonView.getId() == R.id.cvTwo) {
            if (!isChecked && tdsPercentage == 2) {
                tdsPercentage = 0;
                initializeVal(receiveDraftInvoiceModel, false);
            } else if (isChecked) {
                tdsPercentage = 2;
                initializeVal(receiveDraftInvoiceModel, false);
                cvTen.setChecked(false);
            }
        } else if (buttonView.getId() == R.id.cvTen) {
            if (!isChecked && tdsPercentage == 10) {
                tdsPercentage = 0;
                initializeVal(receiveDraftInvoiceModel, false);
            } else if (isChecked) {
                tdsPercentage = 10;
                initializeVal(receiveDraftInvoiceModel, false);
                cvTwo.setChecked(false);
            }
        }
    }

}
