package com.appservice.ui.staffs.doctors

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentEditDoctorInfoBinding
import com.appservice.model.staffModel.*
import com.appservice.ui.catalog.common.AppointmentModel
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.ui.staffs.doctors.bottomsheet.AppointmentBookingBottomSheet
import com.appservice.ui.staffs.ui.Constants
import com.appservice.ui.staffs.ui.startStaffFragmentActivity
import com.appservice.utils.WebEngageController
import com.appservice.utils.changeColorOfSubstring
import com.appservice.viewmodel.StaffViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.pref.UserSessionManager
import com.framework.utils.size
import com.framework.utils.sizeInKb
import com.framework.webengageconstant.*
import java.io.ByteArrayOutputStream
import java.io.File

class EditDoctorsDetailsFragment : AppBaseFragment<FragmentEditDoctorInfoBinding, StaffViewModel>() {

  private var isTimingUpdated: Boolean? = null
  private var isProfileImageUpdated: Boolean? = null
  private var resultCode: Int = 1
  private var yearOfExperience: String = ""
  private var staffDescription: String = ""
  private var businessLicense: String = ""
  private var staffName: String = ""
  private var specializationList: ArrayList<SpecialisationsItem> = arrayListOf()
  private var speciality: String = ""
  private var staffImage: StaffImage? = null
  private var staffSignature: StaffImage? = null
  private var isSignatureSelection: Boolean? = false
  private var profileimageUri: Uri? = null
  private var signatureUri: Uri? = null
  private var isEdit: Boolean = false
  private var staffProfileFile: File? = null
  private var staffSignatureFile: File? = null
  private var servicesList: ArrayList<DataItemService>? = null
  private var staffDetails: StaffDetailsResult? = null

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
      binding?.rlConsultationHour, binding?.rlServiceProvided, binding?.btnOtherInfo, binding?.btnUploadSignature,
      binding?.imageAddBtn, binding?.changeImage, binding?.ctfBookingWindow, binding?.btnSave
    )
    getBundleData()
    setupUIColor()
  }

  private fun setupUIColor() {
    changeColorOfSubstring(R.string.name_doctor, R.color.black_4a4a4a, "*", binding?.tvDoctorName!!)
    changeColorOfSubstring(R.string.description, R.color.black_4a4a4a, "*", binding?.tvDoctorDesc!!)
    changeColorOfSubstring(R.string.speciality, R.color.black_4a4a4a, "*", binding?.tvDoctorSpeciality!!)
    changeColorOfSubstring(R.string.business_license, R.color.black_4a4a4a, "*", binding?.tvDoctorLicense!!)
    changeColorOfSubstring(R.string.upload_signature_, R.color.black_4a4a4a, "*", binding?.tvDoctorUploadSignature!!)
    changeColorOfSubstring(R.string.appointment_booking_window_for_patients, R.color.black_4a4a4a, "*", binding?.tvDoctorBookingWindow!!)
  }

  private fun checkDoctorAdded() {
    val request = GetStaffListingRequest(FilterBy(offset = 0, limit = 2), sessionLocal.fpTag, "")
    viewModel?.getStaffList(request)?.observeOnce(viewLifecycleOwner) {
      if ((it as? GetStaffListingResponse)?.result?.paging?.count ?: 0 >= 1) showAlertAddOneDoctor()
    }
  }

  private fun getBundleData() {
    sessionLocal = UserSessionManager(requireActivity())
    staffDetails = arguments?.getSerializable(IntentConstant.STAFF_DATA.name) as? StaffDetailsResult
    isEdit = (staffDetails != null && staffDetails?.id.isNullOrEmpty().not())
    if (isEdit) {
      updatePreviousData()
      baseActivity.getToolbar()?.getToolbarTitleTextView()?.gravity = Gravity.START
    }
    if (staffDetails == null) staffDetails = StaffDetailsResult()
    if (isDoctor && isEdit.not()) checkDoctorAdded()
  }

  fun showAlertAddOneDoctor() {
    AlertDialog.Builder(ContextThemeWrapper(baseActivity, R.style.CustomAlertDialogTheme)).apply {
      setCancelable(false)
      setTitle("You have exceeded limit!").setMessage(getString(R.string.add_only_one_doctor_profile)).setPositiveButton(getString(R.string.okay)) { dialog, _ ->
        dialog.dismiss()
        baseActivity.finish()
      }
      create().show()
    }
  }

  private fun updatePreviousData() {
    val speciality = staffDetails?.speciality ?: ""
    setImage(listOf(staffDetails?.getUrlImage().toString()), true)
    setSignatureView(staffDetails?.getUrlSignature() ?: "", true)
    binding?.ctfStaffName?.setText(staffDetails?.name)
    binding?.ctfStaffDesc?.setText(staffDetails?.description)
    binding?.tvBusinessLicense?.setText(staffDetails?.businessLicence)
    binding?.ctfStaffSpeciality?.setText(staffDetails?.speciality)
    binding?.ctfBookingWindow?.setText("${staffDetails?.getBookingWindowN()}")
    showHideServicesText()
    if (staffDetails?.timings.isNullOrEmpty().not()) {
      val textStaffDays = AppointmentModel().getStringStaffActive(staffDetails?.timings)
      binding?.ctvTiming?.visibility = if (textStaffDays.isEmpty()) View.GONE else View.VISIBLE
      binding?.ctvTiming?.text = textStaffDays
    } else binding?.ctvTiming?.gone()

    if (speciality.isNotEmpty()) binding?.ctfStaffSpeciality?.setText(speciality)
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
        if (!isEdit && isValid()) createStaffProfile()
        if (isEdit && isValid()) updateStaffProfile()
      }
    }
  }

  private fun openBookingWindowBottomSheet() {
    val appointmentBookingBottomSheet = AppointmentBookingBottomSheet()
    appointmentBookingBottomSheet.onClicked = {
      binding?.ctfBookingWindow?.setText(it)
      staffDetails?.bookingWindow = it?.replace("days", "")?.trim()?.toIntOrNull() ?: 0
    }
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.STAFF_DATA.name, staffDetails)
    appointmentBookingBottomSheet.arguments = bundle
    appointmentBookingBottomSheet.show(parentFragmentManager, AppointmentBookingBottomSheet::class.java.name)
  }

  private fun startAdditionalInfoFragment() {
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.STAFF_DATA.name, staffDetails)
    startStaffFragmentActivity(FragmentType.DOCTOR_ADDITIONAL_INFO, bundle, isResult = true)
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
      baseActivity, FragmentType.STAFF_TIMING_FRAGMENT, bundle, clearTop = false,
      isResult = true, requestCode = Constants.REQUEST_CODE_STAFF_TIMING
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
//    if (imagetype == IMAGETYPE.PROFILE) showProgress(getString(R.string.uploading_image)) else showProgress(getString(R.string.upload_signature))
    viewModel?.updateStaffImage(staffUpdateImageRequest)?.observeOnce(viewLifecycleOwner) {
      if (it.isSuccess().not()) {
        hideProgress()
        showShortToast(it.errorFlowMessage() ?: getString(R.string.please_try_again))
      } else if (!isEdit && imagetype == IMAGETYPE.SIGNATURE && it.isSuccess()) {
        updateStaffImage(IMAGETYPE.PROFILE)
      } else {
        hideProgress()
        finishAndGoBack()
      }
    }
  }

  private fun updateStaffProfile() {
    showProgress()
    viewModel?.updateStaffProfile(requestUpdate())?.observeOnce(viewLifecycleOwner) {
      if (it.isSuccess()) {
        updateStaffTimings()
        WebEngageController.trackEvent(if (isDoctorClinic) DOCTOR_PROFILE_UPDATED else STAFF_PROFILE_UPDATED, ADDED, NO_EVENT_VALUE)
      } else showShortToast(it?.errorFlowMessage() ?: getString(R.string.something_went_wrong))
    }
  }

  private fun requestUpdate(): StaffProfileUpdateRequest {
    val staffProfile = StaffProfileUpdateRequest()
    staffProfile.description = staffDescription
    staffProfile.floatingPointTag = sessionLocal.fpTag
    staffProfile.name = staffName
    staffProfile.speciality = this.speciality
    staffProfile.serviceIds = staffDetails?.serviceIds
    staffProfile.businessLicence = businessLicense
    staffProfile.specialisations = specializationList
    staffProfile.isAvailable = true
    staffProfile.age = staffDetails?.age
    staffProfile.gender = staffDetails?.gender
    staffProfile.experience = yearOfExperience.toIntOrNull()
    staffProfile.staffId = staffDetails?.id
    staffProfile.education = staffDetails?.education
    staffProfile.contactNumber = staffDetails?.contactNumber
    staffProfile.memberships = staffDetails?.memberships
    staffProfile.registration = staffDetails?.registration
    staffProfile.appointmentType = staffDetails?.appointmentType
    staffProfile.bookingWindow = staffDetails?.bookingWindow
    return staffProfile
  }

  private fun isValid(): Boolean {
    this.speciality = binding?.ctfStaffSpeciality?.text.toString()
    this.specializationList = ArrayList()
    this.yearOfExperience = staffDetails?.experience.toString()
    this.staffName = binding?.ctfStaffName?.text.toString()
    this.staffDescription = binding?.ctfStaffDesc?.text.toString()
    this.businessLicense = binding?.tvBusinessLicense?.text.toString()
    if (servicesList.isNullOrEmpty().not()) {
      staffDetails?.serviceIds = ArrayList()
      servicesList?.forEach { items -> if (items.id.isNullOrEmpty().not()) staffDetails?.serviceIds?.add(items.id!!) }
    }
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
    } else if (staffDetails?.bookingWindow == null || staffDetails?.bookingWindow == 0) {
      showLongToast(getString(R.string.please_choose_booking_window_duration))
      return false
    } else if (staffDetails?.serviceIds.isNullOrEmpty()) {
      showLongToast(getString(R.string.please_select_one_service_provided_by_doctor))
      return false
    }
    return true
  }

  private fun profileImageUpdated() {
    val imageExtension = ".png"//profileimageUri?.toString()?.substring(profileimageUri.toString().lastIndexOf("."))
    val imageToByteArray: ByteArray = imageToByteArray(profileimageUri)
    this.staffImage = StaffImage(
      image = "data:image/png;base64,${Base64.encodeToString(imageToByteArray, Base64.DEFAULT)}",
      fileName = "$staffName$imageExtension",
      imageFileType = imageExtension?.removePrefix(".")
    )
  }

  private fun createStaffProfile() {
    showProgress()
    if (staffDetails?.id.isNullOrEmpty()) {
      viewModel?.createStaffProfile(requestCreateModel())?.observe(viewLifecycleOwner) {
        val staffId = (it as? StaffCreateProfileResponse)?.result
        if (it.isSuccess() && staffId.isNullOrEmpty().not()) {
          staffDetails?.id = staffId
          addStaffTimings(staffId)
          onStaffAddedOrUpdated()
          WebEngageController.trackEvent(if (isDoctorClinic) DOCTOR_PROFILE_CREATE else STAFF_PROFILE_CREATE, ADDED, NO_EVENT_VALUE)
        } else {
          hideProgress()
          showShortToast(it.errorFlowMessage() ?: getString(R.string.something_went_wrong))
        }
      }
    } else addStaffTimings(staffDetails?.id)
  }

  private fun requestCreateModel(): StaffCreateProfileRequest {
    val staffProfile = StaffCreateProfileRequest()
    staffProfile.description = staffDescription
    staffProfile.floatingPointTag = sessionLocal.fpTag
    staffProfile.name = staffName
    staffProfile.speciality = this.speciality
    staffProfile.serviceIds = staffDetails?.serviceIds
    staffProfile.image = staffImage
    staffProfile.businessLicence = businessLicense
    staffProfile.specialisations = specializationList
    staffProfile.isAvailable = true
    staffProfile.age = staffDetails?.age
    staffProfile.gender = staffDetails?.gender
    staffProfile.experience = yearOfExperience.toIntOrNull()
    staffProfile.education = staffDetails?.education
    staffProfile.contactNumber = staffDetails?.contactNumber
    staffProfile.memberships = staffDetails?.memberships
    staffProfile.registration = staffDetails?.registration
    staffProfile.appointmentType = staffDetails?.appointmentType
    staffProfile.bookingWindow = staffDetails?.bookingWindow
    return staffProfile
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
    val request = StaffTimingAddUpdateRequest(staffId = staffDetails?.id, workTimings = this.staffDetails?.timings)
    viewModel?.updateStaffTiming(request)?.observeOnce(viewLifecycleOwner) {
      if (it?.isSuccess() == true) {
        finishAndGoBack()
      } else {
        hideProgress()
        showShortToast(it.errorFlowMessage() ?: getString(R.string.please_try_again))
      }
    }
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
    bm.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutStream) // bm is the bitmap object
    return byteArrayOutStream.toByteArray()
  }

  private fun addStaffTimings(staffId: String?) {
    if (staffDetails?.timings == null) staffDetails?.timings = AppointmentModel.getDefaultTimings()
    viewModel?.addStaffTiming(StaffTimingAddUpdateRequest(staffId = staffDetails?.id ?: staffId, staffDetails?.timings))?.observeOnce(viewLifecycleOwner) {
      if (it.isSuccess()) {
        updateStaffImage(IMAGETYPE.SIGNATURE)
      } else {
        hideProgress()
        showShortToast(it.errorFlowMessage() ?: getString(R.string.please_try_again))
      }
    }
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdfOrGif(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(this@EditDoctorsDetailsFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
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
        val mPaths = (data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as? List<String>) ?: listOf()
        if (isSignatureSelection == true) {
          isProfileImageUpdated = false
          setSignature(mPaths)
        } else {
          isProfileImageUpdated = true
          setImage(mPaths)
        }
        if (isEdit.not()) return
        when {
          isProfileImageUpdated == true && this.staffImage != null -> {
            showProgress()
            updateStaffImage(IMAGETYPE.PROFILE)
          }
          isProfileImageUpdated == false && this.staffSignature != null -> {
            showProgress()
            updateStaffImage(IMAGETYPE.SIGNATURE)
          }
        }
      }
      requestCode == Constants.REQUEST_CODE_SERVICES_PROVIDED && resultCode == AppCompatActivity.RESULT_OK -> {
        this.resultCode = resultCode
        this.servicesList = data?.extras?.get(IntentConstant.STAFF_SERVICES.name) as? ArrayList<DataItemService>
        if (servicesList.isNullOrEmpty().not()) {
          staffDetails?.serviceIds = arrayListOf()
          servicesList?.forEach { dataItem -> if (dataItem.id.isNullOrEmpty().not()) staffDetails?.serviceIds?.add(dataItem.id!!) }

        }
        binding?.ctvServices?.text = (servicesList?.map { it.name })?.joinToString(", ", limit = 5, truncated = "+${servicesList?.size?.minus(5)} more")
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
      requestCode == 101 && resultCode == AppCompatActivity.RESULT_OK -> {
        this.staffDetails = data?.extras?.get(IntentConstant.STAFF_DATA.name) as? StaffDetailsResult
      }
    }
  }

  private fun setServicesList() {
    if (isEdit) {
      viewModel?.getServiceListing(ServiceListRequest(floatingPointTag = sessionLocal.fpTag))?.observeOnce(viewLifecycleOwner) {
        if (it?.isSuccess() == true) {
          val data = (it as ServiceListResponse).result?.data
          if (staffDetails?.serviceIds.isNullOrEmpty().not()) {
            val servicesProvided = data?.filter { item -> staffDetails?.serviceIds?.contains(item?.id ?: "") == true } as? ArrayList<DataItemService>
            binding?.ctvServices?.text = servicesProvided?.map { it1 -> it1.name }?.joinToString(" ,", limit = 5, truncated = "+5 more")
            showHideServicesText()
          }
        }
      }
    }
  }

  private fun setImage(mPaths: List<String>, isUrl: Boolean = false) {
    val path = mPaths.firstOrNull() ?: ""
    this.profileimageUri = Uri.parse(path)
    this.staffProfileFile = File(path)
    if (path != "null" && path.isNotEmpty() && this.profileimageUri != null) {
      binding?.imageAddBtn?.gone()
      binding?.staffImageView?.visible()
      binding?.changeImage?.visible()
      binding?.staffImageView?.let { activity?.glideLoad(it, profileimageUri.toString(), R.drawable.placeholder_image_n) }
      if (isUrl.not() && path.isNotEmpty()) profileImageUpdated()
    } else {
      binding?.imageAddBtn?.visible()
      binding?.staffImageView?.gone()
      binding?.changeImage?.gone()
    }
  }

  private fun setSignature(mPaths: List<String>) {
    val path = mPaths.firstOrNull() ?: ""
    setSignatureView(path)
    if (path.isNotEmpty()) {
      val imageExtension = ".png"//signatureUri?.toString()?.substring(signatureUri.toString().lastIndexOf("."))
      val imageToByteArray: ByteArray = imageToByteArray(signatureUri)
      this.staffSignature = StaffImage(
        image = "data:image/png;base64,${Base64.encodeToString(imageToByteArray, Base64.DEFAULT)}",
        fileName = "${System.currentTimeMillis()}$imageExtension",
        imageFileType = imageExtension.removePrefix(".")
      )
    }

  }

  private fun setSignatureView(path: String, isUrl: Boolean = false) {
    this.signatureUri = Uri.parse(path)
    this.staffSignatureFile = File(path)
    if (path != "null" && path.isNotEmpty() && signatureUri != null) {
      binding?.layoutItemPreview?.root?.visible()
      binding?.btnUploadSignature?.gone()
      binding?.layoutItemPreview?.image?.let { activity?.glideLoad(it, signatureUri.toString(), R.drawable.placeholder_image_n) }
    } else {
      binding?.layoutItemPreview?.root?.gone()
      binding?.btnUploadSignature?.visible()
    }

    binding?.layoutItemPreview?.crossIcon?.setOnClickListener {
      binding?.layoutItemPreview?.root?.gone()
      binding?.btnUploadSignature?.visible()
      binding?.layoutItemPreview?.image?.setImageBitmap(null)
    }
    if (staffSignatureFile != null && staffSignatureFile?.size?.equals(0.0) == false)
      binding?.layoutItemPreview?.ctvSize?.text = "${staffSignatureFile?.sizeInKb} Kb"
    else binding?.layoutItemPreview?.ctvSize?.text = this.signatureUri.toString().subSequence(signatureUri?.toString()?.lastIndexOf("/")!! + 1, signatureUri?.toString()?.length!!)
    binding?.layoutItemPreview?.cbChange?.setOnClickListener {
      binding?.layoutItemPreview?.root?.gone()
      isSignatureSelection = true
      openImagePicker()
    }
  }
}