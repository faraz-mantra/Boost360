package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.RiaNetworkInterface;
import com.nowfloats.NavigationDrawer.model.RiaSupportModel;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HelpAndSupportActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView headerText, tvConsultantName, tvConsultantNumber, tvEmail, tvTextHelp, tvTextRia,
            tvTextFaq;
    Button btnSendEmail, btnCall, btnSchedule;
    ImageView ivHelpAvatar;
    ProgressDialog pd;

    UserSessionManager sessionManager;
    RiaSupportModel mRiaSupportModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);

        toolbar = (Toolbar) findViewById(R.id.help_and_support_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText(getResources().getString(R.string.help_and_support));

        tvConsultantName = (TextView) findViewById(R.id.tv_consultant_name);
        tvConsultantNumber = (TextView) findViewById(R.id.tv_contact_number);
        tvEmail = (TextView) findViewById(R.id.tv_consultant_email);
        tvTextHelp = (TextView) findViewById(R.id.tv_text_help);
        tvTextRia = (TextView)  findViewById(R.id.tv_ria_text);
        tvTextFaq = (TextView) findViewById(R.id.tv_text_faq);

        ivHelpAvatar = (ImageView) findViewById(R.id.iv_help_avatar);

        btnSendEmail = (Button) findViewById(R.id.btn_send_mail);
        btnCall = (Button) findViewById(R.id.btn_call);
        btnSchedule = (Button) findViewById(R.id.btn_schedule);

        sessionManager = new UserSessionManager(getApplicationContext(), this);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://ria.withfloats.com")
                .build();
        final RiaNetworkInterface riaNetworkInterface = restAdapter.create(RiaNetworkInterface.class);
        Map<String, String> param = new HashMap<>();
        param.put("clientId", Constants.clientId);
        param.put("fpTag", sessionManager.getFpTag());
        riaNetworkInterface.getMemberForFp(param, new Callback<RiaSupportModel>() {
            @Override
            public void success(final RiaSupportModel riaSupportModel, Response response) {
                if(pd!=null && pd.isShowing()){
                    pd.dismiss();
                }
                if(riaSupportModel!=null){
                    mRiaSupportModel = riaSupportModel;

                    if(riaSupportModel.getGender()==1) {
                        tvConsultantName.setText("Ms. " + riaSupportModel.getName());
                        ivHelpAvatar.setImageDrawable(getResources().getDrawable(R.drawable.help_female_avatar));
                        btnCall.setText("CALL HER");
                    }else {
                        tvConsultantName.setText("Mr. " + riaSupportModel.getName());
                        ivHelpAvatar.setImageDrawable(getResources().getDrawable(R.drawable.help_male_avatar));
                        btnCall.setText("CALL HIM");
                    }
                    tvConsultantNumber.setText(Html.fromHtml("<a href=\"\">" + riaSupportModel.getPhoneNumber() + "</a>"));
                    tvConsultantNumber.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_DIAL);
                            i.setData(Uri.parse("tel:" + riaSupportModel.getPhoneNumber()));
                            startActivity(i);

                        }
                    });
                    tvEmail.setText(Html.fromHtml("<a href=\"mail:" + riaSupportModel.getEmail()+ "\">" + riaSupportModel.getEmail() + "</a>"));
                    String genderVal = (riaSupportModel.getGender()==1)? "her" :"him";
                    tvTextHelp.setText(Html.fromHtml(riaSupportModel.getName() + " is your dedicated web consultant who will be assisting you with all your queries related to your NowFloats website. You can call "+ genderVal + " anytime from <b>9.30 am to 6.30 pm</b> on all working days."));
                }
                else
                {
                    finish();
                    Intent call = new Intent(Intent.ACTION_DIAL);
                    String callString = "tel:" + getString(R.string.contact_us_number);
                    call.setData(Uri.parse(callString));
                    startActivity(call);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(pd!=null && pd.isShowing()){
                    pd.dismiss();
                }
                AlertDialog dialog = new AlertDialog.Builder(HelpAndSupportActivity.this)
                        .setMessage("Error while getting dedicated web consultant")
                        .setCancelable(false)
                        .show();

            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRiaSupportModel!=null){
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_DIAL);
                    i.setData(Uri.parse("tel:" + mRiaSupportModel.getPhoneNumber()));
                    startActivity(i);
                }
            }
        });

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRiaSupportModel!=null) {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mRiaSupportModel.getEmail()});
                    if(emailIntent.resolveActivity(getPackageManager())!=null) {
                        startActivity(emailIntent);
                    }
                }
            }
        });

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HelpAndSupportActivity.this, "This feature will be available soon", Toast.LENGTH_SHORT).show();
            }
        });


        tvTextRia.setText(Html.fromHtml("If your query is unanswered, please contact us at"));
        tvTextFaq.setText(Html.fromHtml("<a href=\"mailto:ria@nowfloats.com\">ria@nowfloats.com</a>. Product related queries, please refer to our <a href=\"https://www.nowfloats.com/faq\">FAQs</a>"));
        tvTextFaq.setMovementMethod(LinkMovementMethod.getInstance());

        pd = ProgressDialog.show(this, "", getString(R.string.please_wait));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            super.onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }
}
