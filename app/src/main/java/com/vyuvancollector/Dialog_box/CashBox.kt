package com.vyuvancollector.Dialog_box


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vyuvancollector.databinding.ActivityCashBoxBinding

class CashBox : AppCompatActivity() {

    private var binding : ActivityCashBoxBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCashBoxBinding.inflate(layoutInflater)

        setContentView(binding?.root)
    }
}