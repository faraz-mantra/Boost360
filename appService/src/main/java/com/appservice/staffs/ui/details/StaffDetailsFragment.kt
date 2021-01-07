package com.appservice.staffs.ui.details

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentStaffDetailsBinding
import com.appservice.staffs.model.DataItem
import com.appservice.staffs.ui.Constants
import com.appservice.staffs.ui.home.startStaffFragmentActivity
import com.appservice.ui.catlogService.widgets.ClickType
import com.appservice.ui.catlogService.widgets.ImagePickerBottomSheet
import com.framework.imagepicker.ImagePicker
import com.framework.views.customViews.customSpinner.SpinnerHintAdapter
import kotlinx.android.synthetic.main.fragment_staff_details.*
import kotlinx.android.synthetic.main.item_preview_image.*
import kotlinx.android.synthetic.main.item_preview_image.view.*

class StaffDetailsFragment : AppBaseFragment<FragmentStaffDetailsBinding, StaffDetailsViewModel>() {
//    private var staffCreateProfileRequest = StaffCreateProfileRequest()

    private lateinit var servicesList: List<DataItem>

    companion object {
        fun newInstance(): StaffDetailsFragment {
            return StaffDetailsFragment()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_staff_details
    }

    override fun getViewModelClass(): Class<StaffDetailsViewModel> {
        return StaffDetailsViewModel::class.java
    }

    override fun onCreateView() {

        setOnClickListener(binding?.flAddStaffImg, binding?.rlStaffTiming, binding?.rlServiceProvided, binding?.rlScheduledBreaks, binding!!.flSavePublish)
        binding!!.toggleYesNo.setOnToggledListener { toggleableView, isOn ->
//            when (isOn) {
////                true ->
////                else ->
//            }
        }
        val gender = mutableListOf("Male", "Female")
        val genderAdapter = SpinnerHintAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, gender, false)
        genderAdapter.hint = "Select Gender"
        binding!!.csGender.adapter = genderAdapter

    }


    override fun onClick(v: View) {
        super.onClick(v)
        val bundle: Bundle = Bundle.EMPTY
        when (v) {
            binding?.flAddStaffImg -> {
                openImagePicker()
            }
            binding?.rlStaffTiming -> {
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_TIMING_FRAGMENT, bundle, clearTop = false, isResult = true, requestCode = Constants.REQUEST_CODE_STAFF_TIMING)
            }
            binding?.rlServiceProvided -> {
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_SELECT_SERVICES_FRAGMENT, bundle, clearTop = false, isResult = true, requestCode = Constants.REQUEST_CODE_SERVICES_PROVIDED)
            }
            binding?.rlScheduledBreaks -> {
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_SCHEDULED_BREAK_FRAGMENT, bundle, clearTop = false, isResult = true, requestCode = Constants.REQUEST_CODE_SCHEDULED_BREAK)
            }
            binding?.flSavePublish -> {
                //validation
                when {
                    binding!!.etvName.text.isNullOrBlank()
                            || binding!!.etvStaffDescription.text.isNullOrBlank() -> {
                        showShortToast("Dont Leave fields Blank")
                    }
                    else -> {
                        val experience = binding!!.csExperience.selectedItem
                        val serviceListId = ArrayList<String>()
                        servicesList.forEach { serviceListId.add(it.id!!) }
//                        StaffCreateProfileRequest(serviceListId, ,)
                    }
                }

            }
        }
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
            }
            requestCode == Constants.REQUEST_CODE_SERVICES_PROVIDED && resultCode == AppCompatActivity.RESULT_OK -> {
                this.servicesList = data!!.extras!![Constants.SERVICES_LIST] as List<DataItem>
                setServicesList()
            }
            requestCode == Constants.REQUEST_CODE_STAFF_TIMING && resultCode == AppCompatActivity.RESULT_OK -> {
            }
        }

    }

    private fun setServicesList() {
        val services = StringBuilder()
        servicesList.forEach { dataItem -> services.append("${dataItem.name}, ") }
        binding!!.ctvServices.text = services
    }

    private fun setImage(mPaths: List<String>) {
        binding?.civStaffImg?.setImageURI(Uri.parse(mPaths[0]))
        binding?.ctvImgChange?.text = getString(R.string.change_picture)
        binding?.ctvImgChange?.setTextColor(getColor(R.color.black_4a4a4a))
        binding?.ctvImgChange?.setBackgroundColor(Color.WHITE)
        binding?.flAddStaffImg?.setPadding(2, 2, 2, 2);
        binding?.flAddStaffImg?.backgroundTintList = ColorStateList.valueOf(getColor(R.color.gray_light_4))

    }
}