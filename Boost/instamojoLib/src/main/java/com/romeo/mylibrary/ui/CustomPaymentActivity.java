package com.romeo.mylibrary.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.instamojo.android.activities.PaymentActivity;
import com.instamojo.android.callbacks.JusPayRequestCallback;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.models.Card;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.Order;
import com.instamojo.android.network.Request;
import com.romeo.mylibrary.R;

import java.util.ArrayList;
import java.util.Collections;

public class CustomPaymentActivity extends AppCompatActivity {

    private static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char CARD_NUMBER_DIVIDER = '-';


    private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    private static final char CARD_DATE_DIVIDER = '/';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_options);
        makeUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //send back the result to Main activity
        if (requestCode == Constants.REQUEST_CODE) {
            setResult(resultCode);
            setIntent(data);
            finish();
        }
    }

    private void makeUI() {
        final Order order = getIntent().getParcelableExtra(Constants.ORDER);
        //finish the activity if the order is null or both the debit and netbanking is disabled
        if (order == null || (order.getCardOptions() == null
                && order.getNetBankingOptions() == null)) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        final EditText cardNumber = (EditText) findViewById(R.id.card_number);
        final EditText cardExpiryDate = (EditText) findViewById(R.id.card_expiry_date);
        cardNumber.setNextFocusDownId(R.id.card_expiry_date);
        final EditText cardHoldersName = (EditText) findViewById(R.id.card_holder_name);
        cardExpiryDate.setNextFocusDownId(R.id.card_holder_name);
        final EditText cvv = (EditText) findViewById(R.id.card_cvv);
        cardHoldersName.setNextFocusDownId(R.id.card_cvv);
        Button proceed = (Button) findViewById(R.id.proceed_with_card);
        View separator = findViewById(R.id.net_banking_separator);
        AppCompatSpinner netBankingSpinner = (AppCompatSpinner) findViewById(R.id.net_banking_spinner);

        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
                    s.replace(0, s.length(), concatString(getDigitArray(s, CARD_NUMBER_TOTAL_DIGITS), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));
                }
            }
        });

        cardExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, CARD_DATE_TOTAL_SYMBOLS, CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER)) {
                    s.replace(0, s.length(), concatString(getDigitArray(s, CARD_DATE_TOTAL_DIGITS), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER));
                }
            }
        });

        if (order.getCardOptions() == null) {
            //seems like card payment is not enabled
            findViewById(R.id.card_layout_1).setVisibility(View.GONE);
            findViewById(R.id.card_layout_2).setVisibility(View.GONE);
            proceed.setVisibility(View.GONE);
            separator.setVisibility(View.GONE);
        } else {
            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Card card = new Card();
                    card.setCardNumber(cardNumber.getText().toString().replace("-", ""));
                    card.setDate(cardExpiryDate.getText().toString());
                    card.setCardHolderName(cardHoldersName.getText().toString());
                    card.setCvv(cvv.getText().toString());
                    //Validate the card here
                    if (!cardValid(card)) {
                        return;
                    }
                    //Get order details form Juspay
                    proceedWithCard(order, card);
                }
            });
        }

        if (order.getNetBankingOptions() == null) {
            //seems like netbanking is not enabled
            separator.setVisibility(View.GONE);
            netBankingSpinner.setVisibility(View.GONE);
        } else {
            final ArrayList<String> banks = new ArrayList<>();
            banks.addAll(order.getNetBankingOptions().getBanks().keySet());
            Collections.sort(banks);
            banks.add(0, "Select a Bank");
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, banks);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            netBankingSpinner.setAdapter(adapter);
            netBankingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        return;
                    }
                    //User selected a Bank. Hence proceed to Juspay
                    String bankCode = order.getNetBankingOptions().getBanks().get(banks.get(position));
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.URL, order.getNetBankingOptions().getUrl());
                    bundle.putString(Constants.POST_DATA, order.
                            getNetBankingOptions().getPostData(order.getAuthToken(), bankCode));
                    startPaymentActivity(bundle);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }
    }

    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

    private void proceedWithCard(Order order, Card card) {
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                getString(com.instamojo.android.R.string.please_wait), true, false);
        Request request = new Request(order, card, new JusPayRequestCallback() {
            @Override
            public void onFinish(final Bundle bundle, final Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (error != null) {
                            if (error instanceof Errors.ConnectionError) {
                                Log.e("App", "No internet");
                            } else if (error instanceof Errors.ServerError) {
                                Log.e("App", "Server Error. try again");
                            } else {
                                Log.e("App", error.getMessage());
                            }
                            return;
                        }
                        startPaymentActivity(bundle);
                    }
                });
            }
        });
        request.execute();
    }

    private boolean cardValid(Card card) {
        if (!card.isCardValid()) {

            if (!card.isCardNameValid()) {
                showErrorToast("Card Holders Name is invalid");
            }

            if (!card.isCardNumberValid()) {
                showErrorToast("Card Number is invalid");
            }

            if (!card.isDateValid()) {
                showErrorToast("Expiry date is invalid");
            }

            if (!card.isCVVValid()) {
                showErrorToast("CVV is invalid");
            }

            return false;
        }

        return true;
    }

    private void startPaymentActivity(Bundle bundle) {
        // Start the payment activity
        //Do not change this unless you know what you are doing
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtras(getIntent());
        intent.putExtra(Constants.PAYMENT_BUNDLE, bundle);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}