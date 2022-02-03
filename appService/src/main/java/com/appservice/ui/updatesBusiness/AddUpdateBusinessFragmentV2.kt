package com.appservice.ui.updatesBusiness

import android.app.Activity
import android.content.*
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.AddUpdateBusinessFragmentV2Binding
import com.appservice.model.updateBusiness.BusinessUpdateResponse
import com.appservice.model.updateBusiness.UpdateFloat
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.viewmodel.UpdatesViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.framework.constants.Constants
import com.framework.extensions.*
import com.framework.firebaseUtils.caplimit_feature.CapLimitFeatureResponseItem
import com.framework.firebaseUtils.caplimit_feature.PropertiesItem
import com.framework.firebaseUtils.caplimit_feature.filterFeature
import com.framework.firebaseUtils.caplimit_feature.getCapData
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.pref.*
import com.framework.pref.Key_Preferences.PREF_NAME_TWITTER
import com.framework.utils.*
import com.framework.webengageconstant.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*



class AddUpdateBusinessFragmentV2 : AppBaseFragment<AddUpdateBusinessFragmentV2Binding, UpdatesViewModel>() {



  private var sttUtils: STTUtils?=null
  private val TAG = "AddUpdateBusinessFragme"
  private var startForCropImageResult: ActivityResultLauncher<Intent>?=null
  private var updateFloat: UpdateFloat? = null
  private var posterImagePath: String? = null


  private val mSharedPreferences: SharedPreferences?
    get() {
      return baseActivity.getSharedPreferences(PREF_NAME_TWITTER, Context.MODE_PRIVATE)
    }

