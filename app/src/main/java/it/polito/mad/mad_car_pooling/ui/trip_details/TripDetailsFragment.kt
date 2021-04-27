package it.polito.mad.mad_car_pooling.ui.trip_details

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.mad.mad_car_pooling.R
import it.polito.mad.mad_car_pooling.StopAdapter
import it.polito.mad.mad_car_pooling.TripAdapter
import it.polito.mad.mad_car_pooling.ui.trip_list.TripListViewModel

class TripDetailsFragment : Fragment() {

    private val viewModel : TripListViewModel by activityViewModels()
    private lateinit var departureLocation : TextView
    private lateinit var arrivalLocation : TextView
    private lateinit var duration : TextView
    private lateinit var seats : TextView
    private lateinit var price : TextView
    private lateinit var description : TextView
    private lateinit var departureDateTime : TextView
    private lateinit var showStopsCard : CardView
    private lateinit var showStopsLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_trip_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        departureLocation = view.findViewById(R.id.departure)
        arrivalLocation = view.findViewById(R.id.arrival)
        duration = view.findViewById(R.id.estimateTrip)
        seats = view.findViewById(R.id.availableSeats)
        price = view.findViewById(R.id.price)
        description = view.findViewById(R.id.description)
        departureDateTime = view.findViewById(R.id.departure_date_time)
        showStopsLayout = view.findViewById(R.id.show_stops_text)
        showStopsCard = view.findViewById(R.id.show_stops_card)

        showStopsCard.setOnClickListener{
            if(showStopsLayout.visibility == GONE) showStopsLayout.visibility = VISIBLE
            else showStopsLayout.visibility = GONE
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.stops_details)
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.trip_.observe(viewLifecycleOwner, Observer { trip ->
            // Update the selected filters UI
            departureLocation.text = trip.departureLocation
            arrivalLocation.text = trip.arrivalLocation
            duration.text = trip.duration
            seats.text = trip.seats
            price.text = trip.price
            description.text = trip.description
            departureDateTime.text = trip.departureDateTime
            if(trip.stops.size == 0) showStopsCard.visibility = GONE //TODO verificare che funzioni e migliorare il design
            else{
                showStopsCard.visibility = VISIBLE
                recyclerView.adapter = StopAdapter(trip.stops, this)
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.trip_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        return when(item.itemId){
            R.id.edit -> {
                findNavController().navigate(R.id.action_details_trip_fragment_to_nav_edit_trip_details)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}