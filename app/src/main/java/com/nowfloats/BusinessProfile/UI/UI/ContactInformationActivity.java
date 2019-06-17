package com.nowfloats.BusinessProfile.UI.UI;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.helper.ui.BaseActivity;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;
import com.thinksity.databinding.ActivityContactInformationBinding;

public class ContactInformationActivity extends BaseActivity
{
    ActivityContactInformationBinding binding;
    private UserSessionManager session;

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

        binding.appBar.toolbarTitle.setText(getResources().getString(R.string.contact__info));
        session = new UserSessionManager(getApplicationContext(), ContactInformationActivity.this);

        setData();
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
}
