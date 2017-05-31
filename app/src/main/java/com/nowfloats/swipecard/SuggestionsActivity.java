package com.nowfloats.swipecard;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
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
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.utils.SpotlightListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.nowfloats.swipecard.SuggestionsActivity.TARGETVIEW.CALL;
import static com.nowfloats.swipecard.SuggestionsActivity.TARGETVIEW.DELETE;
import static com.nowfloats.swipecard.SuggestionsActivity.TARGETVIEW.MESSAGE;
import static com.nowfloats.swipecard.SuggestionsActivity.TARGETVIEW.REMIND;
import static com.nowfloats.swipecard.SuggestionsActivity.TARGETVIEW.SHARE;
import static com.nowfloats.swipecard.SuggestionsActivity.TARGETVIEW.VIEW_MORE;


public class SuggestionsActivity extends AppCompatActivity implements SwipeStack.SwipeStackListener {

    private SwipeStack mSwipeStack;
    private SwipeStackAdapter mSwipeStackAdapter;
    private SuggestionsApi suggestionsApi;
    private Bus mBus;
    private UserSessionManager session;
    private ImageView ivShare, ivCall, ivSms;
    private Button btnBack, btnShare;
    private ImageView ivDelete, ivViewMore, ivRemind;
    private TextView tvEnquiry;
    private LinearLayout llStackView, llProductsView, llCoachMark, llSuggestions;
    private FrameLayout flTopView;
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
        setDisplayMetrics(0.80f, 0.40f, true);
        mSwipeStack = (SwipeStack) findViewById(R.id.swipeStack);
        ivShare = (ImageView) findViewById(R.id.ivShare);
        ivCall = (ImageView) findViewById(R.id.ivCall);
        ivSms = (ImageView) findViewById(R.id.ivSms);
        ivViewMore = (ImageView) findViewById(R.id.ivViewMore);
        ivRemind = (ImageView) findViewById(R.id.ivRemind);
        ivDelete = (ImageView) findViewById(R.id.ivDelete);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnShare = (Button) findViewById(R.id.btnShare);
        llStackView = (LinearLayout) findViewById(R.id.llStackView);
        flTopView = (FrameLayout) findViewById(R.id.flTopView);
        llProductsView = (LinearLayout) findViewById(R.id.llProductsView);
        llCoachMark = (LinearLayout) findViewById(R.id.llCoachMark);
        llSuggestions = (LinearLayout) findViewById(R.id.llSuggestions);
        gvSuggestions = (GridView) findViewById(R.id.gvSuggestions);
        gvSuggestions = (GridView) findViewById(R.id.gvSuggestions);
        tvEnquiry = (TextView) findViewById(R.id.tvEnquiry);
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

