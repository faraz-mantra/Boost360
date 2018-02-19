package com.nowfloats.NavigationDrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.Image_Gallery.ImageGalleryActivity;
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

        String[] adapterTexts = getResources().getStringArray(R.array.manage_content_tab_items);
        int[] adapterImages = {R.drawable.ic_about,R.drawable.ic_image_gallery, R.drawable.ic_custom_pages};
        RecyclerView mRecyclerView = view.findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(mContext, new OnItemClickCallback() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = null;
                switch(pos){
                    case 0:
                        ((SidePanelFragment.OnItemClickListener)mContext).onClick(getString(R.string.update));
                        return;
                    case 1:
                        intent = new Intent(mContext, ImageGalleryActivity.class);
                        break;
                    case 2:
                       intent = new Intent(mContext, CustomPageActivity.class);
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
