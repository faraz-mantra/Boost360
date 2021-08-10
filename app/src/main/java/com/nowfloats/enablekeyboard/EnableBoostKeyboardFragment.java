package com.nowfloats.enablekeyboard;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.Volley.AppController;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.separ.neural.inputmethod.indic.LatinIME;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class EnableBoostKeyboardFragment extends Fragment implements View.OnTouchListener {
    private static final int STORAGE_CODE = 100, MICROPHONE_CODE = 101;
    private static final int INPUT_METHOD_SETTINGS = 102;
    IntentFilter filter = new IntentFilter(Intent.ACTION_INPUT_METHOD_CHANGED);
    private SwitchCompat microphoneSwitchTv, storageSwitchTv, keyboardSwitchTv, keyboardSettingSwitchTv;
    private Context mContext;
    private InputMethodManager imeManager;
    private InputMethodChangeReceiver mReceiver;
    private TextView titleTextView;
    private String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enable_boost_keyboard, container, false);
        storageSwitchTv = view.findViewById(R.id.storage_switch);
        microphoneSwitchTv = view.findViewById(R.id.microphone_switch);
        keyboardSettingSwitchTv = view.findViewById(R.id.keyboard_setting_switch);
        keyboardSwitchTv = view.findViewById(R.id.keyboard_switch);
        titleTextView = HomeActivity.headerText;

        keyboardSettingSwitchTv.setOnTouchListener(this);
        storageSwitchTv.setOnTouchListener(this);
        keyboardSwitchTv.setOnTouchListener(this);
        microphoneSwitchTv.setOnTouchListener(this);


        keyboardSettingSwitchTv.setText(String.format("Enable %s keyboard", AppController.getApplicationName(getActivity()).toLowerCase(Locale.ROOT)));
        keyboardSwitchTv.setText(String.format("Select %s keyboard", AppController.getApplicationName(getActivity()).toLowerCase(Locale.ROOT)));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        imeManager = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        mReceiver = new InputMethodChangeReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (titleTextView != null) titleTextView.setText(getString(R.string.enable_keyboard));
        storageSwitchTv.setChecked(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED);
        microphoneSwitchTv.setChecked(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED);
        keyboardSwitchTv.setChecked(isInputMethodEnabled());
        keyboardSettingSwitchTv.setChecked(isInputMethodActivated());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INPUT_METHOD_SETTINGS) {
            if (isInputMethodActivated()) {
                keyboardSettingSwitchTv.setChecked(true);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                    if (imeManager != null && isInputMethodActivated()) {
                        imeManager.showInputMethodPicker();
                        keyboardSwitchTv.setChecked(isInputMethodEnabled());
                    }
                    break;
                case R.id.storage_switch:
                    MixPanelController.track(EventKeysWL.ALLOW_ACCESS_SWITCH_CLICKED, null);
                    getPermissions();
                   /* if (!storageSwitchTv.isChecked()) {
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
                    }*/
                    break;
            }
        }

        return true;
    }

    private void getPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                requestPermissions(permission, STORAGE_CODE);

            }
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
                title = getString(R.string.microphone_permission);
                content = getString(R.string.we_need_permission_to_enable_voice_input_feature_in) + getString(R.string.boost_keyboard);
                break;
            case STORAGE_CODE:
                title = getString(R.string.storage_permission);
                content = getString(R.string.we_need_permission_to_enable_sharing_feature) + getString(R.string.boost_keyboard);
                break;
        }
        Methods.showApplicationPermissions(title, content, mContext);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        /*if (requestCode == STORAGE_CODE) {
            storageSwitchTv.setChecked(grantResults[0] > 0);
        } else if (requestCode == MICROPHONE_CODE) {
            microphoneSwitchTv.setChecked(grantResults[0] > 0);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }*/
        if (requestCode == STORAGE_CODE) {

            List<Integer> intList = new ArrayList<>();
            for (int i : grantResults) {
                intList.add(i);
            }
            if (!intList.contains(PackageManager.PERMISSION_DENIED)) {
                storageSwitchTv.setChecked(true);
            } else {
                storageSwitchTv.setChecked(false);
            }
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
