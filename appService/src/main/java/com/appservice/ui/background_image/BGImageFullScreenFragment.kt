package com.appservice.ui.background_image

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import androidx.viewpager2.widget.ViewPager2
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.RecyclerViewItemType
import com.appservice.databinding.FragmentBgImageFullScreenBinding
import com.appservice.model.ImageData
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.viewmodel.BackgroundImageViewModel
import com.framework.base.BaseActivity
import com.framework.pref.clientId
import com.framework.utils.convertJsonToObj

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
        setOnClickListener(binding?.delete,binding?.previous,binding?.next,binding?.galleryCancel)
        position = arguments?.getInt(BK_IMAGE_POS)?:0
        imageList = convertJsonToObj(arguments?.getString(BK_IMAGE_LIST))?: ArrayList()
        imageList?.forEach { it.layout =RecyclerViewItemType.BACKGROUND_IMAGE_FULL_SCREEN.getLayout() }
        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,imageList!!)
        binding?.pager?.adapter = adapter

        binding?.pager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding?.current?.text = (position+1).toString()
                binding?.maxCount?.text = imageList?.size.toString()

            }
        })
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.delete->{
                showProgress()
                val currentImage =imageList?.get(binding?.pager?.currentItem!!)
                val map = HashMap<String,String?>()
                map["clientId"] = clientId
                map["fpId"] = sessionLocal.fPID
                map["ExistingBackgroundImageUri"] = currentImage?.url
                map["identifierType"] = "SINGLE"
                viewModel?.deleteBGImage(map)?.observe(viewLifecycleOwner) {
                    hideProgress()
                    if (it.isSuccess()) {
                        requireActivity().finish()
                    }
                }
            }
            binding?.previous->{
                if (binding?.pager?.currentItem?:0>0){
                    binding?.pager?.currentItem=1
                }
            }
            binding?.next->{
                if (binding?.pager?.currentItem?:0+1<imageList?.size?:0-1){
                    binding?.pager?.currentItem=1
                }
            }
            binding?.galleryCancel->{
                requireActivity().finish()
            }
        }

    }
}