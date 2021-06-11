package com.dashboard.controller.ui.business

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.IntentConstant
import com.dashboard.controller.ui.business.bottomsheet.BusinessCategoryBottomSheet
import com.dashboard.controller.ui.business.bottomsheet.BusinessDescriptionBottomSheet
import com.dashboard.controller.ui.business.bottomsheet.BusinessFeaturedBottomSheet
import com.dashboard.controller.ui.business.bottomsheet.BusinessNameBottomSheet
import com.dashboard.controller.ui.business.model.BusinessProfileModel
import com.dashboard.controller.ui.business.model.BusinessProfileUpdateRequest
import com.dashboard.controller.ui.business.model.UpdatesItem
import com.dashboard.databinding.FragmentBusinessProfileBinding
import com.dashboard.viewmodel.BusinessProfileViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.imagepicker.ImagePicker
import com.framework.models.firestore.FirestoreManager
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_ADDRESS
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_DESCRIPTION
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_IMAGE_URI
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId2
import com.framework.views.customViews.CustomImageView
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse.Companion.CONNECTED_CHANNELS
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse.Companion.getConnectedChannel
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse.Companion.visibleChannels
import com.onboarding.nowfloats.rest.response.channel.ChannelsAccessTokenResponse
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.util.*


class BusinessProfileFragment :
    AppBaseFragment<FragmentBusinessProfileBinding, BusinessProfileViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_business_profile
    }

    private var businessImage: File? = null
    var targetMap: Target? = null
    var businessProfileModel = BusinessProfileModel();
    var businessProfileUpdateRequest: BusinessProfileUpdateRequest? = null


    var sessionManager: UserSessionManager? = null

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): BusinessProfileFragment {
            val fragment = BusinessProfileFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getViewModelClass(): Class<BusinessProfileViewModel> {
        return BusinessProfileViewModel::class.java
    }

    private fun uploadBusinessLogo(businessLogoImage: File) {
        showProgress(getString(R.string.uploading_image))
        val uuid: UUID = UUID.randomUUID()
        var s_uuid = uuid.toString()
        s_uuid = s_uuid.replace("-", "")
        viewModel?.putUploadBusinessLogo(
            clientId2,
            fpId = FirestoreManager.fpId,
            reqType = "sequential",
            reqId = s_uuid,
            totalChunks = "1",
            currentChunkNumber = "1",
            file = RequestBody.create(
                "image/png".toMediaTypeOrNull(),
                businessLogoImage.readBytes()
            )
        )?.observeOnce(viewLifecycleOwner, {
            if (it.isSuccess()) {
                sessionManager?.storeFPDetails(
                    GET_FP_DETAILS_IMAGE_URI,
                    it.parseStringResponse()?.replace("\\", "")?.replace("\"", "")
                )
                showSnackBarPositive(requireActivity(), getString(R.string.business_image_uploaded))
            } else showSnackBarNegative(requireActivity(), it.message)
            hideProgress()
        })
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(
            binding?.ctvWhatsThis,
            binding?.ctvBusinessName,
            binding?.ctvBusinessCategory,
            binding?.clBusinessDesc,
            binding?.imageAddBtn,
            binding?.btnChangeImage,
            binding?.btnSavePublish
        )
        binding?.btnSavePublish?.isEnabled=false
        sessionManager = UserSessionManager(requireContext())
        binding?.ctvBusinessName?.text = sessionManager?.getFPDetails(GET_FP_DETAILS_BUSINESS_NAME)
        binding?.ctvBusinessNameCount?.text = "${sessionManager?.fPName?.length}/40"
        binding?.ctvWebsite?.text = "${sessionManager?.rootAliasURI}"
        binding?.ctvBusinessDesc?.text = sessionManager?.getFPDetails(GET_FP_DETAILS_DESCRIPTION)
        binding?.ctvBusinessAddress?.text = sessionManager?.getFPDetails(GET_FP_DETAILS_ADDRESS)
       if ( sessionManager?.getFPDetails(GET_FP_DETAILS_ADDRESS).isNullOrEmpty()){
           binding?.containerBusinessAddress?.gone()
       }else{
           binding?.containerBusinessAddress?.visible()
       }
        binding?.ctvBusinessContacts?.text = """• +91 ${sessionManager?.fPPrimaryContactNumber} (VMN) 
            |• +91 ${sessionManager?.userPrimaryMobile} 
            |• ${sessionManager?.userProfileEmail?:sessionManager?.fPEmail} 
            |• ${sessionManager?.rootAliasURI}""".trimMargin()
        setImage(sessionManager?.getFPDetails(GET_FP_DETAILS_IMAGE_URI)!!)
        setDataToModel()
        setImageGrayScale()
        setConnectedChannels()

    }

    private fun setConnectedChannels() {
        visibleChannels(binding?.containerChannels!!)
        if (getConnectedChannel().isEmpty()) binding?.ctvConnected?.gone() else binding?.ctvConnected?.visible()
        grayScaleDisabledChannels(binding?.containerChannelsDisabled!!)

    }

    private fun grayScaleDisabledChannels(containerChannels: LinearLayout) {
        for (it in containerChannels.children) {
            val customImageView = it as? CustomImageView
            val tag = customImageView?.tag
            if (getConnectedChannel().contains(tag)) {
                customImageView?.gone()
            } else {
                customImageView?.visible()
            }
        }
        if (containerChannels.childCount==0)binding?.ctvNotConnected?.gone()else binding?.ctvNotConnected?.visible()
    }
    private fun setDataToModel() {
        val businessDesc = binding?.ctvBusinessDesc?.text.toString()
        val businessName = binding?.ctvBusinessName?.text.toString()
        businessProfileModel.businessDesc = businessDesc
        businessProfileModel.businessName = businessName

    }

    private fun setImageGrayScale() {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(matrix)
        binding?.civFbPageDisabled?.colorFilter = filter
        binding?.civWhatsappBusinessDisabled?.colorFilter = filter
        binding?.civTwitterDisabled?.colorFilter = filter
        binding?.civGooogleBusinessDisabled?.colorFilter = filter
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.ctvWhatsThis -> {
                openDialogForInformation()
            }
            binding?.ctvBusinessName -> {
                openBusinessNameDialog()
            }
            binding?.ctvBusinessCategory -> {
                openBusinessCategoryBottomSheet()
            }
            binding?.clBusinessDesc -> {
                showBusinessDescDialog()
            }
            binding?.imageAddBtn, binding?.btnChangeImage -> openImagePicker()
            binding?.imageAddBtn, binding?.btnSavePublish -> if (isValid()) {
                updateFpDetails()
            }

        }
    }

    fun isValid(): Boolean {
        if (businessProfileModel.businessName.isNullOrEmpty() || businessProfileModel.businessName?.length ?: 0 <= 2) {
            showLongToast(getString(R.string.please_enter_valid_business_name))
            return false
        } else if (businessProfileModel.businessDesc.isNullOrEmpty()) {
            showLongToast(getString(R.string.please_enter_valid_business_description))
            return false
        }
        return true
    }

    private fun updateFpDetails() {
        showProgress()
        val updateItemList = arrayListOf<UpdatesItem>()
        if (sessionManager?.getFPDetails(GET_FP_DETAILS_BUSINESS_NAME)!=binding?.ctvBusinessName?.text){
            updateItemList.add(UpdatesItem(key = "NAME",value = businessProfileModel.businessName))
        }
        if (sessionManager?.getFPDetails(GET_FP_DETAILS_DESCRIPTION)!=binding?.ctvBusinessDesc?.text){
            updateItemList.add(UpdatesItem(key = "DESCRIPTION",value = businessProfileModel.businessDesc))
        }
        if (businessProfileUpdateRequest == null) businessProfileUpdateRequest =
            BusinessProfileUpdateRequest(
                sessionManager?.fpTag,
                clientId2, updateItemList
            )
        viewModel?.updateBusinessProfile(businessProfileUpdateRequest!!)
            ?.observeOnce(viewLifecycleOwner, {
                hideProgress()
                when (it.isSuccess()) {
                    true -> {
                        val response = it?.parseStringResponse()
                        when (response?.contains("NAME")) {true -> { showSnackBarPositive(requireActivity(),getString(R.string.business_name_published_successfully)) } }
                        when (response?.contains("DESCRIPTION")) {true -> { showSnackBarPositive(requireActivity(),getString(R.string.business_description_published_successfully)) } }
                        sessionManager?.storeFPDetails(
                            GET_FP_DETAILS_DESCRIPTION,
                            businessProfileModel.businessDesc
                        )
                        sessionManager?.storeFPDetails(
                            GET_FP_DETAILS_BUSINESS_NAME,
                            businessProfileModel.businessName
                        )
                    }
                    else -> {
                    }
                }

            })
    }

    private fun openImagePicker() {
        val filterSheet = ImagePickerBottomSheet()
        filterSheet.isHidePdf(true)
        filterSheet.onClicked = { openImagePicker(it) }
        filterSheet.show(
            this@BusinessProfileFragment.parentFragmentManager,
            ImagePickerBottomSheet::class.java.name
        )
    }

    private fun openImagePicker(it: ClickType) {
        val type = if (it == ClickType.CAMERA) ImagePicker.Mode.CAMERA else ImagePicker.Mode.GALLERY
        ImagePicker.Builder(baseActivity)
            .mode(type)
            .compressLevel(ImagePicker.ComperesLevel.SOFT).directory(ImagePicker.Directory.DEFAULT)
            .extension(ImagePicker.Extension.PNG).allowMultipleImages(false)
            .scale(800, 800)
            .enableDebuggingMode(true).build()
    }

    private fun showBusinessDescDialog() {
        val businessDescDialog = BusinessDescriptionBottomSheet()
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.BUSINESS_DETAILS.name, businessProfileModel)
        businessDescDialog.arguments = bundle
        businessDescDialog.onClicked = {
            binding?.btnSavePublish?.isEnabled=true
            binding?.ctvBusinessDesc?.text = it.businessDesc
        }
        businessDescDialog.show(
            parentFragmentManager,
            BusinessDescriptionBottomSheet::javaClass.name
        )
    }

    private fun openBusinessCategoryBottomSheet() {
        val businessCategoryBottomSheet = BusinessCategoryBottomSheet()
        businessCategoryBottomSheet.show(
            parentFragmentManager,
            BusinessCategoryBottomSheet::javaClass.name
        )
    }

    private fun openBusinessNameDialog() {
        val businessNameBottomSheet = BusinessNameBottomSheet()
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.BUSINESS_DETAILS.name, businessProfileModel)
        businessNameBottomSheet.arguments = bundle
        businessNameBottomSheet.onClicked = {
            binding?.btnSavePublish?.isEnabled=true
            binding?.ctvBusinessName?.text = it
            binding?.ctvBusinessNameCount?.text = "${it.length}/40"
        }
        businessNameBottomSheet.show(parentFragmentManager, BusinessNameBottomSheet::javaClass.name)
    }

    private fun openDialogForInformation() {
        val businessFeaturedBottomSheet = BusinessFeaturedBottomSheet()
        businessFeaturedBottomSheet.show(
            parentFragmentManager,
            BusinessFeaturedBottomSheet::javaClass.name
        )
    }

    private fun setImage(imageUri: String) {
        val target: Target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                targetMap = null
                try {
                    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    bindImage(mutableBitmap)
                } catch (e: OutOfMemoryError) {
                } catch (e: Exception) {
                }
            }

            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
                binding?.imageAddBtn?.visible()
                binding?.businessImage?.gone()
                businessImage = null
                binding?.ctvWhatsThis?.compoundDrawables?.get(0)?.setTint(getColor(R.color.blue_4A90E2))
                binding?.ctvWhatsThis?.setTextColor(ColorStateList.valueOf(getColor(R.color.blue_4A90E2)))
                targetMap = null
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        }
        if (imageUri.isEmpty().not()) {
            targetMap = target
            Picasso.get().load(imageUri ?: "").into(target)
        } else {
            binding?.imageAddBtn?.visible()
            binding?.businessImage?.gone()
            businessImage = null
            binding?.ctvWhatsThis?.compoundDrawables?.get(0)?.setTint(getColor(R.color.blue_4A90E2))
            binding?.ctvWhatsThis?.setTextColor(ColorStateList.valueOf(getColor(R.color.blue_4A90E2)))
        }
    }

    private fun bindImage(mutableBitmap: Bitmap?) {
        binding?.businessImage?.setImageBitmap(mutableBitmap)
        binding?.imageAddBtn?.gone()
        binding?.ctvWhatsThis?.compoundDrawables?.get(0)?.setTint(getColor(R.color.white))
        binding?.ctvWhatsThis?.setTextColor(ColorStateList.valueOf(getColor(R.color.white)))
        binding?.businessImage?.visible()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as List<String>
            if (mPaths.isNotEmpty()) {
                this.businessImage = File(mPaths[0])
                bindImage(businessImage?.getBitmap())
                uploadBusinessLogo(businessImage!!)
            }
        }
    }

    fun File.getBitmap(): Bitmap? {
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(this.path), 800, 800)
    }
}

