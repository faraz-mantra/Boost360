package com.nowfloats.NavigationDrawer.businessApps;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.thinksity.R;

/**
 * Created by Admin on 01-02-2017.
 */

public class ImageViewFragment extends Fragment {
    String url;
    public static Fragment getInstance(String url){
        Fragment frag = new ImageViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            url = getArguments().getString("url");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_images_item,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ImageView image = (ImageView) view.findViewById(R.id.screenshot);
        if(isAdded()) {
            Glide.with(this).load(url).into(image);
        }

    }
}

