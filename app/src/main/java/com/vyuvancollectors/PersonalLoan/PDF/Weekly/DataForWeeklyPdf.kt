package com.vyuvancollectors.PersonalLoan.PDF.Weekly

data class DataForWeeklyPdf(
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
