package it.polito.mad.mad_car_pooling

import android.util.StringBuilderPrinter

data class Trip(
    val id: Int,
    var photo: String,
    var departureLocation: String,
    var arrivalLocation: String,
    var departureDate: String,
    var departureTime: String,
    var tripDuration: Int,
    var price:Int) {



}