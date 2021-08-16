package com.nowfloats.helper;

import android.util.Log;

import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

public class Helper {

    public static DecimalFormat getCurrencyFormatter() {
        return new DecimalFormat("#,##,##,##,##,##,##,###.##");
    }

    public static void loadCurrency() {
        //currencyValue = getString(R.string.currency_text);
        //binding.editCurrency.setText(currencyValue);

        new Thread(() -> {

            if (Constants.Currency_Country_Map == null) {
                Constants.Currency_Country_Map = new HashMap<>();
                Constants.currencyArray = new ArrayList<>();
            }

            if (Constants.Currency_Country_Map.size() == 0) {
                for (Locale locale : Locale.getAvailableLocales()) {

                    try {
                        if (locale != null && locale.getISO3Country() != null && Currency.getInstance(locale) != null) {
                            Currency currency = Currency.getInstance(locale);
                            String loc_currency = currency.getCurrencyCode();
                            String country = locale.getDisplayCountry();

                            if (!Constants.Currency_Country_Map.containsKey(country.toLowerCase())) {
                                Constants.Currency_Country_Map.put(country.toLowerCase(), loc_currency);
                                Constants.currencyArray.add(country + "-" + loc_currency);
                            }
                        }
                    } catch (Exception e) {
                        System.gc();
                        e.printStackTrace();
                    }
                }
            }

            /*try
            {
                currencyValue = Constants.Currency_Country_Map.get(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY).toLowerCase());
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }*/
        }).start();
    }

    public static boolean fileExist(String file_name) {

        File imgFile = new File(file_name);
        {
            return imgFile.exists();
        }
    }


}
