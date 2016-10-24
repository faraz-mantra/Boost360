package com.nowfloats.BusinessProfile.UI.UI;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.util.Calendar;

public class Business_Hours_Activity extends ActionBarActivity implements TimePickerDialog.OnTimeSetListener {

    private Toolbar toolbar;
    private TextView titleTextView,openTimeTextView,closeTimeTextView;
    Button openTime,closeTime,closedOn;
    String defaultTime = "00,00";
    UserSessionManager session;

    int daysCheckBoxes [] = {R.id.business_hours_day1_checkBox,
            R.id.business_hours_day2_checkBox,
            R.id.business_hours_day3_checkBox,
            R.id.business_hours_day4_checkBox,
            R.id.business_hours_day5_checkBox,
            R.id.business_hours_day6_checkBox,
            R.id.business_hours_day7_checkBox};
    private boolean openTime_Session = false;

    private boolean mondayChecked,tuesdayChecked,wednesdayChecked,thursdayChecked,fridayChecked,saturdayChecked,sundayChecked;

    private CheckBox mondayCheckBox,tuesdayCheckBox,wednesdayCheckBox,thursdayCheckBox,fridayCheckBox,saturdayCheckBox,sundayCheckBox;

    public static TextView saveTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_hours);
        Methods.isOnline(Business_Hours_Activity.this);
        session = new UserSessionManager(getApplicationContext(),Business_Hours_Activity.this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText("Business Hours");

        openTimeTextView = (TextView) findViewById(R.id.openTime);
        closeTimeTextView = (TextView) findViewById(R.id.closeTime);

        mondayCheckBox = (CheckBox) findViewById(R.id.business_hours_day1_checkBox);
        tuesdayCheckBox = (CheckBox) findViewById(R.id.business_hours_day2_checkBox);
        wednesdayCheckBox = (CheckBox) findViewById(R.id.business_hours_day3_checkBox);
        thursdayCheckBox = (CheckBox) findViewById(R.id.business_hours_day4_checkBox);
        fridayCheckBox = (CheckBox) findViewById(R.id.business_hours_day5_checkBox);
        saturdayCheckBox = (CheckBox) findViewById(R.id.business_hours_day6_checkBox);
        sundayCheckBox = (CheckBox) findViewById(R.id.business_hours_day7_checkBox);

        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME).trim().length()==0)
        {
            mondayCheckBox.setChecked(false);
        } else {
            mondayCheckBox.setChecked(true);
            openTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME));
            closeTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME));
        }

        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME).trim().length()==0)
        {
            tuesdayCheckBox.setChecked(false);
        } else {
            tuesdayCheckBox.setChecked(true);
            openTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME));
            closeTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME));
        }

        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME).trim().length()==0)
        {
            wednesdayCheckBox.setChecked(false);
        } else {
            wednesdayCheckBox.setChecked(true);
            openTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME));
            closeTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME));
        }

        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME).trim().length()==0)
        {
            thursdayCheckBox.setChecked(false);
        } else {
            thursdayCheckBox.setChecked(true);
            openTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME));
            closeTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME));
        }

        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME).trim().length()==0)
        {
            fridayCheckBox.setChecked(false);
        } else {
            fridayCheckBox.setChecked(true);
            openTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME));
            closeTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME));
        }

        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME).trim().length()==0)
        {
            saturdayCheckBox.setChecked(false);
        } else {
            saturdayCheckBox.setChecked(true);
            openTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME));
            closeTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME));
        }

        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME).equals("00")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME).trim().length()==0)
        {
            sundayCheckBox.setChecked(false);
        } else {
            sundayCheckBox.setChecked(true);
            openTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME));
            closeTimeTextView.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME));
        }

        if (openTimeTextView.getText().toString().trim().length()==0)
            openTimeTextView.setText("00");
        if (closeTimeTextView.getText().toString().trim().length()==0)
            closeTimeTextView.setText("00");

        session.storeStartTime(openTimeTextView.getText().toString());
        session.storeEndTime(closeTimeTextView.getText().toString());

       // initializeHours();
        saveTextView = (TextView) toolbar.findViewById(R.id.saveTextView);

        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MixPanelController.track(EventKeysWL.SAVE_CONTACT_INFO, null);
            if(mondayCheckBox.isChecked() || tuesdayCheckBox.isChecked() || wednesdayCheckBox.isChecked() ||
                    thursdayCheckBox.isChecked() || fridayCheckBox.isChecked() || saturdayCheckBox.isChecked()
                    || sundayCheckBox.isChecked() )
            {
                saveCheckPreferences();
                uploadbusinessTimingsInfo();
            }
                else
                Methods.showSnackBarNegative(Business_Hours_Activity.this,"Please select a business day");
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        mondayCheckBox.setOncheckListener(new RadioGroup.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // update your model (or other business logic) based on isChecked
//            }
//        });

        openTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTime_Session = true ;
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        Business_Hours_Activity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                );
                tpd.setThemeDark(false);
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });

        closeTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTime_Session = false ;
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        Business_Hours_Activity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                );
                tpd.setThemeDark(false);
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });



        //openTime = (Button)findViewById(R.id.business_hours_time_open_button);


    }

    private void initializeHours() {

        openTimeTextView.setText(session.getStartTime());
        closeTimeTextView.setText(session.getEndTime());

        if(session.getMonayChecked())
            mondayCheckBox.setChecked(true);
        else
            mondayCheckBox.setChecked(false);

        if(session.getTuesdayChecked())
            tuesdayCheckBox.setChecked(true);
        else
            tuesdayCheckBox.setChecked(false);

        if(session.getWednesdayChecked())
            wednesdayCheckBox.setChecked(true);
        else
            wednesdayCheckBox.setChecked(false);

        if(session.getThursdayChecked())
            thursdayCheckBox.setChecked(true);
        else
            thursdayCheckBox.setChecked(false);

        if(session.getFridayChecked())
            fridayCheckBox.setChecked(true);
        else
            fridayCheckBox.setChecked(false);

        if(session.getSaturdayChecked())
            saturdayCheckBox.setChecked(true);
        else
            saturdayCheckBox.setChecked(false);

        if(session.getSundayChecked())
            sundayCheckBox.setChecked(true);
        else
            sundayCheckBox.setChecked(false);

    }

    private void saveCheckPreferences() {
        if(mondayCheckBox.isChecked())
            session.storeMondayChecked(true);
        else
            session.storeMondayChecked(false);
        if(tuesdayCheckBox.isChecked())
            session.storeTuesdayChecked(true);
        else
            session.storeTuesdayChecked(false);
        if(wednesdayCheckBox.isChecked())
            session.storeWednesdayChecked(true);
        else
            session.storeWednesdayChecked(false);
        if(thursdayCheckBox.isChecked())
            session.storeThursdayChecked(true);
        else
            session.storeThursdayChecked(false);
        if(fridayCheckBox.isChecked())
            session.storeFridayChecked(true);
        else
            session.storeFridayChecked(false);

        if(saturdayCheckBox.isChecked())
            session.storeSaturdayChecked(true);
        else
            session.storeSaturdayChecked(false);
        if(sundayCheckBox.isChecked())
            session.storeSundayChecked(true);
        else
            session.storeSundayChecked(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        initializeHours();

       // mondayCheckBox.setChecked(true);
//        tuesdayCheckBox.setChecked(session.getTuesdayChecked());
//        wednesdayCheckBox.setChecked(session.getWednesdayChecked());
//        thursdayCheckBox.setChecked(session.getThursdayChecked());
//        fridayCheckBox.setChecked(session.getFridayChecked());
//        saturdayCheckBox.setChecked(session.getSaturdayChecked());
//        sundayCheckBox.setChecked(session.getSundayChecked());
//
//        openTimeTextView.setText(session.getStartTime());
//        closeTimeTextView.setText(session.getEndTime());

     //   mondayCheckBox.invalidate();
//        if(session.getMonayChecked())
//        {
//            mondayCheckBox.setChecked(true);
//        }
//        if(Constants.IS_TUESDAY_CHECKED)
//        {
//             tuesdayCheckBox.setChecked(true);
//        }
//        if(Constants.IS_WEDNESDAY_CHECKED)
//        {
//wednesdayCheckBox.setChecked(true);
//        }
//        if(Constants.IS_THURSDAY_CHECKED)
//        {
//thursdayCheckBox.setChecked(true);
//        }
//        if(Constants.IS_FRIDAY_CHECKED)
//        {
//fridayCheckBox.setChecked(true);
//        }
//        if(Constants.IS_SATURDAY_CHECKED)
//        {
//saturdayCheckBox.setChecked(true);
//        }
//        if(Constants.IS_SUNDAY_CHECKED)
//        {
//sundayCheckBox.setChecked(true);
//        }


    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.business_hours_day1_checkBox:
                if (checked) {
                    session.storeMondayChecked(true);
                    Constants.IS_MONDAY_CHECKED = true;
                    mondayChecked = true;

                }
                else {
                    mondayChecked = false;
                    session.storeMondayChecked(false);
                }
                break;
            case R.id.business_hours_day2_checkBox:
                if (checked) {
                    tuesdayChecked = true;
                    session.storeTuesdayChecked(true);
                    Constants.IS_TUESDAY_CHECKED = true ;
                }
                else {
                    tuesdayChecked = false;
                    session.storeTuesdayChecked(false);
                }
                break;
            case R.id.business_hours_day3_checkBox:
                if (checked) {
                    wednesdayChecked = true;
                    session.storeWednesdayChecked(true);
                    Constants.IS_WEDNESDAY_CHECKED = true ;
                }
                else {
                    wednesdayChecked = false;
                    session.storeWednesdayChecked(false);
                }
                break;
            case R.id.business_hours_day4_checkBox:
                if (checked) {
                    thursdayChecked = true;
                    session.storeThursdayChecked(true);
                    Constants.IS_THURSDAY_CHECKED = true ;
                }
                else {
                    thursdayChecked = false;
                    session.storeThursdayChecked(false);
                }
                break;
            case R.id.business_hours_day5_checkBox:
                if (checked) {
                    fridayChecked = true;
                    Constants.IS_FRIDAY_CHECKED = true ;
                    session.storeFridayChecked(true);
                }
                else {
                    fridayChecked = false;
                    session.storeFridayChecked(false);
                }
                break;
            case R.id.business_hours_day6_checkBox:
                if (checked) {
                    saturdayChecked = true;
                    session.storeSaturdayChecked(true);
                    Constants.IS_SATURDAY_CHECKED = true ;
                }
                else {
                    saturdayChecked = false;
                    session.storeSaturdayChecked(false);
                }
                break;
            case R.id.business_hours_day7_checkBox:
                if (checked) {
                    sundayChecked = true;
                    session.storeSundayChecked(true);
                    Constants.IS_SUNDAY_CHECKED = true ;
                }
                else {
                    sundayChecked = false;
                    session.storeSundayChecked(false);
                }
                break;
        }
    }

    public void uploadbusinessTimingsInfo()
    {
        String mondayTime=defaultTime,tuesdayTime=defaultTime,wednesdayTime=defaultTime,thursdayTime=defaultTime,fridayTime=defaultTime,saturdayTime=defaultTime,sundayTime=defaultTime;
        String[] profilesattr =new String[20];
        profilesattr[0] = "TIME";
        JSONArray ja = new JSONArray();
        JSONObject dayData = new JSONObject();

        if(mondayCheckBox.isChecked()) {
             mondayTime = session.getStartTime()+","+session.getEndTime();
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME,session.getStartTime());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME,session.getEndTime());
        }else{
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME,"00");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME,"00");
        }

        if(tuesdayCheckBox.isChecked()) {
             tuesdayTime = session.getStartTime()+","+session.getEndTime();
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME,session.getStartTime());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME,session.getEndTime());
        }else{
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME,"00");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME,"00");
        }

        if(wednesdayCheckBox.isChecked()) {
            wednesdayTime = session.getStartTime()+","+session.getEndTime();
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME,session.getStartTime());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME,session.getEndTime());
        }else{
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME,"00");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME,"00");
        }

        if(thursdayCheckBox.isChecked()) {
            thursdayTime = session.getStartTime()+","+session.getEndTime();
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME,session.getStartTime());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME,session.getEndTime());
        }else{
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME,"00");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME,"00");
        }

        if(fridayCheckBox.isChecked()) {
            fridayTime = session.getStartTime()+","+session.getEndTime();
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME,session.getStartTime());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME,session.getEndTime());
        }else{
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME,"00");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME,"00");
        }
        if(saturdayCheckBox.isChecked()) {
            saturdayTime =  session.getStartTime()+","+session.getEndTime();;
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME,session.getStartTime());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME,session.getEndTime());
        }else{
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME,"00");
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME,"00");
        }
        if(sundayCheckBox.isChecked()) {
            sundayTime =  session.getStartTime()+","+session.getEndTime();;
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME,session.getStartTime());
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME,session.getEndTime());
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


        UploadProfileAsyncTask upa = new UploadProfileAsyncTask(Business_Hours_Activity.this,dayData,profilesattr);
        upa.execute();

       // offerObj.put("TIMINGS","00,00#9:00 AM,6:00 PM#9:00 AM,6:00 PM#9:00 AM,6:00 PM#9:00 AM,6:00 PM#9:00 AM,6:00 PM#00,00")
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

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i2) {
        String time = "";

        if(CountNumberOfDigits(i) == 1  && CountNumberOfDigits(i2) == 2){
            if (i < 12) {

                time = "0"+i % 12 + ":" + i2 + " AM";
            } else
                time = "0"+i % 12 + ":" + i2 + " PM";
        } else if(CountNumberOfDigits(i) == 2  && CountNumberOfDigits(i2) == 1) {

            if (i < 12) {

                time = i % 12 + ":0" + i2 + " AM";
            } else
                time = i % 12 + ":0" + i2 + " PM";

        } else if(CountNumberOfDigits(i) == 1 && CountNumberOfDigits(i2) == 1) {
            if (i < 12) {

                time = "0"+i % 12 + ":0" + i2 + " AM";
            } else
                time = "0"+i % 12 + ":0" + i2 + " PM";
        } else if(CountNumberOfDigits(i) == 2 && CountNumberOfDigits(i2) == 2){
            if (i < 12) {

                time = i % 12 + ":" + i2 + " AM";
            } else
                time = i % 12 + ":" + i2 + " PM";
        }



        if(openTime_Session == true) {
            openTimeTextView.setText(time);
            session.storeStartTime(time);
        }
        else {
            closeTimeTextView.setText(time);
            session.storeEndTime(time);
        }
    }

    static int CountNumberOfDigits(int number)
    {
        int numdgits = 0;
        do
        {
            number = number / 10;
            numdgits++;
        } while (number > 0);
        return numdgits;
    }
}
