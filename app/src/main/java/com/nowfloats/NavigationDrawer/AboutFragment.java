package com.nowfloats.NavigationDrawer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.Store.SimpleImageTextListAdapter;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_NULL;
import static com.framework.webengageconstant.EventLabelKt.NO_EVENT_LABLE;
import static com.framework.webengageconstant.EventNameKt.ABOUT_BOOST_FAQS;
import static com.framework.webengageconstant.EventNameKt.ABOUT_BOOST_FB_LIKE;
import static com.framework.webengageconstant.EventNameKt.ABOUT_BOOST_PLAY_STORE_RATING;
import static com.framework.webengageconstant.EventNameKt.ABOUT_BOOST_PRIVACY;
import static com.framework.webengageconstant.EventNameKt.ABOUT_BOOST_TNC;
import static com.framework.webengageconstant.EventNameKt.ABOUT_BOOST_TRAINING;
import static com.framework.webengageconstant.EventNameKt.ABOUT_BOOST_TWITTER_LIKE;
import static com.framework.webengageconstant.EventValueKt.NULL;

/**
 * Created by Admin on 29-01-2018.
 */

public class AboutFragment extends Fragment {
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upgrade, container, false);
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
        for (int i = 0; i < adapterTexts.length; i++) {
            adapterImages[i] = imagesArray.getResourceId(i, -1);
        }
        imagesArray.recycle();
        RecyclerView mRecyclerView = view.findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        UserSessionManager session = new UserSessionManager(getContext(), getActivity());
        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(mContext, new OnItemClickCallback() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = null;
                switch (pos) {
                    case 0:
                        WebEngageController.trackEvent(ABOUT_BOOST_TRAINING, EVENT_LABEL_NULL, NULL);
                        if (Constants.StoreWidgets.contains("MERCHANT_TRAINING")) {
                            intent = new Intent(mContext, Mobile_Site_Activity.class);
                            intent.putExtra("WEBSITE_NAME", getString(R.string.product_training_link));
                        } else {
                            MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                                    .title("Restricted Access")
                                    .content(R.string.you_need_to_buy_one_time_pack_for_boost_training)
                                    .build();
                            dialog.show();
                        }
                        break;
                    case 1:
                        WebEngageController.trackEvent(ABOUT_BOOST_FAQS, NO_EVENT_LABLE, NULL);
                        intent = new Intent(mContext, Mobile_Site_Activity.class);
                        intent.putExtra("WEBSITE_NAME", getString(R.string.setting_faq_url));
                        break;
                    case 2:
                        WebEngageController.trackEvent(ABOUT_BOOST_FB_LIKE, NO_EVENT_LABLE, NULL);
                        Methods.likeUsFacebook(mContext, "");
                        return;
                    case 3:
                        WebEngageController.trackEvent(ABOUT_BOOST_TWITTER_LIKE, NO_EVENT_LABLE, NULL);
                        intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            getActivity().getPackageManager().getPackageInfo(getString(R.string.twitter_package), 0);
                            intent.setData(Uri.parse(Constants.TWITTER_ID_URL));
                        } catch (PackageManager.NameNotFoundException e1) {
                            intent.setData(Uri.parse(Constants.TWITTER_URL));
                            e1.printStackTrace();
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        break;
                    case 4:
                        WebEngageController.trackEvent(ABOUT_BOOST_PLAY_STORE_RATING, NO_EVENT_LABLE, NULL);
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
                        WebEngageController.trackEvent(ABOUT_BOOST_TNC, NO_EVENT_LABLE, NULL);
                        intent = new Intent(mContext, Mobile_Site_Activity.class);
                        intent.putExtra("WEBSITE_NAME", getResources().getString(R.string.settings_tou_url));
                        break;
                    case 6:
                        WebEngageController.trackEvent(ABOUT_BOOST_PRIVACY, NO_EVENT_LABLE, NULL);
                        intent = new Intent(mContext, Mobile_Site_Activity.class);
                        intent.putExtra("WEBSITE_NAME", getResources().getString(R.string.settings_privacy_url));
                        break;
                    default:
                        return;
                }
                if (intent != null) {
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
        adapter.setItems(adapterImages, adapterTexts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mContext instanceof HomeActivity && HomeActivity.headerText != null) {
            HomeActivity.headerText.setText(getString(R.string.about));
        }
    }

}
