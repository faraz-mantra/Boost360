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
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.Home_Main_Fragment;
import com.nowfloats.sync.DbController;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by Kamal on 17-02-2015.
 */
public class Home_View_Card_Delete  extends AsyncTask<Void,String, String> {
    private static String url;
    private static JSONObject values;
    boolean flag = false;
    int position;
    Activity activity;
    CardAdapter_v2 refresh;
    View v;
    UserSessionManager session;
    int retryKey = 0;
    private boolean dataExists = false;
    public interface CardRefresh{
        public void cardrefresh(boolean flag);
    }
    CardRefresh cardrefresh;
    public Home_View_Card_Delete(Activity activity,String Url,JSONObject Content,int position,View v,int key){
        this.url = Url;
        this.values = Content;
        this.activity = activity;
        this.position = position;
        this.v = v;
        this.retryKey = key;
        flag =false;
        try {
            cardrefresh = (CardRefresh) activity;
        }catch (Exception e){e.printStackTrace();}

        session = new UserSessionManager(activity.getApplicationContext(),activity);
    }
    @Override
    protected void onPreExecute() {}
    @Override
    protected void onPostExecute(final String result)
    {
        String temp = null;
        if(flag)
        {
            try{
                Log.i("IMAGE---","VISIBLE RETRY LayOut");
                if (Home_Main_Fragment.progressBar!=null && Home_Main_Fragment.retryLayout!=null && retryKey!=0){
                    //Home_Main_Fragment.progressBar.setVisibility(View.GONE);
                    Home_Main_Fragment.progressCrd.setVisibility(View.VISIBLE);
                    Home_Main_Fragment.retryLayout.setVisibility(View.VISIBLE);
                }

                if(Home_Main_Fragment.cAdapter != null ){/*&& Home_Main_Fragment.scaleAdapter != null*/
                    Home_Main_Fragment.cAdapter.notifyDataSetChanged();
//                    Home_Main_Fragment.scaleAdapter.notifyDataSetChanged();
                }

                DbController.getDbController(activity).deleteUpdate(new String[]{HomeActivity.StorebizFloats.get(position)._id});
                HomeActivity.StorebizFloats.remove(position);
                temp	=	"Its Gone!";
                refresh = new CardAdapter_v2(null,activity);
                refresh.notifyItemRemoved(position);
                refresh.notifyDataSetChanged();

                getMessages(session.getFPID());
                Log.i("Delete POST---",""+temp);
            }catch(Exception e){
                e.printStackTrace();
                Log.i("Delete POST---","Exception");
            }
        }
        else
        {
            temp	=	"error";
            Log.i("Delete POST---",""+temp);

            try {
                Home_View_Card_Delete cardDelete = new Home_View_Card_Delete(activity, url, values, position, v,retryKey);
                cardDelete.execute();
            } catch(Exception e )
            {
                e.printStackTrace();
            }

        }
    }


    @Override
    protected String doInBackground(Void... params) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpDeleteWithBody del = new HttpDeleteWithBody(url);
        StringEntity se;
        try {
            se = new StringEntity(values.toString(), HTTP.UTF_8);
            se.setContentType("application/json");

            del.setEntity(se);

            HttpResponse response = httpclient.execute(del);

            StatusLine status = response.getStatusLine();
            Log.i("Delete POST---","status----"+status);

            if (status.getStatusCode() == 200) {
                Log.i("Upload Delete msg...","Success");
                flag = true;
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  null;
    }

    public void getMessages(String fpId)
    {
        HashMap<String,String> map = new HashMap<>();
        map.put("clientId",Constants.clientId);
        map.put("skipBy","0");
        map.put("fpId",fpId);
        Login_Interface login_interface = Constants.restAdapter.create(Login_Interface.class);
        login_interface.getMessages(map,new Callback<MessageModel>() {
            @Override
            public void success(MessageModel messageModel, retrofit.client.Response response) {
                parseMessages(messageModel);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    public void parseMessages(MessageModel response) {
        if (response!=null)
        {
            ArrayList<FloatsMessageModel> bizData 	= response.floats;
            Constants.moreStorebizFloatsAvailable 	= response.moreFloatsAvailable;
            for(int i = 0 ; i < bizData.size() ;i++){
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
                    if(!dataExists) {
                        HomeActivity.StorebizFloats.add(data);
                    }
                }
            }
            if(HomeActivity.StorebizFloats!=null && HomeActivity.StorebizFloats.size()==0){
                if (Home_Main_Fragment.emptyMsgLayout!=null && !Constants.isWelcomScreenToBeShown)
                    Home_Main_Fragment.emptyMsgLayout.setVisibility(View.VISIBLE);
            }
            if (cardrefresh!=null)
                cardrefresh.cardrefresh(true);
        }
    }
}