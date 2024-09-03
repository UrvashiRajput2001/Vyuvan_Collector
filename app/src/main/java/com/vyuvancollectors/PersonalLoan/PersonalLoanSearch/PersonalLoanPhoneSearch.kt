package com.vyuvancollectors.PersonalLoan.PersonalLoanSearch

import android.annotation.SuppressLint
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
import com.vyuvancollectors.databinding.ActivityPhoneSearchBinding
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalLoanPhoneSearch : AppCompatActivity() {
    private var binding : ActivityPhoneSearchBinding? = null

    private var recyclerView : RecyclerViewForPhoneSearch? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhoneSearchBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
//            binding?.messageTxt?.isVisible = true
            binding?.progressBar?.isVisible = false
            binding?.txtBar?.isVisible = false
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }

        binding?.progressBar?.isVisible = false
        binding?.txtBar?.isVisible = false
        binding?.messageTxt?.isVisible = false
        binding?.sorryImg?.isVisible = false

        binding?.searchEt?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding?.messageTxt?.isVisible = false
                if(binding?.searchEt?.text?.length == 10){
                    Log.e("urvashi","hhy else")
                    forSearch()
                }else{
                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
                    binding?.messageTxt?.isVisible = true
                    binding?.messageTxt?.text = "'Please Enter 10 Digit Number'"
                }

            }
            true
        }

        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }

    }



    private fun forSearch(){
        var phone = binding?.searchEt?.text

        val typeAgent = "Agent"

        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(token.toString(),typeAgent,"v1/loans/getCustomerLoanDetails/$agentId/$phone")
        call?.enqueue(object  : Callback<JsonObject> {
            @SuppressLint("SuspiciousIndentation", "NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val res = response.body()
                if(response.isSuccessful){

                    val list = ArrayList<Data>()
                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
                    binding?.messageTxt?.isVisible = false
                    binding?.sorryImg?.isVisible = false

                val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                val status = jsonObject.get("status")
                Log.i("urvashi$$", "status $status")

                val message = jsonObject.get("message")
                Log.i("urvashi$$", "message $message")

                val items = jsonObject.get("items")
                Log.e("Urvashi","items $items")

                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                Log.i("urvashi$$", "jsonArray $jsonArray")

                    if(jsonArray.isNull(0)){
                        binding?.messageTxt?.isVisible = true
                        binding?.messageTxt?.text = "'Number not Register'"

                    }

                    if (status == true) {
                        for (i in 0 until jsonArray.length()) {

                            val name = jsonArray.getJSONObject(i).getString("name")
                            Log.e("urvashi", "name $name")

                            val phone = jsonArray.getJSONObject(i).getString("phone")
                            Log.e("urvashi", "phone $phone")

                            val loanDetail = jsonArray.getJSONObject(i).getString("loanDetail")
                            Log.e("urvashi", "loanDetail $loanDetail")

                            val jsonObject2 = JSONTokener(loanDetail.toString()).nextValue() as JSONObject
                            Log.e("urvashi", "loanDetail $loanDetail")

                            val collectionType = jsonObject2.getString("collectionType")
                            Log.e("urvashi","collectionType $collectionType")

                            val loanAmount = jsonObject2.getString("loanAmount")
                            Log.e("urvashi", "loanAmount $loanAmount")

                            val disburseDate = jsonObject2.getString("disburseDate")
                            Log.e("urvashi", "disburseDate $disburseDate")

                            val collectedAmount = jsonObject2.getString("collectedAmount")
                            Log.e("urvashi", "collectedAmount $collectedAmount")

                            val totalInterest = jsonObject2.getString("totalInterest")
                            Log.e("urvashi", "totalInterest $totalInterest")

                            val totalAmount = jsonObject2.getString("totalAmount")
                            Log.e("urvashi", "totalAmount $totalAmount")

                            val customerId = jsonObject2.getString("customerId")
                            Log.e("urvashi", "customerId $customerId")



                            list.add(
                                Data(
                                    name,
                                    phone,
                                    customerId,
                                    loanAmount,
                                    collectionType,
                                    agentId.toString(),
                                    token.toString(),
                                    collectedAmount,
                                    totalInterest,
                                    totalAmount,
                                    disburseDate
                                )
                            )
                            binding?.customerAllLoansEmiRv?.layoutManager = LinearLayoutManager(this@PersonalLoanPhoneSearch)
                            recyclerView = RecyclerViewForPhoneSearch(list)
                            binding?.customerAllLoansEmiRv?.adapter = recyclerView
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