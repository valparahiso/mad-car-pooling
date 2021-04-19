package it.polito.mad.mad_car_pooling.ui.trip_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.mad_car_pooling.R
import it.polito.mad.mad_car_pooling.TripAdapter
class TripListFragment : Fragment() {


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

        val list: List<String> = listOf("Stringa1", "Stringa2", "Stringa3", "Stringa1", "Stringa2", "Stringa3","Stringa1", "Stringa2", "Stringa3" , "Stringa1", "Stringa2", "Stringa3")

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = TripAdapter(list)
    }
}