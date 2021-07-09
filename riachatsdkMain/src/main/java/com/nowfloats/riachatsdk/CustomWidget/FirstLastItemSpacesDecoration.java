package com.nowfloats.riachatsdk.CustomWidget;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by admin on 8/2/2017.
 */

public class FirstLastItemSpacesDecoration extends RecyclerView.ItemDecoration {

    private final int directSpace;
    private final int reverseSpace;

    public FirstLastItemSpacesDecoration(int space, boolean layoutReversed) {
        if (layoutReversed) {
            directSpace = 0;
            reverseSpace = space;
        } else {
            directSpace = space;
            reverseSpace = 0;
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
            outRect.right = directSpace;
        }
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = directSpace;
        }
    }
}
