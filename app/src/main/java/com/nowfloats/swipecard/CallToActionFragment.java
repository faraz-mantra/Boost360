package com.nowfloats.swipecard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nowfloats.swipecard.adapters.CallToActionAdapter;
import com.nowfloats.swipecard.adapters.SuggestionListAdapter;
import com.nowfloats.swipecard.models.SugUpdates;
import com.nowfloats.swipecard.models.SuggestionsDO;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Methods;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thinksity.R;


/**
 * Created by admin on 5/31/2017.
 */

public class CallToActionFragment extends Fragment {


    private RecyclerView rvList;

    private GridView gvSuggestions;

    private CallToActionAdapter actionItemsAdapter;

    public ProgressBar pbView;

    private LinearLayout llProductView;

    private ImageView ivShare, ivCall, ivSms;


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

        rvList = (RecyclerView) view.findViewById(R.id.rvList);
        pbView = (ProgressBar) view.findViewById(R.id.pbView);
        gvSuggestions = (GridView) view.findViewById(R.id.gvSuggestions);
        llProductView = (LinearLayout) view.findViewById(R.id.llProductView);
        ivShare = (ImageView) view.findViewById(R.id.ivShare);
        ivCall = (ImageView) view.findViewById(R.id.ivCall);
        ivSms = (ImageView) view.findViewById(R.id.ivSms);

        pbView.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        ((SuggestionsActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("Make Calls");

        ((SuggestionsActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));

    }

    private void prepareActionItemList() {

        actionItemsAdapter = new CallToActionAdapter(this);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(actionItemsAdapter);

        actionItemsAdapter.refreshList(((SuggestionsActivity) getActivity()).smsSuggestions.getSuggestionList());

    }

    private void setOnClickListeners() {


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

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + suggestionsDO.getValue()));
                startActivity(i);
            }
        });

        ivSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent smsIntent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.setData(Uri.parse("smsto:" + Uri.encode(suggestionsDO.getValue())));
                } else {
                    smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", suggestionsDO.getValue());
                    smsIntent.putExtra("sms_body", "");
                }
                startActivity(smsIntent);

            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareMessageForShare(SHARE_VIA.GMAIL);
            }
        });
    }

    private SuggestionsDO suggestionsDO;

    public void performAction(SuggestionsDO suggestionsDO) {

        this.suggestionsDO = suggestionsDO;

        llProductView.setVisibility(View.VISIBLE);
        rvList.setVisibility(View.GONE);

        YoYo.with(Techniques.SlideInUp)
                .duration(500)
                .playOn(llProductView);

        gvSuggestions.setAdapter(new SuggestionListAdapter(getActivity(), suggestionsDO.getUpdates()));
    }


    public boolean isProductsVisible() {
        return llProductView.getVisibility() == View.VISIBLE;
    }

    public void displayCTA() {

        llProductView.setVisibility(View.GONE);
        rvList.setVisibility(View.VISIBLE);
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
                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.setType("vnd.android-dir/mms-sms");
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

}
