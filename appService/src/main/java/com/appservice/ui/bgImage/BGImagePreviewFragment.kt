package com.appservice.ui.bgImage

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentPreviewBinding
import com.appservice.viewmodel.BackgroundImageViewModel
import com.framework.imagepicker.Utility
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class BGImagePreviewFragment : AppBaseFragment<FragmentPreviewBinding, BackgroundImageViewModel>() {

  private var imagePath: String? = null
  private var bitmap: Bitmap? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): BGImagePreviewFragment {
      val fragment = BGImagePreviewFragment()
      fragment.arguments = bundle
      return fragment
    }

    val BK_IMAGE_PATH = "BK_IMAGE_PATH"
  }

  override fun getLayout(): Int {
    return R.layout.fragment_preview
  }

  override fun getViewModelClass(): Class<BackgroundImageViewModel> {
    return BackgroundImageViewModel::class.java

  }

  override fun onCreateView() {
    super.onCreateView()
    imagePath = arguments?.getString(BK_IMAGE_PATH)
    bitmap = BitmapFactory.decodeFile(imagePath)
    binding?.image?.setImageBitmap(Utility.rotateImageIfRequired(bitmap!!, imagePath))
    setOnClickListener(binding?.btnDone)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnDone -> {
        val file = File(imagePath)
        showProgress()
        viewModel?.createBGImage(file.asRequestBody(), sessionLocal.fPID)?.observe(viewLifecycleOwner) {
          hideProgress()
          if (it.isSuccess()) {
            BGimgUploadSuccessfulSheet().apply {
              onClicked = { type ->
                if (type == TypeSuccess.CLOSE.name) {
                  val output = Intent()
                  output.putExtra(IntentConstant.IS_BACK_PRESS.name, true)
                  baseActivity.setResult(AppCompatActivity.RESULT_OK, output)
                  baseActivity.finish()
                }
              }
              this.show(baseActivity.supportFragmentManager, BGimgUploadSuccessfulSheet::class.java.name)
            }
          }
        }
      }
    }
  }
}