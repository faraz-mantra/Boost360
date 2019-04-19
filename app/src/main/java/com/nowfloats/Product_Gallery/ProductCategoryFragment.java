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
import com.nowfloats.Product_Gallery.Model.Product;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;
import com.thinksity.databinding.FragmentProductCategoryBinding;


public class ProductCategoryFragment extends Fragment {

    private String productType;
    private UserSessionManager session;
    private Product product;

    public static ProductCategoryFragment newInstance(Product product)
    {
        ProductCategoryFragment fragment = new ProductCategoryFragment();

        Bundle args = new Bundle();
        args.putSerializable("PRODUCT", product);
        fragment.setArguments(args);

        return fragment;
    }


    private FragmentProductCategoryBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();

        if (bundle != null)
        {
            this.product = (Product) bundle.getSerializable("PRODUCT");
        }
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

        binding.editCategory.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.toString().trim().length() > 0)
                {
                    binding.btnStart.setBackgroundResource(R.drawable.rounded_button_enabled);
                    binding.btnStart.setEnabled(true);
                }

                else
                {
                    binding.btnStart.setBackgroundResource(R.drawable.rounded_button_disabled);
                    binding.btnStart.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(product != null && product.productId != null)
        {
            binding.editCategory.setText(product.category != null ? product.category : "");

            if(product.productType != null)
            {
                productType = product.productType;
                setProductType(productType);
            }

            else
            {
                productType = setProductType(session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY));
            }
        }

        else
        {
            productType = setProductType(session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY));
        }


        binding.btnStart.setOnClickListener(v-> ((ManageProductActivity) getActivity()).loadFragment(ManageProductFragment.newInstance(productType, binding.editCategory.getText().toString(), product), "MANAGE_PRODUCT"));
    }

    private String setProductType(String productType) {

        if(productType.equalsIgnoreCase("products"))
        {
            binding.layoutPhysicalProduct.setVisibility(View.VISIBLE);
            binding.layoutServiceOffering.setVisibility(View.GONE);
            binding.layoutCustomProduct.setVisibility(View.GONE);

            binding.labelProductType.setText(String.format(getString(R.string.label_product_type), "Product"));
        }

        else if(productType.equalsIgnoreCase("services"))
        {
            binding.layoutPhysicalProduct.setVisibility(View.GONE);
            binding.layoutServiceOffering.setVisibility(View.VISIBLE);
            binding.layoutCustomProduct.setVisibility(View.GONE);

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

                            binding.labelProductType.setText(String.format(getString(R.string.label_product_type), "Product"));
                            setType("products");
                            break;

                        case 1:

                            binding.labelProductType.setText(String.format(getString(R.string.label_product_type), "Service"));
                            setType("services");
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        return productType;
    }

    final void setType(String productType)
    {
        this.productType = productType;
    }
}