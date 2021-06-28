package com.nowfloats.SiteAppearance;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 27-06-2017.
 */

public class ThemeSelectorFragment extends Fragment {
    public static final int[] imageIds = new int[]{R.drawable.theme_bnb, R.drawable.theme_fml, R.drawable.theme_ttf,
            R.drawable.theme_luxor, R.drawable.theme_alexandria, R.drawable.theme_cairo};
    String[] themeNames, themeMessages;
    String[] themeIds;
    int pos;
    private Context mContext;
    String currentThemeId = "";
    UserSessionManager manager;
    ProgressDialog dialog;
    TextView setLook;

    public static Fragment getInstance(int pos) {
        BoostLog.e("","d");
        Fragment frag = new ThemeSelectorFragment();
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            pos = b.getInt("pos", 0);
        }
        themeNames = getResources().getStringArray(R.array.themeNames);
        themeMessages = getResources().getStringArray(R.array.themeMessages);
        themeIds = getResources().getStringArray(R.array.themeIds);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        manager = new UserSessionManager(context, getActivity());
        currentThemeId = manager.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.adapter_item_theme_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded()) return;
        ImageView imageview = (ImageView) view.findViewById(R.id.img_theme);
        setLook = (TextView) view.findViewById(R.id.btn_set_look);
        setLook.setText(currentThemeId.equals(themeIds[pos]) ? "Current look" : "Set this look");

        if (setLook.getText().toString().equals("Current look")) {
            setCurrentThemeButtonBg();
        }
        setLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
                    Methods.showFeatureNotAvailDialog(getContext());
                } else if (setLook.getText().toString().equals("Current look")) {
                    Toast.makeText(mContext, getString(R.string.your_website_already_has_this_look), Toast.LENGTH_SHORT).show();
                } else {
                    showDialog();
                    setTheme();
                }
            }
        });

        ((TextView) view.findViewById(R.id.tv_theme_name)).setText(themeNames[pos]);
        ((TextView) view.findViewById(R.id.tv_theme_message)).setText(themeMessages[pos]);
        view.findViewById(R.id.tv_theme_description).setVisibility(View.GONE);
        Glide.with(this).load(imageIds[pos])
                .apply(new RequestOptions()
                        .placeholder(imageIds[1]))
                .into(imageview);
    }

    private void setCurrentThemeButtonBg() {
        setLook.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        setLook.setBackgroundResource(R.drawable.rounded_gray_padded);
    }

    private void setTheme() {
        UserSessionManager sessionManager = new UserSessionManager(getActivity(), getActivity());
        ThemeApis api = Constants.restAdapter.create(ThemeApis.class);
        api.setTheme(new JSONObject(), Constants.clientId, sessionManager.getFpTag(), themeIds[pos], new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                setCurrentLook(response.getStatus() == 200 ? s : null);
            }

            @Override
            public void failure(RetrofitError error) {
                setCurrentLook(null);
            }
        });
    }

    private void showDialog() {
        if (dialog == null) {
            dialog = ProgressDialog.show(mContext, "", getString(R.string.please_wait), true);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.hide();
        }
    }

    //    private void setDynamicTheme(){
//        ThemeApis api = Constants.restAdapter.create(ThemeApis.class);
//        api.setDynamicTheme(Constants.clientId, manager.getFpTag(), new Callback<String>() {
//            @Override
//            public void success(String s, Response response) {
//                setCurrentLook(response.getStatus() == 200? s:null);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                setCurrentLook(null);
//            }
//        });
//    }
    public void setCurrentLook(String s) {
        if (!isAdded()) return;
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(mContext, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        } else if (s.equals("true")) {
            setLook.setText("Current look");
            setCurrentThemeButtonBg();
            manager.storeFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID, themeIds[pos]);
            ((SiteAppearanceActivity) mContext).notifyDataSetChanged();
            Toast.makeText(mContext, getString(R.string.changed_theme_to) + themeNames[pos], Toast.LENGTH_SHORT).show();
        } else if (s.equals("false")) {
            Toast.makeText(mContext, getString(R.string.not_able_to_change_theme), Toast.LENGTH_SHORT).show();
        }
        hideDialog();
    }

}
