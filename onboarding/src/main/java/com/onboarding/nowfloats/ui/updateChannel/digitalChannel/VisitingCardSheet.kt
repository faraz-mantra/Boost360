package com.onboarding.nowfloats.ui.updateChannel.digitalChannel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.pref.UserSessionManager
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.framework.webengageconstant.*
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.DialogDigitalCardShareBinding
import com.onboarding.nowfloats.extensions.capitalizeWords
import com.onboarding.nowfloats.model.digitalCard.CardData
import com.onboarding.nowfloats.model.digitalCard.DigitalCardData
import com.onboarding.nowfloats.model.digitalCard.getLastShareCard
import com.onboarding.nowfloats.model.digitalCard.savePositionCard
import com.onboarding.nowfloats.model.profile.MerchantProfileResponse
import com.onboarding.nowfloats.model.profile.ProfileProperties
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.ui.updateChannel.startFragmentChannelActivity
import com.onboarding.nowfloats.utils.WebEngageController
import com.onboarding.nowfloats.utils.viewToBitmap
import com.onboarding.nowfloats.viewmodel.channel.ChannelPlanViewModel
import java.util.*
import kotlin.collections.ArrayList

const val WA_KEY = "58ede4d4ee786c1604f6c535"

open class VisitingCardSheet : BaseBottomSheetDialog<DialogDigitalCardShareBinding, ChannelPlanViewModel>(), RecyclerItemClickListener {

  private var shareChannelText: String? = null
  private var isWhatsApp: Boolean? = null
  private var cardPosition = 0
  private var localSessionModel: LocalSessionModel? = null
  private var session: UserSessionManager? = null

  private val pref: SharedPreferences?
    get() {
      return baseActivity.getSharedPreferences(
        PreferenceConstant.NOW_FLOATS_PREFS,
        Context.MODE_PRIVATE
      )
    }

  fun setData(localSessionModel: LocalSessionModel, shareChannelText: String) {
    this.localSessionModel = localSessionModel
    this.shareChannelText = shareChannelText
  }

  override fun getLayout(): Int {
    return R.layout.dialog_digital_card_share
  }

  override fun getViewModelClass(): Class<ChannelPlanViewModel> {
    return ChannelPlanViewModel::class.java
  }

