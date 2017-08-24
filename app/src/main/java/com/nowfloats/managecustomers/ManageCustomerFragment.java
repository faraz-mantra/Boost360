package com.nowfloats.managecustomers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.Analytics_Screen.SubscribersActivity;
import com.nowfloats.Business_Enquiries.BusinessEnquiryActivity;
import com.nowfloats.Image_Gallery.FullScreenImage;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.bubble.CustomerAssistantService;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageCustomerFragment extends Fragment {
    TextView tvBusinessEnquires, tvSubscribers, tvFacebookChat;
    Typeface robotoLight;
    private SharedPreferences pref = null;
    UserSessionManager session;
    SharedPreferences.Editor prefsEditor;
    private Activity activity;
    private Switch bubbleSwitch, customerAssistantSwitch;
    private TextView tvLearnMore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_manage_customers, container, false);
        pref = activity.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
        session = new UserSessionManager(activity.getApplicationContext(), activity);
        return mainView;
    }

    @Override
    public void onViewCreated(final View mainView, Bundle savedInstanceState) {
        super.onViewCreated(mainView, savedInstanceState);
        if (!isAdded()) return;
        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources()
                .getColor(R.color.white), PorterDuff.Mode.SRC_IN);


        try {


            Typeface robotoMedium = Typeface.createFromAsset(activity.getAssets(), "Roboto-Medium.ttf");
            robotoLight = Typeface.createFromAsset(activity.getAssets(), "Roboto-Light.ttf");
            bubbleSwitch = (Switch) mainView.findViewById(R.id.ninethRow_Switch);
            customerAssistantSwitch = (Switch) mainView.findViewById(R.id.swCustomerAssistant);
            tvBusinessEnquires = (TextView) mainView.findViewById(R.id.tvBusinessEnquires);
            tvBusinessEnquires.setTypeface(robotoMedium);

            tvSubscribers = (TextView) mainView.findViewById(R.id.tvSubscribers);
            tvFacebookChat = (TextView) mainView.findViewById(R.id.tvFacebookChat);
            tvLearnMore = (TextView) mainView.findViewById(R.id.tvLearnMore);
            tvSubscribers.setTypeface(robotoMedium);
            tvLearnMore.setTypeface(robotoMedium);
            tvFacebookChat.setTypeface(robotoMedium);

            tvLearnMore.setVisibility(View.GONE);
            CharSequence charSequence = Html.fromHtml("<u><i>Learn More</i></u>");
            tvLearnMore.setText(charSequence);

            tvLearnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showGif();
                }
            });

            tvBusinessEnquires.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), BusinessEnquiryActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            tvSubscribers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), SubscribersActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            tvFacebookChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), FacebookChatActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            bubbleSwitch.setChecked(session.isBoostBubbleEnabled());
            customerAssistantSwitch.setChecked(pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS,false));

            customerAssistantSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS,false)){
                        customerAssistantSwitch.setChecked(false);
                        Toast.makeText(activity, "This feature is not available.", Toast.LENGTH_SHORT).show();
                    }else{
                        if(customerAssistantSwitch.isChecked()){
                            if(!Methods.isMyServiceRunning(activity, CustomerAssistantService.class)){
                                activity.startService(new Intent(activity,CustomerAssistantService.class));
                            }
                        }else{
                            activity.stopService(new Intent(activity,CustomerAssistantService.class));
                        }
                    }
                }
            });

            bubbleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        session.setBubbleStatus(isChecked);
                    } else {

                        if ((android.os.Build.VERSION.SDK_INT >= 23 && getActivity() != null && !Settings.canDrawOverlays(getActivity()))
                                || (!Methods.isAccessibilitySettingsOn(getActivity()))) {

//                        if (!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("1")) {
//                            showAlertMaterialDialog();
//                            bubbleSwitch.setChecked(false);
//                        } else {
                            session.setBubbleTime(-1);
//                        }
                        } else {
                            session.setBubbleStatus(isChecked);
                        }

                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (HomeActivity.headerText != null)
            HomeActivity.headerText.setText(getResources().getString(R.string.manage_customers));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        HomeActivity.headerText.setText(Constants.StoreName);
    }

    private void showGif() {

        Intent fullImage = new Intent(activity, FullScreenImage.class);
        fullImage.putExtra("currentPositon", 0);
        startActivity(fullImage);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
}
