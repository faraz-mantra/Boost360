package com.appservice.ui.domainbooking

import android.os.Bundle
import android.text.SpannableString
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentAddingExistingDomainBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.domainbooking.model.DomainStepsModel
import com.appservice.utils.WebEngageController
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.*
import com.framework.webengageconstant.ADDING_EXISTING_DOMAIN_FLOW_PAGE_LOAD

class AddingExistingDomainFragment :
    AppBaseFragment<FragmentAddingExistingDomainBinding, BaseViewModel>(),
    RecyclerItemClickListener {

    var addingExistingDomainStepStatus = 0
    var domainNameString = ""

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
        WebEngageController.trackEvent(DOMAIN_ADDING_EXISTING_DOMAIN_STEPS_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
        (baseActivity as? DomainBookingContainerActivity)?.setToolbarTitleNew(
            resources.getString(
                R.string.adding_existing_domain
            ), resources.getDimensionPixelSize(R.dimen.size_44)
        )
        setUiStepStatus()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding?.tvInfo?.setOnClickListener{
            WebEngageController.trackEvent(ADDING_EXISTING_DOMAIN_STEPS_HELP_BUTTON_CLICK, CLICK, NO_EVENT_VALUE)
            showShortToast(getString(R.string.coming_soon))
        }

        binding?.btnNextStep?.setOnClickListener {
            if (addingExistingDomainStepStatus < 3) {
                WebEngageController.trackEvent(ADDING_EXISTING_DOMAIN_NEXT_STEPS_CLICK, CLICK, NO_EVENT_VALUE)
                ++addingExistingDomainStepStatus
                setUiStepStatus()
            } else {
                WebEngageController.trackEvent(DOMAIN_ADDING_EXISTING_FINAL_SUCCESS_CLICK, CLICK, NO_EVENT_VALUE)
                startFragmentDomainBookingActivity(
                    activity = requireActivity(),
                    type = com.appservice.constant.FragmentType.ACTIVE_DOMAIN_FRAGMENT,
                    bundle = Bundle(),
                    clearTop = false
                )
            }
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    WebEngageController.trackEvent(ADDING_EXISTING_DOMAIN_STEPS_BACK_CLICK, CLICK, NO_EVENT_VALUE)
                    if (addingExistingDomainStepStatus >= 0) {
                        --addingExistingDomainStepStatus
                        setUiStepStatus()
                    } else if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
            )
    }

    private fun setUiStepStatus() {
        domainNameString = arguments?.getString("domainName")!!
        when (addingExistingDomainStepStatus) {
            0 -> {
                setupStringOnCards(1)
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
                binding?.tvStepIdentity1?.visible()
                binding?.tvStepIdentity2?.gone()
                binding?.tvStepIdentity3?.gone()
                binding?.tvStepIdentity4?.gone()
            }
            1 -> {
                setupStringOnCards(2)
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
                binding?.tvStepIdentity1?.gone()
                binding?.tvStepIdentity2?.visible()
                binding?.tvStepIdentity3?.gone()
                binding?.tvStepIdentity4?.gone()
            }
            2 -> {
                setupStringOnCards(3)
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
                binding?.tvStepIdentity1?.gone()
                binding?.tvStepIdentity2?.gone()
                binding?.tvStepIdentity3?.visible()
                binding?.tvStepIdentity4?.gone()
            }
            3 -> {
                setupStringOnCards(4)
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
                binding?.tvStepIdentity1?.gone()
                binding?.tvStepIdentity2?.gone()
                binding?.tvStepIdentity3?.gone()
                binding?.tvStepIdentity4?.visible()
            }
        }
    }

    private fun setupStringOnCards(stepNumber: Int) {
        var stepsList: ArrayList<DomainStepsModel> = arrayListOf()

        when (stepNumber) {
            1 -> {
                stepsList = stepOneData()
                val adapter = AppBaseRecyclerViewAdapter(baseActivity, stepsList, this)
                binding?.rvStepOne?.adapter = adapter
                binding?.rvStepOne?.layoutManager = LinearLayoutManager(baseActivity)
            }
            2 -> {
                stepsList = stepTwoData()
                val adapter = AppBaseRecyclerViewAdapter(baseActivity, stepsList, this)
                binding?.rvStepTwo?.adapter = adapter
                binding?.rvStepTwo?.layoutManager = LinearLayoutManager(baseActivity)
            }
            3 -> {
                stepsList = stepThreeData()
                val adapter = AppBaseRecyclerViewAdapter(baseActivity, stepsList, this)
                binding?.rvStepThree?.adapter = adapter
                binding?.rvStepThree?.layoutManager = LinearLayoutManager(baseActivity)
            }
            4 -> {
                stepsList = stepFourData1()
                val adapter1 = AppBaseRecyclerViewAdapter(baseActivity, stepsList, this)
                binding?.rvStepFour1?.adapter = adapter1
                binding?.rvStepFour1?.layoutManager = LinearLayoutManager(baseActivity)

                stepsList = stepFourData2()
                val adapter2 = AppBaseRecyclerViewAdapter(baseActivity, stepsList, this)
                binding?.rvStepFour2?.adapter = adapter2
                binding?.rvStepFour2?.layoutManager = LinearLayoutManager(baseActivity)
            }
        }
    }

    private fun stepOneData(): ArrayList<DomainStepsModel> {
        val stepsList = arrayListOf(
            DomainStepsModel(
                SpannableString("Open a new tab in your browser"), true, isTextDark = true
            ),
            DomainStepsModel(
                SpannableString("Go to domain provider’s setting and log in"), true, isTextDark = true
            ),
            DomainStepsModel(
                SpannableString("Come back here for next steps"), true, isTextDark = true
            )
        )

        return stepsList
    }

    private fun stepTwoData(): ArrayList<DomainStepsModel> {
        val stepsList = arrayListOf(
            DomainStepsModel(
                SpannableString("On your domain provider’s site, go to ‘domains’ page."),
                true, isTextDark = true
            ),
            DomainStepsModel(
                SpannableString("Find <b><u>‘$domainNameString’</u></b> and go to it’s control panel or settings panel."),
                true, isTextDark = true
            ),
            DomainStepsModel(
                SpannableString("Find where you manage the domain’s settings. <b>Look for a button or link with the words Manage, Manage settings, domain settings or something similar</b>."),
                true, isTextDark = true
            )
        )
        return stepsList
    }

    private fun stepThreeData(): ArrayList<DomainStepsModel> {
        val stepsList = arrayListOf(
            DomainStepsModel(
                SpannableString("In your domain settings, find the area where you manage or edit nameserver records, <b><u>(help)</u>.\n\nThis area could be called DNS settings, Nameservers, NS or similar. If you don’t see it immediately, try looking for a menu at the top or on the side of this page.</b>"),
                true, isTextDark = true
            ),
            DomainStepsModel(
                SpannableString("Find your current nameservers. They look like this:\n<b>ns-cloud-d1.googleomains.com\nns-cloud-d2.googleomains.com\nns-cloud-d3.googleomains.com\nns-cloud-d4.googleomains.com</b>"),
                true, isTextDark = true
            ),
        )
        return stepsList
    }

    private fun stepFourData1(): ArrayList<DomainStepsModel> {
        val stepsList = arrayListOf(
            DomainStepsModel(
                SpannableString("On your domain provider’s site, replace your current nameservers with the <____> nameservers below, one at a time."),
                true, isTextDark = true
            ),
            DomainStepsModel(
                SpannableString("Remove previously written nameservers and add below shown 2 nameservers."),
                true, isTextDark = true
            )
        )
        return stepsList
    }

    private fun stepFourData2(): ArrayList<DomainStepsModel> {
        val stepsList = arrayListOf(
            DomainStepsModel(
                SpannableString("Now you should have two above mentioned nameserver records. If you have others, just remove them."),
                true, isTextDark = true
            ),
            DomainStepsModel(SpannableString("Save your changes."), true, isTextDark = true),
        )
        return stepsList
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    }

}