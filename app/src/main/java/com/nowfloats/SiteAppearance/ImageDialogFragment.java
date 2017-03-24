package com.nowfloats.SiteAppearance;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.POST;
import retrofit.http.QueryMap;

/**
 * Created by Admin on 23-03-2017.
 */

public class ImageDialogFragment extends DialogFragment {

    private Context mContext;
    UserSessionManager manager;
    String[] themeIds,themeNames;
    ProgressBar progressBar;
    public static ImageDialogFragment getInstance(){
        return new ImageDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_images,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(!isAdded()) return;
        manager = new UserSessionManager(mContext,getActivity());
        themeIds = mContext.getResources().getStringArray(R.array.themeIds);
        themeNames = mContext.getResources().getStringArray(R.array.themeNames);
        final ImageViewTouchViewPager pager = (ImageViewTouchViewPager) view.findViewById(R.id.viewpager);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        Button selectButton = (Button) view.findViewById(R.id.selectButton);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(pager.getCurrentItem());
            }
        });
        ScreenShotsAdapter adapter = new ScreenShotsAdapter(getChildFragmentManager());
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        pager.setPageMargin(20);

        pager.setAdapter(adapter);
    }

    private void setTheme(final int currentItem) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String,String> map = new HashMap<>();
        map.put("clientId",Constants.clientId);
        map.put("fpTag",manager.getFpTag());
        map.put("templateId",themeIds[currentItem]);
        ThemeApi api = Constants.restAdapter.create(ThemeApi.class);
        final SiteAppearanceFragment frag = (SiteAppearanceFragment) getParentFragment();
        api.changeTheme(map, new Callback<Boolean>() {
            @Override
            public void success(Boolean aBoolean, Response response) {
                if(!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                if(frag!=null){
                    frag.hideDialog();
                }
                if(aBoolean){
                    Toast.makeText(mContext, "Successfully Changed theme to "+themeNames[currentItem], Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(mContext, "Failed to change theme into "+themeNames[currentItem], Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(!isAdded()) return;
                if(frag!=null){
                    frag.hideDialog();
                }
                progressBar.setVisibility(View.GONE);
                Toast.makeText(mContext, "Failed to change theme into "+themeNames[currentItem], Toast.LENGTH_LONG).show();
            }
        });
    }

    interface ThemeApi{

        @POST("/Kitsune/v1/fixtheme")
        void changeTheme(@QueryMap Map<String, String> map, Callback<Boolean> response);
    }
    class ScreenShotsAdapter extends FragmentStatePagerAdapter{

        ScreenShotsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ImageViewFragment.getInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
