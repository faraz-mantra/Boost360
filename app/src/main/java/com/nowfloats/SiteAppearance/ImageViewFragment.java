package com.nowfloats.SiteAppearance;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thinksity.R;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * Created by Admin on 23-03-2017.
 */
public class ImageViewFragment extends Fragment{
    int pos;
    String[] themeName;
    int[] images= new int[]{R.drawable.bnb,R.drawable.fml,R.drawable.tff};
    private Context mContext;

    public static Fragment getInstance(int pos){
        Fragment frag = new ImageViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pos",pos);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            pos = getArguments().getInt("pos");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_images_item,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(!isAdded()) return;

        themeName = mContext.getResources().getStringArray(R.array.themeNames);
        ImageViewTouch image = (ImageViewTouch) view.findViewById(R.id.screenshot);
        TextView theme = (TextView) view.findViewById(R.id.themename);
        if(isAdded()){
            Glide.with(getActivity()).load(images[pos]).into(image);
            theme.setText(themeName[pos]);
        }

    }
}
