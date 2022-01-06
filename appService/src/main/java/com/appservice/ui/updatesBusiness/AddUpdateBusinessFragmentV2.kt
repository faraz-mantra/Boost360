package com.appservice.ui.updatesBusiness

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.text.SpannableString
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.net.toFile
import androidx.lifecycle.MutableLiveData
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.AddUpdateBusinessFragmentBinding
import com.appservice.databinding.AddUpdateBusinessFragmentV2Binding
import com.appservice.model.updateBusiness.BusinessUpdateResponse
import com.appservice.model.updateBusiness.PostUpdateTaskRequest
import com.appservice.model.updateBusiness.UpdateFloat
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.WebEngageController
import com.appservice.utils.getBitmap
import com.appservice.viewmodel.UpdatesViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.framework.analytics.SentryController
import com.framework.extensions.*
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.firebaseUtils.caplimit_feature.CapLimitFeatureResponseItem
import com.framework.firebaseUtils.caplimit_feature.PropertiesItem
import com.framework.firebaseUtils.caplimit_feature.filterFeature
import com.framework.firebaseUtils.caplimit_feature.getCapData
import com.framework.pref.*
import com.framework.pref.Key_Preferences.PREF_KEY_TWITTER_LOGIN
import com.framework.pref.Key_Preferences.PREF_NAME_TWITTER
import com.framework.utils.FileUtils
import com.framework.utils.hasHTMLTags
import com.framework.utils.hideKeyBoard
import com.framework.utils.showKeyBoard
import com.framework.views.customViews.CustomTextView
import com.framework.webengageconstant.*
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.ui.updateChannel.startFragmentChannelActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*



class AddUpdateBusinessFragmentV2 : AppBaseFragment<AddUpdateBusinessFragmentV2Binding, UpdatesViewModel>() {

  private var startForCropImageResult: ActivityResultLauncher<Intent>?=null
  private val REQ_CODE_SPEECH_INPUT = 122
  private val RC_GALLERY=101
  private var isUpdate: Boolean = false
  private var toSubscribers = MutableLiveData(false)
  private var fbStatusEnabled = MutableLiveData(false)
  private var twitterSharingEnabled = MutableLiveData(false)
  private var fbPageStatusEnable =MutableLiveData(false)
  private var updateFloat: UpdateFloat? = null
  private var postImagePath: String? = null
  private var firsTime=true


  private val postImage: File?
    get() {
      return if (postImagePath.isNullOrEmpty().not()) File(postImagePath) else null
    }

  private val mSharedPreferences: SharedPreferences?
    get() {
      return baseActivity.getSharedPreferences(PREF_NAME_TWITTER, Context.MODE_PRIVATE)
    }

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AddUpdateBusinessFragmentV2 {
      val fragment = AddUpdateBusinessFragmentV2()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.add_update_business_fragment_v2
  }

  override fun getViewModelClass(): Class<UpdatesViewModel> {
    return UpdatesViewModel::class.java
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    startForCropImageResult =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
          result: ActivityResult ->
        val imgFile = File(
          requireActivity().getExternalFilesDir(null)?.path+File.separator+UpdateImagePickerBSheet.fileName)
        if (imgFile.exists()){
          Glide.with(this).load(imgFile).apply(RequestOptions.skipMemoryCacheOf(true))
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(binding!!.ivImg)
          binding!!.ivImg.visible()
          binding!!.btnEdit.visible()
          binding!!.btnAddImage.gone()
        }
      }
  }

  override fun onCreateView() {
    super.onCreateView()


    baseActivity.onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

          UpdateDraftBSheet().show(parentFragmentManager,UpdateDraftBSheet::class.java.name)
        }
      })

    setOnClickListener(binding!!.btnAddImage,binding!!.btnEdit)
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding!!.btnAddImage->{
         UpdateImagePickerBSheet.newInstance(object :UpdateImagePickerBSheet.Callbacks{
           override fun onImagePicked(path: String) {
             UpdateCropImageActivity.launchActivity(path,requireActivity(),startForCropImageResult)
           }
         }).show(parentFragmentManager,UpdateImagePickerBSheet::class.java.name)
      }
      binding!!.btnEdit->{
        UpdateCropImageActivity.launchActivity(
          requireActivity().getExternalFilesDir(null)?.path+File.separator+UpdateImagePickerBSheet.fileName,
          requireActivity(),
          startForCropImageResult
        )
      }
    }
  }





}


