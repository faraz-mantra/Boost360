package com.nowfloats.sam;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nowfloats.NavigationDrawer.SlidingTabLayout;
import com.nowfloats.sam.adapters.SugProductsAdapter;
import com.nowfloats.sam.adapters.SugUpdatesAdapter;
import com.nowfloats.sam.models.SugProducts;
import com.nowfloats.sam.models.SugUpdates;
import com.nowfloats.sam.models.SuggestionsDO;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by admin on 8/17/2017.
 */

public class SAMCustomerDetailActivity extends Activity {


    public ProgressBar pbView;

    private ViewPager vwSAM;

    private Button btnCall, btnShare;

    private SlidingTabLayout tabs;

    private SuggestionsDO suggestionsDO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.bubble_scale_up, R.anim.bubble_scale_down);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sam_customer_detail);

        initializeControls();


    }

    private void initializeControls() {

        suggestionsDO = new SuggestionsDO();

        pbView = (ProgressBar) findViewById(R.id.pbView);
        vwSAM = (ViewPager) findViewById(R.id.vwSAM);
        btnCall = (Button) findViewById(R.id.btnCall);
        btnShare = (Button) findViewById(R.id.btnShare);
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);

        tabs.setDistributeEvenly(true);
        tabs.setCustomTabView(R.layout.tab_text, R.id.tab_textview);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(SAMCustomerDetailActivity.this, R.color.white);
            }
        });

        vwSAM.setAdapter(new SAMPagerAdapter(SAMCustomerDetailActivity.this));
        tabs.setViewPager(vwSAM);

    }

    public class SAMPagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;
        private ArrayList<String> arrPageTitle;

        public SAMPagerAdapter(Context mcContext) {
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


                if (suggestionsDO.getUpdates() != null && suggestionsDO.getUpdates().size() > 0) {
                    rvSuggestions.setLayoutManager(new LinearLayoutManager(SAMCustomerDetailActivity.this));
                    rvSuggestions.setAdapter(new SugUpdatesAdapter(SAMCustomerDetailActivity.this,
                            (ArrayList<SugUpdates>) suggestionsDO.getUpdates()));

                    ivScrollDown.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rvSuggestions.smoothScrollToPosition(suggestionsDO.getUpdates().size() - 1);
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

                            if (position < (suggestionsDO.getUpdates().size() - 2)) {
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

                if (suggestionsDO.getProducts() != null && suggestionsDO.getProducts().size() > 0) {
                    gvSuggestions.setAdapter(new SugProductsAdapter(SAMCustomerDetailActivity.this, suggestionsDO.getProducts()));

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
                            gvSuggestions.smoothScrollToPosition(suggestionsDO.getProducts().size() - 1);
                        }
                    });

                    gvSuggestions.setOnScrollListener(new GridView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {

                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                            if (visibleItemCount < (suggestionsDO.getUpdates().size() - 2)) {
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
