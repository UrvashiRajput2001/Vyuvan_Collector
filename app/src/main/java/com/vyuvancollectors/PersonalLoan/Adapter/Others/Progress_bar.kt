package com.vyuvancollectors.PersonalLoan.Adapter.Others

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vyuvancollectors.databinding.ActivityProgressBarBinding

class Progress_bar : AppCompatActivity() {

    private var binding : ActivityProgressBarBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProgressBarBinding.inflate(layoutInflater)

        setContentView(binding?.root)
    }
}