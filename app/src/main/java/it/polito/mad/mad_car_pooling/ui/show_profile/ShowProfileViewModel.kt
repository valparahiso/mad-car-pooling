package it.polito.mad.mad_car_pooling.ui.show_profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.mad_car_pooling.Profile

class ShowProfileViewModel : ViewModel() {

    private var profile_ = MutableLiveData<Profile>()
    val profile: LiveData<Profile> get() = profile_

    fun setProfile(item: Profile) {
        profile_.value = item
    }

}