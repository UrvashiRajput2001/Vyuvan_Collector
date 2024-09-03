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
import com.vyuvancollectors.PersonalLoan.Adapter.MonthlyEMIRecyclerView
import com.vyuvancollectors.PersonalLoan.Data_Class.ModalMonthly
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityMonthlyBinding
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

class  Monthly : AppCompatActivity() {

    private  var binding : ActivityMonthlyBinding? = null
    private var recyclerView : MonthlyEMIRecyclerView? = null

    val typeAgent = "Agent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMonthlyBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        if (isConnected()) {
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
        binding?.ll21?.isVisible = false

        monthlyEMIListAPI()

        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.searchEt?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                forSearch()
            }
            true
        }

        val months = "Monthly"
        val loanType = "PL"
        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()
        @Suppress("DEPRECATION")
        val typo = bundle.get("typo").toString()



        if(typo == "3"){
            binding?.dateLl?.isVisible = true
            binding?.searchEt?.isVisible = false
            val json = JsonObject()
            json.addProperty("agentId","$agentId")
            json.addProperty("collectionType","$months")
            json.addProperty("loanType","$loanType")

            @Suppress("DEPRECATION")
            val jsonObjectInBody : RequestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(), json.toString())

            val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
            val call = apiClient?.postTwoHeadersWithTokenData(token,typeAgent,"v1/emi/getAllEmis/agentId/loanType/collectionType",jsonObjectInBody
            )
            call?.enqueue(object : Callback<JsonObject> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val list = ArrayList<ModalMonthly>()
                        binding?.progressBar?.isVisible = false
                        binding?.txtBar?.isVisible = false
                        binding?.ll21?.isVisible = true
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
                            binding?.monthlyEmiRv?.isVisible = false
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

//                                Log.e("monthly","$emiStatus emi status")

                                val jsonObject2 = JSONTokener(customerDetail.toString()).nextValue() as JSONObject
                                val name = jsonObject2.getString("name")

                                Log.e("Name LILI","$name")
                                val phone = jsonObject2.getString("phone")

                                list.add(
                                    ModalMonthly(
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
                        }
                        binding?.monthlyEmiRv?.layoutManager =
                            LinearLayoutManager(this@Monthly)
                        recyclerView = MonthlyEMIRecyclerView(list)
                        binding?.monthlyEmiRv?.adapter = recyclerView
                        recyclerView!!.notifyDataSetChanged()
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("urvashi", "$t your response is fail")
                }
            })

            val c: Calendar = Calendar.getInstance()
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

            listener2 = DatePickerDialog.OnDateSetListener { view: DatePicker, Year: Int, month: Int, date: Int ->
                    stopDate = date
                    stopMonth = month
                    stopYear = Year
                    val end = "$stopDate-${stopMonth + 1}-$stopYear"
                    endDate = parseDate(end.toString())

                    binding?.endDateBtn?.text = endDate


                val json = JsonObject()
                json.addProperty("agentId","$agentId")
                json.addProperty("loanType","$loanType")
                json.addProperty("collectionType","$months")
                json.addProperty("fromDate","$initDate")
                json.addProperty("toDate","$endDate")

                @Suppress("DEPRECATION")
                val jsonObjectInBody: RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(
                    token,typeAgent,
                    "v1/emi/getEmiList/emiListByDate",jsonObjectInBody
                )
                call?.enqueue(object : Callback<JsonObject> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            val list = ArrayList<ModalMonthly>()
                            binding?.progressBar?.isVisible = false
                            binding?.txtBar?.isVisible = false
                            binding?.ll21?.isVisible = true
                            binding?.messageTxt?.isVisible = false
                            binding?.sorryImg?.isVisible = false
                            val res = response.body()

                            Log.e("Kakuda","$res")

                            val jsonObject1 = JSONTokener(res.toString()).nextValue() as JSONObject
                            val status = jsonObject1.get("status")
                            val message = jsonObject1.get("message")
                            val items = jsonObject1.get("items")

                            val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                            if(jsonArray.isNull(0)){
                                binding?.messageTxt?.isVisible = true
                                binding?.messageTxt?.text = "No EMI's"
                                binding?.monthlyEmiRv?.isVisible = false
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
                                        ModalMonthly(
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
                            }
                            binding?.monthlyEmiRv?.layoutManager =
                                LinearLayoutManager(this@Monthly)
                            recyclerView = MonthlyEMIRecyclerView(list)
                            binding?.monthlyEmiRv?.adapter = recyclerView
                            recyclerView!!.notifyDataSetChanged()
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
            monthlyEMIListAPI()
            binding?.messageTxt?.isVisible = false
            recyclerView!!.notifyDataSetChanged()
            binding?.dateLl?.isVisible = false
            binding?.sorryImg?.isVisible = false

            val months = "Monthly"
            val loanType = "PL"
            val bundle = intent.extras!!
            @Suppress("DEPRECATION")
            val token = bundle.get("token").toString()
            @Suppress("DEPRECATION")
            val agentId = bundle.get("agentId").toString()
            @Suppress("DEPRECATION")
            val typo = bundle.get("typo").toString()

            if(typo == "3"){
                binding?.searchEt?.isVisible = false
                binding?.dateLl?.isVisible = true
                val json = JsonObject()
                json.addProperty("agentId","$agentId")
                json.addProperty("collectionType","$months")
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
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            val list = ArrayList<ModalMonthly>()
                            binding?.progressBar?.isVisible = false
                            binding?.txtBar?.isVisible = false
                            binding?.ll21?.isVisible = true
                            binding?.messageTxt?.isVisible = false
                            binding?.sorryImg?.isVisible = false
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
                                binding?.monthlyEmiRv?.isVisible = false
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
                                        ModalMonthly(
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
                            }
                            binding?.monthlyEmiRv?.layoutManager =
                                LinearLayoutManager(this@Monthly)
                            recyclerView = MonthlyEMIRecyclerView(list)
                            binding?.monthlyEmiRv?.adapter = recyclerView
                            recyclerView!!.notifyDataSetChanged()
                        }
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("urvashi", "$t your response is fail")
                    }
                })

                val c: Calendar = Calendar.getInstance()
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
                        stopDate = date
                        stopMonth = month
                        stopYear = Year
                        val end = "$stopDate-${stopMonth + 1}-$stopYear"
                        endDate = parseDate(end.toString())

                        binding?.endDateBtn?.text = endDate

                        val json = JsonObject()
                        json.addProperty("agentId","$agentId")
                        json.addProperty("loanType","$loanType")
                        json.addProperty("collectionType","$months")
                        json.addProperty("fromDate","$initDate")
                        json.addProperty("toDate","$endDate")

                        @Suppress("DEPRECATION")
                        val jsonObjectInBody: RequestBody = RequestBody.create(
                            "application/json".toMediaTypeOrNull(), json.toString())

                        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                        val call = apiClient?.postTwoHeadersWithTokenData(
                            token,typeAgent,
                            "v1/emi/getEmiList/emiListByDate",jsonObjectInBody
                        )
                        call?.enqueue(object : Callback<JsonObject> {
                            @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
                            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                if (response.isSuccessful) {
                                    val list = ArrayList<ModalMonthly>()
                                    binding?.progressBar?.isVisible = false
                                    binding?.txtBar?.isVisible = false
                                    binding?.ll21?.isVisible = true
                                    binding?.messageTxt?.isVisible = false
                                    binding?.sorryImg?.isVisible = false
                                    val res = response.body()

                                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                                    val status = jsonObject.get("status")
                                    val message = jsonObject.get("message")
                                    val items = jsonObject.get("items")

                                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                                    if(jsonArray.isNull(0)){
                                        binding?.messageTxt?.isVisible = true
                                        binding?.messageTxt?.text = "No EMI's"
                                        binding?.monthlyEmiRv?.isVisible = false
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
                                                ModalMonthly(
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
                                    }
                                    binding?.monthlyEmiRv?.layoutManager =
                                        LinearLayoutManager(this@Monthly)
                                    recyclerView = MonthlyEMIRecyclerView(list)
                                    binding?.monthlyEmiRv?.adapter = recyclerView
                                    recyclerView!!.notifyDataSetChanged()
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
    private fun monthlyEMIListAPI(){
        val month = "Monthly"
        val loanType = "PL"
        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()
        @Suppress("DEPRECATION")
        val typo = bundle.get("typo").toString()

        when (typo) {
            "2" -> {
                val json = JsonObject()
                json.addProperty("agentId","$agentId")
                json.addProperty("loanType","$loanType")
                json.addProperty("collectionType","$month")

                @Suppress("DEPRECATION")
                val jsonObjectInBody : RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(
                    token,typeAgent,
                    "v1/emi/getOverDueEmi/agentId/loanType/collectionType",jsonObjectInBody
                )
                call?.enqueue(object : Callback<JsonObject> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            val list = ArrayList<ModalMonthly>()
                            binding?.progressBar?.isVisible = false
                            binding?.txtBar?.isVisible = false
                            binding?.ll21?.isVisible = true
                            binding?.messageTxt?.isVisible = false
                            binding?.sorryImg?.isVisible = false
                            val res = response.body()
                            Log.e("LILI OverDue","$res")
                            val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                            val status = jsonObject.get("status")
                            val message = jsonObject.get("message")
                            val items = jsonObject.get("items")

                            val jsonArrayMain = JSONTokener(items.toString()).nextValue() as JSONArray
                            if(jsonArrayMain.isNull(0)){
                                binding?.messageTxt?.isVisible = true
                                binding?.messageTxt?.text = "No EMI's"
                                binding?.sorryImg?.isVisible = true
                            }
                            if (status == true) {
                                for (i in 0 until jsonArrayMain.length()) {
                                    val emiStatus = jsonArrayMain.getJSONObject(i).getString("status")
                                    val emiId = jsonArrayMain.getJSONObject(i).getString("_id")
                                    val emiNo = jsonArrayMain.getJSONObject(i).getString("emiNumber")
                                    val loanAmount = jsonArrayMain.getJSONObject(i).getString("loanAmount")
                                    val emiAmount = jsonArrayMain.getJSONObject(i).getString("emiAmount")
                                    val dateOfCollect = jsonArrayMain.getJSONObject(i).getString("dateOfCollect")
                                    val remainingAmount = jsonArrayMain.getJSONObject(i).getString("remainingAmount")
                                    val loanDetails = jsonArrayMain.getJSONObject(i).getString("loanDetails")
                                    val jsonObject2 = JSONTokener(loanDetails.toString()).nextValue() as JSONArray

                                    for (j in 0 until jsonObject2.length()) {
                                        val loanId = jsonObject2.getJSONObject(j).getString("_id")
                                        val customerId = jsonObject2.getJSONObject(j).getString("customerId")
                                        val collectionType = jsonObject2.getJSONObject(j).getString("collectionType")

                                        val customerDetail = jsonArrayMain.getJSONObject(i).getString("customerDetails")
                                        val jsonObject3 = JSONTokener(customerDetail.toString()).nextValue() as JSONArray

                                        for (k in 0 until jsonObject3.length()) {
                                            val name = jsonObject3.getJSONObject(k).getString("name")

                                            Log.e("LLii", name)
                                            val phone = jsonObject3.getJSONObject(k).getString("phone")

                                            list.add(
                                                ModalMonthly(
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
                                }

                                }
                                binding?.monthlyEmiRv?.layoutManager =
                                    LinearLayoutManager(this@Monthly)
                                recyclerView = MonthlyEMIRecyclerView(list)
                                binding?.monthlyEmiRv?.adapter = recyclerView
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
                json.addProperty("collectionType","$month")

                @Suppress("DEPRECATION")
                val jsonObjectInBody : RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())


                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(
                    token,typeAgent,
                    "v1/emi/getEmi/paidEmiByAgent", jsonObjectInBody
                )
                call?.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            val list = ArrayList<ModalMonthly>()
                            binding?.progressBar?.isVisible = false
                            binding?.txtBar?.isVisible = false
                            binding?.ll21?.isVisible = true
                            binding?.messageTxt?.isVisible = false
                            binding?.sorryImg?.isVisible = false
                            val res = response.body()

                            val jsonObject1 = JSONTokener(res.toString()).nextValue() as JSONObject
                            val status = jsonObject1.get("status")
                            val message = jsonObject1.get("message")
                            val items = jsonObject1.get("items")

                            Log.e("LILI Paid","$res")

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

                                    val jsonObject2 = JSONTokener(customerDetail.toString()).nextValue() as JSONArray
                                    for(j in 0 until jsonObject2.length()) {
                                        val name = jsonObject2.getJSONObject(j).getString("name")
                                        val phone = jsonObject2.getJSONObject(j).getString("phone")


                                        list.add(
                                            ModalMonthly(
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

                                }
                                binding?.monthlyEmiRv?.layoutManager =
                                    LinearLayoutManager(this@Monthly)
                                recyclerView = MonthlyEMIRecyclerView(list)
                                binding?.monthlyEmiRv?.adapter = recyclerView
                                recyclerView!!.notifyDataSetChanged()
                            }
                        }

                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                        Toast.makeText(this@Monthly, t.toString(), Toast.LENGTH_LONG).show()
                        Log.e("urvashi", "$t your response is fail")
                    }

                })

            }
            "0" -> {
                val json = JsonObject()
                json.addProperty("agentId","$agentId")
                json.addProperty("status","$typo")
                json.addProperty("loanType","$loanType")
                json.addProperty("collectionType","$month")

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
                            val list = ArrayList<ModalMonthly>()
                            binding?.progressBar?.isVisible = false
                            binding?.txtBar?.isVisible = false
                            binding?.ll21?.isVisible = true
                            binding?.messageTxt?.isVisible = false
                            binding?.sorryImg?.isVisible = false
                            val res = response.body()
                            Log.e("LILI Pending","$res")
                            val jsonObject1 = JSONTokener(res.toString()).nextValue() as JSONObject
                            val status = jsonObject1.get("status")
                            val message = jsonObject1.get("message")
                            val items = jsonObject1.get("items")

                            val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                            if(jsonArray.isNull(0)){
                                binding?.messageTxt?.isVisible = true
                                binding?.messageTxt?.text = "No EMI's"
                                binding?.sorryImg?.isVisible = true
                                binding?.monthlyEmiRv?.isVisible = false
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
                                        ModalMonthly(
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
                                binding?.monthlyEmiRv?.layoutManager =
                                    LinearLayoutManager(this@Monthly)
                                recyclerView = MonthlyEMIRecyclerView(list)
                                binding?.monthlyEmiRv?.adapter = recyclerView
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
        val months = "Monthly"
        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()
        @Suppress("DEPRECATION")
        val typo = bundle.get("typo").toString()

        if(binding?.searchEt?.text?.length == 10){

            when(typo){
                "2" -> {

                    val phone = binding?.searchEt?.text

                    val json = JsonObject()
                    json.addProperty("agentId","$agentId")
                    json.addProperty("collectionType","$months")
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
                        @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            if (response.isSuccessful) {
                                val list = ArrayList<ModalMonthly>()
                                binding?.progressBar?.isVisible = false
                                binding?.txtBar?.isVisible = false
                                binding?.ll21?.isVisible = true
                                binding?.messageTxt?.isVisible = false
                                binding?.sorryImg?.isVisible = false
                                val res = response.body()
                                Log.e("monthly","$res")

                                val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                                val status = jsonObject.get("status")
                                val message = jsonObject.get("message")
                                val items = jsonObject.get("items")

                                val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                                if(jsonArray.isNull(0)){
                                    binding?.messageTxt?.isVisible = true
                                    binding?.messageTxt?.text = "No EMI's"
                                    binding?.monthlyEmiRv?.isVisible = false
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
                                            ModalMonthly(
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
                                    binding?.monthlyEmiRv?.layoutManager =
                                        LinearLayoutManager(this@Monthly)
                                    recyclerView = MonthlyEMIRecyclerView(list)
                                    binding?.monthlyEmiRv?.adapter = recyclerView
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
                    json.addProperty("collectionType","$months")
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
                        @SuppressLint("SuspiciousIndentation")
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            if (response.isSuccessful) {
                                val list = ArrayList<ModalMonthly>()
                                binding?.progressBar?.isVisible = false
                                binding?.txtBar?.isVisible = false
                                binding?.ll21?.isVisible = true
                                binding?.messageTxt?.isVisible = false
                                binding?.sorryImg?.isVisible = false
                                val res = response.body()

                                Log.e("monthly","$res")

                                val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                                val status = jsonObject.get("status")
                                val message = jsonObject.get("message")
                                val items = jsonObject.get("items")

                                val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray

                                if(jsonArray.isNull(0)){
                                    binding?.messageTxt?.isVisible = true
                                    binding?.messageTxt?.text = "No EMI's"
                                    binding?.monthlyEmiRv?.isVisible = false
                                    binding?.sorryImg?.isVisible = true
                                }
                                if (status == true) {
                                    for (i in 0 until jsonArray.length()) {
                                        val emiStatus = jsonArray.getJSONObject(i).getString("status")
                                        val emiId = jsonArray.getJSONObject(i).getString("_id")
                                        val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                                        val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                                        val paidEmiAmount = jsonArray.getJSONObject(i).getString("paidEmiAmount")
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
                                            ModalMonthly(
                                                name,
                                                phone,
                                                emiNo,
                                                emiId,
                                                loanAmount,
                                                paidEmiAmount,
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
                                    binding?.monthlyEmiRv?.layoutManager =
                                        LinearLayoutManager(this@Monthly)
                                    recyclerView = MonthlyEMIRecyclerView(list)
                                    binding?.monthlyEmiRv?.adapter = recyclerView
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

                    val phone = binding?.searchEt?.text

                    val json = JsonObject()
                    json.addProperty("agentId","$agentId")
                    json.addProperty("collectionType","$months")
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
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            if (response.isSuccessful) {
                                val list = ArrayList<ModalMonthly>()
                                binding?.progressBar?.isVisible = false
                                binding?.txtBar?.isVisible = false
                                binding?.ll21?.isVisible = true
                                binding?.messageTxt?.isVisible = false
                                binding?.sorryImg?.isVisible = false
                                val res = response.body()
                                Log.e("monthly","$res")
                                val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                                val status = jsonObject.get("status")
                                val message = jsonObject.get("message")
                                val items = jsonObject.get("items")
                                val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray

                                if(jsonArray.isNull(0)){
                                    binding?.messageTxt?.isVisible = true
                                    binding?.messageTxt?.text = "No EMI's"
                                    binding?.monthlyEmiRv?.isVisible = false
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
                                            ModalMonthly(
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
                                    binding?.monthlyEmiRv?.layoutManager =
                                        LinearLayoutManager(this@Monthly)
                                    recyclerView = MonthlyEMIRecyclerView(list)
                                    binding?.monthlyEmiRv?.adapter = recyclerView
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
        }else{
            binding?.progressBar?.isVisible = false
            binding?.txtBar?.isVisible = false
            binding?.messageTxt?.isVisible = true
            binding?.messageTxt?.text = "Please Enter 10 Digit Number"
            binding?.sorryImg?.isVisible = false
            binding?.monthlyEmiRv?.isVisible = false
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