package com.vyuvancollectors.PersonalLoan.PersonalLoanSearch

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.databinding.ActivityRecyclerViewForPhoneSearchBinding

class RecyclerViewForPhoneSearch(private val list :List<Data>) : RecyclerView.Adapter<RecyclerViewForPhoneSearch.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityRecyclerViewForPhoneSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context

        val name = list[position].name
        val phone =list[position].phone
        val customerId = list[position].customerId
        val loanAmount = list[position].loanAmount
        val collectionType = list[position].collectionType
        val disburseDate = list[position].disburse
        val collectedAmount = list[position].emiAmount
        val agentId = list[position].agentId
        val token = list[position].token
        val totalInterest = list[position].totalInterest
        val totalAmount = list[position].totalAmount

        holder.binding.NameTxt.text = "Name: $name"
        holder.binding.mobileTxt.text = "Mobile: $phone"
        holder.binding.loanAmountTxt.text = "LoanAmount: $loanAmount"
        holder.binding.collectionTypeTxt.text = "CollectionType: $collectionType"

        holder.binding.mobileTxt.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            context.startActivity(intent)
        }

        holder.binding.ll1.setOnClickListener {
            val intent = Intent(context, SearchLoanDetails::class.java)
            intent.putExtra("name",name)
            intent.putExtra("phone",phone)
            intent.putExtra("customerId",customerId)
            intent.putExtra("loanAmount",loanAmount)
            intent.putExtra("collectionType",collectionType)
            intent.putExtra("disburseDate",disburseDate)
            intent.putExtra("token",token)
            intent.putExtra("agentId",agentId)
            intent.putExtra("totalInterest",totalInterest)
            intent.putExtra("totalAmount",totalAmount)
            intent.putExtra("collectedAmount",collectedAmount)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: ActivityRecyclerViewForPhoneSearchBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}