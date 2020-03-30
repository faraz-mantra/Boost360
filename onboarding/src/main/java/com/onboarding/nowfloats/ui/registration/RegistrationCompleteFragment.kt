package com.onboarding.nowfloats.ui.registration

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.framework.CustomTypefaceSpan
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.FragmentRegistrationCompleteBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.extensions.getBitmap
import com.onboarding.nowfloats.extensions.setGridRecyclerViewAdapter
import com.onboarding.nowfloats.managers.NavigatorManager
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.navigator.ScreenModel
import com.onboarding.nowfloats.model.navigator.ScreenModel.*
import com.onboarding.nowfloats.model.navigator.ScreenModel.Screen.*
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import java.io.File

class RegistrationCompleteFragment : BaseRegistrationFragment<FragmentRegistrationCompleteBinding>() {

    private var selectedChannelsAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
    var isProfileImage: Boolean? = null

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): RegistrationCompleteFragment {
            val fragment = RegistrationCompleteFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    @ExperimentalStdlibApi
    override fun onCreateView() {
        super.onCreateView()
        NavigatorManager.pushToStackAndSaveRequest(ScreenModel(REGISTRATION_COMPLETE, getToolbarTitle()), requestFloatsModel)
        setSetSelectedChannels(channels)
        setOnClickListener(binding?.menuView)
        binding?.congratsText?.text = resources.getString(R.string.congratulations)
        requestFloatsModel?.contactInfo?.businessName?.let {
            binding?.businessName?.text = it
            binding?.businessNameInitial?.text = it.firstOrNull()?.toUpperCase()?.toString()
        }
        setBusinessName()
        setBusinessImage()

        binding?.imageView?.post {
            binding?.imageView?.fadeIn()?.andThen(binding?.congratsText?.fadeIn(100L))
                    ?.andThen(binding?.businessText?.fadeIn(100L))?.andThen(binding?.tagImage?.fadeIn(200L))
                    ?.andThen(binding?.cardView?.fadeIn(200L))?.andThen(binding?.businessName?.fadeIn(100L))
                    ?.andThen(binding?.settingUpChannels?.fadeIn(100L))?.andThen(binding?.selectedChannels?.fadeIn(100L))
                    ?.andThen(binding?.desc?.fadeIn(50L))?.andThen(binding?.done?.fadeIn(50L))
                    ?.andThen(binding?.skip?.fadeIn(0L))?.andThen { initLottieAnimation() }?.subscribe()
        }
        setOnClickListener(binding?.profileView, binding?.businessNameInitial)
    }

    private fun setBusinessImage() {
        val imageUrl = requestFloatsModel?.channelAccessTokens?.map { it.profilePicture }
                ?.firstOrNull { it?.isNotEmpty() == true } ?: return

        binding?.businessNameInitial?.gone()
        binding?.businessImage?.visible()

        baseActivity.glideLoad(binding?.businessImage, imageUrl)
    }

    private fun initLottieAnimation() {
        binding?.lottieAnimation?.setAnimation(R.raw.lottie_anim_congratulation)
        binding?.lottieAnimation?.repeatCount = 0
        startCheckAnimation()
    }

    private fun startCheckAnimation() {
        binding?.lottieAnimation?.let {
            if (it.isAnimating) it.pauseAnimation()
            else it.playAnimation()
        }
    }

    private fun setBusinessName() {
        var title = requestFloatsModel?.categoryDataModel?.category_Name ?: return
        title = title.replace('\n', ' ')
        val regular = getFont(R.font.regular) ?: return
        val semiBold = getFont(R.font.semi_bold) ?: return

        val spannableStringBuilder = SpannableStringBuilder(resources.getString(R.string.your) + " $title " + resources.getString(R.string.business_setup_boost))
        spannableStringBuilder.setSpan(CustomTypefaceSpan("", regular), 0, 4, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableStringBuilder.setSpan(CustomTypefaceSpan("", semiBold), 5, 5 + title.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableStringBuilder.setSpan(CustomTypefaceSpan("", regular), 5 + title.length + 1, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)

        binding?.businessText?.text = spannableStringBuilder
    }

    private fun setSetSelectedChannels(list: ArrayList<ChannelModel>) {
        val itemSize = ConversionUtils.dp2px(48f)
        var spanCount = (ScreenUtils.instance.getWidth(baseActivity) - ConversionUtils.dp2px(96f)) / itemSize

        if (spanCount > list.size) {
            spanCount = list.size
        }

        val selectedItems = list.map { it.recyclerViewType = RecyclerViewItemType.SMALL_SELECTED_CHANNEL_ITEM.getLayout(); it }
        selectedChannelsAdapter = binding?.selectedChannels?.setGridRecyclerViewAdapter(baseActivity, spanCount, selectedItems)
        selectedChannelsAdapter?.notifyDataSetChanged()

        if (selectedItems.isEmpty()) {
            binding?.settingUpChannels?.gone()
        } else {
            binding?.settingUpChannels?.visible()
            val text = StringBuilder(resources.getString(R.string.setting_total) + " ${selectedItems.count()} " + resources.getString(R.string.channel))
            if (selectedItems.size > 1) {
                text.append(resources.getString(R.string.more_than_one_add_s))
            }
            text.append("…")
            binding?.settingUpChannels?.text = text
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.menuView -> showMenuLogout(v)
            binding?.profileView -> openImagePicker(true)
            binding?.businessNameInitial -> openImagePicker(false)
        }
    }

    private fun openImagePicker(isProfileImage: Boolean) {
        this.isProfileImage = isProfileImage
        ImagePicker.Builder(baseActivity)
                .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                .directory(ImagePicker.Directory.DEFAULT)
                .extension(ImagePicker.Extension.PNG) //       .scale(600, 600)
                .allowMultipleImages(false)
                .enableDebuggingMode(true)
                .build()
    }

    private fun showMenuLogout(view: View) {
        var popup: PopupMenu? = null
        popup = PopupMenu(baseActivity, view)
        popup.inflate(R.menu.menu_facebook_profile)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.menu_logout -> showShortToast("Logout...")
            }
            true
        })
        popup.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as List<String>
            if (mPaths.isNotEmpty()) {
                val imageBitmap = File(mPaths[0]).getBitmap()
                if (this.isProfileImage != null && this.isProfileImage!!) {
                    imageBitmap?.let { binding?.profileImage?.setImageBitmap(it) }
                } else {
                    binding?.businessNameInitial?.gone()
                    binding?.businessImage?.visible()
                    imageBitmap?.let { binding?.businessImage?.setImageBitmap(it) }
                }
            }
        }
    }
}