package com.nowfloats.customerassistant;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.NavigationDrawer.SlidingTabLayout;
import com.nowfloats.customerassistant.adapters.CAProductsAdapter;
import com.nowfloats.customerassistant.adapters.CAUpdatesAdapter;
import com.nowfloats.customerassistant.models.SugProducts;
import com.nowfloats.customerassistant.models.SugUpdates;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thinksity.R;

import java.util.ArrayList;

import static com.nowfloats.customerassistant.CustomerAssistantActivity.KEY_DATA;

/**
 * Created by admin on 8/17/2017.
 */

public class CustomerAssistantDetailFragment extends android.app.Fragment implements SuggestionSelectionListner {


    private ViewPager vwSAM;

    private TextView tvDate, tvSource, tvMessage, tvViewMore, tvExpiryDate, tvTitle;

    private Button btnCall, btnShare;

    private SlidingTabLayout tabs;

    private SuggestionsDO mSuggestionsDO;

    private boolean isViewMore = true;

    private LinearLayout llMessage, llRelevant;

    private int selectedCount = 0;

    private int noOfTimesResponded = 0;

    private final String ACTION_TYPE_NUMBER = "contactNumber";

    private final String ACTION_TYPE_EMAIL = "email";

    private String appVersion = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isViewMore = true;
        selectedCount = 0;
        noOfTimesResponded = 0;
        if (getArguments() != null) {
            mSuggestionsDO = (SuggestionsDO) getArguments().get(KEY_DATA);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.activity_ca_customer_detail, container, false);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeControls(view);

