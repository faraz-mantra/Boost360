package com.nowfloats.ProductGallery;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.appservice.constant.FragmentType;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.inventoryorder.model.bottomsheet.PickInventoryNatureModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.ProductGallery.Model.Product;
import com.nowfloats.ProductGallery.Service.ProductGalleryInterface;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
import com.thinksity.R;
import com.thinksity.databinding.FragmentProductCategoryBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.appservice.ui.catalog.CatalogServiceContainerActivityKt.startFragmentActivityNew;

public class ProductCategoryFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private String productType;
    private UserSessionManager session;
    private Product product;
    private ArrayList<PickInventoryNatureModel> list;
    private FragmentProductCategoryBinding binding;

    public static ProductCategoryFragment newInstance(Product product) {
        ProductCategoryFragment fragment = new ProductCategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("PRODUCT", product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.product = (Product) bundle.getSerializable("PRODUCT");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_category_listing, container, false);
//        binding.btnStart.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        session = new UserSessionManager(getContext(), getActivity());
        if (product != null && product.productId != null) {
            binding.editCategory.setText(product.category != null ? product.category : "");
            if (!TextUtils.isEmpty(product.productType) && (product.productType.equalsIgnoreCase("services") || product.productType.equalsIgnoreCase("products"))) {
                productType = product.productType;
                setProductType(productType,
                        "Editing ".concat(Utils.getSingleProductTaxonomyFromServiceCode(session.getFP_AppExperienceCode())));
            } else {
                productType = setProductType("products", "Editing ".concat(Utils.getSingleProductTaxonomyFromServiceCode(session.getFP_AppExperienceCode())));
            }
        } else {
            String type = "";
            if (product != null && product.productType != null)
                type = product.productType;
            else type = "products";
            productType = setProductType(/*session.getFPDetails(Key_Preferences.PRODUCT_CATEGORY)*/ type, getString(R.string.adding_to_catalogue));
        }
        binding.btnStart.setOnClickListener(v -> ((ManageProductActivity) getActivity()).loadFragment(ManageProductFragment.newInstance(product), "MANAGE_PRODUCT"));
        addInfoButtonListener();
        getCategoryList();
    }

    /**
     * Set type product/service
     *
     * @param productType
     * @return
     */
    private String setProductType(String productType, String title) {

        //If product
        if (productType.equalsIgnoreCase("products")) {
            binding.layoutPhysicalProduct.setVisibility(View.VISIBLE);
            binding.arrowBtn.setVisibility(View.GONE);
            binding.layoutServiceOffering.setVisibility(View.GONE);
            binding.layoutCustomProduct.setVisibility(View.GONE);
            binding.labelProductType.setText(String.format(getString(R.string.label_product_type), "Product"));
        }

        //If service
        else if (productType.equalsIgnoreCase("services")) {
            binding.layoutPhysicalProduct.setVisibility(View.GONE);
            binding.arrowBtn.setVisibility(View.GONE);
            binding.layoutServiceOffering.setVisibility(View.VISIBLE);
            binding.layoutCustomProduct.setVisibility(View.GONE);
            binding.labelProductType.setText(String.format(getString(R.string.label_product_type), "Service"));
        }
        //If custom then display spinner for product/service selection
        else {
            binding.layoutPhysicalProduct.setVisibility(View.VISIBLE);
            binding.arrowBtn.setVisibility(View.VISIBLE); //show arrow btn click to open bottom sheet
            binding.layoutServiceOffering.setVisibility(View.GONE);
            binding.layoutCustomProduct.setVisibility(View.GONE);// hide spinner
            if (binding.arrowBtn.getVisibility() == View.VISIBLE) {
                list = new PickInventoryNatureModel().getData();
                binding.layoutPhysicalProduct.setOnClickListener(v -> {
                    PickInventoryNatureBottomSheetDialog1 dialog = new PickInventoryNatureBottomSheetDialog1(list, this::pickInventory);
                    dialog.show(getParentFragmentManager(), PickInventoryNatureBottomSheetDialog1.class.getName());
                });
            }

//            SpinnerItemCategoryAdapter spinnerAdapter = new SpinnerItemCategoryAdapter(getContext());
//            binding.spinnerItemOption.setAdapter(spinnerAdapter);
//            binding.spinnerItemOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                    spinnerAdapter.setSelection(position);
//
//                    switch (position)
//                    {
//                        case 0:
//
//                            binding.labelProductType.setText(String.format(getString(R.string.label_product_type), "Product"));
//                            setType("products");
//                            break;
//
//                        case 1:
//
//                            binding.labelProductType.setText(String.format(getString(R.string.label_product_type), "Service"));
//                            setType("services");
//                            break;
//                    }
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
        }

        ((ManageProductActivity) getActivity()).setTitle(title);
        return productType;
    }

    private void pickInventory(PickInventoryNatureModel item) {
        if (item.getIconType() != null) binding.ivIcon.setImageResource(item.getIconType());
        binding.labelItemType.setText(item.getInventoryName());
        binding.labelItemDescription.setText(item.getInventoryDescription());
        setType(item.getType());
    }

    final void setType(String productType) {
        this.productType = productType;
    }

    /**
     * Initialize tooltip object
     *
     * @param position
     * @param message
     * @param view
     */
    private void toolTip(ViewTooltip.Position position, String message, View view) {
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

    private void addInfoButtonListener() {
        binding.ibInfoProductType.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.defining_product_type_makes_it_easier_to), binding.ibInfoProductType));
    }


    /**
     * Add listener to autocomplete suggestion
     *
     * @param categories
     */
    private void addAutoCompleteListener(List<String> categories) {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.customized_spinner_item, categories);

            binding.editCategory.setAdapter(adapter);
            binding.editCategory.setOnItemClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                startFragmentActivityNew(getActivity(), FragmentType.SERVICE_DETAIL_VIEW, new Bundle(), false, true);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * Fetch suggested category list
     */
    private void getCategoryList() {
        if (!Methods.isOnline(getActivity())) {
            return;
        }

        HashMap<String, String> values = new HashMap<>();
        values.put("clientId", Constants.clientId);
        values.put("fpId", session.getFPID());

        ProductGalleryInterface galleryInterface = Constants.restAdapterDev.create(ProductGalleryInterface.class);

        galleryInterface.getAllCategories(values, new Callback<List<String>>() {

            @Override
            public void success(List<String> data, Response response) {

                if (data != null && response.getStatus() == 200) {
                    addAutoCompleteListener(data);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


}