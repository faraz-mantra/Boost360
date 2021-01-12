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
import com.framework.views.dotsindicator.OffsetPageTransformer
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
import com.onboarding.nowfloats.ui.updateChannel.startFragmentActivity
import com.onboarding.nowfloats.utils.WebEngageController
import com.onboarding.nowfloats.utils.viewToBitmap
import com.onboarding.nowfloats.viewmodel.channel.ChannelPlanViewModel
import java.util.*
import kotlin.collections.ArrayList


const val WA_KEY = "58ede4d4ee786c1604f6c535"

open class MyDigitalCardShareDialog : BaseBottomSheetDialog<DialogDigitalCardShareBinding, ChannelPlanViewModel>(), RecyclerItemClickListener {

  private var messageCard: String? = null
  private var isWhatsApp: Boolean? = null
  private var cardPosition = 0
  private var localSessionModel: LocalSessionModel? = null

  private val pref: SharedPreferences?
    get() {
      return baseActivity.getSharedPreferences(PreferenceConstant.NOW_FLOATS_PREFS, Context.MODE_PRIVATE)
    }

  fun setData(localSessionModel: LocalSessionModel) {
    this.localSessionModel = localSessionModel
  }

  override fun getLayout(): Int {
    return R.layout.dialog_digital_card_share
  }

  override fun getViewModelClass(): Class<ChannelPlanViewModel> {
    return ChannelPlanViewModel::class.java
  }

  override fun onCreateView() {
    binding?.progress?.visible()
    viewModel?.getMerchantProfile(localSessionModel?.floatingPoint)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        val response = it as? MerchantProfileResponse
        val userDetail = response?.result?.getUserDetail()
        localSessionModel?.contactName = when {
          localSessionModel?.contactName.isNullOrEmpty().not() -> localSessionModel?.contactName
          userDetail?.userName.isNullOrEmpty().not() -> userDetail?.userName
          else -> localSessionModel?.fpTag
        }
        localSessionModel?.primaryNumber = if (localSessionModel?.primaryNumber.isNullOrEmpty().not()) localSessionModel?.primaryNumber else if (userDetail?.userMobile.isNullOrEmpty().not()) userDetail?.userMobile else ""
        localSessionModel?.primaryEmail = if (localSessionModel?.primaryEmail.isNullOrEmpty().not()) localSessionModel?.primaryEmail else if (userDetail?.userEmail.isNullOrEmpty().not()) userDetail?.userEmail else ""

        val userProfile = ProfileProperties(userName = localSessionModel?.contactName, userMobile = localSessionModel?.primaryNumber, userEmail = localSessionModel?.primaryEmail)
        val cardList = ArrayList<DigitalCardData>()
        val cardData = CardData(localSessionModel?.businessName, localSessionModel?.businessImage, localSessionModel?.location, userProfile.userName?.capitalizeWords(),
            addPlus91(userProfile.userMobile), userProfile.userEmail, localSessionModel?.businessType, localSessionModel?.websiteUrl)

        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_ONE_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_TWO_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_THREE_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_FOUR_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_FIVE_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_SIX_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_SEVEN_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_EIGHT_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_NINE_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_TEN_ITEM.getLayout()))

        setAdapterCard(cardList)
      } else {
        showShortToast(it.message())
        dismiss()
      }
      binding?.progress?.gone()
      binding?.btnMainView?.visible()
      binding?.borderView?.visible()
    })
    binding?.backBtn?.setOnClickListener {
      val popup = PopupMenu(baseActivity, it)
      popup.menuInflater.inflate(R.menu.popup_menu_card, popup.menu)
      popup.setOnMenuItemClickListener { menu ->
        if (menu.itemId == R.id.menu_edit_contact) {
          try {
            val contactInfo = Intent(baseActivity, Class.forName("com.nowfloats.BusinessProfile.UI.UI.ContactInformationActivity"))
            startActivity(contactInfo)
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
          } catch (e: Exception) {
            e.printStackTrace()
          }
        } else if (menu.itemId == R.id.menu_digital_channel) {
          baseActivity.startDigitalChannel(getBundle())
        }
        dismiss()
        true
      }
      popup.show()
    }
    binding?.shareWhatsapp?.setOnClickListener { shareCardWhatsApp("Business Card", true) }
    binding?.shareOther?.setOnClickListener { shareCardWhatsApp("Business Card", false) }
  }

  private fun setAdapterCard(cardList: ArrayList<DigitalCardData>) {
    cardList.add(0, cardList.removeAt(getLastShareCard()))
    binding?.pagerDigitalCard?.apply {
      val adapterPager3 = AppBaseRecyclerViewAdapter(baseActivity, cardList, this@MyDigitalCardShareDialog)
      offscreenPageLimit = 3
      isUserInputEnabled = true
      adapter = adapterPager3
      binding?.dotIndicatorCard?.setViewPager2(this)
//      post { setCurrentItem(getLastShareCard(), false) }
      setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
      registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
          super.onPageSelected(position)
          cardPosition = position
        }
      })
    }
  }

  private fun shareCardWhatsApp(messageCard: String?, isWhatsApp: Boolean) {
    this.messageCard = messageCard
    this.isWhatsApp = isWhatsApp
    if (ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
      return
    }
    binding?.progress?.visible()
    val bitmap = binding?.pagerDigitalCard?.getChildAt(0)?.let { viewToBitmap(it) }
    try {
      val cropBitmap = bitmap?.let { Bitmap.createBitmap(it, 33, 0, bitmap.width - 66, bitmap.height) }
      val path = MediaStore.Images.Media.insertImage(baseActivity.contentResolver, cropBitmap, "boost_${Date().time}", null)
      val imageUri: Uri = Uri.parse(path)
      val waIntent = Intent(Intent.ACTION_SEND)
      waIntent.type = "image/*"
      if (isWhatsApp) waIntent.setPackage("com.whatsapp")
      waIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
      waIntent.putExtra(Intent.EXTRA_TEXT, messageCard ?: "")
      baseActivity.startActivity(Intent.createChooser(waIntent, "Share your business card..."))
      binding?.progress?.gone()
      dismiss()
      savePositionCard(cardPosition)
    } catch (e: Exception) {
      showLongToast("Error sharing visiting card, please try again.")
      binding?.progress?.gone()
      dismiss()
    }
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

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when (requestCode) {
      100 -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          shareCardWhatsApp(this.messageCard, this.isWhatsApp ?: false)
        } else showShortToast("Permission denied to read your External storage")
        return
      }
    }
  }
}

fun AppCompatActivity.startDigitalChannel(bundle: Bundle) {
  try {
    WebEngageController.trackEvent("Digital Channel Page", "startview", "");
    startFragmentActivity(FragmentType.MY_DIGITAL_CHANNEL, bundle)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun addPlus91(userMobile: String?): String? {
  if ((userMobile?.contains("+91") == true || userMobile?.contains("+91-") == true).not()) {
    return "+91-$userMobile"
  }
  return userMobile
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

