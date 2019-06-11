package com.nowfloats.Image_Gallery;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.nowfloats.NotificationCenter.AlertArchive;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
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


public class BackgroundImageGalleryActivity extends AppCompatActivity implements UploadFaviconImage.OnImageUpload
{

    ActivityBackgroundImageGalleryBinding binding;
    private UserSessionManager session;
    private ImagesRecyclerAdapter adapter;
    private PorterDuffColorFilter whiteLabelFilter;
    private Uri primaryUri;
    private ProgressDialog dialog;

    private final int CAMERA_PERMISSION_REQUEST_CODE = 1;
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
        //getBackgroundImages();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        getBackgroundImages();
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
                        Picasso.with(BackgroundImageGalleryActivity.this).load(url).into(viewHolder.imageView);
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
                    startActivity(intent);
                });
            }
        }

        public void setData(List<String> images)
        {
            this.images.clear();
            this.images.addAll(images);
            notifyDataSetChanged();
        }

        public void addImage(String url)
        {
            this.images.add(0, url);
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

        final MaterialDialog dialog = new MaterialDialog.Builder(BackgroundImageGalleryActivity.this)
                .customView(R.layout.featuredimage_popup,true)
                .show();

        View view = dialog.getCustomView();
        TextView title = view.findViewById(R.id.textview_heading);
        title.setText(getResources().getString(R.string.upload_image));
        LinearLayout takeCamera = view.findViewById(R.id.cameraimage);
        LinearLayout takeGallery = view.findViewById(R.id.galleryimage);
        ImageView   cameraImg = view.findViewById(R.id.pop_up_camera_imag);
        ImageView galleryImg = view.findViewById(R.id.pop_up_gallery_img);
        cameraImg.setColorFilter(whiteLabelFilter);
        galleryImg.setColorFilter(whiteLabelFilter);

        takeCamera.setOnClickListener(v -> {

            cameraIntent(CAMERA_IMAGE_REQUEST_CODE);
            dialog.dismiss();
        });

        takeGallery.setOnClickListener(v -> {

            openImagePicker(GALLERY_IMAGE_REQUEST_CODE, 1);
            dialog.dismiss();
        });
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

                    Methods.showApplicationPermissions("Camera And Storage Permission", "We need these permission to enable capture and upload images", this);
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
            Toast.makeText(getApplicationContext(), "Failed to Open Camera", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == CAMERA_IMAGE_REQUEST_CODE)
        {
            Log.d("CAMERA_IMAGEPATH", "" + primaryUri.getPath());
            uploadPrimaryPicture(primaryUri.getPath());
        }

        if (resultCode == RESULT_OK && requestCode == GALLERY_IMAGE_REQUEST_CODE && data != null)
        {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);

            if(images.size() > 0)
            {
                File file = new File(images.get(0).getPath());
                uploadPrimaryPicture(file.getPath());
            }
        }
    }


    public void uploadPrimaryPicture(String path)
    {
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

        dialog = ProgressDialog.show(this, "", "Uploading image...");
        dialog.setCancelable(false);
    }

    @Override
    public void onPostUpload(boolean isSuccess, String response) {

        if(isSuccess)
        {
            Methods.showSnackBarPositive(this, "Image updated successfully");

            String url = response.replace("\\", "").replace("\"", "");
            adapter.addImage(url);
        }

        else
        {
            Methods.showSnackBarNegative(this, "Failed to Upload Image");
        }

        if(dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }

        Log.d("onPostUpload", "" + isSuccess);
        Log.d("onPostUpload", "" + response);
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