  companion object {
    val msgPost = "msg_post"
    val imagePost = "image_post"
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
        if (result.resultCode==Activity.RESULT_OK){
          val imgFile = File(
          requireActivity().getExternalFilesDir(null)?.path+File.separator+Constants.UPDATE_PIC_FILE_NAME)
        if (imgFile.exists()&&isImageValid(imgFile)){
            loadImage(imgFile.path)
        }else{
          loadImage(null)
        }
        toggleContinue()
      }
        }

  }



  private fun isImageValid(imgFile: File): Boolean {
    if (imgFile.extension.equals("JPEG",ignoreCase = true)||
      imgFile.extension.equals("JPG",ignoreCase = true)||
      imgFile.extension.equals("PNG",ignoreCase = true)){

      val bitMapOption: BitmapFactory.Options = BitmapFactory.Options()
      bitMapOption.inJustDecodeBounds = true
      BitmapFactory.decodeFile(imgFile.path, bitMapOption)
      val imageWidth: Int = bitMapOption.outWidth
      val imageHeight: Int = bitMapOption.outHeight

      if (imageWidth>=800||imageHeight>=800){
          if (imgFile.sizeInMb<=2){
            return true
          }else{
            showLongToast(getString(R.string.image_file_size_is_bigger_than_2mb))
            return false
          }
      }else{
        showLongToast(getString(R.string.image_resolution_is_smaller_than_800_x_800_px))
        return false
      }

    }else{
      showLongToast(getString(R.string.image_format_is_not_supported_please_use_jpeg_jpg_or_png))
      return false
    }
  }


  private fun capLimitCheck() {
    val featureUpdate = getCapData().filterFeature(CapLimitFeatureResponseItem.FeatureType.LATESTUPDATES)
    val capLimitUpdate = featureUpdate?.filterProperty(PropertiesItem.KeyType.LIMIT)
    if (/*isUpdate.not() && */capLimitUpdate != null) {
      viewModel?.getMessageUpdates(sessionLocal.getRequestUpdate(PaginationScrollListener.PAGE_START))?.observeOnce(viewLifecycleOwner, {
        val data = it as? BusinessUpdateResponse
        if (data?.totalCount != null && capLimitUpdate.getValueN() != null && data.totalCount!! >= capLimitUpdate.getValueN()!!) {
          baseActivity.hideKeyBoard()
          showAlertCapLimit("Can't add the business update, please activate your premium Add-ons plan.",CapLimitFeatureResponseItem.FeatureType.LATESTUPDATES.name)
        }
      })
    }
  }


  private fun loadImage(path: String?) {
    posterImagePath = path
    sessionLocal.storeFPDetails(imagePost,path)
    if (path.isNullOrEmpty()){
      binding!!.ivImg.setImageResource(0)
      binding!!.ivImg.gone()
      binding!!.btnEdit.gone()
      binding!!.btnAddImage.visible()
    }else{
      binding!!.ivImg.visible()
      Glide.with(this).load(
        path
      ).apply(RequestOptions.skipMemoryCacheOf(true))
        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
        .into(binding!!.ivImg)
      binding!!.btnEdit.visible()
      binding!!.btnAddImage.gone()
    }

  }

  override fun onCreateView() {
    super.onCreateView()
    initUI()
    initStt()
    capLimitCheck()
    addHashTagFunction()
    baseActivity.onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          if (binding!!.etUpdate.text.toString().isNotEmpty()||posterImagePath!=null){
            UpdateDraftBSheet.newInstance(binding
              .etUpdate.text.toString(),posterImagePath).show(parentFragmentManager,UpdateDraftBSheet::class.java.name)

          }
        }
      })

    setOnClickListener(binding!!.btnAddImage,binding!!.btnEdit,binding!!.ivMic,binding!!.ivHashtagCross,
      binding!!.tvPreviewAndPost)
  }

  private fun initUI() {
    binding!!.tvHashtagTip.text = spanColor(
      getString(R.string.type_in_the_caption_to_create_your_own_hashtags),
      R.color.blue_4889f8,
      "#"
      )

    binding!!.btnAddImage.text = spanBold(
      getString(R.string.add_image_optional),
      "Add Image"
    )
    FirestoreManager.readDraft {
      binding!!.etUpdate.setText(it?.content)
      binding!!.tvCount.text = (it?.content?.length?:0).toString()


      loadImage(it?.imageUri)
      toggleContinue()

    }


    toggleContinue()

  }

  fun toggleContinue(){
    if (binding!!.etUpdate.text.toString().isNullOrEmpty()&&posterImagePath.isNullOrEmpty()){
      disableContinue()
    }else{
      enableContinue()
    }
  }
  fun disableContinue(){
    binding!!.tvPreviewAndPost.isEnabled = false
    binding!!.tvPreviewAndPost.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.gray_DADADA))
  }

  fun enableContinue(){
    binding!!.tvPreviewAndPost.isEnabled = true
    binding!!.tvPreviewAndPost.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.colorPrimary))
  }

  private fun initStt() {
    sttUtils = STTUtils(object : STTUtils.Callbacks{
      override fun onDone(text: String?) {
        binding?.etUpdate?.append((text ?: "") + ". ")

      }
    })
    sttUtils?.init(this)
  }

  private fun addHashTagFunction() {


    val mSpannable = binding?.etUpdate?.text

    binding?.etUpdate?.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(short_text: CharSequence, start: Int, before: Int, count: Int) {

        val text = binding?.etUpdate?.text.toString()
        var last_index = 0
        text.trim().split(Regex("\\s+")).forEach {
          Log.i(TAG, "addHashTagFunction: $it")
          if (it.isNotEmpty() && it[0] == '#'){
            val boldSpan = StyleSpan(
              Typeface
              .BOLD)
            val foregroundSpan = ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.black))
            mSpannable?.setSpan(foregroundSpan, text.indexOf(it,startIndex = last_index), text.indexOf(it,startIndex = last_index)+it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mSpannable?.setSpan(boldSpan, text.indexOf(it,startIndex = last_index), text.indexOf(it,startIndex = last_index)+it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

          }

          last_index+=it.length-1

        }

      }
      override fun afterTextChanged(s: Editable?) {
        sessionLocal.storeFPDetails(msgPost,s.toString())

        binding!!.tvCount.text = s.toString().length.toString()
        toggleContinue()
      }
    })

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
          posterImagePath!!,
          requireActivity(),
          startForCropImageResult
        )
      }

      binding!!.ivMic->{
        sttUtils?.promptSpeechInput()
      }

      binding!!.ivHashtagCross->{
        binding!!.layoutHashtagTip.gone()
      }
      binding!!.tvPreviewAndPost->{
        startActivity(Intent(requireActivity(), Class.forName(
          "com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity"))
          .putExtra(Constants.MARKET_PLACE_ORIGIN_NAV_DATA, Bundle().apply {
            putString("IK_CAPTION_KEY",binding!!.etUpdate.text.toString())
            putString("IK_POSTER", posterImagePath)
          }))
        Log.i(TAG, "onClick: ${binding!!.etUpdate.text.toString().extractHashTag()}")
      }
    }
  }






}


