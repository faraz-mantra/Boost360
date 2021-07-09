package com.nowfloats.NavigationDrawer;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appservice.constant.FragmentType;
import com.appservice.constant.IntentConstant;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dashboard.utils.CodeUtilsKt;
import com.framework.models.firestore.FirestoreManager;
import com.framework.utils.ContentSharing;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nowfloats.AccrossVerticals.Testimonials.TestimonialsActivity;
import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.Image_Gallery.ImageGalleryActivity;
import com.nowfloats.Login.Fetch_Home_Data;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.Home_View_Card_Delete;
import com.nowfloats.NavigationDrawer.API.Restricted_FP_Service;
import com.nowfloats.NavigationDrawer.floating_view.FloatingViewBottomSheetDialog;
import com.nowfloats.NavigationDrawer.floating_view.FloatingViewBottomSheetDialog.FLOATING_CLICK_TYPE;
import com.nowfloats.NavigationDrawer.model.Image_Text_Model;
import com.nowfloats.NavigationDrawer.model.PostImageSuccessEvent;
import com.nowfloats.NavigationDrawer.model.PostTaskModel;
import com.nowfloats.NavigationDrawer.model.PostTextSuccessEvent;
import com.nowfloats.NavigationDrawer.model.UploadPostEvent;
import com.nowfloats.NavigationDrawer.model.Welcome_Card_Model;
import com.nowfloats.NavigationDrawer.model.WhatsNewDataModel;
import com.nowfloats.ProductGallery.ManageProductActivity;
import com.nowfloats.ProductGallery.Model.ImageListModel;
import com.nowfloats.ProductGallery.Model.Product;
import com.nowfloats.sync.DbController;
import com.nowfloats.sync.model.Updates;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.ButteryProgressBar;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.Utils;
import com.nowfloats.util.WebEngageController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thinksity.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

import static com.appservice.ui.catalog.CatalogServiceContainerActivityKt.startFragmentActivityNew;
import static com.framework.webengageconstant.EventLabelKt.FAB;
import static com.framework.webengageconstant.EventNameKt.DASHBOARD_FAB_CUSTOM_PAGE;
import static com.framework.webengageconstant.EventNameKt.DASHBOARD_FAB_IMAGE;
import static com.framework.webengageconstant.EventNameKt.DASHBOARD_FAB_INVENTORY;
import static com.framework.webengageconstant.EventNameKt.DASHBOARD_FAB_TESTIMONIAL;
import static com.framework.webengageconstant.EventNameKt.DASHBOARD_FAB_UPDATE;
import static com.framework.webengageconstant.EventValueKt.NULL;
import static com.nowfloats.NavigationDrawer.floating_view.FloatingViewBottomSheetDialog.FLOATING_CLICK_TYPE.WRITE_UPDATE;

public class Home_Main_Fragment extends Fragment implements Fetch_Home_Data.Fetch_Home_Data_Interface {

  private static final int STORAGE_CODE = 120;
  private String type;
  private String imageShare;
  private int position;
  public static LinearLayout retryLayout, emptyMsgLayout;
  public ButteryProgressBar progressBar;
  public static CardView progressCrd;
  private static RecyclerView.Adapter adapter;
  private RecyclerView.LayoutManager layoutManager;
  public static RecyclerView recyclerView;
  private static ArrayList<CardData> card;
  private static ArrayList<Integer> removedItems;
  static View.OnClickListener myOnClickListener;
  Fetch_Home_Data fetch_home_data;
  FloatingActionButton fabButton, addUpdateButton, addImageButton, addInventoryButton;
  private int maxSyncCall = 2;
  UserSessionManager session;
  private static final String DATA_ARG_KEY = "HomeFragment.DATA_ARG_KEY";
  public static CardAdapter_V3 cAdapter;
  JSONObject data;
  public static Bus bus;
  OnRenewPlanClickListener mCallback = null;
  private ArrayList<Object> mNewWelcomeTextImageList = new ArrayList<Object>();
  private int visibilityFlag = 1;
  public static UploadPostEvent recentPostEvent = null;
  private String ImageResponseID = "";
  public static int facebookPostCount = 0;
  public Activity current_Activity;
  private SharedPreferences mPref;
  private boolean mIsNewMsg = false;
  private boolean isRotate = false;
  private DbController mDbController;
  LinearLayout addProduct;
  LinearLayout addImage;
  LinearLayout addCustomPage;
  LinearLayout addTestimonial;
  LinearLayout addUpdate;
  LinearLayout addOptions;
  LinearLayout updatesLayout;
  LinearLayout emptyLayout;

