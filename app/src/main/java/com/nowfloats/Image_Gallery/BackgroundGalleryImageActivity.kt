package com.nowfloats.Image_Gallery

import android.Manifest
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.framework.analytics.SentryController.captureException
import com.framework.webengageconstant.*
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.nowfloats.BusinessProfile.UI.API.UploadFaviconImage
import com.nowfloats.BusinessProfile.UI.API.UploadFaviconImage.OnImageUpload
import com.nowfloats.Login.UserSessionManager
import com.nowfloats.NavigationDrawer.floating_view.ImagePickerBottomSheetDialog
import com.nowfloats.NavigationDrawer.floating_view.ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE
import com.nowfloats.NotificationCenter.AlertArchive
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util
import com.nowfloats.util.*
import com.squareup.picasso.Picasso
import com.thinksity.databinding.ActivityBackgroundImageGalleryBinding
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.io.File
import com.thinksity.R

import java.util.*

class BackgroundGalleryImageActivity : AppCompatActivity(), OnImageUpload {
    private val gallery_req_id = 0
    private val media_req_id = 1
    var values: ContentValues? = null
    var imageUri: Uri? = null
    private val CAMERA_PERMISSION_REQUEST_CODE = 1
    private val IMAGE_DELETE_REQUEST_CODE = 2
    private val CAMERA_IMAGE_REQUEST_CODE = 101
    private val GALLERY_IMAGE_REQUEST_CODE = 102
    var binding: ActivityBackgroundImageGalleryBinding? = null
    private var session: UserSessionManager? = null
    private var adapter: ImagesRecyclerAdapter? = null
    private val whiteLabelFilter: PorterDuffColorFilter? = null
    private var primaryUri: Uri? = null
    private var dialog: ProgressDialog? = null
    private var apiDataLoaded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.thinksity.R.layout.activity_background_image_gallery)
        setSupportActionBar(binding!!.appBar.toolbar)
        Methods.isOnline(this)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = ""
        }
        binding!!.appBar.toolbarTitle.setText(resources.getString(com.thinksity.R.string.background_image))
        session = UserSessionManager(applicationContext, this)
        initImageRecyclerView(binding!!.imageList)
        backgroundImages
        binding!!.btnAdd.setOnClickListener { view -> openImageChooser() }
    }

    override fun onResume() {
        super.onResume()
    }

    /**
     * Initialize pickup address list adapter
     *
     * @param recyclerView
     */
    private fun initImageRecyclerView(recyclerView: RecyclerView) {
        adapter = ImagesRecyclerAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private val backgroundImages: Unit
        private get() {
            apiDataLoaded = false
            binding!!.pbLoading.setVisibility(View.VISIBLE)
            val imageApi = Constants.restAdapter.create(ImageApi::class.java)
            imageApi.getBackgroundImages(session!!.fpid, Constants.clientId, object : Callback<List<String?>?> {
                override fun success(strings: List<String?>?, response: Response) {
                    binding!!.pbLoading.setVisibility(View.GONE)
                    apiDataLoaded = true
                    if (strings != null && strings.size > 0) {
                        adapter!!.setData(strings)
                    }
                }

                override fun failure(error: RetrofitError) {
                    binding!!.pbLoading.setVisibility(View.GONE)
                }
            })
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> finish()
            R.id.menu_add -> openImageChooser()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openImageChooser() {
//        if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
//            Methods.showFeatureNotAvailDialog(BackgroundImageGalleryActivity.this);
//            return;
//        }
        if (apiDataLoaded && adapter!!.images!!.size >= 8) {
            Toast.makeText(this, getString(R.string.cannot_upload_more_than_8), Toast.LENGTH_SHORT).show()
            return
        }
        val imagePickerBottomSheetDialog = ImagePickerBottomSheetDialog { image_click_type: IMAGE_CLICK_TYPE -> onClickImagePicker(image_click_type) }
        imagePickerBottomSheetDialog.show(supportFragmentManager, ImagePickerBottomSheetDialog::class.java.name)

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

    private fun onClickImagePicker(image_click_type: IMAGE_CLICK_TYPE) {
        if (image_click_type.name == IMAGE_CLICK_TYPE.CAMERA.name) {
            MixPanelController.track(EventKeysWL.UPDATE_LOGO_CAMERA, null)
            WebEngageController.trackEvent(UPLOAD_LOGO, UPDATED_BUINSESS_LOGO, session!!.fpTag)
            cameraIntent()
        } else if (image_click_type.name == IMAGE_CLICK_TYPE.GALLERY.name) {
            MixPanelController.track(EventKeysWL.UPDATE_LOGO_GALLERY, null)
            galleryIntent()
        }
    }
    //  private void onClickImagePicker(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE image_click_type) {
    //    if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.CAMERA.name())) {
    //      MixPanelController.track(EventKeysWL.UPDATE_LOGO_CAMERA, null);
    //      cameraIntent(CAMERA_IMAGE_REQUEST_CODE);
    //    } else if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.GALLERY.name())) {
    //      MixPanelController.track(EventKeysWL.UPDATE_LOGO_GALLERY, null);
    //      galleryIntent();
    ////            openImagePicker(GALLERY_IMAGE_REQUEST_CODE, 1);
    //    }
    //  }
    /**
     * Check camera permission
     *
     *
     * //   * @param requestCode
     */
    //  private void cameraIntent(int requestCode) {
    //    try {
    //      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
    //          PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
    //          PackageManager.PERMISSION_GRANTED) {
    //
    //        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
    //            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
    //
    //          Methods.showApplicationPermissions(getString(R.string.camera_and_storage_permission), getString(R.string.we_need_this_permission), this);
    //        } else {
    //          ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    //        }
    //      } else {
    //        startCamera(requestCode);
    //      }
    //    } catch (ActivityNotFoundException e) {
    //      SentryController.INSTANCE.captureException(e);
    //      String errorMessage = getString(R.string.device_does_not_support_capturing_image);
    //      Methods.showSnackBarNegative(this, errorMessage);
    //    }
    //  }
    fun cameraIntent() {
        try {
            // use standard intent to capture an image
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                        media_req_id)
                return
            }
            values = ContentValues()
            values!!.put(MediaStore.Images.Media.TITLE, "New Picture")
            values!!.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
            imageUri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val captureIntent = Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE)
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            // we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, CAMERA_PHOTO)
        } catch (anfe: ActivityNotFoundException) {
            captureException(anfe)
            // display an error message
            val errorMessage = resources.getString(R.string.device_does_not_support_capturing_image)
            Methods.showSnackBarNegative(this, errorMessage)
        } catch (e: Exception) {
            captureException(e)
            e.printStackTrace()
        }
    }

    fun galleryIntent() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        gallery_req_id)
                return
            }
            val i = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, Constants.GALLERY_PHOTO)
        } catch (anfe: ActivityNotFoundException) {
            captureException(anfe)
            // display an error message
            val errorMessage = resources.getString(R.string.device_does_not_support_capturing_image)
            Methods.showSnackBarNegative(this, errorMessage)
        }
    }

    /**
     * Start camera intent
     *
     * @param requestCode
     */
    private fun startCamera(requestCode: Int) {
        val mediaStorageDir = File(Environment.getExternalStorageDirectory().absolutePath, "boost")
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir()
        }
        /**
         * Check if we're running on Android 5.0 or higher
         */
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        primaryUri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, primaryUri)
            startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            captureException(e)
            Toast.makeText(applicationContext, R.string.failed_to_open_camera, Toast.LENGTH_LONG).show()
        }
    }

    private fun editImage() {
        val `in` = Intent(this, EditImageBackgroundActivity::class.java)
        `in`.putExtra("image", path)
        `in`.putExtra("isFixedAspectRatio", true)
        startActivityForResult(`in`, ACTION_REQUEST_IMAGE_EDIT)
    }

    var CameraBitmap: Bitmap? = null
    var path: String? = null
    var imageUrl = ""
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == RESULT_OK && requestCode == CAMERA_PHOTO) {
                try {
                    CameraBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    imageUrl = Methods.getRealPathFromURI(this, imageUri)
                    path = imageUrl
                    path = Util.saveBitmap(path, this, "ImageFloat" + System.currentTimeMillis())
                } catch (e: Exception) {
                    captureException(e)
                    e.printStackTrace()
                    //  Util.toast("Uh oh. Something went wrong. Please try again", this);
                } catch (E: OutOfMemoryError) {
                    //Log.d("ANDRO_ASYNC",String.format("catch Out Of Memory error"));
                    E.printStackTrace()
                    System.gc()
                    // Util.toast("Uh oh. Something went wrong. Please try again", this);
                }
                if (!Util.isNullOrEmpty(path)) {
                    editImage()
                } else Methods.showSnackBarNegative(this, resources.getString(R.string.select_image_upload))
            }
            if (resultCode == RESULT_OK && requestCode == Constants.GALLERY_PHOTO) {
                val picUri = data!!.data
                if (picUri != null) {
                    path = Methods.getPath(this, picUri)
                    path = Util.saveBitmap(path, this, "ImageFloat" + System.currentTimeMillis())
                    if (!Util.isNullOrEmpty(path)) {
                        editImage()
                    } else Methods.showSnackBarNegative(this, resources.getString(R.string.select_image_upload))
                }
                //            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
//            if (images.size() > 0) {
//                File file = new File(images.get(0).getPath());
//                uploadPrimaryPicture(file.getPath());
//            }
                WebEngageController.trackEvent(UPLOAD_BACKGROUND_IMAGE, UPDATE_BACKGROUND_IMAGE, session!!.fpTag)
            }
            if (resultCode == RESULT_OK && requestCode == IMAGE_DELETE_REQUEST_CODE && data != null) {
                val position = data.getIntExtra("POSITION", 0)
                adapter!!.removeImage(position)
                WebEngageController.trackEvent(DELETE_BACKGROUND_IMAGE, EVENT_LABEL_DELETE_BACKGROUND_IMAGE, session!!.fpTag)
            } else if (resultCode == RESULT_OK && requestCode == ACTION_REQUEST_IMAGE_EDIT) {
                val path = data!!.getStringExtra("edit_image")
                if (!TextUtils.isEmpty(path)) {
                    this.path = path
                    WebEngageController.trackEvent(BUSINESS_LOGO_ADDED, ADDED, session!!.fpTag)
                    uploadPrimaryPicture(path)
                }
            }
        } catch (e: Exception) {
            captureException(e)
            e.printStackTrace()
        }
    }

    private fun isFileLargeThan2Mb(path: String): Boolean {
        val sizeInMb = File(path).length() / 1048576
        return if (sizeInMb > 2) {
            true
        } else false
    }

    fun uploadPrimaryPicture(path: String?) {
        if (!Methods.isOnline(this)) {
            return
        }
        AlertArchive(Constants.alertInterface, "LOGO", session!!.fpid)
        var s_uuid = UUID.randomUUID().toString()
        s_uuid = s_uuid.replace("-", "")
        val uri: String
        val param = "createBackgroundImage"
        uri = Constants.LoadStoreURI +
                param + "?clientId=" +
                Constants.clientId +
                "&fpId=" + session!!.fpid +
                "&reqType=sequential&reqtId=" +
                s_uuid + "&"
        val url = uri + "totalChunks=1&currentChunkNumber=1"
        val upload = UploadFaviconImage(path, url)
        upload.setUploadListener(this)
        upload.execute()
    }

    override fun onPreUpload() {
        apiDataLoaded = false
        dialog = ProgressDialog.show(this, "", getString(com.thinksity.R.string.uploadin_image))
        dialog!!.setCancelable(false)
    }

    override fun onPostUpload(isSuccess: Boolean, response: String) {
        apiDataLoaded = true
        if (isSuccess) {
            Methods.showSnackBarPositive(this, getString(com.thinksity.R.string.image_added_successfully))
            val url = response.replace("\\", "").replace("\"", "")
            adapter!!.addImage(url)
            session!!.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE, response.replace("\\", "").replace("\"", ""))
        } else {
            Methods.showSnackBarNegative(this, getString(com.thinksity.R.string.failed_to_upload_image))
        }
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    /**
     * Open image picker activity
     *
     * @param requestCode
     * @param max
     */
    private fun openImagePicker(requestCode: Int, max: Int) {
        val folderMode = true
        val multipleMode = true
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
                .start()
    }
    //  public void galleryIntent() {
    //    try {
    //      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
    //        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, gallery_req_id);
    //        return;
    //      }
    //
    //      Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    //      startActivityForResult(i, GALLERY_IMAGE_REQUEST_CODE);
    //    } catch (ActivityNotFoundException anfe) {
    //      SentryController.INSTANCE.captureException(anfe);
    //      String errorMessage = getResources().getString(R.string.device_does_not_support_capturing_image);
    //      Methods.showSnackBarNegative(BackgroundImageGalleryActivity.this, errorMessage);
    //    }
    //  }
    /**
     * Product Pickup Address Dynamic Input Filed
     */
    internal inner class ImagesRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
        var images: MutableList<String?>? = ArrayList()
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
            val view: View = LayoutInflater.from(viewGroup.context).inflate(com.thinksity.R.layout.recyclerview_background_images, viewGroup, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
            if (holder is ImageViewHolder) {
                val url = images!![i]
                try {
                    if (!TextUtils.isEmpty(url)) {
                        Picasso.get().load(url).into(holder.imageView)
                    }
                } catch (e: Exception) {
                    captureException(e)
                    e.printStackTrace()
                }
            }
        }

        override fun getItemCount(): Int {
            return if (images == null) 0 else images!!.size
        }

        fun setData(images: List<String?>?) {
            this.images!!.clear()
            this.images!!.addAll(images!!)
            notifyDataSetChanged()
        }

        fun addImage(url: String) {
            images!!.add(0, url)
            notifyDataSetChanged()
        }

        fun removeImage(position: Int) {
            images!!.removeAt(position)
            notifyDataSetChanged()
        }

        internal inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var imageView: ImageView

            init {
                imageView = itemView.findViewById(R.id.image)
                itemView.setOnClickListener { v: View? ->
                    val array = arrayOfNulls<String>(images!!.size)
//                    images.addAll(array)
                    val intent = Intent(applicationContext, ImageViewerActivity::class.java)
                    intent.putExtra("POSITION", adapterPosition)
                    intent.putExtra("IMAGES", array)
                    startActivityForResult(intent, IMAGE_DELETE_REQUEST_CODE)
                }
            }
        }
    }

    companion object {
        private const val CAMERA_PHOTO = 1
        private const val TAG = "BackgroundImageGalleryA"
        private const val ACTION_REQUEST_IMAGE_EDIT = 3
    }
}