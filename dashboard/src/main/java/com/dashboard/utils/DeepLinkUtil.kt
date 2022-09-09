package com.dashboard.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.appservice.constant.FragmentType
import com.dashboard.R
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.webengageconstant.ABOUT_BOOST_FB_LIKE
import com.framework.webengageconstant.CLICK
import com.framework.webengageconstant.NO_EVENT_VALUE
import java.util.*

const val facebook_chat_main = "facebookchatMain"
const val feedback_chat = "feedbackchat"
const val deep_link_call_tracker = "callTracker"
const val deeplink_customer_enquires = "customerenquires"
const val keyboard = "Keyboard"
const val testimonials = "Testimonials"
const val payment_gateway = "Payment_Gateway"
const val digital_brochure= "Digital_Brochure"
const val projectteam= "ProjectTeam"
const val staff_profile ="Staff_Profile"
const val our_toppers ="Our_Toppers"
const val upcoming_batches ="Upcoming_Batches"
const val faculty ="Faculty"
const val latest_updates ="Latest_Updates"
const val featured_image ="Featured_Image"
const val doctor_profile ="Doctor_Profile"
const val trip_advisor ="Trip_Advisor"
const val nearby_places ="Nearby_Places"
const val business_kyc ="Business_Kyc"
const val facebook_chat = "facebookchat"
const val third_party_queries = "thirdPartyQueries"
const val facebookpage = "facebookpage"
const val addProduct = "addProduct"
const val addService = "addService"
const val keyboardSettings = "keyboardSettings"
const val addCustomPage = "addCustomPage"
const val myorders = "myorders"
const val myorderdetail = "myorderdetail"
const val appointment_fragment = "APPOINTMENT_FRAGMENT"
const val order_fragment = "ORDER_FRAGMENT"
const val consultation_fragment = "CONSULTATION_FRAGMENT"
const val enquiries = "enquiries"
const val store_url = "store"
const val blog = "blog"
const val deeplink_subscribers = "subscribers"
const val deeplink_unsubscribe = "unsubscribe"
const val new_subscribers = "newsubscriber"
const val share_lower_case = "share"
const val visits = "visits"
const val viewgraph = "viewgraph"

const val deeplink_business_app = "businessapp"
const val deeplink_update = "update"
const val deeplink_upgrade = "upgrade"
const val deeplink_featuredimage = "featuredimage"
const val deeplink_analytics = "analytics"
const val deeplink_bizenquiry = "ttb"
const val deeplink_store = "nfstore"
const val deeplink_searchqueries = "searchqueries"
const val deeplink_setings = "settings"
const val deeplink_socailsharing = "social"
const val deeplink_profile = "profile"
const val deeplink_contact = "contact"
const val deeplink_bizaddress = "address"
const val deeplink_bizhours = "hours"
const val deeplink_bizlogo = "logo"
const val deeplink_nfstoreDomainTTBCombo = "bookdomain"
const val deeplink_imageGallery = "imagegallery"
const val deeplink_propack = "proPack"
const val deeplink_nfstoreseo = "nfstoreseo"
const val deeplink_nfstorettb = "nfstorettb"
const val deeplink_nfstoreimage = "nfstoreimage"
const val deeplink_gplaces = "gplaces"
const val deeplink_nfstorebiztiming = "nfstorebiztiming"
const val deeplink_ProductGallery = "productGallery"
const val deeplink_sitemeter = "sitemeter"
const val deeplink_DR_SCORE = "dr_score"
const val deeplink_assuredPurchase = "assuredPurchase"
const val deeplink_accSettings = "accSettings"
const val deeplink_uniqueVisitor = "uniqueVisitor"
const val deeplink_add_ons_marketplace = "ADD_ONS_MARKETPLACE"
const val addon_marketplace = "Add-Ons Marketplace"
const val deeplink_cart_fragment = "CART_FRAGMENT"
const val deeplink_notification = "notification"
const val deeplink_site_health = "site_health"
const val deeplink_manage_content = "manage_content"
const val deeplink_my_bank_account = "my_bank_account"
const val deeplink_boost_360_extensions = "boost_360_extensions"
const val deeplink_purchased_plans = "your_purchased_plans"
const val deeplink_digital_channels = "digital_channels"
const val deeplink_call_tracker_add_on = "call_tracker_add_on"
const val deeplink_service_catalogue = "service_catalogue"
const val deeplink_product_catalogue = "product_catalogue"
const val deeplink_ecommerce_setting = "e_commerce_setting"
const val deeplink_appointment_setting = "appointment_setting"
const val deeplink_all_custom_pages = "all_custom_pages"
const val deeplink_analytics_website_visits = "total_website_visits"
const val deeplink_analytics_website_visitors = "total_website_visitors"
const val deeplink_background_images = "background_images"
const val deeplink_favicon = "favicon"
const val deeplink_order_summary = "order_summary"
const val deeplink_appointment_summary = "appointment_summary"
const val deeplink_my_add_ons = "my_add_ons"
const val deeplink_recommended_add_ons = "recommended_add_ons"
const val deeplink_item_on_market_place = "ITEM_ONS_MARKETPLACE"
const val deeplink_REFER_EARN = "refer_and_earn"
const val deeplink_compare_package = "compare_package_selection"
const val deeplink_package_bundle = "package_bundle"
const val deeplink_promo_banner = "promo_banner"
const val deeplink_expert_contact = "expert_connect"
const val deeplink_website_theme = "website_customization"
const val deeplink_dashboard_tab = "dashboard_tab"
const val deeplink_website_tab = "website_tab"
const val deeplink_enquiries_tab = "enquiries_tab"
const val deeplink_more_tab = "more_tab"
const val deeplink_open_business_card = "open_business_card"
const val deeplink_owner_info = "owner_info"
const val deeplink_festive_poster = "festive_poster"
const val deeplink_domain_booking = "domain_booking"
const val deeplink_topper_list = "topper_list"
const val deeplink_upcoming_batch = "upcoming_batch"
const val deeplink_faculty_member = "faculty_member"
const val deeplink_book_table = "book_table"
const val deeplink_create_order = "create_order"
const val deeplink_create_appointment = "create_appointment"
const val deeplink_create_consultation = "create_consultation"

