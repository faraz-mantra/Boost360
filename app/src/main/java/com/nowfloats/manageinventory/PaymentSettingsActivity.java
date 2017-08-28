package com.nowfloats.manageinventory;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.edmodo.cropper.cropwindow.handle.Handle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manageinventory.fragments.APEligibilityCheckerFragment;
import com.nowfloats.manageinventory.fragments.PaymentInfoEntryFragment;
import com.nowfloats.manageinventory.models.CountModel;
import com.nowfloats.manageinventory.models.MerchantProfileModel;
import com.nowfloats.manageinventory.models.TransactionModel;
import com.nowfloats.manageinventory.models.WaUpdateDataModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PaymentSettingsActivity extends AppCompatActivity implements
        RadioGroup.OnCheckedChangeListener,
        APEligibilityCheckerFragment.EligibilityCheckCallBack,
        PaymentInfoEntryFragment.ProfileUpdateCallBack,
        View.OnTouchListener{

    RadioGroup rgPaymentMethod, rgDeliveryType;

    TextView tvName, tvBankName, tvAccNum, tvIfsc, tvAccountType, tvPan, tvGstn;

    ProgressDialog progressDialog;

    CardView cvPaymentDetails;

    Toolbar toolbar;

    RadioButton rbNfDeliv, rbSelfDeliv;

    private MerchantProfileModel mProfile;


    private UserSessionManager mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Payment Settings");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rgPaymentMethod = (RadioGroup) findViewById(R.id.rg_payment_method);
        rgDeliveryType = (RadioGroup) findViewById(R.id.rg_delivery_mode);

        tvName = (TextView) findViewById(R.id.tv_name);
        tvBankName = (TextView) findViewById(R.id.tv_bank_name);
        tvAccNum = (TextView) findViewById(R.id.tv_acc_num);
        tvIfsc = (TextView) findViewById(R.id.tv_ifsc);
        tvAccountType = (TextView) findViewById(R.id.tv_acc_type);
        tvPan = (TextView) findViewById(R.id.tv_pan_card);
        tvGstn = (TextView) findViewById(R.id.tv_gstin);

        cvPaymentDetails = (CardView) findViewById(R.id.cv_payment_details);

        mSession = new UserSessionManager(this, this);
        progressDialog = new ProgressDialog(this);
        getPaymentSettings();

        rbNfDeliv = (RadioButton) findViewById(R.id.rb_nowfloats_deliv);
        rbSelfDeliv = (RadioButton) findViewById(R.id.rb_seld_deliv);

        rbSelfDeliv.setOnTouchListener(this);
        rbNfDeliv.setOnTouchListener(this);

        rgPaymentMethod.setOnCheckedChangeListener(this);

        findViewById(R.id.iv_edit_payment_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProfile!=null){
                    DialogFragment paymentEntryFragment = new PaymentInfoEntryFragment();
                    Bundle bundle  = new Bundle();
                    bundle.putParcelable("profile", mProfile);
                    paymentEntryFragment.setArguments(bundle);
                    paymentEntryFragment.show(getFragmentManager(), "PaymentInfoEntryFragment");
                }
            }
        });
    }

    private void checkAssuredPurchase(){
        DialogFragment fragment = new APEligibilityCheckerFragment();
        fragment.show(getFragmentManager(), "CheckEligibility");

    }


    private void getPaymentSettings(){
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format(Constants.WA_BASE_URL+"merchant_profile3/get-data?query={merchant_id:'%s'}&limit=1", mSession.getFPID()))
                .header("Authorization", Constants.WA_KEY)
                .build();
        final Gson gson = new Gson();
        client.newCall(request).enqueue(new Callback() {

            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PaymentSettingsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        try {

                            WebActionModel<MerchantProfileModel> profile = gson.fromJson(res,
                                    new TypeToken<WebActionModel<MerchantProfileModel>>() {
                                    }.getType());

                            if (profile != null && profile.getData().size()>0) {
                                mProfile = profile.getData().get(0);
                                processProfileData(mProfile);
                            }else {
                                throw new NullPointerException("Orders Count is Null");
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void processProfileData(MerchantProfileModel profile){
        rgPaymentMethod.setOnCheckedChangeListener(null);
        if(profile.getPaymentType() == 0){
            View v = rgPaymentMethod.getChildAt(0);
            if(v!=null && v instanceof RadioButton){
                ((RadioButton) v).setChecked(true);
            }
        }else {
            View v = rgPaymentMethod.getChildAt(1);
            if(v!=null && v instanceof RadioButton){
                ((RadioButton) v).setChecked(true);
            }
        }

        if(profile.getDeliveryType() == 0){
            View v = rgDeliveryType.getChildAt(0);
            if(v!=null && v instanceof RadioButton){
                ((RadioButton) v).setChecked(true);
            }
        }else {
            View v = rgDeliveryType.getChildAt(1);
            if(v!=null && v instanceof RadioButton){
                ((RadioButton) v).setChecked(true);
            }
        }
        rgPaymentMethod.setOnCheckedChangeListener(this);

        tvName.setText(profile.getMerchantName());
        tvBankName.setText(profile.getBankName());
        tvAccNum.setText(profile.getBankAccNum());
        tvAccountType.setText(profile.getBankAccountType());
        tvIfsc.setText(profile.getIfscCode());
        tvPan.setText(profile.getPanCard());
        tvGstn.setText(profile.getGstn());
        cvPaymentDetails.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (rgPaymentMethod.getCheckedRadioButtonId()){
            case R.id.rb_assured_purchase:
                checkAssuredPurchase();
                break;
            case R.id.rb_use_payment_link:
                showDialog();
                break;
        }
    }


    private void showDialog(){
        new AlertDialog.Builder(this)
                .setMessage("By enabling your own payment link, you will no longer be able to get Assured Purchase benefits.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateAssuredPurchase(false, false);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rgPaymentMethod.setOnCheckedChangeListener(null);
                        View v = rgPaymentMethod.getChildAt(0);
                        if(v!=null && v instanceof RadioButton){
                            ((RadioButton) v).setChecked(true);
                        }
                        rgPaymentMethod.setOnCheckedChangeListener(PaymentSettingsActivity.this);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEligibiltyChecked(boolean isEligible) {
        rgPaymentMethod.setOnCheckedChangeListener(null);
        if(isEligible){

            View v = rgPaymentMethod.getChildAt(0);
            if(v!=null && v instanceof RadioButton){
                ((RadioButton) v).setChecked(true);
            }

            v = rgDeliveryType.getChildAt(0);
            if(v!=null && v instanceof RadioButton){
                ((RadioButton) v).setChecked(true);
            }
            updateAssuredPurchase(true, true);
        }else {
            View v = rgPaymentMethod.getChildAt(1);
            if(v!=null && v instanceof RadioButton){
                ((RadioButton) v).setChecked(true);
            }

            v = rgDeliveryType.getChildAt(1);
            if(v!=null && v instanceof RadioButton){
                ((RadioButton) v).setChecked(true);
            }
        }

        rgPaymentMethod.setOnCheckedChangeListener(this);
    }

    private void updateAssuredPurchase(final boolean isNfPayment, boolean isNfDeliv){
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        int paymentType = 1, delivType = 1;
        if(isNfPayment){
            paymentType = 0;
        }

        if(isNfDeliv){
            delivType = 0;
        }

        WaUpdateDataModel update = new WaUpdateDataModel();
        update.setMulti(true);
        update.setQuery(String.format("{merchant_id:'%s'}", mSession.getFPID()));
        update.setUpdateValue(String.format("{$set : {delivery_type:%d, payment_type:%d}}", delivType, paymentType));

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(update));
        Request request = new Request.Builder()
                                .url(Constants.WA_BASE_URL + "merchant_profile3/update-data")
                                .header("Authorization", Constants.WA_KEY)
                                .post(body)
                                .build();
        client.newCall(request).enqueue(new Callback() {

            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Methods.showSnackBarPositive(PaymentSettingsActivity.this, "Something went wrong");

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if(res.contains("1")){
                            if(isNfPayment) {
                                Methods.showSnackBarPositive(PaymentSettingsActivity.this, "Congrats! Assured Purchase Enabled");
                            }else {
                                Methods.showSnackBarPositive(PaymentSettingsActivity.this, "Own payment link Enabled");
                                ((RadioButton)rgDeliveryType.getChildAt(1)).setChecked(true);
                            }
                        }
                    }
                });
            }
        });


    }

    @Override
    public void onProfileUpdated(MerchantProfileModel profile) {
        tvName.setText(profile.getMerchantName());
        tvBankName.setText(profile.getBankName());
        tvAccNum.setText(profile.getBankAccNum());
        tvAccountType.setText(profile.getBankAccountType());
        tvIfsc.setText(profile.getIfscCode());
        tvPan.setText(profile.getPanCard());
        tvGstn.setText(profile.getGstn());
        cvPaymentDetails.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if((v.getId() == R.id.rb_seld_deliv || v.getId() == R.id.rb_nowfloats_deliv) && event.getAction() == MotionEvent.ACTION_DOWN){
            Toast.makeText(this, "You can't change delivery while Assured Purchase is enabled", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
