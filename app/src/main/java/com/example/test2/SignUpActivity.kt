package com.example.test2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import com.example.test2.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignUpBinding

    private lateinit var dbUser : FirebaseFirestore

    private lateinit var authUser : FirebaseAuth

    private var arrUser : ArrayList<UserCls> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_sign_up)

            dbUser = FirebaseFirestore.getInstance()
            authUser = FirebaseAuth.getInstance()

            LoadDataUser()

            binding = ActivitySignUpBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.ibBacktoLogin.setOnClickListener {
                finish()
            }

            //cek nik
            binding.etNikSignUp.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0!!.length < 16) {
                        binding.tilNIKSignUp.error = "Too Short"
                    }
                    else if (p0!!.length > 16) {
                        binding.tilNIKSignUp.error = "Too Long"
                    }
                    else {
                        binding.tilNIKSignUp.error = null
                    }
                }
            })

            //cek email taken
            binding.etEmailSignUp.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    UpdateEmailStatusView(p0.toString())
                }
            })

            //cek strong password
            binding.etPasswordSignUp.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    UpdatePasswordStrengthView(p0.toString())
                }

            })


            binding.btnSignUp.setOnClickListener {
                when {
                    TextUtils.isEmpty(binding.etNikSignUp.text.toString()) -> {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Enter NIK",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    TextUtils.isEmpty(binding.etEmailSignUp.text.toString().trim { it <= ' ' }) -> {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Enter Email",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    TextUtils.isEmpty(binding.etPasswordSignUp.text.toString().trim { it <= ' ' }) -> {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Enter Password",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    TextUtils.isEmpty(binding.etUsernameSignUp.text.toString()) -> {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Enter Username",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    TextUtils.isEmpty(binding.etPhoneNumberSignUp.text.toString()) -> {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Enter Phone Number",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    TextUtils.isEmpty(binding.etAddressSignUp.text.toString()) -> {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Enter Address",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    PasswordStrength.calculateStrength(binding.etPasswordSignUp.text.toString())
                        .getText(this) == "Weak" -> {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Password Unsecure",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    EmailStatus.cekEmailStatus(arrUser, binding.etEmailSignUp.text.toString())
                        .getText(this) == "Taken" -> {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Email Taken",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {
                        Log.d("else", "masuk")
                        val email = binding.etEmailSignUp.text.toString()
                        val password = binding.etPasswordSignUp.text.toString()

                        authUser.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->

                                if (task.isSuccessful) {

                                    Toast.makeText(
                                        this@SignUpActivity,
                                        "Successfully Registered",
                                        Toast.LENGTH_LONG).show()

                                    val nik = binding.etNikSignUp.text.toString()
                                    val username = binding.etUsernameSignUp.text.toString()
                                    val phoneNumber = binding.etPhoneNumberSignUp.text.toString()
                                    val address = binding.etAddressSignUp.text.toString()

                                    val dataBaru = UserCls(
                                        nik, email, username, phoneNumber, address, "", ""
                                    )

                                    dbUser.collection("tbUser").document(dataBaru.Email)
                                        .set(dataBaru)
                                        .addOnSuccessListener {
                                            finish()
                                        }

                                }

                                else {
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                }
            }

    }

    private fun UpdateEmailStatusView(email : String) {
        val stat = EmailStatus.cekEmailStatus(arrUser, email)

        when (stat.getText(this)) {
            "Taken" -> {
                binding.tilEmailSignUp.error = "Taken"
            }
            "Available" -> {
                binding.tilEmailSignUp.error = null
            }
        }
    }

    private fun UpdatePasswordStrengthView(password: String) {
        val _progressBarStrength = findViewById<ProgressBar>(R.id.progressBarStrenght)
        val _tvPasswordStrength = findViewById<TextView>(R.id.tvPasswordStrength)

        if (TextView.VISIBLE != _tvPasswordStrength.visibility)
            return

        if (TextUtils.isEmpty(password)) {
            _tvPasswordStrength.text = ""
            _progressBarStrength.progress = 0
            return
        }

        val str = PasswordStrength.calculateStrength(password)
        _tvPasswordStrength.text = str.getText(this)
        _tvPasswordStrength.setTextColor(str.color)

        _progressBarStrength.progressDrawable.setColorFilter(str.color, android.graphics.PorterDuff.Mode.SRC_IN)

        when (str.getText(this)) {
            "Weak" ->  _progressBarStrength.progress = 25
            "Medium" -> _progressBarStrength.progress = 50
            "Strong" -> _progressBarStrength.progress = 75
            else -> _progressBarStrength.progress = 100
        }
    }


    private fun LoadDataUser() {

        dbUser.collection("tbUser")
            .get()
            .addOnSuccessListener { result ->
                arrUser.clear()
                for (document in result) {
                    val dataBaru = UserCls(
                        document.data.get("nik").toString(),
                        document.data.get("email").toString(),
                        document.data.get("username").toString(),
                        document.data.get("phoneNumber").toString(),
                        document.data.get("address").toString(),
                        document.data.get("favorite").toString(),
                        document.data.get("profilePicture").toString()
                    )
                    arrUser.add(dataBaru)
                }
            }
            .addOnFailureListener {
                Log.d("Firebase (tbUser)", it.message.toString())
            }
    }
}