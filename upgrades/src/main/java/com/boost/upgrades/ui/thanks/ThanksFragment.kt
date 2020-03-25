package com.boost.upgrades.ui.thanks

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.utils.Constants.Companion.THANKS_2_FRAGMENT
import kotlinx.android.synthetic.main.thanks_fragment.*

class ThanksFragment : BaseFragment() {

    lateinit var root: View

    lateinit var animationTimer: CountDownTimer

    companion object {
        fun newInstance() = ThanksFragment()
    }

    private lateinit var viewModel: ThanksViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.thanks_fragment, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ThanksViewModel::class.java)

        animation_view.repeatCount=1

        animationTimer = object : CountDownTimer(3000, 4000) {
            override fun onTick(millisUntilFinished: Long) {


            }

            override fun onFinish() {

                (activity as UpgradeActivity).replaceFragment(Thanks2Fragment.newInstance(),THANKS_2_FRAGMENT)

            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(::animationTimer.isInitialized){
            animationTimer.cancel()
        }
        if(animation_view != null && animation_view.isAnimating){
            animation_view.cancelAnimation()
        }
    }

}
