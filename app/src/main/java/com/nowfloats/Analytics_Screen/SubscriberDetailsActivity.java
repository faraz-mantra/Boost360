package com.nowfloats.Analytics_Screen;

import static com.nowfloats.helper.ValidationUtilsKt.isMobileNumberValid;
import static com.nowfloats.helper.ValidationUtilsKt.isEmailValid;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import com.framework.views.customViews.CustomImageView;
import com.framework.views.customViews.CustomTextView;
import com.google.gson.Gson;
import com.nowfloats.Analytics_Screen.API.SubscriberApis;
import com.nowfloats.Analytics_Screen.model.AddSubscriberModel;
import com.nowfloats.Analytics_Screen.model.SubscriberModel;
import com.nowfloats.Analytics_Screen.model.UnsubscriberModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 03-03-2017.
 */

public class SubscriberDetailsActivity extends AppCompatActivity implements View.OnClickListener {

  static {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  AppCompatTextView subscriberEmailId, subscriberDate;
  LinearLayoutCompat btnEmailView, viewNumber, btnWhatsapp, btnMessage, btnCall;
  LinearLayout layout;
  String fpTag;
  ProgressDialog mProgressBar;
  private SubscriberModel mSubscriberData;
  private String subActiveText = "Unsubscribe";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_subscriber_details);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      setTitle("Subscriber Details");
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    Intent i = getIntent();
    mSubscriberData = new Gson().fromJson(i.getStringExtra("data"), SubscriberModel.class);
    fpTag = i.getStringExtra("fpTag");
    if (mSubscriberData == null) {
      finish();
      return;
    }
    mProgressBar = new ProgressDialog(this);
    mProgressBar.setIndeterminate(false);
    mProgressBar.setMessage(getString(R.string.please_wait));
    mProgressBar.setCanceledOnTouchOutside(false);
    subscriberEmailId = findViewById(R.id.subscriber_email);
    subscriberDate = findViewById(R.id.subcriber_date);
    btnEmailView = findViewById(R.id.btn_email_view);
    viewNumber = findViewById(R.id.view_number);
    btnWhatsapp = findViewById(R.id.btn_whatsapp);
    btnMessage = findViewById(R.id.btn_message);
    btnCall = findViewById(R.id.btn_call);
    layout = findViewById(R.id.parent_layout);
    btnEmailView.setOnClickListener(this);
    btnMessage.setOnClickListener(this);
    btnWhatsapp.setOnClickListener(this);
    btnCall.setOnClickListener(this);
    if (!mSubscriberData.getUserMobile().toLowerCase().contains("@")) {
      subscriberEmailId.setText(mSubscriberData.getUserMobileWithCountryCode());
      btnEmailView.setVisibility(View.GONE);
      viewNumber.setVisibility(View.VISIBLE);
    } else {
      subscriberEmailId.setText(mSubscriberData.getUserMobile());
      btnEmailView.setVisibility(View.VISIBLE);
      viewNumber.setVisibility(View.GONE);
    }
    try {
      setSubscriberStatus(Integer.parseInt(mSubscriberData.getSubscriptionStatus()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void sendResult(String status) {
    Intent i = new Intent(this, SubscribersActivity.class);
    i.putExtra("STATUS", status);
    setResult(RESULT_OK, i);
    finish();
  }

  private void setSubscriberStatus(int status) {
    if (Constants.SubscriberStatus.SUBSCRIBED.value == status) {
      layout.setBackgroundResource(R.color.primary);
      subActiveText = "Unsubscribe";
      String sDate = mSubscriberData.getCreatedOn().replace("/Date(", "").replace(")/", "");
      String[] splitDate = sDate.split("\\+");
      subscriberDate.setText("Subscribed on " + Methods.getFormattedDate(splitDate[0]));
    } else if (Constants.SubscriberStatus.REQUESTED.value == status) {
      subActiveText = "Cancel initiated subscription";
      layout.setBackgroundResource(R.color.gray_transparent);
      subscriberDate.setVisibility(View.GONE);
    } else if (Constants.SubscriberStatus.UNSUBSCRIBED.value == status) {
      subActiveText = "Subscribe";
      subscriberDate.setVisibility(View.GONE);
      layout.setBackgroundResource(R.color.gray_transparent);
    }
  }

  private void unSubscriber() {
    show();
    UnsubscriberModel model = new UnsubscriberModel();
    model.setClientId(Constants.clientId);
    model.setFpTag(fpTag);
    model.setCountryCode(mSubscriberData.getUserCountryCode());
    model.setIsBulkUnSubscription(false);
    model.setUserContact(mSubscriberData.getUserMobile());
    SubscriberApis mSubscriberApis = Constants.restAdapter.create(SubscriberApis.class);
    mSubscriberApis.unsubscriber(model, new Callback<Object>() {
      @Override
      public void success(Object o, Response response) {
        hide();
        if (response.getStatus() == 200) {
          Toast.makeText(SubscriberDetailsActivity.this, mSubscriberData.getUserMobile() + " Successfully unsubscribed", Toast.LENGTH_SHORT).show();
          mSubscriberData.setSubscriptionStatus(String.valueOf(Constants.SubscriberStatus.UNSUBSCRIBED.value));
          setSubscriberStatus(Constants.SubscriberStatus.UNSUBSCRIBED.value);
        } else {
          Methods.showSnackBarNegative(SubscriberDetailsActivity.this, getString(R.string.something_went_wrong_try_again));
        }
      }

      @Override
      public void failure(RetrofitError error) {
        hide();
        Methods.showSnackBarNegative(SubscriberDetailsActivity.this, getString(R.string.something_went_wrong_try_again));
      }
    });
  }

  private void show() {
    if (!mProgressBar.isShowing()) {
      mProgressBar.show();
    }
  }

  private void hide() {
    if (!isFinishing() && mProgressBar.isShowing()) {
      mProgressBar.hide();
    }
  }

  private boolean checkIsEmailOrNumber(String email) {
    Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    Matcher mat = pattern.matcher(email);
    return mat.matches();
  }

  private void addSubscriber() {
    if (!checkIsEmailOrNumber(mSubscriberData.getUserMobile())) {
      Toast.makeText(this, "You can't subscriber mobile number", Toast.LENGTH_SHORT).show();
      return;
    }
    show();
    AddSubscriberModel model = new AddSubscriberModel();
    model.setUserContact(mSubscriberData.getUserMobile());
    model.setFpTag(fpTag);
    model.setCountryCode(mSubscriberData.getUserCountryCode());
    model.setClientId(Constants.clientId);
    SubscriberApis mSubscriberApis = Constants.restAdapter.create(SubscriberApis.class);
    mSubscriberApis.addSubscriber(model, new Callback<String>() {
      @Override
      public void success(String s, Response response) {
        hide();
        if (response.getStatus() == 200) {
          mSubscriberData.setSubscriptionStatus(String.valueOf(Constants.SubscriberStatus.REQUESTED.value));
          setSubscriberStatus(Constants.SubscriberStatus.REQUESTED.value);
          Toast.makeText(SubscriberDetailsActivity.this, mSubscriberData.getUserMobile() + " Successfully Added", Toast.LENGTH_SHORT).show();

        } else {
          Methods.showSnackBarNegative(SubscriberDetailsActivity.this, getString(R.string.something_went_wrong_try_again));
        }
      }

      @Override
      public void failure(RetrofitError error) {
        Log.v("ggg", error.getMessage());
        hide();
        Methods.showSnackBarNegative(SubscriberDetailsActivity.this, getString(R.string.something_went_wrong_try_again));
      }
    });
  }

  private void sendSms() {
    try {
      String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this);
      Intent sendIntent;
      sendIntent = new Intent(Intent.ACTION_SENDTO);
      if (defaultSmsPackageName != null) {
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Write here");
        sendIntent.setData(Uri.parse("sms:" + mSubscriberData.getUserMobile()));
        sendIntent.setType("text/plain");
        sendIntent.setPackage(defaultSmsPackageName);
        startActivity(sendIntent);
      } else {
        sendIntent.setType("vnd.android-dir/mms-sms");
        sendIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendIntent.putExtra("sms_body", "Write here");
        sendIntent.setData(Uri.parse("sms:" + mSubscriberData.getUserMobile()));
        startActivity(Intent.createChooser(sendIntent, "Sms by:"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void makeCall() {
    try {
      Intent callIntent = new Intent(Intent.ACTION_DIAL);
      callIntent.addCategory(Intent.CATEGORY_DEFAULT);
      callIntent.setData(Uri.parse("tel:" + mSubscriberData.getUserMobileWithCountryCode()));
      startActivity(Intent.createChooser(callIntent, "Call by:"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void sendMail() {
    try {
      Intent email = new Intent(Intent.ACTION_SEND);
      email.addCategory(Intent.CATEGORY_DEFAULT);
      email.setData(Uri.parse("mailto:"));
      email.setType("message/rfc822");
      email.putExtra(Intent.EXTRA_EMAIL, new String[]{mSubscriberData.getUserMobile()});
      startActivity(Intent.createChooser(email, "Email by:"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void openWhatsApp(String countryCode, String mobile) {
    try {
      Uri uri = Uri.parse("whatsapp://send?phone=" + countryCode + mobile);
      Intent i = new Intent(Intent.ACTION_VIEW, uri);
      startActivity(i);
    } catch (ActivityNotFoundException e) {
      e.printStackTrace();
      Toast.makeText(this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onBackPressed() {
    sendResult(mSubscriberData.getSubscriptionStatus());
    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.more_setting_menu, menu);
    View item = menu.findItem(R.id.menu_edit_subscribers).getActionView();
    if (item != null) {
      CustomImageView btnImage = item.findViewById(R.id.more_subs);
      if (btnImage != null) {
        btnImage.setOnClickListener(view -> {
          showPopupWindow(view);
        });
      }
    }
    return super.onCreateOptionsMenu(menu);
  }


  private void showPopupWindow(View anchor) {
    View view = LayoutInflater.from(this).inflate(R.layout.popup_window_subscribers_menu, null);
    PopupWindow popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
    LinearLayoutCompat more = popupWindow.getContentView().findViewById(R.id.ll_subscribers);
    CustomTextView txtSub = popupWindow.getContentView().findViewById(R.id.subscribers_text);
    txtSub.setText(subActiveText);
    more.setOnClickListener(v -> {
      if ((Integer.parseInt(mSubscriberData.getSubscriptionStatus().trim()) == Constants.SubscriberStatus.SUBSCRIBED.value) ||
          (Integer.parseInt(mSubscriberData.getSubscriptionStatus().trim()) == Constants.SubscriberStatus.REQUESTED.value)) {
        unSubscriber();
      } else addSubscriber();
      popupWindow.dismiss();
    });
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) popupWindow.setElevation(5.0F);
    popupWindow.showAsDropDown(anchor, 0, 20);
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
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_whatsapp:
        if (isMobileNumberValid(mSubscriberData.getUserMobile())) openWhatsApp(mSubscriberData.getUserCountryCode(), mSubscriberData.getUserMobile());
        else Toast.makeText(this, "Mobile number not valid!", Toast.LENGTH_SHORT).show();
        break;
      case R.id.btn_call:
        if (isMobileNumberValid(mSubscriberData.getUserMobile())) makeCall();
        else Toast.makeText(this, "Mobile number not valid!", Toast.LENGTH_SHORT).show();
        break;
      case R.id.btn_email_view:
        if (isEmailValid(mSubscriberData.getUserMobile())) sendMail();
        else Toast.makeText(this, "Email not valid!", Toast.LENGTH_SHORT).show();
        break;
      case R.id.btn_message:
        if (isMobileNumberValid(mSubscriberData.getUserMobile())) sendSms();
        else Toast.makeText(this, "Mobile number not valid!", Toast.LENGTH_SHORT).show();
        break;
    }
  }
}