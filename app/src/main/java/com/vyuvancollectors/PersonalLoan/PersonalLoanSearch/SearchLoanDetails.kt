package com.vyuvancollectors.PersonalLoan.PersonalLoanSearch

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivitySearchLoanDetailsBinding
import com.google.gson.JsonObject
import com.vyuvancollectors.PersonalLoan.EMIDetailsAdapter.EmiListDailyAdapter
import com.vyuvancollectors.PersonalLoan.PDF.Daily.DailyPdfConverter
import com.vyuvancollectors.PersonalLoan.PDF.Daily.DataForDailyPdf
import com.vyuvancollectors.PersonalLoan.PDF.Daily.PdfDailyEmiData
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchLoanDetails : AppCompatActivity() {
    private var binding : ActivitySearchLoanDetailsBinding? = null

    private var recyclerView : EmiListRVForPhoneSearch? = null
    private var list = ArrayList<EmiDataClassForPhoneSearch>()
    private var recyclerView2 : EmiListDailyAdapter? = null
    private var PERMISSION_CODE = 101
    val typeAgent = "Agent"

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchLoanDetailsBinding.inflate (layoutInflater)

        setContentView(binding?.root)

        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }

        eMIListAPI()


        if (checkPermissions()) {
            Toast.makeText(this, "Permissions Granted..", Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }

        binding?.downloadBtn?.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                forPdfLoanDetailsApi()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun eMIListAPI(){
        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val name1 = bundle.get("name").toString()
        @Suppress("DEPRECATION")
        val mobile1 = bundle.get("phone")
        @Suppress("DEPRECATION")
        val customerId1 = bundle.get("customerId").toString()
        @Suppress("DEPRECATION")
        val loanAmount1 = bundle.get("loanAmount")
        @Suppress("DEPRECATION")
        val collectionType1 = bundle.get("collectionType")
        @Suppress("DEPRECATION")
        val disburseDate1 = bundle.get("disburseDate")
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val agentId1 = bundle.get("agentId").toString()
        @Suppress("DEPRECATION")
        val emiAmount1 = bundle.get("emiAmount")
        @Suppress("DEPRECATION")
        val totalInterest1 = bundle.get("totalInterest")
        @Suppress("DEPRECATION")
        val totalAmount = bundle.get("totalAmount")

        val typeAgent = "Agent"


        binding?.nameTxt?.text = "$name1"
        binding?.mobileTxt?.text = "$mobile1"
        binding?.collectionTypeTxt?.text = "$collectionType1"
        binding?.totalInterestTxt?.text = "$totalInterest1"
        binding?.loanAmountTxt?.text = "$loanAmount1"
        binding?.totalAmountTxt?.text = "$totalAmount"
        binding?.disburseDateTxt?.text = "$disburseDate1"

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(token,typeAgent,"v1/emi/$agentId1/$customerId1")
        call?.enqueue(object  : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
//                    binding?.progressBar?.isVisible = false
//                    binding?.txtBar?.isVisible = false
                    val res = response.body()
                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.get("status")
                    val items = jsonObject.get("items")
                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray

                    if (status == true) {
                        for (i in 0 until jsonArray.length()) {
                            val emiDetails = jsonArray.getJSONObject(i).getString("emiDetails")

                            val jsonArray2 = JSONTokener(emiDetails.toString()).nextValue() as JSONArray

                            for (j in 0 until jsonArray2.length()) {
                                val emiAmount = jsonArray2.getJSONObject(j).getString("emiAmount")
                                val remainingAmount = jsonArray2.getJSONObject(j).getString("remainingAmount")
                                val collectionType = jsonArray2.getJSONObject(j).getString("collectionType")
                                val emiStatus = jsonArray2.getJSONObject(j).getString("status")
                                val dateOfCollect = jsonArray2.getJSONObject(j).getString("dateOfCollect")

                                list.add(
                                    EmiDataClassForPhoneSearch(
                                        emiAmount,
                                        remainingAmount,
                                        customerId1,
                                        collectionType,
                                        emiStatus,
                                        token,
                                        agentId1,
                                        dateOfCollect
                                    )
                                )

                            }
                            binding?.emiRv?.layoutManager = LinearLayoutManager(this@SearchLoanDetails)
                            recyclerView = EmiListRVForPhoneSearch(list)
                            binding?.emiRv?.adapter = recyclerView
                        }
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("urvashi", "$t your response is fail")
            }
        })
    }



    private fun forPdfLoanDetailsApi(){

        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val customerId1 = bundle.get("customerId").toString()
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(token,typeAgent,"v1/emi/getLoanEmis/$agentId/$customerId1")
        call?.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val list = java.util.ArrayList<DataForDailyPdf>()
                val emilist = java.util.ArrayList<PdfDailyEmiData>()
                if (response.isSuccessful){
                    val res = response.body()

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject

                    val status = jsonObject.getString("status")
                    val message = jsonObject.getString("message")
                    val items = jsonObject.getString("items")
                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray

                    if (status == "true") {
                        for (i in 0 until jsonArray.length()){
                            val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                            val totalInterest = jsonArray.getJSONObject(i).getString("totalInterest")
                            val totalAmount = jsonArray.getJSONObject(i).getString("totalAmount")
                            val disburseDate = jsonArray.getJSONObject(i).getString("disburseDate")
                            val collectedAmount = jsonArray.getJSONObject(i).getString("collectedAmount")
                            val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                            val interest = jsonArray.getJSONObject(i).getString("interest")
                            val customerDetail  = jsonArray.getJSONObject(i).getString("customerDetails")
                            val jsonObject2 = JSONTokener(customerDetail.toString()).nextValue() as JSONObject
                            val name = jsonObject2.getString("name")
                            val phone = jsonObject2.getString("phone")
                            val emiDetails  = jsonArray.getJSONObject(i).getString("emisDetails")

                            val jsonArray2 = JSONTokener(emiDetails.toString()).nextValue() as JSONArray

                            for (j in 0 until jsonArray2.length()){
                                val emiAmount = jsonArray2.getJSONObject(j).getString("emiAmount")
                                val dateOfCollect = jsonArray2.getJSONObject(j).getString("dateOfCollect")
                                val remainingAmount = jsonArray2.getJSONObject(j).getString("remainingAmount")
                                val status = jsonArray2.getJSONObject(j).getString("status")

                                emilist.add(PdfDailyEmiData(remainingAmount, emiAmount, dateOfCollect,status))
                            }
                            list.add(DataForDailyPdf(name, phone, loanAmount, interest, collectionType, totalInterest, totalAmount, disburseDate, collectedAmount))

                            DailyPdfConverter(list, emilist).createPdf(this@SearchLoanDetails, this@SearchLoanDetails)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("urvashi", "$t your response is fail")
            }
        })

    }


    /*Storage Permission*/
    private fun checkPermissions(): Boolean {
        val writeStoragePermission = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readStoragePermission = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && readStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CODE) {

            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                    == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
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
        } catch (e: Exception) {
        }
        return connected
    }


}