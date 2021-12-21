package dev.patrickgold.florisboard.customization.adapter;


import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;

import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener;
import dev.patrickgold.florisboard.customization.adapter.SharedAdapter;

public class NanoChipClass {


  public <T> NanoChipClass(Context context, RecyclerView recyclerView, ArrayList<T> list, OnItemClickListener listener) {
    SharedAdapter chipAdapter = new SharedAdapter(list, listener);
    FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
    layoutManager.setFlexDirection(FlexDirection.ROW);
    layoutManager.setJustifyContent(JustifyContent.FLEX_START);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(chipAdapter);
  }
}
