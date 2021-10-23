package com.appservice.ui.domainbooking

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.constant.IntentConstant
import com.appservice.databinding.ActivityDomainBookingBinding
import com.appservice.databinding.BsheetDomainIntegrationOptionsBinding
import com.appservice.databinding.BsheetInputOwnDomainBinding
import com.appservice.databinding.BsheetInputSubDomainBinding
import com.appservice.model.domainBooking.DomainDetailsResponse
import com.appservice.model.domainBooking.request.ExistingDomainRequest
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.domainbooking.model.DomainStepsModel
import com.appservice.utils.Validations
import com.appservice.utils.WebEngageController
import com.appservice.utils.getDomainSplitValues
import com.appservice.viewmodel.DomainBookingViewModel
import com.framework.base.BaseActivity
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_CATEGORY
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.getDomainName
import com.framework.utils.fromHtml
import com.framework.utils.showKeyBoard
import com.framework.webengageconstant.*
import com.google.android.material.bottomsheet.BottomSheetDialog


class DomainBookingActivity : AppBaseActivity<ActivityDomainBookingBinding, DomainBookingViewModel>(), RecyclerItemClickListener {

  private lateinit var baseActivity: BaseActivity<*, *>
  private lateinit var existingDomainRequest: ExistingDomainRequest

  /**
   * Bottom Sheet : function "showBsheetIntegrationOption"
   * 0: sheetBinding.radioAsBusinessWebsite is selected
   * 1: sheetBinding.radioCreateASubdomain is selected
   * */
  var domainIntegrationUserSelection: Int = 0

  override fun getLayout(): Int {
    return R.layout.activity_domain_booking
  }

  override fun getViewModelClass(): Class<DomainBookingViewModel> {
    return DomainBookingViewModel::class.java
  }

