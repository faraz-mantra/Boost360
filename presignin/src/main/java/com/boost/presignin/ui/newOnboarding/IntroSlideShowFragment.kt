package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.databinding.FragmentIntroSlideShowBinding
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.newOnboarding.IntroItemNew
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.boost.presignin.ui.intro.CircularViewPagerHandler
import com.framework.models.BaseViewModel

class IntroSlideShowFragment : AppBaseFragment<FragmentIntroSlideShowBinding, BaseViewModel>(),
    RecyclerItemClickListener {

    private lateinit var introItems: ArrayList<IntroItemNew>
    private val handler = Handler(Looper.getMainLooper())

    private val nextRunnable = Runnable {
        binding?.viewpagerIntro?.post {
                val lastPosition: Int? = binding?.viewpagerIntro?.adapter?.itemCount?.minus(1)
                val mCurrentPosition = binding?.viewpagerIntro?.currentItem ?: 0
                val isLast = (mCurrentPosition == lastPosition)
                binding?.viewpagerIntro?.setCurrentItem(
                    if (isLast) 0 else mCurrentPosition + 1,
                    isLast.not()
                )
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
        introItems = IntroItemNew().getData(requireActivity())

        binding?.viewpagerIntro?.apply {
            adapter  = AppBaseRecyclerViewAdapter(baseActivity, introItems, this@IntroSlideShowFragment)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding?.introIndicatorNew?.setViewPager2(binding!!.viewpagerIntro)
            binding?.viewpagerIntro?.offscreenPageLimit = introItems.size
        }
        nextPageTimer()
    }

    private fun setOnListeners() {
        binding?.btnGetStarted?.setOnClickListener{
            startFragmentFromNewOnBoardingActivity(
                activity = requireActivity(),
                type = FragmentType.ENTER_PHONE_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    }

    private fun nextPageTimer() {
        handler.postDelayed(nextRunnable, 3000)
    }
}