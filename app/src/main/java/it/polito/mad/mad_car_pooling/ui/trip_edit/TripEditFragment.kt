package it.polito.mad.mad_car_pooling.ui.trip_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import it.polito.mad.mad_car_pooling.R

class TripEditFragment : Fragment() {

    private lateinit var galleryViewModel: TripEditViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
                ViewModelProvider(this).get(TripEditViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_trip_edit, container, false)
        return root
    }
}