  override fun onCreateView() {
    WebEngageController.trackEvent(DOMAIN_BOOKING_INITIAL_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    baseActivity = this
    session = UserSessionManager(this)
    showProgress()
    val domainSplit = getDomainSplitValues(session.getDomainName(true)!!)
    binding?.tvDomainTitle?.text = domainSplit?.domainName
    binding?.tvDomainAssigned?.text = domainSplit?.domainExtension
    setupUI()
    onClickListeners()
  }

  private fun onClickListeners() {
    binding?.btnBuyAddon?.setOnClickListener {
      initiateBuyFromMarketplace()
    }

    binding?.btnBookOldDomain?.setOnClickListener {
      WebEngageController.trackEvent(CLICKED_ON_HAVE_AN_EXISTING_DOMAIN, CLICK, NO_EVENT_VALUE)
      showSheetInputOwnDomain()
    }

    binding?.btnBookNewDomain?.setOnClickListener {
      WebEngageController.trackEvent(CLICKED_ON_BOOK_A_NEW_DOMAIN, CLICK, NO_EVENT_VALUE)
      startFragmentDomainBookingActivity(
        activity = this,
        type = com.appservice.constant.FragmentType.BOOK_A_DOMAIN_SSL_FRAGMENT,
        bundle = Bundle(),
        clearTop = false
      )
    }

    binding?.appBar?.customImageView4?.setOnClickListener {
      WebEngageController.trackEvent(DOMAIN_BOOKING_PURCHASE_BACK_CLICK, CLICK, NO_EVENT_VALUE)
      this.onNavPressed()
    }
  }

  private fun showSheetInputOwnDomain() {
    val bSheet = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
    val sheetBinding =
      DataBindingUtil.inflate<BsheetInputOwnDomainBinding>(layoutInflater, R.layout.bsheet_input_own_domain, null, false)
    bSheet.setContentView(sheetBinding.root)
    bSheet.setCancelable(false)

    sheetBinding.btnContinue.setOnClickListener {
      WebEngageController.trackEvent(ADD_OWN_DOMAIN_SHEET_CONTINUE_CLICK, CLICK, NO_EVENT_VALUE)
      validateDomainAndCallApi(sheetBinding, bSheet)
    }
    sheetBinding.ivClose.setOnClickListener {
      WebEngageController.trackEvent(DOMAIN_BOTTOM_SHEET_CLOSE_CLICK, CLICK, NO_EVENT_VALUE)
      bSheet.dismiss()
    }
    this.showKeyBoard(sheetBinding.etDomain)
    bSheet.show()
  }

  private fun validateDomainAndCallApi(sheetBinding: BsheetInputOwnDomainBinding, bSheet: BottomSheetDialog) {
    if (validateData(sheetBinding)) {
      showSheetIntegrationOption(sheetBinding.etDomain.text.toString())
      bSheet.dismiss()
    } else {
      showShortToast(getString(R.string.error_wrong_domain_entered))
    }
  }

  /**
   * Api Call for Adding Existing Domain to Boost
   * */
  private fun addExistingDomainApiCall(enteredDomainName: String, subDomainName: String) {
    /*showProgress()
    existingDomainRequest = ExistingDomainRequest(clientId, session.fpTag, enteredDomainName, subDomainName)
    viewModel.addExistingDomain(clientId, session.fpTag, existingDomainRequest).observeOnce(this, {
      if (it.isSuccess()) {
        if (it.parseResponse()) {
          WebEngageController.trackEvent(ADDING_EXISTING_DOMAIN_FLOW_PAGE_LOAD, SUCCESSFULLY_ADDED_EXISTING_DOMAIN, NO_EVENT_VALUE)
          openExistingDomainFragmentFlow(enteredDomainName)
        } else showShortToast(getString(R.string.your_domain_could_not_be_added_please_try_again))
      } else showShortToast(it.message())
      hideProgress()
    })*/
    openExistingDomainFragmentFlow(enteredDomainName)
  }

  private fun openExistingDomainFragmentFlow(enteredDomainName: String) {
    val bundle = Bundle()
    bundle.putString(IntentConstant.DOMAIN_NAME.toString(), enteredDomainName)
    startFragmentDomainBookingActivity(activity = this, type = com.appservice.constant.FragmentType.ADDING_EXISTING_DOMAIN_FRAGMENT, bundle = bundle, clearTop = false)
  }

  private fun validateData(sheetBinding: BsheetInputOwnDomainBinding): Boolean {
    return Validations.isDomainValid(sheetBinding.etDomain.text.toString())
  }

  private fun showSheetIntegrationOption(enteredDomainName: String) {
    val bSheet = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
    val sheetBinding = DataBindingUtil.inflate<BsheetDomainIntegrationOptionsBinding>(
      layoutInflater, R.layout.bsheet_domain_integration_options, null, false
    )
    bSheet.setContentView(sheetBinding.root)
    bSheet.setCancelable(false)

    sheetBinding.tvDomainName.text = enteredDomainName
    sheetBinding.radioCreateASubdomain.text = fromHtml("${getString(R.string.create_a_sub_domain_and_map_my)} <u><b>shop</b>.$enteredDomainName)</u>")

    domainIntegrationUserSelection = 0
    sheetBinding.radioAsBusinessWebsite.isChecked = true
    sheetBinding.radioGroup2.setOnCheckedChangeListener { group, checkedId ->
      when (checkedId) {
        sheetBinding.radioAsBusinessWebsite.id -> {
          domainIntegrationUserSelection = 0
        }
        sheetBinding.radioCreateASubdomain.id -> {
          domainIntegrationUserSelection = 1
        }
      }
    }

    sheetBinding.btnContinue.setOnClickListener {
      WebEngageController.trackEvent(DOMAIN_INPUT_DOMAIN_OR_SUBDOMAIN_OPTION_CLICK, CLICK, NO_EVENT_VALUE)
      bSheet.dismiss()
      if (domainIntegrationUserSelection == 0) {
        addExistingDomainApiCall(enteredDomainName, "")
      } else {
        showSubDomainBottomSheet(enteredDomainName)
      }
    }

    sheetBinding.ivClose.setOnClickListener {
      WebEngageController.trackEvent(DOMAIN_BOTTOM_SHEET_CLOSE_CLICK, CLICK, NO_EVENT_VALUE)
      bSheet.dismiss()
    }
    bSheet.show()

  }

  private fun showSubDomainBottomSheet(enteredDomainName: String) {
    val bSheet = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
    val sheetBinding = DataBindingUtil.inflate<BsheetInputSubDomainBinding>(layoutInflater, R.layout.bsheet_input_sub_domain, null, false)
    bSheet.setContentView(sheetBinding.root)
    bSheet.setCancelable(false)

    sheetBinding.tvDomainName.text = fromHtml("<u><font color=#E2E2E2>example</font><font color=#4A4A4A>.${enteredDomainName}</font></u>")

    sheetBinding.etSubDomain.afterTextChanged {
      sheetBinding.btnContinue.isEnabled = it.isEmpty().not()
      if (it.isEmpty().not())
         sheetBinding.tvDomainName.text = fromHtml("<u><font color=#ffb900>$it</font><font color=#4A4A4A>.${enteredDomainName}</font></u>")
      else
        sheetBinding.tvDomainName.text = fromHtml("<u><font color=#E2E2E2>example</font><font color=#4A4A4A>.${enteredDomainName}</font></u>")
    }

    sheetBinding.btnContinue.setOnClickListener {
      WebEngageController.trackEvent(ADD_OWN_SUB_DOMAIN_SHEET_CONTINUE_CLICK, CLICK, NO_EVENT_VALUE)
      addExistingDomainApiCall(enteredDomainName, sheetBinding.etSubDomain.text.toString())
    }
    sheetBinding.ivClose.setOnClickListener {
      WebEngageController.trackEvent(DOMAIN_BOTTOM_SHEET_CLOSE_CLICK, CLICK, NO_EVENT_VALUE)
      bSheet.dismiss()
    }
    this.showKeyBoard(sheetBinding.etSubDomain)
    bSheet.show()
  }

  private fun setupUI() {
    setupSteps()
    if (isPremium()) domainDetailsApi() else nonPremiumMode()
  }

  private fun nonPremiumMode() {
    binding?.layoutBuyAddon?.visible()
    binding?.ivRiaSteps?.gone()
    binding?.btnBookNewDomain?.gone()
    binding?.btnBookOldDomain?.gone()
    binding?.rvSteps?.gone()
  }

  private fun premiumMode() {
    binding?.layoutBuyAddon?.gone()
    binding?.ivRiaSteps?.visible()
    binding?.btnBookNewDomain?.visible()
    binding?.btnBookOldDomain?.visible()
    binding?.rvSteps?.visible()
  }

  private fun domainDetailsApi() {
    viewModel.domainDetails(session.fpTag, clientId).observeOnce(this, {
      if (it.isSuccess()) {
        val domainDetailsResponse = it as DomainDetailsResponse
        if (domainDetailsResponse.domainName != null && domainDetailsResponse.domainName.isNotEmpty()) {
          startFragmentDomainBookingActivity(activity = this, type = com.appservice.constant.FragmentType.ACTIVE_DOMAIN_FRAGMENT, bundle = Bundle(), clearTop = false)
          finish()
        } else premiumMode()
      } else showShortToast(it.message())
      hideProgress()
    })
  }


  private fun isPremium(): Boolean {
    return (session.getStoreWidgets()?.contains("DOMAINPURCHASE") ?: false).not()
  }

  private fun setupSteps() {
    val secondStep = "${getString(R.string.in_case_you_have_a_different_website_connected_to_the_domain)} <b><u>(what’s subdomain?)</u></b>"
    val whatsSubdomain = getString(R.string.whats_subdomain)
    val whatsSubdomainIndex = secondStep.indexOf(whatsSubdomain)

    val stepsList = arrayListOf(
      DomainStepsModel(SpannableString(getString(R.string.since_search_engine_recognize_a_domain_that_already_in_use)), false, isTextDark = false),
      DomainStepsModel(SpannableString(secondStep).apply {
          setSpan(object : ClickableSpan() {
              override fun onClick(widget: View) {
                showLongToast(getString(R.string.a_subdomain_is_a_subdivision_of_a_domain)) } }, whatsSubdomainIndex, whatsSubdomainIndex + whatsSubdomain.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
          )
          val styleSpan = StyleSpan(Typeface.BOLD)
          setSpan(styleSpan, whatsSubdomainIndex, whatsSubdomainIndex + whatsSubdomain.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }, false, isTextDark = false),
      DomainStepsModel(SpannableString(getString(R.string.if_you_dont_have_any_other_domain_click_on_book_a_new_domain)), false, isTextDark = false)
    )

    val adapter = AppBaseRecyclerViewAdapter(this, stepsList, this)
    binding?.rvSteps?.adapter = adapter
    binding?.rvSteps?.layoutManager = LinearLayoutManager(this)
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    //TODO handle clicks
  }

  private fun initiateBuyFromMarketplace() {
    val progressDialog = ProgressDialog(this)
    val status = getString(R.string.please_wait)
    progressDialog.setMessage(status)
    progressDialog.setCancelable(false)
    progressDialog.show()
    val intent = Intent(this, Class.forName("com.boost.upgrades.UpgradeActivity"))
    intent.putExtra("expCode", session.fP_AppExperienceCode)
    intent.putExtra("fpName", session.fPName)
    intent.putExtra("fpid", session.fPID)
    intent.putExtra("fpTag", session.fpTag)
    intent.putExtra("accountType", session.getFPDetails(GET_FP_DETAILS_CATEGORY))
    intent.putStringArrayListExtra("userPurchsedWidgets", ArrayList(session.getStoreWidgets() ?: arrayListOf<String>()))
    if (session.userProfileEmail != null) {
      intent.putExtra("email", session.userProfileEmail)
    } else {
      intent.putExtra("email", "ria@nowfloats.com")
    }
    if (session.userPrimaryMobile != null) {
      intent.putExtra("mobileNo", session.userPrimaryMobile)
    } else {
      intent.putExtra("mobileNo", "9160004303")
    }
    intent.putExtra("profileUrl", session.fPLogo)
    intent.putExtra("buyItemKey", "DOMAINPURCHASE")
    startActivity(intent)
    Handler().postDelayed({ progressDialog.dismiss() }, 1000)
  }
}