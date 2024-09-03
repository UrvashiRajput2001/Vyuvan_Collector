package com.vyuvancollectors.PersonalLoan.StartActivitys

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.vyuvancollectors.PersonalLoan.TypesOfEMI.*
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityPersonalcollectiontypesBinding
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


class PersonalCollectionTypes : AppCompatActivity() {

    private var binding : ActivityPersonalcollectiontypesBinding? = null

    val typeAgent = "Agent"

    @SuppressLint("UseCompatLoadingForDrawables", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val tag = bundle?.get("tag") as String?
        @Suppress("DEPRECATION")
        val typo = bundle?.get("typo") as String?

        binding = ActivityPersonalcollectiontypesBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        agentPendingTodayAmount()

        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }

        binding?.dailyBtn?.setOnClickListener {
           daily()
        }

        binding?.monthlyBtn?.setOnClickListener {
            monthly()
        }

        binding?.weeklyBtn?.setOnClickListener {
            weekly()
        }

//        binding?.customBtn?.setOnClickListener {
//            yearly()
//        }

        binding?.tagTxt?.text = "$tag"

        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }

        val sdf = SimpleDateFormat("dd.MM.yyyy/EEEE")
        val date : String = sdf.format(Date())

        binding?.dateTxt?.text = "Date : $date"

    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun daily(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        @Suppress("DEPRECATION")
        val typo = bundle?.get("typo") as String?

        intent = Intent(this, Daily::class.java)
        intent.putExtra("token","$token")
        intent.putExtra("agentId","$agentId")
        intent.putExtra("typo",typo)
        startActivity(intent)
    }

    private fun monthly(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        @Suppress("DEPRECATION")
        val typo = bundle?.get("typo") as String?

        intent = Intent(this, Monthly::class.java)
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

        intent = Intent(this, Weekly::class.java)
        intent.putExtra("token","$token")
        intent.putExtra("agentId","$agentId")
        intent.putExtra("typo",typo)
        startActivity(intent)
    }


    private fun yearly(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        @Suppress("DEPRECATION")
        val typo = bundle?.get("typo") as String?

        intent = Intent(this, Yearly::class.java)
        intent.putExtra("token","$token")
        intent.putExtra("agentId","$agentId")
        intent.putExtra("typo",typo)
        startActivity(intent)

    }

    private fun agentPendingTodayAmount(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        val loanType = "PL"

        val json = JsonObject()
        json.addProperty("agentId","$agentId")
        json.addProperty("loanType","$loanType")

        Suppress("DEPRECATION")
        val jsonObject: RequestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), json.toString())

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.postTwoHeadersWithTokenData(token.toString(),typeAgent,"v1/emi/amount/todayPendingAmount", jsonObject)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful){
                    val res = response.body()

                    Log.e("LILI","$res")
                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.getString("status")
                    val items = jsonObject.getString("items")

                    val jsonObject2 = JSONTokener(items.toString()).nextValue() as JSONObject

                    if(status == "true"){
                        val todayPendingAmount = jsonObject2.getDouble("todayPendingAmount")
                        val pendingAmount = roundOffDecimal(todayPendingAmount)

                        binding?.todayDueAmountTxt?.text = "Today Due :₹$pendingAmount"
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
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
        }
        return connected
    }

}