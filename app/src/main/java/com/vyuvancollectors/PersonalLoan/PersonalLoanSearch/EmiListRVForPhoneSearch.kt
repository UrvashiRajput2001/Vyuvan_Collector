package com.vyuvancollectors.PersonalLoan.PersonalLoanSearch

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.R
import com.vyuvancollectors.databinding.ActivityEmiListRvforPhoneSearchBinding

class EmiListRVForPhoneSearch(private val list :List<EmiDataClassForPhoneSearch>) : RecyclerView.Adapter<EmiListRVForPhoneSearch.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityEmiListRvforPhoneSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat", "SdCardPath", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context
        val status = list[position].status

        holder.binding.emiAmountTxt.text = "EMIAmount: ${list[position].emiAmount}"
        holder.binding.dateOfCollectionTxt.text = "DateOfCollection: ${list[position].doc}"
        holder.binding.remainingAmountTxt.text = "RemainingAmount: ${list[position].remainingAmount}"

        if (status == "1"){
            holder.binding.statusTxt.text = "Paid"
            holder.binding.statusTxt.background = context.getDrawable(R.drawable.emi_status_pending_dgn)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: ActivityEmiListRvforPhoneSearchBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}