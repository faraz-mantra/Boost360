package com.nowfloats.Store;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Service.OnPaymentOptionClick;
import com.nowfloats.util.Methods;
import com.thinksity.R;

/**
 * Created by Admin on 13-04-2018.
 */

public class PaymentOptionsListFragment extends ListFragment implements AdapterView.OnItemClickListener {
    UserSessionManager sessionManager;
    private Context mContext;

    public static Fragment getInstance(Bundle b) {
        Fragment frag = new PaymentOptionsListFragment();
        frag.setArguments(b);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_option_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            getActivity().setTitle(getString(R.string.select_payment_option));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded() || getActivity() == null) {
            Methods.showSnackBar(view, getString(R.string.something_went_wrong_try_again), Color.RED);
            return;
        }

        sessionManager = new UserSessionManager(mContext, requireActivity());
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.payment_options, R.layout.layout_simple_text_list_item);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                ((OnPaymentOptionClick) mContext).onOptionClicked(PaymentOptionsActivity.PaymentType.OPC);
                break;
            case 1:
                ((OnPaymentOptionClick) mContext).onOptionClicked(PaymentOptionsActivity.PaymentType.CHEQUE);
                break;
            case 2:
                ((OnPaymentOptionClick) mContext).onOptionClicked(PaymentOptionsActivity.PaymentType.BANK_TRANSFER);
                break;
        }
    }
}
