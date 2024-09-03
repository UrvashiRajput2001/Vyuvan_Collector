package com.vyuvancollectors.GroupLoan.OtherActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.vyuvancollectors.GroupLoan.GroupTypesOfEmi.AllInGroup
import com.vyuvancollectors.GroupLoan.GroupTypesOfEmi.MonthlyInGroup
import com.vyuvancollectors.GroupLoan.GroupTypesOfEmi.WeeklyInGroup
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityGroupCollectionTypeBinding
import com.google.gson.JsonObject
import com.vyuvancollectors.GroupLoan.GroupLoanPhoneSearch.GLPhoneSearch
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

class GroupCollectionType : AppCompatActivity() {

    private var binding : ActivityGroupCollectionTypeBinding? = null

    val typeAgent = "Agent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupCollectionTypeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        agentCollectedAmountApi()
        agentPendingTodayAmount()
        agentOverDueAmountAPI()

        binding?.searchBtn?.isVisible = false

        binding?.menuImg?.setOnClickListener {
            binding?.searchBtn?.isVisible = true
        }

        binding?.constLayout?.setOnClickListener {
            binding?.searchBtn?.isVisible = false
        }


        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val tag = bundle?.get("tag") as String?
        @Suppress("DEPRECATION")
        val typo = bundle?.get("typo") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?

        binding?.searchBtn?.setOnClickListener {
            val intent = Intent(this, GLPhoneSearch::class.java)
            intent.putExtra("token", "$token")
            intent.putExtra("agentId", "$agentId")
            startActivity(intent)
        }

        binding?.collectedAmountBtn?.setOnClickListener {
            val intent = Intent(this, GroupCollectedAmountActivity::class.java)
            intent.putExtra("token", "$token")
            intent.putExtra("agentId", "$agentId")
            startActivity(intent)

        }


        binding?.overdueAmountBtn?.setOnClickListener {
            val intent = Intent(this, GroupOverDueAmountPageActivity::class.java)
            intent.putExtra("token", "$token")
            intent.putExtra("agentId", "$agentId")
            startActivity(intent)
        }





        Log.e("urvashi tag", "$tag $typo tag")



        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.monthlyBtn?.setOnClickListener {
            monthly()
        }

        binding?.weeklyBtn?.setOnClickListener {
            weekly()
        }

        binding?.allBtn?.setOnClickListener {
            all()
        }

        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }

        val sdf = SimpleDateFormat("dd.MM.yyyy/EEEE")
        val date: String = sdf.format(Date())
        Log.e("Date", "$date")

        binding?.dateTxt?.text = "Date : $date"

    }

    override fun onResume() {
        super.onResume()
        agentCollectedAmountApi()
        agentOverDueAmountAPI()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun monthly(){

        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        @Suppress("DEPRECATION")
        val typo = bundle?.get("typo") as String?

        Log.e("urvashi","$token token -- $agentId agentId -- $typo typo")

        intent = Intent(this, MonthlyInGroup::class.java)
        intent.putExtra("token","$token")
        intent.putExtra("agentId","$agentId")
        intent.putExtra("typo",typo)
        startActivity(intent)
    }


    private fun weekly(){

        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        @Suppress("DEPRECATION")
        val typo = bundle?.get("typo") as String?

        Log.e("urvashi","$token token -- $agentId agentId -- $typo typo")

        intent = Intent(this, WeeklyInGroup::class.java)
        intent.putExtra("token","$token")
        intent.putExtra("agentId","$agentId")
        intent.putExtra("typo",typo)
        startActivity(intent)
    }

    private fun all(){

        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        @Suppress("DEPRECATION")
        val typo = bundle?.get("typo") as String?


        intent = Intent(this, AllInGroup::class.java)
        intent.putExtra("token","$token")
        intent.putExtra("agentId","$agentId")
        intent.putExtra("typo",typo)
        startActivity(intent)
    }




    @SuppressLint("NewApi")
    private fun agentCollectedAmountApi(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        val loanType = "GL"
        val typeAgent = "Agent"

        val json = JsonObject()
        json.addProperty("agentId","$agentId")
        json.addProperty("loanType","$loanType")

        @Suppress("DEPRECATION")
        val jsonObject: RequestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), json.toString())

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.postTwoHeadersWithTokenData(token.toString(),typeAgent,"v1/emi/amount/dateOfCollect", jsonObject)
        call?.enqueue(object  : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    Log.e("Group collection type","$res res ---")

                    val jsonObject2 = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status1 = jsonObject2.getString("status")
                    val items = jsonObject2.getString("items")

                    val jsonObject3 = JSONTokener(items.toString()).nextValue() as JSONObject
                    Log.e("main","cash $jsonObject2")

                    if (status1 == "true") {

                        val cash = jsonObject3.getDouble("cashAmount")
                        val online = jsonObject3.getDouble("onlineAmount")
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

    private fun agentOverDueAmountAPI(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        val loanType = "GL"

        val json = JsonObject()
        json.addProperty("agentId","$agentId")
        json.addProperty("loanType","$loanType")

        @Suppress("DEPRECATION")
        val jsonObject: RequestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), json.toString())

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.postTwoHeadersWithTokenData(token.toString(),typeAgent,"v1/emi/amount/overDueAmount", jsonObject)
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
        val call = apiClient?.postTwoHeadersWithTokenData(token.toString(),typeAgent,"v1/emi/amount/todayPendingAmount", jsonObject)
        call?.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful){

                    val res = response.body()
                    Log.e("group,","$res res ---")

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

}