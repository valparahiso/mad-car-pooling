package it.polito.mad.mad_car_pooling.ui.trip_details

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import it.polito.mad.mad_car_pooling.R
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

        viewModel.trip_.observe(viewLifecycleOwner, Observer { trip ->
            // Update the selected filters UI
            departureLocation.text = trip.departureLocation
            arrivalLocation.text = trip.arrivalLocation
            duration.text = trip.duration
            seats.text = trip.seats
            price.text = trip.price
            description.text = trip.description
            departureDateTime.text = trip.departureDateTime
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