  TextView addProductText;

  public static ArrayList<FloatsMessageModel> StorebizFloats = new ArrayList<FloatsMessageModel>();

  public Home_Main_Fragment() {
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    bus.unregister(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    MixPanelController.track(EventKeysWL.HOME_SCREEN, null);
    BoostLog.d("Home_Main_Fragment", "onResume : " + session.getFPName());
    getActivity().setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
  }


  private void inflateWhatsNew() {
    SharedPreferences preferences = getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    final SharedPreferences.Editor editor = preferences.edit();
    if (!preferences.getString("currentAppVersion", "default").equals(getVersion())) {
      View v = getActivity().getLayoutInflater().inflate(R.layout.whats_new_layout, null);
      final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setView(v);
      final AlertDialog dialog = builder.show();
      RecyclerView rvWhatsNew = v.findViewById(R.id.rv_whats_new);
      Button done = v.findViewById(R.id.btn_whats_new_done);
      done.setOnClickListener(v1 -> {
        dialog.dismiss();
        editor.putString("currentAppVersion", getVersion());
        editor.commit();
      });
      int[] images = {R.drawable.lock, R.drawable.share, R.drawable.camera, R.drawable.scope, R.drawable.chat};
      String[] headerText = {"Password Management", "Refer and Earn", "New Camera Experience", "Live Visitor Info", "Talk To NowFloats"};
      String[] bodyText = {"Now Change/retrieve your old password. Phew!!!", "Like Us? Help spread the word about our app among your friends :)",
          "Now with preview, crop & rotate features!", "Get to know when & from where someone visited your website in real time",
          "Got Questions for us? We are just now a tap away!"};
      List<WhatsNewDataModel> list = new ArrayList<>();
      for (int i = 0; i < images.length; i++) {
        WhatsNewDataModel model = new WhatsNewDataModel(images[i], headerText[i], bodyText[i]);
        list.add(model);
      }
      WhatsNewAdapter adapter = new WhatsNewAdapter(list);
      rvWhatsNew.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
      rvWhatsNew.setAdapter(adapter);
      adapter.notifyDataSetChanged();
    }
  }

  private String getVersion() {
    String val;
    try {
      val = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      val = "default";
    }
    return val;
  }

  @Subscribe
  public void uploadProcess(UploadPostEvent event) {
    try {
      BoostLog.i("upload msg ...", "TRIGeREd");
      recentPostEvent = event;
      progressCrd.setVisibility(View.GONE);
      progressBar.setVisibility(View.VISIBLE);
      retryLayout.setVisibility(View.GONE);
      fetch_home_data.setNewPostListener(true);
      fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
      uploadPicture(event.path, event.msg, event.mSocialShare, getActivity(), new UserSessionManager(getActivity(), getActivity()));
    } catch (Exception e) {
      e.printStackTrace();
      Toast.makeText(getActivity(), getString(R.string.unable_to_post_message), Toast.LENGTH_SHORT).show();
    }
  }

  @Subscribe
  public void ImageUploadCheck(PostImageSuccessEvent event) {
    ImageResponseID = event.imageResponseId;
    BoostLog.i("IMAGE---", getString(R.string.image_upload_sent_check_triggered));
    mIsNewMsg = true;
    getNewAvailableUpdates();
    mPref.edit().putString("msg_post", "").apply();
    mPref.edit().putInt("quikrStatus", 0).apply();
    mPref.edit().putString("image_post", "").apply();
  }

  @Subscribe
  public void TextUploadCheck(PostTextSuccessEvent event) {
    if (event.status) {
      event.status = false;
      mIsNewMsg = true;
      BoostLog.i("TEXT---", getString(R.string.text_upload_sent_check_triggered));
      getNewAvailableUpdates();
      Create_Message_Activity.path = "";

      Constants.createMsg = false;
      mPref.edit().putString("msg_post", "").apply();
      mPref.edit().putInt("quikrStatus", 0).apply();
      mPref.edit().putString("image_post", "").apply();
      //path = pref.getString("image_post",null);
    }
  }

  public static ArrayList<FloatsMessageModel> getMessageList(Activity activity) {
    if (activity instanceof HomeActivity) return HomeActivity.StorebizFloats;
    else return StorebizFloats;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      if (getActivity() instanceof HomeActivity) mCallback = (HomeActivity) context;
    } catch (ClassCastException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bus = BusProvider.getInstance().getBus();
    bus.register(this);
    current_Activity = getActivity();
    session = new UserSessionManager(getActivity(), current_Activity);
    mPref = current_Activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    mDbController = DbController.getDbController(current_Activity);
    getMessageList(current_Activity).clear();
  }

  private void onClickFloatingView(FLOATING_CLICK_TYPE type) {
    switch (type) {
      case ADD_PRODUCT_SERVICE:
        addInventory();
        break;
      case ADD_IMAGE:
        addImage();
        break;
      case CREATE_CUSTOM_PAGE:
        addCustomPage();
        break;
      case ADD_TESTIMONIAL:
        addTestimonial();
        break;
      case WRITE_UPDATE:
        addUpdate();
        break;
    }
  }

  private void processOnClickOfFabButton() {
    isRotate = ViewAnimation.rotateFab(fabButton, !isRotate);
    if (isRotate) {
      updatesLayout.setAlpha(0.2f);
      emptyLayout.setAlpha(0.2f);
      ViewAnimation.showIn(addOptions);
    } else {
      updatesLayout.setAlpha(1f);
      emptyLayout.setAlpha(1f);
      ViewAnimation.showOut(addOptions);
    }
  }

//    intent = new Intent(ImageMenuActivity.this, ImageGalleryActivity .class);

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    BoostLog.d("Home_Main_Fragment", "onViewCreated");

    /**
     * Call this API to get visitsCount list and display in Analytics
     */
    //new Fetch_Home_Data(getActivity(),session).getVisitors();
  }

