package it.polito.mad.mad_car_pooling.ui.trip_edit

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
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
import org.joda.time.DateTime
import org.json.JSONObject
import java.io.File
import java.util.*


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
    private var isNewTrip: Boolean = false
    private lateinit var carPhoto: String

    private var index = -1
    private var saveFlag = false

    private lateinit var imageTemp: String

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

        showStopsLayout = view.findViewById(R.id.show_stops_text_edit)
        showStopsCard = view.findViewById(R.id.show_stops_card_edit)
        arrowImage = view.findViewById(R.id.info_image_edit)
        addButton = view.findViewById(R.id.add_stop_edit)

        val mcalendar: Calendar = Calendar.getInstance()

        val day = mcalendar.get(Calendar.DAY_OF_MONTH)
        val year = mcalendar.get(Calendar.YEAR)
        val month = mcalendar.get(Calendar.MONTH)

        departureDateTime.setOnFocusChangeListener { _, hasFocus -> run {
            if(hasFocus)
                openCalendarDialog(year, month, day)
        } }

        showStopsCard.setOnClickListener {
            if (showStopsLayout.visibility == View.GONE) {
                showStopsLayout.visibility = View.VISIBLE
                arrowImage.setImageResource(android.R.drawable.arrow_up_float)
            } else {
                showStopsLayout.visibility = View.GONE
                arrowImage.setImageResource(android.R.drawable.arrow_down_float)
            }
        }

        imageTemp = context?.externalCacheDir.toString() + "/tmp.png"

        val imageButton = view.findViewById<ImageButton>(R.id.camera_car)
        registerForContextMenu(imageButton)
        imageButton.setOnClickListener {
            (activity as MainActivity).attentionIV = carImage
            activity?.openContextMenu(it)
        }
        imageButton.setOnLongClickListener { true }

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
            carPhoto = trip.carPhoto
            loadImage(carImage, carPhoto)


            trip.stops.forEach { stop -> stop.deleted = false }
            editAdapter =
                StopAdapterEdit(trip.stops.filter { stop -> stop.saved }.toMutableList(), this)
            recyclerView.adapter = editAdapter


            showStopsCard.visibility = View.VISIBLE

        })

        val fab: FloatingActionButton = view.findViewById(R.id.fab_delete)
        fab.setOnClickListener{

            //alert per confermare l'eliminazione di un trip
            val alertDialogBuilder = AlertDialog.Builder(activity)
            alertDialogBuilder.setTitle("Confirm Delete")
            alertDialogBuilder.setMessage("Are you sure,You want to delete this trip?")
            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setPositiveButton("yes") { arg0, arg1 -> run{
                val sharedPref =
                    requireActivity().getSharedPreferences("trip_list", Context.MODE_PRIVATE)
                viewModel.deleteTrip(index)
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

            //creazione dell'alert
            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }


        viewModel.newTrip_.observe(viewLifecycleOwner, Observer { newTrip ->
            isNewTrip = newTrip
            if (isNewTrip) {
                (activity as MainActivity).supportActionBar?.title = "Add new trip"
                fab.hide()
            }
        })


        addButton.setOnClickListener {
            showStopsLayout.visibility = View.VISIBLE
            arrowImage.setImageResource(android.R.drawable.arrow_up_float)
            editAdapter.data.add(0, Stop("", "", saved = false, deleted = false))
            editAdapter.notifyItemInserted(0)
            recyclerView.smoothScrollToPosition(0)

        }

    }

    //open Calendar Dialog for Birth Date and remove focus form the EditText
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openCalendarDialog(year: Int, month: Int, day: Int){
        departureDateTime.inputType = InputType.TYPE_NULL
        val listener = DatePickerDialog.OnDateSetListener { _, Year, monthOfYear, dayOfMonth -> departureDateTime.setText("${dayOfMonth}/${monthOfYear + 1}/${Year}") }
        val dpDialog = DatePickerDialog(activity as MainActivity, listener, year, month, day)
        //dpDialog.datePicker.maxDate = DateTime().minusYears(18).millis    //set the maximum date (at least 18 years old)
        dpDialog.show()
        departureDateTime.clearFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_option_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        return when (item.itemId) {
            R.id.save -> {

                saveFlag = true

                val tmpFile = File(imageTemp)
                if (tmpFile.exists()) {
                    Log.d("POLIMAD", "New photo saved in: $carPhoto")
                    tmpFile.copyTo(File(carPhoto), overwrite = true)
                    tmpFile.delete()
                }
                var flagPresentValue = true
                if(TextUtils.isEmpty(departureLocation.text.toString())) {
                    departureLocation.error = "Departure Location is required!"
                    flagPresentValue = false
                }
                if(TextUtils.isEmpty(arrivalLocation.text.toString())) {
                    arrivalLocation.error = "Arrival Location is required!"
                    flagPresentValue = false
                }
                if(TextUtils.isEmpty(departureDateTime.text.toString())) {
                    departureDateTime.error = "Departure DateTime is required!"
                    flagPresentValue = false
                }
                if(TextUtils.isEmpty(duration.text.toString())) {
                    duration.error = "Duration is required!"
                    flagPresentValue = false
                }
                if(TextUtils.isEmpty(seats.text.toString())) {
                    seats.error = "Number of seats is required!"
                    flagPresentValue = false
                }
                if(TextUtils.isEmpty(price.text.toString())) {
                    price.error = "Price is required!"
                    flagPresentValue = false
                }

                if(flagPresentValue) {
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
                    //newTrip.increment()

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


                    viewModel.editTrip(newTrip, index)


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
            jsonObjectTrip.put("index", item.index)
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

    override fun onDestroy() {
        super.onDestroy()
        val tmpFile = File(imageTemp)
        if (tmpFile.exists()) {
            tmpFile.delete()
        }

        
    }

    //function to load the picture if exist (icon default)
    private fun loadImage(image: ImageView, path: String) {
        val file = File(path)
        if (file.exists()) {
            image.setImageResource(R.drawable.default_car_image)
            image.setImageURI(path.toUri())
        } else {
            // probabilmente righe inutili (da ricontrollare)
            val options = BitmapFactory.Options()
            options.inScaled = false
            //

            image.setImageResource(R.drawable.default_car_image)
        }
    }

}

