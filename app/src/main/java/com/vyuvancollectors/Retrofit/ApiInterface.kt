package com.vyuvancollectors.Retrofit

import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface{

//    @GET
//    fun getData(@Header("token") token: String,@Url remainingURL: String) : Call<JsonObject>

    @GET
    fun getTwoHeadersWithTokenData(@Header("token") token : String,@Header("type") type : String,@Url remainingURL: String): Call<JsonObject>

    @POST
    fun postData(@Url remainingURL: String, @Body jsonObject: RequestBody) : Call<JsonObject>


//   @POST
//   fun postData2(@Header("token") token: String,@Url remainingURL: String,@Body jsonObject: RequestBody) : Call<JsonObject>


   @POST
   fun postTwoHeadersWithTokenData(@Header("token") token : String,@Header("type") type : String,@Url remainingURL: String,@Body jsonObject: RequestBody) : Call<JsonObject>
}