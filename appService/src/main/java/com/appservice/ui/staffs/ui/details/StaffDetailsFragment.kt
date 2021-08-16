package com.appservice.ui.staffs.ui.details

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentStaffDetailsBinding
import com.appservice.model.staffModel.*
import com.appservice.ui.staffs.ui.Constants
import com.appservice.ui.staffs.ui.StaffFragmentContainerActivity
import com.appservice.ui.staffs.ui.startStaffFragmentActivity
import com.appservice.ui.staffs.ui.viewmodel.StaffViewModel
import com.appservice.ui.staffs.widgets.ExperienceBottomSheet
import com.appservice.ui.catalog.common.AppointmentModel
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.ui.staffs.UserSession
import com.appservice.utils.WebEngageController
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.models.firestore.FirestoreManager
import com.framework.webengageconstant.*
import java.io.ByteArrayOutputStream

class StaffDetailsFragment : AppBaseFragment<FragmentStaffDetailsBinding, StaffViewModel>() {
  private var isTimingUpdated: Boolean? = null
  private var isImageUpdated: Boolean? = null
  private var imageIsChange: Boolean? = null
  private var resultCode: Int = 1
  private var isAvailable: Boolean? = false
  private lateinit var yearOfExperience: String
  private lateinit var staffDescription: String
  private var staffAge: Int? = null
  private lateinit var staffName: String
  private lateinit var specializationList: ArrayList<SpecialisationsItem>
  private var serviceListId: ArrayList<String>? = null
  private lateinit var specialization: String
  private var staffImage: StaffImage? = null
  private lateinit var genderArray: Array<String>
  private var imageUri: Uri? = null
  private var isEdit: Boolean? = null
  private var staffProfile: StaffCreateProfileRequest? = StaffCreateProfileRequest()
  private var servicesList: ArrayList<DataItemService>? = null
  private var staffDetails: StaffDetailsResult? = null

