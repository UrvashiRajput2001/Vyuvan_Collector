package com.vyuvancollectors.AddCustomer

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
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityCustomerListBinding
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class CustomerList : AppCompatActivity() {

    private var binding : ActivityCustomerListBinding? = null
    private var recyclerView : RvCustomerList? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomerListBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        customerListApi()

        binding?.progressBar?.isVisible = false
        binding?.txtBar?.isVisible = false
        binding?.messageTxt?.isVisible = false
        binding?.swipeLl?.isRefreshing = false

        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }


        binding?.searchEt?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                forSearch()
            }
            true
        }


        binding?.addCustomerBtn?.setOnClickListener {

            val bundle = intent.extras
            @Suppress("DEPRECATION")
            val token = bundle?.get("token") as String?
            @Suppress("DEPRECATION")
            val agentId = bundle?.get("agentId") as String?
            val intent = Intent(this,AddCustomerForm::class.java)
            intent.putExtra("token","$token")
            intent.putExtra("agentId","$agentId")
            startActivity(intent)
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

        binding?.swipeLl?.setOnRefreshListener {
            binding?.swipeLl?.isRefreshing = false
             customerListApi()
            recyclerView!!.notifyDataSetChanged()
            binding?.messageTxt?.isVisible = false
            binding?.customerListRv?.isVisible = true
        }
    }


    private fun customerListApi(){

        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        val type  = "Agent"

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(token.toString(),type,"v1/customers/getAllCustomer/$agentId")
        call?.enqueue(object  : Callback<JsonObject> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val list = ArrayList<CustomerDetialsData>()
                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
                    val res = response.body()

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.get("status")
                    val items = jsonObject.get("items")
                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray

                    if(jsonArray.isNull(0)){
                        binding?.messageTxt?.isVisible = true
                        binding?.messageTxt?.text = "No Customer"
                    }
                    if (status == true) {
                        for (i in 0 until jsonArray.length()) {
                            val name = jsonArray.getJSONObject(i).getString("name")
                            val phone = jsonArray.getJSONObject(i).getString("phone")
                            val whatsapp = jsonArray.getJSONObject(i).getString("whatsAppNumber")
                            val email = jsonArray.getJSONObject(i).getString("email")
                            val address = jsonArray.getJSONObject(i).getString("address")
                            val aadharNo = jsonArray.getJSONObject(i).getString("aadhaarNumber")

                            list.add(CustomerDetialsData(name,email,phone,whatsapp,address,aadharNo))

                            binding?.customerListRv?.layoutManager = LinearLayoutManager(this@CustomerList)
                            recyclerView = RvCustomerList(list)
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

    private fun forSearch() {
        val phone  = binding?.searchEt?.text

        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        val type  = "Agent"

        if (binding?.searchEt?.text?.length == 10){

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(token.toString(),type,"v1/customers/getCustomerByPhone/$agentId/$phone")
        call?.enqueue(object  : Callback<JsonObject> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val list = ArrayList<CustomerDetialsData>()
                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false

                    val res = response.body()
                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.get("status")
                    val items = jsonObject.get("items")
                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray

                    if(jsonArray.isNull(0)){
                        binding?.messageTxt?.isVisible = true
                        binding?.messageTxt?.text = "No Customer"
                        binding?.customerListRv?.isVisible = false
                    }
                    if (status == true) {
                        for (i in 0 until jsonArray.length()) {
                            val name = jsonArray.getJSONObject(i).getString("name")
                            val phone = jsonArray.getJSONObject(i).getString("phone")
                            val whatsapp = jsonArray.getJSONObject(i).getString("whatsAppNumber")
                            val email = jsonArray.getJSONObject(i).getString("email")
                            val address = jsonArray.getJSONObject(i).getString("address")
                            val aadharNo = jsonArray.getJSONObject(i).getString("aadhaarNumber")

                            list.add(CustomerDetialsData(name,email,phone,whatsapp,address,aadharNo))

                            binding?.customerListRv?.layoutManager = LinearLayoutManager(this@CustomerList)
                            recyclerView = RvCustomerList(list)
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
        }else {
            binding?.progressBar?.isVisible = false
            binding?.txtBar?.isVisible = false
            binding?.messageTxt?.isVisible = true
            binding?.messageTxt?.text = "'Please Enter 10 Digit Number'"
            binding?.customerListRv?.isVisible = false
        }
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
}