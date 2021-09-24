package com.appservice.ui.domainbooking

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentAddingExistingDomainBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.domainbooking.model.DomainStepsModel
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel

class AddingExistingDomainFragment :
    AppBaseFragment<FragmentAddingExistingDomainBinding, BaseViewModel>(), RecyclerItemClickListener {

    var addingExistingDomainStepStatus = 0

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): AddingExistingDomainFragment {
            val fragment = AddingExistingDomainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_adding_existing_domain
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        (baseActivity as? DomainBookingContainerActivity)?.setToolbarTitleNew(
            resources.getString(
                R.string.adding_existing_domain
            ), resources.getDimensionPixelSize(R.dimen.size_44)
        )
        setUiStepStatus()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding?.btnNextStep?.setOnClickListener {
            if (addingExistingDomainStepStatus < 3) {
                ++addingExistingDomainStepStatus
                setUiStepStatus()
            } else {
                startFragmentDomainBookingActivity(
                    activity = requireActivity(),
                    type = com.appservice.constant.FragmentType.ACTIVE_DOMAIN_FRAGMENT,
                    bundle = Bundle(),
                    clearTop = false
                )
            }
        }
    }

    private fun setUiStepStatus() {
        when (addingExistingDomainStepStatus) {
            0 -> {
                binding?.tvStepIdentity?.text = getString(R.string.step_1_4)
                binding?.tvInfo?.text = getString(R.string.not_sure_provide_is)
                binding?.btnNextStep?.text = getString(R.string.i_logged_in)
                binding?.cardStep1?.visible()
                binding?.cardStep2?.gone()
                binding?.cardStep3?.gone()
                binding?.cardStepFinal?.gone()
                binding?.ivStep1?.visible()
                binding?.ivStep2?.gone()
                binding?.ivStep3?.gone()
                binding?.ivStepFinal?.gone()
            }
            1 -> {
                setupStringOnCards(2)
                binding?.tvStepIdentity?.text = getString(R.string.step_2_4)
                binding?.tvInfo?.text = getString(R.string.cant_find_domain_settings)
                binding?.btnNextStep?.text = getString(R.string.i_found_my_domain_settings)
                binding?.cardStep1?.gone()
                binding?.cardStep2?.visible()
                binding?.cardStep3?.gone()
                binding?.cardStepFinal?.gone()
                binding?.ivStep1?.gone()
                binding?.ivStep2?.visible()
                binding?.ivStep3?.gone()
                binding?.ivStepFinal?.gone()
            }
            2 -> {
                binding?.tvStepIdentity?.text = getString(R.string.step_3_4)
                binding?.tvInfo?.text = getString(R.string.cant_find_nameservers)
                binding?.btnNextStep?.text = getString(R.string.i_found_the_nameservers)
                binding?.cardStep1?.gone()
                binding?.cardStep2?.gone()
                binding?.cardStep3?.visible()
                binding?.cardStepFinal?.gone()
                binding?.ivStep1?.gone()
                binding?.ivStep2?.gone()
                binding?.ivStep3?.visible()
                binding?.ivStepFinal?.gone()
            }
            3 -> {
                binding?.tvStepIdentity?.text = getString(R.string.step_final)
                binding?.tvInfo?.text = getString(R.string.need_help)
                binding?.btnNextStep?.text = getString(R.string.i_replaced_the_nameservers)
                binding?.cardStep1?.gone()
                binding?.cardStep2?.gone()
                binding?.cardStep3?.gone()
                binding?.cardStepFinal?.visible()
                binding?.ivStep1?.gone()
                binding?.ivStep2?.gone()
                binding?.ivStep3?.gone()
                binding?.ivStepFinal?.visible()
            }
        }
    }

    private fun setupStringOnCards(stepNumber: Int) {
        var stepsList: ArrayList<DomainStepsModel> = arrayListOf()

        when (stepNumber) {
            1 -> {
                stepsList = stepOneData()
            }
            2 -> {
                stepsList = stepTwoData()
                val adapter = AppBaseRecyclerViewAdapter(baseActivity, stepsList, this)
                binding?.rvStepTwo?.adapter = adapter
                binding?.rvStepTwo?.layoutManager = LinearLayoutManager(baseActivity)
            }
            3 -> {
                stepsList = stepThreeData()
            }
            4 -> {
                stepsList = stepFourData()
            }
        }
    }

    private fun stepOneData(): ArrayList<DomainStepsModel> {
        val secondStep =
            "In case you have a different website (for same business) connected to the domain that you own, you can integrate this website also as a sub-domain on that domain. (what’s subdomain?)"
        val whatsSubdomain = "(what’s subdomain?)"
        val whatsSubdomainIndex = secondStep.indexOf(whatsSubdomain)

        val stepsList = arrayListOf(
            DomainStepsModel(
                SpannableString("Since search engines recognize a domain that’s already in use, we recommend integrating any relatable domain name that you currently own."),
                true
            ),
            DomainStepsModel(SpannableString(secondStep).apply {
                setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {}
                    },
                    whatsSubdomainIndex,
                    whatsSubdomainIndex + whatsSubdomain.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }, true),
            DomainStepsModel(
                SpannableString("If you don’t have any other domain, click on ‘book a new domain’ and choose a domain you like for your business website."),
                true
            )
        )

        return stepsList
    }

    private fun stepTwoData(): ArrayList<DomainStepsModel> {
        val stepsList = arrayListOf(
            DomainStepsModel(SpannableString("On your domain provider’s site, go to ‘domains’ page."), true),
            DomainStepsModel(SpannableString("Find <b><u>‘samplebusiness.com’</u></b> and go to it’s control panel or settings panel."), true),
            DomainStepsModel(SpannableString("Find where you manage the domain’s settings. <b>Look for a button or link with the words Manage, Manage settings, domain settings or something similar</b>."), true)
        )
        return stepsList
    }

    private fun stepThreeData(): ArrayList<DomainStepsModel> {
        val secondStep =
            "In case you have a different website (for same business) connected to the domain that you own, you can integrate this website also as a sub-domain on that domain. (what’s subdomain?)"
        val whatsSubdomain = "(what’s subdomain?)"
        val whatsSubdomainIndex = secondStep.indexOf(whatsSubdomain)

        val stepsList = arrayListOf(
            DomainStepsModel(
                SpannableString("Since search engines recognize a domain that’s already in use, we recommend integrating any relatable domain name that you currently own."),
                true
            ),
            DomainStepsModel(SpannableString(secondStep).apply {
                setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {}
                    },
                    whatsSubdomainIndex,
                    whatsSubdomainIndex + whatsSubdomain.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }, true),
            DomainStepsModel(
                SpannableString("If you don’t have any other domain, click on ‘book a new domain’ and choose a domain you like for your business website."),
                true
            )
        )

        return stepsList
    }

    private fun stepFourData(): ArrayList<DomainStepsModel> {
        val secondStep =
            "In case you have a different website (for same business) connected to the domain that you own, you can integrate this website also as a sub-domain on that domain. (what’s subdomain?)"
        val whatsSubdomain = "(what’s subdomain?)"
        val whatsSubdomainIndex = secondStep.indexOf(whatsSubdomain)

        val stepsList = arrayListOf(
            DomainStepsModel(
                SpannableString("Since search engines recognize a domain that’s already in use, we recommend integrating any relatable domain name that you currently own."),
                true
            ),
            DomainStepsModel(SpannableString(secondStep).apply {
                setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {}
                    },
                    whatsSubdomainIndex,
                    whatsSubdomainIndex + whatsSubdomain.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }, true),
            DomainStepsModel(
                SpannableString("If you don’t have any other domain, click on ‘book a new domain’ and choose a domain you like for your business website."),
                true
            )
        )

        return stepsList
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    }
}