package com.nowfloats.Product_Gallery;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.nowfloats.Product_Gallery.Adapter.SpinnerItemCategoryAdapter;
import com.nowfloats.util.Constants;
import com.thinksity.R;
import com.thinksity.databinding.FragmentProductCategoryBinding;

public class ProductCategoryFragment extends Fragment {

    private Constants.Type type;

    public static ProductCategoryFragment newInstance()
    {
        ProductCategoryFragment fragment = new ProductCategoryFragment();

//      Bundle args = new Bundle();
//      args.putString(ARG_PARAM1, param1);
//      args.putString(ARG_PARAM2, param2);
//      fragment.setArguments(args);

        return fragment;
    }


    private FragmentProductCategoryBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_category, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        SpinnerItemCategoryAdapter spinnerAdapter = new SpinnerItemCategoryAdapter(getContext());
        binding.spinnerItemOption.setAdapter(spinnerAdapter);
        binding.spinnerItemOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position)
                {
                    case 0:

                        type = Constants.Type.PRODUCT;
                        break;

                    case 1:

                        type = Constants.Type.SERVICE;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnStart.setOnClickListener(v-> ((ManageProductActivity) getActivity()).loadFragment(ManageProductFragment.newInstance(type, binding.editCategory.getText().toString())));
    }
}