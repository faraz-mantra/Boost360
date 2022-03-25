package com.appservice.ui.business_logo

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.databinding.ActivityBusinessLogoV2Binding
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.BusinessLogoViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.framework.analytics.SentryController
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences
import com.framework.utils.FileUtils
import com.framework.utils.FileUtils.saveBitmap
import com.framework.utils.showSnackBarNegative
import com.framework.utils.spanBold
import com.framework.webengageconstant.*
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.Exception
import java.net.URL

class BusinessLogoActivityV2 : AppBaseActivity<ActivityBusinessLogoV2Binding, BusinessLogoViewModel>() {


    private var iconUrl: String?=null
    var path: String? = null

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.logoimageView,binding?.addLogoButton)
        setSupportActionBar(binding?.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = ""
        }

        binding?.tvInst1?.text = spanBold(getString(R.string.business_logo_instructions_1),"jpeg, .png or .gif","(400x400 px)","500 kb")
        binding?.tvBuildMyLogo?.text = spanBold(getString(R.string.free_1_logo_png_powered_by_buildmylogo),
            "BuildMyLogo")

        WebEngageController.trackEvent(EVENT_NAME_BUSINESS_PROFILE, PAGE_VIEW, session.fpTag)
        iconUrl = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl)
        loadImage()

    }


    fun openImagePicker() {
        val filterSheet = ImagePickerBottomSheet()
        filterSheet.isHidePdf(true)
        filterSheet.onClicked = { launchPicker(it) }
        filterSheet.show(supportFragmentManager, ImagePickerBottomSheet::class.java.name)
    }

    fun launchPicker(clickType: ClickType) {
        WebEngageController.trackEvent(
            UPLOAD_LOGO,
            UPDATED_BUINSESS_LOGO,
            session.fpTag
        )
        if (clickType==ClickType.CAMERA){
            ImagePicker.with(this).cameraOnly().start(PICK_FROM_CAMERA)
        }else{
            ImagePicker.with(this).galleryOnly().start(PICK_FROM_GALLERY)

        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when(v){
            binding?.logoimageView->{
                if (path!=null){
                    editImage()
                }
            }
            binding?.addLogoButton->{
                openImagePicker()
            }
        }
    }


    private fun loadImage() {
        try {
            binding?.addLogoButton?.text = resources.getString(R.string.change_logo)
            if (iconUrl != null && iconUrl?.length?:0 > 0 && iconUrl?.contains("http")?.not() == true) {
                Glide.with(this).asGif().load(iconUrl)
                    .apply(RequestOptions().placeholder(R.drawable.logo_default_image)).into(
                        binding?.logoimageView!!
                    )
                onBusinessLogoAddedOrUpdated(true)
                saveUrlToPath()
            } else {
                if (iconUrl != null && iconUrl?.length?:0 > 0) {
                    Glide.with(this).load(iconUrl)
                        .apply(RequestOptions().placeholder(R.drawable.logo_default_image)).into(
                            binding?.logoimageView!!
                        )
                    saveUrlToPath()
                    onBusinessLogoAddedOrUpdated(true)
                } else {
                    Glide.with(this)
                        .load(R.drawable.logo_default_image).into(binding?.logoimageView!!)
                    onBusinessLogoAddedOrUpdated(false)
                    binding?.addLogoButton?.text = resources.getString(R.string.add_logo)
                }
            }
        } catch (e: Exception) {
            SentryController.captureException(e)
            e.printStackTrace()
            System.gc()
        }
    }

    private fun saveUrlToPath() {
      path = Glide.with(this).asBitmap().load(iconUrl).submit().get().saveBitmap(this).path

    }





    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_business__logo, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        return super.onOptionsItemSelected(item)
    }





    private fun onBusinessLogoAddedOrUpdated(isAdded: Boolean) {
        val instance = FirestoreManager
        if (instance.getDrScoreData() != null && instance.getDrScoreData()!!.metricdetail != null) {
            instance.getDrScoreData()!!.metricdetail?.boolean_add_clinic_logo = isAdded
            instance.updateDocument()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK && (PICK_FROM_CAMERA == requestCode || PICK_FROM_GALLERY == requestCode)) {
                try {
                    path = FileUtils.saveFile(data?.data!!,getExternalFilesDir(null)?.path
                            ,"temp_img.jpeg")?.path

                } catch (e: Exception) {
                    SentryController.captureException(e)
                    e.printStackTrace()
                    //  Util.toast("Uh oh. Something went wrong. Please try again", this);
                } catch (E: OutOfMemoryError) {
                    //Log.d("ANDRO_ASYNC",String.format("catch Out Of Memory error"));
                    E.printStackTrace()
                    System.gc()
                    // Util.toast("Uh oh. Something went wrong. Please try again", this);
                }
                if (path.isNullOrEmpty().not()) {
                    editImage()
                } else showSnackBarNegative(
                    this,
                    resources.getString(R.string.image_upload_failed)
                )
            } else if (resultCode == Activity.RESULT_OK && requestCode == ACTION_REQUEST_IMAGE_EDIT) {
                val path = data?.getStringExtra("edit_image")
                if (!TextUtils.isEmpty(path)) {
                    this.path = path
                    WebEngageController.trackEvent(BUSINESS_LOGO_ADDED, ADDED, session.fpTag)
                    uploadPrimaryPicture(path)
                }
            }
        } catch (e: Exception) {
            SentryController.captureException(e)
            e.printStackTrace()
        }
    }

    private fun editImage() {
        val `in` = Intent(this, Class.forName("com.nowfloats.NavigationDrawer.EditImageActivity"))
        `in`.putExtra("image", path)
        `in`.putExtra("isFixedAspectRatio", true)
        startActivityForResult(`in`, ACTION_REQUEST_IMAGE_EDIT)
    }

    fun uploadPrimaryPicture(path: String?) {


        if (path!=null){
            val imgFile = File(path)
            if (imgFile.exists()){
                showProgress()
                binding?.addLogoButton?.text = resources.getString(R.string.change)
                // AlertArchive(Constants.alertInterface, "LOGO", session.getFPID())
                viewModel.archieveAlert(session.fPID,"LOGO")
                viewModel.putUploadImageBusiness(session.fPID,imgFile.name,
                    imgFile.asRequestBody()).observe(this){
                    hideProgress()
                    if (it.isSuccess()){
                        iconUrl =it.stringResponse
                        session?.storeFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl,iconUrl )
                        loadImage()
                        imageUpload(it.isSuccess())

                    }
                }
            }
        }


    }

    private fun imageUpload(isSuccess: Boolean) {
        if (isSuccess) {
            onBusinessLogoAddedOrUpdated(true)
        }
    }


    companion object {
        private const val PICK_FROM_CAMERA = 1
        private const val PICK_FROM_GALLERY = 2
        private const val ACTION_REQUEST_IMAGE_EDIT = 3
    }

    override fun getLayout(): Int {
        return R.layout.activity_business_logo_v2
    }

    override fun getViewModelClass(): Class<BusinessLogoViewModel> {
        return BusinessLogoViewModel::class.java
    }
}