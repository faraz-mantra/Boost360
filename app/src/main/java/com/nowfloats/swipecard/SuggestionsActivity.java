package com.nowfloats.swipecard;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.bubble.BubblesService;
import com.nowfloats.swipecard.models.MessageDO;
import com.nowfloats.swipecard.models.SMSSuggestions;
import com.nowfloats.swipecard.models.SugUpdates;
import com.nowfloats.swipecard.models.SuggestionsDO;
import com.nowfloats.swipecard.service.SuggestionsApi;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SuggestionsActivity extends AppCompatActivity implements SwipeStack.SwipeStackListener {

    private SwipeStack mSwipeStack;
    private SwipeStackAdapter mSwipeStackAdapter;
    private SuggestionsApi suggestionsApi;
    private Bus mBus;
    private UserSessionManager session;
    private ImageView ivMail, ivCall, ivSms;
    private Button btnShare, btnLater, btnBack, btnExit;
    private LinearLayout llStackView, llProductsView;
    private GridView gvSuggestions;
    private SuggestionListAdapter suggestionListAdapter;
    private ProgressBar pbView;
    private SharedPreferences pref;

    private class KillListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    private KillListener killListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);
        this.setFinishOnTouchOutside(false);
        mBus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(getApplicationContext(), SuggestionsActivity.this);
        suggestionsApi = new SuggestionsApi(mBus);
        initializeControls();
    }

    private void initializeControls() {
        setDisplayMetrcis(0.80f, 0.40f, true);
        mSwipeStack = (SwipeStack) findViewById(R.id.swipeStack);
        ivMail = (ImageView) findViewById(R.id.ivMail);
        ivCall = (ImageView) findViewById(R.id.ivCall);
        ivSms = (ImageView) findViewById(R.id.ivSms);
        btnShare = (Button) findViewById(R.id.btnShare);
        btnLater = (Button) findViewById(R.id.btnLater);
        btnBack = (Button) findViewById(R.id.btnBack);
        llStackView = (LinearLayout) findViewById(R.id.llStackView);
        llProductsView = (LinearLayout) findViewById(R.id.llProductsView);
        gvSuggestions = (GridView) findViewById(R.id.gvSuggestions);
        pbView = (ProgressBar) findViewById(R.id.pbView);
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        killListener = new KillListener();
        mSwipeStackAdapter = new SwipeStackAdapter(null);
        mSwipeStack.setAdapter(mSwipeStackAdapter);
        mSwipeStack.setListener(this);
        suggestionListAdapter = new SuggestionListAdapter(SuggestionsActivity.this);
        gvSuggestions.setAdapter(suggestionListAdapter);

        setOnClickListeners();
        loadData();
    }

    private void setDisplayMetrcis(float width, float height, boolean isCenter) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = (int) (metrics.heightPixels * height);
        int screenWidth = (int) (metrics.widthPixels * width);
        if (isCenter)
            getWindow().setGravity(Gravity.CENTER);
        else
            getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(screenWidth, screenHeight);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private void setOnClickListeners() {
        ivMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareMessageForShare(SHARE_VIA.GMAIL);
            }
        });

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestionsDO mSuggestionsDO = mSwipeStackAdapter.getTopItem();
                Intent i = new Intent();
                i.setAction(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + mSuggestionsDO.getValue()));
                startActivity(i);
            }
        });

        ivSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prepareMessageForShare(SHARE_VIA.SMS);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestionsDO mSuggestionsDO = mSwipeStackAdapter.getTopItem();


                if (mSuggestionsDO.getUpdates().size() > 0) {
                    sendBroadcast(new Intent(BubblesService.ACTION_GO_TO_RIGHT_WALL));
                    llProductsView.setVisibility(View.VISIBLE);
                    llStackView.setVisibility(View.GONE);
                    suggestionListAdapter.refreshDetails((ArrayList<SugUpdates>) mSuggestionsDO.getUpdates());
                    gvSuggestions.setAdapter(suggestionListAdapter);
                    setDisplayMetrcis(1.0f, 0.80f, false);
                } else {

                }
            }
        });

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestionsDO mSuggestionsDO = mSwipeStackAdapter.getItem(mSwipeStack.getCurrentPosition());
                mSuggestionsDO.setStatus(0);
                mSwipeStack.swipeTopViewToRight();
            }
        });

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

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent(BubblesService.ACTION_GO_TO_RIGHT_WALL));
                setDisplayMetrcis(0.80f, 0.40f, true);
                llProductsView.setVisibility(View.GONE);
                llStackView.setVisibility(View.VISIBLE);
            }
        });

    }

    enum SHARE_VIA {
        GMAIL,
        SMS
    }

    private Intent shareIntent = null;

    private void prepareMessageForShare(SHARE_VIA share_via) {

        final SuggestionsDO mSuggestionsDO = mSwipeStackAdapter.getTopItem();

        for (final SugUpdates sugUpdates : suggestionListAdapter.getDetails()) {

            shareIntent = null;
            if (sugUpdates.isSelected()) {
                switch (share_via) {
                    case GMAIL:
                        Picasso.with(SuggestionsActivity.this)
                                .load(sugUpdates.getImage())
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                        View view = new View(SuggestionsActivity.this);
                                        view.draw(new Canvas(mutableBitmap));
                                        try {
                                            String path = MediaStore.Images.Media.insertImage(getContentResolver(), mutableBitmap, "Nur", null);
                                            BoostLog.d("Path is:", path);
                                            Uri uri = Uri.parse(path);
                                            shareIntent =
                                                    new Intent(Intent.ACTION_SEND, Uri.parse("mailto:" + mSuggestionsDO.getValue()));
                                            shareIntent.putExtra(Intent.EXTRA_TEXT, sugUpdates.getName());
                                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                            shareIntent.setType("image/*");


                                            if (shareIntent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(shareIntent);
                                            } else {
                                                Methods.showSnackBarNegative(SuggestionsActivity.this, getString(R.string.no_app_available_for_action));
                                            }

                                        } catch (Exception e) {
                                            ActivityCompat.requestPermissions(SuggestionsActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, 2);
                                        }
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {
                                        Methods.showSnackBarNegative(SuggestionsActivity.this, getString(R.string.failed_to_download_image));

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
                            smsIntent.putExtra("address", mSuggestionsDO.getValue());
                            smsIntent.putExtra("sms_body", sugUpdates.getName());
                            startActivity(smsIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }


            }
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

    private void loadData() {

        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("fpId", session.getFPID());
        suggestionsApi.getMessages(offersParam);
    }

    @Subscribe
    public void processSMSDetails(SMSSuggestions suggestions) {

        pbView.setVisibility(View.GONE);
        if (suggestions != null && suggestions.getSuggestion() != null
                && suggestions.getSuggestion().size() > 0) {
            mSwipeStackAdapter.refresh(suggestions.getSuggestion());
        } else {
            Methods.showSnackBarNegative(SuggestionsActivity.this, getString(R.string.no_info_avail));
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BubblesService.ACTION_KILL_DIALOG);
        registerReceiver(killListener, intentFilter);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        unregisterReceiver(killListener);
        super.onStop();
    }

    @Override
    public void onViewSwipedToRight(int position) {
        mSwipeStackAdapter.getItem(position).setStatus(-1);
        processMessage(mSwipeStackAdapter.getItem(position));
    }

    @Override
    public void onViewSwipedToLeft(int position) {
        mSwipeStackAdapter.getItem(position).setStatus(-1);
        processMessage(mSwipeStackAdapter.getItem(position));
    }

    @Override
    public void onStackEmpty() {
        pref.edit().putBoolean(Key_Preferences.HAS_SUGGESTIONS, false).apply();
        finish();
    }


    private void processMessage(SuggestionsDO mSuggestions) {

        MessageDO messageDO = new MessageDO();
        messageDO.setMessageId(mSuggestions.getMessageId());
        messageDO.setStatus(mSuggestions.getStatus());
        messageDO.setFpId(mSuggestions.getFpId());
        ArrayList<MessageDO> arrMessages = new ArrayList<>();
        arrMessages.add(messageDO);
        suggestionsApi.updateMessage(arrMessages);
    }

    public class SwipeStackAdapter extends BaseAdapter {

        private List<SuggestionsDO> mSuggestions;

        public SwipeStackAdapter(List<SuggestionsDO> data) {
            this.mSuggestions = data;
        }

        @Override
        public int getCount() {

            if (mSuggestions != null && mSuggestions.size() > 0)
                return mSuggestions.size();

            return 0;
        }

        public SuggestionsDO getTopItem() {

            return getItem(mSwipeStack.getCurrentPosition());
        }

        @Override
        public SuggestionsDO getItem(int position) {
            return mSuggestions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void refresh(List<SuggestionsDO> mSuggestions) {
            this.mSuggestions = mSuggestions;
            notifyDataSetChanged();
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            SuggestionsDO suggestions = mSuggestions.get(position);
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.suggestions_card_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textViewCard = (TextView) convertView.findViewById(R.id.textViewCard);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            viewHolder.textViewCard.setText(suggestions.getAction());
            return convertView;
        }

        private class ViewHolder {
            TextView textViewCard;
        }

    }

    @Override
    protected void onDestroy() {
        sendBroadcast(new Intent(BubblesService.ACTION_RESET_BUBBLE));
        super.onDestroy();
    }
}
