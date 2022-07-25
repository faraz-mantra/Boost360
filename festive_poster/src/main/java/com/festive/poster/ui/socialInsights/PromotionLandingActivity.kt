package com.festive.poster.ui.socialInsights

import android.content.Intent
import android.os.Bundle
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.constant.FragmentType
import com.festive.poster.constant.FragmentType.Companion.fromValue
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel

class PromotionLandingActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

    private var type: FragmentType? = null

    override fun getLayout(): Int {
        return R.layout.activity_fragment_container
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        intent?.extras?.getString(FRAGMENT_TYPE)?.let { type = fromValue(it) }
        if (type == null) intent?.extras?.getInt(FRAGMENT_TYPE)?.let { type = FragmentType.values()[it] }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView() {
        super.onCreateView()
        setFragment()
    }

    private fun setFragment() {
        val fragment = getFragmentInstance(type)
        fragment?.arguments = intent.extras
        binding?.container?.id?.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
    }

    private fun getFragmentInstance(type: FragmentType?): BaseFragment<*, *>? {
        return when (type) {
           /* FragmentType.ENTER_PHONE_FRAGMENT -> {
                EnterPhoneFragment.newInstance(intent.extras)
            }*/
            else -> throw IllegalFragmentTypeException()
        }
    }

    private fun shouldAddToBackStack(): Boolean {
        return when (type) {
            else -> false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}