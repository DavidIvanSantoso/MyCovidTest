package com.example.test2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import com.example.test2.SplashScreenActivity.Companion.dataUser
import com.example.test2.databinding.FragmentCheckoutBinding
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CheckoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckoutFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding : FragmentCheckoutBinding
    private lateinit var builder:AlertDialog.Builder
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
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
         * @return A new instance of fragment CheckoutFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CheckoutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db= FirebaseFirestore.getInstance()
        val dataRS = CheckoutFragmentArgs.fromBundle(arguments as Bundle).dataRS
        val dataTransanction=CheckoutFragmentArgs.fromBundle(arguments as Bundle).dataTransaction

        binding.etNamaRS.setText(dataRS.Nama)
        binding.etNama.setText(dataUser.Username)
        binding.etEmail.setText(dataUser.Email)
        binding.etJumlahBook.setText(dataTransanction.JumlahBooking)
        binding.etTotalPembayaran.setText(dataTransanction.TotalHargaBooking)
        binding.etKategoriTest.setText(dataTransanction.KategoriTest)

        //builder
        binding.btConfirmCheckout.setOnClickListener {
            builder= AlertDialog.Builder(requireContext())
            builder.setTitle("Alert!!")
            builder.setMessage("Apakah sudah yakin dengan pilihan anda?")
            builder.setCancelable(true)
            builder.setPositiveButton("Yes"){ dialogInterface,it->
                TambahData(db,dataTransanction)
                //pindah
                view?.findNavController()?.navigate(R.id.action_checkoutFragment_to_transactionFragment)
            }
            builder.setNegativeButton("No"){
                    dialogInterface,it->
                dialogInterface.cancel()
            }
                .show()
        }
    }

    private fun TambahData(db: FirebaseFirestore,dataTransaction:TransactionCls )
    {
        db.collection("tbTransaction").document(dataTransaction.IdTransaction).set(
            dataTransaction
        )
    }
}