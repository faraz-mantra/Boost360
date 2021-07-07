package com.nowfloats.NavigationDrawer.businessApps;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.CustomWidget.MaterialProgressBar;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 12/28/2016.
 */
public class BusinessAppStudio extends Fragment implements View.OnClickListener {

    UserSessionManager session;
    SharedPreferences pref;
    private String type;
    private Context context;

    public static Fragment getInstance(String type) {
        Fragment frag = new BusinessAppStudio();
        Bundle b = new Bundle();
        b.putString("type", type);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString("type", "android");
            if (type.equals("android")) {
                setHasOptionsMenu(true);
            }
        }
        MixPanelController.track(MixPanelController.BUSINESS_APP_REQUEST, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_business_app_madman_studio, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!isAdded()) {
            return;
        }
        pref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        session = new UserSessionManager(context, requireActivity());

        Button previewButton = (Button) view.findViewById(R.id.preview_button);
        Button getAppButton = (Button) view.findViewById(R.id.get_app_button);
        ImageView iconImage = (ImageView) view.findViewById(R.id.imgview_icon_type);
        ImageView logoImage = (ImageView) view.findViewById(R.id.business_app_preview_android_image);
        TextView nameTextView = (TextView) view.findViewById(R.id.app_name);
        LinearLayout comming_soon = (LinearLayout) view.findViewById(R.id.comming_soon);
        LinearLayout buttonLayout = (LinearLayout) view.findViewById(R.id.button_layout);

        previewButton.setOnClickListener(this);
        getAppButton.setOnClickListener(this);
        String name = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME);
        nameTextView.setText(name);

        if (type.equals("android")) {
            //getAppButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.android_icon_white), null, null, null );
            if (!pref.getBoolean(Key_Preferences.BUSINESS_APP_REQUESTED, false)) {
                getAppButton.setText(getString(R.string.interest));
                getAppButton.setBackgroundResource(R.drawable.black_round_corner);
            } else {
                getAppButton.setText("Already\nRequested");
                getAppButton.setBackgroundResource(R.drawable.gray_round_corner);
            }
            getAppButton.setPadding(Methods.dpToPx(15, context), 0, 0, 0);
            previewButton.setText(getResources().getString(R.string.android_app_preview));
            iconImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.android_green_padding));
        } else {
            buttonLayout.setVisibility(View.GONE);
            comming_soon.setVisibility(View.VISIBLE);
            getAppButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ios_icon_white), null, null, null);
            getAppButton.setText(getResources().getString(R.string.get_ios_app));
            previewButton.setText(getResources().getString(R.string.ios_app_preview));
            iconImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ios_icon_black));
        }
    }

    @Override
    public void onClick(final View view) {
        final BusinessAppPreview frag = (BusinessAppPreview) getParentFragment();
        switch (view.getId()) {
            case R.id.preview_button:

                if (frag != null) {
                    frag.showScreenShots();
                }
                break;
            case R.id.get_app_button:

                if (isAdded()) {
                    if (type.equals("android")) {

                        if (!pref.getBoolean(Key_Preferences.BUSINESS_APP_REQUESTED, false)) {
                            MaterialProgressBar.startProgressBar(getActivity(), getString(R.string.submiting_request), false);

                            Map<String, String> params = new HashMap<>();
                            params.put("clientId", session.getSourceClientId());
                            params.put("planType", "BizApps");
                            params.put("toEmail", getString(R.string.email_id_to_request_plans));
                            StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
                            storeInterface.requestWidget(session.getFPID(), params, new Callback<String>() {
                                @Override
                                public void success(String s, Response response) {
                                    MaterialProgressBar.dismissProgressBar();
                                    if (response.getStatus() != 200) {
                                        Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong_try_again));
                                        return;
                                    }
                                    pref.edit().putBoolean(Key_Preferences.BUSINESS_APP_REQUESTED, true).apply();
                                    MixPanelController.track(MixPanelController.BUSINESS_APP_INTRESTED, null);
                                    ((Button) view).setText("Already\nRequested");
                                    (view).setBackgroundResource(R.color.business_button_gray);
                                    new MaterialDialog.Builder(context)
                                            .title(getString(R.string.thank_you_for_your_interest))
                                            .content(getString(R.string.business_app_requested_success))
                                            .negativeText(getString(R.string.ok))
                                            .negativeColorRes(R.color.primary_color)
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    MaterialProgressBar.dismissProgressBar();
                                    Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong_try_again));
                                }
                            });
                       /* BusinessAppApis.AppApis apis=BusinessAppApis.getRestAdapter();
                        apis.getGenerate(Constants.clientId, session.getFPID(), new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject s, Response response) {
                                MaterialProgressBar.dismissProgressBar();
                                if(s==null || (s.getAsString().length() == 0) || response.getStatus()!=200){
                                    showMessage("There was an error processing your request. Please write to ria@nowfloats.com or call 1860-123-1233");
                                    return;
                                }
                                //Log.v("ggg",s.toString());
                                String status = s.get("Status").getAsString();
                                if(status!= null && status.equals("1")){
                                    BusinessAppPreview frag= (BusinessAppPreview) getParentFragment();
                                    frag.addAndroidFragment(BusinessAppPreview.SHOW_DEVELOPMENT,"",true);
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.v("ggg",error+"");
                                MaterialProgressBar.dismissProgressBar();
                                showMessage("There was an error processing your request. Please try again in few minutes");
                            }
                        });*/
                        } else {
                            new MaterialDialog.Builder(context)
                                    .title(getString(R.string.thank_you_for_your_interest))
                                    .content(getString(R.string.business_app_requested_success))
                                    .negativeText(getString(R.string.ok))
                                    .negativeColorRes(R.color.primary_color)
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void showMessage(String message) {
        if (!isAdded()) return;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_link_layout, null);
        TextView text = (TextView) view.findViewById(R.id.toast_message_to_contact);
        text.setText(message);
        new MaterialDialog.Builder(context)
                .customView(view, false)
                .build().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isAdded()) return false;
        switch (item.getItemId()) {
            case R.id.action_notif:
                Methods.materialDialog(getActivity(), "Send Push Notification", "Inform your app users about your latest product offerings via push notifications. This feature is coming soon.");
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.business_app, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
