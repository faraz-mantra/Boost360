package com.nowfloats.swipecard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nowfloats.NavigationDrawer.SlidingTabLayout;
import com.nowfloats.swipecard.adapters.CallToActionAdapter;
import com.nowfloats.swipecard.adapters.SugProductsAdapter;
import com.nowfloats.swipecard.adapters.SugUpdatesAdapter;
import com.nowfloats.swipecard.models.SugUpdates;
import com.nowfloats.swipecard.models.SuggestionsDO;
import com.nowfloats.util.BoostLog;
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

        rvActionItems = (RecyclerView) view.findViewById(R.id.rvActionItems);
        pbView = (ProgressBar) view.findViewById(R.id.pbView);
        vwSAM = (ViewPager) view.findViewById(R.id.vwSAM);
        llProductView = (LinearLayout) view.findViewById(R.id.llProductView);

        btnCall = (Button) view.findViewById(R.id.btnCall);
        btnShare = (Button) view.findViewById(R.id.btnShare);

        pbView.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        ((SuggestionsActivity) getActivity()).setSupportActionBar(toolbar);

        getActivity().setTitle(Html.fromHtml("Sam <i>says..</i>"));
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

                MixPanelController.track(MixPanelController.SAM_CALL, null);

                FirebaseLogger.getInstance().logSAMEvent(suggestionsDO.getMessageId(), 3, suggestionsDO.getFpId());

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

                MixPanelController.track(MixPanelController.SAM_SHARE, null);

                FirebaseLogger.getInstance().logSAMEvent(suggestionsDO.getMessageId(), 3, suggestionsDO.getFpId());

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

        FirebaseLogger.getInstance().logSAMEvent(suggestionsDO.getMessageId(), 2, suggestionsDO.getFpId());

        this.suggestionsDO = suggestionsDO;

        getActivity().setTitle(Html.fromHtml("Sam <i>suggests..</i>"));
        ((SuggestionsActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        llProductView.setVisibility(View.VISIBLE);
        rvActionItems.setVisibility(View.GONE);


        if (suggestionsDO.getType().equalsIgnoreCase(ACTION_TYPE_NUMBER)) {
            btnCall.setVisibility(View.VISIBLE);
            btnShare.setText("Message");
        } else {
            btnCall.setVisibility(View.GONE);
            btnShare.setText("Share");
        }

        YoYo.with(Techniques.SlideInUp)
                .duration(500)
                .playOn(llProductView);

        vwSAM.setAdapter(new SAMPagerAdapter(getActivity()));
        tabs.setViewPager(vwSAM);
    }


    public boolean isProductsVisible() {
        return llProductView.getVisibility() == View.VISIBLE;
    }

    public void displayCTA() {

        llProductView.setVisibility(View.GONE);
        rvActionItems.setVisibility(View.VISIBLE);

        getActivity().setTitle(Html.fromHtml("Sam <i>says..</i>"));
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


    private void prepareMessageForShare(SHARE_VIA share_via) {

        shareIntent = null;
        for (final SugUpdates sugUpdates : suggestionsDO.getUpdates()) {

            if (sugUpdates.isSelected()) {
                switch (share_via) {
                    case GMAIL:
                        Picasso.with(getActivity())
                                .load(sugUpdates.getImage())
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
                                            shareIntent.putExtra(Intent.EXTRA_TEXT, sugUpdates.getName());
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
                                    public void onBitmapFailed(Drawable errorDrawable) {
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
                            smsIntent.putExtra("sms_body", sugUpdates.getName());
                            startActivity(smsIntent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }

                break;
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

        public Object instantiateItem(ViewGroup container, int position) {
            View currentView = null;
            Log.e("SAMPagerAdapter", "instantiateItem for " + position);
            if (position == 0) {
                currentView = inflater.inflate(R.layout.sug_updates_list, null);
                RecyclerView rvSuggestions = (RecyclerView) currentView.findViewById(R.id.rvSuggestions);
                rvSuggestions.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvSuggestions.setAdapter(new SugUpdatesAdapter((ArrayList<SugUpdates>) suggestionsDO.getUpdates()));

            } else if (position == 1) {
                currentView = inflater.inflate(R.layout.sug_products_list, null);

                GridView gvSuggestions = (GridView) currentView.findViewById(R.id.gvSuggestions);

                gvSuggestions.setAdapter(new SugProductsAdapter(getActivity(), suggestionsDO.getProducts()));


                gvSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final SugUpdates sugUpdates = (SugUpdates) view.getTag(R.string.key_details);
                        sugUpdates.setSelected(!sugUpdates.isSelected());
                        FrameLayout flMain = (FrameLayout) view.findViewById(R.id.flMain);
                        FrameLayout flOverlay = (FrameLayout) view.findViewById(R.id.flOverlay);
                        View vwOverlay = view.findViewById(R.id.vwOverlay);
                        if (sugUpdates.isSelected()) {
                            flOverlay.setVisibility(View.VISIBLE);
                            setOverlay(vwOverlay, 200, flMain.getWidth(), flMain.getHeight());
                        } else {
                            flOverlay.setVisibility(View.GONE);
                        }
                    }
                });
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
