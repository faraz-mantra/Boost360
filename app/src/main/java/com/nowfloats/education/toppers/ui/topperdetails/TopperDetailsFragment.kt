package com.nowfloats.education.toppers.ui.topperdetails

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.boost.upgrades.utils.Utils
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nowfloats.education.helper.BaseFragment
import com.nowfloats.education.helper.Constants.CAMERA_REQUEST_CODE
import com.nowfloats.education.helper.Constants.GALLERY_REQUEST_CODE
import com.nowfloats.education.helper.Constants.SAVE
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.Constants.TOPPER_PROFILE_IMAGE
import com.nowfloats.education.helper.Constants.TOPPER_TESTIMONIAL_IMAGE
import com.nowfloats.education.helper.Constants.UPDATE
import com.nowfloats.education.helper.PermissionsHelper
import com.nowfloats.education.helper.RuntimePermissionListener
import com.nowfloats.education.helper.getBitmapFromUri
import com.nowfloats.education.toppers.ToppersActivity
import com.nowfloats.education.toppers.model.Data
import com.nowfloats.education.toppers.model.Topper
import com.nowfloats.util.Methods
import com.thinksity.R
import com.thinksity.databinding.ToppersDetailsBinding
import kotlinx.android.synthetic.main.bottom_sheet_camera_gallery.*
import org.koin.android.ext.android.inject

class TopperDetailsFragment(private val topperData: Data?, private val isEditing: Boolean) : BaseFragment(), RuntimePermissionListener {

    constructor() : this(null, false)

    private lateinit var binding: ToppersDetailsBinding
    private val viewModel by inject<TopperDetailsViewModel>()
    private var addUpdateTopper = false
    private lateinit var permissionsHelper: PermissionsHelper
    private var photoURI: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = ToppersDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHeader(view)

        when {
            topperData != null -> {
                binding.toppersData = topperData
                binding.addUpdateTopperButton.text = UPDATE
                addUpdateTopper = true
                viewModel.setProfileImagePath(null)
                viewModel.setTestimonialImagePath(null)
            }
            else -> {
                binding.toppersData = Data()
                binding.addUpdateTopperButton.text = SAVE
                addUpdateTopper = false
            }
        }

        binding.addUpdateTopperButton.setOnClickListener {
            Methods.hideKeyboard(requireActivity())
            addUpdateTopperData()
        }

        val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        permissionsHelper = PermissionsHelper(requireActivity(), permissions)

        binding.toppersProfileImage.setOnClickListener {
            viewModel.topperImageFlag = Topper.PROFILE
            permissionsHelper.askPermission(this)
        }

        binding.topperTestimonialImage.setOnClickListener {
            viewModel.topperImageFlag = Topper.TESTIMONIAL
            permissionsHelper.askPermission(this)
        }

        binding.icRemoveTopperProfileImage.setOnClickListener {
            viewModel.setProfileImagePath(null)
            binding.toppersProfileImage.setImageDrawable(null)
            binding.icRemoveTopperProfileImage.visibility = View.GONE
        }

        binding.icRemoveTopperTestimonialImage.setOnClickListener {
            viewModel.setTestimonialImagePath(null)
            binding.topperTestimonialImage.setImageDrawable(null)
            binding.icRemoveTopperTestimonialImage.visibility = View.GONE
        }

