package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.databinding.FragmentSetupMyWebsiteBinding
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel

class SetupMyWebsiteFragment : AppBaseFragment<FragmentSetupMyWebsiteBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): SetupMyWebsiteFragment {
            val fragment = SetupMyWebsiteFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_setup_my_website
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        initUI()
    }

    private fun initUI() {
        setOnClickListeners()
        setUpStepUI()
        setupStepOneData()
    }

    private fun setupStepOneData() {
    }

    private fun setOnClickListeners() {
        binding?.layoutStep1?.tvNextStep1?.setOnClickListener {
            setUpStepUI(1)
        }

        binding?.layoutStep2?.tvNextStep2?.setOnClickListener {
            setUpStepUI(2)
        }

        binding?.layoutStep3?.tvNextStep3?.setOnClickListener {
            startFragmentFromNewOnBoardingActivity(
                activity = requireActivity(),
                type = FragmentType.LOADING_ANIMATION_DASHBOARD_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
        }

        binding?.layoutStep2?.edInputBusinessName?.afterTextChanged {
            binding?.layoutStep2?.tvNextStep2?.isEnabled = it.isEmpty().not()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_help_on_boarding_new, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_help_onboard -> {
                showShortToast(getString(R.string.coming_soon))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpStepUI(stepNumber: Int = 0) {
        when (stepNumber) {
            0 -> {
                binding?.layoutStep1?.root?.visible()
                binding?.layoutStep2?.root?.gone()
                binding?.layoutStep3?.root?.gone()
                binding?.ivStep1Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
                binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.gray_33727D82)
                binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_gray_hollow)
                binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.gray_33727D82)
                binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_gray_hollow)
            }
            1 -> {
                binding?.layoutStep1?.root?.gone()
                binding?.layoutStep2?.root?.visible()
                binding?.layoutStep3?.root?.gone()
                binding?.ivStep1Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
                binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.green_61CF96)
                binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
                binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.gray_33727D82)
                binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_gray_hollow)
            }
            2 -> {
                binding?.layoutStep1?.root?.gone()
                binding?.layoutStep2?.root?.gone()
                binding?.layoutStep3?.root?.visible()
                binding?.ivStep1Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
                binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.green_61CF96)
                binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
                binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.green_61CF96)
                binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
            }
        }
    }
}