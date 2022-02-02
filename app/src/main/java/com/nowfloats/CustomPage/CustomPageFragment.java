package com.nowfloats.CustomPage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.framework.analytics.SentryController;
import com.framework.firebaseUtils.firestore.FirestoreManager;
import com.framework.views.zero.old.AppFragmentZeroCase;
import com.framework.views.zero.old.AppOnZeroCaseClicked;
import com.framework.views.zero.old.AppRequestZeroCaseBuilder;
import com.framework.views.zero.old.AppZeroCases;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nowfloats.CustomPage.Model.CustomPageEvent;
import com.nowfloats.CustomPage.Model.CustomPageLink;
import com.nowfloats.CustomPage.Model.CustomPageModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.on_boarding.OnBoardingApiCalls;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.onboarding.nowfloats.constant.SupportVideoType;
import com.otaliastudios.cameraview.frame.Frame;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.dashboard.utils.ActivityUtilsKt.startHelpSupportVideoActivity;
import static com.framework.webengageconstant.EventLabelKt.CLICKED_POST_A_CUSTOMPAGE;
import static com.framework.webengageconstant.EventNameKt.CREATE_ACUSTOMPAGE;

/**
 * Created by guru on 25/08/2015.
 */
public class CustomPageFragment extends Fragment implements AppOnZeroCaseClicked {
  public static RecyclerView recyclerView;
  public static CustomPageAdapter custompageAdapter;
  public ArrayList<CustomPageModel> dataModel = new ArrayList<CustomPageModel>();
  public static ArrayList<String> posList = new ArrayList<>();
  public static boolean customPageDeleteCheck = false;
  public CustomPageInterface pageInterface;
  public CustomPageDeleteInterface deleteInterface;
  Bus bus;
  UserSessionManager session;
  Activity activity;
  CustomPageLink customPageLink;
  private LinearLayout emptylayout, progress_layout;
  private FrameLayout mainLayout, childContainer;
  private TextView titleTextView;
  private ImageView delete;

  private AppFragmentZeroCase appFragmentZeroCase;

  @Override
  public void onResume() {
    super.onResume();
    bus.register(this);
    loadData();
  }

  @Override
  public void onPause() {
    super.onPause();
    bus.unregister(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_custom_page, container, false);
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    recyclerView = (RecyclerView) view.findViewById(R.id.custompage_recycler_view);
    recyclerView.setHasFixedSize(true);
    final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(mLinearLayoutManager);
    recyclerView.setItemAnimator(new FadeInUpAnimator());
    emptylayout = (LinearLayout) view.findViewById(R.id.emptycustompage);
    mainLayout = (FrameLayout) view.findViewById(R.id.mainLayout);
    childContainer = (FrameLayout) view.findViewById(R.id.child_container_custom_page);
    progress_layout = (LinearLayout) view.findViewById(R.id.progress_custom_page);
    recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    recyclerView.setItemAnimator(null);
    final FloatingActionButton addProduct = view.findViewById(R.id.fab_custom_page);
    appFragmentZeroCase = new AppRequestZeroCaseBuilder(AppZeroCases.CUSTOM_PAGES, this, getActivity()).getRequest().build();
    getActivity().getSupportFragmentManager().beginTransaction().add(childContainer.getId(), appFragmentZeroCase).commit();

    addProduct.setOnClickListener(v -> addProduct());
    if ((activity instanceof CustomPageActivity) && ((CustomPageActivity) activity).isAdd) addProduct();

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activity = getActivity();
    pageInterface = Constants.restAdapter.create(CustomPageInterface.class);
    session = new UserSessionManager(activity.getApplicationContext(), activity);
    bus = BusProvider.getInstance().getBus();
    deleteInterface = (CustomPageDeleteInterface) activity;
    //Title
    titleTextView = HomeActivity.headerText;
    if (titleTextView != null)
      titleTextView.setText(getString(R.string.custom_pages));
    final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(
        ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_IN);
    if (HomeActivity.shareButton != null) {
      HomeActivity.shareButton.setImageResource(R.drawable.delete_dustbin_small);
      HomeActivity.shareButton.setColorFilter(whiteLabelFilter_pop_ip);
      delete = HomeActivity.shareButton;
      delete.setBackgroundResource(0);
      delete.setVisibility(View.GONE);
    }
  }

