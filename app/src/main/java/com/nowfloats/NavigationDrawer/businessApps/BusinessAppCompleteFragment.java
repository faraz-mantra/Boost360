package com.nowfloats.NavigationDrawer.businessApps;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.Mobile_Site_Activity;
import com.nowfloats.NavigationDrawer.model.StoreAndGoModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.List;

import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsDetailsActivity.SHOW_ABOUT_APP;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_PAID;

/**
 * Created by Admin on 12/27/2016.
 */

public class BusinessAppCompleteFragment extends Fragment implements View.OnClickListener {
    List<StoreAndGoModel.PublishStatusModel> modelList;
    SharedPreferences pref;
    private String type;
    private UserSessionManager session;
    private Context context;

    public static Fragment getInstance(String type, String list) {
        Fragment frag = new BusinessAppCompleteFragment();
        Bundle b = new Bundle();
        b.putString("type", type);
        b.putString("modelList", list);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString("type", "android");
            StoreAndGoModel model = new Gson().fromJson(getArguments().getString("modelList"), StoreAndGoModel.class);
            if (model != null) {
                modelList = model.getPublishStatusModelList();
            }
            if (type.equals("android")) {
                setHasOptionsMenu(true);
            }
        }
        pref = getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        MixPanelController.track(MixPanelController.BUSINESS_APP_PUBLISHED, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_business_app_complete, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!isAdded()) return;

        session = new UserSessionManager(context, requireActivity());
        if (pref.getInt(Key_Preferences.ABOUT_BUSINESS_APP, BIZ_APP_DEMO) == BIZ_APP_DEMO) {
            pref.edit().putInt(Key_Preferences.ABOUT_BUSINESS_APP, BIZ_APP_PAID).apply();
        }
        TextView appNameTextView = (TextView) view.findViewById(R.id.app_name);
        appNameTextView.setSelected(true);
        TextView firstCharText = (TextView) view.findViewById(R.id.textcharacter);
        ImageView logoImage = (ImageView) view.findViewById(R.id.app_logo);
        Button shareButton = (Button) view.findViewById(R.id.share_app);
        Button previewButton = (Button) view.findViewById(R.id.preview_app);
        Button openButton = (Button) view.findViewById(R.id.open_app);
        TextView appTextView = (TextView) view.findViewById(R.id.android_app);

        shareButton.setOnClickListener(this);
        previewButton.setOnClickListener(this);
        openButton.setOnClickListener(this);

        String logo = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl);

        String name = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME);
        appNameTextView.setSelected(true);
        appNameTextView.setText(name);

        if (logo == null || logo.isEmpty()) {
            firstCharText.setText(String.valueOf(name.charAt(0)));
            firstCharText.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(R.drawable.studio_architecture)
                    .placeholder(ContextCompat.getDrawable(context, R.drawable.studio_architecture))
                    .into(logoImage);
        } else if (!logo.contains("http")) {
            logo = "https://" + logo;
            Picasso.get()
                    .load(logo)
                    .placeholder(ContextCompat.getDrawable(context, R.drawable.studio_architecture))
                    .into(logoImage);
        }

        if (type.equals("android")) {
            appTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.android_green), null, null, null);
            appTextView.setText(getResources().getString(R.string.android_app));
        } else {
            appTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ios_icon_black), null, null, null);
            appTextView.setText(getResources().getString(R.string.ios_app));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_app:
                if (modelList == null) {
                    Toast.makeText(context, "App is not ready for share", Toast.LENGTH_SHORT).show();
                    break;
                }
                for (StoreAndGoModel.PublishStatusModel model : modelList) {
                    if (model.getKey().equals("ShareLink")) {
                        share(model.getValue());
                        break;
                    }
                }
                break;
            case R.id.open_app:
                if (modelList == null) {
                    Toast.makeText(context, "App is not ready to see", Toast.LENGTH_SHORT).show();
                    break;
                }
                for (StoreAndGoModel.PublishStatusModel model : modelList) {
                    if (model.getKey().equals("AppStoreLink")) {
                        rateUsPlayStore(model.getValue());
                        break;
                    }
                }

                break;
            case R.id.preview_app:
                BusinessAppPreview frag = (BusinessAppPreview) getParentFragment();
                frag.showScreenShots();
                break;
            default:
                break;
        }
    }

    private void share(String url) {
        MixPanelController.track(MixPanelController.SHARE_BUSINESS_APP, null);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "Download the new app " + url);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(i, "Share with:"));
    }

    private void rateUsPlayStore(String url) {
        Uri uri = Uri.parse("market://" + url.substring(url.indexOf("details?id=")));
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(Intent.createChooser(goToMarket, "options"));
        } catch (ActivityNotFoundException e) {
            Intent showWebSiteIntent = new Intent(context, Mobile_Site_Activity.class);
            showWebSiteIntent.putExtra("WEBSITE_NAME", url);
            context.startActivity(showWebSiteIntent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isAdded()) return false;
        switch (item.getItemId()) {
            case R.id.action_notif:
                Methods.materialDialog(getActivity(), "Send Push Notification", "Inform your app users about your latest product offerings via push notifications. This feature is coming soon.");
                return true;
            case R.id.about:
                ((BusinessAppsDetailsActivity) context).addFragments(SHOW_ABOUT_APP);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.business_app, menu);
        MenuItem about = menu.findItem(R.id.about);
        if (pref != null) {
            about.setVisible(pref.getInt(Key_Preferences.ABOUT_BUSINESS_APP, BIZ_APP_DEMO) > BIZ_APP_PAID);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }
}
