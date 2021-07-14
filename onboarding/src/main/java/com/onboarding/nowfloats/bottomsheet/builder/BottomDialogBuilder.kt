@file:Suppress("unused")

package com.onboarding.nowfloats.bottomsheet.builder

import SectionsFeature
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.framework.base.BaseActivity
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.bottomsheet.*
import com.onboarding.nowfloats.bottomsheet.inerfaces.ContentBuilder
import com.onboarding.nowfloats.bottomsheet.util.ObservableList
import com.onboarding.nowfloats.bottomsheet.util.isDarkMode
import com.onboarding.nowfloats.bottomsheet.util.primaryColor
import com.onboarding.nowfloats.model.channel.ChannelModel

open class BottomDialogBuilder(var context: BaseActivity<*, *>) {
  companion object {
    var enableAutoDarkTheme: Boolean = false
    var darkTheme: Int = 0
  }

  var themeId: Int = if (enableAutoDarkTheme && context.isDarkMode)
    darkTheme.let {
      require(it != 0) { "if enableAutoDarkTheme == true please set the value of darkTheme" }
      it
    }
  else R.style.BottomDialog

  @Suppress("SetterBackingFieldAssignment")
  var peekHeightProportion: Float = 0.0f
    set(value) {
      val out = DisplayMetrics()
      val ws = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
      ws.defaultDisplay.getMetrics(out)
      peekHeight = (out.heightPixels * value).toInt()
    }
  var peekHeight: Int = -1

  init {
    val isLandscape =
      context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (isLandscape) {
      peekHeightProportion = 0.6f
    }
  }


  internal var headerBuilder: ContentBuilder? = null

  internal var onDismiss: (() -> Unit)? = null


  var headerElevation = true

  internal var contentBuilder: ContentBuilder? = null

  internal var footerBuilder: ContentBuilder? = null

  var expand: Boolean = false

  var expandable: Boolean = true

  var mCancelable = true

  var backgroundColor: Int = Color.parseColor("#FDFDFE")


  @ColorInt
  var navBgColor: Int? = context.primaryColor

  fun peekHeight(peekHeight: Int) {
    this.peekHeight = peekHeight
  }

  fun cancelable(flag: Boolean) {
    mCancelable = flag
  }

  fun expand() {
    expand = true
  }

  fun halfExpand() {
    expand = false
  }

  fun theme(tId: Int) {
    themeId = tId
  }

  fun onDismiss(l: () -> Unit) {
    onDismiss = l
  }

  fun <T : ContentBuilder> header(
    headerBuilder: T,
    action: (T.() -> Unit)? = null
  ): BottomDialogBuilder {
    action?.also { it.invoke(headerBuilder) }
    this.headerBuilder = headerBuilder
    return this
  }

  fun <T : ContentBuilder> content(
    contentBuilder: T,
    action: (T.() -> Unit)? = null
  ): BottomDialogBuilder {
    action?.also { it.invoke(contentBuilder) }
    this.contentBuilder = contentBuilder
    return this
  }

  fun <T : ContentBuilder> footer(
    footerBuilder: T,
    action: (T.() -> Unit)? = null
  ): BottomDialogBuilder {
    action?.also { it.invoke(footerBuilder) }
    this.footerBuilder = footerBuilder
    return this
  }

}


fun BottomDialogBuilder.oneButton(
  text: String, @ColorRes colorId: Int? = null,
  @DrawableRes drwableId: Int? = null, autoDismiss: Boolean = true,
  fadDuration: Long = 0L, listener: (ClickListenerSetter.() -> Unit)? = null
): BottomDialogBuilder {
  val cbSetter = ClickListenerSetter()
  listener?.invoke(cbSetter)
  footerBuilder = OneActionBuilder(
    text,
    autoDismiss,
    fadDuration,
    cbSetter._onClick,
    cbSetter._onLongClick,
    colorId,
    drwableId
  )
  return this
}

@Suppress("PropertyName", "unused")
class ClickListenerSetter {
  internal var _onClick: OnClick? = null
  internal var _onLongClick: OnClick? = null

  fun onClick(onClick: OnClick) {
    _onClick = onClick
  }

  fun onLongClick(onLongClick: OnClick) {
    _onLongClick = onLongClick
  }
}


fun BottomDialogBuilder.message(text: String, selectable: Boolean = false): BottomDialogBuilder {
  contentBuilder = MessageContentBuilder(text, selectable)
  return this
}

fun BottomDialogBuilder.imagePicker(text: String, onClick: onClickItem): BottomDialogBuilder {
  contentBuilder = ImagePickerBuilder(text, onClick)
  return this
}

fun BottomDialogBuilder.channelMutableList(
  items: ObservableList<ChannelModel>, autoDismiss: Boolean = true,
  onItemClick: OnItemClick<ChannelModel>
): BottomDialogBuilder {
  contentBuilder = ChannelContentBuilder(context, items, autoDismiss, onItemClick)
  return this
}

fun BottomDialogBuilder.featureMutableList(
  data: SectionsFeature,
  autoDismiss: Boolean = true
): BottomDialogBuilder {
  contentBuilder = FeatureContentBuilder(context, data, autoDismiss)
  return this
}


fun BottomDialogBuilder.title(
  title: CharSequence?,
  round: Boolean = false,
  centerTitle: Boolean = false,
  height: Int = 45
): BottomDialogBuilder {
  if (headerBuilder == null) {
    headerBuilder = ToolbarHeader(title, round, centerTitle, height)
  } else {
    (headerBuilder as ToolbarHeader).title = title
  }
  return this
}

fun BottomDialogBuilder.menu(menuResId: Int): BottomDialogBuilder {
  if (headerBuilder == null) {
    headerBuilder = ToolbarHeader()
  }
  if (headerBuilder is ToolbarHeader) {
    (headerBuilder as ToolbarHeader).apply {
      toolbar.inflateMenu(menuResId)
    }
  } else {
    throw RuntimeException("headerBuilder ToolbarHeader")
  }
  return this
}

fun BottomDialogBuilder.ensureHeaderIsToolbar() {

  if (headerBuilder == null) {
    headerBuilder = ToolbarHeader()
  }
  if (headerBuilder !is ToolbarHeader) {
    throw RuntimeException("headerBuilder ToolbarHeader")
  }
}


fun BottomDialogBuilder.inflateMenu(menuResId: Int, onClick: (MenuItem) -> Boolean) {
  ensureHeaderIsToolbar()
  (headerBuilder as ToolbarHeader).apply {
    menuRes = menuResId
    onMenuItemClick = onClick
  }
}

fun BottomDialogBuilder.withCloseIcon() {
  inflateMenu(R.menu.menu_close_icon) {
    headerBuilder?.dialog?.dismiss()
    true
  }
}