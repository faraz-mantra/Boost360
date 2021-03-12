/*
 * Copyright Txus Ballesteros 2015 (@txusballesteros)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Txus Ballesteros <txus.ballesteros@gmail.com>
 */
package com.nowfloats.bubble;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.nowfloats.managenotification.CallerInfoDialog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.nowfloats.util.Constants.PREF_NOTI_CALL_LOGS;
import static com.nowfloats.util.Constants.PREF_NOTI_ENQUIRIES;
import static com.nowfloats.util.Constants.PREF_NOTI_ORDERS;


public class CustomerAssistantService extends Service {
    private List<BubbleLayout> bubbles = new ArrayList<BubbleLayout>();
    private BubbleTrashLayout bubblesTrash;
    private WindowManager windowManager;
    private BubblesLayoutCoordinator layoutCoordinator;
    private SharedPreferences pref;

    private IntentFilter addIntentFilter = new IntentFilter(ACTION_ADD_BUBBLE);
    private IntentFilter removeIntentFilter = new IntentFilter(ACTION_REMOVE_BUBBLE);
    private IntentFilter resertIntentFilters = new IntentFilter(ACTION_RESET_BUBBLE);
    private IntentFilter moveRightIntentFilters = new IntentFilter(ACTION_GO_TO_RIGHT_WALL);
    private IntentFilter moveSpecificIntentFilters = new IntentFilter(ACTION_GO_TO_RIGHT_WALL_CARDS);

    private float initAplha = 0.7f;

    public static final String ACTION_KILL_DIALOG = "nowfloats.bubblebutton.bubble.ACTION_KILL_DIALOG";
    public static final String ACTION_RESET_BUBBLE = "nowfloats.bubblebutton.bubble.ACTION_RESET_BUBBLE";
    public static final String ACTION_GO_TO_RIGHT_WALL = "nowfloats.bubblebutton.bubble.ACTION_GO_TO_RIGHT_WALL";
    public static final String ACTION_GO_TO_RIGHT_WALL_CARDS = "nowfloats.bubblebutton.bubble.ACTION_GO_TO_RIGHT_WALL_CARDS";

    public static final String ACTION_ADD_BUBBLE = "nowfloats.bubblebutton.bubble.ACTION_ADD_BUBBLE";
    public static final String ACTION_REMOVE_BUBBLE = "nowfloats.bubblebutton.bubble.ACTION_REMOVE_BUBBLE";
    public static final String ACTION_REFRESH_DIALOG = "nowfloats.bubblebutton.bubble.ACTION_REFRESH_DIALOG";

    private PowerManager.WakeLock cpuWakeLock = null;

    private final static int FOREGROUND_ID = 999;

