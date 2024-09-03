package com.vyuvancollectors.Retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {


    private val baseURL = "https://vyuvan-collector-staging-ntogv.ondigitalocean.app/"
//    private val baseURL = "https://vyuvan-collector-prod-rwyqy.ondigitalocean.app/"

    fun getInstance() :Retrofit? {
      val  retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(getRequestHeader())
                .baseUrl(baseURL)
            .build()
      return  retrofit

    }

    private fun getRequestHeader(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
    }
}


