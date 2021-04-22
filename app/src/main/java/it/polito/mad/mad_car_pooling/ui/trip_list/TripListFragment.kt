package it.polito.mad.mad_car_pooling.ui.trip_list

import android.os.Bundle
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
import java.net.URI


class TripListFragment : Fragment() {
    /*
    private val trips: List<Trip> = listOf(
        Trip(URI(""), "Torino", "Milano", "10/03/2020 10:20", "10/03/2020 10:30", 0.10, 100, 30.20, ""),
        Trip(URI(""), "Milano", "Treviso", "10/03/2020 10:20", "10/03/2020 10:30", 0.10, 100, 30.20, ""),
        Trip(URI(""), "Roma", "Udine", "10/03/2020 10:20", "10/03/2020 10:30", 0.10, 100, 30.20, ""),
        Trip(URI(""), "Napoli", "Vibo Valentia", "10/03/2020 10:20", "10/03/2020 10:30", 0.10, 100, 30.20, ""))
     */


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

        viewModel.tripsList.observe(viewLifecycleOwner, Observer {
            list-> recyclerView.adapter = TripAdapter(list,this)
        })

    }

    fun updateTrip(trip :Trip){
        viewModel.updateTrip(trip)
    }


}