        tvDate.setText(Methods.getFormattedDate(mSuggestionsDO.getDate()));
        tvSource.setText(mSuggestionsDO.getShortText());
        tvMessage.setText(mSuggestionsDO.getActualMessage());
        tvExpiryDate.setText("(expires on " + Methods.getFormattedDate(mSuggestionsDO.getExpiryDate()) + ")");
        setOnClickListeners();
        updateMaxLines();
        applyAnimation();
    }

    private void initializeControls(View view) {

        try {
            appVersion = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        vwSAM = (ViewPager) view.findViewById(R.id.vwSAM);
        btnCall = (Button) view.findViewById(R.id.btnCall);
        btnShare = (Button) view.findViewById(R.id.btnShare);
        tvDate = (TextView) view.findViewById(R.id.tvHeader);
        tvSource = (TextView) view.findViewById(R.id.tvSource);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        tvViewMore = (TextView) view.findViewById(R.id.tvViewMore);
        tvExpiryDate = (TextView) view.findViewById(R.id.tvExpiryDate);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        llMessage = (LinearLayout) view.findViewById(R.id.llMessage);
        llRelevant = (LinearLayout) view.findViewById(R.id.llRelevant);
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);

        tabs.setDistributeEvenly(true);
        tabs.setCustomTabView(R.layout.ca_detail_tab_text, R.id.tab_textview);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getActivity(), R.color.white);
            }
        });
        tabs.setSelectedIndicatorColors(getResources().getColor(R.color.lt_black));

        vwSAM.setAdapter(new SAMPagerAdapter(getActivity()));
        tabs.setViewPager(vwSAM,ContextCompat.getColorStateList(getActivity(),R.color.lt_black));
        btnShare.setText(getString(R.string.share));
    }

    private void setOnClickListeners() {
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((CustomerAssistantActivity) getActivity()).pref.edit()
                        .putInt(Key_Preferences.NO_OF_TIMES_RESPONDED, ++noOfTimesResponded).apply();

                mSuggestionsDO.setStatus(1);
                ((CustomerAssistantActivity) getActivity()).updateActionsToServer(mSuggestionsDO);


                FirebaseLogger.getInstance().logSAMEvent(mSuggestionsDO.getMessageId(), FirebaseLogger.SAMSTATUS.ACTION_CALL, mSuggestionsDO.getFpId(), appVersion);
                MixPanelController.track(MixPanelController.SAM_BUBBLE_ACTION_CALL, null);

                Intent i = new Intent();
                i.setAction(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + mSuggestionsDO.getValue()));
                startActivity(i);

            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((CustomerAssistantActivity) getActivity()).pref.edit().
                        putInt(Key_Preferences.NO_OF_TIMES_RESPONDED, ++noOfTimesResponded).apply();

                mSuggestionsDO.setStatus(1);
                ((CustomerAssistantActivity) getActivity()).updateActionsToServer(mSuggestionsDO);

                FirebaseLogger.getInstance().logSAMEvent(mSuggestionsDO.getMessageId(), FirebaseLogger.SAMSTATUS.ACTION_SHARE,
                        mSuggestionsDO.getFpId(), appVersion);
                MixPanelController.track(MixPanelController.SAM_BUBBLE_ACTION_SHARE, null);


                if (mSuggestionsDO.getType().equalsIgnoreCase(ACTION_TYPE_NUMBER)) {
                    prepareMessageForShare(SHARE_VIA.SMS);
                } else if (mSuggestionsDO.getType().equalsIgnoreCase(ACTION_TYPE_EMAIL)) {
                    prepareMessageForShare(SHARE_VIA.GMAIL);
                }

            }
        });

        tvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isViewMore = !isViewMore;
                updateMaxLines();
            }
        });

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private Intent shareIntent = null;
    private String selectedProducts = "";

    private void prepareMessageForShare(SHARE_VIA share_via) {


        if (mSuggestionsDO != null) {
            shareIntent = null;
            selectedProducts = "";

            String imageUrl = "";

            if (mSuggestionsDO.getUpdates() != null) {
                for (final SugUpdates sugUpdates : mSuggestionsDO.getUpdates()) {
                    if (sugUpdates.isSelected()) {

                        try {
                            selectedProducts = selectedProducts + sugUpdates.getName() + "\n" + "\n" + "View Details : " + sugUpdates.getUpdateUrl() + "\n" + "\n";
                            imageUrl = sugUpdates.getImage();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (mSuggestionsDO.getProducts() != null) {
                for (final SugProducts sugProducts : mSuggestionsDO.getProducts()) {
                    if (sugProducts.isSelected()) {

                        try {
                            selectedProducts = selectedProducts + sugProducts.getProductName() + "\n" + "\n" + "View Details : " +
                                    sugProducts.getProductUrl() + "\n" + "\n";
                            imageUrl = sugProducts.getImage();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


            if (TextUtils.isEmpty(selectedProducts)) {
                Toast.makeText(getActivity(), getString(R.string.select_update_or_product), Toast.LENGTH_SHORT).show();
            } else {
                switch (share_via) {
                    case GMAIL:
                        Picasso.get()
                                .load(imageUrl)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                        View view = new View(getActivity());
                                        view.draw(new Canvas(mutableBitmap));
                                        try {
                                            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), mutableBitmap, "Nur", null);
                                            BoostLog.d("Path is:", path);
                                            Uri uri = Uri.parse(path);
                                            shareIntent =
                                                    new Intent(Intent.ACTION_SEND, Uri.parse("mailto:" +
                                                            mSuggestionsDO.getValue()));
                                            shareIntent.putExtra(Intent.EXTRA_TEXT, selectedProducts);
                                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                            shareIntent.setType("image/*");


                                            if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                                startActivity(shareIntent);
                                            } else {
                                                Toast.makeText(getActivity(), getString(R.string.no_app_available_for_action), Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (Exception e) {
                                            ActivityCompat.requestPermissions(getActivity()
                                                    , new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                            android.Manifest.permission.CAMERA}, 2);
                                        }
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e,Drawable errorDrawable) {
                                        Toast.makeText(getActivity(), getString(R.string.failed_to_download_image), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                        break;
                    case SMS:
                        try {

                            Uri uri = Uri.parse("smsto:" + mSuggestionsDO.getValue());
                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                            smsIntent.putExtra("address", mSuggestionsDO.getValue());
                            smsIntent.putExtra("sms_body", selectedProducts);
                            startActivity(smsIntent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }

    }


    enum SHARE_VIA {
        GMAIL,
        SMS
    }

    private void applyAnimation() {

//        YoYo.with(Techniques.SlideInDown)
//                .duration(500)
//                .playOn(llMessage);
//        YoYo.with(Techniques.SlideInUp)
//                .duration(500)
//                .playOn(llRelevant);

        llMessage.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down));
//        llRelevant.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.juspay_slide_from_below));

    }

    private void updateMaxLines() {

        if (!isViewMore) {
            tvViewMore.setText(Methods.fromHtml(getString(R.string.view_more)));
            tvMessage.setMaxLines(2);

        } else {
            tvViewMore.setText(Methods.fromHtml(getString(R.string.view_less)));
            tvMessage.setMaxLines(100);

        }
    }

    @Override
    public void onSelection(boolean isSelected) {

        if (isSelected)
            selectedCount++;
        else
            selectedCount--;

        if (selectedCount > 0) {
            btnShare.setText(getString(R.string.share) + " (" + selectedCount + ")");
        } else {
            btnShare.setText(getString(R.string.share));
        }
    }


    private class SAMPagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;
        private ArrayList<String> arrPageTitle;

        private SAMPagerAdapter(Context mcContext) {
            inflater = LayoutInflater.from(mcContext);
            arrPageTitle = new ArrayList<>();

            arrPageTitle.add("UPDATES");
            arrPageTitle.add("PRODUCTS");
        }

        @Override
        public int getCount() {
            return arrPageTitle.size();
        }

        public Object instantiateItem(ViewGroup container, final int position) {
            View currentView = null;
            Log.e("SAMPagerAdapter", "instantiateItem for " + position);
            if (position == 0) {

                currentView = inflater.inflate(R.layout.sug_updates_list, null);
                TextView tvNoItems = (TextView) currentView.findViewById(R.id.tvNoItems);
                final ImageView ivScrollDown = (ImageView) currentView.findViewById(R.id.iv_scroll_down);
                final RecyclerView rvSuggestions = (RecyclerView) currentView.findViewById(R.id.rvSuggestions);

                rvSuggestions.setVisibility(View.VISIBLE);
                tvNoItems.setVisibility(View.GONE);


                if (mSuggestionsDO.getUpdates() != null && mSuggestionsDO.getUpdates().size() > 0) {
                    rvSuggestions.setLayoutManager(new LinearLayoutManager(getActivity()));
                    rvSuggestions.setAdapter(new CAUpdatesAdapter(getActivity(),
                            (ArrayList<SugUpdates>) mSuggestionsDO.getUpdates(), CustomerAssistantDetailFragment.this));

                    ivScrollDown.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rvSuggestions.smoothScrollToPosition(mSuggestionsDO.getUpdates().size() - 1);
                        }
                    });

                    rvSuggestions.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                            if (position < (mSuggestionsDO.getUpdates().size() - 2)) {
                                ivScrollDown.setVisibility(View.VISIBLE);
                            } else {
                                ivScrollDown.setVisibility(View.INVISIBLE);
                            }
                        }
                    });


                } else {
                    rvSuggestions.setVisibility(View.GONE);
                    tvNoItems.setVisibility(View.VISIBLE);
                }

            } else if (position == 1) {

                currentView = inflater.inflate(R.layout.sug_products_list, null);
                TextView tvNoItems = (TextView) currentView.findViewById(R.id.tvNoItems);
                final ImageView ivScrollDown = (ImageView) currentView.findViewById(R.id.iv_scroll_down);
                final GridView gvSuggestions = (GridView) currentView.findViewById(R.id.gvSuggestions);

                gvSuggestions.setVisibility(View.VISIBLE);
                tvNoItems.setVisibility(View.GONE);
                gvSuggestions.setHorizontalSpacing(Methods.dpToPx(20, getActivity()));
                gvSuggestions.setVerticalSpacing(Methods.dpToPx(20, getActivity()));
                if (mSuggestionsDO.getProducts() != null && mSuggestionsDO.getProducts().size() > 0) {
                    gvSuggestions.setAdapter(new CAProductsAdapter(getActivity(),
                            mSuggestionsDO.getProducts(), CustomerAssistantDetailFragment.this));

                    gvSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            final SugProducts sugProducts = (SugProducts) view.getTag(R.string.key_details);
                            sugProducts.setSelected(!sugProducts.isSelected());
                            FrameLayout flMain = (FrameLayout) view.findViewById(R.id.flMain);
                            FrameLayout flOverlay = (FrameLayout) view.findViewById(R.id.flOverlay);
                            View vwOverlay = view.findViewById(R.id.vwOverlay);
                            if (sugProducts.isSelected()) {
                                flOverlay.setVisibility(View.VISIBLE);
                                setOverlay(vwOverlay, 200, flMain.getWidth(), flMain.getHeight());
                            } else {
                                flOverlay.setVisibility(View.GONE);
                            }

                        }
                    });

                    ivScrollDown.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gvSuggestions.smoothScrollToPosition(mSuggestionsDO.getProducts().size() - 1);
                        }
                    });

                    gvSuggestions.setOnScrollListener(new GridView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {

                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                            if (visibleItemCount < (mSuggestionsDO.getProducts().size() - 2)) {
                                ivScrollDown.setVisibility(View.VISIBLE);
                            } else {
                                ivScrollDown.setVisibility(View.INVISIBLE);
                            }
                        }


                    });

                } else {
                    gvSuggestions.setVisibility(View.GONE);
                    tvNoItems.setVisibility(View.VISIBLE);
                }

            }
            container.addView(currentView);
            return currentView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return arrPageTitle.get(position);
        }
    }

    public void setOverlay(View v, int opac, int width, int height) {
        int opacity = opac; // from 0 to 255
        v.setBackgroundColor(opacity * 0x1000000); // black with a variable alpha
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(width, height);
        params.gravity = Gravity.NO_GRAVITY;
        v.setLayoutParams(params);
        v.invalidate();
    }
}
