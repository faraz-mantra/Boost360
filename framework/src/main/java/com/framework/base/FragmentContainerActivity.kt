package com.framework.base

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.framework.R
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar
import kotlinx.android.synthetic.main.activity_fragment_container.*

const val FRAGMENT_TYPE = "FRAGMENT_TYPE"

abstract class FragmentContainerActivity : BaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  protected var type: Int? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    type = intent?.extras?.getInt(FRAGMENT_TYPE)
    super.onCreate(savedInstanceState)
  }

  override fun getLayout(): Int {
    return R.layout.activity_fragment_container
  }

  override fun onCreateView() {
    setFragment()
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  open fun matchLayoutToParents() {
    container.layoutParams = ConstraintLayout.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT
    )
    container.invalidate()
    container.requestLayout()
  }


  private fun setFragment() {
    val fragment = getFragmentInstance(type)
    fragment.arguments = intent.extras
    binding?.container?.id?.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
  }

  abstract fun shouldAddToBackStack(): Boolean
  abstract fun getFragmentInstance(type: Int?): BaseFragment<*, *>
}

fun Intent.setFragmentType(type: Int) {
  this.putExtra(FRAGMENT_TYPE, type)
}
fun Intent.setFragmentType(type: String) {
  this.putExtra(FRAGMENT_TYPE, type)
}