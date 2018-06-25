package com.nowfloats.enablekeyboard;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;
import io.separ.neural.inputmethod.indic.LatinIME;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.nowfloats.NavigationDrawer.HomeActivity.headerText;

/**
 * Created by Admin on 02-03-2018.
 */

public class KeyboardFragment extends Fragment implements View.OnTouchListener, View.OnClickListener {
    private static final int STORAGE_CODE = 100, MICROPHONE_CODE = 101;
    private static final int INPUT_METHOD_SETTINGS = 102;
    private Context mContext;
    private SwitchCompat microphoneSwitchTv, storageSwitchTv, keyboardSwitchTv;
    private InputMethodManager imeManager;
    IntentFilter filter = new IntentFilter(Intent.ACTION_INPUT_METHOD_CHANGED);
    InputMethodChangeReceiver mReceiver;
    private SwitchCompat keyboardSettingSwitchTv;
    private ImageView gifImageView;
    private RevealFrameLayout overLayout1;
    private RecyclerView rvKeyboardThemes;
    private KeyboardThemesAdapter keyboardThemesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_keyboard, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        imeManager = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        mReceiver = new InputMethodChangeReceiver();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MixPanelController.track(EventKeysWL.SIDE_PANEL_KEYBOARD, null);
        if (!isAdded() && isDetached()) return;
        view.findViewById(R.id.keyboard_info).setOnClickListener(this);
        overLayout1 = view.findViewById(R.id.enable_keyboard_rfl_overlay1);
        view.findViewById(R.id.ll_enable_keyboard).setOnClickListener(this);
        rvKeyboardThemes = view.findViewById(R.id.rv_keyboard_themes);
        rvKeyboardThemes.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        ArrayList<Integer> keyboardDrawables = new ArrayList<>();
        keyboardDrawables.add(R.drawable.ic_keyboard);
        keyboardDrawables.add(R.drawable.ic_keyboard);
        keyboardThemesAdapter = new KeyboardThemesAdapter(getContext(), keyboardDrawables, 0);
        rvKeyboardThemes.setAdapter(keyboardThemesAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (headerText != null && mContext instanceof HomeActivity)
            headerText.setText(getString(R.string.boost_keyboard));

       /* storageSwitchTv.setChecked(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED);
        microphoneSwitchTv.setChecked(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED);
        keyboardSwitchTv.setChecked(isInputMethodEnabled());
        keyboardSettingSwitchTv.setChecked(isInputMethodActivated());*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INPUT_METHOD_SETTINGS) {
            if (isInputMethodActivated()) {
                keyboardSettingSwitchTv.setChecked(true);
            }
           /* if (isInputMethodActivated()) {
                MixPanelController.track(MixPanelController.KEYBOARD_ENABLED, null);
                if (imeManager != null)
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imeManager.showInputMethodPicker();
                        }
                    }, 1000);
            }*/
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getPermission(int code) {
        Activity activity = getActivity();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED && code == STORAGE_CODE) {
            storageSwitchTv.setChecked(false);
            if (activity == null) {
                return;
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                rationalPermissionDialog(STORAGE_CODE);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_CODE);
            }
        } else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED && code == MICROPHONE_CODE) {
            microphoneSwitchTv.setChecked(false);
            if (activity == null) {
                return;
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
                rationalPermissionDialog(MICROPHONE_CODE);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, MICROPHONE_CODE);
            }
        } else {
            if (code == MICROPHONE_CODE) {
                microphoneSwitchTv.setChecked(true);
            } else if (code == STORAGE_CODE) {
                storageSwitchTv.setChecked(true);
            }
            Toast.makeText(activity, "To change permission go to setting", Toast.LENGTH_SHORT).show();
        }
    }

    private void rationalPermissionDialog(final int code) {
        String content = "", title = "";
        switch (code) {
            case MICROPHONE_CODE:
                title = "Microphone Permission";
                content = "We need permission to enable voice input feature in " + getString(R.string.boost_keyboard);
                break;
            case STORAGE_CODE:
                title = "Storage Permission";
                content = "We need permission to enable sharing feature in " + getString(R.string.boost_keyboard);
                break;
        }
        Methods.showApplicationPermissions(title, content, mContext);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_CODE) {
            storageSwitchTv.setChecked(grantResults[0] > 0);
        } else if (requestCode == MICROPHONE_CODE) {
            microphoneSwitchTv.setChecked(grantResults[0] > 0);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean isInputMethodActivated() {
        List<InputMethodInfo> list = imeManager.getEnabledInputMethodList();
        ComponentName myInputMethod = new ComponentName(mContext, LatinIME.class);
        for (InputMethodInfo info : list) {
            if (myInputMethod.equals(info.getComponent())) {
                return true;
            }
        }
        return false;
    }

    public boolean isInputMethodEnabled() {
        String id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);

        ComponentName defaultInputMethod = ComponentName.unflattenFromString(id);

        ComponentName myInputMethod = new ComponentName(mContext, LatinIME.class);

        return myInputMethod.equals(defaultInputMethod);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.keyboard_setting_switch:
                    if (!isInputMethodActivated()) {
                        startActivityForResult(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), INPUT_METHOD_SETTINGS);
                    }
                    break;
                case R.id.keyboard_switch:
                    MixPanelController.track(EventKeysWL.KEYBOARD_SWITCH_CLICKED, null);
                    if (imeManager != null && isInputMethodActivated() && !isInputMethodEnabled()) {
                        imeManager.showInputMethodPicker();
                        keyboardSwitchTv.setChecked(isInputMethodEnabled());
                    }
                    break;
                case R.id.storage_switch:
                    if (!storageSwitchTv.isChecked()) {
                        if (isInputMethodActivated() && isInputMethodEnabled()) {
                            MixPanelController.track(EventKeysWL.STORAGE_SWITCH_CLICKED, null);
                            getPermission(STORAGE_CODE);
                        }
                    } else {
                        MixPanelController.track(EventKeysWL.STORAGE_SWITCH_CLICKED, null);
                        getPermission(STORAGE_CODE);
                    }
                    break;
                case R.id.microphone_switch: {
                    if (!microphoneSwitchTv.isChecked()) {
                        if (isInputMethodActivated() && isInputMethodEnabled()) {
                            MixPanelController.track(EventKeysWL.MICROPHONE_SWITCH_CLICKED, null);
                            getPermission(MICROPHONE_CODE);
                        }
                    } else {
                        MixPanelController.track(EventKeysWL.STORAGE_SWITCH_CLICKED, null);
                        getPermission(STORAGE_CODE);
                    }
                    break;
                }
            }
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.keyboard_info:
                MixPanelController.track(EventKeysWL.MERCHANT_EDUCATION_BOOST_KEYBOARD, null);
                showOverlay(overLayout1, getString(R.string.boost_keyboard), getString(R.string.keyboard_message));
                break;
            case R.id.ll_enable_keyboard:
                startActivity(new Intent(getActivity(), BoostKeyboardActivity.class));
                break;
        }
    }

    private void showOverlay(final RevealFrameLayout overLayout, String title, String msg) {
        RelativeLayout revealLayout = overLayout.findViewById(R.id.ll_reveal_layout);
        revealLayout.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeOverlay(overLayout);
            }
        });
        ((TextView) revealLayout.findViewById(R.id.tvInfoTitle)).setText(title);
        ((TextView) revealLayout.findViewById(R.id.tvInfo)).setText(msg);

        int cx = (revealLayout.getLeft() + revealLayout.getRight());
        int cy = revealLayout.getTop();
        int radius = Math.max(revealLayout.getWidth(), revealLayout.getHeight());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Animator animator =
                    ViewAnimationUtils.createCircularReveal(revealLayout, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(700);

            revealLayout.setVisibility(View.VISIBLE);
            animator.start();
        } else {
            Animator anim = android.view.ViewAnimationUtils.
                    createCircularReveal(revealLayout, cx, cy, 0, radius);
            revealLayout.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    private void closeOverlay(final RevealFrameLayout overLayout) {

        final RelativeLayout revealLayout = overLayout.findViewById(R.id.ll_reveal_layout);
        int cx = (revealLayout.getLeft() + revealLayout.getRight());
        int cy = revealLayout.getTop();
        int radius = Math.max(revealLayout.getWidth(), revealLayout.getHeight());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            Animator anim = ViewAnimationUtils.
                    createCircularReveal(revealLayout, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    revealLayout.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();

        } else {
            Animator anim = android.view.ViewAnimationUtils.
                    createCircularReveal(revealLayout, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    revealLayout.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();

        }

    }

    public class InputMethodChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(Intent.ACTION_INPUT_METHOD_CHANGED)) {
                if (isInputMethodEnabled()) {
                    MixPanelController.track(MixPanelController.KEYBOARD_ACTIVATED, null);
                }
                keyboardSwitchTv.setChecked(isInputMethodEnabled());

                /* You can check the package name of current IME here.*/
            }
        }
    }
}
