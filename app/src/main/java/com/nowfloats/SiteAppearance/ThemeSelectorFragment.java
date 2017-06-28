package com.nowfloats.SiteAppearance;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
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
        view.findViewById(R.id.btn_set_look).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos == 0){
                    setDynamicTheme();
                }else{
                    setTheme();
                }
            }
        });
        ((TextView)view.findViewById(R.id.tv_theme_name)).setText(themeNames[pos]);
        ((TextView)view.findViewById(R.id.tv_theme_message)).setText(themeMessages[pos]);
        view.findViewById(R.id.tv_theme_description).setVisibility(pos == 0 ?View.VISIBLE:View.GONE);
        Glide.with(this).load(imageIds[pos])
                .into(imageview);
    }

    private void setTheme(){
        UserSessionManager sessionManager = new UserSessionManager(getActivity(),getActivity());
        ThemeApis api = Constants.restAdapter.create(ThemeApis.class);
        api.setTheme(Constants.clientId, sessionManager.getFpTag(), themeIds[pos-1], new Callback<String>() {
            @Override
            public void success(String s, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void setDynamicTheme(){
        UserSessionManager sessionManager = new UserSessionManager(getActivity(),getActivity());
        ThemeApis api = Constants.restAdapter.create(ThemeApis.class);
        api.setDynamicTheme(Constants.clientId, sessionManager.getFpTag(), new Callback<String>() {
            @Override
            public void success(String s, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
