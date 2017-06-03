package com.nowfloats.swipecard.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nowfloats.swipecard.CallToActionFragment;
import com.nowfloats.swipecard.models.SuggestionsDO;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 5/31/2017.
 */

public class CallToActionAdapter extends RecyclerView.Adapter<CallToActionAdapter.ActionViewHolder> {


    private ArrayList<SuggestionsDO> arrSuggestions;
    private CallToActionFragment callToActionFragment;

    public CallToActionAdapter(CallToActionFragment callToActionFragment) {
        this.callToActionFragment = callToActionFragment;
    }

    @Override
    public ActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.csp_fragment_cta_list_item, parent, false);
        ActionViewHolder actionViewHolder = new ActionViewHolder(view);
        return actionViewHolder;
    }

    public void refreshList(List<SuggestionsDO> arrSuggestions) {
        this.arrSuggestions = (ArrayList<SuggestionsDO>) arrSuggestions;
    }

    @Override
    public void onBindViewHolder(final ActionViewHolder holder, int position) {

        final SuggestionsDO suggestionsDO = arrSuggestions.get(position);

        holder.tvSource.setText(suggestionsDO.getSource());
        holder.tvActualMessage.setText("'" + suggestionsDO.getActualMessage() + "'");
        holder.tvDate.setText(Methods.getFormattedDate(suggestionsDO.getDate()));
        holder.tvValue.setText(suggestionsDO.getValue());

        if (!suggestionsDO.isExpandGroup()) {

            SpannableString content = new SpannableString(callToActionFragment.getActivity().getString(R.string.view_details));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            holder.tvCTA.setText(content);

            holder.llMessage.setVisibility(View.GONE);
            holder.llShare.setVisibility(View.GONE);
        } else {

            SpannableString content = new SpannableString(callToActionFragment.getActivity().getString(R.string.cta));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            holder.tvCTA.setText(content);

            holder.llMessage.setVisibility(View.VISIBLE);
            holder.llShare.setVisibility(View.GONE);
//            setShareLayouts(suggestionsDO.isShareEnabled(), holder);
        }

        holder.tvCTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.tvCTA.getText().toString()
                        .equalsIgnoreCase(callToActionFragment.getActivity().getString(R.string.cta))) {

                    suggestionsDO.setExpandGroup(false);

                    SpannableString content = new SpannableString(callToActionFragment.getActivity().getString(R.string.view_details));
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    holder.tvCTA.setText(content);

                    holder.llMessage.setVisibility(View.GONE);
                    callToActionFragment.performAction(suggestionsDO);
                } else {
                    suggestionsDO.setExpandGroup(true);
                    setShareLayouts(suggestionsDO.isShareEnabled(), holder);

                    YoYo.with(Techniques.SlideInUp)
                            .duration(1000)
                            .playOn(holder.llMessage);
                }

            }
        });

    }

    private void setShareLayouts(boolean isShareEnabled,
                                 ActionViewHolder holder) {

        SpannableString content = null;
        if (isShareEnabled) {
            content = new SpannableString(callToActionFragment.getActivity().getString(R.string.view_details));
            holder.llMessage.setVisibility(View.GONE);
            holder.llShare.setVisibility(View.VISIBLE);
        } else {
            content = new SpannableString(callToActionFragment.getActivity().getString(R.string.cta));
            holder.llMessage.setVisibility(View.VISIBLE);
            holder.llShare.setVisibility(View.GONE);
        }

        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        holder.tvCTA.setText(content);
    }

    @Override
    public int getItemCount() {

        if (arrSuggestions == null || arrSuggestions.size() == 0)
            return 0;
        return arrSuggestions.size();
    }

    public class ActionViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSource, tvActualMessage,
                tvDate, tvValue, tvCTA;
        private LinearLayout llMessage, llShare;

        public ActionViewHolder(View itemView) {
            super(itemView);
            tvSource = (TextView) itemView.findViewById(R.id.tvSource);
            tvActualMessage = (TextView) itemView.findViewById(R.id.tvActualMessage);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvValue = (TextView) itemView.findViewById(R.id.tvValue);
            tvCTA = (TextView) itemView.findViewById(R.id.tvCTA);
            llMessage = (LinearLayout) itemView.findViewById(R.id.llMessage);
            llShare = (LinearLayout) itemView.findViewById(R.id.llShare);
        }
    }
}
