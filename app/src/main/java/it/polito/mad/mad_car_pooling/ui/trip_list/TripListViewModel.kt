package it.polito.mad.mad_car_pooling.ui.trip_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.mad_car_pooling.Trip
import java.net.URI

class TripListViewModel: ViewModel() {

     var trips = MutableLiveData<MutableList<Trip>>()

     var trip_ = MutableLiveData<Trip>()

     fun initTrips(trips : MutableList<Trip>){
          this.trips = MediatorLiveData<MutableList<Trip>>().apply {
               value = trips
          }
     }

     fun setTrip(trip :Trip){
          Log.d("POLITOMAD_Trip", trip.index.toString() + " CLICCATO")
          trip_.value = trip
     }

     fun editTrip(trip :Trip){
          Log.d("POLITOMAD_Trip", trip.index.toString() + " EDITED")
          trip_.value = trip
          //TODO salvare la lista/ JSON/ Shared preferences
     }





}