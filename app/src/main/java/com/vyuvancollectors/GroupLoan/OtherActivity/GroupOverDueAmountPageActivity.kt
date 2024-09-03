package com.vyuvancollectors.GroupLoan.OtherActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityGroupOverDueAmountPageBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class GroupOverDueAmountPageActivity : AppCompatActivity() {

    private var binding : ActivityGroupOverDueAmountPageBinding? = null
    private var recyclerView : GroupOverDueAmountPageRV? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupOverDueAmountPageBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.progressBar?.isVisible = true
        binding?.txtBar?.isVisible = true
        binding?.swipeLl?.isRefreshing = false
        binding?.messageTxt?.isVisible = false
        binding?.swipeLl?.isRefreshing = false
        binding?.sorryImg?.isVisible = false

        API()

        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }

    }

    private fun API(){


        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?

        val loanType = "GL"
        val month = "Monthly"
        val typeAgent = "Agent"

        val json = JsonObject()
        json.addProperty("agentId","$agentId")
        json.addProperty("loanType","$loanType")
        json.addProperty("collectionType","$month")

        @Suppress("DEPRECATION")
        val jsonObjectInBody : RequestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), json.toString())

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.postTwoHeadersWithTokenData(
            token.toString(),typeAgent,
            "v1/emi/getOverDueEmi/agentId/loanType/collectionType",jsonObjectInBody
        )
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val list = ArrayList<GroupOverDueAmountPageData>()
                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
                    val res = response.body()

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
                            Log.e("emiId","$emiId emiId")
                            val emiNo = jsonArrayMain.getJSONObject(i).getString("emiNumber")
                            val loanAmount = jsonArrayMain.getJSONObject(i).getString("loanAmount")
                            val emiAmount = jsonArrayMain.getJSONObject(i).getString("emiAmount")
                            val dateOfCollect = jsonArrayMain.getJSONObject(i).getString("dateOfCollect")
                            val remainingAmount = jsonArrayMain.getJSONObject(i).getString("remainingAmount")
                            val loanDetails = jsonArrayMain.getJSONObject(i).getString("loanDetails")
                            val customerId = jsonArrayMain.getJSONObject(i).getString("customerId")
                            Log.e("customerID","$customerId customerId")
                            val jsonObject2 = JSONTokener(loanDetails.toString()).nextValue() as JSONArray

                            for(j in 0 until jsonObject2.length()) {

                                val loanId = jsonObject2.getJSONObject(j).getString("_id")


                                val customerDetail =
                                    jsonArrayMain.getJSONObject(i).getString("customerDetails")
                                val jsonObject3 =
                                    JSONTokener(customerDetail.toString()).nextValue() as JSONArray

                                for (k in 0 until jsonObject3.length()) {
                                    val name = jsonObject3.getJSONObject(k).getString("name")
                                    val phone = jsonObject3.getJSONObject(k).getString("phone")
                                    val whatsapp =
                                        jsonObject3.getJSONObject(k).getString("whatsAppNumber")

                                    val leaderDetails =
                                        jsonArrayMain.getJSONObject(i).getString("leaderDetails")
                                    val jsonObject4 =
                                        JSONTokener(leaderDetails.toString()).nextValue() as JSONArray

                                    for (l in 0 until jsonObject4.length()) {

                                        val groupName =
                                            jsonObject4.getJSONObject(l).getString("groupName")


                                        list.add(
                                            GroupOverDueAmountPageData(
                                                name,
                                                phone,
                                                whatsapp,
                                                dateOfCollect,
                                                groupName,
                                                emiNo,
                                                emiAmount, emiStatus, emiId, token.toString(),
                                                customerId,
                                                loanId,
                                                agentId.toString()
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        binding?.overdueEmiRv?.layoutManager =
                            LinearLayoutManager(this@GroupOverDueAmountPageActivity)
                        recyclerView = GroupOverDueAmountPageRV(list)
                        binding?.overdueEmiRv?.adapter = recyclerView
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