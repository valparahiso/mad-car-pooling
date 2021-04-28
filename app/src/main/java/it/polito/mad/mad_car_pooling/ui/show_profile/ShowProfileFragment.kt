package it.polito.mad.mad_car_pooling.ui.show_profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import it.polito.mad.mad_car_pooling.Profile
import it.polito.mad.mad_car_pooling.R
import org.json.JSONObject
import java.io.File

class ShowProfileFragment : Fragment() {

    private val viewModel : ShowProfileViewModel by activityViewModels()
    private lateinit var fullName: TextView
    private lateinit var nickName: TextView
    private lateinit var location: TextView
    private lateinit var email: TextView
    private lateinit var photo: ImageView
    private lateinit var birth: TextView

    private var imagePath: String =""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_show_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fullName = view.findViewById(R.id.fullName)
        nickName = view.findViewById(R.id.nickname)
        email = view.findViewById(R.id.email)
        location = view.findViewById(R.id.location)
        photo = view.findViewById(R.id.show_photo)
        birth = view.findViewById(R.id.birthDate)

        //listeners to show if the value are too long for one line
        fullName.setOnClickListener{ (it as TextView).maxLines = if(it.maxLines==10) 1 else 10 }
        nickName.setOnClickListener{ (it as TextView).maxLines = if(it.maxLines==10) 1 else 10 }
        email.setOnClickListener{ (it as TextView).maxLines = if(it.maxLines==10) 1 else 10 }
        location.setOnClickListener{ (it as TextView).maxLines = if(it.maxLines==10) 1 else 10 }

        //receives values from viewModel
        viewModel.profile.observe(viewLifecycleOwner, Observer { profile ->
            // Update the selected filters UI
            loadFields(profile)
        })
        loadFields(viewModel.profile.value!!)
    }

    //fills textView with viewModel values
    fun loadFields(profile: Profile) {
        fullName.text = profile.fullName
        nickName.text = profile.nickName
        location.text = profile.location
        birth.text = profile.birth
        email.text = profile.email
        imagePath = profile.imagePath

        reloadImageView(photo, imagePath)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.file_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.edit -> {
                val bundle = Bundle()
                bundle.putString("imagePath", imagePath)
                findNavController().navigate(R.id.action_show_profile_fragment_to_nav_edit_profile, bundle)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    //set final user image, if modified
    private fun reloadImageView(image: ImageView, path: String){
        val file = File(path)
        if(file.exists()){
            image.setImageResource(R.drawable.user_image)
            image.setImageURI(file.toUri())
        }else{
            image.setImageResource(R.drawable.user_image)
        }
    }
}