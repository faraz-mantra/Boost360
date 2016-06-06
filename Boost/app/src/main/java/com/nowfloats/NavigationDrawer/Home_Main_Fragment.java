package com.nowfloats.NavigationDrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.gc.materialdesign.views.Card;
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
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.ButteryProgressBar;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class Home_Main_Fragment extends Fragment implements
        Fetch_Home_Data.Fetch_Home_Data_Interface {
    public static LinearLayout retryLayout,emptyMsgLayout;
    public static ButteryProgressBar progressBar;
    public static Card progressCrd;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public static RecyclerView recyclerView;
    private static ArrayList<CardData> card;
    private static ArrayList<Integer> removedItems;
    static View.OnClickListener myOnClickListener;
    Fetch_Home_Data fetch_home_data ;
    FloatingActionButton fabButton ;
    UserSessionManager session;
    private static final String DATA_ARG_KEY = "HomeFragment.DATA_ARG_KEY";
    public static CardAdapter_V3 cAdapter;
    JSONObject data;
    String msg = "", date = "";
    String imageUri = "";
    //UserSessionManager session;
    public static Bus bus;
    OnRenewPlanClickListener mCallback = null;

    private ArrayList<Object> mNewWelcomeTextImageList = new ArrayList<Object>();
    private int visibilityFlag = 1;
    public static UploadPostEvent recentPostEvent = null;
    private String ImageResponseID = "";
    public static int facebookPostCount = 0;
    public static AlphaInAnimationAdapter alphaAdapter ;
    public static ScaleInAnimationAdapter scaleAdapter ;
    public Activity current_Activity;
    public Home_Main_Fragment() {}

    public static Home_Main_Fragment newInstance() {
        Home_Main_Fragment fragment = new Home_Main_Fragment();
        Bundle args = new Bundle();
        return fragment;
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
        Log.d("Home_Main_Fragment","onResume : "+session.getFPName());
        getActivity().setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
    }

    @Subscribe
    public void uploadProcess(UploadPostEvent event){
        try {
            Log.i("upload msg ...", "TRIGeREd");
            recentPostEvent = event;
            progressCrd.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
            fetch_home_data.setNewPostListener(true);
            fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
            uploadPicture(event.path, event.msg, getActivity(), new SampleUploadListener(), new UserSessionManager(getActivity(), getActivity()));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Subscribe
    public void ImageUploadCheck(PostImageSuccessEvent event){
        ImageResponseID = event.imageResponseId;
        Log.i("IMAGE---","Image UpLoAd Check Triggered");
        fetch_home_data.setInterfaceType(1);
        fetch_home_data.setNewPostListener(true);
        fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
        fetch_home_data.getMessages(session.getFPID(),"0");
    }

    @Subscribe
    public void TextUploadCheck(PostTextSuccessEvent event){
        if (event.status){
            event.status = false;
            Log.i("TEXT---","TeXt UpLoAd Check Triggered");
            fetch_home_data.setInterfaceType(0);
            fetch_home_data.setNewPostListener(true);
            fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
            fetch_home_data.getMessages(session.getFPID(),"0");
            Create_Message_Activity.path = "";
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView ;
        mainView =  inflater.inflate(R.layout.fragment_home__main_, container, false);
        fetch_home_data = new Fetch_Home_Data(getActivity(),0);
        session = new UserSessionManager(getActivity().getApplicationContext(),getActivity());
        return mainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Home_Main_Fragment","onViewCreated");

        progressCrd = (Card)view.findViewById(R.id.progressCard);
        progressBar = (ButteryProgressBar)view.findViewById(R.id.progressbar);
        retryLayout = (LinearLayout)view.findViewById(R.id.postRetryLayout);
        emptyMsgLayout = (LinearLayout)view.findViewById(R.id.emptymsglayout);
        emptyMsgLayout.setVisibility(View.GONE);
        ImageView retryPost = (ImageView)view.findViewById(R.id.retryPost);
        ImageView cancelPost = (ImageView)view.findViewById(R.id.cancelPost);
        PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
        retryPost.setColorFilter(whiteLabelFilter);
        cancelPost.setColorFilter(whiteLabelFilter);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
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
                    Methods.showSnackBarNegative(getActivity(),"Retry time out, Create new post");
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

       if(Constants.isWelcomScreenToBeShown == true) {
            Welcome_Card_Model welcome_card_model = new Welcome_Card_Model();
            welcome_card_model.webSiteName = session.getFPName();
            mNewWelcomeTextImageList.add(welcome_card_model);
       }

        if(HomeActivity.StorebizFloats!=null && HomeActivity.StorebizFloats.size()==0&& !Constants.isWelcomScreenToBeShown){
            if (emptyMsgLayout!=null)
                emptyMsgLayout.setVisibility(View.VISIBLE);
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
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
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
                int checkLoad = fetch_home_data.getInterfaceType();
                if (checkLoad==0){
                    Log.d("Home_Main_Fragment", "current_Page : " + current_page);
                    if (Constants.moreStorebizFloatsAvailable == true) {
                        if (current_page == 2) {
                            fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
                            fetch_home_data.getMessages(session.getFPID(), "10");
                        } else if (current_page == 3) {
                            fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
                            fetch_home_data.getMessages(session.getFPID(), "20");
                        }else if (current_page == 4) {
                            fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
                            fetch_home_data.getMessages(session.getFPID(), "30");
                        }
                        else if (current_page == 5) {
                            fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
                            fetch_home_data.getMessages(session.getFPID(), "40");
                        }else if (current_page == 6) {
                            fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
                            fetch_home_data.getMessages(session.getFPID(), "50");
                        }else if (current_page == 7) {
                            fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
                            fetch_home_data.getMessages(session.getFPID(), "60");
                        }
                        //}
                    } else {
//                        Methods.showSnackBar(getActivity(), "No More Messages");
                    }
                }
            }
        });

        fabButton = (FloatingActionButton) view.findViewById(R.id.fab);

        //session.storeFacebookPage("true");

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1") || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("0")) {
                    mCallback.onRenewPlanSelected();
                } else {
                    Intent webIntent = new Intent(getActivity(), Create_Message_Activity.class);
                    startActivity(webIntent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        cAdapter = new CardAdapter_V3(getActivity(),session);
//        alphaAdapter = new AlphaInAnimationAdapter(cAdapter);
//        scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
//        scaleAdapter.setFirstOnly(false);
//        scaleAdapter.setInterpolator(new OvershootInterpolator());
        recyclerView.setAdapter(cAdapter);

        if (HomeActivity.StorebizFloats==null || HomeActivity.StorebizFloats.size()==0){
            HomeActivity.StorebizFloats = new ArrayList<FloatsMessageModel>();
            fetch_home_data.setFetchDataListener(Home_Main_Fragment.this);
            fetch_home_data.getMessages(session.getFPID(), "10");
        }
    }

    private void sendIsInterested(FragmentActivity activity, String fpid, String planType,final Bus bus) {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("clientId", Constants.clientId);
            params.put("plantype", planType);
            new Restricted_FP_Service(activity, fpid, params, bus);
        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public void dataFetched() {
        Log.d("Home_Main_Fragment","dataFetched ");
        cAdapter.notifyDataSetChanged();
//        scaleAdapter.notifyDataSetChanged();
        current_Activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressCrd != null && progressBar != null) {
                    progressCrd.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    recentPostEvent = null;
                }
            }
        });
        Constants.createMsg = false;
    }

    @Override
    public void sendFetched(FloatsMessageModel messageModel) {
        Log.i("IMAGE---interface"," Triggered");
        try {
            Log.i("IMAGE---","{0}_id=="+messageModel._id+"\n deal Id=="+ImageResponseID+"\nURL ="+messageModel.imageUri);
            if(messageModel._id.equals(ImageResponseID) && !messageModel.imageUri.contains(Constants.NOW_FLOATS_API_URL)){
                Create_Message_Activity.path = "";
                if (progressCrd!=null && progressBar!=null){
                    progressCrd.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    recentPostEvent = null;
                }
                Constants.createMsg = false;
                Log.i("IMAGE---", "UPLoaD SucceSS");
                cAdapter.notifyDataSetChanged();
//                scaleAdapter.notifyDataSetChanged();
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
                Log.i("IMAGE---","CALing DeLEte Method");
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


        private void removeItem(View v) {
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
        }
    }

    private class SampleUploadListener implements AsyncFacebookRunner.RequestListener {
        @Override
        public void onComplete(String s, Object o) {
            Log.d("Complete",s);
        }

        @Override
        public void onIOException(IOException e, Object o) {
            Log.d("Complete",e.toString());
        }

        @Override
        public void onFileNotFoundException(FileNotFoundException e, Object o) {
            Log.d("Complete",e.toString());
        }

        @Override
        public void onMalformedURLException(MalformedURLException e, Object o) {
            Log.d("Complete",e.toString());
        }

        @Override
        public void onFacebookError(FacebookError facebookError, Object o) {
            Log.d("Complete",facebookError.toString());
        }
    }

    public void uploadPicture(String path,String msg,Activity act,SampleUploadListener uploadListener,UserSessionManager session) {
        Log.d("Image : ", "Upload Pic Path : "+path);
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
        PostTaskModel task = new PostTaskModel(Constants.clientId,msg,Create_Message_Activity.imageIconButtonSelected,
                merchantId,parentId,Create_Message_Activity.tosubscribers);
        if (facebookPostCount==0) {
            if (Constants.fbShareEnabled) {
                Create_Message_Activity.postUser = new com.nowfloats.NavigationDrawer.API.PostModel(msg);
            }

            if (Constants.fbPageShareEnabled) {
                if (Constants.FbPageList != null && Constants.FbPageList.length() > 0) {
                    Create_Message_Activity.postPage = new com.nowfloats.NavigationDrawer.API.PostModel(msg, Constants.FbPageList);
                }
            }

            if(Create_Message_Activity.facbookEnabled)
                postOnFacebookWall(msg, act, uploadListener,session);
            if(Create_Message_Activity.isFacebookPageShareLoggedIn)
                postOnFacebookPage(msg, session, act);
        }
        UploadMessageTask upa =new UploadMessageTask(act, path, task,session);
        upa.UploadPostService();
    }

    private void postOnFacebookWall(String msg, Activity act, SampleUploadListener uploadlistener,UserSessionManager session) {
        try {
            byte[] data = null;
            String FACEBOOK_ACCESS_TOKEN = session.getFacebookAccessToken();
            if (FACEBOOK_ACCESS_TOKEN == null) {

            } else if (msg.length() >= 1) {
                if (Create_Message_Activity.path == null || Create_Message_Activity.path.trim().length() == 0) {
                    Bundle params = new Bundle();
                    params.putString(Facebook.TOKEN, FACEBOOK_ACCESS_TOKEN);
                    params.putString("message", msg);
//                Create_Message_Activity.facebook.setAccessToken(FACEBOOK_ACCESS_TOKEN);
//                AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(Create_Message_Activity.facebook);
//                mAsyncRunner.request("me/feed", params, "POST", uploadlistener, null);

                    Request.Callback callback = new Request.Callback() {
                        public void onCompleted(Response response) {
                            FacebookRequestError error = response.getError();
                            if (error != null) {
                                Log.e("FACEBOOK WALL ERROR", "" + error.getErrorMessage());
                            } else {
                                JSONObject graphResponse = response
                                        .getGraphObject()
                                        .getInnerJSONObject();
                                String postId = null;
                                try {
                                    postId = graphResponse.getString("id");
                                } catch (JSONException e) {
                                }
                            }
                        }
                    };

                    Request request = new Request(null, "me/feed", params, HttpMethod.POST, callback);
                    RequestAsyncTask task = new RequestAsyncTask(request);
                    task.execute();
                } else {
                    Bitmap bmp = Util.getBitmap(Create_Message_Activity.path, act);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    data = baos.toByteArray();
                    Bundle params = new Bundle();
                    params.putString(Facebook.TOKEN, FACEBOOK_ACCESS_TOKEN);
                    params.putString("message", msg);
                    //params.putString("method", "photos.upload");
                    params.putByteArray("picture", data); // image to post

                    Create_Message_Activity.facebook.setAccessToken(FACEBOOK_ACCESS_TOKEN);
                    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(Create_Message_Activity.facebook);
                    mAsyncRunner.request("me/photos", params, "POST", uploadlistener, null);
                }
            }
        }catch(Exception e){e.printStackTrace();}
        //param.putByteArray("picture", ImageBytes);
        // mAsyncRunner.request("me/photos", param, "POST", new SampleUploadListener());
    }

    public void postOnFacebookPage(String msg, UserSessionManager session, Activity act)
    {
        try {
            if (Create_Message_Activity.path == null || Create_Message_Activity.path.trim().length() == 0) {
                Bundle postParams = new Bundle();
                postParams.putString("message", msg);
                postParams.putString("name", msg);
                postParams.putString("access_token", session.getPageAccessToken());

                Request.Callback callback = new Request.Callback() {

                    public void onCompleted(Response response) {
                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            Log.e("FACEBOOK PAGE ERROR", "" + error.getErrorMessage());
                        } else {
                            JSONObject graphResponse = response
                                    .getGraphObject()
                                    .getInnerJSONObject();
                            String postId = null;
                            try {
                                postId = graphResponse.getString("id");
                            } catch (JSONException e) {
                            }
                        }
                    }
                };
                //postParams.putString("");
                Request request = new Request(null, session.getFacebookPageID() + "/feed", postParams, HttpMethod.POST, callback);
                RequestAsyncTask task = new RequestAsyncTask(request);
                task.execute();
            } else {
                Bundle postParams = new Bundle();
                Bitmap bmp = Util.getBitmap(Create_Message_Activity.path, act);
                postParams.putString("message", msg);
                postParams.putString("name", msg);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                //  postParams.putString("link",link);
                postParams.putByteArray("picture", data);
                postParams.putString("access_token", session.getPageAccessToken());

                Request.Callback callback = new Request.Callback() {

                    public void onCompleted(Response response) {
                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            Log.e("FACEBOOK PAGE ERROR", "" + error.getErrorMessage());
                        } else {
                            JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
                            String postId = null;
                            try {
                                postId = graphResponse.getString("id");
                            } catch (JSONException e) {
                            }
                        }
                    }
                };
                //postParams.putString("");
                Request request = new Request(null, session.getFacebookPageID() + "/photos", postParams, HttpMethod.POST, callback);
                RequestAsyncTask task = new RequestAsyncTask(request);
                task.execute();
            }
        }catch (Exception e){e.printStackTrace();}
    }
    /*
    * This listener for callback to home
    * */

     interface OnRenewPlanClickListener {
         void onRenewPlanSelected();
    }
