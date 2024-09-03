package com.vyuvancollectors.AddCustomer

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.databinding.ActivityRvCustomerListBinding

class RvCustomerList(private val list :List<CustomerDetialsData>) : RecyclerView.Adapter<RvCustomerList.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityRvCustomerListBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context

        val mobile = list[position].phone
        val whatsapp = list[position].whatsappNo

        holder.binding.NameTxt.text = list[position].name
        holder.binding.mobileTxt.text = list[position].phone
        holder.binding.emailTxt.text = list[position].email
        holder.binding.whatsappTxt.text = list[position].whatsappNo
        holder.binding.addressTxt.text = list[position].address
        holder.binding.aadharNoTxt.text = list[position].aadharNo


        holder.binding.mobileTxt.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$mobile")
            context.startActivity(intent)
        }

        holder.binding.whatsappTxt.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$whatsapp")
            context.startActivity(intent)
        }


    }
    override fun getItemCount(): Int {
        return list.size
    }
    inner class MyViewHolder(var binding: ActivityRvCustomerListBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}