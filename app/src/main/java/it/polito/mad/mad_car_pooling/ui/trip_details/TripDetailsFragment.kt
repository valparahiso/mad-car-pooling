package it.polito.mad.mad_car_pooling.ui.trip_details

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.mad.mad_car_pooling.*
import it.polito.mad.mad_car_pooling.ui.trip_list.TripListViewModel
import java.io.File

class TripDetailsFragment : Fragment() {

    private val viewModel : TripListViewModel by activityViewModels()
    private lateinit var departureLocation : TextView
    private lateinit var arrivalLocation : TextView
    private lateinit var duration : TextView
    private lateinit var seats : TextView
    private lateinit var price : TextView
    private lateinit var description : TextView
    private lateinit var departureDateTime : TextView
    private lateinit var showStopsCard : LinearLayout
    private lateinit var showStopsLayout: LinearLayout
    private lateinit var arrowImage : ImageView
    private lateinit var carImage : ImageView

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
        arrowImage = view.findViewById(R.id.info_image)
        carImage = view.findViewById(R.id.car_photo_details)

        //listeners to show if the value is too long for one line
        description.setOnClickListener{ (it as TextView).maxLines = if(it.maxLines==10) 1 else 10 }

        //used to show the Stop List
        showStopsCard.setOnClickListener{
            if(showStopsLayout.visibility == GONE) {
                showStopsLayout.visibility = VISIBLE
                arrowImage.setImageResource(android.R.drawable.arrow_up_float)
            }
            else {
                showStopsLayout.visibility = GONE
                arrowImage.setImageResource(android.R.drawable.arrow_down_float)
            }
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.stops_details)

        //used to show the recyclerView that contains Stops
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

            //stopList shown only if size is not 0
            if(trip.stops.size == 0) showStopsCard.visibility = GONE
            else{
                showStopsCard.visibility = VISIBLE
                recyclerView.adapter = StopAdapter(trip.stops.filter { stop -> stop.saved }.sortedBy { it.stopDateTime }, this)
            }

        })

        //observer to load the fields in trip
        viewModel.trip_.observe(viewLifecycleOwner, Observer { trip ->
            // Update the selected filters UI
            loadFields(trip)
        })
        loadFields(viewModel.trip_.value!!)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.trip_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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

    //loads values for views
    fun loadFields(trip: Trip) {
        departureLocation.text = trip.departureLocation
        arrivalLocation.text = trip.arrivalLocation
        duration.text = trip.duration
        seats.text = trip.seats
        price.text = trip.price
        description.text = trip.description
        departureDateTime.text = trip.departureDateTime
        reloadImageView(carImage, trip.carPhoto)
    }

    //set final car image, if modified
    private fun reloadImageView(image: ImageView, path: String){
        var file = File(path)
        if(file.exists()){
            image.setImageResource(R.drawable.default_car_image)
            image.setImageURI(file.toUri())
        }else{
            image.setImageResource(R.drawable.default_car_image)
        }
    }
}