package com.nowfloats.Store;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Adapters.ItemsRecyclerViewAdapter;
import com.nowfloats.Store.Model.OPCModels.UpdateDraftInvoiceModel;
import com.nowfloats.Store.Model.PaymentTokenResult;
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
import com.romeo.mylibrary.Models.OrderDataModel;
import com.romeo.mylibrary.ui.InstaMojoMainActivity;
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


public class ProductCheckoutActivity extends AppCompatActivity {

    private UserSessionManager mSessionManager;
    private OrderDataModel mOrderData;

    private String mNewPackage, mFinalAmount, mInvoiceId;

    Toolbar toolbar;
    MaterialDialog materialProgress;
    TextView headerText, tvUserName, tvUserEmail, tvPhoneNumber, tvNetTotal, tvTaxes,
             tvAmountToBePaid, tvTanNo, tvTdsAmount;
    RecyclerView rvItems;
    Button btnPayNow, btnOpcApply;
    EditText etOpc;

    TableRow trTanNo, trTdsAmount;

    ArrayList<ReceiveDraftInvoiceModel.KeyValuePair> mOpcDetails;


    private final int DIRECT_REQUEST_CODE = 1;
    private final int OPC_REQUEST_CODE = 2;
    

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

        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText(getResources().getString(R.string.product_checkout));

        mSessionManager = new UserSessionManager(getApplicationContext(), this);

        materialProgress = new MaterialDialog.Builder(this)
                .widgetColorRes(R.color.accentColor)
                .content("Please Wait...")
                .progress(true, 0)
                .cancelable(false).build();

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

