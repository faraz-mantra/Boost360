package com.nowfloats.Business_Enquiries;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nowfloats.Business_Enquiries.Model.BzQueryEvent;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class Business_Enquiries_Fragment extends Fragment {
    private static RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
    private static RecyclerView.Adapter enterpriseAdapter ;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout emptyDataLayout,progressLayout;
    UserSessionManager session;
    Activity activity;
    Bus bus;
    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        HomeActivity.headerText.setText(getResources().getString(R.string.business_enquiries_title));
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
        bus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(activity.getApplicationContext(),activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        BoostLog.d("Business_Enquiri", "onCreateView");
        // Inflate the layout for this fragment
        View mainView =  inflater.inflate(R.layout.fragment_business__enguiries, container, false);
        return mainView ;
    }

    @Override
    public void onViewCreated(View mainView, Bundle savedInstanceState) {
        super.onViewCreated(mainView, savedInstanceState);
        BoostLog.d("Business_Enquiri", "onViewCreated");
        progressLayout = (LinearLayout)mainView.findViewById(R.id.progress_layout);
        progressLayout.setVisibility(View.VISIBLE);
        emptyDataLayout = (LinearLayout)mainView.findViewById(R.id.emptydatalayout);
        recyclerView = (RecyclerView) mainView.findViewById(R.id.businesss_Enquiries_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try{
                    API_Business_enquiries businessEnquiries = new API_Business_enquiries(bus,session);
                    businessEnquiries.getMessages();
                }catch(Exception e){ e.printStackTrace();}
            }
        }).start();
    }

    @Subscribe
    public void getValues(final BzQueryEvent event){
        BoostLog.i("BZ ENQ","event-"+event.StorebizEnterpriseQueries+"\n"+event.StorebizQueries);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (session.getISEnterprise().equals("true") && event.StorebizEnterpriseQueries!=null) {
                    enterpriseAdapter = new Business_Queries_Enterprise_Adapter(activity);
                    if(enterpriseAdapter.getItemCount()==0){
                        emptyDataLayout.setVisibility(View.VISIBLE);
                    }else {
                        recyclerView.setAdapter(enterpriseAdapter);
                        enterpriseAdapter.notifyDataSetChanged();
                        emptyDataLayout.setVisibility(View.GONE);
                    }
                    progressLayout.setVisibility(View.GONE);
                } else if( event.StorebizQueries!=null){
                    adapter = new Business_CardAdapter(activity);
                    if (adapter.getItemCount() == 0) {
                        emptyDataLayout.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        emptyDataLayout.setVisibility(View.GONE);
                    }
                    progressLayout.setVisibility(View.GONE);
                }
            }
        });
    }
}