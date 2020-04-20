package com.boost.upgrades.ui.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.boost.upgrades.R
import com.boost.upgrades.adapter.ImagePreviewAdapter
import com.boost.upgrades.adapter.ZoomOutPageTransformer
import com.boost.upgrades.utils.HorizontalMarginItemDecoration
import kotlinx.android.synthetic.main.image_preview_popup.*

class ImagePreviewPopUpFragement : DialogFragment() {

    lateinit var root: View
    var list = ArrayList<String>()
    var currentPos = 0
    lateinit var imagePreviewAdapter: ImagePreviewAdapter
    var initialLoad = true

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.fullscreen_color)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.image_preview_popup, container, false)

        initialLoad = true

        currentPos = arguments!!.getInt("position")
        list = arguments!!.getStringArrayList("list") as ArrayList<String>
        imagePreviewAdapter = ImagePreviewAdapter(list)

        return root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeViewPager()
        preview_left.setOnClickListener {
            if (currentPos >= 1) {
                preview_pager.setCurrentItem(currentPos - 1)
            }
        }
        preview_right.setOnClickListener {
            if (currentPos < list.size) {
                preview_pager.setCurrentItem(currentPos + 1)
            }
        }
        image_preview_popup_outer_layout.setOnClickListener {
            dialog!!.dismiss()
        }
    }

    private fun initializeViewPager() {
        preview_pager.adapter = imagePreviewAdapter
        preview_pager.offscreenPageLimit = list.size
        preview_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(!initialLoad) {
                    currentPosition(position)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        preview_pager.postDelayed(Runnable {
            initialLoad = false
            preview_pager.setCurrentItem(currentPos)
            if(currentPos == 0){
                currentPosition(0)
            }
        }, 100)
    }

    fun currentPosition(pos: Int) {
        currentPos = pos
        preview_current_position.setText((pos + 1).toString() + "/" + list.size)
    }

}