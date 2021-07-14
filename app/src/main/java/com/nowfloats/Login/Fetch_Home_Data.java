package com.nowfloats.Login;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.Model.MessageModel;
import com.nowfloats.NavigationDrawer.API.VisitorsApiInterface;
import com.nowfloats.NavigationDrawer.Analytics_Fragment;
import com.nowfloats.NavigationDrawer.Home_Main_Fragment;
import com.nowfloats.NavigationDrawer.model.VisitAnalytics;
import com.nowfloats.sync.DbController;
import com.nowfloats.sync.model.Updates;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.MixPanelController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by Dell on 11-02-2015.
 */
public class Fetch_Home_Data {
    public int interfaceType = 0;
    public Fetch_Home_Data_Interface fetchHomeDataInterface = null;
    Activity appActivity;
    private FloatsMessageModel sendJson = null;
    private boolean dataExists = false;
    private boolean newPost = false, interfaceInvoke = true;
    private DbController mDbController;
    private UserSessionManager sessionManager;

    public Fetch_Home_Data(Activity activity, int type) {
        appActivity = activity;
        setInterfaceType(type);
        mDbController = DbController.getDbController(appActivity);
    }

    /**
     * @param context
     * @param session
     */
    public Fetch_Home_Data(Activity context, UserSessionManager session) {
        super();
        appActivity = context;
        sessionManager = session;
    }

    public int getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(int interfaceType) {
        this.interfaceType = interfaceType;
    }

    public void setFetchDataListener(Fetch_Home_Data_Interface fetchHomeDataInterface) {
        this.fetchHomeDataInterface = fetchHomeDataInterface;
    }

    public void setNewPostListener(boolean value) {
        this.newPost = value;
    }

