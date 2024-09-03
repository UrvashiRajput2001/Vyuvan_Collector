package com.vyuvancollectors.PersonalLoan.TypesOfEMI

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.vyuvancollectors.PersonalLoan.Adapter.YearlyEMIRecyclerView
import com.vyuvancollectors.PersonalLoan.Data_Class.ModalYearly
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityYearlyBinding
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Yearly : AppCompatActivity() {

    private var binding: ActivityYearlyBinding? = null

    private var recyclerView: YearlyEMIRecyclerView? = null

    val typeAgent = "Agent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityYearlyBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
//            binding?.messageTxt?.isVisible = true
            binding?.progressBar?.isVisible = false
            binding?.txtBar?.isVisible = false
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }

        binding?.progressBar?.isVisible = true
        binding?.txtBar?.isVisible = true
        binding?.swipeLl?.isRefreshing = false
        binding?.messageTxt?.isVisible = false
        binding?.sorryImg?.isVisible = false
        binding?.dateLl?.isVisible = false

        yearlyEMIListAPI()

        binding?.searchEt?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                forSearch()
            }
            true
        }

        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }

        val yearly = "Yearly"
        val loanType = "PL"
        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()
        @Suppress("DEPRECATION")
        val typo = bundle.get("typo").toString()

        if (typo == "3"){
            binding?.searchEt?.isVisible = false
            binding?.dateLl?.isVisible = true
            val json = JsonObject()
            json.addProperty("agentId","$agentId")
            json.addProperty("collectionType","$yearly")
            json.addProperty("loanType","$loanType")

            @Suppress("DEPRECATION")
            val jsonObjectInBody : RequestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(), json.toString())

            val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
            val call = apiClient?.postTwoHeadersWithTokenData(
                token,typeAgent,
                "v1/emi/getAllEmis/agentId/loanType/collectionType",jsonObjectInBody
            )
            call?.enqueue(object : Callback<JsonObject> {

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val list = ArrayList<ModalYearly>()
                        binding?.progressBar?.isVisible = false
                        binding?.txtBar?.isVisible = false
                        val res = response.body()

                        val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                        val status = jsonObject.get("status")
                        val message = jsonObject.get("message")
                        val items = jsonObject.get("items")

                        val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                        if(jsonArray.isNull(0)){
                            binding?.messageTxt?.isVisible = true
                            binding?.messageTxt?.text = "No EMI's"
                            binding?.sorryImg?.isVisible = true
                        }
                        if (status == true) {
                            for (i in 0 until jsonArray.length()) {
                                val emiStatus = jsonArray.getJSONObject(i).getString("status")
                                val emiId = jsonArray.getJSONObject(i).getString("_id")
                                val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                                val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                                val emiAmount = jsonArray.getJSONObject(i).getString("emiAmount")
                                val customerId = jsonArray.getJSONObject(i).getString("customerId")
                                val loanId = jsonArray.getJSONObject(i).getString("loanId")
                                val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                                val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                                val remainingAmount = jsonArray.getJSONObject(i).getString("remainingAmount")
                                val customerDetail = jsonArray.getJSONObject(i).getString("customerDetail")

                                val jsonObject2 = JSONTokener(customerDetail.toString()).nextValue() as JSONObject
                                val name = jsonObject2.getString("name")
                                val phone = jsonObject2.getString("phone")

                                list.add(
                                    ModalYearly(
                                        name,
                                        phone,
                                        emiNo,
                                        emiId,
                                        loanAmount,
                                        emiAmount,
                                        remainingAmount,
                                        customerId,
                                        collectionType,
                                        loanId,
                                        agentId,
                                        token,
                                        dateOfCollect,
                                        typo,
                                        emiStatus
                                    )
                                )
                            }
                            binding?.yearlyEmiRv?.layoutManager =
                                LinearLayoutManager(this@Yearly)
                            recyclerView = YearlyEMIRecyclerView(list)
                            binding?.yearlyEmiRv?.adapter = recyclerView
                            recyclerView!!.notifyDataSetChanged()
                        }
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("urvashi", "$t your response is fail")
                }

            })

            val c : Calendar = Calendar.getInstance()
            var startYear: Int = c.get(Calendar.YEAR)
            var startDate: Int = c.get(Calendar.DATE)
            var startMonth: Int = c.get(Calendar.MONTH)
            var stopYear: Int = c.get(Calendar.YEAR)
            var stopDate: Int = c.get(Calendar.DATE)
            var stopMonth: Int = c.get(Calendar.MONTH)
            var listener: DatePickerDialog.OnDateSetListener? = null
            var listener2: DatePickerDialog.OnDateSetListener? = null
            var initDate: String? = null
            var endDate: String? = null

            listener = DatePickerDialog.OnDateSetListener { datePicker: DatePicker, Year: Int, month: Int, date: Int ->
                startDate = date
                startMonth = month
                startYear = Year
                var init = "$startDate-${startMonth + 1}-$startYear"

                initDate = parseDate(init.toString())
                binding?.startDateBtn?.text = initDate
            }

            binding?.startDateBtn?.setOnClickListener {
                DatePickerDialog(this, listener, startYear, startMonth, startDate).show()
            }

            listener2 = DatePickerDialog.OnDateSetListener { _: DatePicker, Year: Int, month: Int, date: Int ->

                Log.e("kakuda","kakuda")
                stopDate = date
                stopMonth = month
                stopYear = Year
                val end = "$stopDate-${stopMonth + 1}-$stopYear"
                endDate = parseDate(end.toString())

                binding?.endDateBtn?.text = endDate

                val json = JsonObject()
                json.addProperty("agentId","$agentId")
                json.addProperty("loanType","$loanType")
                json.addProperty("collectionType","$yearly")
                json.addProperty("fromDate","$initDate")
                json.addProperty("toDate","$endDate")

                @Suppress("DEPRECATION")
                val jsonObject: RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(
                    token,typeAgent,
                    "v1/emi/getEmiList/emiListByDate",jsonObject
                )
                call?.enqueue(object : Callback<JsonObject> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            val list = ArrayList<ModalYearly>()
                            binding?.progressBar?.isVisible = false
                            binding?.txtBar?.isVisible = false
                            val res = response.body()

                            Log.e("res ++","$res ++")

                            val jsonObject1 = JSONTokener(res.toString()).nextValue() as JSONObject
                            val status = jsonObject1.get("status")
                            val message = jsonObject1.get("message")
                            val items = jsonObject1.get("items")

                            val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                            if(jsonArray.isNull(0)){
                                binding?.messageTxt?.isVisible = true
                                binding?.messageTxt?.text = "No EMI's"
                            }
                            if (status == true) {
                                for (i in 0 until jsonArray.length()) {
                                    val emiStatus = jsonArray.getJSONObject(i).getString("status")
                                    val emiId = jsonArray.getJSONObject(i).getString("_id")
                                    val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                                    val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                                    val emiAmount = jsonArray.getJSONObject(i).getString("emiAmount")
                                    val customerId = jsonArray.getJSONObject(i).getString("customerId")
                                    val loanId = jsonArray.getJSONObject(i).getString("loanId")
                                    val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                                    val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                                    val remainingAmount = jsonArray.getJSONObject(i).getString("remainingAmount")
                                    val customerDetail = jsonArray.getJSONObject(i).getString("customerDetail")

                                    val jsonObject2 = JSONTokener(customerDetail.toString()).nextValue() as JSONObject
                                    val name = jsonObject2.getString("name")
                                    val phone = jsonObject2.getString("phone")

                                    list.add(
                                        ModalYearly(
                                            name,
                                            phone,
                                            emiNo,
                                            emiId,
                                            loanAmount,
                                            emiAmount,
                                            remainingAmount,
                                            customerId,
                                            collectionType,
                                            loanId,
                                            agentId,
                                            token,
                                            dateOfCollect,
                                            typo,
                                            emiStatus
                                        )
                                    )

                                }
                                binding?.yearlyEmiRv?.layoutManager =
                                    LinearLayoutManager(this@Yearly)
                                recyclerView = YearlyEMIRecyclerView(list)
                                binding?.yearlyEmiRv?.adapter = recyclerView
                                recyclerView!!.notifyDataSetChanged()
                            }
                        }
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("urvashi", "$t your response is fail")
                    }
                })
            }
            binding?.endDateBtn?.setOnClickListener {
                DatePickerDialog(this, listener2, stopYear, stopMonth, stopDate).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding?.swipeLl?.setOnRefreshListener {
            binding?.swipeLl?.isRefreshing = false
            yearlyEMIListAPI()
            recyclerView!!.notifyDataSetChanged()
            binding?.messageTxt?.isVisible = false
            binding?.dateLl?.isVisible = false
            binding?.sorryImg?.isVisible = false

            val yearly = "Yearly"
            val loanType = "PL"
            val bundle = intent.extras!!
            @Suppress("DEPRECATION")
            val token = bundle.get("token").toString()
            @Suppress("DEPRECATION")
            val agentId = bundle.get("agentId").toString()
            @Suppress("DEPRECATION")
            val typo = bundle.get("typo").toString()

            if (typo == "3"){
                binding?.searchEt?.isVisible = false
                binding?.dateLl?.isVisible = true
                val json = JsonObject()
                json.addProperty("agentId","$agentId")
                json.addProperty("collectionType","$yearly")
                json.addProperty("loanType","$loanType")

                @Suppress("DEPRECATION")
                val jsonObjectInBody : RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(
                    token,typeAgent,
                    "v1/emi/getAllEmis/agentId/loanType/collectionType",jsonObjectInBody
                )
                call?.enqueue(object : Callback<JsonObject> {

                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            val list = ArrayList<ModalYearly>()
                            binding?.progressBar?.isVisible = false
                            binding?.txtBar?.isVisible = false
                            val res = response.body()

                            val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                            val status = jsonObject.get("status")
                            val message = jsonObject.get("message")
                            val items = jsonObject.get("items")

                            val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                            if(jsonArray.isNull(0)){
                                binding?.messageTxt?.isVisible = true
                                binding?.sorryImg?.isVisible = true
                                binding?.messageTxt?.text = "No EMI's"
                            }
                            if (status == true) {
                                for (i in 0 until jsonArray.length()) {
                                    val emiStatus = jsonArray.getJSONObject(i).getString("status")
                                    val emiId = jsonArray.getJSONObject(i).getString("_id")
                                    val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                                    val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                                    val emiAmount = jsonArray.getJSONObject(i).getString("emiAmount")
                                    val customerId = jsonArray.getJSONObject(i).getString("customerId")
                                    val loanId = jsonArray.getJSONObject(i).getString("loanId")
                                    val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                                    val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                                    val remainingAmount = jsonArray.getJSONObject(i).getString("remainingAmount")
                                    val customerDetail = jsonArray.getJSONObject(i).getString("customerDetail")

                                    val jsonObject2 = JSONTokener(customerDetail.toString()).nextValue() as JSONObject
                                    val name = jsonObject2.getString("name")
                                    val phone = jsonObject2.getString("phone")

                                    list.add(
                                        ModalYearly(
                                            name,
                                            phone,
                                            emiNo,
                                            emiId,
                                            loanAmount,
                                            emiAmount,
                                            remainingAmount,
                                            customerId,
                                            collectionType,
                                            loanId,
                                            agentId,
                                            token,
                                            dateOfCollect,
                                            typo,
                                            emiStatus
                                        )
                                    )
                                }
                                binding?.yearlyEmiRv?.layoutManager =
                                    LinearLayoutManager(this@Yearly)
                                recyclerView = YearlyEMIRecyclerView(list)
                                binding?.yearlyEmiRv?.adapter = recyclerView
                                recyclerView!!.notifyDataSetChanged()
                            }
                        }
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("urvashi", "$t your response is fail")
                    }

                })

                val c : Calendar = Calendar.getInstance()
                var startYear: Int = c.get(Calendar.YEAR)
                var startDate: Int = c.get(Calendar.DATE)
                var startMonth: Int = c.get(Calendar.MONTH)
                var stopYear: Int = c.get(Calendar.YEAR)
                var stopDate: Int = c.get(Calendar.DATE)
                var stopMonth: Int = c.get(Calendar.MONTH)
                var listener: DatePickerDialog.OnDateSetListener? = null
                var listener2: DatePickerDialog.OnDateSetListener? = null
                var initDate: String? = null
                var endDate: String? = null


                listener = DatePickerDialog.OnDateSetListener { datePicker: DatePicker, Year: Int, month: Int, date: Int ->
                    startDate = date
                    startMonth = month
                    startYear = Year
                    var init = "$startYear-${startMonth + 1}-$startDate"

                    initDate = parseDate(init.toString())
                    binding?.startDateBtn?.text = initDate
                }

                binding?.startDateBtn?.setOnClickListener {
                    DatePickerDialog(this, listener, startYear, startMonth, startDate).show()
                }

                listener2 = DatePickerDialog.OnDateSetListener { view: DatePicker, Year: Int, month: Int, date: Int ->
                    stopDate = date
                    stopMonth = month
                    stopYear = Year
                    val end = "$stopYear-${stopMonth + 1}-$startDate"
                    endDate = parseDate(end.toString())

                    binding?.endDateBtn?.text = endDate

                    val json = JsonObject()
                    json.addProperty("agentId","$agentId")
                    json.addProperty("loanType","$loanType")
                    json.addProperty("collectionType","$yearly")
                    json.addProperty("fromDate","$initDate")
                    json.addProperty("toDate","$endDate")

                    @Suppress("DEPRECATION")
                    val jsonObject: RequestBody = RequestBody.create(
                        "application/json".toMediaTypeOrNull(), json.toString())

                    val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                    val call = apiClient?.postTwoHeadersWithTokenData(
                        token,typeAgent,
                        "v1/emi/getEmiList/emiListByDate",jsonObject
                    )
                    call?.enqueue(object : Callback<JsonObject> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            if (response.isSuccessful) {
                                val list = ArrayList<ModalYearly>()
                                binding?.progressBar?.isVisible = false
                                binding?.txtBar?.isVisible = false
                                val res = response.body()

                                val jsonObject1 = JSONTokener(res.toString()).nextValue() as JSONObject
                                val status = jsonObject1.get("status")
                                val message = jsonObject1.get("message")
                                val items = jsonObject1.get("items")

                                val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                                if(jsonArray.isNull(0)){
                                    binding?.messageTxt?.isVisible = true
                                    binding?.messageTxt?.text = "No EMI's"
                                }
                                if (status == true) {
                                    for (i in 0 until jsonArray.length()) {
                                        val emiStatus = jsonArray.getJSONObject(i).getString("status")
                                        val emiId = jsonArray.getJSONObject(i).getString("_id")
                                        val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                                        val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                                        val emiAmount = jsonArray.getJSONObject(i).getString("emiAmount")
                                        val customerId = jsonArray.getJSONObject(i).getString("customerId")
                                        val loanId = jsonArray.getJSONObject(i).getString("loanId")
                                        val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                                        val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                                        val remainingAmount = jsonArray.getJSONObject(i).getString("remainingAmount")
                                        val customerDetail = jsonArray.getJSONObject(i).getString("customerDetail")

                                        val jsonObject2 = JSONTokener(customerDetail.toString()).nextValue() as JSONObject
                                        val name = jsonObject2.getString("name")
                                        val phone = jsonObject2.getString("phone")

                                        list.add(
                                            ModalYearly(
                                                name,
                                                phone,
                                                emiNo,
                                                emiId,
                                                loanAmount,
                                                emiAmount,
                                                remainingAmount,
                                                customerId,
                                                collectionType,
                                                loanId,
                                                agentId,
                                                token,
                                                dateOfCollect,
                                                typo,
                                                emiStatus
                                            )
                                        )

                                    }
                                    binding?.yearlyEmiRv?.layoutManager =
                                        LinearLayoutManager(this@Yearly)
                                    recyclerView = YearlyEMIRecyclerView(list)
                                    binding?.yearlyEmiRv?.adapter = recyclerView
                                    recyclerView!!.notifyDataSetChanged()
                                }
                            }
                        }
                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            Log.e("urvashi", "$t your response is fail")
                        }

                    })
                }
                binding?.endDateBtn?.setOnClickListener {
                    DatePickerDialog(this, listener2, stopYear, stopMonth, stopDate).show()
                }
            }
        }
    }

    private fun parseDate(time: String): String? {
        val inputPattern = "dd-MM-yyyy"
        val outputPattern = "yyyy-MM-dd"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }


    @SuppressLint("SuspiciousIndentation")
    private fun yearlyEMIListAPI(){
        val yearly = "Yearly"
        val loanType = "PL"
        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()
        @Suppress("DEPRECATION")
        val typo = bundle.get("typo").toString()

        @SuppressLint("NotifyDataSetChanged")
        when (typo) {
            "2" -> {
                val json = JsonObject()
                json.addProperty("agentId","$agentId")
                json.addProperty("loanType","$loanType")
                json.addProperty("collectionType","$yearly")

                @Suppress("DEPRECATION")
                val jsonObjectInBody : RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(
                    token,typeAgent,
                    "v1/emi/getOverDueEmi/agentId/loanType/collectionType",jsonObjectInBody
                )
                call?.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            val list = ArrayList<ModalYearly>()
                            binding?.progressBar?.isVisible = false
                            binding?.txtBar?.isVisible = false
                            val res = response.body()

                            val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                            val status = jsonObject.get("status")
                            val message = jsonObject.get("message")
                            val items = jsonObject.get("items")

                            val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                            if(jsonArray.isNull(0)){
                                binding?.messageTxt?.isVisible = true
                                binding?.sorryImg?.isVisible = true
                                binding?.messageTxt?.text = "No EMI's"
                            }
                            if (status == true) {
                                for (i in 0 until jsonArray.length()) {
                                    val emiStatus = jsonArray.getJSONObject(i).getString("status")
                                    val emiId = jsonArray.getJSONObject(i).getString("_id")
                                    val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                                    val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                                    val emiAmount = jsonArray.getJSONObject(i).getString("emiAmount")
                                    val customerId = jsonArray.getJSONObject(i).getString("customerId")
                                    val loanId = jsonArray.getJSONObject(i).getString("loanId")
                                    val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                                    val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                                    val remainingAmount = jsonArray.getJSONObject(i).getString("remainingAmount")
                                    val customerDetail = jsonArray.getJSONObject(i).getString("customerDetails")

                                    val jsonObject2 = JSONTokener(customerDetail.toString()).nextValue() as JSONObject
                                    val name = jsonObject2.getString("name")
                                    val phone = jsonObject2.getString("phone")

                                    list.add(
                                        ModalYearly(
                                            name,
                                            phone,
                                            emiNo,
                                            emiId,
                                            loanAmount,
                                            emiAmount,
                                            remainingAmount,
                                            customerId,
                                            collectionType,
                                            loanId,
                                            agentId,
                                            token,
                                            dateOfCollect,
                                            typo,
                                            emiStatus
                                        )
                                    )
                                }
                                binding?.yearlyEmiRv?.layoutManager =
                                    LinearLayoutManager(this@Yearly)
                                recyclerView = YearlyEMIRecyclerView(list)
                                binding?.yearlyEmiRv?.adapter = recyclerView
                                recyclerView!!.notifyDataSetChanged()
                            }
                        }
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("urvashi", "$t your response is fail")
                    }

                })

            }

            "1" -> {
                val json = JsonObject()
                json.addProperty("agentId","$agentId")
                json.addProperty("status","$typo")
                json.addProperty("loanType","$loanType")
                json.addProperty("collectionType","$yearly")

                @Suppress("DEPRECATION")
                val jsonObjectInBody: RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(
                    token,typeAgent,
                    "v1/emi/getEmis/collectionType/status", jsonObjectInBody
                )
                call?.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            val list = ArrayList<ModalYearly>()
                            binding?.progressBar?.isVisible = false
                            binding?.txtBar?.isVisible = false
                            val res = response.body()

                            val jsonObject1 = JSONTokener(res.toString()).nextValue() as JSONObject
                            val status = jsonObject1.get("status")
                            val message = jsonObject1.get("message")
                            val items = jsonObject1.get("items")

                            val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                            if(jsonArray.isNull(0)){
                                binding?.messageTxt?.isVisible = true
                                binding?.sorryImg?.isVisible = true
                                binding?.messageTxt?.text = "No EMI's"
                            }
                            if (status == true) {
                                for (i in 0 until jsonArray.length()) {
                                    val emiStatus = jsonArray.getJSONObject(i).getString("status")
                                    val emiId = jsonArray.getJSONObject(i).getString("_id")
                                    val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                                    val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                                    val emiAmount = jsonArray.getJSONObject(i).getString("paidEmiAmount")
                                    val customerId = jsonArray.getJSONObject(i).getString("customerId")
                                    val loanId = jsonArray.getJSONObject(i).getString("loanId")
                                    val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                                    val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                                    val remainingAmount = jsonArray.getJSONObject(i).getString("remainingAmount")
                                    val customerDetail = jsonArray.getJSONObject(i).getString("customerDetail")

                                    val jsonObject2 = JSONTokener(customerDetail.toString()).nextValue() as JSONObject
                                    val name = jsonObject2.getString("name")
                                    val phone = jsonObject2.getString("phone")

                                    list.add(
                                        ModalYearly(
                                            name,
                                            phone,
                                            emiNo,
                                            emiId,
                                            loanAmount,
                                            emiAmount,
                                            remainingAmount,
                                            customerId,
                                            collectionType,
                                            loanId,
                                            agentId,
                                            token,
                                            dateOfCollect,
                                            typo,
                                            emiStatus
                                        )
                                    )

                                }
                                binding?.yearlyEmiRv?.layoutManager =
                                    LinearLayoutManager(this@Yearly)
                                recyclerView = YearlyEMIRecyclerView(list)
                                binding?.yearlyEmiRv?.adapter = recyclerView
                                recyclerView!!.notifyDataSetChanged()
                            }
                        }
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("urvashi", "$t your response is fail")
                    }
                })
            }

            "0" -> {
                val json = JsonObject()
                json.addProperty("agentId","$agentId")
                json.addProperty("status","$typo")
                json.addProperty("loanType","$loanType")
                json.addProperty("collectionType","$yearly")

                @Suppress("DEPRECATION")
                val jsonObjectInBody: RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(
                    token,typeAgent,
                    "v1/emi/getEmis/collectionType/status", jsonObjectInBody
                )
                call?.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            val list = ArrayList<ModalYearly>()
                            binding?.progressBar?.isVisible = false
                            binding?.txtBar?.isVisible = false
                            val res = response.body()

                            val jsonObject1 = JSONTokener(res.toString()).nextValue() as JSONObject
                            val status = jsonObject1.get("status")
                            val message = jsonObject1.get("message")
                            val items = jsonObject1.get("items")

                            val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                            if(jsonArray.isNull(0)){
                                binding?.messageTxt?.isVisible = true
                                binding?.sorryImg?.isVisible = true
                                binding?.messageTxt?.text = "No EMI's"
                            }
                            if (status == true) {
                                for (i in 0 until jsonArray.length()) {
                                    val emiStatus = jsonArray.getJSONObject(i).getString("status")
                                    val emiId = jsonArray.getJSONObject(i).getString("_id")
                                    val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                                    val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                                    val emiAmount = jsonArray.getJSONObject(i).getString("emiAmount")
                                    val customerId = jsonArray.getJSONObject(i).getString("customerId")
                                    val loanId = jsonArray.getJSONObject(i).getString("loanId")
                                    val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                                    val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                                    val remainingAmount = jsonArray.getJSONObject(i).getString("remainingAmount")
                                    val customerDetail = jsonArray.getJSONObject(i).getString("customerDetail")

                                    val jsonObject2 = JSONTokener(customerDetail.toString()).nextValue() as JSONObject
                                    val name = jsonObject2.getString("name")
                                    val phone = jsonObject2.getString("phone")

                                    list.add(
                                        ModalYearly(
                                            name,
                                            phone,
                                            emiNo,
                                            emiId,
                                            loanAmount,
                                            emiAmount,
                                            remainingAmount,
                                            customerId,
                                            collectionType,
                                            loanId,
                                            agentId,
                                            token,
                                            dateOfCollect,
                                            typo,
                                            emiStatus
                                        )
                                    )

                                }
                                binding?.yearlyEmiRv?.layoutManager =
                                    LinearLayoutManager(this@Yearly)
                                recyclerView = YearlyEMIRecyclerView(list)
                                binding?.yearlyEmiRv?.adapter = recyclerView
                                recyclerView!!.notifyDataSetChanged()
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

    private fun forSearch() {

        val yearly = "Yearly"
        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()
        @Suppress("DEPRECATION")
        val typo = bundle.get("typo").toString()

        @SuppressLint("NotifyDataSetChanged")

        if(binding?.searchEt?.text?.length == 10){

            when(typo){
                "2" ->{
                    val phone = binding?.searchEt?.text

                    val json = JsonObject()
                    json.addProperty("agentId","$agentId")
                    json.addProperty("collectionType","$yearly")
                    json.addProperty("phone","$phone")

                    @Suppress("DEPRECATION")
                    val jsonObjectInBody : RequestBody = RequestBody.create(
                        "application/json".toMediaTypeOrNull(), json.toString())

                    val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                    val call = apiClient?.postTwoHeadersWithTokenData(
                        token,typeAgent,
                        "v1/emi/getOverDueEmi/agentId/phone/collectionType",jsonObjectInBody
                    )
                    call?.enqueue(object : Callback<JsonObject> {
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            if (response.isSuccessful) {
                                val list = ArrayList<ModalYearly>()
                                binding?.progressBar?.isVisible = false
                                binding?.txtBar?.isVisible = false
                                val res = response.body()

                                val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                                val status = jsonObject.get("status")
                                val message = jsonObject.get("message")
                                val items = jsonObject.get("items")

                                val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                                if(jsonArray.isNull(0)){
                                    binding?.messageTxt?.isVisible = true
                                    binding?.messageTxt?.text = "No EMI's"
                                    binding?.sorryImg?.isVisible = true
                                    binding?.yearlyEmiRv?.isVisible = false
                                }
                                if (status == true) {
                                    for (i in 0 until jsonArray.length()) {
                                        val emiStatus = jsonArray.getJSONObject(i).getString("status")
                                        val emiId = jsonArray.getJSONObject(i).getString("_id")
                                        val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                                        val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                                        val emiAmount = jsonArray.getJSONObject(i).getString("emiAmount")
                                        val customerId = jsonArray.getJSONObject(i).getString("customerId")
                                        val loanId = jsonArray.getJSONObject(i).getString("loanId")
                                        val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                                        val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                                        val remainingAmount = jsonArray.getJSONObject(i).getString("remainingAmount")
                                        val customerDetail = jsonArray.getJSONObject(i).getString("customerDetail")

                                        val jsonObject2 = JSONTokener(customerDetail.toString()).nextValue() as JSONObject
                                        val name = jsonObject2.getString("name")
                                        val phone = jsonObject2.getString("phone")


                                        list.add(
                                            ModalYearly(
                                                name,
                                                phone,
                                                emiNo,
                                                emiId,
                                                loanAmount,
                                                emiAmount,
                                                remainingAmount,
                                                customerId,
                                                collectionType,
                                                loanId,
                                                agentId,
                                                token,
                                                dateOfCollect,
                                                typo,
                                                emiStatus
                                            )
                                        )

                                    }
                                    binding?.yearlyEmiRv?.layoutManager =
                                        LinearLayoutManager(this@Yearly)
                                    recyclerView = YearlyEMIRecyclerView(list)
                                    binding?.yearlyEmiRv?.adapter = recyclerView
                                    recyclerView!!.notifyDataSetChanged()
                                }
                            }
                        }
                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            Log.e("urvashi", "$t your response is fail")
                        }
                    })
                }
                "1" -> {
                    val phone = binding?.searchEt?.text

                    val json = JsonObject()
                    json.addProperty("agentId","$agentId")
                    json.addProperty("collectionType","$yearly")
                    json.addProperty("status","$typo")
                    json.addProperty("phone","$phone")

                    @Suppress("DEPRECATION")
                    val jsonObjectInBody : RequestBody = RequestBody.create(
                        "application/json".toMediaTypeOrNull(), json.toString())

                    val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                    val call = apiClient?.postTwoHeadersWithTokenData(
                        token,typeAgent,
                        "v1/emi/getEmi/agentId/collectionType/phone/status",jsonObjectInBody
                    )
                    call?.enqueue(object : Callback<JsonObject> {
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            if (response.isSuccessful) {
                                val list = ArrayList<ModalYearly>()
                                binding?.progressBar?.isVisible = false
                                binding?.txtBar?.isVisible = false
                                val res = response.body()

                                val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                                val status = jsonObject.get("status")
                                val message = jsonObject.get("message")
                                val items = jsonObject.get("items")

                                val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                                if(jsonArray.isNull(0)){
                                    binding?.messageTxt?.isVisible = true
                                    binding?.messageTxt?.text = "No EMI's"
                                    binding?.sorryImg?.isVisible = true
                                    binding?.yearlyEmiRv?.isVisible = false
                                }
                                if (status == true) {
                                    for (i in 0 until jsonArray.length()) {
                                        val emiStatus = jsonArray.getJSONObject(i).getString("status")
                                        val emiId = jsonArray.getJSONObject(i).getString("_id")
                                        val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                                        val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                                        val emiAmount = jsonArray.getJSONObject(i).getString("paidEmiAmount")
                                        val customerId = jsonArray.getJSONObject(i).getString("customerId")
                                        val loanId = jsonArray.getJSONObject(i).getString("loanId")
                                        val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                                        val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                                        val remainingAmount = jsonArray.getJSONObject(i).getString("remainingAmount")
                                        val customerDetail = jsonArray.getJSONObject(i).getString("customerDetail")

                                        val jsonObject2 = JSONTokener(customerDetail.toString()).nextValue() as JSONObject
                                        val name = jsonObject2.getString("name")
                                        val phone = jsonObject2.getString("phone")

                                        list.add(
                                            ModalYearly(
                                                name,
                                                phone,
                                                emiNo,
                                                emiId,
                                                loanAmount,
                                                emiAmount,
                                                remainingAmount,
                                                customerId,
                                                collectionType,
                                                loanId,
                                                agentId,
                                                token,
                                                dateOfCollect,
                                                typo,
                                                emiStatus
                                            )
                                        )
                                    }
                                    binding?.yearlyEmiRv?.layoutManager =
                                        LinearLayoutManager(this@Yearly)
                                    recyclerView = YearlyEMIRecyclerView(list)
                                    binding?.yearlyEmiRv?.adapter = recyclerView
                                    recyclerView!!.notifyDataSetChanged()
                                }
                            }
                        }
                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            Log.e("urvashi", "$t your response is fail")
                        }
                    })
                }
                "0" ->{
                    val phone  = binding?.searchEt?.text

                    val json = JsonObject()
                    json.addProperty("agentId","$agentId")
                    json.addProperty("collectionType","$yearly")
                    json.addProperty("status","$typo")
                    json.addProperty("phone","$phone")

                    @Suppress("DEPRECATION")
                    val jsonObjectInBody : RequestBody = RequestBody.create(
                        "application/json".toMediaTypeOrNull(), json.toString())

                    val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                    val call = apiClient?.postTwoHeadersWithTokenData(
                        token,typeAgent,
                        "v1/emi/getEmi/agentId/collectionType/phone/status",jsonObjectInBody
                    )
                    call?.enqueue(object : Callback<JsonObject> {
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            if (response.isSuccessful) {
                                val list = ArrayList<ModalYearly>()
                                binding?.progressBar?.isVisible = false
                                binding?.txtBar?.isVisible = false
                                val res = response.body()

                                val jsonObject2 = JSONTokener(res.toString()).nextValue() as JSONObject
                                val status = jsonObject2.get("status")
                                val message = jsonObject2.get("message")
                                val items = jsonObject2.get("items")

                                val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                                if(jsonArray.isNull(0)){
                                    binding?.messageTxt?.isVisible = true
                                    binding?.messageTxt?.text = "No EMI's"
                                    binding?.yearlyEmiRv?.isVisible = false
                                    binding?.sorryImg?.isVisible = true
                                }
                                if (status == true) {
                                    for (i in 0 until jsonArray.length()) {
                                        val emiStatus = jsonArray.getJSONObject(i).getString("status")
                                        val emiId = jsonArray.getJSONObject(i).getString("_id")
                                        val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                                        val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                                        val emiAmount = jsonArray.getJSONObject(i).getString("emiAmount")
                                        val customerId = jsonArray.getJSONObject(i).getString("customerId")
                                        val loanId = jsonArray.getJSONObject(i).getString("loanId")
                                        val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                                        val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                                        val remainingAmount = jsonArray.getJSONObject(i).getString("remainingAmount")
                                        val customerDetail = jsonArray.getJSONObject(i).getString("customerDetail")

                                        val jsonObject3 = JSONTokener(customerDetail.toString()).nextValue() as JSONObject
                                        val name = jsonObject3.getString("name")
                                        val phone = jsonObject3.getString("phone")

                                        list.add(
                                            ModalYearly(
                                                name,
                                                phone,
                                                emiNo,
                                                emiId,
                                                loanAmount,
                                                emiAmount,
                                                remainingAmount,
                                                customerId,
                                                collectionType,
                                                loanId,
                                                agentId,
                                                token,
                                                dateOfCollect,
                                                typo,
                                                emiStatus
                                            )
                                        )
                                    }
                                    binding?.yearlyEmiRv?.layoutManager =
                                        LinearLayoutManager(this@Yearly)
                                    recyclerView = YearlyEMIRecyclerView(list)
                                    binding?.yearlyEmiRv?.adapter = recyclerView
                                }
                            }
                        }
                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            Log.e("urvashi", "$t your response is fail")
                        }
                    })
                }
            }
        }else{
            binding?.progressBar?.isVisible = false
            binding?.txtBar?.isVisible = false
            binding?.messageTxt?.isVisible = true
            binding?.messageTxt?.text = "'Please Enter 10 Digit Number'"
            binding?.yearlyEmiRv?.isVisible = false
            binding?.sorryImg?.isVisible = true
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