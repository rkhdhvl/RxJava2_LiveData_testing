package com.practice.rxjava2_livedata_testing.api

import android.content.Context
import android.util.Log
import com.practice.rxjava2_livedata_testing.util.Constants
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object Network {

fun getGitHubApi(context: Context): GithubApi{
    val cache = createCache(context);
    val networkCacheInterceptor = createCacheInterceptor()
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY;

    // first build an OkHttpClient
    val httpClient = OkHttpClient.Builder()
        .cache(cache)
        .addNetworkInterceptor(networkCacheInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val  retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(httpClient)
        .build()

    return retrofit.create(GithubApi::class.java);
}

    private fun createCache(context: Context): Cache? {
        var cache: Cache? = null
        try {
            val cacheSize = 10 * 1024 * 1024 // 10 MB
            val httpCacheDirectory = File(context.getCacheDir(), "http-cache")
            cache = Cache(httpCacheDirectory, cacheSize.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("error", "Failed to create create Cache!")
        }

        return cache
    }


    private fun createCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())

            var cacheControl = CacheControl.Builder()
                .maxAge(1, TimeUnit.MINUTES)
                .build()

            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }
    }

}