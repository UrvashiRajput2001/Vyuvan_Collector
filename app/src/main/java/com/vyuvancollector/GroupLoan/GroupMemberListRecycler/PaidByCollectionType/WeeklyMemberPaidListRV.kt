package com.vyuvancollector.GroupLoan.GroupMemberListRecycler.PaidByCollectionType

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollector.GroupLoan.GroupMemberListRecycler.OverDataByCollectionType.MonthlyMemberOverDueData
import com.vyuvancollector.GroupLoan.GroupMemberListRecycler.OverDataByCollectionType.WeeklyMemberOverDueData
import com.vyuvancollector.GroupLoan.GroupMemberListRecycler.OverDueByCollectionType.MonthlyMemberOverDueListRV
import com.vyuvancollector.GroupLoan.GroupMemberListRecycler.PaidDataByCT.WeeklyMemberPaidData
import com.vyuvancollector.databinding.ActivityMonthlyMemberOverDueListRvBinding
import com.vyuvancollector.databinding.ActivityWeeklyMemberPaidListRvBinding

class WeeklyMemberPaidListRV(private val list :List<WeeklyMemberPaidData>) : RecyclerView.Adapter<WeeklyMemberPaidListRV.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityWeeklyMemberPaidListRvBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeeklyMemberPaidListRV.MyViewHolder, position: Int) {
        val context = holder.itemView.context

        val paidEMiAmount = list[position].paidEmiAmount
        val collectionType = list[position].collectionType
        val doc = list[position].doc
        val lastdatecollect = list[position].lastDateCollect

        holder.binding.memberNameTxt.text = list[position].name
        holder.binding.emiTxt.text = "EMI : $paidEMiAmount"
        holder.binding.collectionTypeTxt.text = "Type : $collectionType"
        holder.binding.dateTxt.text = "DateOfCollect : $doc"
        holder.binding.lastDateOfCollectTxt.text = "LastDateCollect : $lastdatecollect"

        val status = list[position].status

        if (status == "1") {
            holder.binding.collectEmiBtn.isVisible = false
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: ActivityWeeklyMemberPaidListRvBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {}
}