package com.onboarding.nowfloats.bottomsheet.builder

import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.bottomsheet.inerfaces.ContentBuilder
import com.onboarding.nowfloats.bottomsheet.util.listenToUpdate
import kotlinx.android.synthetic.main.header_toolbar.view.*

fun BottomDialogBuilder.toolbar(action: ToolbarHeader.() -> Unit): BottomDialogBuilder {
    headerBuilder = ToolbarHeader("NULL").apply(action)
    return this
}

fun BottomDialog.updateToolbar(f: ToolbarHeader.() -> Unit) {
    (headerBuilder as ToolbarHeader).apply(f)
}

class ToolbarHeader(
        title: CharSequence? = null,
        var round: Boolean = false,
        var centerTitle: Boolean = false,
        var height: Int = 45
) : ContentBuilder() {

    var title by listenToUpdate(title, this, type = 1)

    var navIconId: Int? by listenToUpdate(null, this, type = 2)

    var onIconClick: OnClick? by listenToUpdate(null, this, type = 3)

    var menuRes: Int? by listenToUpdate(null, this, type = 4)

    var onMenuItemClick: ((MenuItem) -> Boolean)? = null

    override val layoutRes: Int = R.layout.header_toolbar

    lateinit var toolbar: Toolbar

    override fun init(view: View) {
        toolbar = view.tool_bar
        if (round) toolbar.setBackgroundResource(R.drawable.bg_rounded_top_white_five)
    }

    override fun updateContent(type: Int, data: Any?) {
        if (type >= -1) {
            toolbar.title = title
        }
        if (centerTitle) {

            val textView = Toolbar::class.java.getDeclaredField("mTitleTextView").run {
                isAccessible = true
                get(toolbar) as TextView
            }
            textView.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            textView.gravity = Gravity.CENTER_HORIZONTAL
        }

        if (type <= -1) {
            navIconId?.also {
                toolbar.setNavigationIcon(it)
            } ?: (toolbar.setNavigationIcon(null))
        }

        if (type == -1 || type == 3) {
            toolbar.setNavigationOnClickListener {
                onIconClick?.invoke(dialog)
            }
        }
        if (type == -1 || type == 4) {
            menuRes?.also {
                toolbar.inflateMenu(it)
            }
            toolbar.setOnMenuItemClickListener(onMenuItemClick)
        }
    }

}