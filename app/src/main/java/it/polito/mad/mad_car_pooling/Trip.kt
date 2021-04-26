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
        var stops : MutableList<Stop>
){
    private var id_: Int = id
    val index : Int get() = id_

    companion object {
        @JvmStatic  private var id: Int = 0
    }

    init {
        Log.d("POLITOMAD_Trip", "ID: $id_ Trip Created")
        id++
    }

    fun addStop(stop_location: String, stop_datetime : String){
        stops.add(Stop(stop_location, stop_datetime))
    }
}
