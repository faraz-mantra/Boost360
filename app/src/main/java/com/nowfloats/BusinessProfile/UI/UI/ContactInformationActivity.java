package com.nowfloats.BusinessProfile.UI.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.afollestad.materialdialogs.MaterialDialog;
import com.framework.models.firestore.FirestoreManager;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.google.gson.Gson;
import com.nowfloats.Analytics_Screen.VmnCallCardsActivity;
import com.nowfloats.BusinessProfile.UI.API.Retro_Business_Profile_Interface;
import com.nowfloats.BusinessProfile.UI.API.UpdatePrimaryNumApi;
import com.nowfloats.BusinessProfile.UI.Model.ContactInformationUpdateModel;
import com.nowfloats.BusinessProfile.UI.Model.WhatsAppBusinessNumberModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.helper.ui.BaseActivity;
import com.nowfloats.manageinventory.interfaces.WebActionCallInterface;
import com.nowfloats.manageinventory.models.WAAddDataModel;
import com.nowfloats.manageinventory.models.WaUpdateDataModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.signup.UI.Model.ContactDetailsModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;
import com.thinksity.databinding.ActivityContactInformationBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.framework.webengageconstant.EventLabelKt.BUSINESS_DESCRIPTION;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_NULL;
import static com.framework.webengageconstant.EventNameKt.BUSINESS_CONTACT_ADDED;
import static com.framework.webengageconstant.EventNameKt.DISPLAY_CONTACT_1;
import static com.framework.webengageconstant.EventNameKt.DISPLAY_CONTACT_2;
import static com.framework.webengageconstant.EventNameKt.DISPLAY_CONTACT_3;
import static com.framework.webengageconstant.EventNameKt.EMAIL_ADDRESS;
import static com.framework.webengageconstant.EventNameKt.FACEBOOK_PAGE_URL;
import static com.framework.webengageconstant.EventNameKt.OTHER_WEBSITE;
import static com.framework.webengageconstant.EventNameKt.REGISTERED_CONTACT_NUMBER;
import static com.framework.webengageconstant.EventNameKt.WHATSAPP_FOR_BUSINESS_NUMBER;
import static com.framework.webengageconstant.EventValueKt.NULL;
import static com.nowfloats.helper.ValidationUtilsKt.isEmailValid;
import static com.nowfloats.helper.ValidationUtilsKt.isMobileNumberValid;


