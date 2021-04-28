package it.polito.mad.mad_car_pooling

import android.util.Log
import java.net.URI
import kotlin.properties.Delegates

data class Trip(
        var carPhoto: String,
        var departureLocation: String,
        var arrivalLocation: String,
        var departureDateTime: String,
        var duration: String,
        var seats: String,
        var price: String,
        var description: String,
        var stops : MutableList<Stop>,
        var index: Int
){

    constructor(
        carPhoto: String,
        departureLocation: String,
        arrivalLocation: String,
        departureDateTime: String,
        duration: String,
        seats: String,
        price: String,
        description: String,
        stops : MutableList<Stop>) : this(carPhoto,departureLocation, arrivalLocation, departureDateTime, duration, seats, price, description, stops, 0){
            this.index = id+1
            id++
        Log.e("POLITOMAD_trip", this.index.toString())
    }

    companion object {
        @JvmStatic  private var id: Int = 0
    }

    /*init {
        Log.e("POLITOMAD_Trip", "ID: $id_ Trip Created")
        id++
    }*/

    fun setCounter(){
        if(id < this.index)
            id = this.index
    }
    fun addStop(stop_location: String, stop_datetime : String){
        stops.add(Stop(stop_location, stop_datetime, true, deleted = false))
    }
}
