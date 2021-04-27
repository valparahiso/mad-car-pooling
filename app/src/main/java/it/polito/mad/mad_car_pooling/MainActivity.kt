package it.polito.mad.mad_car_pooling

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        //jsonObject for default values (Trip List)
        val jsonObjectTrip = JSONObject()
        jsonObjectTrip.put("car_photo", "")
        jsonObjectTrip.put("departure_location", "Torino")
        jsonObjectTrip.put("arrival_location", "Milano")
        jsonObjectTrip.put("departure_date_time", "20/02/2012 15:20")
        jsonObjectTrip.put("duration", "10")
        jsonObjectTrip.put("seats", "12")
        jsonObjectTrip.put("price", "12")
        jsonObjectTrip.put("description", "descr")
        val jsonObjectTripSet: Set<String> = listOf(jsonObjectTrip.toString()).toSet()

        sharedPref = this.getSharedPreferences("trip_list", Context.MODE_PRIVATE)

        val tripListString = sharedPref.getStringSet("trips", jsonObjectTripSet)?.toList()

        val tripListIt = tripListString!!.listIterator()
        for (tripString in tripListIt) {


            val tripJson = JSONObject(tripString)

            val trip = Trip(tripJson.get("car_photo") as String,
                tripJson.get("departure_location") as String,
                tripJson.get("arrival_location") as String,
                tripJson.get("departure_date_time") as String,
                tripJson.get("duration") as String,
                tripJson.get("seats") as String,
                tripJson.get("price") as String,
                tripJson.get("description") as String,
                mutableListOf())

            if(tripJson.has("stops")) {
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

            Log.d("POLITOMAD_Trip", tripJson.toString())
            trips.add(trip);
        }

        viewModel.initTrips(trips)


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
}