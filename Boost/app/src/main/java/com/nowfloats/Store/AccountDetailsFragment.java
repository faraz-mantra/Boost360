package com.nowfloats.Store;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.AccountDetails.AccountInfoAdapter;
import com.nowfloats.AccountDetails.Model.AccountDetailModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Adapters.StoreAdapter;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by NowFloatsDev on 29/09/2015.
 */
public class AccountDetailsFragment extends Fragment {
    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    Activity activity;
    UserSessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        session = new UserSessionManager(activity.getApplicationContext(),activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_info_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.account_info_recycler_view);
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayout zerothLayout = (LinearLayout)view.findViewById(R.id.zeroth_layout);
        final LinearLayout progressLayout = (LinearLayout)view.findViewById(R.id.progress_accinfo_layout);
        progressLayout.setVisibility(View.VISIBLE);

        StoreAdapter adapter = new StoreAdapter(activity, StoreFragmentTab.activeWidgetModels,"active");
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        progressLayout.setVisibility(View.GONE);
        if (StoreFragmentTab.activeWidgetModels.size()==0){
            zerothLayout.setVisibility(View.VISIBLE);
        }else {
            zerothLayout.setVisibility(View.GONE);
        }
        /*final LinearLayout zerothLayout = (LinearLayout)view.findViewById(R.id.zeroth_layout);
        zerothLayout.setVisibility(View.GONE);
        TextView zeroth_tv = (TextView)view.findViewById(R.id.zeroth_txt);
        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY).toLowerCase().equals("india")){
            zeroth_tv.setText("You are currently on a demo plan. To get more out of your website, upgrade to one of our paid plans.");
        }else{zeroth_tv.setText("You are currently on a Free plan. To get more out of your website, upgrade to one of our paid plans.");}*/

        /*TextView zeroth_storebtn = (TextView)view.findViewById(R.id.zeroth_storebtn);
        zeroth_storebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Constants.gotoStore = true;
            }
        });*/

        /*try {
            AccInfoInterface infoInterface = Constants.restAdapter.create(AccInfoInterface.class);
            HashMap<String,String> values = new HashMap<>();
            values.put("clientId", Constants.clientId);
            values.put("fpId",session.getFPID());
            infoInterface.getAccDetails(values,new Callback<ArrayList<AccountDetailModel>>() {
                @Override
                public void success(ArrayList<AccountDetailModel> accountDetailModels, Response response) {
                    adapter = new AccountInfoAdapter(activity,accountDetailModels);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressLayout.setVisibility(View.GONE);
                    if (accountDetailModels.size()==0){
                        zerothLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    progressLayout.setVisibility(View.GONE);
                    zerothLayout.setVisibility(View.VISIBLE);
                }
            });
        }catch (Exception e){e.printStackTrace(); progressLayout.setVisibility(View.GONE);}*/
    }

    public interface AccInfoInterface{
        //https://api.withfloats.com/Discover/v1/FloatingPoint/GetPaidWidgetDetailsForFP?clientId={clientId}&fpId={fpId}
        // https://api.withfloats.com/Discover/v1/FloatingPoint/GetPaidWidgetDetailsForFP?
        // clientId=""&fpId=""
        @GET("/Discover/v1/FloatingPoint/GetPaidWidgetDetailsForFP")
        public void getAccDetails(@QueryMap Map<String,String> map,Callback<ArrayList<AccountDetailModel>> callback);
    }
}