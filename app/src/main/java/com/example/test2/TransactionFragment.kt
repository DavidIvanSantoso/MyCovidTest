package com.example.test2

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test2.SplashScreenActivity.Companion.dataUser
import com.example.test2.databinding.FragmentTransactionBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransactionFragment : Fragment() {

    private lateinit var binding : FragmentTransactionBinding

    private lateinit var db : FirebaseFirestore

    private var arrTransaction : ArrayList<TransactionCls> = ArrayList()



    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransactionBinding.inflate(inflater, container, false)
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
         * @return A new instance of fragment TransactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransactionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LoadDataTransaction()

        binding.etSearchTransaction.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrContains = ArrayList<TransactionCls>()

                arrTransaction.forEach {
                    if (it.HospitalName.contains(p0.toString(), ignoreCase = true)) {
                        arrContains.add(it)
                    }
                }

                DisplayData(arrContains)
            }
        })
    }

    private fun LoadDataTransaction() {

        db.collection("tbTransaction")
            .get()
            .addOnSuccessListener { result ->

                for (document in result) {
                    var databaru=TransactionCls(
                        document.data.get("idTransaction").toString(),
                        document.data.get("tanggal").toString(),
                        document.data.get("username").toString(),
                        document.data.get("hospitalName").toString(),
                        document.data.get("hospitalImage").toString(),
                        document.data.get("hospitalAlamat").toString(),
                        document.data.get("kategoriTest").toString(),
                        document.data.get("jumlahBooking").toString(),
                        document.data.get("totalHargaBooking").toString(),
                        document.data.get("statusTest").toString(),
                        document.data.get("rating").toString()
                    )
                    arrTransaction.add(databaru)
                }

                val arrTransactionUser = ArrayList<TransactionCls>()
                arrTransaction.forEach {
                    if (it.Username == dataUser.Username) {
                        arrTransactionUser.add(it)
                    }
                }
                DisplayData(arrTransactionUser)
            }
    }

    private fun DisplayData(arrTransaction : ArrayList<TransactionCls>) {
        val transactionAdapter = AdapterTransaction(arrTransaction)

        binding.rvRSTransaction.adapter = transactionAdapter
        binding.rvRSTransaction.layoutManager = LinearLayoutManager(context)

        transactionAdapter.setOnItemClickCallback(object : AdapterTransaction.OnItemClickCallback {
            override fun onItemTransactionClicked(data: TransactionCls) {

                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.dialog_transaction)
                dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_rectangle_22dp)
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                val _ibClose = dialog.findViewById<ImageButton>(R.id.ibCloseDialogTransaction)
                val _ivRS = dialog.findViewById<ImageView>(R.id.ivRSDialogTransaction)
                val _tvKategori = dialog.findViewById<TextView>(R.id.tvKategoriDialogTransaction)
                val _tvNamaRS = dialog.findViewById<TextView>(R.id.tvNamaRSDialogTransaction)
                val _tvAlamatRS = dialog.findViewById<TextView>(R.id.tvAlamatRSDialogTransaction)
                val _tvStatusTest = dialog.findViewById<TextView>(R.id.tvStatusTestDialogTransaction)
                val _tvTanggalTest = dialog.findViewById<TextView>(R.id.tvTanggalTestDialogTransaction)
                val _tvTanggalExpired = dialog.findViewById<TextView>(R.id.tvTanggalExpiredDialogTransaction)
                val _ivQRCode = dialog.findViewById<ImageView>(R.id.ivQRCodeDialogTransaction)

                val context = requireContext()
                val imageRes = context.resources.getIdentifier(
                    data.HospitalImage,
                    "drawable",
                    context.packageName
                )
                Picasso.get().load(imageRes).into(_ivRS)

                //Date
                val day = data.Tanggal.substring(0,2).toInt()
                val month = data.Tanggal.substring(3,5).toInt()
                val year = data.Tanggal.substring(6,10).toInt()

                var dtTransaction = Date()
                var dtTransactionPlus1 = Date()
                var dtTransactionPlus3 = Date()

                val cTransaction = Calendar.getInstance()
                val cTransactionPlus1 = Calendar.getInstance()
                val cTransactionPlus3 = Calendar.getInstance()

                cTransaction.set(year, month, day)
                dtTransaction = cTransaction.time

                cTransactionPlus1.set(year, month, day)
                cTransactionPlus1.add(Calendar.DATE, 1)
                dtTransactionPlus1 = cTransactionPlus1.time

                cTransactionPlus3.set(year, month, day)
                cTransactionPlus3.add(Calendar.DATE, 3)
                dtTransactionPlus3 = cTransactionPlus1.time

                if(data.KategoriTest=="Antigen"){
                    _tvTanggalExpired.text=dtTransactionPlus1.toString()
                }
                else if(data.KategoriTest=="PCR"){
                    _tvTanggalExpired.text=dtTransactionPlus3.toString()
                }

                _tvTanggalTest.text=dtTransaction.toString()
                _tvKategori.text = data.KategoriTest
                _tvNamaRS.text = data.HospitalName
                _tvAlamatRS.text = data.HospitalAlamat

                _tvStatusTest.text = data.StatusTest
                when (data.StatusTest) {
                    "Booked" -> _tvStatusTest.setTextColor(Color.BLUE)
                    "Active" -> _tvStatusTest.setTextColor(Color.GREEN)
                    "Expired" -> _tvStatusTest.setTextColor(Color.RED)
                }

                val qrCode = QRCode.GenerateQRCode(data)
                _ivQRCode.setImageBitmap(qrCode)


                dialog.show()

                _ibClose.setOnClickListener {
                    dialog.dismiss()
                }

            }
        })
    }
}