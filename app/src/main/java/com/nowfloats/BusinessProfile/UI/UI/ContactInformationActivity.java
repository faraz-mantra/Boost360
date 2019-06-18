package com.nowfloats.BusinessProfile.UI.UI;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.nowfloats.BusinessProfile.UI.API.Retro_Business_Profile_Interface;
import com.nowfloats.BusinessProfile.UI.API.UpdatePrimaryNumApi;
import com.nowfloats.BusinessProfile.UI.Model.ContactInformationUpdateModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.helper.ui.BaseActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;
import com.thinksity.databinding.ActivityContactInformationBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ContactInformationActivity extends BaseActivity
{
    ActivityContactInformationBinding binding;
    private UserSessionManager session;
    private MaterialDialog dialog, otpDialog, progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_information);

        setSupportActionBar(binding.appBar.toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");
        }

        binding.editPrimaryContactNumber.setInputType(InputType.TYPE_NULL);
        binding.appBar.toolbarTitle.setText(getResources().getString(R.string.contact__info));
        session = new UserSessionManager(getApplicationContext(), ContactInformationActivity.this);

        binding.editPrimaryContactNumber.setOnTouchListener((v, event)-> {

            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                    if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats") || Constants.PACKAGE_NAME.equals("com.digitalseoz"))
                    {
                        showOtpDialog();
                    }

                    /*else
                    {
                        MaterialDialog dialog = dialog();

                        if (!isFinishing())
                        {
                            dialog.show();
                        }
                    }*/
                }

                return true;
            });

        this.initProgressBar();
        this.setData();
    }


    private void initProgressBar()
    {
        if(progressbar == null)
        {
            progressbar = new MaterialDialog.Builder(this)
                    .autoDismiss(false)
                    .progress(true, 0)
                    .build();
        }
    }


    private void showOtpDialog()
    {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_otp, null);
        final EditText number = view.findViewById(R.id.editText);

        dialog = new MaterialDialog.Builder(this)
                .customView(view, false)
                .title("Change Primary Number")
                .negativeText("Cancel")
                .positiveText("Send OTP")
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .negativeColorRes(R.color.gray_transparent)
                .positiveColorRes(R.color.primary_color)
                .onPositive((dialog, which)-> {

                    String numText = number.getText().toString().trim();

                    if (numText.length() > 0)
                    {
                        sendSms(numText);
                    }

                    else
                    {
                        Toast.makeText(ContactInformationActivity.this, getString(R.string.enter_mobile_number), Toast.LENGTH_SHORT).show();
                    }
                })
                .onNegative((dialog, which)-> dialog.dismiss()).show();

        final TextView positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setTextColor(ContextCompat.getColor(ContactInformationActivity.this, R.color.gray_transparent));
        number.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (number.getText().toString().trim().length() > 0)
                {
                    positive.setTextColor(ContextCompat.getColor(ContactInformationActivity.this, R.color.primary));
                }

                else
                {
                    positive.setTextColor(ContextCompat.getColor(ContactInformationActivity.this, R.color.gray_transparent));
                }
            }
        });
    }


    private void otpVerifyDialog(final String number)
    {
        //call send otp api
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_otp_verify, null);
        final EditText otp = view.findViewById(R.id.editText);
        //otpEditText = otp;
        final TextView tvNumber = view.findViewById(R.id.tv_number);
        tvNumber.setText("(" + number + ")");
        TextView tvOTPOverCall = view.findViewById(R.id.tv_get_otp_over_call);
        TextView resend = view.findViewById(R.id.resend_tv);

        resend.setOnClickListener(v-> sendSms(number));

        otpDialog = new MaterialDialog.Builder(this)
                .customView(view, false)
                .autoDismiss(false)
                .title("One Time Password")
                .canceledOnTouchOutside(false).show();

        TextView tvSubmit = view.findViewById(R.id.tv_submit);
        tvOTPOverCall.setOnClickListener(v-> /*reSendOTPOverCall(number)*/ {});
        tvSubmit.setOnClickListener(v-> {

            String numText = otp.getText().toString().trim();

            if (numText.length() > 0)
            {
                //new VerifySMS(number, numText).execute();
                verifySms(number, numText);
            }

            else
            {
                Toast.makeText(ContactInformationActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
            }
        });

        final TextView positive = otpDialog.getActionButton(DialogAction.POSITIVE);
        positive.setTextColor(ContextCompat.getColor(ContactInformationActivity.this, R.color.gray_transparent));
        otp.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (otp.getText().toString().trim().length() > 0)
                {
                    positive.setTextColor(ContextCompat.getColor(ContactInformationActivity.this, R.color.primary));
                }

                else
                {
                    positive.setTextColor(ContextCompat.getColor(ContactInformationActivity.this, R.color.gray_transparent));
                }
            }
        });
    }


    private void hideOtpDialog()
    {
        if (dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }

    private void otpDialogDismiss()
    {
        if (otpDialog != null && otpDialog.isShowing())
        {
            otpDialog.dismiss();
        }
    }

    private void showProgressbar(String content)
    {
        if (progressbar != null && !progressbar.isShowing())
        {
            progressbar.setContent(content);
            progressbar.show();
        }
    }

    private void hideProgressbar()
    {
        if (progressbar != null && progressbar.isShowing())
        {
            progressbar.dismiss();
        }
    }

    private void setData()
    {
        binding.editPrimaryContactNumber.setText(session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM));
        binding.editDisplayContactNumber1.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));
        binding.editDisplayContactNumber2.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1));
        binding.editDisplayContactNumber3.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3));
        binding.editBusinessEmailAddress.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));

        String website = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE);

        if (!TextUtils.isEmpty(website))
        {
            if (website.split("://").length == 2 && website.split("://")[0].equals("http"))
            {
                binding.spinnerHttpProtocol.setSelection(0);
                binding.editWebsiteAddress.setText(website.split("://")[1]);
            }

            else if (website.split("://").length == 2 && website.split("://")[0].equals("https"))
            {
                binding.spinnerHttpProtocol.setSelection(1);
                binding.editWebsiteAddress.setText(website.split("://")[1]);
            }

            else
            {
                binding.spinnerHttpProtocol.setSelection(0);
                binding.editWebsiteAddress.setText(website);
            }
        }

        binding.editFbPageWidget.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FBPAGENAME));
    }


    private void saveInformation()
    {
        ContactInformationUpdateModel model = new ContactInformationUpdateModel();

        ArrayList<ContactInformationUpdateModel.Update> updates = new ArrayList<>();

        String url = binding.spinnerHttpProtocol.getSelectedItem().toString().concat(binding.editWebsiteAddress.getText().toString());

        updates.add(new ContactInformationUpdateModel.Update("URL", url));
        updates.add(new ContactInformationUpdateModel.Update("EMAIL", binding.editBusinessEmailAddress.getText().toString()));

        String number1 = binding.editDisplayContactNumber1.getText().toString().trim();
        String number2 = binding.editDisplayContactNumber2.getText().toString().trim();
        String number3 = binding.editDisplayContactNumber3.getText().toString().trim();

        //updates.add(new ContactInformationUpdateModel.Update("CONTACTS", number1.concat("#").concat(number2).concat("#").concat(number3)));
        updates.add(new ContactInformationUpdateModel.Update("FB", binding.editFbPageWidget.getText().toString()));

        StringBuilder webWidgets = new StringBuilder();

        for (String widget: Constants.StoreWidgets)
        {
            webWidgets.append(widget).append("#");
        }

        webWidgets.append("FBLIKEBOX");

        updates.add(new ContactInformationUpdateModel.Update("WEBWIDGETS", webWidgets.toString()));

        model.setClientId(Constants.clientId);
        model.setFpTag(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toUpperCase());
        model.setUpdates(updates);

        Log.d("JSON_VALUE", new Gson().toJson(model));

        Retro_Business_Profile_Interface profile_interface = Constants.restAdapter.create(Retro_Business_Profile_Interface.class);
        profile_interface.updateContactInformation(model, new Callback<ArrayList<String>>() {

            @Override
            public void success(ArrayList<String> strings, Response response) {

                Log.d("JSON_VALUE", "" + response.getBody());
                Log.d("JSON_VALUE", "" + response.getStatus());

                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE, url);
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FBPAGENAME, binding.editFbPageWidget.getText().toString());
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL, binding.editBusinessEmailAddress.getText().toString());

                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER, binding.editDisplayContactNumber1.getText().toString());
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1, binding.editDisplayContactNumber2.getText().toString());
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3, binding.editDisplayContactNumber3.getText().toString());
            }

            @Override
            public void failure(RetrofitError error) {

                Log.d("JSON_VALUE", "" + error.getResponse().getBody());
                Log.d("JSON_VALUE", "" + error.getResponse().getStatus());
            }
        });
    }


    private boolean isValid()
    {
        /*if(TextUtils.isEmpty(binding.editPrimaryContactNumber.getText().toString()))
        {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.primary_num_can_not_empty));
            return false;
        }

        if (binding.editPrimaryContactNumber.getText().toString().trim().length() > 0 && binding.editPrimaryContactNumber.getText().toString().trim().length() <= 6)
        {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.enter_password_6to12_char));
            return false;
        }*/

        if (binding.editDisplayContactNumber1.getText().toString().trim().length() > 0 && binding.editDisplayContactNumber1.getText().toString().trim().length() < 6)
        {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.enter_password_6to12_char));
            return false;
        }

        if (binding.editDisplayContactNumber2.getText().toString().trim().length() > 0 && binding.editDisplayContactNumber2.getText().toString().trim().length() < 6)
        {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.enter_password_6to12_char));
            return false;
        }

        if (binding.editDisplayContactNumber2.getText().toString().trim().length() > 0 && binding.editDisplayContactNumber2.getText().toString().trim().length() < 6)
        {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.enter_password_6to12_char));
            return false;
        }

        if(binding.editWebsiteAddress.getText().toString().trim().length() > 0 && !isValidWebsite(binding.editWebsiteAddress.getText().toString()))
        {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.enter_valid_website));
            return false;
        }

        if(binding.editBusinessEmailAddress.getText().toString().trim().length() > 0 && !isValidEmail(binding.editBusinessEmailAddress.getText().toString()))
        {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.enter_valid_email));
            return false;
        }

        return true;
    }


    private boolean isValidEmail(String email)
    {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidWebsite(String website)
    {
        Pattern pattern = Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?");
        //Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(website);
        return matcher.matches();
    }


    public void onSaveClick(View view)
    {
        if(!Methods.isOnline(this))
        {
            return;
        }

        if(!isValid())
        {
            return;
        }

        saveInformation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void sendSms(String number)
    {
        showProgressbar("Please Wait...");

        Methods.SmsInterface smsApi = Constants.restAdapterDev1.create(Methods.SmsInterface.class);

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("mobileNumber", number);
        hashMap.put("clientId", Constants.clientId);

        smsApi.sendSms(hashMap, new Callback<Boolean>() {

            @Override
            public void success(Boolean model, Response response)
            {
                hideProgressbar();

                if(response.getStatus() == 200 && model)
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    otpVerifyDialog(number);
                    Toast.makeText(ContactInformationActivity.this, "OTP Sent to " + number, Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(ContactInformationActivity.this, "Failed to Send OTP", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error)
            {
                hideProgressbar();
                Toast.makeText(ContactInformationActivity.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void verifySms(String number, String otp)
    {
        showProgressbar("Verifying OTP...");
        Methods.SmsInterface smsApi = Constants.restAdapterDev1.create(Methods.SmsInterface.class);

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("mobileNumber", number);
        hashMap.put("otp", otp);
        hashMap.put("clientId", Constants.clientId);

        smsApi.verifySms(hashMap, new Callback<Boolean>() {

            @Override
            public void success(Boolean model, Response response)
            {
                hideProgressbar();

                if (model == null)
                {
                    Toast.makeText(ContactInformationActivity.this, "Failed to Verify OTP", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(response.getStatus() == 200 && model)
                {
                    //Toast.makeText(ContactInformationActivity.this, "Verification Successful", Toast.LENGTH_SHORT).show();
                    //changePrimary(number);
                    return;
                }

                Toast.makeText(ContactInformationActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error)
            {
                hideProgressbar();
                Toast.makeText(ContactInformationActivity.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePrimary(final String number)
    {
        UpdatePrimaryNumApi updateApi = Constants.restAdapter.create(UpdatePrimaryNumApi.class);
        updateApi.changeNumber(Constants.PrimaryNumberClientId, session.getFPID(), number, new Callback<String>() {

            @Override
            public void success(String s, Response response)
            {
                hideProgressbar();
                otpDialogDismiss();

                if (s == null || response.getStatus() != 200)
                {
                    Methods.showSnackBarNegative(ContactInformationActivity.this, getString(R.string.something_went_wrong_try_again));
                    return;
                }

                MixPanelController.track(MixPanelController.PRIMARY_NUMBER_CHANGE, null);
                session.storeFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM, number);
                binding.editPrimaryContactNumber.setText(number);
                Methods.showSnackBarPositive(ContactInformationActivity.this, "Primary number changed successfully");
            }

            @Override
            public void failure(RetrofitError error)
            {
                hideProgressbar();
                otpDialogDismiss();
                Methods.showSnackBarNegative(ContactInformationActivity.this, getString(R.string.something_went_wrong_try_again));
            }
        });
    }
}