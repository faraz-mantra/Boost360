package com.nowfloats.Product_Gallery;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.github.florent37.viewtooltip.ViewTooltip;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Product_Gallery.Adapter.SpinnerItemCategoryAdapter;
import com.nowfloats.Product_Gallery.Model.Product;
import com.nowfloats.Product_Gallery.Service.ProductGalleryInterface;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;
import com.thinksity.databinding.FragmentProductCategoryBinding;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ProductCategoryFragment extends Fragment implements AdapterView.OnItemClickListener {

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

        session = new UserSessionManager(getContext(), getActivity());

        /*binding.editCategory.addTextChangedListener(new TextWatcher() {

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
        });*/

        if(product != null && product.productId != null)
        {
            binding.editCategory.setText(product.category != null ? product.category : "");

            if(!TextUtils.isEmpty(product.productType) && (product.productType.equalsIgnoreCase("services") || product.productType.equalsIgnoreCase("products")))
            {
                productType = product.productType;
                setProductType(productType, new StringBuilder("Editing "));
            }

            else
            {
                productType = setProductType("", new StringBuilder("Editing "));
            }
        }

        else
        {
            productType = setProductType(session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY), new StringBuilder("Adding "));
        }

        binding.btnStart.setOnClickListener(v -> ((ManageProductActivity) getActivity()).loadFragment(ManageProductFragment.newInstance(productType, binding.editCategory.getText().toString(), product), "MANAGE_PRODUCT"));
        addInfoButtonListener();
        getCategoryList();
    }

    /**
     * Set type product/service
     * @param productType
     * @return
     */
    private String setProductType(String productType, StringBuilder title) {

        //If product
        if(productType.equalsIgnoreCase("products"))
        {
            binding.layoutPhysicalProduct.setVisibility(View.VISIBLE);
            binding.layoutServiceOffering.setVisibility(View.GONE);
            binding.layoutCustomProduct.setVisibility(View.GONE);

            binding.labelProductType.setText(String.format(getString(R.string.label_product_type), "Product"));

            title.append("Product");
        }

        //If service
        else if(productType.equalsIgnoreCase("services"))
        {
            binding.layoutPhysicalProduct.setVisibility(View.GONE);
            binding.layoutServiceOffering.setVisibility(View.VISIBLE);
            binding.layoutCustomProduct.setVisibility(View.GONE);

            binding.labelProductType.setText(String.format(getString(R.string.label_product_type), "Service"));

            title.append("Service");
        }

        //If custom then display spinner for product/service selection
        else
        {
            title.append(productType);

            binding.layoutPhysicalProduct.setVisibility(View.GONE);
            binding.layoutServiceOffering.setVisibility(View.GONE);
            binding.layoutCustomProduct.setVisibility(View.VISIBLE);

            SpinnerItemCategoryAdapter spinnerAdapter = new SpinnerItemCategoryAdapter(getContext());

            binding.spinnerItemOption.setAdapter(spinnerAdapter);
            binding.spinnerItemOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    spinnerAdapter.setSelection(position);

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

        ((ManageProductActivity) getActivity()).setTitle(title.toString());
        return productType;
    }

    final void setType(String productType)
    {
        this.productType = productType;
    }

    /**
     * Initialize tooltip object
     * @param position
     * @param message
     * @param view
     */
    private void toolTip(ViewTooltip.Position position, String message, View view)
    {
        ViewTooltip
                .on(getActivity(), view)
                .autoHide(true, 3500)
                .clickToHide(true)
                .corner(30)
                .textColor(Color.WHITE)
                .color(R.color.accentColor)
                .position(position)
                .text(message)
                .show();
    }

    private void addInfoButtonListener()
    {
        binding.ibInfoProductType.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, "Defining product type makes it easier to categorize the <variable product verb> and helps your customers easily find what they are looking for.", binding.ibInfoProductType));
    }


    /**
     * Add listener to autocomplete suggestion
     * @param categories
     */
    private void addAutoCompleteListener(List<String> categories)
    {
        try
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.customized_spinner_item, categories);

            binding.editCategory.setAdapter(adapter);
            binding.editCategory.setOnItemClickListener(this);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * Fetch suggested category list
     */
    private void getCategoryList()
    {
        if(!Methods.isOnline(getActivity()))
        {
            return;
        }

        HashMap<String, String> values = new HashMap<>();
        values.put("clientId", Constants.clientId);
        values.put("fpId", session.getFPID());

        ProductGalleryInterface galleryInterface = Constants.restAdapterDev.create(ProductGalleryInterface.class);

        galleryInterface.getAllCategories(values, new Callback<List<String>>() {

            @Override
            public void success(List<String> data, Response response) {

                if(data != null && response.getStatus() == 200)
                {
                    addAutoCompleteListener(data);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}