package com.nowfloats.Store;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.nowfloats.AccountDetails.AccountInfoActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.Store.Adapters.StoreAdapter;
import com.nowfloats.Store.Model.StoreEvent;
import com.nowfloats.Store.Model.StoreModel;
import com.nowfloats.Store.Service.API_Service;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

public class Store_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private StoreAdapter storeAdapter;
    Bus bus;
    public static ArrayList<StoreModel> storeModel = new ArrayList<>();
    private LinearLayout emptystorelayout,progress_storelayout;
    private String countryPhoneCode;
    UserSessionManager session;
    Activity activity;

    @Override
    public void onResume() {
        MixPanelController.track(EventKeysWL.STORE_FRAGMENT, null);
        HomeActivity.headerText.setText("Store");
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        session = new UserSessionManager(activity.getApplicationContext(),activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        bus = BusProvider.getInstance().getBus();
        UserSessionManager sessionManager =new UserSessionManager(activity,activity);
        countryPhoneCode = sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE);
        LoadStoreList(activity, bus);
        try {
            final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources()
                    .getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            HomeActivity.shareButton.setImageResource(R.drawable.account_info_icon);
            HomeActivity.shareButton.setVisibility(View.VISIBLE);
            HomeActivity.shareButton.setColorFilter(whiteLabelFilter_pop_ip);
            HomeActivity.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MixPanelController.track("AccountInfo", null);
                    Intent intent = new Intent(activity, AccountInfoActivity.class);
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }catch (Exception e) {e.printStackTrace();}
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.store_recycler_view);
        recyclerView.setHasFixedSize(true);
        emptystorelayout = (LinearLayout) view.findViewById(R.id.emptystorelayout);
        progress_storelayout = (LinearLayout) view.findViewById(R.id.progress_storelayout);
        progress_storelayout.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setItemAnimator(new FadeInUpAnimator());
    }

    @Subscribe
    public void getStoreList(StoreEvent response){
        storeModel = (ArrayList<StoreModel>)response.model;
//        storeModel = filterData(storeModel);
        if(storeModel!=null){
            if (storeModel.size()==0){
                emptystorelayout.setVisibility(View.VISIBLE);
            }else {
                emptystorelayout.setVisibility(View.GONE);
            }
            progress_storelayout.setVisibility(View.GONE);
            storeAdapter = new StoreAdapter(activity, storeModel);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(storeAdapter);
            ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
            scaleAdapter.setFirstOnly(false);
            scaleAdapter.setInterpolator(new OvershootInterpolator());
            recyclerView.setAdapter(scaleAdapter);
        }else{
            emptystorelayout.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<StoreModel> filterData(ArrayList<StoreModel> storeModel) {
        ArrayList<StoreModel> result = new ArrayList<>();
        if (countryPhoneCode!=null){
            if ((String.valueOf("91")).equals(countryPhoneCode)){
                MixPanelController.track(EventKeysWL.STORE_USER_INDIA,null);
                for (int i = 0; i < storeModel.size(); i++) {
                    if (storeModel.get(i).ExternalApplicationDetails==null || storeModel.get(i).ExternalApplicationDetails.equals("null")
                            || storeModel.get(i).ExternalApplicationDetails.size()==0){
                        result.add(storeModel.get(i));
                    }
                }
            }else{
                MixPanelController.track(EventKeysWL.STORE_USER_INTERNATIONAL,null);
                for (int i = 0; i < storeModel.size(); i++) {
                    if (!(storeModel.get(i).ExternalApplicationDetails==null || storeModel.get(i).ExternalApplicationDetails.equals("null")
                            || storeModel.get(i).ExternalApplicationDetails.size()==0)){
                        result.add(storeModel.get(i));
                    }
                }
            }
        }
        return result;
    }

    private void LoadStoreList(final Activity act,final Bus bus) {
        new API_Service(act, session.getSourceClientId(),session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY),bus);
    }
}