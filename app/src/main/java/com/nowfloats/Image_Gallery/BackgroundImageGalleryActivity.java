package com.nowfloats.Image_Gallery;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.nowfloats.BusinessProfile.UI.API.UploadFaviconImage;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.floating_view.ImagePickerBottomSheetDialog;
import com.nowfloats.NotificationCenter.AlertArchive;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.squareup.picasso.Picasso;
import com.thinksity.R;
import com.thinksity.databinding.ActivityBackgroundImageGalleryBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_DELETE_BACKGROUND_IMAGE;
import static com.framework.webengageconstant.EventLabelKt.UPDATE_BACKGROUND_IMAGE;
import static com.framework.webengageconstant.EventNameKt.DELETE_BACKGROUND_IMAGE;
import static com.framework.webengageconstant.EventNameKt.UPLOAD_BACKGROUND_IMAGE;


public class BackgroundImageGalleryActivity extends AppCompatActivity implements UploadFaviconImage.OnImageUpload
{

    ActivityBackgroundImageGalleryBinding binding;
    private UserSessionManager session;
    private ImagesRecyclerAdapter adapter;
    private PorterDuffColorFilter whiteLabelFilter;
    private Uri primaryUri;
    private ProgressDialog dialog;

    private final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private final int IMAGE_DELETE_REQUEST_CODE = 2;
    private final int CAMERA_IMAGE_REQUEST_CODE = 101;
    private final int GALLERY_IMAGE_REQUEST_CODE = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_background_image_gallery);

        setSupportActionBar(binding.appBar.toolbar);
        Methods.isOnline(BackgroundImageGalleryActivity.this);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("");
        }

        binding.appBar.toolbarTitle.setText(getResources().getString(R.string.background_image));

        session = new UserSessionManager(getApplicationContext(), BackgroundImageGalleryActivity.this);

        initImageRecyclerView(binding.imageList);
        getBackgroundImages();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }
    /**
     * Initialize pickup address list adapter
     * @param recyclerView
     */
    private void initImageRecyclerView(RecyclerView recyclerView)
    {
        adapter = new ImagesRecyclerAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getBackgroundImages()
    {
        binding.pbLoading.setVisibility(View.VISIBLE);

        ImageApi imageApi = Constants.restAdapter.create(ImageApi.class);

        imageApi.getBackgroundImages(session.getFPID(), Constants.clientId, new Callback<List<String>>() {

            @Override
            public void success(List<String> strings, Response response) {

                binding.pbLoading.setVisibility(View.GONE);

                if(strings != null && strings.size() >0)
                {
                    adapter.setData(strings);
                }
            }

            @Override
            public void failure(RetrofitError error) {

                binding.pbLoading.setVisibility(View.GONE);
            }
        });
    }


    /**
     * Product Pickup Address Dynamic Input Filed
     */
    class ImagesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        List<String> images = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_background_images, viewGroup, false);
            return new ImagesRecyclerAdapter.ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i)
        {
            if (holder instanceof ImagesRecyclerAdapter.ImageViewHolder)
            {
                final ImageViewHolder viewHolder = (ImageViewHolder) holder;

                String url = images.get(i);

                try
                {
                    if(!TextUtils.isEmpty(url))
                    {
                        Picasso.get().load(url).into(viewHolder.imageView);
                    }
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getItemCount()
        {
            return images == null ? 0 : images.size();
        }

        class ImageViewHolder extends RecyclerView.ViewHolder
        {
            ImageView imageView;

            private ImageViewHolder(View itemView)
            {
                super(itemView);

                imageView = itemView.findViewById(R.id.image);

                itemView.setOnClickListener(v -> {

                    String[] array = new String[images.size()];
                    images.toArray(array);

                    Intent intent = new Intent(BackgroundImageGalleryActivity.this, ImageViewerActivity.class);
                    intent.putExtra("POSITION", getAdapterPosition());
                    intent.putExtra("IMAGES", array);
                    startActivityForResult(intent, IMAGE_DELETE_REQUEST_CODE);
                });
            }
        }

        public void setData(List<String> images)
        {
            this.images.clear();
            this.images.addAll(images);
            notifyDataSetChanged();
        }

        private void addImage(String url)
        {
            this.images.add(0, url);
            notifyDataSetChanged();
        }

        private void removeImage(int position)
        {
            this.images.remove(position);
            notifyDataSetChanged();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                finish();
                break;

            case R.id.menu_add:

                openImageChooser();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openImageChooser()
    {
        if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1"))
        {
            Methods.showFeatureNotAvailDialog(BackgroundImageGalleryActivity.this);
            return;
        }

        final ImagePickerBottomSheetDialog imagePickerBottomSheetDialog = new ImagePickerBottomSheetDialog(this::onClickImagePicker);
        imagePickerBottomSheetDialog.show(getSupportFragmentManager(), ImagePickerBottomSheetDialog.class.getName());

//        final MaterialDialog dialog = new MaterialDialog.Builder(BackgroundImageGalleryActivity.this)
//                .customView(R.layout.featuredimage_popup,true)
//                .show();

//        View view = dialog.getCustomView();
//        TextView title = view.findViewById(R.id.textview_heading);
//        title.setText(getResources().getString(R.string.upload_background_image));
//        LinearLayout takeCamera = view.findViewById(R.id.cameraimage);
//        LinearLayout takeGallery = view.findViewById(R.id.galleryimage);
//        ImageView   cameraImg = view.findViewById(R.id.pop_up_camera_imag);
//        ImageView galleryImg = view.findViewById(R.id.pop_up_gallery_img);
//        cameraImg.setColorFilter(whiteLabelFilter);
//        galleryImg.setColorFilter(whiteLabelFilter);
//
//        takeCamera.setOnClickListener(v -> {
//
//            cameraIntent(CAMERA_IMAGE_REQUEST_CODE);
//            dialog.dismiss();
//        });
//
//        takeGallery.setOnClickListener(v -> {
//
//            openImagePicker(GALLERY_IMAGE_REQUEST_CODE, 1);
//            dialog.dismiss();
//        });
    }

    private void onClickImagePicker(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE image_click_type) {
        if(image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.CAMERA.name())){
            MixPanelController.track(EventKeysWL.UPDATE_LOGO_CAMERA,null);
            cameraIntent(CAMERA_IMAGE_REQUEST_CODE);
        }else if(image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.GALLERY.name())){
            MixPanelController.track(EventKeysWL.UPDATE_LOGO_GALLERY,null);
            openImagePicker(GALLERY_IMAGE_REQUEST_CODE, 1);
        }
    }


    /**
     * Check camera permission
     * @param requestCode
     */
    private void cameraIntent(int requestCode)
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                    Methods.showApplicationPermissions(getString(R.string.camera_and_storage_permission), getString(R.string.we_need_this_permission), this);
                }

                else
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                }
            }

            else
            {
                startCamera(requestCode);
            }
        }

        catch (ActivityNotFoundException e)
        {
            String errorMessage = getString(R.string.device_does_not_support_capturing_image);
            Methods.showSnackBarNegative(this, errorMessage);
        }
    }


    /**
     * Start camera intent
     * @param requestCode
     */
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
            primaryUri = FileProvider.getUriForFile(this,
                    Constants.PACKAGE_NAME + ".provider",
                    new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        }

        else
        {
            primaryUri = Uri.fromFile(new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        }

        try
        {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, primaryUri);
            startActivityForResult(intent, requestCode);
        }

        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), R.string.failed_to_open_camera, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_IMAGE_REQUEST_CODE) {
            uploadPrimaryPicture(primaryUri.getPath());
            WebEngageController.trackEvent(UPLOAD_BACKGROUND_IMAGE, UPDATE_BACKGROUND_IMAGE, session.getFpTag());
        }

        if (resultCode == RESULT_OK && requestCode == GALLERY_IMAGE_REQUEST_CODE && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);

            if (images.size() > 0) {
                File file = new File(images.get(0).getPath());
                uploadPrimaryPicture(file.getPath());
            }
            WebEngageController.trackEvent(UPLOAD_BACKGROUND_IMAGE, UPDATE_BACKGROUND_IMAGE, session.getFpTag());
        }

        if (resultCode == RESULT_OK && requestCode == IMAGE_DELETE_REQUEST_CODE && data != null) {
            int position = data.getIntExtra("POSITION", 0);
            adapter.removeImage(position);
            WebEngageController.trackEvent(DELETE_BACKGROUND_IMAGE, EVENT_LABEL_DELETE_BACKGROUND_IMAGE, session.getFpTag());
        }
    }


    public void uploadPrimaryPicture(String path)
    {
        if(!Methods.isOnline(BackgroundImageGalleryActivity.this))
        {
            return;
        }

        new AlertArchive(Constants.alertInterface,"LOGO",session.getFPID());

        String s_uuid = UUID.randomUUID().toString();
        s_uuid = s_uuid.replace("-", "");
        String uri;
        String param = "createBackgroundImage";
        uri = Constants.LoadStoreURI +
                param + "?clientId=" +
                Constants.clientId +
                "&fpId=" + session.getFPID() +
                "&reqType=sequential&reqtId=" +
                s_uuid + "&";

        String url = uri + "totalChunks=1&currentChunkNumber=1" ;

        UploadFaviconImage upload = new UploadFaviconImage(path, url);
        upload.setUploadListener(this);
        upload.execute();
    }

    @Override
    public void onPreUpload() {

        dialog = ProgressDialog.show(this, "", getString(R.string.uploadin_image));
        dialog.setCancelable(false);
    }

    @Override
    public void onPostUpload(boolean isSuccess, String response) {

        if(isSuccess)
        {
            Methods.showSnackBarPositive(this, getString(R.string.image_added_successfully));

            String url = response.replace("\\", "").replace("\"", "");
            adapter.addImage(url);

            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE, response.replace("\\", "").replace("\"", ""));
        }

        else
        {
            Methods.showSnackBarNegative(this, getString(R.string.failed_to_upload_image));
        }

        if(dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }


    /**
     * Open image picker activity
     * @param requestCode
     * @param max
     */
    private void openImagePicker(int requestCode, int max)
    {
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
}