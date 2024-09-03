package com.vyuvancollectors.GroupLoan.GroupTypesOfEmi

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.vyuvancollectors.GroupLoan.AdapterInGroup.AllGroupRv
import com.vyuvancollectors.GroupLoan.Group_Data_Class.AllGroupDetailsData
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityAllInGroupBinding
import com.google.gson.JsonObject
import com.vyuvancollectors.GroupLoan.AdapterInGroup.MonthlyGroupRv
import com.vyuvancollectors.GroupLoan.Group_Data_Class.MonthlyGroupDetailsData
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
import kotlin.collections.ArrayList

class AllInGroup : AppCompatActivity() {

    private var binding : ActivityAllInGroupBinding? = null

    private var recyclerView : AllGroupRv? = null

    val typeAgent = "Agent"

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAllInGroupBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        binding?.progressBar?.isVisible = true
        binding?.txtBar?.isVisible = true
        binding?.swipeLl?.isRefreshing = false
        binding?.messageTxt?.isVisible = false
        binding?.swipeLl?.isRefreshing = false
        binding?.sorryImg?.isVisible = false

        forMonthlyApi()


        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }

        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
//            binding?.messageTxt?.isVisible = true
            binding?.progressBar?.isVisible = false
            binding?.txtBar?.isVisible = false
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }



        val loanType = "GL"
        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()

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

        listener2 = DatePickerDialog.OnDateSetListener { view: DatePicker, Year: Int, month: Int, date: Int ->
            stopDate = date
            stopMonth = month
            stopYear = Year
            val end = "$stopDate-${stopMonth + 1}-$stopYear"
            endDate = parseDate(end.toString())

            binding?.endDateBtn?.text = endDate

            val json = JsonObject()
            json.addProperty("agentId", "$agentId")
            json.addProperty("loanType", "$loanType")
            json.addProperty("fromDate", "$initDate")
            json.addProperty("toDate", "$endDate")

            @Suppress("DEPRECATION")
            val jsonObjectRequestBody: RequestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(), json.toString()
            )

            val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
            val call = apiClient?.postTwoHeadersWithTokenData(
                token,typeAgent,
                "v1/emi/getEmiList/loanType/getAllEmiByDate",
                jsonObjectRequestBody
            )
            call?.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val list = ArrayList<AllGroupDetailsData>()
                        binding?.allEmiRv?.isVisible = false


                        val res = response.body()
                        Log.e("date","$res ")

                        val jsonObjectMain = JSONTokener(res.toString()).nextValue() as JSONObject
                        val status = jsonObjectMain.get("status")
                        val message = jsonObjectMain.get("message")
                        val items = jsonObjectMain.get("items")

                        val jsonArrayMain = JSONTokener(items.toString()).nextValue() as JSONArray

                        if (jsonArrayMain.isNull(0) ) {
                            binding?.allEmiRv?.isVisible = false
                            binding?.messageTxt?.isVisible = true
                            binding?.messageTxt?.text = "No EMI's"
                            binding?.sorryImg?.isVisible = true
                        }
                        if (status == true) {
                            binding?.progressBar?.isVisible = false
                            binding?.txtBar?.isVisible = false
                            binding?.allEmiRv?.isVisible = true
                            for (i in 0 until jsonArrayMain.length()) {
                                val groupDetail =
                                    jsonArrayMain.getJSONObject(i).getString("groupDetails")
                                val jsonObject =
                                    JSONTokener(groupDetail.toString()).nextValue() as JSONObject
                                val teamLeadName = jsonObject.getString("teamLeadName")
                                val totalGroupMember = jsonObject.getString("totalGroupMember")

                                val leaderDetail =
                                    jsonArrayMain.getJSONObject(i).getString("leaderDetails")
                                val jsonObject2 =
                                    JSONTokener(leaderDetail.toString()).nextValue() as JSONObject
                                val groupName = jsonObject2.getString("groupName")
                                val groupLeaderMobile = jsonObject2.getString("groupLeaderMobile")
                                val groupLeaderName = jsonObject2.getString("groupLeaderName")

                                val groupLoanDetail =
                                    jsonArrayMain.getJSONObject(i).getString("groupLoanDetails")
                                val jsonObject3 =
                                    JSONTokener(groupLoanDetail.toString()).nextValue() as JSONObject
                                val groupId = jsonObject3.getString("groupId")
                                val loanAmount = jsonObject3.getString("loanAmount")
                                val interest = jsonObject3.getString("interest")
                                val collectionType = jsonObject3.getString("collectionType")
                                val disburseDate = jsonObject3.getString("disburseDate")


                                list.add(
                                    AllGroupDetailsData(
                                        agentId,
                                        token,
                                        groupId,
                                        loanAmount,
                                        interest,
                                        teamLeadName,
                                        groupLeaderName,
                                        collectionType,
                                        groupName,
                                        totalGroupMember,
                                        groupLeaderMobile,
                                        disburseDate
                                    )
                                )
                            }
                            binding?.allEmiRv?.layoutManager = LinearLayoutManager(this@AllInGroup)
                            recyclerView = AllGroupRv(list)
                            binding?.allEmiRv?.adapter = recyclerView
                            recyclerView!!.notifyDataSetChanged()
                        }


                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }
            })

        }

        binding?.endDateBtn?.setOnClickListener {
            DatePickerDialog(this, listener2, stopYear, stopMonth, stopDate).show()
        }


    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()

        binding?.swipeLl?.setOnRefreshListener {
            binding?.swipeLl?.isRefreshing = false
            binding?.messageTxt?.isVisible = false
            binding?.sorryImg?.isVisible = false
            recyclerView!!.notifyDataSetChanged()
            binding?.startDateBtn?.text = "Start Date"
            binding?.endDateBtn?.text = "End Date"
            forMonthlyApi()

        }
    }

    private fun forMonthlyApi(){
        val monthly = "Monthly"
        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()

        Log.e("forMonthlyApi","forMonthlyApi $agentId")

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(
            token,typeAgent, "v1/groupDetails/$agentId")
        Log.e("urvashi", "$token  token")
        call?.enqueue(object : Callback<JsonObject> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("urvashi","$response")
                if (response.isSuccessful) {

                    val list = ArrayList<AllGroupDetailsData>()

                    val res = response.body()
                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject

                    Log.e("monthly","res $res")

                    val status = jsonObject.get("status")
                    val message = jsonObject.get("message")
                    val items = jsonObject.get("items")
                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray

                    if (jsonArray.isNull(0)) {
                        binding?.messageTxt?.isVisible = true
                        binding?.messageTxt?.text = "No EMI's"
                        binding?.sorryImg?.isVisible = true
                        binding?.allEmiRv?.isVisible = false
                    }

                    if (status == true) {
                        binding?.allEmiRv?.isVisible = true
                        binding?.progressBar?.isVisible = false
                        binding?.txtBar?.isVisible = false
                        for (i in 0 until jsonArray.length()) {

                            val groupId = jsonArray.getJSONObject(i).getString("groupId")
                            val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                            val interest = jsonArray.getJSONObject(i).getString("interest")
                            val disburseDate = jsonArray.getJSONObject(i).getString("disburseDate")
                            val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                            val groupDetails = jsonArray.getJSONObject(i).getString("groupDetails")
                            val jsonObject2 = JSONTokener(groupDetails.toString()).nextValue() as JSONObject
                            val teamLeadName = jsonObject2.getString("teamLeadName")
                            val totalGroupMember = jsonObject2.getString("totalGroupMember")
                            val leaderDetails = jsonArray.getJSONObject(i).getString("leaderDetails")

                            val jsonObject3 = JSONTokener(leaderDetails.toString()).nextValue() as JSONObject
                            val groupName = jsonObject3.getString("groupName")
                            val groupLeaderName = jsonObject3.getString("groupLeaderName")
                            val groupLeaderMobile = jsonObject3.getString("groupLeaderMobile")


                            list.add(
                                AllGroupDetailsData(
                                    agentId,
                                    token,
                                    groupId,
                                    loanAmount,
                                    interest,
                                    teamLeadName,
                                    groupLeaderName,
                                    collectionType,
                                    groupName,
                                    totalGroupMember,
                                    groupLeaderMobile,
                                    disburseDate
                                )
                            )
                        }
                        binding?.allEmiRv?.layoutManager =
                            LinearLayoutManager(this@AllInGroup)
                        recyclerView = AllGroupRv(list)
                        binding?.allEmiRv?.adapter = recyclerView
                        recyclerView!!.notifyDataSetChanged()
                    }else{
                        binding?.allEmiRv?.isVisible = false
                        binding?.messageTxt?.isVisible = true
                        binding?.messageTxt?.text = "No EMI's"
                        binding?.allEmiRv?.isVisible = false
                        binding?.sorryImg?.isVisible = true
                    }




                }else{
                    Log.e("urvashi", "fail")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                Toast.makeText(this@MonthlyInGroup, t.toString(), Toast.LENGTH_LONG).show()
                Log.e("urvashi", "$t your response is fail")
            }

        })

    }




    @SuppressLint("SimpleDateFormat")
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