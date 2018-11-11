package com.gmail.xbikan.pxcrus.retrofit;

import com.gmail.xbikan.pxcrus.itemModel.ItemDetailPxC;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by xbika on 14-Feb-18.
 */

public interface PxcWebClient {

    //1-ый запрос
    @GET("https://www.phoenixcontact.com/online/portal/ru/pxc/offcontext/")
    @Headers({
            "authority: www.phoenixcontact.com",
            "method: GET",
            "path: online/portal/ru/pxc/offcontext/login",
            "scheme: https",
            "accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
            "accept-encoding: gzip, deflate, br",
            "accept-language: ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7,ar;q=0.6",
            "cache-control: max-age=0",
            "upgrade-insecure-requests: 1",
            "user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36"})
    Completable getCookies();

    //2-ой запрос
    @FormUrlEncoded
    @POST("https://www.phoenixcontact.com/pkmslogin.form?vp=ru/")
    Completable loginToWeb(
            @Field("login-form-type") String loginType,
            @Field("username") String username,
            @Field("password") String password

    );

    //3-ий запрос
    @GET
    Single<ResponseBody> itemMainPage(@Url String itemMainUrl);

    //4-ый запрос
    @GET
    Single <List<ItemDetailPxC>> itemJsonDetail(@Url String itemDetailUrl);
}
