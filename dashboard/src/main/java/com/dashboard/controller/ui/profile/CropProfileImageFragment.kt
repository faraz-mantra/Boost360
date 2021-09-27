package com.dashboard.controller.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentCropProfileImageBinding
import com.dashboard.databinding.FragmentUserProfileBinding
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.dashboard.base.ProgressDialog
import com.dashboard.viewmodel.UserProfileViewModel
import com.framework.pref.clientId2
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*


class CropProfileImageFragment : AppBaseFragment<FragmentCropProfileImageBinding, UserProfileViewModel>() {

  private lateinit var session: UserSessionManager
  private var imagePath:String?=null
  private var bitmap:Bitmap? = null
  private val TAG = "CropProfileImageFragmen"
  private var userSessionManager:UserSessionManager?=null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): CropProfileImageFragment {
      val fragment = CropProfileImageFragment()
      fragment.arguments = bundle
      return fragment
    }
    val BK_IMAGE_PATH="BK_IMAGE_PATH"
  }

  override fun getLayout(): Int {
    return R.layout.fragment_crop_profile_image
  }

  override fun getViewModelClass(): Class<UserProfileViewModel> {
    return UserProfileViewModel::class.java
  }


  override fun onCreateView() {
    super.onCreateView()
    userSessionManager = UserSessionManager(requireActivity())
    setOnClickListener(binding?.btnDone)
    showProgress()
    imagePath = arguments?.getString(BK_IMAGE_PATH)
    bitmap = BitmapFactory.decodeFile(imagePath)
    binding?.cropImg?.setImageBitmap(bitmap)
    viewListeners()
  }



  private fun viewListeners() {
    binding?.slider?.addOnChangeListener { slider, value, fromUser ->
      zoom(value)
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnDone->{
        uploadImage()
      }
    }
  }

  private fun uploadImage() {
    lifecycleScope.launch(Dispatchers.Default) {
      val imgFile = saveBitmap()
      if (imgFile.exists()){

        withContext(Dispatchers.Main){
          showProgress()
          viewModel?.uploadProfileImage(clientId2,userSessionManager?.userProfileId,imgFile.name,
            imgFile.asRequestBody("image/*".toMediaTypeOrNull()))?.observe(viewLifecycleOwner,{
            Log.i(TAG, "uploadImage Response: "+Gson().toJson(it))
            hideProgress()

            if (it.status==200){
              requireActivity().onBackPressed()
            }
          })
        }

      }
    }
  }

  fun zoom(percent:Float){
    bitmap?.let {
      val scaleFactor = percent // Set this to the zoom factor

      val widthOffset = (scaleFactor / 2 * it.width).toInt()
      val heightOffset = (scaleFactor / 2 * it.height).toInt()
      val numWidthPixels: Int = it.width - 2 * widthOffset
      val numHeightPixels: Int = it.height - 2 * heightOffset

      if (widthOffset>0&&heightOffset>0&&numHeightPixels>0&&numWidthPixels>0){
        val rescaledBitmap = Bitmap.createBitmap(
          it,
          widthOffset,
          heightOffset,
          numWidthPixels,
          numHeightPixels,
          null,
          true
        )

        binding?.cropImg?.setImageBitmap(rescaledBitmap)


      }


    }

  }

  suspend fun saveBitmap(): File {
    bitmap = binding?.cropImg?.croppedImage
    //create a file to write bitmap data
    val f = File(context?.cacheDir, "tempimg.jpg");
    f.createNewFile();

//Convert bitmap to byte array
    val bos = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bos);
    val bitmapdata = bos.toByteArray();

//write the bytes in file
    var fos:FileOutputStream? = null;
    try {
      fos = FileOutputStream(f);
    } catch (e: FileNotFoundException) {
      e.printStackTrace();
    }
    try {
      fos?.write(bitmapdata);
      fos?.flush();
      fos?.close();
    } catch (e: IOException) {
      e.printStackTrace();
    }

    return f
  }


}