package it.polito.mad.mad_car_pooling.ui.show_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.mad_car_pooling.Profile

class ShowProfileViewModel : ViewModel() {

    var profile = MutableLiveData<Profile>()

    fun setProfile(item : Profile){
        profile.value = item
    }


}