  public void loadData() {
    progress_layout.setVisibility(View.VISIBLE);
    CustomPageInterface pageInterface2 = Constants.restAdapter.create(CustomPageInterface.class);
    pageInterface2.getPageUrl(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG), 0, 100, 1, new Callback<CustomPageLink>() {
      @Override
      public void success(CustomPageLink pageDetail, Response response) {
        customPageLink = pageDetail;
        LoadPageList(activity, bus);
      }

      @Override
      public void failure(RetrofitError error) {
        progress_layout.setVisibility(View.GONE);
      }

    });
  }

  private void LoadPageList(Activity activity, Bus bus) {
    new CustomPageService().GetPages(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG), Constants.clientId, pageInterface, bus);
  }

  @Subscribe
  public void getPageList(CustomPageEvent response) {
    dataModel = (ArrayList<CustomPageModel>) response.model;
    if (dataModel != null) {
      onCustomPageAddedOrUpdated(!dataModel.isEmpty());
      if (dataModel.isEmpty()) {
        emptyView();
        //emptylayout.setVisibility(View.VISIBLE);
      } else {
        nonEmptyView();
        //emptylayout.setVisibility(View.GONE);
      }
      if (!session.getOnBoardingStatus() && dataModel.size() != session.getCustomPageCount()) {
        session.setCustomPageCount(dataModel.size());
        OnBoardingApiCalls.updateData(session.getFpTag(), String.format("custom_page:%s", dataModel.size() > 0 ? "true" : "false"));
      }
      custompageAdapter = new CustomPageAdapter(activity, dataModel, session, pageInterface, bus, customPageLink);
//            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(custompageAdapter);
//            ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
//            scaleAdapter.setFirstOnly(false);
//            scaleAdapter.setInterpolator(new OvershootInterpolator());
      recyclerView.setAdapter(custompageAdapter);
      custompageAdapter.notifyDataSetChanged();
      recyclerView.invalidate();
    } else {
      emptyView();
      //emptylayout.setVisibility(View.VISIBLE);
    }
    progress_layout.setVisibility(View.GONE);
  }

  private void onCustomPageAddedOrUpdated(boolean isAdded) {
    FirestoreManager instance = FirestoreManager.INSTANCE;
    if (instance.getDrScoreData() != null && instance.getDrScoreData().getMetricdetail() != null) {
      instance.getDrScoreData().getMetricdetail().setBoolean_create_custom_page(isAdded);
      instance.updateDocument();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home && customPageDeleteCheck) {
//            if (deleteView!=null){
//                deleteView.setBackgroundColor(android.R.attr.selectableItemBackground);
//                deleteView = null;
//            }
      CustomPageAdapter.deleteCheck = false;
      posList = new ArrayList<String>();
      deleteInterface.DeletePageTrigger(0, false, null);
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setBackgroundDrawable(defaultColor);
      if (titleTextView != null)
        titleTextView.setText(getString(R.string.custom_pages));
      delete.setVisibility(View.GONE);
      customPageDeleteCheck = false;

      if (custompageAdapter != null)
        custompageAdapter.notifyDataSetChanged();
      if (recyclerView != null)
        recyclerView.invalidate();

    } else if (id == android.R.id.home) {
//            finish();
//            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    return super.onOptionsItemSelected(item);
  }

  //    @Override
  public void onBackPressed() {
    if (customPageDeleteCheck) {
//            if (deleteView!=null){
//                deleteView.setBackgroundColor(android.R.attr.selectableItemBackground);
//                deleteView = null;
//            }
      CustomPageAdapter.deleteCheck = false;
      posList = new ArrayList<String>();
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setBackgroundDrawable(defaultColor);
      if (titleTextView != null)
        titleTextView.setText(getString(R.string.custom_pages));
      delete.setVisibility(View.GONE);
      customPageDeleteCheck = false;

      if (custompageAdapter != null)
        custompageAdapter.notifyDataSetChanged();
      if (recyclerView != null)
        recyclerView.invalidate();
    } else {
//            super.onBackPressed();
//            finish();
//            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
  }

  @Subscribe
  public void DeletePageTrigger(DeletePageTriggerEvent event) {
    final int position = event.position;
    boolean chk = event.b;
    View v = event.v;
    deleteInterface.DeletePageTrigger(0, true, v);
    if (chk) {
//        deleteView = v;
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.gray_transparent)));
      String size = "";
      if (posList.size() > 0) {
        size = posList.size() + "";
      }
      if (titleTextView != null)
        titleTextView.setText(size + "" + getString(R.string.page_selected));
      delete.setImageResource(R.drawable.delete_dustbin_small);
      delete.setVisibility(View.VISIBLE);
      customPageDeleteCheck = true;
      delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//                if (deleteView!=null){
//                    deleteView.setBackgroundColor(android.R.attr.selectableItemBackground);
//                    deleteView = null;
//                }
          new MaterialDialog.Builder(activity)
              .title(getString(R.string.are_you_sure_want_to_delete))
              .content(getString(R.string.page_will_deleted))
              .positiveText(getString(R.string.deleted_in_capital))
              .negativeText(getString(R.string.cancel_in_capital))
              .positiveColorRes(R.color.primaryColor)
              .negativeColorRes(R.color.grey)
              .callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                  super.onPositive(dialog);
                  try {

                    String url = Constants.NOW_FLOATS_API_URL + "/Discover/v1/floatingpoint/custompage/delete";
                    for (int i = 0; i < posList.size(); i++) {
                      new PageDeleteAsyncTaask(url, activity, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG), pageInterface, dataModel, bus).execute();
                    }
                    dialog.dismiss();
                    deleteInterface.DeletePageTrigger(0, false, null);
//                                    getSupportActionBar().setDisplayShowTitleEnabled(false);
//                                    getSupportActionBar().setBackgroundDrawable(defaultColor);
                    if (titleTextView != null)
                      titleTextView.setText(getString(R.string.custom_pages));
                    delete.setVisibility(View.GONE);
                    customPageDeleteCheck = false;
                    CustomPageAdapter.deleteCheck = false;
                  } catch (Exception e) {
                    SentryController.INSTANCE.captureException(e);
                    e.printStackTrace();
                    posList = new ArrayList<String>();
                    deleteInterface.DeletePageTrigger(0, false, null);
//                                    getSupportActionBar().setDisplayShowTitleEnabled(false);
//                                    getSupportActionBar().setBackgroundDrawable(defaultColor);
                    if (titleTextView != null)
                      titleTextView.setText(getString(R.string.custom_pages));
                    delete.setVisibility(View.GONE);
                    customPageDeleteCheck = false;
                    CustomPageAdapter.deleteCheck = false;
                    if (custompageAdapter != null)
                      custompageAdapter.notifyDataSetChanged();
                    if (recyclerView != null)
                      recyclerView.invalidate();

                  }
                }

                @Override
                public void onNegative(MaterialDialog dialog) {
                  super.onNegative(dialog);
                  dialog.dismiss();
                  posList = new ArrayList<String>();
                  deleteInterface.DeletePageTrigger(0, false, null);
//                                getSupportActionBar().setDisplayShowTitleEnabled(false);
//                                getSupportActionBar().setBackgroundDrawable(defaultColor);
                  if (titleTextView != null)
                    titleTextView.setText(getString(R.string.custom_pages));
                  delete.setVisibility(View.GONE);
                  customPageDeleteCheck = false;
                  CustomPageAdapter.deleteCheck = false;
                  if (custompageAdapter != null)
                    custompageAdapter.notifyDataSetChanged();
                  if (recyclerView != null)
                    recyclerView.invalidate();
                }
              })
              .show();
        }
      });
    } else {
      String size = "";
      if (posList.size() > 0) {
        size = posList.size() + "";
        if (titleTextView != null)
          titleTextView.setText(size + "" + getString(R.string.page_selected));
      }
      if (posList.size() == 0) {
        posList = new ArrayList<String>();
        deleteInterface.DeletePageTrigger(0, false, null);
//                getSupportActionBar().setDisplayShowTitleEnabled(false);
//                getSupportActionBar().setBackgroundDrawable(defaultColor);
        if (titleTextView != null)
          titleTextView.setText(getString(R.string.custom_pages));
        delete.setVisibility(View.GONE);
        customPageDeleteCheck = false;
        CustomPageAdapter.deleteCheck = false;
        if (custompageAdapter != null)
          custompageAdapter.notifyDataSetChanged();
        if (recyclerView != null)
          recyclerView.invalidate();
      }

    }
  }

  private void openAddCustomPageActivity() {
    MixPanelController.track("AddCustomPage", null);
    WebEngageController.trackEvent(CREATE_ACUSTOMPAGE, CLICKED_POST_A_CUSTOMPAGE, session.getFpTag());
    Intent intent = new Intent(activity, CreateCustomPageActivity.class);
    if ((activity instanceof CustomPageActivity) && ((CustomPageActivity) activity).isAdd)
      activity.startActivityForResult(intent, 202);
    else activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
  }


  /**
   * Revamped Widget Logic
   */
  private void addProduct() {
//        /**
//         * If not new pricing plan
//         */
//        if (!WidgetKey.isNewPricingPlan) {
//            if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
//                Methods.showFeatureNotAvailDialog(getContext());
//            } else {
//                openAddCustomPageActivity();
//            }
//        } else {
//            String value = WidgetKey.getPropertyValue(WidgetKey.WIDGET_CUSTOM_PAGES, WidgetKey.WIDGET_PROPERTY_MAX);
//
//            if (value.equals(WidgetKey.WidgetValue.FEATURE_NOT_AVAILABLE.getValue())) {
//                Methods.showFeatureNotAvailDialog(getContext());
//            } else if (!value.equals(WidgetKey.WidgetValue.UNLIMITED.getValue()) && dataModel.size() >= Integer.parseInt(value)) {
//                Toast.makeText(getContext(), String.valueOf(getString(R.string.message_custom_page_limit)), Toast.LENGTH_LONG).show();
//            } else {
    openAddCustomPageActivity();
//            }
//        }
  }

  private void nonEmptyView() {
    mainLayout.setVisibility(View.VISIBLE);
    childContainer.setVisibility(View.GONE);
  }


  private void emptyView() {
    mainLayout.setVisibility(View.GONE);
    childContainer.setVisibility(View.VISIBLE);

  }

  @Override
  public void primaryButtonClicked() {
    addProduct();
  }

  @Override
  public void secondaryButtonClicked() {
    startHelpSupportVideoActivity(activity, SupportVideoType.CUSTOM_PAGES.getValue());
  }

  @Override
  public void ternaryButtonClicked() {

  }

  @Override
  public void appOnBackPressed() {

  }
}