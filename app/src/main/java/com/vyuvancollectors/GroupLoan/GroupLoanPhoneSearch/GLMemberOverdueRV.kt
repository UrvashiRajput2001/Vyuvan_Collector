package com.vyuvancollectors.GroupLoan.GroupLoanPhoneSearch

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.vyuvancollectors.GroupLoan.GroupMemberList.AllMemberList
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.OverDataByCollectionType.AllMemberOverDueData
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityCashBoxBinding
import com.vyuvancollectors.databinding.ActivityGlmemberOverdueRvBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class GLMemberOverdueRV(private val list :List<AllMemberOverDueData>) : RecyclerView.Adapter<GLMemberOverdueRV.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityGlmemberOverdueRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: GLMemberOverdueRV.MyViewHolder, position: Int) {
        val context = holder.itemView.context

        holder.binding.progressBar.isVisible = false

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val lastdateCollected = sdf.format(Date())

        val status = list[position].status
        val collectionType = list[position].collectionType
        val emiAmount = list[position].emiAmount
        val agentId = list[position].agentId
        val loanId = list[position].loanId
        val emiId = list[position].emiId
        val customerId = list[position].customerId
        val token = list[position].token
        val totalGroupMember = list[position].totalGroupMember
        val groupName = list[position].groupName
        val groupId = list[position].groupId
        val date = list[position].dateOfCollect
        val phone = list[position].phone
        val paidEMIAmount = list[position].paidEmiAmount

        holder.binding.memberNameTxt.text = list[position].name
        holder.binding.mobileTxt.text = "Mobile : $phone"
        holder.binding.emiTxt.text = "EMI : $emiAmount"
        holder.binding.collectionTypeTxt.text = "Type : $collectionType"
        holder.binding.paidEmiTxt.text = "PaidEmiAmount : $paidEMIAmount"
        holder.binding.dateTxt.text = "Date : $date"


        holder.binding.mobileTxt.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            context.startActivity(intent)
        }


        if (status == "1"){
            holder.binding.collectEmiBtn.isVisible = false
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
                holder.binding.collectEmiBtn.isVisible = false
                holder.binding.progressBar.isVisible = true
                val paymentMethod = "CASH"
                val typeAgent = "Agent"
                val json = JsonObject()
                json.addProperty("emiId","$emiId")
                json.addProperty("emiAmount","$cashEditText")
                json.addProperty("customerId","$customerId")
                json.addProperty("loanId","$loanId")
                json.addProperty("agentId","$agentId")
                json.addProperty("collectionType","$collectionType")
                json.addProperty("paymentMethod","$paymentMethod")
                json.addProperty("lastdateCollected","$lastdateCollected")

                @Suppress("DEPRECATION")
                val jsonObject: RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(token,typeAgent,"v1/emi/emiollect",jsonObject)
                call?.enqueue(object  : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val res = response.body()
                        Toast.makeText(context, "Emi Collect Successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(context1, AllMemberList::class.java)
                        intent.putExtra("token","$token")
                        intent.putExtra("agentId","$agentId")
                        intent.putExtra("totalGroupMember","$totalGroupMember")
                        intent.putExtra("groupName","$groupName")
                        intent.putExtra("groupId","$groupId")
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
                bind.onlineMessageOkBtn.isVisible = false
                bind.progressBarOnline.isVisible = true
                holder.binding.collectEmiBtn.isVisible = false
                holder.binding.progressBar.isVisible = true
                val paymentMethod = "ONLINE"
                val typeAgent = "Agent"

                val json = JsonObject()
                json.addProperty("emiId","$emiId")
                json.addProperty("emiAmount","$onlineEditText")
                json.addProperty("customerId","$customerId")
                json.addProperty("loanId","$loanId")
                json.addProperty("agentId","$agentId")
                json.addProperty("collectionType","$collectionType")
                json.addProperty("paymentMethod","$paymentMethod")
                json.addProperty("lastdateCollected","$lastdateCollected")

                @Suppress("DEPRECATION")
                val jsonObject: RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(token,typeAgent,"v1/emi/emiollect",jsonObject)
                call?.enqueue(object  : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val res = response.body()
                        Toast.makeText(context, "Emi Collect Successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(context1, AllMemberList::class.java)
                        intent.putExtra("token","$token")
                        intent.putExtra("agentId","$agentId")
                        intent.putExtra("totalGroupMember","$totalGroupMember")
                        intent.putExtra("groupName","$groupName")
                        intent.putExtra("groupId","$groupId")
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
                holder.binding.collectEmiBtn.isVisible = false
                holder.binding.progressBar.isVisible = true
                val paymentMethod = "BARCODE"
                val typeAgent = "Agent"

                val json = JsonObject()
                json.addProperty("emiId","$emiId")
                json.addProperty("emiAmount","$barcodeEditText")
                json.addProperty("customerId","$customerId")
                json.addProperty("loanId","$loanId")
                json.addProperty("agentId","$agentId")
                json.addProperty("collectionType","$collectionType")
                json.addProperty("paymentMethod","$paymentMethod")
                json.addProperty("lastdateCollected","$lastdateCollected")

                @Suppress("DEPRECATION")
                val jsonObject: RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postTwoHeadersWithTokenData(token,typeAgent,"v1/emi/emiollect",jsonObject)
                call?.enqueue(object  : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val res = response.body()
                        Toast.makeText(context, "Emi Collect Successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(context1, AllMemberList::class.java)
                        intent.putExtra("token","$token")
                        intent.putExtra("agentId","$agentId")
                        intent.putExtra("totalGroupMember","$totalGroupMember")
                        intent.putExtra("groupName","$groupName")
                        intent.putExtra("groupId","$groupId")
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

    inner class MyViewHolder(var binding: ActivityGlmemberOverdueRvBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}