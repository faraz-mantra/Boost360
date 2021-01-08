package com.appservice.staffs.ui.details

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentStaffDetailsBinding
import com.appservice.staffs.model.*
import com.appservice.staffs.ui.Constants
import com.appservice.staffs.ui.home.UserSession
import com.appservice.staffs.ui.home.startStaffFragmentActivity
import com.appservice.ui.catlogService.widgets.ClickType
import com.appservice.ui.catlogService.widgets.ImagePickerBottomSheet
import com.framework.imagepicker.ImagePicker
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_staff_details.*
import kotlinx.android.synthetic.main.item_preview_image.*
import kotlinx.android.synthetic.main.item_preview_image.view.*
import java.io.ByteArrayOutputStream

class StaffDetailsFragment : AppBaseFragment<FragmentStaffDetailsBinding, StaffDetailsViewModel>() {
    private var imageUri: Uri? = null
    private var gender: String? = null
    private var experience: Int = 0
    private var isAvailable: Boolean = true
    private var isImageChosen: Boolean = false

    private lateinit var servicesList: List<DataItemService>

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
            isAvailable = when (isOn) {
                true -> true
                else -> false
            }
        }
        val genderAdapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, mutableListOf("Select Gender", "Male", "Female"))
        binding!!.csGender.adapter = genderAdapter
        binding!!.csGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when {
                    p2 > 0 -> {
                        gender = p0?.getItemAtPosition(p2).toString()
                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                gender = null
            }

        }
        val experienceAdapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, (1..10).toMutableList())
        binding!!.csExperience.adapter = experienceAdapter
        binding!!.csExperience.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                experience = p0?.getItemAtPosition(p2) as Int
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }


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
                            || binding!!.etvStaffDescription.text.isNullOrBlank()
                            || gender.isNullOrBlank() || experience.equals(0) || !isImageChosen || binding?.etvSpecialization?.text.isNullOrBlank() -> {
                        showShortToast("Don't Leave fields Blank")
                    }
                    else -> {
                        val imageExtension: String = imageUri.toString().substring(imageUri.toString().lastIndexOf("."))
                        val byteArrayImage: ByteArray = imageToByteArray()
                        val specialization = binding?.etvSpecialization?.text.toString()
                        val staffName = binding?.etvName?.text.toString()
                        val specializationList = ArrayList<SpecialisationsItemStaffRequest>()
                        for ((index, s) in specialization.split(" ").withIndex()) {
                            specializationList.add(SpecialisationsItemStaffRequest(s, "key$index"))
                        }
                        val description = binding?.etvStaffDescription?.text.toString()
                        val serviceListId = ArrayList<String>()
                        servicesList.forEach { serviceListId.add(it.id!!) }
                        val staffCreateProfileRequest = StaffCreateProfileRequest(serviceIds = serviceListId, experience = experience.toDouble(), description = description,
                                specialisations = specializationList, isAvailable = isAvailable, floatingPointTag = UserSession.fpId, image = Image(imageFileType = imageExtension,
                                fileName = "$staffName.$imageExtension", image = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)), name = staffName)
                        viewModel?.createStaffProfile(staffCreateProfileRequest)?.observe(viewLifecycleOwner, Observer { t ->
                            println(t.message + " " + t.status)
                            showLongToast("created successfully")
                        })
                    }
                }

            }
        }
    }

    private fun imageToByteArray(): ByteArray {
        val bm: Bitmap = BitmapFactory.decodeFile(imageUri.toString())
        val byteArrayOutStream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutStream) // bm is the bitmap object
        return byteArrayOutStream.toByteArray()
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
                this.servicesList = data!!.extras!![Constants.SERVICES_LIST] as List<DataItemService>
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
        isImageChosen = true
        this.imageUri = Uri.parse(mPaths[0])
        binding?.civStaffImg?.setImageURI(imageUri)
        binding?.ctvImgChange?.text = getString(R.string.change_picture)
        binding?.ctvImgChange?.setTextColor(getColor(R.color.black_4a4a4a))
        binding?.ctvImgChange?.setBackgroundColor(Color.WHITE)
        binding?.flAddStaffImg?.setPadding(2, 2, 2, 2);
        binding?.flAddStaffImg?.backgroundTintList = ColorStateList.valueOf(getColor(R.color.gray_light_4))

    }
}