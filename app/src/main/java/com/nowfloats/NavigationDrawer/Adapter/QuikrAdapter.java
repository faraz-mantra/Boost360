package com.nowfloats.NavigationDrawer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nowfloats.NavigationDrawer.Mobile_Site_Activity;
import com.thinksity.R;


/**
 * Created by Admin on 05-04-2017.
 */

public class QuikrAdapter extends RecyclerView.Adapter<QuikrAdapter.MyHolder> {
    Context mContext;
    String [] guidelines;
    private final int BUTTON_TYPE = 1, TEXT_TYPE = 0;
    public QuikrAdapter(Context context,String[] guidelines){
        mContext = context;
        this.guidelines = guidelines;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        if(viewType == BUTTON_TYPE){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_button_item, parent, false);
        }else if(viewType == TEXT_TYPE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_text_item, parent, false);
        }
        return new MyHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        if(position>=5){
            SpannableString ss = new SpannableString(guidelines[position]);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    int pos = holder.getAdapterPosition();
                    if(pos ==5){
                        openWebView("http://www.quikr.com/html/termsandconditions.php");
                    }else if(pos == 6){
                        openWebView("http://bangalore.quikr.com/html/privacy.php");
                    }else if(pos == 7){
                        openWebView("http://hyderabad.quikr.com/html/policies.php");
                    }
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                }
            };
            ss.setSpan(clickableSpan, 0,guidelines[position].length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.text.setText(ss);
            holder.text.setMovementMethod(LinkMovementMethod.getInstance());
            holder.text.setHighlightColor(Color.TRANSPARENT);

        }else{
            holder.text.setText(guidelines[position]);
        }
    }
    private void openWebView(String url){

        Intent showWebSiteIntent = new Intent(mContext, Mobile_Site_Activity.class);
        // showWebSiteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        showWebSiteIntent.putExtra("WEBSITE_NAME", url);
        mContext.startActivity(showWebSiteIntent);
        ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    @Override
    public int getItemViewType(int position) {
        return TEXT_TYPE;
    }

    @Override
    public int getItemCount() {
        return guidelines.length;
    }
    class MyHolder extends RecyclerView.ViewHolder{

        TextView text;
        Button button;
        MyHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == BUTTON_TYPE){
                button = (Button) itemView.findViewById(R.id.button1);
            }
            else{
                text = (TextView) itemView.findViewById(R.id.text1);
            }

        }
    }


}
