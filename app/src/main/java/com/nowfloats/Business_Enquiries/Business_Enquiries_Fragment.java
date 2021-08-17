package com.nowfloats.Business_Enquiries;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.models.firestore.FirestoreManager;
import com.nowfloats.Business_Enquiries.Model.BzQueryEvent;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_BUSINESS_ENQUIRIES;
import static com.framework.webengageconstant.EventNameKt.BUSINESS_ENQUIRIES;
import static com.framework.webengageconstant.EventValueKt.NULL;

/**
 * A simple {@link Fragment} subclass.
 */
public class Business_Enquiries_Fragment extends Fragment {
    private static RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
    private static RecyclerView.Adapter enterpriseAdapter;
    UserSessionManager session;
    Activity activity;
    Bus bus;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout emptyDataLayout, progressLayout;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        bus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(activity.getApplicationContext(), activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BoostLog.d("Business_Enquiri", "onCreateView");
        View mainView = inflater.inflate(R.layout.fragment_business__enguiries, container, false);
        WebEngageController.trackEvent(BUSINESS_ENQUIRIES, EVENT_LABEL_BUSINESS_ENQUIRIES, NULL);
        return mainView;
    }

    @Override
    public void onViewCreated(View mainView, Bundle savedInstanceState) {
        super.onViewCreated(mainView, savedInstanceState);
        MixPanelController.track(MixPanelController.BUSINESS_ENQUIRY, null);
        BoostLog.d("Business_Enquiri", "onViewCreated");
        progressLayout = (LinearLayout) mainView.findViewById(R.id.progress_layout);
        progressLayout.setVisibility(View.VISIBLE);
        emptyDataLayout = (LinearLayout) mainView.findViewById(R.id.emptydatalayout);
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
                try {
                    API_Business_enquiries businessEnquiries = new API_Business_enquiries(bus, session);
                    businessEnquiries.getMessages();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Subscribe
    public void getValues(final BzQueryEvent event) {
        BoostLog.i("BZ ENQ", "event-" + event.StorebizEnterpriseQueries + "\n" + event.StorebizQueries);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (session.getISEnterprise().equals("true") && event.StorebizEnterpriseQueries != null) {
                    enterpriseAdapter = new Business_Queries_Enterprise_Adapter(activity);
                    if (enterpriseAdapter.getItemCount() == 0) {
                        emptyDataLayout.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setAdapter(enterpriseAdapter);
                        enterpriseAdapter.notifyDataSetChanged();
                        emptyDataLayout.setVisibility(View.GONE);
                    }
                    progressLayout.setVisibility(View.GONE);
                } else if (event.StorebizQueries != null) {
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
        onBusinessEnquiriesAddedOrUpdated(event.StorebizQueries != null && !event.StorebizQueries.isEmpty());
    }

    private void onBusinessEnquiriesAddedOrUpdated(Boolean isAdded) {
        FirestoreManager instance = FirestoreManager.INSTANCE;
        if (instance.getDrScoreData() != null && instance.getDrScoreData().getMetricdetail() != null) {
            instance.getDrScoreData().getMetricdetail().setBoolean_respond_to_customer_enquiries(isAdded);
            instance.updateDocument();
        }
    }

    @Subscribe
    public void error(String error) {
        progressLayout.setVisibility(View.GONE);
    }
}