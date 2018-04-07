package com.nowfloats.on_boarding;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.Mobile_Site_Activity;
import com.nowfloats.NavigationDrawer.businessApps.FragmentsFactoryActivity;
import com.nowfloats.Product_Gallery.ProductGalleryActivity;
import com.nowfloats.on_boarding.models.OnBoardingModel;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import static android.view.Window.FEATURE_NO_TITLE;

/**
 * Created by Admin on 16-03-2018.
 */

public class OnBoardingActivity extends AppCompatActivity implements OnBoardingAdapter.ItemClickListener {

    private UserSessionManager session;
    private OnBoardingAdapter adapter;
    private boolean isSomethingChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(FEATURE_NO_TITLE);
        setContentView(R.layout.activity_onboarding);
        OnBoardingModel mOnBoardingModel = getIntent().getParcelableExtra("data");
        if (mOnBoardingModel == null){
            finish();
            return;
        }
        MixPanelController.track(MixPanelController.ON_BOARDING_SCREEN_SHOW,null);
        session = new UserSessionManager(this, this);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels);
        getWindow().setLayout(screenWidth, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setFinishOnTouchOutside(true);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        adapter = new OnBoardingAdapter(this,mOnBoardingModel);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.scrollToPosition(mOnBoardingModel.getToBeCompletePos());
        new PagerSnapHelper().attachToRecyclerView(mRecyclerView);
    }

    public void shareWebsite() {
        String url = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
        if (!Util.isNullOrEmpty(url)) {
            String eol = System.getProperty("line.separator");
            url = getString(R.string.visit_to_new_website)
                    + eol + url.toLowerCase();
        } else {
            String eol = System.getProperty("line.separator");
            url = getString(R.string.visit_to_new_website)
                    + eol + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                    + getResources().getString(R.string.tag_for_partners);
        }
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
        session.setWebsiteshare(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSomethingChanged) {
            adapter.refreshAfterComplete();
            isSomethingChanged = false;
        }
    }

    @Override
    public void onItemClick(int position, OnBoardingModel.ScreenData screenData) {
        Intent intent = null;
        switch (position){
            case 0:
                MixPanelController.track(MixPanelController.ON_BOARDING_WELCOME_ABOARD,null);
                intent = new Intent(this, Mobile_Site_Activity.class);
                intent.putExtra("WEBSITE_NAME",getString(R.string.onboarding_about_product_url));
                // step complete
                if (!screenData.isComplete()) {
                    screenData.setIsComplete(true);
                    adapter.refreshAfterComplete();
                    OnBoardingApiCalls.updateData(session.getFpTag(), "welcome_aboard:true");
                }
                break;
            case 1:
                MixPanelController.track(MixPanelController.ON_BOARDING_SITE_HEALTH,null);
                intent = new Intent(this, FragmentsFactoryActivity.class);
                intent.putExtra("fragmentName","SiteMeterFragment");
                isSomethingChanged = true;
                break;
            case 2:
                MixPanelController.track(MixPanelController.ON_BOARDING_CUSTOM_PAGE,null);
                intent = new Intent(this, CustomPageActivity.class);
                isSomethingChanged = true;
                break;
            case 3:
                MixPanelController.track(MixPanelController.ON_BOARDING_ADD_PRODUCT,null);
                intent = new Intent(this, ProductGalleryActivity.class);
                isSomethingChanged = true;
                break;
            case 4:
                MixPanelController.track(MixPanelController.ON_BOARDING_BOOST_APP,null);
                if (!screenData.isComplete()){
                    screenData.setIsComplete(true);
                    adapter.refreshAfterComplete();
                    OnBoardingApiCalls.updateData(session.getFpTag(),"boost_app:true");
                }
                return;
            case 5:
                MixPanelController.track(MixPanelController.ON_BOARDING_SHARE_WEBSITE,null);
                shareWebsite();
                if (!screenData.isComplete()){
                    screenData.setIsComplete(true);
                    adapter.refreshAfterComplete();
                    OnBoardingApiCalls.updateData(session.getFpTag(),"share_website:true");
                }
                // step complete
                return;
            default:
                return;
        }
        startActivity(intent);
    }

    @Override
    public void onBoardingComplete() {
        MixPanelController.track(MixPanelController.ON_BOARDING_COMPLETE,null);
        OnBoardingApiCalls.updateData(session.getFpTag(),"is_complete:true");
        finish();
    }

}
