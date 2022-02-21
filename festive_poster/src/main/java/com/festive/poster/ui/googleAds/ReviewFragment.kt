package com.festive.poster.ui.googleAds

import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.databinding.FragmentReviewBinding
import com.festive.poster.viewmodels.ReviewFragmentViewModel

class ReviewFragment : AppBaseFragment<FragmentReviewBinding, ReviewFragmentViewModel>() {


  companion object {
    @JvmStatic
    fun newInstance(): ReviewFragment {
      return ReviewFragment()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_review
  }


  override fun onCreateView() {
    super.onCreateView()

    binding?.CustomiseAdd?.setOnClickListener {

      val businessDescDialog = BusinessDescriptionBottomSheet()
      businessDescDialog.show(parentFragmentManager, BusinessDescriptionBottomSheet::javaClass.name)

    }
  }

  override fun getViewModelClass(): Class<ReviewFragmentViewModel> {
    return ReviewFragmentViewModel::class.java
  }


}

//  fun showShimmerAnimation(){
//    binding?.shimmerLayout?.visible()
//    binding?.rvPosters?.gone()
//  }
//
//  fun hideShimmerAnimation(){
//    binding?.shimmerLayout?.gone()
//    binding?.rvPosters?.visible()
//  }
//  override fun onResume() {
//    super.onResume()
//    binding?.shimmerLayout?.startShimmer()
//
//  }
//
//  override fun onPause() {
//    super.onPause()
//    binding?.shimmerLayout?.stopShimmer()
//  }
