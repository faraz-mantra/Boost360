package com.boost.presignin.ui.newOnboarding

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentBookADomainSslBinding
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.ui.domainbooking.BookDomainSslFragment
import com.boost.presignin.R
import com.boost.presignin.databinding.FragmentEnterPhoneBinding
import com.boost.presignin.dialog.WebViewDialog
import com.boost.presignin.helper.WebEngageController
import com.framework.extensions.afterTextChanged
import com.framework.models.BaseViewModel
import com.framework.utils.fromHtml
import com.framework.utils.makeLinks
import com.framework.webengageconstant.BOOST_360_CONDITIONS_CLICK
import com.framework.webengageconstant.BOOST_360_TERMS_CLICK
import com.framework.webengageconstant.CLICKED
import com.framework.webengageconstant.NO_EVENT_VALUE

class EnterPhoneFragment : AppBaseFragment<FragmentEnterPhoneBinding, BaseViewModel>() {

    private val urlTermsAndCond = "https://www.getboost360.com/tnc?src=android&stage=presignup"

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): EnterPhoneFragment {
            val fragment = EnterPhoneFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_enter_phone
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnListeners()
        initUI()
    }

    private fun initUI() {
        initTncString()
    }

    private fun setOnListeners() {
        binding?.phoneEt?.afterTextChanged {
            binding?.tvRequestOtp?.isEnabled = !it.isNullOrEmpty()
        }

        binding?.tvRequestOtp?.setOnClickListener {
            startFragmentFromNewOnBoardingActivity(
                activity = requireActivity(),
                type = com.boost.presignin.constant.FragmentType.VERIFY_PHONE_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
        }
    }

    private fun initTncString() {
        binding?.acceptTncPhone?.text = fromHtml("${getString(R.string.enter_phone_t_n_c)} <b><u><font color=#ffb900>Terms of Use</font></u></b> and <b><u><font color=#ffb900>Privacy Policy</font></u></b>")
        binding?.acceptTncPhone?.makeLinks(
            Pair("Terms of Use", View.OnClickListener {
                WebEngageController.trackEvent(BOOST_360_TERMS_CLICK, CLICKED, NO_EVENT_VALUE)
                openTNCDialog(
                    urlTermsAndCond,
                    resources.getString(R.string.boost360_terms_conditions)
                )
            }),
            Pair("Privacy Policy", View.OnClickListener {
                WebEngageController.trackEvent(BOOST_360_CONDITIONS_CLICK, CLICKED, NO_EVENT_VALUE)
                openTNCDialog(
                    urlTermsAndCond,
                    resources.getString(R.string.boost360_terms_conditions)
                )
            })
        )
    }

    private fun openTNCDialog(url: String, title: String) {
        showShortToast(getString(R.string.coming_soon))
       /* WebViewDialog().apply {
            setData(false, url, title)
            onClickType = { }
            show(activity.supportFragmentManager, title)
        }*/
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_help_on_boarding_new, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_help_onboard -> {
                showShortToast(getString(R.string.coming_soon))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}