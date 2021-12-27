package com.nowfloats.BusinessProfile.UI.UI;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.framework.firebaseUtils.firestore.FirestoreManager;
import com.framework.views.customViews.CustomButton;
import com.nowfloats.BusinessProfile.UI.API.UploadProfileAsyncTask;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Event;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static android.view.View.NO_ID;
import static com.framework.webengageconstant.EventLabelKt.CLICK;
import static com.framework.webengageconstant.EventLabelKt.CLICKING_USE_SAME_TIME_FOR_WHOLE_WEEK;
import static com.framework.webengageconstant.EventLabelKt.TOGGLE_FRIDAY;
import static com.framework.webengageconstant.EventLabelKt.TOGGLE_MONDAY;
import static com.framework.webengageconstant.EventLabelKt.TOGGLE_SATURDAY;
import static com.framework.webengageconstant.EventLabelKt.TOGGLE_SUNDAY;
import static com.framework.webengageconstant.EventLabelKt.TOGGLE_THURSDAY;
import static com.framework.webengageconstant.EventLabelKt.TOGGLE_TUESDAY;
import static com.framework.webengageconstant.EventLabelKt.TOGGLE_WEDNESDAY;
import static com.framework.webengageconstant.EventNameKt.BUSINESS_HOURS_SAVED;
import static com.framework.webengageconstant.EventNameKt.FRIDAY_MARKED_OFF;
import static com.framework.webengageconstant.EventNameKt.FRIDAY_MARKED_ON;
import static com.framework.webengageconstant.EventNameKt.MONDAY_MARKED_OFF;
import static com.framework.webengageconstant.EventNameKt.MONDAY_MARKED_ON;
import static com.framework.webengageconstant.EventNameKt.SATURDAY_MARKED_OFF;
import static com.framework.webengageconstant.EventNameKt.SATURDAY_MARKED_ON;
import static com.framework.webengageconstant.EventNameKt.STORE_TIMINGS;
import static com.framework.webengageconstant.EventNameKt.SUNDAY_MARKED_OFF;
import static com.framework.webengageconstant.EventNameKt.SUNDAY_MARKED_ON;
import static com.framework.webengageconstant.EventNameKt.THURSDAY_MARKED_OFF;
import static com.framework.webengageconstant.EventNameKt.THURSDAY_MARKED_ON;
import static com.framework.webengageconstant.EventNameKt.TUESDAY_MARKED_OFF;
import static com.framework.webengageconstant.EventNameKt.TUESDAY_MARKED_ON;
import static com.framework.webengageconstant.EventNameKt.WEDNESDAY_MARKED_OFF;
import static com.framework.webengageconstant.EventNameKt.WEDNESDAY_MARKED_ON;
import static com.framework.webengageconstant.EventValueKt.NULL;

/**
 * Created by Admin on 27-09-2017.
 */

