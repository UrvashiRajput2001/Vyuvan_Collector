package com.vyuvancollectors.SavingAccount.CustomerListSavingAccount

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.vyuvancollectors.AddCustomer.AddCustomerForm
import com.vyuvancollectors.AddCustomer.CustomerDetialsData
import com.vyuvancollectors.AddCustomer.RvCustomerList
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityCustomerListSavingAccountBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class CustomerListSavingAccount : AppCompatActivity() {

    private var  binding : ActivityCustomerListSavingAccountBinding? = null
    private var recyclerView : CustomerListSavingAccountRV? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerListSavingAccountBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        api()

        binding?.progressBar?.isVisible = false
        binding?.txtBar?.isVisible = false
        binding?.messageTxt?.isVisible = false

        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }


        binding?.searchEt?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchapi()
            }
            true
        }



        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
//            binding?.messageTxt?.isVisible = true
            binding?.progressBar?.isVisible = false
            binding?.txtBar?.isVisible = false
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
//        recyclerView!!.notifyDataSetChanged()
    }

    private fun isConnected(): Boolean {
        var connected = false
        try {
            val cm =
                applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val nInfo = cm.activeNetworkInfo
            connected = nInfo != null && nInfo.isAvailable && nInfo.isConnected
            return connected
        } catch (e: Exception){
        }
        return connected
    }


    private fun api(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        val type  = "Agent"

        val json = JsonObject()
        json.addProperty("agentId","$agentId")
        json.addProperty("phone","")

        @Suppress("DEPRECATION")
        val jsonObjectInBody : RequestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), json.toString())

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.postTwoHeadersWithTokenData(token.toString(),type,"v1/saving/savingAccount/byAgentIdSavingAcRequestList",jsonObjectInBody)
        call?.enqueue(object  : Callback<JsonObject> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val list = ArrayList<CustomerListSavingAccountData>()
                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
                    val res = response.body()

                    Log.e("res","Respones $res" )

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.get("status")
                    val items = jsonObject.get("items")
                    val message = jsonObject.get("message")
                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray

                    Log.e("CustomerList","$res")

//                    if(jsonArray.isNull(0)){
//                        binding?.messageTxt?.isVisible = true
//                        binding?.messageTxt?.text = "No Customer"
//                    }
                    if (status == true) {
                        for (i in 0 until jsonArray.length()) {
                            val customerId = jsonArray.getJSONObject(i).getString("customerId")
                            val customerDetail = jsonArray.getJSONObject(i).getString("customerDetail")
                            val jsonArray2 = JSONTokener(customerDetail.toString()).nextValue() as JSONArray
                            for (j in 0 until jsonArray2.length()) {
                                val name = jsonArray2.getJSONObject(j).getString("name")
                                val phone = jsonArray2.getJSONObject(j).getString("phone")
                                val localCity = jsonArray2.getJSONObject(j).getString("localCity")
                                val address = jsonArray2.getJSONObject(j).getString("localAddress")

                                list.add(CustomerListSavingAccountData(name,phone,address,localCity,customerId,token.toString(),agentId.toString()))

                            }
                            binding?.customerListRv?.layoutManager = LinearLayoutManager(this@CustomerListSavingAccount)
                            recyclerView = CustomerListSavingAccountRV(list)
                            binding?.customerListRv?.adapter = recyclerView
                            recyclerView!!.notifyDataSetChanged()
                        }
                    }else if (message == false) {
                        binding?.messageTxt?.text = "No Data"
                    }


                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("urvashi", "$t your response is fail")
            }
        })


    }


    private fun searchapi(){
        val phone  = binding?.searchEt?.text

        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        val type  = "Agent"

        if (binding?.searchEt?.text?.length == 10) {

            val json = JsonObject()
            json.addProperty("agentId", "$agentId")
            json.addProperty("phone", "$phone")

            @Suppress("DEPRECATION")
            val jsonObjectInBody: RequestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(), json.toString()
            )

            val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
            val call = apiClient?.postTwoHeadersWithTokenData(
                token.toString(),
                type,
                "v1/saving/savingAccount/byAgentIdSavingAcRequestList",
                jsonObjectInBody
            )
            call?.enqueue(object : Callback<JsonObject> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val list = ArrayList<CustomerListSavingAccountData>()
                        binding?.progressBar?.isVisible = false
                        binding?.txtBar?.isVisible = false
                        val res = response.body()

                        val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                        val status = jsonObject.get("status")
                        val items = jsonObject.get("items")
                        val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray

                        Log.e("CustomerList", "$jsonArray")

                        if (jsonArray.isNull(0)) {
                            binding?.messageTxt?.isVisible = true
                            binding?.messageTxt?.text = "No Customer"
                        }
                        if (status == true) {
                            for (i in 0 until jsonArray.length()) {
                                val customerId = jsonArray.getJSONObject(i).getString("customerId")
                                val customerDetail =
                                    jsonArray.getJSONObject(i).getString("customerDetail")
                                val jsonArray2 =
                                    JSONTokener(customerDetail.toString()).nextValue() as JSONArray
                                for (j in 0 until jsonArray2.length()) {
                                    val name = jsonArray2.getJSONObject(j).getString("name")
                                    val phone = jsonArray2.getJSONObject(j).getString("phone")
                                    val localCity = jsonArray2.getJSONObject(j).getString("localCity")
                                    val address = jsonArray2.getJSONObject(j).getString("localAddress")

                                    list.add(
                                        CustomerListSavingAccountData(
                                            name,
                                            phone,
                                            address,
                                            localCity,
                                            customerId,
                                            token.toString(),
                                            agentId.toString()
                                        )
                                    )
                                }
                                binding?.customerListRv?.layoutManager =
                                    LinearLayoutManager(this@CustomerListSavingAccount)
                                recyclerView = CustomerListSavingAccountRV(list)
                                binding?.customerListRv?.adapter = recyclerView
                                recyclerView!!.notifyDataSetChanged()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("urvashi", "$t your response is fail")
                }
            })
        }


    }



}