public class ContactInformationActivity extends BaseActivity {
    ActivityContactInformationBinding binding;
    private UserSessionManager session;
    private MaterialDialog dialog, otpDialog, progressbar;
    private boolean VMN_Dialog;
    private WhatsAppBusinessNumberModel numberModel;
    private String phoneCountryCode;

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_information);

        setSupportActionBar(binding.appBar.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");
        }

        binding.editPrimaryContactNumber.setInputType(InputType.TYPE_NULL);
        binding.appBar.toolbarTitle.setText(getResources().getString(R.string.contact__info));
        session = new UserSessionManager(getApplicationContext(), ContactInformationActivity.this);

        this.phoneCountryCode = "+".concat(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE));
        binding.editPrimaryContactNumber.setOnClickListener(v -> {
            WebEngageController.trackEvent(REGISTERED_CONTACT_NUMBER, EVENT_LABEL_NULL, NULL);
        });
        binding.editDisplayContactNumber1.setOnClickListener(v -> {
            WebEngageController.trackEvent(DISPLAY_CONTACT_1, EVENT_LABEL_NULL, NULL);
        });
        binding.editDisplayContactNumber1.setOnClickListener(v -> {
            WebEngageController.trackEvent(DISPLAY_CONTACT_2, EVENT_LABEL_NULL, NULL);
        });
        binding.editDisplayContactNumber1.setOnClickListener(v -> {
            WebEngageController.trackEvent(DISPLAY_CONTACT_3, EVENT_LABEL_NULL, NULL);
        });
        binding.editWhatsappNumber.setOnClickListener(v -> {
            WebEngageController.trackEvent(WHATSAPP_FOR_BUSINESS_NUMBER, EVENT_LABEL_NULL, NULL);
        });
        binding.editBusinessEmailAddress.setOnClickListener(v -> {
            WebEngageController.trackEvent(EMAIL_ADDRESS, EVENT_LABEL_NULL, NULL);
        });
        binding.editWebsiteAddress.setOnClickListener(v -> {
            WebEngageController.trackEvent(OTHER_WEBSITE, EVENT_LABEL_NULL, NULL);
        });
        binding.editFbPageWidget.setOnClickListener(v -> {
            WebEngageController.trackEvent(FACEBOOK_PAGE_URL, EVENT_LABEL_NULL, NULL);
        });

        binding.editPrimaryContactNumber.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats") || Constants.PACKAGE_NAME.equals("com.digitalseoz")) {
                    showOtpDialog();
                } else {
                    dialog().show();
                }
            }

            return true;
        });

        binding.editCallTrackerNumber.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_UP) {
                dialog().show();
            }

            return true;
        });

        binding.tvVmnReport.setOnClickListener(v -> {

            Intent i = new Intent(ContactInformationActivity.this, VmnCallCardsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        this.addInfoButtonListener();
        this.initProgressBar();
        this.setData();
        this.getWhatsAppNumber(session.getFpTag());
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }


    private void initProgressBar() {
        if (progressbar == null) {
            progressbar = new MaterialDialog.Builder(this)
                    .autoDismiss(false)
                    .progress(true, 0)
                    .build();
        }
    }


    private void showOtpDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_otp, null);
        final EditText number = view.findViewById(R.id.editText);

        dialog = new MaterialDialog.Builder(this)
                .customView(view, false)
                .negativeText("CANCEL")
                .positiveText("SEND OTP")
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .negativeColorRes(R.color.gray_transparent)
                .positiveColorRes(R.color.colorAccentLight)
                .onPositive((dialog, which) -> {

                    String numText = number.getText().toString().trim();

                    if (numText.length() >= 6) {
                        sendSms(numText);
                    } else {
                        Toast.makeText(ContactInformationActivity.this, getResources().getString(R.string.enter_password_6to12_char), Toast.LENGTH_SHORT).show();
                    }
                })
                .onNegative((dialog, which) -> dialog.dismiss()).show();
    }


    private void otpVerifyDialog(final String number) {
        if (otpDialog != null && otpDialog.isShowing()) {
            return;
        }

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_otp_verify, null);
        final EditText otp = view.findViewById(R.id.editText);
        final TextView tvNumber = view.findViewById(R.id.tv_number);
        tvNumber.setText("(" + number + ")");
        TextView resend = view.findViewById(R.id.resend_tv);

        resend.setOnClickListener(v -> sendSms(number));

        otpDialog = new MaterialDialog.Builder(this)
                .customView(view, false)
                .autoDismiss(false)
                .negativeText("CANCEL")
                .positiveText("VERIFY")
                .canceledOnTouchOutside(false)
                .negativeColorRes(R.color.gray_transparent)
                .positiveColorRes(R.color.colorAccentLight)
                .onPositive((dialog, which) -> {

                    String numText = otp.getText().toString().trim();

                    if (numText.length() > 0) {
                        verifySms(number, numText);
                    } else {
                        Toast.makeText(ContactInformationActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                    }
                })
                .onNegative((dialog, which) -> dialog.dismiss()).show();
    }


    private MaterialDialog dialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_link_layout, null, false);
        TextView message = dialogView.findViewById(R.id.toast_message_to_contact);

        if (VMN_Dialog) {
            //message.setText("Call tracker is enabled. You will receive the call on your primary number." + getString(R.string.primary_contact_number_message));
            message.setText("This is your virtual mobile number through which customers can contact you. All activity on this number is logged and can be viewed or played back with the Call Tracker.");
        } else {
            message.setText(getString(R.string.primary_contact_number_message));
        }

        return new MaterialDialog.Builder(ContactInformationActivity.this)
                .title("Call tracker is enabled")
                .customView(dialogView, false)
                .positiveText(getString(R.string.ok))
                .positiveColorRes(R.color.colorAccentLight)
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                    }

                })
                .build();
    }


    private void hideOtpDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void otpDialogDismiss() {
        if (otpDialog != null && otpDialog.isShowing()) {
            otpDialog.dismiss();
        }
    }

    private void showProgressbar(String content) {
        if (progressbar != null && !progressbar.isShowing()) {
            progressbar.setContent(content);
            progressbar.show();
        }
    }

    private void hideProgressbar() {
        if (progressbar != null && progressbar.isShowing()) {
            progressbar.dismiss();
        }
    }

    private void setData() {
        boolean anyOneItemSave = false;

        String contactNumber1 = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER);
        String contactNumber2 = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1);
        String contactNumber3 = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3);

        if ("VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1)) ||
                "VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3)) ||
                "VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME))) {

            VMN_Dialog = true;

            binding.layoutDisplayContactNumber1.setVisibility(View.GONE);
            binding.layoutDisplayContactNumber2.setVisibility(View.GONE);
            binding.layoutDisplayContactNumber3.setVisibility(View.GONE);
            binding.layoutCallTrackerNumber.setVisibility(View.VISIBLE);

            if ("VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME))) {
                String number = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER);
                number = number.startsWith("0") ? number.substring(1) : number;
                binding.editCallTrackerNumber.setText(number);
                anyOneItemSave = true;
            } else if ("VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1))) {
                String number = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1);
                number = number.startsWith("0") ? number.substring(1) : number;
                binding.editCallTrackerNumber.setText(number);
                anyOneItemSave = true;
            } else if ("VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3))) {
                String number = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3);
                number = number.startsWith("0") ? number.substring(1) : number;
                binding.editCallTrackerNumber.setText(number);
                anyOneItemSave = true;
            }
            binding.editCallTrackerNumberCode.setText(phoneCountryCode);
        } else {
            binding.layoutDisplayContactNumber1.setVisibility(View.VISIBLE);
            binding.layoutDisplayContactNumber2.setVisibility(View.VISIBLE);
            binding.layoutDisplayContactNumber3.setVisibility(View.VISIBLE);
            binding.layoutCallTrackerNumber.setVisibility(View.GONE);

            binding.editDisplayContactNumber1.setText(contactNumber1);
            binding.editDisplayContactNumber2.setText(contactNumber2);
            binding.editDisplayContactNumber3.setText(contactNumber3);

            binding.editDisplayContactNumberCode1.setText(phoneCountryCode);
            binding.editDisplayContactNumberCode2.setText(phoneCountryCode);
            binding.editDisplayContactNumberCode3.setText(phoneCountryCode);
        }

        String primaryContactNumber = session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM);
        binding.editPrimaryContactNumber.setText(primaryContactNumber);
        binding.editPrimaryCode.setText(phoneCountryCode);
        binding.editWhatsappCode.setText(phoneCountryCode);

        String email = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL);
        binding.editBusinessEmailAddress.setText(email);

        String website = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE);

        if (!TextUtils.isEmpty(website)) {
            if (website.split("://").length == 2 && website.split("://")[0].equals("http")) {
                binding.spinnerHttpProtocol.setSelection(0);
                binding.editWebsiteAddress.setText(website.split("://")[1]);
            } else if (website.split("://").length == 2 && website.split("://")[0].equals("https")) {
                binding.spinnerHttpProtocol.setSelection(1);
                binding.editWebsiteAddress.setText(website.split("://")[1]);
            }

            /*else
            {
                binding.spinnerHttpProtocol.setSelection(0);
                binding.editWebsiteAddress.setText(website);
            }*/
        }

        binding.editFbPageWidget.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FBPAGENAME));
        anyOneItemSave = (!TextUtils.isEmpty(contactNumber1) || !TextUtils.isEmpty(contactNumber2) || !TextUtils.isEmpty(contactNumber3) || !TextUtils.isEmpty(primaryContactNumber) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(website));
        onContactInfoAddedOrUpdated(anyOneItemSave);
    }

    private void onContactInfoAddedOrUpdated(Boolean isAdded) {
        FirestoreManager instance = FirestoreManager.INSTANCE;
        if (instance.getDrScoreData()==null || instance.getDrScoreData().getMetricdetail() == null) return;
        instance.getDrScoreData().getMetricdetail().setBoolean_add_contact_details(isAdded);
        instance.updateDocument();
    }


    private void saveInformation() {
        showProgressbar("Updating Information...");

        ContactInformationUpdateModel model = new ContactInformationUpdateModel();

        ArrayList<ContactInformationUpdateModel.Update> updates = new ArrayList<>();

        String url = binding.spinnerHttpProtocol.getSelectedItem().toString().concat(binding.editWebsiteAddress.getText().toString());

        updates.add(new ContactInformationUpdateModel.Update("URL", url));
        updates.add(new ContactInformationUpdateModel.Update("EMAIL", binding.editBusinessEmailAddress.getText().toString()));

        if (!VMN_Dialog) {
            List<ContactDetailsModel> contacts = new ArrayList<>();

            String number1 = binding.editDisplayContactNumber1.getText().toString().trim();
            String number2 = binding.editDisplayContactNumber2.getText().toString().trim();
            String number3 = binding.editDisplayContactNumber3.getText().toString().trim();

            contacts.add(new ContactDetailsModel(number1, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME)));
            contacts.add(new ContactDetailsModel(number2, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1)));
            contacts.add(new ContactDetailsModel(number3, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3)));

            updates.add(new ContactInformationUpdateModel.Update("CONTACTS", new Gson().toJson(contacts) /*number1.concat("#").concat(number2).concat("#").concat(number3)*/));
        }

        updates.add(new ContactInformationUpdateModel.Update("FB", binding.editFbPageWidget.getText().toString()));

        StringBuilder webWidgets = new StringBuilder();

        for (String widget : Constants.StoreWidgets) {
            webWidgets.append(widget).append("#");
        }

        webWidgets.append("FBLIKEBOX");

        updates.add(new ContactInformationUpdateModel.Update("WEBWIDGETS", webWidgets.toString()));

        model.setClientId(Constants.clientId);
        model.setFpTag(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toUpperCase());
        model.setUpdates(updates);

        Retro_Business_Profile_Interface profile_interface = Constants.restAdapter.create(Retro_Business_Profile_Interface.class);
        profile_interface.updateContactInformation(model, new Callback<ArrayList<String>>() {

            @Override
            public void success(ArrayList<String> strings, Response response) {

                hideProgressbar();

                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE, url);
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FBPAGENAME, binding.editFbPageWidget.getText().toString());
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL, binding.editBusinessEmailAddress.getText().toString());

                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER, binding.editDisplayContactNumber1.getText().toString());
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1, binding.editDisplayContactNumber2.getText().toString());
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3, binding.editDisplayContactNumber3.getText().toString());

                Methods.showSnackBarPositive(ContactInformationActivity.this, "Information Updated Successfully");
                WebEngageController.trackEvent(BUSINESS_CONTACT_ADDED, BUSINESS_DESCRIPTION, session.getFpTag());
                onContactInfoAddedOrUpdated(true);
            }

            @Override
            public void failure(RetrofitError error) {

                hideProgressbar();
                Methods.showSnackBarNegative(ContactInformationActivity.this, "Failed to Update Information");
            }
        });


        if (numberModel == null && binding.editWhatsappNumber.getText().toString().trim().length() > 0) {
            WhatsAppBusinessNumberModel whatsAppBusinessNumberModel = new WhatsAppBusinessNumberModel();
            whatsAppBusinessNumberModel.setWhatsAppNumber(binding.editWhatsappNumber.getText().toString());

            WAAddDataModel<WhatsAppBusinessNumberModel> dataModel = new WAAddDataModel<>();
            dataModel.setWebsiteId(session.getFpTag());
            dataModel.setActionData(whatsAppBusinessNumberModel);

            addWhatsAppNumber(dataModel);
        } else if (numberModel != null && !binding.editWhatsappNumber.getText().toString().equals(numberModel.getWhatsAppNumber())) {
            WaUpdateDataModel update = new WaUpdateDataModel();
            update.setQuery(String.format("{_id:'%s'}", numberModel.getId()));

            update.setUpdateValue(String.format("{$set:{active_whatsapp_number:'%s', IsArchived:'%s'}}",
                    binding.editWhatsappNumber.getText().toString(),
                    false));

            update.setMulti(true);
            updateWhatsAppNumber(update);
        }
    }


    private boolean isValid() {
        String contactNumber1 = binding.editDisplayContactNumber1.getText().toString().trim();
        String contactNumber2 = binding.editDisplayContactNumber2.getText().toString().trim();
        String contactNumber3 = binding.editDisplayContactNumber3.getText().toString().trim();
        String whatsAppNumber = binding.editWhatsappNumber.getText().toString().trim();
        String businessEmailAddress = binding.editBusinessEmailAddress.getText().toString().trim();

        if (!contactNumber1.isEmpty() && !isMobileNumberValid(contactNumber1)) {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.contact_number_not_valid));
            binding.editDisplayContactNumber1.requestFocus();
            return false;
        }

        if (!contactNumber2.isEmpty() && !isMobileNumberValid(contactNumber2)) {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.contact_number_not_valid));
            binding.editDisplayContactNumber2.requestFocus();
            return false;
        }

        if (!contactNumber3.isEmpty() && !isMobileNumberValid(contactNumber3)) {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.contact_number_not_valid));
            binding.editDisplayContactNumber3.requestFocus();
            return false;
        }

        if (!whatsAppNumber.isEmpty() && !isMobileNumberValid(whatsAppNumber)) {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.contact_number_not_valid));
            binding.editWhatsappNumber.requestFocus();
            return false;
        }

        if (binding.editWebsiteAddress.getText().toString().trim().length() > 0 && !isValidWebsite(binding.editWebsiteAddress.getText().toString())) {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.enter_valid_website));
            binding.editWebsiteAddress.requestFocus();
            return false;
        }

        if (!businessEmailAddress.isEmpty() && !isEmailValid(businessEmailAddress)) {
            Methods.showSnackBarNegative(this, getResources().getString(R.string.enter_valid_email));
            binding.editBusinessEmailAddress.requestFocus();
            return false;
        }

        return true;
    }


    private boolean isValidWebsite(String website) {
        Pattern pattern = Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?");
        //Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(website);
        return matcher.matches();
    }


    public void onSaveClick(View view) {
        if (!Methods.isOnline(this)) {
            return;
        }

        if (!isValid()) {
            return;
        }

        saveInformation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void sendSms(String number) {
        showProgressbar(getString(R.string.please_wait_));

        Methods.SmsInterface smsApi = Constants.restAdapterDev1.create(Methods.SmsInterface.class);

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("mobileNumber", number);
        hashMap.put("clientId", Constants.clientId);

        smsApi.sendSms(hashMap, new Callback<Boolean>() {

            @Override
            public void success(Boolean model, Response response) {
                hideProgressbar();

                if (response.getStatus() == 200 && model) {
                    hideOtpDialog();
                    otpVerifyDialog(number);

                    Toast.makeText(ContactInformationActivity.this, "OTP Sent to " + number, Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(ContactInformationActivity.this, "Failed to Send OTP", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressbar();
                Toast.makeText(ContactInformationActivity.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void verifySms(String number, String otp) {
        showProgressbar("Verifying OTP...");
        Methods.SmsInterface smsApi = Constants.restAdapterDev1.create(Methods.SmsInterface.class);

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("mobileNumber", number);
        hashMap.put("otp", otp);
        hashMap.put("clientId", Constants.clientId);

        smsApi.verifySms(hashMap, new Callback<Boolean>() {

            @Override
            public void success(Boolean model, Response response) {
                hideProgressbar();

                if (model == null) {
                    Toast.makeText(ContactInformationActivity.this, "Failed to Verify OTP", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getStatus() == 200 && model) {
                    changePrimary(number);
                    return;
                }

                Toast.makeText(ContactInformationActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressbar();
                Toast.makeText(ContactInformationActivity.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePrimary(final String number) {
        UpdatePrimaryNumApi updateApi = Constants.restAdapter.create(UpdatePrimaryNumApi.class);
        updateApi.changeNumber(session.getFPID(), Constants.clientId, number, new Callback<String>() {

            @Override
            public void success(String s, Response response) {
                hideProgressbar();
                otpDialogDismiss();

                if (s == null || response.getStatus() != 200) {
                    Methods.showSnackBarNegative(ContactInformationActivity.this, getString(R.string.something_went_wrong_try_again));
                    return;
                }

                MixPanelController.track(MixPanelController.PRIMARY_NUMBER_CHANGE, null);
                session.storeFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM, number);
                binding.editPrimaryContactNumber.setText(number);
                Methods.showSnackBarPositive(ContactInformationActivity.this, "Primary number changed successfully");
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressbar();
                otpDialogDismiss();

                if (error.getResponse().getStatus() == 400) {
                    showOtpDialog();
                    Methods.showSnackBarNegative(ContactInformationActivity.this, "This primary number is already used");
                    return;
                }

                Methods.showSnackBarNegative(ContactInformationActivity.this, getString(R.string.something_went_wrong_try_again));
            }
        });
    }


    private void getWhatsAppNumber(String websiteId) {

        Constants.webActionAdapter.create(WebActionCallInterface.class)
                .getWhatsAppNumber(String.format("{WebsiteId:'%s'}", websiteId), new Callback<WebActionModel<WhatsAppBusinessNumberModel>>() {

                    @Override
                    public void success(WebActionModel<WhatsAppBusinessNumberModel> model, Response response) {

                        if (model != null && model.getData() != null && model.getData().size() > 0) {
                            numberModel = model.getData().get(0);
                            String whatsAppNumber = numberModel.getWhatsAppNumber() == null ? "" : numberModel.getWhatsAppNumber();
                            binding.editWhatsappNumber.setText(whatsAppNumber);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }


    private void addWhatsAppNumber(WAAddDataModel<WhatsAppBusinessNumberModel> addDataModel) {

        Constants.webActionAdapter.create(WebActionCallInterface.class)
                .addWhatsAppNumber(addDataModel, new Callback<String>() {

                    @Override
                    public void success(String id, Response response) {

                        numberModel = new WhatsAppBusinessNumberModel();
                        numberModel.setId(id);
                        numberModel.setWhatsAppNumber(binding.editWhatsappNumber.getText().toString());

                        Log.d("updateWhatsAppNumber", "SUCCESS");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("updateWhatsAppNumber", "FAIL");
                    }
                });
    }


    private void updateWhatsAppNumber(WaUpdateDataModel updateDataModel) {

        Constants.webActionAdapter.create(WebActionCallInterface.class)
                .updateWhatsAppNumber(updateDataModel, new Callback<String>() {

                    @Override
                    public void success(String model, Response response) {

                        if (numberModel != null) {
                            numberModel.setWhatsAppNumber(binding.editWhatsappNumber.getText().toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }


    /**
     * Tool tip for information/hint
     *
     * @param position
     * @param message
     * @param view
     */
    private void toolTip(ViewTooltip.Position position, String message, View view) {
        ViewTooltip
                .on(this, view)
                .autoHide(true, 3500)
                .clickToHide(true)
                .corner(30)
                .textColor(Color.WHITE)
                .color(R.color.accentColor)
                .position(position)
                .text(message)
                .show();
    }

    /**
     * Add tooltip button listener
     */
    private void addInfoButtonListener() {
        binding.ibInfoWhatsapp.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, "This is your WhatsApp number.", binding.ibInfoWhatsapp));
    }
}