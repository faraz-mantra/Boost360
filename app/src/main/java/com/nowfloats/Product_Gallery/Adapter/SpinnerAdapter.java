package com.nowfloats.Product_Gallery.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends BaseAdapter
{
    private Context context;
    private String[] titles;
    private String[] messages;

    List<PaymentOption> paymentOptionList;

    public SpinnerAdapter(Context context)
    {
        this.context = context;
        this.titles = context.getResources().getStringArray(R.array.payment_method_titles);
        this.messages = context.getResources().getStringArray(R.array.payment_method_messages);

        this.paymentOptionList = getPaymentOptionList();
    }

    @Override
    public Object getItem(int pos)
    {
        return paymentOptionList.get(pos);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }


    @NonNull
    public View getView(int position, View itemView, @NonNull ViewGroup parent)
    {
        return initView(position, itemView, parent);
    }


    private View initView(int position, View itemView, ViewGroup parent) {

        PaymentOption option = paymentOptionList.get(position);

        if (itemView == null)
        {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_payment_configuration_option, null /*parent, false*/);
        }

        TextView tvPaymentType = itemView.findViewById(R.id.label_payment_type);
        TextView tvDescription = itemView.findViewById(R.id.label_description);

        tvPaymentType.setText(option.title);
        tvDescription.setText(option.body);

        return itemView;
    }

    /*@Override
    public View getDropDownView(int position, View itemView, @NonNull ViewGroup parent)
    {
        return getView(position, itemView, parent);
    }*/

    @Override
    public int getCount() {
        return paymentOptionList.size();
    }


    class PaymentOption
    {
        String title, body;

        PaymentOption(String title, String body)
        {
            this.title = title;
            this.body = body;
        }
    }

    private List<PaymentOption> getPaymentOptionList()
    {
        List<PaymentOption> paymentOptions = new ArrayList<>();

        for(int i=0; i<titles.length; i++)
        {
            paymentOptions.add(new PaymentOption(titles[i], messages[i]));
        }
        return paymentOptions;
    }
}