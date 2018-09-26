package com.nowfloats.SellerProfileV2.Adapter;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.SellerProfileV2.OperationModeFragment;
import com.thinksity.R;

public class OperationModeAdapter extends RecyclerView.Adapter<OperationModeAdapter.OperationModeViewHolder> {

    private String[] operationHeaders;
    private String[] operationDescriptions;
    private String[] operationSelectOptions;

    private OperationModeFragment.OperationModeInterface operationModeInterface;

    private CardView cv_SelectedParentCard;
    private TextView btn_SelectedselectOption;

    private boolean renderedOnce;

    public OperationModeAdapter(String[] operationDescriptions , String[] operationHeaders , String[] operationSelectOptions , OperationModeFragment.OperationModeInterface operationModeInterface) {
        this.operationDescriptions = operationDescriptions;
        this.operationSelectOptions = operationSelectOptions;
        this.operationHeaders = operationHeaders;
        this.operationModeInterface = operationModeInterface;
    }

    @NonNull
    @Override
    public OperationModeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_item_operation_model , viewGroup , false);
        return new OperationModeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final OperationModeViewHolder operationModeViewHolder, int i) {
        operationModeViewHolder.tv_Description.setText(operationDescriptions[operationModeViewHolder.getAdapterPosition()]);
        operationModeViewHolder.tv_Header.setText(operationHeaders[operationModeViewHolder.getAdapterPosition()]);
        operationModeViewHolder.btn_selectOption.setText(operationSelectOptions[operationModeViewHolder.getAdapterPosition()]);

        if( !renderedOnce ) {
            unCheckPreviousCard(operationModeViewHolder);
            renderedOnce = true;
        }

        operationModeViewHolder.btn_selectOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unCheckPreviousCard(operationModeViewHolder);
                operationModeInterface.onInterfaceSelected(operationModeViewHolder.getAdapterPosition());
            }
        });

    }

    public void unCheckPreviousCard(OperationModeViewHolder holder){

        if(cv_SelectedParentCard != null ){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cv_SelectedParentCard.setElevation(0f);
            }
            cv_SelectedParentCard.setCardBackgroundColor(Color.parseColor("#bbbbbb"));
            btn_SelectedselectOption.setBackgroundColor(Color.parseColor("#e4e4e4"));

        }

        cv_SelectedParentCard = holder.cv_ParentCard;
        btn_SelectedselectOption = holder.btn_selectOption;

        cv_SelectedParentCard.setCardBackgroundColor(Color.parseColor("#ffffff"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cv_SelectedParentCard.setElevation(10f);
        }
        btn_SelectedselectOption.setBackgroundColor(Color.parseColor("#ffb900"));


    }

    @Override
    public int getItemCount() {
        return operationDescriptions.length;
    }

    public class OperationModeViewHolder extends RecyclerView.ViewHolder{

        public CardView cv_ParentCard;
        public TextView tv_Header;
        public TextView tv_Description;
        public TextView btn_selectOption;

        public OperationModeViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_ParentCard = itemView.findViewById(R.id.cv_operation_parentlayout);
            tv_Header = itemView.findViewById(R.id.tv_opreation_header);
            tv_Description = itemView.findViewById(R.id.tv_operation_description);
            btn_selectOption = itemView.findViewById(R.id.tv_selectOption);
        }
    }

}
