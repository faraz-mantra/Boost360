package com.nowfloats.Business_Enquiries;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.firebaseUtils.firestore.FirestoreManager;
import com.framework.views.zero.old.AppFragmentZeroCase;
import com.framework.views.zero.old.AppOnZeroCaseClicked;
import com.framework.views.zero.old.AppRequestZeroCaseBuilder;
import com.framework.views.zero.old.AppZeroCases;
import com.nowfloats.Business_Enquiries.Model.BzQueryEvent;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;
import com.thinksity.databinding.FragmentBusinessEnguiriesBinding;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_BUSINESS_ENQUIRIES;
import static com.framework.webengageconstant.EventNameKt.BUSINESS_ENQUIRIES;
import static com.framework.webengageconstant.EventValueKt.NULL;

/**
 * A simple {@link Fragment} subclass.
 */
public class Business_Enquiries_Fragment extends Fragment implements AppOnZeroCaseClicked {
  private static RecyclerView recyclerView;
  private static RecyclerView.Adapter adapter;
  private static RecyclerView.Adapter enterpriseAdapter;
  UserSessionManager session;
  Activity activity;
  Bus bus;
  private RecyclerView.LayoutManager layoutManager;
  private LinearLayout progressLayout;
  private AppFragmentZeroCase appFragmentZeroCase;
  private FragmentBusinessEnguiriesBinding binding;

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
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_enguiries, container, false);
    WebEngageController.trackEvent(BUSINESS_ENQUIRIES, EVENT_LABEL_BUSINESS_ENQUIRIES, NULL);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View mainView, Bundle savedInstanceState) {
    super.onViewCreated(mainView, savedInstanceState);
    appFragmentZeroCase = new AppRequestZeroCaseBuilder(AppZeroCases.CUSTOMER_MESSAGES, this, getActivity()).getRequest().build();
    getActivity().getSupportFragmentManager().beginTransaction().add(binding.childContainer.getId(), appFragmentZeroCase).commit();

    MixPanelController.track(MixPanelController.BUSINESS_ENQUIRY, null);
    BoostLog.d("Business_Enquiri", "onViewCreated");
    progressLayout = (LinearLayout) mainView.findViewById(R.id.progress_layout);
    progressLayout.setVisibility(View.VISIBLE);
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
            emptyView();
          } else {
            recyclerView.setAdapter(enterpriseAdapter);
            enterpriseAdapter.notifyDataSetChanged();
            nonEmptyView();
          }
          progressLayout.setVisibility(View.GONE);
        } else if (event.StorebizQueries != null) {
          adapter = new Business_CardAdapter(activity);
          if (adapter.getItemCount() == 0) {
            emptyView();
          } else {
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            nonEmptyView();
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

  private void nonEmptyView() {
    binding.mainlayout.setVisibility(View.VISIBLE);
    binding.childContainer.setVisibility(View.GONE);
  }


  private void emptyView() {
    binding.mainlayout.setVisibility(View.GONE);
    binding.childContainer.setVisibility(View.VISIBLE);

  }

  @Override
  public void primaryButtonClicked() {

  }

  @Override
  public void secondaryButtonClicked() {
    Toast.makeText(getActivity(), getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();

  }

  @Override
  public void ternaryButtonClicked() {

  }

  @Override
  public void appOnBackPressed() {

  }
}