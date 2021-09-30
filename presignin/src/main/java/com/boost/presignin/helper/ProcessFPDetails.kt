package com.boost.presignin.helper

import com.boost.presignin.model.fpdetail.UserFpDetailsResponse
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.utils.convertListObjToString

private const val WIDGET_IMAGE_GALLERY = "IMAGEGALLERY"
private const val WIDGET_IMAGE_TOB = "TOB"
private const val WIDGET_IMAGE_TIMINGS = "TIMINGS"
private const val WIDGET_PRODUCT_GALLERY = "PRODUCTCATALOGUE"
private const val WIDGET_FB_LIKE_BOX = "FbLikeBox"
private const val WIDGET_CUSTOMPAGES = "CUSTOMPAGES"
private const val FP_WEB_WIDGET_DOMAIN = "DOMAINPURCHASE"

class ProcessFPDetails(var session: UserSessionManager) {

  fun storeFPDetails(get_fp_details_model: UserFpDetailsResponse) {
    try {
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CITY, get_fp_details_model.city)

      val name =
        if (get_fp_details_model.contactName == null) "" else if (get_fp_details_model.contactName.toLowerCase()
            .equals("null")
        ) "" else get_fp_details_model.contactName
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME, name)
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TAG, get_fp_details_model.tag)
      session.storeFPDetails(
        Key_Preferences.GET_FP_EXPERIENCE_CODE,
        get_fp_details_model.appExperienceCode
      )
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS, get_fp_details_model.address)
      session.storeFPDetails(
        Key_Preferences.GET_FP_DETAILS_TILE_IMAGE_URI,
        get_fp_details_model.tileImageUri
      )
      session.storeFPDetails(
        Key_Preferences.GET_FP_DETAILS_IMAGE_URI,
        get_fp_details_model.imageUri
      )
      session.storeFPDetails(
        Key_Preferences.GET_FP_DETAILS_FAVICON_IMAGE_URI,
        get_fp_details_model.faviconUrl
      )
      session.storeFPDetails(
        Key_Preferences.GET_FP_DETAILS_CREATED_ON,
        get_fp_details_model.createdOn
      )
      session.storeFPDetails(
        Key_Preferences.GET_FP_DETAILS_EXPIRY_DATE,
        get_fp_details_model.expiryDate
      )
      session.storeFPDetails(
        Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE,
        get_fp_details_model.paymentState
      )
      session.storeFPDetails(Key_Preferences.LANGUAGE_CODE, get_fp_details_model.languageCode)
      session.storeFPDetails(
        Key_Preferences.EXTERNAL_SOURCE_ID,
        get_fp_details_model.externalSourceId
      )

      if ("0" == get_fp_details_model.lat && "0" == get_fp_details_model.lng) {
        session.storeFPDetails(Key_Preferences.LATITUDE, "")
        session.storeFPDetails(Key_Preferences.LONGITUDE, "")
      } else {
        session.storeFPDetails(Key_Preferences.LATITUDE, get_fp_details_model.lat)
        session.storeFPDetails(Key_Preferences.LONGITUDE, get_fp_details_model.lng)
      }
      if (get_fp_details_model.accountManagerId != null && get_fp_details_model.accountManagerId != "null") session.storeFPDetails(
        Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID,
        get_fp_details_model.accountManagerId
      )

      try {
        if (get_fp_details_model.category != null && get_fp_details_model.category.size > 0) {
          session.storeFPDetails(
            Key_Preferences.GET_FP_DETAILS_CATEGORY,
            get_fp_details_model.category[0].key
          )

        }
      } catch (e: Exception) {
        e.printStackTrace()
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY, "GENERAL")

      }

      //session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME, get_fp_details_model.ContactName);
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL, get_fp_details_model.email)
      session.storeFPDetails(
        Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE,
        get_fp_details_model.countryPhoneCode
      )

      session.storeFPDetails(
        Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME,
        get_fp_details_model.name
      )
      session.storeFPDetails(
        Key_Preferences.GET_FP_DETAILS_DESCRIPTION,
        get_fp_details_model.description
      )
      session.storeFPDetails(
        Key_Preferences.GET_FP_WEBTEMPLATE_ID,
        get_fp_details_model.webTemplateId
      )
      session.storeFpWebTempalteType(get_fp_details_model.webTemplateType)

      session.storeFPDetails(
        Key_Preferences.GET_FP_DETAILS_FBPAGENAME,
        get_fp_details_model.fBPageName
      )
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PARENTID, get_fp_details_model.parentId)
      session.storeFPDetails(
        Key_Preferences.PRODUCT_CATEGORY,
        get_fp_details_model.productCategoryVerb
      )

      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY, get_fp_details_model.country)

      try {
        session.storeFPDetails(
          Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL,
          get_fp_details_model.paymentLevel
        )
      } catch (e: Exception) {
        e.printStackTrace()
      }
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE, get_fp_details_model.uri)
      session.storeFPDetails(
        Key_Preferences.MAIN_PRIMARY_CONTACT_NUM,
        get_fp_details_model.primaryNumber
      )
      try {
//        Util.GettingBackGroundId(session) use for background image (API Pending)
        if (get_fp_details_model.contacts != null) {
          when (get_fp_details_model.contacts.size) {
            1 -> {
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER,
                get_fp_details_model.contacts.get(0).contactNumber
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME,
                get_fp_details_model.contacts.get(0).contactName
              )
              session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1, "")
              session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1, "")
              session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3, "")
              session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3, "")
            }
            2 -> {
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER,
                get_fp_details_model.contacts.get(0).contactNumber
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME,
                get_fp_details_model.contacts.get(0).contactName
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1,
                get_fp_details_model.contacts.get(1).contactNumber
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1,
                get_fp_details_model.contacts.get(1).contactName
              )
              session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3, "")
              session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3, "")
            }
            else -> {
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER,
                get_fp_details_model.contacts.get(0).contactNumber
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME,
                get_fp_details_model.contacts.get(0).contactName
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1,
                get_fp_details_model.contacts.get(1).contactNumber
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1,
                get_fp_details_model.contacts.get(1).contactName
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3,
                get_fp_details_model.contacts.get(2).contactNumber
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3,
                get_fp_details_model.contacts.get(2).contactName
              )
            }
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
      val widgetsList: ArrayList<String> = get_fp_details_model.fPWebWidgets ?: ArrayList()
      session.storeFPDetails(Key_Preferences.STORE_WIDGETS, convertListObjToString(widgetsList))

      if (widgetsList.contains(FP_WEB_WIDGET_DOMAIN)) {
        session.storeFPDetails(Key_Preferences.GET_FP_WEB_WIDGET_DOMAIN, FP_WEB_WIDGET_DOMAIN)
      } else {
        session.storeFPDetails(Key_Preferences.GET_FP_WEB_WIDGET_DOMAIN, "")
      }
      if (widgetsList.contains(WIDGET_IMAGE_GALLERY)) {
        session.storeFPDetails(
          Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_GALLERY,
          WIDGET_IMAGE_GALLERY
        )
      } else {
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_GALLERY, "")
      }
      if (widgetsList.contains(WIDGET_IMAGE_TIMINGS)) {
        session.storeFPDetails(
          Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS,
          WIDGET_IMAGE_TIMINGS
        )
      } else {
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS, "")
      }
      if (widgetsList.contains(WIDGET_IMAGE_TOB)) {
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TOB, WIDGET_IMAGE_TOB)
      } else {
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TOB, "")
      }
      if (widgetsList.contains(WIDGET_PRODUCT_GALLERY)) {
        session.storeFPDetails(
          Key_Preferences.GET_FP_DETAILS_WIDGET_PRODUCT_GALLERY,
          WIDGET_PRODUCT_GALLERY
        )
      } else {
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_PRODUCT_GALLERY, "")
      }
      if (widgetsList.contains(WIDGET_FB_LIKE_BOX)) {
        session.storeFPDetails(
          Key_Preferences.GET_FP_DETAILS_WIDGET_FB_LIKE_BOX,
          WIDGET_FB_LIKE_BOX
        )
      } else {
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_FB_LIKE_BOX, "")
      }
      if (widgetsList.contains(WIDGET_CUSTOMPAGES)) {
        session.storeFPDetails(
          Key_Preferences.GET_FP_DETAILS_WIDGET_CUSTOMPAGES,
          WIDGET_CUSTOMPAGES
        )
      } else {
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_CUSTOMPAGES, "")
      }
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE, get_fp_details_model.pinCode)

      session.storeFPDetails(
        Key_Preferences.GET_FP_DETAILS_ROOTALIASURI,
        get_fp_details_model.rootAliasUri
      )
      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl, get_fp_details_model.logoUrl)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    try {
      if (get_fp_details_model.timings != null) {
        session.storeBooleanDetails(Key_Preferences.IS_BUSINESS_TIME_AVAILABLE, true)
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME, "00")
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME, "00")
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME, "00")
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME, "00")
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME, "00")
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME, "00")
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME, "00")
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME, "00")
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME, "00")
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME, "00")
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME, "00")
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME, "00")
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME, "00")
        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME, "00")
        for (i in 0 until get_fp_details_model.timings.size) {
          when (i) {
            0 -> {
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME,
                get_fp_details_model.timings[0].from
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME,
                get_fp_details_model.timings[0].to
              )
              session.businessHours = true
            }
            1 -> {
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME,
                get_fp_details_model.timings[1].from
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME,
                get_fp_details_model.timings[1].to
              )
            }
            2 -> {
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME,
                get_fp_details_model.timings[2].from
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME,
                get_fp_details_model.timings[2].to
              )
            }
            3 -> {
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME,
                get_fp_details_model.timings[3].from
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME,
                get_fp_details_model.timings[3].to
              )
            }
            4 -> {
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME,
                get_fp_details_model.timings[4].from
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME,
                get_fp_details_model.timings[4].to
              )
            }
            5 -> {
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME,
                get_fp_details_model.timings[5].from
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME,
                get_fp_details_model.timings[5].to
              )
            }
            6 -> {
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME,
                get_fp_details_model.timings[6].from
              )
              session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME,
                get_fp_details_model.timings[6].to
              )
            }
          }
        }
      }
      session.storeFPDetails(
        Key_Preferences.GET_FP_DETAILS_APPLICATION_ID,
        get_fp_details_model.applicationId
      )
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}