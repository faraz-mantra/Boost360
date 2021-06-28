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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nowfloats.NavigationDrawer.SlidingTabLayout;
import com.nowfloats.customerassistant.adapters.CallToActionAdapter;
import com.nowfloats.customerassistant.adapters.CAProductsAdapter;
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


/**
 * Created by admin on 5/31/2017.
 */

public class CallToActionFragment extends Fragment {


    private RecyclerView rvActionItems;

    private ViewPager vwSAM;

    private CallToActionAdapter actionItemsAdapter;

    public ProgressBar pbView;

    private LinearLayout llProductView;

    private Button btnCall, btnShare;

    private SlidingTabLayout tabs;

    private final String ACTION_TYPE_NUMBER = "contactNumber";

    private final String ACTION_TYPE_EMAIL = "email";

    private static final int MAX_RESPONDED = 3;

    private int noOfTimesResponded = 0;

    private String appVersion = "";

    public CallToActionFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.csp_fragment_action_items, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeControls(view);

        prepareActionItemList();

        setOnClickListeners();
    }

    private void initializeControls(View view) {

        try {
            appVersion = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        rvActionItems = (RecyclerView) view.findViewById(R.id.rvActionItems);
        pbView = (ProgressBar) view.findViewById(R.id.pbView);
        vwSAM = (ViewPager) view.findViewById(R.id.vwSAM);
        llProductView = (LinearLayout) view.findViewById(R.id.llProductView);

        btnCall = (Button) view.findViewById(R.id.btnCall);
        btnShare = (Button) view.findViewById(R.id.btnShare);

        pbView.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        ((SuggestionsActivity) getActivity()).setSupportActionBar(toolbar);

//        getActivity().setTitle(Html.fromHtml("Sam <i>says..</i>"));
        getActivity().setTitle("Your Leads");
        ((SuggestionsActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabView(R.layout.tab_text, R.id.tab_textview);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getContext(), R.color.white);
            }
        });

        noOfTimesResponded = ((SuggestionsActivity) getActivity()).
                pref.getInt(Key_Preferences.NO_OF_TIMES_RESPONDED, 0);

        if (noOfTimesResponded >= MAX_RESPONDED) {
            showRating();
        }

    }

    private int noOfStars = 0;

    private void showRating() {

        noOfStars = 0;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
//                .title(getString(R.string.enjoying_feature))
                .customView(R.layout.csp_fragment_rating, false)
                .positiveText(getString(R.string.submit))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        ((SuggestionsActivity) getActivity()).pref.edit()
                                .putInt(Key_Preferences.NO_OF_TIMES_RESPONDED, 0).apply();
                        ((SuggestionsActivity) getActivity()).updateRating(noOfStars);
                    }
                })
                .positiveColorRes(R.color.primaryColor);

        if (!getActivity().isFinishing()) {

            final MaterialDialog materialDialog = builder.show();
            materialDialog.setCancelable(false);

            View mView = materialDialog.getCustomView();
            final RatingBar mRatingBar = (RatingBar) mView.findViewById(R.id.ratingbar);
            mRatingBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        float touchPositionX = event.getX();
                        float width = mRatingBar.getWidth();
                        float starsf = (touchPositionX / width) * 5.0f;
                        int stars = (int) starsf + 1;
                        mRatingBar.setRating(stars);
                        noOfStars = stars;
                        v.setPressed(false);
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setPressed(true);
                    }

                    if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        v.setPressed(false);
                    }

                    return true;
                }
            });
        }
    }

    private void prepareActionItemList() {

        actionItemsAdapter = new CallToActionAdapter(this);
        rvActionItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvActionItems.setAdapter(actionItemsAdapter);

        actionItemsAdapter.refreshList(((SuggestionsActivity) getActivity()).smsSuggestions.getSuggestionList());

    }

    private void setOnClickListeners() {


        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseLogger.getInstance().logSAMEvent(suggestionsDO.getMessageId(), FirebaseLogger.SAMSTATUS.ACTION_CALL, suggestionsDO.getFpId(), appVersion);
                MixPanelController.track(MixPanelController.SAM_BUBBLE_ACTION_CALL, null);

                ((SuggestionsActivity) getActivity()).pref.edit()
                        .putInt(Key_Preferences.NO_OF_TIMES_RESPONDED, ++noOfTimesResponded).apply();

                suggestionsDO.setStatus(1);
                ((SuggestionsActivity) getActivity()).updateActionsToServer(suggestionsDO);

                Intent i = new Intent();
                i.setAction(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + suggestionsDO.getValue()));
                startActivity(i);

            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FirebaseLogger.getInstance().logSAMEvent(suggestionsDO.getMessageId(), FirebaseLogger.SAMSTATUS.ACTION_SHARE, suggestionsDO.getFpId(), appVersion);
                MixPanelController.track(MixPanelController.SAM_BUBBLE_ACTION_SHARE, null);

                ((SuggestionsActivity) getActivity()).pref.edit().
                        putInt(Key_Preferences.NO_OF_TIMES_RESPONDED, ++noOfTimesResponded).apply();

                suggestionsDO.setStatus(1);
                ((SuggestionsActivity) getActivity()).updateActionsToServer(suggestionsDO);


                if (suggestionsDO.getType().equalsIgnoreCase(ACTION_TYPE_NUMBER)) {
                    prepareMessageForShare(SHARE_VIA.SMS);
                } else if (suggestionsDO.getType().equalsIgnoreCase(ACTION_TYPE_EMAIL)) {
                    prepareMessageForShare(SHARE_VIA.GMAIL);
                }

            }
        });
    }

    private SuggestionsDO suggestionsDO;

    public void performAction(SuggestionsDO suggestionsDO) {

        FirebaseLogger.getInstance().logSAMEvent(suggestionsDO.getMessageId(), FirebaseLogger.SAMSTATUS.SELECTED_MESSAGES, suggestionsDO.getFpId(), appVersion);
        MixPanelController.track(MixPanelController.SAM_BUBBLE_SELECTED_MESSAGES, null);

        this.suggestionsDO = suggestionsDO;

//        getActivity().setTitle(Html.fromHtml("Sam <i>suggests..</i>"));
        getActivity().setTitle("Updates & Products");
        ((SuggestionsActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        llProductView.setVisibility(View.VISIBLE);
        rvActionItems.setVisibility(View.GONE);


        if (suggestionsDO.getType() != null && suggestionsDO.getType().equalsIgnoreCase(ACTION_TYPE_NUMBER)) {
            btnCall.setVisibility(View.VISIBLE);
            btnShare.setText("Share");
        } else {
            btnCall.setVisibility(View.GONE);
            btnShare.setText("Share");
        }

        YoYo.with(Techniques.SlideInUp)
                .duration(500)
                .playOn(llProductView);

    }


    public boolean isProductsVisible() {
        return llProductView.getVisibility() == View.VISIBLE;
    }

    public void displayCTA() {

        llProductView.setVisibility(View.GONE);
        rvActionItems.setVisibility(View.VISIBLE);

//        getActivity().setTitle(Html.fromHtml("Sam <i>says..</i>"));
        getActivity().setTitle("Your Leads");
        ((SuggestionsActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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

    enum SHARE_VIA {
        GMAIL,
        SMS
    }

    private Intent shareIntent = null;
    private String selectedProducts = "";


    private void prepareMessageForShare(SHARE_VIA share_via) {


        if (suggestionsDO != null) {
            shareIntent = null;
            selectedProducts = "";

            String imageUrl = "";

            if (suggestionsDO.getUpdates() != null) {
                for (final SugUpdates sugUpdates : suggestionsDO.getUpdates()) {
                    if (sugUpdates.isSelected()) {

                        try {
                            selectedProducts = selectedProducts + sugUpdates.getName() + "\n" + "\n" + getString(R.string.view_details_) + sugUpdates.getUpdateUrl() + "\n" + "\n";
                            imageUrl = sugUpdates.getImage();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (suggestionsDO.getProducts() != null) {
                for (final SugProducts sugProducts : suggestionsDO.getProducts()) {
                    if (sugProducts.isSelected()) {

                        try {
                            selectedProducts = selectedProducts + sugProducts.getProductName() + "\n" + "\n" + getString(R.string.view_details_) +
                                    sugProducts.getProductUrl() + "\n" + "\n";
                            imageUrl = sugProducts.getImage();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


            if (TextUtils.isEmpty(selectedProducts)) {
                Methods.showSnackBarNegative(getActivity(), getString(R.string.select_update_or_product));
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
                                                            suggestionsDO.getValue()));
                                            shareIntent.putExtra(Intent.EXTRA_TEXT, selectedProducts);
                                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                            shareIntent.setType("image/*");


                                            if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                                startActivity(shareIntent);
                                            } else {
                                                Methods.showSnackBarNegative(getActivity(),
                                                        getString(R.string.no_app_available_for_action));
                                            }

                                        } catch (Exception e) {
                                            ActivityCompat.requestPermissions(getActivity()
                                                    , new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                            android.Manifest.permission.CAMERA}, 2);
                                        }
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e,Drawable errorDrawable) {
                                        Methods.showSnackBarNegative(getActivity(), getString(R.string.failed_to_download_image));

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                        break;
                    case SMS:
                        try {

                            Uri uri = Uri.parse("smsto:" + suggestionsDO.getValue());
                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                            smsIntent.putExtra("address", suggestionsDO.getValue());
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
                    rvSuggestions.setLayoutManager(new LinearLayoutManager(getActivity()));
//                    rvSuggestions.setAdapter(new CAUpdatesAdapter(getActivity(),
//                            (ArrayList<SugUpdates>) suggestionsDO.getUpdates()),null);

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
                    gvSuggestions.setAdapter(new CAProductsAdapter(getActivity(), suggestionsDO.getProducts()));

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
}
