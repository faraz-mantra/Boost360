package com.nowfloats.SiteAppearance;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 27-06-2017.
 */

public class ThemeSelectorFragment extends Fragment{
    int[] imageIds = new int[]{R.drawable.theme_dynamic,R.drawable.theme_bnb,R.drawable.theme_fml,R.drawable.theme_ttf};
    String[] themeNames,themeMessages;
    String[] themeIds;
    int pos;
    private Context mContext;
    String currentThemeId ="";
    UserSessionManager manager;
    ProgressDialog dialog;
    TextView setLook;

    public static Fragment getInstanse(int pos){
        Fragment frag = new ThemeSelectorFragment();
        Bundle b = new Bundle();
        b.putInt("pos",pos);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null){
            pos = b.getInt("pos",0);
        }
        themeNames = getResources().getStringArray(R.array.themeNames);
        themeMessages = getResources().getStringArray(R.array.themeMessages);
        themeIds = getResources().getStringArray(R.array.themeIds);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        manager = new UserSessionManager(context,getActivity());
        currentThemeId =  manager.getFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.adapter_item_theme_picker,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!isAdded()) return;
        ImageView imageview = (ImageView) view.findViewById(R.id.img_theme);
        setLook = (TextView) view.findViewById(R.id.btn_set_look);
        if(pos == 0){
            setLook.setText(TextUtils.isEmpty(currentThemeId)? "Current look":"Set this look");
        }else {
            setLook.setText(currentThemeId.equals(themeIds[pos-1])? "Current look":"Set this look");
        }
        if(setLook.getText().toString().equals("Current look")){
            setCurrentThemeButtonBg();
        }
        setLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setLook.getText().toString().equals("Current look")){
                    Toast.makeText(mContext, "Your website already has this look", Toast.LENGTH_SHORT).show();
                }else {
                    showDialog();
                    if (pos == 0) {
                        setDynamicTheme();
                    } else {
                        setTheme();
                    }
                }
            }
        });

        ((TextView)view.findViewById(R.id.tv_theme_name)).setText(themeNames[pos]);
        ((TextView)view.findViewById(R.id.tv_theme_message)).setText(themeMessages[pos]);
        view.findViewById(R.id.tv_theme_description).setVisibility(pos == 0 ?View.VISIBLE:View.GONE);
        Glide.with(this).load(imageIds[pos])
                .apply(new RequestOptions()
                        .placeholder(imageIds[1]))
                .into(imageview);
    }

    private void setCurrentThemeButtonBg(){
        setLook.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        setLook.setBackgroundResource(R.drawable.rounded_gray_padded);
    }
    private void setTheme(){
        UserSessionManager sessionManager = new UserSessionManager(getActivity(),getActivity());
        ThemeApis api = Constants.restAdapter.create(ThemeApis.class);
        api.setTheme(Constants.clientId, sessionManager.getFpTag(), themeIds[pos-1], new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                setCurrentLook(response.getStatus() == 200? s:null);
            }

            @Override
            public void failure(RetrofitError error) {
               setCurrentLook(null);
            }
        });
    }

    private void showDialog(){
        if(dialog == null){
            dialog = ProgressDialog.show(mContext,"",getString(R.string.please_wait),true);
            dialog.setCanceledOnTouchOutside(false);
        }
    }
    private void hideDialog(){
        if(dialog != null && dialog.isShowing()){
            dialog.hide();
        }
    }
    private void setDynamicTheme(){
        ThemeApis api = Constants.restAdapter.create(ThemeApis.class);
        api.setDynamicTheme(Constants.clientId, manager.getFpTag(), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                setCurrentLook(response.getStatus() == 200? s:null);
            }

            @Override
            public void failure(RetrofitError error) {
                setCurrentLook(null);
            }
        });
    }
    public void setCurrentLook(String s){
        if(!isAdded()) return;
        if(TextUtils.isEmpty(s)){
            Toast.makeText(mContext, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }else if(s.equals("true")){
            setLook.setText("Current look");
            setCurrentThemeButtonBg();
            manager.storeFPDetails(Key_Preferences.GET_FP_WEBTEMPLATE_ID,pos == 0? "":themeIds[pos-1]);
            ((SiteAppearanceActivity)mContext).notifyDataSetChanged();
            Toast.makeText(mContext, "Changed theme to "+themeNames[pos], Toast.LENGTH_SHORT).show();
        }else if(s.equals("false")){
            Toast.makeText(mContext, "Not able to change theme", Toast.LENGTH_SHORT).show();
        }
        hideDialog();
    }
    
}
