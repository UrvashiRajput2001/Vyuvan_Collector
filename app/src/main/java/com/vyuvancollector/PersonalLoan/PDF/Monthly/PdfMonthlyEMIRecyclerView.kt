package com.vyuvancollector.PersonalLoan.PDF.Monthly

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollector.PersonalLoan.PDF.Custom.PdfYearlyEMIRecyclerView
import com.vyuvancollector.PersonalLoan.PDF.Custom.PdfYearlyEmiData
import com.vyuvancollector.databinding.ActivityPdfMonthlyEmirecyclerViewBinding
import com.vyuvancollector.databinding.ActivityPdfYearlyEmirecyclerViewBinding

class PdfMonthlyEMIRecyclerView(private val list: ArrayList<PdfMonthlyEmiData>) : RecyclerView.Adapter<PdfMonthlyEMIRecyclerView.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityPdfMonthlyEmirecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat", "SdCardPath", "SetTextI18n")
    override fun onBindViewHolder(holder: PdfMonthlyEMIRecyclerView.MyViewHolder, position: Int) {
        val context = holder.itemView.context

        holder.binding.currentDueTxt.text =  list[position].emiAmount
        holder.binding.dateOfCollectionTxt.text =  list[position].dateOfCollect
        holder.binding.remainingAmountTxt.text =  list[position].remainingAmount


//        if (status == "1"){
//            holder.binding.statusTxt.text = "Paid"
//            holder.binding.statusTxt.background = context.getDrawable(R.drawable.emi_status_paid_dgn)
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: ActivityPdfMonthlyEmirecyclerViewBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}