public class BusinessHoursActivity extends AppCompatActivity implements View.OnTouchListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
  EditText etSunOpen, etSunClose, etMonOpen, etTueOpen, etWedOpen, etThuOpen, etFriOpen, etSatOpen, etSatClose, etFriClose, etMonClose, etThuClose, etTueClose, etWedClose;
  int currentId = NO_ID;
  Toolbar toolbar;
  TextView titleTextView;
  CustomButton saveButton;
  LinearLayout linearLayoutAllTime;
  CheckBox checkBoxAllTime;
  private UserSessionManager session;
  private SwitchCompat switchSun, switchMon, switchTue, switchWed, switchThu, switchFri, switchSat;
  Bus bus;
  ProgressDialog pd;
  private static final String TAG = "BusinessHoursActivity";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_business_hours_v2);
    Methods.isOnline(BusinessHoursActivity.this);
    session = new UserSessionManager(getApplicationContext(), BusinessHoursActivity.this);
    bus = BusProvider.getInstance().getBus();
    pd = ProgressDialog.show(this, "", getString(R.string.loading));
    pd.setCancelable(false);
    toolbar = (Toolbar) findViewById(R.id.app_bar);
    titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
    titleTextView.setText(R.string.business_timings_n);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    TextView saveText = (TextView) toolbar.findViewById(R.id.saveTextView);
    saveText.setVisibility(View.GONE);
    saveButton = findViewById(R.id.btn_save_info);
    saveButton.setOnClickListener(v -> {
      MixPanelController.track(EventKeysWL.SAVE_CONTACT_INFO, null);
      if (Methods.isOnline(BusinessHoursActivity.this)) {
        WebEngageController.trackEvent(BUSINESS_HOURS_SAVED, CLICK, session.getFpTag());
        uploadbusinessTimingsInfo();
      } else {
        Methods.snackbarNoInternet(BusinessHoursActivity.this);
      }
    });
    etSunOpen = (EditText) findViewById(R.id.et_sun_open);
    etSunClose = (EditText) findViewById(R.id.et_sun_close);

    etMonOpen = (EditText) findViewById(R.id.et_mon_open);
    etMonClose = (EditText) findViewById(R.id.et_mon_close);

    etTueOpen = (EditText) findViewById(R.id.et_tue_open);
    etTueClose = (EditText) findViewById(R.id.et_tue_close);

    etWedOpen = (EditText) findViewById(R.id.et_wed_open);
    etWedClose = (EditText) findViewById(R.id.et_wed_close);

    etThuOpen = (EditText) findViewById(R.id.et_thu_open);
    etThuClose = (EditText) findViewById(R.id.et_thu_close);

    etFriOpen = (EditText) findViewById(R.id.et_fri_open);
    etFriClose = (EditText) findViewById(R.id.et_fri_close);

    etSatOpen = (EditText) findViewById(R.id.et_sat_open);
    etSatClose = (EditText) findViewById(R.id.et_sat_close);

    switchSun = (SwitchCompat) findViewById(R.id.switch_sun);
    switchMon = (SwitchCompat) findViewById(R.id.switch_mon);
    switchTue = (SwitchCompat) findViewById(R.id.switch_tue);
    switchWed = (SwitchCompat) findViewById(R.id.switch_wed);
    switchThu = (SwitchCompat) findViewById(R.id.switch_thu);
    switchFri = (SwitchCompat) findViewById(R.id.switch_fri);
    switchSat = (SwitchCompat) findViewById(R.id.switch_sat);

    checkBoxAllTime = (CheckBox) findViewById(R.id.chbx_same_time);
    linearLayoutAllTime = (LinearLayout) findViewById(R.id.ll_same_time);
    checkBoxAllTime.setOnClickListener(this);

    switchSun.setOnClickListener(this);
    switchMon.setOnClickListener(this);
    switchTue.setOnClickListener(this);
    switchWed.setOnClickListener(this);
    switchThu.setOnClickListener(this);
    switchFri.setOnClickListener(this);
    switchSat.setOnClickListener(this);

    etSunOpen.setOnTouchListener(this);
    etMonOpen.setOnTouchListener(this);
    etTueOpen.setOnTouchListener(this);
    etWedOpen.setOnTouchListener(this);
    etThuOpen.setOnTouchListener(this);
    etFriOpen.setOnTouchListener(this);
    etSatOpen.setOnTouchListener(this);
    etSunClose.setOnTouchListener(this);
    etMonClose.setOnTouchListener(this);
    etTueClose.setOnTouchListener(this);
    etWedClose.setOnTouchListener(this);
    etThuClose.setOnTouchListener(this);
    etFriClose.setOnTouchListener(this);
    etSatClose.setOnTouchListener(this);
    getFPDetails(session.getFPID(), Constants.clientId, bus);

  }

  private void uploadbusinessTimingsInfo() {
    if (!isValidInput()) {
      Toast.makeText(this, this.getString(R.string.enter_valid_open_close_timing), Toast.LENGTH_SHORT).show();
      return;
    }
    String defaultTime = "00";
    String sundayTime,mondayTime, tuesdayTime, wednesdayTime, thursdayTime, fridayTime, saturdayTime;
    String[] profilesattr = new String[20];
    profilesattr[0] = "TIME";

    /*The clinic must be open for at least one day. If all days closed, then it means the business hours are not set.*/
    boolean openAtleastOneDayFlag = false;

    if (switchSun.isChecked()) {
      sundayTime = etSunOpen.getText().toString().trim() + "," + etSunClose.getText().toString().trim();
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME, etSunOpen.getText().toString().trim());
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME, etSunClose.getText().toString().trim());
      openAtleastOneDayFlag = true;
    } else {
      sundayTime = defaultTime + "," + defaultTime;
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME, "00");
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME, "00");
    }

    if (switchMon.isChecked()) {
      mondayTime = etMonOpen.getText().toString().trim() + "," + etMonClose.getText().toString().trim();
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME, etMonOpen.getText().toString().trim());
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME, etMonClose.getText().toString().trim());
      openAtleastOneDayFlag = true;
    } else {
      mondayTime = defaultTime + "," + defaultTime;
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME, "00");
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME, "00");
    }

    if (switchTue.isChecked()) {
      tuesdayTime = etTueOpen.getText().toString().trim() + "," + etTueClose.getText().toString().trim();
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME, etTueOpen.getText().toString().trim());
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME, etTueClose.getText().toString().trim());
      openAtleastOneDayFlag = true;
    } else {
      tuesdayTime = defaultTime + "," + defaultTime;
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME, "00");
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME, "00");
    }

    if (switchWed.isChecked()) {
      wednesdayTime = etWedOpen.getText().toString().trim() + "," + etWedClose.getText().toString().trim();
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME, etWedOpen.getText().toString().trim());
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME, etWedClose.getText().toString().trim());
      openAtleastOneDayFlag = true;
    } else {
      wednesdayTime = defaultTime + "," + defaultTime;
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME, "00");
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME, "00");
    }

    if (switchThu.isChecked()) {
      thursdayTime = etThuOpen.getText().toString().trim() + "," + etThuClose.getText().toString().trim();
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME, etThuOpen.getText().toString().trim());
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME, etThuClose.getText().toString().trim());
      openAtleastOneDayFlag = true;
    } else {
      thursdayTime = defaultTime + "," + defaultTime;
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME, "00");
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME, "00");
    }

    if (switchFri.isChecked()) {
      fridayTime = etFriOpen.getText().toString().trim() + "," + etFriClose.getText().toString().trim();
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME, etFriOpen.getText().toString().trim());
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME, etFriClose.getText().toString().trim());
      openAtleastOneDayFlag = true;
    } else {
      fridayTime = defaultTime + "," + defaultTime;
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME, "00");
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME, "00");
    }
    if (switchSat.isChecked()) {
      saturdayTime = etSatOpen.getText().toString().trim() + "," + etSatClose.getText().toString().trim();
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME, etSatOpen.getText().toString().trim());
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME, etSatClose.getText().toString().trim());
      openAtleastOneDayFlag = true;
    } else {
      saturdayTime = defaultTime + "," + defaultTime;
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME, "00");
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME, "00");
    }
    JSONObject offerObj = new JSONObject();
    try {
      offerObj.put("key", "TIMINGS");
      offerObj.put("value",
          sundayTime + "#" +
              mondayTime + "#" +
              tuesdayTime + "#" +
              wednesdayTime + "#" +
              thursdayTime + "#" +
              fridayTime + "#" +
              saturdayTime);

    } catch (JSONException e) {
      e.printStackTrace();
    }
    JSONArray ja = new JSONArray();
    JSONObject dayData = new JSONObject();
    ja.put(offerObj);
    try {
      dayData.put("clientId", Constants.clientId);
      dayData.put("fpTag", session.getFpTag());
      dayData.put("updates", ja);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    new UploadProfileAsyncTask(BusinessHoursActivity.this, dayData, profilesattr, isSuccess -> {
      if (isSuccess) saveButton.setVisibility(View.GONE);
    }).execute();
    session.setBusinessHours(openAtleastOneDayFlag);
    onBusinessHourAddedOrUpdated(openAtleastOneDayFlag);
  }

  private boolean isValidInput() {
    if (switchSun.isChecked()) {
      if (etSunOpen.getText().toString().equals("00:00") || etSunClose.getText().toString().equals("00:00"))
        return false;
    }
    if (switchMon.isChecked()) {
      if (etMonOpen.getText().toString().equals("00:00") || etMonClose.getText().toString().equals("00:00"))
        return false;
    }
    if (switchTue.isChecked()) {
      if (etTueOpen.getText().toString().equals("00:00") || etTueClose.getText().toString().equals("00:00"))
        return false;
    }
    if (switchWed.isChecked()) {
      if (etWedOpen.getText().toString().equals("00:00") || etWedClose.getText().toString().equals("00:00"))
        return false;
    }
    if (switchThu.isChecked()) {
      if (etThuOpen.getText().toString().equals("00:00") || etThuClose.getText().toString().equals("00:00"))
        return false;
    }
    if (switchFri.isChecked()) {
      if (etFriOpen.getText().toString().equals("00:00") || etFriClose.getText().toString().equals("00:00"))
        return false;
    }
    if (switchSat.isChecked()) {
      if (etSatOpen.getText().toString().equals("00:00") || etSatClose.getText().toString().equals("00:00"))
        return false;
    }
    return true;
  }

  private void onBusinessHourAddedOrUpdated(Boolean isAdded) {
    FirestoreManager instance = FirestoreManager.INSTANCE;
    if (instance.getDrScoreData() != null && instance.getDrScoreData().getMetricdetail() != null) {
      instance.getDrScoreData().getMetricdetail().setBoolean_add_business_hours(isAdded);
      instance.updateDocument();
    }
  }

  private void updateTimings() {
    boolean openOneFlag = false;
    if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME).toLowerCase().endsWith("am")
        || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME).toLowerCase().endsWith("pm")) {

      etMonOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME));
      etMonClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME));
      openOneFlag = true;
    } else {
      switchMon.setChecked(false);
      setTextTimeOnSwitch(R.id.et_mon_open, R.id.et_mon_close, 0, 0, 0, 0);
    }

    if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME).toLowerCase().endsWith("am")
        || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME).toLowerCase().endsWith("pm")) {
      etTueOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME));
      etTueClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME));
      openOneFlag = true;
    } else {
      switchTue.setChecked(false);
      setTextTimeOnSwitch(R.id.et_tue_open, R.id.et_tue_close, 0, 0, 0, 0);
    }
    if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME).toLowerCase().endsWith("am")
        || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME).toLowerCase().endsWith("pm")) {

      etWedOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME));
      etWedClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME));
      openOneFlag = true;
    } else {
      switchWed.setChecked(false);
      setTextTimeOnSwitch(R.id.et_wed_open, R.id.et_wed_close, 0, 0, 0, 0);
    }
    if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME).toLowerCase().endsWith("am")
        || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME).toLowerCase().endsWith("pm")) {
      etThuOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME));
      etThuClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME));
      openOneFlag = true;
    } else {
      switchThu.setChecked(false);
      setTextTimeOnSwitch(R.id.et_thu_open, R.id.et_thu_close, 0, 0, 0, 0);
    }
    if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME).toLowerCase().endsWith("am")
        || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME).toLowerCase().endsWith("pm")) {

      etFriOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME));
      etFriClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME));
      openOneFlag = true;
    } else {
      switchFri.setChecked(false);
      setTextTimeOnSwitch(R.id.et_fri_open, R.id.et_fri_close, 0, 0, 0, 0);
    }
    if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME).toLowerCase().endsWith("am")
        || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME).toLowerCase().endsWith("pm")) {
      etSatOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME));
      etSatClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME));
      openOneFlag = true;
    } else {
      switchSat.setChecked(false);
      setTextTimeOnSwitch(R.id.et_sat_open, R.id.et_sat_close, 0, 0, 0, 0);
    }
    if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME).toLowerCase().endsWith("am")
        || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME).toLowerCase().endsWith("pm")) {
      etSunOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME));
      etSunClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME));
      openOneFlag = true;
    } else {
      switchSun.setChecked(false);
      setTextTimeOnSwitch(R.id.et_sun_open, R.id.et_sun_close, 0, 0, 0, 0);
    }

    onBusinessHourAddedOrUpdated(openOneFlag);
  }

  private void timePicker() {

       /* TimePickerDialog dialog = TimePickerDialog.newInstance(this,0,0,false);
        dialog.setThemeDark(false);
        dialog.show(getFragmentManager(), "Timepickerdialog");*/
    View parentView = LayoutInflater.from(this).inflate(R.layout.dialog_with_spinner, null);
    final Spinner openSpinner = (Spinner) parentView.findViewById(R.id.spinner_all_open);
    final Spinner closeSpinner = (Spinner) parentView.findViewById(R.id.spinner_all_close);
    final CheckBox checkBox = (CheckBox) parentView.findViewById(R.id.chbx_same_time);

    openSpinner.setOnItemSelectedListener(this);
    closeSpinner.setOnItemSelectedListener(this);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.business_hours_arrays, R.layout.layout_simple_text_item);
    openSpinner.setAdapter(adapter);
    closeSpinner.setAdapter(adapter);
    MaterialDialog dialog = new MaterialDialog.Builder(this)
        .customView(parentView, true)
        .autoDismiss(false)
        .title("Select time")
        .positiveText("Set")
        .negativeText("Cancel")
        .positiveColorRes(R.color.colorAccentLight)
        .negativeColorRes(R.color.black_4a4a4a)
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            setSelectedTime(checkBox.isChecked(), openSpinner.getSelectedItem().toString(), closeSpinner.getSelectedItem().toString());
            dialog.dismiss();
            currentId = NO_ID;
            saveButton.setVisibility(View.VISIBLE);

          }
        })
        .onNegative(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            currentId = NO_ID;
            dialog.dismiss();
          }
        })
        .build();
    dialog.show();

  }

  private void setSelectedTime(boolean isChecked, String openTime, String closeTime) {
    if (isChecked) {
      etMonClose.setText(closeTime);
      etMonOpen.setText(openTime);
      etTueClose.setText(closeTime);
      etTueOpen.setText(openTime);
      etWedClose.setText(closeTime);
      etWedOpen.setText(openTime);
      etThuClose.setText(closeTime);
      etThuOpen.setText(openTime);
      etFriClose.setText(closeTime);
      etFriOpen.setText(openTime);
      etSatClose.setText(closeTime);
      etSatOpen.setText(openTime);
      etSunClose.setText(closeTime);
      etSunOpen.setText(openTime);
    } else {
      switch (currentId) {
        case 0:
          etMonClose.setText(closeTime);
          etMonOpen.setText(openTime);
          break;
        case 1:
          etTueClose.setText(closeTime);
          etTueOpen.setText(openTime);
          break;
        case 2:
          etWedClose.setText(closeTime);
          etWedOpen.setText(openTime);
          break;
        case 3:
          etThuClose.setText(closeTime);
          etThuOpen.setText(openTime);
          break;
        case 4:
          etFriClose.setText(closeTime);
          etFriOpen.setText(openTime);
          break;
        case 5:
          etSatClose.setText(closeTime);
          etSatOpen.setText(openTime);
          break;
        case 6:
          etSunClose.setText(closeTime);
          etSunOpen.setText(openTime);
          break;
      }
    }
  }

  @Override
  public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
    String s = convertToString(hourOfDay, minute);
    ((EditText) findViewById(currentId)).setText(s);
    currentId = NO_ID;
  }

  private String convertToString(int hourOfDay, int minute) {
    return String.format(Locale.ENGLISH, "%02d:%02d", hourOfDay > 12 ? hourOfDay - 12 : hourOfDay, minute);
  }

  /*private String ConvertToTime(String time){
      String hours = time.substring(0,time.indexOf(":")+1);
      String minutes = time.substring(time.indexOf(":"),time.indexOf(" "));
      if (time.toLowerCase().endsWith("am")){

      }else if(time.toLowerCase().endsWith("pm")){

      }
      Integer.parseInt(hours)
  }*/
  @Override
  public boolean onTouch(View v, MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      switch (v.getId()) {
        case R.id.et_mon_open:
        case R.id.et_mon_close:
          currentId = 0;
          break;
        case R.id.et_tue_open:
        case R.id.et_tue_close:
          currentId = 1;
          break;
        case R.id.et_wed_open:
        case R.id.et_wed_close:
          currentId = 2;
          break;
        case R.id.et_thu_open:
        case R.id.et_thu_close:
          currentId = 3;
          break;
        case R.id.et_fri_open:
        case R.id.et_fri_close:
          currentId = 4;
          break;
        case R.id.et_sat_open:
        case R.id.et_sat_close:
          currentId = 5;
          break;
        case R.id.et_sun_open:
        case R.id.et_sun_close:
          currentId = 6;
          break;
      }
      timePicker();
      return true;
    }
    return false;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_business__hours, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == android.R.id.home) {
      //  NavUtils.navigateUpFromSameTask(this);
      // getSupportFragmentManager().popBackStack();
      finish();
      overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    return super.onOptionsItemSelected(item);
  }

  private void setTextTimeOnSwitch(int openId, int closeId, int openHoursTime, int openMinuteTime, int closeHoursTime, int closeMinuteTime) {
    EditText openEdt = ((EditText) findViewById(openId));
    EditText closeEdt = ((EditText) findViewById(closeId));
    if (openId != -1) openEdt.setText(convertToString(openHoursTime, openMinuteTime));
    if (closeId != -1) closeEdt.setText(convertToString(closeHoursTime, closeMinuteTime));
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.switch_sun:
        if (!switchSun.isChecked()) {
          WebEngageController.trackEvent(SUNDAY_MARKED_OFF, TOGGLE_SUNDAY, NULL);
          setTextTimeOnSwitch(R.id.et_sun_open, R.id.et_sun_close, 0, 0, 0, 0);
        } else {
          WebEngageController.trackEvent(SUNDAY_MARKED_ON, TOGGLE_SUNDAY, NULL);
        }
        saveButton.setVisibility(View.VISIBLE);
        break;
      case R.id.switch_mon:
        if (!switchMon.isChecked()) {
          WebEngageController.trackEvent(MONDAY_MARKED_OFF, TOGGLE_MONDAY, NULL);
          setTextTimeOnSwitch(R.id.et_mon_open, R.id.et_mon_close, 0, 0, 0, 0);
        } else {
          WebEngageController.trackEvent(MONDAY_MARKED_ON, TOGGLE_MONDAY, NULL);
        }
        saveButton.setVisibility(View.VISIBLE);
        break;
      case R.id.switch_tue:
        if (!switchTue.isChecked()) {
          setTextTimeOnSwitch(R.id.et_tue_open, R.id.et_tue_close, 0, 0, 0, 0);
          WebEngageController.trackEvent(TUESDAY_MARKED_OFF, TOGGLE_TUESDAY, NULL);
        } else {
          WebEngageController.trackEvent(TUESDAY_MARKED_ON, TOGGLE_TUESDAY, NULL);
        }
        saveButton.setVisibility(View.VISIBLE);
        break;
      case R.id.switch_wed:
        if (!switchWed.isChecked()) {
          setTextTimeOnSwitch(R.id.et_wed_open, R.id.et_wed_close, 0, 0, 0, 0);
          WebEngageController.trackEvent(WEDNESDAY_MARKED_OFF, TOGGLE_WEDNESDAY, NULL);
        } else {
          WebEngageController.trackEvent(WEDNESDAY_MARKED_ON, TOGGLE_WEDNESDAY, NULL);
        }
        saveButton.setVisibility(View.VISIBLE);
        break;
      case R.id.switch_thu:
        if (!switchThu.isChecked()) {
          setTextTimeOnSwitch(R.id.et_thu_open, R.id.et_thu_close, 0, 0, 0, 0);
          WebEngageController.trackEvent(THURSDAY_MARKED_OFF, TOGGLE_THURSDAY, NULL);
        } else {
          WebEngageController.trackEvent(THURSDAY_MARKED_ON, TOGGLE_THURSDAY, NULL);
        }
        saveButton.setVisibility(View.VISIBLE);
        break;
      case R.id.switch_fri:
        if (!switchFri.isChecked()) {
          setTextTimeOnSwitch(R.id.et_fri_open, R.id.et_fri_close, 0, 0, 0, 0);
          WebEngageController.trackEvent(FRIDAY_MARKED_OFF, TOGGLE_FRIDAY, NULL);
        } else {
          WebEngageController.trackEvent(FRIDAY_MARKED_ON, TOGGLE_FRIDAY, NULL);
        }
        saveButton.setVisibility(View.VISIBLE);
        break;
      case R.id.switch_sat:
        if (!switchSat.isChecked()) {
          setTextTimeOnSwitch(R.id.et_sat_open, R.id.et_sat_close, 0, 0, 0, 0);
          WebEngageController.trackEvent(SATURDAY_MARKED_OFF, TOGGLE_SATURDAY, NULL);
        } else {
          WebEngageController.trackEvent(SATURDAY_MARKED_ON, TOGGLE_SATURDAY, NULL);
        }
        saveButton.setVisibility(View.VISIBLE);
        break;
      case R.id.chbx_same_time:
        if (checkBoxAllTime.isChecked()) {
          WebEngageController.trackEvent(STORE_TIMINGS, CLICKING_USE_SAME_TIME_FOR_WHOLE_WEEK, NULL);
          linearLayoutAllTime.setVisibility(View.VISIBLE);
          findViewById(R.id.et_all_close).setOnTouchListener(BusinessHoursActivity.this);
          findViewById(R.id.et_all_open).setOnTouchListener(BusinessHoursActivity.this);
        }
        saveButton.setVisibility(View.VISIBLE);
        break;
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {

  }

  private void getFPDetails(String fpId, String clientId, Bus bus) {
    pd.show();
    new Get_FP_Details_Service(this, fpId, clientId, bus);
  }

  @Subscribe
  public void post_getFPDetails(Get_FP_Details_Event response) {
    Log.i(TAG, "post_getFPDetails: ");
    runOnUiThread(() -> {
      if (pd != null) pd.dismiss();
    });
    updateTimings();
  }

  @Subscribe
  public void post_Error(String message) {
    runOnUiThread(() -> {
      if (pd != null) pd.dismiss();
      Toast.makeText(this, "Business hour loading error!", Toast.LENGTH_SHORT).show();
    });

  }

  @Override
  protected void onResume() {
    super.onResume();
    try{
      bus.register(this);
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    try{
      bus.unregister(this);
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    try{
      bus.unregister(this);
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}
