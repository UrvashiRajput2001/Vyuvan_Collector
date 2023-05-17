package com.vyuvancollector.PersonalLoan.StartActivitys

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.vyuvancollector.PersonalLoan.TypesOfEMI.*
import com.vyuvancollector.R
import com.vyuvancollector.databinding.ActivityPersonalcollectiontypesBinding
import java.text.SimpleDateFormat
import java.util.*


class PersonalCollectionTypes : AppCompatActivity() {

    private var binding : ActivityPersonalcollectiontypesBinding? = null

    @SuppressLint("UseCompatLoadingForDrawables", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val tag = bundle?.get("tag") as String?
        @Suppress("DEPRECATION")
        val typo = bundle?.get("typo") as String?

        binding = ActivityPersonalcollectiontypesBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }

        binding?.dailyBtn?.setOnClickListener {
           daily()
        }

        binding?.monthlyBtn?.setOnClickListener {
            monthly()
        }

        binding?.weeklyBtn?.setOnClickListener {
            weekly()
        }

        binding?.customBtn?.setOnClickListener {
            yearly()
        }

        binding?.tagTxt?.text = "$tag"

        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }

        val sdf = SimpleDateFormat("dd.MM.yyyy/EEEE")
        val date : String = sdf.format(Date())
        Log.e("Date","$date")

        binding?.dateTxt?.text = "Date : $date"

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun daily(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        @Suppress("DEPRECATION")
        val typo = bundle?.get("typo") as String?

        intent = Intent(this, Daily::class.java)
        intent.putExtra("token","$token")
        intent.putExtra("agentId","$agentId")
        intent.putExtra("typo",typo)
        startActivity(intent)
    }

    private fun monthly(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        @Suppress("DEPRECATION")
        val typo = bundle?.get("typo") as String?

        intent = Intent(this, Monthly::class.java)
        intent.putExtra("token","$token")
        intent.putExtra("agentId","$agentId")
        intent.putExtra("typo",typo)
        startActivity(intent)
    }


    private fun weekly(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        @Suppress("DEPRECATION")
        val typo = bundle?.get("typo") as String?

        intent = Intent(this, Weekly::class.java)
        intent.putExtra("token","$token")
        intent.putExtra("agentId","$agentId")
        intent.putExtra("typo",typo)
        startActivity(intent)
    }


    private fun yearly(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?
        @Suppress("DEPRECATION")
        val typo = bundle?.get("typo") as String?

        intent = Intent(this, Yearly::class.java)
        intent.putExtra("token","$token")
        intent.putExtra("agentId","$agentId")
        intent.putExtra("typo",typo)
        startActivity(intent)

    }

    private fun isConnected(): Boolean {
        var connected = false
        try {
            val cm =
                applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val nInfo = cm.activeNetworkInfo
            connected = nInfo != null && nInfo.isAvailable && nInfo.isConnected
            return connected
        } catch (e: Exception) {
            Log.e("Connectivity Exception", e.message!!)
        }
        return connected
    }

}