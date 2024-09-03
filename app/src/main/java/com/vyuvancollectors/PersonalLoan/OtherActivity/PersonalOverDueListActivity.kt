package com.vyuvancollectors.PersonalLoan.OtherActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityPersonalOverDueListBinding
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class PersonalOverDueListActivity : AppCompatActivity() {
    private var binding : ActivityPersonalOverDueListBinding? = null
    private var recyclerView : PersonalOverDueListRV? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalOverDueListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.progressBar?.isVisible = true
        binding?.txtBar?.isVisible = true
        binding?.swipeLl?.isRefreshing = false
        binding?.messageTxt?.isVisible = false
        binding?.swipeLl?.isRefreshing = false
        binding?.sorryImg?.isVisible = false



        apiCall()

        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun apiCall(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        val typeAgent = "Agent"

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(
            token.toString(),typeAgent,
            "v1/emisData/allEmiPaidPerAgent/$agentId"
        )
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val list = ArrayList<PersonalPaidEMIListDataClass>()
                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
                    binding?.messageTxt?.isVisible = false
                    binding?.sorryImg?.isVisible = false
                    val res = response.body()

                    val jsonObject1 = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject1.get("status")
                    val message = jsonObject1.get("message")
                    val items = jsonObject1.get("items")

                    Log.e("LILI Paid collected","$res")

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
                            val emiType = jsonArray.getJSONObject(i).getString("emiType")
                            if (emiType == "Overdue") {
                                val customerDetail =
                                    jsonArray.getJSONObject(i).getString("customerDetail")

                                val jsonObject2 =
                                    JSONTokener(customerDetail.toString()).nextValue() as JSONArray
                                for (j in 0 until jsonObject2.length()) {
                                    val name = jsonObject2.getJSONObject(j).getString("name")
                                    val phone = jsonObject2.getJSONObject(j).getString("phone")


                                    list.add(
                                        PersonalPaidEMIListDataClass(
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
                                            agentId.toString(),
                                            token.toString(),
                                            dateOfCollect,
                                            emiStatus,
                                            emiType
                                        )
                                    )
                                }
                            }
                        }
                        binding?.overdueEmiRv?.layoutManager =
                            LinearLayoutManager(this@PersonalOverDueListActivity)
                        recyclerView = PersonalOverDueListRV(list)
                        binding?.overdueEmiRv?.adapter = recyclerView
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

}