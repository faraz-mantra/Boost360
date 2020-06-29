package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.boost.upgrades.UpgradeActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.Store.SimpleImageTextListAdapter;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

/**
 * Created by Admin on 29-01-2018.
 */

public class AboutFragment extends Fragment {
    private Context mContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upgrade, container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded()) return;

        final String[] adapterTexts = getResources().getStringArray(R.array.about_tab_items);
        final TypedArray imagesArray = getResources().obtainTypedArray(R.array.about_sidepanel);
        final int[] adapterImages = new int[adapterTexts.length];
        for (int i = 0; i<adapterTexts.length;i++){
            adapterImages[i] = imagesArray.getResourceId(i,-1);
        }
        imagesArray.recycle();
        RecyclerView mRecyclerView = view.findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        UserSessionManager session = new UserSessionManager(getContext(), getActivity());
        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(mContext, new OnItemClickCallback() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = null;
                switch(pos){
                    case 0:
                        WebEngageController.trackEvent("ABOUT BOOST - TRAINING","null",null);
                        final String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);
                        if(paymentState == "1") {
                            intent = new Intent(mContext, Mobile_Site_Activity.class);
                            intent.putExtra("WEBSITE_NAME", getString(R.string.product_training_link));
                        } else {
                            MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                                    .title("Restricted Access")
                                    .content("The Boost360 training and certification program is only available for customers with active paid subscriptions.")
                                    .build();
                            dialog.show();
                        }
                        break;
                    case 1:
                        WebEngageController.trackEvent("ABOUT BOOST - FAQs","",null);
                        intent = new Intent(mContext, Mobile_Site_Activity.class);
                        intent.putExtra("WEBSITE_NAME",getString(R.string.setting_faq_url));
                        break;
                    case 2:
                        WebEngageController.trackEvent("ABOUT BOOST - FB_LIKE","",null);
                        Methods.likeUsFacebook(mContext, "");
                        return;
                    case 3:
                        WebEngageController.trackEvent("ABOUT BOOST - TWITTER_LIKE","",null);
                        intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                            intent.setData(Uri.parse(Constants.TWITTER_ID_URL));
                        } catch (PackageManager.NameNotFoundException e1) {
                            intent.setData(Uri.parse(Constants.TWITTER_URL));
                            e1.printStackTrace();
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        break;
                    case 4:
                        WebEngageController.trackEvent("ABOUT BOOST - PLAY_STORE_RATING","",null);
                        Uri uri = Uri.parse("market://details?id=" + Constants.PACKAGE_NAME);
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            startActivity(goToMarket);
                            return;
                        } catch (ActivityNotFoundException e) {
                            String url = getResources().getString(R.string.settings_rate_us_link);
                            intent = new Intent(mContext, Mobile_Site_Activity.class);
                            intent.putExtra("WEBSITE_NAME", url);
                        }
                        break;
                    case 5:
                        WebEngageController.trackEvent("ABOUT BOOST - TNC","",null);
                        intent = new Intent(mContext, Mobile_Site_Activity.class);
                        intent.putExtra("WEBSITE_NAME", getResources().getString(R.string.settings_tou_url));
                        break;
                    case 6:
                        WebEngageController.trackEvent("ABOUT BOOST - PRIVACY","",null);
                        intent = new Intent(mContext, Mobile_Site_Activity.class);
                        intent.putExtra("WEBSITE_NAME", getResources().getString(R.string.settings_privacy_url));
                        break;
                    default:
                        return;
                }
                if(intent != null) {
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
        adapter.setItems(adapterImages,adapterTexts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mContext instanceof HomeActivity && HomeActivity.headerText != null)
        {
            HomeActivity.headerText.setText(getString(R.string.about));
        }
    }

}
