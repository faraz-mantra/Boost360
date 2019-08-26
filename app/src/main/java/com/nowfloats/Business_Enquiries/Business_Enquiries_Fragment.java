package com.nowfloats.Business_Enquiries;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nowfloats.Business_Enquiries.Model.Business_Enquiry_Model;
import com.nowfloats.Business_Enquiries.Model.BzQueryEvent;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class Business_Enquiries_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private Business_CardAdapter adapter;
    private Business_Queries_Enterprise_Adapter enterpriseAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout emptyDataLayout, progressLayout;
    UserSessionManager session;
    Activity activity;
    Bus bus;
    private Filter filterType = Filter.FILTER_ALL;

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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BoostLog.d("Business_Enquiri", "onCreateView");
        View mainView = inflater.inflate(R.layout.fragment_business__enguiries, container, false);
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
                if (session.getISEnterprise().equals("true") && event.StorebizEnterpriseQueries != null)
                {
                    enterpriseAdapter = new Business_Queries_Enterprise_Adapter(activity);
                    recyclerView.setAdapter(enterpriseAdapter);
                    enterpriseAdapter.setData(Constants.StorebizEnterpriseQueries);

                    emptyView (enterpriseAdapter.getItemCount());
                    progressLayout.setVisibility(View.GONE);
                }

                else if (event.StorebizQueries != null)
                {
                    adapter = new Business_CardAdapter(activity);
                    recyclerView.setAdapter(adapter);
                    adapter.setData(Constants.StorebizQueries);

                    emptyView (adapter.getItemCount());
                    progressLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.business_enquery_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_all:

                filterType = Filter.FILTER_ALL;
                filterBusinessEnquiry();
                break;

            case R.id.menu_email:

                filterType = Filter.FILTER_EMAIL;
                filterBusinessEnquiry();
                break;

            case R.id.menu_phone:

                filterType = Filter.FILTER_PHONE;
                filterBusinessEnquiry();
                break;

            case R.id.menu_chat:

                filterType = Filter.FILTER_CHAT;
                filterBusinessEnquiry();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    enum Filter
    {
        FILTER_ALL, FILTER_EMAIL, FILTER_PHONE, FILTER_CHAT
    }


    private void filterBusinessEnquiry()
    {
        if (session.getISEnterprise().equals("true"))
        {
            Toast.makeText(getContext(), "Filter Not Available", Toast.LENGTH_LONG).show();
            return;
        }

        if(Constants.StorebizQueries == null)
        {
            return;
        }

        ArrayList<Business_Enquiry_Model> list = new ArrayList<>();
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");

        try
        {
            for(Business_Enquiry_Model model: Constants.StorebizQueries)
            {
                switch (filterType)
                {
                    case FILTER_ALL:

                        list.add(model);
                        break;

                    case FILTER_EMAIL:

                        if(p.matcher(model.contact).matches())
                        {
                            list.add(model);
                        }

                        break;

                    case FILTER_PHONE:

                        if(!model.entityType.equalsIgnoreCase("WHATSAPPQUERY") && !p.matcher(model.contact).matches())
                        {
                            list.add(model);
                        }

                        break;

                    case FILTER_CHAT:

                        if(model.entityType.equalsIgnoreCase("WHATSAPPQUERY") && !p.matcher(model.contact).matches())
                        {
                            list.add(model);
                        }

                        break;
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            if (adapter != null)
            {
                adapter.setData(list);
                emptyView(adapter.getItemCount());
            }
        }
    }

    private void emptyView(int size)
    {
        if (size == 0)
        {
            emptyDataLayout.setVisibility(View.VISIBLE);
        }

        else
        {
            emptyDataLayout.setVisibility(View.GONE);
        }
    }
}