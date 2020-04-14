package com.onboarding.nowfloats.bottomsheet.builder

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.bottomsheet.inerfaces.ContentBuilder
import com.onboarding.nowfloats.bottomsheet.util.listenToUpdate
import com.onboarding.nowfloats.bottomsheet.util.primaryColor
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.extensions.fadeOut
import kotlinx.android.synthetic.main.one_action_button.view.*

class OneActionBuilder(buttonText: String, private val autoDismiss: Boolean, private val fadDuration: Long = 0L,
                       private val onClick: OnClick?, private val onLongClick: OnClick?,
                       @ColorRes private val colorRes: Int?, @DrawableRes private val drwableId: Int?) : ContentBuilder() {

    var buttonText: String by listenToUpdate(buttonText, this)

    override val layoutRes: Int = R.layout.one_action_button

    lateinit var actionButton: Button
    override fun init(view: View) {
        actionButton = view.action_button
        val appC = actionButton.context.applicationContext
        (actionButton.parent as ViewGroup).fadeOut(fadDuration).doOnComplete {
            actionButton.text = buttonText
            if (drwableId == null) {
                if (colorRes != null) (actionButton.parent as ViewGroup).setBackgroundColor(ContextCompat.getColor(appC, colorRes))
                else appC.primaryColor?.also { (actionButton.parent as ViewGroup).setBackgroundColor(it) }
            } else (actionButton.parent as ViewGroup).background = ContextCompat.getDrawable(appC, drwableId)
        }.andThen((actionButton.parent as ViewGroup).fadeIn(100L)).subscribe()
        actionButton.setOnClickListener {
            onClick?.invoke(dialog)
            if (autoDismiss) dialog.dismiss()
        }
    }

    override fun updateContent(type: Int, data: Any?) {
    }
}

typealias OnClick = (dialog: BottomDialog) -> Unit