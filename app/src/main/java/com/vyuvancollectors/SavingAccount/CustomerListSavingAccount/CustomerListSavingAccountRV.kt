package com.vyuvancollectors.SavingAccount.CustomerListSavingAccount

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.AddCustomer.CustomerDetialsData
import com.vyuvancollectors.AddCustomer.RvCustomerList
import com.vyuvancollectors.PersonalLoan.CustomerLoanDetails.LoanDetailsMonthly
import com.vyuvancollectors.SavingAccount.SavingAccountForm
import com.vyuvancollectors.databinding.ActivityCustomerListSavingAccountRvBinding
import com.vyuvancollectors.databinding.ActivityRvCustomerListBinding

class CustomerListSavingAccountRV(private val list :List<CustomerListSavingAccountData>) : RecyclerView.Adapter<CustomerListSavingAccountRV.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityCustomerListSavingAccountRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context

        val mobile = list[position].phone
        val customerId = list[position].customerId
        val agentId = list[position].agentId
        val token = list[position].token

        holder.binding.NameTxt.text = list[position].name
        holder.binding.mobileTxt.text = list[position].phone
        holder.binding.emailTxt.text = list[position].localCity
        holder.binding.addressTxt.text = list[position].address


        holder.binding.mobileTxt.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$mobile")
            context.startActivity(intent)
        }

        holder.binding.listEmiCard.setOnClickListener{
            val intent = Intent(context, SavingAccountForm::class.java)
            intent.putExtra("customerId",customerId)
            intent.putExtra("agentId",agentId)
            intent.putExtra("token",token)

            context.startActivity(intent)
        }



    }
    override fun getItemCount(): Int {
        return list.size
    }
    inner class MyViewHolder(var binding: ActivityCustomerListSavingAccountRvBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}