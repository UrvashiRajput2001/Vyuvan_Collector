package com.vyuvancollectors.SavingAccount.CustomerListSavingAccount

import android.app.backup.BackupAgent

data class CustomerListSavingAccountData(
    val name: String,
    val phone: String,
    val address: String,
    val localCity : String,
    val customerId : String,
    val token : String,
    val agentId : String
    )
