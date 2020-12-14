package com.dashboard.controller.ui.allAddOns

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.dashboard.checkIsPremiumUnlock
import com.dashboard.databinding.FragmentAllBoostAddOnsBinding
import com.dashboard.model.live.addOns.AllBoostAddOnsData
import com.dashboard.model.live.addOns.ManageAddOnsBusinessResponse
import com.dashboard.model.live.addOns.ManageBusinessData
import com.dashboard.pref.UserSessionManager
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.AddOnsViewModel
import com.framework.extensions.observeOnce
import java.util.*

class AllBoostAddonsFragment : AppBaseFragment<FragmentAllBoostAddOnsBinding, AddOnsViewModel>(), RecyclerItemClickListener {

  private var session: UserSessionManager? = null
  private var searchView: SearchView? = null
  var adapterAddOns: AppBaseRecyclerViewAdapter<AllBoostAddOnsData>? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AllBoostAddonsFragment {
      val fragment = AllBoostAddonsFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_all_boost_add_ons
  }

  override fun getViewModelClass(): Class<AddOnsViewModel> {
    return AddOnsViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
  }

  override fun onResume() {
    super.onResume()
    Handler().postDelayed({ getBoostAddOnsData() }, 100)
  }

  private fun getBoostAddOnsData() {
    viewModel?.getBoostAddOns(baseActivity)?.observeOnce(viewLifecycleOwner, {
      val response = it as? ManageAddOnsBusinessResponse
      val dataAction = response?.data?.firstOrNull { it1 -> it1.type?.toUpperCase(Locale.ROOT) == getAddonsType(session?.fP_AppExperienceCode?.toUpperCase(Locale.ROOT)) }
      if (dataAction != null && dataAction.actionItem.isNullOrEmpty().not()) {
        dataAction.actionItem?.map { it2 -> it2.manageBusinessList?.map { it3 -> if (it3.premiumCode.isNullOrEmpty().not() && session.checkIsPremiumUnlock(it3.premiumCode).not()) it3.isLock = true } }
        if (adapterAddOns == null) {
          binding?.rvBoostAddOns?.apply {
            adapterAddOns = AppBaseRecyclerViewAdapter(baseActivity, setLastSeenData(dataAction.actionItem!!), this@AllBoostAddonsFragment)
            adapter = adapterAddOns
          }
        } else adapterAddOns?.notify(setLastSeenData(dataAction.actionItem!!))
      } else showShortToast(baseActivity.getString(R.string.manage_business_not_found))
    })
  }

  private fun setLastSeenData(data: ArrayList<AllBoostAddOnsData>): ArrayList<AllBoostAddOnsData> {
    val listAddOns = ManageBusinessData().getLastSeenData()
    if (listAddOns.isNotEmpty()) data.add(0, AllBoostAddOnsData(title = "Last Seen", manageBusinessList = listAddOns, isLastSeen = true))
    return data
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.BUSINESS_ADD_ONS_CLICK.ordinal -> {
        val data = item as? ManageBusinessData ?: return
        ManageBusinessData().saveLastSeenData(data)
        ManageBusinessData.BusinessType.fromName(data.businessType)?.let { businessAddOnsClick(it, baseActivity, session) }
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_search_icon_d, menu)
    val searchItem = menu.findItem(R.id.menu_item_search)
    if (searchItem != null) {
      searchView = searchItem.actionView as SearchView
      val searchAutoComplete = searchView?.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
      searchAutoComplete?.setHintTextColor(getColor(R.color.white_70))
      searchAutoComplete?.setTextColor(getColor(R.color.white))
      searchView?.queryHint = "Search boost add-ons"
      searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
          return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
          newText?.let { startFilter(it.trim().toUpperCase(Locale.ROOT)) }
          return false
        }
      })
    }
  }

  private fun startFilter(query: String) {

  }
}

