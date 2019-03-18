package com.nowfloats.Product_Gallery;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.helper.Helper;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;
import com.thinksity.databinding.FragmentManageProductBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT;
import static com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE;

public class ManageProductFragment extends Fragment {

    private String currencyValue;
    private String currencyType = "";

    private List<Product.Property> propertyList = new ArrayList<>();
    private List<Product.Image> imageList = new ArrayList<>();

    private ProductSpecificationRecyclerAdapter adapter;
    private ProductImageRecyclerAdapter adapterImage;

    private final int CAMERA_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE = 2;
    private Uri picUri;
    private UserSessionManager session;


    public static ManageProductFragment newInstance()
    {
        ManageProductFragment fragment = new ManageProductFragment();

//      Bundle args = new Bundle();
//      args.putString(ARG_PARAM1, param1);
//      args.putString(ARG_PARAM2, param2);
//      fragment.setArguments(args);

        return fragment;
    }

    private FragmentManageProductBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_product, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        session = new UserSessionManager(getContext(), getActivity());

        initProductSpecificationRecyclerView(binding.layoutVariants.productSpecificationList);
        initProductImageRecyclerView(binding.productImageList);

        initCurrencyList();
        addPropertyListener();
        addSpinnerListener();
        addSwitchVariantListener();
        addImagePickerListener();
    }


    private void addPropertyListener()
    {
        imageList.add(new Product().new Image());
        imageList.add(new Product().new Image());
        imageList.add(new Product().new Image());

        propertyList.add(new Product().new Property("Key 1", "Value 1"));
        propertyList.add(new Product().new Property("Key 2", "Value 2"));
        propertyList.add(new Product().new Property("Key 3", "Value 3"));

        binding.layoutVariants.buttonAddProperty.setOnClickListener(view -> {

            propertyList.add(new Product().new Property());
            adapter.notifyItemInserted(propertyList.size());
        });
    }

    private void addSwitchVariantListener()
    {
        binding.switchVariants.setOnToggledListener((labeledSwitch, isOn) ->{


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

    private void addImagePickerListener()
    {
        binding.imagePicker.setOnClickListener(v -> choosePicture());
        binding.btnSecondaryImage.setOnClickListener(v -> choosePicture());
    }

    private void choosePicture()
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

            cameraIntent();
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

                viewHolder.btn_change.setOnClickListener(view -> choosePicture());

                viewHolder.ib_remove.setOnClickListener(view -> {

                    imageList.remove(viewHolder.getAdapterPosition());
                    notifyItemRemoved(viewHolder.getAdapterPosition());
                });

                Product.Property property = propertyList.get(i);
            }
        }

        @Override
        public int getItemCount()
        {
            return imageList.size();
        }


        class ProductImageViewHolder extends RecyclerView.ViewHolder
        {
            Button btn_change;
            ImageButton ib_remove;

            private ProductImageViewHolder(View itemView)
            {
                super(itemView);

                btn_change = itemView.findViewById(R.id.btn_change);
                ib_remove = itemView.findViewById(R.id.ib_remove);
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

                Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                //set limit on number of images that can be selected, default is 10
                intent.putExtra(INTENT_EXTRA_LIMIT, 8);
                startActivityForResult(intent, REQUEST_CODE);
            }
        }

        catch (ActivityNotFoundException e)
        {
            String message = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(getActivity(), message);
        }
    }

    private void cameraIntent()
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
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                }
            }

            else
            {
                //mIsImagePicking = true;

                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.TITLE, "New Picture");
                contentValues.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                Intent captureIntent;

                picUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                startActivityForResult(captureIntent, Constants.CAMERA_PHOTO);
            }
        }

        catch (ActivityNotFoundException e)
        {
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(getActivity(), errorMessage);
        }
    }
}