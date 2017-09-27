package com.nowfloats.BusinessProfile.UI.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nowfloats.BusinessProfile.UI.API.UploadProfileAsyncTask;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by Admin on 27-09-2017.
 */

public class BusinessHoursActivity extends AppCompatActivity implements View.OnTouchListener, TimePickerDialog.OnTimeSetListener {
    EditText etSunOpen,etSunClose,etMonOpen,etTueOpen,etWedOpen,etThuOpen,etFriOpen,etSatOpen,etSatClose,etFriClose,etMonClose,etThuClose,etTueClose,etWedClose;
    int currentId = View.NO_ID;
    Toolbar toolbar;
    TextView titleTextView,saveTextView;
    private UserSessionManager session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_hours_v2);
        Methods.isOnline(BusinessHoursActivity.this);
        session = new UserSessionManager(getApplicationContext(),BusinessHoursActivity.this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText("Business Hours");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        saveTextView = (TextView) toolbar.findViewById(R.id.saveTextView);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MixPanelController.track(EventKeysWL.SAVE_CONTACT_INFO, null);
                uploadbusinessTimingsInfo();

                //Methods.showSnackBarNegative(BusinessHoursActivity.this,getResources().getString(R.string.select_business_day));
            }
        });
        etSunOpen = (EditText) findViewById(R.id.et_sun_open);
        etMonOpen = (EditText) findViewById(R.id.et_mon_open);
        etTueOpen = (EditText) findViewById(R.id.et_tue_open);
        etWedOpen = (EditText) findViewById(R.id.et_wed_open);
        etThuOpen = (EditText) findViewById(R.id.et_thu_open);
        etFriOpen = (EditText) findViewById(R.id.et_fri_open);
        etSatOpen = (EditText) findViewById(R.id.et_sat_open);
        etSunClose = (EditText) findViewById(R.id.et_sun_close);
        etMonClose = (EditText) findViewById(R.id.et_mon_close);
        etTueClose = (EditText) findViewById(R.id.et_tue_close);
        etWedClose = (EditText) findViewById(R.id.et_wed_close);
        etThuClose = (EditText) findViewById(R.id.et_thu_close);
        etFriClose = (EditText) findViewById(R.id.et_fri_close);
        etSatClose = (EditText) findViewById(R.id.et_sat_close);
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
        updateTimings();
    }

    private void uploadbusinessTimingsInfo(){
        String defaultTime = "00";
        String mondayTime=defaultTime,tuesdayTime=defaultTime,wednesdayTime=defaultTime,thursdayTime=defaultTime,fridayTime=defaultTime,saturdayTime=defaultTime,sundayTime=defaultTime;
        String[] profilesattr =new String[20];
        profilesattr[0] = "TIME";
        JSONArray ja = new JSONArray();
        JSONObject dayData = new JSONObject();

        if(etMonClose.getText().toString().equalsIgnoreCase("closed")||etMonOpen.getText().toString().equalsIgnoreCase("closed")) {
            mondayTime = etMonOpen.getText().toString().trim()+","+etMonClose.getText().toString().trim();
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME, etMonOpen.getText().toString().trim());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME,etMonClose.getText().toString().trim());
        }else{
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME,"00");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME,"00");
        }

        if(etTueClose.getText().toString().equalsIgnoreCase("closed")||etTueOpen.getText().toString().equalsIgnoreCase("closed")) {
            tuesdayTime = etTueOpen.getText().toString().trim()+","+etTueClose.getText().toString().trim();
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME,etTueOpen.getText().toString().trim());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME,etTueClose.getText().toString().trim());
        }else{
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME,"00");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME,"00");
        }

        if(etWedClose.getText().toString().equalsIgnoreCase("closed")||etWedOpen.getText().toString().equalsIgnoreCase("closed")) {
            wednesdayTime =  etWedOpen.getText().toString().trim()+","+etWedClose.getText().toString().trim();
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME,etWedOpen.getText().toString().trim());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME,etWedClose.getText().toString().trim());
        }else{
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME,"00");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME,"00");
        }

        if(etThuClose.getText().toString().equalsIgnoreCase("closed")||etThuOpen.getText().toString().equalsIgnoreCase("closed")) {
            thursdayTime = etThuOpen.getText().toString().trim()+","+etThuClose.getText().toString().trim();
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME,etThuOpen.getText().toString().trim());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME,etThuClose.getText().toString().trim());
        }else{
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME,"00");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME,"00");
        }

        if(etFriClose.getText().toString().equalsIgnoreCase("closed")||etFriOpen.getText().toString().equalsIgnoreCase("closed")) {
            fridayTime = etFriOpen.getText().toString().trim()+","+etFriClose.getText().toString().trim();
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME,etFriOpen.getText().toString().trim());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME,etFriClose.getText().toString().trim());
        }else{
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME,"00");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME,"00");
        }
        if(etSatClose.getText().toString().equalsIgnoreCase("closed")||etSatOpen.getText().toString().equalsIgnoreCase("closed")) {
            saturdayTime =  etSatOpen.getText().toString().trim()+","+etSatClose.getText().toString().trim();
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME,etSatOpen.getText().toString().trim());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME,etSatClose.getText().toString().trim());
        }else{
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME,"00");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME,"00");
        }
        if(etSunClose.getText().toString().equalsIgnoreCase("closed")||etSunOpen.getText().toString().equalsIgnoreCase("closed")) {
            sundayTime =  etSunOpen.getText().toString().trim()+","+etSunClose.getText().toString().trim();
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME,etSunOpen.getText().toString().trim());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME,etSunClose.getText().toString().trim());
        }else{
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME,"00");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME,"00");
        }
        JSONObject offerObj = new JSONObject();
        try {
            //offerObj.put("clientId", Constants.clientId);
            //offerObj.put("fpTag", (Constants.StoreTag).toUpperCase());
            offerObj.put("key","TIMINGS");
            offerObj.put("value",
                    sundayTime+"#" +
                            mondayTime+"#" +
                            tuesdayTime+"#" +
                            wednesdayTime+"#" +
                            thursdayTime+"#" +
                            fridayTime+"#" +
                            saturdayTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ja.put(offerObj);
        try {
            dayData.put("clientId", Constants.clientId);
            dayData.put("fpTag", (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)).toUpperCase());
            dayData.put("updates", ja);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UploadProfileAsyncTask upa = new UploadProfileAsyncTask(BusinessHoursActivity.this,dayData,profilesattr);
        upa.execute();
    }

    private void updateTimings(){
        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME).trim().length()==0)
        {
            etMonOpen.setText("closed");
            etMonClose.setText("closed");
        } else {

            etMonOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME));
            etMonClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME));
        }

        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME).trim().length()==0)
        {
            etTueOpen.setText("closed");
            etTueOpen.setText("closed");
        } else {

            etTueOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME));
            etTueClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME));
        }

        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME).trim().length()==0)
        {
            etWedOpen.setText("closed");
            etWedClose.setText("closed");
        } else {

            etWedOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME));
            etWedClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME));
        }

        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME).trim().length()==0)
        {
            etThuOpen.setText("closed");
            etThuClose.setText("closed");
        } else {

            etThuOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME));
            etThuClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME));
        }

        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME).trim().length()==0)
        {
            etFriOpen.setText("closed");
            etFriClose.setText("closed");
        } else {

            etFriOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME));
            etFriClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME));
        }

        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME).trim().length()==0)
        {
            etSatOpen.setText("closed");
            etSatClose.setText("closed");
        } else {

            etSatOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME));
            etSatClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME));
        }

        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME).trim().length()==0)
        {
            etSunOpen.setText("closed");
            etSunClose.setText("closed");
        } else {
            etSunOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME));
            etSunClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME));
        }

    }
    private void timePicker(){
        TimePickerDialog dialog = TimePickerDialog.newInstance(this,0,0,false);
        dialog.setThemeDark(false);
        dialog.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String s = convertToString(hourOfDay,minute);
        ((EditText)findViewById(currentId)).setText(s);
        currentId = View.NO_ID;
    }

    private String convertToString(int hourOfDay,int minute){
        return String.format(Locale.ENGLISH,"%02d:%02d %s",hourOfDay>12?hourOfDay-12:hourOfDay,minute,hourOfDay<12?"AM":"PM");
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
                case R.id.et_sun_open:
                case R.id.et_sun_close:
                case R.id.et_mon_open:
                case R.id.et_mon_close:
                case R.id.et_tue_open:
                case R.id.et_tue_close:
                case R.id.et_wed_open:
                case R.id.et_wed_close:
                case R.id.et_thu_open:
                case R.id.et_thu_close:
                case R.id.et_fri_open:
                case R.id.et_fri_close:
                case R.id.et_sat_open:
                case R.id.et_sat_close:
                    currentId = v.getId();
                    timePicker();
                    break;
            }
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

        if(id==android.R.id.home){
            //  NavUtils.navigateUpFromSameTask(this);
            // getSupportFragmentManager().popBackStack();
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }


        return super.onOptionsItemSelected(item);
    }
}
