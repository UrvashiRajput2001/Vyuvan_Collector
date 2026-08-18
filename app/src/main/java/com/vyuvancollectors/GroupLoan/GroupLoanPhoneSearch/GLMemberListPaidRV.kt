package com.vyuvancollectors.GroupLoan.GroupLoanPhoneSearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PaidDataByCT.AllMemberPaidData
import com.vyuvancollectors.databinding.ActivityGlmemberListPaidRvBinding

class GLMemberListPaidRV(private val list :List<AllMemberPaidData>) : RecyclerView.Adapter<GLMemberListPaidRV.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityGlmemberListPaidRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GLMemberListPaidRV.MyViewHolder, position: Int) {
        val context = holder.itemView.context

        val paidEMiAmount = list[position].paidEmiAmount
        val collectionType = list[position].collectionType
        val doc = list[position].dateOfCollect
        val lastdatecollect = list[position].lastDateCollect

        holder.binding.memberNameTxt.text = list[position].name
        holder.binding.emiTxt.text = "EMI : $paidEMiAmount"
        holder.binding.collectionTypeTxt.text = "Type : $collectionType"
        holder.binding.dateTxt.text = "DateOfCollect : $doc"
        holder.binding.lastDateOfCollectTxt.text = "LastDateCollect : $lastdatecollect"

        val status = list[position].status

        if (status == "1"){
            holder.binding.collectEmiBtn.isVisible = false
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: ActivityGlmemberListPaidRvBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}