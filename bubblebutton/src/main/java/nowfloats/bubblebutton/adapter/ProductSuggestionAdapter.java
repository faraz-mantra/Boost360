package nowfloats.bubblebutton.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nowfloats.bubblebutton.R;

/**
 * Created by Admin on 13-04-2017.
 */

public class ProductSuggestionAdapter extends RecyclerView.Adapter {

    private Context mContext;
    public ProductSuggestionAdapter(Context context){
        mContext = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_product_suggestion_item,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    private class MyHolder extends RecyclerView.ViewHolder{

        MyHolder(View itemView) {
            super(itemView);
        }
    }
}
