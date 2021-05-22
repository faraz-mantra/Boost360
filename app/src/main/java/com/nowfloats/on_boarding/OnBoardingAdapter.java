package com.nowfloats.on_boarding;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nowfloats.Login.LoginManager;
import com.nowfloats.on_boarding.models.OnBoardingModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Admin on 16-03-2018.
 */

public class OnBoardingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private String experienceCode;
    private String[] titles, descriptions, buttonText;
    private LinearLayout.LayoutParams linearLayoutParams;
    private int leftSpace, topSpace;
    private OnBoardingModel mOnBoardingModel;
    private RecyclerView mRecyclerView;
    private DisplayMetrics metrics;
    private SharedPreferences sharedPreferences;
    public OnBoardingAdapter(Context context, OnBoardingModel onBoardingModel){
        mContext = context;
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        mOnBoardingModel = onBoardingModel;
        metrics = mContext.getResources().getDisplayMetrics();
        leftSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, metrics);
        topSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, metrics);
        linearLayoutParams = new LinearLayout.LayoutParams(metrics.widthPixels * 80 / 100,
                metrics.heightPixels * 55 / 100);
        linearLayoutParams.setMargins(leftSpace, topSpace, leftSpace, topSpace);
        descriptions = mContext.getResources().getStringArray(R.array.on_boarding_description);
        titles = mContext.getResources().getStringArray(R.array.on_boarding_titles);
        buttonText = mContext.getResources().getStringArray(R.array.on_boarding_btnTexts);
        //customise "Business" to category specific word
        experienceCode = sharedPreferences.getString(Key_Preferences.GET_FP_EXPERIENCE_CODE, null);
        switch (experienceCode){
            case "DOC":
                titles[2] = titles[2].replace("Business", "Practise");
                titles[3] = titles[3].replace("products", "services");
                buttonText[3] = buttonText[3].replace("Products", "Services");
                buttonText[5] = buttonText[5].replace("Business", "Practise");
                break;
            case "HOS":
                titles[2] = titles[2].replace("Business", "Hospital");
                titles[3] = titles[3].replace("products", "services");
                buttonText[3] = buttonText[3].replace("Products", "Services");
                buttonText[5] = buttonText[5].replace("Business", "Hospital");
                break;
            case "HOT":
                titles[2] = titles[2].replace("Business", "Hotel");
                titles[3] = titles[3].replace("products", "rooms");
                buttonText[3] = buttonText[3].replace("Products", "Rooms");
                buttonText[5] = buttonText[5].replace("Business", "Hotel");
                break;
            case "CAF":
                titles[2] = titles[2].replace("Business", "Restaurant");
                titles[3] = titles[3].replace("products", "food items");
                buttonText[3] = buttonText[3].replace("Products", "Food Item");
                buttonText[5] = buttonText[5].replace("Business", "Restaurant");
                break;
            case "EDU":
                titles[2] = titles[2].replace("Business", "Institute");
                titles[3] = titles[3].replace("products", "courses");
                buttonText[3] = buttonText[3].replace("Products", "Courses");
                buttonText[5] = buttonText[5].replace("Business", "Institute");
                break;
            case "SVC":
                titles[3] = titles[3].replace("products", "services");
                buttonText[3] = buttonText[3].replace("Products", "Services");
                break;
            case "RTL":
                titles[2] = titles[2].replace("Business", "Shop");
                break;
            case "SPA":
                titles[2] = titles[2].replace("Business", "Spa");
                titles[3] = titles[3].replace("products", "services");
                buttonText[3] = buttonText[3].replace("Products", "Services");
                buttonText[5] = buttonText[5].replace("Business", "Spa");
                break;
            case "SAL":
                titles[2] = titles[2].replace("Business", "Salon");
                titles[3] = titles[3].replace("products", "services");
                buttonText[3] = buttonText[3].replace("Products", "Services");
                buttonText[5] = buttonText[5].replace("Business", "Salon");
                break;
            case "MFG":
                titles[2] = titles[2].replace("Business", "Unit");
                break;
        }


        //addAuthScreens();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_onboarding_item,parent,false);
        return new MyViewHolder(view);
    }

    private void addAuthScreens() {

        ArrayList<OnBoardingModel.ScreenData> data = new ArrayList<>();
        OnBoardingModel.ScreenData facebookData = new OnBoardingModel.ScreenData();
        facebookData.key = "FACEBOOK";
        facebookData.setIsComplete(sharedPreferences.getBoolean(facebookData.getKey(), false));

        data.add(facebookData);

        OnBoardingModel.ScreenData googleData = new OnBoardingModel.ScreenData();
        googleData.key = "GOOGLE";
        googleData.setIsComplete(sharedPreferences.getBoolean(googleData.getKey(), false));

        data.add(googleData);

        OnBoardingModel.ScreenData otpData = new OnBoardingModel.ScreenData();
        otpData.key = "OTP";
        otpData.setIsComplete(sharedPreferences.getBoolean(otpData.getKey(), false));

        data.add(otpData);

        data.addAll(mOnBoardingModel.getScreenDataArrayList());
        mOnBoardingModel.setScreenDataArrayList(data);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder){
            ((MyViewHolder) holder).setData(position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    public void refreshAfterComplete(){
        mOnBoardingModel.setToBeCompletePos(-1);
        int  i = -1;
        for (OnBoardingModel.ScreenData data : mOnBoardingModel.getScreenDataArrayList()){
            i++;
            switch (i){
                case 0:
                    break;
                case 1:
                    data.setIsComplete(sharedPreferences.getInt(Key_Preferences.SITE_HEALTH,0)>=80);
                    data.setValue(String.valueOf(sharedPreferences.getInt(Key_Preferences.SITE_HEALTH,0)));
                    break;
                case 2:
                    data.setIsComplete(sharedPreferences.getInt(Key_Preferences.CUSTOM_PAGE,0)>0);
                    break;
                case 3:
                    data.setIsComplete(sharedPreferences.getInt(Key_Preferences.PRODUCTS_COUNT,0)>0);
                    break;
                case 4:
                    data.setIsComplete(true);
                    break;
                case 5:
                    data.setIsComplete(sharedPreferences.getInt(Key_Preferences.PRODUCTS_COUNT,0)>0);
                    break;
                default:
                    data.setIsComplete(sharedPreferences.getBoolean(data.getKey(), false));
            }
            if (!data.isComplete() && mOnBoardingModel.getToBeCompletePos() == -1){
                mOnBoardingModel.setToBeCompletePos(i);
            }
        }
        if (mOnBoardingModel.getToBeCompletePos() == -1){
            mOnBoardingModel.setToBeCompletePos(5);
            sharedPreferences.edit().putBoolean(Key_Preferences.ON_BOARDING_STATUS,true).apply();
            ((ItemClickListener)mContext).onBoardingComplete();
        }
        if(mRecyclerView != null){
            mRecyclerView.scrollToPosition(mOnBoardingModel.getToBeCompletePos());
        }
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return descriptions.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView titleTv, cardNumberTv, descriptionTv, btnTv;
        private CardView itemView;
        private ImageView completeImg;
        ConstraintLayout layout;
        public MyViewHolder(final View itemView) {
            super(itemView);
            this.itemView = (CardView) itemView;
            itemView.setLayoutParams(linearLayoutParams);
            layout = itemView.findViewById(R.id.card_layout);
            titleTv = itemView.findViewById(R.id.tv_title);
            cardNumberTv = itemView.findViewById(R.id.tv_card_number);
            descriptionTv = itemView.findViewById(R.id.tv_description);
            completeImg = itemView.findViewById(R.id.img_complete);
            btnTv = itemView.findViewById(R.id.btn_tv);
            btnTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(titles[getAdapterPosition()].contains("via Google") ||
                            titles[getAdapterPosition()].contains("via Facebook") ||
                            titles[getAdapterPosition()].contains("via mobile number")) {

                        if(titles[getAdapterPosition()].contains("via Google")) {
                            LoginManager.getInstance().getListener().onGoogleLogin();
                        }else if(titles[getAdapterPosition()].contains("via Facebook")){
                            LoginManager.getInstance().getListener().onFacebookLogin();
                        }else{
                            LoginManager.getInstance().getListener().onOTPLogin();
                        }

                    }else if (mOnBoardingModel.getScreenDataArrayList().get(getAdapterPosition()).isComplete()
                            || getAdapterPosition() == mOnBoardingModel.getToBeCompletePos()){

                        ((ItemClickListener)mContext).onItemClick(getAdapterPosition(), mOnBoardingModel.getScreenDataArrayList().get(getAdapterPosition()));

                    }else if(mRecyclerView != null){
                        Toast.makeText(mContext, mContext.getString(R.string.you_need_to_finish_the_previous_setup_before_), Toast.LENGTH_SHORT).show();
                        mRecyclerView.scrollToPosition(mOnBoardingModel.getToBeCompletePos());
                    }
                }
            });
        }

        void setData(int position){

//            boolean authInfoDisplay = titles[getAdapterPosition()].contains("via Google") ||
//                    titles[getAdapterPosition()].contains("via Facebook") ||
//                    titles[getAdapterPosition()].contains("via mobile number");

            titleTv.setText(String.format(titles[position],mOnBoardingModel.getScreenDataArrayList().get(position).getValue()+"%"));
            cardNumberTv.setText(String.valueOf(position+1));
            descriptionTv.setText(descriptions[position]);
            btnTv.setText(buttonText[position]);

            if(mOnBoardingModel.getScreenDataArrayList().get(position).isComplete()){
                completeImg.setVisibility(View.VISIBLE);
                layout.setAlpha(1f);
                itemView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                btnTv.setBackgroundResource(R.drawable.btn_bg);
            } else if(position != mOnBoardingModel.getToBeCompletePos()){
                layout.setAlpha(.4f);
                completeImg.setVisibility(View.GONE);
                itemView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.light_gray));
                btnTv.setBackgroundResource(R.color.gray);
            }else{
                layout.setAlpha(1f);
                completeImg.setVisibility(View.GONE);
                itemView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                btnTv.setBackgroundResource(R.drawable.btn_bg);
            }


            btnTv.setVisibility(position == 4?View.INVISIBLE:View.VISIBLE);

            int padd = Methods.dpToPx(10,mContext);
            btnTv.setPadding(padd,padd,padd,padd);
        }

    }
    public interface ItemClickListener{
        void onItemClick(int pos, OnBoardingModel.ScreenData data);
        void onBoardingComplete();
    }
}
