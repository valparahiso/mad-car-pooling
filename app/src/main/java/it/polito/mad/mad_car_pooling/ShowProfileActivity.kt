package it.polito.mad.mad_car_pooling

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.system.Os.close
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import org.json.JSONObject
import java.io.File


class ShowProfileActivity : AppCompatActivity() {
    private lateinit var fullName: TextView
    private lateinit var nickName: TextView
    private lateinit var location: TextView
    private lateinit var email: TextView
    private lateinit var photo: ImageView

    private lateinit var image_path: String
    private lateinit var sharedPref: SharedPreferences
    private lateinit var jsonGlobal: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
        fullName = findViewById(R.id.fullName)
        nickName = findViewById(R.id.nickname)
        email = findViewById(R.id.email)
        location = findViewById(R.id.location)
        photo = findViewById(R.id.edit_photo)

        sharedPref = this?.getPreferences(Context.MODE_PRIVATE)
        var jsonObject: JSONObject = JSONObject()
        jsonObject.put("fullName", "Mario Rossi")
        jsonObject.put("nickName", "mario89")
        jsonObject.put("email", "mario.rossi@polito.it")
        jsonObject.put("location", "Lombardia")
        jsonObject.put("photo", "android.resource://it.polito.mad.mad_car_pooling/drawable/user_image")
        val str: String? = sharedPref.getString("profile", jsonObject.toString())
        jsonGlobal = JSONObject(str!!)
        fullName.text =  jsonGlobal.getString("fullName")
        nickName.text =  jsonGlobal.getString("nickName")
        email.text = jsonGlobal.getString("email")
        location.text =  jsonGlobal.getString("location")
        photo.setImageURI(Uri.parse(jsonGlobal.getString("photo")))


        image_path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/profile.png"
/*
        fullName.text =  "Mario Rossi"
        nickName.text =  "mariored89"
        email.text =  "mario.rossi@polito.it"
        location.text =  "Lombardia"

 */
        reloadImageView(photo, image_path)

        //Log.d("polito_path", "${image_path}")

        /*var uri: Uri = Uri.parse("android.resource://${getApplicationContext().getPackageName()}/drawable/download.png")
        val stream: InputStream? = contentResolver.openInputStream(uri)
        val myBitmap = BitmapFactory.decodeStream(stream)
        photo.setImageBitmap(myBitmap)*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fullname", fullName.text.toString())
        outState.putString("nickname", nickName.text.toString())
        outState.putString("email", email.text.toString())
        outState.putString("location", location.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        fullName.text = savedInstanceState.getString("fullname")
        nickName.text = savedInstanceState.getString("nickname")
        email.text = savedInstanceState.getString("email")
        location.text = savedInstanceState.getString("location")
    }

    //create option menu for calling the EditProfileActivity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.file_menu, menu)
        return true
    }

    //behaviour of click event on the option menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Log.d("POLITOMAD","onOptionsItemSelected()")
        editProfile()
        return super.onOptionsItemSelected(item)
    }

    //creation of the intent and start of EditProfileActivity
    private fun editProfile() {
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra("group02.lab1.FULL_NAME", fullName.text)
        intent.putExtra("group02.lab1.NICK_NAME", nickName.text)
        intent.putExtra("group02.lab1.EMAIL", email.text)
        intent.putExtra("group02.lab1.LOCATION", location.text)
        intent.putExtra("group02.lab1.IMAGE_PATH", image_path)

        startActivityForResult(intent, 1)
    }

    //recieve result from EditProfileActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            showProfile(data)
        }
    }

    //put result from EditProfileActivity in TextViews and ImageView
    private fun showProfile(data: Intent?) {
        fullName.text = data?.getStringExtra("group02.lab1.FULL_NAME")
        nickName.text = data?.getStringExtra("group02.lab1.NICK_NAME")
        email.text = data?.getStringExtra("group02.lab1.EMAIL")
        location.text = data?.getStringExtra("group02.lab1.LOCATION")
        reloadImageView(photo, image_path)
        jsonGlobal.put("fullName", fullName.text.toString())
        jsonGlobal.put("nickName", nickName.text.toString())
        jsonGlobal.put("email", email.text.toString())
        jsonGlobal.put("location", location.text.toString())
        if(!jsonGlobal.getString("photo").equals(image_path)) {jsonGlobal.put("photo", image_path)}
        with (sharedPref.edit()){
            putString("profile", jsonGlobal.toString())
            apply()
        }
    }


    private fun reloadImageView(image: ImageView, path: String){
        var file = File(path)
        if(file.exists()){
            image.setImageURI(Uri.parse("android.resource://it.polito.mad.mad_car_pooling/drawable/user_image"))
            image.setImageURI(file.toUri())
        }else{
            image.setImageURI(Uri.parse("android.resource://it.polito.mad.mad_car_pooling/drawable/user_image"))
        }

    }

}
