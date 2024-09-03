package com.vyuvancollectors.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.JsonObject
import com.vyuvancollectors.GroupLoan.OtherActivity.GroupCollectionType
import com.vyuvancollectors.PersonalLoan.StartActivitys.PersonalCollectionTypes
import com.vyuvancollectors.PersonalLoan.StartActivitys.PersonalEmiStatus
import com.vyuvancollectors.R
import com.vyuvancollectors.RecentLoan.RecentLoanData
import com.vyuvancollectors.RecentLoan.RecentLoanRV
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.SavingAccount.CustomerListSavingAccount.CustomerListSavingAccount
import com.vyuvancollectors.SavingAccount.SavingAccountForm
import com.vyuvancollectors.databinding.ActivityLoansTypeBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class LoansType : AppCompatActivity() {

    private var binding : ActivityLoansTypeBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("LongLogTag", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityLoansTypeBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)

        setContentView(binding?.root)
        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the permission launcher
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        // Check if the location permission is already granted
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Check if location services are enabled
        if (isLocationEnabled(this)) {
//            getCurrentLocation()
//            Toast.makeText(this, "Location services are enabled", Toast.LENGTH_SHORT).show()
        } else {
            showSettingsAlert()

//            Toast.makeText(this, "Location services are disabled", Toast.LENGTH_SHORT).show()
        }




        recentLoan()
        agentCollectedAmountApi()


        val sdf = SimpleDateFormat("dd.MM.yyyy/EEEE")
        val date : String = sdf.format(Date())
        binding?.dateTxt?.text = "Date : $date"

        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        val sharedPreference = getSharedPreferences("VYuvan_Collector", MODE_PRIVATE)
        val agentName =  sharedPreference.getString("agentName","")

        binding?.agentNameTxt?.text = "$agentName"

        Log.e("Token In LoanType Screen","$token Token $agentId agentId")

        binding?.logoutBtn?.isVisible = false
//        binding?.customListBtn?.isVisible = false
        binding?.messageTxt?.isVisible = false
        binding?.savingsAccountBtn?.isVisible = false

        binding?.menuImg?.setOnClickListener {
            binding?.logoutBtn?.isVisible = true
            binding?.savingsAccountBtn?.isVisible = true
//            binding?.customListBtn?.isVisible = true
        }


//        binding?.customListBtn?.setOnClickListener {
//            val intent = Intent(this, CustomerList::class.java)
//            intent.putExtra("token","$token")
//            intent.putExtra("agentId","$agentId")
//            startActivity(intent)
//            binding?.logoutBtn?.isVisible = false
//            binding?.customListBtn?.isVisible = false

//        }

        binding?.logoutBtn?.setOnClickListener {
            logOut()
        }

        binding?.savingsAccountBtn?.setOnClickListener {
            val intent = Intent(this, CustomerListSavingAccount::class.java)
            intent.putExtra("token","$token")
            intent.putExtra("agentId","$agentId")
          startActivity(intent)
        }


        binding?.constLayout?.setOnClickListener {
            binding?.logoutBtn?.isVisible = false
            binding?.savingsAccountBtn?.isVisible= false
//            binding?.customListBtn?.isVisible = false
        }

        binding?.personalLoanBtn?.setOnClickListener {
            val intent = Intent(this, PersonalEmiStatus::class.java)
            intent.putExtra("token","$token")
            intent.putExtra("agentId","$agentId")
            startActivity(intent)
            binding?.logoutBtn?.isVisible = false
            binding?.savingsAccountBtn?.isVisible= false
//            binding?.customListBtn?.isVisible = false
        }

        binding?.groupLoanBtn?.setOnClickListener {
            val intent = Intent(this,  GroupCollectionType::class.java)
            intent.putExtra("token","$token")
            intent.putExtra("agentId","$agentId")
            startActivity(intent)
            binding?.logoutBtn?.isVisible = false
            binding?.savingsAccountBtn?.isVisible= false
//            binding?.customListBtn?.isVisible = false

        }

        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
//            binding?.messageTxt?.isVisible = true
            binding?.progressBar?.isVisible = false
            binding?.txtBar?.isVisible = false
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        recentLoan()
        agentCollectedAmountApi()

//        getCurrentLocation()


        binding?.logoutBtn?.isVisible = false
//        binding?.customListBtn?.isVisible = false
        binding?.messageTxt?.isVisible = false
        binding?.savingsAccountBtn?.isVisible = false
    }

    private fun recentLoan(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        val type = "Agent"

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
//        val call = apiClient?.postDataGET2(token.toString(),"v1/loans/getRecentLoanDetails/$agentId")
        val call = apiClient?.getTwoHeadersWithTokenData(token.toString(),type,"v1/loans/getRecentLoanDetails/$agentId")
        call?.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful){
                    binding?.progressBar?.isVisible = false
                    binding?.txtBar?.isVisible = false
                    binding?.recentLoanRv?.isVisible = true
                    val res = response.body()
                    val list = ArrayList<RecentLoanData>()

                    var recyclerView : RecentLoanRV? = null
                    var totalGroupMember = ""
                    var groupName = ""
                    var groupHeadMobile = ""
                    var name  = ""
                    var mobile = ""

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val status = jsonObject.get("status")
                    val items = jsonObject.get("items")

                    Log.e("JsonArray1","$res respones")

                    val jsonArray = JSONTokener(items.toString()).nextValue() as JSONArray


                    if(status == true){
                        for (i in 0 until jsonArray.length()){
                            val loanType = jsonArray.getJSONObject(i).getString("loanType")
                            val loanAmount = jsonArray.getJSONObject(i).getString("loanAmount")
                            val collectionType = jsonArray.getJSONObject(i).getString("collectionType")
                            val disburseDate = jsonArray.getJSONObject(i).getString("disburseDate")
                            val emiAmount = jsonArray.getJSONObject(i).getString("collectedAmount")

                            val customerDetail = jsonArray.getJSONObject(i).getString("CustomerDetail")
                            val jsonArrayPL = JSONTokener(customerDetail.toString()).nextValue() as JSONArray
                            for (j in 0 until  jsonArrayPL.length()){
                                     name = jsonArrayPL.getJSONObject(j).getString("name")
                                     mobile = jsonArrayPL.getJSONObject(j).getString("phone")
                            }

                            val groupDetail = jsonArray.getJSONObject(i).getString("GroupDetail")
                            val jsonArrayGL = JSONTokener(groupDetail.toString()).nextValue() as JSONArray
                            for (k in 0 until jsonArrayGL.length()){
                                    totalGroupMember = jsonArrayGL.getJSONObject(k).getString("totalGroupMember")
                            }

                            val leaderDetails = jsonArray.getJSONObject(i).getString("LeaderDetail")
                            val jsonArrayLeaderDetails = JSONTokener(leaderDetails.toString()).nextValue() as JSONArray
                            for (l in 0 until jsonArrayLeaderDetails.length()){
                                    groupName = jsonArrayLeaderDetails.getJSONObject(l).getString("groupName")
                                    groupHeadMobile = jsonArrayLeaderDetails.getJSONObject(l).getString("groupLeaderMobile")
                            }

                            list.add(RecentLoanData(name,mobile,collectionType,emiAmount,loanAmount,disburseDate,totalGroupMember,groupHeadMobile,groupName,loanType))

                            binding?.recentLoanRv?.layoutManager = LinearLayoutManager(this@LoansType)
                            recyclerView = RecentLoanRV(list)
                            binding?.recentLoanRv?.adapter = recyclerView
                            recyclerView.notifyDataSetChanged()
                        }
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }
        })

    }

    private fun logOut(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        val type = " Agent"

        val json = JsonObject()
        json.addProperty("agentId","$agentId")

        @Suppress("DEPRECATION")
        val jsonObject: RequestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), json.toString())

        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.postTwoHeadersWithTokenData(token.toString(),type,"v1/agent/agent/logout", jsonObject)
        call?.enqueue(object  : Callback<JsonObject> {
            @SuppressLint("CommitPrefEdits")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    forLoginPage()
                    val sharedPreferences = getSharedPreferences("VYuvan_Collector", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("token","")
                    editor.putString("agentId","")
                    editor.putString("agentName","")
                    editor.apply()
                    finish()
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("urvashi", "$t your response is fail")
            }
        })
    }


    private fun agentCollectedAmountApi(){
        val bundle = intent.extras
        @Suppress("DEPRECATION")
        val token = bundle?.get("token") as String?
        @Suppress("DEPRECATION")
        val agentId = bundle?.get("agentId") as String?

        val typeAgent = "Agent"



        val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
        val call = apiClient?.getTwoHeadersWithTokenData(token.toString(),typeAgent,"v1/emisData/toBePaidEmi/$agentId")
        call?.enqueue(object  : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val res = response.body()

                    Log.e("Ja re","$res")

                    val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                    val totalAmountPL = jsonObject.getDouble("totalAmountPL")
                    val totalAmountGL = jsonObject.getDouble("totalAmountGL")
                    val total = totalAmountPL + totalAmountGL


                        binding?.todayDueAmountTxt?.text = "₹$total"




                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("urvashi", "$t your response is fail")
            }
        })

    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    private fun forLoginPage(){
        val intent = Intent(this, MainActivity::class.java)
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

//    fun canGetLocation(): Boolean {
//        return isLocationEnabled(this) // application context
//    }
//
//    fun isLocationEnabled(context: Context): Boolean {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            try {
////                showSettingsAlert()
//                val locationMode = Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
//                locationMode != Settings.Secure.LOCATION_MODE_OFF
//
//            } catch (e: Settings.SettingNotFoundException) {
//                e.printStackTrace()
//                false
//            }
//        } else {
//            val locationProviders = Settings.Secure.getString(context.contentResolver, Settings.Secure.LOCATION_MODE)
//            !locationProviders.isNullOrEmpty()
//        }
//    }


    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun showSettingsAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.logo)
        builder.setTitle("GPS Settings")
        builder.setMessage("GPS Location is off. Please go to settings menu and enable your location")

        builder.setPositiveButton("Settings") { dialog, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val alert = builder.create()
        alert.show()

        val pButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        pButton.setTextColor(Color.GRAY)
        pButton.gravity = Gravity.CENTER

        val nButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        nButton.setTextColor(Color.GRAY)
        nButton.gravity = Gravity.CENTER
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener(this, OnSuccessListener<Location> { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                Toast.makeText(this, "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_LONG).show()
//                Log.e("chand","Latitude: $latitude, Longitude: $longitude")
            } else {
//                Toast.makeText(this, "Location is null", Toast.LENGTH_SHORT).show()
            }
        })
    }



}