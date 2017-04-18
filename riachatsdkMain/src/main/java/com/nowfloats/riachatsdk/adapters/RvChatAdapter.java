package com.nowfloats.riachatsdk.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.nowfloats.riachatsdk.CustomWidget.playpause.PlayPauseView;
import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.models.Section;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.RunnableFuture;

/**
 * Created by NowFloats on 16-03-2017.
 */

public class RvChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum SectionTypeEnum
    {
        Image(0), Text(1), Graph(2), Gif(3), Audio(4), Video(5), Link(6), EmbeddedHtml(7), Carousel(8), Typing(9);
        private final int val;
        private SectionTypeEnum(int val){
            this.val = val;
        }

        public int getValue(){
            return val;
        }
    }

    private List<Section> mChatSections;
    private Context mContext;

    public RvChatAdapter(List<Section> mChatSections, Context context) {
        this.mChatSections = mChatSections;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        Section section  = mChatSections.get(position);
        return SectionTypeEnum.valueOf(section.getSectionType()).getValue();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout;
        View v;
        switch (viewType){
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image_row_layout, parent, false);
                return new ImageViewHolder(v);
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_text_row_layout, parent, false);
                return new TextViewHolder(v);
            case 2:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_graph_row_layout, parent, false);
                return new GraphViewholder(v);
            case 3:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_gif_row_layout, parent, false);
                return new GifViewHolder(v);
            case 4:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_audio_row_layout, parent, false);
                return new AudioViewHolder(v);
            case 5:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_video_row_layout, parent, false);
                return new VideoViewHolder(v);
            case 7:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_embedded_html_row_layout, parent, false);
                return new EmbeddedHtmlViewHolder(v);
            case 9:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_typing_row_layout, parent, false);
                return new TypingViewHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_typing_row_layout, parent, false);
                return new TypingViewHolder(v);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Section section = mChatSections.get(position);
        if(holder instanceof TextViewHolder){
            TextViewHolder textViewHolder = (TextViewHolder) holder;
            textViewHolder.tvMessageText.setText(Html.fromHtml(section.getText()));
            if(!section.isFromRia()){
                ((LinearLayout) textViewHolder.itemView).setGravity(Gravity.RIGHT);
                textViewHolder.ivRiaChatHead.setVisibility(View.GONE);
                textViewHolder.llBubbleContainer.setBackgroundResource(R.drawable.chat_layout_reply_bg);
                textViewHolder.tvMessageText.setTextColor(Color.parseColor("#ffffff"));
            }else {
                ((LinearLayout) textViewHolder.itemView).setGravity(Gravity.LEFT);
                textViewHolder.llBubbleContainer.setBackgroundResource(R.drawable.chat_layout_bg);
                textViewHolder.tvMessageText.setTextColor(Color.parseColor("#808080"));
                if(mChatSections!= null && mChatSections.size()>0 && position>0 && mChatSections.get(position-1).isFromRia()){
                    textViewHolder.ivRiaChatHead.setVisibility(View.INVISIBLE);
                }else {
                    textViewHolder.ivRiaChatHead.setVisibility(View.VISIBLE);
                }
            }
        }else if(holder instanceof ImageViewHolder){
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            if(section.isLoading()){
                imageViewHolder.pbLoading.setVisibility(View.VISIBLE);
            }else {
                imageViewHolder.pbLoading.setVisibility(View.INVISIBLE);
            }
            if(section.getTitle()!=null && !section.getTitle().trim().equals("")) {
                imageViewHolder.tvImageTitle.setText(section.getTitle());
            }else {
                imageViewHolder.tvImageTitle.setVisibility(View.GONE);
            }
            Glide.with(mContext)
                    .load(section.getUrl())
                    .centerCrop()
                    .placeholder(R.drawable.default_product_image)
                    .into(imageViewHolder.ivMainImage);
            if(section.getCaption()!=null && !section.getCaption().trim().equals("")) {
                imageViewHolder.tvImageCaption.setText(section.getCaption());
            }else {
                imageViewHolder.tvImageCaption.setVisibility(View.GONE);
            }
            if(!section.isFromRia()){
                ((LinearLayout) imageViewHolder.itemView).setGravity(Gravity.RIGHT);
                imageViewHolder.ivRiaChatHead.setVisibility(View.GONE);
            }else {
                ((LinearLayout) imageViewHolder.itemView).setGravity(Gravity.LEFT);
                if(mChatSections!= null && mChatSections.size()>0 && position>0 && mChatSections.get(position-1).isFromRia()){
                    imageViewHolder.ivRiaChatHead.setVisibility(View.INVISIBLE);
                }else {
                    imageViewHolder.ivRiaChatHead.setVisibility(View.VISIBLE);
                }
            }
        }else if(holder instanceof GifViewHolder){
            GifViewHolder imageViewHolder = (GifViewHolder) holder;
            if(section.getTitle()!=null && !section.getTitle().trim().equals("")) {
                imageViewHolder.tvImageTitle.setText(section.getTitle());
            }else {
                imageViewHolder.tvImageTitle.setVisibility(View.GONE);
            }
            Glide.with(mContext)
                    .load(section.getUrl())
                    .asGif()
                    .centerCrop()
                    .placeholder(R.drawable.default_product_image)
                    .into(imageViewHolder.ivMainImage);
            if(section.getCaption()!=null && !section.getCaption().trim().equals("")) {
                imageViewHolder.tvImageCaption.setText(section.getCaption());
            }else {
                imageViewHolder.tvImageCaption.setVisibility(View.GONE);
            }
            if(!section.isFromRia()){
                ((LinearLayout) imageViewHolder.itemView).setGravity(Gravity.RIGHT);
                imageViewHolder.ivRiaChatHead.setVisibility(View.GONE);
            }else {
                ((LinearLayout) imageViewHolder.itemView).setGravity(Gravity.LEFT);
                if(mChatSections!= null && mChatSections.size()>0 && position>0 && mChatSections.get(position-1).isFromRia()){
                    imageViewHolder.ivRiaChatHead.setVisibility(View.INVISIBLE);
                }else {
                    imageViewHolder.ivRiaChatHead.setVisibility(View.VISIBLE);
                }
            }
        }else if(holder instanceof EmbeddedHtmlViewHolder){
            EmbeddedHtmlViewHolder embeddedHtmlViewHolder = (EmbeddedHtmlViewHolder) holder;
            embeddedHtmlViewHolder.tvTitle.setText(section.getTitle());
            embeddedHtmlViewHolder.wvEmbeddedHtml.getSettings().setJavaScriptEnabled(true);
            embeddedHtmlViewHolder.wvEmbeddedHtml.loadUrl(section.getUrl());
            if(section.isDisplayOpenInBrowserButton()){
                embeddedHtmlViewHolder.tvOpenInBrowser.setVisibility(View.VISIBLE);
            }
            embeddedHtmlViewHolder.tvOpenInBrowser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Open in browser
                }
            });
            embeddedHtmlViewHolder.tvCaption.setText(section.getCaption());
            if(!section.isFromRia()){
                ((LinearLayout) embeddedHtmlViewHolder.itemView).setGravity(Gravity.RIGHT);
                embeddedHtmlViewHolder.ivRiaChatHead.setVisibility(View.GONE);
            }else {
                ((LinearLayout) embeddedHtmlViewHolder.itemView).setGravity(Gravity.LEFT);
                if(mChatSections!= null && mChatSections.size()>0 && position>0 && mChatSections.get(position-1).isFromRia()){
                    embeddedHtmlViewHolder.ivRiaChatHead.setVisibility(View.INVISIBLE);
                }else {
                    embeddedHtmlViewHolder.ivRiaChatHead.setVisibility(View.VISIBLE);
                }
            }
        }else  if(holder instanceof VideoViewHolder){
            //TODO: set loading icon
            MediaController mediaControls = new MediaController(mContext);
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            videoViewHolder.tvTitle.setText(section.getTitle());

            if(section.isLoading()){
                videoViewHolder.pbVideoLoading.setVisibility(View.VISIBLE);
            }else {
                videoViewHolder.pbVideoLoading.setVisibility(View.INVISIBLE);
            }

            try{
                videoViewHolder.vvMainVideo.setMediaController(mediaControls);
                videoViewHolder.vvMainVideo.setVideoURI(Uri.parse(section.getUrl()));
            }catch (Exception e){
                e.printStackTrace();
            }
            if(!section.isFromRia()){
                ((LinearLayout) videoViewHolder.itemView).setGravity(Gravity.RIGHT);
                videoViewHolder.ivRiaChatHead.setVisibility(View.GONE);
            }else {
                ((LinearLayout) videoViewHolder.itemView).setGravity(Gravity.LEFT);
                if(mChatSections!= null && mChatSections.size()>0 && position>0 && mChatSections.get(position-1).isFromRia()){
                    videoViewHolder.ivRiaChatHead.setVisibility(View.INVISIBLE);
                }else {
                    videoViewHolder.ivRiaChatHead.setVisibility(View.VISIBLE);
                }
            }
        }else if(holder instanceof AudioViewHolder){

            final AudioViewHolder audioViewHolder = (AudioViewHolder) holder;

            if(!section.isFromRia()){
                ((LinearLayout) audioViewHolder.itemView).setGravity(Gravity.RIGHT);
                audioViewHolder.ivRiaChatHead.setVisibility(View.GONE);
            }else {
                ((LinearLayout) audioViewHolder.itemView).setGravity(Gravity.LEFT);
                if(mChatSections!= null && mChatSections.size()>0 && position>0 && mChatSections.get(position-1).isFromRia()){
                    audioViewHolder.ivRiaChatHead.setVisibility(View.INVISIBLE);
                }else {
                    audioViewHolder.ivRiaChatHead.setVisibility(View.VISIBLE);
                }
            }

            if(section.isLoading()){
                audioViewHolder.pbAudioLoading.setVisibility(View.VISIBLE);
                audioViewHolder.ppvAudio.setEnabled(false);
            }else {
                audioViewHolder.pbAudioLoading.setVisibility(View.GONE);
                audioViewHolder.ppvAudio.setEnabled(true);
            }

            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(section.getUrl());
                mediaPlayer.prepareAsync();
            }catch (IOException e){
                e.printStackTrace();
            }
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int mCurrentPosition = (mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration())*100;
                    audioViewHolder.sbAudio.setProgress(mCurrentPosition);
                    handler.postDelayed(this, 1000);
                }
            };
            audioViewHolder.ppvAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        handler.removeCallbacks(null);
                    }else {
                        mediaPlayer.start();
                        handler.post(runnable);
                    }
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return mChatSections.size();
    }


    private class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView ivRiaChatHead;
        TextView tvImageTitle;
        ImageView ivMainImage;
        TextView tvImageCaption;
        ProgressBar pbLoading;


        public ImageViewHolder(View itemView) {
            super(itemView);

            ivRiaChatHead = (ImageView) itemView.findViewById(R.id.iv_ria_chat_head);
            tvImageTitle = (TextView) itemView.findViewById(R.id.tv_title);
            ivMainImage = (ImageView) itemView.findViewById(R.id.iv_main_row_image);
            tvImageCaption = (TextView) itemView.findViewById(R.id.tv_caption);
            pbLoading = (ProgressBar) itemView.findViewById(R.id.pb_loading);

        }
    }

    private class TextViewHolder extends RecyclerView.ViewHolder{

        TextView tvMessageText;
        ImageView ivRiaChatHead;
        View itemView;
        LinearLayout llBubbleContainer;

        public TextViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            tvMessageText = (TextView) itemView.findViewById(R.id.tv_message_text);
            ivRiaChatHead = (ImageView) itemView.findViewById(R.id.iv_ria_chat_head);
            llBubbleContainer = (LinearLayout) itemView.findViewById(R.id.ll_bubble_container);
        }
    }

    private class GraphViewholder extends RecyclerView.ViewHolder{

        public GraphViewholder(View itemView) {
            super(itemView);
        }
    }

    private class GifViewHolder extends RecyclerView.ViewHolder{

        ImageView ivRiaChatHead;
        TextView tvImageTitle;
        ImageView ivMainImage;
        TextView tvImageCaption;

        public GifViewHolder(View itemView) {
            super(itemView);

            ivRiaChatHead = (ImageView) itemView.findViewById(R.id.iv_ria_chat_head);
            tvImageTitle = (TextView) itemView.findViewById(R.id.tv_title);
            ivMainImage = (ImageView) itemView.findViewById(R.id.iv_main_row_image);
            tvImageCaption = (TextView) itemView.findViewById(R.id.tv_caption);
        }
    }

    private class AudioViewHolder extends RecyclerView.ViewHolder{

        ImageView ivRiaChatHead;
        PlayPauseView ppvAudio;
        SeekBar sbAudio;
        ProgressBar pbAudioLoading;

        public AudioViewHolder(View itemView) {
            super(itemView);

            ivRiaChatHead = (ImageView) itemView.findViewById(R.id.iv_ria_chat_head);
            ppvAudio = (PlayPauseView) itemView.findViewById(R.id.ppv_audio);
            sbAudio = (SeekBar) itemView.findViewById(R.id.sb_audio);
            pbAudioLoading = (ProgressBar) itemView.findViewById(R.id.pb_audio_loading);
            sbAudio.setEnabled(false);
        }
    }

    private class VideoViewHolder extends RecyclerView.ViewHolder{

        ImageView ivRiaChatHead;
        TextView tvTitle;
        VideoView vvMainVideo;
        ProgressBar pbVideoLoading;

        public VideoViewHolder(View itemView) {
            super(itemView);

            ivRiaChatHead = (ImageView) itemView.findViewById(R.id.iv_ria_chat_head);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            vvMainVideo = (VideoView) itemView.findViewById(R.id.vv_main_video);
            pbVideoLoading = (ProgressBar) itemView.findViewById(R.id.pb_video_loading);
        }
    }

    private class EmbeddedHtmlViewHolder extends RecyclerView.ViewHolder{

        ImageView ivRiaChatHead;
        WebView wvEmbeddedHtml;
        TextView tvTitle, tvOpenInBrowser, tvCaption;

        public EmbeddedHtmlViewHolder(View itemView) {
            super(itemView);

            ivRiaChatHead = (ImageView) itemView.findViewById(R.id.iv_ria_chat_head);
            wvEmbeddedHtml = (WebView) itemView.findViewById(R.id.wv_embedded_html);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvOpenInBrowser = (TextView) itemView.findViewById(R.id.tv_open_in_browser);
            tvCaption = (TextView) itemView.findViewById(R.id.tv_caption);

        }
    }

    private class TypingViewHolder extends RecyclerView.ViewHolder{

        ImageView ivRiaChatHead;

        public TypingViewHolder(View itemView) {
            super(itemView);

            ivRiaChatHead = (ImageView) itemView.findViewById(R.id.iv_ria_chat_head);
            Section section = mChatSections.get(mChatSections.size()-1);
            if(!section.isFromRia()){
                ((LinearLayout) itemView).setGravity(Gravity.RIGHT);
                ivRiaChatHead.setVisibility(View.GONE);
            }else {
                if(mChatSections!= null && mChatSections.size()>0 && mChatSections.get(mChatSections.size()-1).isFromRia()){
                    ivRiaChatHead.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

}
