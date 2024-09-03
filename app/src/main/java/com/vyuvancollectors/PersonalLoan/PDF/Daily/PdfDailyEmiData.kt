package com.vyuvancollectors.PersonalLoan.PDF.Daily

data class PdfDailyEmiData(
    val remainingAmount: String,
    val emiAmount : String,
    val dateOfCollect : String,
    val status : String
)
