package com.nowfloats.Product_Gallery;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Product_Gallery.Adapter.SpinnerAdapter;
import com.nowfloats.Product_Gallery.Service.ProductGalleryInterface;
import com.nowfloats.Product_Gallery.Service.UploadImage;
import com.nowfloats.Product_Gallery.fragments.ProductPickupAddressFragment;
import com.nowfloats.helper.Helper;
import com.nowfloats.helper.ui.ImageLoader;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.widget.Widget;
import com.nowfloats.widget.WidgetKey;
import com.thinksity.R;
import com.thinksity.databinding.FragmentManageProductBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.app.Activity.RESULT_OK;
import static com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT;
import static com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE;

public class ManageProductFragment extends Fragment implements UploadImage.ImageUploadListener{

    private String currencyValue;
    private String currencyType = "";

    private List<Product.Property> propertyList = new ArrayList<>();
    private List<Product.Image> imageList = new ArrayList<>();

    private ProductSpecificationRecyclerAdapter adapter;
    private ProductImageRecyclerAdapter adapterImage;
    private ProductPickupAddressRecyclerAdapter adapterAddress;

    private final int CAMERA_PERMISSION_REQUEST_CODE = 1;

    private final int CAMERA_PRIMARY_IMAGE_REQUEST_CODE = 100;
    private final int CAMERA_SECONDARY_IMAGE_REQUEST_CODE = 200;

    private final int GALLERY_REQUEST_CODE = 2;
    private Uri picUri;
    private String CATEGORY;
    private UserSessionManager session;
    private MaterialDialog materialDialog;


    private HashMap<String, String> values;
    private Constants.PaymentAndDeliveryMode paymentAndDeliveryMode = Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE;

    private com.nowfloats.Product_Gallery.Model.Product product;

    private BottomSheetBehavior sheetBehavior;
    private BottomSheetBehavior sheetBehaviorAddress;

    private ProductPickupAddressFragment pickupAddressFragment;

    public static ManageProductFragment newInstance(Constants.Type category, String type, com.nowfloats.Product_Gallery.Model.Product product)
    {
        ManageProductFragment fragment = new ManageProductFragment();

        Bundle args = new Bundle();
        args.putString("CATEGORY", category.name());
        args.putString("TYPE", type);
        args.putSerializable("PRODUCT", product);
        fragment.setArguments(args);

        return fragment;
    }


