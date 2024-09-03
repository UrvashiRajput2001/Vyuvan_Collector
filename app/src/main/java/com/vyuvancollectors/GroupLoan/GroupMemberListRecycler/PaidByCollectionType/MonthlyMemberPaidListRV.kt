package com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PaidByCollectionType

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PaidDataByCT.MonthlyMemberPaidData
import com.vyuvancollectors.R
import com.vyuvancollectors.databinding.ActivityMonthlyMemberPaidListRvBinding

class MonthlyMemberPaidListRV(private val list :List<MonthlyMemberPaidData>) : RecyclerView.Adapter<MonthlyMemberPaidListRV.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityMonthlyMemberPaidListRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonthlyMemberPaidListRV.MyViewHolder, position: Int) {
        val context = holder.itemView.context

        val paidEMiAmount = list[position].paidEmiAmount
        val collectionType = list[position].collectionType
        val doc = list[position].doc
        val lastdatecollect = list[position].lastDateCollect
        val phone = list[position].phone
        val emiNo = list[position].emiNo

        holder.binding.memberNameTxt.text = list[position].name
        holder.binding.emiTxt.text = "EMI: $paidEMiAmount"
        holder.binding.collectionTypeTxt.text = "Type: $collectionType"
        holder.binding.dateTxt.text = "DateOfCollect: $doc"
        holder.binding.lastDateOfCollectTxt.text = "LastDateCollect: $lastdatecollect"
        holder.binding.mobileTxt.text ="Mobile: $phone"
        holder.binding.emiNumTxt.text = "EMI No. : $emiNo"

        val status = list[position].status

        holder.binding.mobileTxt.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            context.startActivity(intent)
        }

        if(status == "1"){
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

    inner class MyViewHolder(var binding: ActivityMonthlyMemberPaidListRvBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}