package com.vyuvancollector.Retrofit

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface{

    @GET
    fun postDataGET2(@Header("token") token: String,@Url remainingURL: String) : Call<JsonObject>

    @POST
    fun postData(@Url remainingURL: String, @Body jsonObject: RequestBody) : Call<JsonObject>

   @POST
   fun postData2(@Header("token") token: String,@Url remainingURL: String,@Body jsonObject: RequestBody) : Call<JsonObject>
}