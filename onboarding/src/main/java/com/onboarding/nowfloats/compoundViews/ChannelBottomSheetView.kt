package com.onboarding.nowfloats.compoundViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.framework.base.BaseActivity
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.CompoundViewChannelBottomSheetBinding
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter

@Deprecated("not in use")
class ChannelBottomSheetView : ConstraintLayout, View.OnClickListener {

  private var binding: CompoundViewChannelBottomSheetBinding? = null
  private var adapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
  private var channels = ArrayList<ChannelModel>()

  var onDoneClicked: () -> Unit = { }

  constructor(context: Context) : super(context) {
    setup(context, null)
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    setup(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    setup(context, attrs)
  }

  private fun setup(context: Context?, attrs: AttributeSet?) {
    if (isInEditMode) {
      View.inflate(context, R.layout.compound_view_channel_bottom_sheet, this)
    } else {
      binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.compound_view_channel_bottom_sheet, this, true)
      setCustomAttrs(context, attrs)
      binding?.done?.setOnClickListener(this)
    }
  }

  private fun setCustomAttrs(context: Context?, attrs: AttributeSet?) {
    if (attrs == null) return
  }

  fun setChannels(activity: BaseActivity<*, *>, list: ArrayList<ChannelModel>) {
    if (adapter == null) {
      adapter = AppBaseRecyclerViewAdapter(activity, channels)
      binding?.recyclerView?.adapter = adapter
    }
    channels.clear()
    channels.addAll(list.map {
      it.recyclerViewType = RecyclerViewItemType.CHANNEL_BOTTOM_SHEET_ITEM.getLayout(); it
    })
    adapter?.runLayoutAnimation(binding?.recyclerView)
  }

  fun scrollToTop() {
    if (channels.isNotEmpty()) binding?.recyclerView?.layoutManager?.scrollToPosition(0)
  }


  override fun onClick(v: View?) {
    onDoneClicked()
  }
}