    private FragmentManageProductBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /*Bundle bundle = this.getArguments();

        if (bundle != null)
        {
            this.product = (Product) bundle.getSerializable("PRODUCT");
        }*/
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_product, container, false);
        sheetBehavior = BottomSheetBehavior.from(binding.layoutBottomSheet.getRoot());
        sheetBehaviorAddress = BottomSheetBehavior.from(binding.layoutBottomSheetAddress.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        session = new UserSessionManager(getContext(), getActivity());

        initProductSpecificationRecyclerView(binding.layoutVariants.productSpecificationList);
        initProductImageRecyclerView(binding.productImageList);
        initProductPickupAddressRecyclerView(binding.layoutBottomSheetAddress.pickupAddressList);

        Bundle bundle = getArguments();

        if(bundle != null)
        {
            CATEGORY = bundle.getString("CATEGORY");
            this.product = (com.nowfloats.Product_Gallery.Model.Product) bundle.getSerializable("PRODUCT");

            String type = bundle.getString("TYPE");
            ((ManageProductActivity) getActivity()).setTitle(String.valueOf("Listing " + type));

            if(CATEGORY.equals(Constants.Type.SERVICE.name()))
            {
                binding.layoutInventory.layoutInventoryRoot.setVisibility(View.GONE);
                binding.layoutPaymentMethod.layoutPaymentAssuredPurchase.setVisibility(View.GONE);

                binding.layoutBottomSheet.tvPickAddress.setVisibility(View.GONE);
                binding.layoutBottomSheet.layoutPickupAddressInfo.setVisibility(View.GONE);
            }

            else
            {
                binding.layoutInventory.layoutInventoryRoot.setVisibility(View.VISIBLE);
                binding.layoutPaymentMethod.layoutPaymentAssuredPurchase.setVisibility(View.VISIBLE);

                binding.layoutBottomSheet.tvPickAddress.setVisibility(View.VISIBLE);
                binding.layoutBottomSheet.layoutPickupAddressInfo.setVisibility(View.VISIBLE);
            }
        }

        initCurrencyList();
        addPropertyListener();
        addSpinnerListener();
        addSwitchVariantListener();
        addImagePickerListener();
        addPaymentConfigListener();
        initPaymentAdapter();
        spinnerAddressListener();
        addBottomSheetListener();
        addTextChangeListener();

        placeholder();

        if(product == null)
        {
            Log.d("PRODUCT_LOG", "NULL");
        }

        else
        {
            Log.d("PRODUCT_LOG", "PRODUCT IS NOT NULL");
        }

        binding.layoutPaymentMethod.editWeight.setKeyListener(DigitsKeyListener.getInstance(true,true));
        binding.layoutPaymentMethod.editHeight.setKeyListener(DigitsKeyListener.getInstance(true,true));
        binding.layoutPaymentMethod.editLength.setKeyListener(DigitsKeyListener.getInstance(true,true));
        binding.layoutPaymentMethod.editThickness.setKeyListener(DigitsKeyListener.getInstance(true,true));
        binding.layoutInventory.editAvailableQty.setKeyListener(DigitsKeyListener.getInstance(true,true));
        binding.layoutInventory.editCodMaxQty.setKeyListener(DigitsKeyListener.getInstance(true,true));
        binding.layoutInventory.editPrepaidOnlineMaxQty.setKeyListener(DigitsKeyListener.getInstance(true,true));

        binding.btnPublish.setOnClickListener(view -> {

            if(product == null)
            {
                addProduct();
            }
        });


        String transactionFees = getTransactionFees();
        binding.layoutBottomSheet.tvPaymentConfigurationAcceptanceMessage.setText(String.format(String.valueOf(getString(R.string.payment_config_acceptance_message)), transactionFees));
    }


    private void addTextChangeListener()
    {
        binding.editBasePrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                setFinalPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        binding.editDiscount.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                setFinalPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setFinalPrice()
    {
        if(binding.editBasePrice.getText().toString().isEmpty())
        {
            binding.editDiscount.setEnabled(false);
        }

        else
        {
            binding.editDiscount.setEnabled(true);
        }

        String basePrice = binding.editBasePrice.getText().toString().isEmpty() ? "0" : binding.editBasePrice.getText().toString();
        String discount = binding.editDiscount.getText().toString().isEmpty() ? "0" : binding.editDiscount.getText().toString();

        double finalPrice = Double.valueOf(basePrice) - Double.valueOf(discount);
        binding.labelFinalPrice.setText(Helper.getCurrencyFormatter().format(finalPrice));
    }

    /**
     * Revamped Widget Logic
     */
    private String getTransactionFees()
    {
        String value = WidgetKey.getPropertyValue(WidgetKey.WIDGET_TRANSACTION_FEES, WidgetKey.WIDGET_PROPERTY_TRANSACTION_FEES);
        return value.contains("%") ? value : value.concat("%");
    }

    private void placeholder()
    {
        String category;

        if(CATEGORY.equals(Constants.Type.SERVICE.name()))
        {
            category = "Service";
        }

        else
        {
            category = "Product";
        }

        binding.labelProductName.setText(String.format(getString(R.string.label_product_name), category));
        binding.editProductName.setHint(String.format(getString(R.string.hint_product_name), category.toLowerCase()));

        binding.labelProductDescription.setText(String.format(getString(R.string.label_product_description), category));
        binding.editProductDescription.setHint(String.format(getString(R.string.hint_product_description), category.toLowerCase()));
    }


    private void addPropertyListener()
    {
        //imageList.add(new Product().new Image());
        //imageList.add(new Product().new Image());
        //imageList.add(new Product().new Image());

        propertyList.add(new Product().new Property("Key 1", "Value 1"));
        propertyList.add(new Product().new Property("Key 2", "Value 2"));
        propertyList.add(new Product().new Property("Key 3", "Value 3"));

        binding.layoutVariants.buttonAddProperty.setOnClickListener(view -> {

            propertyList.add(new Product().new Property());
            adapter.notifyItemInserted(propertyList.size());
        });
    }

    private void initPaymentAdapter()
    {
        binding.layoutBottomSheet.layoutAssuredPurchase.setVisibility(View.GONE);
        binding.layoutBottomSheet.layoutMyPaymentGateway.setVisibility(View.GONE);

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getContext());
        binding.layoutBottomSheet.spinnerPaymentOption.setAdapter(spinnerAdapter);

        binding.layoutBottomSheet.spinnerPaymentOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position)
                {
                    case 0:

                        binding.layoutBottomSheet.layoutAssuredPurchase.setVisibility(View.VISIBLE);
                        binding.layoutBottomSheet.layoutMyPaymentGateway.setVisibility(View.GONE);
                        break;

                    case 1:

                        binding.layoutBottomSheet.layoutAssuredPurchase.setVisibility(View.GONE);
                        binding.layoutBottomSheet.layoutMyPaymentGateway.setVisibility(View.VISIBLE);
                        break;

                    default:

                        binding.layoutBottomSheet.layoutAssuredPurchase.setVisibility(View.GONE);
                        binding.layoutBottomSheet.layoutMyPaymentGateway.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.layoutBottomSheet.btnCancel.setOnClickListener(v -> toggleBottomSheet());
        binding.layoutBottomSheet.btnSaveInfo.setOnClickListener(v -> {

            switch (binding.layoutBottomSheet.spinnerPaymentOption.getSelectedItemPosition())
            {
                case 0:

                    if(isValidAssuredPurchase())
                    {
                        if(CATEGORY.equals(Constants.Type.PRODUCT.name()))
                        {
                            binding.layoutPaymentMethod.layoutPaymentAssuredPurchase.setVisibility(View.VISIBLE);
                        }

                        else
                        {
                            binding.layoutPaymentMethod.layoutPaymentAssuredPurchase.setVisibility(View.GONE);
                        }

                        binding.layoutPaymentMethod.layoutPaymentMyPaymentGateway.setVisibility(View.GONE);
                        binding.layoutPaymentMethod.layoutPaymentExternalPurchaseUrl.setVisibility(View.GONE);
                        binding.layoutPaymentMethod.layoutPaymentDontWantToSell.setVisibility(View.GONE);
                        binding.layoutPaymentMethod.tvPaymentConfiguration.setText(String.valueOf("Assured Purchase"));

                        paymentAndDeliveryMode = Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE;
                        toggleBottomSheet();
                    }

                    break;

                case 1:

                    if(isValidMyPayementGateway())
                    {
                        binding.layoutPaymentMethod.layoutPaymentAssuredPurchase.setVisibility(View.GONE);
                        binding.layoutPaymentMethod.layoutPaymentMyPaymentGateway.setVisibility(View.VISIBLE);
                        binding.layoutPaymentMethod.layoutPaymentExternalPurchaseUrl.setVisibility(View.GONE);
                        binding.layoutPaymentMethod.layoutPaymentDontWantToSell.setVisibility(View.GONE);
                        binding.layoutPaymentMethod.tvPaymentConfiguration.setText(String.valueOf("My payment gateway"));

                        paymentAndDeliveryMode = Constants.PaymentAndDeliveryMode.MY_PAYMENT_GATEWAY;
                        toggleBottomSheet();
                    }

                    break;

                case 2:

                    binding.layoutPaymentMethod.layoutPaymentAssuredPurchase.setVisibility(View.GONE);
                    binding.layoutPaymentMethod.layoutPaymentMyPaymentGateway.setVisibility(View.GONE);
                    binding.layoutPaymentMethod.layoutPaymentExternalPurchaseUrl.setVisibility(View.VISIBLE);
                    binding.layoutPaymentMethod.layoutPaymentDontWantToSell.setVisibility(View.GONE);
                    binding.layoutPaymentMethod.tvPaymentConfiguration.setText(String.valueOf("Variant's unique payment URL"));

                    paymentAndDeliveryMode = Constants.PaymentAndDeliveryMode.UNIQUE_PAYMENT_URL;
                    toggleBottomSheet();
                    break;

                case 3:

                    binding.layoutPaymentMethod.layoutPaymentAssuredPurchase.setVisibility(View.GONE);
                    binding.layoutPaymentMethod.layoutPaymentMyPaymentGateway.setVisibility(View.GONE);
                    binding.layoutPaymentMethod.layoutPaymentExternalPurchaseUrl.setVisibility(View.GONE);
                    binding.layoutPaymentMethod.layoutPaymentDontWantToSell.setVisibility(View.VISIBLE);
                    binding.layoutPaymentMethod.tvPaymentConfiguration.setText(String.valueOf("Don't want to sell online"));

                    paymentAndDeliveryMode = Constants.PaymentAndDeliveryMode.DONT_WANT_TO_SELL;
                    toggleBottomSheet();
                    break;
            }
        });
    }


    private void addSwitchVariantListener()
    {
        binding.switchVariants.setOnToggledListener((labeledSwitch, isOn) ->{

        });
    }


    private void spinnerAddressListener()
    {
       binding.layoutBottomSheetAddress.buttonAddNew.setOnClickListener(v -> {

           sheetBehaviorAddress.setState(BottomSheetBehavior.STATE_COLLAPSED);
           openAddressDialog();
       });

       binding.layoutBottomSheet.tvPickAddress.setOnClickListener(view -> toggleAddressBottomSheet());
    }


    private void addBottomSheetListener()
    {
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View view, int i) {

                sheetBehaviorAddress.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }


    /**
     * Initialize service adapter
     * @param recyclerView
     */
    private void initProductSpecificationRecyclerView(RecyclerView recyclerView)
    {
        adapter = new ProductSpecificationRecyclerAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    /**
     * Initialize product image adapter
     * @param recyclerView
     */
    private void initProductImageRecyclerView(RecyclerView recyclerView)
    {
        adapterImage = new ProductImageRecyclerAdapter();
        recyclerView.setAdapter(adapterImage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    /**
     * Initialize service adapter
     * @param recyclerView
     */
    private void initProductPickupAddressRecyclerView(RecyclerView recyclerView)
    {
        adapterAddress = new ProductPickupAddressRecyclerAdapter();
        recyclerView.setAdapter(adapterAddress);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void openAddressDialog()
    {
        if(pickupAddressFragment == null)
        {
            pickupAddressFragment = ProductPickupAddressFragment.newInstance();
        }

        pickupAddressFragment.show(getFragmentManager(), "Address");
    }

    public void addPaymentConfigListener()
    {
        binding.layoutPaymentMethod.tvPaymentConfiguration.setOnClickListener(view -> {

            toggleBottomSheet();
        });
    }

    private void addImagePickerListener()
    {
        binding.cardPrimaryImage.setOnClickListener(v -> choosePicture(CAMERA_PRIMARY_IMAGE_REQUEST_CODE));
        binding.btnSecondaryImage.setOnClickListener(v -> choosePicture(CAMERA_SECONDARY_IMAGE_REQUEST_CODE));
    }

    private void choosePicture(int requestCode)
    {
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.featuredimage_popup, true)
                .show();

        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);

        View view = dialog.getCustomView();

        LinearLayout takeCamera = view.findViewById(R.id.cameraimage);
        LinearLayout takeGallery = view.findViewById(R.id.galleryimage);
        ImageView cameraImg = view.findViewById(R.id.pop_up_camera_imag);
        ImageView galleryImg = view.findViewById(R.id.pop_up_gallery_img);
        cameraImg.setColorFilter(whiteLabelFilter_pop_ip);
        galleryImg.setColorFilter(whiteLabelFilter_pop_ip);

        takeCamera.setOnClickListener(v -> {

            cameraIntent(requestCode);
            dialog.dismiss();
        });

        takeGallery.setOnClickListener(v -> {

            galleryIntent();
            dialog.dismiss();
        });
    }

    private void initCurrencyList()
    {
        currencyValue = getString(R.string.currency_text);
        binding.editCurrency.setText(currencyValue);

        Helper.loadCurrency();

        binding.editCurrency.setOnClickListener(view -> {

            String[] array = Constants.currencyArray.toArray(new String[Constants.currencyArray.size()]);
            Arrays.sort(array);

            showCurrencyList(array);
        });

        try
        {
            currencyValue = Constants.Currency_Country_Map.get(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY).toLowerCase());
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void addSpinnerListener()
    {
        binding.layoutInventory.spinnerStockAvailability.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                if(pos == 0)
                {
                    binding.layoutInventory.spinnerStockAvailability.setBackgroundDrawable(getResources().getDrawable(R.drawable.spinner_background_instock));

                    binding.layoutInventory.spinnerCodAvailability.setEnabled(true);
                    binding.layoutInventory.spinnerPrepaidOnlineAvailability.setEnabled(true);
                    binding.layoutInventory.editCodMaxQty.setEnabled(true);
                    binding.layoutInventory.editPrepaidOnlineMaxQty.setEnabled(true);

                    binding.layoutInventory.editAvailableQty.setEnabled(true);
                }

                else
                {
                    binding.layoutInventory.spinnerStockAvailability.setBackgroundDrawable(getResources().getDrawable(R.drawable.spinner_background_out_of_stock));

                    binding.layoutInventory.spinnerCodAvailability.setEnabled(false);
                    binding.layoutInventory.spinnerPrepaidOnlineAvailability.setEnabled(false);
                    binding.layoutInventory.editCodMaxQty.setEnabled(false);
                    binding.layoutInventory.editPrepaidOnlineMaxQty.setEnabled(false);

                    binding.layoutInventory.editAvailableQty.setEnabled(false);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        binding.layoutInventory.spinnerCodAvailability.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                if(pos == 0)
                {
                    binding.layoutInventory.spinnerCodAvailability.setBackgroundDrawable(getResources().getDrawable(R.drawable.spinner_background_instock));
                }

                else
                {
                    binding.layoutInventory.spinnerCodAvailability.setBackgroundDrawable(getResources().getDrawable(R.drawable.spinner_background_out_of_stock));
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        binding.layoutInventory.spinnerPrepaidOnlineAvailability.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                if(pos == 0)
                {
                    binding.layoutInventory.spinnerPrepaidOnlineAvailability.setBackgroundDrawable(getResources().getDrawable(R.drawable.spinner_background_instock));
                }

                else
                {
                    binding.layoutInventory.spinnerPrepaidOnlineAvailability.setBackgroundDrawable(getResources().getDrawable(R.drawable.spinner_background_out_of_stock));
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    public String showCurrencyList(final String[] currencyList)
    {
        String currencyVal = binding.editCurrency.getText().toString().trim();
        int index = 0;

        if (!Util.isNullOrEmpty(currencyVal))
        {
            index = Arrays.asList(currencyList).indexOf(currencyVal);
        }

        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.select_currency))
                .items(currencyList)
                .widgetColorRes(R.color.primaryColor)
                .itemsCallbackSingleChoice(index, (dialog, view, position, text) -> {

                        try
                        {
                            currencyType = currencyList[position];
                            String s = currencyType.split("-")[1];
                            binding.editCurrency.setText(s);
                        }

                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                        return true;
                }).show();

        return currencyType;
    }


    /**
     * Product Specification Dynamic Input Filed
     */
    class ProductSpecificationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_product_specification_input, viewGroup, false);
            return new ProductSpecificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i)
        {
            if (holder instanceof ProductSpecificationViewHolder)
            {
                final ProductSpecificationViewHolder viewHolder = (ProductSpecificationViewHolder) holder;

                viewHolder.ibRemove.setVisibility(View.VISIBLE);

                viewHolder.ibRemove.setOnClickListener(view -> {

                    propertyList.remove(viewHolder.getAdapterPosition());
                    notifyItemRemoved(viewHolder.getAdapterPosition());
                });

                Product.Property property = propertyList.get(i);

                viewHolder.editKey.setText(property.getKey());
                viewHolder.editValue.setText(property.getValue());
            }
        }

        @Override
        public int getItemCount()
        {
            return propertyList.size();
        }


        class ProductSpecificationViewHolder extends RecyclerView.ViewHolder
        {
            ImageButton ibRemove;
            EditText editKey;
            EditText editValue;

            private ProductSpecificationViewHolder(View itemView)
            {
                super(itemView);

                ibRemove = itemView.findViewById(R.id.ib_remove);
                editKey = itemView.findViewById(R.id.editKey);
                editValue = itemView.findViewById(R.id.editValue);
            }
        }

        public void setData(List<String> productList)
        {
            //this.productList.addAll(productList);
            //notifyDataSetChanged();
        }
    }



    /**
     * Product Image Dynamic Input Filed
     */
    class ProductImageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_product_secondary_image, viewGroup, false);
            return new ProductImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i)
        {
            if (holder instanceof ProductImageViewHolder)
            {
                final ProductImageViewHolder viewHolder = (ProductImageViewHolder) holder;

                //viewHolder.btn_change.setOnClickListener(view -> choosePicture());

                viewHolder.ib_remove.setOnClickListener(view -> {

                    imageList.remove(viewHolder.getAdapterPosition());
                    notifyItemRemoved(viewHolder.getAdapterPosition());
                });

                Product.Image image = imageList.get(i);

                ImageLoader.load(getContext(), image.url, viewHolder.iv_image);
            }
        }

        @Override
        public int getItemCount()
        {
            return imageList.size();
        }


        public void setData(List<Product.Image> images)
        {
            imageList.addAll(images);
            notifyDataSetChanged();
        }

        class ProductImageViewHolder extends RecyclerView.ViewHolder
        {
            Button btn_change;
            ImageButton ib_remove;
            ImageView iv_image;

            private ProductImageViewHolder(View itemView)
            {
                super(itemView);

                btn_change = itemView.findViewById(R.id.btn_change);
                ib_remove = itemView.findViewById(R.id.ib_remove);
                iv_image = itemView.findViewById(R.id.iv_image);
            }
        }
    }



    /**
     * Product Pickup Address Dynamic Input Filed
     */
    class ProductPickupAddressRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_pickup_address, viewGroup, false);
            return new ProductPickupAddressViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i)
        {
            if (holder instanceof ProductPickupAddressViewHolder)
            {
                final ProductPickupAddressViewHolder viewHolder = (ProductPickupAddressViewHolder) holder;

                /*viewHolder.ibRemove.setVisibility(View.VISIBLE);

                viewHolder.ibRemove.setOnClickListener(view -> {

                    propertyList.remove(viewHolder.getAdapterPosition());
                    notifyItemRemoved(viewHolder.getAdapterPosition());
                });

                Product.Property property = propertyList.get(i);

                viewHolder.editKey.setText(property.getKey());
                viewHolder.editValue.setText(property.getValue());*/
            }
        }

        @Override
        public int getItemCount()
        {
            return propertyList.size();
        }


        class ProductPickupAddressViewHolder extends RecyclerView.ViewHolder
        {
            TextView tvType;
            TextView tvAddress;

            private ProductPickupAddressViewHolder(View itemView)
            {
                super(itemView);

                tvType = itemView.findViewById(R.id.label_type);
                tvAddress = itemView.findViewById(R.id.label_address);

                tvType.setPaintFlags(tvType.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                tvType.setOnClickListener(v -> {

                    sheetBehaviorAddress.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    openAddressDialog();
                });

                itemView.setOnClickListener(v -> {

                });
            }
        }
    }


    class Product
    {
        class Property
        {
            private String key = "", value = "";

            public Property()
            {

            }

            public Property(String key, String value)
            {
                this.key = key;
                this.value = value;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        class Image
        {
            private String url;

            Image(String url)
            {
                this.url = url;
            }


            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }


    private void galleryIntent()
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    Methods.showApplicationPermissions("Storage Permission", "We need this permission to enable image upload", getActivity());
                }

                else
                {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
                }
            }

            else
            {
                //mIsImagePicking = true;
                //Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(i, Constants.GALLERY_PHOTO);

                //Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                //set limit on number of images that can be selected, default is 10
                //intent.putExtra(INTENT_EXTRA_LIMIT, 8);
                //startActivityForResult(intent, REQUEST_CODE);
            }
        }

        catch (ActivityNotFoundException e)
        {
            String message = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(getActivity(), message);
        }
    }

    private void cameraIntent(int requestCode)
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {

                    Methods.showApplicationPermissions("Camera And Storage Permission", "We need these permission to enable capture and upload images", getActivity());
                }

                else
                {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                }
            }

            else
            {
                //mIsImagePicking = true;

                //ContentValues contentValues = new ContentValues();
                //contentValues.put(MediaStore.Images.Media.TITLE, "New Picture");
                //contentValues.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                //Intent captureIntent;

                //picUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                //captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                //startActivityForResult(captureIntent, CAMERA_REQUEST_CODE);

                startCamera(requestCode);
            }
        }

        catch (ActivityNotFoundException e)
        {
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(getActivity(), errorMessage);
        }
    }


    private void startCamera(int requestCode)
    {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "boost");

        if(!mediaStorageDir.exists())
        {
            mediaStorageDir.mkdir();
        }

        /**
         * Check if we're running on Android 5.0 or higher
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            picUri = FileProvider.getUriForFile(getActivity(),
                    Constants.PACKAGE_NAME + ".provider",
                    new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        }

        else
        {
            picUri = Uri.fromFile(new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        }


        try
        {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
            startActivityForResult(intent, requestCode);
        }

        catch (Exception e)
        {
            Toast.makeText(getContext(), "Failed to Open Camera", Toast.LENGTH_LONG).show();
        }
    }

    private void toggleBottomSheet()
    {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
        {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        else
        {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


    private void toggleAddressBottomSheet()
    {
        if (sheetBehaviorAddress.getState() != BottomSheetBehavior.STATE_EXPANDED)
        {
            sheetBehaviorAddress.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        else
        {
            sheetBehaviorAddress.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && (requestCode == CAMERA_PRIMARY_IMAGE_REQUEST_CODE || requestCode == CAMERA_SECONDARY_IMAGE_REQUEST_CODE)) {

            Log.d("onActivityResult", "" + picUri.getPath());

            File file = new File(picUri.getPath());
            long length = file.length() / 1024; // Size in KB

            display_image(picUri.getPath(), requestCode);
        }
    }


    private void display_image(String path, int requestCode)
    {
        if(Helper.fileExist(path))
        {
            /*if(!new InternetConnectionDetector(ProfileActivity.this).isConnected())
            {
                new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
                return;
            }*/

            try
            {
                File file = new File(path);

                // bitmap factory
                // BitmapFactory.Options options = new BitmapFactory.Options();
                // downsizing image as it throws OutOfMemory Exception for larger
                // images
                // options.inSampleSize = 4;
                // final Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(), options);
                // binding.ivPrimaryImage.setImageBitmap(bitmap);

                if(requestCode == CAMERA_PRIMARY_IMAGE_REQUEST_CODE)
                {
                    ImageLoader.load(getContext(), file, binding.ivPrimaryImage);
                }

                if(requestCode == CAMERA_SECONDARY_IMAGE_REQUEST_CODE)
                {
                    List<Product.Image> imageList = new ArrayList<>();
                    imageList.add(new Product().new Image(path));

                    adapterImage.setData(imageList);
                }
            }

            catch(Exception e)
            {
                Toast.makeText(getContext(), "Failed to Set Image", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        else
        {
            Toast.makeText(getContext(), "File Not Found", Toast.LENGTH_LONG).show();
        }
    }



    private boolean isValid(boolean keyCheck) {

        String desc = "description", disc = "discountAmount", link = "buyOnlineLink", name = "name",
                price = "price", currency = "currencyCode", avail = "isAvailable", ship = "shipmentDuration",
                freeShipment = "isFreeShipmentAvailable", priority = "priority", availableUnits = "availableUnits";

        if (keyCheck)
        {
            desc = desc.toUpperCase();
            disc = "DISCOUNTPRICE";
            link = link.toUpperCase();
            name = name.toUpperCase();
            price = price.toUpperCase();
            currency = currency.toUpperCase();
            avail = "ISAVAIALABLE";
            ship = ship.toUpperCase();
            freeShipment = "FREESHIPMENT";
        }

        double product_price = 0, product_discount = 0;

        //values.put(avail, switchValue);

        //values.put(freeShipment, String.valueOf(mIsFreeShipment));
        //values.put(priority, String.valueOf(mPriorityVal));

        if (product == null && picUri == null)
        {
            Toast.makeText(getContext(), "Add product image", Toast.LENGTH_LONG).show();
            return false;
            //Methods.showSnackBarNegative(activity, String.format(String.valueOf(getString(R.string.upload_product_image)), productCategory.toLowerCase()));
        }

        try
        {
            values.put(currency, binding.editCurrency.getText().toString());
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (binding.editProductName.getText().toString().trim().length() > 0)
        {
            values.put(name, binding.editProductName.getText().toString().trim());
        }

        else
        {
            //YoYo.with(Techniques.Shake).playOn(productName);
            //Methods.showSnackBarNegative(activity, String.format(String.valueOf(getString(R.string.enter_product_name)), productCategory.toLowerCase()));

            Toast.makeText(getContext(), "Enter product name", Toast.LENGTH_LONG).show();
            return false;
        }

        if (binding.editProductDescription.getText().toString().trim().length() > 0)
        {
            values.put(desc, binding.editProductDescription.getText().toString().trim());
        }

        else
        {
            //YoYo.with(Techniques.Shake).playOn(productDesc);
            //Methods.showSnackBarNegative(activity, String.format(String.valueOf(getString(R.string.enter_product_desc)), productCategory.toLowerCase()));

            Toast.makeText(getContext(), "Enter product description", Toast.LENGTH_LONG).show();
            return false;
        }

        /*if (etShipmentDuration != null && etShipmentDuration.getText().toString().trim().length() > 0)
        {
            values.put(ship, etShipmentDuration.getText().toString());
        }

        else
        {
            values.put(ship, null);
        }*/

        if (binding.editBasePrice.getText().toString().trim().length() > 0) {

            try
            {
                product_price = Double.valueOf(binding.editBasePrice.getText().toString().trim());
            }

            catch (Exception e)
            {
                //YoYo.with(Techniques.Shake).playOn(productPrice);
                //Methods.showSnackBarNegative(activity, "Please enter valid price");

                Toast.makeText(getContext(), "Enter valid price", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        values.put(price, String.valueOf(product_price));

        if (binding.editDiscount.getText().toString().trim().length() > 0)
        {
            try
            {
                product_discount = Double.valueOf(binding.editDiscount.getText().toString().trim());
            }

            catch (Exception e)
            {
                //YoYo.with(Techniques.Shake).playOn(productDiscount);
                //Methods.showSnackBarNegative(activity, "Please enter valid discount");

                Toast.makeText(getContext(), "Enter valid discount", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        values.put(disc, String.valueOf(product_discount));


        if(paymentAndDeliveryMode.equals(Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE))
        {
            if(CATEGORY.equals(Constants.Type.PRODUCT.name()))
            {
                if(binding.layoutPaymentMethod.editWeight.getText().toString().trim().length() == 0)
                {
                    binding.layoutPaymentMethod.editWeight.requestFocus();
                    Toast.makeText(getContext(), "Enter product weight", Toast.LENGTH_LONG).show();
                    return false;
                }

                if(binding.layoutPaymentMethod.editLength.getText().toString().trim().length() == 0)
                {
                    binding.layoutPaymentMethod.editLength.requestFocus();
                    Toast.makeText(getContext(), "Enter product length", Toast.LENGTH_LONG).show();
                    return false;
                }

                if(binding.layoutPaymentMethod.editHeight.getText().toString().trim().length() == 0)
                {
                    binding.layoutPaymentMethod.editHeight.requestFocus();
                    Toast.makeText(getContext(), "Enter product height", Toast.LENGTH_LONG).show();
                    return false;
                }

                if(binding.layoutPaymentMethod.editThickness.getText().toString().trim().length() == 0)
                {
                    binding.layoutPaymentMethod.editThickness.requestFocus();
                    Toast.makeText(getContext(), "Enter product thickness", Toast.LENGTH_LONG).show();
                    return false;
                }
            }

            if(!isValidAssuredPurchase())
            {
                return false;
            }

            /*if(binding.layoutBottomSheet.editBankAccount.getText().toString().trim().length() == 0)
            {
                binding.layoutBottomSheet.editBankAccount.requestFocus();
                Toast.makeText(getContext(), "Enter bank account number", Toast.LENGTH_LONG).show();
                return false;
            }

            if(binding.layoutBottomSheet.editIfscCode.getText().toString().trim().length() == 0)
            {
                binding.layoutBottomSheet.editIfscCode.requestFocus();
                Toast.makeText(getContext(), "Enter IFSC code", Toast.LENGTH_LONG).show();
                return false;
            }

            if(binding.layoutBottomSheet.editGst.getText().toString().trim().length() == 0)
            {
                binding.layoutBottomSheet.editGst.requestFocus();
                Toast.makeText(getContext(), "Enter TAX/GST number", Toast.LENGTH_LONG).show();
                return false;
            }

            if(!binding.layoutBottomSheet.checkPaymentConfiguration.isChecked())
            {
                Toast.makeText(getContext(), "Please allow payment and delivery agreement", Toast.LENGTH_LONG).show();
                return false;
            }*/
        }

        /*if(paymentAndDeliveryMode.equals(Constants.PaymentAndDeliveryMode.MY_PAYMENT_GATEWAY))
        {
            if(binding.layoutBottomSheet.editSaltAndKey.getText().toString().trim().length() == 0)
            {
                binding.layoutBottomSheet.editSaltAndKey.requestFocus();
                Toast.makeText(getContext(), "Enter Salt and Key", Toast.LENGTH_LONG).show();
                return false;
            }
        }*/

        if(!isValidMyPayementGateway())
        {
            return false;
        }

        if(CATEGORY.equals(Constants.Type.PRODUCT.name()))
        {
            /*if(binding.layoutInventory.spinnerStockAvailability.getSelectedItemPosition() == 0)
            {
                binding.layoutInventory.editAvailableQty.requestFocus();
                Toast.makeText(getContext(), "Enter available quantity", Toast.LENGTH_LONG).show();
                return false;
            }*/

            if(binding.layoutInventory.spinnerStockAvailability.getSelectedItemPosition() == 0)
            {
                if(binding.layoutInventory.editAvailableQty.getText().toString().trim().length() == 0)
                {
                    binding.layoutInventory.editAvailableQty.requestFocus();
                    Toast.makeText(getContext(), "Enter available quantity", Toast.LENGTH_LONG).show();
                    return false;
                }

                if(binding.layoutInventory.spinnerCodAvailability.getSelectedItemPosition() == 0)
                {
                    binding.layoutInventory.editCodMaxQty.requestFocus();
                    Toast.makeText(getContext(), "Enter max quantity", Toast.LENGTH_LONG).show();
                    return false;
                }

                if(binding.layoutInventory.spinnerPrepaidOnlineAvailability.getSelectedItemPosition() == 0)
                {
                    binding.layoutInventory.editPrepaidOnlineMaxQty.requestFocus();
                    Toast.makeText(getContext(), "Enter max quantity", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        /*if (productLink != null && productLink.getText().toString().trim().length() > 0)
        {
            values.put(link, productLink.getText().toString().trim());
        }

        else
        {
            values.put(link, "");
        }

        if ((productPrice != null && productPrice.getText().toString().trim().length() > 0) &&
                (productDiscount != null && productDiscount.getText().toString().trim().length() > 0)) {

            if (!(Double.parseDouble(productPrice.getText().toString().trim()) >= Double.parseDouble(productDiscount.getText().toString().trim()))) {

                YoYo.with(Techniques.Shake).playOn(productDiscount);
                Methods.showSnackBarNegative(activity, getString(R.string.discount_amount_can_not_more_than_price));
                return false;
            }
        }

        if(mShippingMetrix == null)
        {
            Methods.showSnackBarNegative(activity, "Shipping Details Required");
            return false;
        }

        if(mShippingMetrix.getWeight() == null || mShippingMetrix.getHeight() == null || mShippingMetrix.getLength() == null ||
                mShippingMetrix.getWidth() == null || mShippingMetrix.getShippingCharge() == null)
        {
            Methods.showSnackBarNegative(activity, "Invalid Shipping Details");
            return false;
        }*/

        /*if (etAvailableUnits != null && etAvailableUnits.getText().toString().trim().length() > 0)
        {
            values.put(availableUnits, etAvailableUnits.getText().toString());
        }

        else
        {
            values.put(availableUnits, "-1");

            if (!keyCheck && flag)
            {
                YoYo.with(Techniques.Shake).playOn(etAvailableUnits);
                Methods.showSnackBarNegative(activity, "Please enter product available units");
                flag = false;
            }
        }*/

        /**
         * If delivery method ASSUREDPURCHASE OR SELF then add shipping metrix
         */
        /*if((deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.ASSURED_PURCHASE.getValue())
                || deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.SELF.getValue())))
        {
            if(mShippingMetrix == null)
            {
                Methods.showSnackBarNegative(activity, "Shipping Details Required");
                return false;
            }

            else if(deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.ASSURED_PURCHASE.getValue())
                    && Constants.PACKAGE_NAME.equals("com.biz2.nowfloats"))
            {
                if(mShippingMetrix.getWeight() == null || mShippingMetrix.getHeight() == null || mShippingMetrix.getLength() == null ||
                        mShippingMetrix.getWidth() == null || mShippingMetrix.getShippingCharge() == null || mShippingMetrix.getGstCharge() == null)
                {
                    Methods.showSnackBarNegative(activity, "Invalid Shipping Details");
                    return false;
                }
            }

            else if(deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.ASSURED_PURCHASE.getValue())
                    && !Constants.PACKAGE_NAME.equals("com.biz2.nowfloats"))
            {
                if(mShippingMetrix.getWeight() == null || mShippingMetrix.getHeight() == null || mShippingMetrix.getLength() == null ||
                        mShippingMetrix.getWidth() == null || mShippingMetrix.getShippingCharge() == null)
                {
                    Methods.showSnackBarNegative(activity, "Invalid Shipping Details");
                    return false;
                }
            }

            else if(deliveryMethod.equalsIgnoreCase(Constants.DeliveryMethod.SELF.getValue()))
            {
                if(mShippingMetrix.getShippingCharge() == null || mShippingMetrix.getGstCharge() == null)
                {
                    Methods.showSnackBarNegative(activity, "Invalid Shipping Details");
                    return false;
                }
            }
        }*/

        System.out.println(values);
        return true;
    }


    private void uploadProductImage(String productId)
    {
        try
        {
            String valuesStr = "clientId=" + Constants.clientId
                    + "&requestType=sequential&requestId=" + Constants.deviceId
                    + "&totalChunks=1&currentChunkNumber=1&productId=" + productId;

            String url = Constants.NOW_FLOATS_API_URL + "/Product/v1/AddImage?" + valuesStr;

            byte[] imageBytes = Methods.compressToByte(picUri.getPath(), getActivity());

            //new ProductImageUploadV45(url, imageBytes, getActivity(), productId).execute();
            //new UploadImage(url, imageBytes, productId, this).execute();

            UploadImage upload = new UploadImage(url, imageBytes, productId);
            upload.setImageUploadListener(this);
            upload.execute();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong_try_again));
        }
    }


    private void addProduct()
    {
        ProductGalleryInterface productInterface = Constants.restAdapter.create(ProductGalleryInterface.class);

        try
        {
            values = new HashMap<>();

            values.put("clientId", Constants.clientId);
            values.put("fpTag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toUpperCase());

            if (isValid(false))
            {
                showDialog("Adding product ...");

                productInterface.addProduct(values, new Callback<String>() {

                    @Override
                    public void success(String productId, Response response) {

                        Log.d("PRODUCT_RESPONSE", "" + productId);
                        uploadProductImage(productId);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        hideDialog();
                        Toast.makeText(getContext(), "Failed to add product", Toast.LENGTH_LONG).show();
                        Log.d("PRODUCT_RESPONSE", "FAIL");
                    }
                });
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void initProgressDialog(String content)
    {
        if(materialDialog != null)
        {
            return;
        }

        materialDialog = new MaterialDialog.Builder(getActivity())
                .widgetColorRes(R.color.accentColor)
                .content(content)
                .progress(true, 0).build();

        materialDialog.setCancelable(false);
    }

    private void hideDialog()
    {
        if(materialDialog != null && materialDialog.isShowing())
        {
            materialDialog.dismiss();
        }
    }

    private void showDialog(String content)
    {
        initProgressDialog(content);

        if(!materialDialog.isShowing())
        {
            materialDialog.show();
        }
    }

    @Override
    public void onPreExecute()
    {

    }

    @Override
    public void onPostExecute(String result, int responseCode)
    {
        hideDialog();

        if(responseCode == 200 || responseCode == 202)
        {
            Toast.makeText(getContext(), "Product added successfully", Toast.LENGTH_LONG).show();
        }

        else
        {
            Toast.makeText(getContext(), "Failed to add product", Toast.LENGTH_LONG).show();
        }
    }


    private boolean isValidMyPayementGateway()
    {
        if(paymentAndDeliveryMode.equals(Constants.PaymentAndDeliveryMode.MY_PAYMENT_GATEWAY))
        {
            if(binding.layoutBottomSheet.editSaltAndKey.getText().toString().trim().length() == 0)
            {
                binding.layoutBottomSheet.editSaltAndKey.requestFocus();
                Toast.makeText(getContext(), "Enter Salt and Key", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    private boolean isValidAssuredPurchase()
    {
        if(paymentAndDeliveryMode.equals(Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE))
        {
            if(binding.layoutBottomSheet.editBankAccount.getText().toString().trim().length() == 0)
            {
                binding.layoutBottomSheet.editBankAccount.requestFocus();
                Toast.makeText(getContext(), "Enter bank account number", Toast.LENGTH_LONG).show();
                return false;
            }

            if(binding.layoutBottomSheet.editIfscCode.getText().toString().trim().length() == 0)
            {
                binding.layoutBottomSheet.editIfscCode.requestFocus();
                Toast.makeText(getContext(), "Enter IFSC code", Toast.LENGTH_LONG).show();
                return false;
            }

            if(binding.layoutBottomSheet.editGst.getText().toString().trim().length() == 0)
            {
                binding.layoutBottomSheet.editGst.requestFocus();
                Toast.makeText(getContext(), "Enter TAX/GST number", Toast.LENGTH_LONG).show();
                return false;
            }

            if(!binding.layoutBottomSheet.checkPaymentConfiguration.isChecked())
            {
                Toast.makeText(getContext(), "Please allow payment and delivery agreement", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }


    /*private void addImage()
    {
        if(!WidgetKey.isNewPricingPlan)
        {
            if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1"))
            {
                Methods.showFeatureNotAvailDialog(getContext());
            }

            else
            {
                imageChooserDialog();
            }
        }

        else
        {
            String value = WidgetKey.getPropertyValue(WidgetKey.WIDGET_PROPERTY_PAYMENT_GATEWAY, WidgetKey.WIDGET_PROPERTY_PAYMENT_GATEWAY);

            if(value.equals(WidgetKey.WidgetValue.FEATURE_NOT_AVAILABLE.getValue()))
            {
                Toast.makeText(getContext(), String.valueOf(getString(R.string.message_feature_not_available)), Toast.LENGTH_LONG).show();
            }

            else if(!value.equals(WidgetKey.WidgetValue.UNLIMITED.getValue()) && otherImagesAdapter.getCount() >= Integer.parseInt(value))
            {
                Toast.makeText(getContext(), String.valueOf(getString(R.string.message_add_image_limit)), Toast.LENGTH_LONG).show();
            }

            else
            {
                imageChooserDialog();
            }
        }
    }*/
}