package com.example.test2

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test2.SplashScreenActivity.Companion.dataUser
import com.example.test2.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding : FragmentHomeBinding

    private lateinit var db : FirebaseFirestore

    private var arrRS : ArrayList<HospitalCls> = ArrayList()

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
        binding = FragmentHomeBinding.inflate(inflater, container, false)
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvWelcome.setText("Welcome,"+ dataUser.Username)

        LoadDataRS(db)

        binding.etSearchHome.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrContains = ArrayList<HospitalCls>()

                arrRS.forEach {
                    if (it.Nama.contains(p0.toString(), ignoreCase = true)) {
                        arrContains.add(it)
                    }
                }

                DisplayData(arrContains)
            }
        })
    }

    private fun LoadDataRS(dbRS : FirebaseFirestore) {
        dbRS.collection("tbRS")
            .get()
            .addOnSuccessListener { result ->
                arrRS.clear()

                for (document in result) {
                    val dataBaru = HospitalCls(
                        document.data.get("nama").toString(),
                        document.data.get("alamat").toString(),
                        document.data.get("noTelp").toString(),
                        document.data.get("jamOperasional").toString(),
                        document.data.get("kategoriTest").toString(),
                        document.data.get("rating").toString(),
                        document.data.get("hargaPCR").toString(),
                        document.data.get("hargaAntigen").toString(),
                        document.data.get("gambar").toString()
                    )
                    arrRS.add(dataBaru)
                }
                DisplayData(arrRS)
            }
            .addOnFailureListener {
                Log.d("Firebase (tbRS)", it.message.toString())
            }
    }

    private fun DisplayData(arrRS : ArrayList<HospitalCls>) {
        val rsAdapter = AdapterRumahSakit(arrRS)

        binding.rvRSHome.adapter = rsAdapter
        binding.rvRSHome.layoutManager = LinearLayoutManager(context)

        rsAdapter.setOnItemClickCallback(object : AdapterRumahSakit.OnItemClickCallback {
            override fun onFavoriteClicked(data: HospitalCls) {

                when (dataUser.Favorite.contains(data.Nama)) {
                    true -> {
                        dataUser.Favorite = dataUser.Favorite.replace("${data.Nama},", "")
                    }
                    false -> {
                        dataUser.Favorite += "${data.Nama},"
                    }
                }

                db.collection("tbUser").document(dataUser.Email)
                    .update("favorite", dataUser.Favorite)
            }

            override fun onItemRSClicked(data: HospitalCls) {

                val dialog : Dialog

                dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.dialog_information_rs)
                dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_rectangle_22dp)
                //dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                val _ivRS = dialog.findViewById<ImageView>(R.id.ivRSDialog)
                val _tvNama = dialog.findViewById<TextView>(R.id.tvNamaRSDialog)
                val _tvAlamat = dialog.findViewById<TextView>(R.id.tvAlamatRSDialog)
                val _tvNoTelp = dialog.findViewById<TextView>(R.id.tvNoTelpRSDialog)
                val _tvHargaPCR = dialog.findViewById<TextView>(R.id.tvHargaPCRDialog)
                val _tvHargaAntigen = dialog.findViewById<TextView>(R.id.tvHargaAntigenDialog)
                val _tvJamOperasional = dialog.findViewById<TextView>(R.id.tvJamOperasionalDialog)
                val _tvRating = dialog.findViewById<TextView>(R.id.tvRatingRSDialog)

                val context = requireContext()
                val imageres = context.resources.getIdentifier(
                    data.Gambar,
                    "drawable",
                    context.packageName
                )

                Picasso.get().load(imageres).into(_ivRS)
                _tvNama.text = data.Nama
                _tvAlamat.text = data.Alamat
                _tvNoTelp.text = data.NoTelp
                _tvHargaPCR.text = "Rp. ${data.HargaPCR}"
                _tvHargaAntigen.text = "Rp. ${data.HargaAntigen}"
                _tvJamOperasional.text = data.JamOperasional
                _tvRating.text = data.Rating

                val _groupHargaPCR = dialog.findViewById<LinearLayout>(R.id.groupHargaPCR)
                val _groupHargaAntigen = dialog.findViewById<LinearLayout>(R.id.groupHargaAntigen)

                when (data.KategoriTest) {
                    "0" -> {
                        _groupHargaPCR.visibility = ViewGroup.VISIBLE
                        _groupHargaAntigen.visibility = ViewGroup.VISIBLE
                    }
                    "1" -> {
                        _groupHargaPCR.visibility = ViewGroup.VISIBLE
                        _groupHargaAntigen.visibility = ViewGroup.GONE
                    }
                    "2" -> {
                        _groupHargaPCR.visibility = ViewGroup.GONE
                        _groupHargaAntigen.visibility = ViewGroup.VISIBLE
                    }
                }

                dialog.show()

                val _ibClose = dialog.findViewById<ImageButton>(R.id.ibCloseDialogInformationRS)
                _ibClose.setOnClickListener {
                    dialog.dismiss()
                }

                val _ibNext = dialog.findViewById<ImageButton>(R.id.ibNexttoBooking)
                _ibNext.setOnClickListener {
                    dialog.dismiss()

                    //kirim data rs
                    val toBookingFragment = HomeFragmentDirections.actionHomeFragmentToBookingFragment(
                        data)
                    view?.findNavController()?.navigate(toBookingFragment)
                }

            }

        })
    }


}