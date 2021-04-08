package com.boost.presignin.ui.registration

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import com.boost.presignin.R
import com.boost.presignin.databinding.FragmentRegistrationSuccessBinding
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel


class RegistrationSuccessFragment : BaseFragment<FragmentRegistrationSuccessBinding, BaseViewModel>() {


    companion object {
        @JvmStatic
        fun newInstance() =
                RegistrationSuccessFragment().apply {}
    }

    override fun getLayout(): Int {
        return R.layout.fragment_registration_success
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        val amountSpannableString = SpannableString("Beauty & Makeup").apply {
             setSpan(ForegroundColorSpan(Color.rgb(0,0,0)), 0, length, 0)
            setSpan(StyleSpan(Typeface.BOLD), 0,length,0)
        }

        binding?.subheading?.text = SpannableStringBuilder().apply {
            append(getString(R.string.your))
            append(amountSpannableString)
            append(getString(R.string.registration_complete_subheading))
        }
        val underLineSpan = SpannableString(getString(R.string.contact_support)).apply {
            setSpan(UnderlineSpan(), 0, length, 0)
        }


        binding?.lottieAnimation?.setAnimation(R.raw.lottie_anim_congratulation)
        binding?.lottieAnimation?.repeatCount = 0
        binding?.lottieAnimation?.playAnimation()
//        binding?.lottieAnimation?.let {
//            if (it.isAnimating) it.pauseAnimation()
//            else it.playAnimation()
//        }


//        binding?.linkTv?.text = SpannableStringBuilder().apply {
//            append(amountSpannableString)
//        }
//
//        binding?.li
    }
}