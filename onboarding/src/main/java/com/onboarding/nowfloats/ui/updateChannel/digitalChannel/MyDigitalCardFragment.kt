package com.onboarding.nowfloats.ui.updateChannel.digitalChannel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media.insertImage
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseFragment
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.FragmentDigitalCardBinding
import com.onboarding.nowfloats.extensions.capitalizeWords
import com.onboarding.nowfloats.model.digitalCard.CardData
import com.onboarding.nowfloats.model.digitalCard.DigitalCardData
import com.onboarding.nowfloats.model.profile.MerchantProfileResponse
import com.onboarding.nowfloats.model.profile.ProfileProperties
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.utils.viewToBitmap
import com.onboarding.nowfloats.viewmodel.channel.ChannelPlanViewModel


class MyDigitalCardFragment : AppBaseFragment<FragmentDigitalCardBinding, ChannelPlanViewModel>(), RecyclerItemClickListener {

  private var cardPosition = 0

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): MyDigitalCardFragment {
      val fragment = MyDigitalCardFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_digital_card
  }

  override fun onCreateView() {
    super.onCreateView()
    val floatingPoint = arguments?.getString(PreferenceConstant.KEY_FP_ID)
    var contactName: String? = arguments?.getString(PreferenceConstant.CONTACT_NAME)?.capitalizeWords()
    val businessName: String? = arguments?.getString(PreferenceConstant.BUSINESS_NAME)?.capitalizeWords()
    val businessImage: String? = arguments?.getString(PreferenceConstant.BUSINESS_IMAGE)
    val location = arguments?.getString(PreferenceConstant.LOCATION)
    val websiteUrl = arguments?.getString(PreferenceConstant.WEBSITE_URL)
    val businessType = arguments?.getString(PreferenceConstant.BUSINESS_TYPE)
    //Optional
    var primaryNumber: String? = arguments?.getString(PreferenceConstant.PRIMARY_NUMBER)
    var primaryEmail: String? = arguments?.getString(PreferenceConstant.PRIMARY_EMAIL)
    val fpTag = arguments?.getString(PreferenceConstant.GET_FP_DETAILS_TAG)
    showProgress()
    viewModel?.getMerchantProfile(floatingPoint)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        val response = it as? MerchantProfileResponse
        val userDetail = response?.result?.getUserDetail()
        contactName = when {
          contactName.isNullOrEmpty().not() -> contactName
          userDetail?.userName.isNullOrEmpty().not() -> userDetail?.userName
          else -> fpTag
        }
        primaryNumber = if (primaryNumber.isNullOrEmpty().not()) primaryNumber else if (userDetail?.userMobile.isNullOrEmpty().not()) userDetail?.userMobile else ""
        primaryEmail = if (primaryEmail.isNullOrEmpty().not()) primaryEmail else if (userDetail?.userEmail.isNullOrEmpty().not()) userDetail?.userEmail else ""

        val userProfile = ProfileProperties(userName = contactName, userMobile = primaryNumber, userEmail = primaryEmail)
        val cardList = ArrayList<DigitalCardData>()
        val cardData = CardData(businessName, businessImage, location, userProfile.userName?.capitalizeWords(),
            userProfile.userMobile, userProfile.userEmail, businessType, websiteUrl)

        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_ONE_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_TWO_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_THREE_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_FOUR_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_FIVE_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_SIX_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_SEVEN_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_EIGHT_ITEM.getLayout()))
        cardList.add(DigitalCardData(cardData = cardData, recyclerViewType = RecyclerViewItemType.VISITING_CARD_NINE_ITEM.getLayout()))

        setAdapterCard(cardList)
      } else showShortToast(it.message())
      hideProgress()
    })
    binding?.shareWhatsapp?.setOnClickListener { shareCardWhatsApp("Business Card", true) }
    binding?.shareOther?.setOnClickListener { shareCardWhatsApp("Business Card", false) }
  }

  private fun shareCardWhatsApp(messageN: String?, isWhatsApp: Boolean) {
    if (ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(baseActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
      return
    }
    showProgress()
    val bitmap = binding?.pagerDigitalCard?.getChildAt(0)?.let { viewToBitmap(it) }
    val pm = baseActivity.packageManager
    try {
      val cropBitmap = bitmap?.let { Bitmap.createBitmap(it, 33, 0, bitmap.width - 66, bitmap.height) }
      val path = insertImage(baseActivity.contentResolver, cropBitmap, "boost_360", null)
      val imageUri: Uri = Uri.parse(path)
      val waIntent = Intent(Intent.ACTION_SEND)
      waIntent.type = "image/*"
      if (isWhatsApp) waIntent.setPackage("com.whatsapp")
      waIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
      waIntent.putExtra(Intent.EXTRA_TEXT, messageN ?: "")
      baseActivity.startActivity(Intent.createChooser(waIntent, "Share your business card..."))
      hideProgress()
    } catch (e: Exception) {
      showLongToast("App not Installed")
      hideProgress()
    }
  }

  private fun setAdapterCard(cardList: ArrayList<DigitalCardData>) {
    binding?.pagerDigitalCard?.apply {
      val adapterPager3 = AppBaseRecyclerViewAdapter(baseActivity, cardList, this@MyDigitalCardFragment)
      offscreenPageLimit = 3
      isUserInputEnabled = true
      adapter = adapterPager3
      binding?.dotIndicatorCard?.setViewPager2(this)
      setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
      registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
          super.onPageSelected(position)
          cardPosition = position
        }
      })
    }
  }

  override fun getViewModelClass(): Class<ChannelPlanViewModel> {
    return ChannelPlanViewModel::class.java
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

  }

  override fun showProgress(title: String?, cancelable: Boolean?) {
    binding?.progress?.visible()
  }

  override fun hideProgress() {
    binding?.progress?.gone()
  }
}