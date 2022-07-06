package com.nowfloats.education.faculty.ui.facultydetails

import android.Manifest
import android.app.Activity
import android.content.Intent
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
import com.appservice.utils.changeColorOfSubstring
import com.boost.upgrades.utils.Utils
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nowfloats.Login.UserSessionManager
import com.nowfloats.education.faculty.FacultyActivity
import com.nowfloats.education.faculty.model.Data
import com.nowfloats.education.helper.BaseFragment
import com.nowfloats.education.helper.Constants.CAMERA_REQUEST_CODE
import com.nowfloats.education.helper.Constants.FACULTY_PROFILE_IMAGE
import com.nowfloats.education.helper.Constants.GALLERY_REQUEST_CODE
import com.nowfloats.education.helper.Constants.SAVE
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.Constants.UPDATE
import com.nowfloats.education.helper.PermissionsHelper
import com.nowfloats.education.helper.RuntimePermissionListener
import com.nowfloats.education.helper.getBitmapFromUri
import com.nowfloats.util.Methods
import com.thinksity.R
import com.thinksity.databinding.FacultyDetailsBinding
import kotlinx.android.synthetic.main.bottom_sheet_camera_gallery.*
import org.koin.android.ext.android.inject

class FacultyDetailsFragment(private val facultyData: Data?) : BaseFragment(),
  RuntimePermissionListener {

  constructor() : this(null)

  private val viewModel by inject<FacultyDetailsViewModel>()
  private lateinit var binding: FacultyDetailsBinding
  private var addUpdateFaculty = false
  private lateinit var permissionHelper: PermissionsHelper
  private var photoURI: Uri? = null
  private lateinit var session: UserSessionManager


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FacultyDetailsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    session = UserSessionManager(requireContext(), requireActivity())
    setHeader(view)

    when {
      facultyData != null -> {
        binding.facultyData = facultyData
        binding.addUpdateFaculty.text = UPDATE
        addUpdateFaculty = true
        viewModel.setFilePath("")
      }
      else -> {
        binding.facultyData = Data()
        binding.addUpdateFaculty.text = SAVE
        addUpdateFaculty = false
      }
    }

    binding.addUpdateFaculty.setOnClickListener {
      Methods.hideKeyboard(requireActivity())
      addUpdateFacultyData()
    }

    binding.facultyProfileImage.setOnClickListener {
      val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      )
      permissionHelper = PermissionsHelper(requireActivity(), permissions)
      permissionHelper.askPermission(this)
    }

    binding.removeFacultyProfileImage.setOnClickListener {
      binding.facultyProfileImage.setImageDrawable(null)
      binding.removeFacultyProfileImage.visibility = View.GONE
      viewModel.setFilePath(null)
    }

    initLiveDataObservables()
    setupUIColor()
  }

  private fun setupUIColor() {
    changeColorOfSubstring(R.string.add_profile_image, com.appservice.R.color.color_747474, "*", binding.tvAddProfileImage)
    changeColorOfSubstring(R.string.name, com.appservice.R.color.color_747474, "*", binding.tvFacultyName)
    changeColorOfSubstring(R.string.designation_aster, com.appservice.R.color.color_747474, "*", binding.tvFacultyDesignation)
  }

  private fun addUpdateFacultyData() {
    when {
      viewModel.getFilePath() != null -> {
        when {
          validate() -> {
            if (viewModel.getFilePath() != "") {
              showLoader(getString(R.string.loading_image))
              viewModel.getFilePath()?.let { path -> viewModel.getFacultyProfileImageUrl(path) }
            } else {
              showLoader(getString(R.string.updating_faculty_details))
              viewModel.updateOurFaculty(binding.facultyData as Data, null)
            }
          }
        }
      }
      else -> {
        showToast(getString(R.string.please_select_profile_image))
      }
    }
  }

  private fun initLiveDataObservables() {
    viewModel.apply {
      addFacultyResponse.observe(viewLifecycleOwner, Observer {
        hideLoader()
        if (!it.isNullOrBlank()) {
          Toast.makeText(
            requireContext(),
            getString(R.string.faculty_added_successfully),
            Toast.LENGTH_SHORT
          ).show()
          (activity as FacultyActivity).popFragmentFromBackStack()
        }
      })

      errorResponse.observe(viewLifecycleOwner, Observer {
        hideLoader()
        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
      })

      deleteFacultyResponse.observe(viewLifecycleOwner, Observer {
        if (!it.isNullOrBlank()) {
          if (it == SUCCESS) {
            hideLoader()
            Toast.makeText(
              requireContext(),
              getString(R.string.faculty_deleted_successfully),
              Toast.LENGTH_SHORT
            ).show()
            (activity as FacultyActivity).popFragmentFromBackStack()
          }
        }
      })

      updateFacultyResponse.observe(viewLifecycleOwner, Observer {
        hideLoader()
        if (!it.isNullOrBlank()) {
          Toast.makeText(
            requireContext(),
            getString(R.string.faculty_updated_successfully),
            Toast.LENGTH_SHORT
          ).show()
          (activity as FacultyActivity).popFragmentFromBackStack()
        }
      })

      uploadImageResponse.observe(viewLifecycleOwner, Observer {
        hideLoader()
        when {
          addUpdateFaculty -> {
            showLoader(getString(R.string.updating_faculty_details))
            viewModel.updateOurFaculty(binding.facultyData as Data, it[0].url)
          }
          else -> {
            showLoader(getString(R.string.adding_faculty))
            viewModel.addOurFaculty(session.fpTag, binding.facultyData as Data, it[0].url)
          }
        }
      })
    }
  }

  fun setHeader(view: View) {
    val rightButton: LinearLayout = view.findViewById(R.id.right_icon_layout)
    val backButton: LinearLayout = view.findViewById(R.id.back_button)
    val rightIcon: ImageView = view.findViewById(R.id.right_icon)
    val title: TextView = view.findViewById(R.id.title)
    title.text = getString(R.string.faculty_details)
    rightIcon.setImageResource(R.drawable.ic_delete_white_outerline)
    rightButton.setOnClickListener {
      when {
        addUpdateFaculty -> {
          showLoader(getString(R.string.deleting_faculty))
          viewModel.deleteOurFaculty(facultyData as Data)
        }
      }
    }
    backButton.setOnClickListener { requireActivity().onBackPressed() }
  }

  private fun validate(): Boolean {
    when {
      binding.facultyName.text.isNullOrBlank() -> {
        binding.facultyName.error = getString(R.string.enter_valid_name)
        binding.facultyName.requestFocus()
        return false
      }
      else -> {
        binding.facultyName.error = null
      }
    }

    when {
      binding.facultyDesignation.text.isNullOrBlank() -> {
        binding.facultyDesignation.error = getString(R.string.enter_valid_description)
        binding.facultyDesignation.requestFocus()
        return false
      }
      else -> {
        binding.facultyDesignation.error = null
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
    dialog.setCancelable(false)
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

  override fun onDenied() {

  }

  override fun onShowRationale(rationale: String) {

  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    permissionHelper.onRequestPermissionResult(requestCode, permissions, grantResults)
  }

  private fun launchGallery() {
    val intent = Intent()
    intent.action = Intent.ACTION_GET_CONTENT
    intent.type = "image/*"
    startActivityForResult(intent, GALLERY_REQUEST_CODE)
  }

  private fun launchCamera() {
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    photoURI = viewModel.createImageUri(FACULTY_PROFILE_IMAGE)
    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      CAMERA_REQUEST_CODE -> {
        if (resultCode == Activity.RESULT_OK && photoURI != null) {
          binding.removeFacultyProfileImage.visibility = View.VISIBLE
          var bitmap = requireActivity().getBitmapFromUri(photoURI)
          bitmap = viewModel.rotateImageIfRequired(photoURI!!.path, bitmap!!)
          Glide.with(binding.facultyProfileImage)
            .load(bitmap)
            .into(binding.facultyProfileImage)
          val path = viewModel.saveImage(bitmap, FACULTY_PROFILE_IMAGE)
          viewModel.setFilePath(path)
        }
      }

      GALLERY_REQUEST_CODE -> {
        if (resultCode == Activity.RESULT_OK && data?.data != null) {
          binding.removeFacultyProfileImage.visibility = View.VISIBLE
          val bitmap = requireActivity().getBitmapFromUri(data.data)
          Glide.with(binding.facultyProfileImage).load(data.data).into(binding.facultyProfileImage)
          val path = viewModel.saveImage(bitmap, FACULTY_PROFILE_IMAGE)
          viewModel.setFilePath(path)
        }
      }
    }
  }

  companion object {
    fun newInstance(): FacultyDetailsFragment = FacultyDetailsFragment()
    fun newInstance(facultyDetails: Data): FacultyDetailsFragment =
      FacultyDetailsFragment(facultyDetails)
  }
}