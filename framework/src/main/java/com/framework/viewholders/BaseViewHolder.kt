package com.framework.viewholders

import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import androidx.recyclerview.widget.RecyclerView
import com.framework.adapters.RecyclerViewItemClickListener
import java.io.Serializable

open class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view), Serializable,
  OnClickListener {

  protected var context: Context = view.context
  var listener: RecyclerViewItemClickListener? = null

  protected fun sendClickEvent(actionType: Int, model: Any) {
    listener?.onItemClick(itemView, adapterPosition, model, actionType)
  }

  protected fun findViewById(id: Int): View? {
    return itemView.findViewById(id)
  }

  open fun onBind(position: Int, obj: Any) {

  }

  override fun onClick(v: View) {

  }
}
