package com.nowfloats.NavigationDrawer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.framework.views.zero.FragmentZeroCase;
import com.framework.views.zero.OnZeroCaseClicked;
import com.framework.views.zero.RequestZeroCaseBuilder;
import com.framework.views.zero.ZeroCases;
import com.github.clans.fab.FloatingActionMenu;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.OffersApiService;
import com.nowfloats.NavigationDrawer.Adapter.OffersAdapter;
import com.nowfloats.NavigationDrawer.model.OfferFloatsModel;
import com.nowfloats.NavigationDrawer.model.OfferModel;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.ButteryProgressBar;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class OffersFragment extends Fragment implements View.OnClickListener, OnZeroCaseClicked {
    LinearLayout retryLayout, emptyMsgLayout;
    ButteryProgressBar progressBar;
    CardView progressCrd;
    RecyclerView recyclerView;
    FloatingActionMenu fabButton;
    OnRenewPlanClickListener mCallback = null;
    Bus mBus;
    private UserSessionManager session;
    private OffersApiService apiService;
    private List<OfferFloatsModel> offersModelList = new ArrayList<>();
    private OffersAdapter adapter;
    private boolean moreFloatsAvailable = false;
    private int visibilityFlag = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mBus = BusProvider.getInstance().getBus();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (HomeActivity) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        session = new UserSessionManager(getActivity().getApplicationContext(), getActivity());

        apiService = new OffersApiService(mBus);
        apiService.getAllOffers(getOffersParam(0));
        return inflater.inflate(R.layout.fragment_offers, container, false);
    }

    private HashMap<String, String> getOffersParam(int i) {
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("clientId", Constants.clientId);
        offersParam.put("fpId", session.getFPID());
        offersParam.put("skipBy", i + "");
        return offersParam;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        progressCrd = (CardView) view.findViewById(R.id.progressCard);
        progressBar = (ButteryProgressBar) view.findViewById(R.id.progressbar);
        retryLayout = (LinearLayout) view.findViewById(R.id.postRetryLayout);
        fabButton = (FloatingActionMenu) view.findViewById(R.id.fab);
        emptyMsgLayout = (LinearLayout) view.findViewById(R.id.emptymsglayout);
        view.findViewById(R.id.fab_offer).setOnClickListener(this);
        view.findViewById(R.id.fab_update).setOnClickListener(this);
        ImageView retryPost = (ImageView) view.findViewById(R.id.retryPost);
        ImageView cancelPost = (ImageView) view.findViewById(R.id.cancelPost);
        PorterDuffColorFilter whiteLabelFilter =
                new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor),
                        PorterDuff.Mode.SRC_IN);
        retryPost.setColorFilter(whiteLabelFilter);
        cancelPost.setColorFilter(whiteLabelFilter);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new OffersAdapter(offersModelList, getActivity());
        adapter.setOnItemClickListener(new OffersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Intent i = new Intent();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (visibilityFlag == 0) {
                        visibilityFlag = 1;
                        YoYo.with(Techniques.SlideInUp).interpolate(new DecelerateInterpolator()).duration(200)
                                .playOn(fabButton);
                    }
                } else if (scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (visibilityFlag == 1) {
                        YoYo.with(Techniques.SlideOutDown).interpolate(new AccelerateInterpolator()).duration(200).playOn(fabButton);
                        visibilityFlag = 0;
                    }
                }
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (moreFloatsAvailable) {
                    apiService.getAllOffers(getOffersParam(offersModelList.size()));
                }
            }
        });
        fabButton.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    fabButton.setMenuButtonColorNormal(Color.parseColor("#545454"));
                    fabButton.setMenuButtonColorRipple(Color.parseColor("#fddc80"));
                    fabButton.setMenuButtonColorPressed(Color.parseColor("#545454"));
                } else {
                    fabButton.setMenuButtonColorNormal(getResources().getColor(R.color.primaryColor));
                    fabButton.setMenuButtonColorRipple(Color.parseColor("#fddc80"));
                    fabButton.setMenuButtonColorPressed(getResources().getColor(R.color.primaryColor));
                }
            }
        });
        fabButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (fabButton.isOpened()) {
                    fabButton.close(true);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_update:
                fabButton.close(true);
                if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
                    mCallback.onRenewPlanSelected();
                } else {
                    Intent webIntent = new Intent(getActivity(), Create_Message_Activity.class);
                    startActivity(webIntent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }
                break;
            case R.id.fab_offer:
                fabButton.close(true);
                if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
                    mCallback.onRenewPlanSelected();
                } else {
                    Intent webIntent = new Intent(getActivity(), CreateOffersActivity.class);
                    startActivity(webIntent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }
                break;
        }
    }

    @Subscribe
    public void getAllOffers(OfferModel result) {
        progressBar.setVisibility(View.GONE);
        if (result.response) {
            int offerSize = result.floats.size();
            if (offerSize > 0) {
                for (int i = 0; i < offerSize; i++) {
                    offersModelList.add(result.floats.get(i));
                }
                if (offersModelList.size() ==0||offersModelList==null) {
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,
                            new RequestZeroCaseBuilder(ZeroCases.SERVICES, this, requireActivity()).getRequest().build(), FragmentZeroCase.class.getName())
                            .commit();


                }
                adapter.notifyDataSetChanged();
            }
            moreFloatsAvailable = result.moreFloatsAvailable;
        } else {
            Methods.showSnackBarNegative(getActivity(), getString(R.string.offer_getting_error));
        }

    }

    @Override
    public void primaryButtonClicked() {

    }

    @Override
    public void secondaryButtonClicked() {

    }

    @Override
    public void ternaryButtonClicked() {

    }

    @Override
    public void onBackPressed() {

    }


    interface OnRenewPlanClickListener {
        void onRenewPlanSelected();
    }

    interface OnFragmentInteractionListener {

    }

}
