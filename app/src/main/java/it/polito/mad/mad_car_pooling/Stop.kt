package it.polito.mad.mad_car_pooling

import android.util.Log

data class Stop(
        var locationName: String,
        var stopDateTime: String,
        var saved: Boolean
){
        init {
            Log.d("POLITOMAD_Stop", "Location Name: " + this.locationName)
                Log.d("POLITOMAD_Stop", "Date Time: " + this.stopDateTime)
        }
}