  private void startSync() {

    progressBar.setVisibility(View.VISIBLE);
    fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
    fetch_home_data.getMessages(session.getFPID(), "0");
  }

  private void getNewAvailableUpdates() {
    fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
    BoostLog.d("Latest Message Id: ", mDbController.getLatestMessageId());
    fetch_home_data.getNewAvailableMessage(mDbController.getLatestMessageId(), session.getFPID());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View mainView;
    mainView = inflater.inflate(R.layout.fragment_home__main_, container, false);
    fetch_home_data = new Fetch_Home_Data(getActivity(), 0);

    getMessageList(current_Activity).clear();
    progressCrd = mainView.findViewById(R.id.progressCard);
    progressBar = mainView.findViewById(R.id.progressbar);
    retryLayout = mainView.findViewById(R.id.postRetryLayout);
    emptyMsgLayout = mainView.findViewById(R.id.emptymsglayout);
    emptyMsgLayout.setVisibility(View.GONE);


    ImageView retryPost = mainView.findViewById(R.id.retryPost);
    ImageView cancelPost = mainView.findViewById(R.id.cancelPost);
    PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
    retryPost.setColorFilter(whiteLabelFilter);
    cancelPost.setColorFilter(whiteLabelFilter);
    recyclerView = mainView.findViewById(R.id.my_recycler_view);
    recyclerView.setHasFixedSize(true);

    cAdapter = new CardAdapter_V3(getActivity(), session, this::shareContent);

    recyclerView.setAdapter(cAdapter);
    boolean isSynced = mPref.getBoolean(Constants.SYNCED, false);
    if (isSynced) {

      if (Methods.isOnline(getActivity())) {
        BoostLog.d("OnViewCreated", "This is getting called");
        getNewAvailableUpdates();
      } else {
        loadDataFromDb(0, false);
      }


    } else {

      startSync();
    }

    retryPost.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (recentPostEvent != null) {
          facebookPostCount = 1;
          bus.post(recentPostEvent);
          retryLayout.setVisibility(View.GONE);
          progressBar.setVisibility(View.VISIBLE);
        } else {
          fetch_home_data.setNewPostListener(false);
          fetch_home_data.setInterfaceType(0);
          facebookPostCount = 0;
          retryLayout.setVisibility(View.GONE);
          progressCrd.setVisibility(View.GONE);
          Methods.showSnackBarNegative(getActivity(), getString(R.string.retry_create_new_post));
          Constants.createMsg = false;
        }
      }
    });

    cancelPost.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        fetch_home_data.setNewPostListener(false);
        fetch_home_data.setInterfaceType(0);
        facebookPostCount = 0;
        recentPostEvent = null;
        retryLayout.setVisibility(View.GONE);
        progressCrd.setVisibility(View.GONE);
        Constants.createMsg = false;
      }
    });

    if (Constants.isWelcomScreenToBeShown) {

      Welcome_Card_Model welcome_card_model = new Welcome_Card_Model();
      welcome_card_model.webSiteName = session.getFPName();
      mNewWelcomeTextImageList.add(welcome_card_model);

    }


    for (int i = 0; i < getMessageList(current_Activity).size(); i++) {
      Image_Text_Model image_text_model_1 = new Image_Text_Model();
      mNewWelcomeTextImageList.add(image_text_model_1);
    }
    layoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setItemAnimator(new FadeInUpAnimator());
    myOnClickListener = new MyOnClickListener(getActivity());
    recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
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
      public void onLoadMore(int current_page) {
        BoostLog.d("ILUD OnLoadMore:", "This is getting Called");
        int checkLoad = fetch_home_data.getInterfaceType();
        if (checkLoad == 0) {
          int skipVal = (current_page - 1) * 10;
          if (!loadDataFromDb(skipVal, false)) {
            fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
            fetch_home_data.getMessages(session.getFPID(), String.valueOf(skipVal));
            progressBar.setVisibility(View.GONE);
          }
        }
      }
    });

    fabButton = mainView.findViewById(R.id.fab);

    addProduct = mainView.findViewById(R.id.addProduct);
    addImage = mainView.findViewById(R.id.addImage);
    addCustomPage = mainView.findViewById(R.id.addCustomPage);
    addTestimonial = mainView.findViewById(R.id.addTestimonial);
    addUpdate = mainView.findViewById(R.id.addUpdate);
    addOptions = mainView.findViewById(R.id.addOptions);
    updatesLayout = mainView.findViewById(R.id.updatesLayout);
    emptyLayout = mainView.findViewById(R.id.emptymsglayout);

    addProductText = mainView.findViewById(R.id.addProductText);

    addProductText.setText("Add a " + Utils.getSingleProductTaxonomyFromServiceCode(session.getFP_AppExperienceCode()));

    ViewAnimation.init(addOptions);

    fabButton.setOnClickListener(v -> {
      if (getActivity() instanceof HomeActivity) {
        FloatingViewBottomSheetDialog dialog = new FloatingViewBottomSheetDialog(session, this::onClickFloatingView);
        dialog.show(getParentFragmentManager(), FloatingViewBottomSheetDialog.class.getName());
      } else {
        onClickFloatingView(WRITE_UPDATE);
      }
//            processOnClickOfFabButton()
    });
    addUpdate.setOnClickListener(v -> {
      addUpdate();
      processOnClickOfFabButton();
    });
    addImage.setOnClickListener(v -> {
      addImage();
      processOnClickOfFabButton();
    });
    addProduct.setOnClickListener(v -> {
      addInventory();
      processOnClickOfFabButton();
    });
    addCustomPage.setOnClickListener(v -> {
      addCustomPage();
      processOnClickOfFabButton();
    });
    addTestimonial.setOnClickListener(v -> {
      addTestimonial();
      processOnClickOfFabButton();
    });

    return mainView;
  }

  @Override
  public void onPause() {
    super.onPause();

  }

  @Override
  public void onStop() {
    super.onStop();

  }

  private void sendIsInterested(FragmentActivity activity, String fpid, String planType, final Bus bus) {
    try {
      HashMap<String, String> params = new HashMap<String, String>();
      params.put("clientId", Constants.clientId);
      params.put("plantype", planType);
      new Restricted_FP_Service(activity, fpid, params, bus);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void dataFetched(int skip, boolean isNewMessage) {

    Create_Message_Activity.path = "";
    Constants.createMsg = false;
    loadDataFromDb(skip, isNewMessage);
    bus.post(new UpdateFetchAfterPost());

    progressBar.setVisibility(View.GONE);
  }

  private void onBusinessUpdateAddedOrUpdated(Integer count) {
    if (count == null) count = 0;
    FirestoreManager instance = FirestoreManager.INSTANCE;
    if(instance.getDrScoreData() != null && instance.getDrScoreData().getMetricdetail() != null){
      instance.getDrScoreData().getMetricdetail().setNumber_updates_posted(count);
      instance.updateDocument();
    }
  }

  @Override
  public void sendFetched(FloatsMessageModel messageModel) {
    BoostLog.i("IMAGE---interface", " Triggered");
    try {
      BoostLog.i("IMAGE---", "{0}_id==" + messageModel._id + "\n deal Id==" + ImageResponseID + "\nURL =" + messageModel.imageUri);
      if (messageModel._id.equals(ImageResponseID) && !messageModel.imageUri.contains(Constants.NOW_FLOATS_API_URL)) {
        Create_Message_Activity.path = "";
        if (progressCrd != null && progressBar != null) {
          progressCrd.setVisibility(View.GONE);
          progressBar.setVisibility(View.GONE);
          recentPostEvent = null;
        }
        Create_Message_Activity.path = "";
        Constants.createMsg = false;
        BoostLog.i("IMAGE---", "UPLoaD SucceSS");
        cAdapter.notifyDataSetChanged();

        recyclerView.invalidate();
        fetch_home_data.setInterfaceType(0);
        fetch_home_data.setNewPostListener(false);
        facebookPostCount = 0;
      } else {
        JSONObject obj2 = new JSONObject();
        try {
          obj2.put("dealId", ImageResponseID);
          obj2.put("clientId", Constants.clientId1);
        } catch (Exception ex1) {
          ex1.printStackTrace();
        }
        BoostLog.i("IMAGE---", "CALing DeLEte Method");
        Home_View_Card_Delete deleteCard = new Home_View_Card_Delete(getActivity(), Constants.DeleteCard, obj2, 0, null, 1);
        deleteCard.execute();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void uploadPicture(String path, String msg, String socialShare, Activity act, UserSessionManager session) {
    BoostLog.d("Image : ", "Upload Pic Path : " + path);
    String merchantId = null, parentId = null;

    try {
      if (session.getISEnterprise().equals("true")) {
        merchantId = null;
      } else {
        merchantId = session.getFPID();
      }
      if (session.getISEnterprise().equals("true")) {
        parentId = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PARENTID);
      } else {
        parentId = null;
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
    PostTaskModel task;
    if (!Util.isNullOrEmpty(path) && path.length() > 1) {
      task = new PostTaskModel(Constants.clientId, msg, socialShare, Create_Message_Activity.imageIconButtonSelected,
          merchantId, parentId, false);
    } else {
      task = new PostTaskModel(Constants.clientId, msg, socialShare, Create_Message_Activity.imageIconButtonSelected,
          merchantId, parentId, Create_Message_Activity.tosubscribers);
    }

      /*  if (facebookPostCount==0) {
            if (Constants.fbShareEnabled) {
                Create_Message_Activity.postUser = new com.nowfloats.NavigationDrawer.API.PostModel(msg);
            }

            if (Constants.fbPageShareEnabled) {
                if (Constants.FbPageList != null && Constants.FbPageList.length() > 0) {
                    Create_Message_Activity.postPage = new com.nowfloats.NavigationDrawer.API.PostModel(msg, Constants.FbPageList);
                }
            }


        }*/
    UploadMessageTask upa = new UploadMessageTask(act, path, task, session);
    upa.UploadPostService();
  }

  private void addImage() {
    WebEngageController.trackEvent(DASHBOARD_FAB_IMAGE, FAB, NULL);
    Intent webIntent = new Intent(getActivity(), ImageGalleryActivity.class);
    webIntent.putExtra("create_image", true);
    startActivity(webIntent);
    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
  }

  private boolean loadDataFromDb(int skip, boolean isNewMessage) {

    if (mIsNewMsg) {
      mIsNewMsg = false;
      getMessageList(current_Activity).clear();

      cAdapter.notifyDataSetChanged();
      recyclerView.setAdapter(cAdapter);
      Constants.createMsg = false;
    }
    List<Updates> updates = null;
    try {
      updates = mDbController.getAllUpdates(skip);
    } catch (Exception e) {
      MixPanelController.track(MixPanelController.UPDATE_DB_CRASH, null);
      mPref.edit().putBoolean(com.nowfloats.util.Constants.SYNCED, false).apply();
      mDbController.deleteDataBase();
      startSync();
      return true;
    }
    if (updates == null || updates.isEmpty()) {
      if (skip == 0 && maxSyncCall > 0) {
        maxSyncCall--;
        startSync();
      }
      return false;
    }
    if (emptyMsgLayout.getVisibility() == View.VISIBLE) {
      emptyMsgLayout.setVisibility(View.GONE);
    }


    for (Updates update : updates) {
      FloatsMessageModel floatModel = new FloatsMessageModel(update.getServerId(), update.getDate(),
          update.getImageUrl(), update.getUpdateText(), update.getTileImageUrl(), update.getType(), update.getUrl());
      getMessageList(current_Activity).add(floatModel);
    }


    SharedPreferences.Editor editor = mPref.edit();
    editor.putBoolean(Constants.SYNCED, true);
    editor.apply();

    cAdapter.notifyDataSetChanged();
    progressBar.setVisibility(View.GONE);
    ArrayList<FloatsMessageModel> listMessage = getMessageList(current_Activity);
    if (listMessage != null && !listMessage.isEmpty()) onBusinessUpdateAddedOrUpdated(listMessage.size());
    return true;

  }

  private void openAddUpdateActivity() {
    WebEngageController.trackEvent(DASHBOARD_FAB_UPDATE, FAB, NULL);
    Intent webIntent = new Intent(getActivity(), Create_Message_Activity.class);
    startActivity(webIntent);
    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
  }

  private void addCustomPage() {
    WebEngageController.trackEvent(DASHBOARD_FAB_CUSTOM_PAGE, FAB, NULL);
    Intent webIntent = new Intent(getActivity(), CustomPageActivity.class);
    webIntent.putExtra("IS_ADD", true);
    startActivity(webIntent);
    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
  }

  private void addTestimonial() {
    WebEngageController.trackEvent(DASHBOARD_FAB_TESTIMONIAL, FAB, NULL);
    Intent webIntent = new Intent(getActivity(), TestimonialsActivity.class);
    webIntent.putExtra("IS_ADD", true);
    startActivity(webIntent);
    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
  }

  private void addUpdate() {
    openAddUpdateActivity();
//        /**
//         * If not new pricing plan
//         */
//        if(!WidgetKey.isNewPricingPlan)
//        {
//            if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1"))
//            {
//                Methods.showFeatureNotAvailDialog(getContext());
//            }
//
//            else
//            {
//
//            }
//        }
//
//        else
//        {
//            String value = WidgetKey.getPropertyValue(WidgetKey.WIDGET_LATEST_UPDATES, WidgetKey.WIDGET_PROPERTY_MAX);
//
//            if(value.equals(WidgetKey.WidgetValue.FEATURE_NOT_AVAILABLE.getValue()))
//            {
//                Methods.showFeatureNotAvailDialog(getContext());
//            }
//
//            else if(!value.equals(WidgetKey.WidgetValue.UNLIMITED.getValue()) && cAdapter.getItemCount() >= Integer.parseInt(value))
//            {
//                Toast.makeText(getContext(), String.valueOf(getString(R.string.message_add_update_limit)), Toast.LENGTH_LONG).show();
//            }
//
//            else
//            {
//                openAddUpdateActivity();
//            }
//        }
  }

  private Bundle getBundleData(Product p) {
    Bundle bundle = new Bundle();
    bundle.putSerializable(IntentConstant.PRODUCT_DATA.name(), getProductData(p));
    bundle.putBoolean(IntentConstant.NON_PHYSICAL_EXP_CODE.name(), session.isNonPhysicalProductExperienceCode());
    String currencyType = "";
    if (TextUtils.isEmpty(p.CurrencyCode)) {
      try {
        currencyType = Constants.Currency_Country_Map.get(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY).toLowerCase());
      } catch (Exception e) {
        e.printStackTrace();
      }

    } else currencyType = p.CurrencyCode;
    if (TextUtils.isEmpty(currencyType)) currencyType = getString(R.string.currency_text);
    bundle.putString(IntentConstant.CURRENCY_TYPE.name(), currencyType);
    bundle.putString(IntentConstant.FP_ID.name(), session.getFPID());
    bundle.putString(IntentConstant.FP_TAG.name(), session.getFpTag());
    bundle.putString(IntentConstant.USER_PROFILE_ID.name(), session.getUserProfileId());
    bundle.putString(IntentConstant.CLIENT_ID.name(), Constants.clientId);
    bundle.putString(IntentConstant.EXTERNAL_SOURCE_ID.name(), session.getFPDetails(Key_Preferences.EXTERNAL_SOURCE_ID));
    bundle.putString(IntentConstant.APPLICATION_ID.name(), session.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID));
    return bundle;
  }

  private com.appservice.model.serviceProduct.CatalogProduct getProductData(Product p) {
    ArrayList<com.appservice.model.serviceProduct.ImageListModel> listImages = new ArrayList<>();
    if (p.Images != null) {
      for (ImageListModel data : p.Images) {
        listImages.add(new com.appservice.model.serviceProduct.ImageListModel(data.ImageUri, data.TileImageUri));
      }
    }
    ArrayList<com.appservice.model.KeySpecification> otherSpec = new ArrayList<>();
    if (p.otherSpecification != null) {
      for (Product.Specification spec : p.otherSpecification) {
        otherSpec.add(new com.appservice.model.KeySpecification(spec.key, spec.value));
      }
    }
    com.appservice.model.serviceProduct.CatalogProduct newProduct = new com.appservice.model.serviceProduct.CatalogProduct();
    newProduct.setCurrencyCode(p.CurrencyCode);
    newProduct.setDescription(p.Description);
    newProduct.setDiscountAmount(p.DiscountAmount);
    newProduct.setExternalSourceId(p.ExternalSourceId);
    newProduct.setIsArchived(p.IsArchived);
    newProduct.setIsAvailable(p.IsAvailable);
    newProduct.setIsFreeShipmentAvailable(p.IsFreeShipmentAvailable);
    newProduct.setName(p.Name);
    newProduct.setPrice(p.Price);
    newProduct.setPriority(p.Priority);
    newProduct.setShipmentDuration(p.ShipmentDuration);
    newProduct.setAvailableUnits(p.availableUnits);
    newProduct.set_keywords(p._keywords);
    newProduct.setTags((ArrayList<String>) p.tags);
    newProduct.setApplicationId(p.ApplicationId);
    newProduct.setFPTag(p.FPTag);
    newProduct.setImageUri(p.ImageUri);
    newProduct.setProductUrl(p.ProductUrl);
    newProduct.setImages(listImages);
    newProduct.setMerchantName(p.MerchantName);
    newProduct.setTileImageUri(p.TileImageUri);
    newProduct.setProductId(p.productId);
    newProduct.setGPId(p.GPId);
    newProduct.setTotalQueries(p.TotalQueries);
    newProduct.setCreatedOn(p.CreatedOn);
    newProduct.setProductIndex(p.ProductIndex);
    newProduct.setPicimageURI(p.picimageURI);
    newProduct.setUpdatedOn(p.UpdatedOn);
    newProduct.setProductSelected(p.isProductSelected);
    newProduct.setProductType(p.productType);
    newProduct.setPaymentType(p.paymentType);
    newProduct.setVariants(p.variants);
    newProduct.setBrandName(p.brandName);
    newProduct.setCategory(p.category);
    newProduct.setCodAvailable(p.codAvailable);
    newProduct.setMaxCodOrders(p.maxCodOrders);
    newProduct.setPrepaidOnlineAvailable(p.prepaidOnlineAvailable);
    newProduct.setMaxPrepaidOnlineAvailable(p.maxPrepaidOnlineAvailable);
    if (p.BuyOnlineLink != null) {
      newProduct.setBuyOnlineLink(new com.appservice.model.serviceProduct.BuyOnlineLink(p.BuyOnlineLink.url, p.BuyOnlineLink.description));
    }
    if (p.keySpecification != null) {
      newProduct.setKeySpecification(new com.appservice.model.KeySpecification(p.keySpecification.key, p.keySpecification.value));
    }
    newProduct.setOtherSpecification(otherSpec);
    newProduct.setPickupAddressReferenceId(p.pickupAddressReferenceId);
    return newProduct;
  }

  interface OnRenewPlanClickListener {
    void onRenewPlanSelected();
  }

  private void addInventory() {
    WebEngageController.trackEvent(DASHBOARD_FAB_INVENTORY, FAB, NULL);
    String type = Utils.getProductType(session.getFP_AppExperienceCode());
    Product newProduct = new Product();
    newProduct.setProductType(type);
    switch (type.toUpperCase()) {
      case "SERVICES":
        Bundle bundle = getBundleData(newProduct);
        startFragmentActivityNew(getActivity(), FragmentType.SERVICE_DETAIL_VIEW, bundle, false, true);
        break;
      default:
        Intent intent = new Intent(getContext(), ManageProductActivity.class);
        intent.putExtra("PRODUCT", newProduct);
        startActivityForResult(intent, 300);
        break;
    }
//        Intent webIntent = new Intent(getActivity(), ProductCatalogActivity.class);
//        webIntent.putExtra("IS_ADD", true);
//        startActivity(webIntent);
//        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
  }

  public class MyOnClickListener implements View.OnClickListener {
    private final Context context;

    private MyOnClickListener(Context context) {
      this.context = context;
    }

    @Override
    public void onClick(View v) {
      Log.d("Click Listener", " Listener : " + v.getId());
      int selectedItemPosition = recyclerView.getChildPosition(v);
      Intent webIntent = new Intent(context, Card_Full_View_MainActivity.class);
      webIntent.putExtra("POSITION", selectedItemPosition);
      webIntent.putExtra("IS_DASHBOARD", true);
      startActivity(webIntent);
      getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }


       /* private void removeItem(View v) {
            int selectedItemPosition = recyclerView.getChildPosition(v);
            RecyclerView.ViewHolder viewHolder
                    = recyclerView.findViewHolderForPosition(selectedItemPosition);
            TextView textViewName
                    = (TextView) viewHolder.itemView.findViewById(R.id.textViewName);
            String selectedName = (String) textViewName.getText();
            int selectedItemId = -1;
            for (int i = 0; i < MyData.nameArray.length; i++) {
                if (selectedName.equals(MyData.nameArray[i])) {
                    selectedItemId = MyData.id_[i];
                }
            }
            removedItems.add(selectedItemId);
            card.remove(selectedItemPosition);
            adapter.notifyItemRemoved(selectedItemPosition);
        }*/
  }

  void shareContent(String type, String imageShare, int position) {
    MixPanelController.track("SharePost", null);
    this.type = type;
    this.imageShare = imageShare;
    this.position = position;
    FloatsMessageModel floatsMessageModel = Home_Main_Fragment.getMessageList(current_Activity).get(position);
    if (ActivityCompat.checkSelfPermission(current_Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      Methods.showDialog(current_Activity, "Storage Permission", "To share service image, we need storage permission.",
          (dialog, which) -> requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_CODE));
      return;
    }
    ProgressDialog pd = ProgressDialog.show(current_Activity, "", "Sharing . . .");
    String productType = CodeUtilsKt.getProductType(session.getFP_AppExperienceCode());
    String subDomain = "";
    if (productType.equals("PRODUCTS")) {
      subDomain = "all-products";
    } else {
      subDomain = "all-services";
    }
//    final Intent shareIntent = new Intent(Intent.ACTION_SEND);
    String message = floatsMessageModel.message;
    String url = floatsMessageModel.url;
    String rootAliasURI = session.getRootAliasURI();
    String imageUri = floatsMessageModel.imageUri;
    switch (type) {
      case "whatsapp":
        ContentSharing.Companion.shareUpdates(requireActivity(),message,url, rootAliasURI + "/"+subDomain,session.getUserPrimaryMobile(),true,false,imageUri);
        break;
      case "facebook":
        ContentSharing.Companion.shareUpdates(requireActivity(),message,url, rootAliasURI + "/"+subDomain,session.getUserPrimaryMobile(),false,true,imageUri);
        break;
      default:ContentSharing.Companion.shareUpdates(requireActivity(),message,url, rootAliasURI + "/"+subDomain,session.getUserPrimaryMobile(),false,false,imageUri);


    }
      pd.dismiss();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == STORAGE_CODE) {
      if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        shareContent(type, imageShare, position);
      } else Toast.makeText(current_Activity, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
    }
  }
}