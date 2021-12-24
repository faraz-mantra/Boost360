package com.nowfloats.BusinessProfile.UI.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

/**
 * Created by Admin on 04-10-2017.
 */

public class BusinessDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_details_layout);
        UserSessionManager session = new UserSessionManager(this, this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        findViewById(R.id.tv_address_edit).setOnClickListener(this);
        findViewById(R.id.tv_contact_edit).setOnClickListener(this);
        findViewById(R.id.tv_hours_edit).setOnClickListener(this);

        updateAddress(session);
        updateContactInformation(session);
        updateTimings(session);
    }

    private void updateAddress(UserSessionManager session) {
        TextView addressText = (TextView) findViewById(R.id.tv_address);
        addressText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS) + ", " +
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY) + ", " +
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY));

        String url = "http://maps.google.com/maps/api/staticmap?center=" + Constants.latitude + "," + Constants.longitude + "&zoom=14&size=400x400&sensor=false" + "&markers=color:red%7Clabel:C%7C" + Constants.latitude + "," + Constants.longitude + "&key=" + getString(R.string.google_map_key);
        try {
            Glide.with(this)
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.default_product_image)
                            .centerCrop()
                            .error(R.drawable.default_product_image))
                    .into((ImageView) findViewById(R.id.img_address));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateContactInformation(UserSessionManager session) {

        TextView primaryNumber = (TextView) findViewById(R.id.tv_primary_number);
        TextView alternateNumbers = (TextView) findViewById(R.id.tv_other_numbers);
        TextView emailAddress = (TextView) findViewById(R.id.tv_email);
        TextView websiteAddress = (TextView) findViewById(R.id.tv_website_url);
        TextView facebookPage = (TextView) findViewById(R.id.tv_facebook_page);

        primaryNumber.setText(session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM));
        alternateNumbers.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER) + ", " +
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1) + ", " +
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3));
        emailAddress.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
        String websiteAddr = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE);
        if (TextUtils.isEmpty(websiteAddr)) {
            if (websiteAddr.startsWith("https://") || websiteAddr.startsWith("http://")) {
                websiteAddress.setText(websiteAddr);
            } else {
                websiteAddress.setText("http://" + websiteAddr);
            }
        }
        facebookPage.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FBPAGENAME));
    }

    private void updateTimings(UserSessionManager session) {
        TextView etSunOpen = (TextView) findViewById(R.id.et_sun_open);
        TextView etMonOpen = (TextView) findViewById(R.id.et_mon_open);
        TextView etTueOpen = (TextView) findViewById(R.id.et_tue_open);
        TextView etWedOpen = (TextView) findViewById(R.id.et_wed_open);
        TextView etThuOpen = (TextView) findViewById(R.id.et_thu_open);
        TextView etFriOpen = (TextView) findViewById(R.id.et_fri_open);
        TextView etSatOpen = (TextView) findViewById(R.id.et_sat_open);
        TextView etSunClose = (TextView) findViewById(R.id.et_sun_close);
        TextView etMonClose = (TextView) findViewById(R.id.et_mon_close);
        TextView etTueClose = (TextView) findViewById(R.id.et_tue_close);
        TextView etWedClose = (TextView) findViewById(R.id.et_wed_close);
        TextView etThuClose = (TextView) findViewById(R.id.et_thu_close);
        TextView etFriClose = (TextView) findViewById(R.id.et_fri_close);
        TextView etSatClose = (TextView) findViewById(R.id.et_sat_close);

        if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME).toLowerCase().endsWith("am")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME).toLowerCase().endsWith("pm")) {

            etMonOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME));
            etMonClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME));

        } else {
            etMonOpen.setText("closed");
            etMonClose.setVisibility(View.INVISIBLE);
        }

        if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME).toLowerCase().endsWith("am")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME).toLowerCase().endsWith("pm")) {
            etTueOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME));
            etTueClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME));
        } else {
            etTueOpen.setText("closed");
            etTueClose.setVisibility(View.INVISIBLE);
        }

        if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME).toLowerCase().endsWith("am")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME).toLowerCase().endsWith("pm")) {

            etWedOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME));
            etWedClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME));
        } else {
            etWedOpen.setText("closed");
            etWedClose.setVisibility(View.INVISIBLE);
        }
        if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME).toLowerCase().endsWith("am")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME).toLowerCase().endsWith("pm")) {
            etThuOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME));
            etThuClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME));
        } else {
            etThuOpen.setText("closed");
            etThuClose.setVisibility(View.INVISIBLE);
        }
        if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME).toLowerCase().endsWith("am")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME).toLowerCase().endsWith("pm")) {

            etFriOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME));
            etFriClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME));
        } else {
            etFriOpen.setText("closed");
            etFriClose.setVisibility(View.INVISIBLE);
        }

        if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME).toLowerCase().endsWith("am")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME).toLowerCase().endsWith("pm")) {

            etSatOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME));
            etSatClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME));
        } else {
            etSatOpen.setText("closed");
            etSatClose.setVisibility(View.INVISIBLE);
        }

        if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME).toLowerCase().endsWith("am")
                || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME).toLowerCase().endsWith("pm")) {
            etSunOpen.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME));
            etSunClose.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME));

        } else {
            etSunOpen.setText("closed");
            etSunClose.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_address_edit:
                intent = new Intent(BusinessDetailsActivity.this, Business_Address_Activity.class);
                break;
            case R.id.tv_hours_edit:
                intent = new Intent(BusinessDetailsActivity.this, BusinessHoursActivity.class);
                break;
            case R.id.tv_contact_edit:
                intent = new Intent(BusinessDetailsActivity.this, ContactInformationActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
