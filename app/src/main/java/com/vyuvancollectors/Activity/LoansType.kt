package com.vyuvancollectors.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.vyuvancollectors.GroupLoan.OtherActivity.GroupCollectionType
import com.vyuvancollectors.PersonalLoan.StartActivitys.PersonalEmiStatus
import com.vyuvancollectors.RecentLoan.RecentLoanData
import com.vyuvancollectors.RecentLoan.RecentLoanRV
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.SavingAccount.CustomerListSavingAccount.CustomerListSavingAccount
import com.vyuvancollectors.SavingAccount.SavingAccountForm
import com.vyuvancollectors.databinding.ActivityLoansTypeBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoansType : AppCompatActivity() {

    private var binding : ActivityLoansTypeBinding? = null

    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityLoansTypeBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)

        setContentView(binding?.root)

        recentLoan()

        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        Log.e("Token In LoanType Screen","$token Token $agentId agentId")

        binding?.logoutBtn?.isVisible = false
//        binding?.customListBtn?.isVisible = false
        binding?.messageTxt?.isVisible = false
        binding?.savingsAccountBtn?.isVisible = false

        binding?.menuImg?.setOnClickListener {
            binding?.logoutBtn?.isVisible = true
            binding?.savingsAccountBtn?.isVisible = true
//            binding?.customListBtn?.isVisible = true
        }


//        binding?.customListBtn?.setOnClickListener {
//            val intent = Intent(this, CustomerList::class.java)
//            intent.putExtra("token","$token")
//            intent.putExtra("agentId","$agentId")
//            startActivity(intent)
//            binding?.logoutBtn?.isVisible = false
//            binding?.customListBtn?.isVisible = false

//        }

        binding?.logoutBtn?.setOnClickListener {
            logOut()
        }

        binding?.savingsAccountBtn?.setOnClickListener {
            val intent = Intent(this, CustomerListSavingAccount::class.java)
            intent.putExtra("token","$token")
            intent.putExtra("agentId","$agentId")
          startActivity(intent)
        }


        binding?.constLayout?.setOnClickListener {
            binding?.logoutBtn?.isVisible = false
            binding?.savingsAccountBtn?.isVisible= false
//            binding?.customListBtn?.isVisible = false
        }

        binding?.personalLoanBtn?.setOnClickListener {
            val intent = Intent(this, PersonalEmiStatus::class.java)
            intent.putExtra("token","$token")
            intent.putExtra("agentId","$agentId")
            startActivity(intent)
            binding?.logoutBtn?.isVisible = false
//            binding?.customListBtn?.isVisible = false
        }

        binding?.groupLoanBtn?.setOnClickListener {
            val intent = Intent(this,  GroupCollectionType::class.java)
            intent.putExtra("token","$token")
            intent.putExtra("agentId","$agentId")
            startActivity(intent)
            binding?.logoutBtn?.isVisible = false
//            binding?.customListBtn?.isVisible = false

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

    override fun onResume() {
        super.onResume()
        recentLoan()
    }

    private fun recentLoan(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        val type = "Agent"

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
//        val call = apiClient?.postDataGET2(token.toString(),"v1/loans/getRecentLoanDetails/$agentId")
        val call = apiClient?.getTwoHeadersWithTokenData(token.toString(),type,"v1/loans/getRecentLoanDetails/$agentId")
        call?.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful){
                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
                    binding?.recentLoanRv?.isVisible = true
                    val res = response.body()
                    val list = ArrayList<RecentLoanData>()

                    var recyclerView : RecentLoanRV? = null
                    var totalGroupMember = ""
                    var groupName = ""
                    var groupHeadMobile = ""
                    var name  = ""
                    var mobile = ""

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.get("status")
                    val items = jsonObject.get("items")

                    Log.e("JsonArray1","$res respones")

                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray


                    if(status == true){
                        for (i in 0 until jsonArray.length()){
                            val loanType = jsonArray.getJSONObject(i).getString("loanType")
                            val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                            val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                            val disburseDate = jsonArray.getJSONObject(i).getString("disburseDate")
                            val emiAmount = jsonArray.getJSONObject(i).getString("collectedAmount")

                            val customerDetail = jsonArray.getJSONObject(i).getString("CustomerDetail")
                            val jsonArrayPL = JSONTokener(customerDetail.toString()).nextValue() as JSONArray
                            for (j in 0 until  jsonArrayPL.length()){
                                     name = jsonArrayPL.getJSONObject(j).getString("name")
                                     mobile = jsonArrayPL.getJSONObject(j).getString("phone")
                            }

                            val groupDetail = jsonArray.getJSONObject(i).getString("GroupDetail")
                            val jsonArrayGL = JSONTokener(groupDetail.toString()).nextValue() as JSONArray
                            for (k in 0 until jsonArrayGL.length()){
                                    totalGroupMember = jsonArrayGL.getJSONObject(k).getString("totalGroupMember")
                            }

                            val leaderDetails = jsonArray.getJSONObject(i).getString("LeaderDetail")
                            val jsonArrayLeaderDetails = JSONTokener(leaderDetails.toString()).nextValue() as JSONArray
                            for (l in 0 until jsonArrayLeaderDetails.length()){
                                    groupName = jsonArrayLeaderDetails.getJSONObject(l).getString("groupName")
                                    groupHeadMobile = jsonArrayLeaderDetails.getJSONObject(l).getString("groupLeaderMobile")
                            }

                            list.add(RecentLoanData(name,mobile,collectionType,emiAmount,loanAmount,disburseDate,totalGroupMember,groupHeadMobile,groupName,loanType))

                            binding?.recentLoanRv?.layoutManager = LinearLayoutManager(this@LoansType)
                            recyclerView = RecentLoanRV(list)
                            binding?.recentLoanRv?.adapter = recyclerView
                            recyclerView.notifyDataSetChanged()
                        }
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }
        })

    }

    private fun logOut(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        val type = " Agent"

        val json = JsonObject()
        json.addProperty("agentId","$agentId")

        @Suppress("DEPRECATION")
        val jsonObject: RequestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), json.toString())

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.postTwoHeadersWithTokenData(token.toString(),type,"v1/agent/agent/logout", jsonObject)
        call?.enqueue(object  : Callback<JsonObject> {
            @SuppressLint("CommitPrefEdits")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    forLoginPage()
                    val sharedPreferences = getSharedPreferences("VYuvan_Collector", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("token","")
                    editor.putString("agentId","")
                    editor.apply()
                    finish()
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("urvashi", "$t your response is fail")
            }
        })
    }

    private fun forLoginPage(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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