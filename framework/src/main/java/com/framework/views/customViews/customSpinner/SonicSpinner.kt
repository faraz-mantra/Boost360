package com.framework.views.customViews.customSpinner

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner

class SonicSpinner : AppCompatSpinner {

  private var adapter: SpinnerHintAdapter<*>? = null

  constructor(context: Context) : super(context)

  constructor(context: Context, mode: Int) : super(context, mode)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  fun setHintAdapter(adapter: SpinnerHintAdapter<*>) {
    this.adapter = adapter
    super.setAdapter(adapter)
    if (adapter.isSelectionMandatory) {
      setSelection(adapter.count)
    } else {
      setSelection(0)
    }
  }

  override fun getSelectedItem(): Any? {
    val selectedItem = super.getSelectedItem()
    return if (selectedItem === adapter!!.hint) {
      null
    } else selectedItem
  }
}
