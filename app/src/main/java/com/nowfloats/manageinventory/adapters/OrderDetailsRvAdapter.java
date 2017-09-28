package com.nowfloats.manageinventory.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.manageinventory.OrderListActivity;
import com.nowfloats.manageinventory.models.UserModel;
import com.nowfloats.manageinventory.models.OrderModel;
import com.nowfloats.manageinventory.models.ProductModel;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by NowFloats on 05-09-2017.
 */

public class OrderDetailsRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ProductModel> mProducts;
    private OrderModel mOrder;
    private UserModel mCustomer;
    private Context mContext;
    private String mTag;


    public OrderDetailsRvAdapter(Context context, String tag, List<ProductModel> products, OrderModel orderModel, UserModel customerModel){
        this.mProducts = products;
        this.mOrder = orderModel;
        this.mCustomer = customerModel;
        this.mContext = context;
        this.mTag = tag;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType){
            case 0:
                v = layoutInflater.inflate(R.layout.customer_details_row_layout, parent, false);
                return new CustomerDetailsHolder(v);
            case 1:
                v = layoutInflater.inflate(R.layout.product_details_row_layout, parent, false);
                return new ProductDetailsHolder(v);
            case 2:
                v = layoutInflater.inflate(R.layout.order_details_row_layout, parent, false);
                return new OrderDetailsHolder(v);
            default:
                v = layoutInflater.inflate(R.layout.order_details_row_layout, parent, false);
                return new OrderDetailsHolder(v);
        }
    }

    @Override
    @SuppressLint("NewApi")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CustomerDetailsHolder){
            CustomerDetailsHolder customerDetailsHolder = (CustomerDetailsHolder) holder;
            customerDetailsHolder.tvCustomerName.setText(mCustomer.getCustomerName());
            customerDetailsHolder.tvCustomerEmail.setText(mCustomer.getCustomerEmail());
            customerDetailsHolder.tvCustomerAddress.setText(mCustomer.getAddressLine1()
                    + ", " + mCustomer.getAddressLine2()
                    + ", " + mCustomer.getDeliveryCity()
                    + ", " + mCustomer.getDeliveryState()
                    + ", " + mCustomer.getDeliveryCountry()
                    + ", " + mCustomer.getDeliveryPincode());
            customerDetailsHolder.getTvCustomerPhone.setText(mCustomer.getCustomerMobileNumber());
            switch (mTag){
                case "Fresh":
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.received_order_status_bg));
                    break;
                case "Returned":
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.returned_order_status_bg));
                    break;
                case "Delivered":
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.delivered_order_status_bg));
                    break;
                case "Cancelled":
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.cancelled_order_status_bg));
                    break;
                case "Abandoned":
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.abandoned_order_status_bg));
                    break;
                default:
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.abandoned_order_status_bg));
                    break;
            }
            customerDetailsHolder.tvStatusTag.setText(mTag.toUpperCase());
        }else if(holder instanceof ProductDetailsHolder){
            ProductDetailsHolder productDetailsHolder = (ProductDetailsHolder) holder;
            productDetailsHolder.tvProductName.setText(mProducts.get(position-1).getProductName());
            productDetailsHolder.tvDiscount.setText(mProducts.get(position-1).getDiscount()+"");
            productDetailsHolder.tvProductQuantity.setText(mProducts.get(position-1).getQuantity()+"");
            productDetailsHolder.tvFinalPrice.setText(mProducts.get(position-1).getFinalPrice()+"");
            productDetailsHolder.tvUnitPrice.setText(mProducts.get(position-1).getPrice()+"");
            Picasso.with(mContext).load(mProducts.get(position-1).getProductImgUrl()).into(productDetailsHolder.ivProductImg);
        }else if(holder instanceof OrderDetailsHolder){
            OrderDetailsHolder orderDetailsHolder = (OrderDetailsHolder) holder;
            orderDetailsHolder.tvTotalPrice.setText(mOrder.getTotalAmount()+"");
            orderDetailsHolder.tvAssuredPurchaseCharge.setText(mOrder.getNfAssuranceChargeVal()+"");
            orderDetailsHolder.tvShippingCharge.setText(mOrder.getShippingChargeMerchant()+"");
            orderDetailsHolder.tvNetAmount.setText(mOrder.getNetAmount()+"");
            if(mOrder.getOrderStatus() == OrderListActivity.OrderStatus.DELIVERED.ordinal()) {
                orderDetailsHolder.tvDeliveryDate.setText(getParsedDate(mOrder.getActualDeliveryDate()));
            }else {
                orderDetailsHolder.tvDeliveryDate.setText(getParsedDate(mOrder.getExpectedDeliveryDate()));
            }
            orderDetailsHolder.tvOrderDate.setText(getParsedDate(mOrder.getCreatedOn()));
        }
    }

    @Override
    public int getItemCount() {
        return mProducts.size()+2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 0;
        }else if(position == mProducts.size()+1){
            return 2;
        }else {
            return 1;
        }
    }

    public class CustomerDetailsHolder extends RecyclerView.ViewHolder{

        TextView tvCustomerName, tvCustomerEmail, tvCustomerAddress, getTvCustomerPhone, tvStatusTag;

        public CustomerDetailsHolder(View itemView) {
            super(itemView);

            tvCustomerName = (TextView) itemView.findViewById(R.id.tv_customer_name);
            tvCustomerEmail = (TextView) itemView.findViewById(R.id.tv_customer_email);
            tvCustomerAddress = (TextView) itemView.findViewById(R.id.tv_customer_address);
            getTvCustomerPhone = (TextView) itemView.findViewById(R.id.tv_customer_phone);
            tvStatusTag = (TextView) itemView.findViewById(R.id.tv_order_status_tag);

        }
    }

    private String getParsedDate(String strDate) {
        if(strDate==null){
            return "N/A";
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        String parsedDate;
        try {
            Date date = format.parse(strDate);
            parsedDate = new SimpleDateFormat("HH:mm a dd/MM/yyyy",Locale.ENGLISH).format(date);
        }catch (ParseException e){
            parsedDate = strDate;
        }

        return parsedDate;
    }

    public class ProductDetailsHolder extends RecyclerView.ViewHolder{

        TextView tvProductName, tvProductQuantity, tvUnitPrice, tvDiscount, tvFinalPrice;
        ImageView ivProductImg;

        public ProductDetailsHolder(View itemView) {
            super(itemView);

            tvProductName = (TextView) itemView.findViewById(R.id.tv_product_name);
            tvProductQuantity = (TextView) itemView.findViewById(R.id.tv_product_quantity);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.tv_product_price);
            tvDiscount = (TextView) itemView.findViewById(R.id.tv_product_discount);
            tvFinalPrice = (TextView) itemView.findViewById(R.id.tv_final_price);

            ivProductImg = (ImageView) itemView.findViewById(R.id.iv_product_img);

        }
    }

    public class OrderDetailsHolder extends RecyclerView.ViewHolder{

        TextView tvOrderDate, tvDeliveryDate, tvNetAmount, tvShippingCharge, tvAssuredPurchaseCharge, tvTotalPrice;

        public OrderDetailsHolder(View itemView) {
            super(itemView);

            tvOrderDate = (TextView) itemView.findViewById(R.id.tv_order_date);
            tvDeliveryDate = (TextView) itemView.findViewById(R.id.tv_delivery_date);
            tvNetAmount = (TextView) itemView.findViewById(R.id.tv_net_amount);
            tvShippingCharge = (TextView) itemView.findViewById(R.id.tv_shipping_charge);
            tvAssuredPurchaseCharge = (TextView) itemView.findViewById(R.id.tv_assured_purchase_charge);
            tvTotalPrice = (TextView) itemView.findViewById(R.id.tv_total_price);
        }
    }
}
