package com.example.test2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TransactionCls (
    var IdTransaction : String,
    var Tanggal : String,
    var Username : String,
    var HospitalName : String,
    var HospitalImage:String,
    var HospitalAlamat:String,
    var KategoriTest : String,
    var JumlahBooking:String,
    var TotalHargaBooking:String,
//    //0 = Booked, 1 = Active, 2 = Expired
    var StatusTest : String,
    var Rating : String
        ) : Parcelable