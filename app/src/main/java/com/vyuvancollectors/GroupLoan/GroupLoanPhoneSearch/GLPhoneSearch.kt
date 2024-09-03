package com.vyuvancollectors.GroupLoan.GroupLoanPhoneSearch

import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.vyuvancollectors.GroupLoan.Group_Data_Class.AllGroupDetailsData
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityGlphoneSearchBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GLPhoneSearch : AppCompatActivity() {

    private var binding : ActivityGlphoneSearchBinding? = null

    private var recyclerView : GLPhoneSearchRV? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGlphoneSearchBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
//            binding?.messageTxt?.isVisible = true
            binding?.progressBar?.isVisible = false
            binding?.txtBar?.isVisible = false
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }

        binding?.searchEt?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                forPhoneSearch()
            }
            true
        }

        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.progressBar?.isVisible = false
        binding?.txtBar?.isVisible = false
        binding?.messageTxt?.isVisible = false




    }

    private fun forPhoneSearch(){
        if(binding?.searchEt?.text?.length == 10) {
            val phone = binding?.searchEt?.text

            val bundle = intent.extras!!
            @Suppress("DEPRECATION")
            val token = bundle.get("token").toString()
            @Suppress("DEPRECATION")
            val agentId = bundle.get("agentId").toString()

            val typeAgent = "Agent"

            val json = JsonObject()
            json.addProperty("groupLeaderMobile", "$phone")
            json.addProperty("agentId", "$agentId")

            @Suppress("DEPRECATION")
            val jsonObject: RequestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(), json.toString()
            )

            val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
            val call = apiClient?.postTwoHeadersWithTokenData(token, typeAgent,"/v1/emi/groupEmis/getAllGroupDetailByMobile", jsonObject)
            call?.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val list = ArrayList<AllGroupDetailsData>()

                        binding?.progressBar?.isVisible = false
                        binding?.txtBar?.isVisible = false
                        val res = response.body()

                        Log.e("phone","$res")

                        val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                        val status = jsonObject.get("status")
                        val message = jsonObject.get("message")
                        val items = jsonObject.get("items")

                        val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray

                        if (jsonArray.isNull(0)) {
                            binding?.messageTxt?.isVisible = true
                            binding?.messageTxt?.text = "No EMI's"
                            binding?.customerAllLoansEmiRv?.isVisible = false
                        }
                        if (status == true) {
                            for (i in 0 until jsonArray.length()) {
                                val teamLeadName =
                                    jsonArray.getJSONObject(i).getString("teamLeadName")
                                val totalGroupMember =
                                    jsonArray.getJSONObject(i).getString("totalGroupMember")
                                val groupLoanDetail =
                                    jsonArray.getJSONObject(i).getString("groupLoanDetail")

                                val leaderDetail =
                                    jsonArray.getJSONObject(i).getString("leaderDetail")
                                val jsonObject2 =
                                    JSONTokener(leaderDetail.toString()).nextValue() as JSONObject
                                val groupName = jsonObject2.getString("groupName")
                                val groupLeaderMobile = jsonObject2.getString("groupLeaderMobile")
                                val groupLeaderName = jsonObject2.getString("groupLeaderName")

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
                            binding?.customerAllLoansEmiRv?.layoutManager = LinearLayoutManager(this@GLPhoneSearch)
                            recyclerView = GLPhoneSearchRV(list)
                            binding?.customerAllLoansEmiRv?.adapter = recyclerView
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
            binding?.messageTxt?.text = "Please Enter 10 Digit Number"
            binding?.customerAllLoansEmiRv?.isVisible = false
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