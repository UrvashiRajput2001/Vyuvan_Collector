package com.vyuvancollector.PersonalLoan.StartActivitys

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.vyuvancollector.Retrofit.ApiClient
import com.vyuvancollector.Retrofit.ApiInterface
import com.google.gson.JsonObject
import com.vyuvancollector.PersonalLoan.PersonalLoanSearch.PersonalLoanPhoneSearch
import com.vyuvancollector.databinding.ActivityPersonalemistatusBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class PersonalEmiStatus : AppCompatActivity() {

    private var binding : ActivityPersonalemistatusBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPersonalemistatusBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        agentPendingTodayAmount()

        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }

        binding?.searchBtn?.isVisible = false

        binding?.menuImg?.setOnClickListener {
            binding?.searchBtn?.isVisible = true
        }

        binding?.constLayout?.setOnClickListener {
            binding?.searchBtn?.isVisible = false
        }



        agentCollectedAmountApi()

        agentOverDueAmountAPI()

        val sdf = SimpleDateFormat("dd.MM.yyyy/EEEE")
        val date : String = sdf.format(Date())
        Log.e("Date","$date")

        binding?.dateTxt?.text = "Date : $date"

        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        var typo = ""
        var tag = ""

        binding?.searchBtn?.setOnClickListener {
            val intent = Intent(this, PersonalLoanPhoneSearch::class.java)
            intent.putExtra("token","$token")
            intent.putExtra("agentId","$agentId")
            startActivity(intent)
        }

        binding?.pendingBtn?.setOnClickListener {
            typo = "0"
            tag = "Pending EMI's"
            val intent = Intent(this, PersonalCollectionTypes::class.java)
            intent.putExtra("typo", typo)
            intent.putExtra("tag", tag)
            intent.putExtra("token","$token")
            intent.putExtra("agentId","$agentId")
            startActivity(intent)
            Log.e("urvashi","$tag $typo tag typo")
        }


        binding?.paidBtn?.setOnClickListener {
            typo = "1"
            tag = "Paid EMI's"
            val intent = Intent(this, PersonalCollectionTypes::class.java)
            intent.putExtra("token","$token")
            intent.putExtra("tag", tag)
            intent.putExtra("agentId","$agentId")
            intent.putExtra("typo", typo)
            startActivity(intent)
            Log.e("urvashi","$tag $typo tag typo")
        }


        binding?.overdueBtn?.setOnClickListener {
            typo = "2"
            tag = "OverDue EMI's"
            val intent = Intent(this, PersonalCollectionTypes::class.java)
            intent.putExtra("token","$token")
            intent.putExtra("tag", tag)
            intent.putExtra("agentId","$agentId")
            intent.putExtra("typo", typo)
            startActivity(intent)
            Log.e("urvashi","$tag $typo tag typo")
        }


        binding?.allBtn?.setOnClickListener {
            typo = "3"
            tag = "All EMI's"
            val intent = Intent(this, PersonalCollectionTypes::class.java)
            intent.putExtra("token","$token")
            intent.putExtra("tag", tag)
            intent.putExtra("agentId","$agentId")
            intent.putExtra("typo", typo)
            startActivity(intent)
            Log.e("urvashi","$tag $typo tag typo")
        }


        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onResume() {
        super.onResume()
        agentCollectedAmountApi()
    }

    @SuppressLint("NewApi")
    private fun agentCollectedAmountApi(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        val loanType = "PL"

        val json = JsonObject()
        json.addProperty("agentId","$agentId")
        json.addProperty("loanType","$loanType")

        @Suppress("DEPRECATION")
        val jsonObject: RequestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), json.toString())

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.postData2(token.toString(),"v1/emi/amount/dateOfCollect", jsonObject)
        call?.enqueue(object  : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    Log.e("urvashi,","$res res ---")

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status1 = jsonObject.getString("status")
                    val items = jsonObject.getString("items")

                    val jsonObject2 = JSONTokener(items.toString()).nextValue() as JSONObject
                    Log.e("main","cash $jsonObject2")

                    if (status1 == "true") {

                        val cash = jsonObject2.getDouble("cashAmount")
                        val online = jsonObject2.getDouble("onlineAmount")
                        val total1 = cash + online

                        val cash1 = roundOffDecimal(cash)
                        val online1 = roundOffDecimal(online)
                        val  total = roundOffDecimal(total1)

                        binding?.agentCashTxt?.text = "₹$cash1"
                        binding?.agentOnlineTxt?.text = "₹$online1"
                        binding?.agentTotalTxt?.text = "₹$total"

                    }

                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("urvashi", "$t your response is fail")
            }
        })

    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    private fun agentOverDueAmountAPI(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        val loanType = "PL"

        val json = JsonObject()
        json.addProperty("agentId","$agentId")
        json.addProperty("loanType","$loanType")

        @Suppress("DEPRECATION")
        val jsonObject: RequestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), json.toString())

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.postData2(token.toString(),"v1/emi/amount/overDueAmount", jsonObject)
        call?.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful){

                    val res = response.body()
                    Log.e("urvashi,","$res res ---")

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.getString("status")
                    val items = jsonObject.getString("items")

                    val jsonObject2 = JSONTokener(items.toString()).nextValue() as JSONObject
                    Log.e("main","cash $jsonObject2")

                    if(status == "true"){
                        val agentOverDueAmount = jsonObject2.getDouble("emiAmount")

                        val overDueAmount = roundOffDecimal(agentOverDueAmount)

                        binding?.overdueTxt?.text = "₹$overDueAmount"

                    }

                }

            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

        })

    }

    private fun agentPendingTodayAmount(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        val loanType = "GL"

        val json = JsonObject()
        json.addProperty("agentId","$agentId")
        json.addProperty("loanType","$loanType")

        Suppress("DEPRECATION")
        val jsonObject: RequestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), json.toString())

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.postData2(token.toString(),"v1/emi/amount/todayPendingAmount", jsonObject)
        call?.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful){

                    val res = response.body()
                    Log.e("urvashi,","$res res ---")

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.getString("status")
                    val items = jsonObject.getString("items")

                    val jsonObject2 = JSONTokener(items.toString()).nextValue() as JSONObject
                    Log.e("main","cash $jsonObject2")

                    if(status == "true"){
                        val todayPendingAmount = jsonObject2.getDouble("todayPendingAmount")
                        val pendingAmount = roundOffDecimal(todayPendingAmount)

                        binding?.todayDueAmountTxt?.text = "Today Due : ₹$pendingAmount"
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
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