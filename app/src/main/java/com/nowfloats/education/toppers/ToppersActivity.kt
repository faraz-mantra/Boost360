package com.nowfloats.education.toppers

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.nowfloats.Login.UserSessionManager
import com.nowfloats.education.helper.Constants
import com.nowfloats.education.helper.Constants.TOPPERS_FRAGMENT
import com.nowfloats.education.model.UnlockFeatureModel
import com.nowfloats.education.toppers.ui.topperhome.ToppersFragment
import com.nowfloats.education.unlockfeature.UnlockFeatureFragment
import com.thinksity.R
import java.util.*

class ToppersActivity : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null
    private val hmPrices = HashMap<String, Int>()
    var session: UserSessionManager? = null
    private val toolbar: Toolbar? = null
    private var currentFragment: Fragment? = null
    private var fragmentManager: FragmentManager? = null
    private var fragmentTransaction: FragmentTransaction? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toppers)
        initializeView()
        initView()
    }

    private fun initView() {
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.mainFrame)
            if (currentFragment != null) {
                val tag = currentFragment.tag
                Log.e("tag", ">>>$tag")
            } else {
                finish()
            }
        }
    }

    private fun initializeView() {
        session = UserSessionManager(this, this)

        if (com.nowfloats.util.Constants.StoreWidgets.contains(Constants.TOPPER_FEATURE)) {
            addFragment(ToppersFragment.newInstance(), TOPPERS_FRAGMENT)
        } else {
            val unlockFeatureModel = UnlockFeatureModel(
                    buyItemKey = Constants.TOPPER_FEATURE,
                    titleFeatureName = getString(R.string.our_toppers),
                    featureDescription = getString(R.string.our_toppers_feature_description)
            )
            addFragment(UnlockFeatureFragment.newInstance(session, unlockFeatureModel), Constants.UNLOCK_FEATURE_FRAGMENT)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun showLoader(message: String?) {
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

    fun addFragment(fragment: Fragment?, fragmentTag: String?) {
        currentFragment = fragment
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.mainFrame, fragment as Fragment, fragmentTag)
        fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction!!.addToBackStack(fragmentTag)
        fragmentTransaction!!.commit()
    }

    fun replaceFragment(fragment: Fragment?, fragmentTag: String?) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        var headerText: TextView? = null
    }
}