const val deeplink_add_to_menu = "add_to_menu"
const val deeplink_add_table_booking = "add_table_booking"
const val deeplink_add_customer_testimonial = "add_customer_testimonial"
const val deeplink_book_in_clinic_appointment = "book_in_clinic_appointment"
const val deeplink_book_video_consultation = "book_video_consultation"
const val deeplink_add_doctor = "add_doctor"
const val deeplink_add_new_room_type = "add_new_room_type"
const val deeplink_post_seasonal_offer = "post_seasonal_offer"
const val deeplink_add_nearby_attraction = "add_nearby_attraction"
const val deeplink_add_featured_image = "add_featured_image"

const val deeplink_user_merchant_profile = "user_merchant_profile"
const val visit_to_new_website = "Woohoo! We have a new website. Visit it at"
const val tag_for_partners = ".nowfloats.com"

const val VISITS_TABLE = 0
const val VISITORS_TABLE = 1

class DeepLinkUtil(var baseActivity: AppCompatActivity, var session: UserSessionManager, var baseFragment: Fragment?) {
  private val TAG = "DeepLinkUtil"
  fun deepLinkPage(url: String, buyItemKey: String, isFromRia: Boolean) {
    Log.i(TAG, "deepLinkPage: " + url)
    try {
      if (url.isNotEmpty()) {
        if (url.contains(keyboard) || url.contains(keyboardSettings)) {
          baseActivity.startKeyboardActivity(session)
        } else if (url.contains(facebook_chat)) {
          //pending
        } else if (url.contains(third_party_queries)) {
          baseActivity.startThirdPartyQueries(session)
        } else if (session.isService() && (url.contains(deeplink_create_appointment) || url.contains(deeplink_book_in_clinic_appointment))) {
          baseActivity.startBookAppointmentConsult(session, false)
        } else if (session.isDocHos() && (url.contains(deeplink_create_consultation) || url.contains(deeplink_book_video_consultation))) {
          baseActivity.startBookAppointmentConsult(session, true)
        } else if (session.isProduct() && url.contains(deeplink_create_order)) {
          baseActivity.startOrderCreate(session)
        } else if (url.contains(facebook_chat_main)) {
          //pending
        } else if (url.contains(deeplink_customer_enquires)) {
          baseActivity.startBusinessEnquiry(session)
        } else if (url.contains(feedback_chat)) {
          baseActivity.startHelpAndSupportActivity(session)
        } else if (url.contains(facebookpage)) {
          likeUsFacebook(baseActivity, "/reviews/")
        } else if (url.contains(deeplink_update)) {
          baseActivity.startPostUpdate()
        } else if (url.contains(deeplink_featuredimage)) {
          baseActivity.startBusinessProfileDetailEdit(session)
        } else if (session.isProduct() && url.contains(addProduct)) {
          baseActivity.startAddServiceProduct(session)
        } else if (session.isService() && url.contains(addService)) {
          baseActivity.startAddServiceProduct(session)
        } else if (session.isRestaurant() && url.contains(deeplink_add_to_menu)) {
          baseActivity.startAddServiceProduct(session)
        } else if (session.isHotel() && url.contains(deeplink_add_new_room_type)) {
          baseActivity.startAddServiceProduct(session)
        } else if (url.contains(addCustomPage)) {
          baseActivity.startCustomPage(session, true)
        } else if (session.isProduct() && (url.contains(myorders) || url.contains(myorderdetail) || url.contains(order_fragment))) {
          baseActivity.startOrderAptConsultList(session, isOrder = true)
        } else if (session.isSpaSalSvc() && url.contains(appointment_fragment)) {
          baseActivity.startOrderAptConsultList(session)
        } else if (session.isDocHos() && url.contains(consultation_fragment)) {
          baseActivity.startOrderAptConsultList(session, isConsult = url.contains(consultation_fragment))
        } else if (url.contains(deeplink_upgrade)) {
          baseActivity.upgradeApp()
        } else if (url.contains(deeplink_analytics)) {
          //pending
        } else if (url.contains(deeplink_bizenquiry) || url.contains(enquiries)) {
          baseActivity.startBusinessEnquiry(session)
        } else if (url.contains(deep_link_call_tracker)) {
          baseActivity.startVmnCallCard(session)
        } else if (url.contains(store_url) || url.contains(deeplink_store) ||
          url.contains(deeplink_propack) || url.contains(deeplink_nfstoreseo) ||
          url.contains(deeplink_nfstorettb) || url.contains(deeplink_nfstorebiztiming) ||
          url.contains(deeplink_nfstoreimage) || url.contains(deeplink_nfstoreimage)
        ) {
          baseActivity.startPricingPlan(session)
        } else if (url.contains(deeplink_searchqueries)) {
//          baseActivity.startSearchQuery(session)
        } else if (url.contains(blog)) {
          baseActivity.startBlog(url, session)
        } else if (url.contains(deeplink_subscribers) || url.contains(new_subscribers) || url.contains(deeplink_unsubscribe)) {
          baseActivity.startSubscriber(session)
        } else if (url.contains(share_lower_case)) {
          baseActivity.shareWebsite(session)
        } else if (url.contains(visits) || url.contains(viewgraph)) {
          baseActivity.startAnalytics(session, table_name = VISITS_TABLE)
        } else if (url.contains(deeplink_business_app)) {
          //pending
        } else if (url.contains(deeplink_socailsharing)) {
          baseActivity.startDigitalChannel(session)
        } else if (url.contains(deeplink_notification)) {
          baseActivity.startNotification(session)
        } else if (url.contains(deeplink_profile)) {
          baseActivity.startBusinessProfileDetailEdit(session)
        } else if (url.contains(deeplink_contact)) {
          baseActivity.startBusinessInfoEmail(session)
        } else if (url.contains(deeplink_bizaddress)) {
          baseActivity.startBusinessAddress(session)
        } else if (url.contains(deeplink_bizhours)) {
          baseActivity.startBusinessHours(session)
        } else if (url.contains(deeplink_bizlogo)) {
          baseActivity.startBusinessLogo(session)
        } else if (url.contains(deeplink_nfstoreDomainTTBCombo)) {
          baseActivity.startBusinessProfileDetailEdit(session)
        } else if (url.contains(deeplink_sitemeter) || url.contains(deeplink_site_health) || url.contains(deeplink_DR_SCORE)) {
          baseActivity.startReadinessScoreView(session, 0)
        } else if (url.contains(deeplink_imageGallery)) {
          baseActivity.startAddImageGallery(session, isCreate = false)
        } else if (url.contains(deeplink_ProductGallery)) {
          baseActivity.startProductGallery(session)
        } else if (url.contains(deeplink_assuredPurchase)) {
          baseActivity.startManageInventoryActivity(session)
        } else if (url.contains(deeplink_gplaces)) {
          //pending
        } else if (url.contains(deeplink_accSettings)) {
          baseActivity.startSettingActivity(session)
        } else if (url.contains(deeplink_uniqueVisitor)) {
          baseActivity.startAnalytics(session, VISITORS_TABLE)
        } else if (url.contains(addon_marketplace) || url.contains(deeplink_add_ons_marketplace)) {
          baseActivity.delayProgressShow()
          baseActivity.initiateAddonMarketplace(session, false, "", "")
        } else if (url.contains(deeplink_cart_fragment)) {
          baseActivity.delayProgressShow()
          baseActivity.initiateCart(session, true, "", buyItemKey)
        } else if (url.contains(deeplink_manage_content)) {
          baseActivity.startManageContentActivity(session)
        } else if (url.contains(deeplink_my_bank_account)) {
          baseActivity.startMyBankAccount(session)
        } else if (url.contains(deeplink_boost_360_extensions)) {
          baseActivity.startBoostExtension(session)
        } else if (url.contains(deeplink_purchased_plans)) {
          //pending
        } else if (url.contains(deeplink_digital_channels)) {
          baseActivity.startDigitalChannel(session)
        } else if (url.contains(deeplink_call_tracker_add_on)) {
          baseActivity.startVmnCallCard(session)
        } else if (session.isService() && url.contains(deeplink_service_catalogue)) {
          baseActivity.startListServiceProduct(session)
        } else if (session.isProduct() && url.contains(deeplink_product_catalogue)) {
          baseActivity.startListServiceProduct(session)
        } else if (session.isProduct() && url.contains(deeplink_ecommerce_setting)) {
          baseActivity.startEcommerceAppointmentSetting(session)
        } else if (session.isService() && url.contains(deeplink_appointment_setting)) {
          baseActivity.startEcommerceAppointmentSetting(session)
        } else if (url.contains(deeplink_all_custom_pages)) {
          baseActivity.startCustomPage(session, false)
        } else if (url.contains(deeplink_analytics_website_visits)) {
          baseActivity.startSiteViewAnalytic(session, "TOTAL")
        } else if (url.contains(deeplink_analytics_website_visitors)) {
          baseActivity.startSiteViewAnalytic(session, "UNIQUE")
        } else if (url.contains(deeplink_background_images)) {
          baseFragment!!.startBackgroundActivity(session, FragmentType.BACKGROUND_IMAGE_FRAGMENT)
        } else if (url.contains(deeplink_favicon)) {
          baseActivity.startFeviconImage(session)
        } else if (session.isProduct() && url.contains(deeplink_order_summary)) {
          baseActivity.startAptOrderSummary(session)
        } else if (session.isService() && url.contains(deeplink_appointment_summary)) {
          baseActivity.startAptOrderSummary(session)
        } else if (session.isRestaurant() && url.contains(deeplink_book_table)) {
          baseActivity.startBookTable(session)
        } else if (session.isRestaurant() && url.contains(deeplink_add_table_booking)) {
          baseActivity.startBookTable(session, true)
        } else if (url.contains(deeplink_my_add_ons)) {
          baseActivity.delayProgressShow()
          baseActivity.initiateAddonMarketplace(session, false, "myAddOns", "")
        } else if (url.contains(deeplink_recommended_add_ons)) {
          baseActivity.delayProgressShow()
          baseActivity.initiateAddonMarketplace(session, false, "recommendedAddOns", "")
        } else if (buyItemKey.isNotEmpty() && url.contains(deeplink_item_on_market_place)) {
          baseActivity.delayProgressShow()
          baseActivity.initiateAddonMarketplace(session, false, "deeplinkRedirection", buyItemKey)
        } else if (url.contains(deeplink_compare_package)) {
          baseActivity.initiateAddonMarketplace(session, false, "comparePackageSelection", "")
        } else if (url.contains(deeplink_package_bundle)) {
          Log.v("deeplink_package_bundle", " $url $buyItemKey")
//        baseActivity.initiateAddonMarketplace(session, false, "packageBundle", "")
          baseActivity.initiateAddonMarketplace(session, false, "packageBundle", buyItemKey)

        } else if (url.contains(deeplink_promo_banner)) {
          baseActivity.initiateAddonMarketplace(session, false, "promoBanner", buyItemKey)
        } else if (url.contains(deeplink_REFER_EARN)) {
          baseActivity.startReferralView(session)
        } else if (url.contains(deeplink_website_theme)) {
          baseActivity.startWebsiteTheme(session)
        } else if (url.contains(deeplink_expert_contact)) {
          Log.v("deeplink_expert_contact", " $url $buyItemKey")
          baseActivity.initiateAddonMarketplace(session, false, "expertContact", "",false)
        } else if (session.isSpa() && url.contains(deeplink_owner_info)) {
          baseActivity.startOwnersInfo(session)
        } else if (url.contains(deeplink_domain_booking)) {
          baseActivity.startDomainDetail(session)
        } else if (url.contains(deeplink_festive_poster)) {
          baseActivity.startFestivePosterActivity(true)
        } else if (url.contains(deeplink_user_merchant_profile)) {
          baseActivity.startUserProfileDetail(session)
        }else if (url.contains(testimonials)) {
          baseActivity.startTestimonial(session)
        } else if (url.contains(projectteam)) {
          baseActivity.startListProjectAndTeams(session)
        }else if (url.contains(digital_brochure)) {
          baseActivity.startAddDigitalBrochure(session)
        } else if (url.contains(payment_gateway)) {
          baseActivity.startSelfBrandedGateway(session)
        }else if (url.contains(staff_profile)) {
          baseActivity.startListStaff(session)
        }else if (url.contains(our_toppers)) {
          baseActivity.startListToppers(session)
        }else if (url.contains(upcoming_batches)) {
          baseActivity.startListBatches(session)
        }else if (url.contains(faculty)) {
          baseActivity.startFacultyMember(session)
        }else if (url.contains(latest_updates)) {
          baseActivity.startUpdateLatestStory(session)
        }else if (url.contains(featured_image)) {
          baseActivity.startFeatureLogo(session)
        }else if (url.contains(doctor_profile)) {
          baseActivity.startListDoctors(session)
        }else if (url.contains(trip_advisor)) {
          baseActivity.startListTripAdvisor(session)
        }else if (url.contains(nearby_places)) {
          baseActivity.startNearByView(session)
        }else if (url.contains(business_kyc)) {
          baseActivity.startBusinessKycBoost(session)
        } else if (session.isEducation() && url.contains(deeplink_topper_list)) {
          baseActivity.startListToppers(session)
        } else if (session.isEducation() && url.contains(deeplink_upcoming_batch)) {
          baseActivity.startListBatches(session)
        } else if (session.isEducation() && url.contains(deeplink_faculty_member)) {
          baseActivity.startFacultyMember(session)
        } else if (url.contains(deeplink_add_customer_testimonial)) {
          baseActivity.startTestimonial(session, true)
        } else if (session.isDocHos() && url.contains(deeplink_add_doctor)) {
          baseActivity.startAddStaff(session)
        } else if (session.isHotel() && url.contains(deeplink_post_seasonal_offer)) {
          baseActivity.startAddSeasonalOffer(session)
        } else if (session.isHotel() && url.contains(deeplink_add_nearby_attraction)) {
          baseActivity.startNearByView(session, true)
        } else if (url.contains(deeplink_add_featured_image)) {
          baseActivity.startFeatureLogo(session)
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

  }
}

fun likeUsFacebook(context: Context, review: String) {
  WebEngageController.trackEvent(ABOUT_BOOST_FB_LIKE, CLICK, NO_EVENT_VALUE)
  if (context.getString(R.string.settings_facebook_page_id).isNotEmpty() || context.getString(R.string.settings_facebook_like_link).isNotEmpty()) {
    val facebookIntent: Intent = try {
      context.packageManager.getPackageInfo(context.getString(R.string.facebook_package), 0)
      Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.settings_facebook_page_id)))
    } catch (e: Exception) {
      Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.settings_facebook_like_link) + review))
    }
    facebookIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
    try {
      context.startActivity(facebookIntent)
    } catch (e: Exception) {
      Toast.makeText(context, context.getString(R.string.unable_to_open_facebook), Toast.LENGTH_SHORT).show()
    }
  } else Toast.makeText(context, context.getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.upgradeApp() {
  try {
    val appPackageName: String = this.packageName
    try {
      this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
    } catch (anfe: ActivityNotFoundException) {
      this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBlog(urlN: String, session: UserSessionManager) {
  try {
    var url = urlN
    url = if (url.isNotEmpty()) {
      "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI)
    } else {
      ("http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)?.toLowerCase(Locale.ROOT) + tag_for_partners)
    }
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(intent)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.shareWebsite(session: UserSessionManager) {
  try {
    var url: String? = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI)
    url = if (url.isNullOrEmpty().not()) {
      val eol = System.getProperty("line.separator")
      visit_to_new_website + eol + url!!.toLowerCase(Locale.ROOT)
    } else {
      val eol = System.getProperty("line.separator")
      (visit_to_new_website + eol + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)?.toLowerCase(Locale.ROOT) + tag_for_partners)
    }
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, url)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(Intent.createChooser(intent, "Share with:"))
    session.websiteshare = true
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

enum class DashboardTabs(var title: String, var position: Int) {
  dashboard_tab(deeplink_dashboard_tab, 0), website_tab(deeplink_website_tab, 1), enquiries_tab(deeplink_enquiries_tab, 2),
  more_tab(deeplink_more_tab, 4), open_business_card(deeplink_open_business_card, 0);

  companion object {
    fun fromUrl(url: String?): DashboardTabs? = values().firstOrNull { url?.contains(it.title) == true }
  }
}