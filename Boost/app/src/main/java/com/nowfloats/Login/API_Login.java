package com.nowfloats.Login;

import android.app.Activity;

import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.Model.Login_Data_Model;
import com.nowfloats.Login.Model.MessageModel;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DataBase;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nowfloats.util.Constants.clientId;

/**
 * Created by Dell on 17-01-2015.
 */
public class API_Login {

    private Activity appContext ;
    UserSessionManager session;
    Bus bus;
    String temp [];

    public interface API_Login_Interface{
        public void authenticationStatus(String value);
        public void authenticationFailure(String value);
    }

    API_Login_Interface apiInterface ;
    public API_Login(Activity context,UserSessionManager currentSession,Bus bus)
    {
        appContext = context ;
        session = currentSession;
        apiInterface = (API_Login_Interface) context ;
        this.bus = bus;
    }

    public void authenticate(String userName, String password, final String clientId)
    {
    BoostLog.d("AUthenticate","Usrname : "+userName+" , Pwd : "+password+" Client Id : "+clientId);
    HashMap<String, String> params = new HashMap<String, String>();
    params.put("loginKey", userName);
    params.put("loginSecret", password);

        params.put("clientId", clientId);
       /* try {
            Constants.restAdapter = Methods.createAdapter(appContext,Constants.NOW_FLOATS_API_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Login_Interface api_login_request = Constants.restAdapter.create(Login_Interface.class);
        api_login_request.authenticationProcess(params, new Callback<Login_Data_Model>() {
            @Override
            public void success(Login_Data_Model response_Data, Response response) {
                try {
                    if (response_Data.ValidFPIds != null && response_Data.ValidFPIds.size() > 0) {
                        String fpId = response_Data.ValidFPIds.get(0).toString();
                        session.storeFPID(fpId);
                        if (response_Data.sourceClientId!=null && response_Data.sourceClientId.trim().length()>0)
                            session.storeSourceClientId(response_Data.sourceClientId);
                        if(clientId.equals(Constants.clientIdThinksity)){
                            if(response_Data.sourceClientId!=null && response_Data.sourceClientId.trim().length()>0
                                    && response_Data.sourceClientId.equals(Constants.clientIdThinksity))
                            {
                                session.storeIsThinksity("true");
//                                session.storeIsRestricted(response_Data.isRestricted);
                                session.storeISEnterprise(response_Data.isEnterprise);
                                DataBase dataBase = new DataBase(appContext);
                                dataBase.insertLoginStatus(response_Data, fpId);
                                //  dataBase.insertFPIDs(response_Data.ValidFPIds);
                                //getMessages(fpId);
                                bus.post(new ArrayList<FloatsMessageModel>());
                            }else{
                                apiInterface.authenticationFailure("true");
                                Methods.showSnackBarNegative(appContext,appContext.getString(R.string.check_your_crediential));
                            }
                        }
                        else{
                            //BOost Login
                            session.storeIsThinksity("false");
//                            session.storeIsRestricted(response_Data.isRestricted);
                            session.storeISEnterprise(response_Data.isEnterprise);
                            DataBase dataBase = new DataBase(appContext);
                            dataBase.insertLoginStatus(response_Data, fpId);
                            //  dataBase.insertFPIDs(response_Data.ValidFPIds);
                            //getMessages(fpId);
                            bus.post(new ArrayList<FloatsMessageModel>());
                        }
                        BoostLog.d("FPID: ", fpId);
                    } else {
                        apiInterface.authenticationFailure("true");
                        Methods.showSnackBarNegative(appContext, appContext.getString(R.string.check_your_crediential));
                    }
                } catch (Exception e) {
                    apiInterface.authenticationFailure("true");
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                StringBuffer networkError = new StringBuffer(appContext.getString(R.string.check_your_crediential));
                if(error.getKind() == RetrofitError.Kind.HTTP || error.getKind() == RetrofitError.Kind.NETWORK ){
                    networkError.delete(0, networkError.length()).append(appContext.getString(R.string.something_went_wrong));
                }
                apiInterface.authenticationFailure("true");
                Methods.showSnackBarNegative(appContext, networkError.toString());
            }
        });
    }

//    public static void unsubscribeRIA(String fpID,Activity activity)
//    {
//        final ProgressDialog pd ;
//
//        pd = ProgressDialog.show(activity, "", "Logging out ...");
//        pd.setCancelable(false);
//
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("clientId", Constants.clientId);
//        params.put("fpId",fpID);
//
//        Login_Interface api_login_request = Constants.restAdapter.create(Login_Interface.class);
//        api_login_request.logoutUnsubcribeRIA(params,new Callback<String>() {
//            @Override
//            public void success(String s, Response response) {
//                Log.d("Valid Email","Valid Email Response: "+response);
//               pd.dismiss();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                pd.dismiss();
//            }
//        });
//    }

    public void getMessages(String fpId)
    {
        HashMap<String,String> map = new HashMap<>();
        map.put("clientId", clientId);
        map.put("skipBy","0");
        map.put("fpId",fpId);
        Login_Interface login_interface = Constants.restAdapter.create(Login_Interface.class);
        login_interface.getMessages(map, new Callback<MessageModel>() {
            @Override
            public void success(MessageModel messageModel, Response response) {
                parseMessages(messageModel);

            }
            @Override
            public void failure(RetrofitError error) {
                apiInterface.authenticationFailure("true");
            }
        });
    }

    public void parseMessages(MessageModel response){
        ArrayList<FloatsMessageModel> bizData 		= response.floats;
        Constants.moreStorebizFloatsAvailable 	= response.moreFloatsAvailable;
        for(int i = 0 ; i < bizData.size() ;i++){
            FloatsMessageModel data = bizData.get(i);
            String formatted = Methods.getFormattedDate(data.createdOn.split("\\(")[1].split("\\)")[0]);
            data.createdOn = formatted;
        }
        bus.post(bizData);
    }
}
