package com.appservice.ui.testimonial

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.appservice.R
import com.appservice.databinding.FragmentTestimonialAddEditBinding
import com.appservice.model.SessionData

class TestimonialAddEditFragment : BaseTestimonialFragment<FragmentTestimonialAddEditBinding>() {

  private var menuDelete: MenuItem? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): TestimonialAddEditFragment {
      val fragment = TestimonialAddEditFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.ic_menu_delete_new, menu)
    menuDelete = menu.findItem(R.id.id_delete)
    menuDelete?.isVisible = false
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.id_delete -> {
//        val bundle: Bundle = Bundle.EMPTY
//        startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_DETAILS_FRAGMENT, bundle, clearTop = false, isResult =false,requestCode = Constants.REQUEST_CODE)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }
}