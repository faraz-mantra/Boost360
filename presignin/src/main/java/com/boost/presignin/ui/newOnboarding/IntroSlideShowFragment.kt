package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.FragmentIntroSlideShowBinding
import com.boost.presignin.model.newOnboarding.IntroItemNew
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.framework.models.BaseViewModel

class IntroSlideShowFragment : AppBaseFragment<FragmentIntroSlideShowBinding, BaseViewModel>(), RecyclerItemClickListener {

  private var myState = 0
  private var currentPosition = 0
  private lateinit var introItems: ArrayList<IntroItemNew>
  private val handler = Handler(Looper.getMainLooper())

  private val nextRunnable = Runnable {
    binding?.viewpagerIntro?.post {
      changePageOnAnimationEnd()
      nextPageTimer()
    }
  }

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): IntroSlideShowFragment {
      val fragment = IntroSlideShowFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_intro_slide_show
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnListeners()
    initUI()
  }

  private fun initUI() {
    introItems = IntroItemNew().getData(baseActivity)
    binding?.viewpagerIntro?.apply {
      adapter = AppBaseRecyclerViewAdapter(baseActivity, introItems, this@IntroSlideShowFragment)
      orientation = ViewPager2.ORIENTATION_HORIZONTAL
      binding?.introIndicatorNew?.setViewPager2(this)
      offscreenPageLimit = introItems.size
      registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
          currentPosition = position
          binding?.consWrapperIntro?.setBackgroundColor(ContextCompat.getColor(baseActivity, IntroItemNew().getData(baseActivity)[position].slideBackgroundColor ?: 0))
          super.onPageSelected(position)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
          if (myState == ViewPager2.SCROLL_STATE_DRAGGING && currentPosition == position && currentPosition == 0) setCurrentItem(3, true)
          else if (myState == ViewPager2.SCROLL_STATE_DRAGGING && currentPosition == position && currentPosition == 3) setCurrentItem(0, true)
          super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        override fun onPageScrollStateChanged(state: Int) {
          myState = state
          super.onPageScrollStateChanged(state)
        }
      })
    }
  }

  private fun setOnListeners() {
    binding?.btnGetStarted?.setOnClickListener {
      startFragmentFromNewOnBoardingActivity(
        activity = baseActivity, type = FragmentType.ENTER_PHONE_FRAGMENT,
        bundle = Bundle().apply { putString(IntentConstant.EXTRA_PHONE_NUMBER.name, "phoneNumber") }, clearTop = false
      )
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.INTRO_LOTTIE_ANIMATION_COMPLETE_INVOKE.ordinal -> changePageOnAnimationEnd()
    }
  }

  private fun nextPageTimer() {
    handler.postDelayed(nextRunnable, 5000)
  }

  private fun changePageOnAnimationEnd() {
    val lastPosition: Int? = binding?.viewpagerIntro?.adapter?.itemCount?.minus(1)
    val mCurrentPosition = binding?.viewpagerIntro?.currentItem ?: 0
    val isLast = (mCurrentPosition == lastPosition)
    val setItemPosition = if (isLast) 0 else mCurrentPosition + 1
    binding?.viewpagerIntro?.setCurrentItem(setItemPosition, isLast.not())
  }
}