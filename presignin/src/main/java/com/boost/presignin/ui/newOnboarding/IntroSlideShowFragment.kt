package com.boost.presignin.ui.newOnboarding

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.graphics.translationMatrix
import androidx.viewpager2.widget.ViewPager2
import com.boost.presignin.R
import com.boost.presignin.adapter.IntroNewAdapter
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentIntroSlideShowBinding
import com.boost.presignin.model.newOnboarding.IntroItemNew
import com.boost.presignin.ui.intro.CircularViewPagerHandler
import com.framework.errorHandling.ErrorTransparentActivity
import com.framework.models.BaseViewModel

class IntroSlideShowFragment : AppBaseFragment<FragmentIntroSlideShowBinding, BaseViewModel>() {

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
      adapter = IntroNewAdapter(childFragmentManager, lifecycle, introItems) { setNextPage(it) }
      orientation = ViewPager2.ORIENTATION_HORIZONTAL
      binding?.introIndicatorNew?.setViewPager2(this)
      this.offscreenPageLimit = 1
      this.registerOnPageChangeCallback(object : CircularViewPagerHandler(this) {
        override fun onPageSelected(position: Int) {
          currentPosition = position
          binding?.consWrapperIntro?.setBackgroundColor(ContextCompat.getColor(baseActivity, IntroItemNew().getData(baseActivity)[position].slideBackgroundColor ?: 0))
          super.onPageSelected(position)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
          if (myState == ViewPager2.SCROLL_STATE_DRAGGING && currentPosition == position && currentPosition == 0) currentItem = 3
          else if (myState == ViewPager2.SCROLL_STATE_DRAGGING && currentPosition == position && currentPosition == 3) currentItem = 0
          super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        override fun onPageScrollStateChanged(state: Int) {
          myState = state
          super.onPageScrollStateChanged(state)
        }
      })
    }
  }

  private fun setNextPage(pos: Int?) {
    if (currentPosition == pos) changePageOnAnimationEnd()
  }


  private fun setOnListeners() {
    binding?.btnGetStarted?.setOnClickListener {
      startActivity(Intent(baseActivity, ErrorTransparentActivity::class.java))
      /*startFragmentFromNewOnBoardingActivity(
        activity = baseActivity, type = FragmentType.ENTER_PHONE_FRAGMENT,
        bundle = Bundle().apply { putString(IntentConstant.EXTRA_PHONE_NUMBER.name, "") }, clearTop = false
      )*/
    }
  }

  private fun moveToWelcomeScreen(enteredPhone: String?) {
    startFragmentFromNewOnBoardingActivity(
      activity = baseActivity, type = FragmentType.WELCOME_FRAGMENT,
      bundle = Bundle().apply {
        putString(IntentConstant.EXTRA_PHONE_NUMBER.name, enteredPhone)
        putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, false)
      }
    )
  }


  private fun nextPageTimer() {
    handler.postDelayed(nextRunnable, 5000)
  }

  private fun changePageOnAnimationEnd() {
    val lastPosition: Int? = binding?.viewpagerIntro?.adapter?.itemCount?.minus(1)
    val mCurrentPosition = binding?.viewpagerIntro?.currentItem ?: 0
    val isLast = (mCurrentPosition == lastPosition)
    val setItemPosition = if (isLast) 0 else mCurrentPosition + 1
    binding?.viewpagerIntro?.currentItem = setItemPosition
  }

}