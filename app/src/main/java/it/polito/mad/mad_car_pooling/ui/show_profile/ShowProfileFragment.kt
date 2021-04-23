package it.polito.mad.mad_car_pooling.ui.show_profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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

    private lateinit var imagePath: String
    private lateinit var sharedPref: SharedPreferences
    private lateinit var jsonGlobal: JSONObject

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


        fullName.setOnClickListener{ (it as TextView).maxLines = if(it.maxLines==10) 1 else 10 }
        nickName.setOnClickListener{ (it as TextView).maxLines = if(it.maxLines==10) 1 else 10 }
        email.setOnClickListener{ (it as TextView).maxLines = if(it.maxLines==10) 1 else 10 }
        location.setOnClickListener{ (it as TextView).maxLines = if(it.maxLines==10) 1 else 10 }

        val sharedPref = requireActivity().getSharedPreferences("profile", Context.MODE_PRIVATE)

        //jsonObject for default values
        var jsonObject = JSONObject()
        jsonObject.put("fullName", "John Doe")
        jsonObject.put("nickName", "Gionny")
        jsonObject.put("email", "john.doe.90@polito.it")
        jsonObject.put("location", "Turin")
        jsonObject.put("birth", "01/01/1990")
        jsonObject.put("photoPath", context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/profile.png")

        //retriving data from the file (if present)
        val str: String? = sharedPref.getString("profile", jsonObject.toString())
        jsonGlobal = JSONObject(str!!)
        fullName.text =  jsonGlobal.getString("fullName")
        nickName.text =  jsonGlobal.getString("nickName")
        email.text = jsonGlobal.getString("email")
        location.text =  jsonGlobal.getString("location")
        birth.text = jsonGlobal.getString("birth")
        imagePath = jsonGlobal.getString("photoPath")
        reloadImageView(photo, imagePath)

        viewModel.profile.observe(viewLifecycleOwner, Observer { profile ->
            // Update the selected filters UI
            fullName.text = profile.fullName
            nickName.text = profile.nickName
            email.text = profile.email
            location.text = profile.location
            birth.text = profile.birth
            imagePath = profile.imagePath
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.file_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        return when(item.itemId){
            R.id.edit -> {
                findNavController().navigate(R.id.action_show_profile_fragment_to_nav_edit_profile)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    //set image, if modified
    private fun reloadImageView(image: ImageView, path: String){
        var file = File(path)
        if(file.exists()){
            image.setImageResource(R.drawable.user_image)
            image.setImageURI(file.toUri())
        }else{
            image.setImageResource(R.drawable.user_image)
        }
    }

}