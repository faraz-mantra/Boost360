package com.nowfloats.Store;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NotificationCenter.AlertArchive;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by guru on 05-05-2015.
 */
public class DomainLookup extends AppCompatActivity {
    String domainName = "", domainType = ".com";
    UserSessionManager session;
    private boolean domainPurchase = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.domain_lookup);
        session = new UserSessionManager(getApplicationContext(), DomainLookup.this);
        if (getIntent().hasExtra("key")) {
            domainPurchase = true;
        } else {
            domainPurchase = false;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.store_data_action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        if (domainPurchase) getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        else getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Title
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.store_title);
        if (domainPurchase) {
            titleTextView.setText(getString(R.string.book_domain));
        } else {
            titleTextView.setText(getString(R.string.domain_lookup));
        }

        Button SearchBtn = (Button) findViewById(R.id.search_domain);
        final EditText domainText = (EditText) findViewById(R.id.domainNameEditText);
        MultiStateToggleButton domainSwitch = (MultiStateToggleButton) findViewById(R.id.domainTypeSwitchView);

        /*domainSwitch.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(Switch aSwitch, boolean value) {
            if (value) {domainType = ".net";
                netDomain.setTextColor(getResources().getColor(R.color.light_black));
                comDomain.setTextColor(getResources().getColor(R.color.signup_hint_grey));
            }
            else {domainType = ".com";
                comDomain.setTextColor(getResources().getColor(R.color.light_black));
                netDomain.setTextColor(getResources().getColor(R.color.signup_hint_grey));
            }
            }
        });*/
        domainSwitch.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                switch (value) {
                    case 0:
                        domainType = ".com";
                        break;
                    case 1:
                        domainType = ".net";
                        break;
                }
            }
        });

        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                domainName = domainText.getText().toString().trim();
                int checkFlag = 0;
                if (domainName.length() == 0) {
                    Methods.showSnackBarNegative(DomainLookup.this, getString(R.string.enter_domain_nmae_to_continue));
                    checkFlag = 1;
                }
                if (checkFlag == 0) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("clientId", session.getSourceClientId());
                    params.put("domainType", domainType);

                    StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
                    storeInterface.checkDomain(domainName, params, new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {
                            if (s.equals("true")) {
                                Methods.showSnackBarPositive(DomainLookup.this, getString(R.string.domain_is_available));
                                if (domainPurchase) {
                                    PurchaseDomainDialog();
                                }
                            } else {
                                Methods.showSnackBarNegative(DomainLookup.this, getString(R.string.domain_not_available));
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Methods.showSnackBarNegative(DomainLookup.this, getString(R.string.something_went_wrong_try_again));
                        }
                    });
                }
            }
        });
    }

    private void PurchaseDomainDialog() {
        new MaterialDialog.Builder(DomainLookup.this)
                .title(getString(R.string.domain_booking_confirmation))
                .content(getString(R.string.hello_do_you_want_this) + domainName + domainType + getString(R.string.as_domain))
                .positiveText(getString(R.string.yes))
                .negativeText(getString(R.string.no))
                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.light_gray)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        PurchaseDomainProcess(dialog);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void PurchaseDomainProcess(final MaterialDialog PrevDialog) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("clientId", session.getSourceClientId());
        params.put("domainType", domainType);
        params.put("domainName", domainName);
        params.put("existingFPTag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toUpperCase());

        StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
        storeInterface.purchaseDomain(params, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                new AlertArchive(Constants.alertInterface, "DOTCOM", session.getFPID());
                PrevDialog.dismiss();
                //Show success dialog
                new MaterialDialog.Builder(DomainLookup.this)
                        .title(getString(R.string.success))
                        .content(getString(R.string.thank_for_booking_domain) + domainName + domainType + getString(R.string.activate_in_24_hours))
                        .positiveText(getString(R.string.okay))
                        .positiveColorRes(R.color.primaryColor)
                        .negativeColorRes(R.color.light_gray)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            }

            @Override
            public void failure(RetrofitError error) {
                Methods.showSnackBarNegative(DomainLookup.this, getString(R.string.something_went_wrong_try_again));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home && !domainPurchase) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!domainPurchase) {
            super.onBackPressed();
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }
}
