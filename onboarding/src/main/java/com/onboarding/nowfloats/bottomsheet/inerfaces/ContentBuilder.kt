package com.onboarding.nowfloats.bottomsheet.inerfaces

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.onboarding.nowfloats.bottomsheet.builder.BottomDialog
import com.onboarding.nowfloats.bottomsheet.builder.StatusCallback

abstract class ContentBuilder {

    lateinit var contentView: View

    abstract val layoutRes: Int
    lateinit var dialog: BottomDialog


    abstract fun init(view: View)

    private var afterShow: (() -> Unit)? = null

    fun afterShow(action: () -> Unit) {
        afterShow = action
    }

    fun build(context: Context, dialog: BottomDialog): View {
        this.dialog = dialog
        try {
            contentView
        } catch (e: Exception) {
            contentView = LayoutInflater.from(context).inflate(layoutRes, null, false)
            init(contentView)
            updateContent(-1)
            afterShow?.invoke()
        }

        return contentView
    }

    open fun onAfterShow() {}

    fun listenStatus(lis: StatusCallback) {
        dialog.listenStatus(lis)
    }

    abstract fun updateContent(type: Int, data: Any? = null)

}