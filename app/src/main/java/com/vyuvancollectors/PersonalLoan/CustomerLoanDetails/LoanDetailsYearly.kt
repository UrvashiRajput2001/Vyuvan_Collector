package com.vyuvancollectors.PersonalLoan.CustomerLoanDetails

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.vyuvancollectors.PersonalLoan.PDF.Custom.DataForYearlyPdf
import com.vyuvancollectors.PersonalLoan.PDF.Custom.PdfYearlyEmiData
import com.vyuvancollectors.PersonalLoan.EMIDetailsAdapter.EmiListYearlyAdapter
import com.vyuvancollectors.PersonalLoan.Emi_Data_Class.YearlyEmiModal
import com.vyuvancollectors.PersonalLoan.PDF.Custom.YearlyPdfConverter
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityLoanDetailsYearlyBinding
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoanDetailsYearly : AppCompatActivity() {

    private var binding : ActivityLoanDetailsYearlyBinding? = null

    private var recyclerView : EmiListYearlyAdapter? = null

    private var PERMISSION_CODE = 101

    val typeAgent = "Agent"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoanDetailsYearlyBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        yearlyEMIListAPI()
        forLoanDetails()

        if (Build.VERSION.SDK_INT < 34) {
            if (checkAndRequestPermissions()) {
                Toast.makeText(this, "Permissions Granted..", Toast.LENGTH_SHORT).show()
                Log.e("permission", "done")
            } else {
                requestPermission()
                Log.e("permission", "not")
            }
        }

        binding?.downloadBtn?.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                forPdfLoanDetailsApi()
                Log.e("urvashi","manish sir pareshaan karte hai")
            }
        }


        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }


    private fun yearlyEMIListAPI(){
        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val customerId = bundle.get("customerId").toString()
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val mobile = bundle?.get("mobile")
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(token,typeAgent,"v1/emi/$agentId/$customerId")
        call?.enqueue(object  : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    var list = ArrayList<YearlyEmiModal>()

                    val res = response.body()

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.get("status")
                    val message = jsonObject.get("message")
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
                                val emiNo = jsonArray2.getJSONObject(j).getString("emiNumber")

                                list.add(
                                    YearlyEmiModal(
                                        emiAmount,
                                        remainingAmount,
                                        customerId,
                                        collectionType,
                                        emiStatus,
                                        token,
                                        agentId,
                                        dateOfCollect,emiNo
                                    )
                                )
                            }
                            binding?.yearlyEmiRv1?.layoutManager = LinearLayoutManager(this@LoanDetailsYearly)
                            recyclerView = EmiListYearlyAdapter(list)
                            binding?.yearlyEmiRv1?.adapter = recyclerView
                        }
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("urvashi", "$t your response is fail")
            }
        })
    }

    private fun forLoanDetails(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val mobile = bundle?.get("mobile")
        @Suppress("DEPRECATION")
        val token = bundle?.get("token").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId").toString()

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(token,typeAgent,"v1/loans/getCustomerLoanDetails/$agentId/$mobile")
        call?.enqueue(object  : Callback<JsonObject> {
            @SuppressLint("SuspiciousIndentation", "NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val res = response.body()
                if (response.isSuccessful) {

                    var listPdf = ArrayList<DataForYearlyPdf>()

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.get("status")
                    val message = jsonObject.get("message")
                    val items = jsonObject.get("items")

                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray

                    if (status == true) {
                        for (i in 0 until jsonArray.length()) {
                            val name = jsonArray.getJSONObject(i).getString("name")
                            val phone = jsonArray.getJSONObject(i).getString("phone")
                            val loanDetail = jsonArray.getJSONObject(i).getString("loanDetail")

                            val jsonObject2 = JSONTokener(loanDetail.toString()).nextValue() as JSONObject
                            val collectionType = jsonObject2.getString("collectionType")
                            val loanAmount = jsonObject2.getString("loanAmount")
                            val disburseDate = jsonObject2.getString("disburseDate")
                            val collectedAmount = jsonObject2.getString("collectedAmount")
                            val totalInterest = jsonObject2.getString("totalInterest")
                            val totalAmount = jsonObject2.getString("totalAmount")

                            binding?.nameTxt?.text = "$name"
                            binding?.mobileTxt?.text = "Mobile : $mobile"
                            binding?.collectionTypeTxt?.text = "Type : $collectionType"
                            binding?.totalInterestTxt?.text = "Interest : $totalInterest"
                            binding?.loanAmountTxt?.text = "Loan Amount : $loanAmount"
                            binding?.totalAmountTxt?.text = "Total : $totalAmount"
                            binding?.disburseDateTxt?.text = "Disburse : $disburseDate"
                            binding?.collectionAmountTxt?.text = "Collected EMI : Rs.$collectedAmount"

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
                val list = ArrayList<DataForYearlyPdf>()
                val emilist = ArrayList<PdfYearlyEmiData>()
                if (response.isSuccessful) {

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

                                emilist.add(PdfYearlyEmiData(remainingAmount, emiAmount, dateOfCollect))
                            }
                            list.add(
                                DataForYearlyPdf(
                                    name,
                                    phone,
                                    loanAmount,
                                    interest,
                                    collectionType,
                                    totalInterest,
                                    totalAmount,
                                    disburseDate,
                                    collectedAmount
                                )
                            )
//                            YearlyPdfConverter(list, emilist).createPdf(
//                                this@LoanDetailsYearly,
//                                this@LoanDetailsYearly
//                            )
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

        val readStoragePermission = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && readStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your action
            } else {
                // Permission denied, show a message and potentially exit
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    PERMISSION_CODE
                )
                return false
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    PERMISSION_CODE
                )
                return false
            }
        }
        return true
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