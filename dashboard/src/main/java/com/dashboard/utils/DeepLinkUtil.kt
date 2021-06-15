package com.dashboard.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.framework.pref.FACEBOOK_PAGE_WITH_ID
import com.framework.pref.FACEBOOK_URL
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import java.util.*

const val facebook_chat_main = "facebookchatMain"
const val feedback_chat = "feedbackchat"
const val deep_link_call_tracker = "callTracker"
const val deeplink_manage_customer = "managecustomer"
const val keyboard = "Keyboard"
const val testimonials = "Testimonials"
const val facebook_chat = "facebookchat"
const val third_party_queries = "thirdPartyQueries"
const val facebookpage = "facebookpage"
const val addProduct = "addProduct"
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
const val subscribers = "subscribers"
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
const val deeplink_all_custom_pages = "all_custom_pages"
const val deeplink_analytics_website_visits = "total_website_visits"
const val deeplink_analytics_website_visitors = "total_website_visitors"
const val deeplink_background_images = "background_images"
const val deeplink_favicon = "favicon"
const val deeplink_appointment_summary = "appointment_summary"
const val deeplink_book_table = "book_table"
const val deeplink_my_add_ons = "my_add_ons"
const val deeplink_recommended_add_ons = "recommended_add_ons"
const val deeplink_item_on_market_place = "ITEM_ONS_MARKETPLACE"
const val deeplink_REFER_EARN = "refer_and_earn"
const val deeplink_compare_package = "compare_package_selection"
const val deeplink_package_bundle = "package_bundle"
const val deeplink_promo_banner = "promo_banner"
const val deeplink_create_order = "create_order"
const val deeplink_create_appointment = "create_appointment"
const val deeplink_create_consultation = "create_consultation"
const val deeplink_website_theme = "website_customization"
const val visit_to_new_website = "Woohoo! We have a new website. Visit it at"
const val tag_for_partners = ".nowfloats.com"

const val VISITS_TABLE = 0
const val VISITORS_TABLE = 1

class DeepLinkUtil(var baseActivity: AppCompatActivity, var session: UserSessionManager) {

