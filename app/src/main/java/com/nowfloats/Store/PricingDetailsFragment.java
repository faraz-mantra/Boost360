package com.nowfloats.Store;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nowfloats.Store.Adapters.AllPlansRvAdapter;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Store.Model.WidgetPacks;
import com.squareup.picasso.Picasso;
import com.thinksity.BuildConfig;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PricingDetailsFragment extends Fragment {

    private static final String PRICING_TYPE = "pricing_type";

    ImageView ivPackageLogo;
    RecyclerView rvAllPlanDetails;
    private PackageDetails mBasePackage;
    private List<PackageDetails> mTopUps;
    private AllPlansRvAdapter mRvAdapter;
    private Context mContext;

    public PricingDetailsFragment() {
    }


    public static PricingDetailsFragment newInstance(PackageDetails basePackage, List<PackageDetails> topUps) {
        PricingDetailsFragment fragment = new PricingDetailsFragment();
        fragment.mBasePackage = basePackage;
        fragment.mTopUps = topUps;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View pricingView = inflater.inflate(R.layout.fragment_pricing_details, container, false);
        rvAllPlanDetails = (RecyclerView) pricingView.findViewById(R.id.rv_all_plan_details);
        ivPackageLogo = (ImageView) pricingView.findViewById(R.id.iv_package_logo);
        if (mBasePackage == null) return pricingView;
        Picasso.with(getActivity()).load(mBasePackage.getPrimaryImageUri()).into(ivPackageLogo);
        mRvAdapter = new AllPlansRvAdapter(new ArrayList<Pair<String, Boolean>>());
        prepareBasePackageDetails();

        rvAllPlanDetails.setAdapter(mRvAdapter);
        rvAllPlanDetails.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return pricingView;
    }

    private void prepareBasePackageDetails() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Pair<String, Boolean>> packageDetails = new ArrayList<>();
                Map<String, StringBuilder> map = new HashMap<>();
                packageDetails.add(new Pair<>(mBasePackage.getValidityInMths() + " MONTHS PLAN", true));
                int i = 0;
                int counter = 0;
                WidgetPacks widgetPack;
                /*if (Constants.PACKAGE_NAME.equals("com.redtim")) {
                    for (; i < mBasePackage.getWidgetPacks().size(); i++) {
                        StringBuilder groupValues = new StringBuilder();
                        widgetPack = mBasePackage.getWidgetPacks().get(i);
                        if (widgetPack.Name != null) {
                            if (widgetPack.Group == null) {
                                packageDetails.add(new Pair<>(widgetPack.Name.toUpperCase(), true));
                            } else {
                                if (map.containsKey(widgetPack.Group)) {
                                    groupValues = map.get(widgetPack.Group);
                                    groupValues.append("   " + widgetPack.Name + "<br/>");
                                    map.put(widgetPack.Group.toUpperCase(), groupValues);
                                } else {
                                    groupValues.append(widgetPack.Group + "<br/>");
                                    groupValues.append("   " + widgetPack.Name);
                                    map.put(widgetPack.Group.toUpperCase(), groupValues);
                                }
                                packageDetails.add(new Pair<>(groupValues.toString(), true));
                            }
                            counter++;
                        }
                    }
                } else {*/
                if (mBasePackage.getWidgetPacks() != null && mBasePackage.getWidgetPacks().size() > 0) {
                    StringBuilder mainPackageFeatures = new StringBuilder("A DYNAMIC WEBSITE WITH:<br/>");
                    for (; counter < 4 && i < mBasePackage.getWidgetPacks().size(); i++) {
                        if (mBasePackage.getWidgetPacks().get(i).Name != null) {
                            mainPackageFeatures.append("- " + mBasePackage.getWidgetPacks().get(i).Name.toUpperCase() + "<br/>");
                            counter++;
                        }
                    }
                    packageDetails.add(new Pair<>(mainPackageFeatures.toString(), true));
                    for (; counter < 8 && i < mBasePackage.getWidgetPacks().size(); i++) {
                        if (mBasePackage.getWidgetPacks().get(i).Name != null) {
                            packageDetails.add(new Pair<>(mBasePackage.getWidgetPacks().get(i).Name.toUpperCase(), true));
                            counter++;
                        }
                    }
                }
                if (BuildConfig.APPLICATION_ID.equals("com.biz2.nowfloats")) {
                    if (mBasePackage.getName().toLowerCase().contains("pro")) {
                        packageDetails.add(new Pair<>("<b>DICTATE</b> - WEBSITE CONTENT SERVICE", true));
                        packageDetails.add(new Pair<>("<b>WILDFIRE</b> - Rs. 60000 OF ADWORDS & FACEBOOK MARKETING", true));
                        packageDetails.add(new Pair<>("<b>YOUR APP</b> - YOUR OWN BUSINESS APP FOR GOOGLE PLAYSTORE", true));
                    } else {
                        packageDetails.add(new Pair<>("<b>DICTATE</b> - WEBSITE CONTENT SERVICE", false));
                        packageDetails.add(new Pair<>("<b>WILDFIRE</b> - Rs. 60000 OF ADWORDS & FACEBOOK MARKETING", false));
                        packageDetails.add(new Pair<>("<b>YOUR APP</b> - YOUR OWN BUSINESS APP FOR GOOGLE PLAYSTORE", false));
                    }
                }
                // }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mRvAdapter.setPlanDetails(packageDetails);
                    }
                });
            }
        }).start();
    }

}
