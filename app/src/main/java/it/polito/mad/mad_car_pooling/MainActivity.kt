package it.polito.mad.mad_car_pooling

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.theartofdev.edmodo.cropper.CropImage
import it.polito.mad.mad_car_pooling.ui.show_profile.ShowProfileViewModel
import it.polito.mad.mad_car_pooling.ui.trip_list.TripListViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefProfile: SharedPreferences
    private val viewModel: TripListViewModel by viewModels()
    private val viewModelProfile: ShowProfileViewModel by viewModels()
    private var trips: MutableList<Trip> = mutableListOf()
    private lateinit var imageTemp: String

    //code of request (camera or gallery)
    val REQUEST_IMAGE_CAPTURE = 1       //camera
    val REQUEST_IMAGE_GALLERY = 2       //gallery
    val REQUEST_IMAGE_CROP = 3          //crop
    lateinit var attentionIV: ImageView
    var takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        manageActivityResult(result.resultCode, result.data, REQUEST_IMAGE_CAPTURE)
    }
    var takeGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        manageActivityResult(result.resultCode, result.data, REQUEST_IMAGE_GALLERY)
    }
    var takeCropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        manageActivityResult(result.resultCode, result.data, REQUEST_IMAGE_CROP)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageTemp = externalCacheDir.toString() + "/tmp.png"

        getTrips()
        getProfile()
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_list, R.id.nav_show_profile), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        setHeader(findViewById<NavigationView>(R.id.nav_view).getHeaderView(0))

        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getTrips() {
        trips.clear()

        sharedPref = this.getSharedPreferences("trip_list", Context.MODE_PRIVATE)
        val tripListString = sharedPref.getStringSet("trips", null)?.toList()

        if (tripListString != null) {
            if (tripListString.isNotEmpty()) {
                val tripListIt = tripListString!!.listIterator()
                for (tripString in tripListIt) {

                    val tripJson = JSONObject(tripString)

                    val trip = Trip(
                        tripJson.get("car_photo") as String,
                        tripJson.get("departure_location") as String,
                        tripJson.get("arrival_location") as String,
                        tripJson.get("departure_date_time") as String,
                        tripJson.get("duration") as String,
                        tripJson.get("seats") as String,
                        tripJson.get("price") as String,
                        tripJson.get("description") as String,
                        mutableListOf(),
                        tripJson.get("index") as Int
                    )
                    trip.setCounter()

                    if (tripJson.has("stops")) {
                        val stopJSONArray = JSONArray(tripJson.get("stops") as String)

                        val stopListString = List(stopJSONArray.length()) {
                            stopJSONArray.getString(it)
                        }

                        val stopListIt = stopListString.listIterator()
                        for (stopString in stopListIt) {
                            val stopJson = JSONObject(stopString)
                            trip.addStop(
                                stopJson.get("departure_stop") as String,
                                stopJson.get("date_time_stop") as String
                            )
                        }
                    }
                    trips.add(trip);

                }
            }
        }
        viewModel.initTrips(trips)
    }

    //open Calendar Dialog for Birth Date and remove focus form the EditText
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun openCalendarDialog(departureDateTime: TextView, year: Int, month: Int, day: Int, hour: Int, minute: Int){
        departureDateTime.inputType = InputType.TYPE_NULL
        val listener = DatePickerDialog.OnDateSetListener { _, Year, monthOfYear, dayOfMonth ->
            val listener_time = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                departureDateTime.setText(
                    "${dayOfMonth}/${monthOfYear + 1}/${Year} ${hourOfDay}:${minute}"
                )
            }
            val tDialog = TimePickerDialog(this, listener_time, hour, minute, true)
            tDialog.show()
        }
        val dpDialog = DatePickerDialog(this, listener, year, month, day)
        //dpDialog.datePicker.maxDate = DateTime().minusYears(18).millis    //set the maximum date (at least 18 years old)
        dpDialog.show()
        departureDateTime.clearFocus()
    }

    fun getProfile() {
        //jsonObject for default values (Profile)
        var defaultJsonObject = JSONObject()
        defaultJsonObject.put("fullName", "John Doe")
        defaultJsonObject.put("nickName", "Gionny")
        defaultJsonObject.put("email", "john.doe.90@polito.it")
        defaultJsonObject.put("location", "Turin")
        defaultJsonObject.put("birth", "01/01/1990")
        defaultJsonObject.put("photoPath", getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/profile.png")

        sharedPrefProfile = this.getSharedPreferences("profile_pref", Context.MODE_PRIVATE)

        val str: String? = sharedPrefProfile.getString("profile", defaultJsonObject.toString())

        val jsonGlobal = JSONObject(str!!)
        viewModelProfile.setProfile(Profile(
                jsonGlobal.getString("fullName"),
                jsonGlobal.getString("nickName"),
                jsonGlobal.getString("location"),
                jsonGlobal.getString("email"),
                jsonGlobal.getString("birth"),
                jsonGlobal.getString("photoPath")
        ))
    }

    fun saveProfile(profile: Profile) {
        sharedPrefProfile = this.getSharedPreferences("profile_pref", Context.MODE_PRIVATE)
        val jsonGlobal = JSONObject()
        jsonGlobal.put("fullName", profile.fullName)
        jsonGlobal.put("nickName", profile.nickName)
        jsonGlobal.put("email", profile.email)
        jsonGlobal.put("location", profile.location)
        jsonGlobal.put("birth", profile.birth)
        jsonGlobal.put("photoPath", profile.imagePath)

        with(sharedPrefProfile.edit()) {
            putString("profile", jsonGlobal.toString())
            apply()
        }
    }

    private fun setHeader(headerView : View) {
        val header_fullName : TextView = headerView.findViewById(R.id.header_fullnameView)
        val header_nickName : TextView = headerView.findViewById(R.id.header_nicknameView)
        val header_photo : ImageView = headerView.findViewById(R.id.header_imageView)

        viewModelProfile.profile.observe(this, Observer { profile ->

            // Update the selected filters UI
            header_fullName.text = profile.fullName
            header_nickName.text = profile.nickName

            var file = File(profile.imagePath)
            if(file.exists()){
                header_photo.setImageResource(R.drawable.user_image)
                header_photo.setImageURI(file.toUri())
            }else{
                header_photo.setImageResource(R.drawable.user_image)
            }

        })

    }

    //permits to create the floating context menu
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        //val inflater: MenuInflater = menuInflater
        menuInflater?.inflate(R.menu.menu_context_photo, menu)
    }

    //behaviour of item in the floating context menu
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.gallery -> {
                openGallery()
                true
            }
            R.id.camera_profile -> {
                dispatchTakePictureIntent()   //open camera
                true
            }
            else -> super.onContextItemSelected(item)
        }

    }

    //create an intent for the gallery activity
    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent?.type = "image/*"
        takeGalleryLauncher.launch(gallery)

    }
    //function to open the camera
    @RequiresApi(Build.VERSION_CODES.N)
    private fun dispatchTakePictureIntent() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent?.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val photoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            File(imageTemp)
        )

        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        takePhotoLauncher.launch(takePhotoIntent)

    }

    //permits to receive the photo from the camera or gallery
    fun manageActivityResult(resultCode: Int, data: Intent?, requestCode: Int) {

        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                //return from camera
                REQUEST_IMAGE_CAPTURE -> {
                    try {
                        val file = File(imageTemp)
                        if (file.exists()) {
                            takeCropLauncher.launch(
                                CropImage.activity(file.toUri())
                                    .setAspectRatio(1, 1)
                                    .getIntent(this)
                            )
                        }
                    } catch (e: TypeCastException) {
                        Log.e("POLITOMAD", "Camera Exception")
                    }
                }
                //return from gallery
                REQUEST_IMAGE_GALLERY -> {
                    try {
                        val imageUri = data?.data
                        takeCropLauncher.launch(
                            CropImage.activity(imageUri)
                                .setAspectRatio(1, 1)
                                .getIntent(this)
                        )
                    } catch (e: TypeCastException) {
                        Log.e("POLITOMAD", "Gallery Exception")
                    }
                }

                REQUEST_IMAGE_CROP -> {
                    try {

                        File(CropImage.getActivityResult(data).uri.path!!).copyTo(
                            File(imageTemp),
                            overwrite = true
                        )

                        attentionIV.setImageResource(R.drawable.user_image)
                        attentionIV.setImageURI(imageTemp.toUri())

                    } catch (e: TypeCastException) {
                        Log.e("POLITOMAD", "Crop Exception")
                    }
                }
            }
        }
    }
}