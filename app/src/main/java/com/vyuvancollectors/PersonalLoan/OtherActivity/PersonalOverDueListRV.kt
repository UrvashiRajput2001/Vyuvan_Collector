package com.vyuvancollectors.PersonalLoan.OtherActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.R
import com.vyuvancollectors.databinding.ActivityPersonalOverDueListRvBinding
import com.vyuvancollectors.databinding.ActivityPersonalPaidEmilistRvBinding
import java.text.SimpleDateFormat
import java.util.*

class PersonalOverDueListRV(private val list :List<PersonalPaidEMIListDataClass>) : RecyclerView.Adapter<PersonalOverDueListRV.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ActivityPersonalOverDueListRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonalOverDueListRV.MyViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.binding.progressBar.isVisible = false
        val typeAgent = "Agent"

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val lastdateCollected: String = sdf.format(Date())

        val name = list[position].name
        val mobile = list[position].mobile
        val emiId = list[position].emiId
        val doc = list[position].doc
        val loanAmount = list[position].loanAmount
        val emiAmount = list[position].emiAmount
        val remainingAmount = list[position].remainingAmount
        val customerId = list[position].customerId
        val collectionType = list[position].collectionType
        val loanId = list[position].loanId
        val agentId = list[position].agentId
        val token = list[position].token
        val emiStatus = list[position].emiStatus
        val emiNum = list[position].emiNo

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





    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: ActivityPersonalOverDueListRvBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {}
}