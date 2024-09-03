package com.vyuvancollectors.GroupLoan.GroupMemberList

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.OverDataByCollectionType.WeeklyMemberOverDueData
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.OverDueByCollectionType.WeeklyMemberOverDueListRV
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PaidByCollectionType.WeeklyMemberPaidListRV
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PaidDataByCT.WeeklyMemberPaidData
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PendingByCollectionType.WeeklyMemberPendingListRV
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PendingDataByCT.WeeklyMemberPendingData
import com.vyuvancollectors.R
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityWeeklyMemberListBinding
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class WeeklyMemberList : AppCompatActivity() {

    private var binding : ActivityWeeklyMemberListBinding? = null

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWeeklyMemberListBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        binding?.pendingRv?.isVisible = false
        binding?.paidRv?.isVisible = false
        binding?.overdueRv?.isVisible = false
        binding?.messageTxt?.isVisible = false
        binding?.progressBar?.isVisible = false
        binding?.txtBar?.isVisible = false
        binding?.sorryImg?.isVisible = false

        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val groupId = bundle.get("groupId").toString()
        @Suppress("DEPRECATION")
        val groupName = bundle.get("groupName").toString()
        @Suppress("DEPRECATION")
        val totalGroupMember = bundle.get("totalGroupMember").toString()

        Log.e("Urvashi","$token token, $groupId groupTd,")

        binding?.groupNameTxt?.text = "$groupName"
        binding?.memberCountTxt?.text = "Total $totalGroupMember Members"

        binding?.paidBtn?.background = resources.getDrawable(R.drawable.nav_blue_button_bg)
        binding?.paidBtn?.setTextColor(application.resources.getColor(R.color.white))
        paidAPI()



        binding?.pendingBtn?.setOnClickListener {
            binding?.pendingBtn?.background = resources.getDrawable(R.drawable.nav_blue_button_bg)
            binding?.paidBtn?.background = resources.getDrawable(R.drawable.status_btn_bg_for_gl)
            binding?.overdueBtn?.background = resources.getDrawable(R.drawable.status_btn_bg_for_gl)
            binding?.pendingBtn?.setTextColor(application.resources.getColor(R.color.white))
            binding?.paidBtn?.setTextColor(application.resources.getColor(R.color.black))
            binding?.overdueBtn?.setTextColor(application.resources.getColor(R.color.black))
            pendingAPI()
        }


        binding?.paidBtn?.setOnClickListener {
            binding?.paidBtn?.background = resources.getDrawable(R.drawable.nav_blue_button_bg)
            binding?.pendingBtn?.background = resources.getDrawable(R.drawable.status_btn_bg_for_gl)
            binding?.overdueBtn?.background = resources.getDrawable(R.drawable.status_btn_bg_for_gl)
            binding?.paidBtn?.setTextColor(application.resources.getColor(R.color.white))
            binding?.pendingBtn?.setTextColor(application.resources.getColor(R.color.black))
            binding?.overdueBtn?.setTextColor(application.resources.getColor(R.color.black))
            paidAPI()
        }

        binding?.overdueBtn?.setOnClickListener {
            binding?.overdueBtn?.background = resources.getDrawable(R.drawable.nav_blue_button_bg)
            binding?.paidBtn?.background = resources.getDrawable(R.drawable.status_btn_bg_for_gl)
            binding?.pendingBtn?.background = resources.getDrawable(R.drawable.status_btn_bg_for_gl)
            binding?.overdueBtn?.setTextColor(application.resources.getColor(R.color.white))
            binding?.paidBtn?.setTextColor(application.resources.getColor(R.color.black))
            binding?.pendingBtn?.setTextColor(application.resources.getColor(R.color.black))
            overDueAPI()
        }

        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
//            binding?.messageTxt?.isVisible = true
            binding?.progressBar?.isVisible = false
            binding?.txtBar?.isVisible = false
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }


    private fun pendingAPI(){
        binding?.pendingRv?.isVisible = true
        binding?.paidRv?.isVisible = false
        binding?.overdueRv?.isVisible = false
        binding?.messageTxt?.isVisible = false
        binding?.sorryImg?.isVisible = false

        var recyclerView : WeeklyMemberPendingListRV? = null
        val pending = "0"

        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val groupId = bundle.get("groupId").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()
        @Suppress("DEPRECATION")
        val groupName = bundle.get("groupName").toString()
        @Suppress("DEPRECATION")
        val totalGroupMember = bundle.get("totalGroupMember").toString()

        val typeAgent = "Agent"

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(
            token,typeAgent,
            "v1/emi/groupEmi/$agentId/$groupId/$pending"
        )
        Log.e("Urvashi","$token token, $groupId groupTd,")
        call?.enqueue(object : Callback<JsonObject> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.isSuccessful) {
                    val list = ArrayList<WeeklyMemberPendingData>()
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
                        binding?.messageTxt?.text = "'NO Emi's'"
                    }

                    if (status == true) {
                        for (i in 0 until jsonArray.length()) {
                            val id = jsonArray.getJSONObject(i).getString("_id")
                            val emiAmount = jsonArray.getJSONObject(i).getString("emiAmount")
                            val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                            val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                            val customerId = jsonArray.getJSONObject(i).getString("customerId")
                            val loanId = jsonArray.getJSONObject(i).getString("loanId")
                            val status = jsonArray.getJSONObject(i).getString("status")
                            val customerDetails = jsonArray.getJSONObject(i).get("customerDetails")
                            val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")

                            val jsonObject2 = JSONTokener(customerDetails.toString()).nextValue() as JSONObject
                            val name = jsonObject2.getString("name")
                            val phone = jsonObject2.getString("phone")

                            list.add(
                                WeeklyMemberPendingData(
                                    id,token,name,phone,collectionType,emiAmount,status,agentId,customerId,loanId,dateOfCollect,totalGroupMember,groupName,groupId,emiNo
                                )
                            )
                        }
                        binding?.pendingRv?.layoutManager = LinearLayoutManager(this@WeeklyMemberList)
                        recyclerView = WeeklyMemberPendingListRV(list)
                        binding?.pendingRv?.adapter = recyclerView
                        recyclerView!!.notifyDataSetChanged()
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("urvashi", "$t your response is fail")
            }
        })
    }

    private fun paidAPI(){
        binding?.paidRv?.isVisible = true
        binding?.pendingRv?.isVisible = false
        binding?.overdueRv?.isVisible = false
        binding?.messageTxt?.isVisible = false
        binding?.sorryImg?.isVisible = false

        var recyclerView : WeeklyMemberPaidListRV? = null

        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val groupId = bundle.get("groupId").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()

        val typeAgent = "Agent"

        val paid = "1"

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(
            token,typeAgent,
            "v1/emi/groupEmi/$agentId/$groupId/$paid"
        )
        Log.e("urvashi", "$token  token")
        call?.enqueue(object : Callback<JsonObject> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {

                    val list = ArrayList<WeeklyMemberPaidData>()

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
                            val paidEmiAmount = jsonArray.getJSONObject(i).getString("paidEmiAmount")
                            val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                            val lastDateCollect = jsonArray.getJSONObject(i).getString("lastDateCollected")
                            val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                            val status = jsonArray.getJSONObject(i).getString("status")
                            val customerDetails = jsonArray.getJSONObject(i).get("customerDetails")
                            val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")

                            val jsonObject2 = JSONTokener(customerDetails.toString()).nextValue() as JSONObject
                            val name = jsonObject2.getString("name")
                            val phone = jsonObject2.getString("phone")

                            list.add(
                                WeeklyMemberPaidData(
                                    token,name,phone,collectionType,paidEmiAmount,status,dateOfCollect,lastDateCollect,emiNo
                                )
                            )
                        }
                        binding?.paidRv?.layoutManager = LinearLayoutManager(this@WeeklyMemberList)
                        recyclerView = WeeklyMemberPaidListRV(list)
                        binding?.paidRv?.adapter = recyclerView
                        recyclerView!!.notifyDataSetChanged()
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("urvashi", "$t your response is fail")
            }
        })
    }

    private fun overDueAPI(){
        binding?.pendingRv?.isVisible = false
        binding?.paidRv?.isVisible = false
        binding?.overdueRv?.isVisible = true
        binding?.messageTxt?.isVisible = false
        binding?.sorryImg?.isVisible = false

        var recyclerViewOverDue : WeeklyMemberOverDueListRV? = null

        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val groupId = bundle.get("groupId").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()
        @Suppress("DEPRECATION")
        val groupName = bundle.get("groupName").toString()
        @Suppress("DEPRECATION")
        val totalGroupMember = bundle.get("totalGroupMember").toString()

        val typeAgent = "Agent"

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(
            token,typeAgent,
            "v1/emi/groupEmi/$agentId/$groupId"
        )
        Log.e("urvashi", "$token  token")
        call?.enqueue(object : Callback<JsonObject> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val list = ArrayList<WeeklyMemberOverDueData>()
                if (response.isSuccessful) {
                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
                    val res = response.body()

                    Log.e("kakuda","$res ---")
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
                            val id = jsonArray.getJSONObject(i).getString("_id")
                            val emiAmount = jsonArray.getJSONObject(i).getString("emiAmount")
                            val paidEmiAmount = jsonArray.getJSONObject(i).getString("paidEmiAmount")
                            val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                            val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                            val customerId = jsonArray.getJSONObject(i).getString("customerId")
                            val loanId = jsonArray.getJSONObject(i).getString("loanId")
                            val status = jsonArray.getJSONObject(i).getString("status")
                            val customerDetails = jsonArray.getJSONObject(i).get("customerDetails")
                            val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")

                            val jsonObject2 = JSONTokener(customerDetails.toString()).nextValue() as JSONObject
                            val name = jsonObject2.getString("name")
                            val phone = jsonObject2.getString("phone")

                            list.add(
                                WeeklyMemberOverDueData(
                                    id,token,name,phone,collectionType,emiAmount,status,agentId,customerId,loanId,dateOfCollect,paidEmiAmount,totalGroupMember,groupName,groupId,emiNo
                                )
                            )
                        }
                        binding?.overdueRv?.layoutManager = LinearLayoutManager(this@WeeklyMemberList)
                        recyclerViewOverDue = WeeklyMemberOverDueListRV(list)
                        binding?.overdueRv?.adapter = recyclerViewOverDue
                        recyclerViewOverDue!!.notifyDataSetChanged()
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