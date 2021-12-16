package com.framework.base

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.framework.R
import com.framework.analytics.SentryController
import com.framework.helper.Navigator
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.hideKeyBoard
import com.framework.views.customViews.CustomToolbar
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity<Binding : ViewDataBinding, ViewModel : BaseViewModel> : AppCompatActivity(), View.OnClickListener {

  protected open var TAG = this.javaClass.simpleName
  protected var navigator: Navigator? = null
  protected var binding: Binding? = null
  protected lateinit var viewModel: ViewModel
  protected var compositeDisposable = CompositeDisposable()

  protected abstract fun getLayout(): Int
  protected abstract fun getViewModelClass(): Class<ViewModel>
  protected abstract fun onCreateView()

  override fun onCreate(savedInstanceState: Bundle?) {
    customTheme()?.let { this.setTheme(it) }
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, getLayout())
    binding?.lifecycleOwner = this
    viewModel = ViewModelProviders.of(this).get(getViewModelClass())
    navigator = Navigator(this)
    setToolbar()
    val observables = getObservables()
    for (observable in observables) {
      observable?.let { compositeDisposable.add(it) }
    }
    onCreateView()
  }

  protected open fun getObservables(): List<Disposable?> {
    return listOf()
  }

  open fun customTheme(): Int? {
    return null
  }

  override fun onDestroy() {
    super.onDestroy()
    compositeDisposable.clear()
  }

  open fun getToolbar(): CustomToolbar? {
    return null
  }

  open fun getToolbarTitle(): String? {
    return ""
  }

  open fun getToolbarSubTitle(): String? {
    return null
  }

  open fun getToolbarTitleTypeface(): Typeface? {
    return ResourcesCompat.getFont(this, R.font.semi_bold)
  }

  open fun getToolbarTitleSize(): Float? {
    return ConversionUtils.dp2px(16f).toFloat()
  }

  open fun getToolbarSubTitleSize(): Float? {
    return resources.getDimension(R.dimen.toolbar_sub_title)
  }

  open fun getNavIconScale(): Float {
    return 1.0f
  }

  open fun getToolbarTitleGravity(): Int {
    return Gravity.START
  }

  /**
   * The new text color in 0xAARRGGBB format
   */
  open fun getToolbarTitleColor(): Int? {
    return null
  }

  /**
   * The new text color in 0xAARRGGBB format
   */
  open fun getToolbarBackgroundColor(): Int? {
    return null
  }

  open fun getNavigationIcon(): Drawable? {
    return null
  }

  open fun isVisibleBackButton(): Boolean {
    return true
  }

  open fun isHideToolbar(): Boolean {
    return false
  }

  open fun getSubtitleAlpha(): Float? {
    return 0.7F
  }


  fun adjustToolbarTitleMarginEnd(menu: Menu) {
    if (this.getToolbarTitleGravity() == Gravity.CENTER_HORIZONTAL || this.getToolbarTitleGravity() == Gravity.CENTER) {
      val iteration = menu.children.iterator()
      var b = false
      if (iteration.hasNext()) b = iteration.next().isVisible
      if (menu.size() > 0 && b) {
        this.getToolbar()?.titleMarginEnd = ConversionUtils.dp2px(18f)
      } else this.getToolbar()?.titleMarginEnd = ConversionUtils.dp2px(70f)
    }
  }

  private fun setToolbar() {
    val toolbar = getToolbar() ?: return
    if (!isHideToolbar()) {
      toolbar.setNavigationOnClickListener { onBackPressed() }
      setToolbarTitle(getToolbarTitle())
      getToolbarSubTitle()?.let { setToolbarSubTitle(it) }
      toolbar.getNavImageButton()?.let {
        it.scaleX = getNavIconScale()
        it.scaleY = getNavIconScale()
      }
      getNavigationIcon()?.let { toolbar.navigationIcon = it }
      getToolbarBackgroundColor()?.let { toolbar.setBackgroundColor(it) }
      setSupportActionBar(toolbar)
      val actionBar: ActionBar? = supportActionBar
      actionBar?.setDisplayHomeAsUpEnabled(isVisibleBackButton())
    } else toolbar.visibility = View.GONE
  }

  fun setToolbarTitle(title: String?) {
    val toolbar = getToolbar() ?: return
    toolbar.title = title
    getToolbarTitleColor()?.let { toolbar.setTitleTextColor(it) }
    toolbar.getToolbarTitleTextView()?.let { titleView ->
      titleView.setToolbarTitleGravity()
      getToolbarTitleTypeface()?.let { titleView.typeface = it }
      getToolbarTitleSize()?.let { titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it) }
    }
  }


  fun setToolbarSubTitle(subTitle: String) {
    val toolbar = getToolbar() ?: return
    toolbar.subtitle = subTitle
    getToolbarTitleColor()?.let { toolbar.setSubtitleTextColor(it) }
    toolbar.getToolbarSubTitleTextView()?.let { subTitleView ->
      subTitleView.setToolbarTitleGravity()
      getToolbarSubTitleSize()?.let { subTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it) }
      getSubtitleAlpha()?.let { subTitleView.alpha = it }
    }
  }

  private fun TextView.setToolbarTitleGravity() {
    textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
    gravity = getToolbarTitleGravity()
    (layoutParams as? Toolbar.LayoutParams)?.width = Toolbar.LayoutParams.MATCH_PARENT
  }


  override fun onClick(v: View?) {

  }

  open fun onNavPressed() {
    this.hideKeyBoard()
    super.onBackPressed()
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    return if (keyCode == KeyEvent.KEYCODE_MENU) {
      true
    } else super.onKeyDown(keyCode, event)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        onNavPressed()
        return true
      }
    }
    return false
  }


  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    val result = super.onPrepareOptionsMenu(menu)
    this.adjustToolbarTitleMarginEnd(menu)
    return result
  }

  open fun setOnClickListener(vararg views: View?) {
    for (view in views) view?.setOnClickListener(this)
  }

  open fun removeOnClickListener(vararg views: View) {
    for (view in views) view.setOnClickListener(null)
  }

  open fun addFragment(containerID: Int?, fragment: Fragment?, addToBackStack: Boolean,showAnim:Boolean=false) {
    if (supportFragmentManager.isDestroyed) return
    if (containerID == null || fragment == null) return

    val fragmentTransaction = supportFragmentManager.beginTransaction()
    if (showAnim){
      fragmentTransaction?.
      setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
    }
    if (addToBackStack) {
      fragmentTransaction.addToBackStack(fragment.javaClass.name)
    }
    fragmentTransaction.add(containerID, fragment, fragment.javaClass.name).commit()
  }

  // Fragment
  open fun addFragmentReplace(containerId: Int?, fragment: Fragment?, addToBackStack: Boolean,showAnim:Boolean=false) {
    if (supportFragmentManager.isDestroyed) return
    if (containerId == null || fragment == null) return

    val fragmentTransaction = supportFragmentManager.beginTransaction()
    if (showAnim){
      fragmentTransaction?.
      setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
    }
    if (addToBackStack) {
      fragmentTransaction.addToBackStack(fragment.javaClass.name)
    }
    try {
      fragmentTransaction.replace(containerId, fragment,fragment.javaClass.name).commit()
    } catch (e: IllegalStateException) {
      e.printStackTrace()
      SentryController.captureException(e)
    }
  }

  open fun getTopFragment(): Fragment? {
    supportFragmentManager.run {
      return when (backStackEntryCount) {
        0 -> null
        else -> findFragmentByTag(getBackStackEntryAt(backStackEntryCount - 1).name)
      }
    }
  }

  fun showLongToast(string: String?) {
    string?.let {
      Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }
  }

  fun showShortToast(string: String?) {
    string?.let {
      Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }
  }

}
