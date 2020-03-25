package com.onboarding.nowfloats.ui.channel

import android.view.View
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.constant.RecyclerViewActionType
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.DialogChannelBottomSheetNewBinding
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener

class ChannelBottomSheetNDialog : BaseDialogFragment<DialogChannelBottomSheetNewBinding, BaseViewModel>(), RecyclerItemClickListener {

    private var list = ArrayList<ChannelModel>()
    private var adapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
    private var channels = ArrayList<ChannelModel>()

    var onDoneClicked: () -> Unit = { }

    override fun getLayout(): Int {
        return R.layout.dialog_channel_bottom_sheet_new
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun getTheme(): Int {
        return R.style.MaterialDialogThemeFull
    }

    fun setChannels(list: ArrayList<ChannelModel>) {
        this.list.clear()
        this.list.addAll(list)
    }

    override fun onViewCreated() {
        setOnClickListener(binding?.done)
        binding?.iconShare?.post {
            binding?.iconShare?.fadeIn()?.andThen(binding?.title?.fadeIn(100L))
                ?.andThen(binding?.subTitle?.fadeIn(100L))?.doOnComplete {
                    setChannelAdapter(list)
                }?.andThen(binding?.done?.fadeIn(300L))?.subscribe()
        }
    }

    private fun setChannelAdapter(list: ArrayList<ChannelModel>) {
        if (adapter == null) {
            adapter = AppBaseRecyclerViewAdapter(baseActivity, channels, this)
            binding?.recyclerView?.adapter = adapter
        }
        channels.clear()
        channels.addAll(list.map {
            it.recyclerViewType = RecyclerViewItemType.CHANNEL_BOTTOM_SHEET_ITEM.getLayout(); it
        })
        adapter?.runLayoutAnimation(binding?.recyclerView)
    }


    override fun onClick(v: View?) {
        onDoneClicked()
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.CHANNEL_ITEM_WHY_CLICKED.ordinal -> openWhyChannelDialog(item as? ChannelModel)
            RecyclerViewActionType.CHANNEL_ITEM_CLICKED.ordinal -> {
                if (position != 0) {
                    list[position].isSelected = !list[position].isSelected
                    if (position == 1) list[3].isSelected = list[position].isSelected
                    else if (position == 3) list[1].isSelected = list[position].isSelected
                    adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun openWhyChannelDialog(channelModel: ChannelModel?) {
        ChannelWhyDialog().apply {
            setChannels(channelModel)
            show(this@ChannelBottomSheetNDialog.parentFragmentManager, "")
        }
    }
}