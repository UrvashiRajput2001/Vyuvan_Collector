package com.vyuvancollectors.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vyuvancollectors.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    private var binding : ActivitySplashScreenBinding? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)

        setContentView(binding?.root)



/*
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
        } else {
            // Request permission
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        }
*/

        val sharedPreference = getSharedPreferences("VYuvan_Collector", MODE_PRIVATE)
        val token =  sharedPreference.getString("token","")
        val agentId = sharedPreference.getString("agentId","")

        Log.e("urvashi","$token token  $agentId agentId sharedPreference")
        if (token == "") {
            val time: Long = 2000
            Handler().postDelayed(Runnable() {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, time)
        }else{
            val time : Long =  2000
            Handler().postDelayed(Runnable(){
                val intent = Intent(this, com.vyuvancollectors.Activity.LoansType::class.java)
                intent.putExtra("token","$token")
                intent.putExtra("agentId","$agentId")
                startActivity(intent)
                finish()
            },time)
        }
        Log.e("main","$agentId agentId , $token token")
    }

/*    override fun onLocationChanged(p0: Location) {
        TODO("Not yet implemented")
    }*/
}