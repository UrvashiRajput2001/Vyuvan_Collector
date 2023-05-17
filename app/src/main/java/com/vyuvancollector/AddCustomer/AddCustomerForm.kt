package com.vyuvancollector.AddCustomer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.vyuvancollector.R
import com.vyuvancollector.Retrofit.ApiClient
import com.vyuvancollector.Retrofit.ApiInterface
import com.vyuvancollector.databinding.ActivityAddCustomerFormBinding
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCustomerForm : AppCompatActivity() {

    private var binding : ActivityAddCustomerFormBinding? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddCustomerFormBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        binding?.submitBtn?.setOnClickListener {
            if (binding?.nameTxt?.text?.length == 0){
            binding?.submitBtn?.isEnabled = false
                binding?.textView1?.setTextColor(resources.getColor(R.color.red))
                binding?.textView2?.setTextColor(resources.getColor(R.color.grey))
                binding?.textView3?.setTextColor(resources.getColor(R.color.grey))
                binding?.textView4?.setTextColor(resources.getColor(R.color.grey))
                binding?.textView5?.setTextColor(resources.getColor(R.color.grey))
        }else  if (binding?.emailTxt?.text?.length == 0) {
                binding?.submitBtn?.isEnabled = false
                binding?.textView2?.setTextColor(resources.getColor(R.color.red))
                binding?.textView1?.setTextColor(resources.getColor(R.color.grey))
                binding?.textView3?.setTextColor(resources.getColor(R.color.grey))
                binding?.textView4?.setTextColor(resources.getColor(R.color.grey))
                binding?.textView5?.setTextColor(resources.getColor(R.color.grey))
            }else if (binding?.phoneTxt?.text?.length != 10){
            binding?.submitBtn?.isEnabled = false
                binding?.textView3?.setTextColor(resources.getColor(R.color.red))
             binding?.textView1?.setTextColor(resources.getColor(R.color.grey))
             binding?.textView2?.setTextColor(resources.getColor(R.color.grey))
             binding?.textView4?.setTextColor(resources.getColor(R.color.grey))
                binding?.textView5?.setTextColor(resources.getColor(R.color.grey))
        }else if (binding?.whatsappTxt?.text?.length != 10){
            binding?.submitBtn?.isEnabled = false
                binding?.textView4?.setTextColor(resources.getColor(R.color.red))
                    binding?.textView1?.setTextColor(resources.getColor(R.color.grey))
                    binding?.textView2?.setTextColor(resources.getColor(R.color.grey))
                    binding?.textView3?.setTextColor(resources.getColor(R.color.grey))
                binding?.textView5?.setTextColor(resources.getColor(R.color.grey))
        }else if(binding?.addressEt?.text?.length == 0){
                binding?.textView5?.setTextColor(resources.getColor(R.color.red))
                binding?.textView1?.setTextColor(resources.getColor(R.color.grey))
                binding?.textView2?.setTextColor(resources.getColor(R.color.grey))
                binding?.textView3?.setTextColor(resources.getColor(R.color.grey))
                binding?.textView4?.setTextColor(resources.getColor(R.color.grey))
        }else{
            addCustomerApi()
                binding?.nameTxt?.setText("")
                binding?.emailTxt?.setText("")
                binding?.phoneTxt?.setText("")
                binding?.whatsappTxt?.setText("")
                binding?.addressEt?.setText("")
            binding?.submitBtn?.isEnabled = true

        }
            binding?.submitBtn?.isEnabled = true


        }

        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addCustomerApi(){

        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?


        val customerName = binding?.nameTxt?.text
        val customerEmail = binding?.emailTxt?.text
        val customerPhone = binding?.phoneTxt?.text
        val customerWhatsapp = binding?.whatsappTxt?.text
        val customerAddress = binding?.addressEt?.text

        val json = JsonObject()
        json.addProperty("agentId","$agentId")
        json.addProperty("name","$customerName")
        json.addProperty("email","$customerEmail")
        json.addProperty("phone","$customerPhone")
        json.addProperty("whatsAppNumber","$customerWhatsapp")
        json.addProperty("address","$customerAddress")

        @Suppress("DEPRECATION")
        val jsonObject: RequestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), json.toString())

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.postData2(token.toString(),"v1/customers/createCustomer",jsonObject)
        call?.enqueue(object  : Callback<JsonObject> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val res = response.body()
                Log.e("urvashi","$res res")
                val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                val status = jsonObject.get("status")
                val message = jsonObject.get("message")
                val items = jsonObject.get("items")

                val jsonObject2 = JSONTokener(items.toString()).nextValue() as JSONObject
                Toast.makeText(this@AddCustomerForm, "$message", Toast.LENGTH_LONG).show()

                if (status == "true"){
                    val id = jsonObject2.get("_id")
                    Toast.makeText(this@AddCustomerForm,"$message", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("urvashi", "$t your response is fail")
            }
        })
    }

    private fun isConnected(): Boolean {
        var connected = false
        try {
            val cm =
                applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val nInfo = cm.activeNetworkInfo
            connected = nInfo != null && nInfo.isAvailable && nInfo.isConnected
            return connected
        } catch (e: Exception) {
            Log.e("Connectivity Exception", e.message!!)
        }
        return connected
    }
}