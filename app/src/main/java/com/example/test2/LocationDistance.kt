package com.example.test2

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import java.util.*
import kotlin.coroutines.coroutineContext

class LocationDistance {

    companion object {
        fun CalculateDistance(location1 : String, location2 : String, ctx : Context) : Float {
            val geocoder : Geocoder
            geocoder = Geocoder(ctx, Locale.getDefault())

            val addresses : List<Address>
            addresses = geocoder.getFromLocationName(location1, 1)

            val lat1 = addresses.get(0).latitude
            val long1 = addresses.get(0).longitude

            val addresses2 : List<Address>
            addresses2 = geocoder.getFromLocationName(location2, 1)

            val lat2 = addresses2.get(0).latitude
            val long2 = addresses2.get(0).longitude

            val location1 = Location("locA")
            location1.latitude = lat1
            location1.longitude = long1

            val location2 = Location("locB")
            location2.latitude = lat2
            location2.longitude = long2

            return (location1.distanceTo(location2)/1000)
        }
    }

}