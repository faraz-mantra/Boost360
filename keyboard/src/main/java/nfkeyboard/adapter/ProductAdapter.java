package nfkeyboard.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Paint;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.inputmethod.keyboard.top.UpdateActionBarEvent;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import io.separ.neural.inputmethod.indic.R;
import io.separ.neural.inputmethod.slash.EventBusExt;
import nfkeyboard.interface_contracts.ItemClickListener;
import nfkeyboard.models.AllSuggestionModel;
import nfkeyboard.util.MixPanelUtils;

/**
 * Created by Admin on 27-02-2018.
 */

class ProductAdapter extends BaseAdapter<AllSuggestionModel> {

    private final String LABEL = "copy";
    private final ItemClickListener listener;
    private final EventBusHandler mEventHandler;

    ViewGroup parent;
    private ImageHolder mHolder;
    private DecimalFormat df;

    ProductAdapter(Context context, ItemClickListener listener) {
        super(context, listener);
        this.listener = listener;
        this.mEventHandler = new EventBusHandler();
        this.mEventHandler.register();
        df = new DecimalFormat("#,##,##,##,##,##,##,###.##");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        this.parent = parent;
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_product_v1, parent, false);
        mHolder = new ImageHolder(view);
        return mHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, AllSuggestionModel suggestion) {
        if (holder instanceof ImageHolder) {
            ImageHolder myHolder = (ImageHolder) holder;
            myHolder.copyButton.setText(R.string.share_link);
            myHolder.makeOfferButton.setText(R.string.create_offer);
            myHolder.cancelButton.setText(R.string.cancel);
            myHolder.cancelBtn.setText(R.string.cancel);
            myHolder.createButton.setText(R.string.share);
            //myHolder.tvBack.setText(R.string.back);
            myHolder.offerCurrencyTv.setText(R.string.currency);
            myHolder.productPriceTv.setText(R.string.price);
            myHolder.doneButton.setText(R.string.tv_done);
            mHolder.validity = mContext.getResources().getStringArray(R.array.validity_items);
            if (mHolder.validity != null && mHolder.validity.length > 0) {
                mHolder.selectedValidityTv.setText(mHolder.validity[0]);
            }
            myHolder.setModelData(suggestion);
        }
    }

    @Override
    void unRegisterEventBus() {
        super.unRegisterEventBus();
        if (mEventHandler != null) {
            this.mEventHandler.unregister();
        }
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
        private TextView nameTv, priceTv, discountTv, descriptionTv, productNameTv, productPriceTv, productDiscountTv,
                keyboardCurrencyTv, offerCurrencyTv, selectedQuantityTv, selectedValidityTv/*, tvBack*/;
        private ImageView productImage/*, productIv*/;
        private AllSuggestionModel dataModel;
        private ConstraintLayout constraintLayout, /*constraintLayoutFlipped,*/
                offerCl, productsKeyboardCl;
        private Button copyButton, makeOfferButton, createButton, cancelButton, oneButton,
                twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton, eightButton,
                nineButton, zeroButton, decimalButton, doneButton, cancelBtn;
        private EditText offerPriceEt, editText;
        private ListPopupWindow listPopupWindowValidity, listPopupWindowQuantity;
        private LinearLayout linearLayoutQuantity, linearLayoutValidity;
        private ImageButton delButton;
        private boolean hasDecimal, flippable;
        private int focusId, availableUnits;
        private String temp = null;
        private String[] validity, quantity;
        private int MIN_OFFER_PRICE = 10;

        @SuppressLint("ClickableViewAccessibility")
        ImageHolder(final View itemView) {
            super(itemView);
            setViewLayoutSize(itemView);
            flippable = true;
            productImage = itemView.findViewById(R.id.imageView);
            nameTv = itemView.findViewById(R.id.tv_name);
            priceTv = itemView.findViewById(R.id.tv_price);
            discountTv = itemView.findViewById(R.id.tv_discount);
            descriptionTv = itemView.findViewById(R.id.tv_description);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            //constraintLayoutFlipped = itemView.findViewById(R.id.constraintLayoutFlipped);
            //constraintLayoutFlipped.setVisibility(View.GONE);
            productsKeyboardCl = itemView.findViewById(R.id.cl_products_keyboard);
            productsKeyboardCl.setVisibility(View.GONE);

            offerCl = itemView.findViewById(R.id.cl_offer);
            //productIv = itemView.findViewById(R.id.iv_product);
            productNameTv = itemView.findViewById(R.id.tv_product_name);
            productPriceTv = itemView.findViewById(R.id.tv_product_price);
            productDiscountTv = itemView.findViewById(R.id.tv_discounted_price);

            offerPriceEt = itemView.findViewById(R.id.et_offer_price);
            createButton = itemView.findViewById(R.id.button_create);
            cancelButton = itemView.findViewById(R.id.button_cancel);
            //tvBack = itemView.findViewById(R.id.back);

            oneButton = itemView.findViewById(R.id.btn_one);
            twoButton = itemView.findViewById(R.id.btn_two);
            threeButton = itemView.findViewById(R.id.btn_three);
            fourButton = itemView.findViewById(R.id.btn_four);
            fiveButton = itemView.findViewById(R.id.btn_five);
            sixButton = itemView.findViewById(R.id.btn_six);
            sevenButton = itemView.findViewById(R.id.btn_seven);
            eightButton = itemView.findViewById(R.id.btn_eight);
            nineButton = itemView.findViewById(R.id.btn_nine);
            zeroButton = itemView.findViewById(R.id.btn_zero);
            decimalButton = itemView.findViewById(R.id.btn_decimal);

            keyboardCurrencyTv = itemView.findViewById(R.id.tv_keyboard_currency);
            offerCurrencyTv = itemView.findViewById(R.id.tv_offer_currency);

            doneButton = itemView.findViewById(R.id.button_done);
            delButton = itemView.findViewById(R.id.ib_del);
            cancelBtn = itemView.findViewById(R.id.btn_cancel);
            hasDecimal = false;

            editText = itemView.findViewById(R.id.editText);

            linearLayoutQuantity = itemView.findViewById(R.id.ll_quantity);
            linearLayoutValidity = itemView.findViewById(R.id.ll_validity);

            selectedQuantityTv = itemView.findViewById(R.id.tv_selected_quantity);
            selectedValidityTv = itemView.findViewById(R.id.tv_selected_validity);

            validity = mContext.getResources().getStringArray(R.array.validity_items);
            quantity = new String[]{"1", "2", "3", "4", "5"};
            ListPopupWindowSetup(validity, quantity);

            oneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnButtonClick("1");
                }
            });

            twoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnButtonClick("2");
                }
            });

            threeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnButtonClick("3");
                }
            });

            fourButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnButtonClick("4");
                }
            });

            fiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnButtonClick("5");
                }
            });

            sixButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnButtonClick("6");
                }
            });

            sevenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnButtonClick("7");
                }
            });

            eightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnButtonClick("8");
                }
            });

            nineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnButtonClick("9");
                }
            });

            zeroButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnButtonClick("0");
                }
            });

            decimalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int cursorStart = editText.getSelectionStart();
                    if (cursorStart == 1 && editText.getText().toString().charAt(0) == '0') {
                        editText.append(".");
                        hasDecimal = true;
                    } else if (cursorStart == editText.getText().toString().length() && editText.length() != 0) {
                        if (!hasDecimal) {
                            editText.append(".");
                            hasDecimal = true;
                        }
                    } else if (editText.length() == 0) {
                        editText.append("0.");
                        hasDecimal = true;
                    } else {
                        if (!hasDecimal) {
                            editText.setText(
                                    editText.getText().insert(cursorStart, ".")
                            );
                            editText.setSelection(cursorStart + 1);
                            hasDecimal = true;
                        }
                    }
                }
            });

            delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteText();
                }
            });

            delButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    editText.setText("");
                    editText.setSelection(0);
                    hasDecimal = false;
                    return true;
                }
            });

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (offerPriceEt.getId() == focusId) {
                        if (editText.getText().length() != 0) {
                            if (!(editText.getText().length() == 1 && editText.getText().toString().charAt(0) == '0')) {
                                String valPrice = editText.getText().toString().replace(",", "");
                                if (Double.valueOf(valPrice) >= MIN_OFFER_PRICE) {
                                    offerPriceEt.setText(editText.getText().toString());
                                    editText.clearFocus();
                                    editText.setText("");
                                    productsKeyboardCl.setVisibility(View.GONE);
                                    offerCl.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(mContext, "Offer price cannot be less than 10", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(mContext, "Offer price cannot be less than 10", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(mContext, "Offer price cannot be less than 10", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editText.clearFocus();
                    editText.setText("");
                    productsKeyboardCl.setVisibility(View.GONE);
                    offerCl.setVisibility(View.VISIBLE);
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AnimationFade(false);
                }
            });

            copyButton = itemView.findViewById(R.id.buttonCopy);
            copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (TextUtils.isEmpty(dataModel.getUrl())) {
                        Toast.makeText(mContext, "Invalid Link", Toast.LENGTH_SHORT).show();
                    } else {
                        String url = onCopyClick(dataModel);
                        if (!url.equalsIgnoreCase("")) {
                            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(LABEL, url);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.link_copied), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.invalid), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            makeOfferButton = itemView.findViewById(R.id.buttonMakeOffer);
            makeOfferButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_CREATE_OFFER, null);
                    AnimationFade(true);
                }
            });

            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_SHARE_OFFER, null);
                    dataModel.setQuantity(Integer.valueOf(selectedQuantityTv.getText().toString()));
                    dataModel.setMaxUsage(Integer.valueOf(selectedQuantityTv.getText().toString()));
                    String text = selectedValidityTv.getText().toString();
                    String textToReplace = text.indexOf("Hrs") > 0 ? "Hrs" : text.indexOf("घंटे") > 0 ? "घंटे" : "";
                    String linkExpiryTime = null;
                    if (textToReplace.trim().length() > 0) {
                        int validity = Integer.valueOf(selectedValidityTv.getText().toString().replaceAll(textToReplace, "").trim());
                        linkExpiryTime = null;
                        switch (validity) {
                            case 24:
                                linkExpiryTime = dateFormatter(24);
                                break;
                            case 36:
                                linkExpiryTime = dateFormatter(36);
                                break;
                            case 48:
                                linkExpiryTime = dateFormatter(48);
                                break;
                            case 96:
                                linkExpiryTime = dateFormatter(96);
                                break;
                        }
                    }
                    dataModel.setLinkExpiryDateTime(linkExpiryTime);
                    if (offerPriceEt.getText().toString().equals("")) {
                        dataModel.setAmount(0.0f);
                    } else {
                        dataModel.setAmount(Float.valueOf(offerPriceEt.getText().toString().replaceAll(",", "")));
                    }
                    dataModel.setP_id(dataModel.getP_id());
                    listener.onCreateProductOfferClick(dataModel);

                    AnimationFlip(true);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AnimationFlip(false);
                }

            });

            offerPriceEt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    offerCl.setVisibility(View.GONE);
                    editText.requestFocus();
                    if (offerPriceEt.getText().toString().length() != 0) {
                        editText.setText(offerPriceEt.getText());
                        editText.setSelection(offerPriceEt.getText().length());
                    }
                    productsKeyboardCl.setVisibility(View.VISIBLE);
                    focusId = offerPriceEt.getId();
                }
            });

        }

        void setModelData(AllSuggestionModel model) {
            dataModel = model;

            if (!TextUtils.isEmpty(model.getImageUrl()) && !model.getImageUrl().equalsIgnoreCase("null")) {
                Glide.with(mContext).load(model.getImageUrl()).into(productImage);
            } else {
                Glide.with(mContext).load(R.drawable.default_product_image).into(productImage);
            }

            //productPriceTv.setText(MethodUtils.fromHtml(String.format(mContext.getResources().getString(R.string.tv_price) + "<br> %s <b>%s</b>", model.getCurrencyCode(), model.getPrice())));

            //priceTv.setText(MethodUtils.fromHtml(String.format(mContext.getResources().getString(R.string.tv_price) + "%s <b>%s</b>", model.getCurrencyCode(), model.getPrice())));
            //discountTv.setText(MethodUtils.fromHtml(String.format(mContext.getResources().getString(R.string.tv_discount) + "%s <b>%s</b>", model.getCurrencyCode(), model.getDiscount())));

            //descriptionTv.setText(MethodUtils.fromHtml(String.format(mContext.getResources().getString(R.string.tv_description) + "<b>%s</b>", model.getDescription())));
            //nameTv.setText(MethodUtils.fromHtml(String.format(mContext.getResources().getString(R.string.tv_name) + " <b>%s</b>", model.getText())));

            nameTv.setText(model.getText());
            descriptionTv.setText(model.getDescription());

            try {
                double price = TextUtils.isEmpty(model.getPrice()) ? 0 : Double.valueOf(model.getPrice());
                double discount = TextUtils.isEmpty(model.getDiscount()) ? 0 : Double.valueOf(model.getDiscount());

                String formattedPrice = df.format(price - discount);
                priceTv.setText(String.valueOf(model.getCurrencyCode() + " " + formattedPrice));
                productPriceTv.setText(String.valueOf(model.getCurrencyCode() + " " + formattedPrice));

                if (discount != 0) {
                    discountTv.setVisibility(View.VISIBLE);
                    productDiscountTv.setVisibility(View.VISIBLE);

                    formattedPrice = df.format(price);

                    discountTv.setPaintFlags(discountTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    productDiscountTv.setPaintFlags(discountTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    discountTv.setText(String.valueOf(model.getCurrencyCode() + " " + formattedPrice));
                    productDiscountTv.setText(String.valueOf(model.getCurrencyCode() + " " + formattedPrice));
                } else {
                    discountTv.setVisibility(View.INVISIBLE);
                    productDiscountTv.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                discountTv.setVisibility(View.INVISIBLE);
                productDiscountTv.setVisibility(View.INVISIBLE);
                e.printStackTrace();
            }

            productNameTv.setText(model.getText());

            keyboardCurrencyTv.setText(model.getCurrencyCode());
            offerCurrencyTv.setText(model.getCurrencyCode());
            availableUnits = model.getAvailableUnits();

            if (Double.valueOf(model.getPrice()) >= MIN_OFFER_PRICE) {
                offerPriceEt.setText(model.getPrice());
            } else {
                offerPriceEt.setText("10");
            }

            String[] items;

            if (availableUnits > 0) {
                items = new String[availableUnits];

                for (int i = 1; i <= availableUnits; i++) {
                    items[i - 1] = Integer.toString(i);
                }
            } else {
                items = new String[10];

                for (int i = 1; i <= 10; i++) {
                    items[i - 1] = Integer.toString(i);
                }
            }

            ListPopupWindowSetup(validity, items);
        }

        void OnButtonClick(String number) {
            int cursorStart = editText.getSelectionStart();

            if (editText.getText().length() == 1 && editText.getText().toString().charAt(0) == '0') {
                if (!number.equals("0")) {
                    editText.setText(number);
                    editText.setSelection(cursorStart);
                }
            } else if (cursorStart == editText.getText().toString().length()) {
                temp = editText.getText().toString().replaceAll(",", "") + number;
                String yourFormattedString = df.format(Double.valueOf(temp));
                editText.setText(yourFormattedString);
                editText.setSelection(yourFormattedString.length());
            } else if (editText.length() == 0) {
                editText.append(number);
            } else {
                temp = editText.getText().insert(cursorStart, number).toString();
                temp = editText.getText().toString().replaceAll(",", "");
                String yourFormattedString = df.format(Double.valueOf(temp));
                editText.setText(yourFormattedString);
                editText.setSelection(cursorStart + 1);
            }
        }

        void AnimationFlip(final boolean endShare) {

            if (constraintLayout.getVisibility() == View.VISIBLE && !endShare) {
                //constraintLayout.setVisibility(View.GONE);
                //constraintLayoutFlipped.setVisibility(View.VISIBLE);
                //constraintLayoutFlipped.setAlpha(1);
                //offerCl.setVisibility(View.GONE);
            } else {
                //constraintLayout.setVisibility(View.VISIBLE);
                //constraintLayoutFlipped.setVisibility(View.GONE);
                //constraintLayoutFlipped.setAlpha(1);
                //offerCl.setVisibility(View.GONE);

                /*if (endShare)
                {
                    flippable = true;
                }*/
            }

//            try {
//                itemView.setCameraDistance(4000f);
//                if (flippable || endShare) {
//                    final AnimatorSet animatorSetFlipIn = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.flip_in);
//                    AnimatorSet animatorSetFlipOut = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.flip_out);
//                    animatorSetFlipIn.setTarget(itemView);
//                    animatorSetFlipOut.setTarget(itemView);
//
//                    animatorSetFlipOut.start();
//
//                    animatorSetFlipOut.addListener(new Animator.AnimatorListener() {
//                        @Override
//                        public void onAnimationStart(Animator animator) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animator) {
//                            if (constraintLayout.getVisibility() == View.VISIBLE && !endShare) {
//                                constraintLayout.setVisibility(View.GONE);
//                                constraintLayoutFlipped.setVisibility(View.VISIBLE);
//                                constraintLayoutFlipped.setAlpha(1);
//                                offerCl.setVisibility(View.GONE);
//                            } else {
//                                constraintLayout.setVisibility(View.VISIBLE);
//                                constraintLayoutFlipped.setVisibility(View.GONE);
//                                constraintLayoutFlipped.setAlpha(1);
//                                offerCl.setVisibility(View.GONE);
//                                if (endShare) {
//                                    flippable = true;
//                                }
//                            }
//                            animatorSetFlipIn.start();
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animator) {
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animator animator) {
//
//                        }
//                    });
//                }
//            } catch (Exception e) {
//                Log.d("here", e.toString());
//            }


        }

        void AnimationFade(final boolean reversed) {

            flippable = false;

            if (reversed) {
                //constraintLayoutFlipped.setVisibility(View.GONE);
                offerCl.setVisibility(View.VISIBLE);

            } else {
                offerCl.setVisibility(View.GONE);

                //constraintLayoutFlipped.setVisibility(View.VISIBLE);
                flippable = true;
            }

            /*ObjectAnimator objectAnimatorFadeOut = ObjectAnimator.ofFloat(
                    (reversed) ? constraintLayoutFlipped : offerCl,
                    "alpha",
                    1f,
                    0f
            );*/
//            objectAnimatorFadeOut.setDuration(200);
//
//            final ObjectAnimator objectAnimatorFadeIn = ObjectAnimator.ofFloat(
//                    (reversed) ? offerCl : constraintLayoutFlipped,
//                    "alpha",
//                    0f,
//                    1f
//            );
//            objectAnimatorFadeIn.setDuration(200);
//
//            objectAnimatorFadeOut.addListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animator) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animator) {
//                    if (reversed) {
//                        constraintLayoutFlipped.setVisibility(View.GONE);
//                        offerCl.setVisibility(View.VISIBLE);
//                        objectAnimatorFadeIn.start();
//                    } else {
//                        offerCl.setVisibility(View.GONE);
//                        objectAnimatorFadeIn.start();
//                        constraintLayoutFlipped.setVisibility(View.VISIBLE);
//                        flippable = true;
//                    }
//
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animator) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animator) {
//
//                }
//            });
//
//            objectAnimatorFadeOut.start();

        }

        private void DeleteText() {
            int cursorStart = editText.getSelectionStart();
            if (cursorStart == editText.getText().toString().length() && editText.getText().toString().length() != 0) {
                if (editText.length() != 0 && editText.getText().toString().charAt(cursorStart - 1) == '.') {
                    hasDecimal = false;
                }
                //DecimalFormat formatter = new DecimalFormat("#,###,###.##");
                if (editText.getText().length() > 1) {
                    temp = editText.getText().toString().replaceAll(",", "");
                    String yourFormattedString = df.format(Double.valueOf(temp.substring(0, temp.length() - 1)));
                    editText.setText(yourFormattedString);
                    editText.setSelection(yourFormattedString.length());
                } else {
                    editText.setText("");
                    editText.setSelection(0);
                }
                if (!editText.getText().toString().contains(".")) {
                    hasDecimal = false;
                }
            } else {
                if (editText.length() != 0 && editText.getText().toString().charAt(cursorStart - 1) == '.') {
                    hasDecimal = false;
                }
                editText.setSelection(cursorStart);
            }
        }

        String dateFormatter(int hours) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE, -330);
            calendar.add(Calendar.HOUR_OF_DAY, hours);
            return getParsedDate(calendar.getTime(), YYYY_MM_DD_HH_MM_SS);
        }

        public String getParsedDate(Date dateFormat, String format) {
            return new java.text.SimpleDateFormat(format).format(dateFormat);
        }

        void ListPopupWindowSetup(final String[] validity, final String[] quantity) {

            listPopupWindowValidity = new ListPopupWindow(mContext);
            /*listPopupWindowValidity.setAdapter(new ArrayAdapter(
                    mContext,
                    android.R.layout.simple_spinner_dropdown_item, validity));*/
            listPopupWindowValidity.setAdapter(new ArrayAdapter(
                    mContext,
                    R.layout.spinner_item, validity));
            listPopupWindowValidity.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.listpopup_background));
            listPopupWindowValidity.setListSelector(mContext.getResources().getDrawable(R.drawable.listpopup_selector_background));
            listPopupWindowValidity.setAnchorView(selectedValidityTv);
            listPopupWindowValidity.setDropDownGravity(topSpace);
            listPopupWindowValidity.setWidth(250);
            listPopupWindowValidity.setHeight(400);

            listPopupWindowValidity.setModal(false);
            listPopupWindowValidity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedValidityTv.setText(validity[i]);
                    listPopupWindowValidity.dismiss();
                }
            });
            linearLayoutValidity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listPopupWindowValidity.isShowing()) {
                        listPopupWindowValidity.dismiss();
                    } else {
                        listPopupWindowValidity.show();
                    }
                }
            });

            listPopupWindowQuantity = new ListPopupWindow(mContext);
            /*listPopupWindowQuantity.setAdapter(new ArrayAdapter(
                    mContext,
                    android.R.layout.simple_spinner_dropdown_item, quantity));*/
            listPopupWindowQuantity.setAdapter(new ArrayAdapter(
                    mContext,
                    R.layout.spinner_item, quantity));
            listPopupWindowQuantity.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.listpopup_background));
            listPopupWindowQuantity.setListSelector(mContext.getResources().getDrawable(R.drawable.listpopup_selector_background));
            listPopupWindowQuantity.setAnchorView(selectedQuantityTv);
            listPopupWindowQuantity.setDropDownGravity(topSpace);
            listPopupWindowQuantity.setWidth(200);
            listPopupWindowQuantity.setHeight(400);

            listPopupWindowQuantity.setModal(false);
            listPopupWindowQuantity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedQuantityTv.setText(quantity[i]);
                    listPopupWindowQuantity.dismiss();
                }
            });
            linearLayoutQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listPopupWindowQuantity.isShowing()) {
                        listPopupWindowQuantity.dismiss();
                    } else {
                        listPopupWindowQuantity.show();
                    }
                }
            });

        }
    }

    public class EventBusHandler {
        public void register() {
            if (!EventBusExt.getDefault().isRegistered(this)) {
                EventBusExt.getDefault().register(this);
            }
        }

        public void unregister() {
            if (EventBusExt.getDefault().isRegistered(this)) {
                EventBusExt.getDefault().unregister(this);
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(UpdateActionBarEvent event) {
            if (mHolder != null) {
                mHolder.validity = mContext.getResources().getStringArray(R.array.validity_items);
                if (mHolder.validity != null && mHolder.validity.length > 0) {
                    mHolder.selectedValidityTv.setText(mHolder.validity[0]);
                }

                mHolder.copyButton.setText(R.string.share_link);
                mHolder.makeOfferButton.setText(R.string.create_offer);
                mHolder.cancelButton.setText(R.string.cancel);
                mHolder.cancelBtn.setText(R.string.cancel);
                mHolder.createButton.setText(R.string.share);
                //mHolder.tvBack.setText(R.string.back);
                mHolder.offerCurrencyTv.setText(R.string.currency);
                //mHolder.productPriceTv.setText(R.string.price);
                mHolder.nameTv.setText(R.string.tv_name);
                mHolder.priceTv.setText(R.string.tv_price);
                mHolder.discountTv.setText(R.string.tv_discount);
                mHolder.descriptionTv.setText(R.string.tv_description);
                mHolder.doneButton.setText(R.string.tv_done);
            }
        }
    }
}
