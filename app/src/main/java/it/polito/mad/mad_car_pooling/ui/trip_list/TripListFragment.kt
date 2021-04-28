package it.polito.mad.mad_car_pooling.ui.trip_list


import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.mad.mad_car_pooling.R
import it.polito.mad.mad_car_pooling.Trip
import it.polito.mad.mad_car_pooling.TripAdapter
import java.io.File

class TripListFragment : Fragment() {

    private val viewModel : TripListViewModel by activityViewModels()
    private lateinit var emptyList: TextView

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
        emptyList = view.findViewById(R.id.empty_List)


        if(viewModel.trips.value.isNullOrEmpty())
        {
            emptyList.visibility = View.VISIBLE
        } else {
            emptyList.visibility = View.GONE
        }

        viewModel.trips.observe(viewLifecycleOwner, Observer {
            list-> recyclerView.adapter = TripAdapter(list,this)
        })
        val fab: FloatingActionButton = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            val newTrip = Trip(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                mutableListOf()
            )

            Log.e("POLIMA_INDEX", newTrip.index.toString())
            Log.e("POLIMA_INDEX", "[ ${newTrip.departureLocation} ]")

            newTrip.carPhoto = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/trip"+newTrip.index+".png"
            viewModel.setTrip(newTrip, true)
            findNavController().navigate(R.id.nav_edit_trip_details)
            /*view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()*/
        }
    }


    fun updateTrip(trip :Trip){
        viewModel.setTrip(trip, false)
    }


}