package it.polito.mad.mad_car_pooling.ui.trip_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TripEditViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is trip edit Fragment"
    }
    val text: LiveData<String> = _text
}