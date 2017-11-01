package com.nowfloats.NavigationDrawer.API;

import android.content.Context;
import android.text.TextUtils;

import com.nowfloats.NavigationDrawer.model.DomainDetails;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Model;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.squareup.otto.Bus;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloats on 04-11-2016.
 */

public class DomainApiService {
    Bus mBus;

    public enum DomainAPI {

        CHECK_DOMAIN,
        LINK_DOMAIN,
        ERROR_DOMAIN,
        DOMAIN_NOT_AVAILABLE,
        RENEW_DOMAIN,
        RENEW_NOT_AVAILABLE
    }

    public DomainApiService(Bus bus) {
        this.mBus = bus;
    }

    public void getDomainDetails(final Context context, String fpTag, HashMap<String, String> data) {
        /*new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
            DomainDetails domainDetails = null;
            try {
                InputStream is = context.getResources().getAssets().open("domain.json");
                Writer writer = new StringWriter();
                char[] buffer = new char[1024];
                try {
                    Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    int n;
                    while ((n = reader.read(buffer)) != -1) {
                        writer.write(buffer, 0, n);
                    }
                } finally {
                    is.close();
                }

                String jsonString = writer.toString();
                domainDetails = new Gson().fromJson(jsonString,DomainDetails.class);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {

                        if (domainDetails != null) {
                            domainDetails.response = true;
                        } else {
                            domainDetails = new DomainDetails();
                            domainDetails.response = false;
                        }
                        mBus.post(domainDetails);
                }
            }
        },1000);*/

        DomainInterface domainInterface = Constants.pluginRestAdapter.create(DomainInterface.class);
        domainInterface.getDomainDetailsForFloatingPoint(fpTag, data, new Callback<DomainDetails>() {
            @Override
            public void success(DomainDetails domainDetails, Response response) {
                if (domainDetails != null) {
                    domainDetails.response = DomainDetails.DOMAIN_RESPONSE.DATA;
                } else {
                    domainDetails = new DomainDetails();
                    domainDetails.response = DomainDetails.DOMAIN_RESPONSE.NO_DATA;
                }
                mBus.post(domainDetails);
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("DomainApiService", error.getMessage());
                DomainDetails domainDetails = new DomainDetails();
                domainDetails.response = DomainDetails.DOMAIN_RESPONSE.ERROR;
                mBus.post(domainDetails);
            }
        });

    }

    public void getDomainSupportedTypes(HashMap<String, String> data) {
        DomainInterface domainInterface = Constants.pluginRestAdapter.create(DomainInterface.class);
        domainInterface.getDomainSupportedTypes(data, new Callback<List<String>>() {
            @Override
            public void success(List<String> domainSupportedTypes, Response response) {
                mBus.post((ArrayList<String>) domainSupportedTypes);
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("DomainApiService", error.getMessage());
                mBus.post(new ArrayList<String>());
            }
        });

    }

    public void checkDomainAvailability(String domainName, HashMap<String, String> data, final DomainAPI domainApi) {
        DomainInterface domainInterface = Constants.pluginRestAdapter.create(DomainInterface.class);
        domainInterface.checkDomainAvailability(domainName, data, new Callback<Boolean>() {
            @Override
            public void success(Boolean flag, Response response) {
                switch (domainApi){
                    case RENEW_DOMAIN:
                        mBus.post(flag ? DomainAPI.RENEW_DOMAIN:DomainAPI.RENEW_NOT_AVAILABLE);
                        break;
                    default:
                        mBus.post(flag ? DomainAPI.CHECK_DOMAIN:DomainAPI.DOMAIN_NOT_AVAILABLE);
                        break;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("DomainApiService", error.getMessage());
                mBus.post(DomainAPI.ERROR_DOMAIN);
            }
        });

    }

    public void linkDomain(HashMap<String, String> bodyData, HashMap<String, String> data) {
        DomainInterface domainInterface = Constants.riaRestAdapter.create(DomainInterface.class);
        domainInterface.linkDomain(data, bodyData, new Callback<Boolean>() {
            @Override
            public void success(Boolean flag, Response response) {
                if (flag)
                    mBus.post(DomainAPI.LINK_DOMAIN);
                else
                    mBus.post(DomainAPI.DOMAIN_NOT_AVAILABLE);
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("DomainApiService", error.getMessage());
                mBus.post(DomainAPI.ERROR_DOMAIN);
            }
        });

    }

    public void buyDomain(HashMap<String, String> bodyData) {
        DomainInterface domainInterface = Constants.pluginRestAdapter.create(DomainInterface.class);
        domainInterface.buyDomain(bodyData, new Callback<String>() {
            @Override
            public void success(String domainMsg, Response response) {

                if (TextUtils.isEmpty(domainMsg) || response.getStatus() != HttpStatus.SC_OK) {
                    domainMsg = "Domain book error";
                }else{
                    domainMsg = "Your Domain will be activated within 48 hours.";
                }
                mBus.post(domainMsg);
            }

            @Override
            public void failure(RetrofitError error) {
                mBus.post(error.getMessage());
            }
        });

    }


    public void getDomainFPDetails(String fpId, HashMap<String, String> data) {
        DomainInterface domainInterface = Constants.restAdapter.create(DomainInterface.class);
        domainInterface.getFPDetails(fpId, data, new Callback<Get_FP_Details_Model>() {
            @Override
            public void success(Get_FP_Details_Model get_fp_details_model, Response response) {
                mBus.post(get_fp_details_model);
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("DomainApiService", error.getMessage());
                Get_FP_Details_Model get_fp_details_model = new Get_FP_Details_Model();
                get_fp_details_model.response = "Unable to connect with server";
                mBus.post(get_fp_details_model);
            }
        });

    }
}
