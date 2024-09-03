package com.vyuvancollectors.GroupLoan.GroupMemberList

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.OverDataByCollectionType.AllMemberOverDueData
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.OverDueByCollectionType.AllMemberOverDueListRV
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PaidByCollectionType.AllMemberPaidListRV
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PaidDataByCT.AllMemberPaidData
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PendingByCollectionType.AllMemberPendingListRV
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PendingDataByCT.AllMemberPendingData
import com.vyuvancollectors.R
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityAllMemberListBinding
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class AllMemberList : AppCompatActivity() {

    private var binding : ActivityAllMemberListBinding? = null
    val typeAgent = "Agent"


    @SuppressLint("NotifyDataSetChanged", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAllMemberListBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        binding?.pendingRv?.isVisible = false
        binding?.paidRv?.isVisible = false
        binding?.overdueRv?.isVisible = false
        binding?.messageTxt?.isVisible = false
        binding?.progressBar?.isVisible = true
        binding?.txtBar?.isVisible = true
        binding?.sorryImg?.isVisible = true

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
            binding?.progressBar?.isVisible = true
            binding?.txtBar?.isVisible = true
            binding?.pendingRv?.isVisible = false
            binding?.paidRv?.isVisible = false
            binding?.overdueRv?.isVisible = false
            binding?.pendingBtn?.background = resources.getDrawable(R.drawable.nav_blue_button_bg)
            binding?.paidBtn?.background = resources.getDrawable(R.drawable.status_btn_bg_for_gl)
            binding?.overdueBtn?.background = resources.getDrawable(R.drawable.status_btn_bg_for_gl)
            binding?.pendingBtn?.setTextColor(application.resources.getColor(R.color.white))
            binding?.paidBtn?.setTextColor(application.resources.getColor(R.color.black))
            binding?.overdueBtn?.setTextColor(application.resources.getColor(R.color.black))
            pendingAPI()
        }


        binding?.paidBtn?.setOnClickListener {
            binding?.progressBar?.isVisible = true
            binding?.txtBar?.isVisible = true
            binding?.pendingRv?.isVisible = false
            binding?.paidRv?.isVisible = false
            binding?.overdueRv?.isVisible = false
            binding?.paidBtn?.background = resources.getDrawable(R.drawable.nav_blue_button_bg)
            binding?.pendingBtn?.background = resources.getDrawable(R.drawable.status_btn_bg_for_gl)
            binding?.overdueBtn?.background = resources.getDrawable(R.drawable.status_btn_bg_for_gl)
            binding?.paidBtn?.setTextColor(application.resources.getColor(R.color.white))
            binding?.pendingBtn?.setTextColor(application.resources.getColor(R.color.black))
            binding?.overdueBtn?.setTextColor(application.resources.getColor(R.color.black))
            paidAPI()
        }

        binding?.overdueBtn?.setOnClickListener {
            binding?.progressBar?.isVisible = true
            binding?.txtBar?.isVisible = true
            binding?.pendingRv?.isVisible = false
            binding?.paidRv?.isVisible = false
            binding?.overdueRv?.isVisible = false
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
        binding?.pendingRv?.isVisible = false
        binding?.paidRv?.isVisible = false
        binding?.overdueRv?.isVisible = false
        val pending = "0"
        var recyclerView : AllMemberPendingListRV? = null

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
                    val list = ArrayList<AllMemberPendingData>()

                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
                    binding?.messageTxt?.isVisible = false
                    binding?.sorryImg?.isVisible = false
                    binding?.pendingRv?.isVisible = true

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
                        binding?.pendingRv?.isVisible = false
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

                            val jsonObject2 = JSONTokener(customerDetails.toString()).nextValue() as JSONObject
                            val name = jsonObject2.getString("name")
                            val phone = jsonObject2.getString("phone")

                            list.add(
                                AllMemberPendingData(
                                    id,token,name,phone,collectionType,emiAmount,status,agentId,customerId,loanId,dateOfCollect,totalGroupMember,groupName,groupId,emiAmount
                                )
                            )
                        }
                        binding?.pendingRv?.layoutManager = LinearLayoutManager(this@AllMemberList)
                        recyclerView = AllMemberPendingListRV(list)
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
        binding?.paidRv?.isVisible = false
        binding?.pendingRv?.isVisible = false
        binding?.overdueRv?.isVisible = false
        binding?.sorryImg?.isVisible = false

        var recyclerView : AllMemberPaidListRV? = null

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

                    val list = ArrayList<AllMemberPaidData>()

                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
                    binding?.messageTxt?.isVisible = false
                    binding?.sorryImg?.isVisible = false
                    binding?.paidRv?.isVisible = true
                    val res = response.body()
                    Log.e("Paid EMI Response","$res responses")

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.get("status")
                    val message = jsonObject.get("message")
                    val items = jsonObject.get("items")

                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                    if(jsonArray.isNull(0)){
                        binding?.messageTxt?.isVisible = true
                        binding?.messageTxt?.text = "No EMI's"
                        binding?.sorryImg?.isVisible = true
                        binding?.paidRv?.isVisible = false
                    }
                    if (status == true) {
                        for (i in 0 until jsonArray.length()) {
                            val paidEmiAmount = jsonArray.getJSONObject(i).getString("paidEmiAmount")
                            val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                            val emiStatus = jsonArray.getJSONObject(i).getString("status")
                            val  emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                            val lastDateCollected = jsonArray.getJSONObject(i).getString("lastDateCollected")
                            val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                            val customerDetails = jsonArray.getJSONObject(i).get("customerDetails")
                            val jsonObject2 = JSONTokener(customerDetails.toString()).nextValue() as JSONObject
                            val name = jsonObject2.getString("name")
                            val phone = jsonObject2.getString("phone")

                            list.add(
                                AllMemberPaidData(
                                    agentId,token,name,phone,collectionType,paidEmiAmount,emiStatus,dateOfCollect,lastDateCollected,emiNo
                                )
                            )
                        }
                        binding?.paidRv?.layoutManager = LinearLayoutManager(this@AllMemberList)
                        recyclerView = AllMemberPaidListRV(list)
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

        var recyclerView : AllMemberOverDueListRV?

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

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(token, typeAgent,"v1/emi/groupEmi/$agentId/$groupId")
        Log.e("urvashi", "$token  token")
        call?.enqueue(object : Callback<JsonObject> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val list = ArrayList<AllMemberOverDueData>()

                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
                    binding?.messageTxt?.isVisible = false
                    binding?.sorryImg?.isVisible = false
                    binding?.overdueRv?.isVisible = true
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
                        binding?.overdueRv?.isVisible = false
                    }

                    if (status == true) {
                        for (i in 0 until jsonArray.length()) {
                            val id = jsonArray.getJSONObject(i).getString("_id")
                            val emiAmount = jsonArray.getJSONObject(i).getString("emiAmount")
                            val emiNo = jsonArray.getJSONObject(i).getString("emiNumber")
                            val paidEmiAmount = jsonArray.getJSONObject(i).getString("paidEmiAmount")
                            val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                            val dateOfCollect = jsonArray.getJSONObject(i).getString("dateOfCollect")
                            val customerId = jsonArray.getJSONObject(i).getString("customerId")
                            val loanId = jsonArray.getJSONObject(i).getString("loanId")
                            val status = jsonArray.getJSONObject(i).getString("status")
                            val customerDetails = jsonArray.getJSONObject(i).get("customerDetails")

                            val jsonObject2 = JSONTokener(customerDetails.toString()).nextValue() as JSONObject
                            val name = jsonObject2.getString("name")
                            val phone = jsonObject2.getString("phone")

                            list.add(AllMemberOverDueData(id,token,name,phone,collectionType,emiAmount,status,agentId,customerId,loanId,dateOfCollect, paidEmiAmount,totalGroupMember,groupName,groupId,emiNo))
                        }
                        binding?.overdueRv?.layoutManager = LinearLayoutManager(this@AllMemberList)
                        recyclerView = AllMemberOverDueListRV(list)
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
            Log.e("Connectivity Exception", e.message!!)
        }
        return connected
    }


}