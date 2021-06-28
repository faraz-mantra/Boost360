package com.nowfloats.manageinventory.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manageinventory.models.MerchantProfileModel;
import com.nowfloats.manageinventory.models.WAAddDataModel;
import com.nowfloats.manageinventory.models.WaUpdateDataModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PaymentInfoEntryFragment extends DialogFragment {

    MerchantProfileModel mProfile;

    EditText etName, etAccNum, etBankName, etIfsc, etPanCard, etGstn;

    RadioGroup rgAccType;

    ProgressDialog progressDialog;

    UserSessionManager mSession;

    private ProfileUpdateCallBack mProfileCallBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_TITLE, 0);

        if(getArguments()!=null) {
            mProfile = getArguments().getParcelable("profile");
        }
        mSession = new UserSessionManager(getActivity(), getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof  ProfileUpdateCallBack){
            mProfileCallBack = (ProfileUpdateCallBack) activity;
        }else {
            throw new RuntimeException(getString(R.string.must_implement_profile_update_callback));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_info_entry, container, false);

        progressDialog = new ProgressDialog(getActivity());

        etName = (EditText) view.findViewById(R.id.et_name);
        etAccNum = (EditText) view.findViewById(R.id.et_acc_num);
        etBankName = (EditText) view.findViewById(R.id.et_bank_name);
        etIfsc = (EditText) view.findViewById(R.id.et_ifsc);
        etPanCard = (EditText) view.findViewById(R.id.et_pan);
        etGstn = (EditText) view.findViewById(R.id.et_gstn);

        rgAccType = (RadioGroup) view.findViewById(R.id.rg_account_type);

        if(mProfile!=null){
            etName.setText(mProfile.getMerchantName());
            etAccNum.setText(mProfile.getBankAccNum());
            etBankName.setText(mProfile.getBankName());
            etIfsc.setText(mProfile.getIfscCode());
            etPanCard.setText(mProfile.getPanCard());
            etGstn.setText(mProfile.getGstn());

            if(mProfile.getBankAccountType().equalsIgnoreCase("Savings")){
                ((RadioButton)rgAccType.getChildAt(0)).setChecked(true);
            }else {
                ((RadioButton)rgAccType.getChildAt(1)).setChecked(true);
            }
        }

        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrUpdateProfileData();
            }
        });

        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void saveOrUpdateProfileData() {
        if(TextUtils.isEmpty(etName.getText().toString().trim())){
            etName.setError(getString(R.string.name_is_mandatory));
            return;
        }
        if(TextUtils.isEmpty(etAccNum.getText().toString().trim())){
            etName.setError(getString(R.string.account_number_is_mandatory));
            return;
        }
        if(TextUtils.isEmpty(etBankName.getText().toString().trim())){
            etName.setError(getString(R.string.bank_name_is_mandatory));
            return;
        }
        if(TextUtils.isEmpty(etIfsc.getText().toString().trim())){
            etName.setError(getString(R.string.ifcs_code_is_mandatory));
            return;
        }
        if(TextUtils.isEmpty(etPanCard.getText().toString().trim())){
            etName.setError(getString(R.string.pan_card_is_mandatory));
            return;
        }
        if(mProfile == null){
            saveMerchantProfile();
        }else {
            updateAssuredPurchase();
        }
    }

    private void saveMerchantProfile() {
        final MerchantProfileModel profile = new MerchantProfileModel();
        profile.setMerchantName(etName.getText().toString().trim());
        profile.setBankName(etBankName.getText().toString().trim());
        profile.setBankAccNum(etAccNum.getText().toString().trim());
        profile.setDeliveryType(1);
        profile.setPaymentType(1);
        profile.setEmail(mSession.getFPEmail());
        profile.setFpTag(mSession.getFpTag());
        profile.setGstn(etGstn.getText().toString().trim());
        profile.setMerchantId(mSession.getFPID());
        profile.setPhoneNumber(mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));
        profile.setPanCard(etPanCard.getText().toString().trim());
        profile.setIfscCode(etIfsc.getText().toString().trim());

        if(rgAccType.getCheckedRadioButtonId() == R.id.rb_savings){
            profile.setBankAccountType(getString(R.string.savings));
        }else {
            profile.setBankAccountType(getString(R.string.current));
        }

        WAAddDataModel<MerchantProfileModel> dataModel = new WAAddDataModel<>();
        dataModel.setActionData(profile);
        dataModel.setWebsiteId(mSession.getFPID());

        progressDialog.setMessage(getString(R.string.please_wait_));
        progressDialog.show();

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(dataModel));
        Request request = new Request.Builder()
                .url(Constants.WA_BASE_URL + "merchant_profile3/add-data")
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
                        PaymentInfoEntryFragment.this.dismiss();
                        Toast.makeText(getActivity(), R.string.something_wen_wrong, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        PaymentInfoEntryFragment.this.dismiss();
                        mProfileCallBack.onProfileUpdated(profile);
                        Toast.makeText(getActivity(), getString(R.string.successfully_saved), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void updateAssuredPurchase(){
        progressDialog.setMessage(getString(R.string.please_wait_));
        progressDialog.show();

        String accType = "Savings";
        if(rgAccType.getCheckedRadioButtonId() != R.id.rb_savings){
            accType = "Current";
        }

        final MerchantProfileModel merchantProfile = new MerchantProfileModel();
        merchantProfile.setMerchantName(etName.getText().toString().trim());
        merchantProfile.setBankName(etBankName.getText().toString().trim());
        merchantProfile.setBankAccNum(etAccNum.getText().toString().trim());
        merchantProfile.setIfscCode(etIfsc.getText().toString().trim());
        merchantProfile.setBankAccountType(accType);
        merchantProfile.setPanCard(etPanCard.getText().toString().trim());
        merchantProfile.setGstn(etGstn.getText().toString().trim());

        WaUpdateDataModel update = new WaUpdateDataModel();
        update.setMulti(true);
        update.setQuery(String.format("{merchant_id:'%s'}", mSession.getFPID()));
        update.setUpdateValue(String.format("{$set : {merchant_name:'%s', bank_name:'%s', bank_acc_num:'%s', ifsc_code:'%s', bank_account_type:'%s', pan_card:'%s', gstn:'%s'}}",
                merchantProfile.getMerchantName(),
                merchantProfile.getBankName(),
                merchantProfile.getBankAccNum(),
                merchantProfile.getIfscCode(),
                merchantProfile.getBankAccountType(),
                merchantProfile.getPanCard(),
                merchantProfile.getGstn()
                ));

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
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(getActivity(), R.string.successfully_updated, Toast.LENGTH_SHORT).show();
                        mProfileCallBack.onProfileUpdated(merchantProfile);
                        PaymentInfoEntryFragment.this.dismiss();
                    }
                });
            }
        });


    }

    public interface ProfileUpdateCallBack{
        void onProfileUpdated(MerchantProfileModel profile);
    }

}
