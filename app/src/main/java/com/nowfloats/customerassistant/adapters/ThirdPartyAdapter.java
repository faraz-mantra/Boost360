package com.nowfloats.customerassistant.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.nowfloats.customerassistant.ThirdPartySuggestionDetailActivity;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 09-10-2017.
 */

public class ThirdPartyAdapter extends RecyclerView.Adapter {


    final int EMPTY_LAYOUT = -1, MESSAGE_LAYOUT = -2;
    public boolean isCounterStopped;
    private Context mContext;
    private List<SuggestionsDO> rvList;
    private RecyclerView mRecyclerView;


    public ThirdPartyAdapter(Context context, List<SuggestionsDO> list) {
        mContext = context;
        rvList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case MESSAGE_LAYOUT:
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_third_party_sms_item, parent, false);
                return new MySmsViewHolder(view);
            case EMPTY_LAYOUT:
                view = LayoutInflater.from(mContext).inflate(R.layout.empty_screen_layout, parent, false);
                return new MyEmptyHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof MySmsViewHolder) {
            MySmsViewHolder holder = (MySmsViewHolder) viewHolder;
            final SuggestionsDO suggestionsDO = rvList.get(position);
            holder.addressTextView.setText(suggestionsDO.getValue());
            holder.titleTextView.setText(suggestionsDO.getActualMessage());
            holder.timeTextView.setText(Methods.getFormattedDate(suggestionsDO.getDate(), "dd MMM, hh:mm a"));
            //suggestionsDO.setShortText("https://s3-ap-southeast-1.amazonaws.com/nfcontent-cdn/boostBubbleLogos/JD.png");
            if (!TextUtils.isEmpty(suggestionsDO.getLogoUrl()) && suggestionsDO.getLogoUrl().contains("http")) {
                holder.sourceImg.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(suggestionsDO.getLogoUrl())
                        .into(holder.sourceImg);

            } else {
                holder.sourceImg.setVisibility(View.GONE);
            }

            long millis = suggestionsDO.getExpiryDate() - Calendar.getInstance().getTimeInMillis();

            if (holder.customTimer != null) {
                holder.customTimer.cancel();
            }

            holder.customTimer = (CustomTimer) new CustomTimer(millis, holder, position).start();
        } else if (viewHolder instanceof MyEmptyHolder) {
            MyEmptyHolder holder = (MyEmptyHolder) viewHolder;
            holder.mainText.setVisibility(View.GONE);
            holder.mainImg.setImageResource(R.drawable.enquiry_icon);
            holder.descriptionText.setText("You do not have any active enquiries from third party platforms.");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return rvList.get(position).isEmptyLayout ? EMPTY_LAYOUT : MESSAGE_LAYOUT;
    }

    private void finishTimer(final int mPosition) {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                rvList.remove(mPosition);
                notifyItemRemoved(mPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rvList.size();
    }

    public void refreshListData(List<SuggestionsDO> newList) {
        if (newList == null) {
            return;
        }
        if (rvList.size() > 0) {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new SuggestionCallback(newList));
            rvList.clear();
            rvList.addAll(newList);
            result.dispatchUpdatesTo(this);
        } else {
            rvList.addAll(newList);
            notifyDataSetChanged();
        }

    }

    private void showActualMessage(int pos) {
        SuggestionsDO suggestionsDO = rvList.get(pos);
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title("Full Text Message From " + suggestionsDO.getSource())
                .content(suggestionsDO.getActualMessage())
                .linkColorRes(R.color.primaryColor)
                .show();
    }

    class CustomTimer extends CountDownTimer {
        private MySmsViewHolder holder;

        CustomTimer(long millis, MySmsViewHolder holder, int position) {
            super(millis, 1000);
            this.holder = holder;
            if (millis < 60 * 60 * 24 * 1000) {
                holder.responseTextView.setTextColor(ContextCompat.getColorStateList(mContext, R.color.red_btn_text_color));
                holder.responseTextView.setBackgroundResource(R.drawable.red_btn_state_bg);
            } else {
                String days = TimeUnit.MILLISECONDS.toDays(millis) == 1 ? "day" : " days";
                holder.responseTextView.setText("Respond in " + days);
                holder.responseTextView.setTextColor(ContextCompat.getColorStateList(mContext, R.color.yellow_btn_text_color));
                holder.responseTextView.setBackgroundResource(R.drawable.yellow_btn_bg);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            isCounterStopped = false;
            if (millisUntilFinished < 60 * 60 * 24 * 1000) {
                String hms = String.format(Locale.ENGLISH, "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                if (holder.responseTextView.getText().toString().endsWith("days")) {
                    holder.responseTextView.setTextColor(ContextCompat.getColorStateList(mContext, R.color.red_btn_text_color));
                    holder.responseTextView.setBackgroundResource(R.drawable.red_btn_state_bg);
                }
                holder.responseTextView.setText("Respond in " + hms);
            } else {
                String days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished) == 1 ? " day" : " days";
                holder.responseTextView.setText("Respond in " + TimeUnit.MILLISECONDS.toDays(millisUntilFinished) + days);
            }
            if (!Methods.isMyActivityAtTop(mContext)) {
                this.cancel();
                isCounterStopped = true;
            }
        }

        @Override
        public void onFinish() {
            holder.responseTextView.setTextColor(ContextCompat.getColorStateList(mContext, R.color.red_btn_text_color));
            holder.responseTextView.setBackgroundResource(R.drawable.red_btn_state_bg);
            holder.responseTextView.setText("Expired");
            //finishTimer(mPosition);
        }
    }

    private class SuggestionCallback extends DiffUtil.Callback {

        List<SuggestionsDO> newList;

        private SuggestionCallback(List<SuggestionsDO> list) {
            newList = list;
        }

        @Override
        public int getOldListSize() {
            return rvList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (newList.get(newItemPosition).getMessageId() != null && rvList.get(oldItemPosition).getMessageId() != null) {
                return newList.get(newItemPosition).getMessageId().equals(rvList.get(oldItemPosition).getMessageId());
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return false;
        }
    }

    class MyEmptyHolder extends RecyclerView.ViewHolder {

        ImageView mainImg;
        TextView mainText, descriptionText;

        MyEmptyHolder(View view) {
            super(view);
            mainImg = view.findViewById(R.id.image_item);
            mainText = view.findViewById(R.id.main_text);
            descriptionText = view.findViewById(R.id.description_text);

        }
    }

    class MySmsViewHolder extends RecyclerView.ViewHolder {

        TextView addressTextView, titleTextView, timeTextView, responseTextView;
        CustomTimer customTimer;
        ImageView infoImag, sourceImg;

        public MySmsViewHolder(View itemView) {
            super(itemView);
            addressTextView = (TextView) itemView.findViewById(R.id.tv_address);
            titleTextView = (TextView) itemView.findViewById(R.id.tv_title);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_time);
            sourceImg = itemView.findViewById(R.id.img_source);
            responseTextView = (TextView) itemView.findViewById(R.id.tv_response);
            infoImag = (ImageView) itemView.findViewById(R.id.img_info);
            infoImag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showActualMessage(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, ThirdPartySuggestionDetailActivity.class);
                    i.putExtra("message", rvList.get(getAdapterPosition()));
                    mContext.startActivity(i);
                }
            });
            responseTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, ThirdPartySuggestionDetailActivity.class);
                    i.putExtra("message", rvList.get(getAdapterPosition()));
                    mContext.startActivity(i);
                }
            });
        }
    }
}
