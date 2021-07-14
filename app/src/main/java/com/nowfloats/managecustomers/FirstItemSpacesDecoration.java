package com.nowfloats.riachatsdk.CustomWidget;

import android.graphics.Rect;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

/**
 * Created by admin on 8/2/2017.
 */

public class FirstItemSpacesDecoration extends RecyclerView.ItemDecoration {

    private final int directSpace;
    private final int reverseSpace;
    private final int rightSpace;

    public FirstItemSpacesDecoration(int leftSpace, int rightSpace, boolean layoutReversed) {
        if (layoutReversed) {
            directSpace = 0;
            reverseSpace = leftSpace;
        } else {
            directSpace = leftSpace;
            reverseSpace = 0;
        }
        this.rightSpace = rightSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
            outRect.right = rightSpace;
        }
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = directSpace;
        }
    }
}
