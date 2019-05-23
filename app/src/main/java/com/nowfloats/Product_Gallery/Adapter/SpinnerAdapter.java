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

    List<PaymentOption> addressList;

    public SpinnerAdapter(Context context)
    {
        this.addressList = new PaymentOption().getList();
        this.context = context;
    }

    @Override
    public Object getItem(int pos)
    {
        return addressList.get(pos);
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

        PaymentOption option = addressList.get(position);

        if (itemView == null)
        {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_payment_configuration_option, null /*parent, false*/);
        }

        TextView tvPaymentType = itemView.findViewById(R.id.label_payment_type);
        TextView tvDescription = itemView.findViewById(R.id.label_description);

        tvPaymentType.setText(option.title);
        tvDescription.setText(option.body);



        /*if (parent == null)
        {
            itemView = LayoutInflater.from(painitViewrent.getContext()).inflate(R.layout.spinner_item_dynamic, null);
        }

        else
        {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_dynamic, parent, false);
        }*/

        /*CircleImageView thumbnail = itemView.findViewById(R.id.patient_image);
        TextView textView = itemView.findViewById(R.id.patient_name);

        if(position == (mList.size() - 1) && mList.get(position) == null)
        {
            textView.setText(String.valueOf("Add New"));
            thumbnail.setImageResource(R.drawable.ic_plus);
            return itemView;
        }

        else
        {
            textView.setText(Helper.toCamelCase(patient.getFullName()));

            try
            {
                if(!patient.getProfilePic().isEmpty())
                {
                    Glide.with(context)
                            .load(patient.getProfilePic())
                            .placeholder(R.drawable.anonymous)
                            .dontAnimate()
                            .override(50, 50)
                            .centerCrop()
                            .into(thumbnail);
                }

                else
                {
                    Glide.with(context)
                            .load(R.drawable.anonymous)
                            .placeholder(R.drawable.anonymous)
                            .dontAnimate()
                            .override(50, 50)
                            .centerCrop()
                            .into(thumbnail);
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }*/

        return itemView;

        //return LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_payment_configuration_option, null /*parent, false*/);
    }

    /*@Override
    public View getDropDownView(int position, View itemView, @NonNull ViewGroup parent)
    {
        return getView(position, itemView, parent);
    }*/

    @Override
    public int getCount() {
        return 4; //addressList.size();
    }

    /*public void setItems(int count)
    {
        if(count < 4)
        {
            this.mList.add(null);
        }

        notifyDataSetChanged();
    }*/

    static class PaymentOption {
        String title, body;

        PaymentOption()
        {

        }

        PaymentOption(String title, String body)
        {
            this.title = title;
            this.body = body;
        }

        public List<PaymentOption> getList()
        {
            List<PaymentOption> paymentOptions = new ArrayList<>();

            paymentOptions.add(new PaymentOption("Assured Purchase™", "A Boost service which ensures a secure payment and safe delivery of this product."));
            paymentOptions.add(new PaymentOption("My Payment Gateway", "Simply receive payments in your account by adding salt & key of popular gateways. e.g paypal, ICICI etc. This works best if you've already sorted the delivery part."));
            paymentOptions.add(new PaymentOption("Variants Unique Payment URL", "If you already listed your product on some order platform simply enter the URL here and handle to purchase from there."));
            paymentOptions.add(new PaymentOption("Don't want to sell online", "If you just wish to list the product on the site and wants to only receive enquiries from your customers."));

            return paymentOptions;
        }
    }
}