package com.vyuvancollectors.PersonalLoan.PDF.Monthly

data class DataForMonthlyPdf(
    val name: String,
    val mobile: String,
    val loanAmount: String,
    val interest: String,
    val collectionType: String,
    val totalInterest: String,
    val totalAmount: String,
    val disburseDate: String,
    val collectionAmount: String,
)
