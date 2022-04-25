package com.example.test2

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.test2.SplashScreenActivity.Companion.dataUser
import com.example.test2.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding : FragmentProfileBinding

    private lateinit var authUser : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        authUser = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (dataUser.ProfilePicture != "") {
            //Picasso.get().load(dataUser.ProfilePicture).into(binding.ivProfile)
            Glide.with(this).load(dataUser.ProfilePicture).into(binding.ivProfile)
        }

        binding.tvUsernameProfile.text = dataUser.Username
        binding.tvPhoneNumberProfile.text = dataUser.PhoneNumber

        binding.groupEditProfile.setOnClickListener {
            view.findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.groupChangePassword.setOnClickListener {
            view.findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }

        binding.groupLogout.setOnClickListener {
            val dialog : Dialog

            dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_logout)
            dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_rectangle_30dp)

            dialog.show()

            val _btnYes = dialog.findViewById<Button>(R.id.btnYesLogout)
            _btnYes.setOnClickListener {

                authUser.signOut()

                val logoutIntent = Intent(requireContext(), LoginActivity::class.java)
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(logoutIntent)
            }
            val _btnNo = dialog.findViewById<Button>(R.id.btnNoLogout)
            _btnNo.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

}