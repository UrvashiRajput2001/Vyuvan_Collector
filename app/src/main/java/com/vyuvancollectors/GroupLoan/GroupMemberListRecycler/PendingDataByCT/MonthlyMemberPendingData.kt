package com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PendingDataByCT

data class MonthlyMemberPendingData(
    val emiId : String,
    val token : String,
    val name : String,
    val phone : String,
    val collectionType: String,
    val emiAmount : String,
    val status : String,
    val agentId : String,
    val customerId : String,
    val loanId : String,
    val dateOfCollect : String,
    val totalGroupMember : String,
    val groupName : String,
    val groupId : String,
    val emiNo : String
)