  override fun onCreateView() {
    showSimmer(true)
    session = UserSessionManager(baseActivity)
    viewModel?.getMerchantProfile(session?.fPID)?.observeOnce(viewLifecycleOwner, {
//        if (it.isSuccess()) {
      val response = it as? MerchantProfileResponse
      val userDetail = response?.result?.getUserDetail()
      localSessionModel?.contactName = when {
        localSessionModel?.contactName.isNullOrEmpty().not() -> localSessionModel?.contactName
        userDetail?.userName.isNullOrEmpty().not() -> userDetail?.userName
        else -> localSessionModel?.fpTag
      }
      localSessionModel?.primaryNumber = when {
        localSessionModel?.primaryNumber.isNullOrEmpty()
          .not() -> localSessionModel?.primaryNumber
        userDetail?.userMobile.isNullOrEmpty().not() -> userDetail?.userMobile
        else -> ""
      }
      localSessionModel?.primaryEmail = when {
        localSessionModel?.primaryEmail.isNullOrEmpty().not() -> localSessionModel?.primaryEmail
        userDetail?.userEmail.isNullOrEmpty().not() -> userDetail?.userEmail
        else -> ""
      }

      val userProfile = ProfileProperties(
        userName = localSessionModel?.contactName, userMobile = localSessionModel?.primaryNumber,
        userEmail = localSessionModel?.primaryEmail
      )
      val cardList = ArrayList<DigitalCardData>()
      val cardData = CardData(
        localSessionModel?.businessName,
        localSessionModel?.businessImage,
        localSessionModel?.location,
        userProfile.userName?.capitalizeWords(),
        addPlus91(userProfile.userMobile),
        userProfile.userEmail,
        localSessionModel?.businessType,
        localSessionModel?.websiteUrl,
        getIconCard()
      )
      cardList.add(
        DigitalCardData(
          cardData = cardData,
          recyclerViewType = RecyclerViewItemType.VISITING_CARD_ONE_ITEM.getLayout()
        )
      )
      cardList.add(
        DigitalCardData(
          cardData = cardData,
          recyclerViewType = RecyclerViewItemType.VISITING_CARD_FOUR_ITEM.getLayout()
        )
      )
      cardList.add(
        DigitalCardData(
          cardData = cardData,
          recyclerViewType = RecyclerViewItemType.VISITING_CARD_SIX_ITEM.getLayout()
        )
      )
      cardList.add(
        DigitalCardData(
          cardData = cardData,
          recyclerViewType = RecyclerViewItemType.VISITING_CARD_EIGHT_ITEM.getLayout()
        )
      )
      cardList.add(
        DigitalCardData(
          cardData = cardData,
          recyclerViewType = RecyclerViewItemType.VISITING_CARD_TWO_ITEM.getLayout()
        )
      )
      cardList.add(
        DigitalCardData(
          cardData = cardData,
          recyclerViewType = RecyclerViewItemType.VISITING_CARD_THREE_ITEM.getLayout()
        )
      )
      cardList.add(
        DigitalCardData(
          cardData = cardData,
          recyclerViewType = RecyclerViewItemType.VISITING_CARD_FIVE_ITEM.getLayout()
        )
      )
      cardList.add(
        DigitalCardData(
          cardData = cardData,
          recyclerViewType = RecyclerViewItemType.VISITING_CARD_SEVEN_ITEM.getLayout()
        )
      )
      cardList.add(
        DigitalCardData(
          cardData = cardData,
          recyclerViewType = RecyclerViewItemType.VISITING_CARD_NINE_ITEM.getLayout()
        )
      )
      cardList.add(
        DigitalCardData(
          cardData = cardData,
          recyclerViewType = RecyclerViewItemType.VISITING_CARD_TEN_ITEM.getLayout()
        )
      )
      setAdapterCard(cardList)
//        } else {
//          showShortToast(it.message())
//          dismiss()
//        }
      showSimmer(false)
      binding?.btnMainView?.visible()
      binding?.borderView?.visible()
    })
    binding?.backBtn?.setOnClickListener {
      val popup = PopupMenu(baseActivity, it)
      popup.menuInflater.inflate(R.menu.popup_menu_card, popup.menu)
      popup.setOnMenuItemClickListener { menu ->
        if (menu.itemId == R.id.menu_edit_contact) {
          try {
            WebEngageController.trackEvent(MY_BUSINESS_CARD_MENU_CONTACT, CLICK, TO_BE_ADDED)
            val contactInfo = Intent(
              baseActivity,
              Class.forName("com.nowfloats.BusinessProfile.UI.UI.ContactInformationActivity")
            )
            startActivity(contactInfo)
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
          } catch (e: Exception) {
            e.printStackTrace()
          }
        } else if (menu.itemId == R.id.menu_digital_channel) {
          WebEngageController.trackEvent(MY_BUSINESS_CARD_MENU_DIGITAL_CHANNEL, CLICK, TO_BE_ADDED)
          baseActivity.startDigitalChannel(getBundle())
        }
//        dismiss()
        true
      }
      popup.show()
    }
    binding?.shareWhatsapp?.setOnClickListener { shareCardWhatsApp(shareChannelText, true) }
    binding?.shareOther?.setOnClickListener { shareCardWhatsApp(shareChannelText, false) }
  }

  private fun showSimmer(isSimmer: Boolean) {
    if (isSimmer) {
      binding?.viewMain?.gone()
      binding?.progressSimmer?.parentShimmerLayout?.visible()
    } else {
      binding?.viewMain?.visible()
      binding?.progressSimmer?.parentShimmerLayout?.gone()
      binding?.progressSimmer?.parentShimmerLayout?.hideShimmer()
    }
  }

