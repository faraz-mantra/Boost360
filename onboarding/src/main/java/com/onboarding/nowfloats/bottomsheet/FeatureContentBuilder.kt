package com.onboarding.nowfloats.bottomsheet

import DetailsFeature
import SectionsFeature
import android.view.View
import com.facebook.shimmer.ShimmerFrameLayout
import com.framework.base.BaseActivity
import com.framework.views.viewgroups.BaseRecyclerView
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.bottomsheet.inerfaces.ContentBuilder
import com.onboarding.nowfloats.bottomsheet.util.listenToUpdate
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import kotlinx.android.synthetic.main.feature_content_sheet.view.*
import java.util.*
import kotlin.concurrent.schedule

class FeatureContentBuilder(
  var context: BaseActivity<*, *>,
  data: SectionsFeature, autoDismiss: Boolean = true
) : ContentBuilder(), RecyclerItemClickListener {

  val feature: SectionsFeature by listenToUpdate(data, this)
  var adapter: AppBaseRecyclerViewAdapter<DetailsFeature>? = null


  private lateinit var recyclerView: BaseRecyclerView
  private lateinit var parentShimmerLayout: ShimmerFrameLayout
  override val layoutRes: Int
    get() = R.layout.feature_content_sheet


  override fun init(view: View) {
    recyclerView = view.recyclerView
    parentShimmerLayout = view.parentShimmerLayout
    Timer().schedule(100) { recyclerView.post { setShimmerView() } }
  }

  private fun setShimmerView() {
    feature.let { it1 ->
      parentShimmerLayout.post {
        parentShimmerLayout.fadeIn(1000L).andThen {
          parentShimmerLayout.visibility = View.VISIBLE
          parentShimmerLayout.showShimmer(true)
          if ((it1.details != null && it1.details.isNotEmpty()).not()) {
            parentShimmerLayout.visibility = View.GONE
          } else setChannelAdapter(it1.details!!)
        }?.subscribe()
      }
    }
  }

  private fun setChannelAdapter(details: ArrayList<DetailsFeature>) {
    Timer().schedule(800) {
      parentShimmerLayout.post {
        parentShimmerLayout.visibility = View.GONE
        parentShimmerLayout.hideShimmer()
        recyclerView.visibility = View.VISIBLE
        adapter = AppBaseRecyclerViewAdapter(context, details)
        recyclerView.adapter = adapter
      }
    }
  }

  override fun updateContent(type: Int, data: Any?) {
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
  }
}
