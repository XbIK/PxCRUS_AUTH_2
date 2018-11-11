package com.gmail.xbikan.pxcrus.retrofit;

import android.support.annotation.NonNull;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by xbika on 09-Feb-18.
 */

public interface CbClient {

    @GET
    Call<ValCurs> getCbValuteOnDate(@Url String url);

    void onSuccess(@NonNull String value);
    void onError(@NonNull Throwable throwable);

    //Single<ValCurs> getCbValuteRx(@Url String url);

}
