package com.appservice.ui.testimonial

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.constant.FragmentType
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar


open class TestimonialContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var type: FragmentType? = null
  private var testimonialListFragment: TestimonialListFragment? = null
  private var testimonialAddEditFragment: TestimonialAddEditFragment? = null

  override fun getLayout(): Int {
    return com.framework.R.layout.activity_fragment_container
  }


  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    intent?.extras?.getInt(FRAGMENT_TYPE)?.let { type = FragmentType.values()[it] }
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView() {
    super.onCreateView()
    setFragment()
  }


  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }


  override fun getToolbarBackgroundColor(): Int? {
    return when (type) {
      FragmentType.SERVICE_INFORMATION,FragmentType.TESTIMONIAL_ADD_EDIT_FRAGMENT-> ContextCompat.getColor(this, R.color.orange)
      else -> super.getToolbarBackgroundColor()
    }
  }

  override fun getToolbarTitleColor(): Int? {
    return when (type) {
      FragmentType.SERVICE_INFORMATION,FragmentType.TESTIMONIAL_ADD_EDIT_FRAGMENT-> ContextCompat.getColor(this, R.color.white)
      else -> super.getToolbarTitleColor()
    }
  }

  override fun getNavigationIcon(): Drawable? {
    return when (type) {
      FragmentType.SERVICE_INFORMATION,FragmentType.TESTIMONIAL_ADD_EDIT_FRAGMENT-> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_new)
      else -> super.getNavigationIcon()
    }
  }

  override fun getToolbarTitle(): String? {
    return when (type) {
      FragmentType.SERVICE_INFORMATION-> resources.getString(R.string.testimonial_title)
      FragmentType.TESTIMONIAL_ADD_EDIT_FRAGMENT -> resources.getString(R.string.testimonial_add)
      else -> super.getToolbarTitle()
    }
  }


  private fun shouldAddToBackStack(): Boolean {
    return when (type) {
      else -> false
    }
  }

  private fun setFragment() {
    val fragment = getFragmentInstance(type)
    fragment?.arguments = intent.extras
    binding?.container?.id?.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
  }

  private fun getFragmentInstance(type: FragmentType?): BaseFragment<*, *>? {
    return when (type) {
      FragmentType.TESTIMONIAL_LIST_FRAGMENT -> {
        testimonialListFragment = TestimonialListFragment.newInstance()
        testimonialListFragment
      }
      FragmentType.TESTIMONIAL_ADD_EDIT_FRAGMENT -> {
        testimonialAddEditFragment = TestimonialAddEditFragment.newInstance()
        testimonialAddEditFragment
      }
      else -> throw IllegalFragmentTypeException()
    }
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    testimonialListFragment?.onActivityResult(requestCode, resultCode, data)
    testimonialAddEditFragment?.onActivityResult(requestCode, resultCode, data)
  }

  override fun onBackPressed() {
    when (type) {
      FragmentType.SERVICE_DETAIL_VIEW -> testimonialAddEditFragment?.onNavPressed()
      else -> super.onBackPressed()
    }
  }
}

fun Fragment.startTestimonialFragmentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false, isResult: Boolean = false) {
  val intent = Intent(activity, TestimonialContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun startTestimonialFragmentActivityNew(activity: Activity, type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean, isResult: Boolean = false) {
  val intent = Intent(activity, TestimonialContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) activity.startActivity(intent) else activity.startActivityForResult(intent, 101)
}

fun AppCompatActivity.startTestimonialFragmentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false) {
  val intent = Intent(this, TestimonialContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
  return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}