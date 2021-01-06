package com.appservice.staffs.ui.home

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.StaffHomeActivityBinding
import com.framework.models.BaseViewModel

class StaffHomeFragment : AppBaseFragment<StaffHomeActivityBinding, BaseViewModel>() {
    private var mViewModel: StaffHomeViewModel? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(StaffHomeViewModel::class.java)
    }

    companion object {
        fun newInstance(): StaffHomeFragment {
            return StaffHomeFragment()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_staff_home
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_add -> {
                val bundle: Bundle = Bundle.EMPTY
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_ADD_FRAGMENT, bundle, clearTop = false, isResult = false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}