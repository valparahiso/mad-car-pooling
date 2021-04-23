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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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