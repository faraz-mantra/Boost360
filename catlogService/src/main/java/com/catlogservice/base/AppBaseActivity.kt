package com.catlogservice.base

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import com.catlogservice.R
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel


abstract class AppBaseActivity<Binding : ViewDataBinding, ViewModel : BaseViewModel> : BaseActivity<Binding, ViewModel>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView() {

    }

    override fun getToolbarTitleTypeface(): Typeface? {
        return ResourcesCompat.getFont(this, R.font.semi_bold)
    }

    override fun getToolbarTitleSize(): Float? {
        return resources.getDimension(R.dimen.heading_5)
    }

    override fun getNavIconScale(): Float {
        return 0.75f
    }

    override fun getToolbarBackgroundColor(): Int? {
        return Color.parseColor("#747474")
    }

    override fun getToolbarTitleColor(): Int? {
        return Color.parseColor("#FFFFFF")
    }

    override fun getToolbarTitleGravity(): Int {
        return Gravity.CENTER
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
