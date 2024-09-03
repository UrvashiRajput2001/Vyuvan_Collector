package com.vyuvancollectors.PersonalLoan.Emi_Data_Class

data class YearlyEmiModal(
    val emiAmount: String,
    val remainingAmount: String,
    val customerId: String,
    val collectionType: String,
    val status: String,
    val token : String,
    val agentId : String,
    val doc : String,
    val emiNo : String
)
