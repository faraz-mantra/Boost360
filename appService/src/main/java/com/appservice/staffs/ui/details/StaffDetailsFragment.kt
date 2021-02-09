package com.appservice.staffs.ui.details

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
import com.appservice.staffs.model.*
import com.appservice.staffs.ui.Constants
import com.appservice.staffs.ui.StaffFragmentContainerActivity
import com.appservice.staffs.ui.UserSession
import com.appservice.staffs.ui.startStaffFragmentActivity
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import com.appservice.staffs.widgets.ExperienceBottomSheet
import com.appservice.ui.catalog.common.AppointmentModel
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.framework.extensions.observeOnce
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_staff_details.*
import kotlinx.android.synthetic.main.item_preview_image.*
import kotlinx.android.synthetic.main.item_preview_image.view.*
import java.io.ByteArrayOutputStream
import java.lang.StringBuilder

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
        setOnClickListener(binding?.flAddStaffImg, binding?.rlStaffTiming, binding?.rlServiceProvided,
                binding?.rlScheduledBreaks, binding!!.btnSave, binding?.edtExperience)
        initViews()
        getBundleData()

    }

    private fun getBundleData() {
        staffDetails = arguments?.getSerializable(IntentConstant.STAFF_DATA.name) as? StaffDetailsResult
        isEdit = (staffDetails != null && staffDetails?.id.isNullOrEmpty().not())
        when {
            isEdit!! -> {
                updatePreviousData()
                (requireActivity() as StaffFragmentContainerActivity).getToolbar().getTitleTextView()?.gravity = Gravity.START

            }
            isEdit == false -> {
                (requireActivity() as StaffFragmentContainerActivity).window.statusBarColor = getColor(R.color.color_primary_dark)
                (requireActivity() as StaffFragmentContainerActivity).getToolbar().setBackgroundColor(resources.getColor(R.color.color_primary))
            }
        }
        if (staffDetails == null) {
            staffDetails = StaffDetailsResult()
        }
    }


    private fun updatePreviousData() {
        val specialisations = staffDetails?.specialisations
        setImage(listOf(staffDetails?.tileImageUrl.toString()))
        binding?.etvName?.setText(staffDetails?.name.toString())
        binding?.etvStaffDescription?.setText(staffDetails?.description.toString())
        binding?.spinnerGender?.setSelection(genderArray.toList().indexOf(staffDetails?.gender))
        binding?.cetAge?.setText(staffDetails?.age.toString())
        showHideTimingText()
        showHideServicesText()
       // setTimings()
        var selectedDays = StringBuilder()

        if (staffDetails?.timings != null) {
            for (item in staffDetails?.timings!!) {
                if (item != null && !item?.day.isNullOrEmpty() && item?.timeSlots?.isNotEmpty()) {
                    if (selectedDays.isNotEmpty()) selectedDays.append(", ")
                    selectedDays.append(item?.day)
                }
            }

            if (selectedDays.isNullOrEmpty()) {
                binding?.ctvTiming?.visibility = View.GONE
            } else {
                binding?.ctvTiming?.text = selectedDays
            }
        }

        if (specialisations?.isNullOrEmpty() == false)
            binding?.etvSpecialization?.setText(specialisations[0]?.value)
        setExperience()
        binding?.btnSave?.text = getString(R.string.update)
        binding?.toggleIsAvailable?.isOn = staffDetails?.isAvailable!!
        if (resultCode != AppCompatActivity.RESULT_OK) setServicesList()
    }

    private fun showHideTimingText() {
        when (staffDetails?.timings?.size ?: 0 > 0) {
            true -> binding?.ctvTiming?.visibility = View.VISIBLE
            else -> binding?.ctvTiming?.visibility = View.GONE
        }
    }

    private fun setExperience() {
        when {
            staffDetails?.experience!! < 2 -> {
                binding?.edtExperience?.setText("${staffDetails?.experience} Year")
            }
            else -> {
                binding?.edtExperience?.setText("${staffDetails?.experience} Years")
            }
        }
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
        experienceSheet.show(this@StaffDetailsFragment.parentFragmentManager, ExperienceBottomSheet::class.java.name)
    }

    private fun initViews() {
        this.genderArray = arrayOf("Male", "Female", "Please select")
        binding!!.spinnerGender.setHintAdapter(requireContext(), list = genderArray)
        binding?.toggleIsAvailable?.isOn = true
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.flAddStaffImg -> {
                openImagePicker()
            }
            binding?.rlStaffTiming -> {
                val bundle = Bundle()
                bundle.putSerializable(IntentConstant.STAFF_DATA.name, staffDetails)
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_TIMING_FRAGMENT, bundle, clearTop = false, isResult = true, requestCode = Constants.REQUEST_CODE_STAFF_TIMING)
            }
            binding?.rlServiceProvided -> {
                val bundle = Bundle()
                if (staffDetails?.serviceIds.isNullOrEmpty().not()) {
                    val serviceIds = staffDetails?.serviceIds as ArrayList
                    bundle.putSerializable(IntentConstant.STAFF_SERVICES.name, serviceIds)
                }
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_SELECT_SERVICES_FRAGMENT, bundle, clearTop = false, isResult = true, requestCode = Constants.REQUEST_CODE_SERVICES_PROVIDED)
            }
            binding?.rlScheduledBreaks -> {
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_SCHEDULED_BREAK_FRAGMENT, clearTop = false, isResult = true, requestCode = Constants.REQUEST_CODE_SCHEDULED_BREAK)
            }
            binding?.edtExperience -> {
                openExperienceDetail()
            }
            binding?.btnSave -> {
                if ((isEdit == false || isEdit == null) && isValid()) {
                    createStaffProfile()
                }
                if (isEdit == true && isValid()) {
                    updateStaffProfile()

                }
            }
        }
    }

    private fun updateStaffImage() {
        showProgress(getString(R.string.uploading_image))
        viewModel?.updateStaffImage(StaffUpdateImageRequest(staffDetails?.id, staffImage))?.observeOnce(viewLifecycleOwner, Observer {
            hideProgress()
            when (it.status) {
                200 -> {
                }
                else -> {

                }
            }

        })
    }

    private fun updateStaffProfile() {
        val staffGender = binding?.spinnerGender?.selectedItem.toString()
        viewModel?.updateStaffProfile(
                StaffProfileUpdateRequest(isAvailable, staffDetails?.serviceIds, staffGender, UserSession.fpId, name = staffName, staffDescription,
                        experience = yearOfExperience.toInt(), staffDetails?.id, staffAge, specializationList
                ))?.observeOnce(viewLifecycleOwner, Observer { t ->
            when (t.status) {
                200 -> {
                    updateStaffTimings()
                }
                else -> {
                    showShortToast(getString(R.string.something_went_wrong))
                }
            }


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

        if (imageUri.toString() == "null" || imageUri == null || imageUri.toString().isEmpty() || imageUri.toString().isBlank()) {
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
        } else if (!this::yearOfExperience.isInitialized || yearOfExperience.equals("null", ignoreCase = true)) {
            showLongToast(getString(R.string.select_year_of_experience))
            return false
        }

        specializationList.add(SpecialisationsItem(specialization, "key"))
        servicesList?.forEach { serviceListId?.add(it.id!!) }
        if (serviceListId.isNullOrEmpty()) serviceListId = null
        if (isImageUpdated == true) {
            val imageExtension: String? = imageUri?.toString()?.substring(imageUri.toString().lastIndexOf("."))
            val imageToByteArray: ByteArray = imageToByteArray()
            this.staffImage = StaffImage(image = "data:image/png;base64,${Base64.encodeToString(imageToByteArray, Base64.DEFAULT)}", fileName = "$staffName$imageExtension", imageFileType = imageExtension?.removePrefix("."))
        }

        if (isEdit == null || isEdit == false) {
            staffProfile?.age = staffAge
            staffProfile?.isAvailable = isAvailable
            staffProfile?.description = staffDescription
            staffProfile?.gender = binding?.spinnerGender?.selectedItem.toString()
            staffProfile?.experience = yearOfExperience.toIntOrNull() ?: 0
            staffProfile?.floatingPointTag = UserSession.fpId
            staffProfile?.name = staffName
            staffProfile?.serviceIds = serviceListId
            staffProfile?.image = staffImage
            staffProfile?.specialisations = specializationList
        }
        return true
    }

    private fun createStaffProfile() {
        showProgress(getString(R.string.profile_created))
        viewModel?.createStaffProfile(staffProfile)?.observe(viewLifecycleOwner, Observer { t ->
            hideProgress()
            when (t.status) {
                200 -> {
                    showShortToast(getString(R.string.profile_created))
                    // get the id from result
//                    addStaffTimings()

                    addStaffTimings((t as StaffCreateProfileResponse).result)
                }
                else -> {
                    showShortToast(getString(R.string.something_went_wrong))
                }
            }
        })
    }


    private fun updateStaffTimings() {
        if (staffDetails?.timings == null && isTimingUpdated == null || isTimingUpdated == false) {
            finishAndGoBack();
            return;
        }
        showProgress(getString(R.string.staff_timings_updating))
        viewModel?.updateStaffTiming(StaffTimingAddUpdateRequest(staffId = staffDetails?.id, this.staffDetails?.timings!!))?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            hideProgress()
            when (it.status) {
                200 -> {
                    Log.v(getString(R.string.staff_timings), getString(R.string.staff_timings_added))
                    finishAndGoBack()
                }
                else -> {
                    Log.v(getString(R.string.staff_timings), getString(R.string.something_went_wrong))
                }
            }
        })
    }


    private fun finishAndGoBack() {
        val intent = Intent();
        intent.putExtra(IntentConstant.STAFF_DATA.name, staffDetails);
        requireActivity().setResult(AppCompatActivity.RESULT_OK, intent);
        requireActivity().finish();
    }

    private fun imageToByteArray(): ByteArray {
        val bm: Bitmap = BitmapFactory.decodeFile(imageUri?.toString())
        val byteArrayOutStream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutStream) // bm is the bitmap object
        return byteArrayOutStream.toByteArray()
    }

    private fun addStaffTimings(staffId: String?) {
        if (staffDetails?.timings == null) {
            staffDetails?.timings = AppointmentModel.getDefaultTimings()
           /* finishAndGoBack();
            return;*/
        }

        showProgress(getString(R.string.staff_timing_add))
        viewModel?.addStaffTiming(StaffTimingAddUpdateRequest(staffId = staffDetails?.id
                ?: staffId, staffDetails?.timings))?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            hideProgress()
            when (it.status) {
                200 -> {
                    Log.v(getString(R.string.staff_timings), getString(R.string.staff_timings_added))
                    finishAndGoBack()
                }
                else -> {
                    Log.v(getString(R.string.staff_timings), getString(R.string.something_went_wrong))
                }
            }
        })
    }

    private fun openImagePicker() {
        val filterSheet = ImagePickerBottomSheet()
        filterSheet.isHidePdf(true)
        filterSheet.onClicked = { openImagePicker(it) }
        filterSheet.show(this@StaffDetailsFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
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
                this.servicesList?.clear()
                this.servicesList = data!!.extras!![IntentConstant.STAFF_SERVICES.name] as ArrayList<DataItemService>
                servicesList?.forEach { dataItem -> serviceListId?.add(dataItem.id!!) }
                if (staffDetails?.serviceIds == null) staffDetails?.serviceIds = arrayListOf()
                staffDetails?.serviceIds = servicesList?.map { it.id }
                if (staffDetails?.serviceIds.isNullOrEmpty()) staffDetails?.serviceIds =null
                binding!!.ctvServices.text = (servicesList?.map { it.name })?.joinToString(", ", limit = 5, truncated = "+${servicesList?.size?.minus(5)} more")
                showHideServicesText()
            }
            requestCode == Constants.REQUEST_CODE_STAFF_TIMING && resultCode == AppCompatActivity.RESULT_OK -> {
                this.staffDetails = data!!.extras!![IntentConstant.STAFF_TIMINGS.name] as StaffDetailsResult
                setTimings()
                showHideTimingText()
                var selectedDays = StringBuilder()
                for (item in staffDetails?.timings!!) {
                    if (item.isTurnedOn == true) {
                        if (selectedDays.isNotEmpty()) selectedDays.append(", ")
                        selectedDays.append(item.day)
                    }
                }
                binding?.ctvTiming?.text = selectedDays
                isTimingUpdated = true
            }
        }
        when (isEdit == true && imageIsChange != null && imageIsChange == true && isValid()) {
            true -> updateStaffImage()
        }


    }

    private fun setTimings() {
        staffDetails?.timings?.filter { it?.timeSlots?.isNullOrEmpty().not() }?.map { it.day }?.joinToString(separator = ", ", truncated = "...")
    }

    private fun setServicesList() {
        if (isEdit == true) {
            viewModel!!.getServiceListing(ServiceListRequest(floatingPointTag = UserSession.fpId)
            ).observeOnce(viewLifecycleOwner, Observer {
                when (it.status) {
                    200 -> {
                        val data = (it as ServiceListResponse).result!!.data!!
                        if (staffDetails?.serviceIds.isNullOrEmpty().not()) {
                            val servicesProvided = data.filter { item -> staffDetails?.serviceIds!!.contains(item?.id) } as ArrayList<DataItemService>
                            binding!!.ctvServices.text = servicesProvided.map { it.name }.joinToString(" ,", limit = 5, truncated = "+5 more")
                            showHideServicesText()
                        }
                    }
                    else -> {
                    }
                }
            })
        }
    }

    private fun setImage(mPaths: List<String>) {
        this.imageUri = Uri.parse(mPaths[0])
        let { activity?.glideLoad(binding?.civStaffImg!!, imageUri.toString(), R.drawable.ic_staff_img_blue) }
        binding?.ctvImgChange?.text = getString(R.string.change_picture)
        binding?.ctvImgChange?.visibility = View.VISIBLE
        binding?.ctvImgChange?.setTextColor(getColor(R.color.black_4a4a4a))

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
    val hintAdapter =
            HintAdapter(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    list
            )
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