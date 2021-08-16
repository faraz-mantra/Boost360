package com.nowfloats.managecustomers.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nowfloats.managecustomers.models.FacebookChatDataModel;
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import java.util.List;

/**
 * Created by Admin on 17-08-2017.
 */

public class FacebookChatDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String MERCHANT = "merchant", USER = "user", WAITING = "waiting", ERROR = "error";
    final int TEXT = 0, IMAGE = 1;
    List<FacebookChatDataModel.Datum> chatList;
    private Context mContext;

    public FacebookChatDetailAdapter(Context context, List<FacebookChatDataModel.Datum> list) {
        mContext = context;
        chatList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TEXT:
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_facebook_chat_text, parent, false);
                return new MyTextChatDetailViewHolder(view);
            case IMAGE:
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_facebook_chat_image, parent, false);
                return new MyImageChatDetailViewHolder(view);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FacebookChatDataModel.Datum data = chatList.get(position);

        MainViewHolder mainViewHolder = null;
        if (holder instanceof MyTextChatDetailViewHolder) {
            MyTextChatDetailViewHolder textHolder = (MyTextChatDetailViewHolder) holder;
            textHolder.tvMessage.setText(data.getMessage().getData().getText());

            mainViewHolder = textHolder;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textHolder.parentLayout.getLayoutParams();
            if (!data.getSender().equals(USER)) {
                if (chatList.get(position).isShowCornerBackground()) {
                    textHolder.parentLayout.setBackgroundResource(R.drawable.ic_chat_reply);
                    lp.setMargins(Utils.dpToPx(mContext, 60), 0, Utils.dpToPx(mContext, 5), 0);
                } else {
                    textHolder.parentLayout.setBackgroundResource(R.drawable.ic_chat_reply);
                    lp.setMargins(Utils.dpToPx(mContext, 60), 0, Utils.dpToPx(mContext, 15), 0);
                }
                textHolder.tvMessage.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                mainViewHolder.itemView.setGravity(Gravity.START);
                if (chatList.get(position).isShowCornerBackground()) {
                    textHolder.parentLayout.setBackgroundResource(R.drawable.ic_chat_reply);
                    lp.setMargins(Utils.dpToPx(mContext, 5), 0, Utils.dpToPx(mContext, 60), 0);
                } else {
                    textHolder.parentLayout.setBackgroundResource(R.drawable.ic_chat_reply);
                    lp.setMargins(Utils.dpToPx(mContext, 15), 0, Utils.dpToPx(mContext, 60), 0);
                }
                textHolder.tvMessage.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
            }

        } else if (holder instanceof MyImageChatDetailViewHolder) {
            MyImageChatDetailViewHolder imageHolder = (MyImageChatDetailViewHolder) holder;
            if (data.getMessage().getData().getText().length() > 0) {
                imageHolder.captionText.setVisibility(View.VISIBLE);
                imageHolder.captionText.setText(data.getMessage().getData().getText());
            } else {
                imageHolder.captionText.setVisibility(View.GONE);
            }
            Glide.with(mContext)
                    .asGif()
                    .load(chatList.get(position).getMessage().getData().getUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.default_product_image))
                    .into(imageHolder.imgMessage);
            mainViewHolder = imageHolder;
        }
        if (mainViewHolder == null) return;

        switch (data.getSender()) {
            case WAITING:
                mainViewHolder.tvDate.setText("sending...");
                mainViewHolder.itemView.setGravity(Gravity.END);

                break;
            case MERCHANT:

                mainViewHolder.itemView.setGravity(Gravity.END);
                mainViewHolder.tvDate.setText(getTime(Methods.getFormattedDate(String.valueOf(data.getTimestamp()))));
                break;
            case USER:

                mainViewHolder.itemView.setGravity(Gravity.START);
                mainViewHolder.tvDate.setText(getTime(Methods.getFormattedDate(String.valueOf(data.getTimestamp()))));
                break;
            case ERROR:
                mainViewHolder.itemView.setGravity(Gravity.END);
                mainViewHolder.tvDate.setText("message not delivered");

                break;
        }

        mainViewHolder.tvDate.setTextColor(ContextCompat.getColor(mContext, data.getSender().equals(ERROR) ? R.color.red : R.color.light_gray));

    }

    private String getTime(String date) {
        return date.replaceAll(".*?at", /*"'"+subYear.substring(subYear.length()-2)*/ "");
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getMessage().getType().equals("text") ? TEXT : IMAGE;
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    private class MyTextChatDetailViewHolder extends MainViewHolder {

        TextView tvMessage;
        LinearLayout parentLayout;

        MyTextChatDetailViewHolder(View itemView) {
            super(itemView);
            tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.ll_bubble_container);
        }
    }

    private class MyImageChatDetailViewHolder extends MainViewHolder {

        ImageView imgMessage;
        TextView captionText;

        MyImageChatDetailViewHolder(View itemView) {
            super(itemView);
            imgMessage = (ImageView) itemView.findViewById(R.id.img_message);
            captionText = (TextView) itemView.findViewById(R.id.tv_caption);
        }
    }

    abstract class MainViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        LinearLayout itemView;

        MainViewHolder(View itemView) {
            super(itemView);
            this.itemView = (LinearLayout) itemView;
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
}
