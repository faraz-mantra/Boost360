package com.nowfloats.manageinventory;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * Created by NowFloats on 27-03-2017 by Romio Ranjan Jena.
 */

public class ShipOrderFragment extends DialogFragment {

    String shippedOn = "";
    private TextInputEditText etShippedOn, etDeliveryProvider, etTrackingNumber, etTrackingURL, etDeliveryCharges;
    private Button btnConfirm;
    private ShipOrderFragment.OnResultReceive mResultListener;

    public static ShipOrderFragment newInstance() {

        ShipOrderFragment fragment = new ShipOrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getDialog().getWindow()
//                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void setResultListener(ShipOrderFragment.OnResultReceive onResultReceive) {
        mResultListener = onResultReceive;
    }

    private boolean verifyData() {

        boolean isAllFieldsValid = true;
        if (etShippedOn.getText().toString().trim().equals("")) {
            isAllFieldsValid = false;
            etShippedOn.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        }
        if (etDeliveryCharges.getText().toString().trim().equals("")) {
            isAllFieldsValid = false;
            etDeliveryCharges.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        }


        if (!isAllFieldsValid) {
            Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_mandatory_fields), Toast.LENGTH_SHORT).show();
        }

        return isAllFieldsValid;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ship_order, container, false);
        setCancelable(true);
        btnConfirm = v.findViewById(R.id.btnConfirm);
        etShippedOn = v.findViewById(R.id.etShippedOn);
        etDeliveryProvider = v.findViewById(R.id.etDeliveryProvider);
        etDeliveryCharges = v.findViewById(R.id.etDeliveryCharges);
        etTrackingNumber = v.findViewById(R.id.etTrackingNumber);
        etTrackingURL = v.findViewById(R.id.etTrackingURL);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verifyData()) {
                    btnConfirm.setClickable(false);
                    btnConfirm.setEnabled(false);
                    btnConfirm.setVisibility(View.INVISIBLE);
                    mResultListener.OnResult(shippedOn,
                            etDeliveryProvider.getText().toString(), etTrackingNumber.getText().toString(), etTrackingURL.getText().toString(),
                            Double.parseDouble(etDeliveryCharges.getText().toString()));
                }
            }
        });

        etShippedOn.setInputType(InputType.TYPE_NULL);
        etShippedOn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    pickDate(etShippedOn);
                }
                return true;
            }
        });

        return v;
    }

    public void pickDate(final EditText et) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat formatter = new SimpleDateFormat(Methods.ISO_8601_24H_FULL_FORMAT, Locale.ENGLISH);
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                shippedOn = formatter.format(calendar.getTime());
                et.setText(monthOfYear + "-" + dayOfMonth + "-" + year);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public interface OnResultReceive {
        void OnResult(String shippedOn, String deliveryProvider, String trackingNumber,
                      String trackingURL, double deliveryCharges);

    }


}