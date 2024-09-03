package com.vyuvancollectors.GroupLoan.GroupLoanPhoneSearch

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.OverDataByCollectionType.AllMemberOverDueData
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PaidDataByCT.AllMemberPaidData
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PendingDataByCT.AllMemberPendingData
import com.vyuvancollectors.R
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityGlphoneSearchMemberListBinding
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class GLPhoneSearchMemberList : AppCompatActivity() {

    private var binding : ActivityGlphoneSearchMemberListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGlphoneSearchMemberListBinding.inflate(layoutInflater)

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
        binding?.messageTxt?.isVisible = false
        binding?.sorryImg?.isVisible = false
        binding?.pendingRv?.isVisible = true
        binding?.paidRv?.isVisible = false
        binding?.overdueRv?.isVisible = false
        val pending = "0"
        var recyclerView : GLMemberListPendingRV? = null

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
        val call = apiClient?.getTwoHeadersWithTokenData(token, typeAgent,"v1/emi/groupEmi/$agentId/$groupId/$pending")
        call?.enqueue(object : Callback<JsonObject> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.isSuccessful) {
                    val list = ArrayList<AllMemberPendingData>()

                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
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
                                AllMemberPendingData(
                                    id,token,name,phone,collectionType,emiAmount,status,agentId,customerId,loanId,dateOfCollect,totalGroupMember,groupName,groupId,emiNo
                                )
                            )
                        }
                        binding?.pendingRv?.layoutManager = LinearLayoutManager(this@GLPhoneSearchMemberList)
                        recyclerView = GLMemberListPendingRV(list)
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
        binding?.messageTxt?.isVisible = false
        binding?.paidRv?.isVisible = true
        binding?.pendingRv?.isVisible = false
        binding?.overdueRv?.isVisible = false
        binding?.sorryImg?.isVisible = false

        var recyclerView : GLMemberListPaidRV? = null

        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val groupId = bundle.get("groupId").toString()
        @Suppress("DEPRECATION")
        val groupName = bundle.get("groupName").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()
        @Suppress("DEPRECATION")
        val totalGroupMember = bundle.get("totalGroupMember").toString()

        val typeAgent = "Agent"

        val paid = "1"

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(
            token,
            typeAgent,
            "v1/emi/groupEmi/$agentId/$groupId/$paid"
        )
        call?.enqueue(object : Callback<JsonObject> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {

                    val list = ArrayList<AllMemberPaidData>()

                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
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
                        binding?.sorryImg?.isVisible = true
                    }
                    if (status == true) {
                        for (i in 0 until jsonArray.length()) {
                            val paidEmiAmount = jsonArray.getJSONObject(i).getString("paidEmiAmount")
                            val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                            val emiStatus = jsonArray.getJSONObject(i).getString("status")
                            val lastDateCollected = jsonArray.getJSONObject(i).getString("lastDateCollected")
                            val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                            val customerDetails = jsonArray.getJSONObject(i).get("customerDetails")
                            val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                            val jsonObject2 = JSONTokener(customerDetails.toString()).nextValue() as JSONObject
                            val name = jsonObject2.getString("name")
                            val phone = jsonObject2.getString("phone")

                            list.add(
                                AllMemberPaidData(
                                    agentId,token,name,phone,collectionType,paidEmiAmount,emiStatus,dateOfCollect,lastDateCollected,emiNo
                                )
                            )
                        }
                        binding?.paidRv?.layoutManager = LinearLayoutManager(this@GLPhoneSearchMemberList)
                        recyclerView = GLMemberListPaidRV(list)
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

        var recyclerView : GLMemberOverdueRV?

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
        val call = apiClient?.getTwoHeadersWithTokenData(token, typeAgent,"v1/emi/groupEmi/$agentId/$groupId")
        call?.enqueue(object : Callback<JsonObject> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val list = ArrayList<AllMemberOverDueData>()

                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
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

                            list.add(AllMemberOverDueData(id,token,name,phone,collectionType,emiAmount,status,agentId,customerId,loanId,dateOfCollect, paidEmiAmount,totalGroupMember,groupName,groupId,emiNo))
                        }
                        binding?.overdueRv?.layoutManager = LinearLayoutManager(this@GLPhoneSearchMemberList)
                        recyclerView = GLMemberOverdueRV(list)
                        binding?.overdueRv?.adapter = recyclerView
                        recyclerView!!.notifyDataSetChanged()
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
        }
        return connected
    }


}