package com.appservice.ui.bgImage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.constant.RecyclerViewItemType
import com.appservice.databinding.FragmentBackgroundImageBinding
import com.appservice.model.ImageData
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.BackgroundImageViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.imagepicker.ImagePicker
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.convertListObjToString
import com.framework.webengageconstant.*

class BackgroundImageFragment : AppBaseFragment<FragmentBackgroundImageBinding, BackgroundImageViewModel>(), RecyclerItemClickListener {

  private var listImages: ArrayList<ImageData>? = null
  private var adapterImage: AppBaseRecyclerViewAdapter<ImageData>? = null
  private var isShowProgress: Boolean = true

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): BackgroundImageFragment {
      val fragment = BackgroundImageFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_background_image
  }

  override fun getViewModelClass(): Class<BackgroundImageViewModel> {
    return BackgroundImageViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(BACKGROUND_IMAGE_PAGE_LOAD, START_VIEW, sessionLocal.fpTag)
//    ImagePickerUtil.initLauncher(this)
    sessionLocal = UserSessionManager(baseActivity)
    setOnClickListener(binding?.btnDone)
    getBackgroundImages()
  }

  private fun getBackgroundImages() {
    if (isShowProgress) showProgress()
    viewModel?.getImages(sessionLocal.fPID, clientId)?.observeOnce(viewLifecycleOwner) { res ->
      if (res.isSuccess()) {
        val response = res.arrayResponse
        listImages = ArrayList()
        response?.forEach { listImages?.add(ImageData(it as? String, RecyclerViewItemType.BACKGROUND_IMAGE_RV.getLayout())) }
        uiVisibility()
        if (adapterImage == null) {
          binding?.imageList?.apply {
            adapterImage = AppBaseRecyclerViewAdapter(baseActivity, listImages!!, this@BackgroundImageFragment)
            adapter = adapterImage
          }
        } else adapterImage?.notify(listImages!!)
        onImageGalleryAddedOrUpdated(listImages.isNullOrEmpty().not())
      }
      isShowProgress = false
      hideProgress()
    }
  }

  private fun onImageGalleryAddedOrUpdated(isAdded: Boolean) {
    val instance = FirestoreManager
    if (instance.getDrScoreData() != null && instance.getDrScoreData()?.metricdetail != null) {
      instance.getDrScoreData()?.metricdetail?.boolean_image_uploaded_to_gallery = isAdded
      instance.updateDocument()
    }
  }

  private fun uiVisibility() {
    if (listImages.isNullOrEmpty()) {
      binding?.layoutDefaultImage?.visible()
      binding?.imageList?.gone()
    } else {
      binding?.layoutDefaultImage?.gone()
      binding?.imageList?.visible()
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.ON_CLICK_BACKGROUND_IMAGE.ordinal -> {
        startBackgroundActivity(
          FragmentType.BACKGROUND_IMAGE_F_SCREEN_FRAGMENT,
          Bundle().apply {
            putInt(BGImageFullScreenFragment.BK_IMAGE_POS, position)
            putString(BGImageFullScreenFragment.BK_IMAGE_LIST, convertListObjToString(listImages ?: arrayListOf()))
          }, isResult = true
        )
      }
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnDone -> openImagePicker()
    }
  }

  private fun openImagePicker() {
    //ImagePickerUtil.openPicker(this, object : ImagePickerUtil.Listener { override fun onFilePicked(filePath: String) { } })
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(baseActivity.supportFragmentManager, ImagePickerBottomSheet::class.java.name)
  }

  private fun openImagePicker(it: ClickType) {
    WebEngageController.trackEvent(UPLOAD_GALLERY_IMAGE, CLICK, NO_EVENT_VALUE)
    val type = if (it == ClickType.CAMERA) ImagePicker.Mode.CAMERA else ImagePicker.Mode.GALLERY
    ImagePicker.Builder(baseActivity).mode(type).compressLevel(ImagePicker.ComperesLevel.SOFT)
      .directory(ImagePicker.Directory.DEFAULT).extension(ImagePicker.Extension.PNG)
      .allowMultipleImages(false).enableDebuggingMode(true).build()
  }

  override fun showProgress(title: String?, cancelable: Boolean?) {
    binding?.pbLoading?.visible()
  }

  override fun hideProgress() {
    binding?.pbLoading?.gone()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as? List<String>
      if (mPaths.isNullOrEmpty().not()) {
        WebEngageController.trackEvent(GALLERY_IMAGE_ADDED, ADDED, sessionLocal.fpTag)
        startBackgroundActivity(
          FragmentType.BACKGROUND_IMAGE_CROP_FRAGMENT,
          Bundle().apply { putString(BGImageCropFragment.BK_IMAGE_PATH, mPaths!![0]) }, isResult = true
        )
      }
    } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 101) {
      if (data?.getBooleanExtra(IntentConstant.IS_UPDATED.name, false) == true) {
        getBackgroundImages()
      }
    }
  }
}