        btnPayNow = (Button) findViewById(R.id.btn_pay_now);
        btnOpcApply = (Button) findViewById(R.id.btnOpcApply);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Util.isNullOrEmpty(mNewPackage) && !Util.isNullOrEmpty(mFinalAmount)) {
                    Intent i = new Intent(ProductCheckoutActivity.this, InstaMojoMainActivity.class);
                    mOrderData = new OrderDataModel(mSessionManager.getFpTag(), mSessionManager.getFpTag(),
                            mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL),
                            mFinalAmount , mNewPackage.substring(0, mNewPackage.length() - 4),
                            mSessionManager.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM),
                            "NowFloats Package", StoreDataActivity.product.CurrencyCode);
                    i.putExtra(com.romeo.mylibrary.Constants.PARCEL_IDENTIFIER, mOrderData);
                    //write logic for with and without opc cases
                    if(!etOpc.isEnabled()) {
                        if(mOpcDetails==null) {
                            initiatePaymentProcess(i, mInvoiceId);
                        }else {
                            showConfirmationDialog(i, mInvoiceId);
                        }
                    }else {
                        initiatePaymentProcess(i, mInvoiceId);
                    }
                }else {
                    Methods.showSnackBarNegative(ProductCheckoutActivity.this, "Error in processing Amount");
                }
            }
        });

        btnOpcApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateDraftInvoice(etOpc.getText().toString().trim());
            }
        });

        PurchaseDetail purchaseDetail = new PurchaseDetail();
        String clientId;
        if (!Util.isNullOrEmpty(mSessionManager.getSourceClientId())) {
            clientId = mSessionManager.getSourceClientId();
        } else {
            clientId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
        }
        double totalTax = 0;
        if(StoreDataActivity.product.Taxes != null) {
            for (TaxDetail taxData : StoreDataActivity.product.Taxes/*taxes*/) {
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
        purchaseDetail.setTaxDetails(StoreDataActivity.product.Taxes/*taxes*/);
        List<PurchaseDetail> purchaseDetailList = new ArrayList<PurchaseDetail>();
        purchaseDetailList.add(purchaseDetail);

        ReceiveDraftInvoiceModel receiveDraftInvoiceModel = new ReceiveDraftInvoiceModel();
        receiveDraftInvoiceModel.setPurchaseDetails(purchaseDetailList);

        initializeVal(receiveDraftInvoiceModel, false);
        createDraftInvoice();
    }

    private void showConfirmationDialog(final Intent i, final String mInvoiceId) {
        ReceiveDraftInvoiceModel.KeyValuePair keyVal = mOpcDetails.get(0);
        if(keyVal.getValue()!=null){
            new AlertDialog.Builder(this)
                    .setMessage("Please note that your package will be activated on " + keyVal.getValue() +". Are you sure you want to proceed?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initiatePaymentProcess(i, mInvoiceId);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initiatePaymentProcess(i, mInvoiceId);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
            Methods.showSnackBarNegative(this, "Online Voucher can't be empty");
            return;
        }
        try {
            DataBase dataBase = new DataBase(ProductCheckoutActivity.this);
            Cursor cursor = dataBase.getLoginStatus();
            String fpUserProfileId;
            if (cursor.moveToFirst()){
                fpUserProfileId = cursor.getString(cursor.getColumnIndex(DataBase.colloginId));
            }else {
                showDialog("Alert!", "This is an added security feature to protect your package details. Kindly Log-out and Login again to pay for this package.");
                return;
            }
            UpdateDraftInvoiceModel updateDraftInvoiceModel;
            if(mInvoiceId!=null && fpUserProfileId!=null) {
                updateDraftInvoiceModel = new UpdateDraftInvoiceModel(fpUserProfileId, OPCCode, mInvoiceId);
            }else {
                Methods.showSnackBarNegative(this, "Unable to create Draft Invoice");
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("clientId", Constants.clientId);
            if(materialProgress!=null){
                materialProgress.show();
            }
            if(updateDraftInvoiceModel==null){
                Methods.showSnackBarNegative(this, "Unable to apply Coupon");
                return;
            }
            StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
            storeInterface.updateDraftInvoice(params, updateDraftInvoiceModel, new Callback<ReceivedDraftInvoice>() {
                @Override
                public void success(ReceivedDraftInvoice receiveDraftInvoice, Response response) {
                    if(receiveDraftInvoice!=null){
                        if(materialProgress!=null){
                            materialProgress.dismiss();
                        }
                        if(receiveDraftInvoice.getError().getErrorList()==null || receiveDraftInvoice.getStatusCode()==200) {
                            if(receiveDraftInvoice.getResult().getPurchaseDetails().get(0).getPackageId().equals(StoreDataActivity.product._id)) {
                                etOpc.setEnabled(false);
                                btnOpcApply.setEnabled(false);
                                mInvoiceId = receiveDraftInvoice.getResult().getInvoiceId();
                                mOpcDetails = receiveDraftInvoice.getResult().getOpcDetails();
                                initializeVal(receiveDraftInvoice.getResult(), true);
                                Methods.showSnackBarPositive(ProductCheckoutActivity.this, "Online voucher Applied Successfully");
                            }else {
                                Methods.showSnackBarNegative(ProductCheckoutActivity.this, "The entered Online voucher is not valid for this product.");
                            }
                        }else {
                            Methods.showSnackBarNegative(ProductCheckoutActivity.this, receiveDraftInvoice.getError().getErrorList().get(0).Key);
                            //layout.setError(;
                        }
                    }else {
                        if(materialProgress!=null){
                            materialProgress.dismiss();
                        }
                        Methods.showSnackBarNegative(ProductCheckoutActivity.this, getResources().getString(R.string.error_invoice));
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    if(materialProgress!=null){
                        materialProgress.dismiss();
                    }
                    Methods.showSnackBarNegative(ProductCheckoutActivity.this, getResources().getString(R.string.error_invoice));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            if(materialProgress!=null){
                materialProgress.dismiss();
            }
            Toast.makeText(this, "Error while generating Invoice", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==DIRECT_REQUEST_CODE || requestCode==OPC_REQUEST_CODE && resultCode==RESULT_OK) {
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
        }
    }

    private void initiatePaymentProcess(final Intent i, final String invoiceId) {
        if(mInvoiceId==null){
            Toast.makeText(this, "Invalid Invoice", Toast.LENGTH_SHORT).show();
            return;
        }
        StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("clientId", Constants.clientId);
        params.put("invoiceId", invoiceId);

        SupportedPaymentMethods method = null;
        if(StoreDataActivity.product!=null &&
                StoreDataActivity.product.SupportedPaymentMethods!=null
                && StoreDataActivity.product.SupportedPaymentMethods.size()>0){
            for (SupportedPaymentMethods paymentMethod : StoreDataActivity.product.SupportedPaymentMethods){
                if(paymentMethod.Type==1){
                    method = paymentMethod;
                }
            }
            if (materialProgress!=null){
                materialProgress.show();
            }
            storeInterface.initiatePaymentProcess(params, method, new Callback<PaymentTokenResult>() {
                @Override
                public void success(PaymentTokenResult paymentTokenResult, Response response) {
                    if(paymentTokenResult!=null && paymentTokenResult.getResult()!=null) {
                        i.putExtra(com.romeo.mylibrary.Constants.PAYMENT_REQUEST_IDENTIFIER, paymentTokenResult.getResult().getPaymentRequestId());
                        i.putExtra(com.romeo.mylibrary.Constants.ACCESS_TOKEN_IDENTIFIER, paymentTokenResult.getResult().getAccessToken());
                        i.putExtra(com.romeo.mylibrary.Constants.WEB_HOOK_IDENTIFIER, "https://api.withfloats.com/Payment/v1/floatingpoint/instaMojoWebHook?clientId="+Constants.clientId);//change this later
                        if (materialProgress!=null){
                            materialProgress.dismiss();
                        }
                        startActivityForResult(i, OPC_REQUEST_CODE);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }else {
                        if (materialProgress!=null){
                            materialProgress.dismiss();
                        }
                        Methods.showSnackBarNegative(ProductCheckoutActivity.this, "Error while processing payment");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (materialProgress!=null){
                        materialProgress.dismiss();
                    }
                    Methods.showSnackBarNegative(ProductCheckoutActivity.this, "Error while processing payment");
                }
            });
        }

    }

    private void createDraftInvoice() {
        try {
            SendDraftInvoiceModel sendDraftInvoiceModel = new SendDraftInvoiceModel();
            PurchaseDetail purchaseDetail = new PurchaseDetail();
            String clientId;

            if (!Util.isNullOrEmpty(mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID))) {
                clientId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
            } else if(!Util.isNullOrEmpty(mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID))) {
                clientId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID);
            }else if(!Util.isNullOrEmpty(mSessionManager.getSourceClientId())){
                clientId = mSessionManager.getSourceClientId();
            }else {
                Methods.showSnackBarNegative(this, "Can't Proceed for Payment");
                return;
            }

            double totalTax = 0;
            if(StoreDataActivity.product.Taxes != null) {
                for (TaxDetail taxData : StoreDataActivity.product.Taxes) {
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
            purchaseDetail.setTaxDetails(StoreDataActivity.product.Taxes);

            List<PurchaseDetail> purchaseDetailList = new ArrayList<PurchaseDetail>();
            purchaseDetailList.add(purchaseDetail);

            sendDraftInvoiceModel.setPurchaseDetails(purchaseDetailList);
            DataBase dataBase = new DataBase(ProductCheckoutActivity.this);
            Cursor cursor = dataBase.getLoginStatus();
            if (cursor.moveToFirst() && !cursor.getString(cursor.getColumnIndex(DataBase.colloginId)).equals("0")){
                sendDraftInvoiceModel.setFpUserProfileId(cursor.getString(cursor.getColumnIndex(DataBase.colloginId)));
                sendDraftInvoiceModel.setOpc(null);
            }else {
                showDialog("Alert!", "This is an added security feature to protect your package details. Kindly Log-out and Login again to pay for this package.");
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("clientId", Constants.clientId);
            if(materialProgress!=null){
                materialProgress.show();
            }
            StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
            storeInterface.createDraftInvoice(params, sendDraftInvoiceModel, new Callback<ReceivedDraftInvoice>() {
                @Override
                public void success(ReceivedDraftInvoice receiveDraftInvoice, Response response) {
                    if(receiveDraftInvoice!=null && receiveDraftInvoice.getStatusCode()==200){
                        if(materialProgress!=null){
                            materialProgress.dismiss();
                        }
                        initializeVal(receiveDraftInvoice.getResult(), false);
                        mInvoiceId = receiveDraftInvoice.getResult().getInvoiceId();
                    }else {
                        if(materialProgress!=null){
                            materialProgress.dismiss();
                        }
                        Methods.showSnackBarNegative(ProductCheckoutActivity.this, getResources().getString(R.string.error_invoice));
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    if(materialProgress!=null){
                        materialProgress.dismiss();
                    }
                    Methods.showSnackBarNegative(ProductCheckoutActivity.this, getResources().getString(R.string.error_invoice));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            if(materialProgress!=null){
                materialProgress.dismiss();
            }
            Toast.makeText(ProductCheckoutActivity.this, "Error while generating Invoice", Toast.LENGTH_SHORT).show();
        }
    }

    // showing list of products with discount, if opc added
    private void initializeVal(final ReceiveDraftInvoiceModel invoiceData, boolean showDiscount) {
        if(invoiceData==null){
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
        for(PurchaseDetail data : invoiceData.getPurchaseDetails()){
            if(data.getDiscount()==null) {
                netAmount += data.getBasePrice();
            }else {
                netAmount += (data.getBasePrice()-(data.getBasePrice()*data.getDiscount().value/100.0));
            }
        }
        netAmount = Math.round((netAmount * 100) / 100.0);
        tvNetTotal.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " +
                NumberFormat.getIntegerInstance(Locale.US).format(netAmount)+ " /-");
        float taxVal = 0;
        StringBuilder taxNames= new StringBuilder();

        for (TaxDetail taxData : invoiceData.getPurchaseDetails().get(0).getTaxDetails()) {
            taxVal += taxData.getValue();
            taxNames.append(taxData.getKey() + "-" + taxData.getValue() + "%,\n ");
        }

        double taxAmount = 0;
        if(invoiceData.getPurchaseDetails().get(0).getTaxDetails().get(0).getAmountType()==0) {
            taxAmount = (netAmount * taxVal) / 100.0;
        }else {
            taxAmount =(int) taxVal;
        }
        taxAmount = Math.round((taxAmount * 100) / 100.0);
        tvTaxes.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " +
                NumberFormat.getIntegerInstance(Locale.US).format(taxAmount) + " /-\n" + "( " + taxNames.substring(0, taxNames.length() - 3) + " )");
        if(showDiscount) {
            trTdsAmount.setVisibility(View.VISIBLE);
            tvTdsAmount.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " + invoiceData.getTdsAmount());
        }

        tvAmountToBePaid.setText(StoreDataActivity.product.CurrencyCode + " " +
                NumberFormat.getIntegerInstance(Locale.US).format(Math.round((netAmount + taxAmount - invoiceData.getTdsAmount()) * 100) / 100) + " /-");
        String packages="";
        for(int i=0; i<invoiceData.getPurchaseDetails().size(); i++){
            packages+=invoiceData.getPurchaseDetails().get(i).getPackageName() + " and ";
        }
        mNewPackage = packages;
        mFinalAmount = String.valueOf(Math.round((netAmount + taxAmount - invoiceData.getTdsAmount()) * 100) / 100);


        rvItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ItemsRecyclerViewAdapter adapter = new ItemsRecyclerViewAdapter(invoiceData.getPurchaseDetails(), StoreDataActivity.product.CurrencyCode, showDiscount);
        rvItems.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void showDialog(String title, String msg){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
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
