package it.polito.mad.mad_car_pooling.ui.edit_profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.theartofdev.edmodo.cropper.CropImage
import it.polito.mad.mad_car_pooling.MainActivity
import it.polito.mad.mad_car_pooling.Profile
import it.polito.mad.mad_car_pooling.R
import it.polito.mad.mad_car_pooling.ui.show_profile.ShowProfileViewModel
import org.joda.time.DateTime
import org.json.JSONObject
import java.io.File
import java.util.*


class EditProfileFragment : Fragment() {

    private val viewModel: ShowProfileViewModel by activityViewModels()
    //code of request (camera or gallery)
    val REQUEST_IMAGE_CAPTURE = 1       //camera
    val PICK_IMAGE = 2                  //gallery

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

        val imageButton = view.findViewById<ImageButton>(R.id.camera)
        registerForContextMenu(imageButton)

        imageButton.setOnClickListener {
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


    //permits to create the floating context menu
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        //val inflater: MenuInflater = menuInflater
        activity?.menuInflater?.inflate(R.menu.menu_context_photo, menu)
    }

    //behaviour of item in the floating context menu
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.gallery -> {
                openGallery()
                true
            }
            R.id.camera -> {
                dispatchTakePictureIntent()   //open camera
                true
            }
            else -> super.onContextItemSelected(item)
        }

    }

    //create an intent for the gallery activity
    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        activity?.intent?.type = "image/*"
        startActivityForResult(gallery, PICK_IMAGE)
    }
    //function to open the camera
    @RequiresApi(Build.VERSION_CODES.N)
    private fun dispatchTakePictureIntent() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity?.intent?.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val photoUri = FileProvider.getUriForFile(
                activity as MainActivity,
                "${activity?.packageName}.provider",
                File(imageTemp)
        )

        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE)
    }

    //permits to receive the photo from the camera or gallery
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            //return from camera
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    try {
                        val file = File(imageTemp)
                        if(file.exists()) {
                            /*
                            CropImage.activity(file.toUri())
                                    .setAspectRatio(1,1)
                                    .start(activity as MainActivity)   //activity instead of this
                            */
                            imageTempModified = true
                            photoIV.setImageResource(R.drawable.user_image)
                            photoIV.setImageURI(imageTemp.toUri())
                            print(imageTemp)
                            imagePath =imageTemp //aggiunto per far funzionare senza la crop
                        }
                    } catch (e: TypeCastException) {
                        Log.e("POLITOMAD", "Camera Exception")
                    }
                }
            }
            //return from gallery
            PICK_IMAGE -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    try {
                        val imageUri = data?.data
                        /*
                        CropImage.activity(imageUri)
                                .setAspectRatio(1,1)
                                .start(activity as MainActivity)
                         */
                        imageTempModified = true
                        photoIV.setImageResource(R.drawable.user_image)
                        photoIV.setImageURI(imageUri)
                        imagePath =imageTemp   //aggiunto per far funzionare senza la crop
                    } catch (e: TypeCastException) {
                        Log.e("POLITOMAD", "Gallery Exception")
                    }
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    try {
                        File(CropImage.getActivityResult(data).uri.path!!).copyTo(
                                File(imageTemp),
                                overwrite = true
                        )
                        imageTempModified = true
                        photoIV.setImageResource(R.drawable.user_image)
                        photoIV.setImageURI(imageTemp.toUri())
                    } catch (e: TypeCastException) {
                        Log.e("POLITOMAD", "Crop Exception")
                    }
                }

            }
        }
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
                val newProfile = Profile(
                        fullNameET.text.toString(),
                        nicknameET.text.toString(),
                        locationET.text.toString(),
                        emailET.text.toString(),
                        birthET.text.toString(),
                        imagePath)

                viewModel.setProfile(newProfile)

                (requireActivity() as MainActivity).saveProfile(newProfile)
                /*
                val sharedPref = requireActivity().getSharedPreferences("profile_pref", Context.MODE_PRIVATE)
                val jsonGlobal = JSONObject()
                jsonGlobal.put("fullName", fullNameET.text.toString())
                jsonGlobal.put("nickName", nicknameET.text.toString())
                jsonGlobal.put("email", emailET.text.toString())
                jsonGlobal.put("location", locationET.text.toString())
                jsonGlobal.put("birth", birthET.text.toString())
                jsonGlobal.put("photoPath", imagePath)

                with(sharedPref.edit()) {
                    putString("profile", jsonGlobal.toString())
                    apply()
                }
                */
                findNavController().navigate(R.id.action_nav_edit_profile_to_show_profile_fragment)
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
        /*if (imageTempModified) {
            Log.d("POLIMAD", "ONDestroy: $imagePath")
            val tmpFile = File(imageTemp)
            tmpFile.delete()
        }*/
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