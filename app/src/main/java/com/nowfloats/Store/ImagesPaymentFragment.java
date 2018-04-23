package com.nowfloats.Store;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Model.ChequePaymentModel;
import com.nowfloats.Store.Model.InitiateModel;
import com.nowfloats.Store.Model.MarkAsPaidModel;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Store.Model.ProductPaymentModel;
import com.nowfloats.Store.Model.TaxDetail;
import com.nowfloats.Store.Service.OnPaymentOptionClick;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 17-04-2018.
 */

public class ImagesPaymentFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, OnImagePicked, View.OnTouchListener {
    public Context mContext;
    public TextInputEditText bankNameEt, chequeNumberEt, transactionIdEt, accountNumberEt, transactionAmountEt,
            ifscCodeEt, paymentDateEt, gstNumberEt;
    public RadioGroup gstRadioGroup;
    public TextView addAltTv, addMainTv;
    CardView altImageCv, mainImageCv;
    private UserSessionManager manager;
    public ImageView altChequeImgView, mainChequeImgView;
    public String mainImage, altImage;
    private int totalPrice, taxAmount;
    private String currencyCode;
    private List<ProductPaymentModel> mProductPaymentModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arg = getArguments();
        if (arg != null){
            initializeProductList(new Gson().<List<PackageDetails>>fromJson(getArguments().getString("packageList"),new TypeToken<List<PackageDetails>>(){}.getType()));
        }
        manager = new UserSessionManager(mContext,getActivity());
    }

    private void initializeProductList(List<PackageDetails> packageList) {
        if (packageList ==  null) return;
        mProductPaymentModel = new ArrayList<>();
        for (PackageDetails details: packageList){
            ProductPaymentModel model = new ProductPaymentModel();
            model.setName(details.getName());
            model.setPrice(details.getPrice().intValue());
            model.setDescription(details.getDesc());
            model.setProductId(details.getId());
            model.setQuantity(1);
            model.setType(null);
            currencyCode = details.getCurrencyCode();
            totalPrice+=details.getPrice();
            double totalTax = 0;
            if(details.getTaxes() != null) {
                for (TaxDetail taxData : details.getTaxes()) {
                    totalTax += taxData.getValue();
                }
            }
            model.setTaxValue(totalTax);
            taxAmount += (details.getPrice()*totalTax)/100;
            mProductPaymentModel.add(model);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded() || getActivity() == null) return;

        bankNameEt = view.findViewById(R.id.editText_bank_name);
        ifscCodeEt = view.findViewById(R.id.editText_ifsc_code);
        accountNumberEt = view.findViewById(R.id.editText_account_number);
        transactionAmountEt = view.findViewById(R.id.editText_transfer_amount);
        transactionIdEt = view.findViewById(R.id.editText_transaction_id);
        chequeNumberEt = view.findViewById(R.id.editText_cheque_number);
        paymentDateEt = view.findViewById(R.id.editText_payment_date);
        gstNumberEt = view.findViewById(R.id.editText_gst_number);
        final TextInputLayout gstLayout = view.findViewById(R.id.inputLayout_gst);
        gstRadioGroup = view.findViewById(R.id.rg_is_gst);
        addMainTv = view.findViewById(R.id.textView_add_main);
        addMainTv.setOnClickListener(this);
        addAltTv = view.findViewById(R.id.textView_add_alt);
        addAltTv.setOnClickListener(this);
        mainChequeImgView = view.findViewById(R.id.imageView_primary);
        mainImageCv = view.findViewById(R.id.cardView_main);
        altImageCv = view.findViewById(R.id.cardView_alternative);
        altChequeImgView = view.findViewById(R.id.imageView_alternate);
        view.findViewById(R.id.textView_pay).setOnClickListener(this);
        view.findViewById(R.id.imageView_alt_delete).setOnClickListener(this);
        view.findViewById(R.id.imageView_main_delete).setOnClickListener(this);
        gstRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.radioButton_yes:
                        gstLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        gstLayout.setVisibility(View.GONE);
                        break;
                }
            }
        });
        bankNameEt.setFocusable(false);
        bankNameEt.setFocusableInTouchMode(false);
        bankNameEt.setOnTouchListener(this);
        paymentDateEt.setFocusable(false);
        paymentDateEt.setFocusableInTouchMode(false);
        paymentDateEt.setOnTouchListener(this);
    }

    public boolean validateAllFields(){
       if (gstRadioGroup.getCheckedRadioButtonId() == R.id.radioButton_yes) {
           if (gstNumberEt.getText().toString().trim().length() == 0) {
               showMessage("Enter GST number");
               return false;
           } else if (!validateGstNumber(gstNumberEt.getText().toString())) {
               showMessage("Wrong GST number");
               return false;
           }
       }
       return true;
    }

    private boolean validateGstNumber(String gstNumber){
        String regex = "[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}";
        return gstNumber.matches(regex);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.imageView_main_delete:
                mainImage = null;
                addMainTv.setVisibility(View.VISIBLE);
                mainImageCv.setVisibility(View.GONE);
                break;
            case R.id.imageView_alt_delete:
                altImage = null;
                altImageCv.setVisibility(View.GONE);
                addAltTv.setVisibility(View.VISIBLE);
                break;
            case R.id.textView_pay:
                if (validateAllFields()) {
                    //showPaidConfirmation();
                    initiateProcess(new InitiateModel());
                }
                break;
        }
    }

    private void getBankNames() {
        new MaterialDialog.Builder(mContext)
                .title("Select a Bank")
                .titleColorRes(R.color.light_gray)
                .items(getResources().getStringArray(R.array.bank_names))
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        bankNameEt.setText(text);
                        return true;
                    }
                })
                .show();
    }

    private void showPaidConfirmation(){
        new MaterialDialog.Builder(mContext)
                .title("Successfully Updated")
                .titleColorRes(R.color.gray)
                .content("Payment Details successfully uploaded.")
                .positiveColorRes(R.color.primary_color)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ((OnPaymentOptionClick)mContext).setResult(null);
                        dialog.dismiss();
                    }
                })
                .show();
    }
    @Override
    public void onShowPicked(Bundle bundle) {
        String path = bundle.getString("path");
        int code = bundle.getInt("requestCode");
        Bitmap bitmap = Methods.decodeSampledBitmap(path, 300,300);
        String imageString = Methods.convertBitmapToString(bitmap);
        switch (code){
            case 10:
                mainImageCv.setVisibility(View.VISIBLE);
                addAltTv.setVisibility(View.VISIBLE);
                addMainTv.setVisibility(View.GONE);
                Glide.with(this).load(bitmap).into(mainChequeImgView);
                mainImage = imageString;
                break;
            case 11:
                altImageCv.setVisibility(View.VISIBLE);
                addAltTv.setVisibility(View.GONE);
                Glide.with(this).load(bitmap).into(altChequeImgView);
                altImage = imageString;
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,dayOfMonth);
        String s = formatter.format(calendar.getTime());
        paymentDateEt.setText(s);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP){
            switch (v.getId()){
                case R.id.editText_bank_name:
                    getBankNames();
                    return true;
                case R.id.editText_payment_date:
                    Calendar now = Calendar.getInstance();
                    android.app.DatePickerDialog dialog  = new android.app.DatePickerDialog(
                            mContext,
                            this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                    return true;
            }
        }
        return false;
    }


    public void initiateProcess(final InitiateModel initiateModel){
        ((OnPaymentOptionClick)mContext).showProcess(getString(R.string.please_wait));
        String accId = manager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
        String appId = manager.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID);
        initiateModel.setClientId(TextUtils.isEmpty(accId) ? appId : accId);
        initiateModel.setEmail(manager.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
        initiateModel.setCustomerName(manager.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        initiateModel.setFpId(manager.getFPID());
        initiateModel.setFpTag(manager.getFpTag());
        initiateModel.setPaymentTransactionChannel(1);
        initiateModel.setTdsPercentage(0.0);
        initiateModel.setRecurringMonths(0);
        initiateModel.setProducts(mProductPaymentModel);
        initiateModel.setPhoneNumber(manager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));
        initiateModel.setPhoneNumberExtension(manager.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE));
        StoreInterface api = Constants.movingFloatsDevAdapter.create(StoreInterface.class);
        api.initiate(Constants.clientId, initiateModel, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if (!TextUtils.isEmpty(s)){
                    ChequePaymentModel model = new ChequePaymentModel();
                    model.setTransactionId(s);
                    model.setProducts(mProductPaymentModel);
                    model.setClientId(initiateModel.getClientId());
                    updatePaymentDetails(model);
                }else{
                    ((OnPaymentOptionClick)mContext).hideProcess();
                    showMessage("Request Failed");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ((OnPaymentOptionClick)mContext).hideProcess();
                showMessage(getString(R.string.something_went_wrong_try_again));
            }
        });
    }

    public void updatePaymentDetails(final ChequePaymentModel chequeModel){
        chequeModel.setBankName(bankNameEt.getText().toString());
        chequeModel.setIfscCode(ifscCodeEt.getText().toString());
        chequeModel.setFpTag(manager.getFpTag());
        chequeModel.setCurrencyCode(currencyCode);
        chequeModel.setId(null);
        chequeModel.setMicrCode(null);
        chequeModel.setName(manager.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        chequeModel.setPaymentStatus("initiated");
        chequeModel.setPartnerType(null);
        chequeModel.setPaymentFor(null);
        //chequeModel.setPaymentDate(String.format("\\/Date(%s)\\/",String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())));
        chequeModel.setPaymentTransactionChannel(1);
        chequeModel.setTotalPrice(totalPrice+taxAmount);
        chequeModel.setTdsPercentage(0);
        chequeModel.setTaxAmount(taxAmount);
        chequeModel.setProducts(mProductPaymentModel);
        chequeModel.setReferenceId(null);
        chequeModel.setRejectionReason(null);
        chequeModel.setChequeNumber(chequeNumberEt.getText().toString().trim());
        chequeModel.setRtgsId(transactionIdEt.getText().toString().trim());
        chequeModel.setGSTNumber(gstRadioGroup.getCheckedRadioButtonId() == R.id.radioButton_yes ? gstNumberEt.getText().toString():"");
        chequeModel.setImage(mainImage);
        chequeModel.setAlternateImage(altImage);
        StoreInterface api = Constants.movingFloatsDevAdapter.create(StoreInterface.class);
        api.updateChequeLog(chequeModel, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if ("updated".equalsIgnoreCase(s) || "inserted".equalsIgnoreCase(s)){
                    MarkAsPaidModel model = new MarkAsPaidModel();
                    model.setPaymentTransactionId(chequeModel.getTransactionId());
                    model.setClientId(chequeModel.getClientId());
                    markAsPaid(model);
                }else{
                    ((OnPaymentOptionClick)mContext).hideProcess();
                    showMessage("Request Cancelled");
                    //cancel process api
                }
            }

            @Override
            public void failure(RetrofitError error) {
                // cancel process
                ((OnPaymentOptionClick)mContext).hideProcess();
                showMessage( getString(R.string.something_went_wrong_try_again));
            }
        });
    }

    public void markAsPaid(MarkAsPaidModel model){
        model.setFpTag(manager.getFpTag());
        model.setFpId(manager.getFPID());
        model.setCurrencyCode(currencyCode);
        model.setBaseAmount(totalPrice);
        model.setExpectedAmount(totalPrice+taxAmount);
        model.setIsCustomBundle(false);
        model.setIsPartOfComboPlan(false);
        model.setTaxAmount(taxAmount);
        MarkAsPaidModel.CustomerSalesOrderRequest salesOrderRequest = new MarkAsPaidModel.CustomerSalesOrderRequest();
        salesOrderRequest.setCustomerEmailId(manager.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
        salesOrderRequest.setInvoiceStatus(1);
        salesOrderRequest.setPaymentMode(1);
        salesOrderRequest.setPurchasedUnits(1);
        salesOrderRequest.setSendEmail(true);
        salesOrderRequest.setDiscountPercentageValue(0);
        salesOrderRequest.setPaymentTransactionStatus(3);
        model.setCustomerSalesOrderRequest(salesOrderRequest);
        List<MarkAsPaidModel.Package> packages = new ArrayList<>();
        for (ProductPaymentModel product : mProductPaymentModel){
            MarkAsPaidModel.Package package1 = new MarkAsPaidModel.Package();
            package1.setPackageId(product.getProductId());
            package1.setQuantity(product.getQuantity());
            packages.add(package1);
        }
        model.setPackages(packages);
        model.setIsPartOfComboPlan(false);
        model.setComboPackageId(null);
        StoreInterface api = Constants.movingFloatsDevAdapter.create(StoreInterface.class);
        api.markAsPaid(model, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                ((OnPaymentOptionClick)mContext).hideProcess();
                if (!TextUtils.isEmpty(s)){
                    // invoice created
                    showPaidConfirmation();
                }else{
                    ((OnPaymentOptionClick)mContext).hideProcess();

                }
            }

            @Override
            public void failure(RetrofitError error) {
                ((OnPaymentOptionClick)mContext).hideProcess();
                showMessage( getString(R.string.something_went_wrong_try_again));
            }
        });
    }

    public void showMessage(String msg){
        if (getActivity() != null) {
            Methods.showSnackBarNegative(getActivity(), msg);
        }else{
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
