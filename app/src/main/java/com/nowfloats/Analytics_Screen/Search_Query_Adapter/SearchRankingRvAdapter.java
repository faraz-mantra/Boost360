package com.nowfloats.Analytics_Screen.Search_Query_Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.SearchRankModel;
import com.thinksity.R;

import java.util.List;

/**
 * Created by NowFloats on 15-03-2017.
 */

public class SearchRankingRvAdapter extends RecyclerView.Adapter<SearchRankingRvAdapter.SearchRankingViewHolder> {

    private List<SearchRankModel> mSearchRankList;


    public SearchRankingRvAdapter(List<SearchRankModel> list){
        this.mSearchRankList = list;
    }

    @Override
    public SearchRankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType==0){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_query_item_bg, parent, false);
        }else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_query_item_layout, parent, false);
        }
        return new SearchRankingViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if((position % 2)==0){
            return 0;
        }
        return 1;
    }

    @Override
    public void onBindViewHolder(SearchRankingViewHolder holder, int position) {

        SearchRankModel data = mSearchRankList.get(position);
        if(null!=data) {
            holder.tvSearchQuery.setText(data.getKeyword());
            if (data.getOldRank() == -1) {
                holder.tvOldPage.setText("-");
            } else {
                holder.tvOldPage.setText(getPage(data.getOldRank()) + "");
            }
            if (data.getNewRank() == -1) {
                holder.tvNewPage.setText("-");
            } else {
                holder.tvNewPage.setText(getPage(data.getNewRank()) + "");
            }
        }
    }

    private int getPage(int rank){
        return ((rank-1)/10)+1;
    }

    @Override
    public int getItemCount() {
        return mSearchRankList.size();
    }

    class SearchRankingViewHolder extends RecyclerView.ViewHolder{

        TextView tvSearchQuery, tvOldPage, tvNewPage;

        public SearchRankingViewHolder(View itemView) {
            super(itemView);

            tvSearchQuery = (TextView) itemView.findViewById(R.id.tv_search_query);
            tvOldPage = (TextView) itemView.findViewById(R.id.tv_old_page_rank);
            tvNewPage = (TextView) itemView.findViewById(R.id.tv_new_rank);
        }
    }
}
