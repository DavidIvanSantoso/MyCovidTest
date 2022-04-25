package com.example.test2

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.test2.databinding.ActivityAdminBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

class AdminActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminBinding

    private lateinit var dbUser : FirebaseFirestore
    private var arrUser : ArrayList<UserCls> = ArrayList()

    private lateinit var dbRS : FirebaseFirestore
    private var arrRS : ArrayList<HospitalCls> = ArrayList()

    companion object {
        private val PICK_IMAGE_REQUEST = 1
    }

    private lateinit var storage : StorageReference

    private lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        dbUser = FirebaseFirestore.getInstance()
        dbRS = FirebaseFirestore.getInstance()


        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDeleteAllCollectionUser.setOnClickListener {
            dbUser.collection("tbUser").document().delete()
        }

        binding.btnDeleteAllCollectionRS.setOnClickListener {
            dbRS.collection("tbRS")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val email = document.data.get("nama").toString()
                        dbRS.collection("tbRS").document(email)
                            .delete()
                    }
                }
        }

        binding.btnAddAllCollectionUser.setOnClickListener {
            arrUser.forEach {
                dbUser.collection("tbUser").document(it.Email)
                    .set(it)
            }
        }

        binding.btnAddAllCollectionRS.setOnClickListener {
            AddDataRS()
            arrRS.forEach {
                dbRS.collection("tbRS").document(it.Nama)
                    .set(it)
            }
        }

        binding.btnLogoutAdmin.setOnClickListener {
            val logoutIntent = Intent(this@AdminActivity, LoginActivity::class.java)
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(logoutIntent)
        }

    }



    private fun AddDataRS() {
        val nama = resources.getStringArray(R.array.namarumahsakit)
        val alamat = resources.getStringArray(R.array.alamatrumahsakit)
        val notelp = resources.getStringArray(R.array.notelprumahsakit)
        val jamoper = resources.getStringArray(R.array.jamoperasionalrumahsakit)
        val kategoriTest = resources.getStringArray(R.array.kategoritestrumahsakit)
        val rating = resources.getStringArray(R.array.ratingrumahsakit)
        val hargaPCR = resources.getStringArray(R.array.hargapcrrumahsakit)
        val hargaAntigen = resources.getStringArray(R.array.hargaantigenrumahsakit)
        val gambar = resources.getStringArray(R.array.gambarrumahsakit)

        arrRS.clear()
        for (index in nama.indices) {
            val dataBaru = HospitalCls(
                nama[index],
                alamat[index],
                notelp[index],
                jamoper[index],
                kategoriTest[index],
                rating[index],
                hargaPCR[index],
                hargaAntigen[index],
                gambar[index]
            )
            arrRS.add(dataBaru)
        }
    }
}