  fun deepLinkPage(url: String, buyItemKey: String, isFromRia: Boolean) {
    try {
      if (url.isNotEmpty()) {
        if (url.contains(keyboard) || url.contains(keyboardSettings)) {
          baseActivity.startKeyboardActivity(session)
        } else if (url.contains(facebook_chat)) {
          //pending
        } else if (url.contains(third_party_queries)) {
          baseActivity.startThirdPartyQueries(session)
        } else if (url.contains(deeplink_create_appointment)) {
          baseActivity.startBookAppointmentConsult(session, false)
        } else if (url.contains(deeplink_create_consultation)) {
          baseActivity.startBookAppointmentConsult(session, true)
        } else if (url.contains(deeplink_create_order)) {
          baseActivity.startOrderCreate(session)
        } else if (url.contains(facebook_chat_main)) {
          //pending
        } else if (url.contains(deeplink_manage_customer)) {
          baseActivity.startManageCustomer(session)
        } else if (url.contains(feedback_chat)) {
          baseActivity.startHelpAndSupportActivity(session)
        } else if (url.contains(facebookpage)) {
          likeUsFacebook(baseActivity, "/reviews/")
        } else if (url.contains(deeplink_update)) {
          baseActivity.startPostUpdate(session)
        } else if (url.contains(deeplink_featuredimage)) {
          baseActivity.startBusinessProfileDetailEdit(session)
        } else if (url.contains(addProduct)) {
          baseActivity.startAddServiceProduct(session)
        } else if (url.contains(addCustomPage)) {
          baseActivity.startCustomPage(session, true)
        } else if (url.contains(myorders)) {
          baseActivity.startOrderAptConsultList(session, isOrder = true)
        } else if (url.contains(myorderdetail)) {
          if (session.fP_AppExperienceCode.equals("SVC") || session.fP_AppExperienceCode.equals("SPA") ||
              session.fP_AppExperienceCode.equals("SAL") || session.fP_AppExperienceCode.equals("DOC")) {
            baseActivity.startOrderAptConsultList(session)
          } else baseActivity.startOrderAptConsultList(session, isOrder = true)
        } else if (url.contains(appointment_fragment)) {
          if (session.fP_AppExperienceCode.equals("SVC") || session.fP_AppExperienceCode.equals("SPA") ||
              session.fP_AppExperienceCode.equals("SAL") || session.fP_AppExperienceCode.equals("DOC")) {
            baseActivity.startOrderAptConsultList(session)
          }
        } else if (url.contains(order_fragment)) {
          if ((session.fP_AppExperienceCode.equals("SVC") || session.fP_AppExperienceCode.equals("SPA") ||
                  session.fP_AppExperienceCode.equals("SAL") || session.fP_AppExperienceCode.equals("DOC")).not()) {
            baseActivity.startOrderAptConsultList(session, isOrder = true)
          }
        } else if (url.contains(consultation_fragment)) {
          if (session.fP_AppExperienceCode.equals("SVC") || session.fP_AppExperienceCode.equals("SPA") ||
              session.fP_AppExperienceCode.equals("SAL") || session.fP_AppExperienceCode.equals("DOC")) {
            baseActivity.startOrderAptConsultList(session, isConsult = true)
          }
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
            url.contains(deeplink_nfstoreimage) || url.contains(deeplink_nfstoreimage)) {
          baseActivity.startPricingPlan(session)
        } else if (url.contains(deeplink_searchqueries)) {
//          baseActivity.startSearchQuery(session)
        } else if (url.contains(blog)) {
          baseActivity.startBlog(url, session)
        } else if (url.contains(subscribers) || url.contains(new_subscribers)) {
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
          baseActivity.initiateAddonMarketplace(session, true, "", "")
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
        } else if (url.contains(deeplink_service_catalogue)) {
          baseActivity.startListServiceProduct(session)
        } else if (url.contains(deeplink_all_custom_pages)) {
          baseActivity.startCustomPage(session, false)
        } else if (url.contains(deeplink_analytics_website_visits)) {
          baseActivity.startSiteViewAnalytic(session, "TOTAL")
        } else if (url.contains(deeplink_analytics_website_visitors)) {
          baseActivity.startSiteViewAnalytic(session, "UNIQUE")
        } else if (url.contains(deeplink_background_images)) {
          baseActivity.startBackgroundImageGallery(session)
        } else if (url.contains(deeplink_favicon)) {
          baseActivity.startFeviconImage(session)
        } else if (url.contains(deeplink_appointment_summary)) {
          baseActivity.startAptOrderSummary(session)
        } else if (url.contains(deeplink_book_table)) {
          baseActivity.startBookTable(session)
        } else if (url.contains(deeplink_my_add_ons)) {
          baseActivity.delayProgressShow()
          baseActivity.initiateAddonMarketplace(session, false, "myAddOns", "")
        } else if (url.contains(deeplink_recommended_add_ons)) {
          baseActivity.delayProgressShow()
          baseActivity.initiateAddonMarketplace(session, false, "recommendedAddOns", "")
        } else if (buyItemKey.isNotEmpty() && url.contains(deeplink_item_on_market_place)) {
          baseActivity.delayProgressShow()
          baseActivity.initiateAddonMarketplace(session, false, "", buyItemKey)
        } else if (url.contains(deeplink_compare_package)) {
          baseActivity.initiateAddonMarketplace(session, false, "comparePackageSelection", "")
        } else if (url.contains(deeplink_package_bundle)) {
//          println("deeplink_package_bundle ${url}  ${buyItemKey}")
          Log.v("deeplink_package_bundle", " "+ url + " "+ buyItemKey)
//          baseActivity.initiateAddonMarketplace(session, false, "packageBundle", "")
          baseActivity.initiateAddonMarketplace(session, false, "packageBundle", buyItemKey)

        }else if (url.contains(deeplink_promo_banner)) {
          baseActivity.initiateAddonMarketplace(session, false, "promoBanner", buyItemKey)
        }else if (url.contains(deeplink_REFER_EARN)) {
          baseActivity.startReferralView(session)
        }else if (url.contains(deeplink_website_theme)) {
          baseActivity.startWebsiteTheme(session)
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

  }
}

fun likeUsFacebook(context: Context, review: String) {
  val facebookIntent: Intent = try {
    context.packageManager.getPackageInfo("com.facebook.katana", 0)
    Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_PAGE_WITH_ID))
  } catch (e: java.lang.Exception) {
    Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL + review))
  }
  facebookIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
  try {
    context.startActivity(facebookIntent)
  } catch (e: java.lang.Exception) {
    Toast.makeText(context, "unable to open facebook", Toast.LENGTH_SHORT).show()
  }
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