package com.example.test2

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.test2.SplashScreenActivity.Companion.dataUser
import com.example.test2.databinding.FragmentEditProfileBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var dbUser : FirebaseFirestore

    private lateinit var binding : FragmentEditProfileBinding

    private lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        dbUser = FirebaseFirestore.getInstance()

        imageUri = Uri.parse("")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
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
         * @return A new instance of fragment EditProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (dataUser.ProfilePicture != "") {
            //Picasso.get().load(dataUser.ProfilePicture).into(binding.ivEditProfile)
            Glide.with(this).load(dataUser.ProfilePicture).into(binding.ivEditProfile)
        }

        binding.ibBacktoProfileEditProfile.setOnClickListener {
            view.findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }

        binding.tvChangePicture.setOnClickListener {
            ChooseImageGallery()
        }

        binding.etNikEditProfile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length < 16) {
                    binding.tilNIKEditProfile.error = "Too Short"
                } else if (p0!!.length > 16) {
                    binding.tilNIKEditProfile.error = "Too Long"
                } else {
                    binding.tilNIKEditProfile.error = null
                }
            }
        })



        binding.etNikEditProfile.setText(dataUser.NIK)
        binding.etUsernameEditProfile.setText(dataUser.Username)
        binding.etPhoneNumberEditProfile.setText(dataUser.PhoneNumber)
        binding.etAddressEditProfile.setText(dataUser.Address)



        binding.btnSaveEditProfile.setOnClickListener {
            if (binding.etNikEditProfile.text.toString() == "" ||
                binding.etUsernameEditProfile.text.toString() == "" ||
                binding.etPhoneNumberEditProfile.text.toString() == "" ||
                binding.etAddressEditProfile.text.toString() == "" ||
                binding.etNikEditProfile.text!!.length < 16 || binding.etNikEditProfile.text!!.length > 16) {
                Toast.makeText(
                    requireContext(),
                    "Check Again!!!",
                    Toast.LENGTH_LONG
                ).show()
            }
            else {
                if (imageUri != Uri.parse("")) {
                    UploadImage()
                }

                dataUser.NIK = binding.etNikEditProfile.text.toString()
                dataUser.Username = binding.etUsernameEditProfile.text.toString()
                dataUser.PhoneNumber = binding.etPhoneNumberEditProfile.text.toString()
                dataUser.Address = binding.etAddressEditProfile.text.toString()

                dbUser.collection("tbUser").document(dataUser.Email)
                    .update(mapOf(
                        "nik" to dataUser.NIK,
                        "username" to dataUser.Username,
                        "phoneNumber" to dataUser.PhoneNumber,
                        "address" to dataUser.Address
                    ))
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Your Profile Updated",
                            Toast.LENGTH_LONG
                        ).show()

                        view.findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                    }
                    .addOnFailureListener {
                        Log.d("Firebase (tbUser)", it.message.toString())
                    }
            }
        }


    }

    private fun ChooseImageGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            Picasso.get().load(imageUri).into(binding.ivEditProfile)
        }
    }

    private fun GetFileExtension() : String {
        val CR : ContentResolver = requireContext().contentResolver
        val mime : MimeTypeMap = MimeTypeMap.getSingleton()
        val ext = mime.getExtensionFromMimeType(CR.getType(imageUri)).toString()
        return ext
    }

    private fun UploadImage() {
        Log.d("ext", GetFileExtension())
        val fileName : String = dataUser.Email + "." + GetFileExtension()
        val storageReference = FirebaseStorage.getInstance().getReference().child("images/user/$fileName")
        storageReference.putFile(imageUri)

        storageReference.downloadUrl
            .addOnSuccessListener {
                dataUser.ProfilePicture = it.toString()
                dbUser.collection("tbUser").document(dataUser.Email)
                    .update("profilePicture", dataUser.ProfilePicture)
            }


    }


}