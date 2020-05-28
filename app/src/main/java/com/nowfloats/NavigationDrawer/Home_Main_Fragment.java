package com.nowfloats.NavigationDrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.melnykov.fab.FloatingActionButton;
import com.nowfloats.Login.Fetch_Home_Data;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.Home_View_Card_Delete;
import com.nowfloats.NavigationDrawer.API.Restricted_FP_Service;
import com.nowfloats.NavigationDrawer.model.Image_Text_Model;
import com.nowfloats.NavigationDrawer.model.PostImageSuccessEvent;
import com.nowfloats.NavigationDrawer.model.PostTaskModel;
import com.nowfloats.NavigationDrawer.model.PostTextSuccessEvent;
import com.nowfloats.NavigationDrawer.model.UploadPostEvent;
import com.nowfloats.NavigationDrawer.model.Welcome_Card_Model;
import com.nowfloats.NavigationDrawer.model.WhatsNewDataModel;
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
import com.nowfloats.widget.WidgetKey;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;


public class Home_Main_Fragment extends Fragment implements Fetch_Home_Data.Fetch_Home_Data_Interface {

    public static LinearLayout retryLayout,emptyMsgLayout;
    public ButteryProgressBar progressBar;
    public static CardView progressCrd;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public static RecyclerView recyclerView;
    private static ArrayList<CardData> card;
    private static ArrayList<Integer> removedItems;
    static View.OnClickListener myOnClickListener;
    Fetch_Home_Data fetch_home_data ;
    FloatingActionButton fabButton ;
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
    private DbController mDbController;
    public Home_Main_Fragment() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        MixPanelController.track(EventKeysWL.HOME_SCREEN, null);
        BoostLog.d("Home_Main_Fragment","onResume : "+session.getFPName());
        getActivity().setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));

    }

    private void inflateWhatsNew() {
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        if(!preferences.getString("currentAppVersion", "default").equals(getVersion())) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.whats_new_layout, null);



            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(v);
            final AlertDialog dialog = builder.show();
            RecyclerView rvWhatsNew = (RecyclerView) v.findViewById(R.id.rv_whats_new);
            Button done = (Button) v.findViewById(R.id.btn_whats_new_done);
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                    editor.putString("currentAppVersion", getVersion());
                    editor.commit();
                }
            });
            int images[] = {R.drawable.lock, R.drawable.share, R.drawable.camera, R.drawable.scope, R.drawable.chat};
            String[] headerText = {"Password Management", "Refer a Friend", "New Camera Experience", "Live Visitor Info", "Talk To NowFloats"};
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
    private String getVersion(){
        String val;
        try {
            val =  getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            val = "default";
        }
        return val;
    }

    @Subscribe
    public void uploadProcess(UploadPostEvent event){
        try {
            BoostLog.i("upload msg ...", "TRIGeREd");
            recentPostEvent = event;
            progressCrd.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
            fetch_home_data.setNewPostListener(true);
            fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
            uploadPicture(event.path, event.msg, event.mSocialShare, getActivity(), new UserSessionManager(getActivity(), getActivity()));
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(), "Unable to post Message", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void ImageUploadCheck(PostImageSuccessEvent event){
        ImageResponseID = event.imageResponseId;
        BoostLog.i("IMAGE---","Image UpLoAd sent_check Triggered");
        mIsNewMsg = true;
        getNewAvailableUpdates();
        mPref.edit().putString("msg_post","").apply();
        mPref.edit().putInt("quikrStatus",0).apply();
        mPref.edit().putString("image_post","").apply();
    }

    @Subscribe
    public void TextUploadCheck(PostTextSuccessEvent event){
        if (event.status){
            event.status = false;
            mIsNewMsg = true;
            BoostLog.i("TEXT---","TeXt UpLoAd sent_check Triggered");
            getNewAvailableUpdates();
            Create_Message_Activity.path = "";

            Constants.createMsg =false;
            mPref.edit().putString("msg_post","").apply();
            mPref.edit().putInt("quikrStatus",0).apply();
            mPref.edit().putString("image_post","").apply();
            //path = pref.getString("image_post",null);
        }
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus = BusProvider.getInstance().getBus();
        bus.register(this);
        current_Activity = getActivity();
        session = new UserSessionManager(getActivity(),getActivity());

        mPref = current_Activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        mDbController = DbController.getDbController(current_Activity);
        HomeActivity.StorebizFloats.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView ;
        mainView =  inflater.inflate(R.layout.fragment_home__main_, container, false);
        fetch_home_data = new Fetch_Home_Data(getActivity(),0);

        HomeActivity.StorebizFloats.clear();
        progressCrd = (CardView)mainView.findViewById(R.id.progressCard);
        progressBar = (ButteryProgressBar)mainView.findViewById(R.id.progressbar);
        retryLayout = (LinearLayout)mainView.findViewById(R.id.postRetryLayout);
        emptyMsgLayout = (LinearLayout)mainView.findViewById(R.id.emptymsglayout);
        emptyMsgLayout.setVisibility(View.GONE);


        ImageView retryPost = (ImageView)mainView.findViewById(R.id.retryPost);
        ImageView cancelPost = (ImageView)mainView.findViewById(R.id.cancelPost);
        PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
        retryPost.setColorFilter(whiteLabelFilter);
        cancelPost.setColorFilter(whiteLabelFilter);
        recyclerView = (RecyclerView) mainView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        cAdapter = new CardAdapter_V3(getActivity(),session);




        recyclerView.setAdapter(cAdapter);
        boolean isSynced = mPref.getBoolean(Constants.SYNCED, false);
        if(isSynced){

            if(Methods.isOnline(getActivity())) {
                BoostLog.d("OnViewCreated", "This is getting called");
                getNewAvailableUpdates();
            }else {
                loadDataFromDb(0, false);
            }



        }else {

            startSync();
        }

        retryPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recentPostEvent!=null){
                    facebookPostCount = 1;
                    bus.post(recentPostEvent);
                    retryLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    fetch_home_data.setNewPostListener(false);
                    fetch_home_data.setInterfaceType(0);
                    facebookPostCount = 0 ;
                    retryLayout.setVisibility(View.GONE);
                    progressCrd.setVisibility(View.GONE);
                    Methods.showSnackBarNegative(getActivity(),getString(R.string.retry_create_new_post));
                    Constants.createMsg = false;
                }
            }
        });

        cancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetch_home_data.setNewPostListener(false);
                fetch_home_data.setInterfaceType(0);
                facebookPostCount = 0 ;
                recentPostEvent = null;
                retryLayout.setVisibility(View.GONE);
                progressCrd.setVisibility(View.GONE);
                Constants.createMsg = false;
            }
        });

        if(Constants.isWelcomScreenToBeShown) {

            Welcome_Card_Model welcome_card_model = new Welcome_Card_Model();
            welcome_card_model.webSiteName = session.getFPName();
            mNewWelcomeTextImageList.add(welcome_card_model);

        }



        for(int i = 0 ; i < HomeActivity.StorebizFloats.size();i++)
        {
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
                    if (visibilityFlag == 0){
                        visibilityFlag = 1;
                        YoYo.with(Techniques.SlideInUp).interpolate(new DecelerateInterpolator()).duration(200)
                                .playOn(fabButton);
                    }
                } else if (scrollState == RecyclerView.SCROLL_STATE_DRAGGING){
                    if (visibilityFlag == 1){
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
                    int skipVal = (current_page-1)*10;
                    if(!loadDataFromDb(skipVal, false)){
                        fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
                        fetch_home_data.getMessages(session.getFPID(), String.valueOf(skipVal));
                        progressBar.setVisibility(View.GONE);
                    }


                }
            }
        });

        fabButton = mainView.findViewById(R.id.fab);
        fabButton.setOnClickListener(v -> addUpdate());

        return mainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BoostLog.d("Home_Main_Fragment","onViewCreated");

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

    private boolean loadDataFromDb(int skip, boolean isNewMessage) {

        if(mIsNewMsg){
            mIsNewMsg = false;
            HomeActivity.StorebizFloats.clear();



            cAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(cAdapter);
            Constants.createMsg = false;
        }
        List<Updates> updates = null;
        try {
            updates = mDbController.getAllUpdates(skip);
        }catch (Exception e){
            MixPanelController.track(MixPanelController.UPDATE_DB_CRASH,null);
            mPref.edit().putBoolean(com.nowfloats.util.Constants.SYNCED,false).apply();
            mDbController.deleteDataBase();
            startSync();
            return true;
        }
        if(updates == null || updates.isEmpty()){
            if (skip == 0 && maxSyncCall>0){
                maxSyncCall--;
                startSync();
            }
            return false;
        }
        if(emptyMsgLayout.getVisibility()==View.VISIBLE){
            emptyMsgLayout.setVisibility(View.GONE);
        }


        for (Updates update : updates) {
            FloatsMessageModel floatModel = new FloatsMessageModel(update.getServerId(), update.getDate(),
                    update.getImageUrl(), update.getUpdateText(), update.getTileImageUrl(), update.getType(), update.getUrl());
            HomeActivity.StorebizFloats.add(floatModel);
        }


        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(Constants.SYNCED, true);
        editor.apply();

        cAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        return true;

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
        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public void dataFetched(int skip, boolean isNewMessage) {

        Create_Message_Activity.path = "";
        Constants.createMsg = false;
        loadDataFromDb(skip, isNewMessage);
        bus.post(new UpdateFetchAfterPost());

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void sendFetched(FloatsMessageModel messageModel) {
        BoostLog.i("IMAGE---interface"," Triggered");
        try {
            BoostLog.i("IMAGE---","{0}_id=="+messageModel._id+"\n deal Id=="+ImageResponseID+"\nURL ="+messageModel.imageUri);
            if(messageModel._id.equals(ImageResponseID) && !messageModel.imageUri.contains(Constants.NOW_FLOATS_API_URL)){
                Create_Message_Activity.path = "";
                if (progressCrd!=null && progressBar!=null){
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
                facebookPostCount = 0 ;
            }else{
                JSONObject obj2 = new JSONObject();
                try {
                    obj2.put("dealId", ImageResponseID);
                    obj2.put("clientId", Constants.clientId1);
                } catch (Exception ex1) {
                    ex1.printStackTrace();
                }
                BoostLog.i("IMAGE---","CALing DeLEte Method");
                Home_View_Card_Delete deleteCard =  new Home_View_Card_Delete(getActivity(),Constants.DeleteCard,obj2,0,null,1);
                deleteCard.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public class MyOnClickListener implements View.OnClickListener {
        private final Context context;
        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Log.d("Click Listener", " Listener : "+v.getId());
            int selectedItemPosition = recyclerView.getChildPosition(v);
            Intent webIntent = new Intent(context, Card_Full_View_MainActivity.class);
            webIntent.putExtra("POSITION",selectedItemPosition);
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



    public void uploadPicture(String path,String msg, String socialShare, Activity act,UserSessionManager session) {
        BoostLog.d("Image : ", "Upload Pic Path : "+path);
        String merchantId = null,parentId=null;

        try {
            if(session.getISEnterprise().equals("true"))
            {
                merchantId = null;
            } else
            {   merchantId = session.getFPID();
            }
            if(session.getISEnterprise().equals("true")) {
                parentId = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PARENTID);
            } else {
                parentId = null;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        PostTaskModel task;
        if (!Util.isNullOrEmpty(path) && path.length() > 1){
            task = new PostTaskModel(Constants.clientId, msg, socialShare, Create_Message_Activity.imageIconButtonSelected,
                    merchantId, parentId, false);
        }else {
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
        UploadMessageTask upa =new UploadMessageTask(act, path, task,session);
        upa.UploadPostService();
    }



     interface OnRenewPlanClickListener {
         void onRenewPlanSelected();
    }


    private void openAddUpdateActivity()
    {
        Intent webIntent = new Intent(getActivity(), Create_Message_Activity.class);
        startActivity(webIntent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void addUpdate()
    {
        /**
         * If not new pricing plan
         */
        if(!WidgetKey.isNewPricingPlan)
        {
            if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1"))
            {
                Methods.showFeatureNotAvailDialog(getContext());
            }

            else
            {
                openAddUpdateActivity();
            }
        }

        else
        {
            String value = WidgetKey.getPropertyValue(WidgetKey.WIDGET_LATEST_UPDATES, WidgetKey.WIDGET_PROPERTY_MAX);

            if(value.equals(WidgetKey.WidgetValue.FEATURE_NOT_AVAILABLE.getValue()))
            {
                Methods.showFeatureNotAvailDialog(getContext());
            }

            else if(!value.equals(WidgetKey.WidgetValue.UNLIMITED.getValue()) && cAdapter.getItemCount() >= Integer.parseInt(value))
            {
                Toast.makeText(getContext(), String.valueOf(getString(R.string.message_add_update_limit)), Toast.LENGTH_LONG).show();
            }

            else
            {
                openAddUpdateActivity();
            }
        }
    }
}