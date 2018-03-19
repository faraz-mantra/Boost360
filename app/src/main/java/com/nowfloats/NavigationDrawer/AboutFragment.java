package com.nowfloats.NavigationDrawer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.BusinessProfile.UI.UI.FAQ.FAQMainAcivity;
import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.Store.SimpleImageTextListAdapter;
import com.nowfloats.riachatsdk.ChatManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
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
        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(mContext, new OnItemClickCallback() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = null;
                switch(adapterTexts[pos]){
                    case "About NowFloats":
                        MixPanelController.track(EventKeysWL.EVENT_ABOUT_US, null);
                        intent = new Intent(mContext, Mobile_Site_Activity.class);
                        intent.putExtra("WEBSITE_NAME", getResources().getString(R.string.settings_about_us_link));
                        break;
                    case "FAQs":
                        MixPanelController.track(EventKeysWL.EVENT_FAQs, null);
                        intent = new Intent(mContext, FAQMainAcivity.class);
                        intent.putExtra("array", getResources().getStringArray(R.array.faqmain));
                        break;
                    case "Feedback":
                        MixPanelController.track("ChatFeedback", null);
                        ChatManager.getInstance(getActivity()).startChat(ChatManager.ChatType.FEEDBACK);
                        return;
                    case "Like us on Facebook":
                        MixPanelController.track(EventKeysWL.EVENT_RATE_US_ON_FACEBOOK,null);
                        Methods.likeUsFacebook(mContext, "");
                        return;
                    case "Follow us on Twitter":
                        MixPanelController.track(EventKeysWL.EVENT_RATE_ON_TWITTER,null);
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
                    case "Rate us on Play Store":
                        MixPanelController.track(EventKeysWL.EVENT_RATE_US_ON_PLAYSTORE, null);
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
                    case "Terms of Use":
                        MixPanelController.track(EventKeysWL.EVENT_TERM_OF_USE, null);
                        intent = new Intent(mContext, Mobile_Site_Activity.class);
                        intent.putExtra("WEBSITE_NAME", getResources().getString(R.string.settings_tou_url));
                        break;
                    case "Privacy Policy":
                        MixPanelController.track(EventKeysWL.EVENT_PRIVACY_POLICY, null);
                        intent = new Intent(mContext, Mobile_Site_Activity.class);
                        intent.putExtra("WEBSITE_NAME", getResources().getString(R.string.settings_privacy_url));
                        break;
                    default:
                        return;
                }
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