  companion object {
    fun newInstance(): StaffDetailsFragment {
      return StaffDetailsFragment()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_staff_details
  }

  override fun getViewModelClass(): Class<StaffViewModel> {
    return StaffViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(
      binding?.flAddStaffImg,
      binding?.rlStaffTiming,
      binding?.rlServiceProvided,
      binding?.rlScheduledBreaks,
      binding?.btnSave,
      binding?.edtExperience
    )
    initViews()
    getBundleData()
  }

  private fun getBundleData() {
    staffDetails = arguments?.getSerializable(IntentConstant.STAFF_DATA.name) as? StaffDetailsResult
    isEdit = (staffDetails != null && staffDetails?.id.isNullOrEmpty().not())
    if (isEdit == true) {
      updatePreviousData()
      (requireActivity() as StaffFragmentContainerActivity).getToolbar()
        ?.getTitleTextView()?.gravity = Gravity.START
    } else {
      (requireActivity() as StaffFragmentContainerActivity).window.statusBarColor =
        getColor(R.color.color_primary_dark)
      (requireActivity() as StaffFragmentContainerActivity).getToolbar()
        ?.setBackgroundColor(resources.getColor(R.color.color_primary))
    }
    if (staffDetails == null) staffDetails = StaffDetailsResult()
  }


  private fun updatePreviousData() {
    val specialisations = staffDetails?.specialisations
    setImage(listOf(staffDetails?.tileImageUrl.toString()))
    binding?.etvName?.setText(staffDetails?.name.toString())
    binding?.etvStaffDescription?.setText(staffDetails?.description.toString())
    binding?.spinnerGender?.setSelection(genderArray.toList().indexOf(staffDetails?.gender))
    binding?.cetAge?.setText(staffDetails?.age.toString())
    showHideServicesText()
    // setTimings()
    if (staffDetails?.timings.isNullOrEmpty().not()) {
      val textStaffDays = AppointmentModel().getStringStaffActive(staffDetails?.timings)
      binding?.ctvTiming?.visibility = if (textStaffDays.isEmpty()) View.GONE else View.VISIBLE
      binding?.ctvTiming?.text = textStaffDays
    } else binding?.ctvTiming?.gone()

    if (specialisations?.isNullOrEmpty() == false) binding?.etvSpecialization?.setText(
      specialisations[0].value
    )
    setExperience()
    binding?.btnSave?.text = getString(R.string.update)
    binding?.toggleIsAvailable?.isOn = staffDetails?.isAvailable ?: false
    if (resultCode != AppCompatActivity.RESULT_OK) setServicesList()
  }

  private fun setExperience() {
    binding?.edtExperience?.setText("${staffDetails?.experience}".plus(if (staffDetails?.experience ?: 0 < 2) " Year" else " Years"))

  }

  private fun showHideServicesText() {
    when (staffDetails?.serviceIds?.size ?: 0 > 0) {
      true -> binding?.ctvServices?.visibility = View.VISIBLE
      else -> binding?.ctvServices?.visibility = View.GONE
    }
  }

  private fun openExperienceDetail() {
    val experienceSheet = ExperienceBottomSheet()
    experienceSheet.onClicked = {
      yearOfExperience = it.toString()
      staffDetails?.experience = it
      staffProfile?.experience = it
      setExperience()
    }
    experienceSheet.show(
      this@StaffDetailsFragment.parentFragmentManager,
      ExperienceBottomSheet::class.java.name
    )
  }

  private fun initViews() {
    this.genderArray = arrayOf("Male", "Female", "Please select")
    binding?.spinnerGender?.setHintAdapter(requireContext(), list = genderArray)
    binding?.toggleIsAvailable?.isOn = true
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.flAddStaffImg -> openImagePicker()
      binding?.rlStaffTiming -> {
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
      binding?.rlServiceProvided -> {
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
      binding?.rlScheduledBreaks -> {
        startStaffFragmentActivity(
          baseActivity,
          FragmentType.STAFF_SCHEDULED_BREAK_FRAGMENT,
          clearTop = false,
          isResult = true,
          requestCode = Constants.REQUEST_CODE_SCHEDULED_BREAK
        )
      }
      binding?.edtExperience -> {
        openExperienceDetail()
      }
      binding?.btnSave -> {
        if ((isEdit == false) && isValid()) createStaffProfile()
        if (isEdit == true && isValid()) updateStaffProfile()
      }
    }
  }

  private fun updateStaffImage() {
    showProgress(getString(R.string.uploading_image))
    viewModel?.updateStaffImage(StaffUpdateImageRequest(staffDetails?.id, staffImage))
      ?.observeOnce(viewLifecycleOwner, Observer {
        hideProgress()
        if (it.isSuccess().not()) showShortToast(
          it.errorMessage() ?: getString(R.string.something_went_wrong)
        )
      })
  }

  private fun updateStaffProfile() {
    val staffGender = binding?.spinnerGender?.selectedItem.toString()
    val request = StaffProfileUpdateRequest(
      isAvailable,
      staffDetails?.serviceIds,
      staffGender,
      UserSession.fpTag,
      name = staffName,
      staffDescription,
      experience = yearOfExperience.toInt(),
      staffDetails?.id,
      staffAge,
      specializationList
    )
    viewModel?.updateStaffProfile(request)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.isSuccess()) {
        updateStaffTimings()
        WebEngageController.trackEvent(STAFF_PROFILE_UPDATED, ADDED, NO_EVENT_VALUE)
      } else showShortToast(it?.errorMessage() ?: getString(R.string.something_went_wrong))
    })

  }

  private fun isValid(): Boolean {
    this.specialization = binding?.etvSpecialization?.text.toString()
    this.serviceListId = ArrayList()
    this.specializationList = ArrayList()
    this.yearOfExperience = staffDetails?.experience.toString()
    this.staffName = binding?.etvName?.text.toString()
    this.staffAge = binding?.cetAge?.text.toString().toIntOrNull()
    this.staffDescription = binding?.etvStaffDescription?.text.toString()
    val staffGender = binding?.spinnerGender?.isHintSelected()
    this.isAvailable = binding?.toggleIsAvailable?.isOn
    servicesList?.forEach { items ->
      if (items.id.isNullOrEmpty().not()) serviceListId?.add(items.id!!)
    }
    if (serviceListId.isNullOrEmpty().not()) staffDetails?.serviceIds = serviceListId
    if (imageUri.toString() == "null" || imageUri == null || imageUri.toString()
        .isEmpty() || imageUri.toString().isBlank()
    ) {
      showLongToast(getString(R.string.please_choose_image))
      return false
    } else if (staffName.isBlank()) {
      showLongToast(getString(R.string.enter_staff_name))
      return false
    } else if (staffGender == null || staffGender) {
      showLongToast(getString(R.string.select_staff_gender))
      return false
    } else if (staffAge == null) {
      showLongToast(getString(R.string.please_enter_your_age))
      return false
    } else if (staffAge == 0 || staffAge ?: 0 >= 100) {
      showLongToast(getString(R.string.please_enter_valid_age))
      return false
    } else if (specialization.isEmpty()) {
      showLongToast(getString(R.string.please_add_specialization))
      return false
    } else if (!this::yearOfExperience.isInitialized || yearOfExperience.equals(
        "null",
        ignoreCase = true
      )
    ) {
      showLongToast(getString(R.string.select_year_of_experience))
      return false
    } else if (staffDetails?.serviceIds.isNullOrEmpty()) {
      showLongToast(getString(R.string.error_select_service))
      return false
    }

    specializationList.add(SpecialisationsItem(specialization, "key"))

    if (isImageUpdated == true) {
      val imageExtension: String? =
        imageUri?.toString()?.substring(imageUri.toString().lastIndexOf("."))
      val imageToByteArray: ByteArray = imageToByteArray()
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

    if (isEdit == null || isEdit == false) {
      staffProfile?.age = staffAge
      staffProfile?.isAvailable = isAvailable
      staffProfile?.description = staffDescription
      staffProfile?.gender = binding?.spinnerGender?.selectedItem.toString()
      staffProfile?.experience = yearOfExperience.toIntOrNull() ?: 0
      staffProfile?.floatingPointTag = UserSession.fpTag
      staffProfile?.name = staffName
      staffProfile?.serviceIds = serviceListId
      staffProfile?.image = staffImage
      staffProfile?.specialisations = specializationList
    }
    return true
  }

  private fun createStaffProfile() {
    showProgress()
    viewModel?.createStaffProfile(staffProfile)?.observe(viewLifecycleOwner, { t ->
      if (t.isSuccess()) {
        addStaffTimings((t as StaffCreateProfileResponse).result)
        showShortToast(getString(R.string.profile_created))
        onStaffAddedOrUpdated()
        WebEngageController.trackEvent(STAFF_PROFILE_CREATE, ADDED, NO_EVENT_VALUE)
      } else showShortToast(getString(R.string.something_went_wrong))
      hideProgress()
    })
  }

  private fun onStaffAddedOrUpdated() {
    val instance = FirestoreManager
    instance.getDrScoreData()?.metricdetail?.boolean_create_staff = true
    instance.updateDocument()
  }

  private fun updateStaffTimings() {
    if (staffDetails?.timings == null && isTimingUpdated == null || isTimingUpdated == false) {
      finishAndGoBack()
      return
    }
    showProgress(getString(R.string.staff_timings_updating))
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

  private fun imageToByteArray(): ByteArray {
    val bm: Bitmap = BitmapFactory.decodeFile(imageUri?.toString())
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
        finishAndGoBack()
      } else showShortToast(it.errorMessage() ?: getString(R.string.something_went_wrong))
    })
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(
      this@StaffDetailsFragment.parentFragmentManager,
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
        setImage(mPaths)
        isImageUpdated = true
        if (isEdit == true)
          imageIsChange = true
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
    }
    when (isEdit == true && imageIsChange != null && imageIsChange == true && isValid()) {
      true -> updateStaffImage()
    }
  }

  private fun setServicesList() {
    if (isEdit == true) {
      viewModel?.getServiceListing(ServiceListRequest(floatingPointTag = UserSession.fpTag))
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
    this.imageUri = Uri.parse(mPaths[0])
    binding?.civStaffImg?.let {
      activity?.glideLoad(
        it,
        imageUri.toString(),
        R.drawable.placeholder_image_n
      )
    }
    binding?.ctvImgChange?.text = getString(R.string.change_picture)
    binding?.ctvImgChange?.visibility = View.VISIBLE
    binding?.ctvImgChange?.setTextColor(getColor(R.color.black_4a4a4a))
    if (binding?.ctvImgChange?.visibility == View.VISIBLE) binding?.civStaffImg?.borderColor =
      getColor(R.color.pinkish_grey)
  }
}

class HintAdapter<T>(context: Context, resource: Int, objects: Array<T>) :
  ArrayAdapter<T>(context, resource, objects) {
  override fun getCount(): Int {
    val count = super.getCount()
    // The last item will be the hint.
    return if (count > 0) count - 1 else count
  }

  override fun isEnabled(position: Int): Boolean {
    return when (position) {
      count -> false
      else -> true
    }
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
    val view = super.getDropDownView(position, convertView, parent)
    val textView = view as TextView
    if (position == count)
      textView.setTextColor(Color.GRAY)
    else textView.setTextColor(Color.BLACK)
    return view
  }


}

fun Spinner.setHintAdapter(context: Context, list: Array<String>) {
  val hintAdapter = HintAdapter(context, android.R.layout.simple_spinner_dropdown_item, list)
  adapter = hintAdapter
  setSelection(count)
  onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
      val textView = view as TextView
      when (position) {
        count -> textView.setTextColor(Color.GRAY)
        else -> textView.setTextColor(Color.BLACK)
      }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

  }
}

fun Spinner.isHintSelected(): Boolean {
  return selectedItemPosition == count
}