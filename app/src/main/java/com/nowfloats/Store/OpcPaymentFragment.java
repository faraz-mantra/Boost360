package com.nowfloats.Store;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Adapters.ItemsRecyclerViewAdapter;
import com.nowfloats.Store.Model.OPCModels.UpdateDraftInvoiceModel;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Store.Model.PaymentTokenResult;
import com.nowfloats.Store.Model.PurchaseDetail;
import com.nowfloats.Store.Model.ReceiveDraftInvoiceModel;
import com.nowfloats.Store.Model.ReceivedDraftInvoice;
import com.nowfloats.Store.Model.SendDraftInvoiceModel;
import com.nowfloats.Store.Model.SupportedPaymentMethods;
import com.nowfloats.Store.Model.TaxDetail;
import com.nowfloats.Store.Service.OnPaymentOptionClick;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DataBase;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.framework.webengageconstant.EventLabelKt.BUTTON;
import static com.framework.webengageconstant.EventNameKt.APPLY_COUPON;
import static com.framework.webengageconstant.EventValueKt.CLICKED;

/**
 * Created by Admin on 13-04-2018.
 */

public class OpcPaymentFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private EditText opcEditText;
//    private OrderDataModel mOrderData;
    private UserSessionManager mSessionManager;
    ArrayList<ReceiveDraftInvoiceModel.KeyValuePair> mOpcDetails;
    private final int DIRECT_REQUEST_CODE = 2013;
    private final int OPC_REQUEST_CODE = 2;
    private final int PADDLE_REQUEST_CODE = 3;
    private String mInvoiceId;
    private List<PackageDetails> mPurchasePlans;
    TextView tvNetTotal, tvTaxes,
            tvAmountToBePaid, tvTdsAmount, btnPayNow, btnOpcApply;
    RecyclerView rvItems;

    TableRow trTanNo, trTdsAmount;

    public static Fragment getInstance(Bundle b) {
        Fragment frag = new OpcPaymentFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSessionManager = new UserSessionManager(mContext, getActivity());
        if (getArguments() != null) {
//            mOrderData = getArguments().getParcelable(com.romeo.mylibrary.Constants.PARCEL_IDENTIFIER);
            mPurchasePlans = new Gson().fromJson(getArguments().getString("packageList"), new TypeToken<List<PackageDetails>>() {
            }.getType());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            getActivity().setTitle("OPC Online Payment");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_opc_option, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnOpcApply = view.findViewById(R.id.textView_apply);
        btnOpcApply.setOnClickListener(this);

        btnPayNow = view.findViewById(R.id.textView_pay);
        btnPayNow.setOnClickListener(this);
        opcEditText = view.findViewById(R.id.editText_opc);
        tvNetTotal = (TextView) view.findViewById(R.id.tv_net_total);
        tvTaxes = (TextView) view.findViewById(R.id.tv_taxes);
        tvAmountToBePaid = (TextView) view.findViewById(R.id.tv_amount_to_be_paid);
        tvTdsAmount = (TextView) view.findViewById(R.id.tv_tds_amount);

        rvItems = (RecyclerView) view.findViewById(R.id.rv_store_items);
        trTdsAmount = (TableRow) view.findViewById(R.id.tr_tds_amount);

        preProcessAndDispatchPlans();
    }

    private void preProcessAndDispatchPlans() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PurchaseDetail> purchaseDetailList = new ArrayList<PurchaseDetail>();
                for (PackageDetails packageDetail : mPurchasePlans) {
                    PurchaseDetail purchaseDetail = new PurchaseDetail();
                    String clientId;
                    if (!Util.isNullOrEmpty(mSessionManager.getSourceClientId())) {
                        clientId = mSessionManager.getSourceClientId();
                    } else {
                        clientId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
                    }
                    double totalTax = 0;
                    if (packageDetail.getTaxes() != null) {
                        for (TaxDetail taxData : packageDetail.getTaxes()) {
                            totalTax += taxData.getValue();
                        }
                    }
                    purchaseDetail.setBasePrice(packageDetail.getPrice());
                    purchaseDetail.setClientId(clientId);
                    purchaseDetail.setDurationInMnths(packageDetail.getValidityInMths());
                    purchaseDetail.setFPId(mSessionManager.getFPID());
                    purchaseDetail.setMRP(packageDetail.getPrice() + (packageDetail.getPrice() * totalTax) / 100);
                    purchaseDetail.setMRPCurrencyCode(packageDetail.getCurrencyCode());
                    purchaseDetail.setPackageId(packageDetail.getId());
                    purchaseDetail.setPackageName(packageDetail.getName());
                    purchaseDetail.setTaxDetails(packageDetail.getTaxes());
                    purchaseDetailList.add(purchaseDetail);
                }
                final ReceiveDraftInvoiceModel receiveDraftInvoiceModel = new ReceiveDraftInvoiceModel();
                receiveDraftInvoiceModel.setPurchaseDetails(purchaseDetailList);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        initializeVal(receiveDraftInvoiceModel, false);
                        createDraftInvoice();
                    }
                });
            }
        }).start();

    }

    private void updateDraftInvoice(String OPCCode) {
        if (Util.isNullOrEmpty(OPCCode)) {
            showMessage("Online Voucher can't be empty");
            return;
        }
        try {
            DataBase dataBase = new DataBase(getActivity());
            Cursor cursor = dataBase.getLoginStatus();
            String fpUserProfileId;
            if (cursor.moveToFirst()) {
                fpUserProfileId = cursor.getString(cursor.getColumnIndex(DataBase.colloginId));
            } else {
                Methods.showDialog(mContext, "Alert!", getString(R.string.this_is_an_added_security_feature));
                return;
            }
            UpdateDraftInvoiceModel updateDraftInvoiceModel;
            if (mInvoiceId != null && fpUserProfileId != null) {
                updateDraftInvoiceModel = new UpdateDraftInvoiceModel(fpUserProfileId, OPCCode, mInvoiceId);
            } else {
                showMessage(getString(R.string.unable_to_create_draft_invoice));
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("clientId", Constants.clientId);

            if (updateDraftInvoiceModel == null) {
                showMessage(getString(R.string.unable_to_apply_coupon));
                return;
            }
            ((OnPaymentOptionClick) mContext).showProcess(getString(R.string.please_wait));

            StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
            storeInterface.updateDraftInvoice(params, updateDraftInvoiceModel, new Callback<ReceivedDraftInvoice>() {
                @Override
                public void success(ReceivedDraftInvoice receiveDraftInvoice, Response response) {
                    if (receiveDraftInvoice != null) {
                        ((OnPaymentOptionClick) mContext).hideProcess();
                        if (receiveDraftInvoice.getError().getErrorList() == null || receiveDraftInvoice.getStatusCode() == 200) {
                            if (receiveDraftInvoice.getResult().getPurchaseDetails().get(0).getPackageId().equals(mPurchasePlans.get(0).getId())) {
                                opcEditText.setEnabled(false);
                                btnOpcApply.setEnabled(false);
                                mInvoiceId = receiveDraftInvoice.getResult().getInvoiceId();
                                mOpcDetails = receiveDraftInvoice.getResult().getOpcDetails();
                                initializeVal(receiveDraftInvoice.getResult(), true);
                                showMessage(getString(R.string.online_voucher_applied_successfully));
                            } else {
                                showMessage(getString(R.string.the_entered_online_voucher_is_not_valid));
                            }
                        } else {
                            showMessage(receiveDraftInvoice.getError().getErrorList().get(0).Key);
                        }
                    } else {
                        ((OnPaymentOptionClick) mContext).hideProcess();
                        showMessage(getString(R.string.error_invoice));
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    ((OnPaymentOptionClick) mContext).hideProcess();
                    showMessage(getString(R.string.error_invoice));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ((OnPaymentOptionClick) mContext).hideProcess();
            Toast.makeText(mContext, getString(R.string.error_while_generating_invoice), Toast.LENGTH_SHORT).show();
        }

    }

    private void initializeVal(final ReceiveDraftInvoiceModel invoiceData, boolean showDiscount) {
        if (invoiceData == null || mPurchasePlans == null) {
            return;
        }

        double netAmount = 0;
        for (PurchaseDetail data : invoiceData.getPurchaseDetails()) {
            if (data.getDiscount() == null) {
                netAmount += data.getBasePrice();
            } else {
                netAmount += (data.getBasePrice() - (data.getBasePrice() * data.getDiscount().value / 100.0));
            }
        }
        netAmount = Math.round((netAmount * 100) / 100.0);
        tvNetTotal.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " +
                NumberFormat.getIntegerInstance(Locale.US).format(netAmount) + " /-");
        float taxVal = 0;
        StringBuilder taxNames = new StringBuilder();

        for (TaxDetail taxData : invoiceData.getPurchaseDetails().get(0).getTaxDetails()) {
            taxVal += taxData.getValue();
            taxNames.append(taxData.getKey() + "-" + taxData.getValue() + "%,\n ");
        }

        double taxAmount = 0;
        if (invoiceData.getPurchaseDetails().get(0).getTaxDetails().get(0).getAmountType() == 0) {
            taxAmount = (netAmount * taxVal) / 100.0;
        } else {
            taxAmount = (int) taxVal;
        }
        taxAmount = Math.round((taxAmount * 100) / 100.0);
        tvTaxes.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " +
                NumberFormat.getIntegerInstance(Locale.US).format(taxAmount) + " /-\n" + "( " + taxNames.substring(0, taxNames.length() - 3) + " )");
        if (showDiscount) {
            trTdsAmount.setVisibility(View.VISIBLE);
            tvTdsAmount.setText(invoiceData.getPurchaseDetails().get(0).getMRPCurrencyCode() + " " + invoiceData.getTdsAmount());
        }

        tvAmountToBePaid.setText(mPurchasePlans.get(0).getCurrencyCode() + " " +
                NumberFormat.getIntegerInstance(Locale.US).format(Math.round((netAmount + taxAmount - invoiceData.getTdsAmount()) * 100) / 100) + " /-");
        String packages = "";
        for (int i = 0; i < invoiceData.getPurchaseDetails().size(); i++) {
            packages += invoiceData.getPurchaseDetails().get(i).getPackageName() + " and ";
        }

        rvItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        ItemsRecyclerViewAdapter adapter = new ItemsRecyclerViewAdapter(invoiceData.getPurchaseDetails(), mPurchasePlans.get(0).getCurrencyCode(), showDiscount);
        rvItems.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void showConfirmationDialog(final Intent i, final String mInvoiceId) {
        ReceiveDraftInvoiceModel.KeyValuePair keyVal = mOpcDetails.get(0);
        if (keyVal.getValue() != null) {
            new AlertDialog.Builder(mContext)
                    .setMessage(getString(R.string.please_note_that_your_package_will_be_activated_on) + keyVal.getValue() + getString(R.string.are_you_sure_want_to_proceed))
                    .setPositiveButton(getString( R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initiatePaymentProcess(i, mInvoiceId);
                        }
                    })
                    .setNegativeButton(getString( R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();

        } else {
            new AlertDialog.Builder(mContext)
                    .setMessage(getString(R.string.dialog_to_be_activated_null_text))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initiatePaymentProcess(i, mInvoiceId);
                        }
                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }

    private void payWithInstaMojo() {
//        Intent i = new Intent(mContext, InstaMojoMainActivity.class);
//        i.putExtra(com.romeo.mylibrary.Constants.PARCEL_IDENTIFIER, mOrderData);
//        //write logic for with and without opc cases
//        if (!opcEditText.isEnabled()) {
//            if (mOpcDetails == null) {
//                initiatePaymentProcess(i, mInvoiceId);
//            } else {
//                showConfirmationDialog(i, mInvoiceId);
//            }
//        } else {
//            initiatePaymentProcess(i, mInvoiceId);
//        }

    }

    private void initiatePaymentProcess(final Intent i, final String invoiceId) {
        if (mInvoiceId == null) {
            Toast.makeText(mContext, "Invalid Invoice", Toast.LENGTH_SHORT).show();
            return;
        }
        StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("clientId", Constants.clientId);
        params.put("invoiceId", invoiceId);

        SupportedPaymentMethods method = null;
        if (mPurchasePlans != null &&
                mPurchasePlans.get(0).getSupportedPaymentMethods() != null
                && mPurchasePlans.get(0).getSupportedPaymentMethods().size() > 0) {
            for (SupportedPaymentMethods paymentMethod : mPurchasePlans.get(0).getSupportedPaymentMethods()) {
                if (paymentMethod.Type == 1) {
                    method = paymentMethod;
                }
            }

            ((OnPaymentOptionClick) mContext).showProcess(getString(R.string.please_wait));
            if (method != null) {
                method.RedirectUri = "https://hello.nowfloats.com";
            }

            storeInterface.initiatePaymentProcess(params, method, new Callback<PaymentTokenResult>() {
                @Override
                public void success(PaymentTokenResult paymentTokenResult, Response response) {

                    ((OnPaymentOptionClick) mContext).hideProcess();
                    if (paymentTokenResult != null && paymentTokenResult.getResult() != null) {
                        switch (paymentTokenResult.getResult().getPaymentMethodType()) {
                            case "INSTAMOJO":
//                                i.putExtra(com.romeo.mylibrary.Constants.PAYMENT_REQUEST_IDENTIFIER, paymentTokenResult.getResult().getPaymentRequestId());
//                                i.putExtra(com.romeo.mylibrary.Constants.ACCESS_TOKEN_IDENTIFIER, paymentTokenResult.getResult().getAccessToken());
//                                i.putExtra(com.romeo.mylibrary.Constants.WEB_HOOK_IDENTIFIER, "https://api.withfloats.com/Payment/v1/floatingpoint/instaMojoWebHook?clientId=" + Constants.clientId);//change this later

                                startActivityForResult(i, OPC_REQUEST_CODE);
//                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;
                            case "PADDLE":
                                Intent paddleIntent = new Intent(getActivity(), PaddleCheckoutActivity.class);
                                paddleIntent.putExtra("paymentUrl", paymentTokenResult.getResult().getTargetPaymentCollectionUri());
                                startActivityForResult(paddleIntent, PADDLE_REQUEST_CODE);
                                break;
                            default:
                                showMessage(getString(R.string.error_while_processing_payment));
                        }

                    } else {
                        showMessage(getString(R.string.error_while_processing_payment));
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                    ((OnPaymentOptionClick) mContext).hideProcess();
                    showMessage(getString(R.string.error_while_processing_payment));
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DIRECT_REQUEST_CODE || requestCode == OPC_REQUEST_CODE || requestCode == PADDLE_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            if (mOpcDetails != null) {
                data.putExtra("showToBeActivatedOn", true);
                if (mOpcDetails.get(0).getValue() != null) {
                    data.putExtra("toBeActivatedOn", mOpcDetails.get(0).getValue());
                }
            }
            ((OnPaymentOptionClick) mContext).setResult(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void createDraftInvoice() {
        try {
            SendDraftInvoiceModel sendDraftInvoiceModel = new SendDraftInvoiceModel();
            List<PurchaseDetail> purchaseDetailList = new ArrayList<PurchaseDetail>();
            for (PackageDetails packageDetail : mPurchasePlans) {
                PurchaseDetail purchaseDetail = new PurchaseDetail();
                String clientId;

                if (!Util.isNullOrEmpty(mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID))) {
                    clientId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
                } else if (!Util.isNullOrEmpty(mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID))) {
                    clientId = mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID);
                } else if (!Util.isNullOrEmpty(mSessionManager.getSourceClientId())) {
                    clientId = mSessionManager.getSourceClientId();
                } else {
                    showMessage(getString(R.string.cant_proceed_for_payment));
                    return;
                }

                double totalTax = 0;
                if (packageDetail.getTaxes() != null) {
                    for (TaxDetail taxData : packageDetail.getTaxes()) {
                        totalTax += taxData.getValue();
                    }
                }
                purchaseDetail.setBasePrice(packageDetail.getPrice());
                purchaseDetail.setClientId(clientId);
                purchaseDetail.setDurationInMnths(packageDetail.getValidityInMths());
                purchaseDetail.setFPId(mSessionManager.getFPID());
                purchaseDetail.setMRP(packageDetail.getPrice() + (packageDetail.getPrice() * totalTax) / 100);
                purchaseDetail.setMRPCurrencyCode(packageDetail.getCurrencyCode());
                purchaseDetail.setPackageId(packageDetail.getId());
                purchaseDetail.setPackageName(packageDetail.getName());
                purchaseDetail.setTaxDetails(packageDetail.getTaxes());
                purchaseDetailList.add(purchaseDetail);
            }

            sendDraftInvoiceModel.setPurchaseDetails(purchaseDetailList);
            DataBase dataBase = new DataBase(getActivity());
            Cursor cursor = dataBase.getLoginStatus();
            if (cursor.moveToFirst() && !cursor.getString(cursor.getColumnIndex(DataBase.colloginId)).equals("0")) {
                sendDraftInvoiceModel.setFpUserProfileId(cursor.getString(cursor.getColumnIndex(DataBase.colloginId)));
                sendDraftInvoiceModel.setOpc(null);
            } else {
                Methods.showDialog(mContext, "Alert!", getString(R.string.this_is_an_added_security_feature_to_protect_your_package_details));
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("clientId", Constants.clientId);
            ((OnPaymentOptionClick) mContext).showProcess(getString(R.string.please_wait));
            StoreInterface storeInterface = Constants.restAdapter.create(StoreInterface.class);
            storeInterface.createDraftInvoice(params, sendDraftInvoiceModel, new Callback<ReceivedDraftInvoice>() {
                @Override
                public void success(ReceivedDraftInvoice receiveDraftInvoice, Response response) {
                    if (receiveDraftInvoice != null && receiveDraftInvoice.getStatusCode() == 200) {

                        ((OnPaymentOptionClick) mContext).hideProcess();
                        mInvoiceId = receiveDraftInvoice.getResult().getInvoiceId();
                        initializeVal(receiveDraftInvoice.getResult(), false);
                    } else {
                        ((OnPaymentOptionClick) mContext).hideProcess();
                        showMessage(getResources().getString(R.string.error_invoice));
                    }

                }

                @Override
                public void failure(RetrofitError error) {

                    ((OnPaymentOptionClick) mContext).hideProcess();
                    showMessage(getResources().getString(R.string.error_invoice));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

            ((OnPaymentOptionClick) mContext).hideProcess();
            Toast.makeText(mContext, R.string.error_while_generating_invoice, Toast.LENGTH_SHORT).show();
        }
    }

    public void showMessage(String msg) {
        if (getActivity() != null) {
            Methods.showSnackBarNegative(getActivity(), msg);
        } else {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.textView_apply) {//Apply Coupon Event Trigger
            WebEngageController.trackEvent(APPLY_COUPON, BUTTON, CLICKED);

            updateDraftInvoice(opcEditText.getText().toString());
            // check for opc
        } else if (id == R.id.textView_pay) {
            payWithInstaMojo();
        }
    }
}
