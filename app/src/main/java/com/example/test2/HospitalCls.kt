package com.example.test2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class HospitalCls (
    var Nama : String,
    var Alamat : String,
    var NoTelp : String,
    var JamOperasional : String,
    var KategoriTest : String,
    var Rating : String,
    var HargaPCR : String,
    var HargaAntigen : String,
    var Gambar : String
        ): Parcelable