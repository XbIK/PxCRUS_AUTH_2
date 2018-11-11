package com.gmail.xbikan.pxcrus.retrofit;

import android.support.annotation.Nullable;
import android.util.Log;

import com.gmail.xbikan.pxcrus.ConstantsPxC;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import static android.content.ContentValues.TAG;

/**
 * Created by xbika on 09-Feb-18.
 */

public class RequestGenerator {

    private String cbrCurrency;

    public void updateEuroCB(String date,@Nullable final CbClient callbacks) {

        String dynamicLink = "XML_daily.asp?date_req=" + date;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.cbr.ru/scripts/")
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        CbClient api = retrofit.create(CbClient.class);
        Call<ValCurs> call = api.getCbValuteOnDate(dynamicLink);
        call.enqueue(new Callback<ValCurs>() {
            @Override
            public void onResponse(Call<ValCurs> call, Response<ValCurs> response) {
                ValCurs valuteFromResponse = response.body();
                int size = valuteFromResponse.getValute().size();
                for (int i = 0; i < size; i++) {
                    if (valuteFromResponse.getValute().get(i).getID().equals("R01239")) {
                         cbrCurrency = valuteFromResponse.getValute().get(i).getValue().replace(",", ".");
                        if (callbacks != null)
                            callbacks.onSuccess(cbrCurrency);
                    }
                }
            }
            @Override
            public void onFailure(Call<ValCurs> call, Throwable t) {
                Log.d(TAG, "ErrorRetrofit ");
                if (callbacks != null)
                    callbacks.onError(t);
            }

        });

    }
}

