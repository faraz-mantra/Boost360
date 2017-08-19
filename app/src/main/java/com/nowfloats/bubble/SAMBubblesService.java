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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.nowfloats.sam.SAMCustomerListActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;


public class SAMBubblesService extends Service {
    private List<BubbleLayout> bubbles = new ArrayList<BubbleLayout>();
    private BubbleTrashLayout bubblesTrash;
    private WindowManager windowManager;
    private BubblesLayoutCoordinator layoutCoordinator;
    private SharedPreferences pref;


    private IntentFilter addIntentFilter = new IntentFilter(ACTION_ADD_BUBBLE);
    private IntentFilter removeIntentFilter = new IntentFilter(ACTION_REMOVE_BUBBLE);

    private float initAplha = 1.0f;

    public static final String ACTION_ADD_BUBBLE = "nowfloats.bubblebutton.bubble.ACTION_ADD_BUBBLE";
    public static final String ACTION_REMOVE_BUBBLE = "nowfloats.bubblebutton.bubble.ACTION_REMOVE_BUBBLE";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    BroadcastReceiver resetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getAction().equalsIgnoreCase(ACTION_ADD_BUBBLE)) {
                WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = window.getDefaultDisplay();
                int x_pos = 0;
                int y_Pos = (display.getHeight() * 20) / 100;

                if (bubbles == null || bubbles.size() == 0) {
                    addTrash(R.layout.bubble_trash_layout);
                    addBubble(x_pos, y_Pos);
                }
            } else if (intent.getAction().equalsIgnoreCase(ACTION_REMOVE_BUBBLE)) {
                if (bubbleView != null)
                    recycleBubble(bubbleView);
            }
        }

    };

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

    private BubbleLayout bubbleView;

    public void addBubble(final int x, final int y) {

        try {
            bubbleView = new BubbleLayout(this);
            bubbleView.addView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.sam_bubble_layout, null));
            bubbleView.initalizeBubbleView(initAplha);
            bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
                @Override
                public void onBubbleRemoved(BubbleLayout bubble) {
                }
            });
            bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {

                @Override
                public void onBubbleClick(BubbleLayout bubble) {
                    Intent intent = new Intent(SAMBubblesService.this, SAMCustomerListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            });

            bubbleView.setShouldStickToWall(true);

            WindowManager.LayoutParams layoutParams = buildLayoutParamsForBubble(x, y);
            bubbleView.setWindowManager(getWindowManager());
            bubbleView.setViewParams(layoutParams);
            bubbleView.setLayoutCoordinator(layoutCoordinator);
            bubbleView.setAnimationListener(false);
            bubbles.add(bubbleView);
            new ShakeAnimation(bubbleView).animate();
            addViewToWindow(bubbleView);
            showCustomToastView();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        if (intent == null) {
            return Service.START_NOT_STICKY;
        } else {

            return Service.START_REDELIVER_INTENT;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(resetReceiver, addIntentFilter);
        registerReceiver(resetReceiver, removeIntentFilter);
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

    private void showCustomToastView() {

        Toast mToast = new Toast(SAMBubblesService.this);
        mToast.setView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.sam_toast, null));
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.TOP, -20, (getResources().getDisplayMetrics().heightPixels * 5) / 100);
        mToast.show();

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
        MixPanelController.track(MixPanelController.SAM_BUBBLE_CLOSED, null);
        stopSelf();
    }

}