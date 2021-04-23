package it.polito.mad.mad_car_pooling

import android.util.Log
import java.net.URI

data class Trip(
    private var car_photo_: String,
    private var departure_location_: String,
    private var arrivalLocation_: String,
    private var departureDateTime_: String,
    private var arrivalDateTime_: String,
    private var duration_: String,
    private var seats_: String,
    private var price_: String,
    private var description_: String
){
    private var id_: Int = -1
    val index: Int get() = id_
    val car_photo: String get() = car_photo_
    val departureLocation: String get() = departure_location_
    val arrivalLocation: String get() = arrivalLocation_
    val departureDateTime: String get() = departureDateTime_
    val arrivalDateTime: String get() = arrivalDateTime_
    val duration: String get() = duration_
    val seats: String get() = seats_
    val price: String get() = price_
    val description: String get() = description_


    companion object {
        @JvmStatic  private var id: Int = 0
    }

    init {
        id_ = id
        Log.d("POLITOMAD_Trip", "ID: $id_")
        Log.d("POLITOMAD_Trip", "Departure: " + this.departureLocation)
        id++
    }

}