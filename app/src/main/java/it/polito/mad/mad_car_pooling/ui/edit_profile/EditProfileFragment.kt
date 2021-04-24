package it.polito.mad.mad_car_pooling.ui.edit_profile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import it.polito.mad.mad_car_pooling.R
import it.polito.mad.mad_car_pooling.ui.show_profile.ShowProfileViewModel
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
    private lateinit var imagePath: String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val imageButton = view.findViewById<ImageButton>(R.id.camera)
        registerForContextMenu(imageButton)

        imageButton.setOnClickListener {
            //openContextMenu(it)
        }
        imageButton.setOnLongClickListener { true }

        fullNameET = view.findViewById<EditText>(R.id.edit_fullname)
        nicknameET = view.findViewById<EditText>(R.id.edit_nickname)
        locationET = view.findViewById<EditText>(R.id.edit_location)
        emailET = view.findViewById<EditText>(R.id.edit_email)
        photoIV = view.findViewById(R.id.show_photo)
        birthET = view.findViewById(R.id.edit_birthDate)

        val mcalendar: Calendar = Calendar.getInstance()

        val day = mcalendar.get(Calendar.DAY_OF_MONTH)
        val year = mcalendar.get(Calendar.YEAR)
        val month = mcalendar.get(Calendar.MONTH)

        birthET.setOnFocusChangeListener { _, hasFocus -> run {
            //if(hasFocus)
                //openCalendarDialog(year, month, day)
        } }

        imageTemp = context?.externalCacheDir.toString() + "/tmp.png"
        getData_setViews()

        //load photo and save status bitmap
        loadImage(photoIV, imagePath)
    }

    /*

    //permits to create the floating context menu
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_context_photo, menu)
    }

    //behaviour of item in the floating context menu
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.gallery -> {
                //openGallery()
                true
            }
            R.id.camera -> {
                //dispatchTakePictureIntent()   //open camera
                true
            }
            else -> super.onContextItemSelected(item)
        }

    }

    */

    //retrieve data from intent of ShowActivityProfile
    private fun getData_setViews() {

        viewModel.profile.observe(viewLifecycleOwner, Observer { profile ->
            // Update the selected filters UI
            fullNameET.setText(profile.fullName)
            nicknameET.setText(profile.nickName)
            emailET.setText(profile.email)
            locationET.setText(profile.location)
            birthET.setText(profile.birth)
            imagePath = profile.imagePath
        })

        Log.e("POLIMAD", "imagePath= ${fullNameET.text}")
    }

    //function to load the picture if exist (icon default)
    private fun loadImage(image:ImageView, path:String){
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