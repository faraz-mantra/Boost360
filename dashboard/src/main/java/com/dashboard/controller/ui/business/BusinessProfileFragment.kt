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
import androidx.appcompat.app.AppCompatActivity
import com.appservice.model.FileModel
import com.appservice.model.serviceTiming.ServiceTiming
import com.appservice.model.servicev1.ServiceModelV1
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.getBitmap
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.IntentConstant
import com.dashboard.controller.ui.business.bottomsheet.BusinessCategoryBottomSheet
import com.dashboard.controller.ui.business.bottomsheet.BusinessDescriptionBottomSheet
import com.dashboard.controller.ui.business.bottomsheet.BusinessFeaturedBottomSheet
import com.dashboard.controller.ui.business.bottomsheet.BusinessNameBottomSheet
import com.dashboard.controller.ui.business.model.BusinessProfileModel
import com.dashboard.databinding.FragmentBusinessProfileBinding
import com.dashboard.extension.getBitmap
import com.dashboard.utils.getBitmap
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.imagepicker.ImagePicker
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_ADDRESS
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_DESCRIPTION
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_IMAGE_URI
import com.framework.pref.UserSessionManager
import com.onboarding.nowfloats.extensions.getBitmap
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File


class BusinessProfileFragment : AppBaseFragment<FragmentBusinessProfileBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_business_profile
    }

    private var businessImage: File? = null
    var targetMap: Target? = null
    var businessProfileModel = BusinessProfileModel();

    var sessionManager: UserSessionManager? = null

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): BusinessProfileFragment {
            val fragment = BusinessProfileFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(
            binding?.ctvWhatsThis,
            binding?.ctvBusinessName,
            binding?.ctvBusinessCategory,
            binding?.clBusinessDesc,
            binding?.imageAddBtn,
            binding?.btnChangeImage
        )
        sessionManager = UserSessionManager(requireContext())
        binding?.ctvBusinessName?.text = sessionManager?.getFPDetails(GET_FP_DETAILS_BUSINESS_NAME)
        binding?.ctvBusinessNameCount?.text = "${sessionManager?.fPName?.length}/40"
        binding?.ctvWebsite?.text = "${sessionManager?.rootAliasURI}"
        binding?.ctvBusinessDesc?.text = sessionManager?.getFPDetails(GET_FP_DETAILS_DESCRIPTION)
        binding?.ctvBusinessAddress?.text = sessionManager?.getFPDetails(GET_FP_DETAILS_ADDRESS)
        binding?.ctvBusinessContacts?.text = sessionManager?.fPPrimaryContactNumber
        setImage(sessionManager?.getFPDetails(GET_FP_DETAILS_IMAGE_URI)!!)
        setDataToModel()
        setImageGrayScale()

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
            binding?.imageAddBtn,binding?.btnChangeImage-> openImagePicker()

        }
    }
    private fun openImagePicker() {
        val filterSheet = ImagePickerBottomSheet()
        filterSheet.isHidePdf(true)
        filterSheet.onClicked = { openImagePicker(it) }
        filterSheet.show(this@BusinessProfileFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
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
        bundle.putSerializable(IntentConstant.BUSINESS_DETAILS.name,businessProfileModel)
        businessDescDialog.arguments = bundle
        businessDescDialog.onClicked = {
            binding?.ctvBusinessDesc?.text =it.businessDesc
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
        bundle.putSerializable(IntentConstant.BUSINESS_DETAILS.name,businessProfileModel)
        businessNameBottomSheet.arguments = bundle
        businessNameBottomSheet.onClicked = {
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
                targetMap = null
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        }
        if (imageUri.isEmpty().not()) {
            targetMap = target
            Picasso.get().load(imageUri ?: "").into(target)
        }else{
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
            }
        }
    }
    fun File.getBitmap(): Bitmap? {
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(this.path), 800, 800)
    }
}

