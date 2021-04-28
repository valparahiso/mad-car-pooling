package it.polito.mad.mad_car_pooling.ui.trip_edit

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import it.polito.mad.mad_car_pooling.*
import it.polito.mad.mad_car_pooling.ui.trip_list.TripListViewModel
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class TripEditFragment : Fragment() {

    private val viewModel: TripListViewModel by activityViewModels()
    private lateinit var departureLocation: TextView
    private lateinit var arrivalLocation: TextView
    private lateinit var departureDateTime: TextView
    private lateinit var duration: TextView
    private lateinit var seats: TextView
    private lateinit var price: TextView
    private lateinit var description: TextView
    private lateinit var carImage: ImageView
    private lateinit var showStopsCard: LinearLayout
    private lateinit var showStopsLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var editAdapter: StopAdapterEdit
    private lateinit var arrowImage: ImageView
    private lateinit var addButton: ImageView
    private lateinit var carPhoto: String
    private lateinit var infoStops: TextView
    private lateinit var imageTemp: String

    private var index = -1 //index to save the id of the trip
    private var saveFlag = false //flag to see if tmp img has to be deleted
    private var isNewTrip: Boolean = false //if it's an edit trip or a new trip
    private var rotate : Boolean = false //flag to know if we have rotated the phone


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_trip_edit, container, false)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        departureLocation = view.findViewById(R.id.departure_edit)
        arrivalLocation = view.findViewById(R.id.arrival_edit)
        duration = view.findViewById(R.id.duration_edit)
        seats = view.findViewById(R.id.seat_edit)
        price = view.findViewById(R.id.price_edit)
        description = view.findViewById(R.id.description_edit)
        departureDateTime = view.findViewById(R.id.departure_date_time_edit)
        carImage = view.findViewById(R.id.car_photo_edit)

        infoStops = view.findViewById(R.id.info_text_edit)
        showStopsLayout = view.findViewById(R.id.show_stops_text_edit)
        showStopsCard = view.findViewById(R.id.show_stops_card_edit)
        arrowImage = view.findViewById(R.id.info_image_edit)
        addButton = view.findViewById(R.id.add_stop_edit)
        imageTemp = context?.externalCacheDir.toString() + "/tmp.png"


        val mcalendar: Calendar = Calendar.getInstance()
        val myday = mcalendar.get(Calendar.DAY_OF_MONTH)
        val myyear = mcalendar.get(Calendar.YEAR)
        val mymonth = mcalendar.get(Calendar.MONTH)
        val hour = mcalendar.get(Calendar.HOUR)
        val minute = mcalendar.get(Calendar.MINUTE)

        departureDateTime.setOnFocusChangeListener { _, hasFocus -> run {
            if(hasFocus)
                (activity as MainActivity).openCalendarDialog(
                    departureDateTime,
                    myyear,
                    mymonth,
                    myday,
                    hour,
                    minute
                )
        } }

        //to edit the stops
        showStopsCard.setOnClickListener {
            if (showStopsLayout.visibility == View.GONE) {
                showStopsLayout.visibility = View.VISIBLE
                arrowImage.setImageResource(android.R.drawable.arrow_up_float)
                val toast = Toast.makeText(
                    activity,
                    "All fields of stops are required to be saved",
                    Toast.LENGTH_LONG
                )
                toast.show()
            } else {
                showStopsLayout.visibility = View.GONE
                arrowImage.setImageResource(android.R.drawable.arrow_down_float)
            }
        }


        val imageButton = view.findViewById<ImageButton>(R.id.camera_car)
        registerForContextMenu(imageButton)
        //button to choose the img
        imageButton.setOnClickListener {
            (activity as MainActivity).attentionIV = carImage
            activity?.openContextMenu(it)
        }

        //disable LongClick
        imageButton.setOnLongClickListener { true }

        recyclerView = view.findViewById(R.id.stops_details_edit)
        recyclerView.layoutManager = LinearLayoutManager(context)

        //fab used to delete a trip
        val fab: FloatingActionButton = view.findViewById(R.id.fab_delete)
        fab.setOnClickListener{

            //aler to confirm deleting
            val alertDialogBuilder = AlertDialog.Builder(activity)
            alertDialogBuilder.setTitle("Confirm Delete")
            alertDialogBuilder.setMessage("Are you sure,You want to delete this trip?")
            alertDialogBuilder.setCancelable(false)

            //listener to delete the trip
            alertDialogBuilder.setPositiveButton("yes") { arg0, arg1 -> run{
                val sharedPref =
                    requireActivity().getSharedPreferences("trip_list", Context.MODE_PRIVATE)
                //calling delete trip to update viewModel
                viewModel.deleteTrip(index)
                //observer for shared preferences (new list)
                viewModel.trips.observe(viewLifecycleOwner, Observer { list ->
                    with(sharedPref.edit()) {
                        putStringSet("trips", setTrips(list))
                        apply()
                    }
                })
                view.let {
                    Snackbar.make(it, "Trip deleted", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
                findNavController().navigate(R.id.action_nav_edit_trip_details_to_nav_list)
            } }
            alertDialogBuilder.setNegativeButton("No") { dialog, which ->  }

            //alert creation
            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        //observer to create a new Trip
        viewModel.newTrip_.observe(viewLifecycleOwner, Observer { newTrip ->
            isNewTrip = newTrip
            if (isNewTrip) {
                //change name of the action Bar
                (activity as MainActivity).supportActionBar?.title = "Add new trip"
                fab.hide()
            }
        })

        //listener to add a Stop
        addButton.setOnClickListener {
            showStopsLayout.visibility = View.VISIBLE
            arrowImage.setImageResource(android.R.drawable.arrow_up_float)
            editAdapter.data.add(0, Stop("", "", saved = false, deleted = false))
            editAdapter.notifyItemInserted(0)
            recyclerView.smoothScrollToPosition(0)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_option_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {

                //setting flag in order to delete tmp img
                saveFlag = true

                val tmpFile = File(imageTemp)

                if (tmpFile.exists()) {
                    Log.d("POLIMAD", "New photo saved in: $carPhoto")
                    tmpFile.copyTo(File(carPhoto), overwrite = true)
                    tmpFile.delete()
                }

                //checking if the fields are filled using a flag
                var flagPresentValue = true
                if (TextUtils.isEmpty(departureLocation.text.toString())) {
                    departureLocation.error = "Departure Location is required!"
                    flagPresentValue = false
                }
                if (TextUtils.isEmpty(arrivalLocation.text.toString())) {
                    arrivalLocation.error = "Arrival Location is required!"
                    flagPresentValue = false
                }
                if (TextUtils.isEmpty(departureDateTime.text.toString())) {
                    departureDateTime.error = "Departure DateTime is required!"
                    flagPresentValue = false
                }
                if (TextUtils.isEmpty(duration.text.toString())) {
                    duration.error = "Duration is required!"
                    flagPresentValue = false
                }
                if (TextUtils.isEmpty(seats.text.toString())) {
                    seats.error = "Number of seats is required!"
                    flagPresentValue = false
                }
                if (TextUtils.isEmpty(price.text.toString())) {
                    price.error = "Price is required!"
                    flagPresentValue = false
                }

                //if the fields are filled the trip is edited
                if (flagPresentValue) {
                    val newTrip = Trip(
                        carPhoto,
                        departureLocation.text.toString(),
                        arrivalLocation.text.toString(),
                        departureDateTime.text.toString(),
                        duration.text.toString(),
                        seats.text.toString(),
                        price.text.toString(),
                        description.text.toString(),
                        mutableListOf(),
                        index
                    )

                    //reading stop list in order to save them in the trip
                    val itemNumber = recyclerView.adapter?.itemCount
                    if (itemNumber != null)
                        for (i in 0 until itemNumber) {

                            var holder = recyclerView.findViewHolderForAdapterPosition(i)
                            if (holder == null) {
                                holder = editAdapter.holderHashMap[i]
                            }

                            if (holder != null && !editAdapter.data[i].deleted)
                                newTrip.addStop(
                                    holder.itemView.findViewById<TextView>(R.id.departure_stop_edit).text.toString(),
                                    holder.itemView.findViewById<TextView>(R.id.date_time_stop_edit).text.toString()
                                )
                            else if (!editAdapter.data[i].deleted)
                                newTrip.addStop(
                                    editAdapter.data[i].locationName,
                                    editAdapter.data[i].stopDateTime
                                )
                        }

                    //updating trip in viewModel in order to modify the trip list
                    viewModel.editTrip(newTrip, index)

                    //saving shared preferences of the new trip list
                    val sharedPref =
                        requireActivity().getSharedPreferences("trip_list", Context.MODE_PRIVATE)
                    viewModel.trips.observe(viewLifecycleOwner, Observer { list ->
                        with(sharedPref.edit()) {
                            putStringSet("trips", setTrips(list))
                            apply()
                        }
                    })

                    if (isNewTrip) {
                        view?.let {
                            Snackbar.make(it, "Trip created", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                        }
                        findNavController().navigate(R.id.action_nav_edit_trip_details_to_nav_list)
                    } else {
                        view?.let {
                            Snackbar.make(it, "Trip modified", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                        }
                        findNavController().navigate(R.id.action_nav_edit_trip_details_to_details_trip_fragment)
                    }
                }
                true
            }

            //clear of the fields in edit Trip
            R.id.clear -> {
                departureLocation.text = ""
                arrivalLocation.text = ""
                departureDateTime.text = ""
                duration.text = ""
                seats.text = ""
                price.text = ""
                description.text = ""
                //clear of the stop list in recycler view
                editAdapter.data.clear()
                recyclerView.adapter = editAdapter
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    //save state of the fragment
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        rotate = true
        outState.putString("departureLocation", departureLocation.text.toString())
        outState.putString("arrivalLocation", arrivalLocation.text.toString())
        outState.putString("duration", duration.text.toString())
        outState.putString("seats", seats.text.toString())
        outState.putString("price", price.text.toString())
        outState.putString("description", description.text.toString())
        outState.putString("index", index.toString())
        outState.putString("departureDateTime", departureDateTime.text.toString())
        outState.putString("carPhoto", carPhoto)
        // tmp list in order to take the stop status
        val newTrip = mutableListOf<Stop>()
        val itemNumber = recyclerView.adapter?.itemCount
        if (itemNumber != null)
            for (i in 0 until itemNumber) {

                var holder = recyclerView.findViewHolderForAdapterPosition(i)
                if (holder == null) {
                    holder = editAdapter.holderHashMap[i]
                }

                if (holder != null && !editAdapter.data[i].deleted)
                    newTrip.add(
                        i, Stop(
                            holder.itemView.findViewById<TextView>(R.id.departure_stop_edit).text.toString(),
                            holder.itemView.findViewById<TextView>(R.id.date_time_stop_edit).text.toString(),
                            editAdapter.data[i].saved,
                            editAdapter.data[i].deleted
                        )
                    )
                else if (!editAdapter.data[i].deleted)
                    newTrip.add(
                        i, Stop(
                            editAdapter.data[i].locationName,
                            editAdapter.data[i].stopDateTime,
                            editAdapter.data[i].saved,
                            editAdapter.data[i].deleted
                        )
                    )
            }
        //putting the Stop list in the bundle
        outState.putParcelableArrayList("dataStop", ArrayList(newTrip))
    }

    //to restore the state of the fragment
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        rotate = false
        //checking if it's the first execution of the fragment
        if (savedInstanceState != null) {
            departureLocation.setText(savedInstanceState.getString("departureLocation"))
            arrivalLocation.setText(savedInstanceState.getString("arrivalLocation"))
            duration.setText(savedInstanceState.getString("duration"))
            seats.setText(savedInstanceState.getString("seats"))
            price.setText(savedInstanceState.getString("price"))
            description.setText(savedInstanceState.getString("description"))
            index = savedInstanceState.getString("index")?.toInt()!!
            departureDateTime.setText(savedInstanceState.getString("departureDateTime"))
            carPhoto=(savedInstanceState.getString("carPhoto"))!!
            loadImage(carImage, carPhoto)
            //loading the stop list and sorting it
            editAdapter = StopAdapterEdit(
                savedInstanceState.getParcelableArrayList<Stop>("dataStop")?.toMutableList()!!, this
            )
            editAdapter.data.sortBy { it.stopDateTime }
            recyclerView.adapter = editAdapter
        }
        else
            setEditTextTrip()
    }

    //loading fields of the trip from the viewModel observer
    private fun  setEditTextTrip() {
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
            carPhoto = trip.carPhoto
            loadImage(carImage, carPhoto)

            //to restore the deleted field to false if they are not saved
            trip.stops.forEach { stop -> stop.deleted = false }
            //to show only stops that are saved
            editAdapter =
                StopAdapterEdit(trip.stops.filter { stop -> stop.saved }.toMutableList(), this)
            editAdapter.data.sortBy { it.stopDateTime }
            recyclerView.adapter = editAdapter

            showStopsCard.visibility = View.VISIBLE

        })
    }

    //to save the trips in shared preferences
    private fun setTrips(trips: MutableList<Trip>): Set<String> {

        val jsonObjectTripSet: MutableSet<String> = mutableSetOf()

        val iterator = trips.listIterator()
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
            jsonObjectTrip.put("index", item.index)
            val iteratorStops = item.stops.listIterator()
            for (stop in iteratorStops) {
                val jsonObjectStop = JSONObject()
                jsonObjectStop.put("departure_stop", stop.locationName)
                jsonObjectStop.put("date_time_stop", stop.stopDateTime)
                jsonObjectStopSet.add(jsonObjectStop.toString())
            }

            jsonObjectTrip.put("stops", jsonObjectStopSet)

            jsonObjectTripSet.add(jsonObjectTrip.toString())
        }

        return jsonObjectTripSet.toSet()

    }

    //deleting tmp img if the trip is saved
    override fun onDestroy() {
        super.onDestroy()
        if(saveFlag || rotate == false) {
            val tmpFile = File(imageTemp)
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
        }
    }


    //function to load the picture if exist (icon default)
    private fun loadImage(image: ImageView, path: String) {
        val file = File(path)
        if (file.exists()) {
            image.setImageResource(R.drawable.default_car_image)
            image.setImageURI(path.toUri())
        } else {
            val fileTmp = File(imageTemp)
            if(fileTmp.exists()) {
                Log.d("POLITOMAD_img", "file tmp exists")
                image.setImageResource(R.drawable.default_car_image)
                image.setImageURI(imageTemp.toUri())
            }
            else{
                image.setImageResource(R.drawable.default_car_image)
            }
        }
    }

}

