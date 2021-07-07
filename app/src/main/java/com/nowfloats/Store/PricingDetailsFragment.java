package com.nowfloats.Store;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nowfloats.Store.Adapters.AllPlansRvAdapter;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Store.Model.WidgetPacks;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PricingDetailsFragment extends Fragment {

    private static final String PRICING_TYPE = "pricing_type";

    ImageView ivPackageLogo;
    RecyclerView rvAllPlanDetails;
    List<List<Pair<String, List<WidgetPacks>>>> packages = new ArrayList<>();
    private PackageDetails mBasePackage;
    private List<PackageDetails> mTopUps;
    private AllPlansRvAdapter mRvAdapter;
    private Context mContext;
    private PopupWindow popup;
    private TextView tvDesc;
    public OnPlanDescriptionClickListener planDescriptionClickListener = new OnPlanDescriptionClickListener() {
        @Override
        public void onPlanClick(ImageView view, String desc) {
            initiatePopupWindow(view, desc);
        }
    };

    public PricingDetailsFragment() {
    }

    public static PricingDetailsFragment newInstance(PackageDetails basePackage, List<PackageDetails> topUps) {
        PricingDetailsFragment fragment = new PricingDetailsFragment();
        fragment.mBasePackage = basePackage;
        fragment.mTopUps = topUps;
        return fragment;
    }

    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
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
        Picasso.Builder builder = new Picasso.Builder(requireActivity());
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });
        builder.build().load(mBasePackage.getPrimaryImageUri()).into(ivPackageLogo);
       /* Picasso.with(requireActivity()).load(mBasePackage.getPrimaryImageUri()).placeholder(R.drawable.default_product_image).into(ivPackageLogo, new Callback() {
            @Override
            public void onSuccess() {
                Log.v(PricingDetailsFragment.class.getName(), "success");
            }

            @Override
            public void onError() {
                Log.v(PricingDetailsFragment.class.getName(), "failed");
            }
        });*/
        mRvAdapter = new AllPlansRvAdapter(new ArrayList<Pair<String, Boolean>>(), requireActivity());
        prepareBasePackageDetails();

        rvAllPlanDetails.setAdapter(mRvAdapter);
        mRvAdapter.setClickListener(planDescriptionClickListener);
        rvAllPlanDetails.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return pricingView;
    }

    private void prepareBasePackageDetails() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Pair<String, Boolean>> packageDetails = new ArrayList<>();
                final List<Pair<String, List<WidgetPacks>>> groupPackageDetails = new ArrayList<>();
                final List<Pair<String, List<WidgetPacks>>> normalPackageDetails = new ArrayList<>();
                final List<Pair<String, List<WidgetPacks>>> allPackages = new ArrayList<>();
                Map<String, List<WidgetPacks>> map = new HashMap<>();
                int i = 0;
                int counter = 0;
                WidgetPacks widgetPack;
                List<WidgetPacks> groupValues;
                List<WidgetPacks> packageItems;
                List<WidgetPacks> normalPackageItems = new ArrayList<>();
                //if (Constants.PACKAGE_NAME.equals("com.redtim")) {
                normalPackageDetails.add(new Pair<>((Math.round(mBasePackage.getValidityInMths()) + " MONTHS PLAN"), normalPackageItems));
                for (; i < mBasePackage.getWidgetPacks().size(); i++) {
                    widgetPack = mBasePackage.getWidgetPacks().get(i);
                    if (widgetPack.Name != null) {
                        if (widgetPack.Group == null) {
                            normalPackageItems = new ArrayList<>();
                            normalPackageItems.add(widgetPack);
                            normalPackageDetails.add(new Pair<>(widgetPack.Name, normalPackageItems));
                        } else {
                            if (map.containsKey(widgetPack.Group.toUpperCase())) {
                                groupValues = new ArrayList<>();
                                groupValues = map.get(widgetPack.Group);
                                for (Pair<String, List<WidgetPacks>> pair : groupPackageDetails) {
                                    if (pair.first.equalsIgnoreCase(widgetPack.Group)) {
                                        groupPackageDetails.remove(pair);
                                        groupPackageDetails.remove(pair);
                                        break;
                                    }
                                }
                                groupValues.add(widgetPack);
                                map.put(widgetPack.Group.toUpperCase(), groupValues);
                                Collections.sort(groupValues, new Comparator<WidgetPacks>() {
                                    @Override
                                    public int compare(WidgetPacks o1, WidgetPacks o2) {
                                        return o1.Priority - o2.Priority;
                                    }
                                });
                                groupPackageDetails.add(new Pair<>(widgetPack.Group.toUpperCase(), groupValues));
                            } else {
                                packageItems = new ArrayList<>();
                                packageItems.add(widgetPack);
                                map.put(widgetPack.Group.toUpperCase(), packageItems);
                                groupPackageDetails.add(new Pair<>(widgetPack.Group.toUpperCase(), packageItems));
                            }
                        }
                        counter++;
                    }
                }
                packages = Arrays.asList(normalPackageDetails, groupPackageDetails);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        allPackages.clear();
                        for (List<Pair<String, List<WidgetPacks>>> pair : packages) {
                            allPackages.addAll(pair);
                        }
                        mRvAdapter.setRedTimPlanDetails(allPackages);
                    }
                });
            }
        }).start();
    }

    private void initiatePopupWindow(View image, String desc) {
        Rect location = locateView(image);
        if (location == null) return;
        View layout1 = LayoutInflater.from(requireActivity()).inflate(R.layout.layout_all_plan_description_popup_dialog, null);
        layout1.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int position_x = location.centerX() - layout1.getMeasuredWidth();
        int position_y = location.bottom - location.height() - layout1.getMeasuredHeight();
        //if (popup == null) {
        try {

            popup = new PopupWindow(requireActivity());
            View layout = LayoutInflater.from(requireActivity()).inflate(R.layout.layout_all_plan_description_popup_dialog, null);
            popup.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.color.transparent));
            tvDesc = layout.findViewById(R.id.tv_plan_desc);
            tvDesc.setText(desc);
            popup.setContentView(layout);
            popup.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popup.setOutsideTouchable(true);
            popup.setFocusable(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                popup.setAttachedInDecor(true);
            }
            if (popup.isShowing()) {
                popup.dismiss();
            } else {
                popup.showAtLocation(image.getRootView(), Gravity.NO_GRAVITY, location.left + 18, position_y - 40);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnPlanDescriptionClickListener {
        void onPlanClick(ImageView view, String desc);
    }

}
