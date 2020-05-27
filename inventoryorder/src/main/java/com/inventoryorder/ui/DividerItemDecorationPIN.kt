package com.inventoryorder.ui

import android.R.attr.orientation
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class DividerItemDecorationPIN(
        private var mDivider: Drawable? = null,
        private var mShowFirstDivider: Boolean = false,
        private var mShowLastDivider: Boolean = false
) : RecyclerView.ItemDecoration() {

    private var mOrientation = -1

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (mDivider == null) {
            return
        }

//We do not want to add any padding for the first child
//Because we do not want to have any unwanted space above the Recycler view

        val position = parent?.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION || position == 0 && !mShowFirstDivider) {
            return
        }
        if (mOrientation == -1) getOrientation(parent)
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.top = mDivider!!.intrinsicHeight
            if (mShowLastDivider && position == state.itemCount - 1) {
                outRect.bottom = outRect.top
            }
        } else {
            outRect.left = mDivider!!.intrinsicWidth
            if (mShowLastDivider && position == state.itemCount - 1) {
                outRect.right = outRect.left
            }
        }

    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (mDivider == null) {
            super.onDrawOver(c, parent, state)
            return
        }
        val dividerLeft = 0
        val dividerRight = parent.width - 0
        var childCount: Int = parent.childCount
        val left = 0
        val right = 0
        val top = 0
        val bottom = 0
        var size: Int = 0

        for (i in 0 until parent.childCount) {

            if (i != parent.childCount) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                val dividerTop = child.bottom + params.bottomMargin
                val dividerBottom = dividerTop + mDivider!!.intrinsicHeight
                mDivider?.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                mDivider?.draw(c)
            } else {

            }
        }

        if (mShowLastDivider && childCount > 0) {
            val child = parent.getChildAt(childCount - 1)
            if (parent.getChildAdapterPosition(child) === state.itemCount - 1) {
                val params =
                        child.layoutParams as RecyclerView.LayoutParams
                if (orientation == LinearLayoutManager.VERTICAL) {
                    val top = child.bottom + params.bottomMargin
                    val bottom = top + size
                } else { // horizontal
                    val left = child.right + params.rightMargin
                    val right = left + size
                }
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(c)
            }
        }

    }

    private fun getOrientation(parent: RecyclerView): Int {
        if (mOrientation == -1) {
            mOrientation = if (parent.layoutManager is LinearLayoutManager) {
                val layoutManager =
                        parent.layoutManager as LinearLayoutManager?
                layoutManager!!.orientation
            } else {
                throw IllegalStateException(
                        "DividerItemDecoration can only be used with a LinearLayoutManager."
                )
            }
        }
        return mOrientation
    }
}
