package com.nowfloats.enablekeyboard;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boost.upgrades.UpgradeActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.util.Constants;
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
import static com.nowfloats.util.Key_Preferences.GET_FP_DETAILS_CATEGORY;

/**
 * Created by Admin on 02-03-2018.
 */

public class KeyboardFragment extends Fragment implements View.OnTouchListener, View.OnClickListener {

  private static final int STORAGE_CODE = 100, MICROPHONE_CODE = 101;
  private static final int INPUT_METHOD_SETTINGS = 102;
  SharedPreferences sharedPreferences;
  IntentFilter filter = new IntentFilter(Intent.ACTION_INPUT_METHOD_CHANGED);
  InputMethodChangeReceiver mReceiver;
  private Context mContext;
  private SwitchCompat microphoneSwitchTv, storageSwitchTv, keyboardSwitchTv;
  private InputMethodManager imeManager;
  private SwitchCompat keyboardSettingSwitchTv;
  private ImageView gifImageView;
  private RevealFrameLayout overLayout1;
  private RecyclerView rvKeyboardThemes;
  private KeyboardThemesAdapter keyboardThemesAdapter;
  private ScrollView mainLayout;
  private LinearLayout secondaryLayout;
  private TextView buyItemButton;
  private UserSessionManager session;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    sharedPreferences = requireContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
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

    session = new UserSessionManager(requireActivity().getApplicationContext(), requireActivity());

    //show or hide if feature is available to user
    mainLayout = view.findViewById(R.id.main_layout);
    secondaryLayout = view.findViewById(R.id.secondary_layout);
    buyItemButton = view.findViewById(R.id.buy_item);
    if ((Constants.StoreWidgets != null && Constants.StoreWidgets.contains("BOOSTKEYBOARD")) ||
        (Constants.currentActivePackageId != null && Constants.currentActivePackageId.contains("59ce2ae56431a80b009cb1fa"))) {
      mainLayout.setVisibility(View.VISIBLE);
      secondaryLayout.setVisibility(View.GONE);
    } else {
      mainLayout.setVisibility(View.GONE);
      secondaryLayout.setVisibility(View.VISIBLE);
    }
    buyItemButton.setOnClickListener(v -> initiateBuyFromMarketplace());

    MixPanelController.track(EventKeysWL.SIDE_PANEL_KEYBOARD, null);
    if (!isAdded() && isDetached()) return;
    view.findViewById(R.id.keyboard_info).setOnClickListener(this);
    overLayout1 = view.findViewById(R.id.enable_keyboard_rfl_overlay1);
    view.findViewById(R.id.ll_enable_keyboard).setOnClickListener(this);
    if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
      view.findViewById(R.id.cv_themes).setVisibility(View.GONE);
    } else {
      view.findViewById(R.id.cv_themes).setVisibility(View.VISIBLE);
      TextView tvBoostThemes = view.findViewById(R.id.tv_boost_themes);
      if (requireContext().getApplicationContext().getPackageName().equalsIgnoreCase("com.redtim")) {
        tvBoostThemes.setText("RedTim Keyboard Themes");
      } else {
        tvBoostThemes.setText("Boost Keyboard Themes");
      }
      rvKeyboardThemes = view.findViewById(R.id.rv_keyboard_themes);
      rvKeyboardThemes.setLayoutManager(new GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false));
      ArrayList<Integer> keyboardDrawables = new ArrayList<>();
      keyboardDrawables.add(R.drawable.ic_keyboard_theme_two);
      keyboardDrawables.add(R.drawable.ic_keyboard_theme_one);
      String selectedString = sharedPreferences.getString("keyboard_theme", KeyboardThemesAdapter.Themes.LXX_DARK.toString());
      int selected = 0;
      if (selectedString.equals(KeyboardThemesAdapter.Themes.LXX_DARK.toString())) {
        selected = 0;
      } else if (selectedString.equals(KeyboardThemesAdapter.Themes.LXX_DARK_UNBORDERED.toString())) {
        selected = 1;
      }
      keyboardThemesAdapter = new KeyboardThemesAdapter(requireContext(), keyboardDrawables, selected, sharedPreferences);
      rvKeyboardThemes.setAdapter(keyboardThemesAdapter);
    }
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
    if (mContext instanceof HomeActivity && HomeActivity.headerText != null)
      HomeActivity.headerText.setText(getString(R.string.boost_keyboard));

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
    Activity activity = requireActivity();
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
      Toast.makeText(activity, getString(R.string.to_change_permission_go_to_settings), Toast.LENGTH_SHORT).show();
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
        content = getString(R.string.we_need_permission_to_sharing) + getString(R.string.boost_keyboard);
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
        startActivity(new Intent(requireActivity(), BoostKeyboardActivity.class));
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

  private void initiateBuyFromMarketplace() {
    ProgressDialog progressDialog = new ProgressDialog(requireContext());
    String status = "Loading. Please wait...";
    progressDialog.setMessage(status);
    progressDialog.setCancelable(false);
    progressDialog.show();
    Intent intent = new Intent(requireActivity(), UpgradeActivity.class);
    intent.putExtra("expCode", session.getFP_AppExperienceCode());
    intent.putExtra("fpName", session.getFPName());
    intent.putExtra("fpid", session.getFPID());
    intent.putExtra("fpTag", session.getFpTag());
    intent.putExtra("accountType", session.getFPDetails(GET_FP_DETAILS_CATEGORY));
    intent.putStringArrayListExtra("userPurchsedWidgets", Constants.StoreWidgets);
    if (session.getFPEmail() != null) {
      intent.putExtra("email", session.getFPEmail());
    } else {
      intent.putExtra("email", "ria@nowfloats.com");
    }
    if (session.getFPPrimaryContactNumber() != null) {
      intent.putExtra("mobileNo", session.getFPPrimaryContactNumber());
    } else {
      intent.putExtra("mobileNo", "9160004303");
    }
    intent.putExtra("profileUrl", session.getFPLogo());
    intent.putExtra("buyItemKey", "BOOSTKEYBOARD");
    startActivity(intent);
    new Handler().postDelayed(() -> {
      progressDialog.dismiss();
    }, 1000);
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
