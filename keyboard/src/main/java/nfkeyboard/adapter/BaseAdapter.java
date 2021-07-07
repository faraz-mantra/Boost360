package nfkeyboard.adapter;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import nfkeyboard.interface_contracts.ItemClickListener;
import nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Admin on 23-02-2018.
 */

public abstract class BaseAdapter<T extends AllSuggestionModel> {
    final int leftSpace, topSpace, padding, paddingTop;
    final Context mContext;
    final DisplayMetrics metrics;
    LinearLayout.LayoutParams linLayoutParams;
    SharedPreferences preferences;
    private ItemClickListener mItemClickListener;

    BaseAdapter(Context context) {
        mContext = context;
        preferences = mContext.getApplicationContext().getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE);
        metrics = mContext.getResources().getDisplayMetrics();
        leftSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, metrics);
        topSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, metrics);
        padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, metrics);
        paddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 23, metrics);
        linLayoutParams = new LinearLayout.LayoutParams(metrics.widthPixels * 75 / 100,
                preferences.getInt("keyboard_height", (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, metrics)) - 2 * topSpace);
    }

    BaseAdapter(Context context, ItemClickListener listener) {
        this(context);
        preferences = mContext.getApplicationContext().getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE);
        mItemClickListener = listener;
    }

    public void setViewLayoutSize(View itemView) {
        linLayoutParams.setMargins(leftSpace, topSpace, leftSpace, topSpace);
        itemView.setLayoutParams(linLayoutParams);
        itemView.setPadding(padding, paddingTop, padding, padding);
    }

    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent);

    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, T suggestion);

    String onCopyClick(AllSuggestionModel model) {
        return mItemClickListener.onCopyClick(model);
    }

    void onItemClicked(AllSuggestionModel model) {
        mItemClickListener.onItemClick(model);
    }

    void unRegisterEventBus() {

    }
}
