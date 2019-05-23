package com.nowfloats.NavigationDrawer;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.Analytics_Screen.SubscribersActivity;
import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.Image_Gallery.ImageGalleryActivity;
import com.nowfloats.NavigationDrawer.businessApps.FragmentsFactoryActivity;
import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.Store.SimpleImageTextListAdapter;
import com.thinksity.R;

/**
 * Created by Admin on 29-01-2018.
 */

public class ManageContentFragment extends Fragment{
    private Context mContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upgrade, container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded()) return;

        final String[] adapterTexts = getResources().getStringArray(R.array.manage_content_tab_items);
        final TypedArray imagesArray = getResources().obtainTypedArray(R.array.manage_content_sidepanel);
        int[] adapterImages = new int[adapterTexts.length];
        for (int i = 0; i<adapterTexts.length;i++){
            adapterImages[i] = imagesArray.getResourceId(i,-1);
        }
        imagesArray.recycle();
        RecyclerView mRecyclerView = view.findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(mContext, new OnItemClickCallback() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = null;
                switch(adapterTexts[pos]){
                    case "Business Profile":
                        intent = new Intent(mContext,FragmentsFactoryActivity.class);
                        intent.putExtra("fragmentName","Business_Profile_Fragment_V2");
                        break;
                    case "All Updates":
                        ((SidePanelFragment.OnItemClickListener)mContext).onClick(getString(R.string.update));
                        return;
                    case "Image Gallery":
                        intent = new Intent(mContext, ImageGalleryActivity.class);
                        break;
                    case "Custom Pages":
                       intent = new Intent(mContext, CustomPageActivity.class);
                        break;
                    case "Content Sharing Settings":
                        intent = new Intent(mContext, SocialSharingActivity.class);
                        break;
                    default:
                        return;
                }
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        adapter.setItems(adapterImages,adapterTexts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mContext instanceof HomeActivity && HomeActivity.headerText != null)
        {
            HomeActivity.headerText.setText(getString(R.string.manage_content));
        }
    }

}
