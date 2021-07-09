package com.framework.views.customViews.customSpinner

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SpinnerHintAdapter<T>(context: Context, resource: Int, private val objects: MutableList<T>, isSelectionMandatory: Boolean) : ArrayAdapter<T>(context, resource, objects) {

  var hint: T? = null
    set(hint) {
      field = hint
      objects.add(hint!!)
    }


  var isSelectionMandatory = false
    private set

  init {
    isSelectionMandatory(isSelectionMandatory)
  }

  fun isSelectionMandatory(isMandatory: Boolean) {
    this.isSelectionMandatory = isMandatory
    if (this.hint != null) {
      objects.remove(this.hint!!)
      if (isMandatory) {
        objects.add(this.hint!!)
      } else {
        objects.add(0, this.hint!!)
      }
    }
  }

  override fun getCount(): Int {
    val count = super.getCount()
    return if (isSelectionMandatory) {
      if (count > 0) count - 1 else count
    } else count
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val view = super.getView(position, convertView, parent)
    setHintColor(position, view)
    return view
  }

  override fun getDropDownView(
          position: Int, convertView: View?,
          parent: ViewGroup
  ): View {
    val view = super.getDropDownView(position, convertView, parent)
    setHintColor(position, view)
    return view
  }

  private fun setHintColor(position: Int, view: View) {
    val hintPosition = if (isSelectionMandatory) objects.size - 1 else 0
    if (position == hintPosition) {
      val row = view.findViewById<TextView>(android.R.id.text1)
      if (row != null) {
        row.setTextColor(Color.parseColor("#9e9e9e"))
        row.text = this.hint!!.toString()
      }
    }
  }
}
