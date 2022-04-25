package com.example.test2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.test2.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityForgotPasswordBinding

    private lateinit var authUser : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authUser = FirebaseAuth.getInstance()

        binding.ibBacktoLoginForgotPassword.setOnClickListener {
            finish()
        }

        binding.btnSubmitForgotPassword.setOnClickListener {

            when {
                TextUtils.isEmpty(binding.etEmailLoginForgotPassword.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Enter Email",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    val email = binding.etEmailLoginForgotPassword.text.toString()

                    authUser.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Email Sent Successfully",
                                    Toast.LENGTH_LONG).show()

                                finish()
                            }
                            else {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }



        }
    }
}