    public void getMessages(final String fpId, final String skipByCount) {
        Log.d("Fetch_Home_Data", "getMessages : " + fpId);
        HashMap<String, String> map = new HashMap<>();
        map.put("clientId", Constants.clientId);
        map.put("skipBy", skipByCount);
        map.put("fpId", fpId);
        Login_Interface login_interface = Constants.restAdapter.create(Login_Interface.class);
        login_interface.getMessages(map, new Callback<MessageModel>() {
            @Override
            public void success(MessageModel messageModel, retrofit.client.Response response) {
                parseMessages(messageModel, fpId, skipByCount, false);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    public void getNewAvailableMessage(String messageId, final String fpId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("clientId", Constants.clientId);
        map.put("messageId", messageId);
        map.put("merchantId", fpId);
        Login_Interface login_interface = Constants.restAdapter.create(Login_Interface.class);
        login_interface.getNewAvailableMessage(map, new Callback<MessageModel>() {
            @Override
            public void success(MessageModel messageModel, retrofit.client.Response response) {
                parseMessages(messageModel, fpId, "0", true);
            }

            @Override
            public void failure(RetrofitError error) {
                if (fetchHomeDataInterface != null && interfaceType == 0) {
                    fetchHomeDataInterface.dataFetched(0, false);
                } else if (fetchHomeDataInterface != null && interfaceType == 1) {
                    fetchHomeDataInterface.sendFetched(sendJson);
                }
            }
        });
    }

    public void parseMessages(MessageModel response, String fpId, String skip, boolean isNewMessage) {
        BoostLog.d("Called Parse Message: ", "Parsing Message");
        if (response != null) {
            interfaceInvoke = true;
            ArrayList<FloatsMessageModel> bizData = response.floats;
            Constants.moreStorebizFloatsAvailable = response.moreFloatsAvailable;
            if (bizData != null && bizData.size() > 0) {
                sendJson = bizData.get(0);
                Constants.NumberOfUpdates = Home_Main_Fragment.getMessageList(appActivity).size();
                MixPanelController.setProperties("NoOfUpdates", "" + Constants.NumberOfUpdates);

                /*for (int i = 0; i < bizData.size(); i++) {
                    FloatsMessageModel data = bizData.get(i);
                    if (HomeActivity.StorebizFloats!=null) {
                        String formatted = Methods.getFormattedDate(data.createdOn);
                        data.createdOn = formatted;

                        for (int j = 0; j < HomeActivity.StorebizFloats.size(); j++) {
                           if (HomeActivity.StorebizFloats.get(j)._id.equals(data._id)) {
                               dataExists = true; break;
                           }else{
                               dataExists = false;
                           }
                        }

                        if(newPost) {
                            if (dataExists && interfaceType==0){
                                interfaceInvoke = false;
                                //TODO delay
                                getMessages(fpId,skip);
                                break;
                            }else{
                                newPost = false;
                                HomeActivity.StorebizFloats.add(0,data);
                            }
                        }else if(!dataExists){
                            HomeActivity.StorebizFloats.add(data);
                        }
                    }
                }*/
                for (int i = 0; i < bizData.size(); i++) {
                    FloatsMessageModel data = bizData.get(i);
                    Updates update = new Updates();
                    update.setServerId(data._id)
                            .setDate(data.createdOn.split("\\(")[1].split("\\)")[0])
                            .setImageUrl(data.imageUri)
                            .setSynced(1)
                            .setTileImageUrl(data.imageUri)
                            .setType(data.type)
                            .setUpdateText(data.message)
                            .setUrl(data.url);
                    mDbController.postUpdate(update);
                    BoostLog.d("Saving To Db:", "Oh Saved to Db" + data.message);
                }
            }

            if (Home_Main_Fragment.getMessageList(appActivity) != null && Home_Main_Fragment.getMessageList(appActivity).size() == 0) {
                if (Home_Main_Fragment.emptyMsgLayout != null && !Constants.isWelcomScreenToBeShown) {
                    Home_Main_Fragment.emptyMsgLayout.setVisibility(View.VISIBLE);
                }
            } else {
                if (Home_Main_Fragment.emptyMsgLayout != null)
                    Home_Main_Fragment.emptyMsgLayout.setVisibility(View.GONE);
            }

            if (interfaceInvoke) {
                if (fetchHomeDataInterface != null && interfaceType == 0) {
                    fetchHomeDataInterface.dataFetched(Integer.parseInt(skip), isNewMessage);
                } else if (fetchHomeDataInterface != null && interfaceType == 1) {
                    fetchHomeDataInterface.sendFetched(sendJson);
                }
            }
        } else {

            if (fetchHomeDataInterface != null && interfaceType == 0) {
                fetchHomeDataInterface.dataFetched(Integer.parseInt(skip), isNewMessage);
            } else if (fetchHomeDataInterface != null && interfaceType == 1) {
                fetchHomeDataInterface.sendFetched(sendJson);
            }

        }
    }

    public void getVisitors() {
        /**
         * Create calendar instance
         */
        Calendar calendar = Calendar.getInstance();

        /**
         * Get current date object from calendar and call method to convert to UTC
         */
        Date currentDate = localToGMT(calendar.getTime());

        /**
         * Subtract 2 months from current date
         */
        calendar.add(Calendar.MONTH, -2);

        /**
         * Get date object 2 months before
         */
        Date previousDate = localToGMT(calendar.getTime());

        /**
         * Format current date and previous date
         */
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String startDate = df.format(previousDate);
        String endDate = df.format(currentDate);

        Log.d("VisitorsApiInterface", "Start Date : " + startDate);
        Log.d("VisitorsApiInterface", "End Date : " + endDate);

        List<String> array = new ArrayList<>();
        array.add(sessionManager.getFPID());

        Log.d("VisitorsApiInterface", "FP ID : " + array);


        /**
         * Create HashMap for query string parameter
         */
        HashMap<String, String> map = new HashMap<>();

        map.put("clientId", Constants.clientId);
        map.put("startDate", startDate /*"2018-12-05"*/);
        map.put("endDate", endDate /*"2019-02-05"*/);
        map.put("batchType", "DAILY");
        map.put("scope", sessionManager.getISEnterprise().equals("true") ? "1" : "0");

        /**
         * Create object for retrofit API interface
         */
        VisitorsApiInterface visitors_interface = Constants.restAdapter.create(VisitorsApiInterface.class);

        visitors_interface.getVisitors(array, map, new Callback<List<VisitAnalytics>>() {

            @Override
            public void success(List<VisitAnalytics> visitAnalyticsList, retrofit.client.Response response) {
                /**
                 * Store visit count on session
                 */
                sessionManager.setVisitsCount(String.valueOf(getTotalVisits(visitAnalyticsList)));
                /**
                 * Store visitors count on session
                 */
                sessionManager.setVisitorsCount(String.valueOf(getTotalVisitors(visitAnalyticsList)));

                Log.d("VisitorsApiInterface", "Total Analytics Data - " + (visitAnalyticsList == null ? "NULL" : "" + visitAnalyticsList.size()));
                Log.d("VisitorsApiInterface", "Total Visits - " + sessionManager.getVisitsCount());
                Log.d("VisitorsApiInterface", "Total Visitors - " + sessionManager.getVisitorsCount());

                appActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        /**
                         * Display total visit count om TextView
                         */
                        if (Analytics_Fragment.visitCount != null && Analytics_Fragment.visits_progressBar != null) {
                            Analytics_Fragment.visitCount.setVisibility(View.VISIBLE);
                            Analytics_Fragment.visits_progressBar.setVisibility(View.GONE);
                            Analytics_Fragment.visitCount.setText(Analytics_Fragment.getNumberFormat(sessionManager.getVisitsCount()));
                        }

                        /**
                         * Display total visitors count om TextView
                         */
                        if (Analytics_Fragment.visitorsCount != null && Analytics_Fragment.visitors_progressBar != null) {
                            Analytics_Fragment.visitorsCount.setVisibility(View.VISIBLE);
                            Analytics_Fragment.visitors_progressBar.setVisibility(View.GONE);
                            Analytics_Fragment.visitorsCount.setText(Analytics_Fragment.getNumberFormat(sessionManager.getVisitorsCount()));
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("VisitorsApiInterface", "Fail - " + error.getMessage());
            }
        });
    }

    /**
     * Convert local date to UTC date
     *
     * @param date local date object
     * @return UTC date
     */
    private Date localToGMT(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return new Date(sdf.format(date));
    }

    /**
     * Count total visitors
     *
     * @param analytics list of analytics data
     * @return sum of visitors
     */
    private int getTotalVisitors(List<VisitAnalytics> analytics) {
        int sum = 0;

        if (analytics == null) {
            return sum;
        }

        for (VisitAnalytics value : analytics) {
            sum += value.getVisitors();
        }

        return sum;
    }

    /**
     * Count total visits
     *
     * @param analytics list of analytics data
     * @return sum of visits
     */
    private int getTotalVisits(List<VisitAnalytics> analytics) {
        int sum = 0;

        if (analytics == null) {
            return sum;
        }

        for (VisitAnalytics value : analytics) {
            sum += value.getVisits();
        }

        return sum;
    }

    public interface Fetch_Home_Data_Interface {
        public void dataFetched(int skip, boolean isNewMessage);

        public void sendFetched(FloatsMessageModel jsonObject);
    }
}