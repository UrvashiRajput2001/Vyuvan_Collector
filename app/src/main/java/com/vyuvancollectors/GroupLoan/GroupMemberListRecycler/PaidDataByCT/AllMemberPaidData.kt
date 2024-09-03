package com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PaidDataByCT


data class AllMemberPaidData(
    val agentId : String,
    val token : String,
    val name : String,
    val phone : String,
    val collectionType: String,
    val paidEmiAmount : String,
    val status : String,
    val dateOfCollect : String,
    val lastDateCollect : String,
    val emiNo : String
)
