package com.appservice.ui.domainbooking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentActiveDomainBinding
import com.appservice.databinding.FragmentAddingExistingDomainBinding
import com.framework.models.BaseViewModel

class ActiveDomainFragment : AppBaseFragment<FragmentActiveDomainBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): ActiveDomainFragment {
            val fragment = ActiveDomainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(){
        (baseActivity as? DomainBookingContainerActivity)?.setToolbarTitleNew(
            resources.getString(
                R.string.website_domain
            ), resources.getDimensionPixelSize(R.dimen.size_44))
    }

    override fun getLayout(): Int {
        return  R.layout.fragment_active_domain
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
}