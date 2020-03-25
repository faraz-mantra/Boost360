package com.boost.upgrades.ui.thanks

import android.content.Intent
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
import com.boost.upgrades.ui.home.HomeFragment
import com.boost.upgrades.utils.Constants.Companion.HOME_FRAGMENT
import kotlinx.android.synthetic.main.thanks2_fragment.*
import kotlinx.android.synthetic.main.thanks_fragment.*

class Thanks2Fragment : BaseFragment() {

    lateinit var root: View

    companion object {
        fun newInstance() = Thanks2Fragment()
    }

    private lateinit var viewModel: Thanks2ViewModel

    lateinit var animationTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root =  inflater.inflate(R.layout.thanks2_fragment, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(Thanks2ViewModel::class.java)

        animationTimer = object : CountDownTimer(3000, 4000) {
            override fun onTick(millisUntilFinished: Long) {


            }

            override fun onFinish() {

                animation_view2.setAnimation(R.raw.success)
                animation_view2.playAnimation()

            }
        }.start()

        back_thankyou.setOnClickListener{
            val homeFragment: HomeFragment = HomeFragment.newInstance()
            val args = Bundle()
            args.putString("data", "21")
            homeFragment.arguments = args
            (activity as UpgradeActivity).resetFragment(homeFragment ,HOME_FRAGMENT)
//            val intent = Intent(this, UpdatesActivity::class.java)
//            intent.putExtra("data","21")
//            startActivity(intent)
        }
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
