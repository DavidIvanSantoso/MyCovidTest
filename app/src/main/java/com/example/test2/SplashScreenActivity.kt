package com.example.test2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var dbUser : FirebaseFirestore

    private lateinit var authUser : FirebaseAuth

    companion object {
        lateinit var dataUser : UserCls

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        dataUser = UserCls("","","","","","","")

        dbUser = FirebaseFirestore.getInstance()
        authUser = FirebaseAuth.getInstance()

        Handler().postDelayed({

            if (authUser.currentUser != null) {
                val email = authUser.currentUser!!.email.toString()
                Log.d("email", email)
                GetDataUser(email)
                print(dataUser)
                val mainActivityIntent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(mainActivityIntent)
            }
            else {
                val loginIntent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                startActivity(loginIntent)
            }
            finish()

        }, 3000)
    }

    private fun GetDataUser(email : String) {
        dbUser.collection("tbUser").document(email)
            .get()
            .addOnSuccessListener { result ->

                dataUser = UserCls(
                    result.data!!.get("nik").toString(),
                    result.data!!.get("email").toString(),
                    result.data!!.get("username").toString(),
                    result.data!!.get("phoneNumber").toString(),
                    result.data!!.get("address").toString(),
                    result.data!!.get("favorite").toString(),
                    result.data!!.get("profilePicture").toString()
                )
            }
    }
}