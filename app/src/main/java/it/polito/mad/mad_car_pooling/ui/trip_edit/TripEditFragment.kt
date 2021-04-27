package it.polito.mad.mad_car_pooling.ui.trip_edit

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.mad_car_pooling.R
import it.polito.mad.mad_car_pooling.StopAdapterEdit
import it.polito.mad.mad_car_pooling.Trip
import it.polito.mad.mad_car_pooling.ui.trip_list.TripListViewModel
import org.json.JSONObject

class TripEditFragment : Fragment() {

    private val viewModel: TripListViewModel by activityViewModels()
    private lateinit var departureLocation: TextView
    private lateinit var arrivalLocation: TextView
    private lateinit var departureDateTime: TextView
    private lateinit var duration: TextView
    private lateinit var seats: TextView
    private lateinit var price: TextView
    private lateinit var description: TextView
    private lateinit var showStopsCard: LinearLayout
    private lateinit var showStopsLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var editAdapter: StopAdapterEdit
    private lateinit var arrowImage: ImageView
    private var isNewTrip: Boolean = false

    private var index = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_trip_edit, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        departureLocation = view.findViewById(R.id.departure_edit)
        arrivalLocation = view.findViewById(R.id.arrival_edit)
        duration = view.findViewById(R.id.duration_edit)
        seats = view.findViewById(R.id.seat_edit)
        price = view.findViewById(R.id.price_edit)
        description = view.findViewById(R.id.description_edit)
        departureDateTime = view.findViewById(R.id.departure_date_time_edit)

        showStopsLayout = view.findViewById(R.id.show_stops_text_edit)
        showStopsCard = view.findViewById(R.id.show_stops_card_edit)
        arrowImage = view.findViewById(R.id.info_image_edit)

        showStopsCard.setOnClickListener {
            if(showStopsLayout.visibility == View.GONE) {
                showStopsLayout.visibility = View.VISIBLE
                arrowImage.setImageResource(android.R.drawable.arrow_up_float)
            }
            else {
                showStopsLayout.visibility = View.GONE
                arrowImage.setImageResource(android.R.drawable.arrow_down_float)
            }
        }

        recyclerView = view.findViewById(R.id.stops_details_edit)
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.trip_.observe(viewLifecycleOwner, Observer { trip ->
            // Update the selected filters UI
            departureLocation.text = trip.departureLocation
            arrivalLocation.text = trip.arrivalLocation
            duration.text = trip.duration
            seats.text = trip.seats
            price.text = trip.price
            description.text = trip.description
            index = trip.index
            departureDateTime.text = trip.departureDateTime

            if (trip.stops.size == 0) showStopsCard.visibility =
                View.GONE //TODO verificare che funzioni e migliorare il design
            else {
                showStopsCard.visibility = View.VISIBLE
                editAdapter = StopAdapterEdit(trip.stops, this)
                recyclerView.adapter = editAdapter
            }
        })
        viewModel.newTrip_.observe(viewLifecycleOwner, Observer { newTrip ->
            isNewTrip = newTrip
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_option_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        return when (item.itemId) {
            R.id.save -> {

                val newTrip = Trip(
                    "",
                    departureLocation.text.toString(),
                    arrivalLocation.text.toString(),
                    departureDateTime.text.toString(),
                    duration.text.toString(),
                    seats.text.toString(),
                    price.text.toString(),
                    description.text.toString(),
                    mutableListOf()
                )

                val itemNumber = recyclerView.adapter?.itemCount
                if(itemNumber != null)
                for (i in 0 until itemNumber) {
                    var holder = recyclerView.findViewHolderForAdapterPosition(i)
                    if (holder == null) {
                        holder = editAdapter.holderHashMap[i]
                    }

                    newTrip.addStop(holder!!.itemView.findViewById<TextView>(R.id.departure_stop_edit).text.toString(), holder.itemView.findViewById<TextView>(R.id.date_time_stop_edit).text.toString())
                }


                viewModel.editTrip(newTrip, index)


                val sharedPref =
                    requireActivity().getSharedPreferences("trip_list", Context.MODE_PRIVATE)
                viewModel.trips.observe(viewLifecycleOwner, Observer { list ->
                    with(sharedPref.edit()) {
                        putStringSet("trips", setTrips(list))
                        apply()
                    }
                })

                if(isNewTrip)
                    findNavController().navigate(R.id.action_nav_edit_trip_details_to_nav_list)
                else
                    findNavController().navigate(R.id.action_nav_edit_trip_details_to_details_trip_fragment)
                true
            }
            R.id.clear -> {
                departureLocation.text = ""
                arrivalLocation.text = ""
                departureDateTime.text = ""
                duration.text = ""
                seats.text = ""
                price.text = ""
                description.text = ""

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setTrips(trips: MutableList<Trip>): Set<String> {

        val jsonObjectTripSet: MutableSet<String> = mutableSetOf()

        val iterator = trips!!.listIterator()
        for (item in iterator) {

            val jsonObjectTrip = JSONObject()
            val jsonObjectStopSet: MutableSet<String> = mutableSetOf()

            jsonObjectTrip.put("car_photo", item.carPhoto)
            jsonObjectTrip.put("departure_location", item.departureLocation)
            jsonObjectTrip.put("arrival_location", item.arrivalLocation)
            jsonObjectTrip.put("departure_date_time", item.departureDateTime)
            jsonObjectTrip.put("duration", item.duration)
            jsonObjectTrip.put("seats", item.seats)
            jsonObjectTrip.put("price", item.price)
            jsonObjectTrip.put("description", item.description)
            val iteratorStops = item.stops?.listIterator()
            for (stop in iteratorStops) {
                var jsonObjectStop = JSONObject()
                jsonObjectStop.put("departure_stop", stop.locationName)
                jsonObjectStop.put("date_time_stop", stop.stopDateTime)
                jsonObjectStopSet.add(jsonObjectStop.toString())
            }

            jsonObjectTrip.put("stops", jsonObjectStopSet)


            jsonObjectTripSet.add(jsonObjectTrip.toString())
        }

        return jsonObjectTripSet.toSet()

    }

}

  