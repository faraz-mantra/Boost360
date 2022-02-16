package com.festive.poster.ui.promoUpdates.pastUpdates

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.constant.FragmentType
import com.festive.poster.constant.FragmentType.Companion.fromValue
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar

class PastUpdatesContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

    private var type: FragmentType? = null

    override fun getLayout(): Int {
        return R.layout.activity_fragment_container
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun getToolbar(): CustomToolbar? {
        return binding?.appBarLayout?.toolbar
    }

    override fun getToolbarBackgroundColor(): Int {
        return ContextCompat.getColor(this, R.color.black_4a4a4a)
    }

    override fun getToolbarTitle(): String {
        return getString(when (type) {
                FragmentType.UPDATES_LISTING_FRAGMENT -> R.string.posted_updates
                FragmentType.STATS_FRAGMENT -> R.string.update_details
                else -> R.string.empty_string
            }
        )
    }

    override fun getToolbarTitleColor(): Int? {
        return when (type) {
            else -> ContextCompat.getColor(this, R.color.white)
        }
    }

    override fun getNavigationIcon(): Drawable? {
        return when (type) {
            else -> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_left)
        }
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
            FragmentType.UPDATES_LISTING_FRAGMENT -> {
                UpdatesListingFragment.newInstance(intent.extras)
            }
            FragmentType.STATS_FRAGMENT -> {
                StatsFragment.newInstance(intent.extras)
            }
            else -> throw IllegalFragmentTypeException()
        }
    }

    private fun shouldAddToBackStack(): Boolean {
        return when (type) {
            FragmentType.UPDATES_LISTING_FRAGMENT -> true
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

fun startFragmentPastUpdatesContainerActivity(activity: Activity, type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false) {
    val intent = Intent(activity, PastUpdatesContainerActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentType(type)
    if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    activity.startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
    return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}