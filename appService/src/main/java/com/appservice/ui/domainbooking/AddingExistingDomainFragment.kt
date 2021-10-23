package com.appservice.ui.domainbooking

import android.os.Bundle
import android.text.SpannableString
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
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

class AddingExistingDomainFragment : AppBaseFragment<FragmentAddingExistingDomainBinding, BaseViewModel>(), RecyclerItemClickListener {

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
    setUiStepStatus()
    setOnClickListeners()
  }

  private fun setOnClickListeners() {
    binding?.tvInfo?.setOnClickListener {
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
        startFragmentDomainBookingActivityFinish(
          activity = requireActivity(),
          type = com.appservice.constant.FragmentType.ACTIVE_DOMAIN_FRAGMENT,
          bundle = Bundle(),
          clearTop = false
        )
      }
    }

    requireActivity()
      .onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
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
    domainNameString = arguments?.getString(IntentConstant.DOMAIN_NAME.toString())!!
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
    var stepsList: ArrayList<DomainStepsModel>

    when (stepNumber) {
      1 -> {
        stepsList = getStepData(stepNumber)
        val adapter = AppBaseRecyclerViewAdapter(baseActivity, stepsList, this)
        binding?.rvStepOne?.adapter = adapter
        binding?.rvStepOne?.layoutManager = LinearLayoutManager(baseActivity)
      }
      2 -> {
        stepsList = getStepData(stepNumber)
        val adapter = AppBaseRecyclerViewAdapter(baseActivity, stepsList, this)
        binding?.rvStepTwo?.adapter = adapter
        binding?.rvStepTwo?.layoutManager = LinearLayoutManager(baseActivity)
      }
      3 -> {
        stepsList = getStepData(stepNumber)
        val adapter = AppBaseRecyclerViewAdapter(baseActivity, stepsList, this)
        binding?.rvStepThree?.adapter = adapter
        binding?.rvStepThree?.layoutManager = LinearLayoutManager(baseActivity)
      }
      4 -> {
        stepsList = getStepData(stepNumber)
        val adapter1 = AppBaseRecyclerViewAdapter(baseActivity, stepsList, this)
        binding?.rvStepFour1?.adapter = adapter1
        binding?.rvStepFour1?.layoutManager = LinearLayoutManager(baseActivity)

        stepsList = getStepData(5)
        val adapter2 = AppBaseRecyclerViewAdapter(baseActivity, stepsList, this)
        binding?.rvStepFour2?.adapter = adapter2
        binding?.rvStepFour2?.layoutManager = LinearLayoutManager(baseActivity)
      }
    }
  }

  private fun getStepData(stepNumber: Int): ArrayList<DomainStepsModel> {
    var stepsList:ArrayList<DomainStepsModel> = arrayListOf()
    when(stepNumber){
      1->{
        stepsList =  arrayListOf(
          DomainStepsModel(SpannableString(getString(R.string.open_a_new_tab_in_your_browser)), true, isTextDark = true),
          DomainStepsModel(SpannableString(getString(R.string.go_to_domain_providers_setting_and_log_in)), true, isTextDark = true),
          DomainStepsModel(SpannableString(getString(R.string.come_back_here_for_next_steps)), true, isTextDark = true))
      }
      2 -> {
        stepsList = arrayListOf(
          DomainStepsModel(SpannableString(getString(R.string.one_your_domain_providers_site_go_to_domains_page)), true, isTextDark = true),
          DomainStepsModel(SpannableString("Find <b><u>‘$domainNameString’</u></b> ${getString(R.string.and_go_to_its_control_panel_or_settings)}"), true, isTextDark = true),
          DomainStepsModel(SpannableString("${getString(R.string.find_where_you_manage_the_domains_settings)} <b>${getString(R.string.look_for_a_button_or_link_with_the_word_manage)}</b>"), true, isTextDark = true))
      }
      3 -> {
        stepsList = arrayListOf(
          DomainStepsModel(SpannableString(getString(R.string.in_your_domain_settings_find_the_area_where_your_manage)), true, isTextDark = true),
          DomainStepsModel(SpannableString(getString(R.string.find_your_current_nameservers_they_look_like_this)), true, isTextDark = true),)
      }
      4 -> {
        stepsList = arrayListOf(
          DomainStepsModel(SpannableString(getString(R.string.one_your_domain_providers_site_replace_your_current_nameservers)), isBulletIndicated = true, isTextDark = true),
          DomainStepsModel(SpannableString(getString(R.string.remove_previously_written_nameservers_and_add_below_shown_2_nameservers)), isBulletIndicated = true, isTextDark = true))
      }
      5 -> {
        stepsList = arrayListOf(
          DomainStepsModel(SpannableString(getString(R.string.now_you_should_have_two_above_mentioned_nameservers_records)), true, isTextDark = true),
          DomainStepsModel(SpannableString(getString(R.string.save_your_changes)), isBulletIndicated = true, isTextDark = true),)
      }
    }
    return stepsList
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
  }

}