fun businessAddOnsClick(type: ManageBusinessData.BusinessType, baseActivity: AppCompatActivity, session: UserSessionManager?) {
  when (type) {
    ManageBusinessData.BusinessType.ic_customer_call_d -> baseActivity.startVmnCallCard()
    ManageBusinessData.BusinessType.ic_customer_enquiries_d -> baseActivity.startBusinessEnquiry()
    ManageBusinessData.BusinessType.ic_daily_business_update_d -> baseActivity.startPostUpdate(session)

    ManageBusinessData.BusinessType.ic_product_cataloge_d,
    ManageBusinessData.BusinessType.ic_service_cataloge_d,
    -> baseActivity.startListServiceProduct(session)

    ManageBusinessData.BusinessType.ic_customer_testimonial_d -> baseActivity.startAddTestimonial(session)
    ManageBusinessData.BusinessType.ic_business_keyboard_d -> session?.let { baseActivity.startKeyboardActivity(it) }
    ManageBusinessData.BusinessType.clinic_logo -> baseActivity.startBusinessLogo(session)
    ManageBusinessData.BusinessType.feature_business_image -> baseActivity.startFeatureLogo(session)
    ManageBusinessData.BusinessType.business_hours -> baseActivity.startBusinessHours(session)

    ManageBusinessData.BusinessType.doctor_profile,
    ManageBusinessData.BusinessType.contact_details,
    -> baseActivity.startFragmentsFactory(session, fragmentType = "Business_Profile_Fragment_V2")

    ManageBusinessData.BusinessType.content_sync_acros_channels -> session?.let { baseActivity.startDigitalChannel(it) }
    ManageBusinessData.BusinessType.ic_custom_page_add -> baseActivity.startCreateCustomPage(session)
    ManageBusinessData.BusinessType.in_clinic_appointments -> baseActivity.startOrderAptConsultList(session, isConsult = false)
    ManageBusinessData.BusinessType.customer_order_d -> baseActivity.startOrderAptConsultList(session, isOrder = true)
    ManageBusinessData.BusinessType.video_consultations -> baseActivity.startOrderAptConsultList(session, isConsult = true)
    ManageBusinessData.BusinessType.newsletter_subscription -> baseActivity.startSubscriber()
    ManageBusinessData.BusinessType.picture_gallery -> baseActivity.startAddImageGallery(session)
    ManageBusinessData.BusinessType.premium_boost_support -> session?.let { baseActivity.startHelpAndSupportActivity(it) }
    ManageBusinessData.BusinessType.custom_payment_gateway -> baseActivity.startSelfBrandedGateway(session)
    ManageBusinessData.BusinessType.business_kyc_verification -> baseActivity.startBusinessKycBoost(session)
    ManageBusinessData.BusinessType.my_bank_account -> baseActivity.startMyBankAccount(session)
    ManageBusinessData.BusinessType.ic_project_terms_d -> baseActivity.startListProjectAndTeams(session)
    ManageBusinessData.BusinessType.ic_digital_brochures -> baseActivity.startListDigitalBrochure(session)

    ManageBusinessData.BusinessType.boost_payment_gateway -> {
    }
    ManageBusinessData.BusinessType.domain_name_ssl -> {
    }
    ManageBusinessData.BusinessType.ic_latest_news_d -> {
    }
    ManageBusinessData.BusinessType.ic_my_customers_clients_d -> {
    }
    ManageBusinessData.BusinessType.ic_featured_client_projects_d -> {
    }
    ManageBusinessData.BusinessType.ic_my_business_faqs -> {
    }
    ManageBusinessData.BusinessType.advertising_google_fb -> {
    }
    ManageBusinessData.BusinessType.appointment_settings -> {
    }
    ManageBusinessData.BusinessType.ic_e_commerce_setting_d -> {
    }
    ManageBusinessData.BusinessType.assisted_content_creation -> {
    }
    ManageBusinessData.BusinessType.autamated_seo_d -> {
    }
    ManageBusinessData.BusinessType.boost_data_security -> {
    }
    ManageBusinessData.BusinessType.e_commerce_website -> {
    }
    ManageBusinessData.BusinessType.facebook_lead_ads -> {
    }
    ManageBusinessData.BusinessType.facebook_likebox_plugin -> {
    }
    ManageBusinessData.BusinessType.my_email_accounts -> {
    }
    ManageBusinessData.BusinessType.ria_digital_assistant -> {
    }
    ManageBusinessData.BusinessType.unlimited_content_updates -> {
    }
    ManageBusinessData.BusinessType.unlimited_website_bandwidth -> {
    }
    ManageBusinessData.BusinessType.chatbot_analytics -> {
    }
    ManageBusinessData.BusinessType.website_chatbot -> {
    }
    ManageBusinessData.BusinessType.website_social_share_plugin -> {
    }
  }
}