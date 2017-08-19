package com.nowfloats.Product_Gallery.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    ProgressDialog progressDialog;

    TextView tvShippingCharge;

    Button btnCalculateShippingCharge, btnSaveMetrics;

    private ShippingMetricsModel mShippingMetric;
    private ProductMetricCallBack mProductMetricCallBack;

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
        View view = inflater.inflate(R.layout.fragment_shipping_calculator, container, false);

        etLength = (EditText) view.findViewById(R.id.et_length);
        etWidth = (EditText) view.findViewById(R.id.et_width);
        etHeight = (EditText) view.findViewById(R.id.et_height);
        etWeight = (EditText) view.findViewById(R.id.et_weight);

        tvShippingCharge = (TextView) view.findViewById(R.id.tv_shipping_charge);

        progressDialog = new ProgressDialog(getActivity());

        btnCalculateShippingCharge = (Button) view.findViewById(R.id.btn_calculate_shipping_charges);
        btnSaveMetrics = (Button) view.findViewById(R.id.btn_save_metrics);



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

            etLength.setText(mShippingMetric.getLength()+"");
            etHeight.setText(mShippingMetric.getHeight()+"");
            etWidth.setText(mShippingMetric.getWidth()+"");
            etWeight.setText(mShippingMetric.getWeight()+"");
            tvShippingCharge.setText("INR " + mShippingMetric.getShippingCharge());
        }

        etLength.addTextChangedListener(this);
        etHeight.addTextChangedListener(this);
        etWidth.addTextChangedListener(this);
        etWeight.addTextChangedListener(this);


        return view;
    }

    private void updateMetric() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        WaUpdateDataModel update = new WaUpdateDataModel();
        final ShippingMetricsModel shippingMetric = new ShippingMetricsModel();
        shippingMetric.setHeight(etHeight.getText().toString().trim());
        shippingMetric.setWidth(etWidth.getText().toString().trim());
        shippingMetric.setWeight(etWeight.getText().toString().trim());
        shippingMetric.setLength(etLength.getText().toString().trim());
        shippingMetric.setShippingCharge(mShippingCharge);

        update.setQuery(String.format("{product_id:'%s'}", mShippingMetric.getProductId()));
        update.setUpdateValue(String.format("{$set:{length:'%s', width:'%s', weight:'%s', height:'%s', shipping_charge:%s}}",
                shippingMetric.getLength(),
                shippingMetric.getWidth(),
                shippingMetric.getWeight(),
                shippingMetric.getHeight(),
                shippingMetric.getShippingCharge().toString()));
        update.setMulti(true);
        Constants.webActionAdapter.create(ProductGalleryInterface.class)
                .updateProductMetrics(update, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        progressDialog.dismiss();
                        ShippingCalculatorFragment.this.dismiss();
                        mProductMetricCallBack.onProductMetricCalculated(shippingMetric, ShippingAddOrUpdate.UPDATE);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                    }
                });

    }

    private void addMetric() {
        final ShippingMetricsModel shippingMetric = new ShippingMetricsModel();
        shippingMetric.setHeight(etHeight.getText().toString().trim());
        shippingMetric.setWidth(etWidth.getText().toString().trim());
        shippingMetric.setWeight(etWeight.getText().toString().trim());
        shippingMetric.setLength(etLength.getText().toString().trim());
        shippingMetric.setShippingCharge(mShippingCharge);
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
        btnSaveMetrics.setVisibility(View.GONE);
        btnCalculateShippingCharge.setVisibility(View.VISIBLE);

    }

    public interface ProductMetricCallBack{
        void onProductMetricCalculated(ShippingMetricsModel shippingMetricsModel, ShippingAddOrUpdate val);
    }
}
