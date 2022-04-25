package com.example.test2

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import com.example.test2.SplashScreenActivity.Companion.dataUser
import com.example.test2.databinding.FragmentBookingBinding
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
 * Use the [BookingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding : FragmentBookingBinding
    private var arrTransaksi=ArrayList<TransactionCls>()

    private var count = 0
    private var total : Int = 0
    private var idTransaction:String="-1"

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
        binding = FragmentBookingBinding.inflate(inflater, container, false)
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
         * @return A new instance of fragment BookingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic

        private lateinit var fromHomeOrFavorite : String

        fun newInstance(param1: String, param2: String) =
            BookingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //database
        db= FirebaseFirestore.getInstance()
        loadDataTransaksi(db)


        val dataRS = BookingFragmentArgs.fromBundle(arguments as Bundle).dataRS

        val context = requireContext()
        val imageres = context.resources.getIdentifier(
            dataRS.Gambar,
            "drawable",
            context.packageName
        )
        Picasso.get().load(imageres).into(binding.ivRSBooking)

        CalculateTotal(dataRS.HargaPCR, dataRS.HargaAntigen)

        binding.tvNamaRSBooking.text = dataRS.Nama
        binding.tvAlamatRSBooking.text = dataRS.Alamat
        binding.tvHargaPCRBooking.text = "Rp. ${dataRS.HargaPCR}"
        binding.tvHargaAntigenBooking.text = "Rp. ${dataRS.HargaAntigen}"

        when (dataRS.KategoriTest) {
            "0" -> {
                binding.tvKategoriPCRBooking.visibility = ViewGroup.VISIBLE
                binding.tvKategoriAntigenBooking.visibility = ViewGroup.VISIBLE
                binding.groupPCRBooking.visibility = ViewGroup.VISIBLE
                binding.groupAntigenBooking.visibility = ViewGroup.VISIBLE
            }
            "1" -> {
                binding.tvKategoriPCRBooking.visibility = ViewGroup.VISIBLE
                binding.tvKategoriAntigenBooking.visibility = ViewGroup.GONE
                binding.groupPCRBooking.visibility = ViewGroup.VISIBLE
                binding.groupAntigenBooking.visibility = ViewGroup.GONE

                binding.rbAntigenBooking.isEnabled = false

                binding.rbPCRBooking.isChecked = true
                binding.rbAntigenBooking.isChecked = false

            }
            "2" -> {
                binding.tvKategoriPCRBooking.visibility = ViewGroup.GONE
                binding.tvKategoriAntigenBooking.visibility = ViewGroup.VISIBLE
                binding.groupPCRBooking.visibility = ViewGroup.GONE
                binding.groupAntigenBooking.visibility = ViewGroup.VISIBLE

                binding.rbPCRBooking.isEnabled = false

                binding.rbPCRBooking.isChecked = false
                binding.rbAntigenBooking.isChecked = true
            }
        }

        binding.tvJumlahBooking.text = count.toString()

        binding.ibMinusBooking.setOnClickListener {
            if (count != 0) {
                count--
            }
            binding.tvJumlahBooking.text = count.toString()
            CalculateTotal(dataRS.HargaPCR, dataRS.HargaAntigen)
        }

        binding.ibPlusBooking.setOnClickListener {
            count++
            binding.tvJumlahBooking.text = count.toString()
            CalculateTotal(dataRS.HargaPCR, dataRS.HargaAntigen)
        }

        binding.rgKategoriTestBooking.setOnCheckedChangeListener { radioGroup, i ->
            CalculateTotal(dataRS.HargaPCR, dataRS.HargaAntigen)
        }

        binding.btnNexttoSchedule.setOnClickListener {

            if (count == 0) {
                Toast.makeText(
                    requireContext(),
                    "check again!!!",
                    Toast.LENGTH_LONG
                ).show()
            }
            else {
                val dialog : Dialog

                dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.dialog_schedule_booking)
                dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_rectangle_30dp)

                dialog.show()

                val _ibClose = dialog.findViewById<ImageButton>(R.id.ibCloseDialogSchedule)
                _ibClose.setOnClickListener {
                    dialog.dismiss()
                }

                val _actvTanggal = dialog.findViewById<AutoCompleteTextView>(R.id.actvTanggalDialogSchedule)
                _actvTanggal.setOnClickListener {
                    var cal = Calendar.getInstance()
                    var year = cal.get(Calendar.YEAR)
                    var month = cal.get(Calendar.MONTH)
                    var day = cal.get(Calendar.DAY_OF_MONTH)

                    val dpd = DatePickerDialog(
                        requireContext()
                        , DatePickerDialog.OnDateSetListener { datePicker, mYear, mMonth, mDay ->
                            _actvTanggal.setText("$mDay/$mMonth/$mYear")
                        }, year, month, day)
                    dpd.datePicker.minDate = System.currentTimeMillis() - 1000

                    dpd.show()

                    val _btnClose = dialog.findViewById<ImageButton>(R.id.ibCloseDialogSchedule)
                    _btnClose.setOnClickListener {
                        dialog.dismiss()
                    }


                    //cek dialog
                    val _btnBookNow = dialog.findViewById<Button>(R.id.btnBookNowDialogSchedule)
                    _btnBookNow.setOnClickListener {
                        if (_actvTanggal.text.toString() == "dd/mm/YY") {
                            Toast.makeText(
                                requireContext(),
                                "check again!!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else {
                            dialog.dismiss()
                            arrTransaksi.forEach {
                                if (it.IdTransaction>idTransaction){
                                    idTransaction=it.IdTransaction
                                }
                            }
                            var _getKategori=""
                            when {
                                binding.rbAntigenBooking.isChecked==true -> _getKategori="Antigen"
                                binding.rbPCRBooking.isChecked==true-> _getKategori="PCR"
                            }

                            val dataTransaction=TransactionCls(
                                (idTransaction.toInt()+1).toString(),
                                _actvTanggal.text.toString(),
                                dataUser.Username,
                                dataRS.Nama,
                                dataRS.Gambar,
                                dataRS.Alamat,
                                _getKategori,
                                count.toString(),
                                total.toString(),
                                "",
                                "",
                            )
                            val toCheckoutFragment=BookingFragmentDirections.actionBookingFragmentToCheckoutFragment(dataRS,dataTransaction)
                            view.findNavController().navigate(toCheckoutFragment)
                        }
                    }

                }

            }

        }
    }

    private fun loadDataTransaksi(db: FirebaseFirestore) {
        db.collection("tbTransaction").get()
            .addOnSuccessListener { result->
                for(document in result) {
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
                    arrTransaksi.add(databaru)
                }

            }
    }

    private fun CalculateTotal(hargaPCR : String, hargaAntigen : String) {
        total = 0

        when (binding.rbPCRBooking.isChecked) {
            true -> {
                total = count * hargaPCR.toInt()
            }
            false -> {
                total = count * hargaAntigen.toInt()
            }
        }

        binding.tvTotalRupiahBooking.text = "Rp. $total"
    }
}