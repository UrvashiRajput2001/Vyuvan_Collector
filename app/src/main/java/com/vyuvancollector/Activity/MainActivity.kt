package com.vyuvancollector.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import com.vyuvancollector.Retrofit.ApiClient
import com.vyuvancollector.Retrofit.ApiInterface
import com.vyuvancollector.databinding.ActivityMainBinding
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

                        if (status == true){
                            val token = items.getString("token")
                            val agentId = items.getString("_id")

                            val sharedPreferences = getSharedPreferences("VYuvan_Collector", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("token",token)
                            editor.putString("agentId",agentId)
                            editor.apply()

                            val intent = Intent(this@MainActivity, com.vyuvancollector.Activity.LoansType::class.java)
                            intent.putExtra("token", "$token")
                            intent.putExtra("agentId","$agentId")
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
    }
}