  private fun setAdapterCard(cardList: ArrayList<DigitalCardData>) {
    cardList.add(0, cardList.removeAt(getLastShareCard()))
    binding?.pagerDigitalCard?.apply {
      val adapterPager3 = AppBaseRecyclerViewAdapter(baseActivity, cardList, this@VisitingCardSheet)
      offscreenPageLimit = 3
      isUserInputEnabled = true
      adapter = adapterPager3
      binding?.dotIndicatorCard?.setViewPager2(this)
      setPageTransformer { page, position ->
        OffsetPageTransformer().transformPage(page, position)
      }
      registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
          super.onPageSelected(position)
          cardPosition = position
        }
      })
    }
  }

  private fun getIconCard(): Int {
    return when (this.localSessionModel?.experienceCode?.toUpperCase(Locale.ROOT)) {
      "DOC" -> R.drawable.ic_business_card_doctor_hospital_d
      "HOS" -> R.drawable.ic_business_card_doctor_hospital_d
      "RTL" -> R.drawable.ic_business_card_retail_d
      "EDU" -> R.drawable.ic_business_card_education_d
      "HOT" -> R.drawable.ic_business_card_hotel_d
      "MFG" -> R.drawable.ic_business_card_manufacture_d
      "CAF" -> R.drawable.ic_business_card_restaurant_d
      "SVC" -> R.drawable.ic_business_card_services_d
      "SPA" -> R.drawable.ic_business_card_spa_n
      "SAL" -> R.drawable.ic_business_card_spa_n
      else -> R.drawable.ic_business_card_spa_n
    }
  }

  private fun shareCardWhatsApp(messageCard: String?, isWhatsApp: Boolean) {
    this.isWhatsApp = isWhatsApp
    if (ActivityCompat.checkSelfPermission(
        baseActivity,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      ) == PackageManager.PERMISSION_DENIED
    ) {
      requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
      return
    }
    val bitmap = binding?.pagerDigitalCard?.getChildAt(0)?.let { viewToBitmap(it) }
    try {
      val cropBitmap = bitmap?.let { Bitmap.createBitmap(it, 40, 0, bitmap.width - 80, bitmap.height) }
      val path = MediaStore.Images.Media.insertImage(baseActivity.contentResolver, cropBitmap, "boost_${Date().time}", null)
      val imageUri: Uri = Uri.parse(path)
      val waIntent = Intent(Intent.ACTION_SEND)
      waIntent.type = "image/*"
      if (isWhatsApp) waIntent.setPackage(getString(R.string.whatsapp_package))
      waIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
      waIntent.putExtra(Intent.EXTRA_TEXT, messageCard ?: "")
      baseActivity.startActivity(
        Intent.createChooser(waIntent, resources.getString(R.string.share_your_business_card))
      )
      dismiss()
      savePositionCard(cardPosition)
      WebEngageController.trackEvent(
        VISITING_CARD_SHARE,
        VISITING_CARD,
        localSessionModel?.fpTag ?: ""
      )
      onBusinessCardAddedOrUpdated(true)
    } catch (e: Exception) {
      showLongToast(getString(R.string.error_sharing_visiting_card_please_try_again))
      dismiss()
    }
  }

  private fun onBusinessCardAddedOrUpdated(isAdded: Boolean) {
    val instance = FirestoreManager
    instance.getDrScoreData()?.metricdetail?.boolean_share_business_card = isAdded
    instance.updateDocument()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

  }

  private fun getBundle(): Bundle {
    val bundle = Bundle()
    pref?.edit()?.putString(PreferenceConstant.AUTHORIZATION, WA_KEY)?.apply()
    bundle.putString(PreferenceConstant.KEY_FP_ID, localSessionModel?.floatingPoint)
    bundle.putString(PreferenceConstant.GET_FP_DETAILS_TAG, localSessionModel?.fpTag)
    bundle.putString(PreferenceConstant.GET_FP_EXPERIENCE_CODE, localSessionModel?.experienceCode)
    bundle.putBoolean(PreferenceConstant.IS_UPDATE, true)
    bundle.putString(PreferenceConstant.BUSINESS_NAME, localSessionModel?.businessName)
    bundle.putString(PreferenceConstant.CONTACT_NAME, localSessionModel?.contactName)
    bundle.putString(PreferenceConstant.BUSINESS_IMAGE, localSessionModel?.businessImage)
    bundle.putString(PreferenceConstant.BUSINESS_TYPE, localSessionModel?.businessType)
    bundle.putString(PreferenceConstant.LOCATION, localSessionModel?.location)
    bundle.putString(PreferenceConstant.WEBSITE_URL, localSessionModel?.websiteUrl)
    bundle.putString(PreferenceConstant.PRIMARY_NUMBER, localSessionModel?.primaryNumber)
    bundle.putString(PreferenceConstant.PRIMARY_EMAIL, localSessionModel?.primaryEmail)
    return bundle
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
  ) {
    when (requestCode) {
      100 -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          shareCardWhatsApp(shareChannelText, this.isWhatsApp ?: false)
        } else showShortToast(getString(R.string.permission_denied_to_your_external_storage))
        return
      }
    }
  }
}

fun AppCompatActivity.startDigitalChannel(bundle: Bundle) {
  try {
    WebEngageController.trackEvent(DIGITAL_CHANNEL_PAGE_CLICK, START_VIEW, NO_EVENT_VALUE)
    startFragmentChannelActivity(FragmentType.MY_DIGITAL_CHANNEL, bundle)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun addPlus91(userMobile: String?): String? {
  return when {
    userMobile?.contains("+91-") == true -> userMobile.replace("+91-", "+91")
    userMobile?.contains("+91") == false -> "+91$userMobile"
    else -> userMobile
  }
}

data class LocalSessionModel(
  var floatingPoint: String? = null,
  var contactName: String? = null,
  var businessName: String? = null,
  var businessImage: String? = null,
  var location: String? = null,
  var websiteUrl: String? = null,
  var businessType: String? = null,
  var primaryNumber: String? = null,
  var primaryEmail: String? = null,
  var fpTag: String? = null,
  var experienceCode: String? = null,
)

