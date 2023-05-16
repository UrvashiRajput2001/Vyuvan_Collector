package com.vyuvancollector.AddCustomer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollector.R
import com.vyuvancollector.databinding.ActivityRvCustomerListBinding

class RvCustomerList(private val list :List<CustomerDetialsData>) : RecyclerView.Adapter<RvCustomerList.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityRvCustomerListBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context

        holder.binding.NameTxt.text = list[position].name
        holder.binding.mobileTxt.text = list[position].phone
        holder.binding.emailTxt.text = list[position].email
        holder.binding.whatsappTxt.text = list[position].whatsappNo
        holder.binding.addressTxt.text = list[position].address
    }
    override fun getItemCount(): Int {
        return list.size
    }
    inner class MyViewHolder(var binding: ActivityRvCustomerListBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}