package com.vyuvancollectors.GroupLoan.GroupTypesOfEmi

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.vyuvancollectors.GroupLoan.AdapterInGroup.WeekLyGroupRv
import com.vyuvancollectors.GroupLoan.Group_Data_Class.WeeklyGroupDetailsData
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityWeeklyInGroupBinding
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class WeeklyInGroup : AppCompatActivity() {

    private var binding : ActivityWeeklyInGroupBinding? = null


    private var recyclerView : WeekLyGroupRv? = null

    val typeAgent = "Agent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWeeklyInGroupBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        binding?.progressBar?.isVisible = true
        binding?.txtBar?.isVisible = true
        binding?.swipeLl?.isRefreshing = false
        binding?.messageTxt?.isVisible = false
        binding?.swipeLl?.isRefreshing = false
        binding?.sorryImg?.isVisible = false

        forWeeklyApi()

        binding?.searchEt?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                forPhoneSearch()
            }
            true
        }

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
            forWeeklyApi()
            binding?.messageTxt?.isVisible = false
            binding?.sorryImg?.isVisible = false
            recyclerView!!.notifyDataSetChanged()

        }
    }


    private fun forWeeklyApi(){
        val weekly = "Weekly"
        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(
            token,typeAgent,
            "v1/groupDetails/$agentId/$weekly"
        )

        Log.e("urvashi", "$token  token")
        call?.enqueue(object : Callback<JsonObject> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {

                    val list = ArrayList<WeeklyGroupDetailsData>()

                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
                    val res = response.body()
                    Log.e("Urvashi", "$res res")

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject

                    val status = jsonObject.get("status")
                    Log.i("urvashi$$", "status $status")

                    val message = jsonObject.get("message")
                    Log.i("urvashi$$", "message $message")

                    val items = jsonObject.get("items")
                    Log.e("Urvashi", "items $items")

                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                    Log.i("urvashi$$", "jsonArray $jsonArray")

                    if (jsonArray.isNull(0)) {
                        binding?.messageTxt?.isVisible = true
                        binding?.messageTxt?.text = "No EMI's"
                        binding?.sorryImg?.isVisible = true
                    }


                    if (status == true) {
                            for (i in 0 until jsonArray.length()) {

                                val groupId = jsonArray.getJSONObject(i).getString("groupId")
                                Log.e("Urvashi", "groupId $groupId")

                                val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                                Log.e("Urvashi", "loanAmount $loanAmount")

                                val interest = jsonArray.getJSONObject(i).getString("interest")
                                Log.e("Urvashi", "interest $interest")

                                val disburseDate = jsonArray.getJSONObject(i).getString("disburseDate")
                                Log.e("Urvashi", "disburseDate $disburseDate")

                                val collectionType =
                                    jsonArray.getJSONObject(i).getString("collectionType")
                                Log.e("Urvashi", "collectionType $collectionType")

                                val groupDetails =
                                    jsonArray.getJSONObject(i).getString("groupDetails")
                                Log.e("Urvashi", "groupDetails $groupDetails")

                                val jsonObject2 =
                                    JSONTokener(groupDetails.toString()).nextValue() as JSONObject
                                Log.i("urvashi$$", "jsonObject2 $jsonObject2")

                                val teamLeadName = jsonObject2.getString("teamLeadName")
                                Log.e("urvashi", "teamLeadName $teamLeadName")

                                val totalGroupMember = jsonObject2.getString("totalGroupMember")
                                Log.e("urvashi", "totalGroupMember $totalGroupMember")

                                val leaderDetails =
                                    jsonArray.getJSONObject(i).getString("leaderDetails")
                                Log.e("Urvashi", "leaderDetails $leaderDetails")

                                val jsonObject3 =
                                    JSONTokener(leaderDetails.toString()).nextValue() as JSONObject
                                Log.i("urvashi$$", "jsonObject3 $jsonObject3")


                                val groupName = jsonObject3.getString("groupName")
                                Log.e("urvashi", "groupName $groupName")

                                val groupLeaderName = jsonObject3.getString("groupLeaderName")
                                Log.e("urvashi", "groupLeaderName $groupLeaderName")

                                val groupLeaderMobile = jsonObject3.getString("groupLeaderMobile")
                                Log.e("urvashi", "groupLeaderMobile $groupLeaderMobile")

                                list.add(
                                    WeeklyGroupDetailsData(
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
                        binding?.weeklyEmiRv?.layoutManager =
                                LinearLayoutManager(this@WeeklyInGroup)
                        recyclerView = WeekLyGroupRv(list)
                        binding?.weeklyEmiRv?.adapter = recyclerView
                        recyclerView!!.notifyDataSetChanged()
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                Toast.makeText(this@WeeklyInGroup, t.toString(), Toast.LENGTH_LONG).show()
                Log.e("urvashi", "$t your response is fail")
            }
        })
    }

    private fun forPhoneSearch(){
        if (binding?.searchEt?.text?.length == 10) {
            val collectionType = "Weekly"
            val phone = binding?.searchEt?.text

            val bundle = intent.extras!!

            @Suppress("DEPRECATION")
            val token = bundle.get("token").toString()

            @Suppress("DEPRECATION")
            val agentId = bundle.get("agentId").toString()

            val json = JsonObject()
            json.addProperty("groupLeaderMobile", "$phone")
            json.addProperty("collectionType","$collectionType")
            json.addProperty("agentId", "$agentId")

            @Suppress("DEPRECATION")
            val jsonObjectRequestBody : RequestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(), json.toString()
            )

            val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
            val call = apiClient?.postTwoHeadersWithTokenData(token, typeAgent,"/v1/emi/groupEmis/collectionType/groupLeaderMobile", jsonObjectRequestBody)
            call?.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val list = ArrayList<WeeklyGroupDetailsData>()

                        binding?.progressBar?.isVisible = false
                        binding?.txtBar?.isVisible = false
                        val res = response.body()

                        Log.e("Message","$res res")

                        val jsonObjectMain = JSONTokener(res.toString()).nextValue() as JSONObject
                        val status = jsonObjectMain.get("status")
                        val message = jsonObjectMain.get("message")
                        val items = jsonObjectMain.get("items")

                        val jsonArrayMain = JSONTokener(items.toString()).nextValue() as JSONArray

                        if (jsonArrayMain.isNull(0)) {
                            binding?.messageTxt?.isVisible = true
                            binding?.messageTxt?.text = "No EMI's"
                            binding?.weeklyEmiRv?.isVisible = false
                            binding?.sorryImg?.isVisible = true
                        }
                        if (status == true) {
                            for (i in 0 until jsonArrayMain.length()) {
                                val groupDetail = jsonArrayMain.getJSONObject(i).getString("groupDetail")
                                val jsonObject = JSONTokener(groupDetail.toString()).nextValue() as JSONObject
                                val teamLeadName = jsonObject.getString("teamLeadName")
                                val totalGroupMember = jsonObject.getString("totalGroupMember")

                                val leaderDetail = jsonArrayMain.getJSONObject(i).getString("leaderDetail")
                                val jsonObject2 = JSONTokener(leaderDetail.toString()).nextValue() as JSONObject
                                val groupName = jsonObject2.getString("groupName")
                                val groupLeaderMobile = jsonObject2.getString("groupLeaderMobile")
                                val groupLeaderName = jsonObject2.getString("groupLeaderName")

                                val groupLoanDetail = jsonArrayMain.getJSONObject(i).getString("groupLoanDetail")
                                val jsonObject3 = JSONTokener(groupLoanDetail.toString()).nextValue() as JSONObject
                                val groupId = jsonObject3.getString("groupId")
                                val loanAmount = jsonObject3.getString("loanAmount")
                                val interest = jsonObject3.getString("interest")
                                val collectionType = jsonObject3.getString("collectionType")
                                val disburseDate = jsonObject3.getString("disburseDate")

                                list.add(
                                    WeeklyGroupDetailsData(
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
                                Log.e("Message","$message message")
                            }
                            binding?.weeklyEmiRv?.layoutManager =
                                LinearLayoutManager(this@WeeklyInGroup)
                            recyclerView = WeekLyGroupRv(list)
                            binding?.weeklyEmiRv?.adapter = recyclerView
                            recyclerView!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }

            })
        }else{
            binding?.progressBar?.isVisible = false
            binding?.txtBar?.isVisible = false
            binding?.messageTxt?.isVisible = true
            binding?.sorryImg?.isVisible = false
            binding?.messageTxt?.text = "Please Enter 10 Digit Number"
            binding?.weeklyEmiRv?.isVisible = false
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
            Log.e("Connectivity Exception", e.message!!)
        }
        return connected
    }





}