package com.boost.presignup.ui

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.boost.presignup.R
import com.boost.presignup.datamodel.SharedViewModel
import kotlinx.android.synthetic.main.fragment_splash.view.*

class SplashFragment : Fragment() {


  lateinit var root: View
  private lateinit var viewModel: SharedViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    root = inflater.inflate(R.layout.fragment_splash, container, false)

//        root.animation_view.setAnimation(R.raw.splash_globe)
    root.animation_view.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationRepeat(animation: Animator?) {
        Log.d("onAnimationRepeat", "")
      }

      override fun onAnimationEnd(animation: Animator?) {
        Log.d("onAnimationEnd", "")
//                findNavController().navigate(R.id.action_splashFragment_to_videoPlayerFragment)
      }

      override fun onAnimationCancel(animation: Animator?) {
        Log.d("onAnimationCancel", "")
      }

      override fun onAnimationStart(animation: Animator?) {
        Log.d("onAnimationStart", "")
      }

    })

    setUpMvvm()

    return root
  }

  private fun setUpMvvm() {

  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
    root.animation_view.playAnimation()
  }

}
