package com.nowfloats.Product_Gallery;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Product_Gallery.Adapter.SpinnerItemCategoryAdapter;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;
import com.thinksity.databinding.FragmentProductCategoryBinding;


public class ProductCategoryFragment extends Fragment {

    private Constants.Type type;
    private UserSessionManager session;


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

        ((ManageProductActivity) getActivity()).setTitle(String.valueOf("Listing an item"));

        session = new UserSessionManager(getContext(), getActivity());
        setProductCategory(session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY));

        binding.btnStart.setOnClickListener(v-> ((ManageProductActivity) getActivity()).loadFragment(ManageProductFragment.newInstance(type, binding.editCategory.getText().toString()), "MANAGE_PRODUCT"));

        binding.editCategory.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.toString().trim().length() > 0)
                {
                    binding.btnStart.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    binding.btnStart.setEnabled(true);
                }

                else
                {
                    binding.btnStart.setBackgroundColor(getResources().getColor(R.color.disableButtonColor));
                    binding.btnStart.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setProductCategory(String category) {

        if(category.equalsIgnoreCase("products"))
        {
            binding.layoutPhysicalProduct.setVisibility(View.VISIBLE);
            binding.layoutServiceOffering.setVisibility(View.GONE);
            binding.layoutCustomProduct.setVisibility(View.GONE);

            type = Constants.Type.PRODUCT;
            binding.labelProductType.setText(String.format(getString(R.string.label_product_type), "Product"));
        }

        else if(category.equalsIgnoreCase("services"))
        {
            binding.layoutPhysicalProduct.setVisibility(View.GONE);
            binding.layoutServiceOffering.setVisibility(View.VISIBLE);
            binding.layoutCustomProduct.setVisibility(View.GONE);

            type = Constants.Type.SERVICE;
            binding.labelProductType.setText(String.format(getString(R.string.label_product_type), "Service"));
        }

        else
        {
            binding.layoutPhysicalProduct.setVisibility(View.GONE);
            binding.layoutServiceOffering.setVisibility(View.GONE);
            binding.layoutCustomProduct.setVisibility(View.VISIBLE);

            SpinnerItemCategoryAdapter spinnerAdapter = new SpinnerItemCategoryAdapter(getContext());

            binding.spinnerItemOption.setAdapter(spinnerAdapter);
            binding.spinnerItemOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    switch (position)
                    {
                        case 0:

                            type = Constants.Type.PRODUCT;
                            binding.labelProductType.setText(String.format(getString(R.string.label_product_type), "Product"));
                            break;

                        case 1:

                            type = Constants.Type.SERVICE;
                            binding.labelProductType.setText(String.format(getString(R.string.label_product_type), "Service"));
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
}