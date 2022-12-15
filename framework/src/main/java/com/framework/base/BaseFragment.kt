package com.framework.base

import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.framework.R
import com.framework.analytics.UserExperiorController
import com.framework.helper.Navigator
import com.framework.models.BaseViewModel
import com.framework.utils.hideKeyBoard
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment<Binding : ViewDataBinding, ViewModel : BaseViewModel> : Fragment(), View.OnClickListener {

  protected lateinit var baseActivity: BaseActivity<*, *>
  protected lateinit var root: View
  protected var viewModel: ViewModel? = null
  protected lateinit var binding: Binding
  protected var navigator: Navigator? = null
  protected var compositeDisposable = CompositeDisposable()
  private var progressDialog: ProgressDialog? = null


  protected abstract fun getLayout(): Int
  protected abstract fun getViewModelClass(): Class<ViewModel>
  protected abstract fun onCreateView()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    baseActivity = activity as BaseActivity<*, *>
    viewModel = ViewModelProvider(this).get(getViewModelClass())
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    setHasOptionsMenu(true)
    binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
    binding.lifecycleOwner = this
    navigator = Navigator(baseActivity)
    return binding.root
  }

  override fun onPrepareOptionsMenu(menu: Menu) {
    super.onPrepareOptionsMenu(menu)
    baseActivity.adjustToolbarTitleMarginEnd(menu)
  }

  fun setToolbarTitle(title: String?) {
    baseActivity.setToolbarTitle(title)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    onCreateView()
    val observables = getObservables()
    for (observable in observables) {
      observable?.let { compositeDisposable.add(it) }
    }
  }


  override fun onResume() {
    super.onResume()
    Log.d("user experior", "onResume: userexperior ${binding!!::class.simpleName}${viewModel!!::class.simpleName}")
    UserExperiorController.startScreen("${binding!!::class.simpleName}${viewModel!!::class.simpleName}")
  }

  fun showSnackBarNegative(msg: String) {
    val snackBar = Snackbar.make(baseActivity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_INDEFINITE)
    snackBar.view.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.snackbar_negative_color))
    snackBar.duration = 4000
    snackBar.show()
  }


  fun showSnackBarPositive(msg: String) {
    val snackBar = Snackbar.make(baseActivity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_INDEFINITE)
    snackBar.view.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.snackbar_positive_color))
    snackBar.duration = 4000
    snackBar.show()
  }

  override fun onDestroy() {
    super.onDestroy()
    compositeDisposable.clear()
  }

  override fun onClick(v: View) {}


  fun setOnClickListener(vararg views: View?) {
    for (view in views) view?.setOnClickListener(this)
  }

  fun isVisible(vararg views: View?) {
    for (view in views) view?.visibility = View.VISIBLE
  }

  fun isGone(vararg views: View?) {
    for (view in views) view?.visibility = View.GONE
  }

  fun removeOnClickListener(vararg views: View) {
    for (view in views) view.setOnClickListener(null)
  }

  protected open fun getObservables(): List<Disposable?> {
    return listOf()
  }

  // Transactions
  open fun addFragmentReplace(containerID: Int?, fragment: Fragment?, addToBackStack: Boolean, showAnim: Boolean = false) {
    if (activity?.supportFragmentManager?.isDestroyed == true) return
    if (containerID == null || fragment == null) return

    val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
    if (showAnim) {
      fragmentTransaction?.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
    }


    if (addToBackStack) {
      fragmentTransaction?.addToBackStack(fragment.javaClass.name)
    }
    fragmentTransaction?.replace(containerID, fragment, fragment.javaClass.name)?.commit()
  }

  open fun addFragment(containerID: Int?, fragment: Fragment?, addToBackStack: Boolean, showAnim: Boolean = false) {
    if (activity?.supportFragmentManager?.isDestroyed == true) return
    if (containerID == null || fragment == null) return

    val fragmentTransaction = baseActivity.supportFragmentManager.beginTransaction()
    if (showAnim) {
      fragmentTransaction?.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
    }
    if (addToBackStack) {
      fragmentTransaction.addToBackStack(fragment.javaClass.name)
    }
    fragmentTransaction.add(containerID, fragment, fragment.javaClass.name).commit()
  }

  fun removeFragment(name: String) {
    val fm = activity?.supportFragmentManager
    fm?.popBackStack(name, 0)
    activity?.supportFragmentManager?.popBackStack()
  }

  fun getTopFragment(): Fragment? {
    parentFragmentManager.run {
      return when (backStackEntryCount) {
        0 -> null
        else -> findFragmentByTag(getBackStackEntryAt(backStackEntryCount - 1).name)
      }
    }
  }

  fun getLifeCycleState(): Lifecycle.State {
    return viewLifecycleOwner.lifecycle.currentState
  }

  fun showLongToast(string: String?) {
    string?.let { Toast.makeText(baseActivity, string, Toast.LENGTH_LONG).show() }
  }

  fun showShortToast(string: String?) {
    string?.let { Toast.makeText(baseActivity, string, Toast.LENGTH_SHORT).show() }
  }

  protected fun getColor(@ColorRes color: Int): Int {
    return ContextCompat.getColor(baseActivity, color)
  }

  protected fun getFont(@FontRes font: Int): Typeface? {
    return ResourcesCompat.getFont(baseActivity, font)
  }

  fun showLoader(message: String?) {
    if (activity == null || !isAdded) return
    if (progressDialog == null) {
      progressDialog = ProgressDialog(activity)
      progressDialog?.setCanceledOnTouchOutside(false)
      progressDialog?.setCancelable(false)
    }
    progressDialog?.setMessage(message)
    progressDialog?.show()
  }

  fun hideLoader() {
    if (progressDialog != null && progressDialog?.isShowing()!!) {
      progressDialog?.dismiss()
    }
  }

  override fun onStop() {
    super.onStop()
    baseActivity.hideKeyBoard()
  }
}