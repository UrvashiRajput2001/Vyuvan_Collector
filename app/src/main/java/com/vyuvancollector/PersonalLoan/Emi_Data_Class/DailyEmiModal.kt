package com.vyuvancollector.PersonalLoan.Emi_Data_Class

data class DailyEmiModal(
    val emiAmount: String,
    val remainingAmount: String,
    val customerId: String,
    val collectionType: String,
    val status: String,
    val token : String,
    val agentId : String,
    val doc : String
    )
