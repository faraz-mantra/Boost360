package com.nowfloats.Store;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Adapters.ItemsRecyclerViewAdapter;
import com.nowfloats.Store.Model.PaymentTokenResult;
import com.nowfloats.Store.Model.PurchaseDetail;
import com.nowfloats.Store.Model.ReceiveDraftInvoiceModel;
import com.nowfloats.Store.Model.ReceivedDraftInvoice;
import com.nowfloats.Store.Model.SendDraftInvoiceModel;
import com.nowfloats.Store.Model.StoreModel;
import com.nowfloats.Store.Model.SupportedPaymentMethods;
import com.nowfloats.Store.Model.TaxDetail;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.Volley.AppController;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DataBase;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.romeo.mylibrary.Models.OrderDataModel;
import com.romeo.mylibrary.ui.InstaMojoMainActivity;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *  interface
 * to handle interaction events.
 * Use the {@link OPCFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OPCFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    CheckBox cbIhaveOpc;
    TextInputLayout tilOpcInput;
    EditText etOpcInput;
    Button btnProceed;
    LinearLayout llOpcdetails;

    private OrderDataModel mOrderData;

    MaterialDialog materialProgress;
    private final int DIRECT_REQUEST_CODE = 1;
    private final int OPC_REQUEST_CODE = 2;

    UserSessionManager session;

    public OPCFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OPCFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OPCFragment newInstance() {
        OPCFragment fragment = new OPCFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        session = new UserSessionManager(getActivity().getApplicationContext(),getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_opc, container, false);
        cbIhaveOpc = (CheckBox) view.findViewById(R.id.cb_i_have_opc);
        tilOpcInput = (TextInputLayout) view.findViewById(R.id.til_opc_input);
        etOpcInput = (EditText) view.findViewById(R.id.et_opc_input);
        btnProceed = (Button) view.findViewById(R.id.btn_proceed_with_opc);
        llOpcdetails = (LinearLayout) view.findViewById(R.id.ll_opc_details);


        tilOpcInput.setErrorEnabled(true);
        tilOpcInput.setHint("Enter OPC");


        materialProgress = new MaterialDialog.Builder(getActivity())
                .widgetColorRes(R.color.accentColor)
                .content("Please Wait...")
                .cancelable(false)
                .progress(true, 0).build();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        cbIhaveOpc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY).equalsIgnoreCase("India")){
                        showDialog("Alert!", getResources().getString(R.string.not_available_in_ur_country));
                        cbIhaveOpc.setChecked(false);
                    }else {
                        llOpcdetails.setVisibility(View.VISIBLE);
                    }
                }else {
                    llOpcdetails.setVisibility(View.GONE);
                }
            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDraftInvoice(etOpcInput.getText().toString().trim(), tilOpcInput);
                etOpcInput.setText("");
            }
        });
    }



    private void createDraftInvoice(String OPCCode, final TextInputLayout layout) {
        if(Util.isNullOrEmpty(OPCCode)){
            layout.setError("OPC can't be empty");
            return;
        }
        try {
            SendDraftInvoiceModel sendDraftInvoiceModel = new SendDraftInvoiceModel();
            //PurchaseDetail purchaseDetail = new PurchaseDetail();
            DataBase dataBase = new DataBase(getActivity());

            Cursor cursor = dataBase.getLoginStatus();
            if (cursor.moveToFirst()){
                //System.out.println(cursor.getString(cursor.getColumnIndex("title"));
                sendDraftInvoiceModel.setFpUserProfileId(cursor.getString(cursor.getColumnIndex(DataBase.colloginId)));
                sendDraftInvoiceModel.setOpc(OPCCode);
            }else {
                Toast.makeText(getActivity(), R.string.login_status_error, Toast.LENGTH_SHORT).show();
                return;
            }
            sendDraftInvoiceModel.setPurchaseDetails(null);

            Map<String, String> params = new HashMap<String, String>();
            params.put("clientId", Constants.clientId);
            if(materialProgress!=null){
                materialProgress.show();
            }
            StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
            storeInterface.createDraftInvoice(params, sendDraftInvoiceModel, new Callback<ReceivedDraftInvoice>() {
                @Override
                public void success(ReceivedDraftInvoice receiveDraftInvoice, Response response) {
                    if(receiveDraftInvoice!=null){
                        if(materialProgress!=null){
                            materialProgress.dismiss();
                        }
                        if(receiveDraftInvoice.getError().getErrorList()==null || receiveDraftInvoice.getStatusCode()==200) {
                            showInvoiceDialog(receiveDraftInvoice.getResult());
                        }else {
                            layout.setError(receiveDraftInvoice.getError().getErrorList().get(0).Key);
                        }
                    }else {
                        if(materialProgress!=null){
                            materialProgress.dismiss();
                        }
                        Methods.showSnackBarNegative(getActivity(), getResources().getString(R.string.error_invoice));
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    if(materialProgress!=null){
                        materialProgress.dismiss();
                    }
                    Methods.showSnackBarNegative(getActivity(), getResources().getString(R.string.error_invoice));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            if(materialProgress!=null){
                materialProgress.dismiss();
            }
            Toast.makeText(getActivity(), "Error while generating Invoice", Toast.LENGTH_SHORT).show();
        }
    }

    private void showInvoiceDialog(final ReceiveDraftInvoiceModel invoiceData) {
        if(invoiceData==null){
            return;
        }
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.invoice_dialog_layout);

        TextView tvUserName = (TextView) dialog.findViewById(R.id.tv_username);
        TextView tvUserEmail = (TextView) dialog.findViewById(R.id.tv_user_email);
        TextView tvPhoneNumber = (TextView) dialog.findViewById(R.id.tv_user_phone_no);
        TextView tvNetTotal = (TextView) dialog.findViewById(R.id.tv_net_total);
        TextView tvTaxes = (TextView) dialog.findViewById(R.id.tv_taxes);
        TextView tvAmountToBePaid = (TextView) dialog.findViewById(R.id.tv_amount_to_be_paid);

        tvUserName.setText(session.getFpTag().toLowerCase());
        tvUserEmail.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
        tvPhoneNumber.setText(session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM));
        double amountToBePaid = 0;
        double totalDiscountedPrice = 0;
        for(PurchaseDetail data : invoiceData.getPurchaseDetails()){
            amountToBePaid+= data.getMRP();
        }
        for(PurchaseDetail data : invoiceData.getPurchaseDetails()){
            totalDiscountedPrice+= (data.getBasePrice() - ((data.getBasePrice()*data.getDiscount().value)/100));
        }

        StringBuilder taxNames= new StringBuilder();
        //double taxAmount = 0;
        for(TaxDetail taxData : invoiceData.getPurchaseDetails().get(0).getTaxDetails()){
            taxNames.append(taxData.getKey() + "-" + taxData.getValue() +"%,\n ");
        }
        double taxAmount = amountToBePaid-totalDiscountedPrice;
        tvNetTotal.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " + Math.round(totalDiscountedPrice*100.0)/100.0 + " /-");
        tvTaxes.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " + Math.round(taxAmount*100.0)/100.0 + " /-\n" + "( " + taxNames.substring(0, taxNames.length() - 3) + " )");

        tvAmountToBePaid.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " + (amountToBePaid) + " /-");
        String packages="";
        for(int i=0; i<invoiceData.getPurchaseDetails().size(); i++){
            packages+=invoiceData.getPurchaseDetails().get(i).getPackageName() + " and ";
        }
        final String newPackage = packages;
        final String finalAmount = String.valueOf(Math.round(amountToBePaid*100.0)/100.0);
        dialog.findViewById(R.id.btn_pay_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i = new Intent(getActivity(), InstaMojoMainActivity.class);
                mOrderData = new OrderDataModel(session.getFpTag(), session.getFpTag(),
                        session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL),
                        finalAmount, newPackage.substring(0, newPackage.length()-4),
                        session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM),
                        "NowFloats Package", invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode());
                i.putExtra(com.romeo.mylibrary.Constants.PARCEL_IDENTIFIER, mOrderData);
                initiatePaymentProcess(i, invoiceData.getInvoiceId(), invoiceData);
            }
        });


        RecyclerView rvItems = (RecyclerView) dialog.findViewById(R.id.rv_store_items);
        rvItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        ItemsRecyclerViewAdapter adapter = new ItemsRecyclerViewAdapter(invoiceData.getPurchaseDetails(), invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode());
        rvItems.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.show();
    }

    private void initiatePaymentProcess(final Intent i, String invoiceId, ReceiveDraftInvoiceModel invoiceModel) {
        StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("clientId", Constants.clientId);
        params.put("invoiceId", invoiceId);

        /*
         *Remove this code
         */
        //List<InitiatePaymentModel> supportedpaymentMethods = new ArrayList<>();
        List<SupportedPaymentMethods> paymentMethods = null;
        for(PurchaseDetail purchaseDetail : invoiceModel.getPurchaseDetails()){
            boolean packageAvilableflag = false;
            for(StoreModel storeModel : StoreFragmentTab.additionalWidgetModels){
                if(purchaseDetail.getPackageId().equals(storeModel._id)){
                    packageAvilableflag  =true;
                    paymentMethods = storeModel.SupportedPaymentMethods;
                    break;
                }
            }
            if(!packageAvilableflag){
                Methods.showSnackBarNegative(getActivity(), "Error while initiating payment process.");
                return;
            }
        }
        SupportedPaymentMethods method = null;
        if(paymentMethods!=null) {
            for (SupportedPaymentMethods paymentMethod : paymentMethods) {
                if (paymentMethod.Type == 1) {
                    method = paymentMethod;
                }
            }
        }else {
            Methods.showSnackBarNegative(getActivity(), "Error while initiating payment process.");
            return;
        }
        /*SupportedPaymentMethods method = new SupportedPaymentMethods();
        method.Key = "53d97d8086e9c14b6695e5973cb0b08d";
        method.PaymentSource = "INSTAMOJO";
        method.Secret = "a208e391a8a49488b7d204faa50c983d";
        method.TargetPaymentUri = null;
        method.Type = 1;*/
        //supportedpaymentMethods.add(method);

        /*InitiatePaymentModel initiatePaymentModel = new InitiatePaymentModel();
        initiatePaymentModel.Key = method.Key;
        initiatePaymentModel.PaymentSource = method.PaymentSource;
        initiatePaymentModel.Secret = method.Secret;
        initiatePaymentModel.TargetPaymentUri = null;
        initiatePaymentModel.Type = method.Type;
        initiatePaymentModel.RedirectUri = null;
        initiatePaymentModel.WebHookUri = "http://54.254.184.142/Payment/v1/floatingpoint/instaMojoWebHook";*/
        if (materialProgress!=null){
            materialProgress.show();
        }
        storeInterface.initiatePaymentProcess(params, /*product.SupportedPaymentMethods.get(0)*/ method, new Callback<PaymentTokenResult>() {
            @Override
            public void success(PaymentTokenResult paymentTokenResult, Response response) {
                if(paymentTokenResult!=null && paymentTokenResult.getResult()!=null) {
                    i.putExtra(com.romeo.mylibrary.Constants.PAYMENT_REQUEST_IDENTIFIER, paymentTokenResult.getResult().getPaymentRequestId());
                    i.putExtra(com.romeo.mylibrary.Constants.ACCESS_TOKEN_IDENTIFIER, paymentTokenResult.getResult().getAccessToken());
                    i.putExtra(com.romeo.mylibrary.Constants.WEB_HOOK_IDENTIFIER, "https://api.withfloats.com/Payment/v1/floatingpoint/instaMojoWebHook?clientId="+Constants.clientId);//change this later
                    if (materialProgress!=null){
                        materialProgress.dismiss();
                    }
                    startActivityForResult(i, OPC_REQUEST_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }else {
                    if (materialProgress!=null){
                        materialProgress.dismiss();
                    }
                    Methods.showSnackBarNegative(getActivity(), "Error while processing payment");

                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (materialProgress!=null){
                    materialProgress.dismiss();
                }
                Methods.showSnackBarNegative(getActivity(), "Error while processing payment");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==DIRECT_REQUEST_CODE || requestCode==OPC_REQUEST_CODE && resultCode==getActivity().RESULT_OK){
            if(data==null){
                return;
            }
            final boolean success = data.getBooleanExtra(com.romeo.mylibrary.Constants.RESULT_SUCCESS_KEY, false);
            final String status = data.getStringExtra(com.romeo.mylibrary.Constants.RESULT_STATUS);
            final String message = data.getStringExtra(com.romeo.mylibrary.Constants.ERROR_MESSAGE);
            final String paymentId = data.getStringExtra(com.romeo.mylibrary.Constants.PAYMENT_ID);
            final String transactionId = data.getStringExtra(com.romeo.mylibrary.Constants.TRANSACTION_ID);
            final String amount = data.getStringExtra(com.romeo.mylibrary.Constants.FINAL_AMOUNT);
            //sendEmail(success, status, message, paymentId, transactionId, amount);
            BoostLog.d("TransaCtionId", transactionId);
            if(success) {
                if(status.equals("Success")) {

                    MixPanelController.track(EventKeysWL.PAYMENT_SUCCESSFULL, null);

                    /*String msg = "Thank you! \n" +
                            "The Payment ID for your transaction is " + paymentId +". Your package will be activated within 24 hours. \n" +
                            "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
                    showDialog(status, msg);*/
                    pollServerforStatus(transactionId, paymentId, status);
                }
            }else {
                if(status.equals("Pending")){
                    String msg = "Alert! \n" +
                            "Your payment is pending. Once your payment is successful, your package will be activated within 24 hours. The Payment ID for your transaction is " + paymentId +" . \n" +
                            "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
                    showDialog(status, msg);
                }else if (status.equals("Failure")){
                    String msg = "Sorry! \n" +
                            "This transaction failed. To retry, please go to the Store and pay again. \n" +
                            "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
                    showDialog(status, msg);
                }
            }
        }
    }

    private void pollServerforStatus(final String transactionId, final String paymentid, final String status) {
        if(!materialProgress.isShowing()) {
            materialProgress.show();
        }
        String url = Constants.NOW_FLOATS_API_URL + "/payment/v1/floatingpoint/getPaymentStatus?clientId=" + Constants.clientId +"&paymentRequestId=" + transactionId;
        JsonObjectRequest request  =new JsonObjectRequest(Request.Method.POST, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("Result").equals("SUCCESS")) {
                        if(materialProgress!=null){
                            materialProgress.dismiss();
                            String msg = "Thank you! \n" +
                                    "The Payment ID for your transaction is " + paymentid +". Your package will be activated within 24 hours. \n" +
                                    "You can reach customer support at ria@nowfloats.com or 1860-123-1233 for any queries.";
                            showDialog(status, msg);
                        }
                    }else {
                        pollServerforStatus(transactionId, paymentid, status);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    materialProgress.dismiss();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String msg = "Your PaymentId is: " + paymentid + ". Please Contact Customer Support.";
                materialProgress.dismiss();
                showDialog(status, msg);
            }
        });
        AppController.getInstance().addToRequstQueue(request);
    }

    private void showDialog(String title, String msg){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(msg).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}
