package com.appservice.ui.domainbooking

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.constant.FragmentType
import com.appservice.databinding.ActivityFragmentContainerDomainBookingBinding
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar

open class DomainBookingContainerActivity :
    AppBaseActivity<ActivityFragmentContainerDomainBookingBinding, BaseViewModel>() {

    private var type: FragmentType? = null

    private var bookDomainSslFragment: BookDomainSslFragment? = null
    private var addingExistingDomainFragment: AddingExistingDomainFragment? = null
    private var activeExistingDomainFragment: ActiveExistingDomainFragment? = null
    private var confirmingDomainFragment: ConfirmingDomainFragment? = null
    private var activeNewDomainFragment: ActiveNewDomainFragment? = null

    override fun getLayout(): Int {
        return R.layout.activity_fragment_container_domain_booking
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        intent?.extras?.getInt(FRAGMENT_TYPE)?.let { type = FragmentType.values()[it] }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView() {
        super.onCreateView()
        setFragment()
    }

    override fun getToolbar(): CustomToolbar? {
        return binding?.toolbar
    }

    fun setToolbarTitleNew(title: String, marginEnd: Int = 0) {
        binding?.title?.text = title
        getToolbarTitleColor()?.let { binding?.title?.setTextColor(it) }
    }

    override fun customTheme(): Int? {
        return when (type) {
            /*FragmentType.ADD_BANK_ACCOUNT_START -> R.style.AppTheme_add_account*/
                FragmentType.ACTIVE_DOMAIN_FRAGMENT -> R.style.StatusBarYellowTheme
            else -> super.customTheme()
        }
    }

    override fun getToolbarBackgroundColor(): Int? {
        return when (type) {
            /*FragmentType.BANK_ACCOUNT_DETAILS -> ContextCompat.getColor(this, R.color.color_primary)*/
            FragmentType.ACTIVE_DOMAIN_FRAGMENT -> ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
            else -> ContextCompat.getColor(this, R.color.black_4a4a4a)
        }
    }

    override fun getToolbarTitleColor(): Int? {
        return when (type) {
            /* FragmentType.BANK_ACCOUNT_DETAILS -> ContextCompat.getColor(this, R.color.white)*/
            else -> super.getToolbarTitleColor()
        }
    }

    override fun getNavigationIcon(): Drawable? {
        return when (type) {
            /*FragmentType.BOOK_A_DOMAIN_SSL_FRAGMENT -> ContextCompat.getDrawable(
                this,
                R.drawable.ic_domain_toolbar_back
            )*/
            else -> ContextCompat.getDrawable(
                this,
                R.drawable.ic_domain_toolbar_back
            )
        }
    }

    private fun setFragment() {
        val fragment = getFragmentInstance(type)
        fragment?.arguments = intent.extras
        binding?.container?.id?.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
    }

    private fun getFragmentInstance(type: FragmentType?): BaseFragment<*, *>? {
        return when (type) {
            FragmentType.BOOK_A_DOMAIN_SSL_FRAGMENT -> {
                bookDomainSslFragment = BookDomainSslFragment.newInstance(intent.extras)
                bookDomainSslFragment
            }
            FragmentType.ADDING_EXISTING_DOMAIN_FRAGMENT -> {
                addingExistingDomainFragment = AddingExistingDomainFragment.newInstance(intent.extras)
                addingExistingDomainFragment
            }
            FragmentType.ACTIVE_DOMAIN_FRAGMENT -> {
                activeExistingDomainFragment = ActiveExistingDomainFragment.newInstance(intent.extras)
                activeExistingDomainFragment
            }
            FragmentType.CONFIRMING_DOMAIN_FRAGMENT -> {
                confirmingDomainFragment = ConfirmingDomainFragment.newInstance(intent.extras)
                confirmingDomainFragment
            }
            FragmentType.ACTIVE_NEW_DOMAIN_FRAGMENT -> {
                activeNewDomainFragment = ActiveNewDomainFragment.newInstance(intent.extras)
                activeNewDomainFragment
            }
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
        bookDomainSslFragment?.onActivityResult(requestCode, resultCode, data)
        addingExistingDomainFragment?.onActivityResult(requestCode, resultCode, data)
        activeExistingDomainFragment?.onActivityResult(requestCode, resultCode, data)
        confirmingDomainFragment?.onActivityResult(requestCode, resultCode, data)
        activeNewDomainFragment?.onActivityResult(requestCode, resultCode, data)
    }
}

fun startFragmentDomainBookingActivity(
    activity: Activity,
    type: FragmentType,
    bundle: Bundle = Bundle(),
    clearTop: Boolean
) {
    val intent = Intent(activity, DomainBookingContainerActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentType(type)
    if (clearTop) intent.flags =
        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    activity.startActivity(intent)
}

fun startFragmentDomainBookingActivityFinish(
    activity: Activity,
    type: FragmentType,
    bundle: Bundle = Bundle(),
    clearTop: Boolean
) {
    val intent = Intent(activity, DomainBookingContainerActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentType(type)
    if (clearTop) intent.flags =
        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    activity.startActivity(intent)
    activity.finish()
}

fun Intent.setFragmentType(type: FragmentType): Intent {
    return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}