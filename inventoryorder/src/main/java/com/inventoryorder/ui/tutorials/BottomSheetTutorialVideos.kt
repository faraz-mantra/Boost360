package com.inventoryorder.ui.tutorials

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.observeOnce
import com.inventoryorder.R
import com.inventoryorder.base.AppBaseFragment
import com.inventoryorder.databinding.BottomSheetTutorialsOnAppointmentMgmtBinding
import com.inventoryorder.databinding.FragmentAllTutorialsBinding
import com.inventoryorder.databinding.FragmentTutorialDescBinding
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.ui.tutorials.viewmodel.TutorialViewModel

class BottomSheetTutorialVideos : BaseBottomSheetDialog<BottomSheetTutorialsOnAppointmentMgmtBinding, TutorialViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_tutorials_on_appointment_mgmt
    }

    override fun getViewModelClass(): Class<TutorialViewModel> {
        return TutorialViewModel::class.java
    }

    override fun onCreateView() {
        val tutorialPagerAdapter = TutorialPagerAdapter(childFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewModel?.getTutorialsList()?.observeOnce(viewLifecycleOwner, {
            binding?.viewPagerTutorials?.adapter = tutorialPagerAdapter
            binding?.tabLayout?.setupWithViewPager(binding?.viewPagerTutorials)
        })

    }

}

class TutorialPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FragmentTutorialDesc.newInstance()
            1 -> FragmentAllTutorials.newInstance()
            else -> FragmentTutorialDesc.newInstance()
        }

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Tutorial contents"
            1 -> "All tutorials"
            else -> ""
        }
    }

}

class FragmentAllTutorials : AppBaseFragment<FragmentAllTutorialsBinding, TutorialViewModel>() {
    companion object {
        fun newInstance(): FragmentAllTutorials {
            return FragmentAllTutorials()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_all_tutorials
    }

    override fun getViewModelClass(): Class<TutorialViewModel> {
        return TutorialViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        viewModel?.getTutorialsList()?.observeOnce(viewLifecycleOwner, {
            binding?.rvVideos?.adapter = AppBaseRecyclerViewAdapter(baseActivity, it.contents?.allTutorials!!)
        })
    }

}

class FragmentTutorialDesc : AppBaseFragment<FragmentTutorialDescBinding, TutorialViewModel>() {
    companion object {
        fun newInstance(): FragmentTutorialDesc {
            return FragmentTutorialDesc()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_tutorial_desc
    }

    override fun getViewModelClass(): Class<TutorialViewModel> {
        return TutorialViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        binding?.ctvSteps?.text = "Step 1. Click on ‘Add A New Order’.\n" +
                "Step 2. Fill in the customer details.\n" +
                "Step 3. Select products from your product list.\n" +
                "Step 4. Review your order. Verify the payment options.\n" +
                "Step 5. Done."
    }

}