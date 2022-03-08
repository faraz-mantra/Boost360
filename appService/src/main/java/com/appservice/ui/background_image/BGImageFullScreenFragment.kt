package com.appservice.ui.background_image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.RecyclerViewItemType
import com.appservice.databinding.FragmentBgImageFullScreenBinding
import com.appservice.databinding.FragmentPreviewBinding
import com.appservice.model.ImageData
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.viewmodel.BackgroundImageViewModel
import com.framework.base.BaseActivity
import com.framework.imagepicker.Utility
import com.framework.models.BaseViewModel
import com.framework.utils.convertJsonToObj
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class BGImageFullScreenFragment : AppBaseFragment<FragmentBgImageFullScreenBinding, BackgroundImageViewModel>() {


    var position:Int=0
    var imageList:ArrayList<ImageData>?=null
    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): BGImageFullScreenFragment {
            val fragment = BGImageFullScreenFragment()
            fragment.arguments = bundle
            return fragment
        }

        val BK_IMAGE_POS = "BK_IMAGE_POS"
        val BK_IMAGE_LIST = "BK_IMAGE_LIST"

    }


    override fun getLayout(): Int {
        return R.layout.fragment_bg_image_full_screen
    }
    override fun getViewModelClass(): Class<BackgroundImageViewModel> {
        return BackgroundImageViewModel::class.java

    }

    override fun onCreateView() {
        super.onCreateView()
        position = arguments?.getInt(BK_IMAGE_POS)?:0
        imageList = convertJsonToObj(arguments?.getString(BK_IMAGE_LIST))?: ArrayList()
        imageList?.forEach { it.layout =RecyclerViewItemType.BACKGROUND_IMAGE_FULL_SCREEN.getLayout() }
        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,imageList!!)
        binding?.pager?.adapter = adapter
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){

        }

    }
}