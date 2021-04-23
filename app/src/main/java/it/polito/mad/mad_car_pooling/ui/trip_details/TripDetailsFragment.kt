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
    private lateinit var departureTv : TextView
    private lateinit var arrivalTv : TextView
    private lateinit var estimateTripDurationTv : TextView
    private lateinit var availableSeatTv : TextView
    private lateinit var priceTv : TextView
    private lateinit var descriptionTv : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_trip_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        departureTv = view.findViewById(R.id.departure)
        arrivalTv = view.findViewById(R.id.arrival)
        estimateTripDurationTv = view.findViewById(R.id.estimateTrip)
        availableSeatTv = view.findViewById(R.id.availableSeats)
        priceTv = view.findViewById(R.id.price)
        descriptionTv = view.findViewById(R.id.description)

        viewModel.trip_.observe(viewLifecycleOwner, Observer { trip ->
            // Update the selected filters UI
            departureTv.text = trip.departureLocation
            arrivalTv.text = trip.arrivalLocation
            estimateTripDurationTv.text = ""
            availableSeatTv.text = trip.seats.toString()
            priceTv.text = trip.price.toString()
            descriptionTv.text = trip.description
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