package nowfloats.nfkeyboard.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

import nowfloats.nfkeyboard.interface_contracts.ItemClickListener;
import nowfloats.nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Admin on 23-02-2018.
 */

public class BaseAdapterManager {
    public enum SectionTypeEnum {

        ImageAndText(0), Text(1), Product(2),EmptyList(3), Login(4);

        private final int val;

        private SectionTypeEnum(int val) {
            this.val = val;
        }

        public int getValue() {
            return val;
        }
    }

    private ArrayList<BaseAdapter<AllSuggestionModel>> adapterList;
    public BaseAdapterManager(Context context, ItemClickListener listener) {
        adapterList = new ArrayList<>();
        makeSectionAdapterList(context, listener);
    }


    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        // may be we add adapter in hashmap here if position is not present
        return adapterList.get(position).onCreateViewHolder(parent);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder holder, AllSuggestionModel node, int position) {
        adapterList.get(position).onBindViewHolder(holder, node);
    }

    private void makeSectionAdapterList(Context context, ItemClickListener listener){
        adapterList.add(new ImageAdapter(context, listener));
        adapterList.add(new TextAdapter(context, listener));
        adapterList.add(new ProductAdapter(context, listener));
        adapterList.add(new EmptyListAdapter(context, listener));
        adapterList.add(new LoginAdapter(context, listener));
    }
}
