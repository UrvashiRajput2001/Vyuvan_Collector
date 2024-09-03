package com.vyuvancollectors.SavingAccount

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.gson.JsonObject
import com.vyuvancollectors.Activity.LoansType
import com.vyuvancollectors.PersonalLoan.TypesOfEMI.Monthly
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.SavingAccount.Spinner.InvestSpinnerData
import com.vyuvancollectors.SavingAccount.Spinner.PeriodSpinnerData
import com.vyuvancollectors.databinding.ActivitySavingAccountFormBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class SavingAccountForm : AppCompatActivity() {

    private var binding : ActivitySavingAccountFormBinding? = null
    var invest = ArrayList<String>()
    var period = ArrayList<String>()

    val periodSpinnerData = ArrayList<PeriodSpinnerData>()
    val investSpinnerData = ArrayList<InvestSpinnerData>()
//    var period : List<SpinnerData>? = null

    private var periodString : String?  = null
    private var  investString: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySavingAccountFormBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        val bundle = intent.extras!!
        @Suppress("DEPRECATION")
        val token = bundle.get("token").toString()
        @Suppress("DEPRECATION")
        val customerId = bundle.get("customerId").toString()
        @Suppress("DEPRECATION")
        val agentId = bundle.get("agentId").toString()

        val type = "Agent"



        binding?.submitBtn?.setOnClickListener {
            if (binding?.applicationNoTxt?.text?.length == 0){
                binding?.submitBtn?.isEnabled = false
                binding?.textView1?.setTextColor(resources.getColor(com.vyuvancollectors.R.color.red))
                binding?.textView2?.setTextColor(resources.getColor(com.vyuvancollectors.R.color.grey))
                binding?.textView3?.setTextColor(resources.getColor(com.vyuvancollectors.R.color.grey))
            }else  if (binding?.investSpinner?.selectedItem == 0) {
                binding?.submitBtn?.isEnabled = false
                binding?.textView2?.setTextColor(resources.getColor(com.vyuvancollectors.R.color.red))
                binding?.textView1?.setTextColor(resources.getColor(com.vyuvancollectors.R.color.grey))
                binding?.textView3?.setTextColor(resources.getColor(com.vyuvancollectors.R.color.grey))
            }else if (binding?.periodSpinner?.selectedItem == 0){
                binding?.submitBtn?.isEnabled = false
                binding?.textView3?.setTextColor(resources.getColor(com.vyuvancollectors.R.color.red))
                binding?.textView1?.setTextColor(resources.getColor(com.vyuvancollectors.R.color.grey))
                binding?.textView2?.setTextColor(resources.getColor(com.vyuvancollectors.R.color.grey))
            }else{
                binding?.applicationNoTxt?.setText("")
                binding?.investSpinner?.setSelection(0)
                binding?.periodSpinner?.setSelection(0)
                binding?.submitBtn?.isEnabled = true
            }
            binding?.submitBtn?.isEnabled = true
        }


//        val paymentMode = arrayOf(
//            "Select Mode",
//            "Cash",
//            "Online",
//        )
//
//     seletedPaymentMode = paymentMode.toList()
//
//        val spinnerArrayAdapter = object : ArrayAdapter<String>(
//            this, R.layout.simple_spinner_item, seletedPaymentMode!!
//        ) {
//            override fun isEnabled(position: Int): Boolean {
//                return position != 0
//            }
//
//            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
//                val view = super.getDropDownView(position, convertView, parent)
//                val tv = view as TextView
//                if (position == 0) {
//                    // Set the hint text color gray
//                    tv.setTextColor(Color.GRAY)
//                } else {
//                    tv.setTextColor(Color.WHITE)
//                }
//                return view
//            }
//        }
//
//        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_item)
//        binding?.paymentModeSpinner?.adapter = spinnerArrayAdapter
//
//        binding?.paymentModeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val selectedItemText = parent?.getItemAtPosition(position) as? String
//                if (parent != null) {
//                    (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
//                }
//                if (position > 0 && selectedItemText != null) {
//                    // Notify the selected item text
//                    Toast.makeText(
//                        applicationContext,
//                        "Selected : $selectedItemText",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                // Handle case where nothing is selected
//            }
//        }




        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(token,type,"v1/saving/savingAccount/getInterestList")
        call?.enqueue(object  : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val res = response.body()

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.get("status")
                    val message = jsonObject.get("message")
                    val items = jsonObject.get("items")

                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                    Log.e("Amount Range"," amountrange $res")
                    period.add("Select Period")

                    if (status == true) {
                        for (i in 0 until jsonArray.length()) {
                            val year = jsonArray.getJSONObject(i).getString("year")
                            val period_id = jsonArray.getJSONObject(i).getString("_id")
                            Log.e("Amount Range"," year $year")
                            period.add("$year")
                            periodSpinnerData.add(PeriodSpinnerData(period_id,year))
                           // period.add(1,"$year")

                            val listp = period.toList()

                            val spinnerArrayAdapter = object : ArrayAdapter<String>(
                                this@SavingAccountForm, R.layout.simple_spinner_item, listp!!
                            ) {
                                override fun isEnabled(position: Int): Boolean {
                                    return position != 0
                                }
                                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.WHITE)
                }
                return view
            }
                            }
                            spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_item)
                            binding?.periodSpinner?.adapter = spinnerArrayAdapter


                            binding?.periodSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    val selectedItemText = parent?.getItemAtPosition(position) as? String
                                    if (parent != null) {
                                        (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                                    }
                                    if (position > 0 && selectedItemText != null) {
                                        // Notify the selected item text
                                        periodString = periodSpinnerData[position]._id
                                    }
                                }
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    // Handle case where nothing is selected
                                }
                            }
                        }
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("urvashi", "$t your response is fail")
            }
        })



        val apiClient3 = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call3 = apiClient3?.getTwoHeadersWithTokenData(token,type,"v1/saving/savingAccount/getAmountRangeList")
        call3?.enqueue(object  : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {

                    val res = response.body()

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.get("status")
                    val message = jsonObject.get("message")
                    val items = jsonObject.get("items")

                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray
                    Log.e("Amount Range"," amountrange $res")
                    invest.add("Select Invest")


                    if (status == true) {
                        for (i in 0 until jsonArray.length()) {
                            val amount = jsonArray.getJSONObject(i).getString("amount")
                            val invest_id = jsonArray.getJSONObject(i).getString("_id")
                            val jsonArray2 = JSONTokener(amount.toString()).nextValue() as JSONArray
                             for(j in 0 until jsonArray2.length()){
                                 val max = jsonArray2.getJSONObject(j).getString("max")
                                 val min = jsonArray2.getJSONObject(j).getString("min")
                                 val investRangeAmount = "$min - $max"
                                 invest.add("$investRangeAmount")
                                 investSpinnerData.add(InvestSpinnerData(invest_id,investRangeAmount))

                             }


                            val spinnerArrayAdapter = object : ArrayAdapter<String>(
                                this@SavingAccountForm, R.layout.simple_spinner_item,invest
                            ) {
                                override fun isEnabled(position: Int): Boolean {
                                    return position != 0
                                }

                                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                    val view = super.getDropDownView(position, convertView, parent)
                                    val tv = view as TextView
                                    if (position == 0) {
                                        // Set the hint text color gray
                                        tv.setTextColor(Color.GRAY)
                                    } else {
                                        tv.setTextColor(Color.WHITE)
                                    }
                                    return view
                                }
                            }

                            spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_item)
                            binding?.investSpinner?.adapter = spinnerArrayAdapter

                            binding?.investSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    val selectedItemText = parent?.getItemAtPosition(position) as? String
                                    if (parent != null) {
                                        (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                                    }
                                    if (position > 0 && selectedItemText != null) {

                                       investString = periodSpinnerData[position]._id
                                    }
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    // Handle case where nothing is selected
                                }
                            }
                        }
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("urvashi", "$t your response is fail")
            }
        })



        binding?.submitBtn?.setOnClickListener {

            val json = JsonObject()
            json.addProperty("customerId","$customerId")
            json.addProperty("agentId","$agentId")
            json.addProperty("interestYearId","$periodString")
            json.addProperty("amountRangeId","$investString")

            Log.e("Submit","$customerId customerId $agentId agentId $investString interestYearId $periodString amountRangeId")

            @Suppress("DEPRECATION")
            val jsonObject: RequestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(), json.toString())

            val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
            val call = apiClient?.postTwoHeadersWithTokenData(token,type,"v1/saving/savingAccount/agentCreateSavingAccount",jsonObject)

            call?.enqueue(object : Callback<JsonObject> {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    val res = response.body()
                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.get("status")
                    val items = jsonObject.getJSONObject("items")

                    Log.e("res","$res  res")

                    if (status == true){
                        Toast.makeText(this@SavingAccountForm, "Emi Collect Successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@SavingAccountForm, LoansType::class.java)
                        intent.putExtra("token","$token")
                        intent.putExtra("agentId","$agentId")
                        startActivity(intent)
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("urvashi", "your response is fail")
                }
            })

        } }
}