package com.nowfloats.NavigationDrawer;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.businessApps.FragmentsFactoryActivity;
import com.nowfloats.ProductGallery.ProductCatalogActivity;
import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.Store.SimpleImageTextListAdapter;
import com.nowfloats.hotel.placesnearby.PlacesNearByActivity;
import com.nowfloats.hotel.tripadvisor.TripAdvisorActivity;
import com.nowfloats.util.Utils;
import com.thinksity.R;

/**
 * Created by Admin on 29-01-2018.
 */

public class ManageContentFragment extends Fragment {
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upgrade, container, false);
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

        UserSessionManager session = new UserSessionManager(getContext(), getActivity());

        final String[] adapterTexts = getResources().getStringArray(R.array.manage_content_tab_items);
        adapterTexts[0] = Utils.getProductCatalogTaxonomyFromServiceCode(session.getFP_AppExperienceCode());

        final TypedArray imagesArray = getResources().obtainTypedArray(R.array.manage_content_sidepanel);
        int[] adapterImages = new int[adapterTexts.length];
        for (int i = 0; i < adapterTexts.length; i++) {
            adapterImages[i] = imagesArray.getResourceId(i, -1);
        }
        imagesArray.recycle();
        RecyclerView mRecyclerView = view.findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(mContext, new OnItemClickCallback() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = null;
                switch (pos) {
                    case 0:
                        intent = new Intent(mContext, ProductCatalogActivity.class);
                        break;
                    case 1:
                        ((SidePanelFragment.OnItemClickListener) mContext).onClick(getString(R.string.update));
                        return;
                    case 2:
                        intent = new Intent(mContext, ImageMenuActivity.class);
                        break;
                    case 3:
                        intent = new Intent(mContext, FragmentsFactoryActivity.class);
                        intent.putExtra("fragmentName", "Business_Profile_Fragment_V2");
                        break;
                    case 4:
                        intent = new Intent(mContext, CustomPageActivity.class);
                        break;
                    case 5:
                        intent = new Intent(mContext, PlacesNearByActivity.class);
                        break;
//                    case 6:
//                        intent = new Intent(mContext, TripAdvisorActivity.class);
//                        break;
//                    case 5:
//                        intent = new Intent(mContext, TestimonialsActivity.class);
//                        break;
//                    case 6:
//                        intent = new Intent(mContext, ProjectAndTermsActivity.class);
//                        break;
                    default:
                        return;
                }
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        adapter.setItems(adapterImages, adapterTexts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mContext instanceof HomeActivity && HomeActivity.headerText != null) {
            HomeActivity.headerText.setText(getString(R.string.manage_content));
        }
    }
}
