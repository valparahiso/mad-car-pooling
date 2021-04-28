package it.polito.mad.mad_car_pooling.ui.edit_profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import it.polito.mad.mad_car_pooling.MainActivity
import it.polito.mad.mad_car_pooling.Profile
import it.polito.mad.mad_car_pooling.R
import it.polito.mad.mad_car_pooling.ui.show_profile.ShowProfileViewModel
import org.joda.time.DateTime
import java.io.File
import java.util.*


class EditProfileFragment : Fragment() {

    private val viewModel: ShowProfileViewModel by activityViewModels()


    private lateinit var fullNameET: EditText
    private lateinit var nicknameET: EditText
    private lateinit var emailET: EditText
    private lateinit var locationET: EditText
    private lateinit var photoIV: ImageView
    private lateinit var birthET: EditText

    private var imageTempModified: Boolean = false
    private lateinit var imageTemp: String
    private var imagePath: String = String()

    private lateinit var prof: Profile




    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)

    } 

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val imageButton = view.findViewById<ImageButton>(R.id.camera_profile)
        registerForContextMenu(imageButton)

        imageButton.setOnClickListener {
            (activity as MainActivity).attentionIV = photoIV
            activity?.openContextMenu(it)
        }
        imageButton.setOnLongClickListener { true }

        fullNameET = view.findViewById(R.id.edit_fullname)
        nicknameET = view.findViewById(R.id.edit_nickname)
        locationET = view.findViewById(R.id.edit_location)
        emailET = view.findViewById(R.id.edit_email)
        photoIV = view.findViewById(R.id.show_photo)
        birthET = view.findViewById(R.id.edit_birthDate)

        val mcalendar: Calendar = Calendar.getInstance()

        val day = mcalendar.get(Calendar.DAY_OF_MONTH)
        val year = mcalendar.get(Calendar.YEAR)
        val month = mcalendar.get(Calendar.MONTH)

        birthET.setOnFocusChangeListener { _, hasFocus -> run {
            if(hasFocus)
                openCalendarDialog(year, month, day)
        } }

        imageTemp = context?.externalCacheDir.toString() + "/tmp.png"
        imagePath = arguments?.getString("imagePath").toString();
        setEditText()

        //load photo and save status bitmap
        loadImage(photoIV, imagePath)
    }
    //open Calendar Dialog for Birth Date and remove focus form the EditText
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openCalendarDialog(year: Int, month: Int, day: Int){
        birthET.inputType = InputType.TYPE_NULL
        val listener = DatePickerDialog.OnDateSetListener { _, Year, monthOfYear, dayOfMonth -> birthET.setText("${dayOfMonth}/${monthOfYear + 1}/${Year}") }
        val dpDialog = DatePickerDialog(activity as MainActivity, listener, year, month, day)
        dpDialog.datePicker.maxDate = DateTime().minusYears(18).millis    //set the maximum date (at least 18 years old)
        dpDialog.show()
        birthET.clearFocus()
    }






    //option menu for saving
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_option_save, menu)
    }

    //items of save option menu
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {

                //saveContent()

                //check if photo is changed -> save photo and delete tmp cached file
                val tmpFile = File(imageTemp)
                if (tmpFile.exists()) {
                    Log.d("POLIMAD", "New photo saved in: $imagePath")
                    tmpFile.copyTo(File(imagePath), overwrite = true)
                    tmpFile.delete()
                }

                var flagPresentValue = true

                if(TextUtils.isEmpty(fullNameET.text.toString())) {
                    fullNameET.error = "Full name is required!"
                    flagPresentValue = false
                }
                if (TextUtils.isEmpty(nicknameET.text.toString())) {
                    nicknameET.error = "Nick name is required!"
                    flagPresentValue = false
                }
                if (TextUtils.isEmpty(emailET.text.toString())) {
                    emailET.error = "Email is required!"
                    flagPresentValue = false
                }
                if (TextUtils.isEmpty(locationET.text.toString())) {
                    locationET.error = "Location is required!"
                    flagPresentValue = false
                }

                if (TextUtils.isEmpty(birthET.text.toString())) {
                    birthET.error = "Date of birth is required!"
                    flagPresentValue = false
                }

                if(flagPresentValue) {

                    val newProfile = Profile(
                        fullNameET.text.toString(),
                        nicknameET.text.toString(),
                        locationET.text.toString(),
                        emailET.text.toString(),
                        birthET.text.toString(),
                        imagePath
                    )

                    viewModel.setProfile(newProfile)

                    (requireActivity() as MainActivity).saveProfile(newProfile)
                    view?.let {
                        Snackbar.make(it, "Profile updated", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                    findNavController().navigate(R.id.action_nav_edit_profile_to_show_profile_fragment)
                }
                true
            }
            R.id.clear -> {
                clearFields()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    //function to reset textEdit
    private fun clearFields(){
        fullNameET.setText("")
        nicknameET.setText("")
        emailET.setText("")
        locationET.setText("")
        birthET.setText("")

    }
    override fun onDestroy() {
        super.onDestroy()
        val tmpFile = File(imageTemp)
        if (tmpFile.exists()) {
            //Log.d("POLIMAD", "ONDestroy: $imagePath")
            tmpFile.delete()
        }
    }

    //retrieve data from intent of ShowActivityProfile
    private fun setEditText() {

        viewModel.profile.observe(viewLifecycleOwner, Observer { profile ->

            // Update the selected filters UI
            fullNameET.setText(profile.fullName)
            nicknameET.setText(profile.nickName)
            emailET.setText(profile.email)
            locationET.setText(profile.location)
            birthET.setText(profile.birth)

        })

    }

    //function to load the picture if exist (icon default)
    private fun loadImage(image: ImageView, path: String){
        val file = File(path)
        if(file.exists()) {
            image.setImageResource(R.drawable.user_image)
            image.setImageURI(path.toUri())
        }else{
            // probabilmente righe inutili (da ricontrollare)
            val options = BitmapFactory.Options()
            options.inScaled = false
            //

            image.setImageResource(R.drawable.user_image)
        }
    }

}