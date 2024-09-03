package com.vyuvancollectors.GroupLoan.AdapterInGroup

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.GroupLoan.GroupMemberList.WeeklyMemberList
import com.vyuvancollectors.GroupLoan.Group_Data_Class.WeeklyGroupDetailsData
import com.vyuvancollectors.databinding.ActivityWeekLyGroupRvBinding

class WeekLyGroupRv(private val list :List<WeeklyGroupDetailsData>) : RecyclerView.Adapter<WeekLyGroupRv.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ActivityWeekLyGroupRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: WeekLyGroupRv.MyViewHolder, position: Int) {
        val context = holder.itemView.context
        val groupName = list[position].groupName
        val totalGroupMember = list[position].totalGroupMember
        val groupId = list[position].groupId
        val token = list[position].token
        val agentId = list[position].agentId
        val groupLeaderName = list[position].groupLeaderName
        val type = list[position].collectionType
        val loanAmount = list[position].loanAmount
        val groupLeaderMobile = list[position].groupLeaderMobile
        val member = list[position].totalGroupMember
        val disburseDate = list[position].disburseDate

        holder.binding.groupNameTxt.text = list[position].groupName
        holder.binding.collectionTypeTxt.text = "Type : $type"
        holder.binding.headTxt.text = "$groupLeaderName (Group Head)"
        holder.binding.emiTxt.text = "Loan : $loanAmount"
        holder.binding.phoneTxt.text = "Mobile : $groupLeaderMobile"
        holder.binding.disburseDateTxt.text = "Date : $disburseDate"
        holder.binding.memberCountTxt.text = "Members : $member"


        holder.binding.listEmiCard.setOnClickListener {
            val intent = Intent(context, WeeklyMemberList::class.java)
            intent.putExtra("token","$token")
            intent.putExtra("agentId","$agentId")
            intent.putExtra("totalGroupMember","$totalGroupMember")
            intent.putExtra("groupName","$groupName")
            intent.putExtra("groupId","$groupId")
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }
    inner class MyViewHolder(var binding: ActivityWeekLyGroupRvBinding) : RecyclerView.ViewHolder(
        binding.root
    ){}
}