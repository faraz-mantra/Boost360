package com.onboarding.nowfloats.bottomsheet

import android.app.Dialog
import android.view.View
import com.framework.base.BaseActivity
import com.framework.views.viewgroups.BaseRecyclerView
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.bottomsheet.inerfaces.ContentBuilder
import com.onboarding.nowfloats.bottomsheet.util.ObservableList
import com.onboarding.nowfloats.bottomsheet.util.listenListToUpdate
import com.onboarding.nowfloats.constant.RecyclerViewActionType
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import kotlinx.android.synthetic.main.channel_content_sheet.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class ChannelContentBuilder(var context: BaseActivity<*, *>, items: ObservableList<ChannelModel>, autoDismiss: Boolean = true,
                            val onItemClick: OnItemClick<ChannelModel>) : ContentBuilder(), RecyclerItemClickListener {

    val items: ObservableList<ChannelModel> by listenListToUpdate(items, this)
    val channel: ArrayList<ChannelModel>
        get() {
            return ArrayList(items)
        }
    private var adapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null

    private lateinit var recyclerView: BaseRecyclerView
    override val layoutRes: Int
        get() = R.layout.channel_content_sheet


    override fun init(view: View) {
        recyclerView = view.recyclerView
        Timer().schedule(800) { recyclerView.post { setChannelAdapter() } }
    }

    private fun setChannelAdapter() {
        if (adapter == null) {
            channel.clear()
            channel.addAll(items.map {
                it.recyclerViewType = RecyclerViewItemType.CHANNEL_BOTTOM_SHEET_ITEM.getLayout(); it
            })
            adapter = AppBaseRecyclerViewAdapter(context, channel, this)
            recyclerView.adapter = adapter
            adapter?.runLayoutAnimation(recyclerView)
        }
    }

    override fun updateContent(type: Int, data: Any?) {
        if (type == 3) adapter?.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        val action = when (actionType) {
            RecyclerViewActionType.CHANNEL_ITEM_CLICKED.ordinal -> true
            RecyclerViewActionType.CHANNEL_ITEM_WHY_CLICKED.ordinal -> false
            else -> false
        }
        onItemClick.invoke(dialog, position, item as ChannelModel, action)
    }

}

typealias OnItemClick<T> = (dialog: Dialog, position: Int, item: T, isLongClick: Boolean) -> Unit
