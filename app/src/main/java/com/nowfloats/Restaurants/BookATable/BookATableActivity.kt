package com.nowfloats.Restaurants.BookATable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.boost.dbcenterapi.utils.Utils
import com.boost.payment.base_class.BaseFragment
import com.nowfloats.Restaurants.BookATable.ui.BookATableFragment.BookATableFragment

import com.thinksity.R

class BookATableActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_book_a_table)
    initView()
  }

  fun initView() {
    addFragment(BookATableFragment.newInstance(), "BOOK_A_TABLE_FRAGMENT")

    supportFragmentManager.addOnBackStackChangedListener {
      val currentFragment = supportFragmentManager.findFragmentById(R.id.ao_fragment_container)
      if (currentFragment != null) {
        val tag = currentFragment.tag
        Log.e("Add tag", ">>>$tag")
        tellFragments()
      } else finish()

    }
  }

  override fun onBackPressed() {
    performBackPressed()
  }

  private fun performBackPressed() {
    try {
      Utils.hideSoftKeyboard(this)
      if (supportFragmentManager.backStackEntryCount > 0) {
        fragmentManager!!.popBackStack()
      } else super.onBackPressed()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }


  private var currentFragment: Fragment? = null
  private var fragmentManager: FragmentManager? = null
  private var fragmentTransaction: FragmentTransaction? = null


  fun addFragment(fragment: Fragment, fragmentTag: String?) {
    currentFragment = fragment
    fragmentManager = supportFragmentManager
    fragmentTransaction = fragmentManager!!.beginTransaction()
    fragmentTransaction!!.add(R.id.ao_fragment_container, fragment, fragmentTag)
    fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    fragmentTransaction!!.addToBackStack(fragmentTag)
    fragmentTransaction!!.commit()
  }

  fun replaceFragment(fragment: Fragment, fragmentTag: String?) {
    popFragmentFromBackStack()
    addFragment(fragment, fragmentTag)
  }

  fun popFragmentFromBackStack() {
    fragmentManager!!.popBackStack()
  }


  private fun tellFragments() {
    val fragments =
      supportFragmentManager.fragments
    for (f in fragments) {
      if (f != null && f is BaseFragment)
        f.onBackPressed()
    }
  }

}