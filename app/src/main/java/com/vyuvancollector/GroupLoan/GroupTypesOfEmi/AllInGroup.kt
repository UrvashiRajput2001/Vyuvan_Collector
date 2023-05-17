package com.vyuvancollector.GroupLoan.GroupTypesOfEmi

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.vyuvancollector.GroupLoan.AdapterInGroup.AllGroupRv
import com.vyuvancollector.GroupLoan.Group_Data_Class.AllGroupDetailsData
import com.vyuvancollector.Retrofit.ApiClient
import com.vyuvancollector.Retrofit.ApiInterface
import com.vyuvancollector.databinding.ActivityAllInGroupBinding
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllInGroup : AppCompatActivity() {

    private var binding : ActivityAllInGroupBinding? = null

    private var recyclerView : AllGroupRv? = null

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

        forAllAPI()

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


    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()

        binding?.swipeLl?.setOnRefreshListener {
            binding?.swipeLl?.isRefreshing = false
            forAllAPI()
            binding?.messageTxt?.isVisible = false
            binding?.sorryImg?.isVisible = false
            recyclerView!!.notifyDataSetChanged()
        }
    }

    private fun forAllAPI(){
        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.postDataGET2(
            token,
            "v1/groupDetails/getAllGroupDetails/$agentId"
        )
        Log.e("urvashi", "$token  token")
        call?.enqueue(object : Callback<JsonObject> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val list = ArrayList<AllGroupDetailsData>()
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

                            list.add(AllGroupDetailsData(
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
                            ))
                        }
                        binding?.allEmiRv?.layoutManager = LinearLayoutManager(this@AllInGroup)
                        recyclerView = AllGroupRv(list)
                        binding?.allEmiRv?.adapter = recyclerView
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