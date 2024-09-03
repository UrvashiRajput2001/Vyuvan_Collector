package com.vyuvancollectors.PersonalLoan.PersonalLoanSearch

data class EmiDataClassForPhoneSearch(
    val emiAmount: String,
    val remainingAmount: String,
    val customerId: String,
    val collectionType: String,
    val status: String,
    val token : String,
    val agentId : String,
    val doc : String
)
