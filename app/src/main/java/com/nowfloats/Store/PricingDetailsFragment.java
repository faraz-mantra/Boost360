package com.nowfloats.Store;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinksity.R;

public class PricingDetailsFragment extends Fragment {

    private static final String PRICING_TYPE = "pricing_type";

    ImageView ivPackageLogo, ivCheckDictate, ivCheckWildFire, ivCheckApp;
    CheckBox cbDictate, cbWildFire, cbApp;
    TextView tvDictate, tvWildFire, tvApp;

    public enum PricingType {
        BOOST_LITE, BOOST_PRO, BOOST_CUSTOM
    }

    public PricingDetailsFragment() {

    }

    private PricingType mPricingType;


    public static PricingDetailsFragment newInstance(PricingType pricingType) {
        PricingDetailsFragment fragment = new PricingDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PRICING_TYPE, pricingType.ordinal());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null && bundle.containsKey(PRICING_TYPE)){
            mPricingType = PricingType.values()[bundle.getInt(PRICING_TYPE)];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View pricingView =  inflater.inflate(R.layout.fragment_pricing_details, container, false);

        ivPackageLogo = (ImageView) pricingView.findViewById(R.id.iv_package_logo);
        ivCheckDictate = (ImageView) pricingView.findViewById(R.id.iv_check_dictate);
        ivCheckWildFire = (ImageView) pricingView.findViewById(R.id.iv_check_wildfire);
        ivCheckApp = (ImageView) pricingView.findViewById(R.id.iv_check_app);
        cbDictate = (CheckBox) pricingView.findViewById(R.id.cb_dictate);
        cbWildFire = (CheckBox) pricingView.findViewById(R.id.cb_wild_fire);
        cbApp = (CheckBox) pricingView.findViewById(R.id.cb_app);
        tvDictate = (TextView) pricingView.findViewById(R.id.tv_dictate);
        tvWildFire = (TextView) pricingView.findViewById(R.id.tv_wildfire);
        tvApp = (TextView) pricingView.findViewById(R.id.tv_app);

        tvDictate.setText(Html.fromHtml(getString(R.string.text_dictate)));
        tvWildFire.setText(Html.fromHtml(getString(R.string.text_wildfire)));
        tvApp.setText(Html.fromHtml(getString(R.string.text_app)));

        switch (mPricingType){
            case BOOST_LITE:
                ivPackageLogo.setImageResource(R.drawable.boost_lite_logo);
                tvDictate.setTextColor(Color.parseColor("#A6A6A6"));
                tvWildFire.setTextColor(Color.parseColor("#A6A6A6"));
                tvApp.setTextColor(Color.parseColor("#A6A6A6"));
                break;
            case BOOST_PRO:
                ivPackageLogo.setImageResource(R.drawable.boost_pro_logo);
                ivCheckDictate.setVisibility(View.VISIBLE);
                ivCheckWildFire.setVisibility(View.VISIBLE);
                ivCheckApp.setVisibility(View.VISIBLE);
                break;
            case BOOST_CUSTOM:
                ivPackageLogo.setImageResource(R.drawable.boost_custom_logo);
                cbDictate.setVisibility(View.VISIBLE);
                cbWildFire.setVisibility(View.VISIBLE);
                cbApp.setVisibility(View.VISIBLE);
        }

        return pricingView;
    }
}
