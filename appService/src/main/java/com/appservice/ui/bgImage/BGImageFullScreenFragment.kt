package com.appservice.ui.bgImage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewItemType
import com.appservice.databinding.FragmentBgImageFullScreenBinding
import com.appservice.model.ImageData
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.BackgroundImageViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.clientId
import com.framework.utils.convertStringToList
import com.framework.webengageconstant.*

class BGImageFullScreenFragment : AppBaseFragment<FragmentBgImageFullScreenBinding, BackgroundImageViewModel>() {

  var position: Int = 0
  var imageList: ArrayList<ImageData>? = null

  companion object {
    const val BK_IMAGE_POS = "BK_IMAGE_POS"
    const val BK_IMAGE_LIST = "BK_IMAGE_LIST"

    @JvmStatic
    fun newInstance(bundle: Bundle? = null): BGImageFullScreenFragment {
      val fragment = BGImageFullScreenFragment()
      fragment.arguments = bundle
      return fragment
    }
  }


  override fun getLayout(): Int {
    return R.layout.fragment_bg_image_full_screen
  }

  override fun getViewModelClass(): Class<BackgroundImageViewModel> {
    return BackgroundImageViewModel::class.java

  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(BACKGROUND_IMAGE_FULL_SCREEN_LOAD, START_VIEW, sessionLocal.fpTag)
    setOnClickListener(binding?.delete, binding?.previous, binding?.next, binding?.galleryCancel)
    position = arguments?.getInt(BK_IMAGE_POS) ?: 0
    imageList = ArrayList(convertStringToList(arguments?.getString(BK_IMAGE_LIST) ?: "") ?: arrayListOf())
    imageList?.forEach { it.layout = RecyclerViewItemType.BACKGROUND_IMAGE_FULL_SCREEN.getLayout() }
    setAdapterImage()
  }

  private fun setAdapterImage() {
    binding?.pager?.apply {
      val adapter = AppBaseRecyclerViewAdapter(baseActivity, imageList!!)
      this.adapter = adapter
      this.currentItem = position
      this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(pos: Int, positionOffset: Float, positionOffsetPixels: Int) {
          super.onPageScrolled(pos, positionOffset, positionOffsetPixels)
          binding?.countText?.text = "${pos + 1} of ${imageList?.size.toString()}"
        }
      })
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.delete -> {
        showProgress()
        WebEngageController.trackEvent(BACKGROUND_IMAGE_DELETE_CLICK, CLICK, sessionLocal.fpTag)
        val currentImage = imageList?.get(binding?.pager?.currentItem!!)
        val map = HashMap<String, String?>()
        map["clientId"] = clientId
        map["fpId"] = sessionLocal.fPID
        map["ExistingBackgroundImageUri"] = currentImage?.url
        map["identifierType"] = "SINGLE"
        viewModel?.deleteBGImage(map)?.observeOnce(viewLifecycleOwner) {
          if (it.isSuccess()) {
            val output = Intent()
            output.putExtra(IntentConstant.IS_UPDATED.name, true)
            baseActivity.setResult(AppCompatActivity.RESULT_OK, output)
            baseActivity.finish()
          } else showShortToast(it.errorFlowMessage() ?: getString(R.string.something_went_wrong))
          hideProgress()
        }
      }
      binding?.previous -> {
        if (getCountPager() != 0) binding?.pager?.setCurrentItem(getCountPager() - 1, true)
      }
      binding?.next -> {
        if ((getCountPager() + 1) != imageList?.size ?: 0) binding?.pager?.setCurrentItem(getCountPager() + 1, true);
      }
      binding?.galleryCancel -> baseActivity.finish()
    }
  }

  fun getCountPager(): Int {
    return binding?.pager?.currentItem ?: 0
  }
}