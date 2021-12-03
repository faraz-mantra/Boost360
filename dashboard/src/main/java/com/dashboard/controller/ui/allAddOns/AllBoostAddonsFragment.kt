package com.dashboard.controller.ui.allAddOns

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.dashboard.AppDashboardApplication
import com.dashboard.R

import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.dashboard.checkIsPremiumUnlock
import com.dashboard.databinding.FragmentAllBoostAddOnsBinding
import com.dashboard.model.live.addOns.AllBoostAddOnsData
import com.dashboard.model.live.addOns.ManageAddOnsBusinessResponse
import com.dashboard.model.live.addOns.ManageBusinessData
import com.dashboard.model.live.domainDetail.DomainDetailResponse
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.AddOnsViewModel
import com.framework.extensions.observeOnce
import com.framework.webengageconstant.BOOST_ADD_ONS_PAGE
import com.framework.webengageconstant.PAGE_VIEW
import com.framework.webengageconstant.WEBSITE_REPORT_ALL_VISITS_CLICK
import java.util.*
import kotlin.collections.ArrayList

const val LAST_SEEN_TEXT = "Last Seen"

class AllBoostAddonsFragment : AppBaseFragment<FragmentAllBoostAddOnsBinding, AddOnsViewModel>(), RecyclerItemClickListener {

  private var session: UserSessionManager? = null
  private var searchView: SearchView? = null
  private var adapterAddOns: AppBaseRecyclerViewAdapter<AllBoostAddOnsData>? = null
  private var addOnsList = ArrayList<AllBoostAddOnsData>()
  private var addOnsListFilter = ArrayList<AllBoostAddOnsData>()

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
    getDomainDetail()
    WebEngageController.trackEvent(BOOST_ADD_ONS_PAGE, PAGE_VIEW, session?.fpTag)
  }

  override fun onResume() {
    super.onResume()
    Handler().postDelayed({ getBoostAddOnsData() }, 100)
  }

  private fun getBoostAddOnsData() {
    viewModel?.getBoostAddOns(baseActivity)?.observeOnce(viewLifecycleOwner, {
      val response = it as? ManageAddOnsBusinessResponse
      val dataAction = response?.data?.firstOrNull { it1 ->
        it1.type.equals(
          session?.fP_AppExperienceCode,
          ignoreCase = true
        )
      }
      if (dataAction != null && dataAction.actionItem.isNullOrEmpty().not()) {
        dataAction.actionItem?.forEach { it2 ->
          it2.manageBusinessList =
            ArrayList(it2.manageBusinessList?.filter { it3 -> !it3.isHide } ?: ArrayList())
        }
        dataAction.actionItem?.map { it2 ->
          it2.manageBusinessList?.map { it3 ->
            if (it3.premiumCode.isNullOrEmpty().not()) {
              it3.isLock = session.checkIsPremiumUnlock(it3.premiumCode).not()
            }
          }
        }
        addOnsList.clear()
        addOnsListFilter.clear()
        val list = setLastSeenData(dataAction.actionItem!!)
        addOnsList.addAll(list)
        addOnsListFilter.addAll(list)
        if (adapterAddOns == null) {
          binding?.rvBoostAddOns?.apply {
            adapterAddOns =
              AppBaseRecyclerViewAdapter(baseActivity, list, this@AllBoostAddonsFragment)
            adapter = adapterAddOns
          }
        } else adapterAddOns?.notify(list)

      } else showShortToast(baseActivity.getString(R.string.manage_business_not_found))
    })
  }

  private fun setLastSeenData(data: ArrayList<AllBoostAddOnsData>): ArrayList<AllBoostAddOnsData> {
    val listAddOns = ManageBusinessData().getLastSeenData()
    if (listAddOns.isNotEmpty()) data.add(
      0,
      AllBoostAddOnsData(title = LAST_SEEN_TEXT, manageBusinessList = listAddOns, isLastSeen = true)
    )
    return data
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.BUSINESS_ADD_ONS_CLICK.ordinal -> {
        val data = item as? ManageBusinessData ?: return
        ManageBusinessData().saveLastSeenData(data)
        ManageBusinessData.BusinessType.fromName(data.businessType)?.let {
          if (it == ManageBusinessData.BusinessType.domain_name_ssl) {
            if (DomainDetailResponse().getDomainDetail() != null) baseActivity.startDomainDetail(
              session
            ) else getDomainDetail(true)

          } else businessAddOnsClick(it, baseActivity, session)
        }
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_search_icon_d, menu)
    val searchItem = menu.findItem(R.id.menu_item_search)
    if (searchItem != null) {
      searchView = searchItem.actionView as SearchView
      val searchAutoComplete =
        searchView?.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
      searchAutoComplete?.setHintTextColor(getColor(R.color.white_70))
      searchAutoComplete?.setTextColor(getColor(R.color.white))
      searchView?.setIconifiedByDefault(true)
      searchView?.queryHint = getString(R.string.search_boost_add_ons)
      searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
          return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
          newText?.let { startFilter(it.trim().toLowerCase(Locale.ROOT)) }
          return false
        }
      })
    }
  }

  private fun startFilter(query: String) {
    if (query.isNotEmpty()) {
      val list = ArrayList<ManageBusinessData>()
      addOnsListFilter.forEach { it0 ->
        if (it0.title.equals(LAST_SEEN_TEXT).not())
          it0.manageBusinessList?.forEach {
            if (it.title?.toLowerCase(Locale.ROOT)?.contains(query) == true) list.add(it)
          }
      }
      val listAddOns = ArrayList<AllBoostAddOnsData>()
      if (list.isNotEmpty()) listAddOns.add(
        AllBoostAddOnsData(
          title = "Boost Add-ons",
          manageBusinessList = list
        )
      )
      adapterAddOns?.notify(listAddOns)
    } else adapterAddOns?.notify(addOnsListFilter)
  }

  private fun getDomainDetail(isClick: Boolean = false) {
    if (isClick) showProgress()
    viewModel?.getDomainDetailsForFloatingPoint(session?.fpTag, getDomainDetailsParam())
      ?.observeOnce(viewLifecycleOwner, {
        if (isClick) hideProgress()
        val response = it as? DomainDetailResponse
        if (response?.isSuccess() == true) response.saveDomainDetail()
        if (isClick) baseActivity.startDomainDetail(session)
      })
  }

  private fun getDomainDetailsParam(): HashMap<String, String>? {
    val offersParam = HashMap<String, String>()
    offersParam["clientId"] = clientId
    return offersParam
  }
}

