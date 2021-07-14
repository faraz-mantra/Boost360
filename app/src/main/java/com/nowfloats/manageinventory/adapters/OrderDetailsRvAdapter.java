package com.nowfloats.manageinventory.adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.manageinventory.models.OrderDataModel;
import com.nowfloats.manageinventory.models.OrderDataModel.Order;
import com.nowfloats.util.Methods;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.List;

/**
 * Created by NowFloats on 05-09-2017.
 */

public class OrderDetailsRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<OrderDataModel.OrderDetails> mProducts;
    private Order mOrder;
    private OrderDataModel.BuyerDetails buyerDetails;
    private Context mContext;
    private String mTag;


    public OrderDetailsRvAdapter(Context context, String tag, Order orderModel) {
        this.mProducts = orderModel.getOrderDetails();
        this.mOrder = orderModel;
        this.buyerDetails = orderModel.getBuyerDetails();
        this.mContext = context;
        this.mTag = tag;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType) {
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
        if (holder instanceof CustomerDetailsHolder) {
            CustomerDetailsHolder customerDetailsHolder = (CustomerDetailsHolder) holder;

            if (buyerDetails != null) {

                if (buyerDetails.getContactDetails() != null) {

                    customerDetailsHolder.tvCustomerName.setText(buyerDetails.getContactDetails().getFullName());
                    customerDetailsHolder.tvCustomerEmail.setText(buyerDetails.getContactDetails().getEmailId());
                    customerDetailsHolder.getTvCustomerPhone.setText(buyerDetails.getContactDetails().getPrimaryContactNumber());

                }
                if (buyerDetails.getAddress() != null) {
                    customerDetailsHolder.tvCustomerAddress.setText(buyerDetails.getAddress().getAddressLine1()
                            + ", " + buyerDetails.getAddress().getAddressLine2()
                            + ", " + buyerDetails.getAddress().getCity()
                            + ", " + buyerDetails.getAddress().getRegion()
                            + ", " + buyerDetails.getAddress().getCountry()
                            + ", " + buyerDetails.getAddress().getZipcode());
                }
            }
            switch (mTag) {
                case "Initiated":
                    customerDetailsHolder.tvStatusTag.setText(mTag.toUpperCase());
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.initiated_order_status_bg));
                    break;
                case "Confirmed":
                    if (mOrder.getLogisticsDetails() != null && mOrder.getLogisticsDetails().getStatus().equalsIgnoreCase("Shipped")) {
                        customerDetailsHolder.tvStatusTag.setText("Delivery Confirmation Pending");
                    } else if (mOrder.getMode().equalsIgnoreCase("Pickup")) {
                        if (mOrder.getLogisticsDetails().getDeliveryConfirmationDetails() == null ||
                                TextUtils.isEmpty(mOrder.getLogisticsDetails().getDeliveryConfirmationDetails().getNotificationSentOn())) {
                            customerDetailsHolder.tvStatusTag.setText("Pickup Pending");
                        } else {
                            customerDetailsHolder.tvStatusTag.setText("Pickup Confirmation Pending");
                        }
                    } else {
                        customerDetailsHolder.tvStatusTag.setText("Shipping Pending");
                    }
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.received_order_status_bg));
                    break;
                case "Placed":
                case "PLACED":
                    customerDetailsHolder.tvStatusTag.setText("Confirmation Pending");
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.received_order_status_bg));
                    break;
                case "Returned":
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.returned_order_status_bg));
                    break;
                case "Completed":
                    customerDetailsHolder.tvStatusTag.setText(mTag.toUpperCase());
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.delivered_order_status_bg));
                    break;
                case "Cancelled":
                    customerDetailsHolder.tvStatusTag.setText(mTag.toUpperCase());
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.cancelled_order_status_bg));
                    break;
                case "Disputed":
                case "Escalated":
                    customerDetailsHolder.tvStatusTag.setText(mTag.toUpperCase());
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.cancelled_order_status_bg));
                    break;
                case "Abandoned":
                    customerDetailsHolder.tvStatusTag.setText(mTag.toUpperCase());
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.cancelled_order_status_bg));
                    break;
                default:
                    customerDetailsHolder.tvStatusTag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.cancelled_order_status_bg));
                    break;
            }
        } else if (holder instanceof ProductDetailsHolder) {
            ProductDetailsHolder productDetailsHolder = (ProductDetailsHolder) holder;
            productDetailsHolder.tvProductName.setText(mProducts.get(position - 1).getProduct().getName());
            productDetailsHolder.tvDiscount.setText(TextUtils.isEmpty(mOrder.getBillingDetails().getCurrencyCode()) ? "INR" :
                    mOrder.getBillingDetails().getCurrencyCode() + " " + (mProducts.get(position - 1).getActualPrice() -
                            mProducts.get(position - 1).getSalePrice()) + "");
            productDetailsHolder.tvProductQuantity.setText(mProducts.get(position - 1).getQuantity() + "");
            productDetailsHolder.tvFinalPrice.setText(TextUtils.isEmpty(mOrder.getBillingDetails().getCurrencyCode()) ? "INR" :
                    mOrder.getBillingDetails().getCurrencyCode() + " " + mProducts.get(position - 1).getSalePrice() + "");
            productDetailsHolder.tvUnitPrice.setText(TextUtils.isEmpty(mOrder.getBillingDetails().getCurrencyCode()) ? "INR" :
                    mOrder.getBillingDetails().getCurrencyCode() + " " + mProducts.get(position - 1).getActualPrice() + "");

            if (mProducts.get(position - 1).getProduct().getFeaturedImage() != null) {
                Picasso.get().load(mProducts.get(position - 1).getProduct().getFeaturedImage().getTileImageUri())
                        .into(productDetailsHolder.ivProductImg);
            } else {
                productDetailsHolder.ivProductImg.setImageResource(R.drawable.default_product_image);
            }
        } else if (holder instanceof OrderDetailsHolder) {
            OrderDetailsHolder orderDetailsHolder = (OrderDetailsHolder) holder;
            if (mOrder.getBillingDetails() != null) {
//                orderDetailsHolder.tvTotalPrice.setText((mOrder.getBillingDetails().getGrossAmount() +
//                        mOrder.getBillingDetails().getDiscountAmount() + mOrder.getBillingDetails().getAssuredPurchaseCharges() +
//                        mOrder.getBillingDetails().getNfDeliveryCharges()
//                        + mOrder.getBillingDetails().getSellerDeliveryCharges()) + "");

                orderDetailsHolder.tvTotalPrice.setText(TextUtils.isEmpty(mOrder.getBillingDetails().getCurrencyCode()) ? "INR" :
                        mOrder.getBillingDetails().getCurrencyCode() + " " + mOrder.getBillingDetails().getAmountPayableByBuyer() + "");
                orderDetailsHolder.tvAssuredPurchaseCharge.setText(mOrder.getBillingDetails().getAssuredPurchaseCharges() + "");
                orderDetailsHolder.tvShippingCharge.setText((mOrder.getBillingDetails().getNfDeliveryCharges()
                        + mOrder.getBillingDetails().getSellerDeliveryCharges()) + "");
                orderDetailsHolder.tvNetAmount.setText(TextUtils.isEmpty(mOrder.getBillingDetails().getCurrencyCode()) ? "INR" :
                        mOrder.getBillingDetails().getCurrencyCode() + " " + (mOrder.getBillingDetails().getGrossAmount() +
                                mOrder.getBillingDetails().getDiscountAmount()) + "");
            } else {
                orderDetailsHolder.tvTotalPrice.setText("N/A");
                orderDetailsHolder.tvAssuredPurchaseCharge.setText("N/A");
                orderDetailsHolder.tvShippingCharge.setText("N/A");
                orderDetailsHolder.tvNetAmount.setText("N/A");
            }
            if (mOrder.getLogisticsDetails() != null && !TextUtils.isEmpty(mOrder.getLogisticsDetails().getDeliveredOn())) {
                orderDetailsHolder.tvDeliveryDate.setText(Methods.getParsedDate(mOrder.getLogisticsDetails().getDeliveredOn()));
            } else {
                orderDetailsHolder.tvDeliveryDate.setText("N/A");
            }
            orderDetailsHolder.tvOrderDate.setText(Methods.getParsedDate(mOrder.getCreatedOn()));
        }
    }

    @Override
    public int getItemCount() {
        return mProducts.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == mProducts.size() + 1) {
            return 2;
        } else {
            return 1;
        }
    }

    public class CustomerDetailsHolder extends RecyclerView.ViewHolder {

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


    public class ProductDetailsHolder extends RecyclerView.ViewHolder {

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

    public class OrderDetailsHolder extends RecyclerView.ViewHolder {

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
