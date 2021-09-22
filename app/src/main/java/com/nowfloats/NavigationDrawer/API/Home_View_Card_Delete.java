package com.nowfloats.NavigationDrawer.API;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.nowfloats.CustomWidget.HttpDeleteWithBody;
import com.nowfloats.Login.Login_Interface;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.Model.MessageModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.CardAdapter_v2;
import com.nowfloats.NavigationDrawer.Card_Full_View_MainActivity;
import com.nowfloats.NavigationDrawer.Home_Main_Fragment;
import com.nowfloats.sync.DbController;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
import com.nowfloats.util.WebEngageController;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

import static com.framework.webengageconstant.EventLabelKt.DELETED_UPDATE;
import static com.framework.webengageconstant.EventLabelKt.UNABLE_TO_DELETE_UPDATE;
import static com.framework.webengageconstant.EventNameKt.DELETE_AN_UPDATE;

/**
 * Created by Kamal on 17-02-2015.
 */
public class Home_View_Card_Delete extends AsyncTask<Void, String, String> {
    private static String url;
    private static JSONObject values;
    boolean flag = false;
    int position;
    Activity activity;
    CardAdapter_v2 refresh;
    View v;
    UserSessionManager session;
    int retryKey = 0;
    boolean isDashboard = false;
    CardRefresh cardrefresh;
    private boolean dataExists = false;

    public Home_View_Card_Delete(Activity activity, String Url, JSONObject Content, int position, View v, int key) {
        this.url = Url;
        this.values = Content;
        this.activity = activity;
        this.position = position;
        this.v = v;
        this.retryKey = key;
        flag = false;
        try {
            cardrefresh = (CardRefresh) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }

        session = new UserSessionManager(activity.getApplicationContext(), activity);
    }

    public void setDashboard(boolean isDashboard) {
        this.isDashboard = isDashboard;
    }

    @Override
    protected void onPostExecute(final String result) {
        String temp = null;
        if (flag) {
            try {
                Log.i("IMAGE---", "VISIBLE RETRY LayOut");
                if (/*Home_Main_Fragment.progressBar!=null &&*/ Home_Main_Fragment.retryLayout != null && retryKey != 0) {
                    //Home_Main_Fragment.progressBar.setVisibility(View.GONE);
                    Home_Main_Fragment.progressCrd.setVisibility(View.VISIBLE);
                    Home_Main_Fragment.retryLayout.setVisibility(View.VISIBLE);
                }

                if (Home_Main_Fragment.cAdapter != null) {/*&& Home_Main_Fragment.scaleAdapter != null*/
                    Home_Main_Fragment.cAdapter.notifyDataSetChanged();
//                    Home_Main_Fragment.scaleAdapter.notifyDataSetChanged();
                }

                DbController.getDbController(activity).deleteUpdate(new String[]{Card_Full_View_MainActivity.getMessageList(isDashboard).get(position)._id});
                Card_Full_View_MainActivity.getMessageList(isDashboard).remove(position);
                temp = "Its Gone!";
                WebEngageController.trackEvent(DELETE_AN_UPDATE, DELETED_UPDATE, session.getFpTag());
                refresh = new CardAdapter_v2(null, activity);
                refresh.notifyItemRemoved(position);
                refresh.notifyDataSetChanged();

                getMessages(session.getFPID());
                Log.i("Delete POST---", "" + temp);
            } catch (Exception e) {
                e.printStackTrace();
                if (cardrefresh != null) cardrefresh.error(true);
                Log.i("Delete POST---", "Exception");
            }
        } else {
            temp = "error";
            Log.i("Delete POST---", "" + temp);
            WebEngageController.trackEvent(DELETE_AN_UPDATE, UNABLE_TO_DELETE_UPDATE, session.getFpTag());

            try {
                Home_View_Card_Delete cardDelete = new Home_View_Card_Delete(activity, url, values, position, v, retryKey);
                cardDelete.execute();
            } catch (Exception e) {
                e.printStackTrace();
                if (cardrefresh != null) cardrefresh.error(true);
            }

        }
    }

    @Override
    protected void onPreExecute() {
    }

    public void getMessages(String fpId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("clientId", Constants.clientId);
        map.put("skipBy", "0");
        map.put("fpId", fpId);
        Login_Interface login_interface = Constants.restAdapter.create(Login_Interface.class);
        login_interface.getMessages(map, new Callback<MessageModel>() {
            @Override
            public void success(MessageModel messageModel, retrofit.client.Response response) {
                parseMessages(messageModel);
            }

            @Override
            public void failure(RetrofitError error) {
                if (cardrefresh != null) cardrefresh.error(true);
            }
        });
    }


    @Override
    protected String doInBackground(Void... params) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpDeleteWithBody del = new HttpDeleteWithBody(url);
        del.setHeader("Authorization", Utils.getAuthToken());
        StringEntity se;
        try {
            se = new StringEntity(values.toString(), HTTP.UTF_8);
            se.setContentType("application/json");


            del.setEntity(se);

            HttpResponse response = httpclient.execute(del);

            StatusLine status = response.getStatusLine();
            Log.i("Delete POST---", "status----" + status);

            if (status.getStatusCode() == 200) {
                Log.i("Upload Delete msg...", "Success");
                flag = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void parseMessages(MessageModel response) {
        if (response != null) {
            ArrayList<FloatsMessageModel> bizData = response.floats;
            Constants.moreStorebizFloatsAvailable = response.moreFloatsAvailable;
            for (int i = 0; i < bizData.size(); i++) {
                FloatsMessageModel data = bizData.get(i);
                if (Card_Full_View_MainActivity.getMessageList(isDashboard) != null) {
                    String formatted = Methods.getFormattedDate(data.createdOn);
                    data.createdOn = formatted;

                    for (int j = 0; j < Card_Full_View_MainActivity.getMessageList(isDashboard).size(); j++) {
                        if (Card_Full_View_MainActivity.getMessageList(isDashboard).get(j)._id.equals(data._id)) {
                            dataExists = true;
                            break;
                        } else {
                            dataExists = false;
                        }
                    }
                    if (!dataExists) {
                        Card_Full_View_MainActivity.getMessageList(isDashboard).add(data);
                    }
                }
            }
            if (Card_Full_View_MainActivity.getMessageList(isDashboard) != null && Card_Full_View_MainActivity.getMessageList(isDashboard).size() == 0) {
                if (Home_Main_Fragment.emptyMsgLayout != null && !Constants.isWelcomScreenToBeShown)
                    Home_Main_Fragment.emptyMsgLayout.setVisibility(View.VISIBLE);
            }
            if (cardrefresh != null) cardrefresh.cardrefresh(true);
        } else if (cardrefresh != null) cardrefresh.error(true);
    }

    public interface CardRefresh {
        public void cardrefresh(boolean flag);

        default void error(boolean flag) {

        }
    }
}