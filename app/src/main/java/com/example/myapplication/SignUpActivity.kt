package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    lateinit var userIDView: EditText
    lateinit var userPWView: EditText
    lateinit var userPCView: EditText
    lateinit var signupBtn: TextView
    lateinit var cancelBtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        cancel_button.setOnClickListener {
            finish()
        }

        if((application as MasterApplication).checkIsLogin()) {
            finish()
            startActivity(Intent(this, MyTubeActivity::class.java))
        } else {
            setContentView(R.layout.activity_sign_up)
            initView(this@SignUpActivity)
            setupListener(this)
        }
    }

    fun setupListener(activity: Activity) {
        signupBtn.setOnClickListener{
            register(this@SignUpActivity)
        }
        cancelBtn.setOnClickListener {
            startActivity(
                Intent(this@SignUpActivity, SignInActivity::class.java)
            )
        }
    }

    fun register(activity: Activity) {
        val userID = getUserID()
        val userPW = getUserPW()
        val userPC = getUserPC()

        (application as MasterApplication).service.register(
            userID, userPW, userPC
        ).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.d("registerr", userID + userPC + userPW)
                if(response.isSuccessful) {
                    Toast.makeText(activity, "가입에 성공했습니다.", Toast.LENGTH_LONG).show()
                    val user = response.body()
                    val token = user!!.token!!
                    saveUserToken(token, activity)
                    (application as MasterApplication).createRetrofit()
                    Log.d("registerr", userID + userPC + userPW)
                    activity.startActivity(
                        Intent(activity, MyTubeActivity::class.java)
                    )
                } else {
                    Toast.makeText(activity, "가입에 실패했습니다.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(activity, "가입에 실패했습니다.", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun saveUserToken(token: String, activity: Activity) {
        val tokenSP = activity.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val tokenEditor = tokenSP.edit()
        tokenEditor.putString("login_sp", token)
        tokenEditor.commit()
    }

    fun initView(activity: Activity) {
        userIDView = activity.findViewById(R.id.signup_id)
        userPWView = activity.findViewById(R.id.signup_pw)
        userPCView = activity.findViewById(R.id.signup_pc)
        signupBtn = activity.findViewById(R.id.signup)
        cancelBtn = activity.findViewById(R.id.cancel_button)
    }

    fun getUserID(): String {
        return userIDView.text.toString()
    }

    fun getUserPW(): String {
        return userPWView.text.toString()
    }

    fun getUserPC(): String {
        return userPCView.text.toString()
    }
}