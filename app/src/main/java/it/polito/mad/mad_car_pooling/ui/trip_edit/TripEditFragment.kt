package it.polito.mad.mad_car_pooling.ui.trip_edit

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import it.polito.mad.mad_car_pooling.R
import it.polito.mad.mad_car_pooling.Trip
import it.polito.mad.mad_car_pooling.ui.trip_list.TripListViewModel

class TripEditFragment : Fragment() {

    private val viewModel: TripListViewModel by activityViewModels()
    private lateinit var departureTv: TextView
    private lateinit var arrivalTv: TextView
    private lateinit var estimateTripDurationTv: TextView
    private lateinit var availableSeatTv: TextView
    private lateinit var priceTv: TextView
    private lateinit var descriptionTv: TextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_trip_edit, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        departureTv = view.findViewById(R.id.departure_edit)
        arrivalTv = view.findViewById(R.id.arrival_edit)
        estimateTripDurationTv = view.findViewById(R.id.duration_edit)
        availableSeatTv = view.findViewById(R.id.seat_edit)
        priceTv = view.findViewById(R.id.price_edit)
        descriptionTv = view.findViewById(R.id.description_edit)

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
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        return when (item.itemId) {
            R.id.save -> {

                findNavController().navigate(R.id.action_nav_edit_trip_details_to_details_trip_fragment)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}