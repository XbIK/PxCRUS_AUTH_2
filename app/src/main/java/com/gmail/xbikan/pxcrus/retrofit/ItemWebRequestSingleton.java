package com.gmail.xbikan.pxcrus.retrofit;

import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.xbikan.pxcrus.R;
import com.gmail.xbikan.pxcrus.itemModel.ItemDetailPxC;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xbika on 14-Feb-18.
 */

public class ItemWebRequestSingleton {

    String loginFormType = "pwd";
    String login = "akotkovetc@yandex.ru";
    String pass = "pogemu70";
    String itemUrl = "https://www.phoenixcontact.com/online/myportal/ru/?uri=pxc-oc-itemdetail:pid=";



    public void itemDetailWeb(String articul, TextView view, ImageView statusview, ImageView updateView) {

        updateView.setEnabled(false);

       // retrofitClient = new RetrofitClient();
        PxcWebClient apiItem = RetrofitClient.getInstance().getExploreService();

        Completable getCookieRx = apiItem.getCookies();
        Completable loginWebPxC = apiItem.loginToWeb(loginFormType, login, pass);
        Single<ResponseBody> itemPage = apiItem.itemMainPage(
                itemUrl + articul);

        getCookieRx
                .subscribeOn(Schedulers.io())
                .andThen(loginWebPxC)
                .andThen(itemPage)
                .map(s -> parseResponse(s, articul))
                .flatMap(url -> apiItem.itemJsonDetail(url))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((List<ItemDetailPxC> myData) -> {
                            String sourceString = "";
                            String itemStaus = myData
                                    .get(0)
                                    .getAvailability()
                                    .getItemStatus();
                            if (itemStaus.equals("GREEN")) {
                                sourceString = myData
                                        .get(0)
                                        .getLine1()
                                        .replace("&nbsp;", " ")
                                        .replace("/", ".")
                                        .replace(":", ": <b>")
                                        .replace(")", "<\b> )")
                                        .replace("<.strong>", "")
                                        .replace("отгрузки", "отгрузки:")
                                        .replace("<strong>", "")
                                        .replace("(", "<br>")
                                        .replace("Возможная", "возможная")
                                        .replace("Наличие", "наличие");
                                statusview.setColorFilter(Color.rgb(135, 188, 67));
                            } else {
                                sourceString = "не доступен для заказа";
                                statusview.setColorFilter(Color.rgb(244, 67, 54));
                            }
                            Log.v("RXJAVA", itemStaus);
                            view.setText(Html.fromHtml(sourceString));
                            statusview.setVisibility(View.VISIBLE);
                            updateView.setEnabled(true);
                            updateView.clearAnimation();


                        },
                        throwable -> {
                            Log.v("RXJAVA_ERROR", "Не удалось подключиться к сайту");
                            view.setText("не удалось загрузить информацию");
                            statusview.setVisibility(View.GONE);
                            updateView.setEnabled(true);
                            updateView.clearAnimation();
                        }
                );

    }


    private String parseResponse(ResponseBody response, String art) throws IOException {

        Document doc = Jsoup.parse(response.string());
        Element link = doc.select("base").first();
        String linkHref = link.attr("href");
        Elements getPrice = doc.select("div#pxc-itemdetail-header-getPricesUrl");
        String getPriceUrl = getPrice.attr("data-url");
        Elements info = doc.select("div#pxc-itemdetail-header-request-cache");
        String token = info.attr("data-request-token");
        String requestWarehouse = linkHref + getPriceUrl + "?productId=" + art + "&requestCacheToken="
                + token + "&productionTextSet=false";
        return requestWarehouse;
    }
}
