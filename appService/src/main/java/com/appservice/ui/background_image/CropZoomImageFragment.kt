package com.appservice.ui.background_image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentCropZoomBinding
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.gcd
import com.framework.utils.zoom
import kotlin.math.abs

class CropZoomImageFragment : AppBaseFragment<FragmentCropZoomBinding,BaseViewModel>() {

    private var imagePath: String? = null
    private var bitmap: Bitmap? = null
    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): CropZoomImageFragment {
            val fragment = CropZoomImageFragment()
            fragment.arguments = bundle
            return fragment
        }

        val BK_IMAGE_PATH = "BK_IMAGE_PATH"
    }
    override fun getLayout(): Int {
        return R.layout.fragment_crop_zoom
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        imagePath = arguments?.getString(BK_IMAGE_PATH)
/*        bitmap = BitmapFactory.decodeFile(imagePath)
        binding?.cropImg?.setImageBitmap(Utility.rotateImageIfRequired(bitmap!!, imagePath))*/
        val options = BitmapFactory.Options()
        options.inScaled = false
        bitmap= BitmapFactory.decodeResource(resources,R.drawable.imgtest16to7,options)
        binding?.cropImg?.setImageBitmap(bitmap)
        checkImageDim()
        viewListeners()
    }

    private fun checkImageDim() {
        if (bitmap?.width?:0>=1600&&bitmap?.height?:0>=700){
            if (checkAspectRatio()){
                imageSuccessView()
            }else{
               imageErrorView()

            }
        }else{
            imageErrorView()
        }
    }

    private fun imageErrorView() {
        binding?.layoutImageMisConfig?.visible()
        binding?.tvSliderSugg?.gone()
        binding?.tvImgDesc?.text = bitmap?.width.toString()+" x "+
                bitmap?.height.toString()
        binding?.btnDone?.setBackgroundColor(
            ContextCompat.getColor(requireActivity(),
                R.color.red_E39595))
        binding?.btnDone?.text = resources.getString(
            R.string.change_image)
        binding?.layoutSeek?.gone()

    }

    private fun imageSuccessView() {
        binding?.layoutImageMisConfig?.gone()
        binding?.tvSliderSugg?.visible()
        binding?.layoutSeek?.visible()
        binding?.btnDone?.setBackgroundColor(
            ContextCompat.getColor(requireActivity(),
                R.color.colorPrimary))
        binding?.btnDone?.text = resources.getString(
            R.string.crop_picture)
    }

    private fun viewListeners() {
        binding?.slider?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
              if (p1!=100){
                  binding?.cropImg?.setImageBitmap(
                      bitmap?.zoom(p1.toFloat()/100))
              }

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }

    fun checkAspectRatio(): Boolean {

        val gcd = gcd(bitmap!!.width,bitmap!!.height)
       return (bitmap!!.width.div(gcd)==16&&bitmap!!.height.div(gcd)==7)


    }
}