        initLiveDataObservables()
    }

    private fun addUpdateTopperData() {
        if (!addUpdateTopper) {
            if (validate() && imageValidate()) {
                showLoader(getString(R.string.loadin_images))
                if (viewModel.getTestimonialImagePath() != null) {
                    viewModel.getToppersImagesUrl(viewModel.getProfileImagePath()!!, viewModel.getTestimonialImagePath()!!)
                } else {
                    viewModel.getToppersImagesUrl(viewModel.getProfileImagePath()!!, null)
                }
            }
        } else {
            if (validate()) {
                when {
                    viewModel.getProfileImagePath() != null && viewModel.getTestimonialImagePath() != null -> {
                        showLoader(getString(R.string.updating_image_))
                        viewModel.getToppersImagesUrl(viewModel.getProfileImagePath(), viewModel.getTestimonialImagePath())
                    }
                    viewModel.getProfileImagePath() != null -> {
                        showLoader(getString(R.string.updating_image_))
                        viewModel.getToppersImagesUrl(viewModel.getProfileImagePath(), null)
                    }
                    viewModel.getTestimonialImagePath() != null -> {
                        showLoader(getString(R.string.updating_image_))
                        viewModel.getToppersImagesUrl(null, viewModel.getTestimonialImagePath())
                    }
                    else -> {
                        showLoader(getString(R.string.updating_image_))
                        viewModel.updateOurTopper(binding.toppersData as Data, null, null)
                    }
                }
            }
        }
    }

    private fun initLiveDataObservables() {
        viewModel.apply {
            addTopperResponse.observe(viewLifecycleOwner, Observer {
                hideLoader()
                if (!it.isNullOrBlank()) {
                    Toast.makeText(requireContext(), getString(R.string.topper_added_successfully), Toast.LENGTH_SHORT).show()
                    (activity as ToppersActivity).popFragmentFromBackStack()
                }
            })

            errorResponse.observe(viewLifecycleOwner, Observer {
                hideLoader()
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            })

            deleteTopperResponse.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrBlank()) {
                    if (it == SUCCESS) {
                        hideLoader()
                        Toast.makeText(requireContext(), getString(R.string.topper_deleted_successfully), Toast.LENGTH_SHORT).show()
                        (activity as ToppersActivity).popFragmentFromBackStack()
                    }
                }
            })

            updateTopperResponse.observe(viewLifecycleOwner, Observer {
                hideLoader()
                if (!it.isNullOrBlank()) {
                    Toast.makeText(requireContext(), getString(R.string.topper_updated_successfully), Toast.LENGTH_SHORT).show()
                    (activity as ToppersActivity).popFragmentFromBackStack()
                }
            })

            uploadImageResponse.observe(viewLifecycleOwner, Observer {
                hideLoader()
                var profileImageUrl: String? = null
                var testimonialImageUrl: String? = null
                it.forEach { imageModel ->
                    if (imageModel.imageType == TOPPER_PROFILE_IMAGE) profileImageUrl = imageModel.url
                    if (imageModel.imageType == TOPPER_TESTIMONIAL_IMAGE) testimonialImageUrl = imageModel.url
                }
                if (addUpdateTopper) {
                    showLoader("Updating Topper")
                    viewModel.updateOurTopper(binding.toppersData as Data, profileImageUrl, testimonialImageUrl)
                } else {
                    showLoader("Adding Topper")
                    viewModel.addOurTopper(binding.toppersData as Data, profileImageUrl, testimonialImageUrl)
                }
            })
        }
    }

    fun setHeader(view: View) {
        val rightButton: LinearLayout = view.findViewById(R.id.right_icon_layout)
        val backButton: LinearLayout = view.findViewById(R.id.back_button)
        val rightIcon: ImageView = view.findViewById(R.id.right_icon)
        val title: TextView = view.findViewById(R.id.title)
        if(this.isEditing!!){
            title.text = getString(R.string.candidate_details)
            rightIcon.setImageResource(R.drawable.ic_delete_white_outerline)
            rightButton.setOnClickListener {
                when {
                    addUpdateTopper -> {
                        showLoader("Deleting Topper")
                        viewModel.deleteOurTopper(topperData as Data)
                    }
                    else -> {
                        Toast.makeText(requireContext(), getString(R.string.no_candidate_data_found), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            title.text = getString(R.string.add_candidate)
        }

        backButton.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun imageValidate(): Boolean {
        if (viewModel.getProfileImagePath() == null) {
            Toast.makeText(requireContext(), getString(R.string.please_select_topper_image), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validate(): Boolean {
        when {
            binding.userName.text.isNullOrBlank() -> {
                binding.userName.error = getString(R.string.enter_valid_name)
                binding.userName.requestFocus()
                return false
            }
            else -> {
                binding.userName.error = null
            }
        }

        when {
            binding.userCourseName.text.isNullOrBlank() -> {
                binding.userCourseName.error = getString(R.string.enter_valid_course_name)
                binding.userCourseName.requestFocus()
                return false
            }
            else -> {
                binding.userCourseName.error = null
            }
        }

        when {
            binding.userCourseType.text.isNullOrBlank() -> {
                binding.userCourseType.error = getString(R.string.enter_valid_course_type)
                binding.userCourseType.requestFocus()
                return false
            }
            else -> {
                binding.userCourseType.error = null
            }
        }

        if (!Utils.isConnectedToInternet(requireContext())) {
            Toast.makeText(requireContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onGranted() {
        val view = layoutInflater.inflate(R.layout.bottom_sheet_camera_gallery, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
        dialog.camera.setOnClickListener {
            dialog.dismiss()
            launchCamera()
        }
        dialog.gallery.setOnClickListener {
            dialog.dismiss()
            launchGallery()
        }
        dialog.close.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun launchCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoURI = viewModel.createImageUri(TOPPER_PROFILE_IMAGE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
    }

    override fun onDenied() {
    }

    override fun onShowRationale(rationale: String) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && photoURI != null) {
                    var bitmap = requireActivity().getBitmapFromUri(photoURI)
                    bitmap = viewModel.rotateImageIfRequired(photoURI!!.path, bitmap!!)
                    if (viewModel.topperImageFlag == Topper.PROFILE) {
                        binding.icRemoveTopperProfileImage.visibility = View.VISIBLE
                        setProfileImage(binding.toppersProfileImage, bitmap)
                    }

                    if (viewModel.topperImageFlag == Topper.TESTIMONIAL) {
                        binding.icRemoveTopperTestimonialImage.visibility = View.VISIBLE
                        setTestimonialImage(binding.topperTestimonialImage, bitmap)
                    }
                }
            }
            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data?.data != null) {
                    val bitmap = requireActivity().getBitmapFromUri(data.data)
                    if (viewModel.topperImageFlag == Topper.PROFILE) {
                        binding.icRemoveTopperProfileImage.visibility = View.VISIBLE
                        setProfileImage(binding.toppersProfileImage, bitmap)
                    }

                    if (viewModel.topperImageFlag == Topper.TESTIMONIAL) {
                        binding.icRemoveTopperTestimonialImage.visibility = View.VISIBLE
                        setTestimonialImage(binding.topperTestimonialImage, bitmap)
                    }
                }
            }
        }
    }

    private fun setProfileImage(view: ImageView, bitmap: Bitmap?) {
        Glide.with(view).load(bitmap).into(view)
        val path = viewModel.saveImage(bitmap, TOPPER_PROFILE_IMAGE)
        viewModel.setProfileImagePath(path)
    }

    private fun setTestimonialImage(view: ImageView, bitmap: Bitmap?) {
        Glide.with(view).load(bitmap).into(view)
        val path = viewModel.saveImage(bitmap, TOPPER_TESTIMONIAL_IMAGE)
        viewModel.setTestimonialImagePath(path)
    }

    companion object {
        fun newInstance(): TopperDetailsFragment = TopperDetailsFragment()
        fun newInstance(topperData: Data, isEditing: Boolean): TopperDetailsFragment = TopperDetailsFragment(topperData, isEditing)
    }
}