//    @Subscribe
//    public void getStoreList(StoreEvent response){
//        ArrayList<StoreModel> allModels = response.model.AllPackages;
//        ArrayList<ActiveWidget> activeIdArray = response.model.ActivePackages;
//        ArrayList<StoreModel> additionalPlans = response.model.AllPackages;
//        if(allModels!=null && activeIdArray!=null){
//            LoadActivePlans(getActivity(),allModels,additionalPlans,activeIdArray);
//        }else{
//            Methods.showSnackBarNegative(getActivity(),"Something went wrong");
//        }
//    }
//    public static ArrayList<StoreModel> activeWidgetModels = new ArrayList<>();
//    public static ArrayList<StoreModel> additionalWidgetModels = new ArrayList<>();

//    private void LoadActivePlans(final Activity activity, ArrayList<StoreModel> allModels, final ArrayList<StoreModel> additionalPlan, ArrayList<ActiveWidget> acIdarray) {
//        try {
//            if (acIdarray!=null && acIdarray.size()>0){
//                for (int i = 0; i < allModels.size(); i++) {
//                    for (int j=0; j < acIdarray.size(); j++){
//                        if (allModels.get(i)._id.equals(acIdarray.get(j).clientProductId)){
//                            activeWidgetModels.add(allModels.get(i));
//                            Log.d("Load Plans",activeWidgetModels.get(i).ExpiryInMths);
//                        }
//                    }
//                }
//                for (int i = 0; i < acIdarray.size(); i++) {
//                    for (int j = 0; j < allModels.size(); j++) {
//                        if (allModels.get(j)._id.equals(acIdarray.get(i).clientProductId)){
//                            additionalPlan.remove(allModels.get(j));
//                            Log.d("Load Plans",additionalPlan.get(i).ExpiryInMths);
//                        }
//                    }
//                }
//                additionalWidgetModels = additionalPlan;
//            }else {
//                additionalWidgetModels = allModels;
//            }
//        }catch (Exception e){e.printStackTrace(); Methods.showSnackBarNegative(activity,"Something went wrong,Please try again");}
//    }
}
