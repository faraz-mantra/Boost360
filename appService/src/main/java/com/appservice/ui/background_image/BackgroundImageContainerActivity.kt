package com.appservice.ui.background_image

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.constant.FragmentType
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.views.customViews.CustomToolbar
import java.util.*

open class BackgroundImageContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

    private var type: FragmentType? = null

    override fun getLayout(): Int {
        return com.framework.R.layout.activity_fragment_container
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        intent?.extras?.getInt(FRAGMENT_TYPE)?.let { type = FragmentType.values()[it] }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView() {
        super.onCreateView()
        setFragment()
    }

    override fun customTheme(): Int? {
        return when (type) {
            else -> return super.customTheme()
        }
    }

    override fun getToolbar(): CustomToolbar? {
        return binding?.appBarLayout?.toolbar
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.help_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun getToolbarBackgroundColor(): Int? {
        return when (type) {
            FragmentType.BACKGROUND_IMAGE_FRAGMENT -> ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
            )
            FragmentType.BACKGROUND_IMAGE_CROP_FRAGMENT -> ContextCompat.getColor(
                this,
                R.color.black_4a4a4a
            )
            else -> super.getToolbarBackgroundColor()
        }
    }

    override fun getToolbarTitleColor(): Int? {
        return when (type) {
            FragmentType.BACKGROUND_IMAGE_FRAGMENT -> ContextCompat.getColor(
                    this,
                    R.color.white
            )
            else -> super.getToolbarTitleColor()
        }
    }

    override fun isHideToolbar(): Boolean {
        return when (type) {
            FragmentType.BACKGROUND_IMAGE_F_SCREEN_FRAGMENT -> true
            else -> super.isHideToolbar()
        }
    }

    override fun getNavigationIcon(): Drawable? {
        return when (type) {
            FragmentType.BACKGROUND_IMAGE_FRAGMENT -> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_new)
            FragmentType.BACKGROUND_IMAGE_CROP_FRAGMENT ->
                ContextCompat.getDrawable(this,
                    R.drawable.ic_cross_white)

            FragmentType.BACKGROUND_IMAGE_PREVIEW ->
                ContextCompat.getDrawable(this,
                    R.drawable.ic_cross_white)
            else -> super.getNavigationIcon()
        }
    }

    override fun getToolbarTitle(): String? {
        return when (type) {
            FragmentType.BACKGROUND_IMAGE_FRAGMENT -> getString(R.string.background_image_title)
            FragmentType.BACKGROUND_IMAGE_CROP_FRAGMENT -> getString(R.string.crop_background_image)
            FragmentType.BACKGROUND_IMAGE_PREVIEW ->getString(R.string.preview_picture)
            else -> super.getToolbarTitle()
        }
    }

    private fun shouldAddToBackStack(): Boolean {
        return when (type) {
            else -> false
        }
    }

    private fun setFragment() {
        val fragment = getFragmentInstance(type)
        fragment?.arguments = intent.extras
        binding?.container?.id?.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
    }

    private fun getFragmentInstance(type: FragmentType?): BaseFragment<*, *>? {
        return when (type) {
            FragmentType.BACKGROUND_IMAGE_FRAGMENT -> BackgroundImageFragment.newInstance()
            FragmentType.BACKGROUND_IMAGE_CROP_FRAGMENT -> BGImageCropFragment.newInstance()
            FragmentType.BACKGROUND_IMAGE_PREVIEW -> BGImagePreviewFragment.newInstance()
            FragmentType.BACKGROUND_IMAGE_F_SCREEN_FRAGMENT -> BGImageFullScreenFragment.newInstance()
            else -> BackgroundImageFragment()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

}

fun Fragment.startBackgroundActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false, isResult: Boolean = false) {
    val intent = Intent(activity, BackgroundImageContainerActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentTypeNew(type)
    if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}


fun Intent.setFragmentTypeNew(type: FragmentType): Intent {
    return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}


fun getLatestUpdatesTaxonomyFromServiceCode(category_code: String?): String? {
    return when (category_code) {
        "DOC", "HOS" -> "Latest Updates & Health Tips"
        "SPA", "SAL" -> "Latest Updates & Offers"
        "HOT" -> "Latest Updates, News & Events"
        "MFG" -> "Latest News & Update"
        "CAF", "EDU" -> "Latest Updates & Tips"
        else -> "Latest Updates"
    }
}

fun isService(category_code: String?): Boolean {
    return when (category_code) {
        "SVC", "DOC", "HOS", "SPA", "SAL" -> true
        else -> false
    }
}

