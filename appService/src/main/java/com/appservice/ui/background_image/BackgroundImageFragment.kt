package com.appservice.ui.background_image

import android.os.Bundle
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.RecyclerViewActionType
import com.appservice.constant.RecyclerViewItemType
import com.appservice.databinding.FragmentBackgroundImageBinding
import com.appservice.model.ImageData
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.viewmodel.BackgroundImageViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.ImagePickerUtil
import com.framework.utils.toJson
import com.google.gson.Gson
import java.util.ArrayList

class BackgroundImageFragment : AppBaseFragment<FragmentBackgroundImageBinding, BackgroundImageViewModel>(), RecyclerItemClickListener {


    private var listImages: ArrayList<ImageData>?=null

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): BackgroundImageFragment {
            val fragment = BackgroundImageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_background_image
    }

    override fun getViewModelClass(): Class<BackgroundImageViewModel> {
        return BackgroundImageViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        ImagePickerUtil.initLauncher(this)
        sessionLocal = UserSessionManager(baseActivity)
        setOnClickListener(binding?.btnDone)
        getBackgroundImages()
    }

    private fun getBackgroundImages() {

        showProgress()
        viewModel?.getImages(sessionLocal.fPID!!, clientId)?.observeOnce(viewLifecycleOwner) { res ->
            hideProgress()
            if (res.isSuccess()) {
                val response = res.arrayResponse
                listImages = ArrayList<ImageData>()
                response?.forEach { listImages?.add(ImageData(it as String, RecyclerViewItemType.BACKGROUND_IMAGE_RV.getLayout())) }
                if (listImages?.isEmpty() == true){
                    binding?.layoutDefaultImage?.visible()
                    binding?.dataView?.gone()
                }else{
                    binding?.layoutDefaultImage?.gone()
                    binding?.dataView?.visible()
                }
                binding?.imageList?.apply {
                    val adapterImage = AppBaseRecyclerViewAdapter(baseActivity, listImages!!, this@BackgroundImageFragment)
                    adapter = adapterImage
                }


            }
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.ON_CLICK_BACKGROUND_IMAGE.ordinal -> {
                startBackgroundActivity(FragmentType.BACKGROUND_IMAGE_F_SCREEN_FRAGMENT,
                Bundle().apply
                 {
                     putInt(BGImageFullScreenFragment.BK_IMAGE_POS,position)
                     putString(BGImageFullScreenFragment.BK_IMAGE_LIST,listImages.toJson())

                 })
            }
        }

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.btnDone->{
                ImagePickerUtil.openPicker(this,object :ImagePickerUtil.Listener{
                    override fun onFilePicked(filePath: String) {
                        startBackgroundActivity(FragmentType.BACKGROUND_IMAGE_CROP_FRAGMENT,
                        Bundle().apply
                         {
                             putString(BGImageCropFragment.BK_IMAGE_PATH,filePath)
                         })
                    }
                })
            }
        }
    }
}