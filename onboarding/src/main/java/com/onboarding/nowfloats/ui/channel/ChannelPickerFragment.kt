package com.onboarding.nowfloats.ui.channel

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseBottomSheetDialog
import com.framework.base.BaseFragment
import com.framework.base.BaseResponse
import com.framework.extensions.observeOnce
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.constant.IntentConstant
import com.onboarding.nowfloats.constant.RecyclerViewActionType
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.extensions.addInt
import com.onboarding.nowfloats.extensions.addParcelable
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.model.RequestFloatsModel
import com.onboarding.nowfloats.model.category.CategoryModel
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.haveFacebookChannels
import com.onboarding.nowfloats.model.channel.haveTwitterChannels
import com.onboarding.nowfloats.model.channel.haveWhatsAppChannels
import com.onboarding.nowfloats.model.feature.FeatureModel
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.FragmentChannelPickerBinding
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.rest.response.channel.ChannelListResponse
import com.onboarding.nowfloats.rest.response.feature.FeatureListResponse
import com.onboarding.nowfloats.ui.features.FeaturesBottomSheetDialog
import com.onboarding.nowfloats.ui.startFragmentActivity
import com.onboarding.nowfloats.viewmodel.channel.ChannelPlanViewModel

class ChannelPickerFragment : BaseFragment<FragmentChannelPickerBinding, ChannelPlanViewModel>(), BaseBottomSheetDialog.BottomSheetDialogResult,
    RecyclerItemClickListener {

    private var responseFeatures: FeatureListResponse? = null
    private var channelBottomSheetNDialog: ChannelBottomSheetNDialog? = null
    private var channelFeaturesAdapter: AppBaseRecyclerViewAdapter<FeatureModel>? = null
    private var channelAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
    private var channelList = ArrayList<ChannelModel>()
    private val selectedChannels: ArrayList<ChannelModel>
        get() {
            return ArrayList(channelList.filter { it.isSelected })
        }
    private val category: CategoryModel?
        get() {
            return requestFloatsModel?.category
        }
    private var requestFloatsModel: RequestFloatsModel? = null

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

    fun setBundle(requestFloatsModel: RequestFloatsModel?) {
        this.requestFloatsModel = requestFloatsModel
    }

    override fun onCreateView() {
        viewModel?.getFeatures(baseActivity)
            ?.observeOnce(viewLifecycleOwner, Observer { onGetFeatures(it) })
        viewModel?.getChannels(baseActivity)
            ?.observeOnce(viewLifecycleOwner, Observer { onGetChannels(it) })
        setOnClickListener(binding?.next, binding?.editContainer)
        binding?.channelList?.setOnClickListener { openChannelSelectionSheet() }
    }

    private fun onGetFeatures(response: BaseResponse?) {
        if (response?.error != null) {
            showLongToast(response.error?.localizedMessage); return
        }
        responseFeatures = response as? FeatureListResponse ?: return
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.next -> {
                if (selectedChannels.isEmpty()) {
                    showShortToast(resources.getString(R.string.at_least_one_channel_selected))
                    return
                }
                ChannelConfirmDialog().apply {
                    setCount(selectedChannels.count())
                    setOnConfirmClick(this@ChannelPickerFragment::onChannelConfirmed)
                    show(this@ChannelPickerFragment.parentFragmentManager, "")
                }
            }
            binding?.editContainer -> openChannelSelectionSheet()
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.FEATURE_ITEM_CLICKED.ordinal -> openFeatureDetailSheet(item as? FeatureModel)
        }
    }

    override fun onBottomSheetDismiss(result: Int, data: Any?) {
        when (result) {
            BaseBottomSheetDialog.RESULT_OK,
            BaseBottomSheetDialog.RESULT_CANCELED -> {
                val selectedChannels = channelList.map {
                    it.recyclerViewType = RecyclerViewItemType.CHANNEL_ITEM.getLayout(); it
                }.filter { it.isSelected }
                setChannelAdapter(ArrayList(selectedChannels))
            }
        }
    }

    private fun openChannelSelectionSheet() {
        channelBottomSheetNDialog = ChannelBottomSheetNDialog()
        channelBottomSheetNDialog?.isCancelable = false
        channelBottomSheetNDialog?.onDoneClicked = { onChannelSelected() }
        channelBottomSheetNDialog?.setChannels(channelList)
        channelBottomSheetNDialog?.show(this@ChannelPickerFragment.parentFragmentManager, ChannelBottomSheetNDialog::class.java.name)
    }

    private fun onChannelSelected() {
        channelBottomSheetNDialog?.dismiss()
        val selectedChannels = channelList.map {
            it.recyclerViewType = RecyclerViewItemType.CHANNEL_ITEM.getLayout(); it
        }.filter { it.isSelected }
        setChannelAdapter(ArrayList(selectedChannels))
    }


    private fun openFeatureDetailSheet(feature: FeatureModel?) {
        val bottomSheetFragment = FeaturesBottomSheetDialog()
        bottomSheetFragment.isCancelable = false
        bottomSheetFragment.setFeature(feature)
        bottomSheetFragment.show(baseActivity.supportFragmentManager, bottomSheetFragment::class.java.name)
    }

    private fun setChannelFeaturesAdapter(list: ArrayList<FeatureModel>?) {
        list?.let {
            channelFeaturesAdapter = AppBaseRecyclerViewAdapter(baseActivity, it, this)
            binding?.featureList?.layoutManager = LinearLayoutManager(baseActivity)
            binding?.featureList?.adapter = channelFeaturesAdapter
            binding?.featureList?.let { channelFeaturesAdapter?.runLayoutAnimation(it) }
        }

    }

    private fun setChannelAdapter(channels: ArrayList<ChannelModel>) {
        val channelList = binding?.channelList
        channelAdapter = AppBaseRecyclerViewAdapter(baseActivity, channels, this)
        val recyclerViewWidth = ScreenUtils.instance.getWidth(baseActivity) - ConversionUtils.dp2px(
            32f,
            20f,
            24f,
            16f,
            16f
        )
        val itemWidth = ConversionUtils.dp2px(44f)
        val spanCount = recyclerViewWidth / itemWidth

        channelList?.layoutManager = GridLayoutManager(baseActivity, spanCount)
        channelList?.adapter = channelAdapter
        binding?.channelList?.let { channelAdapter?.runLayoutAnimation(it) }
        val text = StringBuilder(resources.getString(R.string.presence_on) + " ${channels.size} " + resources.getString(R.string.channel))
        if (channels.size > 1 || channels.size == 0) {
            text.append("s")
        }
        binding?.channelPresence?.text = text
    }

    private fun onChannelConfirmed() {
        val bundle = Bundle()
        var totalPages = 2
        if (selectedChannels.haveFacebookChannels()) totalPages++
        if (selectedChannels.haveTwitterChannels()) totalPages++
        if (selectedChannels.haveWhatsAppChannels()) totalPages++

        bundle.addParcelable(
                IntentConstant.REQUEST_FLOATS_INTENT,
                RequestFloatsModel(category, ArrayList(selectedChannels))
            )
            .addInt(IntentConstant.TOTAL_PAGES, totalPages)
            .addInt(IntentConstant.CURRENT_PAGES, 1)
        startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_BASIC_DETAILS, bundle)
    }

    private fun onGetChannels(response: BaseResponse) {
        if (response.error != null) {
            showLongToast(response.error?.localizedMessage); return
        }
        val apiResponse = response as? ChannelListResponse ?: return
        channelList.clear()
        channelList.addAll(apiResponse.data)
    }

    fun startAnimationChannelFragment() {
        binding?.viewChannel?.post {
            binding?.viewChannel?.fadeIn(200L)?.doOnComplete {
                setChannelAdapter(selectedChannels)
                setChannelFeaturesAdapter(responseFeatures?.data)
            }?.andThen(binding?.next?.fadeIn(200L))?.subscribe()
        }
    }
}

