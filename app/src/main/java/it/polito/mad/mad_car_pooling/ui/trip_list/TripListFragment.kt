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

        //msg if the list is empty
        if(viewModel.trips.value.isNullOrEmpty())
        {
            emptyList.visibility = View.VISIBLE
        } else {
            emptyList.visibility = View.GONE
        }

        viewModel.trips.observe(viewLifecycleOwner, Observer {
            list-> recyclerView.adapter = TripAdapter(list,this)
        })

        //fab for new Trip
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
            newTrip.carPhoto = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/trip"+newTrip.index+".png"
            viewModel.setTrip(newTrip, true)
            findNavController().navigate(R.id.action_nav_list_to_nav_edit_trip_details)
        }
    }


    //setting the trip in viewModel
    fun updateTrip(trip :Trip){
        viewModel.setTrip(trip, false)
    }

}