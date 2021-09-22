package nfkeyboard.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ViewGroup;


/**
 * Created by Admin on 02-04-2018.
 */

public class TextSuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

//    private ArrayList<KeywordModel> mSuggestionList = new ArrayList<>();
//    private Context mContext;
//    private CandidateViewItemClickListener mListener;
//
//    public TextSuggestionAdapter(Context context) {
//        mContext = context;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(nowfloats.nfkeyboard.R.layout.layout_dictionary_text_suggestions, parent, false);
//        return new TextSuggestionHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof TextSuggestionHolder) {
//            TextSuggestionHolder myHolder = (TextSuggestionHolder) holder;
//            myHolder.mTextView.setText(mSuggestionList.get(position).getWord());
//        }
//    }
//
//    public void addNewSuggestions(ArrayList<KeywordModel> list) {
//        mSuggestionList.clear();
//        if (list != null) {
//            mSuggestionList.addAll(list);
//        }
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getItemCount() {
//        return mSuggestionList.size();
//    }
//
//    public void setKeyboardItemClickListener(CandidateViewItemClickListener listener) {
//        this.mListener = listener;
//    }
//
//    public class TextSuggestionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        TextView mTextView;
//
//        public TextSuggestionHolder(View itemView) {
//            super(itemView);
//            mTextView = itemView.findViewById(nowfloats.nfkeyboard.R.id.tv_suggestion);
//            mTextView.setOnClickListener(this);
//
//        }
//
//        @Override
//        public void onClick(View v) {
//            mListener.onItemClick(mSuggestionList.get(getAdapterPosition()));
//        }
//    }
}
