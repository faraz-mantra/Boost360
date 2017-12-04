package com.nowfloats.NavigationDrawer.API;

import android.content.Context;
import android.text.TextUtils;

import com.nowfloats.NavigationDrawer.model.DomainDetails;
import com.nowfloats.NavigationDrawer.model.EmailBookingModel;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Model;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;

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
    private DomainCallback domainCallback;

    public enum DomainAPI {

        CHECK_DOMAIN,
        LINK_DOMAIN,
        ERROR_DOMAIN,
        DOMAIN_NOT_AVAILABLE,
        RENEW_DOMAIN,
        RENEW_NOT_AVAILABLE
    }

    public interface DomainCallback{
        void getDomainDetails(DomainDetails details);
        void emailBookingStatus(ArrayList<EmailBookingModel.EmailBookingStatus> bookingStatuses);
        void  getEmailBookingList(ArrayList<String> ids);
        void getDomainSupportedTypes(ArrayList<String> arrExtensions);
        void domainAvailabilityStatus(DomainApiService.DomainAPI domainAPI);
        void domainBookStatus(String response);
        void getFpDetails(Get_FP_Details_Model model);
    }
    public enum EmailBookingStatus{
        NOT_INITIATED,
        VALIDATED,
        ORDER_COMPLETED,
        USERADD_COMPLETED ,
        DNSFETCH_COMPLETED,
        DNSUPDATE_COMPLETED,
        COMPLETED;
    }
    public DomainApiService(DomainCallback callback) {
        domainCallback = callback;
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
                domainCallback.getDomainDetails(domainDetails);
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("DomainApiService", error.getMessage());
                DomainDetails domainDetails = new DomainDetails();
                domainDetails.response = DomainDetails.DOMAIN_RESPONSE.ERROR;
                domainCallback.getDomainDetails(domainDetails);
            }
        });

    }

    public void getDomainSupportedTypes(HashMap<String, String> data) {
        DomainInterface domainInterface = Constants.pluginRestAdapter.create(DomainInterface.class);
        domainInterface.getDomainSupportedTypes(data, new Callback<List<String>>() {
            @Override
            public void success(List<String> domainSupportedTypes, Response response) {
                domainCallback.getDomainSupportedTypes((ArrayList<String>)domainSupportedTypes);
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("DomainApiService", error.getMessage());
                domainCallback.getDomainSupportedTypes(new ArrayList<String>());
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
                        domainCallback.domainAvailabilityStatus(flag ? DomainAPI.RENEW_DOMAIN:DomainAPI.RENEW_NOT_AVAILABLE);
                        break;
                    default:
                        domainCallback.domainAvailabilityStatus(flag ? DomainAPI.CHECK_DOMAIN:DomainAPI.DOMAIN_NOT_AVAILABLE);
                        break;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("DomainApiService", error.getMessage());
                domainCallback.domainAvailabilityStatus(DomainAPI.ERROR_DOMAIN);
            }
        });

    }

    public void bookEmail(String clientId, EmailBookingModel model){
        DomainInterface domainInterface = Constants.pluginRestAdapter.create(DomainInterface.class);
        domainInterface.bookEmails(clientId, model,new Callback<ArrayList<String>>() {
            @Override
            public void success(ArrayList<String> emailBookingIds, Response response) {

                domainCallback.getEmailBookingList(emailBookingIds);
            }

            @Override
            public void failure(RetrofitError error) {
                domainCallback.getEmailBookingList(null);
            }
        });
    }
    public void emailsBookingStatus(String clientId, String fpTag){
        DomainInterface domainInterface = Constants.pluginRestAdapter.create(DomainInterface.class);
        domainInterface.emailStatus(clientId, fpTag, new Callback<ArrayList<EmailBookingModel.EmailBookingStatus>>() {
            @Override
            public void success(ArrayList<EmailBookingModel.EmailBookingStatus> emailBookingStatuses, Response response) {
                if (emailBookingStatuses != null){
                    domainCallback.emailBookingStatus(emailBookingStatuses);
                }else{
                    domainCallback.emailBookingStatus(new ArrayList<EmailBookingModel.EmailBookingStatus>(0));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                domainCallback.emailBookingStatus(new ArrayList<EmailBookingModel.EmailBookingStatus>(0));
            }
        });
    }
    public void linkDomain(HashMap<String, String> bodyData, HashMap<String, String> data) {
        DomainInterface domainInterface = Constants.riaRestAdapter.create(DomainInterface.class);
        domainInterface.linkDomain(data, bodyData, new Callback<Boolean>() {
            @Override
            public void success(Boolean flag, Response response) {
                if (flag)
                    domainCallback.domainAvailabilityStatus(DomainAPI.LINK_DOMAIN);
                else
                    domainCallback.domainAvailabilityStatus(DomainAPI.DOMAIN_NOT_AVAILABLE);
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("DomainApiService", error.getMessage());
                domainCallback.domainAvailabilityStatus(DomainAPI.ERROR_DOMAIN);
            }
        });

    }

    public void buyDomain(HashMap<String, String> bodyData) {
        DomainInterface domainInterface = Constants.pluginRestAdapter.create(DomainInterface.class);
        domainInterface.buyDomain(bodyData, new Callback<String>() {
            @Override
            public void success(String domainMsg, Response response) {

                if (TextUtils.isEmpty(domainMsg) || response.getStatus() != HttpStatus.SC_OK) {
                    domainMsg = "Something went wrong. Please try again later.";
                }else{
                    domainMsg = "Your Domain will be activated within 48 hours.";
                }
                domainCallback.domainBookStatus(domainMsg);
            }

            @Override
            public void failure(RetrofitError error) {
                domainCallback.domainBookStatus("Something went wrong. Please try again later.");
            }
        });

    }


    public void getDomainFPDetails(String fpId, HashMap<String, String> data) {
        DomainInterface domainInterface = Constants.restAdapter.create(DomainInterface.class);
        domainInterface.getFPDetails(fpId, data, new Callback<Get_FP_Details_Model>() {
            @Override
            public void success(Get_FP_Details_Model get_fp_details_model, Response response) {
                domainCallback.getFpDetails(get_fp_details_model);
            }

            @Override
            public void failure(RetrofitError error) {
                BoostLog.d("DomainApiService", error.getMessage());
                Get_FP_Details_Model get_fp_details_model = new Get_FP_Details_Model();
                get_fp_details_model.response = "Unable to connect with server";
                domainCallback.getFpDetails(get_fp_details_model);
            }
        });

    }
}
