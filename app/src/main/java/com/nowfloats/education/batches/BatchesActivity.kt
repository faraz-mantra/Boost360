package com.nowfloats.education.batches

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.nowfloats.Login.UserSessionManager
import com.nowfloats.education.batches.ui.batchesfragment.BatchesFragment
import com.nowfloats.education.helper.Constants
import com.thinksity.R
import com.thinksity.databinding.ActivityBatchesBinding
import java.util.*

class BatchesActivity : BaseActivity<ActivityBatchesBinding, BaseViewModel>() {
  private var progressDialog: ProgressDialog? = null
  private val hmPrices = HashMap<String, Int>()
  var session: UserSessionManager? = null
  private val toolbar: Toolbar? = null
  private var currentFragment: Fragment? = null
  private var fragmentManager: FragmentManager? = null
  private var fragmentTransaction: FragmentTransaction? = null


  private fun initView() {
    supportFragmentManager.addOnBackStackChangedListener {
      val currentFragment = supportFragmentManager.findFragmentById(R.id.mainFrame)
      if (currentFragment != null) {
        val tag = currentFragment.tag
        Log.e("tag", ">>>\$tag")
      } else {
        finish()
      }
    }
  }

  private fun initializeView() {
    session = UserSessionManager(this, this)
    addFragment(BatchesFragment.newInstance(), Constants.BATCHES_FRAGMENT)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      onBackPressed()
      overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  fun showLoader(message: String) {
    runOnUiThread {
      if (progressDialog == null) {
        progressDialog = ProgressDialog(applicationContext)
        progressDialog!!.setCanceledOnTouchOutside(false)
      }
      progressDialog!!.setMessage(message)
      progressDialog!!.show()
    }
  }

  fun hideLoader() {
    runOnUiThread {
      if (progressDialog != null && progressDialog!!.isShowing) {
        progressDialog!!.dismiss()
      }
    }
  }

  fun addFragment(fragment: Fragment, fragmentTag: String?) {
    currentFragment = fragment
    fragmentManager = supportFragmentManager
    fragmentTransaction = fragmentManager!!.beginTransaction()
    fragmentTransaction!!.replace(R.id.mainFrame, fragment, fragmentTag)
    fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    fragmentTransaction!!.addToBackStack(fragmentTag)
    fragmentTransaction!!.commit()
  }

  fun replaceFragment(fragment: Fragment, fragmentTag: String?) {
    popFragmentFromBackStack()
    addFragment(fragment, fragmentTag)
  }

  fun setHeaderText(value: String?) {
    headerText!!.text = value
  }

  fun popFragmentFromBackStack() {
    fragmentManager!!.popBackStack()
  }

  override fun onBackPressed() {
    performBackPressed()
  }

  private fun performBackPressed() {
    try {
      if (supportFragmentManager.backStackEntryCount > 1) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.mainFrame)
        val tag = currentFragment!!.tag
        Log.e("back pressed tag", ">>>\$tag")
        popFragmentFromBackStack()
      } else {
        super.onBackPressed()
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  companion object {
    var headerText: TextView? = null
  }

  override fun getLayout(): Int {
    return R.layout.activity_batches
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    initializeView()
    initView()
  }
}