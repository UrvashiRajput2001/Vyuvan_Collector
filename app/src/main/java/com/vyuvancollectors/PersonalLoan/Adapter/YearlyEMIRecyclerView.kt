package com.vyuvancollectors.PersonalLoan.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.PersonalLoan.CustomerLoanDetails.LoanDetailsYearly
import com.vyuvancollectors.PersonalLoan.Data_Class.ModalYearly
import com.vyuvancollectors.R
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityCashBoxBinding
import com.vyuvancollectors.databinding.ActivityYearlyEmirecyclerViewBinding
import com.google.gson.JsonObject
import com.vyuvancollectors.PersonalLoan.TypesOfEMI.Yearly
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class YearlyEMIRecyclerView(private val list :List<ModalYearly>) : RecyclerView.Adapter<YearlyEMIRecyclerView.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityYearlyEmirecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat", "SdCardPath", "SetTextI18n")
    override fun onBindViewHolder(holder: YearlyEMIRecyclerView.MyViewHolder, position: Int) {
        val context = holder.itemView.context

        val typeAgent = "Agent"

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val lastdateCollected: String = sdf.format(Date())

        val name = list[position].name
        val mobile = list[position].mobile
        val emiId = list[position].emiId
        val loanAmount = list[position].loanAmount
        val emiAmount = list[position].emiAmount
        val emiNum = list[position].emiNo
        val remainingAmount = list[position].remainingAmount
        val customerId = list[position].customerId
        val collectionType = list[position].collectionType
        val loanId = list[position].loanId
        val agentId = list[position].agentId
        val token = list[position].token
        val typo = list[position].typo
        val emiStatus = list[position].emiStatus
        val doc = list[position].doc

        holder.binding.NameTxt.text = name
        holder.binding.mobileTxt.text = "Mobile : $mobile"
        holder.binding.collectedAccountTxt.text = "EMI : $emiAmount"
        holder.binding.dateTxt.text = "Date : $doc"
        holder.binding.emiNumTxt.text = "EMI No.: $emiNum"

        if(emiStatus == "1"){
            holder.binding.collectEmiBtn.text = "Paid"
            holder.binding.collectEmiBtn.isEnabled = false
            ContextCompat.getDrawable(context, R.drawable.ellipse_2)
                ?.let { holder.binding.collectEmiBtn.background = it }
            Log.e("Monthly Emi","$emiStatus loop wala emi status")
        }else{
            holder.binding.collectEmiBtn.text = "Collect"
            ContextCompat.getDrawable(context, R.drawable.ellipse)
                ?.let { holder.binding.collectEmiBtn.background = it }
        }

        holder.binding.ll1.setOnClickListener{
            val intent = Intent(context, LoanDetailsYearly::class.java)

            intent.putExtra("name",name)
            intent.putExtra("mobile",mobile)
            intent.putExtra("emiId",emiId)
            intent.putExtra("loanAmount",loanAmount)
            intent.putExtra("emiAmount",emiAmount)
            intent.putExtra("remainingAmount",remainingAmount)
            intent.putExtra("customerId",customerId)
            intent.putExtra("collectionType",collectionType)
            intent.putExtra("token",token)
            intent.putExtra("loanId",loanId)
            intent.putExtra("doc",lastdateCollected)
            intent.putExtra("agentId",agentId)
            context.startActivity(intent)
        }

        holder.binding.collectEmiBtn.setOnClickListener {

            val context1 = holder.itemView.context

            val dialog = AlertDialog.Builder(context1)
            var bind : ActivityCashBoxBinding? = null
            bind = ActivityCashBoxBinding.inflate(LayoutInflater.from(context1))

            val alert = dialog.create()
            alert.setView(bind.root)

            bind.cashTxt.setText("$emiAmount")
            val cashEditText = bind.cashTxt.text

            bind.barcodeCashTxt.setText("$emiAmount")
            val barcodeEditText = bind.barcodeCashTxt.text

            bind.onlineCashTxt.setText("$emiAmount")
            val onlineEditText = bind.onlineCashTxt.text



            bind.layoutForButtons.isVisible = true
            bind.cashBoxLayout.isVisible = false
            bind.cashMsgLayout.isVisible = false
            bind.barcodeLayout.isVisible = false
            bind.barcodeMsgBoxLayout.isVisible = false
            bind.barcodeBoxLayout.isVisible = false
            bind.onlineMsgBoxLayout.isVisible = false
            bind.onlineLayout.isVisible = false
            bind.backBtn.isVisible = false

            bind.backBtn.setOnClickListener {

                bind.layoutForButtons.isVisible = true
                bind.cashBoxLayout.isVisible = false
                bind.cashMsgLayout.isVisible = false
                bind.barcodeLayout.isVisible = false
                bind.barcodeMsgBoxLayout.isVisible = false
                bind.barcodeBoxLayout.isVisible = false
                bind.onlineMsgBoxLayout.isVisible = false
                bind.onlineLayout.isVisible = false
                bind.backBtn.isVisible = false

            }

            bind.cashBtn.setOnClickListener {
                bind.layoutForButtons.isVisible = false
                bind.cashBoxLayout.isVisible = true
                bind.cashMsgLayout.isVisible = false
                bind.barcodeLayout.isVisible = false
                bind.barcodeMsgBoxLayout.isVisible = false
                bind.barcodeBoxLayout.isVisible = false
                bind.onlineMsgBoxLayout.isVisible = false
                bind.onlineLayout.isVisible = false
                bind.backBtn.isVisible = true

                Log.e("currentCoroutineContext","$lastdateCollected")
            }

            bind.cashPayBtn.setOnClickListener {
                bind.layoutForButtons.isVisible = false
                bind.cashBoxLayout.isVisible = false
                bind.cashMsgLayout.isVisible = true
                bind.barcodeLayout.isVisible = false
                bind.barcodeMsgBoxLayout.isVisible = false
                bind.barcodeBoxLayout.isVisible = false
                bind.onlineMsgBoxLayout.isVisible = false
                bind.onlineLayout.isVisible = false
                bind.progressBarCash.isVisible = false
            }

            bind.cashMsgOkBtn.setOnClickListener {
                bind.progressBarCash.isVisible = true
                bind.cashMsgOkBtn.isVisible = false
                val paymentMethod = "CASH"
                val json = JsonObject()
                json.addProperty("emiId","$emiId")
                json.addProperty("emiAmount","$cashEditText")
                json.addProperty("customerId","$customerId")
                json.addProperty("loanId","$loanId")
                json.addProperty("agentId","$agentId")
                json.addProperty("collectionType","$collectionType")
                json.addProperty("paymentMethod","$paymentMethod")
                json.addProperty("lastdateCollected","$lastdateCollected")

                Log.e("urvashi","$emiId emiId")
                Log.e("urvashi","$emiAmount emiAmount cashEditText $cashEditText")
                Log.e("urvashi","$customerId customerId")
                Log.e("urvashi","$loanId loanId")
                Log.e("urvashi","$agentId agentId")
                Log.e("urvashi","$collectionType collectionType")
                Log.e("urvashi","$paymentMethod paymentMethod")
                Log.e("urvashi","$lastdateCollected lastdateCollected")

                @Suppress("DEPRECATION")
                val jsonObject: RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(token,typeAgent,"v1/emi/emiollect",jsonObject)
                call?.enqueue(object  : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val res = response.body()
                        Log.e("urvashi","$res res $paymentMethod paymentMethod")
                        Toast.makeText(context, "Emi Collect Successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(context1, Yearly::class.java)
                        intent.putExtra("token","$token")
                        intent.putExtra("agentId","$agentId")
                        intent.putExtra("typo","$typo")
                        context.startActivity(intent)
                        bind.progressBarCash.isVisible = false
                        (context as Activity).finish()
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("urvashi", "$t your response is fail")
                    }
                })
                alert.hide()
            }

            bind.cashCancelBtn.setOnClickListener {
                alert.dismiss()
            }


            bind.onlineBtn.setOnClickListener {
                bind.layoutForButtons.isVisible = false
                bind.cashBoxLayout.isVisible = false
                bind.cashMsgLayout.isVisible = false
                bind.barcodeLayout.isVisible = false
                bind.barcodeMsgBoxLayout.isVisible = false
                bind.barcodeBoxLayout.isVisible = false
                bind.onlineMsgBoxLayout.isVisible = false
                bind.onlineLayout.isVisible = true
                bind.backBtn.isVisible = true
            }

            bind.onlinePayBtn.setOnClickListener {
                bind.layoutForButtons.isVisible = false
                bind.cashBoxLayout.isVisible = false
                bind.cashMsgLayout.isVisible = false
                bind.barcodeLayout.isVisible = false
                bind.barcodeMsgBoxLayout.isVisible = false
                bind.barcodeBoxLayout.isVisible = false
                bind.onlineMsgBoxLayout.isVisible = true
                bind.onlineLayout.isVisible = false
                bind.progressBarOnline.isVisible = false

            }

            bind.onlineMessageCancelBtn.setOnClickListener {
                alert.dismiss()
            }

            bind.onlineMessageOkBtn.setOnClickListener {
                bind.progressBarOnline.isVisible = true
                bind.onlineMessageOkBtn.isVisible = false
                val paymentMethod = "ONLINE"

                val json = JsonObject()
                json.addProperty("emiId","$emiId")
                json.addProperty("emiAmount","$onlineEditText")
                json.addProperty("customerId","$customerId")
                json.addProperty("loanId","$loanId")
                json.addProperty("agentId","$agentId")
                json.addProperty("collectionType","$collectionType")
                json.addProperty("paymentMethod","$paymentMethod")
                json.addProperty("lastdateCollected","$lastdateCollected")

                Log.e("urvashi","$emiId emiId")
                Log.e("urvashi","$emiAmount emiAmount ouramount $onlineEditText")
                Log.e("urvashi","$customerId customerId")
                Log.e("urvashi","$loanId loanId")
                Log.e("urvashi","$agentId agentId")
                Log.e("urvashi","$collectionType collectionType")
                Log.e("urvashi","$paymentMethod paymentMethod")
                Log.e("urvashi","$lastdateCollected lastdateCollected")

                @Suppress("DEPRECATION")
                val jsonObject: RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(token,typeAgent,"v1/emi/emiollect",jsonObject)
                call?.enqueue(object  : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val res = response.body()
                        Log.e("urvashi","$res res $paymentMethod paymentMethod")
                        Toast.makeText(context, "Emi Collect Successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(context1, Yearly::class.java)
                        intent.putExtra("token","$token")
                        intent.putExtra("agentId","$agentId")
                        intent.putExtra("typo","$typo")
                        context.startActivity(intent)
                        bind.progressBarOnline.isVisible = false
                        (context as Activity).finish()
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("urvashi", "$t your response is fail")
                    }
                })
                alert.hide()
            }






            bind.BarcodeBtn.setOnClickListener {
                bind.layoutForButtons.isVisible = false
                bind.cashBoxLayout.isVisible = false
                bind.cashMsgLayout.isVisible = false
                bind.barcodeLayout.isVisible = true
                bind.barcodeMsgBoxLayout.isVisible = false
                bind.barcodeBoxLayout.isVisible = false
                bind.onlineMsgBoxLayout.isVisible = false
                bind.onlineLayout.isVisible = false
                bind.backBtn.isVisible = true
            }

            bind.barcodeOkBtn.setOnClickListener {

                bind.layoutForButtons.isVisible = false
                bind.cashBoxLayout.isVisible = false
                bind.cashMsgLayout.isVisible = false
                bind.barcodeLayout.isVisible = false
                bind.barcodeMsgBoxLayout.isVisible = false
                bind.barcodeBoxLayout.isVisible = true
                bind.onlineMsgBoxLayout.isVisible = false
                bind.onlineLayout.isVisible = false
                bind.backBtn.isVisible = true
            }

            bind.barcodePayBtn.setOnClickListener {
                bind.layoutForButtons.isVisible = false
                bind.cashBoxLayout.isVisible = false
                bind.cashMsgLayout.isVisible = false
                bind.barcodeLayout.isVisible = false
                bind.barcodeMsgBoxLayout.isVisible = true
                bind.barcodeBoxLayout.isVisible = false
                bind.onlineMsgBoxLayout.isVisible = false
                bind.onlineLayout.isVisible = false
                bind.backBtn.isVisible = true
                bind.progressBarBarcode.isVisible = false
            }

            bind.barcodeCancelBtn.setOnClickListener {
                alert.dismiss()
            }

            bind.barcodeMsgOkBtn.setOnClickListener {
                bind.progressBarBarcode.isVisible = true
                bind.barcodeMsgOkBtn.isVisible = false
                val paymentMethod = "BARCODE"

                val json = JsonObject()
                json.addProperty("emiId","$emiId")
                json.addProperty("emiAmount","$barcodeEditText")
                json.addProperty("customerId","$customerId")
                json.addProperty("loanId","$loanId")
                json.addProperty("agentId","$agentId")
                json.addProperty("collectionType","$collectionType")
                json.addProperty("paymentMethod","$paymentMethod")
                json.addProperty("lastdateCollected","$lastdateCollected")

                Log.e("urvashi","$emiId emiId")
                Log.e("urvashi","$emiAmount emiAmount  ourAmount $barcodeEditText")
                Log.e("urvashi","$customerId customerId")
                Log.e("urvashi","$loanId loanId")
                Log.e("urvashi","$agentId agentId")
                Log.e("urvashi","$collectionType collectionType")
                Log.e("urvashi","$paymentMethod paymentMethod")
                Log.e("urvashi","$lastdateCollected lastdateCollected")

                @Suppress("DEPRECATION")
                val jsonObject: RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(token,typeAgent,"v1/emi/emiollect",jsonObject)
                call?.enqueue(object  : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val res = response.body()
                        Log.e("urvashi","$res res $paymentMethod paymentMethod")
                        Toast.makeText(context, "Emi Collect Successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(context1, Yearly::class.java)
                        intent.putExtra("token","$token")
                        intent.putExtra("agentId","$agentId")
                        intent.putExtra("typo","$typo")
                        context.startActivity(intent)
                        bind.progressBarBarcode.isVisible = false
                        (context as Activity).finish()
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("urvashi", "$t your response is fail")
                    }
                })
                alert.hide()
            }


            alert.show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: ActivityYearlyEmirecyclerViewBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}