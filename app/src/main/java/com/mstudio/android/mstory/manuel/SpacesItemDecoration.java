package com.mstudio.android.mstory.manuel;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int mSpace;

    public SpacesItemDecoration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = 15;
        outRect.right = 15;
        outRect.bottom = 15;
        outRect.top = 15;
        // Add top margin only for the first item to avoid double space between items
        //if (parent.getChildAdapterPosition(view) == 0)

    }
}




  // if (parent.getChildAdapterPosition(view) == 0)
       //    outRect.top = mSpace;
       //    }