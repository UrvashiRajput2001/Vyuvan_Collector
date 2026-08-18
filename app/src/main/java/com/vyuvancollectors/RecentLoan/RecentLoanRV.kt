package com.vyuvancollectors.RecentLoan

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.databinding.ActivityRecentLoanRvBinding

class RecentLoanRV(private val list :List<RecentLoanData>) : RecyclerView.Adapter<RecentLoanRV.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityRecentLoanRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat", "SdCardPath", "SetTextI18n")
    override fun onBindViewHolder(holder: RecentLoanRV.MyViewHolder, position: Int) {
        val context = holder.itemView.context


        val loanType = list[position].loanType

        val name = list[position].name
        val groupName = list[position].groupName
        val totalGroupMember = list[position].totalGroupMember
        val emiAmount = list[position].emiAmount
        val loanAmount = list[position].loanAmount
        val disburseDate = list[position].disburseDate
        val mobile = list[position].mobile
        val collectionType = list[position].collectionType

        holder.binding.collectionTypeTxt.text = collectionType
        holder.binding.loanAmountTxt.text = loanAmount
        holder.binding.disburseDateTxt.text = disburseDate



        if (loanType == "PL"){
            holder.binding.nameTxt.text = name
            holder.binding.mobileTxt.text = mobile
            holder.binding.emiAmountTxt.text = emiAmount
        }else if (loanType == "GL"){
            holder.binding.nameTxt.text = "$groupName Group"
            holder.binding.mobileTxt.text = "Total $totalGroupMember member"
            holder.binding.emiAmountTxt.text = "$emiAmount each members"
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: ActivityRecentLoanRvBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}