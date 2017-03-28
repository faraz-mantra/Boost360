package com.nowfloats.Store;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.AccountDetails.AccountInfoAdapter;
import com.nowfloats.AccountDetails.Model.AccountDetailModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.Store.Adapters.StoreAdapter;
import com.nowfloats.Store.Model.StoreEvent;
import com.nowfloats.Store.Model.StoreModel;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.romeo.mylibrary.Models.OrderDataModel;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.QueryMap;

public class Store_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private StoreAdapter storeAdapter;
    Bus bus;
    //public static ArrayList<StoreModel> storeModel = new ArrayList<>();
    private LinearLayout emptystorelayout,progress_storelayout;
    private String countryPhoneCode;
    UserSessionManager session;
    Activity activity;
    boolean args;

    private OrderDataModel mOrderData;

    MaterialDialog materialProgress;
    private final int DIRECT_REQUEST_CODE = 1;
    private final int OPC_REQUEST_CODE = 2;

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    public static Fragment newInstance(boolean args){
        Store_Fragment fragment = new Store_Fragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("key",args);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        session = new UserSessionManager(activity.getApplicationContext(),activity);
        Bundle bundle = getArguments();
        args = bundle.getBoolean("key");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        bus = BusProvider.getInstance().getBus();
        UserSessionManager sessionManager =new UserSessionManager(activity,activity);
        countryPhoneCode = sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE);
        progress_storelayout = (LinearLayout) view.findViewById(R.id.progress_storelayout);

        /*if (args.equals("inactive")){
            LoadStoreList(activity, bus);
        }else if(args.equals("active")){
            LoadActivePlans(activity);
        }*/

        try {
//            final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources()
//                    .getColor(R.color.white), PorterDuff.Mode.SRC_IN);
//            HomeActivity.shareButton.setImageResource(R.drawable.account_info_icon);
            HomeActivity.shareButton.setVisibility(View.GONE);
           /* HomeActivity.shareButton.setColorFilter(whiteLabelFilter_pop_ip);
            HomeActivity.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MixPanelController.track("AccountInfo", null);
                    Intent intent = new Intent(activity, AccountInfoActivity.class);
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });*/
        }catch (Exception e) {e.printStackTrace();}
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.store_recycler_view);
        recyclerView.setHasFixedSize(true);
        emptystorelayout = (LinearLayout) view.findViewById(R.id.emptystorelayout);
        emptystorelayout.setVisibility(View.GONE);
        progress_storelayout = (LinearLayout) view.findViewById(R.id.progress_storelayout);
        progress_storelayout.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new FadeInUpAnimator());

        storeAdapter = new StoreAdapter(activity, StoreFragmentTab.additionalWidgetModels,"all", session);
//        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(storeAdapter);
//        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
//        scaleAdapter.setFirstOnly(false);
//        scaleAdapter.setInterpolator(new OvershootInterpolator());
        recyclerView.setAdapter(storeAdapter);
        progress_storelayout.setVisibility(View.GONE);
        if (StoreFragmentTab.additionalWidgetModels.size()==0){
            emptystorelayout.setVisibility(View.VISIBLE);
        }else {
            emptystorelayout.setVisibility(View.GONE);
            //Log.d("Args Val", args+"1");
            if(args){
                args = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(storeAdapter.getItemCount()-1);
                    }
                }, 100);
            }
        }
    }


    private void LoadActivePlans(final Activity activity) {
        try {
            AccInfoInterface infoInterface = Constants.restAdapter.create(AccInfoInterface.class);
            HashMap<String,String> values = new HashMap<>();
            values.put("clientId", Constants.clientId);
            values.put("fpId",session.getFPID());
            infoInterface.getAccDetails(values,new Callback<ArrayList<AccountDetailModel>>() {
                @Override
                public void success(ArrayList<AccountDetailModel> accountDetailModels, Response response) {
                    AccountInfoAdapter adapter = new AccountInfoAdapter(activity,accountDetailModels);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progress_storelayout.setVisibility(View.GONE);
                }

                @Override
                public void failure(RetrofitError error) {
                    progress_storelayout.setVisibility(View.GONE);
                }
            });
        }catch (Exception e){e.printStackTrace(); progress_storelayout.setVisibility(View.GONE);}
    }

    @Subscribe
    public void getStoreList(StoreEvent response){
        List<StoreModel> storeModel = (ArrayList<StoreModel>)response.model.AllPackages;
//        storeModel = filterData(storeModel);
        if(storeModel!=null){
            if (storeModel.size()==0){
                emptystorelayout.setVisibility(View.VISIBLE);
            }else {
                emptystorelayout.setVisibility(View.GONE);
            }
            progress_storelayout.setVisibility(View.GONE);
            storeAdapter = new StoreAdapter(activity, StoreFragmentTab.additionalWidgetModels,"all", session);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(storeAdapter);
            ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
            scaleAdapter.setFirstOnly(false);
            scaleAdapter.setInterpolator(new OvershootInterpolator());
            recyclerView.setAdapter(scaleAdapter);
            //Log.d("Args Val", args+"2");
            if(args){
                args = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(storeAdapter.getItemCount()-1);
                    }
                }, 100);

            }
        }else{
            emptystorelayout.setVisibility(View.VISIBLE);
        }
    }

    public interface AccInfoInterface{
        //https://api.withfloats.com/Discover/v1/FloatingPoint/GetPaidWidgetDetailsForFP?clientId={clientId}&fpId={fpId}
        // https://api.withfloats.com/Discover/v1/FloatingPoint/GetPaidWidgetDetailsForFP?
        // clientId=""&fpId=""
        @GET("/Discover/v1/FloatingPoint/GetPaidWidgetDetailsForFP")
        public void getAccDetails(@QueryMap Map<String,String> map, Callback<ArrayList<AccountDetailModel>> callback);
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
//        new API_Service(act, session.getSourceClientId(), session.getSourceClientId(),session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY),session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID), ,bus, );
    }
}