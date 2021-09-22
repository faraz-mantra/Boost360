package com.appservice.recyclerView

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding

abstract class AppBaseRecyclerViewHolder<Binding : ViewDataBinding>(binding: Binding) :
  BaseRecyclerViewHolder<Binding>(binding), View.OnClickListener {

  protected fun getApplicationContext(): Context? {
    return activity?.applicationContext
  }

  protected fun getResources(): Resources? {
    return getApplicationContext()?.resources
  }

  protected fun getColor(@ColorRes color: Int): Int? {
    return getResources()?.let {
      ResourcesCompat.getColor(
        it,
        color,
        getApplicationContext()?.theme
      )
    }
  }

  protected fun setClickListeners(vararg views: View?) {
    for (view in views) {
      view?.setOnClickListener(this)
    }
  }

  protected fun showLongToast(string: CharSequence) {
    Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show()
  }

  protected fun showShortToast(string: CharSequence) {
    Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show()
  }

}