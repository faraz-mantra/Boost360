package com.onboarding.nowfloats.ui.channel

import SectionsFeature
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseFragment
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.constant.IntentConstant
import com.onboarding.nowfloats.constant.RecyclerViewActionType
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.FragmentChannelPickerBinding
import com.onboarding.nowfloats.extensions.addInt
import com.onboarding.nowfloats.extensions.addParcelable
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.model.RequestFloatsModel
import com.onboarding.nowfloats.model.category.CategoryDataModel
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.ui.features.FeaturesBottomSheetDialog
import com.onboarding.nowfloats.ui.startFragmentActivity
import com.onboarding.nowfloats.viewmodel.channel.ChannelPlanViewModel

class ChannelPickerFragment : BaseFragment<FragmentChannelPickerBinding, ChannelPlanViewModel>(), RecyclerItemClickListener {

    private var featuresBottomSheetDialog: FeaturesBottomSheetDialog? = null

    private var categoryDataModel: CategoryDataModel? = null
    private var channelBottomSheetNDialog: ChannelBottomSheetNDialog? = null
    private var channelFeaturesAdapter: AppBaseRecyclerViewAdapter<SectionsFeature>? = null
    private var channelAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
    private val channelList: ArrayList<ChannelModel> = ArrayList()
    private val selectedChannels: ArrayList<ChannelModel>?
        get() {
            return channelList.let { it.filter { it1 -> it1.isSelected!! } as ArrayList<ChannelModel> }
        }
    private val responseFeatures: ArrayList<SectionsFeature>?
        get() {
            return categoryDataModel?.sections
        }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): ChannelPickerFragment {
            val fragment = ChannelPickerFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_channel_picker
    }

    override fun getViewModelClass(): Class<ChannelPlanViewModel> {
        return ChannelPlanViewModel::class.java
    }

    fun setBundle(categoryDataModel: CategoryDataModel?) {
        this.categoryDataModel = categoryDataModel
        this.categoryDataModel?.getChannelList()?.let { channelList.addAll(it) }
    }

    override fun onCreateView() {
        setOnClickListener(binding?.next, binding?.editContainer)
        binding?.channelList?.setOnClickListener { openChannelSelectionSheet() }
    }


    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.next -> {
                selectedChannels?.let { channels ->
                    if (channels.isEmpty()) {
                        showShortToast(resources.getString(R.string.at_least_one_channel_selected))
                        return
                    }
                    ChannelConfirmDialog().apply {
                        setCount(channels.count())
                        isCancelable = false
                        setOnConfirmClick(this@ChannelPickerFragment::onChannelConfirmed)
                        show(this@ChannelPickerFragment.parentFragmentManager, "")
                    }
                }
            }
            binding?.editContainer -> openChannelSelectionSheet()
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.FEATURE_ITEM_CLICKED.ordinal -> openFeatureDetailSheet(item as? SectionsFeature)
        }
    }

    private fun openChannelSelectionSheet() {
        channelList.let { channels ->
            channelBottomSheetNDialog = ChannelBottomSheetNDialog()
            channelBottomSheetNDialog?.isCancelable = false
            channelBottomSheetNDialog?.onDoneClicked = { onChannelSelected(it) }
            channelBottomSheetNDialog?.setChannels(channels)
            channelBottomSheetNDialog?.show(this@ChannelPickerFragment.parentFragmentManager, ChannelBottomSheetNDialog::class.java.name)
        }
    }

    private fun onChannelSelected(channels: ArrayList<ChannelModel>?) {
        channelBottomSheetNDialog?.dismiss()
        channels?.let {
            channelList.clear()
            channelList.addAll(it)
        }
        val selectedChannels = channelList.map {
            it.recyclerViewType = RecyclerViewItemType.CHANNEL_ITEM.getLayout(); it
        }.filter { it.isSelected!! }
        setChannelAdapter(selectedChannels as ArrayList<ChannelModel>?)

    }


    private fun openFeatureDetailSheet(feature: SectionsFeature?) {
        val featuresBottomSheetDialog = FeaturesBottomSheetDialog()
        featuresBottomSheetDialog.isCancelable = true
        featuresBottomSheetDialog.setFeature(feature)
        featuresBottomSheetDialog.show(baseActivity.supportFragmentManager, featuresBottomSheetDialog::class.java.name)
        this.featuresBottomSheetDialog = featuresBottomSheetDialog
    }

    private fun setChannelFeaturesAdapter(list: ArrayList<SectionsFeature>?) {
        list?.let {
            channelFeaturesAdapter = AppBaseRecyclerViewAdapter(baseActivity, it, this)
            binding?.featureList?.layoutManager = LinearLayoutManager(baseActivity)
            binding?.featureList?.adapter = channelFeaturesAdapter
            binding?.featureList?.let { channelFeaturesAdapter?.runLayoutAnimation(it) }
        }

    }

    private fun setChannelAdapter(list: ArrayList<ChannelModel>?, animate: Boolean = true) {
        list?.let { channels ->
            channelAdapter = AppBaseRecyclerViewAdapter(baseActivity, channels, this)
            val recyclerViewWidth =
                    ScreenUtils.instance.getWidth(baseActivity) - ConversionUtils.dp2px(32f, 20f, 24f, 16f, 16f)
            val itemWidth = ConversionUtils.dp2px(44f)
            val spanCount = recyclerViewWidth / itemWidth
            binding?.channelList?.layoutManager = GridLayoutManager(baseActivity, spanCount)
            binding?.channelList?.adapter = channelAdapter
            binding?.channelList?.let {
                if(animate) channelAdapter?.runLayoutAnimation(it)
                else channelAdapter?.notifyDataSetChanged()
            }
            val text = StringBuilder(resources.getString(R.string.presence_on) + " ${channels.size} " + resources.getString(R.string.channel))
            if (channels.size > 1 || channels.size == 0) text.append(resources.getString(R.string.more_than_one_add_s))
            binding?.channelPresence?.text = text
        }
    }

    private fun onChannelConfirmed() {
        val bundle = Bundle()
        var totalPages = 2
        selectedChannels?.let { channels ->
            if (channels.haveFacebookShop()) totalPages++
            if (channels.haveFacebookPage()) totalPages++
            if (channels.haveTwitterChannels()) totalPages++
            if (channels.haveWhatsAppChannels()) totalPages++
        }
        bundle.addParcelable(
                IntentConstant.REQUEST_FLOATS_INTENT,
                        RequestFloatsModel(categoryDataModel, ArrayList(selectedChannels))
            )
            .addInt(IntentConstant.TOTAL_PAGES, totalPages)
            .addInt(IntentConstant.CURRENT_PAGES, 1)
        startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_BASIC_DETAILS, bundle)
    }


    fun startAnimationChannelFragment() {
        binding?.viewChannel?.post {
            setChannelAdapter(selectedChannels, animate = false)
            binding?.viewChannel?.fadeIn(200L)?.doOnComplete {
                setChannelFeaturesAdapter(responseFeatures)
            }?.andThen(binding?.next?.fadeIn(200L))?.subscribe()
        }
    }
}

