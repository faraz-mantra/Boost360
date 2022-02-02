package com.appservice.ui.staffs.doctors

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentEditDoctorInfoBinding
import com.appservice.model.staffModel.*
import com.appservice.ui.staffs.doctors.bottomsheet.AppointmentBookingBottomSheet
import com.appservice.ui.staffs.ui.StaffFragmentContainerActivity
import com.appservice.ui.staffs.ui.startStaffFragmentActivity
import com.appservice.ui.catalog.common.AppointmentModel
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.ui.staffs.ui.Constants
import com.appservice.viewmodel.StaffViewModel
import com.appservice.utils.WebEngageController
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.pref.UserSessionManager
import com.framework.utils.size
import com.framework.utils.sizeInKb
import com.framework.webengageconstant.ADDED
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.STAFF_PROFILE_CREATE
import com.framework.webengageconstant.STAFF_PROFILE_UPDATED
import kotlinx.android.synthetic.main.item_preview_image.*
import java.io.ByteArrayOutputStream
import java.io.File

class EditDoctorsDetailsFragment :
  AppBaseFragment<FragmentEditDoctorInfoBinding, StaffViewModel>() {
  private var isTimingUpdated: Boolean? = null
  private var isProfileImageUpdated: Boolean? = null
  private var profileImageIsChange: Boolean? = null
  private var resultCode: Int = 1
  private var isAvailable: Boolean? = true
  private lateinit var yearOfExperience: String
  private lateinit var staffDescription: String
  private var staffAge: Int? = null
  private lateinit var staffName: String
  private lateinit var specializationList: ArrayList<SpecialisationsItem>
  private var serviceListId: ArrayList<String>? = null
  private lateinit var speciality: String
  private var staffImage: StaffImage? = null
  private var staffSignature: StaffImage? = null
  private var isSignatureSelection: Boolean? = false
  private var profileimageUri: Uri? = null
  private var signatureUri: Uri? = null
  private var isEdit: Boolean? = null
  private var staffProfileFile: File? = null
  private var staffSignatureFile: File? = null
  private var staffProfile: StaffCreateProfileRequest? = StaffCreateProfileRequest()
  private var servicesList: ArrayList<DataItemService>? = null
  private var staffDetails: StaffDetailsResult? = null
  private var appointmentBookingWindow: String? = null
  override fun getLayout(): Int {
    return R.layout.fragment_edit_doctor_info
  }

  override fun getViewModelClass(): Class<StaffViewModel> {
    return StaffViewModel::class.java
  }

  companion object {
    fun newInstance(): EditDoctorsDetailsFragment {
      return EditDoctorsDetailsFragment()
    }
  }

  private fun showHideServicesText() {
    when (staffDetails?.serviceIds?.size ?: 0 > 0) {
      true -> binding?.ctvServices?.visibility = View.VISIBLE
      else -> binding?.ctvServices?.visibility = View.GONE
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(
      binding?.rlConsultationHour,
      binding?.rlServiceProvided,
      binding?.btnOtherInfo,
      binding?.btnUploadSignature,
      binding?.imageAddBtn,
      binding?.changeImage,
      binding?.ctfBookingWindow,
      binding?.btnSave

    )
    getBundleData()
  }

  private fun getBundleData() {
    sessionLocal= UserSessionManager(requireActivity())
    staffDetails = arguments?.getSerializable(IntentConstant.STAFF_DATA.name) as? StaffDetailsResult
    isEdit = (staffDetails != null && staffDetails?.id.isNullOrEmpty().not())
    if (isEdit == true) {
      updatePreviousData()
      (requireActivity() as StaffFragmentContainerActivity).getToolbar()
        ?.getToolbarTitleTextView()?.gravity = Gravity.START
    }
    if (staffDetails == null) staffDetails = StaffDetailsResult()
  }

  private fun updatePreviousData() {
    val speciality = staffDetails?.speciality?:""
    setImage(listOf(staffDetails?.tileImageUrl.toString()))
    setSignatureView(listOf(staffDetails?.signature.toString()))
    binding?.ctfStaffName?.setText(staffDetails?.name)
    binding?.ctfStaffDesc?.setText(staffDetails?.description)
    binding?.ctfStaffSpeciality?.setText(staffDetails?.speciality)
    binding?.ctfBookingWindow?.setText(staffDetails?.bookingWindow.toString())
    showHideServicesText()
    // setTimings()
    if (staffDetails?.timings.isNullOrEmpty().not()) {
      val textStaffDays = AppointmentModel().getStringStaffActive(staffDetails?.timings)
      binding?.ctvTiming?.visibility = if (textStaffDays.isEmpty()) View.GONE else View.VISIBLE
      binding?.ctvTiming?.text = textStaffDays
    } else binding?.ctvTiming?.gone()

    if (speciality?.isNullOrEmpty() == false) binding?.ctfStaffSpeciality?.setText(speciality)
    binding?.btnSave?.text = getString(R.string.update)
    if (resultCode != AppCompatActivity.RESULT_OK) setServicesList()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.imageAddBtn, binding?.changeImage -> {
        isSignatureSelection = false;openImagePicker()
      }
      binding?.rlConsultationHour -> {
        consultationHourFragment()
      }
      binding?.rlServiceProvided -> {
        selectServicesFragment()
      }
      binding?.btnOtherInfo -> {
        startAdditionalInfoFragment()
      }
      binding?.btnUploadSignature -> {
        isSignatureSelection = true
        openImagePicker()
      }
      binding?.ctfBookingWindow -> {
        openBookingWindowBottomSheet()
      }
      binding?.btnSave -> {
        if ((isEdit == false) && isValid()) createStaffProfile()
        if (isEdit == true && isValid()) updateStaffProfile()
      }
    }
  }

  private fun openBookingWindowBottomSheet() {
    val appointmentBookingBottomSheet = AppointmentBookingBottomSheet()
    appointmentBookingBottomSheet.onClicked = {
      this.appointmentBookingWindow = it
      binding?.ctfBookingWindow?.setText(it)
      staffDetails?.bookingWindow = it
    }
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.STAFF_DATA.name, staffDetails)
    appointmentBookingBottomSheet.arguments = bundle
    appointmentBookingBottomSheet.show(
      parentFragmentManager,
      AppointmentBookingBottomSheet::class.java.name
    )
  }

  private fun startAdditionalInfoFragment() {
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.STAFF_DATA.name, staffDetails)
    startStaffFragmentActivity(
      baseActivity,
      FragmentType.DOCTOR_ADDITIONAL_INFO,
      bundle,
      clearTop = false,
      isResult = true,
      requestCode = Constants.STAFF_ADDITIONAL_DATA
    )
  }

  private fun selectServicesFragment() {
    val bundle = Bundle()
    if (staffDetails?.serviceIds.isNullOrEmpty().not()) {
      val serviceIds = staffDetails?.serviceIds
      bundle.putStringArrayList(IntentConstant.STAFF_SERVICES.name, serviceIds)
    }
    startStaffFragmentActivity(
      baseActivity,
      FragmentType.STAFF_SELECT_SERVICES_FRAGMENT,
      bundle,
      clearTop = false,
      isResult = true,
      requestCode = Constants.REQUEST_CODE_SERVICES_PROVIDED
    )
  }

  private fun consultationHourFragment() {
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.STAFF_DATA.name, staffDetails)
    startStaffFragmentActivity(
      baseActivity,
      FragmentType.STAFF_TIMING_FRAGMENT,
      bundle,
      clearTop = false,
      isResult = true,
      requestCode = Constants.REQUEST_CODE_STAFF_TIMING
    )
  }

  private fun updateStaffImage(imagetype: IMAGETYPE) {
    val staffUpdateImageRequest = StaffUpdateImageRequest()
    if (imagetype == IMAGETYPE.PROFILE) {
      staffUpdateImageRequest.image = staffImage
      staffUpdateImageRequest.imageType = IMAGETYPE.PROFILE.ordinal
    }
    if (imagetype == IMAGETYPE.SIGNATURE) {
      staffUpdateImageRequest.image = staffSignature
      staffUpdateImageRequest.imageType = IMAGETYPE.SIGNATURE.ordinal
    }
    staffUpdateImageRequest.staffId = staffDetails?.id
    if (imagetype==IMAGETYPE.PROFILE) showProgress(getString(R.string.uploading_image)) else showProgress(getString(R.string.upload_signature))
    viewModel?.updateStaffImage(staffUpdateImageRequest)
      ?.observeOnce(viewLifecycleOwner, {
        hideProgress()
        if (it.isSuccess().not()) {
          showShortToast(it.errorMessage() ?: getString(R.string.something_went_wrong))
        } else if (isEdit==false&&imagetype==IMAGETYPE.SIGNATURE&&it.isSuccess()){
          updateStaffImage(IMAGETYPE.PROFILE)
        }else {
          finishAndGoBack()
        }
      })
  }

  private fun updateStaffProfile() {
    val request = StaffProfileUpdateRequest(
      isAvailable = isAvailable,
      serviceIds = staffDetails?.serviceIds,
      floatingPointTag = sessionLocal.fpTag,
      name = staffName,
      description = staffDescription,
      experience = yearOfExperience.toIntOrNull(),
      staffId = staffDetails?.id,
      age = staffAge,
      specialisations = specializationList
    )
    viewModel?.updateStaffProfile(request)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.isSuccess()) {
        updateStaffTimings()
        WebEngageController.trackEvent(STAFF_PROFILE_UPDATED, ADDED, NO_EVENT_VALUE)
      } else showShortToast(it?.errorMessage() ?: getString(R.string.something_went_wrong))
    })

  }

  private fun isValid(): Boolean {
    this.speciality = binding?.ctfStaffSpeciality?.text.toString()
    this.serviceListId = ArrayList()
    this.specializationList = ArrayList()
    this.yearOfExperience = staffDetails?.experience.toString()
    this.staffName = binding?.ctfStaffName?.text.toString()
    this.staffDescription = binding?.ctfStaffDesc?.text.toString()
    val businessLicense = binding?.tvBusinessLicense?.text.toString()
    this.appointmentBookingWindow = binding?.ctfBookingWindow?.text.toString()
    servicesList?.forEach { items -> if (items.id.isNullOrEmpty().not()) serviceListId?.add(items.id!!) }
    if (serviceListId.isNullOrEmpty().not()) staffDetails?.serviceIds = serviceListId
    if (profileimageUri.toString() == "null" || profileimageUri == null || profileimageUri.toString().isEmpty() || profileimageUri.toString().isBlank()) {
      showLongToast(getString(R.string.please_choose_doctor_profile))
      return false
    } else if (staffName.isBlank()) {
      showLongToast(getString(R.string.enter_doctor_name))
      return false
    } else if (staffDescription.isBlank()) {
      showLongToast(getString(R.string.please_enter_doctors_description))
      return false
    } else if (speciality.isBlank()) {
      showLongToast(getString(R.string.please_enter_speciality))
      return false
    } else if (businessLicense.isEmpty()) {
      showLongToast(getString(R.string.please_enter_valid_business_))
      return false
    } else if (signatureUri.toString() == "null" || signatureUri == null || signatureUri.toString().isEmpty() || signatureUri.toString().isBlank()
    ) {
      showLongToast(getString(R.string.please_choose_signature))
      return false
    } else if (appointmentBookingWindow.isNullOrEmpty() || appointmentBookingWindow?.isBlank() == true) {
      showLongToast(getString(R.string.please_choose_booking_window_duration))
      return false
    } else if (staffDetails?.serviceIds.isNullOrEmpty()) {
      showLongToast(getString(R.string.please_select_one_service_provided_by_doctor))
      return false
    }
    if (isProfileImageUpdated == true) {
      profileImageUpdated()
    }

    if (isEdit == null || isEdit == false) {
      staffProfile?.description = staffDescription
      staffProfile?.floatingPointTag = sessionLocal.fpTag
      staffProfile?.name = staffName
      staffProfile?.serviceIds = serviceListId
      staffProfile?.image = staffImage
      staffProfile?.specialisations = specializationList
    }
    return true
  }

  private fun profileImageUpdated() {
    val imageExtension: String? =
      profileimageUri?.toString()?.substring(profileimageUri.toString().lastIndexOf("."))
    val imageToByteArray: ByteArray = imageToByteArray(profileimageUri)
    this.staffImage = StaffImage(
      image = "data:image/png;base64,${
        Base64.encodeToString(
          imageToByteArray,
          Base64.DEFAULT
        )
      }",
      fileName = "$staffName$imageExtension",
      imageFileType = imageExtension?.removePrefix(".")
    )
  }

  private fun createStaffProfile() {
    showProgress()
    viewModel?.createStaffProfile(staffProfile)?.observe(viewLifecycleOwner, { t ->
      if (t.isSuccess()) {
        addStaffTimings((t as StaffCreateProfileResponse).result)
        staffDetails?.id = t.result
        showShortToast(getString(R.string.profile_created))
        onStaffAddedOrUpdated()
        WebEngageController.trackEvent(STAFF_PROFILE_CREATE, ADDED, NO_EVENT_VALUE)
      } else showShortToast(getString(R.string.something_went_wrong))
      hideProgress()
    })
  }

  private fun onStaffAddedOrUpdated() {
    val instance = FirestoreManager
    if (instance.getDrScoreData()?.metricdetail == null) return
    instance.getDrScoreData()?.metricdetail?.boolean_create_staff = true
    instance.updateDocument()
  }

  private fun updateStaffTimings() {
    if (staffDetails?.timings == null && isTimingUpdated == null || isTimingUpdated == false) {
      finishAndGoBack()
      return
    }
    showProgress(getString(R.string.updating_doctor_timing))
    val request = StaffTimingAddUpdateRequest(
      staffId = staffDetails?.id,
      workTimings = this.staffDetails?.timings
    )
    viewModel?.updateStaffTiming(request)?.observeOnce(viewLifecycleOwner, Observer {
      hideProgress()
      if (it?.isSuccess() == true) {
        Log.v(getString(R.string.staff_timings), getString(R.string.staff_timings_added))
        finishAndGoBack()
      } else Log.v(getString(R.string.staff_timings), getString(R.string.something_went_wrong))
    })
  }


  private fun finishAndGoBack() {
    val intent = Intent()
    intent.putExtra(IntentConstant.IS_UPDATED.name, true)
    requireActivity().setResult(AppCompatActivity.RESULT_OK, intent)
    requireActivity().finish()
  }

  private fun imageToByteArray(uri: Uri?): ByteArray {
    val bm: Bitmap = BitmapFactory.decodeFile(uri?.toString())
    val byteArrayOutStream = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutStream) // bm is the bitmap object
    return byteArrayOutStream.toByteArray()
  }

  private fun addStaffTimings(staffId: String?) {
    if (staffDetails?.timings == null) staffDetails?.timings = AppointmentModel.getDefaultTimings()
    showProgress(getString(R.string.staff_timing_add))
    viewModel?.addStaffTiming(
      StaffTimingAddUpdateRequest(
        staffId = staffDetails?.id ?: staffId,
        staffDetails?.timings
      )
    )?.observeOnce(viewLifecycleOwner, Observer {
      hideProgress()
      if (it.isSuccess()) {
        Log.v(getString(R.string.staff_timings), getString(R.string.staff_timings_added))
        updateStaffImage(IMAGETYPE.SIGNATURE)
      } else showShortToast(it.errorMessage() ?: getString(R.string.something_went_wrong))
    })
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(
      this@EditDoctorsDetailsFragment.parentFragmentManager,
      ImagePickerBottomSheet::class.java.name
    )
  }

  private fun openImagePicker(it: ClickType) {
    val type = if (it == ClickType.CAMERA) ImagePicker.Mode.CAMERA else ImagePicker.Mode.GALLERY
    ImagePicker.Builder(baseActivity)
      .mode(type)
      .compressLevel(ImagePicker.ComperesLevel.SOFT).directory(ImagePicker.Directory.DEFAULT)
      .extension(ImagePicker.Extension.PNG).allowMultipleImages(false)
      .enableDebuggingMode(true).build()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when {
      requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK -> {
        val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as List<String>
        if (isSignatureSelection == true) {
          setSignature(mPaths)
        } else {
          setImage(mPaths)
          isProfileImageUpdated = true
          if (isEdit == true)
            profileImageIsChange = true
        }
        when (isEdit == true && profileImageIsChange != null && profileImageIsChange == true && isValid()) {
          true -> updateStaffImage(IMAGETYPE.PROFILE)
        }
      }
      requestCode == Constants.REQUEST_CODE_SERVICES_PROVIDED && resultCode == AppCompatActivity.RESULT_OK -> {
        this.resultCode = resultCode
        this.serviceListId = arrayListOf()
        this.servicesList =
          data?.extras?.get(IntentConstant.STAFF_SERVICES.name) as? ArrayList<DataItemService>
        serviceListId = ArrayList();
        servicesList?.forEach { dataItem ->
          if (dataItem.id.isNullOrEmpty().not()) serviceListId?.add(dataItem.id!!)
        }
        staffDetails?.serviceIds = serviceListId
        binding?.ctvServices?.text = (servicesList?.map { it.name })?.joinToString(
          ", ",
          limit = 5,
          truncated = "+${servicesList?.size?.minus(5)} more"
        )
        showHideServicesText()
      }
      requestCode == Constants.REQUEST_CODE_STAFF_TIMING && resultCode == AppCompatActivity.RESULT_OK -> {
        this.staffDetails =
          data?.extras?.get(IntentConstant.STAFF_TIMINGS.name) as? StaffDetailsResult
        if (staffDetails?.timings.isNullOrEmpty().not()) {
          val textStaffDays = AppointmentModel().getStringStaffActiveN(staffDetails?.timings)
          binding?.ctvTiming?.visibility = if (textStaffDays.isEmpty()) View.GONE else View.VISIBLE
          binding?.ctvTiming?.text = textStaffDays
        } else binding?.ctvTiming?.gone()
        isTimingUpdated = true
      }
      //staff additional details
      requestCode == Constants.STAFF_ADDITIONAL_DATA && resultCode == AppCompatActivity.RESULT_OK -> {
        this.staffDetails = data?.extras?.get(IntentConstant.STAFF_DATA.name) as? StaffDetailsResult
      }
    }

  }

  private fun setServicesList() {
    if (isEdit == true) {
      viewModel?.getServiceListing(ServiceListRequest(floatingPointTag = sessionLocal.fpTag))
        ?.observeOnce(viewLifecycleOwner, Observer {
          if (it?.isSuccess() == true) {
            val data = (it as ServiceListResponse).result?.data
            if (staffDetails?.serviceIds.isNullOrEmpty().not()) {
              val servicesProvided = data?.filter { item ->
                staffDetails?.serviceIds?.contains(
                  item?.id ?: ""
                ) == true
              } as? ArrayList<DataItemService>
              binding?.ctvServices?.text = servicesProvided?.map { it1 -> it1.name }
                ?.joinToString(" ,", limit = 5, truncated = "+5 more")
              showHideServicesText()
            }
          }
        })
    }
  }

  private fun setImage(mPaths: List<String>) {
    this.profileimageUri = Uri.parse(mPaths[0])
    staffProfileFile = File(mPaths[0])
    if (profileimageUri?.path!="null"||profileimageUri?.path!=null||profileimageUri!=null){
      binding?.imageAddBtn?.gone()
      binding?.staffImageView?.visible()
      binding?.changeImage?.visible()
      binding?.staffImageView?.let { activity?.glideLoad(it, profileimageUri.toString(), R.drawable.placeholder_image_n) }
    }else{
      binding?.imageAddBtn?.visible()
      binding?.staffImageView?.gone()
      binding?.changeImage?.gone()
    }

  }

  private fun setSignature(mPaths: List<String>) {
    setSignatureView(mPaths)
    if (signatureUri?.path!="null"&&signatureUri?.path!=null&&signatureUri!=null) {
      val imageExtension: String? =
        signatureUri?.toString()?.substring(signatureUri.toString().lastIndexOf("."))
      val imageToByteArray: ByteArray = imageToByteArray(signatureUri)
      this.staffSignature = StaffImage(
        image = "data:image/png;base64,${
          Base64.encodeToString(
            imageToByteArray,
            Base64.DEFAULT
          )
        }",
        fileName = "${System.currentTimeMillis()}$imageExtension",
        imageFileType = imageExtension?.removePrefix(".")
      )
    }

  }

  private fun setSignatureView(mPaths: List<String>) {
    this.signatureUri = Uri.parse(mPaths[0])
    staffSignatureFile = File(mPaths[0])
    if(signatureUri!=null){
      binding?.layoutItemPreview?.root?.visible()
      binding?.btnUploadSignature?.gone()
      binding?.layoutItemPreview?.image?.let { activity?.glideLoad(it, signatureUri.toString(), R.drawable.placeholder_image_n) }
    }else{
      binding?.layoutItemPreview?.root?.gone()
      binding?.btnUploadSignature?.visible()
    }

    binding?.layoutItemPreview?.crossIcon?.setOnClickListener {
      binding?.layoutItemPreview?.root?.gone()
      binding?.btnUploadSignature?.visible()
      binding?.layoutItemPreview?.image?.setImageBitmap(null)
    }
    if (staffSignatureFile!=null&&staffSignatureFile?.size?.equals(0.0)==false)
    binding?.layoutItemPreview?.ctvSize?.text = "${staffSignatureFile?.sizeInKb} Kb"
    else binding?.layoutItemPreview?.ctvSize?.text = this.signatureUri.toString().subSequence(signatureUri?.toString()?.lastIndexOf("/")!!+1,signatureUri?.toString()?.length!!)
    binding?.layoutItemPreview?.cbChange?.setOnClickListener {
      binding?.layoutItemPreview?.root?.gone()
      isSignatureSelection = true
      openImagePicker()
    }
  }

}