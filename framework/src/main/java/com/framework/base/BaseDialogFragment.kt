package com.framework.base

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.framework.helper.Navigator
import com.framework.models.BaseViewModel
import com.framework.views.blur.BlurView
import com.framework.views.blur.RenderScriptBlur

abstract class BaseDialogFragment<T : ViewDataBinding, ViewModel : BaseViewModel> : DialogFragment(), View.OnClickListener {

  protected var binding: T? = null
  protected var viewModel: ViewModel? = null
  protected lateinit var baseActivity: BaseActivity<*, *>
  protected abstract fun getViewModelClass(): Class<ViewModel>
  protected var navigator: Navigator? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
    baseActivity = activity as BaseActivity<*, *>
    viewModel = ViewModelProvider(this).get(getViewModelClass())
    navigator = Navigator(baseActivity)
    return binding?.root
  }

  @LayoutRes
  abstract fun getLayout(): Int

  abstract fun onCreateView()

  protected open fun getWidth(): Int? {
    return null
  }

  protected open fun getHeight(): Int? {
    return null
  }

  protected fun setClickListeners(vararg views: View?) {
    for (view in views) {
      view?.setOnClickListener(this)
    }
  }

  override fun onClick(v: View?) {

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    onCreateView()
  }

  fun setOnClickListener(vararg views: View?) {
    for (view in views) view?.setOnClickListener(this)
  }

  fun removeOnClickListener(vararg views: View) {
    for (view in views) view.setOnClickListener(null)
  }

  fun showLongToast(string: String?) {
    string?.let {
      Toast.makeText(activity, string, Toast.LENGTH_LONG).show()
    }
  }

  fun showShortToast(string: String?) {
    string?.let {
      Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
    }
  }

  override fun onResume() {
    super.onResume()
    val window = dialog?.window ?: return
    val params = window.attributes
    params.width = getWidth() ?: params.width
    params.height = getHeight() ?: params.height
    window.attributes = params
  }
}