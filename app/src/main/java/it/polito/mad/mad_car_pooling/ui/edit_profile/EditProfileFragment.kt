package it.polito.mad.mad_car_pooling.ui.edit_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import it.polito.mad.mad_car_pooling.R
import it.polito.mad.mad_car_pooling.ui.show_profile.ShowProfileViewModel

class EditProfileFragment : Fragment() {

    private val viewModel: ShowProfileViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_edit_profile, container, false)

    }
}