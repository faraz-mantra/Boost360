package com.onboarding.nowfloats.ui.channel

import android.view.View
import com.framework.base.BaseDialogFragment
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewActionType
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.DialogChannelBottomSheetNewBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener

@Deprecated("not use")
class ChannelBottomSheetNDialog : BaseDialogFragment<DialogChannelBottomSheetNewBinding, BaseViewModel>(), RecyclerItemClickListener {

    private var list = ArrayList<ChannelModel>()
    private var adapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
    private var channels = ArrayList<ChannelModel>()

    var onDoneClicked: (channels: ArrayList<ChannelModel>?) -> Unit = { }

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

  override fun onCreateView() {
        setOnClickListener(binding?.done)
        list.filter { it.isSelected!! }.takeIf { it.size > 1 }?.let { binding?.done?.visible() }
        val text = StringBuilder(resources.getString(R.string.recommended_on) + " " + list.size + " " + resources.getString(R.string.channel))
        if (channels.size > 1 || channels.size == 0) text.append(resources.getString(R.string.more_than_one_add_s))
        binding?.title?.text = text
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
        onDoneClicked(list)
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.CHANNEL_ITEM_WHY_CLICKED.ordinal -> openWhyChannelDialog(item as? ChannelModel)
            RecyclerViewActionType.CHANNEL_ITEM_CLICKED.ordinal -> {
                if (position != 0) {
                    val isSelected = !list[position].isSelected!!
                    list[position].isSelected = isSelected
                    if (list[position].isFacebookPage()) {
                        if (isSelected.not()) {
                            val isShop = list.isFbPageOrShop(ChannelType.FB_SHOP)
                            if (isShop != null && isShop.isSelected!!) isShop.isSelected = !isShop.isSelected!!
                        }
                    } else if (list[position].isFacebookShop()) {
                        if (isSelected) {
                            val isPage = list.isFbPageOrShop(ChannelType.FB_PAGE)
                            if (isPage != null && isPage.isSelected!!.not()) isPage.isSelected = !isPage.isSelected!!
                        }
                    }
                    adapter?.notifyDataSetChanged()
                    if (binding?.done?.visibility == View.GONE) binding?.done?.visible()
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