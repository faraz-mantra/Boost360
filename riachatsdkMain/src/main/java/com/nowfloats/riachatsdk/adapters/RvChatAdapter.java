package com.nowfloats.riachatsdk.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.nowfloats.riachatsdk.CustomWidget.AVLoadingIndicatorView;
import com.nowfloats.riachatsdk.CustomWidget.FirstLastItemSpacesDecoration;
import com.nowfloats.riachatsdk.CustomWidget.playpause.PlayPauseView;
import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.activities.ChatViewActivity;
import com.nowfloats.riachatsdk.activities.ChatWebViewActivity;
import com.nowfloats.riachatsdk.fragments.CustomDialogFragment;
import com.nowfloats.riachatsdk.interfaces.IConfirmationCallback;
import com.nowfloats.riachatsdk.models.Items;
import com.nowfloats.riachatsdk.models.RiaCardModel;
import com.nowfloats.riachatsdk.models.Section;
import com.nowfloats.riachatsdk.utils.Constants;
import com.nowfloats.riachatsdk.utils.Utils;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NowFloats on 16-03-2017.
 */

public class RvChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum SectionTypeEnum {

        Header(-1), Image(0), Text(1), Graph(2), Gif(3), Audio(4), Video(5), Link(6), EmbeddedHtml(7),
        Carousel(8), Typing(9), Card(10), AddressCard(11), UnConfirmedCard(12), UnConfirmedAddressCard(13), PrintOTP(14), SubmitForm(15);

        private final int val;

        private SectionTypeEnum(int val) {
            this.val = val;
        }

        public int getValue() {
            return val;
        }
    }

    private int MAX_CARD_COUNT = 6;

    private List<Section> mChatSections;
    private Map<String, String> mDataMap;
    private Context mContext;
    private IConfirmationCallback mConfirmationCallback;
    private float MAX_WIDTH, TARGET_WIDTH, TARGET_HEIGHT;

    public RvChatAdapter(List<Section> mChatSections, Map<String, String> dataMap, Context context) {
        this.mChatSections = mChatSections;
        this.mDataMap = dataMap;
        mContext = context;
        if (context instanceof IConfirmationCallback) {
            mConfirmationCallback = (IConfirmationCallback) mContext;
        }

        Resources r = context.getResources();
        MAX_WIDTH = r.getDisplayMetrics().widthPixels;
        TARGET_WIDTH = (int) (300 * r.getDisplayMetrics().density);
        TARGET_HEIGHT = (int) (200 * r.getDisplayMetrics().density);

    }

    @Override
    public int getItemViewType(int position) {
        Section section = mChatSections.get(position);
        return SectionTypeEnum.valueOf(section.getSectionType()).getValue();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {

            case -1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat_head, parent, false);
                return new HeaderViewHolder(v);
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image_row_layout, parent, false);

                ImageViewHolder imageViewHolder = new ImageViewHolder(v);

