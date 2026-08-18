package com.vyuvancollectors.PersonalLoan.PDF.Weekly

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.databinding.ActivityPdfWeeklyEmirecyclerViewBinding

class PdfWeeklyEMIRecyclerView(private val list: ArrayList<PdfWeeklyEmiData>) : RecyclerView.Adapter<PdfWeeklyEMIRecyclerView.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityPdfWeeklyEmirecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat", "SdCardPath", "SetTextI18n")
    override fun onBindViewHolder(holder: PdfWeeklyEMIRecyclerView.MyViewHolder, position: Int) {
        val context = holder.itemView.context

        holder.binding.currentDueTxt.text =  list[position].emiAmount
        holder.binding.dateOfCollectionTxt.text =  list[position].dateOfCollect
        holder.binding.remainingAmountTxt.text =  list[position].remainingAmount

//
//        if (status == "1"){
//            holder.binding.statusTxt.text = "Paid"
//            holder.binding.statusTxt.background = context.getDrawable(R.drawable.emi_status_paid_dgn)
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: ActivityPdfWeeklyEmirecyclerViewBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}