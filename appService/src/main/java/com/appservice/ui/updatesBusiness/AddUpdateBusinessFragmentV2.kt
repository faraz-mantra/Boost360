package com.appservice.ui.updatesBusiness

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
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
import com.framework.constants.IntentConstants
import com.framework.constants.UPDATE_PIC_FILE_NAME
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.firebaseUtils.caplimit_feature.CapLimitFeatureResponseItem
import com.framework.firebaseUtils.caplimit_feature.PropertiesItem
import com.framework.firebaseUtils.caplimit_feature.filterFeature
import com.framework.firebaseUtils.caplimit_feature.getCapData
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.pref.Key_Preferences.PREF_NAME_TWITTER
import com.framework.utils.*
import com.google.firebase.firestore.ListenerRegistration
import com.onboarding.nowfloats.bottomsheet.util.runOnUi
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.io.File
import java.net.URL


class AddUpdateBusinessFragmentV2 : AppBaseFragment<AddUpdateBusinessFragmentV2Binding, UpdatesViewModel>() {


  private var lisReg: ListenerRegistration?=null
  private var mSpannable: Spannable?=null
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
          requireActivity().getExternalFilesDir(null)?.path+File.separator+UPDATE_PIC_FILE_NAME)
        if (imgFile.exists()&&isImageValid(imgFile)){
            loadImage(imgFile.path)
        }else{
          loadImage(null)
        }
        toggleContinue()
      }
        }

  }


  override fun onResume() {
    super.onResume()
    setStatusBarColor(R.color.white)

  }

  private fun isImageValid(imgFile: File): Boolean {
    if (imgFile.extension.equals("JPEG",ignoreCase = true)||
      imgFile.extension.equals("JPG",ignoreCase = true)||
      imgFile.extension.equals("PNG",ignoreCase = true)){

      val bitMapOption: BitmapFactory.Options = BitmapFactory.Options()
      bitMapOption.inJustDecodeBounds = true
      bitMapOption.inScaled = false
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
    val featureUpdate = getCapData().filterFeature(CapLimitFeatureResponseItem.FeatureKey.LATESTUPDATES)
    val capLimitUpdate = featureUpdate?.filterProperty(PropertiesItem.KeyType.LIMIT)
    if (/*isUpdate.not() && */capLimitUpdate != null) {
      viewModel?.getMessageUpdates(sessionLocal.getRequestUpdate(PaginationScrollListener.PAGE_START))?.observeOnce(viewLifecycleOwner, {
        val data = it as? BusinessUpdateResponse
        if (data?.totalCount != null && capLimitUpdate.getValueN() != null && data.totalCount!! >= capLimitUpdate.getValueN()!!) {
          baseActivity.hideKeyBoard()
          showAlertCapLimit("Can't add the business update, please activate your premium Add-ons plan.",
            CapLimitFeatureResponseItem.FeatureKey.LATESTUPDATES.name)
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
      binding.tvImgReq.visible()
      binding.etUpdate.hint=getString(R.string.write_about_your_customer_location_recent_updates_timing_etc)
    }else{
      binding.etUpdate.hint=getString(R.string.describe_what_you_want_to_say_in_the_picture_added_above)
      binding.tvImgReq.gone()
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
    baseActivity.onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            openDraftSheet()
        }
      })

    setOnClickListener(binding!!.btnAddImage,binding!!.btnEdit,binding!!.ivMic,binding!!.ivHashtagCross,
      binding!!.tvPreviewAndPost,binding.ivCross)
  }

  fun openDraftSheet(){
    if (binding!!.etUpdate.text.toString().isNotEmpty()||posterImagePath!=null){
            UpdateDraftBSheet.newInstance(binding
              .etUpdate.text.toString(),posterImagePath).show(parentFragmentManager,UpdateDraftBSheet::class.java.name)

          }else{
            activity?.finish()
    }

  }

  override fun onPause() {
    super.onPause()
    lisReg?.remove()
  }
  private fun initUI() {
    binding.etUpdate.requestFocus()
    val imm =
      activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(binding.etUpdate, InputMethodManager.SHOW_IMPLICIT)
    binding!!.tvHashtagTip.text = spanColor(
      getString(R.string.type_in_the_caption_to_create_your_own_hashtags),
      R.color.blue_4889f8,
      "#"
      )

    binding!!.btnAddImage.text = spanBold(
      getString(R.string.add_image_optional),
      "Add image"
    )
    FirestoreManager.readDraft {
      if (activity != null && isAdded) {

        binding!!.etUpdate.setText(highlightHashTag(it?.content, R.color.black_4a4a4a))
        addHashTagFunction()
        binding!!.tvCount.text = (it?.content?.length ?: 0).toString()

        lifecycleScope.launch {
          withContext(Dispatchers.IO) {
            if (it?.imageUri.isNullOrEmpty().not()) {
              Log.i(TAG, "initUI: ${it?.imageUri}")
              val bitmap = Picasso.get().load(it?.imageUri).get()
              val imgFile = File(
                requireActivity().getExternalFilesDir(null)?.path + File.separator
                        + UPDATE_PIC_FILE_NAME
              )
              bitmap.saveAsImageToAppFolder(imgFile.path)

              runOnUi {
                loadImage(imgFile.path)
              }
            }else{
              loadImage(null)
            }
            runOnUi {
              toggleContinue()
            }

          }

        }


      }

      KeyboardVisibilityEvent.setEventListener(
        requireActivity(),
        KeyboardVisibilityEventListener {
          // Ah... at last. do your thing :)
          if (it) {
            binding.cardInput.strokeColor =
              ContextCompat.getColor(requireActivity(), R.color.black_4a4a4a)
          } else {
            binding.cardInput.strokeColor =
              ContextCompat.getColor(requireActivity(), R.color.colorAFAFAF)
          }
        })

      toggleContinue()

    }
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


    mSpannable = binding?.etUpdate?.text

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
        binding!!.layoutHashtagTip.animate().alpha(0F).setListener(object :Animator.AnimatorListener{
          override fun onAnimationStart(p0: Animator?) {

          }

          override fun onAnimationEnd(p0: Animator?) {
            binding!!.layoutHashtagTip.gone()

          }

          override fun onAnimationCancel(p0: Animator?) {
          }

          override fun onAnimationRepeat(p0: Animator?) {
          }

        })
      }
      binding!!.tvPreviewAndPost->{
        startActivity(Intent(requireActivity(), Class.forName(
          "com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity"))
          .putExtra(IntentConstants.MARKET_PLACE_ORIGIN_NAV_DATA, Bundle().apply {
            putString(IntentConstants.IK_CAPTION_KEY,binding!!.etUpdate.text.toString())
            putString(IntentConstants.IK_POSTER, posterImagePath)
            putString(IntentConstants.IK_UPDATE_TYPE,
              if (posterImagePath==null) IntentConstants.UpdateType.UPDATE_TEXT.name
              else IntentConstants.UpdateType.UPDATE_IMAGE_TEXT.name)
          }))
        Log.i(TAG, "onClick: ${binding!!.etUpdate.text.toString().extractHashTag()}")
      }

      binding.ivCross->{
        openDraftSheet()
      }
    }
  }






}