    private void setDisplayMetrics(float width, float height, boolean isCenter) {
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
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestionsDO mSuggestionsDO = mSwipeStackAdapter.getTopItem();
                if (mSuggestionsDO.getUpdates().size() > 0) {
                    sendBroadcast(new Intent(BubblesService.ACTION_GO_TO_RIGHT_WALL));
                    llProductsView.setVisibility(View.VISIBLE);
                    llStackView.setVisibility(View.GONE);
                    llSuggestions.setVisibility(View.GONE);
                    suggestionListAdapter.refreshDetails((ArrayList<SugUpdates>) mSuggestionsDO.getUpdates());
                    gvSuggestions.setAdapter(suggestionListAdapter);
                    setDisplayMetrics(1.0f, 0.80f, false);
                } else {

                }
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

                SuggestionsDO mSuggestionsDO = mSwipeStackAdapter.getTopItem();
                Intent smsIntent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.setData(Uri.parse("smsto:" + Uri.encode(mSuggestionsDO.getValue())));
                } else {
                    smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", mSuggestionsDO.getValue());
                    smsIntent.putExtra("sms_body", "");
                }
                startActivity(smsIntent);

//                prepareMessageForShare(SHARE_VIA.SMS);
            }
        });

        ivViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestionsDO mSuggestionsDO = mSwipeStackAdapter.getTopItem();
                if (mSuggestionsDO.getUpdates().size() > 0) {
                    sendBroadcast(new Intent(BubblesService.ACTION_GO_TO_RIGHT_WALL));
                    llSuggestions.setVisibility(View.VISIBLE);
                    llStackView.setVisibility(View.GONE);
                    setDisplayMetrics(0.80f, 0.40f, true);

                    if (pref.getBoolean(Key_Preferences.IS_TO_SHOW_COACH_MARKS_STWO, true)) {
                        coachMessage = getString(R.string.coach_mark_msg_view_call);
                        targetView = ivCall;
                        showCoachMark(CALL);
                    }

                    tvEnquiry.setText("The enquiry says\n"
                            + mSuggestionsDO.getActualMessage());

                } else {

                }
            }
        });

        ivRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeStack.swipeTopViewToRight();
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeStack.swipeTopViewToLeft();
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
                setDisplayMetrics(0.80f, 0.40f, true);
                llProductsView.setVisibility(View.GONE);
                llSuggestions.setVisibility(View.VISIBLE);
                llStackView.setVisibility(View.GONE);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareMessageForShare(SHARE_VIA.GMAIL);
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
//        offersParam.put("fpId", session.getFPID());
        offersParam.put("fpId", "590462167ca16e051830201a");
        suggestionsApi.getMessages(offersParam);
    }

    @Subscribe
    public void processSMSDetails(SMSSuggestions suggestions) {

        pbView.setVisibility(View.GONE);
        if (suggestions != null && suggestions.getSuggestion() != null
                && suggestions.getSuggestion().size() > 0) {
            mSwipeStackAdapter.refresh(suggestions.getSuggestion());
            llStackView.setVisibility(View.VISIBLE);
//            ViewGroup.LayoutParams vwLayoutParams = new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, mSwipeStack.getHeight() + 100);
//            mSwipeStack.setLayoutParams(vwLayoutParams);

            if (pref.getBoolean(Key_Preferences.IS_TO_SHOW_COACH_MARKS_SONE, true))
                addCoachMarks();
        } else {
            Methods.showSnackBarNegative(SuggestionsActivity.this, getString(R.string.no_info_avail));
            finish();
        }
    }


    private void addCoachMarks() {

        llCoachMark.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(SuggestionsActivity.this)
                .inflate(R.layout.coach_mark_swipe_view, llCoachMark);

        AnimationSet animationSet = new AnimationSet(true);

        Animation rightAnimation = AnimationUtils.loadAnimation(SuggestionsActivity.this, R.anim.slide_coach_mark_right);
        Animation leftAnimation = AnimationUtils.loadAnimation(SuggestionsActivity.this, R.anim.slide_coach_mark_left);
        animationSet.addAnimation(rightAnimation);
        animationSet.addAnimation(leftAnimation);

        llCoachMark.findViewById(R.id.ivRight).setAnimation(rightAnimation);
        llCoachMark.findViewById(R.id.ivLeft).setAnimation(leftAnimation);
        animationSet.start();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llCoachMark.removeAllViews();
                llCoachMark.setVisibility(View.GONE);
                coachMessage = getString(R.string.coach_mark_msg_view_more);
                targetView = ivViewMore;

                showCoachMark(VIEW_MORE);
            }
        });
    }

    private String coachMessage = "";
    private View targetView;

    enum TARGETVIEW {
        DELETE,
        REMIND,
        VIEW_MORE,
        CALL,
        MESSAGE,
        SHARE
    }

    private void prepareCoachMark(final TARGETVIEW targetview) {


        switch (targetview) {
            case VIEW_MORE:
                coachMessage = getString(R.string.coach_mark_msg_remind);
                targetView = ivRemind;
                showCoachMark(REMIND);
                break;
            case REMIND:
                coachMessage = getString(R.string.coach_mark_msg_delete);
                targetView = ivDelete;
                showCoachMark(DELETE);
                break;
            case DELETE:
                pref.edit().putBoolean(Key_Preferences.IS_TO_SHOW_COACH_MARKS_SONE, false).apply();
                break;
            case CALL:
                coachMessage = getString(R.string.coach_mark_msg_view_message);
                targetView = ivSms;
                showCoachMark(MESSAGE);
                break;
            case MESSAGE:
                coachMessage = getString(R.string.coach_mark_msg_view_share);
                targetView = ivShare;
                showCoachMark(SHARE);
                break;
            case SHARE:
                pref.edit().putBoolean(Key_Preferences.IS_TO_SHOW_COACH_MARKS_STWO, false).apply();
                break;
        }


    }

    private void showCoachMark(final TARGETVIEW targetview) {

        SpotlightView.Builder spBuilder = new SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(getResources().getColor(R.color.white))
                .headingTvSize(32)
                .headingTvText("Hello")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText(coachMessage)
                .maskColor(Color.parseColor("#dc000000"))
                .target(targetView)
                .lineAnimDuration(400)
                .lineAndArcColor(getResources().getColor(R.color.primary_color))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .setListener(new SpotlightListener() {
                    @Override
                    public void onUserClicked(String s) {
                        prepareCoachMark(targetview);
                    }
                })
                .usageId(UUID.randomUUID().toString());//UNIQUE ID
        spBuilder.show();
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
        mSwipeStackAdapter.getItem(position).setStatus(0);
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
                viewHolder.tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
                viewHolder.tvSource = (TextView) convertView.findViewById(R.id.tvSource);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            viewHolder.tvSource.setText(suggestions.getSource());
            viewHolder.tvMessage.setText(getString(R.string.customer_has_sent_an_enquiry));
            return convertView;
        }

        private class ViewHolder {
            TextView tvSource, tvMessage;
        }

    }

    @Override
    protected void onDestroy() {
        sendBroadcast(new Intent(BubblesService.ACTION_RESET_BUBBLE));
        super.onDestroy();
    }
}
