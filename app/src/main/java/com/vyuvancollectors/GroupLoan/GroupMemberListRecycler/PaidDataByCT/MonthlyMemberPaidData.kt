package com.vyuvancollectors.GroupLoan.GroupMemberListRecycler.PaidDataByCT

data class MonthlyMemberPaidData(
    val token : String,
    val name : String,
    val phone : String,
    val collectionType: String,
    val paidEmiAmount : String,
    val status : String,
    val doc : String,
    val lastDateCollect : String,
    val emiNo : String
)
