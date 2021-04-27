package it.polito.mad.mad_car_pooling.ui.trip_list

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.mad_car_pooling.Trip

class TripListViewModel: ViewModel() {

     var trips = MutableLiveData<MutableList<Trip>>()

     var trip_ = MutableLiveData<Trip>()
     var newTrip_ = MutableLiveData<Boolean>()

     fun initTrips(trips : MutableList<Trip>){
          this.trips = MediatorLiveData<MutableList<Trip>>().apply {
               value = trips
          }
     }

     fun setTrip(trip :Trip, newTrip: Boolean){
          Log.d("POLITOMAD_Trip", trip.index.toString() + " CLICCATO")
          trip_.value = trip
          newTrip_.value = newTrip
     }

     fun editTrip(trip :Trip, index: Int){
          var find = false
          val mutableListTrips = mutableListOf<Trip>()
          val iterator = trips.value!!.listIterator()
          for (item in iterator) {
               if(item.index == index){
                    find = true
                    item.departureLocation = trip.departureLocation
                    item.arrivalLocation = trip.arrivalLocation
                    item.departureDateTime = trip.departureDateTime
                    item.duration = trip.duration
                    item.seats = trip.seats
                    item.price = trip.price
                    item.description = trip.description
                    item.carPhoto = trip.carPhoto
                    item.stops = trip.stops.filter { stop -> stop.saved }.toMutableList()

                    setTrip(item, false)
               }
               mutableListTrips.add(item)
          }

          if(!find){
               mutableListTrips.add(trip)
          }

          initTrips(mutableListTrips)

          Log.d("POLITOMAD_Trip", trip.index.toString() + " EDITED")
     }

     fun deleteTrip(index: Int){
          val mutableListTrips = mutableListOf<Trip>()
          val iterator = trips.value!!.listIterator()
          for (item in iterator) {
               if(item.index != index){
                    mutableListTrips.add(item)
               }
          }
          initTrips(mutableListTrips)
     }



}