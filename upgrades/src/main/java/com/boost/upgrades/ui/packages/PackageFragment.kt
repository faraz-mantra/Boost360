package com.boost.upgrades.ui.packages

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.PackageAdaptor
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.package_fragment.*

class PackageFragment : BaseFragment() {

    lateinit var root: View

    lateinit var packageAdaptor: PackageAdaptor

    companion object {
        fun newInstance() = PackageFragment()
    }

    private lateinit var viewModel: PackageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.package_fragment, container, false)

        packageAdaptor = PackageAdaptor(ArrayList())

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PackageViewModel::class.java)

        spannableString()
        initializeRecycler()

        Glide.with(this).load(R.drawable.back_beau)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .into(back_image)

        package_back.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }

        package_submit.setOnClickListener {

        }
    }

    fun spannableString() {
        val origCost = SpannableString("₹2,559/month")

        origCost.setSpan(
            StrikethroughSpan(),
            0,
            origCost.length,
            0
        )
        orig_cost.setText(origCost)
    }

    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        package_addons_recycler.apply {
            layoutManager = gridLayoutManager
        }
        package_addons_recycler.adapter = packageAdaptor
    }

}
