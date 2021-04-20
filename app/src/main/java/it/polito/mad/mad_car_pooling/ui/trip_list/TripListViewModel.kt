package it.polito.mad.mad_car_pooling.ui.trip_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.mad_car_pooling.Trip
import java.net.URI

class TripListViewModel: ViewModel() {

     private val trips: List<Trip> = listOf(
             Trip(URI(""), "Torino", "Milano", "10/03/2020 10:20", "10/03/2020 10:30", 0.10, 100, 30.20, ""),
             Trip(URI(""), "Milano", "Treviso", "10/03/2020 10:20", "10/03/2020 10:30", 0.10, 100, 30.20, ""),
             Trip(URI(""), "Roma", "Udine", "10/03/2020 10:20", "10/03/2020 10:30", 0.10, 100, 30.20, ""),
             Trip(URI(""), "Napoli", "Vibo Valentia", "10/03/2020 10:20", "10/03/2020 10:30", 0.10, 100, 30.20, ""))

     val tripsList : LiveData<List<Trip>> = MediatorLiveData<List<Trip>>().apply {
          value = trips
     }

     var trip_ = MutableLiveData<Trip>()

     fun updateTrip(trip :Trip){
          Log.d("POLITOMAD_Trip", trip.index.toString() + " CLICCATO")
          trip_.value = trip
     }





}