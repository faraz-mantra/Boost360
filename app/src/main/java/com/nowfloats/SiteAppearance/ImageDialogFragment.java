package com.nowfloats.SiteAppearance;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Admin on 23-03-2017.
 */

public class ImageDialogFragment extends DialogFragment {

    private Context mContext;
    UserSessionManager manager;
    String[] themeIds;
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
        manager = new UserSessionManager(mContext,getActivity());
        themeIds = mContext.getResources().getStringArray(R.array.themeIds);
        final ViewPager pager = (ViewPager) view.findViewById(R.id.viewpager);
        Button selectButton = (Button) view.findViewById(R.id.selectButton);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(pager.getCurrentItem());
            }
        });
        ScreenShotsAdapter adapter = new ScreenShotsAdapter(getChildFragmentManager());
        pager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        pager.setPadding(10, 0, 10, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        pager.setPageMargin(30);

        pager.setAdapter(adapter);
    }

    private void setTheme(int currentItem) {
        HashMap<String,String> map = new HashMap<>();
        map.put("clientId",Constants.clientId);
        map.put("fpTag",manager.getFpTag());
        map.put("templateId",themeIds[currentItem]);
        ThemeApi api = Constants.restAdapter.create(ThemeApi.class);
        api.changeTheme(map, new Callback<Boolean>() {
            @Override
            public void success(Boolean aBoolean, Response response) {
                if(aBoolean){
                    Log.v("ggg","successful");
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    interface ThemeApi{
        @FormUrlEncoded
        @POST("/Kitsune/v1/fixtheme")
        void changeTheme(@FieldMap Map<String, String> map, Callback<Boolean> response);
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
