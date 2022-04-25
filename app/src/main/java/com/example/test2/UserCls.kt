package com.example.test2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class UserCls (
    var NIK : String,
    var Email : String,
    var Username : String,
    var PhoneNumber : String,
    var Address : String,
    var Favorite : String,
    var ProfilePicture : String
        ) : Parcelable