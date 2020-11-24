package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        movesignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        signin.setOnClickListener {
            var userID = id.text.toString()
            var userPW = password.text.toString()

            (application as MasterApplication).service.requestLogin(userID, userPW)
                .enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val user = response.body()
                            val token = user!!.token!!
                            saveUserToken(token, this@SignInActivity)
                            (application as MasterApplication).createRetrofit()

                            Toast.makeText(this@SignInActivity, "로그인 하셨습니다.", Toast.LENGTH_LONG)
                                .show()
                            this@SignInActivity.finish()
                            startActivity(Intent(this@SignInActivity, MyTubeActivity::class.java))
                        } else {
                            Toast.makeText(this@SignInActivity, "로그인에 실패했습니다.", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(this@SignInActivity, "로그인에 실패했습니다.", Toast.LENGTH_LONG)
                            .show()
                    }
                })
        }
    }

    fun saveUserToken(token: String, activity: Activity) {
        val tokenSP = activity.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val tokenEditor = tokenSP.edit()
        tokenEditor.putString("login_sp", token)
        tokenEditor.commit()
    }
}