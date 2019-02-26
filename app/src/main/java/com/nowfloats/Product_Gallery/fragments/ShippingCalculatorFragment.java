package com.nowfloats.Product_Gallery.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.Product_Gallery.Model.ShippingMetricsModel;
import com.nowfloats.Product_Gallery.Service.ProductGalleryInterface;
import com.nowfloats.manageinventory.models.APIResponseModel;
import com.nowfloats.manageinventory.models.WaUpdateDataModel;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ShippingCalculatorFragment extends DialogFragment implements TextWatcher{

    EditText etLength, etWidth, etHeight, etWeight;

    /**
     * Added Shipping and GST
     */
    EditText etShippingCharges, etGST;
    //Switch switchHidePrice;

    ProgressDialog progressDialog;

    TextView tvShippingCharge;

    Button btnCalculateShippingCharge, btnSaveMetrics;

    private ShippingMetricsModel mShippingMetric;
    private ProductMetricCallBack mProductMetricCallBack;
    private String deliveryMethod;
    private double mShippingCharge = 0;

    public enum ShippingAddOrUpdate{
        ADD,
        UPDATE
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_TITLE, 0);

        if(getArguments()!=null && getArguments().containsKey("shippingMetric")){
            mShippingMetric = getArguments().getParcelable("shippingMetric");
        }

        if(getArguments() != null && getArguments().containsKey("deliveryMethod"))
        {
            deliveryMethod = getArguments().getString("deliveryMethod", "");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ProductMetricCallBack){
            mProductMetricCallBack = (ProductMetricCallBack) activity;
        }else {
            throw new RuntimeException("Implement ProductMetricCallBack");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shipping_calculator_v1, container, false);

        etLength = (EditText) view.findViewById(R.id.et_length);
        etWidth = (EditText) view.findViewById(R.id.et_width);
        etHeight = (EditText) view.findViewById(R.id.et_height);
        etWeight = (EditText) view.findViewById(R.id.et_weight);

        /**
         * Newly Added
         */
        etShippingCharges = view.findViewById(R.id.et_shipping_charge);
        etGST = view.findViewById(R.id.et_gst_charge);
        //switchHidePrice = view.findViewById(R.id.switch_hide_price);

        tvShippingCharge = (TextView) view.findViewById(R.id.tv_shipping_charge);

        progressDialog = new ProgressDialog(getActivity());

        btnCalculateShippingCharge = (Button) view.findViewById(R.id.btn_calculate_shipping_charges);
        btnSaveMetrics = (Button) view.findViewById(R.id.btn_save_metrics);


        if(deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.ASSURED_PURCHASE.getValue()))
        {
            etLength.setVisibility(View.VISIBLE);
            etWidth.setVisibility(View.VISIBLE);
            etHeight.setVisibility(View.VISIBLE);
            etWeight.setVisibility(View.VISIBLE);
            etShippingCharges.setVisibility(View.VISIBLE);

            if (!Constants.PACKAGE_NAME.equals("com.biz2.nowfloats"))
            {
                etGST.setVisibility(View.GONE);
            }

            else
            {
                etGST.setVisibility(View.VISIBLE);
            }
        }

        else
        {
            etLength.setVisibility(View.GONE);
            etWidth.setVisibility(View.GONE);
            etHeight.setVisibility(View.GONE);
            etWeight.setVisibility(View.GONE);
            etShippingCharges.setVisibility(View.VISIBLE);
            etGST.setVisibility(View.VISIBLE);
        }

        btnCalculateShippingCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateShippingCharge();
            }
        });

        btnSaveMetrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mShippingMetric==null){
                    addMetric();
                }else {
                    updateMetric();
                }
            }
        });

        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        if(mShippingMetric!=null){

            etLength.setText(isNullOrEmplty(mShippingMetric.getLength()));
            etHeight.setText(isNullOrEmplty(mShippingMetric.getHeight()));
            etWidth.setText(isNullOrEmplty(mShippingMetric.getWidth()));
            etWeight.setText(isNullOrEmplty(mShippingMetric.getWeight()));

            //tvShippingCharge.setText("INR " + isNullOrEmplty(mShippingMetric.getLength()));

            etShippingCharges.setText(isNullOrEmplty(String.valueOf(mShippingMetric.getShippingCharge())));

            if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats"))
            {
                etGST.setText(isNullOrEmplty(String.valueOf(mShippingMetric.getGstCharge())));
            }

            //switchHidePrice.setChecked(mShippingMetric.getHidePrice());
        }

        etLength.addTextChangedListener(this);
        etHeight.addTextChangedListener(this);
        etWidth.addTextChangedListener(this);
        etWeight.addTextChangedListener(this);

        //etShippingCharges.addTextChangedListener(this);
        //etGST.addTextChangedListener(this);

        return view;
    }

    private void updateMetric()
    {
        if(!isValidForm())
        {
            return;
        }

        //WaUpdateDataModel update = new WaUpdateDataModel();
        final ShippingMetricsModel shippingMetric = initShippingMatrix();

        if(shippingMetric == null)
        {
            return;
        }

        mProductMetricCallBack.onProductMetricCalculated(shippingMetric, ShippingAddOrUpdate.UPDATE);
        ShippingCalculatorFragment.this.dismiss();

        /*update.setQuery(String.format("{product_id:'%s'}", mShippingMetric.getProductId()));

        if(deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.ASSURED_PURCHASE.getValue()))
        {
            if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats"))
            {
                update.setUpdateValue(String.format("{$set:{length:'%s', width:'%s', weight:'%s', height:'%s', shipping_charge:%s, gst_slab:%s, hide_price:%s}}",
                        shippingMetric.getLength(),
                        shippingMetric.getWidth(),
                        shippingMetric.getWeight(),
                        shippingMetric.getHeight(),
                        shippingMetric.getShippingCharge().toString(),
                        shippingMetric.getGstCharge().toString(), shippingMetric.getHidePrice()));
            }

            else
            {
                update.setUpdateValue(String.format("{$set:{length:'%s', width:'%s', weight:'%s', height:'%s', shipping_charge:%s, hide_price:%s}}",
                        shippingMetric.getLength(),
                        shippingMetric.getWidth(),
                        shippingMetric.getWeight(),
                        shippingMetric.getHeight(),
                        shippingMetric.getShippingCharge().toString(), shippingMetric.getHidePrice()));
            }
        }

        else
        {
            update.setUpdateValue(String.format("{$set:{shipping_charge:%s, gst_slab:%s, hide_price:%s}}",
                    shippingMetric.getShippingCharge().toString(),
                    shippingMetric.getGstCharge().toString(), shippingMetric.getHidePrice()));
        }

        update.setMulti(true);

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        Constants.webActionAdapter.create(ProductGalleryInterface.class)
                .updateProductMetrics(update, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {

                        ShippingCalculatorFragment.this.dismiss();
                        mProductMetricCallBack.onProductMetricCalculated(shippingMetric, ShippingAddOrUpdate.UPDATE);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        if(error.getResponse().getStatus() == 200)
                        {
                            ShippingCalculatorFragment.this.dismiss();
                            mProductMetricCallBack.onProductMetricCalculated(shippingMetric, ShippingAddOrUpdate.UPDATE);
                        }

                        progressDialog.dismiss();
                    }
                });*/

    }

    private void addMetric() {

        if(!isValidForm())
        {
            return;
        }

        final ShippingMetricsModel shippingMetric = initShippingMatrix();

        if(shippingMetric == null)
        {
            return;
        }

        mProductMetricCallBack.onProductMetricCalculated(shippingMetric, ShippingAddOrUpdate.ADD);
        ShippingCalculatorFragment.this.dismiss();
    }


    private void calculateShippingCharge(){
        String length = etLength.getText().toString().trim();
        String width = etWidth.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();

        if(TextUtils.isEmpty(length)){
            etLength.setError("Length is required");
            return;
        }

        if(TextUtils.isEmpty(width)){
            etWidth.setError("Width is required");
            return;
        }

        if(TextUtils.isEmpty(height)){
            etHeight.setError("Height is required");
            return;
        }

        if(TextUtils.isEmpty(weight)){
            etWeight.setError("Weight is required");
            return;
        }

        Map<String, String> query = new HashMap<>();
        query.put("length", length);
        query.put("width", width);
        query.put("height", height);
        query.put("weight", weight);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Constants.restAdapter.create(ProductGalleryInterface.class)
                .getShippingCharge(query, new Callback<APIResponseModel<Integer>>() {
                    @Override
                    public void success(APIResponseModel<Integer> shippingCharge, Response response) {
                        progressDialog.dismiss();
                        tvShippingCharge.setText("INR " + shippingCharge.Result);
                        mShippingCharge = shippingCharge.Result;
                        btnCalculateShippingCharge.setVisibility(View.GONE);
                        btnSaveMetrics.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Something went wrong! Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //btnSaveMetrics.setVisibility(View.GONE);
        //btnCalculateShippingCharge.setVisibility(View.VISIBLE);

    }


    /**
     * Initialize ShippingMatixModel
     */

    private ShippingMetricsModel initShippingMatrix()
    {
        ShippingMetricsModel shippingMetric = new ShippingMetricsModel();

        try
        {
            if(deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.ASSURED_PURCHASE.getValue()))
            {
                shippingMetric.setHeight(etHeight.getText().toString().trim());
                shippingMetric.setWidth(etWidth.getText().toString().trim());
                shippingMetric.setWeight(etWeight.getText().toString().trim());
                shippingMetric.setLength(etLength.getText().toString().trim());
            }

            shippingMetric.setShippingCharge(Double.valueOf(etShippingCharges.getText().toString().trim()));

            if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats"))
            {
                shippingMetric.setGstCharge(Double.valueOf(etGST.getText().toString().trim()));
            }

            //shippingMetric.setHidePrice(switchHidePrice.isChecked());
        }

        catch (Exception e)
        {
            Toast.makeText(getActivity(), "Please enter valid input", Toast.LENGTH_LONG).show();
            return null;
        }

        return shippingMetric;
    }


    private String isNullOrEmplty(String value)
    {
        return value == null ? "" : (value.equalsIgnoreCase("null") ? "" : value);
    }

    /**
     * Check weather all input fields are valid
     * @return
     */
    private boolean isValidForm()
    {
        if(deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.ASSURED_PURCHASE.getValue()))
        {
            if(TextUtils.isEmpty(etLength.getText().toString().trim())){
                etLength.setError("Length is required");
                return false;
            }

            if(TextUtils.isEmpty(etWidth.getText().toString().trim())){
                etWidth.setError("Width is required");
                return false;
            }

            if(TextUtils.isEmpty(etHeight.getText().toString().trim())){
                etHeight.setError("Height is required");
                return false;
            }

            if(TextUtils.isEmpty(etWeight.getText().toString().trim())){
                etWeight.setError("Weight is required");
                return false;
            }
        }

        if(TextUtils.isEmpty(String.valueOf(etShippingCharges.getText().toString().trim()))){
            etShippingCharges.setError("Shipping Charge is required");
            return false;
        }

        if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats"))
        {
            if(TextUtils.isEmpty(String.valueOf(etGST.getText().toString().trim()))){
                etGST.setError("GST is required");
                return false;
            }
        }

        return true;
    }

    public interface ProductMetricCallBack{
        void onProductMetricCalculated(ShippingMetricsModel shippingMetricsModel, ShippingAddOrUpdate val);
    }
}
