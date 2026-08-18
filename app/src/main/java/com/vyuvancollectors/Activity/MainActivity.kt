package com.vyuvancollectors.Activity

<<<<<<< HEAD
import android.annotation.SuppressLint
import android.content.Intent
=======
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
>>>>>>> e6194dd065e378a06eb4b376475ff1604e6d4bb3
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
<<<<<<< HEAD
=======
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
>>>>>>> e6194dd065e378a06eb4b376475ff1604e6d4bb3
import com.vyuvancollectors.Retrofit.ApiClient
import com.vyuvancollectors.Retrofit.ApiInterface
import com.vyuvancollectors.databinding.ActivityMainBinding
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding?.root)

<<<<<<< HEAD
=======



>>>>>>> e6194dd065e378a06eb4b376475ff1604e6d4bb3
        binding?.passwordTxt?.transformationMethod = PasswordTransformationMethod()

        binding?.codeTxt?.setOnClickListener {
            binding?.textInputLayoutCode?.isErrorEnabled = false
            binding?.textInputLayoutCode?.isFocusable = true
        }

        binding?.passwordTxt?.setOnClickListener {
            binding?.textInputLayoutPassword?.isErrorEnabled = false
            binding?.textInputLayoutPassword?.isFocusable = true
        }

        binding?.btnSubmit?.setOnClickListener {
            if(binding?.codeTxt?.text?.isEmpty() == true){
                binding?.textInputLayoutCode?.isErrorEnabled = true
                binding?.btnSubmit?.isEnabled = true
                binding?.textInputLayoutCode?.error = "Please Enter Your Code"
                binding?.textInputLayoutCode?.requestFocus()
            } else if(binding?.passwordTxt?.text?.isEmpty() == true){
                binding?.textInputLayoutPassword?.isErrorEnabled = true
                binding?.textInputLayoutPassword?.error = "Please Enter Your password"
                binding?.textInputLayoutCode?.requestFocus()
                binding?.btnSubmit?.isEnabled = true
            }else {
                val agentCode = binding?.codeTxt?.text
                val password = binding?.passwordTxt?.text
                Log.e("urvashi","$agentCode code")
                Log.e("urvashi","$password password")

                val json = JsonObject()
                json.addProperty("code","$agentCode")
                json.addProperty("password","$password")

                @Suppress("DEPRECATION")
                val jsonObject: RequestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(), json.toString())

                val apiClient = ApiClient.getInstance()?.create(ApiInterface::class.java)
                val call = apiClient?.postData("v1/agent/loginAgent",jsonObject)

                call?.enqueue(object : Callback<JsonObject> {
                    @SuppressLint("SuspiciousIndentation")
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val res = response.body()
                        val jsonObject = JSONTokener(res.toString()).nextValue() as JSONObject
                        val status = jsonObject.get("status")
                        val items = jsonObject.getJSONObject("items")

                        Log.e("res","$res  res")

                        if (status == true){
                            val token = items.getString("token")
                            val agentId = items.getString("_id")
<<<<<<< HEAD
=======
                            val agentName = items.getString("name")
                            val cityCode = items.getString("cityCode")
>>>>>>> e6194dd065e378a06eb4b376475ff1604e6d4bb3

                            val sharedPreferences = getSharedPreferences("VYuvan_Collector", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("token",token)
                            editor.putString("agentId",agentId)
<<<<<<< HEAD
                            editor.apply()

                            val intent = Intent(this@MainActivity, LoansType::class.java)
                            intent.putExtra("token", "$token")
                            intent.putExtra("agentId","$agentId")
=======
                            editor.putString("agentName",agentName)
                            editor.apply()

                            Log.e("agentName","$agentName")

                            val intent = Intent(this@MainActivity, LoansType::class.java)
                            intent.putExtra("token", "$token")
                            intent.putExtra("agentId","$agentId")
                            intent.putExtra("agentName","$agentName")
>>>>>>> e6194dd065e378a06eb4b376475ff1604e6d4bb3
                            startActivity(intent)
                            finish()
                            binding?.btnSubmit?.isEnabled = false
                        } else {
                            binding?.textInputLayoutCode?.isErrorEnabled = true
                            binding?.textInputLayoutCode?.error = "Invalid details"
                            binding?.textInputLayoutPassword?.isErrorEnabled = true
                            binding?.textInputLayoutPassword?.error = "Invalid details"
                            binding?.btnSubmit?.isEnabled = true
                        }
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("urvashi", "your response is fail")
                    }
                })
            }
        }

        if (isConnected()) {
//            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
//            binding?.messageTxt?.isVisible = true
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
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