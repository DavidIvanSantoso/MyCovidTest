package com.example.test2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.test2.SplashScreenActivity.Companion.dataUser
import com.example.test2.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    private lateinit var dbUser : FirebaseFirestore

    private lateinit var authUser : FirebaseAuth

    private var arrUser : ArrayList<UserCls> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbUser = FirebaseFirestore.getInstance()
        authUser = FirebaseAuth.getInstance()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            when {
                TextUtils.isEmpty(binding.etEmailLogin.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Enter Email",
                        Toast.LENGTH_LONG
                    ).show()
                }

                TextUtils.isEmpty(binding.etPasswordLogin.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Enter Password",
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {
                    val email = binding.etEmailLogin.text.toString()
                    val password = binding.etPasswordLogin.text.toString()

                    if (email == "Admin") {
                        val adminActivityIntent = Intent(this@LoginActivity, AdminActivity::class.java)
                        startActivity(adminActivityIntent)
                        finish()
                    }

                    authUser.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login Successful",
                                    Toast.LENGTH_LONG
                                ).show()

                                GetDataUser(email)

                                val mainActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(mainActivityIntent)
//                                finish()
                            }

                            else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        }


        binding.tvForgotPassword.setOnClickListener {
            val forgotPasswordActivityIntent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(forgotPasswordActivityIntent)
        }

        binding.tvSignUp.setOnClickListener {
            val signUpActivityIntent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(signUpActivityIntent)
        }
    }

    private fun GetDataUser(email : String) {
        dbUser.collection("tbUser")
            .get()
            .addOnSuccessListener { result ->

                val arrUser = ArrayList<UserCls>()

                for (document in result) {
                    val dataBaru = UserCls(
                        document.data!!.get("nik").toString(),
                        document.data!!.get("email").toString(),
                        document.data!!.get("username").toString(),
                        document.data!!.get("phoneNumber").toString(),
                        document.data!!.get("address").toString(),
                        document.data!!.get("favorite").toString(),
                        document.data!!.get("profilePicture").toString()
                    )
                    arrUser.add(dataBaru)
                }

                arrUser.forEach {
                    if (it.Email.toLowerCase() == email.toLowerCase()) {
                        dataUser = it
                    }
                }
            }
    }
}