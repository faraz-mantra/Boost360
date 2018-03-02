package com.nowfloats.NavigationDrawer;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.List;

import nowfloats.nfkeyboard.keyboards.ImeKeyboardService;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Admin on 02-03-2018.
 */

public class KeyboardFragment extends Fragment implements View.OnClickListener {
    private static final int STORAGE_CODE = 100, MICROPHONE_CODE = 101;
    private static final int INPUT_METHOD_SETTINGS = 102;
    private Context mContext;
    private SwitchCompat microphoneSwitchTv, storageSwitchTv,keyboardSwitchTv;
    private InputMethodManager imeManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imeManager = (InputMethodManager)getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_keyboard,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MixPanelController.track(EventKeysWL.SIDE_PANEL_KEYBOARD, null);
        if (!isAdded() && isDetached()) return;
        storageSwitchTv = view.findViewById(R.id.storage_switch);
        microphoneSwitchTv = view.findViewById(R.id.microphone_switch);
        keyboardSwitchTv = view.findViewById(R.id.keyboard_switch);
        storageSwitchTv.setOnClickListener(this);
        keyboardSwitchTv.setOnClickListener(this);
        microphoneSwitchTv.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        storageSwitchTv.setChecked(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED);
        microphoneSwitchTv.setChecked(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED);
        keyboardSwitchTv.setChecked(isInputMethodEnabled());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INPUT_METHOD_SETTINGS){
            if (!isInputMethodEnabled()){
                if (imeManager != null)
                imeManager.showInputMethodPicker();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getPermission(int code){
        Activity activity = getActivity();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED && code == STORAGE_CODE){
            storageSwitchTv.setChecked(false);
            if (activity == null){
                return;
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                rationalPermissionDialog(STORAGE_CODE);
            }else{
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_CODE);
            }
        }else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED && code == MICROPHONE_CODE){
            microphoneSwitchTv.setChecked(false);
            if (activity == null){
                return;
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.RECORD_AUDIO)){
                rationalPermissionDialog(MICROPHONE_CODE);
            }else{
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.RECORD_AUDIO},MICROPHONE_CODE);
            }
        }else{
            if (code == MICROPHONE_CODE) {
              microphoneSwitchTv.setChecked(true);
            }else if(code == STORAGE_CODE) {
                storageSwitchTv.setChecked(true);
            }
            Toast.makeText(activity, "To change permission go to setting", Toast.LENGTH_SHORT).show();
        }
    }
    private void rationalPermissionDialog(final int code){
        String content = "", title = "";
        switch (code){
            case MICROPHONE_CODE:
                title = "Microphone Permission";
                content = "We need permission to enable voice input feature in Boost Keyboard";
                break;
            case STORAGE_CODE:
                title = "Storage Permission";
                content = "We need permission to enable sharing feature in Boost Keyboard";
                break;
        }
        new MaterialDialog.Builder(mContext)
                .content(content)
                .title(title)
                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.light_gray)
                .negativeText("Cancel")
                .positiveText("Take Me There")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .build().show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_CODE){
            storageSwitchTv.setChecked(grantResults[0]>0);
        }else if (requestCode == MICROPHONE_CODE){
            microphoneSwitchTv.setChecked(grantResults[0]>0);
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.keyboard_switch:
                MixPanelController.track(EventKeysWL.KEYBOARD_SWITCH_CLICKED,null);
                if(imeManager == null){

                } else if(!isInputMethodActivated(imeManager)){
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS),INPUT_METHOD_SETTINGS);
                } else {
                    imeManager.showInputMethodPicker();
                    keyboardSwitchTv.setChecked(isInputMethodEnabled());
                }
                break;
            case R.id.storage_switch:
                MixPanelController.track(EventKeysWL.STORAGE_SWITCH_CLICKED,null);
                getPermission(STORAGE_CODE);
                break;
            case R.id.microphone_switch:
                MixPanelController.track(EventKeysWL.MICROPHONE_SWITCH_CLICKED,null);
                getPermission(MICROPHONE_CODE);
                break;
        }
    }

    private boolean isInputMethodActivated(InputMethodManager manager) {
        List<InputMethodInfo> list = manager.getEnabledInputMethodList();
        ComponentName myInputMethod = new ComponentName(mContext, ImeKeyboardService.class);
        for (InputMethodInfo info: list){
            if(myInputMethod.equals(info.getComponent())){
                return true;
            }
        }
        return false;
    }

    public boolean isInputMethodEnabled() {
        String id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);

        ComponentName defaultInputMethod = ComponentName.unflattenFromString(id);

        ComponentName myInputMethod = new ComponentName(mContext, ImeKeyboardService.class);

        return myInputMethod.equals(defaultInputMethod);
    }
}