fun businessAddOnsClick(type: ManageBusinessData.BusinessType, baseActivity: AppCompatActivity, session: UserSessionManager?) {
  when (type) {
    ManageBusinessData.BusinessType.ic_customer_call_d,
    ManageBusinessData.BusinessType.ic_customer_call_tracker_d,
    -> baseActivity.startVmnCallCard(session)
    ManageBusinessData.BusinessType.ic_customer_enquiries_d -> baseActivity.startBusinessEnquiry(
      session
    )
    ManageBusinessData.BusinessType.ic_daily_business_update_d -> session?.let {
      baseActivity.startUpdateLatestStory(
        it
      )
    }
    ManageBusinessData.BusinessType.ic_product_cataloge_d,
    ManageBusinessData.BusinessType.ic_service_cataloge_d,
    -> baseActivity.startListServiceProduct(session)
    ManageBusinessData.BusinessType.ic_customer_testimonial_d -> baseActivity.startTestimonial(
      session,
      false
    )
    ManageBusinessData.BusinessType.ic_business_keyboard_d -> session?.let {
      baseActivity.startKeyboardActivity(
        it
      )
    }
    ManageBusinessData.BusinessType.clinic_logo -> baseActivity.startBusinessLogo(session)
    ManageBusinessData.BusinessType.featured_image_video -> baseActivity.startFeatureLogo(session)
    ManageBusinessData.BusinessType.business_hours -> baseActivity.startBusinessHours(session)
    ManageBusinessData.BusinessType.contact_details -> baseActivity.startBusinessInfoEmail(session)
    ManageBusinessData.BusinessType.content_sync_acros_channels -> session?.let {
      baseActivity.startDigitalChannel(
        it
      )
    }
    ManageBusinessData.BusinessType.ic_custom_page_add -> baseActivity.startCustomPage(
      session,
      false
    )
    ManageBusinessData.BusinessType.in_clinic_appointments -> baseActivity.startOrderAptConsultList(
      session,
      isConsult = false
    )
    ManageBusinessData.BusinessType.customer_order_d,
    ManageBusinessData.BusinessType.customer_booking_d,
    -> baseActivity.startOrderAptConsultList(session, isOrder = true)
    ManageBusinessData.BusinessType.video_consultations -> baseActivity.startOrderAptConsultList(
      session,
      isConsult = true
    )
    ManageBusinessData.BusinessType.newsletter_subscription -> baseActivity.startSubscriber(session)
    ManageBusinessData.BusinessType.picture_gallery,
    ManageBusinessData.BusinessType.client_logos_d,
    -> baseActivity.startAddImageGallery(session, false)
    ManageBusinessData.BusinessType.ria_digital_assistant -> session?.let {
      baseActivity.startHelpAndSupportActivity(
        it
      )
    }
    ManageBusinessData.BusinessType.custom_payment_gateway -> baseActivity.startSelfBrandedGateway(
      session
    )
    ManageBusinessData.BusinessType.business_kyc_verification -> baseActivity.startBusinessKycBoost(
      session
    )
    ManageBusinessData.BusinessType.my_bank_account -> baseActivity.startMyBankAccount(session)
    ManageBusinessData.BusinessType.ic_digital_brochures -> baseActivity.startListDigitalBrochure(
      session
    )
    ManageBusinessData.BusinessType.places_look_around_d -> baseActivity.startNearByView(session)
    ManageBusinessData.BusinessType.trip_advisor_reviews_d -> baseActivity.startListTripAdvisor(
      session
    )
    ManageBusinessData.BusinessType.team_page_d -> baseActivity.startListProjectAndTeams(session)
    ManageBusinessData.BusinessType.upcoming_batches_d -> baseActivity.startListBatches(session)
    ManageBusinessData.BusinessType.toppers_institute_d -> baseActivity.startListToppers(session)
    ManageBusinessData.BusinessType.website_visits_visitors -> baseActivity.startSiteViewAnalytic(
      session,
      "TOTAL",
      WEBSITE_REPORT_ALL_VISITS_CLICK
    )
    //remove after fixed with jio(business_name_d)
    ManageBusinessData.BusinessType.business_name_d,
    ManageBusinessData.BusinessType.clinic_basic_info,
    ManageBusinessData.BusinessType.business_description_d,
    -> baseActivity.startBusinessProfileDetailEdit(session)
    ManageBusinessData.BusinessType.ic_my_business_faqs -> baseActivity.startMobileSite(
      session,
      "https://www.getboost360.com/faqs/"
    )
    ManageBusinessData.BusinessType.website_social_share_plugin -> baseActivity.startBoostExtension(
      session
    )
    ManageBusinessData.BusinessType.project_portfolio_d -> baseActivity.startListProjectAndTeams(
      session
    )
    ManageBusinessData.BusinessType.table_reservations_d -> baseActivity.startBookTable(session)
    ManageBusinessData.BusinessType.sales_analytics -> baseActivity.startAptOrderSummary(session)
//    ManageBusinessData.BusinessType.search_analytics -> baseActivity.startSearchQuery(session)
    ManageBusinessData.BusinessType.ic_staff_profile_d -> baseActivity.startListStaff(session)

    ManageBusinessData.BusinessType.room_booking_engine_d,
    ManageBusinessData.BusinessType.search_analytics,
    ManageBusinessData.BusinessType.ic_ivr_faculty,
    ManageBusinessData.BusinessType.boost_payment_gateway,
    ManageBusinessData.BusinessType.advertising_google_fb,
    ManageBusinessData.BusinessType.appointment_settings,
    ManageBusinessData.BusinessType.assisted_content_creation,
    ManageBusinessData.BusinessType.autamated_seo_d,
    ManageBusinessData.BusinessType.boost_data_security,
    ManageBusinessData.BusinessType.e_commerce_website,
    ManageBusinessData.BusinessType.facebook_lead_ads,
    ManageBusinessData.BusinessType.facebook_likebox_plugin,
    ManageBusinessData.BusinessType.my_email_accounts,
    ManageBusinessData.BusinessType.premium_boost_support,
    ManageBusinessData.BusinessType.unlimited_content_updates,
    ManageBusinessData.BusinessType.unlimited_website_bandwidth,
    ManageBusinessData.BusinessType.chatbot_analytics,
    ManageBusinessData.BusinessType.website_chatbot,
    ManageBusinessData.BusinessType.sync_otas_channel_manager_d,
    ManageBusinessData.BusinessType.social_sharing_analytics,
    ManageBusinessData.BusinessType.membership_plans,
    ManageBusinessData.BusinessType.restaurant_story_d,
    ManageBusinessData.BusinessType.doctor_profile,
    ManageBusinessData.BusinessType.faculty_profiles_d,
    -> {
      Toast.makeText(baseActivity, AppDashboardApplication.instance.getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
    }
  }
}