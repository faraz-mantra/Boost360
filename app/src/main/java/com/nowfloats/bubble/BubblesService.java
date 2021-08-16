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
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.nowfloats.accessbility.TempDisplayDialog;
import com.nowfloats.accessbility.WhatsAppBubbleCloseDialog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class BubblesService extends Service {
    public static final String ACTION_KILL_DIALOG = "nowfloats.bubblebutton.bubble.ACTION_KILL_DIALOG";
    public static final String ACTION_RESET_BUBBLE = "nowfloats.bubblebutton.bubble.ACTION_RESET_BUBBLE";
    public static final String ACTION_GO_TO_RIGHT_WALL = "nowfloats.bubblebutton.bubble.ACTION_GO_TO_RIGHT_WALL";
    public static final String ACTION_GO_TO_RIGHT_WALL_CARDS = "nowfloats.bubblebutton.bubble.ACTION_GO_TO_RIGHT_WALL_CARDS";
    private List<BubbleLayout> bubbles = new ArrayList<BubbleLayout>();
    private BubbleTrashLayout bubblesTrash;
    private WindowManager windowManager;
    private BubblesLayoutCoordinator layoutCoordinator;
    private FROM from;
    private SharedPreferences pref;
    private IntentFilter resertIntentFilters = new IntentFilter(ACTION_RESET_BUBBLE);
    private IntentFilter moveRightIntentFilters = new IntentFilter(ACTION_GO_TO_RIGHT_WALL);
    private IntentFilter moveSpecificIntentFilters = new IntentFilter(ACTION_GO_TO_RIGHT_WALL_CARDS);
    private float initAplha = 0.5f;
    private BubbleLayout bubbleView;
    BroadcastReceiver resetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (bubbleView != null) {

                if (intent.getAction().equalsIgnoreCase(ACTION_RESET_BUBBLE))
                    bubbleView.applyAlpha();
                else if (intent.getAction().equalsIgnoreCase(ACTION_GO_TO_RIGHT_WALL))
                    bubbleView.goToRightWall();
                else if (intent.getAction().equalsIgnoreCase(ACTION_GO_TO_RIGHT_WALL_CARDS))
                    bubbleView.goToRightWallForCards();
            }
        }

    };
    private String BUBBLE_CLASS_NAME = "com.nowfloats.accessbility.BubbleDialog";
    private String BUBBLE_V2_CLASS_NAME = "com.nowfloats.swipecard.SuggestionsActivity";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        for (BubbleLayout bubble : bubbles) {
            recycleBubble(bubble);
        }
        unregisterReceiver(resetReceiver);
        super.onDestroy();
    }

    private void recycleBubble(final BubbleLayout bubble) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getWindowManager().removeView(bubble);
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
                        Intent intent = new Intent(BubblesService.this, TempDisplayDialog.class).
                                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra(Key_Preferences.DIALOG_FROM, from);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        if (intent == null) {
            return Service.START_NOT_STICKY;
        } else {

            Bundle bundle = intent.getExtras();
            int y_Pos = 0, x_pos = 20;
            if (bundle != null) {
                y_Pos = bundle.getInt(Key_Preferences.BUBBLE_POS_Y, 130);

                if (bundle.containsKey(Key_Preferences.DIALOG_FROM)) {
                    from = (FROM) bundle.get(Key_Preferences.DIALOG_FROM);
                } else {
                    from = FROM.WHATSAPP;
                }


                if (bundle.containsKey(Key_Preferences.BUBBLE_POS_X)) {
                    x_pos = bundle.getInt(Key_Preferences.BUBBLE_POS_X);
                }
            } else {
                from = FROM.WHATSAPP;
            }

            if (from == FROM.WHATSAPP
                    || from == FROM.LAUNCHER_HOME_ACTIVITY) {

                WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = window.getDefaultDisplay();
                x_pos = display.getWidth();
                y_Pos = (display.getHeight() * 20) / 100;
            } else {
                initAplha = 1.0f;
            }

            if (bubbles == null || bubbles.size() == 0) {
                addTrash(R.layout.bubble_trash_layout);
                addBubble(x_pos, y_Pos);
            }

            return Service.START_REDELIVER_INTENT;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(resetReceiver, resertIntentFilters);
        registerReceiver(resetReceiver, moveRightIntentFilters);
        registerReceiver(resetReceiver, moveSpecificIntentFilters);

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

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = x;
        params.y = y;
        return params;
    }

    private WindowManager.LayoutParams buildLayoutParamsForTrash() {
        int x = 0;
        int y = 0;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);

        params.x = x;
        params.y = y;
        return params;
    }

    public void removeBubble(BubbleLayout bubble) {
        MixPanelController.track(MixPanelController.BUBBLE_CLOSED, null);
        pref.edit().putLong(Key_Preferences.SHOW_BUBBLE_TIME, Calendar.getInstance().getTimeInMillis()).apply();
        if (from == FROM.WHATSAPP && !pref.getBoolean(Key_Preferences.SHOW_WHATSAPP_CLOSE_DIALOG, false)) {
            pref.edit().putBoolean(Key_Preferences.SHOW_WHATSAPP_CLOSE_DIALOG, true).apply();
            Intent intent = new Intent(BubblesService.this, WhatsAppBubbleCloseDialog.class).
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);

            startActivity(intent);
        }
        stopSelf();
    }

    //test
    public enum FROM {
        HOME_ACTIVITY,
        WHATSAPP,
        WHATSAPP_DIALOG,
        LAUNCHER_HOME_ACTIVITY,
        CALLER_INFO_ACTIVITY
    }
}