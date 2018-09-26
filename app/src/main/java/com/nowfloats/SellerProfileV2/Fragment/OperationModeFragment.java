package com.nowfloats.SellerProfileV2.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.SellerProfileV2.Adapter.OperationModeAdapter;
import com.thinksity.R;


public class OperationModeFragment extends Fragment {


    private RecyclerView rv_operationRecyclerView;
    private OperationModeAdapter operationModeAdapter;
    private OperationModeInterface operationModeInterface;



    // TODO: Rename and change types and number of parameters
    public static OperationModeFragment newInstance(OperationModeInterface operationModeInterface) {
        OperationModeFragment fragment = new OperationModeFragment();
        fragment.operationModeInterface = operationModeInterface;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_operation_mode, container, false);
        rv_operationRecyclerView = v.findViewById(R.id.rv_OperationMode);
        setUpAdapter();
        setUpRecyclerView();
        return v;
    }

    private void setUpRecyclerView() {
        rv_operationRecyclerView.setAdapter(operationModeAdapter);
        rv_operationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setUpAdapter() {

        String[] operationHeaders = getResources().getStringArray(R.array.operation_mode_headers);
        String[] operationDescriptions = getResources().getStringArray(R.array.operation_mode_description);
        String[] operationSelectOptions = getResources().getStringArray(R.array.operation_mode_selection_option);

        operationModeAdapter = new OperationModeAdapter(operationDescriptions , operationHeaders , operationSelectOptions , operationModeInterface);
    }



    public interface OperationModeInterface{
        void onInterfaceSelected(int operationIndex);
    }

}
