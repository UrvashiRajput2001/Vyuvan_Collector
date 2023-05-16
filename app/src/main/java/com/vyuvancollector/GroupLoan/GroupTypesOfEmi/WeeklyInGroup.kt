package com.vyuvancollector.GroupLoan.GroupTypesOfEmi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.vyuvancollector.GroupLoan.AdapterInGroup.MonthlyGroupRv
import com.vyuvancollector.GroupLoan.AdapterInGroup.WeekLyGroupRv
import com.vyuvancollector.GroupLoan.Group_Data_Class.MonthlyGroupDetailsData
import com.vyuvancollector.GroupLoan.Group_Data_Class.WeeklyGroupDetailsData
import com.vyuvancollector.PersonalLoan.Adapter.DailyEMIRecyclerView
import com.vyuvancollector.PersonalLoan.Data_Class.ModalDaily
import com.vyuvancollector.Retrofit.ApiClient
import com.vyuvancollector.Retrofit.ApiInterface
import com.vyuvancollector.databinding.ActivityWeeklyInGroupBinding
import com.google.gson.JsonObject
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

        binding?.backBtn?.setOnClickListener {
            onBackPressed()
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
        val call = apiClient?.postDataGET2(
            token,
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





}