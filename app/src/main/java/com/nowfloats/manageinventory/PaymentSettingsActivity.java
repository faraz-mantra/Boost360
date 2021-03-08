package com.nowfloats.manageinventory;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manageinventory.fragments.APEligibilityCheckerFragment;
import com.nowfloats.manageinventory.fragments.PaymentInfoEntryFragment;
import com.nowfloats.manageinventory.models.MerchantProfileModel;
import com.nowfloats.manageinventory.models.WaUpdateDataModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
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
        View.OnTouchListener, View.OnClickListener {

    RadioGroup rgPaymentMethod, rgDeliveryType;

    TextView tvName, tvBankName, tvAccNum, tvIfsc, tvAccountType, tvPan, tvGstn, tvAssuredPurchase, tvPaymentLink;

    ProgressDialog progressDialog;

    CardView cvPaymentDetails;

    Toolbar toolbar;

    RadioButton rbNfDeliv, rbSelfDeliv;

    private MerchantProfileModel mProfile;


    private UserSessionManager mSession;

    private String mApplicableTxnCharge = "9%";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_settings);
        MixPanelController.track(EventKeysWL.SIDE_PANEL_PAYMENT_SETTING, null);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Payment Settings");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rgPaymentMethod = (RadioGroup) findViewById(R.id.rg_payment_method);
        rgDeliveryType = (RadioGroup) findViewById(R.id.rg_delivery_mode);

        tvName = (TextView) findViewById(R.id.tv_person_name);
        tvBankName = (TextView) findViewById(R.id.tv_bank_name);
        tvAccNum = (TextView) findViewById(R.id.tv_acc_num);
        tvIfsc = (TextView) findViewById(R.id.tv_ifsc);
        tvAccountType = (TextView) findViewById(R.id.tv_acc_type);
        tvPan = (TextView) findViewById(R.id.tv_pan_card);
        tvGstn = (TextView) findViewById(R.id.tv_gstin);
        tvAssuredPurchase = (TextView) findViewById(R.id.tv_ap_learn_more);
        tvPaymentLink = (TextView) findViewById(R.id.tv_deliv_learn_more);
        tvAssuredPurchase.setOnClickListener(this);
        tvPaymentLink.setOnClickListener(this);

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
        progressDialog.setMessage(getString(R.string.please_wait_));
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
                        Toast.makeText(PaymentSettingsActivity.this, getString(R.string.something_wen_wrong), Toast.LENGTH_SHORT).show();
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
                                mApplicableTxnCharge = mProfile.getApplicableTxnCharge()+"%";
                            }else {
                                throw new NullPointerException(getString(R.string.order_count_is_null));
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

    public void showLearnMoreDialog(final View v, final boolean showAssureDialog){

        int arrayId;
        String title,content = null;
        switch (v.getId()){

            case R.id.tv_ap_learn_more:
                arrayId = R.array.assured_purchase_points;
                title =getString(R.string.assured_purchase);
                content = getString(R.string.assured_purchase_is_service_guarantee_by_nowfloats);
                break;
            case R.id.tv_deliv_learn_more:
                arrayId = R.array.delivery_points;
                title = getString(R.string.use_own_payment_link);
                //msg = getString(R.string.deliv_learn_more);
                break;
            default:
                return;
        }

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .canceledOnTouchOutside(false)
                .itemsGravity(GravityEnum.CENTER)
                .adapter(new MyArrayAdapter(PaymentSettingsActivity.this,arrayId,mApplicableTxnCharge),
                        new LinearLayoutManager(PaymentSettingsActivity.this,LinearLayoutManager.VERTICAL,false))
                .dividerColorRes(R.color.gray);

        if(!TextUtils.isEmpty(content)){
            builder.content(content);
        }
        if(!TextUtils.isEmpty(title)){
            builder.title(title);
        }

        if(showAssureDialog){

            builder.negativeText(getString(R.string.cancel));
            builder.positiveText(R.string.i_agree);
            builder.onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    rgPaymentMethod.setOnCheckedChangeListener(null);
                    View view = rgPaymentMethod.getChildAt(1);
                    if(view instanceof RadioButton){
                        ((RadioButton) view).setChecked(true);
                    }
                    rgPaymentMethod.setOnCheckedChangeListener(PaymentSettingsActivity.this);
                    dialog.dismiss();
                }
            })
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    switch (v.getId()) {
                        case R.id.tv_ap_learn_more:
                            checkAssuredPurchase();
                            break;
                    }
                    dialog.dismiss();
                }
            });
        }else {
            builder.positiveText("Ok");
            builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                }
            });
        }

        builder.show();
    }

    public class MyArrayAdapter extends RecyclerView.Adapter<MyArrayAdapter.MyHolder>{
        String[] bulletPoints = null;
        Context mContext;
        String textCharge;
        public MyArrayAdapter(Context context,int id,String textCharge){
            mContext = context;
            this.textCharge = textCharge;
            bulletPoints = getResources().getStringArray(id);
        }
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_bullet_point_item, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            if(holder != null){
                holder.text.setText(String.format(bulletPoints[position],textCharge));
            }
        }

        @Override
        public int getItemCount() {
            return bulletPoints.length;
        }

        class MyHolder extends RecyclerView.ViewHolder{

            public TextView text;
            MyHolder(View itemView) {
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.text1);
            }
        }
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
                showLearnMoreDialog(tvAssuredPurchase,true);
                break;
            case R.id.rb_use_payment_link:
                showDialog();
                break;
        }
    }


    private void showDialog(){
        new AlertDialog.Builder(this)
                .setMessage(R.string.by_enabling_your_own_payment_link)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateAssuredPurchase(false, false);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
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
        progressDialog.setMessage(getString(R.string.please_wait_));
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
                        Methods.showSnackBarPositive(PaymentSettingsActivity.this, getString(R.string.something_wen_wrong));

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
                                Methods.showSnackBarPositive(PaymentSettingsActivity.this, getString(R.string.congrats_assured_purchase_enabled));
                            }else {
                                Methods.showSnackBarPositive(PaymentSettingsActivity.this, getString(R.string.own_payment_link_enabled));
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
            Toast.makeText(this,getString( R.string.you_cant_change_delivery_while_assured), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.tv_ap_learn_more:
                showLearnMoreDialog(v,false);
                break;
            case R.id.tv_deliv_learn_more:
                showLearnMoreDialog(v,false);
                break;
        }
    }
}
