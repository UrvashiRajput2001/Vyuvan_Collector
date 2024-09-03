package com.vyuvancollectors.GroupLoan.GroupLoanPhoneSearch

data class ModalDataForSearch(
    val agentId : String,
    val token : String,
    val groupId : String,
    val loanAmount : String,
    val interest : String,
    val teamLeadName : String,
    val groupLeaderName : String,
    val collectionType : String,
    val groupName: String,
    val totalGroupMember : String,
    val groupLeaderMobile : String,
    val disburseDate : String
)
