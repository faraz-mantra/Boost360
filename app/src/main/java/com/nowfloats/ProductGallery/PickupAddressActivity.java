package com.nowfloats.ProductGallery;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.databinding.DataBindingUtil;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.ProductGallery.Model.AddressInformation;
import com.nowfloats.ProductGallery.Service.FileUpload;
import com.nowfloats.ProductGallery.Service.ProductGalleryInterface;
import com.nowfloats.ProductGallery.fragments.ProductPickupAddressFragment;
import com.nowfloats.sellerprofile.model.WebResponseModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;
import com.thinksity.databinding.ActivityPickupAddressBinding;
/*import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;*/

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nowfloats.util.Constants.DEV_ASSURED_PURCHASE_URL;

public class PickupAddressActivity extends AppCompatActivity implements FileUpload.OnFileUpload {

    public static final String FILE_EXTENSIONS[] = new String[]{"doc", "docx", "xls", "xlsx", "pdf"};
    private final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private final int FILE_PICKER_PERMISSION_REQUEST_CODE = 1;
    private final int CAMERA_PROOF_IMAGE_REQUEST_CODE = 101;
    private final int GALLERY_PROOF_IMAGE_REQUEST_CODE = 102;
    private final int FILE_PROOF_REQUEST_CODE = 103;
    private ActivityPickupAddressBinding binding;
    private ProductPickupAddressRecyclerAdapter adapterAddress;
    private ProductPickupAddressFragment pickupAddressFragment;
    private List<AddressInformation> addressInformationList;
    private AddressInformation addressInformation;
    private UserSessionManager session;
    private int selected = -1;
    private File file;
    private Uri proofUri;
    private MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pickup_address);

        setSupportActionBar(binding.layoutToolbar.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");
        }

        binding.layoutToolbar.toolbarTitle.setText(getString(R.string.select_pickup_address));

        session = new UserSessionManager(getApplicationContext(), this);

        this.initProductPickupAddressRecyclerView(binding.pickupAddressList);
        this.getAddressInformation();
    }

    /**
     * Initialize pickup address list adapter
     *
     * @param recyclerView
     */
    private void initProductPickupAddressRecyclerView(RecyclerView recyclerView) {
        adapterAddress = new ProductPickupAddressRecyclerAdapter();
        recyclerView.setAdapter(adapterAddress);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Fetch pickup address information
     */
    private void getAddressInformation() {
        binding.pbLoading.setVisibility(View.VISIBLE);

        Constants.assuredPurchaseRestAdapterDev.create(ProductGalleryInterface.class)
                .getPickupAddress(session.getFPID(), new Callback<WebResponseModel<List<AddressInformation>>>() {

                    @Override
                    public void success(WebResponseModel<List<AddressInformation>> webResponseModel, Response response) {

                        binding.pbLoading.setVisibility(View.GONE);

                        if (webResponseModel != null && webResponseModel.getData() != null && webResponseModel.getData().size() > 0) {
                            binding.layoutEmptyView.setVisibility(View.GONE);
                            binding.btnSave.setVisibility(View.VISIBLE);
                            adapterAddress.setData(webResponseModel.getData());
                            return;

                            /*AddressInformation information = getAddress();

                            if(information != null)
                            {
                                changePickupAddressText(information);
                            }

                            else
                            {
                                binding.layoutBottomSheet.tvPickAddress.setText("");
                            }*/
                        }

                        if (adapterAddress.getItemCount() == 0) {
                            binding.layoutEmptyView.setVisibility(View.VISIBLE);
                        }

                        Log.d("PRODUCT_JSON", "GET ADDRESS");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        binding.pbLoading.setVisibility(View.GONE);
                        Methods.showSnackBarNegative(PickupAddressActivity.this, getString(R.string.something_went_wrong_try_again));
                        Log.d("PRODUCT_JSON", "GET ADDRESS FAIL");
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                break;

            case R.id.menu_add:

                openAddressDialog(null);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * open add/edit address dialog
     *
     * @param addressInformation
     */
    private void openAddressDialog(AddressInformation addressInformation) {
        /*if(addressInformation == null)
        {
            pickupAddressFragment = ProductPickupAddressFragment.newInstance();
        }

        else if(pickupAddressFragment == null)
        {
            pickupAddressFragment = ProductPickupAddressFragment.newInstance();
        }*/

        pickupAddressFragment = ProductPickupAddressFragment.newInstance();
        pickupAddressFragment.setAddress(addressInformation);
        pickupAddressFragment.isFileSelected(false);

        pickupAddressFragment.show(getSupportFragmentManager(), "Address");

        pickupAddressFragment.setOnClickListener(information -> saveAddressInformation(information));

        pickupAddressFragment.setFileChooserListener(new ProductPickupAddressFragment.OnFileChooser() {

            @Override
            public void openDialog() {

                chooseFile();
            }

            @Override
            public void onFileRemove() {

                file = null;
                pickupAddressFragment.isFileSelected(false);
            }
        });
    }

    /**
     * Save pickup address information
     *
     * @param information
     */
    private void saveAddressInformation(AddressInformation information) {
        if (!isValidAddress()) {
            return;
        }

        addressInformation = information;
        addressInformation.websiteId = session.getFPID();

        this.uploadFile(file);
    }

    /**
     * check if pickup address form is valid or not
     *
     * @return
     */
    private boolean isValidAddress() {
        if (file == null) {
            Toast.makeText(getApplicationContext(), R.string.address_proof_required, Toast.LENGTH_LONG).show();
            return false;
        }

        return pickupAddressFragment.isValid();
    }

    /**
     * File picker dialog
     */
    private void chooseFile() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.layout_file_upload_dialog, true)
                .show();

        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);

        View view = dialog.getCustomView();

        LinearLayout takeCamera = view.findViewById(R.id.cameraimage);
        LinearLayout takeGallery = view.findViewById(R.id.galleryimage);
        LinearLayout takeFile = view.findViewById(R.id.filepicker);

        ImageView cameraImg = view.findViewById(R.id.pop_up_camera_imag);
        ImageView galleryImg = view.findViewById(R.id.pop_up_gallery_img);
        ImageView fileImg = view.findViewById(R.id.pop_up_file_img);

        cameraImg.setColorFilter(whiteLabelFilter_pop_ip);
        galleryImg.setColorFilter(whiteLabelFilter_pop_ip);
        fileImg.setColorFilter(whiteLabelFilter_pop_ip);

        takeCamera.setOnClickListener(v -> {

            cameraIntent(CAMERA_PROOF_IMAGE_REQUEST_CODE);
            dialog.dismiss();
        });

        takeGallery.setOnClickListener(v -> {

            openImagePicker(GALLERY_PROOF_IMAGE_REQUEST_CODE, 1);
            dialog.dismiss();
        });

        takeFile.setOnClickListener(v -> {

            openFileChooser();
            dialog.dismiss();
        });
    }

    /**
     * Check camera permission
     *
     * @param requestCode
     */
    private void cameraIntent(int requestCode) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                    Methods.showApplicationPermissions(getString(R.string.camera_and_storage_permission), getString(R.string.we_need_this_permission_to_enable_capture_an), this);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                }
            } else {
                startCamera(requestCode);
            }
        } catch (ActivityNotFoundException e) {
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(this, errorMessage);
        }
    }

    /**
     * Start camera intent
     *
     * @param requestCode
     */
    private void startCamera(int requestCode) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "boost");
        Uri tempUri;

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }

        /**
         * Check if we're running on Android 5.0 or higher
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tempUri = FileProvider.getUriForFile(this,
                    Constants.PACKAGE_NAME + ".provider",
                    new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        } else {
            tempUri = Uri.fromFile(new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        }

        proofUri = tempUri;

        try {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
            startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.failed_to_open_camera, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Open file chooser activity
     */
    private void openFileChooser() {
        /*int limit = 1;

        Intent intent4 = new Intent(this, NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, limit);
        intent4.putExtra(NormalFilePickActivity.SUFFIX, FILE_EXTENSIONS);
        startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);*/

        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Toast.makeText(this, getString(R.string.allow_external_storage_reading), Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, FILE_PICKER_PERMISSION_REQUEST_CODE);
            }
        } else {
            new MaterialFilePicker()
                    .withActivity(this)
                    .withRequestCode(FILE_PROOF_REQUEST_CODE)
                    .withFilter(Pattern.compile(".*\\.pdf$")) // Filtering files and directories by file name using regexp
                    .withFilterDirectories(false) // Set directories filterable (false by default)
                    .withHiddenFiles(true) // Show hidden files and folders
                    .start();
        }
    }

    /**
     * Open image picker activity
     *
     * @param requestCode
     * @param max
     */
    private void openImagePicker(int requestCode, int max) {
        boolean folderMode = true;
        boolean multipleMode = true;

        ImagePicker.with(this)
                .setFolderMode(folderMode)
                .setShowCamera(false)
                .setFolderTitle("Album")
                .setMultipleMode(multipleMode)
                .setMaxSize(max)
                .setBackgroundColor("#212121")
                .setAlwaysShowDoneButton(true)
                .setRequestCode(requestCode)
                .setKeepScreenOn(true)
                .start();
    }

    /**
     * Upload pickup  address proof
     *
     * @param file
     */
    private void uploadFile(File file) {
        if (!Methods.isOnline(this)) {
            return;
        }

        String valuesStr;

        //If not proof not exists for address
        if (TextUtils.isEmpty(addressInformation.addressProof)) {
            valuesStr = "clientId=" + Constants.clientId
                    + "&requestType=sequential"
                    + "&totalChunks=1&currentChunkNumber=1&fileName=" + file.getName();
        }

        //If proof already exists replace address proof
        else {
            String url = addressInformation.addressProof;
            String fileName = url.substring(url.lastIndexOf('/') + 1);

            valuesStr = "clientId=" + Constants.clientId
                    + "&requestType=sequential"
                    + "&totalChunks=1&currentChunkNumber=1&fileName=" + file.getName() + "&proofFileId=" + fileName;
        }

        String url = DEV_ASSURED_PURCHASE_URL + "/api/seller/UploadOrReplaceFile?" + valuesStr;

        //Call file upload
        FileUpload upload = new FileUpload(file);
        upload.setFileUploadListener(this);
        upload.execute(url);
    }

    @Override
    public void onSuccess(String url) {

        Log.d("PRODUCT_JSON", "URL - " + url);

        //save address once address proof uploaded
        addressInformation.addressProof = url;
        saveAddress();
    }

    @Override
    public void onFailure() {

        Log.d("PRODUCT_JSON", "FAILURE");
        Toast.makeText(getApplicationContext(), R.string.failed_to_upload_address_proof, Toast.LENGTH_LONG).show();
        hideDialog();
    }

    @Override
    public void onPreUpload() {

        Log.d("PRODUCT_JSON", "PREUPLOAD");
        showDialog(getString(R.string.please_wait_));
    }

    /**
     * Save pickup address
     */
    private void saveAddress() {
        if (!Methods.isOnline(this)) {
            return;
        }

        Constants.assuredPurchaseRestAdapterDev.create(ProductGalleryInterface.class)
                .savePickupAddress(addressInformation, new Callback<WebResponseModel<AddressInformation>>() {

                    @Override
                    public void success(WebResponseModel<AddressInformation> webResponseModel, Response response) {

                        hideDialog();

                        if (webResponseModel != null && webResponseModel.getData() != null) {
                            AddressInformation addressResponse = webResponseModel.getData();

                            if (TextUtils.isEmpty(addressInformation.id)) {
                                //If new address added then add it locally to address list
                                adapterAddress.addData(addressResponse);
                                Toast.makeText(getApplicationContext(), getString(R.string.address_addded_successfully), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent();
                                intent.putExtra("ADDRESS", addressResponse);
                                setResult(RESULT_OK, intent);

                                finish();
                            } else {
                                //If address updated the update it locally to address list
                                for (int i = 0; i < addressInformationList.size(); i++) {
                                    if (addressInformation.id.equals(addressInformationList.get(i).id)) {
                                        addressInformationList.add(i, addressResponse);
                                        adapterAddress.notifyItemChanged(i);
                                        break;
                                    }
                                }

                                Toast.makeText(getApplicationContext(), getString(R.string.address_updated_successfully), Toast.LENGTH_LONG).show();
                            }

                            //product.pickupAddressReferenceId = webResponseModel.getData().id;
                            //changePickupAddressText(addressResponse);
                            addressInformation.id = webResponseModel.getData().id;
                        }

                        Log.d("PRODUCT_JSON", getString(R.string.address_successfully_added_updated));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideDialog();
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_save_address), Toast.LENGTH_LONG).show();
                        Log.d("PRODUCT_JSON", "FAIL " + error.getMessage() + " CODE " + error.getSuccessType());
                    }
                });
    }

    /**
     * Hide progress bar
     */
    private void hideDialog() {
        if (materialDialog != null && materialDialog.isShowing()) {
            materialDialog.dismiss();
        }
    }

    /**
     * Show progress bar
     *
     * @param content
     */
    private void showDialog(String content) {
        initProgressDialog(content);

        if (!materialDialog.isShowing()) {
            materialDialog.show();
        }
    }

    /**
     * Initialize progress bar
     *
     * @param content
     */
    private void initProgressDialog(String content) {
        if (materialDialog != null) {
            materialDialog.setContent(content);
            return;
        }

        materialDialog = new MaterialDialog.Builder(this)
                .widgetColorRes(R.color.accentColor)
                .content(content)
                .progress(true, 0).build();

        materialDialog.setCancelable(false);
    }

    public void onAddNewAddress(View view) {
        openAddressDialog(null);
    }

    public void onSaveAddress(View view) {
        if (selected == -1) {
            Toast.makeText(getApplicationContext(), getString(R.string.please_choose_pickup_address), Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("ADDRESS", addressInformationList.get(selected));
        setResult(RESULT_OK, intent);

        finish();
    }

    /**
     * Activity result for CAMERA, IMAGE GALLERY AND FILE PICKER
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == GALLERY_PROOF_IMAGE_REQUEST_CODE && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);

            if (images.size() > 0) {
                file = new File(images.get(0).getPath());

                pickupAddressFragment.setFileName(file.getName());
                pickupAddressFragment.isFileSelected(true);
            }
        } else if (resultCode == RESULT_OK && requestCode == CAMERA_PROOF_IMAGE_REQUEST_CODE) {
            file = new File(proofUri.getPath());

            pickupAddressFragment.setFileName(file.getName());
            pickupAddressFragment.isFileSelected(true);

        }

        if (requestCode == FILE_PROOF_REQUEST_CODE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            if (!TextUtils.isEmpty(filePath)) {
                file = new File(filePath);

                pickupAddressFragment.setFileName(file.getName());
                pickupAddressFragment.isFileSelected(true);
            }
        }

        /*else if(requestCode == Constant.REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null)
        {
            ArrayList<NormalFile> files = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);

            if(files.size() > 0)
            {
                file = new File(files.get(0).getPath());

                pickupAddressFragment.setFileName(file.getName());
                pickupAddressFragment.isFileSelected(true);
            }
        }*/
    }

    /**
     * Product Pickup Address Dynamic Input Filed
     */
    class ProductPickupAddressRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private CompoundButton lastCheckedRB = null;
        private CompoundButton.OnCheckedChangeListener ls = (buttonView, isChecked) ->
        {
            int tag = (int) buttonView.getTag();

            if (lastCheckedRB == null) {
                lastCheckedRB = buttonView;
            } else if (tag != (int) lastCheckedRB.getTag()) {
                lastCheckedRB.setChecked(false);
                lastCheckedRB = buttonView;
            }

            selected = (int) lastCheckedRB.getTag();
        };

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_pickup_address_v1, viewGroup, false);
            return new ProductPickupAddressRecyclerAdapter.ProductPickupAddressViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
            if (holder instanceof ProductPickupAddressRecyclerAdapter.ProductPickupAddressViewHolder) {
                final ProductPickupAddressViewHolder viewHolder = (ProductPickupAddressViewHolder) holder;

                AddressInformation addressInformation = addressInformationList.get(i);

                viewHolder.tvType.setText(addressInformation.areaName != null ? addressInformation.areaName : "");
                viewHolder.tvAddress.setText(addressInformation.toString());
                viewHolder.tvContactNumber.setText(addressInformation.contactNumber != null ? addressInformation.contactNumber : "");

                viewHolder.btnEdit.setTag(i);
                viewHolder.radioChoose.setTag(i);
                viewHolder.radioChoose.setOnCheckedChangeListener(ls);

                if (!TextUtils.isEmpty(getIntent().getStringExtra("ADDRESS_ID"))
                        && addressInformation.id.equals(getIntent().getStringExtra("ADDRESS_ID"))) {
                    viewHolder.radioChoose.setChecked(true);
                    viewHolder.layoutAddressTitle.setBackgroundResource(R.drawable.white_rectangle_background);
                    selected = i;
                } else {
                    viewHolder.radioChoose.setChecked(false);
                    viewHolder.layoutAddressTitle.setBackgroundResource(R.drawable.grey_rectangle_background);
                }
            }
        }

        @Override
        public int getItemCount() {
            return addressInformationList == null ? 0 : addressInformationList.size();
        }

        public void setData(List<AddressInformation> informationList) {
            if (addressInformationList == null) {
                addressInformationList = new ArrayList<>();
            }

            addressInformationList.clear();
            addressInformationList.addAll(informationList);
            notifyDataSetChanged();
        }

        public void addData(AddressInformation information) {
            if (addressInformationList == null) {
                addressInformationList = new ArrayList<>();
            }

            addressInformationList.add(information);
            notifyItemInserted(addressInformationList.size());
        }

        class ProductPickupAddressViewHolder extends RecyclerView.ViewHolder {
            TextView tvType;
            TextView tvAddress;
            TextView tvContactNumber;
            Button btnEdit;
            RadioButton radioChoose;
            RelativeLayout layoutAddressTitle;

            private ProductPickupAddressViewHolder(View itemView) {
                super(itemView);

                tvType = itemView.findViewById(R.id.tv_address_type);
                tvAddress = itemView.findViewById(R.id.tv_pick_address);
                tvContactNumber = itemView.findViewById(R.id.tv_mobile_number);
                btnEdit = itemView.findViewById(R.id.btn_edit);
                radioChoose = itemView.findViewById(R.id.radio_choose);
                layoutAddressTitle = itemView.findViewById(R.id.layout_address_title);

                btnEdit.setOnClickListener(v -> {

                    addressInformation = addressInformationList.get(getAdapterPosition());
                    openAddressDialog(addressInformation);
                });

                itemView.setOnClickListener(v -> {

                    //AddressInformation information = addressInformationList.get(getAdapterPosition());
                    //product.pickupAddressReferenceId = information.id;
                    //changePickupAddressText(information);
                    //sheetBehaviorAddress.setState(BottomSheetBehavior.STATE_COLLAPSED);
                });
            }
        }
    }
}