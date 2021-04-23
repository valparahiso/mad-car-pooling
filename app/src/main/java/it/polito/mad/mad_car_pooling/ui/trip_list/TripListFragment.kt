package it.polito.mad.mad_car_pooling.ui.trip_list

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.mad_car_pooling.R
import it.polito.mad.mad_car_pooling.Trip
import it.polito.mad.mad_car_pooling.TripAdapter
import org.json.JSONObject
import java.net.URI

class TripListFragment : Fragment() {

    private val viewModel : TripListViewModel by activityViewModels()
    private lateinit var sharedPref: SharedPreferences
    private var trips: MutableList<Trip> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //jsonObject for default values
        var jsonObjectTrip = JSONObject()
        jsonObjectTrip.put("car_photo","")
        jsonObjectTrip.put("departure_location", "Torino")
        jsonObjectTrip.put("arrival_location", "Milano")
        jsonObjectTrip.put("departure_date_time", "20/02/2012 15:20")
        jsonObjectTrip.put("arrival_date_time", "01/01/1990 12:20")
        jsonObjectTrip.put("duration", "10")
        jsonObjectTrip.put("seats", "12")
        jsonObjectTrip.put("price", "12")
        jsonObjectTrip.put("description", "descr")
        var jsonObjectTripSet : Set<String> = listOf<String>(jsonObjectTrip.toString()).toSet()


        //TODO: DA METTERE QUA??
        sharedPref = requireActivity().getSharedPreferences("trip_list", Context.MODE_PRIVATE)

        val trips_json = sharedPref.getStringSet("trips", jsonObjectTripSet)?.toList()

        val iterator = trips_json!!.listIterator()
        for (item in iterator) {
            var item_json = JSONObject(item)
            trips.add(Trip(item_json.get("car_photo")  as String,
                    item_json.get("departure_location")  as String,
                    item_json.get("arrival_location")  as String,
                    item_json.get("departure_date_time")  as String,
                    item_json.get("arrival_date_time")  as String,
                    item_json.get("duration")  as String,
                    item_json.get("seats")  as String,
                    item_json.get("price")  as String,
                    item_json.get("description") as String))
        }

        viewModel.initTrips(trips);


        return inflater.inflate(R.layout.fragment_trip_list, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.tripListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.trips.observe(viewLifecycleOwner, Observer {
            list-> recyclerView.adapter = TripAdapter(list,this)
        })

    }

    fun updateTrip(trip :Trip){
        viewModel.setTrip(trip)
    }


}