//                final android.view.ViewGroup.MarginLayoutParams layoutParams1 = (ViewGroup.MarginLayoutParams) imageViewHolder.ivMainImage.getLayoutParams();
//                layoutParams1.width = (int) TARGET_WIDTH;
//                layoutParams1.height = (int) TARGET_HEIGHT;
//
//                imageViewHolder.ivMainImage.setLayoutParams(layoutParams1);
//                imageViewHolder.ivMainImage.setImageResource(R.drawable.site_sc_default);

                return imageViewHolder;
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
            case 8:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_carousel_layout, parent, false);

                CarouselViewHolder carouselViewHolder = new CarouselViewHolder(v);
                int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27,
                        mContext.getResources().getDisplayMetrics());
                carouselViewHolder.rvCarousel.addItemDecoration(new FirstLastItemSpacesDecoration(space, false));
                carouselViewHolder.pageIndicatorView.setDynamicCount(true);
                final LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                carouselViewHolder.rvCarousel.setLayoutManager(manager);

                return carouselViewHolder;
            case 9:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_typing_row_layout, parent, false);
                return new TypingViewHolder(v);
            case 10:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card_row_layout, parent, false);
                return new CardViewHolder(v);
            case 11:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_address_card_row_layout, parent, false);
                return new AddressCardViewHolder(v);
            case 12:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card_unconfirmed_layout, parent, false);
                return new UnConfirmedCardViewHolder(v);
            case 13:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_unconfirmed_address_card_layout, parent, false);
                return new UnconfirmedAddressCardViewHolder(v);
            case 14:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_print_otp_layout, parent, false);
                return new OTPViewHolder(v);
            case 15:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_submit_form_layout, parent, false);
                return new FormViewHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_typing_row_layout, parent, false);
                return new TypingViewHolder(v);

        }
        //((LinearLayout)v).setLayoutTransition(new LayoutTransition());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Section section = mChatSections.get(position);

        /*if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM", Locale.US);
            headerViewHolder.tvDateTime.setText(format.format(new Date()));
        } else */
        if (holder instanceof CardViewHolder) {
            CardViewHolder cardViewHolder = (CardViewHolder) holder;

            String riaText = getParsedPrefixPostfixText(section.getCardModel().getSections().get(0).getText());

            if (riaText.contains(".nowfloats.com")) {
                riaText = riaText.split(".nowfloats.com")[0].replace("<br>", "");
                cardViewHolder.tvConfirmSubText.setVisibility(View.VISIBLE);
            } else {
                cardViewHolder.tvConfirmSubText.setVisibility(View.GONE);
            }

            cardViewHolder.tvConfirmationText.setText(Html.fromHtml(riaText));
            cardViewHolder.tvConfirmedHintText.setText(Html.fromHtml(getParsedPrefixPostfixText(section.getCardModel().getCardFooter())));


            if (section.isShowDate()) {
                cardViewHolder.tvDateTime.setVisibility(View.VISIBLE);
                cardViewHolder.tvDateTime.setText(section.getDateTime());
            } else {
                cardViewHolder.tvDateTime.setVisibility(View.GONE);
            }

            if (!section.isAnimApplied()) {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.flip_in_anim);
                cardViewHolder.llBubbleContainer.setAnimation(animation);
                animation.start();
                section.setIsAnimApplied(true);
            }

            if (section.getCardPos() > MAX_CARD_COUNT) {
                cardViewHolder.llCardPos.setVisibility(View.GONE);
            } else {
                cardViewHolder.llCardPos.setVisibility(View.VISIBLE);
            }

            cardViewHolder.tvCurrentPos.setText(section.getCardPos() + "");
            cardViewHolder.tvTotalCount.setText("" + MAX_CARD_COUNT);

            /*((LinearLayout) cardViewHolder.itemView).setGravity(Gravity.RIGHT);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) cardViewHolder.llBubbleContainer.getLayoutParams();

            cardViewHolder.llBubbleContainer.setLayoutParams(lp);
            cardViewHolder.tvConfirmationText.setTextColor(Color.parseColor("#ffffff"));*/

        } else if (holder instanceof UnConfirmedCardViewHolder) {
            RiaCardModel model = section.getCardModel();
            final UnConfirmedCardViewHolder cardViewHolder = (UnConfirmedCardViewHolder) holder;
            if (model.getCardHeader() == null)
                model.setCardHeader("");
            cardViewHolder.tvConfirmationTitle.setText(Html.fromHtml(getParsedPrefixPostfixText(model.getCardHeader())));

            final boolean fpTagChecker;

            String riaText = getParsedPrefixPostfixText(model.getSections().get(0).getText());

            if (riaText.contains(".nowfloats.com")) {
                riaText = riaText.split(".nowfloats.com")[0].replace("<br>", "");
                cardViewHolder.tvConfirmSubText.setVisibility(View.VISIBLE);

                fpTagChecker = true;
            } else {

                fpTagChecker = false;
                cardViewHolder.tvConfirmSubText.setVisibility(View.GONE);
            }

            cardViewHolder.tvConfirmationText.setText(Html.fromHtml(riaText));

            cardViewHolder.llConfirm.setVisibility(View.VISIBLE);
            cardViewHolder.tvSubmit.setVisibility(View.GONE);

            cardViewHolder.tvConfirm.setClickable(true);
            cardViewHolder.tvConfirm.setEnabled(true);

            cardViewHolder.tvEdit.setClickable(true);
            cardViewHolder.tvEdit.setEnabled(true);

            if (mChatSections.get(position).getCardModel().getButtons() != null && mChatSections.get(position).getCardModel().getButtons().size() == 1) {
                cardViewHolder.tvEdit.setText("");
                cardViewHolder.tvEdit.setVisibility(View.GONE);
                cardViewHolder.tvConfirm.setText(mChatSections.get(position).getCardModel().getButtons().get(0).getButtonText());
                cardViewHolder.tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cardViewHolder.tvConfirm.setClickable(false);
                        cardViewHolder.tvConfirm.setEnabled(false);

                        cardViewHolder.llConfirm.setVisibility(View.GONE);
                        cardViewHolder.tvSubmit.setVisibility(View.VISIBLE);

                        mConfirmationCallback.onCardResponse(Constants.ConfirmationType.BIZ_NAME, mChatSections.get(position).getCardModel().getButtons().get(0),
                                cardViewHolder.tvConfirmationText.getText().toString(),
                                mChatSections.get(position).getCardModel().getButtons().get(0).getNextNodeId());
                    }
                });
            } else {
                cardViewHolder.tvEdit.setVisibility(View.VISIBLE);
                cardViewHolder.tvConfirm.setText(mChatSections.get(position).getCardModel().getButtons().get(0).getButtonText());
                cardViewHolder.tvEdit.setText(mChatSections.get(position).getCardModel().getButtons().get(1).getButtonText());
                cardViewHolder.tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cardViewHolder.tvConfirm.setClickable(false);
                        cardViewHolder.tvConfirm.setEnabled(false);

                        cardViewHolder.llConfirm.setVisibility(View.GONE);
                        cardViewHolder.tvSubmit.setVisibility(View.VISIBLE);

                        if (fpTagChecker)
                        {
                            mConfirmationCallback.onCardResponse(Constants.ConfirmationType.FP_TAG_CONFIRM, mChatSections.get(position).getCardModel().getButtons().get(0),
                                    cardViewHolder.tvConfirmationText.getText().toString(),
                                    mChatSections.get(position).getCardModel().getButtons().get(0).getNextNodeId());
                        }

                        else
                        {
                            mConfirmationCallback.onCardResponse(Constants.ConfirmationType.BIZ_NAME, mChatSections.get(position).getCardModel().getButtons().get(0),
                                    cardViewHolder.tvConfirmationText.getText().toString(),
                                    mChatSections.get(position).getCardModel().getButtons().get(0).getNextNodeId());
                        }
                    }
                });

                cardViewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cardViewHolder.tvEdit.setClickable(false);
                        cardViewHolder.tvEdit.setEnabled(false);

                        cardViewHolder.llConfirm.setVisibility(View.GONE);
                        cardViewHolder.tvSubmit.setVisibility(View.VISIBLE);

                        if(fpTagChecker)
                        {
                            mConfirmationCallback.onCardResponse(Constants.ConfirmationType.FP_TAG_EDIT, mChatSections.get(position).getCardModel().getButtons().get(1),
                                    cardViewHolder.tvConfirmationText.getText().toString(),
                                    mChatSections.get(position).getCardModel().getButtons().get(1).getNextNodeId());
                        }

                        else
                        {
                            mConfirmationCallback.onCardResponse(Constants.ConfirmationType.BIZ_NAME, mChatSections.get(position).getCardModel().getButtons().get(1),
                                    cardViewHolder.tvConfirmationText.getText().toString(),
                                    mChatSections.get(position).getCardModel().getButtons().get(1).getNextNodeId());
                        }
                    }
                });
            }

        } else if (holder instanceof OTPViewHolder) {
            final RiaCardModel model = section.getCardModel();
            final OTPViewHolder otpViewHolder = (OTPViewHolder) holder;
            otpViewHolder.tvConfirmationTitle.setText(getParsedPrefixPostfixText(model.getCardHeader()));
            otpViewHolder.etOTPConfirmation.setMaxEms(model.getSections().get(0).getOtpLength());
            otpViewHolder.etOTPConfirmation.setText("");
            otpViewHolder.etOTPConfirmation.setEnabled(true);

            otpViewHolder.etOTPConfirmation.requestFocus();
            InputMethodManager imm = (InputMethodManager) otpViewHolder.etOTPConfirmation.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(otpViewHolder.etOTPConfirmation, InputMethodManager.SHOW_FORCED);

            otpViewHolder.llConfirm.setVisibility(View.VISIBLE);
            otpViewHolder.tvSubmit.setVisibility(View.GONE);

            if (mChatSections.get(position).getCardModel().getButtons() != null && mChatSections.get(position).getCardModel().getButtons().size() == 1) {
                otpViewHolder.tvEdit.setVisibility(View.GONE);
                otpViewHolder.tvConfirm.setText(mChatSections.get(position).getCardModel().getButtons().get(0).getButtonText());
                otpViewHolder.tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        otpViewHolder.llConfirm.setVisibility(View.INVISIBLE);
                        otpViewHolder.tvSubmit.setVisibility(View.VISIBLE);

                        mDataMap.put("[~" + model.getVariableName() + "]", otpViewHolder.etOTPConfirmation.getText().toString().trim());
                        otpViewHolder.etOTPConfirmation.clearFocus();
                        otpViewHolder.etOTPConfirmation.setEnabled(false);
                        mConfirmationCallback.onCardResponse(Constants.ConfirmationType.OTP, mChatSections.get(position).getCardModel().getButtons().get(0),
                                otpViewHolder.etOTPConfirmation.getText().toString(),
                                mChatSections.get(position).getCardModel().getButtons().get(0).getNextNodeId());
                    }
                });
            } else {
                otpViewHolder.tvEdit.setVisibility(View.VISIBLE);

                otpViewHolder.tvConfirm.setText(mChatSections.get(position).getCardModel().getButtons().get(0).getButtonText());
                otpViewHolder.tvEdit.setText(mChatSections.get(position).getCardModel().getButtons().get(1).getButtonText());
                otpViewHolder.tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        otpViewHolder.llConfirm.setVisibility(View.INVISIBLE);
                        otpViewHolder.tvSubmit.setVisibility(View.VISIBLE);

                        mDataMap.put("[~" + model.getVariableName() + "]", otpViewHolder.etOTPConfirmation.getText().toString().trim());
                        otpViewHolder.etOTPConfirmation.clearFocus();
                        otpViewHolder.etOTPConfirmation.setEnabled(false);

                        mConfirmationCallback.onCardResponse(Constants.ConfirmationType.OTP, mChatSections.get(position).getCardModel().getButtons().get(0),
                                otpViewHolder.etOTPConfirmation.getText().toString(),
                                mChatSections.get(position).getCardModel().getButtons().get(0).getNextNodeId());
                    }
                });

                otpViewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        otpViewHolder.etOTPConfirmation.setText("");
                        otpViewHolder.etOTPConfirmation.setEnabled(true);
                        mConfirmationCallback.onCardResponse(Constants.ConfirmationType.OTP, mChatSections.get(position).getCardModel().getButtons().get(1),
                                otpViewHolder.etOTPConfirmation.getText().toString(),
                                mChatSections.get(position).getCardModel().getButtons().get(1).getNextNodeId());
                    }
                });
            }

        } else if (holder instanceof FormViewHolder) {

            final FormViewHolder formViewHolder = (FormViewHolder) holder;

            formViewHolder.tvBusinessName.setText(mDataMap.get("[~BUSINESS_NAME]") + "");
            formViewHolder.tvPhoneNumber.setText(mDataMap.get("[~COUNTRYCODE]") + " " + mDataMap.get("[~PHONE]") + "");
            formViewHolder.tvCategory.setText(mDataMap.get("[~BUSINESS_CATEGORY]") + "");
            formViewHolder.tvAddress.setText(mDataMap.get("[~STREET_ADDRESS]") + ", " + mDataMap.get("[~CITY]") + ", " + mDataMap.get("[~STATE]") + ", " + mDataMap.get("[~COUNTRY]") + "");
            formViewHolder.tvEmailAddress.setText(mDataMap.get("[~EMAIL]") + "");
            formViewHolder.tvWebsiteURL.setText("https://" + mDataMap.get("[~TAG]") + ".nowfloats.com");

            CharSequence charSequence = Html.fromHtml("By clicking on 'Create my site' you agree to our " +
                    "<a href=\"" + mContext.getString(R.string.settings_tou_url) + "\"><u>Terms</u></a> and <a href=\"" + mContext.getString(R.string.settings_privacy_url) + "\"><u>Privacy Policy</u></a>.");
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
            makeLinkClickable(spannableStringBuilder, charSequence);

            formViewHolder.tvtermAndPolicy.setMovementMethod(LinkMovementMethod.getInstance());
            formViewHolder.tvtermAndPolicy.setText(spannableStringBuilder);

            if (section.isAnimApplied()) {
                formViewHolder.btnCreateWebsite.setEnabled(false);
                formViewHolder.btnCreateWebsite.setClickable(false);
                formViewHolder.btnCreateWebsite.setTextColor(mContext.getResources().getColor(R.color.white));
                formViewHolder.btnCreateWebsite.setBackgroundResource(R.drawable.done_button_bg_grey);
            } else {
                formViewHolder.btnCreateWebsite.setEnabled(true);
                formViewHolder.btnCreateWebsite.setClickable(true);
                formViewHolder.btnCreateWebsite.setTextColor(mContext.getResources().getColor(R.color.white));
                formViewHolder.btnCreateWebsite.setBackgroundResource(R.drawable.done_button_bg);
            }


            formViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!section.isAnimApplied()) {

                        ((ChatViewActivity) mContext).showCustomDialog(
                                CustomDialogFragment.DialogFrom.CREATE_MY_SITE);
                    }
                }
            });

            formViewHolder.btnCreateWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    section.setIsAnimApplied(true);
                    formViewHolder.btnCreateWebsite.setEnabled(false);
                    formViewHolder.btnCreateWebsite.setClickable(false);
                    formViewHolder.btnCreateWebsite.setTextColor(mContext.getResources().getColor(R.color.white));
                    formViewHolder.btnCreateWebsite.setBackgroundResource(R.drawable.done_button_bg_grey);

                    mConfirmationCallback.onCardResponse(Constants.ConfirmationType.SUBMIT_FORM, null,
                            "",
                            "");
                }
            });


        } else if (holder instanceof AddressCardViewHolder) {
            AddressCardViewHolder cardViewHolder = (AddressCardViewHolder) holder;
            cardViewHolder.tvAddressText.setText(Html.fromHtml(getParsedPrefixPostfixText(section.getCardModel().getSections().get(1).getText())));
            cardViewHolder.tvAddrFooter.setText(Html.fromHtml(getParsedPrefixPostfixText(section.getCardModel().getCardFooter())));

            /*Glide.with(mContext)
                    .load(getParsedPrefixPostfixText(section.getCardModel().getSections().get(0).getUrl()))
                    .apply(new RequestOptions()
                    .placeholder(R.drawable.default_product_image)
                    .error(R.drawable.default_product_image))
                    .into(cardViewHolder.ivMap);*/

            Picasso.get()
                    .load(getParsedPrefixPostfixText(section.getCardModel().getSections().get(0).getUrl()))
                    .placeholder(R.drawable.default_product_image).into(cardViewHolder.ivMap);

            if (section.isShowDate()) {
                cardViewHolder.tvDateTime.setVisibility(View.VISIBLE);
                cardViewHolder.tvDateTime.setText(section.getDateTime());
            } else {
                cardViewHolder.tvDateTime.setVisibility(View.GONE);
            }
            if (!section.isAnimApplied()) {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.flip_in_anim);
                cardViewHolder.llBubbleContainer.setAnimation(animation);
                animation.start();
                section.setIsAnimApplied(true);
            }

        } else if (holder instanceof UnconfirmedAddressCardViewHolder) {
            UnconfirmedAddressCardViewHolder cardViewHolder = (UnconfirmedAddressCardViewHolder) holder;
            cardViewHolder.tvAddressText.setText(Html.fromHtml(getParsedPrefixPostfixText(section.getCardModel().getSections().get(1).getText())));

            cardViewHolder.tvConfirm.setClickable(true);
            cardViewHolder.tvConfirm.setEnabled(true);

            cardViewHolder.tvEdit.setClickable(true);
            cardViewHolder.tvEdit.setEnabled(true);


            /*Glide.with(mContext)
                    .load(getParsedPrefixPostfixText(section.getCardModel().getSections().get(0).getUrl()))
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.default_product_image)
                            .error(R.drawable.default_product_image))
                    .into(cardViewHolder.ivMap);*/


            Picasso.get()
                    .load(getParsedPrefixPostfixText(section.getCardModel().getSections().get(0).getUrl()))
                    .placeholder(R.drawable.default_product_image).into(cardViewHolder.ivMap);

            /*if (section.isShowDate()) {
                cardViewHolder.tvDateTime.setVisibility(View.VISIBLE);
                cardViewHolder.tvDateTime.setText(section.getDateTime());
            } else {
                cardViewHolder.tvDateTime.setVisibility(View.GONE);
            }*/

        } else if (holder instanceof TextViewHolder) {

            final TextViewHolder textViewHolder = (TextViewHolder) holder;


            CharSequence charSequence = Html.fromHtml(section.getText());
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
            makeLinkClickable(spannableStringBuilder, charSequence);

            textViewHolder.tvMessageText.setMovementMethod(LinkMovementMethod.getInstance());
            textViewHolder.tvMessageText.setText(spannableStringBuilder);


            if (section.isShowDate()) {
                textViewHolder.tvDateTime.setVisibility(View.VISIBLE);
                textViewHolder.tvDateTime.setText(section.getDateTime());
            } else {
                textViewHolder.tvDateTime.setVisibility(View.GONE);
            }
            if (!section.isFromRia()) {
                textViewHolder.llBubbleContainer.setVisibility(View.VISIBLE);
                ((LinearLayout) textViewHolder.itemView).setGravity(Gravity.RIGHT);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textViewHolder.llBubbleContainer.getLayoutParams();
                if (mChatSections != null && mChatSections.size() > 0 && position > 0 && mChatSections.get(position - 1).isFromRia()) {
                    textViewHolder.llBubbleContainer.setBackgroundResource(R.drawable.reply_main_bubble);
                    lp.setMargins(Utils.dpToPx(mContext, 60), 0, Utils.dpToPx(mContext, 5), 0);
                } else {
                    textViewHolder.llBubbleContainer.setBackgroundResource(R.drawable.reply_followup_bubble);
                    lp.setMargins(Utils.dpToPx(mContext, 60), 0, Utils.dpToPx(mContext, 15), 0);
                }
                if (!section.isAnimApplied()) {
                    Animation a = new TranslateAnimation(
                            Animation.ABSOLUTE, //from xType
                            -200,
                            Animation.ABSOLUTE, //to xType
                            0,
                            Animation.ABSOLUTE, //from yType
                            200,
                            Animation.ABSOLUTE, //to yType
                            0
                    );
                    a.setDuration(500);
                    textViewHolder.itemView.setAnimation(a);
                    a.start();
                    //Anim End
                    section.setIsAnimApplied(true);
                }

                textViewHolder.llBubbleContainer.setLayoutParams(lp);
                textViewHolder.tvMessageText.setTextColor(Color.parseColor("#ffffff"));
            } else {
                ((LinearLayout) textViewHolder.itemView).setGravity(Gravity.LEFT);
//                textViewHolder.tvMessageText.setTextColor(Color.parseColor("#808080"));
                textViewHolder.tvMessageText.setTextColor(Color.parseColor("#000000"));
                final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textViewHolder.llBubbleContainer.getLayoutParams();
                if (mChatSections != null && mChatSections.size() > 0 && position > 0 && mChatSections.get(position - 1).isFromRia()) {
                    textViewHolder.llBubbleContainer.setBackgroundResource(R.drawable.ria_followup_bubble);
                    lp.setMargins(Utils.dpToPx(mContext, 15), 0, Utils.dpToPx(mContext, 60), 0);
                } else {
                    textViewHolder.llBubbleContainer.setBackgroundResource(R.drawable.ria_main_bubble);
                    lp.setMargins(Utils.dpToPx(mContext, 5), 0, Utils.dpToPx(mContext, 60), 0);
                }

                textViewHolder.llBubbleContainer.setLayoutParams(lp);
                if (!section.isAnimApplied()) {
                    textViewHolder.llBubbleContainer.setVisibility(View.INVISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textViewHolder.llBubbleContainer.setVisibility(View.VISIBLE);
                            textViewHolder.llBubbleContainer
                                    .startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left));
                            section.setIsAnimApplied(true);
                        }
                    }, 500);
                } else {
                    textViewHolder.llBubbleContainer.setVisibility(View.VISIBLE);
                    textViewHolder.llBubbleContainer.setLayoutParams(lp);
                }


            }
        } else if (holder instanceof ImageViewHolder) {
            final ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            if (section.isShowDate()) {
                imageViewHolder.tvDateTime.setVisibility(View.VISIBLE);
                imageViewHolder.tvDateTime.setText(section.getDateTime());
            } else {
                imageViewHolder.tvDateTime.setVisibility(View.GONE);
            }
            if (section.isLoading()) {
                imageViewHolder.pbLoading.setVisibility(View.VISIBLE);
            } else {
                imageViewHolder.pbLoading.setVisibility(View.INVISIBLE);
            }
            if (section.getTitle() != null && !section.getTitle().trim().equals("")) {
                imageViewHolder.tvImageTitle.setText(section.getTitle());
            } else {
                imageViewHolder.tvImageTitle.setVisibility(View.GONE);
            }


            String imageURL = getParsedPrefixPostfixText(section.getUrl());

            if (!imageURL.contains("http")) {
//                imageURL = "file://"+section.getUrl();
                ((LinearLayout) imageViewHolder.itemView).setGravity(Gravity.LEFT);

                imageViewHolder.ivMainImage.setImageResource(R.drawable.site_sc_default);

                final android.view.ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) imageViewHolder.ivMainImage.getLayoutParams();
                layoutParams.height = (int) TARGET_WIDTH;
                layoutParams.width = (int) TARGET_WIDTH;
                imageViewHolder.ivMainImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                /*Glide.with(mContext)
                        .load(getParsedPrefixPostfixText(imageURL))
                        .apply(new RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.site_sc_default))
                        .into(imageViewHolder.ivMainImage);*/

                Picasso.get()
                        .load(getParsedPrefixPostfixText(imageURL))
                        .centerCrop()
                        .placeholder(R.drawable.site_sc_default).into(imageViewHolder.ivMainImage);


            } else {

                imageViewHolder.ivMainImage.setScaleType(ImageView.ScaleType.FIT_XY);

                ((LinearLayout) imageViewHolder.itemView).setGravity(Gravity.RIGHT);


                Picasso.get().load(imageURL).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {

                        //whatever algorithm here to compute size
                        final android.view.ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) imageViewHolder.ivMainImage.getLayoutParams();

                        if (bitmap.getWidth() > MAX_WIDTH || bitmap.getHeight() > MAX_WIDTH) {
                            if (!getParsedPrefixPostfixText(section.getUrl()).contains("http")) {

                                if (bitmap.getHeight() > bitmap.getWidth()) {
                                    float ratio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
                                    float widthFloat = ((float) MAX_WIDTH) * ratio;
                                    layoutParams.height = (int) MAX_WIDTH;
                                    layoutParams.width = (int) widthFloat;
                                } else {
                                    float ratio = (float) bitmap.getHeight() / (float) bitmap.getWidth();
                                    float heightFloat = ((float) MAX_WIDTH) * ratio;
                                    layoutParams.height = (int) heightFloat;
                                    layoutParams.width = (int) MAX_WIDTH;
                                }

                            } else {

                                float ratio = (float) bitmap.getHeight() / (float) bitmap.getWidth();
                                float heightFloat = ((float) MAX_WIDTH) * ratio;
                                layoutParams.height = (int) heightFloat;
                                layoutParams.width = (int) MAX_WIDTH;

                            }
                        } else {

                            layoutParams.height = (int) bitmap.getHeight();
                            layoutParams.width = (int) bitmap.getWidth();
                        }


                        imageViewHolder.ivMainImage.setLayoutParams(layoutParams);
//                    if(bitmap!=null && !bitmap.isRecycled())
//                        bitmap.recycle();
                        imageViewHolder.ivMainImage.setImageBitmap(bitmap);

                    }



                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        imageViewHolder.ivMainImage.setImageResource(R.drawable.site_sc_default);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        imageViewHolder.ivMainImage.setImageResource(R.drawable.site_sc_default);
                    }

                });
            }

            if (section.getCaption() != null && !section.getCaption().trim().equals("")) {
                imageViewHolder.tvImageCaption.setText(section.getCaption());
            } else {
                imageViewHolder.tvImageCaption.setVisibility(View.GONE);
            }
            if (!section.isFromRia()) {
                ((LinearLayout) imageViewHolder.itemView).setGravity(Gravity.RIGHT);
            } else {
                ((LinearLayout) imageViewHolder.itemView).setGravity(Gravity.LEFT);
                if (mChatSections != null && mChatSections.size() > 0 && position > 0 && mChatSections.get(position - 1).isFromRia()) {
                } else {
                }
            }

            imageViewHolder.ivMainImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ChatViewActivity) mContext).showImageDilaog(getParsedPrefixPostfixText(section.getUrl()));
                }
            });

        } else if (holder instanceof GifViewHolder) {
            GifViewHolder imageViewHolder = (GifViewHolder) holder;
            if (section.isShowDate()) {
                imageViewHolder.tvDateTime.setVisibility(View.VISIBLE);
                imageViewHolder.tvDateTime.setText(section.getDateTime());
            } else {
                imageViewHolder.tvDateTime.setVisibility(View.GONE);
            }
            if (section.getTitle() != null && !section.getTitle().trim().equals("")) {
                imageViewHolder.tvImageTitle.setText(section.getTitle());
            } else {
                imageViewHolder.tvImageTitle.setVisibility(View.GONE);
            }
            /*Glide.with(mContext)
                    .asGif()
                    .load(section.getUrl())
                    .apply(new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.default_product_image))
                    .into(imageViewHolder.ivMainImage);*/

            Picasso.get()
                    .load(section.getUrl())
                    .centerCrop()
                    .placeholder(R.drawable.default_product_image).into(imageViewHolder.ivMainImage);

            if (section.getCaption() != null && !section.getCaption().trim().equals("")) {
                imageViewHolder.tvImageCaption.setText(section.getCaption());
            } else {
                imageViewHolder.tvImageCaption.setVisibility(View.GONE);
            }
            if (!section.isFromRia()) {
                ((LinearLayout) imageViewHolder.itemView).setGravity(Gravity.RIGHT);
            } else {
                ((LinearLayout) imageViewHolder.itemView).setGravity(Gravity.LEFT);
                if (mChatSections != null && mChatSections.size() > 0 && position > 0 && mChatSections.get(position - 1).isFromRia()) {
                } else {
                }
            }
        } else if (holder instanceof EmbeddedHtmlViewHolder) {
            EmbeddedHtmlViewHolder embeddedHtmlViewHolder = (EmbeddedHtmlViewHolder) holder;
            embeddedHtmlViewHolder.tvTitle.setText(section.getTitle());
            embeddedHtmlViewHolder.wvEmbeddedHtml.getSettings().setJavaScriptEnabled(true);
            embeddedHtmlViewHolder.wvEmbeddedHtml.loadUrl(section.getUrl());
            if (section.isDisplayOpenInBrowserButton()) {
                embeddedHtmlViewHolder.tvOpenInBrowser.setVisibility(View.VISIBLE);
            }
            embeddedHtmlViewHolder.tvOpenInBrowser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Open in browser
                }
            });
            embeddedHtmlViewHolder.tvCaption.setText(section.getCaption());
            if (!section.isFromRia()) {
                ((LinearLayout) embeddedHtmlViewHolder.itemView).setGravity(Gravity.RIGHT);
            } else {
                ((LinearLayout) embeddedHtmlViewHolder.itemView).setGravity(Gravity.LEFT);
                if (mChatSections != null && mChatSections.size() > 0 && position > 0 && mChatSections.get(position - 1).isFromRia()) {
                } else {
                }
            }
        } else if (holder instanceof VideoViewHolder) {
            //TODO: set loading icon
            MediaController mediaControls = new MediaController(mContext);
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            videoViewHolder.tvTitle.setText(section.getTitle());

            if (section.isLoading()) {
                videoViewHolder.pbVideoLoading.setVisibility(View.VISIBLE);
            } else {
                videoViewHolder.pbVideoLoading.setVisibility(View.INVISIBLE);
            }

            try {
                videoViewHolder.vvMainVideo.setMediaController(mediaControls);
                videoViewHolder.vvMainVideo.setVideoURI(Uri.parse(section.getUrl()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!section.isFromRia()) {
                ((LinearLayout) videoViewHolder.itemView).setGravity(Gravity.RIGHT);
            } else {
                ((LinearLayout) videoViewHolder.itemView).setGravity(Gravity.LEFT);
                if (mChatSections != null && mChatSections.size() > 0 && position > 0 && mChatSections.get(position - 1).isFromRia()) {
                } else {
                }
            }
        } else if (holder instanceof AudioViewHolder) {

            final AudioViewHolder audioViewHolder = (AudioViewHolder) holder;

            if (!section.isFromRia()) {
                ((LinearLayout) audioViewHolder.itemView).setGravity(Gravity.RIGHT);
            } else {
                ((LinearLayout) audioViewHolder.itemView).setGravity(Gravity.LEFT);
                if (mChatSections != null && mChatSections.size() > 0 && position > 0 && mChatSections.get(position - 1).isFromRia()) {
                } else {
                }
            }

            if (section.isLoading()) {
                audioViewHolder.pbAudioLoading.setVisibility(View.VISIBLE);
                audioViewHolder.ppvAudio.setEnabled(false);
            } else {
                audioViewHolder.pbAudioLoading.setVisibility(View.GONE);
                audioViewHolder.ppvAudio.setEnabled(true);
            }

            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(section.getUrl());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int mCurrentPosition = (mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100;
                    audioViewHolder.sbAudio.setProgress(mCurrentPosition);
                    handler.postDelayed(this, 1000);
                }
            };
            audioViewHolder.ppvAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        handler.removeCallbacks(null);
                    } else {
                        mediaPlayer.start();
                        handler.post(runnable);
                    }
                }
            });


        } else if (holder instanceof CarouselViewHolder) {
            final CarouselViewHolder carouselViewHolder = (CarouselViewHolder) holder;
            final RiaCardModel riaCardModel = section.getCardModel();
            final CarouselAdapter adapter = new CarouselAdapter(mContext, riaCardModel.getSections().get(0).getItems(), mDataMap);
            carouselViewHolder.rvCarousel.setAdapter(adapter);
            carouselViewHolder.pageIndicatorView.setCount(riaCardModel.getSections().get(0).getItems().size());


            if (mChatSections.get(position).getCardModel().getButtons() != null && mChatSections.get(position).getCardModel().getButtons().size() == 1) {
                carouselViewHolder.tvEdit.setText("");
                carouselViewHolder.tvEdit.setVisibility(View.GONE);
                carouselViewHolder.tvConfirm.setText(mChatSections.get(position).getCardModel().getButtons().get(0).getButtonText());
                carouselViewHolder.tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        carouselViewHolder.tvConfirm.setClickable(false);
                        carouselViewHolder.tvConfirm.setEnabled(false);

                        carouselViewHolder.llConfirm.setVisibility(View.GONE);
                        carouselViewHolder.tvSubmit.setVisibility(View.VISIBLE);

                        int position = ((LinearLayoutManager) carouselViewHolder.rvCarousel.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        Items items = riaCardModel.getSections().get(0).getItems().get(position);

                        mConfirmationCallback.onCardResponse(Constants.ConfirmationType.FB_PAGE, riaCardModel.getButtons().get(0),
                                items.getTitle(),
                                riaCardModel.getButtons().get(0).getNextNodeId());
                    }
                });
            } else {
                carouselViewHolder.tvEdit.setVisibility(View.VISIBLE);
                carouselViewHolder.tvConfirm.setText(mChatSections.get(position).getCardModel().getButtons().get(0).getButtonText());
                carouselViewHolder.tvEdit.setText(mChatSections.get(position).getCardModel().getButtons().get(1).getButtonText());
                carouselViewHolder.tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        carouselViewHolder.tvConfirm.setClickable(false);
                        carouselViewHolder.tvConfirm.setEnabled(false);

                        carouselViewHolder.llConfirm.setVisibility(View.GONE);
                        carouselViewHolder.tvSubmit.setVisibility(View.VISIBLE);

                        int position = ((LinearLayoutManager) carouselViewHolder.rvCarousel.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        Items items = riaCardModel.getSections().get(0).getItems().get(position);


                        mConfirmationCallback.onCardResponse(Constants.ConfirmationType.FB_PAGE, riaCardModel.getButtons().get(0),
                                items.getTitle(),
                                riaCardModel.getButtons().get(0).getNextNodeId());
                    }
                });

                carouselViewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        carouselViewHolder.tvEdit.setClickable(false);
                        carouselViewHolder.tvEdit.setEnabled(false);

                        carouselViewHolder.llConfirm.setVisibility(View.GONE);
                        carouselViewHolder.tvSubmit.setVisibility(View.VISIBLE);

                        int position = ((LinearLayoutManager) carouselViewHolder.rvCarousel.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        Items items = riaCardModel.getSections().get(0).getItems().get(position);

                        mConfirmationCallback.onCardResponse(Constants.ConfirmationType.FB_PAGE, riaCardModel.getButtons().get(1),
                                items.getTitle(),
                                riaCardModel.getButtons().get(1).getNextNodeId());
                    }
                });
            }

            carouselViewHolder.rvCarousel.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    int action = e.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_MOVE:
                            carouselViewHolder.rvCarousel.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                    }
                    return false;
                }

                @Override
                public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });
            carouselViewHolder.rvCarousel.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int position = ((LinearLayoutManager) carouselViewHolder.rvCarousel.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        if (position > -1) {
                            carouselViewHolder.pageIndicatorView.setSelection(position);
                            adapter.notifyVisibleItemChanged(position);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mChatSections.size();
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView tvDateTime;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            tvDateTime = (TextView) itemView.findViewById(R.id.tv_date_time);
        }
    }


    private class ImageViewHolder extends RecyclerView.ViewHolder {

        TextView tvImageTitle, tvDateTime;
        ImageView ivMainImage;
        TextView tvImageCaption;
        ProgressBar pbLoading;


        public ImageViewHolder(View itemView) {
            super(itemView);

            tvImageTitle = (TextView) itemView.findViewById(R.id.tv_title);
            ivMainImage = (ImageView) itemView.findViewById(R.id.iv_main_row_image);
            tvImageCaption = (TextView) itemView.findViewById(R.id.tv_caption);
            pbLoading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
            tvDateTime = (TextView) itemView.findViewById(R.id.tv_date_time);

        }
    }

    protected void makeLinkClickable(SpannableStringBuilder sp, CharSequence charSequence) {

        URLSpan[] spans = sp.getSpans(0, charSequence.length(), URLSpan.class);

        for (final URLSpan urlSpan : spans) {

            ClickableSpan clickableSpan = new ClickableSpan() {
                public void onClick(View view) {
                    Log.e("urlSpan", urlSpan.getURL());

                    Intent intent = new Intent(mContext, ChatWebViewActivity.class);
                    intent.putExtra(ChatWebViewActivity.KEY_URL, urlSpan.getURL());
                    mContext.startActivity(intent);

                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            };

            sp.setSpan(clickableSpan, sp.getSpanStart(urlSpan),
                    sp.getSpanEnd(urlSpan), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }

    }

    public class TextViewHolder extends RecyclerView.ViewHolder {

        TextView tvMessageText, tvDateTime;
        View itemView;
        public LinearLayout llBubbleContainer;

        public TextViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvMessageText = (TextView) itemView.findViewById(R.id.tv_message_text);
            tvDateTime = (TextView) itemView.findViewById(R.id.tv_date_time);
            llBubbleContainer = (LinearLayout) itemView.findViewById(R.id.ll_bubble_container);
        }
    }

    private class CardViewHolder extends RecyclerView.ViewHolder {

        TextView tvConfirmationText, tvDateTime, tvConfirmedHintText, tvConfirmSubText, tvTotalCount, tvCurrentPos;
        View itemView;
        LinearLayout llBubbleContainer, llCardPos;

        public CardViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            this.tvConfirmationText = (TextView) itemView.findViewById(R.id.tv_confirmed_business_text);
            this.tvConfirmSubText = (TextView) itemView.findViewById(R.id.tv_confirmation_sub_text);
            this.tvConfirmedHintText = (TextView) itemView.findViewById(R.id.tv_confirmed_hint_text);
            this.tvCurrentPos = (TextView) itemView.findViewById(R.id.tvCurrentPos);
            this.tvTotalCount = (TextView) itemView.findViewById(R.id.tvTotalCount);
            this.tvDateTime = (TextView) itemView.findViewById(R.id.tv_date_time);
            this.llBubbleContainer = (LinearLayout) itemView.findViewById(R.id.ll_bubble_container);
            this.llCardPos = (LinearLayout) itemView.findViewById(R.id.llCardPos);
        }
    }

    private class OTPViewHolder extends RecyclerView.ViewHolder {

        TextView tvConfirmationTitle;
        EditText etOTPConfirmation;
        TextView tvConfirm, tvEdit, tvSubmit;
        View itemView;
        LinearLayout llConfirm;

        public OTPViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.etOTPConfirmation = (EditText) itemView.findViewById(R.id.et_otp_confirm_text);
            this.tvConfirmationTitle = (TextView) itemView.findViewById(R.id.tv_confirmation_title);
            this.tvConfirm = (TextView) itemView.findViewById(R.id.tv_confirm);
            this.tvEdit = (TextView) itemView.findViewById(R.id.tv_edit);
            this.llConfirm = (LinearLayout) itemView.findViewById(R.id.llConfirm);
            this.tvSubmit = (TextView) itemView.findViewById(R.id.tvSubmit);

        }
    }

    private class FormViewHolder extends RecyclerView.ViewHolder {

        TextView tvBusinessName, tvPhoneNumber, tvCategory, tvAddress, tvEmailAddress, tvWebsiteURL, tvtermAndPolicy;
        View itemView;
        Button btnCreateWebsite;

        public FormViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.tvBusinessName = (TextView) itemView.findViewById(R.id.tvBusinessName);
            this.tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
            this.tvCategory = (TextView) itemView.findViewById(R.id.tvCategory);
            this.tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            this.tvEmailAddress = (TextView) itemView.findViewById(R.id.tvEmailAddress);
            this.tvWebsiteURL = (TextView) itemView.findViewById(R.id.tvWebsiteURL);
            this.tvtermAndPolicy = (TextView) itemView.findViewById(R.id.tvtermAndPolicy);
            this.btnCreateWebsite = (Button) itemView.findViewById(R.id.btnCreateWebsite);

        }
    }

    private class UnConfirmedCardViewHolder extends RecyclerView.ViewHolder {

        TextView tvConfirmationTitle, tvConfirmationText, tvConfirmSubText;
        TextView tvConfirm, tvEdit, tvSubmit;
        View itemView;
        //        ProgressBar progress;
        LinearLayout llConfirm;

        public UnConfirmedCardViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.tvConfirmationText = (TextView) itemView.findViewById(R.id.tv_confirmation_text);
            this.tvConfirmationTitle = (TextView) itemView.findViewById(R.id.tv_confirmation_title);
            this.tvConfirmSubText = (TextView) itemView.findViewById(R.id.tv_confirmation_sub_text);
            this.tvConfirm = (TextView) itemView.findViewById(R.id.tv_confirm);
            this.tvEdit = (TextView) itemView.findViewById(R.id.tv_edit);
            this.tvSubmit = (TextView) itemView.findViewById(R.id.tvSubmit);
            this.llConfirm = (LinearLayout) itemView.findViewById(R.id.llConfirm);
//            this.progress = (ProgressBar) itemView.findViewById(R.id.progressBar);

        }
    }

    private class AddressCardViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddressText, tvDateTime, tvAddrFooter;
        View itemView;
        LinearLayout llBubbleContainer;
        ImageView ivMap;

        public AddressCardViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            ivMap = (ImageView) itemView.findViewById(R.id.iv_map_data);

            this.tvAddressText = (TextView) itemView.findViewById(R.id.tv_confirmation_text);
            this.tvAddrFooter = (TextView) itemView.findViewById(R.id.tv_addr_footer);
            this.tvDateTime = (TextView) itemView.findViewById(R.id.tv_date_time);
            this.llBubbleContainer = (LinearLayout) itemView.findViewById(R.id.ll_bubble_container);
        }
    }

    private class UnconfirmedAddressCardViewHolder extends RecyclerView.ViewHolder {

        ImageView ivMap;
        TextView tvAddressText, tvConfirm, tvEdit;
        View itemView;


        public UnconfirmedAddressCardViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            this.ivMap = (ImageView) itemView.findViewById(R.id.iv_map_data);
            this.tvAddressText = (TextView) itemView.findViewById(R.id.tv_address_text);
            this.tvEdit = (TextView) itemView.findViewById(R.id.tv_addr_edit);
            this.tvConfirm = (TextView) itemView.findViewById(R.id.tv_addr_confirm);

            this.tvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvConfirm.setEnabled(false);
                    tvConfirm.setClickable(false);
                    mConfirmationCallback.onCardResponse(Constants.ConfirmationType.ADDRESS_ENTRY, mChatSections.get(getAdapterPosition()).getCardModel().getButtons().get(0),
                            tvAddressText.getText().toString(),
                            mChatSections.get(getAdapterPosition()).getCardModel().getButtons().get(0).getNextNodeId());
                }
            });

            this.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvEdit.setEnabled(false);
                    tvEdit.setClickable(false);
                    mConfirmationCallback.onCardResponse(Constants.ConfirmationType.ADDRESS_ENTRY, mChatSections.get(getAdapterPosition()).getCardModel().getButtons().get(1),
                            tvAddressText.getText().toString(),
                            mChatSections.get(getAdapterPosition()).getCardModel().getButtons().get(1).getNextNodeId());
                }
            });

        }
    }

    private class GraphViewholder extends RecyclerView.ViewHolder {

        public GraphViewholder(View itemView) {
            super(itemView);
        }
    }

    private class GifViewHolder extends RecyclerView.ViewHolder {

        TextView tvImageTitle, tvDateTime;
        ImageView ivMainImage;
        TextView tvImageCaption;

        public GifViewHolder(View itemView) {
            super(itemView);

            tvImageTitle = (TextView) itemView.findViewById(R.id.tv_title);
            ivMainImage = (ImageView) itemView.findViewById(R.id.iv_main_row_image);
            tvDateTime = (TextView) itemView.findViewById(R.id.tv_date_time);
            tvImageCaption = (TextView) itemView.findViewById(R.id.tv_caption);
        }
    }

    private class AudioViewHolder extends RecyclerView.ViewHolder {
        PlayPauseView ppvAudio;
        SeekBar sbAudio;
        ProgressBar pbAudioLoading;

        public AudioViewHolder(View itemView) {
            super(itemView);

            ppvAudio = (PlayPauseView) itemView.findViewById(R.id.ppv_audio);
            sbAudio = (SeekBar) itemView.findViewById(R.id.sb_audio);
            pbAudioLoading = (ProgressBar) itemView.findViewById(R.id.pb_audio_loading);
            sbAudio.setEnabled(false);
        }
    }

    private class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        VideoView vvMainVideo;
        ProgressBar pbVideoLoading;

        public VideoViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            vvMainVideo = (VideoView) itemView.findViewById(R.id.vv_main_video);
            pbVideoLoading = (ProgressBar) itemView.findViewById(R.id.pb_video_loading);
        }
    }

    private class EmbeddedHtmlViewHolder extends RecyclerView.ViewHolder {

        WebView wvEmbeddedHtml;
        TextView tvTitle, tvOpenInBrowser, tvCaption;

        public EmbeddedHtmlViewHolder(View itemView) {
            super(itemView);

            wvEmbeddedHtml = (WebView) itemView.findViewById(R.id.wv_embedded_html);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvOpenInBrowser = (TextView) itemView.findViewById(R.id.tv_open_in_browser);
            tvCaption = (TextView) itemView.findViewById(R.id.tv_caption);

        }
    }

    public class TypingViewHolder extends RecyclerView.ViewHolder {
        AVLoadingIndicatorView ldiDots;
        public RelativeLayout rlLoadingDots;

        public TypingViewHolder(View itemView) {
            super(itemView);
            ldiDots = (AVLoadingIndicatorView) itemView.findViewById(R.id.ldi_dots);
            rlLoadingDots = (RelativeLayout) itemView.findViewById(R.id.rl_typing_container);
            ldiDots.smoothToShow();
            Section section = mChatSections.get(mChatSections.size() - 1);
            if (!section.isFromRia()) {
                ((LinearLayout) itemView).setGravity(Gravity.RIGHT);
            } else {
                if (mChatSections != null && mChatSections.size() > 0 && mChatSections.get(mChatSections.size() - 1).isFromRia()) {
                }
            }
        }
    }


    private String getParsedPrefixPostfixText(String text) {
        if (text == null)
            return null;
        Matcher m = Pattern.compile("\\[~(.*?)\\]").matcher(text);
        while (m.find()) {
            if (mDataMap.get(m.group()) != null) {
                text = text.replace(m.group(), mDataMap.get(m.group()));
            }
        }
        return text;
    }

    private class CarouselViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rvCarousel;
        PageIndicatorView pageIndicatorView;
        LinearLayout llConfirm;
        TextView tvEdit, tvConfirm, tvSubmit;

        public CarouselViewHolder(View v) {
            super(v);
            rvCarousel = (RecyclerView) v.findViewById(R.id.rv_carousel);
            pageIndicatorView = (PageIndicatorView) v.findViewById(R.id.ps_indicator);
            this.tvEdit = (TextView) itemView.findViewById(R.id.tv_edit);
            this.tvConfirm = (TextView) itemView.findViewById(R.id.tv_confirm);
            this.tvSubmit = (TextView) itemView.findViewById(R.id.tvSubmit);
            this.llConfirm = (LinearLayout) itemView.findViewById(R.id.llConfirm);
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(rvCarousel);
        }
    }
}
