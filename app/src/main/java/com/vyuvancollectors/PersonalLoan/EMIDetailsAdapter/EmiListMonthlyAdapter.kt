package com.vyuvancollectors.PersonalLoan.EMIDetailsAdapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.PersonalLoan.Emi_Data_Class.MonthlyEmiModal
import com.vyuvancollectors.R
import com.vyuvancollectors.databinding.ActivityEmiListMonthlyAdapterBinding

class EmiListMonthlyAdapter(private val list :List<MonthlyEmiModal>) : RecyclerView.Adapter<EmiListMonthlyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityEmiListMonthlyAdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat", "SdCardPath", "SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: EmiListMonthlyAdapter.MyViewHolder, position: Int) {
        val context = holder.itemView.context
        val status = list[position].status

        holder.binding.emiAmountTxt.text = "EMIAmount : ${list[position].emiAmount}"
        holder.binding.dateOfCollectionTxt.text = "DateOfCollection : ${list[position].doc}"
        holder.binding.remainingAmountTxt.text = "RemainingAmount : ${list[position].remainingAmount}"
        holder.binding.emiNumTxt.text = "EMI NO. : ${list[position].emiNo}"

        if (status == "1"){
            holder.binding.statusTxt.text = "Paid"
//            holder.binding.statusTxt.setTextColor(Color.parseColor("#0BDA51"))
            holder.binding.statusTxt.setBackgroundColor(Color.parseColor("#0AB850"))
        }else{
            holder.binding.statusTxt.text = "Pending"
//            holder.binding.statusTxt.setTextColor(Color.parseColor("#0BDA51"))
            holder.binding.statusTxt.setBackgroundColor(Color.parseColor("#EC0B0B"))

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: ActivityEmiListMonthlyAdapterBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}