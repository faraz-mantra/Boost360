package com.nowfloats.NavigationDrawer.API;

import com.nowfloats.NavigationDrawer.model.DomainDetails;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Model;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.squareup.otto.Bus;

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
        DOMAIN_NOT_AVAILABLE
    }

    public DomainApiService(Bus bus) {
        this.mBus = bus;
    }

    public void getDomainDetails(String fpTag, HashMap<String, String> data) {
        DomainInterface domainInterface = Constants.pluginRestAdapter.create(DomainInterface.class);
        domainInterface.getDomainDetailsForFloatingPoint(fpTag, data, new Callback<DomainDetails>() {
            @Override
            public void success(DomainDetails domainDetails, Response response) {
                if (domainDetails != null) {
                    domainDetails.response = true;
                }else{
                    domainDetails = new DomainDetails();
                    domainDetails.response = false;
                }
                mBus.post(domainDetails);
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("DomainApiService", error.getMessage());
                DomainDetails domainDetails = new DomainDetails();
                domainDetails.response = false;
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

    public void checkDomainAvailability(String domainName, HashMap<String, String> data) {
        DomainInterface domainInterface = Constants.pluginRestAdapter.create(DomainInterface.class);
        domainInterface.checkDomainAvailability(domainName, data, new Callback<Boolean>() {
            @Override
            public void success(Boolean flag, Response response) {
                if(flag)
                    mBus.post(DomainAPI.CHECK_DOMAIN);
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

    public void linkDomain(HashMap<String, String> bodyData, HashMap<String, String> data) {
        DomainInterface domainInterface = Constants.riaRestAdapter.create(DomainInterface.class);
        domainInterface.linkDomain(data,bodyData,new Callback<Boolean>() {
            @Override
            public void success(Boolean flag, Response response) {
                if(flag)
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
        domainInterface.buyDomain(bodyData,new Callback<String>() {
            @Override
            public void success(String domainMsg, Response response) {
                mBus.post(domainMsg);
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("DomainApiService", error.getMessage());
                mBus.post(DomainAPI.ERROR_DOMAIN);
            }
        });

    }


    public void getDomainFPDetails(String fpId, HashMap<String, String> data) {
        DomainInterface domainInterface = Constants.restAdapter.create(DomainInterface.class);
        domainInterface.getFPDetails(fpId,data,new Callback<Get_FP_Details_Model>() {
            @Override
            public void success(Get_FP_Details_Model get_fp_details_model, Response response) {
                mBus.post(get_fp_details_model);
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("DomainApiService", error.getMessage());
                Get_FP_Details_Model get_fp_details_model = new Get_FP_Details_Model();
                get_fp_details_model.response = error.getMessage()+"";
                mBus.post(get_fp_details_model);
            }
        });

    }
}
