package com.example.test2

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.test2.SplashScreenActivity.Companion.dataUser
import com.example.test2.databinding.FragmentChangePasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChangePasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChangePasswordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var dbUser : FirebaseFirestore

    private lateinit var authUser : FirebaseAuth

    private lateinit var binding : FragmentChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        dbUser = FirebaseFirestore.getInstance()
        authUser = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        val view = binding.root
        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChangePasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChangePasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ibBacktoProfileChangePassword.setOnClickListener {
            view.findNavController().navigate(R.id.action_changePasswordFragment_to_profileFragment)
        }

        //cek strong password
        binding.etNewPasswordChangePassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                UpdatePasswordStrengthView(
                    p0.toString(),
                    binding.progressBarStrenghtChangePassword,
                    binding.tvPasswordStrengthChangePassword
                )
            }
        })

        binding.btnSaveChangePassword.setOnClickListener {

            val newPassword = binding.etNewPasswordChangePassword.text.toString()
            val confirmPassword = binding.etConfirmPasswordChangePassword.text.toString()

            when {
                TextUtils.isEmpty(newPassword.trim { it <= ' '}) -> {
                    Toast.makeText(
                        requireContext(),
                        "Enter New Password",
                        Toast.LENGTH_LONG
                    ).show()
                }

                TextUtils.isEmpty(confirmPassword.trim { it <= ' '}) -> {
                    Toast.makeText(
                        requireContext(),
                        "Enter Confirm Password",
                        Toast.LENGTH_LONG
                    ).show()
                }

                newPassword != confirmPassword -> {
                    Toast.makeText(
                        requireContext(),
                        "New Password and Confirm Password is not same",
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {
                    authUser.currentUser!!.updatePassword(newPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Update Password Successful",
                                    Toast.LENGTH_LONG
                                ).show()

                                view.findNavController().navigate(R.id.action_changePasswordFragment_to_profileFragment)
                            }
                            else {
                                Toast.makeText(
                                    requireContext(),
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        }
    }

    private fun UpdatePasswordStrengthView(password: String, _progressBarStrength : ProgressBar, _tvPasswordStrength : TextView) {
        if (TextView.VISIBLE != _tvPasswordStrength.visibility)
            return

        if (TextUtils.isEmpty(password)) {
            _tvPasswordStrength.text = ""
            _progressBarStrength.progress = 0
            return
        }

        val str = PasswordStrength.calculateStrength(password)
        _tvPasswordStrength.text = str.getText(requireContext())
        _tvPasswordStrength.setTextColor(str.color)

        _progressBarStrength.progressDrawable.setColorFilter(str.color, android.graphics.PorterDuff.Mode.SRC_IN)

        when (str.getText(requireContext())) {
            "Weak" ->  _progressBarStrength.progress = 25
            "Medium" -> _progressBarStrength.progress = 50
            "Strong" -> _progressBarStrength.progress = 75
            else -> _progressBarStrength.progress = 100
        }
    }

}