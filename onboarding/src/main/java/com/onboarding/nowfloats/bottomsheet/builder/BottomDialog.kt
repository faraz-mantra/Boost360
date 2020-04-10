package com.onboarding.nowfloats.bottomsheet.builder

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.View.NO_ID
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import com.framework.base.BaseActivity
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.bottomsheet.inerfaces.ContentBuilder
import kotlinx.android.synthetic.main.dialog_content.*


@Suppress("UNCHECKED_CAST", "unused")
class BottomDialog internal constructor(
        builder: BottomDialogBuilder
) : Dialog(builder.context, builder.themeId) {

    companion object {
        fun builder(
                activity: BaseActivity<*, *>,
                show: Boolean = true,
                action: BottomDialogBuilder.() -> Unit
        ): BottomDialog {
            val b = BottomDialogBuilder(activity).apply(action)
            return BottomDialog(b).also {
                if (show) {
                    it.show()
                }
            }
        }
    }

    internal val headerElevation = builder.headerElevation

    val activity: Activity = builder.context

    private val expand: Boolean = builder.expand
    private val expandable: Boolean = builder.expandable

    var headerBuilder: ContentBuilder? = builder.headerBuilder
        private set

    var contentBuilder: ContentBuilder? = builder.contentBuilder
        private set

    var footerBuilder: ContentBuilder? = builder.footerBuilder
        private set

    var immersionStatusBar = false

    private var peekHeight: Int = builder.peekHeight

    val headerView: ViewGroup get() = findViewById(R.id.header_container)
    val footerView: ViewGroup get() = findViewById(R.id.footer_content)
    val footerContainer: ViewGroup get() = findViewById(R.id.footer_container)
    val contentView: ViewGroup get() = findViewById(R.id.content)

    private lateinit var behaviorController: BehaviorController

    private var mCancelable = builder.mCancelable

    var navColor: Int? = builder.navBgColor

    private val onDismiss: (() -> Unit)? = builder.onDismiss

    private var bottomHeight = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_content)
        buildContent()
    }

    val stateBarHeight: Int
        get() {
            var result = 0
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    lateinit var statusCallbacks: MutableList<StatusCallback>

    private var firstShow = true

    private val lis: StatusCallback = object : StatusCallback {
        override fun onSlide(slideOffset: Float) {
            if (this@BottomDialog::statusCallbacks.isInitialized) {
                statusCallbacks.forEach { it.onSlide(slideOffset) }
            }
            footerBuilder ?: return
            if (slideOffset < 0) {
                footerContainer.scrollY = (slideOffset * bottomHeight).toInt()
            } else {
                footerContainer.scrollY = 0
            }
        }

        override fun onExpand() {
            if (expand && firstShow) {
                firstShow = false
                headerBuilder?.onAfterShow()
                contentBuilder?.onAfterShow()
                footerBuilder?.onAfterShow()
            }
            if (this@BottomDialog::statusCallbacks.isInitialized) {
                statusCallbacks.forEach { it.onExpand() }
            }
        }

        override fun onCollapsed() {
            if (!expand && firstShow) {
                firstShow = false
                headerBuilder?.onAfterShow()
                contentBuilder?.onAfterShow()
                footerBuilder?.onAfterShow()
            }
            if (this@BottomDialog::statusCallbacks.isInitialized) {
                statusCallbacks.forEach { it.onCollapsed() }
            }
        }

        override fun onHidden() {
            if (this@BottomDialog::statusCallbacks.isInitialized) {
                statusCallbacks.forEach { it.onHidden() }
            }
            sDismiss()
        }
    }

    private fun sDismiss() {
        super.dismiss()
        if (::statusCallbacks.isInitialized) {
            statusCallbacks.clear()
        }
    }


    @Synchronized
    fun listenStatus(lis: StatusCallback) {
        if (!::statusCallbacks.isInitialized) {
            statusCallbacks = mutableListOf()
        }
        statusCallbacks.add(lis)
    }

    override fun setCancelable(flag: Boolean) {
        super.setCancelable(flag)
        behaviorController.isHideable = flag
    }

    override fun dismiss() {
        behaviorController.hide()
    }

    val bsView get() = findViewById<View>(R.id.bs_root)

    private fun buildContent() {
        behaviorController = BehaviorController(bsView, lis)
        behaviorController.hide()
        behaviorController.peekHeight = peekHeight

        val rootView = findViewById<ViewGroup>(R.id.root)

        if (!expandable) {
            if (peekHeight > 0) {
                bsView.layoutParams.also {
                    it.height = peekHeight
                    bsView.layoutParams = it
                }
            } else {
                throw Exception("expandable must set peekHeight")
            }

        }


        rootView.setOnClickListener {
            if (mCancelable) cancel()
        }

        headerBuilder?.apply {
            headerView.addView(build(context, this@BottomDialog))
        }
        contentBuilder?.also {
            contentView.addView(it.build(context, this))
        }
        val navHeight = getNavigationBarHeight()
        footerBuilder?.also {
            footerView.addView(it.build(context, this).apply {
                post {
                    bottomHeight = footerContainer.height + navHeight
                    setContentMarginBottom(bottom + navHeight)
                }
            })
        } ?: {
            footerView.visibility = View.GONE
            setContentMarginBottom(navHeight)
        }.invoke()

        findViewById<View>(R.id.fill_nav)?.also {
            navColor?.also { c ->
                it.setBackgroundColor(c)
            }
            it.layoutParams = it.layoutParams.apply {
                height = navHeight
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        onDismiss?.also {
            setOnDismissListener { it() }
        }

        val tag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        rootView.systemUiVisibility = tag

        val sf = findViewById<View>(R.id.statusbar_fill)
        if (immersionStatusBar) {
            sf.visibility = View.GONE
        } else {
            sf.layoutParams = sf.layoutParams.also { it.height = stateBarHeight }
        }
        shadowListener()
    }

    private fun shadowListener() {
        showFooterElevation = footerBuilder != null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            container.setOnScrollChangeListener { nv: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
                if (headerElevation && headerBuilder != null) {
                    if (scrollY == 0) {
                        if (showAppBarElevation) {
                            showAppBarElevation = false
                        }
                    } else if (!showAppBarElevation) {
                        showAppBarElevation = true
                    }
                }

                if (headerElevation && footerBuilder != null) {
                    if (contentView.height == scrollY + nv.height) {
                        showFooterElevation = false
                    } else {
                        if (!showFooterElevation) {
                            showFooterElevation = true
                        }
                    }
                }
            }
        }
    }

    private val container get() = this.findViewById<NestedScrollView>(R.id.container)

    private fun setContentMarginBottom(value: Int) {
        container.layoutParams = (container.layoutParams as ViewGroup.MarginLayoutParams).also { p ->
            p.setMargins(0, 0, 0, value)
        }

    }

    var showAppBarElevation: Boolean
        get() = appbar_elevation.visibility == View.VISIBLE
        set(value) {
            appbar_elevation.visibility = if (value) View.VISIBLE else View.GONE
        }

    var showFooterElevation: Boolean
        get() = footer_elevation.visibility == View.VISIBLE
        set(value) {
            footer_elevation.visibility = if (value) View.VISIBLE else View.GONE
        }

    private fun getNavigationBarHeight(): Int {
//        val resources = context.resources
//        return if (checkNavigationBarShow(activity.window)) {
//            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
//            resources.getDimensionPixelSize(resourceId)
//        } else 0
        return 0
    }

    fun <T> get(action: BottomDialog.() -> T): T {
        return with(this, action)
    }

    @SuppressLint("ObsoleteSdkInt")
    fun checkHasNavigationBar(activity: Activity): Boolean {
        val NAVIGATION = "navigationBarBackground"
        val vp = activity.window.decorView as ViewGroup?
        return if (vp != null) {
            (0 until vp.childCount).map { vp.getChildAt(it) }.any {
                it.id != NO_ID && NAVIGATION == activity.resources.getResourceEntryName(it.id)
            }
        } else false

    }

    private fun checkNavigationBarShow(window: Window): Boolean {
        val display: Display = window.windowManager.defaultDisplay
        val point = Point()
        display.getRealSize(point)
        val decorView: View = window.decorView
        val rect = Rect()
        decorView.getWindowVisibleDisplayFrame(rect)
        return rect.bottom != point.y
    }

    override fun show() {
        super.show()
        window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            window?.attributes = this
        }
        window?.decorView?.apply {
            setPadding(0, 0, 0, 0)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.navigationBarColor = ResourcesCompat.getColor(context.resources, R.color.colorAccent, context.theme)
        }

        Handler().postDelayed({ showInternal() }, 1)
    }

    private fun showInternal() {
        if (expand) behaviorController.expand()
        else behaviorController.collapsed()

        behaviorController.isHideable = mCancelable
    }

    fun <T : ContentBuilder> updateHeader(f: T.() -> Unit) {
        (headerBuilder as T).apply(f)
    }

    fun <T : ContentBuilder> updateContent(f: T.() -> Unit) {
        f.invoke(contentBuilder as T)
    }

    fun <T : ContentBuilder> updateFooter(f: T.() -> Unit) {
        f.invoke(footerBuilder as T)
    }


    fun expand() {
        behaviorController.expand()
    }

    fun halfExpand() {
        behaviorController.collapsed()
    }
}
