package it.polito.mad.mad_car_pooling.ui.trip_list

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.mad_car_pooling.Trip
import java.io.File

class TripListViewModel: ViewModel() {

     var trips = MutableLiveData<MutableList<Trip>>()
     var trip_ = MutableLiveData<Trip>()
     var newTrip_ = MutableLiveData<Boolean>() //flag to know if it's a new trip or an update

     fun initTrips(trips : MutableList<Trip>){
          this.trips = MediatorLiveData<MutableList<Trip>>().apply {
               value = trips
          }
     }

     //update values of the trip
     fun setTrip(trip :Trip, newTrip: Boolean){
          Log.d("POLITOMAD_Trip", trip.index.toString() + " CLICCATO")
          trip_.value = trip
          newTrip_.value = newTrip
     }

     //update the list of trips with updated fields
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
                    item.stops = trip.stops.filter { stop -> stop.saved && !stop.deleted}.toMutableList()

                    setTrip(item, false)
               }
               mutableListTrips.add(item)
          }

          //if the trip is not in the list then we put it in
          if(!find){
               mutableListTrips.add(trip)
          }

          initTrips(mutableListTrips)
     }

     //to delete the trip
     fun deleteTrip(index: Int){
          val mutableListTrips = mutableListOf<Trip>()
          val iterator = trips.value!!.listIterator()
          for (item in iterator) {
               if(item.index != index){
                    mutableListTrips.add(item)
               }
               else{
                    val photo = File(item.carPhoto)
                    if(photo.exists())
                         photo.delete()
               }
          }
          initTrips(mutableListTrips)
     }

}