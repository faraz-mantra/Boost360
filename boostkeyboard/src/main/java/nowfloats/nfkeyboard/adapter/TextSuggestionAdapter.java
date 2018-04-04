package nowfloats.nfkeyboard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Admin on 02-04-2018.
 */

public class TextSuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> mSuggestionList = new ArrayList<>();
    private Context mContext;

    public TextSuggestionAdapter(Context context){
        mContext = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1,parent,false);
        return new TextSuggestionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TextSuggestionHolder){
            TextSuggestionHolder myHolder = (TextSuggestionHolder) holder;
            myHolder.mTextView.setText(mSuggestionList.get(position));
        }
    }

    public void addNewSuggestions(ArrayList<String> list){
        mSuggestionList.clear();
        if (list != null){
            mSuggestionList.addAll(list);
        }
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mSuggestionList.size();
    }

    public class TextSuggestionHolder extends RecyclerView.ViewHolder{
        TextView mTextView;
        public TextSuggestionHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
