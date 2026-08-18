package com.vyuvancollectors.GroupLoan.OtherActivity

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.R
import com.vyuvancollectors.databinding.ActivityGroupCollectedAmountRvBinding
import java.text.SimpleDateFormat
import java.util.*

class GroupCollectedAmountRv(private val list :List<GroupCollectedAmountData>) : RecyclerView.Adapter<GroupCollectedAmountRv.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ActivityGroupCollectedAmountRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupCollectedAmountRv.MyViewHolder, position: Int) {
        val context = holder.itemView.context

        val typeAgent = "Agent"

        val emiStatus = list[position].emiStatus
        val emiAmount = list[position].emiAmount
        val token = list[position].token
        val emiId = list[position].emiId
        val customerId = list[position].customerId
        val loanId = list[position].loanId
        val agentId = list[position].agentId
        val collectionType = "Monthly"

        val phone = list[position].mobile
        val whatsapp = list[position].whatsapp

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val lastdateCollected: String = sdf.format(Date())

        holder.binding.nameTxt.text = "${list[position].name}"
        holder.binding.mobileTxt.text ="Mobile: ${list[position].mobile}"
        holder.binding.whatsappTxt.text = "Whatsapp: ${list[position].whatsapp}"
        holder.binding.emiNumTxt.text= "Emi No: ${list[position].emiNo}"
        holder.binding.collectedAccountTxt.text = "EMI: ${list[position].emiAmount}"
        holder.binding.dateTxt.text = "Date: ${list[position].lastDateOfCollect}"
        holder.binding.groupNameTxt.text = "Group: ${list[position].groupName}"

        holder.binding.mobileTxt.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            context.startActivity(intent)
        }

        holder.binding.whatsappTxt.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$whatsapp")
            context.startActivity(intent)
        }


        if(emiStatus == "1"){
            holder.binding.collectEmiBtn.text = "Paid"
            holder.binding.collectEmiBtn.isEnabled = false
            ContextCompat.getDrawable(context, R.drawable.ellipse_2)
                ?.let { holder.binding.collectEmiBtn.background = it }
        }else{
            holder.binding.collectEmiBtn.text = "Collect"
            ContextCompat.getDrawable(context, R.drawable.ellipse)
                ?.let { holder.binding.collectEmiBtn.background = it }
        }



    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: ActivityGroupCollectedAmountRvBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {}
}