    BroadcastReceiver resetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(ACTION_ADD_BUBBLE)) {
                repositionBubble();
                if (bubbleView != null)
                    bubbleView.applyAlpha();
            } else if (bubbleView != null) {
                if (intent.getAction().equalsIgnoreCase(ACTION_REMOVE_BUBBLE)) {
                    Log.d("here", "remove bubble");
                    recycleBubble(bubbleView);
                } else if (intent.getAction().equalsIgnoreCase(ACTION_RESET_BUBBLE))
                    bubbleView.applyAlpha();
                else if (intent.getAction().equalsIgnoreCase(ACTION_GO_TO_RIGHT_WALL))
                    bubbleView.goToRightWall();
                else if (intent.getAction().equalsIgnoreCase(ACTION_GO_TO_RIGHT_WALL_CARDS))
                    bubbleView.goToRightWallForCards();
            }
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (pref == null)
            pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        if (bubbles != null && bubbles.size() > 0) {
            for (BubbleLayout bubble : bubbles) {
                recycleBubble(bubble);
            }
        }
        bubblesTrash = null;
        if (cpuWakeLock != null)
            cpuWakeLock.release();
        unregisterReceiver(resetReceiver);
        Log.e("onDestroy", "onDestroy sam");
        super.onDestroy();
    }

    private void recycleBubble(final BubbleLayout bubble) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getWindowManager().removeView(bubble);
                bubbleView = null;
                for (BubbleLayout cachedBubble : bubbles) {
                    if (cachedBubble == bubble) {
                        bubble.notifyBubbleRemoved();
                        bubbles.remove(cachedBubble);
                        break;
                    }
                }
            }
        });
    }

    private WindowManager getWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }
        return windowManager;
    }

    private BubbleLayout bubbleView;

    public void addBubble(final int x, final int y) {

        try {
            bubbleView = new BubbleLayout(this, BubbleLayout.BUBBLE_TYPE.WHATSAPP_BUBBLE);
            bubbleView.addView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.bubble_layout, null));
            bubbleView.initalizeBubbleView(initAplha);
            bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
                @Override
                public void onBubbleRemoved(BubbleLayout bubble) {
                }
            });
            bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {

                @Override
                public void onBubbleClick(BubbleLayout bubble) {
                    Log.v("ggg", "bubble clicked");
                    if (isDialogShowing()) {
                        killDialog();
                    } else {
                        killDialog();
                        bubbleView.resetAlpha();
                        bubble.goToRightWall();
                        Intent intent = new Intent(CustomerAssistantService.this, CallerInfoDialog.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                }
            });

            bubbleView.setShouldStickToWall(true);

            WindowManager.LayoutParams layoutParams = buildLayoutParamsForBubble(x, y);
            bubbleView.setWindowManager(getWindowManager());
            bubbleView.setViewParams(layoutParams);
            bubbleView.setLayoutCoordinator(layoutCoordinator);
            bubbles.add(bubbleView);
            addViewToWindow(bubbleView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String BUBBLE_CLASS_NAME = "com.nowfloats.managenotification.CallerInfoDialog";
    private String BUBBLE_V2_CLASS_NAME = "com.nowfloats.swipecard.SuggestionsActivity";

    private boolean isDialogShowing() {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if (taskInfo == null || taskInfo.isEmpty()) {
            return false;
        }
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        componentInfo.getPackageName();
        //Log.v("gggg",componentInfo.getClassName());
        return (componentInfo.getClassName().equalsIgnoreCase(BUBBLE_CLASS_NAME)
                || componentInfo.getClassName().equalsIgnoreCase(BUBBLE_V2_CLASS_NAME));
    }

    private boolean shouldOpen = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        MixPanelController.track(MixPanelController.BUBBLE_SERVICE_ENABLED, null);
        PendingIntent pendingIntent = createPendingIntent();
        Notification notification = createNotification(pendingIntent);
        startForeground(FOREGROUND_ID, notification);


        if (intent == null) {
            repositionBubble();
            return Service.START_STICKY;
        } else {
            if (intent.getExtras() != null) {
                shouldOpen = intent.getExtras().getBoolean("shouldOpen", false);
            }
            repositionBubble();
            return Service.START_REDELIVER_INTENT;
        }


    }


    private void repositionBubble() {

        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);


        int y_Pos = 0, x_pos = 20;
        Display display = window.getDefaultDisplay();
        x_pos = display.getWidth();
        y_Pos = (display.getHeight() * 20) / 100;

        initAplha = 0.7f;

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am.getRunningTasks(1) != null && am.getRunningTasks(1).size() > 0) {
            ComponentName componentName = am.getRunningTasks(1).get(0).topActivity;
            if (!componentName.getPackageName().equalsIgnoreCase(getApplicationContext().getPackageName())) {
                if (Methods.hasOverlayPerm(CustomerAssistantService.this) && pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS, false)) {
                    if (bubbles == null || bubbles.size() == 0) {
                        addTrash(R.layout.bubble_trash_layout);
                        addBubble(x_pos, y_Pos);
                    }

                    if (shouldOpen && bubbleView != null) {
                        shouldOpen = false;
                        if (!isDialogShowing()) {
                            Intent intent = new Intent(CustomerAssistantService.this, CallerInfoDialog.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);
                        }
                    }
                }
            }

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(resetReceiver, resertIntentFilters);
        registerReceiver(resetReceiver, addIntentFilter);
        registerReceiver(resetReceiver, removeIntentFilter);
        registerReceiver(resetReceiver, moveRightIntentFilters);
        registerReceiver(resetReceiver, moveSpecificIntentFilters);

        if ((cpuWakeLock != null) && (cpuWakeLock.isHeld() == false)) {
            PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            cpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ShakeEventService onCreate Tag");
            cpuWakeLock.acquire();
        }

    }

    private PendingIntent createPendingIntent() {
        try {
            Intent intent = new Intent(this, Class.forName("com.dashboard.controller.DashboardActivity"));
            return PendingIntent.getActivity(this, 0, intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void killDialog() {
        sendBroadcast(new Intent(ACTION_KILL_DIALOG));
    }


    void addTrash(int trashLayoutResourceId) {
        if (trashLayoutResourceId != 0) {
            bubblesTrash = new BubbleTrashLayout(this);
            bubblesTrash.setWindowManager(windowManager);
            bubblesTrash.setViewParams(buildLayoutParamsForTrash());
            bubblesTrash.setVisibility(View.GONE);
            LayoutInflater.from(this).inflate(trashLayoutResourceId, bubblesTrash, true);
            addViewToWindow(bubblesTrash);
            initializeLayoutCoordinator();
        }
    }

    private void initializeLayoutCoordinator() {
        layoutCoordinator = new BubblesLayoutCoordinator.Builder(this)
                .setWindowManager(getWindowManager())
                .setTrashView(bubblesTrash)
                .build();
    }

    private void addViewToWindow(final BubbleBaseLayout view) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getWindowManager().addView(view, view.getViewParams());
            }
        });
    }

    private void addDialogViewToWindow(final View view) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getWindowManager().addView(view, view.getLayoutParams());
            }
        });
    }

    private WindowManager.LayoutParams buildLayoutParamsForBubble(int x, int y) {
        // WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,


        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);

        }

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = x;
        params.y = y;
        return params;

    }

    private WindowManager.LayoutParams buildLayoutParamsForTrash() {
        int x = 0;
        int y = 0;
        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
        }


        params.x = x;
        params.y = y;
        return params;
    }

    public void removeBubble(BubbleLayout bubble) {
        MixPanelController.track(MixPanelController.BUBBLE_CLOSED, null);
        pref.edit().putBoolean(Key_Preferences.HAS_SUGGESTIONS, false).commit();
        pref.edit().putString(PREF_NOTI_CALL_LOGS, "").commit();
        pref.edit().putString(PREF_NOTI_ENQUIRIES, "").commit();
        pref.edit().putString(PREF_NOTI_ORDERS, "").commit();
        pref.edit().putLong(Key_Preferences.SHOW_BUBBLE_TIME, Calendar.getInstance().getTimeInMillis()).apply();
        stopSelf();
    }


    private Notification createNotification(PendingIntent intent) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "0001")
                .setSmallIcon(R.drawable.app_launcher2)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_launcher))
                .setContentText(getString(R.string.you_have_new_notifications))
                .setContentTitle(getString(R.string.app_name))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setColor(ContextCompat.getColor(this, R.color.primaryColor))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.you_have_new_notifications)))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        return notificationBuilder.build();
    }
}