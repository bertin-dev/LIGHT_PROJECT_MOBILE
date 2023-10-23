package com.adsyst.light_project_mobile.web_service;

import com.adsyst.light_project_mobile.BuildConfig;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitBuilder {

    //Domaine de production
    private static final String BASE_URL = "https://Api-light.adsys-solutions.com/api/";

    private final static OkHttpClient client = buildClient();
    private final static Retrofit retrofit = buildRetrofit(client);

    //cette methode permet d'intercepter la requête, ajouter les Headers correspondant ensuite de renvoyer à nouveau la requête
    private static OkHttpClient buildClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        Request.Builder builder = request.newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Connection", "close");

                        request = builder.build();

                        return chain.proceed(request);

                    }
                });

        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }

        return builder.build();

    }


    private static Retrofit buildRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }


    public static <T> T createService(Class<T> service) {
        return retrofit.create(service);
    }



    public static Retrofit getRetrofit() {
        return retrofit;
    }


}
